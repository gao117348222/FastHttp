package com.gx303;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/3.
 */
public interface Call<T> extends Cloneable  {
    Response<T> execute() throws IOException;
    void enqueue(Callback<T> callback);

}
