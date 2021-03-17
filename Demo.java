package com.xianghuanji.http.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xianghuanji.http.converter.serializationChecker.SerializationCheckerConverterFactory;
import com.xianghuanji.http.converter.serializationChecker.SerializationCheckerConverterListener;

import java.lang.annotation.Annotation;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Demo {
    public static void main(String[] args) {


        //伪代码

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();

        new Retrofit.Builder()
                .baseUrl("baseUrl")

                //必须要 添加在 GsonConverterFactory 的签名
                .addConverterFactory(SerializationCheckerConverterFactory.create(gson, new SerializationCheckerConverterListener() {
                    @Override
                    public void onSerializationFailed(String url, String errorMessage, Annotation[] annotations, Retrofit retrofit) {

                    }
                }))
                .addConverterFactory(GsonConverterFactory.create(gson));
    }
}
