package com.rian_noronha.organizze.organizacao.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = simpleDateFormat.format(data);

        return dataAtual;
    }

    public static String mesAnoDataEscolhida(String data){

        //O split quebra a data se basenda na /. E coloca dentro de um array, neste caso, de String.
        String retornoData[] = data.split("/");

        //1º posição: dia
        String dia = retornoData[0];
        //2° posição: mês
        String mes = retornoData[1];
        //3º posição: ano
        String ano = retornoData[2];

        String mesAno = mes + ano;

        return mesAno;



    }

}
