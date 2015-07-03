package com.iflytek.logcenter.extract;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hangli2 on 2015/6/1.
 *
 * 这里写的都是可以修改的配置项
 */
public class BaseConfig {
    public static String SERVERURL = getConfig("serverurl");//"http://172.16.95.177:8080/collect_dev/";
    public static String serverUrl = SERVERURL.equals("")? "http://172.16.95.177:8080/collect_dev/" : SERVERURL;
    public final static String logProduct = "jsj";
    //监管统计应用（app）名
    public final static String logApp = "jsj";

    //应用退出重进 消除的间隔时间
    //暂记为1分钟
    public final static long skipTime = 60*1000;
//    public final static String serverUrl = getConfig("serverurl");
    //各个应用对应的映射表
    public static Map<String,String> map = new HashMap<String, String>();
    static {
        map.put("com.iflytek.cloudclass","114200");
        map.put("com.iflytek.smartbook","114100");
        map.put("com.iflytek.elpmobile.onlineteaching","114300");
    }

    //使用SDCARD配置文件，方便测试
    public static String getConfig(String key) {
        String configURl = "";
        String fileName = "/mnt/sdcard/iflytek/ifly_tm_config.xml";// 文件路径
        try {
            File file = new File(fileName);
            if (!file.exists())
                return configURl;
            FileInputStream fin = new FileInputStream(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fin);
            // 获取根节点
            Element root = document.getDocumentElement();

            Element item = (Element) root.getElementsByTagName(key).item(0);
            configURl = item.getFirstChild().getNodeValue();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            Log.i("BASE_URL", configURl);
            return configURl;
        }
    }
}
