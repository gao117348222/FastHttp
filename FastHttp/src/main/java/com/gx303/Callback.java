package com.gx303;

/**
 * Created by Administrator on 2016/5/3.
 */
public interface Callback<T> {
    void onResponse(Call<T> call, Response<T> response);
    void onFailure(Call<T> call, Throwable t);
}
