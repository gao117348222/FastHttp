package com.gx303;

import com.google.gson.Gson;
import com.gx303.http.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * Created by Administrator on 2016/5/3.
 */
public class OkHttpCall<T> implements Call<T>  {
    final String baseurl;
    final Method method;
    final Annotation[] methodAnnotations;
    final Annotation[][] parameterAnnotationsArray;
    Object[] args;
    String httpMethod;
    String url;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    Request request;
    okhttp3.Call rawCall;
    public OkHttpCall(String baseurl,Method method,Object[] args) {
        // TODO Auto-generated constructor stub
        this.baseurl=baseurl;
        this.method=method;
        this.args=args;
        this.methodAnnotations=method.getAnnotations();
        this.parameterAnnotationsArray = method.getParameterAnnotations();
        for (Annotation annotation : methodAnnotations) {
//           L(annotation.toString());
            parseMethodAnnotation(annotation);
        }

        if(httpMethod.equals("GET"))
        {
            int parameterCount = parameterAnnotationsArray.length;
            for(int j=0;j<parameterCount;j++)
            {
                for(int k=0;k<parameterAnnotationsArray[j].length;k++)
                {
                    Annotation annotation=parameterAnnotationsArray[j][k];

                    if(annotation instanceof Path)
                    {
                        String name=((Path) annotation).value();
//                        L(j+" "+k+"||"+name);
                        //在这里修改url
                        if(args.length>=j+1)
                        {
                            url=url.replace("{"+name+"}",(String)args[j]);
                        }
                    }
                }
            }
//            L("GET最终url"+url);
            request=new Request.Builder().url(url).build();
        }
        else if(httpMethod.equals("POST"))
        {
//            L("POST最终url"+url);
            request=new Request.Builder().url(url).post(RequestBody.create(JSON,new Gson().toJson(args[0]))).build();
        }

    }
    public Call<T> getThis()
    {
        return this;
    }
    @Override
    public Response<T> execute()  throws IOException {
        // TODO Auto-generated method stub
        rawCall=client.newCall(request);
        okhttp3.Response response=rawCall.execute();
        return parseResponse(response);
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        // TODO Auto-generated method stub
        rawCall=client.newCall(request);
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure(getThis(),e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                callback.onResponse(getThis(),parseResponse(response));
            }
        });
    }

    @Override
    public void cancel() {
        if(rawCall!=null)
        {
            if(rawCall.isCanceled())
            {
                rawCall.cancel();
            }
        }
    }

    @Override
    public boolean isCanceled() {
       if(rawCall!=null)
       {
           return  rawCall.isCanceled();
       }
       else
       {
           return false;
       }
    }

    private void parseMethodAnnotation(Annotation annotation)
    {
        if(annotation instanceof GET)
        {
            parseHttpMethodAndPath("GET",((GET) annotation).value());
        }
        else if (annotation instanceof POST)
        {
            parseHttpMethodAndPath("POST",((POST) annotation).value());
        }
    }
    private void parseHttpMethodAndPath(String httpMethod, String value)
    {
        this.httpMethod=httpMethod;
        this.url=baseurl+value;
//        L("httpMethod:"+httpMethod+" value:"+value);
    }
    T toResponse(ResponseBody body) throws IOException {
        Type t1=Utils.getCallResponseType(method.getGenericReturnType());
        Gson gson=new Gson();
        return gson.fromJson(body.string(), t1);
    }
    Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
        ResponseBody rawBody = rawResponse.body();

        // Remove the body's source (the only stateful object) so we can pass the response along.
        rawResponse = rawResponse.newBuilder()
                .body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
                .build();

        int code = rawResponse.code();
        if (code < 200 || code >= 300) {
            try {
                // Buffer the entire body to avoid future I/O.
                ResponseBody bufferedBody = Utils.buffer(rawBody);
                return Response.error(bufferedBody, rawResponse);
            } finally {
                rawBody.close();
            }
        }

        if (code == 204 || code == 205) {
            return Response.success(null, rawResponse);
        }

        ExceptionCatchingRequestBody catchingBody = new ExceptionCatchingRequestBody(rawBody);
        try {
            T body = toResponse(catchingBody);
            return Response.success(body, rawResponse);
        } catch (RuntimeException e) {
            // If the underlying source threw an exception, propagate that rather than indicating it was
            // a runtime exception.
            catchingBody.throwIfCaught();
            throw e;
        }
    }
    static final class NoContentResponseBody extends ResponseBody {
        private final MediaType contentType;
        private final long contentLength;

        NoContentResponseBody(MediaType contentType, long contentLength) {
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        @Override public MediaType contentType() {
            return contentType;
        }

        @Override public long contentLength() {
            return contentLength;
        }

        @Override public BufferedSource source() {
            throw new IllegalStateException("Cannot read raw response body of a converted body.");
        }
    }
    static final class ExceptionCatchingRequestBody extends ResponseBody {
        private final ResponseBody delegate;
        IOException thrownException;

        ExceptionCatchingRequestBody(ResponseBody delegate) {
            this.delegate = delegate;
        }

        @Override public MediaType contentType() {
            return delegate.contentType();
        }

        @Override public long contentLength() {
            return delegate.contentLength();
        }

        @Override public BufferedSource source() {
            return Okio.buffer(new ForwardingSource(delegate.source()) {
                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    try {
                        return super.read(sink, byteCount);
                    } catch (IOException e) {
                        thrownException = e;
                        throw e;
                    }
                }
            });
        }

        @Override public void close() {
            delegate.close();
        }

        void throwIfCaught() throws IOException {
            if (thrownException != null) {
                throw thrownException;
            }
        }
    }
//    public void L(String s)
//    {
//        Log.e("Unity", s);
//    }
}
