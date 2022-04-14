package com.rian_noronha.organizze.organizacao.helper;

import android.util.Base64;

public class Base64Custom {


    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)","");
    }

    public static String decodificarBase64(String textoCodificado){
        return new String( Base64.decode(textoCodificado, Base64.DEFAULT) );
    }

}
