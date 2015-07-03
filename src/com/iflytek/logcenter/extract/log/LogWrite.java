package com.iflytek.logcenter.extract.log;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.logcenter.extract.BaseConfig;
import com.iflytek.logcenter.extract.LogConfig;
import com.iflytek.logcenter.extract.common.ConstKey;
import com.iflytek.logcenter.extract.utils.IdGenerator;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hangli2 on 2014/9/18.
 * <p/>
 * 完成写日志操作，无模块过滤，若不需要将事务记录上传不能调用。
 * 日志按照分钟进行分割
 */
public class LogWrite {
    private String path;// = AppConfig.getInstance().getStoragePath();
    //日志保存文件名
    private String fileName = "extract.log";
    //日志条目的头信息
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,sss");
    //日志时间戳格式
    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
    //日志文件滚动的后缀
    private static final SimpleDateFormat sdfRoll = new SimpleDateFormat("-yyyy-MM-dd-HH-mm");
    //用于标记文件是否为第一次写入
    private boolean flagFrist = false;

    /**
     * 打印信息,对应trigger事件
     *
     * @param message 打印到日志中的信息，必须为JSON格式的数据
     */
    public synchronized void info(String message) {
        long fileTime = 0;
        try {
            path = LogConfig.getInstance().getStoragePath();
            File filePath = new File(path);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File logFile = new File(path + fileName);
            if (logFile.exists()) {
                /**
                 * 新增异常catch 如果日志文件错误则删除该日志文件 *
                 * 异常发生位置fileTime = fileDate.getTime();
                 * 2015、01、06 *
                 * hangli2 *
                 */
                try {
                    BufferedReader in = new BufferedReader(new FileReader(path + fileName));
                    String dateStr = in.readLine().substring(1, 24);
                    Date fileDate = new Date();
                    fileDate = sdf.parse(dateStr);
                    fileTime = fileDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    logFile.delete();
                    logFile.createNewFile();
                    flagFrist = true;
                }

            } else {
                logFile.createNewFile();
                flagFrist = true;
                fileTime = System.currentTimeMillis();
            }

            long sysTime = System.currentTimeMillis();
//            Date sysDate = new Date(sysTime);
            if (sysTime - fileTime >= 60 * 1000) {//日志分割
                int i = 0;
                File renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
                while (renameFile.exists()) {
                    i++;
                    renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
                }
                logFile.renameTo(renameFile);
                logFile.createNewFile();
                flagFrist = true;
            }
            //向日志文件中写入日志message
            BufferedWriter out = new BufferedWriter(new FileWriter(path + fileName, true));
            if (flagFrist) {
                message = "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + message;
                flagFrist = false;
            } else {
                message = "\n" + "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + message;
                flagFrist = false;
            }
            Log.i("LogWrite", message);
            out.write(message);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void info(String messageEntry, String messageQuit) {
        long fileTime = 0;
        try {
            path = LogConfig.getInstance().getStoragePath();
            File filePath = new File(path);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File logFile = new File(path + "extractdur.log");
            if (logFile.exists()) {
                /**
                 * 新增异常catch 如果日志文件错误则删除该日志文件 *
                 * 异常发生位置fileTime = fileDate.getTime();
                 * 2015、01、06 *
                 * hangli2 *
                 */
                try {
                    BufferedReader in = new BufferedReader(new FileReader(path + "extractdur.log"));
                    String strLastEntry = in.readLine();
                    //读取上次写入的退出日志
                    String strLastQuit = in.readLine();//.substring(1, 24);
                    //上次写日志的时间
                    String dateStr = strLastQuit.substring(1, 24);
                    Date fileDate = new Date();
                    fileDate = sdf.parse(dateStr);
                    fileTime = fileDate.getTime();

                    //上次写日志的json数据
                    String jsonStrLastEntry = strLastEntry.substring(strLastEntry.indexOf("]: {") + 3);
                    String jsonStrLastQuit = strLastQuit.substring(strLastQuit.indexOf("]: {") + 3);
                    JSONObject jsonLastQuit = JSON.parseObject(jsonStrLastEntry);
                    JSONObject jsonEntry = JSON.parseObject(messageEntry);
                    JSONObject jsonQuit = JSON.parseObject(messageQuit);

                    Date entryDate = sdfTime.parse(jsonEntry.getString(ConstKey.KEY_TIMESTAMP));
                    long entryTime = entryDate.getTime();
//                long sysTime = System.currentTimeMillis();
                    if ((entryTime - fileTime >= BaseConfig.skipTime) || (!jsonLastQuit.getString("module").equals(jsonEntry.getString("module")))) {
                        //日志分割
                        // 时间为1m，1m内用户在同一应用内操作不进行分割或者两条日志分属不同文件
                        int i = 0;
                        File renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
                        while (renameFile.exists()) {
                            i++;
                            renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
                        }
                        logFile.renameTo(renameFile);
                        logFile.createNewFile();
                        flagFrist = true;
                    } else {//不满足条件将原文件重写，用上次的entry和本次的quit作为数据
                        messageEntry = jsonStrLastEntry;
                        JSONObject json = new JSONObject();
                        json.put(ConstKey.KEY_PRODUCT, jsonQuit.getString(ConstKey.KEY_PRODUCT));
                        json.put(ConstKey.KEY_APP, jsonQuit.getString(ConstKey.KEY_APP));
                        json.put(ConstKey.KEY_MODULE, jsonQuit.getString(ConstKey.KEY_MODULE));
                        json.put(ConstKey.KEY_USER_ID, jsonQuit.getString(ConstKey.KEY_USER_ID));
                        json.put(ConstKey.KEY_ACTION, "quit");
                        json.put(ConstKey.KEY_TIMESTAMP, jsonQuit.getString(ConstKey.KEY_TIMESTAMP));
                        json.put(ConstKey.KEY_SESSION_ID, jsonLastQuit.getString(ConstKey.KEY_SESSION_ID));
//                        json.put(ConstKey.KEY_PARAM, "onPause");
                        messageQuit = json.toJSONString();
                        logFile.delete();
                        logFile.createNewFile();
                        flagFrist = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    logFile.delete();
                    logFile.createNewFile();
                    flagFrist = true;
                }
            } else {
                logFile.createNewFile();
                flagFrist = true;
            }


            long sysTime = System.currentTimeMillis();
////            Date sysDate = new Date(sysTime);
//            if (sysTime - fileTime >= 15 * 1000) {//日志分割时间为15秒，15内用户在同一应用内操作不进行分割
//                int i = 0;
//                File renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
//                while (renameFile.exists()) {
//                    i++;
//                    renameFile = new File(path + "extract" + sdfRoll.format(new Date(fileTime)) + "." + i + ".log");
//                }
//                logFile.renameTo(renameFile);
//                logFile.createNewFile();
//                flagFrist = true;
//            }
            //向日志文件中写入日志message
            BufferedWriter out = new BufferedWriter(new FileWriter(path + "extractdur.log", true));
            String message = null;//向日志文件中写的内容
            if (flagFrist) {
                String messageTrigger = messageEntry.replaceAll("enter", "trigger");
                messageTrigger = messageTrigger.replaceAll("onResume", "frequency");
                int start = messageTrigger.indexOf("_id\":\"") + 6;
                messageTrigger = messageTrigger.replaceAll(messageTrigger.substring(start, start + 32), IdGenerator.generate());
                message = "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + messageEntry;
                message += "\n" + "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + messageQuit;
                message += "\n" + "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + messageTrigger;
                flagFrist = false;
            } else {
                message = "\n" + "[" + sdf.format(new Date(sysTime)) + "]-[INFO]: " + message;
                flagFrist = false;
            }
            Log.i("LogWrite", message);
            out.write(message);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
