package com.okhttp3.lvping.okhttp3.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttp3.lvping.okhttp3.R;
import com.okhttp3.lvping.okhttp3.okhttp3.ProgressListener;
import com.okhttp3.lvping.okhttp3.okhttp3.ProgressResponseBody;
import com.okhttp3.lvping.okhttp3.okhttp3.BaseCallback;
import com.okhttp3.lvping.okhttp3.okhttp3.BaseResult;
import com.okhttp3.lvping.okhttp3.okhttp3.SimpleOkhttp3;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String fileName = "downloadtest.apk";
    private OkHttpClient okHttpClient;
    EditText user_name;
    EditText user_pwd;
    Button btn_login, btn_get, download;
    TextView tv_content;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOkhttp();
        progressBar = findViewById(R.id.progressBar);
        btn_login = findViewById(R.id.login);
        user_name = findViewById(R.id.user_name);
        user_pwd = findViewById(R.id.user_pwd);
        tv_content = findViewById(R.id.tv_content);
        btn_get = findViewById(R.id.btn_get);
        download = findViewById(R.id.download);
        btn_login.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        download.setOnClickListener(this);
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
    private void getReq() {
        SimpleOkhttp3.newBuilder().url(Api.NEWS)
                .get()
                .build()
                .enqueue(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        tv_content.setText(o.toString());
                    }

                    @Override
                    public void onError(int code) {
                        super.onError(code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                });
    }

    //post请求
    private void postReq() {
        SimpleOkhttp3.newBuilder().url(Api.LOGIN)
                .addParam("userAccount", user_name.getText().toString().trim())
                .addParam("userPass", user_pwd.getText().toString().trim())
                .post()
                .json()
                .build().enqueue(new BaseCallback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                super.onSuccess(baseResult);
                Toast.makeText(MainActivity.this, baseResult.getMsg().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code) {
                super.onError(code);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }
        });
    }

    //文件下载(普通方式&拦截器)
    private void download() {
        requestPermission();
        Request request = new Request.Builder()
                .url(Api.DOWNLOAD)
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

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                int progress = msg.arg1;
//                progressBar.setProgress(progress);
//
//            }
//        }
//    };

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                getReq();
                break;
            case R.id.login:
                postReq();
                break;
            case R.id.download:
                download();
                break;
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
