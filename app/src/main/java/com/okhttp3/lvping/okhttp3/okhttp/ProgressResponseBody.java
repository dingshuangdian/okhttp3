package com.okhttp3.lvping.okhttp3.okhttp;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {
    private ResponseBody mResponseBody;
    private BufferedSource mSource;
    private ProgressListener listener;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.mResponseBody = responseBody;
        this.listener = progressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mSource == null) {
            mSource = Okio.buffer(getSource(mResponseBody.source()));

        }
        return mSource;
    }

    private Source getSource(Source source) {
        return new ForwardingSource(source) {
            long totalSize = 0l;
            long sum = 0l;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (totalSize == 0) {
                    totalSize = contentLength();
                }
                long len = super.read(sink, byteCount);
                sum += (len == -1 ? 0 : len);
                int progress = (int) ((sum * 1.0f / totalSize) * 100);
                if (len == -1) {
                    listener.onDone(totalSize);
                } else {
                    listener.onProgress(progress);
                }
                return len;
            }
        };
    }
}
