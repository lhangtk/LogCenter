package com.iflytek.logcenter.extract.client;

import org.apache.thrift.transport.THttpClient;

/**
 * Created by yancai on 2015/3/4.
 */
public class WrappedClient<T> {

    private THttpClient httpClient;
    private T iFace;

    public WrappedClient(THttpClient tHttpClient, T clazz) {
        httpClient = tHttpClient;
        iFace = clazz;
    }

    public T getIFace() {
        return iFace;
    }

    /**
     * 调用HttpClient.close，用以关闭文件流
     */
    public void close() {
        httpClient.close();
    }

}
