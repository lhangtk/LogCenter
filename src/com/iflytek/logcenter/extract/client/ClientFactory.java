package com.iflytek.logcenter.extract.client;

import com.iflytek.edu.usp.collect.api.UploadSvc;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * Created by yancai on 2015/3/4.
 */
public class ClientFactory {
    private static final String CLIENT_VERSION = "1.0.0";
    private static final String PROPERTIES_FILE = "collect.client.properties";
    private static final String KEY_CLIENT_APP = "usp.collect.client.app";
    private static final String KEY_SERVER_URL = "usp.collect.server.url";

    private static Logger log = LoggerFactory.getLogger(ClientFactory.class);

    private static String clientApp;
    private static String serverUrl;


    public ClientFactory() {
        ClassLoader classLoader = ClientFactory.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(PROPERTIES_FILE);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("load `" + PROPERTIES_FILE + "` error!");
            return;
        }

        clientApp = properties.getProperty(KEY_CLIENT_APP);
        if (clientApp.trim().length() < 1) {
            log.error("load `" + KEY_CLIENT_APP + "` error");
        }

        serverUrl = properties.getProperty(KEY_SERVER_URL);
        if (serverUrl.trim().length() < 1) {
            log.error("load `" + KEY_SERVER_URL + "` error");
        }

        if (!serverUrl.substring(serverUrl.length() - 1).equals("/")) {
            serverUrl += "/";
        }
    }

    public ClientFactory(String appName, String url) {

        clientApp = appName;
        if (clientApp.trim().length() < 1) {
            log.error("load `" + KEY_CLIENT_APP + "` error");
        }

        serverUrl = url;
        if (serverUrl.trim().length() < 1) {
            log.error("load `" + KEY_SERVER_URL + "` error");
        }

        if (!serverUrl.substring(serverUrl.length() - 1).equals("/")) {
            serverUrl += "/";
        }

    }


    private <T> WrappedClient createClient(Class<T> clazz, String url) {
        try {
            THttpClient httpClient = new THttpClient(url);
            httpClient.setCustomHeader("client_app", clientApp);
            httpClient.setCustomHeader("client_version", CLIENT_VERSION);
//            httpClient.setCustomHeader("client_version", null);
            httpClient.setCustomHeader("client_type", "android");

            TProtocol protocol = new TBinaryProtocol(httpClient);
            Constructor<T> constructor = clazz.getConstructor(TProtocol.class);
            return new WrappedClient<T>(httpClient, constructor.newInstance(protocol));

        } catch (Exception e) {
            log.error("init client error: ", e);
            return null;
        }
    }

    public WrappedClient createUploadSvc() {
        String url = String.format("%supload_log", serverUrl);
        return createClient(UploadSvc.Client.class, url);
    }



}
