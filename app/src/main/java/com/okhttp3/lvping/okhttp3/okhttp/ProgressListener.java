package com.okhttp3.lvping.okhttp3.okhttp;

public interface ProgressListener {
    void onProgress(int progress);

    void onDone(long totalSize);
}
