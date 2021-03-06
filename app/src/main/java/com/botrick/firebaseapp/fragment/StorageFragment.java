package com.botrick.firebaseapp.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.botrick.firebaseapp.R;
import com.botrick.firebaseapp.model.Upload;
import com.botrick.firebaseapp.util.LoadinDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StorageFragment extends Fragment {
    //Referencia para o Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;
    private Button btnGaleria;
    private ImageView imageView;
    private Uri imageUri = null;
    private EditText editNome;

    //referencia para um nó RealtimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference("uploads").child(auth.getUid());

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        btnUpload = layout.findViewById(R.id.storage_btn_upload);
        imageView = layout.findViewById(R.id.storage_image_cel);
        btnGaleria = layout.findViewById(R.id.storage_btn_galeria);
        editNome = layout.findViewById(R.id.storage_edit_nome);

        btnUpload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Digite um nome para a imagem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null){
                uploadImagemUri();
            }

            else {
                uploadImagemByte();
            }

        });

        btnGaleria.setOnClickListener(v -> {
            //intent implicita
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            //inicia uma activity, e espera um retorno (foto)
            startActivityForResult(intent, 111);
        });

        return layout;
    }

    private void uploadImagemUri() {
        LoadinDialog dialog = new LoadinDialog(getActivity(), R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);

        //referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();

        //criando uma referencia para a imagem no Storage
        StorageReference imagemRef = storage.getReference().child("imagem/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Upload feito com sucesso!", Toast.LENGTH_SHORT).show();

            //Inserir dados da imagem no RealtimeDatabase

            //pegar a URL da imagem
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                //inserir no database

                //criando referencia(database) do upload
                DatabaseReference refUpload = database.push();
                String id = refUpload.getKey();

                Upload upload = new Upload(id, nome, uri.toString());
                //Salvando upload no db
                refUpload.setValue(upload).addOnSuccessListener(aVoid -> {
                    dialog.dismissDialog();
                    Toast.makeText(getActivity(), "Uplaod Feito com Sucesso!", Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

                    //Voltar para a fragment inicial
                    navController.navigateUp();
                });

            });

        })

        .addOnFailureListener(e -> {
            e.printStackTrace();
        });

    }

    //retorna o tipo(.pngm .jpg) da imagem
    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getActivity().getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    //resultado do startActivityResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Result", "RequestCode: "+ requestCode +", resultCode: " +resultCode);

        if (requestCode==111 && resultCode== Activity.RESULT_OK){
            //caso o usuario selecionou outra imagem da galeria

            //Endereço da imagem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }

    public byte[] convertImagem2Byte(ImageView imageView){
        //Converter ImageView -> byte[]
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        //objeto baos
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //Upload de uma imagem convertida para byte
    public void uploadImagemByte(){
        byte[] data = convertImagem2Byte(imageView);

        //Criar uma referencia para a imagem no storage
        StorageReference imagemRef = storage.getReference().child("imagem/01.jpg");

        //Realiza o Upload da imagem
        imagemRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Upload feito com suceso!", Toast.LENGTH_SHORT).show();
            Log.i("UPLOAD", "Sucesso");
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

        //storage.getReference().putBytes();
    }

}
