package com.rian_noronha.organizze.organizacao.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rian_noronha.organizze.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterPrincipal extends RecyclerView.Adapter<AdapterPrincipal.MyViewHolder> {

    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Context context;

    public AdapterPrincipal(List<Movimentacao> movimentacoes, Context context) {
        this.movimentacoes = movimentacoes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemLista = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.recycler_principal, parent, false);

       return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Movimentacao mov = this.movimentacoes.get(position);
        holder.titulo.setText(mov.getDescricao());
        holder.categoria.setText(mov.getCategoria());
        holder.valor.setText(String.valueOf(mov.getValor()));
        holder.valor.setTextColor(this.context.getResources().getColor(R.color.colorAccentReceita));

        //mudar algumas coisinhas conforme o tipo da movimentacao:
        if(mov.getTipo().equals("d")){
            holder.valor.setTextColor(this.context.getResources().getColor(R.color.colorAccent));
            holder.valor.setText("-" + mov.getValor());
        }

    }

    @Override
    public int getItemCount() {
        return this.movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo, categoria, valor;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titulo = itemView.findViewById(R.id.textAdapterTitulo);
            this.categoria = itemView.findViewById(R.id.textAdapterCategoria);
            this.valor = itemView.findViewById(R.id.textAdapterValor);
        }
    }


}
