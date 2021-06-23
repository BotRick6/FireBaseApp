package com.botrick.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.botrick.firebaseapp.adapter.ImageAdapter;
import com.botrick.firebaseapp.model.Upload;
import com.botrick.firebaseapp.util.LoadinDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnLogout, btnStorage;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ArrayList<Upload> listaUploads = new ArrayList();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.main_btn_logout);
        btnStorage = findViewById(R.id.main_btn_storage);
        recyclerView = findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getApplicationContext(), listaUploads);

        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUploads.get(position);

                deleteUpload(upload);
            }

            @Override
            public void onUpdateClick(int position) {

            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

        btnStorage.setOnClickListener(v -> {
            //abrir a storagem activity
            Intent intent = new Intent(getApplicationContext(), StorageActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            //desloga o usuario
            auth.signOut();
            finish();
        });

        TextView textEmail = findViewById(R.id.main_text_email);
        textEmail.setText(auth.getCurrentUser().getEmail());
        TextView textNome = findViewById(R.id.main_text_user);
        textNome.setText(auth.getCurrentUser().getDisplayName());

    }

    @Override
    protected void onStart() {
        //onStart: - faz parte do ciclo de vida da Activity, depois do onCreat()
        // - É execultado quando app inicia e quando volta do background
        super.onStart();
        getData();
    }

    public void deleteUpload(Upload upload){
        LoadinDialog dialog = new LoadinDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        //deletando a imagem no storagem
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());

        imagemRef.delete().addOnSuccessListener(aVoid -> {
            //deletando a imagem no database
            database.child(upload.getId()).removeValue().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(getApplicationContext(), "Item deletado!", Toast.LENGTH_SHORT).show();

                dialog.dismissDialog();
            });

        });

    }

    public void getData(){
        //Listener para o nó uploads
        // - caso ocorra alguma alteração -> retorna todos os dados!
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUploads.clear();
               for(DataSnapshot no_filho : snapshot.getChildren()){
                   Upload upload = no_filho.getValue(Upload.class);
                   listaUploads.add(upload);
                   Log.i("DATABASE", "id: " +upload.getId()+ ", nome: " +upload.getNomeImagem());
               }

               imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}
