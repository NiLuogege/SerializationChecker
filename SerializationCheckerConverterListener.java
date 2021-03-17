package com.xianghuanji.http.converter.serializationChecker;

import java.lang.annotation.Annotation;

import retrofit2.Retrofit;

public interface SerializationCheckerConverterListener {

    void onSerializationFailed(String url, String errorMessage ,Annotation[] annotations, Retrofit retrofit);

}
