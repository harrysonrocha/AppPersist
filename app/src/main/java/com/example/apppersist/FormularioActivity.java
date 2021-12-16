package com.example.apppersist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioActivity extends AppCompatActivity {

    private EditText etNome;
    private EditText etQuantidade;
    private Spinner spCategorias;
    private EditText etData;
    private Button btnSalvar;
    private String acao;
    private Produto produto;

    private FirebaseDatabase database;
    private DatabaseReference references;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        database = FirebaseDatabase.getInstance();
        references = database.getReference();

        etNome = findViewById(R.id.etNome);
        spCategorias =  findViewById(R.id.spCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);
        etQuantidade = findViewById(R.id.etQuantidade);
        etData = findViewById(R.id.etData);



        acao = getIntent().getStringExtra("acao");
        if( acao.equals("editar") ){
            carregarFormulario();
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        });

        String texto = getResources().getString(R.string.alert);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                   finish();
                }
            }
        };


    }

    private void carregarFormulario(){
        String id = getIntent().getStringExtra("idProduto");

        produto = new Produto();
        produto.setId( id );
        produto.setNome( getIntent().getStringExtra("nome") );
        produto.setCategoria( getIntent().getStringExtra("categoria") );
        produto.setData( getIntent().getStringExtra("data") );

        etNome.setText( produto.getNome() );
        String[] categorias = getResources().getStringArray(R.array.categorias);
        for (int i = 1; i < categorias.length ;i++){
            if( produto.getCategoria().equals( categorias[i] ) ){
                spCategorias.setSelection(i);
                break;
            }
        }
    }
    private void salvar(){
        String nome = etNome.getText().toString();
        String quantidade = etQuantidade.getText().toString();
        String data = etData.getText().toString();
        if( nome.isEmpty() || quantidade.isEmpty() || spCategorias.getSelectedItemPosition()  == 0){
            Toast.makeText(this, getResources().getString(R.string.alert), Toast.LENGTH_LONG ).show();
        }else{
            if( acao.equals("inserir")) {
                produto = new Produto("lista vazia", "");
            }
            produto.setNome( nome );
            double qtd = Double.valueOf(quantidade);
            produto.setQuantidade(qtd);
            produto.setCategoria( spCategorias.getSelectedItem().toString() );
            produto.setData(data);

            if( acao.equals("inserir")) {
                references.child("produtos").push().setValue(produto);
                etNome.setText("");
                etQuantidade.setText("");
                spCategorias.setSelection(0, true);
                etData.setText("");
            }else{
                references.child("produtos").child(produto.getId()).setValue(produto);
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menuSair){
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}