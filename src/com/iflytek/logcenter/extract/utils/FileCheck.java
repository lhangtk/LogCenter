package com.iflytek.logcenter.extract.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by llwang4 on 2014/8/27.
 */
public class FileCheck {
    /**
     * 文件过滤
     * @param file
     * @return
     */
    public static boolean fileFilter(File file){
        String fileName = file.getName();
        if (fileName.contains("extract") && !FilenameUtils.getBaseName(fileName).equals("extract")&&!FilenameUtils.getBaseName(fileName).equals("extractdur")){
            return true;
        }
        return false;
    }
}
