package com.botrick.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.storage.FirebaseStorage;

public class StorageActivity extends AppCompatActivity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        btnUpload = findViewById(R.id.storage_btn_upload);

        btnUpload.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.google.com/url?sa=i&url=https%3A%2F%2Fazeheb.com.br%2Fblog%2Frick-and-morty-e-precisao-cientifica%2F&psig=AOvVaw0fu7k3mi0vZiu4BjECDSPl&ust=1624065749970000&source=images&cd=vfe&ved=0CAoQjRxqFwoTCPDUs8WCoPECFQAAAAAdAAAAABAD");
            storage.getReference().putFile(uri);
        });

    }

}
