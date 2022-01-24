package com.daalzzwi.kidalkidal.config;

import com.daalzzwi.kidalkidal.function.FunctionRestApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigRetrofit {

    private static ConfigRetrofit configRetrofit = null;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static FunctionRestApi functionRestApi;

    private ConfigRetrofit() {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout( 5, TimeUnit.SECONDS )
                .readTimeout( 30, TimeUnit.SECONDS )
                .writeTimeout( 15, TimeUnit.SECONDS )
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl( "http://192.168.35.55:3232/" )
                .client( okHttpClient )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        functionRestApi = retrofit.create( FunctionRestApi.class );
    }

    public static ConfigRetrofit getRetrofit() {

        if( configRetrofit == null ) {

            configRetrofit = new ConfigRetrofit();
        }
        return configRetrofit;
    }

    public static FunctionRestApi getFunctionRestApi() {

        return functionRestApi;
    }
}
