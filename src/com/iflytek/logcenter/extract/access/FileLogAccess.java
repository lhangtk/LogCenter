package com.iflytek.logcenter.extract.access;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.iflytek.logcenter.extract.LogConfig;
import com.iflytek.logcenter.extract.access.api.ILogAccess;
import com.iflytek.logcenter.extract.log.LogWrite;
import com.iflytek.logcenter.extract.model.Position;
import com.iflytek.logcenter.extract.utils.FileCheck;
import com.iflytek.logcenter.extract.utils.PositionWriter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yancai on 2014/8/20.
 * <p/>
 * Modified by hangli2 on 2014/9/24
 */
public class FileLogAccess implements ILogAccess {
    private final static LogWrite logWrite = new LogWrite();
    private final static Logger log = LoggerFactory.getLogger(FileLogAccess.class);
    private static final long timeSpend = LogConfig.getInstance().getUploadTimeSpend();// 60*1000;//Long.parseLong(AppProperties.getAppProperties().get(ConstKey.UPLOAD_TIME_SPEND))*60*1000;//ms
    private static final long recordNum = LogConfig.getInstance().getUploadRecordNum();// 10;//Long.parseLong(AppProperties.getAppProperties().get(ConstKey.UPLOAD_RECORD_NUM));

    @Override
    public boolean cleanHistory(Position position) {
        if (position == null) {
            return false;
        }
        File files = new File(position.getPath());
        String positionName = FilenameUtils.getBaseName(position.getFileName());
        if (files.exists()) {
            for (File file : files.listFiles()) {
                if (FileCheck.fileFilter(file)) {
                    String fileName = FilenameUtils.getBaseName(file.getName());
                    int result = fileName.compareTo(positionName);
                    if (result < 0) {
                        System.out.println("delete:" + fileName);
                        file.delete();
//                        file.renameTo(new File(file.getAbsolutePath().replace("extract", "oldlog")));
                        continue;
                    } else if (result == 0 && position.getLineNO() == 0) {//当前文件已读取完毕，则删除
                        file.delete();
//                        file.renameTo(new File(file.getAbsolutePath().replace("extract", "oldlog")));
                        break;
                    }
                }
            }
        } else {
            log.error("clean history, files in this position don't exist");
            return false;
        }
        return true;
    }

    @Override
    public Position getLastPosition() {
        File file = new File(LogConfig.getInstance().getStorageNextPosition());//AppProperties.getAppProperties().get(ConstKey.STORAGE_NEXT_POSITION));
        Position position = null;
        try {
            if (!file.getParentFile().exists()) {
                File dir = file.getParentFile();
                dir.mkdirs();
//                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String nextPosition = reader.readLine();
            if (nextPosition == null || nextPosition.equals("")) {
                //若没有当前位置信息，则从第一个文件开始读取
                String path = LogConfig.getInstance().getStoragePath();//"/logs/extract/";//AppProperties.getAppProperties().get(ConstKey.STORAGE_BASEDIR);
                File files = new File(path);
                if (files.exists()) {
                    for (File newfile : files.listFiles()) {
                        if (FileCheck.fileFilter(newfile)) {
                            position = new Position();
                            position.setFileName(newfile.getName());
                            position.setPath(path);
                            position.setLineNO(1);
                            position.setTimeSpend(0);
                            break;
                        }
                    }
                }
            } else {
                position = JSON.parseObject(nextPosition, Position.class);
                if (position.getLineNO() == 0) {//若当前文件已读取完毕，获取下一个文件
                    position = PositionWriter.getNextPosition(position);
                }
            }
            //读完必须关闭，否再次读取时会直接从
            reader.close();
        } catch (FileNotFoundException e) {
            log.error("read nextPosition, file not found:", e);
        } catch (IOException e) {
            log.error("position read failed:", e);
        }
        return position;
    }

    @Override
    public String read(Position position) throws IOException {
        long start = System.currentTimeMillis();
        long end = 0;
        if (position == null) {
            return null;
        }
        File file = new File(position.getPath() + File.separator + position.getFileName());
        if (!file.exists()) {
            position = PositionWriter.getNextPosition(position);//若文件已被logback按时间删除，则从下一个文件开始读取
            if (position == null) {
                return null;
            }
            file = new File(position.getPath() + File.separator + position.getFileName());
        }
        FileReader filereader = null;
        List<String> logInfos = new ArrayList<String>();
        if (position.getLineNO() != 0) {//若当前文件没有读完
            int count = 0;
            Position positionTemp = null;
            try {
                filereader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                String jsonLine = null;
                count = 0;
                while (position.getRecordNum() < recordNum) {
                    if (position.getLineNO() != 0 && (jsonLine = bufferedReader.readLine()) != null) {
                        count++;
                        if (count >= position.getLineNO()) {//从指定行开始读取
                            jsonLine = jsonLine.substring(jsonLine.indexOf("]: {") + 3);
                            logInfos.add(jsonLine);
                            Log.d("jsonLine",jsonLine.toString());
                            //每次读取出一条满足条件的记录时需要改变记录数
                            position.setRecordNum(position.getRecordNum() + 1);
                        }
                    }if (jsonLine == null){
                        break;
                    }
//                    else {//若当前文件读取完毕但还未达到上传条件则继续向下读取
//                        bufferedReader.close();//一定要关闭，否则删除历史时删除不成功
//                        filereader.close();
//                        position.setLineNO(0);
//                        Position nextposition = PositionWriter.getNextPosition(position);
//                        if (nextposition == null) {//若没有文件可读取则等待
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            //记录执行时间，满足一定条件时触发上传
//                            end = System.currentTimeMillis();
//                            position.setTimeSpend(position.getTimeSpend() + (end - start));
//                            start = end;
//                            continue;
//                        } else {
//                            positionTemp = position;
//                            position = nextposition;
//                            file = new File(position.getPath() + File.separator + position.getFileName());
//                            filereader = new FileReader(file);
//                            bufferedReader = new BufferedReader(filereader);
//                            count = 0;
//                        }
//                    }
//                    //记录执行时间，满足一定条件时触发上传
//                    end = System.currentTimeMillis();
//                    position.setTimeSpend(position.getTimeSpend() + (end - start));
//                    start = end;
                }
//                bufferedReader.close();
//                filereader.close();
//                //由于读取文件异常出现上传失败的现象，在该位置修改
//                //异常是由文件bufferedReader关闭引起
//                file = new File(position.getPath() + File.separator + position.getFileName());
//                filereader = new FileReader(file);
//                bufferedReader = new BufferedReader(filereader);
//                int c =0;
//                while (c<=count){
//                    bufferedReader.readLine();
//                    c++;
//                };

                if (jsonLine != null) {
                    //记录下次开始读取的行号
                    position.setLineNO(count+1);
                } else {
                    //若当前文件读取的正好是最后一条记录，则将行号设为0
                    position.setLineNO(0);
                }
                bufferedReader.close();
                filereader.close();
                log.debug("current position:{}", position);
                //每次上传过后需清零
                position.setTimeSpend(0);
                position.setRecordNum(0);
                log.debug("start write next position:{}", position);
                //记录下次读取的位置信息
                PositionWriter.writePosition(position);
            } catch (FileNotFoundException e) {
                log.error("read logs, File Not Found:", e);
            } catch (IOException e) {
                Log.d("position",position.toString());
                Log.d("positionT",positionTemp.toString());
                e.printStackTrace();
//                position.setLineNO(0);
                positionTemp.setTimeSpend(0);
                positionTemp.setRecordNum(0);
                position.setRecordNum(0);
                position.setTimeSpend(0);
                position.setLineNO(0);
                PositionWriter.writePosition(position);
                log.error("read logs failed:", e);
            }
        }
        return logInfos.toString();
    }

    @Override
    public void write(String message) {
        logWrite.info(message);
    }

    @Override
    public void write(String messageEntry, String messageQuit) {
        logWrite.info(messageEntry, messageQuit);
    }
}
