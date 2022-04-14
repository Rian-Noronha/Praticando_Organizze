package com.rian_noronha.organizze.organizacao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rian_noronha.organizze.R;
import com.rian_noronha.organizze.organizacao.config.ConfiguracaoFirebase;
import com.rian_noronha.organizze.organizacao.helper.Base64Custom;
import com.rian_noronha.organizze.organizacao.helper.DateCustom;
import com.rian_noronha.organizze.organizacao.helper.Movimentacao;
import com.rian_noronha.organizze.organizacao.model.Usuario;

public class ReceitasActivity extends AppCompatActivity {

    private EditText editValorReceita, editDataReceita, editCategoriaReceita, editDescricaoReceita;
    private Movimentacao movimentacao;
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        this.editValorReceita       = findViewById(R.id.editValorReceita);
        this.editDataReceita        = findViewById(R.id.editDataReceita);
        this.editCategoriaReceita   = findViewById(R.id.editCategoriaReceita);
        this.editDescricaoReceita   = findViewById(R.id.editDescricaoReceita);

        //configurar uma data padrão
        editDataReceita.setText(DateCustom.dataAtual());

        //Método para recuperar, ligeiro, a receita total antes de ser atualizada.
        recuperarReceitaTotal();

    }


    public void salvarReceita(View view){

        //1°, vou validar os campos.
        this.editDataReceita.getText().toString();

        String editValor = this.editValorReceita.getText().toString();
        String editCategoria = this.editCategoriaReceita.getText().toString();
        String editDescricao = this.editDescricaoReceita.getText().toString();
        String editData = this.editDataReceita.getText().toString();

        if(validarCampos(editValor, editCategoria, editValor, editData)){

            Double receitaRecuperada = Double.parseDouble(editValor);

            //Agora, salvo de fato a receita
            this.movimentacao = new Movimentacao();
            this.movimentacao.setValor(receitaRecuperada);
            this.movimentacao.setCategoria(editCategoria);
            this.movimentacao.setDescricao(editDescricao);
            this.movimentacao.setData(editData);
            this.movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + receitaRecuperada;

            atualizarReceita(receitaAtualizada);

            this.movimentacao.salvar(editData);

        }



    }


    public void atualizarReceita(Double valorReceita){

        String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);
        //usuarios é a referência lá do Firebase. Bate no e-mail do usuário.
        DatabaseReference usuariosRef = this.databaseReference.child("usuarios").child(userEmailEncriptado);

        usuariosRef.child("receitaTotal").setValue(valorReceita);


    }

    public void recuperarReceitaTotal(){
        String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

        //usuarios é a referência lá do Firebase. Bate no e-mail do usuário.
        DatabaseReference usuarios = this.databaseReference.child("usuarios").child(userEmailEncriptado);
        usuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public boolean validarCampos(String editValor, String editCategoria, String editDescricao, String editData){
        boolean camposValidados = false;


        if(!editValor.isEmpty()){
            if(!editCategoria.isEmpty()){
                if(!editDescricao.isEmpty()){
                    if(!editData.isEmpty()){
                        camposValidados = true;
                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "Entre com o valor da data!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            "Entre com o valor da descrição!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }else{
                Toast.makeText(
                        getApplicationContext(),
                        "Entre com a categoria!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Entre com o valor da receita!",
                    Toast.LENGTH_SHORT
            ).show();
        }


        return camposValidados;

    }

}