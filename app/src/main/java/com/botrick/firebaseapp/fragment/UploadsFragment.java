package com.botrick.firebaseapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.botrick.firebaseapp.R;
import com.botrick.firebaseapp.UpdateActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<Upload> listaUploads = new ArrayList();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadsFragment newInstance(String param1, String param2) {
        UploadsFragment fragment = new UploadsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_uploads, container, false);

        recyclerView = layout.findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getContext(), listaUploads);

        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUploads.get(position);

                deleteUpload(upload);
            }

            @Override
            public void onUpdateClick(int position) {
                Upload upload = listaUploads.get(position);
                Intent intent = new Intent(getActivity(), UpdateActivity.class);

                //envia o upload para outra activity
                intent.putExtra("upload", upload);
                startActivity(intent);
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

        return layout;
    }

    @Override
    public void onStart() {
        //onStart: - faz parte do ciclo de vida da Activity, depois do onCreat()
        // - É execultado quando app inicia e quando volta do background
        super.onStart();
        getData();
    }

    public void deleteUpload(Upload upload){
        LoadinDialog dialog = new LoadinDialog(getActivity(), R.layout.custom_dialog);
        dialog.startLoadingDialog();

        //deletando a imagem no storagem
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());

        imagemRef.delete().addOnSuccessListener(aVoid -> {
            //deletando a imagem no database
            database.child(upload.getId()).removeValue().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(getContext(), "Item deletado!", Toast.LENGTH_SHORT).show();

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
