package com.iflytek.logcenter.extract;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.logcenter.extract.access.api.ILogAccess;
import com.iflytek.logcenter.extract.common.ConstKey;
import com.iflytek.logcenter.extract.utils.IdGenerator;
import com.iflytek.logcenter.extract.utils.ServerCheck;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hangli2 on 2014/9/20.
 */
public class Writer {
    private Context context;
    private ILogAccess logAccess = Factory.getLogAccess();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
    private JSONObject logEntry = null;
    private JSONObject logQuit = null;

    public Writer(Context context) {
        this.context = context;
        //若未设置app名则设置为0000
        if (LogConfig.getInstance().getUspLogcenterModule().equals("module"))
            LogConfig.getInstance().setUspLogcenterModule("000000");
        //日志存储路径
        if (LogConfig.getInstance().getStoragePath() == null)
            LogConfig.getInstance().setStoragePath(Environment.getExternalStorageDirectory().getPath() + "/iflytek/logcenter/" + context.getPackageName() + "/");
        //服务器地址
        if(!BaseConfig.serverUrl.equals(""))
        {
            LogConfig.getInstance().setUspLogcenterServerUrl(BaseConfig.serverUrl);
            Log.w("serverurl",LogConfig.getInstance().getUspLogcenterServerUrl());
        }
        //app
        LogConfig.getInstance().setUspLogcenterApp(BaseConfig.logApp);
        //product
        LogConfig.getInstance().setUspLogcenterProduct(BaseConfig.logProduct);
//        if (LogConfig.getInstance().getUspLogcenterServerUrl().equals("")) {
//            try {
//                InputStream is = context.getResources().getAssets().open("serverUrl");
//                //获取文件的字节数
//                int lenght = is.available();
//                //创建byte数组
//                byte[] buffer = new byte[lenght];
//                //将文件中的数据读到byte数组中
//                is.read(buffer);
//                String result = EncodingUtils.getString(buffer, "UTF-8");
//                LogConfig.getInstance().setUspLogcenterServerUrl(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        HttpThread httpThread = new HttpThread();
        httpThread.start();

    }

    /**
     * 启动会话（Activity）
     *
     * @param uid       用户ID可以为空null或者""
     * @param sessionId 会话ID
     * @param module    模块编码
     */
    @Deprecated
    public void entry(String uid, String sessionId, String module) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "enter");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        json.put(ConstKey.KEY_SESSION_ID, sessionId);
//        json.put(ConstKey.KEY_PARAM, "onResume");
        logEntry = json;

        JSONObject jsonT = new JSONObject();
        jsonT.put(ConstKey.KEY_PRODUCT, product);
        jsonT.put(ConstKey.KEY_APP, app);
        jsonT.put(ConstKey.KEY_MODULE, module);
        jsonT.put(ConstKey.KEY_USER_ID, uid);
        jsonT.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        jsonT.put(ConstKey.KEY_SESSION_ID, sessionId);
        jsonT.put(ConstKey.KEY_ACTION, "quit");
//        jsonT.put(ConstKey.KEY_PARAM, "onPause");
        logAccess.write(logEntry.toJSONString(), jsonT.toJSONString());
    }

    /**
     * 启动会话（Activity）
     *
     * @param uid       用户ID可以为空null或者""
     * @param sessionId 会话ID
     */
    public void entry(String uid, String sessionId) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        String module = BaseConfig.map.get(context.getPackageName());
        if (module == null) {
            module = "0000";
        }
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "enter");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        json.put(ConstKey.KEY_SESSION_ID, sessionId);
//        json.put(ConstKey.KEY_PARAM, "onResume");
        logEntry = json;

        JSONObject jsonT = new JSONObject();
        jsonT.put(ConstKey.KEY_PRODUCT, product);
        jsonT.put(ConstKey.KEY_APP, app);
        jsonT.put(ConstKey.KEY_MODULE, module);
        jsonT.put(ConstKey.KEY_USER_ID, uid);
        jsonT.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        jsonT.put(ConstKey.KEY_SESSION_ID, sessionId);
        jsonT.put(ConstKey.KEY_ACTION, "quit");
//        jsonT.put(ConstKey.KEY_PARAM, "onPause");
        logAccess.write(logEntry.toJSONString(), jsonT.toJSONString());
    }

    /**
     * 退出会话（Activity）
     *
     * @param uid       用户ID可以为空null或者""
     * @param sessionId 会话ID
     * @param module    模块编码
     */
    @Deprecated
    public void quit(String uid, String sessionId, String module) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "quit");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        json.put(ConstKey.KEY_SESSION_ID, sessionId);
//        json.put(ConstKey.KEY_PARAM, "onPause");

        logAccess.write(logEntry.toJSONString(), json.toJSONString());
    }

    /**
     * 退出会话（Activity）
     *
     * @param uid       用户ID可以为空null或者""
     * @param sessionId 会话ID
     */
    public void quit(String uid, String sessionId) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        String module = BaseConfig.map.get(context.getPackageName());
        if (module == null) {
            module = "0000";
        }
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "quit");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        json.put(ConstKey.KEY_SESSION_ID, sessionId);
//        json.put(ConstKey.KEY_PARAM, "onPause");

        logAccess.write(logEntry.toJSONString(), json.toJSONString());
    }

    /**
     * 触发了某些行为
     *
     * @param uid    用户ID可以为空null或者""
     * @param param  记录触发时需要被统计的一些参数
     * @param module 模块编码
     */
    public void trigger(String uid, String param, String module) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        if(uid == null){
            uid = "";
        }
//        String paramCoded = "";
//        paramCoded = StrToUnicode.strToUnicode(param);
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
//        if(uid != null)
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "trigger");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
        json.put(ConstKey.KEY_PARAM, param);
        json.put(ConstKey.KEY_SESSION_ID, IdGenerator.generate());
        logAccess.write(json.toJSONString());
    }
    /**
     * 触发了某些行为
     *
     * @param uid    用户ID可以为空null或者""
     * @param module 模块编码
     */
    public void trigger(String uid, String module) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        if(uid == null){
            uid = "";
        }
//        String paramCoded = "";
//        paramCoded = StrToUnicode.strToUnicode(param);
//        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
//        if(uid != null)
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "trigger");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
//        json.put(ConstKey.KEY_PARAM, param);
        json.put(ConstKey.KEY_SESSION_ID, IdGenerator.generate());
        logAccess.write(json.toJSONString());
    }
    /**
     * 登录日志记录
     *
     * @param uid 用户ID
     */
    public void login(String uid) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "login");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
//        json.put(ConstKey.KEY_PARAM, "login");
        json.put(ConstKey.KEY_SESSION_ID, uid);
        logAccess.write(json.toJSONString());
    }

    /**
     * 登出日志记录
     *
     * @param uid 用户ID
     */

    public void logout(String uid) {
        String product = LogConfig.getInstance().getUspLogcenterProduct();
        String app = LogConfig.getInstance().getUspLogcenterApp();
        String module = LogConfig.getInstance().getUspLogcenterModule();
        JSONObject json = new JSONObject();
        json.put(ConstKey.KEY_PRODUCT, product);
        json.put(ConstKey.KEY_APP, app);
        json.put(ConstKey.KEY_MODULE, module);
        json.put(ConstKey.KEY_USER_ID, uid);
        json.put(ConstKey.KEY_ACTION, "logout");
        json.put(ConstKey.KEY_TIMESTAMP, sdf.format(new Date()));
//        json.put(ConstKey.KEY_PARAM, "logout");
        json.put(ConstKey.KEY_SESSION_ID, uid);
        logAccess.write(json.toJSONString());
    }

    /**
     * 新增线程，启动Uploader
     */
    public class HttpThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (!Uploader.isIsUploading()) {
                try{
                    /**
                     * 修改测试服务器地址方式，直接使用ping命令，
                     * 修复bug：pad连接的wifi已连接网络但是需要二次认证才能连接到Internet
                     * 2015/01/26 hangli2
                     */
                    Runtime runtime = Runtime.getRuntime();
                    Process process = null;
                    //ping 百度确保联网
                    String str = "ping -c 1 " + "www.baidu.com";//BaseConfig.serverUrl.substring(7);
                    process = runtime.exec(str);
                    int result = process.waitFor();
                    Log.i("ping result", String.valueOf(result));
                    if (result == 0){
                        if (ServerCheck.canConnectServer()) {
                            Uploader.getInstance("upload",context).start();
                        }
                    }
//                    URL url = new URL(LogConfig.getInstance().getUspLogcenterServerUrl());
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    Log.e("connection Res", String.valueOf(connection.getResponseCode()));
//                    if (connection.getResponseCode() == 200){
//
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
