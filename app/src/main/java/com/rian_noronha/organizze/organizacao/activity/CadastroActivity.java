package com.rian_noronha.organizze.organizacao.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.rian_noronha.organizze.R;
import com.rian_noronha.organizze.organizacao.config.ConfiguracaoFirebase;
//import com.rian_noronha.organizze.organizacao.helper.Base64Custom;
import com.rian_noronha.organizze.organizacao.helper.Base64Custom;
import com.rian_noronha.organizze.organizacao.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    //private TextView testando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Dá um título para minha Toolbar
        getSupportActionBar().setTitle("Cadastro");


        this.editNome       = findViewById(R.id.editNome);
        this.editEmail      = findViewById(R.id.editEmail);
        this.editSenha      = findViewById(R.id.editSenha);
       // this.testando       = findViewById(R.id.teste);

        this.buttonCadastrar = findViewById(R.id.buttonCadastrar);


        this.buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String campoNome = editNome.getText().toString();
                String campoEmail = editEmail.getText().toString();
                String campoSenha = editSenha.getText().toString();

                if(validaCampos(campoNome, campoEmail, campoSenha)){

                    usuario = new Usuario();
                    usuario.setNome(campoNome);
                    usuario.setEmail(campoEmail);
                    usuario.setSenha(campoSenha);
                    cadastrarUsuario();
                }

            }
        });


    }


    private void cadastrarUsuario() {

        //pegando a variável de referência para autenticar um user:
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setIdUsuario( idUsuario );
                    usuario.salvar();
                    finish();
                }else{

                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                            excecao = "Sua senha tá fraquinha. Entre com uma mais nutrida:)";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao = "Entre com o email da forma certinha:)";
                    }catch(FirebaseAuthUserCollisionException e){
                        excecao = "Aqui dentro já tem esse email:)";
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

    private boolean validaCampos(String campoNome, String campoEmail, String campoSenha) {

        boolean camposValidados = false;

        if(!campoNome.isEmpty()){
            if(!campoEmail.isEmpty()){
                if(!campoSenha.isEmpty()){
                    camposValidados = true;
                }else{
                    Toast.makeText(
                           CadastroActivity.this,
                            "Entre com sua senha!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }else{
                Toast.makeText(
                        CadastroActivity.this,
                        "Entre com seu e-mail!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }else{
            Toast.makeText(
                    CadastroActivity.this,
                    "Entre com seu nome!",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return camposValidados;


    }



}