package com.okhttp3.lvping.okhttp3.okhttp3;

public interface ProgressListener {
    void onProgress(int progress);

    void onDone(long totalSize);
}
