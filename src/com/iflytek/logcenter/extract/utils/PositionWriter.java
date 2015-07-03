package com.iflytek.logcenter.extract.utils;

import com.iflytek.logcenter.extract.LogConfig;
import com.iflytek.logcenter.extract.model.Position;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by llwang4 on 2014/8/25.
 * 读取完当前位置后记录日志的下一个位置
 */
public class PositionWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PositionWriter.class);

    /**
     * 写入下一个位置信息
     * @param nextPosition
     */
    public static void writePosition(Position nextPosition){
        File filename = new File(LogConfig.getInstance().getStorageNextPosition());//AppProperties.getAppProperties().get(ConstKey.STORAGE_NEXT_POSITION));
        try {
            LOGGER.info("start write next position into {}", LogConfig.getInstance().getStorageNextPosition());//AppProperties.getAppProperties().get(ConstKey.STORAGE_NEXT_POSITION));
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(nextPosition.toString());//覆盖原内容
            out.flush();
            out.close();
        } catch (IOException e) {
            LOGGER.error("write the last position failed:",e);
        }

    }

    /**
     * 检测是否存在下一个日志文件
     * @param position
     * @return
     */
    public static Position getNextPosition(Position position){
        if (position == null){
            return null;
        }
        Position nextPosition = null;
        File files = new File(position.getPath());
        String positionName = FilenameUtils.getBaseName(position.getFileName());
        if (files.exists()) {
            for (File file : files.listFiles()) {
                String fileName = FilenameUtils.getBaseName(file.getName());
                if (FileCheck.fileFilter(file)) {
                    if (fileName.compareTo(positionName) > 0) {
                        nextPosition = new Position();
                        nextPosition.setFileName(file.getName());
                        nextPosition.setPath(position.getPath());
                        nextPosition.setLineNO(1);
                        nextPosition.setTimeSpend(position.getTimeSpend());
                        nextPosition.setRecordNum(position.getRecordNum());
                        break;
                    }
                }
            }
        }
        return nextPosition;
    }
}
