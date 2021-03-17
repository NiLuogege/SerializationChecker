/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xianghuanji.http.converter.serializationChecker;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * 序列化 检查 转换器
 *
 * 注意：Retrofit  addConverterFactory 时 必须放到 第一个添加
 */
public final class SerializationCheckerConverterFactory extends Converter.Factory {

    public static SerializationCheckerConverterFactory create() {
        return create(new Gson(),null);
    }


    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static SerializationCheckerConverterFactory create(Gson gson,SerializationCheckerConverterListener listener) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new SerializationCheckerConverterFactory(gson,listener);
    }

    private final Gson gson;
    private final SerializationCheckerConverterListener listener;

    private SerializationCheckerConverterFactory(Gson gson,SerializationCheckerConverterListener listener) {
        this.gson = gson;
        this.listener = listener;
    }


    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        //获取下一个  ResponseBodyConverter 因为  BeanCheckerResponseBodyConverter 是不做 真正结果的返回的
        Converter<ResponseBody, Object> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new SerializationCheckerResponseBodyConverter<>(delegate, gson, type,annotations,retrofit,listener);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        return null;
    }
}
