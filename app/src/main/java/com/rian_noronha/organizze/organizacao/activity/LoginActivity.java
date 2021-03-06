package com.rian_noronha.organizze.organizacao.activity;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.rian_noronha.organizze.R;
import com.rian_noronha.organizze.organizacao.config.ConfiguracaoFirebase;
import com.rian_noronha.organizze.organizacao.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmailEntrar, editSenhaEntrar;
    private Button btnEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        this.editEmailEntrar = findViewById(R.id.editEmailEntrar);
        this.editSenhaEntrar = findViewById(R.id.editSenhaEntrar);
        this.btnEntrar = findViewById(R.id.btnEntrar);

        this.btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String campoEmail = editEmailEntrar.getText().toString();
                String campoSenha = editSenhaEntrar.getText().toString();

                if(validaCampos(campoEmail, campoSenha)) {
                    usuario = new Usuario();
                    usuario.setEmail(campoEmail);
                    usuario.setSenha(campoSenha);

                    validarLogin();
                }



            }
        });


    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }


    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                  abrirTelaPrincipal();
                }else{

                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha n??o constam a nenhum usu??rio cadastrado:(";
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usu??rio n??o se encontra cadastrado:(";
                    }catch (Exception e){
                        excecao = "Erro no cadastramento: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(
                            getApplicationContext(),
                            excecao,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

    }

    public boolean validaCampos(String campoEmail, String campoSenha){

        boolean camposValidados = false;

        if(!campoEmail.isEmpty()){

            if(!campoSenha.isEmpty()){
                camposValidados = true;
            }else{
                Toast.makeText(
                        LoginActivity.this,
                        "Entre com sua senha, por favor:)",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }else{
            Toast.makeText(
                    LoginActivity.this,
                    "Entre com seu email, por favor:)",
                    Toast.LENGTH_SHORT
            ).show();
        }


        return camposValidados;



    }

}