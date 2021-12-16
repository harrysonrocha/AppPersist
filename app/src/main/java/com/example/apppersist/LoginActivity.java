package com.example.apppersist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText etEmail, etSenha;
    private Button btnEntrar, btnCadastro;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnCadastro = findViewById(R.id.btnCadastro);

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastro();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrar();
            }
        });
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String texto6 = getResources().getString(R.string.bemvindo);

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.bemvindo),Toast.LENGTH_LONG).show();
                }
            }
        };

        auth.addAuthStateListener(authStateListener);


    }

    private void entrar(){


        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Email e senha obrigatórios!",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                            }else{
                                Toast.makeText(LoginActivity.this, "Email ou senha inválidos!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }


    }

    private void cadastro(){
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Email e senha obrigatórios!",Toast.LENGTH_LONG).show();
        }else{
            auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                            }
                        }
                    });
        }

    }


}