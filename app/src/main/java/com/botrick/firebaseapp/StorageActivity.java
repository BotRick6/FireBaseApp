package com.botrick.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    //Referencia para o Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;
    private Button btnGaleria;
    private ImageView imageView;
    private Uri imageUri = null;
    private EditText editNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        btnUpload = findViewById(R.id.storage_btn_upload);
        imageView = findViewById(R.id.storage_image_cel);
        btnGaleria = findViewById(R.id.storage_btn_galeria);
        editNome = findViewById(R.id.storage_edit_nome);

        btnUpload.setOnClickListener(v -> {
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "Digite um nome para a imagem", Toast.LENGTH_SHORT).show();
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
            intent.setType("imagem/*");

            startActivityForResult(intent, 111);
        });

    }

    private void uploadImagemUri() {
        String tipo = getFileExtension(imageUri);

        //referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();
        StorageReference imagemRef = storage.getReference().child("imagem/"+nome+"."+tipo);

        imagemRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upload feito com sucesso!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

    }

    //retorna o tipo(.pngm .jpg) da imagem
    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Result", "RequestCode: "+ requestCode +", resultCode: " +resultCode);

        if (resultCode==111 && resultCode== Activity.RESULT_OK){
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
                Toast.makeText(this, "Upload feito com suceso!", Toast.LENGTH_SHORT).show();
                Log.i("UPLOAD", "Sucesso");
            }).addOnFailureListener(e -> {
                e.printStackTrace();
            });

            //storage.getReference().putBytes();
        }
    }

