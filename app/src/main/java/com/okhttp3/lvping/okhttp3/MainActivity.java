package com.okhttp3.lvping.okhttp3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.okhttp3.lvping.okhttp3.okhttp.ProgressListener;
import com.okhttp3.lvping.okhttp3.okhttp.ProgressResponseBody;
import com.okhttp3.lvping.okhttp3.okhttp.SimpleHttpClient;
import com.okhttp3.lvping.okhttp3.okhttp3.SimpleOkhttp3;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String URL = "http://v.juhe.cn/toutiao/index?type=top&key=c0aa53b4b2fee9ca8cb6ee0776ad25f3";
    private final String fileName = "ChiefStore.apk";
    OkHttpClient okHttpClient;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        initOkhttp();
    }

    private void initOkhttp() {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), new Prg())).build();
            }
        }).build();
    }

    //get请求
    public void getReq(View v) {



//        Request request = new Request.Builder().get().url(URL).build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                if (response.isSuccessful()) {
//                    Log.e("response", response.body().string());
//
//                }
//                if (response.body() != null) {
//                    response.body().close();
//                }
//            }
//        });
    }

    //post请求
    public void postReq(View v) {
        String username = "qw123456";
        String userpassword = "123456";
        LoginWithForm(username, userpassword);


    }

    private void LoginWithForm(String username, String userpassword) {
        RequestBody requestBody = new FormBody.Builder().add("username", username).add("userpassword", userpassword).build();
        Request request = new Request.Builder().post(requestBody).url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


            }
        });
    }

    private void LoginWithJson(String username, String userpassword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("userpassword", userpassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonParams = jsonObject.toString();
        Toast.makeText(MainActivity.this, jsonParams, Toast.LENGTH_SHORT).show();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonParams);
        Request request = new Request.Builder().post(requestBody).url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


            }
        });
    }

    //文件下载(普通方式&拦截器)
    public void download(View v) {
        requestPermission();
        Request request = new Request.Builder()
                .url("https://dm.chiefchain.cn/apks/ChiefStore.apk")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    writeFile(response);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int progress = msg.arg1;
                progressBar.setProgress(progress);

            }
        }
    };

    private void writeFile(Response response) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        is = response.body().byteStream();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, fileName);
        try {
            fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int len = 0;
            //long totalSize = response.body().contentLength();
            //long sum = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes);
//                sum += len;
//                int progress = (int) ((sum * 1.0f / totalSize) * 100);
//                Message msg = handler.obtainMessage(1);
//                msg.arg1 = progress;
//                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    class Prg implements ProgressListener {

        @Override
        public void onProgress(final int progress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                }
            });


        }

        @Override
        public void onDone(long totalSize) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    public void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "please give me the permission", Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行相应操作

                    Toast.makeText(MainActivity.this, "已获取权限", Toast.LENGTH_LONG).show();
                } else {
                    //申请失败，可以继续向用户解释。
                }
                return;
            }
        }

    }

}
