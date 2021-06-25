package com.botrick.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.botrick.firebaseapp.model.Upload;
import com.botrick.firebaseapp.util.LoadinDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Date;

public class UpdateActivity extends AppCompatActivity {
    //Referencia para o Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;
    private Button btnGaleria;
    private ImageView imageView;
    private Uri imageUri = null;
    private EditText editNome;

    //referencia para um nó RealtimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");

    private Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        btnUpload = findViewById(R.id.update_btn_upload);
        imageView = findViewById(R.id.update_image_cel);
        btnGaleria = findViewById(R.id.update_btn_galeria);
        editNome = findViewById(R.id.update_edit_nome);

        //recuperar o upload selecionado
        upload = (Upload) getIntent().getSerializableExtra("upload");
        editNome.setText(upload.getNomeImagem());
        Glide.with(this).load(upload.getUrl()).into(imageView);

        btnGaleria.setOnClickListener(v -> {
            //intent implicita
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("imagem/*");

            //inicia uma activity, e espera um retorno (foto)
            startActivityForResult(intent, 111);
        });

        btnUpload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "Sem nome", Toast.LENGTH_SHORT).show();
                return;
            }

            //caso imagem tenha sido atualizada
            if (imageUri==null){
                //atualizar o nome da imagem
                String nome = editNome.getText().toString();

                upload.setNomeImagem(nome);
                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {
                    finish();
                });

                return;
            }

            atualizarImagem();
        });

    }

    public void atualizarImagem(){
        //Deletar a imagem antiga no storage
            storage.getReference(upload.getUrl()).delete();
        //Fazer o upload da imagem atualizada no storage
            uploadImagemUri();
        //Recupera a url da imagem no storage

        //atualizar no database
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void uploadImagemUri() {
        LoadinDialog dialog = new LoadinDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);

        //referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();

        //criando uma referencia para a imagem no Storage
        StorageReference imagemRef = storage.getReference().child("imagem/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upload feito com sucesso!", Toast.LENGTH_SHORT).show();

            //Inserir dados da imagem no RealtimeDatabase

            //pegar a URL da imagem
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            //atualizar no database
            //atulizar o objeto upload
                upload.setUrl(uri.toString());
                upload.setNomeImagem(editNome.getText().toString());

                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {
                    dialog.dismissDialog();
                    finish();
                });

            });

       })

       .addOnFailureListener(e -> {
            e.printStackTrace();
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==111 && resultCode== Activity.RESULT_OK){
            //caso o usuario selecionou outra imagem da galeria

            //Endereço da imagem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }

}
