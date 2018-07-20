package com.okhttp3.lvping.okhttp3.okhttp3;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
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
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();

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

    public void request(SimpleOkhttp3 simpleOkhttp3, final BaseCallback callback){
        if (callback == null) {
            throw new NullPointerException("callback is null");

        }
        okHttpClient.newCall(simpleOkhttp3.buildRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendOnFailureMessage(callback, call, e);

            }

            @Override
            public void onResponse(Call call, Response response){
                if (response.isSuccessful()) {
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("result", result);
                    if (callback.mType == null || callback.mType == String.class) {
                        sendOnSuccessMessage(callback, result);
                    } else {
                        sendOnSuccessMessage(callback, mGson.fromJson(result, callback.mType));
                    }
                    if (response.body() != null) {
                        response.body().close();
                    }
                } else {
                    sendOnErrorMessage(callback, response.code());
                }

            }
        });
    }

    private void sendOnFailureMessage(final BaseCallback callback, final Call call, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(call, e);
            }
        });

    }

    private void sendOnErrorMessage(final BaseCallback callback, final int code) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(code);
            }
        });
    }

    private void sendOnSuccessMessage(final BaseCallback callback, final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(obj);
            }
        });
    }

}
