package com.iflytek.logcenter.extract.model;

import com.alibaba.fastjson.JSON;

/**
 * Created by yancai on 2014/8/20.
 */
public class Position {
    private String path;
    private String fileName;
    private int lineNO;//当前行号，为0代表读取完毕
    private long timeSpend;
    private long recordNum;//指定读取的记录数

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNO() {
        return lineNO;
    }

    public void setLineNO(int lineNO) {
        this.lineNO = lineNO;
    }

    public long getTimeSpend() {
        return timeSpend;
    }

    public void setTimeSpend(long timeSpend) {
        this.timeSpend = timeSpend;
    }

    public long getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(long recordNum) {
        this.recordNum = recordNum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
