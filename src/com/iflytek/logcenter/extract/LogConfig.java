package com.iflytek.logcenter.extract;

/**
 * Created by hangli2 on 2014/9/17.
 * <p/>
 * 已单例模式进行创建，避免数据冲突
 *
 * 这里写的都是不建议修改的配置项
 */
public class LogConfig {
    private String storageType;//日志存储类型 file或db
    private String storagePath = null;//日志存储路径
    private String storageNextPosition=null;
    private String uspLogcenterServerUrl;//日志中心URL
    private String uspLogcenterApp;//应用名称
    private String uspLogcenterProduct;//产品名称
    private String uspLogcenterModule;//模块名称
    private boolean cleanHistorySelect;//是否删除历史true为删除
    private int uploadTimeSpend;//上传触发时间
    private int uploadRecordNum;//单次上传记录数
    private int uploadSleep;//没有新记录时休眠时间

    //各个应用对应的映射表
//    public static Map<String,String> map = new HashMap<String, String>();

    private static LogConfig logConfig = null;

    private LogConfig() {
        storageType = "file";
//        storagePath = Environment.getExternalStorageDirectory().getPath()+"/logcenter/";
//        storageNextPosition = storagePath+"logs/position.txt";
        uspLogcenterServerUrl = "";//BaseConfig.serverUrl;
        uspLogcenterApp = BaseConfig.logApp;
        uspLogcenterProduct = BaseConfig.logProduct;
        uspLogcenterModule = "module";
        cleanHistorySelect = true;
        uploadTimeSpend = 1 * 60 * 1000;
        uploadRecordNum = 10;
        uploadSleep = 1 * 60 * 1000;
//        initMap();
    }

    public static LogConfig getInstance() {
        if (logConfig == null) {
            logConfig = new LogConfig();
        }
        return logConfig;
    }

//    public void initMap(){
//        map.put("com.iflytek.cloudclass","0301");
//        map.put("com.iflytek.elpmobile.app.smartbook","0201");
//        map.put("com.iflytek.elpmobile.onlineteaching","0401");
//        map.put("com.iflytek.elpmobile.shootteaching","0501");
//    }

    public String getUspLogcenterModule() {
        return uspLogcenterModule;
    }

    public void setUspLogcenterModule(String uspLogcenterModule) {
        this.uspLogcenterModule = uspLogcenterModule;
    }

    public String getUspLogcenterProduct() {
        return uspLogcenterProduct;
    }

    public void setUspLogcenterProduct(String uspLogcenterProduct) {
        this.uspLogcenterProduct = uspLogcenterProduct;
    }
    public String getStorageType() {
        return storageType;
    }

//    public void setStorageType(String storageType) {
//        this.storageType = storageType;
//    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        this.storageNextPosition = storagePath + "logs/position.txt";
    }

    public String getUspLogcenterServerUrl() {
        return uspLogcenterServerUrl;
    }

    public void setUspLogcenterServerUrl(String uspLogcenterServerUrl) {
        this.uspLogcenterServerUrl = uspLogcenterServerUrl;
    }

    public String getStorageNextPosition() {
        return storageNextPosition;
    }

//    public void setStorageNextPosition(String storageNextPosition) {
//        this.storageNextPosition = storageNextPosition;
//    }

    public String getUspLogcenterApp() {
        return uspLogcenterApp;
    }

    public void setUspLogcenterApp(String uspLogcenterApp) {
        this.uspLogcenterApp = uspLogcenterApp;
    }

    public boolean isCleanHistorySelect() {
        return cleanHistorySelect;
    }

    public void setCleanHistorySelect(boolean cleanHistorySelect) {
        this.cleanHistorySelect = cleanHistorySelect;
    }

    public int getUploadTimeSpend() {
        return uploadTimeSpend;
    }

    public void setUploadTimeSpend(int uploadTimeSpend) {
        this.uploadTimeSpend = uploadTimeSpend;
    }

    public int getUploadRecordNum() {
        return uploadRecordNum;
    }

    public void setUploadRecordNum(int uploadRecordNum) {
        this.uploadRecordNum = uploadRecordNum;
    }

    public int getUploadSleep() {
        return uploadSleep;
    }

    public void setUploadSleep(int uploadSleep) {
        this.uploadSleep = uploadSleep;
    }
}
