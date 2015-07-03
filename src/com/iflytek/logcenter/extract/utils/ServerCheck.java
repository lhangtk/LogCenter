package com.iflytek.logcenter.extract.utils;

import com.iflytek.edu.usp.collect.KEYConstants;
import com.iflytek.edu.usp.collect.api.UploadSvc;
import com.iflytek.logcenter.extract.LogConfig;
import com.iflytek.logcenter.extract.client.ClientFactory;
import com.iflytek.logcenter.extract.client.WrappedClient;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务检查者类
 * 负责检查客户端是否可以连通日志收集服务
 */
public class ServerCheck {
    private static Logger log = LoggerFactory.getLogger(ServerCheck.class);
    private static ClientFactory clientFactory = new ClientFactory("test", LogConfig.getInstance().getUspLogcenterServerUrl());

    /**
     * 检查服务端是否可连通
     * @return
     */
    public static boolean canConnectServer() {
        try {
            WrappedClient<UploadSvc.Iface> client = clientFactory.createUploadSvc();
            String result = client.getIFace().test_connect();
            client.close();
            return KEYConstants.SUCCESS.equals(result);
        } catch (TException e) {
            log.error("test_connect error: ", e);
            return false;
        }
    }

}
