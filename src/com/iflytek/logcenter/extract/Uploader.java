package com.iflytek.logcenter.extract;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.iflytek.edu.usp.collect.api.UploadSvc;
import com.iflytek.logcenter.extract.access.api.ILogAccess;
import com.iflytek.logcenter.extract.client.WrappedClient;
import com.iflytek.logcenter.extract.model.Position;
import com.iflytek.logcenter.extract.utils.PositionWriter;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yancai on 2014/8/20.
 */

public class Uploader extends Thread {
    // 穿入Context是为了获得网络状态
    private Context context;
    // 线程名称
    private String name;
    // 上传状态，所有Uploader共享
    private static boolean isUploading = false;
    // 上传日志客户端
//    UploadSvc.Iface client = Factory.getLogCenterClient();

    WrappedClient<UploadSvc.Iface> client = Factory.getClient();

    private static ILogAccess fileLogAccess = Factory.getLogAccess();

    private static final Logger LOGGER = LoggerFactory.getLogger(Uploader.class);

    //    private static final String cleanHistory = "yes";//AppProperties.getAppProperties().get(ConstKey.CLEAN_HISTORY_SELECT);
    private static final long sleepTime = LogConfig.getInstance().getUploadSleep();// 60*1000;//Long.parseLong(AppProperties.getAppProperties().get(ConstKey.UPLOAD_SLEEP))*60*1000;

    private static Uploader uploader = null;

    public static boolean isIsUploading() {
        return isUploading;
    }

    public synchronized static void setIsUploading(boolean isUploading) {
        Uploader.isUploading = isUploading;
    }

    public Uploader(String name, Context context) {
        super(name);
        this.context = context;
        this.name = name;
    }

    public static Uploader getInstance(String name, Context context){
        if (uploader == null||!(uploader.context.equals(context))){
            uploader = new Uploader(name,context);
        }
        return uploader;
    }

    public void run() {
        setIsUploading(true);
        //用于比较两次取position是否相同
        String positionTemp = "";
        while (isIsUploading()) {
            Log.i("start thread:", name);
//            LOGGER.info("start thread:{}", name);
            try {
                if (!isNetworkStateWifi()) {
                    Log.i("wifi is not connected,start sleep...", "");
//                    LOGGER.info("wifi is not connected,start sleep...");
                    this.sleep(sleepTime);
                    continue;
                }
                /**
                 * 修改测试服务器地址方式，直接使用ping命令，
                 * 修复bug：pad连接的wifi已连接网络但是需要二次认证才能连接到Internet
                 * 2015/01/26 hangli2
                 */
                Runtime runtime = Runtime.getRuntime();
                Process process = null;
                String str = "ping -c 1 " + "usp.cycore.cn";
                process = runtime.exec(str);
                int pingResult = process.waitFor();
//                Log.e("ping result", String.valueOf(pingResult));
                if (pingResult != 0){//ping不通
                    Log.i("Server cannot connect,start sleep...", "");
                    this.sleep(sleepTime);
                    continue;
                }

                Position position = fileLogAccess.getLastPosition();
                Log.i("positionTemp:", positionTemp);
                Log.i("get last position:", String.valueOf(position));
//                if (String.valueOf(position).equals(positionTemp)) {
//                    Log.i("uploader:", "position do not update,start sleep...");
//                    this.sleep(sleepTime);
//                    continue;
//                }
//                LOGGER.info("get last position:{}", position);
                if ((position != null && position.getLineNO() == 0 && PositionWriter.getNextPosition(position) == null)
                        || position == null) {
                    Log.i("no next position,start sleep..", "");
//                    LOGGER.info("no next position,start sleep...");
                    this.sleep(sleepTime);
                    continue;
                }
                if (LogConfig.getInstance().isCleanHistorySelect()) {
                    boolean result = fileLogAccess.cleanHistory(position);
                    Log.i("clean history:", String.valueOf(result));
//                    LOGGER.info("clean history:{}", result);
                }
                String logMsg = fileLogAccess.read(position);
                if (logMsg != null && logMsg.length() != 0) {
                    Log.i("start upload logMsg", "");
                    if (position != null)
                        positionTemp = String.valueOf(position);
//                    LOGGER.info("start upload logMsg");
                    System.out.println(client.getIFace().upload_log(logMsg));
                }
            } catch (TException e) {
                LOGGER.error("connect thrift error:", e);
            } catch (InterruptedException e) {
                LOGGER.error("read the file failed:", e);
            } catch (Exception e) {
                LOGGER.error("thread error:", e);
            }
        }
    }

    public boolean isNetworkStateWifi() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

}
