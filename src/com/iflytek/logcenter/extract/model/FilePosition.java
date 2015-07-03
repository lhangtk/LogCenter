package com.iflytek.logcenter.extract.model;

/**
 * Created by yancai on 2014/8/20.
 */
public class FilePosition extends Position {
    private String filename;
    private long lineNo;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLineNo() {
        return lineNo;
    }

    public void setLineNo(long lineNo) {
        this.lineNo = lineNo;
    }
}
