package com.example.apppersist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FormularioActivity extends AppCompatActivity {

    private EditText etNome;
    private EditText etQuantidade;
    private Spinner spCategorias;
    private Button btnSalvar;
    private String acao;
    private Produto produto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        etNome = findViewById(R.id.etNome);
        spCategorias =  findViewById(R.id.spCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);
        etQuantidade = findViewById(R.id.etQuantidade);



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


    }

    private void carregarFormulario(){
        int id = getIntent().getIntExtra("idProduto", 0);
        produto = ProdutoDAO.getProdutoById(this, id);
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
        if( nome.isEmpty() || quantidade.isEmpty() || spCategorias.getSelectedItemPosition()  == 0 ){
            Toast.makeText(this, getResources().getString(R.string.alert), Toast.LENGTH_LONG ).show();
        }else{
            if( acao.equals("inserir")) {
                produto = new Produto("lista vazia", "");
            }
            produto.setNome( nome );
            double qtd = Double.valueOf(quantidade);
            produto.setQuantidade(qtd);
            produto.setCategoria( spCategorias.getSelectedItem().toString() );

            if( acao.equals("inserir")) {
                ProdutoDAO.inserir(this, produto);
                etNome.setText("");
                etQuantidade.setText("");
                spCategorias.setSelection(0, true);
            }else{
                ProdutoDAO.editar(this, produto);
                finish();
            }
        }
    }

}