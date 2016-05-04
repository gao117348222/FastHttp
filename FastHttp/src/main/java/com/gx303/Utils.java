package com.gx303;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by Administrator on 2016/5/3.
 */
final class Utils {
    static ResponseBody buffer(final ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    static Type getCallResponseType(Type returnType) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        return getParameterUpperBound(0, (ParameterizedType) returnType);
    }
    static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException(
                    "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    static Class<?> boxIfPrimitive(Class<?> type) {
        if (boolean.class == type) return Boolean.class;
        if (byte.class == type) return Byte.class;
        if (char.class == type) return Character.class;
        if (double.class == type) return Double.class;
        if (float.class == type) return Float.class;
        if (int.class == type) return Integer.class;
        if (long.class == type) return Long.class;
        if (short.class == type) return Short.class;
        return type;
    }

//    static Class<?> getRawType(Type type) {
//        if (type == null) throw new NullPointerException("type == null");
//
//        if (type instanceof Class<?>) {
//            // Type is a normal class.
//            return (Class<?>) type;
//        }
//        if (type instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) type;
//
//            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
//            // suspects some pathological case related to nested classes exists.
//            Type rawType = parameterizedType.getRawType();
//            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
//            return (Class<?>) rawType;
//        }
//        if (type instanceof GenericArrayType) {
//            Type componentType = ((GenericArrayType) type).getGenericComponentType();
//            return Array.newInstance(getRawType(componentType), 0).getClass();
//        }
//        if (type instanceof TypeVariable) {
//            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
//            // type that's more general than necessary is okay.
//            return Object.class;
//        }
//        if (type instanceof WildcardType) {
//            return getRawType(((WildcardType) type).getUpperBounds()[0]);
//        }
//
//        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
//                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
//    }
}
