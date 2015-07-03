package com.iflytek.logcenter.extract;

import com.iflytek.edu.usp.collect.api.UploadSvc;
import com.iflytek.logcenter.extract.access.FileLogAccess;
import com.iflytek.logcenter.extract.access.api.ILogAccess;
import com.iflytek.logcenter.extract.client.ClientFactory;
import com.iflytek.logcenter.extract.client.WrappedClient;

/**
 * Created by yancai on 2014/8/20.
 */
public class Factory {

    private static ILogAccess logAccess = null;
    private static WrappedClient<UploadSvc.Iface> client = null;

    /**
     * 获取日志记录接口
     *
     * @return
     */
    public synchronized static ILogAccess getLogAccess() {
        if (logAccess == null) {
            logAccess = createLogAccess();
        }

        return logAccess;
    }

    /**
     * 获取日志中心客户端
     *
     * @return
     */
//    public synchronized static UploadSvc.Iface getLogCenterClient() {
//        if (logCenterClient == null) {
//            logCenterClient = createLogCenterClient();
//        }
//        return logCenterClient;
//    }

    /**
     * 创建日志记录接口
     *
     * @return
     */
    private static ILogAccess createLogAccess() {
            return new FileLogAccess();
    }

    /**
     * 创建日志中心客户端
     *
     * @return
     */
//    private static UploadSvc.Iface createLogCenterClient() {
//        ClientFactory factory = new ClientFactory(
//                LogConfig.getInstance().getUspLogcenterApp(),
//                LogConfig.getInstance().getUspLogcenterServerUrl()
////                "test","http://172.16.79.22:8280/logcenter_alpha/"
////                AppProperties.getAppProperties().get(ConstKey.CLIENT_APP_NAME),
////                AppProperties.getAppProperties().get(ConstKey.SERVER_URL)
//        );
//        return factory.createUploadSvc();
//    }

    /**
     * 初始化客户端
     */
    private static synchronized void initClient() {
        ClientFactory clientFactory = new ClientFactory("jxj", LogConfig.getInstance().getUspLogcenterServerUrl());
        client = clientFactory.createUploadSvc();
    }

    /**
     * 获取客户端
     * @return
     */
    public synchronized static WrappedClient<UploadSvc.Iface> getClient() {
        if (client == null) {
            initClient();
        }
        return client;
    }

}
