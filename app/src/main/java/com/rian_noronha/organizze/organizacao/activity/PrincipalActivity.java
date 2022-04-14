package com.rian_noronha.organizze.organizacao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.rian_noronha.organizze.R;
import com.rian_noronha.organizze.databinding.ActivityPrincipalBinding;
import com.rian_noronha.organizze.organizacao.config.ConfiguracaoFirebase;
import com.rian_noronha.organizze.organizacao.helper.AdapterPrincipal;
import com.rian_noronha.organizze.organizacao.helper.Base64Custom;
import com.rian_noronha.organizze.organizacao.helper.Movimentacao;
import com.rian_noronha.organizze.organizacao.model.Usuario;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private ActivityPrincipalBinding binding;
    private MaterialCalendarView calendarViewPrincipal;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;
    private DatabaseReference usuarios;
    private TextView textSaudacao, textSaldo;
    private Double receitaTotal = 0.0;
    private Double despesaTotal = 0.0;
    private Double resumoUsuario = 0.0;
    private RecyclerView recyclerViewPrincipal;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    AdapterPrincipal adapterPrincipal;
    private String mesAnoSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setTitle("Organizze");


        this.calendarViewPrincipal  = findViewById(R.id.calendarViewPrincipal);
        this.textSaudacao           = findViewById(R.id.textSaudacao);
        this.textSaldo              = findViewById(R.id.textSaldo);
        this.recyclerViewPrincipal = findViewById(R.id.recyclerPrincipal);
        configurarCalendarView();
        swipe();

        //configurar o adapter
        adapterPrincipal = new AdapterPrincipal(movimentacoes, this);

        //configurar o recycler
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerViewPrincipal.setLayoutManager(layoutManager);
        this.recyclerViewPrincipal.setHasFixedSize(true);
        this.recyclerViewPrincipal.setAdapter(adapterPrincipal);



    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //Defino como será o movimento.

                //IDLE para deixar este movimenot inativo.
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;

                //Deixar o usuário arrastar do começo para o fim.
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Pelo viewHolder dá para excluir.

                excluirMovimentacao(viewHolder);

                Log.i("swipe", "Item foi arrastado");

            }
        };

        //linkar meu ajudador de toque de item com meu recycler.
        new ItemTouchHelper(itemTouch).attachToRecyclerView(this.recyclerViewPrincipal);
    }

    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            //título
            alertDialog.setTitle("Excluir Movimentação da conta");


            //mensagem
            alertDialog.setMessage("Fi de Deus, tu tem certeza que quer excluir?");


            //cancelável
            alertDialog.setCancelable(false);

            //settar o lado positivo da coisa
            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int position = viewHolder.getAdapterPosition();
                    movimentacao = movimentacoes.get(position);

                    String emailUsuario = firebaseAuth.getCurrentUser().getEmail();
                    String idUsuario = Base64Custom.codificarBase64( emailUsuario );
                    //chego até as datas da movimentacoes no Firebase
                    movimentacaoRef = databaseReference.child("movimentacao")
                            .child( idUsuario )
                            .child( mesAnoSelecionado );

                    //chego ao item que quero despachar pela sua chave
                    movimentacaoRef.child(movimentacao.getChave()).removeValue();

                    //notificando o adapter do item que foi excluído.
                    adapterPrincipal.notifyItemRemoved(position);

                    atualizarSaldo();
                }
            });

            //settar o lado negativo da coisa
            alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Cancelado",
                            Toast.LENGTH_SHORT
                    ).show();

                    //notificar o adapter para voltar o item ao normal
                    adapterPrincipal.notifyDataSetChanged();
                }
            });

            //criar o alert dialog
            alertDialog.create();

            //exibir
            alertDialog.show();
    }

    public void atualizarSaldo(){

        //testando para uma receita
        if(this.movimentacao.getTipo().equals("r")){
            this.receitaTotal = this.receitaTotal - this.movimentacao.getValor();

            String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
            String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

            //usuarios é a referência lá do Firebase. Bate no e-mail do usuário.
            this.usuarios = this.databaseReference.child("usuarios").child(userEmailEncriptado);

            this.usuarios.child("receitaTotal").setValue(receitaTotal);
        }else{
            this.despesaTotal = this.despesaTotal - movimentacao.getValor();

            String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
            String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

            //usuarios é a referência lá do Firebase. Bate no e-mail do usuário.
            this.usuarios = this.databaseReference.child("usuarios").child(userEmailEncriptado);

            this.usuarios.child("despesaTotal").setValue(despesaTotal);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();

    }

    /*public void recuperarMovimentacoes(){
        String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

        this.movimentacaoRef = databaseReference.child("movimentacao")
                        .child(userEmailEncriptado)
                        .child(mesAnoSelecionado);

        valueEventListenerMovimentacoes = this.movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Começo limpando as movimentações da minha lista
                movimentacoes.clear();

                for(DataSnapshot dados: snapshot.getChildren()){

                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacoes.add(movimentacao);
                }

                //notificar meu adapter que houveram modificações
                adapterPrincipal.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }*/

    public void recuperarMovimentacoes(){

        String emailUsuario = firebaseAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = databaseReference.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    //recupero a chave da minha movimentacao by dados.
                    movimentacao.setChave(dados.getKey());
                    movimentacoes.add( movimentacao );

                }

                adapterPrincipal.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){
        String userEmailDesencriptado = this.firebaseAuth.getCurrentUser().getEmail();
        String userEmailEncriptado = Base64Custom.codificarBase64(userEmailDesencriptado);

        //usuarios é a referência lá do Firebase. Bate no e-mail do usuário.
       this.usuarios = this.databaseReference.child("usuarios").child(userEmailEncriptado);
       this.valueEventListenerUsuario = usuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Evento", "Evento iniciado.");

                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);

                textSaudacao.setText("Olá, " + usuario.getNome());
                textSaldo.setText("R$"+resultadoFormatado);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //uso o get para pegar o inflater e inflar com o inflate.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_Sair:
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void configurarCalendarView(){
        CharSequence[] meses = {"Jan", "Fev", "Mar", "Abr","Maio", "Jun", "Jul", "Agos", "Set", "Out", "Nov", "Dez"};
        this.calendarViewPrincipal.setTitleMonths(meses);

        CalendarDay dataAtual = calendarViewPrincipal.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth()) );
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear() );


        this.calendarViewPrincipal.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth()) );
                mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

                movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
                Log.i("Evento", "mes:" + mesAnoSelecionado);
                recuperarMovimentacoes();
            }
        });
    }

    @Override
    protected void onStop() {
        //vai ajudar o Firebase a não ficar atualizando quando o app não estiver sendo usado.
        super.onStop();
        this.usuarios.removeEventListener(this.valueEventListenerUsuario);
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
        Log.i("Evento", "Evento removido.");
    }
}