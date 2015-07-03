package com.iflytek.logcenter.extract.access.api;


import com.iflytek.logcenter.extract.model.Position;

import java.io.IOException;

/**
 * Created by yancai on 2014/8/20.
 */
public interface ILogAccess {

    public boolean cleanHistory(Position position);

    public Position getLastPosition();

    public String read(Position position) throws IOException;

    public void write(String message);

    public void write(String messageEntry,String messageQuit);
}
