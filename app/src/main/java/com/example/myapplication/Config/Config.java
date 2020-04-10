package com.example.myapplication.Config;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {
    private static final String BASEURL = "http://138.68.42.71/estadope/api/";
    //private static final String BASEURL = "http://157.245.233.151/estado/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if (retrofit==null){
            retrofit = new retrofit2.Retrofit
                    .Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void Mensaje(Context context, String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
    }
}
