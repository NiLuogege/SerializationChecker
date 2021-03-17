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
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.xianghuanji.http.utils.RetrofitUtils;
import com.xianghuanji.util.utils.string.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

final class SerializationCheckerResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Converter<ResponseBody, T> delegate;
    private final Annotation[] annotations;
    private final Retrofit retrofit;
    private final SerializationCheckerConverterListener listener;
    private final TypeAdapter<?> adapter;
    private final String requestUrl;

    SerializationCheckerResponseBodyConverter(Converter<ResponseBody, T> delegate, Gson gson, Type type, Annotation[] annotations, Retrofit retrofit, SerializationCheckerConverterListener listener) {
        this.delegate = delegate;
        this.gson = gson;
        this.annotations = annotations;
        this.retrofit = retrofit;
        this.listener = listener;

        adapter = gson.getAdapter(TypeToken.get(type));

        requestUrl = retrofit.baseUrl().toString() + RetrofitUtils.getRequestUrl(annotations);

    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        //创建一个新的 ResponseBody 给后面的 Converter 使用
        MediaType contentType = value.contentType();
        String bodyString = value.string();
        ResponseBody newResponseBody = ResponseBody.create(contentType, bodyString);


        StringReader reader = new StringReader(bodyString);
        JsonReader jsonReader = gson.newJsonReader(reader);

        try {
            adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            return delegate.convert(newResponseBody);
        } catch (Exception e) {

            String errorMessage = StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : e.getLocalizedMessage();

            if (listener != null) {
                listener.onSerializationFailed(requestUrl, errorMessage, annotations, retrofit);
            }

            return delegate.convert(newResponseBody);
        } finally {
            value.close();
        }
    }
}
