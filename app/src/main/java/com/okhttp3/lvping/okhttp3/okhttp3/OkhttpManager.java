package com.okhttp3.lvping.okhttp3.okhttp3;


import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkhttpManager {
    private static OkhttpManager mInstance;
    private OkHttpClient okHttpClient;
    private Handler mHandler;
    private Gson mGson;

    private OkhttpManager() {
        initOkHttp();
        mHandler=new Handler(Looper.getMainLooper());

    }

    public static OkhttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkhttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpManager();
                }
            }
        }
        return mInstance;
    }

    private void initOkHttp() {
        okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).build();
    }
    public void request(SimpleOkhttp3 simpleOkhttp3,BaseCallback callback) throws JSONException {
        if(callback==null){
            throw new NullPointerException("callback is null");

        }
        okHttpClient.newCall(simpleOkhttp3.buildRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
    private void sendOnFailureMessage(final BaseCallback callback,final Call call,final IOException e){

    }

}
