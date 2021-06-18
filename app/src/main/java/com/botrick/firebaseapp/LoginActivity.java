package com.botrick.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private Button btnCadastrar;
    private Button btnLogin;
    private EditText editEmail, editSenha;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnCadastrar = findViewById(R.id.login_btn_cadastrar);
        btnLogin = findViewById(R.id.login_btn_logar);

        editEmail  = findViewById(R.id.login_edit_email);
        editSenha = findViewById(R.id.login_edit_senha);

        //caso usuario logado
        if(auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            //passar o email para a MainActivity
            intent.putExtra("email", email);
            startActivity(intent);
        }

        btnCadastrar.setOnClickListener(v -> {Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            logar();
        });

    }

    public void logar(){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        if (email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();

            return;
        }

        auth.signInWithEmailAndPassword(email, senha)
        .addOnSuccessListener(authResult -> {
            Toast.makeText(this,"Bem Vindo!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);
        })

        //Listener de falha
        .addOnFailureListener(e -> {
            /*
            //parametro e -> Exception
            Toast.makeText(this, "Erro: " + e.getClass().toString(), Toast.LENGTH_SHORT).show();

            Log.e("Erro", "Mensagem: " + e.getMessage() + "Classe: " + e.getClass().toString());
            */

            try {
                //disparando a exception
                throw e;
            }

            catch (FirebaseAuthInvalidUserException userException){
                //exceção para e-mail invalido
                Toast.makeText(this, "E-mail invalido!", Toast.LENGTH_SHORT).show();
            }

            catch (FirebaseAuthInvalidCredentialsException credException){
                //exceção para senha incorreta
                Toast.makeText(this, "Senha incorreta!", Toast.LENGTH_SHORT).show();
            }

            catch (Exception ex){
                //exceção genérica
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
            }

        });

    }

}
