package com.rian_noronha.organizze.organizacao.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rian_noronha.organizze.organizacao.config.ConfiguracaoFirebase;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor;
    private String chave;

    public Movimentacao() {
    }




    public void salvar(String dataEscolhida){

        //Recuperando o e-mail do usuário dentro da minha aplicação:
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String userEmail = Base64Custom.codificarBase64(firebaseAuth.getCurrentUser().getEmail());
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();

        String mesAno = DateCustom.mesAnoDataEscolhida(dataEscolhida);

         databaseReference.child("movimentacao")
                 .child(userEmail)
                 .child(mesAno)
                 .push()
                 .setValue(this);//o objeto movimentacao
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
