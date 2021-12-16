package com.example.apppersist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private ArrayAdapter adapter;
    private List<Produto> listaDeProdutos;

    private FirebaseDatabase database;
    private DatabaseReference references;
    private ChildEventListener childEventListener;
    private Query query;



    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    finish();
                }
            }
        };



        lvProdutos = findViewById(R.id.lvProdutos);

        listaDeProdutos = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDeProdutos);
        lvProdutos.setAdapter(adapter);


        FloatingActionButton fab = findViewById(R.id.fab);

        String texto2 = getResources().getString(R.string.CEP);
        String texto3 = getResources().getString(R.string.excluir);
        String texto4 = getResources().getString(R.string.cancelar);
        String texto5 = getResources().getString(R.string.sim);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "inserir");
                startActivity(intent);
            }
        });

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto prodSelecionado = listaDeProdutos.get( position );
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "editar");
                intent.putExtra("idProduto" , prodSelecionado.getId());
                intent.putExtra("nome" , prodSelecionado.getNome());
                intent.putExtra("categoria" , prodSelecionado.getCategoria());
                intent.putExtra("quantidade" , prodSelecionado.getQuantidade());
                intent.putExtra("data" , prodSelecionado.getData());
                startActivity(intent);
            }
        });

        lvProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                excluir(position);
                return true;
            }
        });



    }

    private void excluir(int posicao){
        Produto prod = listaDeProdutos.get( posicao );
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getResources().getString(R.string.excluir));
        alerta.setIcon(android.R.drawable.ic_delete);
        alerta.setMessage(getResources().getString(R.string.CEP) + " " + prod.getNome() +"?");
        alerta.setNeutralButton(getResources().getString(R.string.cancelar), null);

        alerta.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                references.child("produtos").child( prod.getId()).removeValue();
            }
        });
        alerta.show();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
       }

    @Override
    protected void onStart() {
        super.onStart();
        carregarProdutos();
    }

    private void carregarProdutos(){

        listaDeProdutos.clear();

        database = FirebaseDatabase.getInstance();
        references =database.getReference();
        query = references.child("produtos").orderByChild("nome");


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Produto prod = new Produto();
                prod.setId( snapshot.getKey() );
                prod.setNome( snapshot.child("nome").getValue(String.class));
                prod.setCategoria( snapshot.child("categoria").getValue(String.class));
                prod.setQuantidade( snapshot.child("quantidade").getValue(Double.class));
                prod.setData( snapshot.child("data").getValue(String.class));


                listaDeProdutos.add(prod);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String idProduto = snapshot.getKey();

                    for (Produto prod : listaDeProdutos) {
                        if (!(!prod.getId().equals(idProduto))) {
                            prod.setNome(snapshot.child("nome").getValue(String.class));
                            prod.setCategoria(snapshot.child("categoria").getValue(String.class));
                            prod.setQuantidade(snapshot.child("quantidade").getValue(Double.class));
                            prod.setData( snapshot.child("data").getValue(String.class));

                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idProduto = snapshot.getKey();
                for (Produto prod: listaDeProdutos) {
                    if( prod.getId().equals( idProduto)){
                        listaDeProdutos.remove( prod );
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query.addChildEventListener( childEventListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        query.removeEventListener( childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuAdicionarProduto) {

            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            intent.putExtra("acao", "inserir");
            startActivity(intent);

            return true;
        }

        if (id == R.id.menuSair){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}