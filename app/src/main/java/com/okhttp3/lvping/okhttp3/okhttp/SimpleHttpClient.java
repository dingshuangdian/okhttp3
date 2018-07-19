package com.okhttp3.lvping.okhttp3.okhttp;

import android.net.Uri;

import com.okhttp3.lvping.okhttp3.okhttp3.RequestParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SimpleHttpClient {
    private Builder builder;

    private SimpleHttpClient(Builder mBuilder) {
        this.builder = mBuilder;

    }

    public Request buildRequest() {
        Request.Builder ReBuilder = new Request.Builder();
        if (builder.method == "GET") {
            ReBuilder.url(buildGetRequestParam());
            builder.get();
        } else if (builder.method == "POST") {
            try {
                ReBuilder.post(buildRequestBody());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return ReBuilder.build();
    }

    private String buildGetRequestParam() {
        if (builder.requestParams.size() <= 0) {
            return this.builder.url;
        }
        Uri.Builder uBuilder = Uri.parse(builder.url).buildUpon();
        for (RequestParam p : builder.requestParams) {
            uBuilder.appendQueryParameter(p.getKey(), p.getObj() == null ? "" : p.getObj().toString());

        }
        String url = uBuilder.build().toString();
        return url;
    }

    private RequestBody buildRequestBody() throws JSONException {
        if (builder.isJsonParam) {
            JSONObject jsonObject = new JSONObject();
            for (RequestParam p : builder.requestParams) {
                jsonObject.put(p.getKey(), p.getObj());
            }
            String json = jsonObject.toString();
            return RequestBody.create(MediaType.parse("application/json"), json);
        }
        FormBody.Builder fBuilder = new FormBody.Builder();
        for (RequestParam p : builder.requestParams) {
            fBuilder.add(p.getKey(), p.getObj() == null ? "" : p.getObj().toString());
        }
        return fBuilder.build();
    }

    public Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private String method;
        private boolean isJsonParam;
        private List<RequestParam> requestParams;

        public SimpleHttpClient build() {
            return new SimpleHttpClient(this);
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        /**
         * Form 表单
         *
         * @return
         */
        public Builder post() {
            method = "POST";
            return this;
        }

        /**
         * Json参数
         *
         * @return
         */
        public Builder json() {
            isJsonParam = true;
            return post();
        }

        public Builder addParam(String key, Object value) {
            if (requestParams == null) {
                requestParams = new ArrayList<>();
            }
            requestParams.add(new RequestParam(key, value));
            return this;

        }


    }
}
