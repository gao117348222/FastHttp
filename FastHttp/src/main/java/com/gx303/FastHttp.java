package com.gx303;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/4/28.
 */
public final class FastHttp {
    final String baseUrl;
    FastHttp(Builder builder){
        this.baseUrl=builder.baseUrl;
    }
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service)
    {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler(){

                    @Override
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
//					concreteClass=new proxy();

                        // TODO Auto-generated method stub
                        System.out.println("proxy:"+proxy.getClass().getName());
                        System.out.println("method:"+method.getName());
                        System.out.println("args:"+args[0].getClass().getName());

                        System.out.println("Before invoke method...");
//		            Object object=method.invoke(concreteClass, args);//普通的Java反射代码,通过反射执行某个类的某方法
//		            ServiceMethod sm=new ServiceMethod.Builder<T>(method).build();
                        OkHttpCall call=new OkHttpCall(baseUrl,method,args);
                        //System.out.println(((ConcreteClass)concreteClass).targetMethod(10)+(Integer)args[0]);
                        System.out.println("After invoke method...");

                        return call;
                    }});
    }


    public static final class Builder{
        private String  baseUrl;
        public Builder(String baseUrl)
        {
            this.baseUrl=baseUrl;
        }
        public FastHttp build()
        {
            return new FastHttp(this);
        }
    }


//    private final OkHttpClient client = new OkHttpClient();
//    public void GET(FastRequset fastRequset)
//    {
////        Request request = new Request.Builder()
////                .url(fastRequset.url)
////                .build();
//        String url=fastRequset.url+"?";
//        HashMap<String,String> hm1=getAllField(fastRequset.input,fastRequset.input.getClass());
//        Iterator<Map.Entry<String, String>> entryKeyIterator = hm1.entrySet().iterator();
//        while (entryKeyIterator.hasNext()) {
//            Map.Entry<String, String> e = entryKeyIterator.next();
//            String key=e.getKey();
//            String value=e.getValue();
////            L("key:"+key+"value:"+value);
//            url+=key+"="+value+"&";
//        }
//        url=url.substring(0,url.length()-1);
//        L("最终地址:"+url);
//
//    }
//    public <T>HashMap<String ,String> getAllField(T t1,Class c1)
//    {
//        HashMap<String,String> HM1=new HashMap<String, String>();
//        if(c1.getSuperclass()!=null)
//        {
////            L("有父类");
//            HM1.putAll(getAllField(t1,c1.getSuperclass()));
//        }
////        else
////        {
////            L("无父类");
//            Field[] fields=c1.getDeclaredFields();
//            Field.setAccessible(fields,true);
//            for(int i=0;i<fields.length;i++)
//            {
//                try
//                {
////                    L("key:" + fields[i].getName() + "value" + fields[i].get(t1));
//                    String name=fields[i].getName();
//                    Object value=fields[i].get(t1);
//                    if(name!=null&&!name.contains("$")&&value!=null) {
//                        HM1.put(fields[i].getName(), fields[i].get(t1).toString());
//                    }
//                }
//                catch (IllegalAccessException e)
//                {
//                    L("IllegalAccessException "+e.toString());
//                }
//
//            }
////        }
//        return HM1;
//    }


    public void L(String s)
    {
        Log.e("Unity",s);
    }

}
