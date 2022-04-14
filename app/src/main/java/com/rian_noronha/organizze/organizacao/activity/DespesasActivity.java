package com.rian_noronha.organizze.organizacao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText  editDataDespesa, editCategoriaDespesa, editDescricaoDespesa;
    private EditText editValorDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    //despesa que o usuário vai botar na hora
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        this.editDataDespesa            = findViewById(R.id.editDataDespesa);
        this.editCategoriaDespesa       = findViewById(R.id.editCategoriaDespesa);
        this.editDescricaoDespesa       = findViewById(R.id.editDescricaoDespesa);
        this.editValorDespesa           = findViewById(R.id.editValorReceita);

        //Preencer a data do comapo editDataDespesa com uma data que esteja autal!
        this.editDataDespesa.setText(DateCustom.dataAtual());

        //Chamar ligeiro a recuperação de despesa total
        recuperarDespesaTotal();


    }

    public void salvarDespesa(View view){

       if(validarCampos()){
           String data = this.editDataDespesa.getText().toString();
           Double valorRecuperado = Double.parseDouble(this.editValorDespesa.getText().toString());
           this.movimentacao = new Movimentacao();

           this.movimentacao.setValor(valorRecuperado);
           this.movimentacao.setCategoria(this.editCategoriaDespesa.getText().toString());
           this.movimentacao.setDescricao(this.editDescricaoDespesa.getText().toString());
           this.movimentacao.setData(data);
           this.movimentacao.setTipo("d");

          Double despesaAtualizada = this.despesaTotal + valorRecuperado;

          atualizarDespesa(despesaAtualizada);


           this.movimentacao.salvar(data);
       }


    }

    public void recuperarDespesaTotal(){
        //Pego o email do usuário corrente. Depois, encripto para a base 64 para poder caminhar no Firebase.
        String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);
        DatabaseReference usuarioRef = this.databaseReference.child("usuarios").child(userEmailEncriptado);


        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void atualizarDespesa(Double despesa){
        String userEmailDesencriptado = firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

        DatabaseReference usuarioRef = databaseReference.child("usuarios").child(userEmailEncriptado);

        usuarioRef.child("despesaTotal").setValue(despesa);


    }


    public boolean validarCampos(){

            boolean camposValidados = false;

            String textValor = this.editValorDespesa.getText().toString();
            String textCategoria = this.editCategoriaDespesa.getText().toString();
            String textDescricao = this.editDescricaoDespesa.getText().toString();
            String textData = this.editDataDespesa.getText().toString();

            if(!textValor.isEmpty()){
                if(!textCategoria.isEmpty()){
                    if(!textDescricao.isEmpty()){
                        if(!textData.isEmpty()){
                            camposValidados = true;
                        }else{
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Entre com a data!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "Entre com a descrição!",
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
                        "Entre com o valor da despesa!",
                        Toast.LENGTH_SHORT
                ).show();
            }


       return camposValidados;


    }

}