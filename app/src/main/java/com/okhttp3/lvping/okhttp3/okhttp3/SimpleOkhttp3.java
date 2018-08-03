package com.okhttp3.lvping.okhttp3.okhttp3;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SimpleOkhttp3 {
    private Builder mBuilder;

    private SimpleOkhttp3(Builder builder) {
        this.mBuilder = builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void enqueue(BaseCallback callback) {
        OkhttpManager.getInstance().request(this, callback);
    }

    public static class Builder {
        private String mUrl;
        private String method;
        private boolean isJsonParam;
        private List<RequestParam> mParams;

        private Builder() {
            method = "GET";
        }

        public SimpleOkhttp3 build() {
            return new SimpleOkhttp3(this);
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        public Builder post() {
            method = "POST";
            return this;
        }

        public Builder json() {
            isJsonParam = true;
            return this;
        }

        public Builder url(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder addParam(String key, Object value) {
            if (mParams == null) {
                mParams = new ArrayList<>();
            }
            mParams.add(new RequestParam(key, value));
            return this;
        }
    }

    public Request buildRequest() {
        Request.Builder builder = new Request.Builder();
        if (mBuilder.method == "GET") {
            builder.url(buildGetRequestParam());
            builder.get();
        } else if (mBuilder.method == "POST") {
            builder.post(buildRequestBody());
            builder.url(mBuilder.mUrl);
        }
        return builder.build();
    }

    private String buildGetRequestParam() {
        if (mBuilder.mParams == null) {
            mBuilder.mParams = new ArrayList<>();
        }
        if (mBuilder.mParams.size() <= 0) {
            return this.mBuilder.mUrl;
        }
        //把string字符串变成 URI.BUILDER对象
        Uri.Builder builder = Uri.parse(mBuilder.mUrl).buildUpon();
        for (RequestParam p : mBuilder.mParams) {
            builder.appendQueryParameter(p.getKey(), p.getObj() == null ? "" : p.getObj().toString());
        }
        String url = builder.build().toString();
        Log.e("url", url);
        return url;
    }

    private RequestBody buildRequestBody() {
        //json形式传递
        if (mBuilder.isJsonParam) {
            JSONObject jsonObject = new JSONObject();
            for (RequestParam p : mBuilder.mParams) {
                try {
                    jsonObject.put(p.getKey(), p.getObj());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String json = jsonObject.toString();
            Log.e("request json=", json);
            return RequestBody.create(MediaType.parse("application/json"), json);
        }
        //form表单形式传递
        FormBody.Builder builder = new FormBody.Builder();
        for (RequestParam p : mBuilder.mParams) {
            builder.add(p.getKey(), p.getObj() == null ? "" : p.getObj().toString());
        }
        return builder.build();
    }


}
