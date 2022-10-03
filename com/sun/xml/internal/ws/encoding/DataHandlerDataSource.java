package com.sun.xml.internal.ws.encoding;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class DataHandlerDataSource implements DataSource
{
    private final DataHandler dataHandler;
    
    public DataHandlerDataSource(final DataHandler dh) {
        this.dataHandler = dh;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.dataHandler.getInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.dataHandler.getOutputStream();
    }
    
    @Override
    public String getContentType() {
        return this.dataHandler.getContentType();
    }
    
    @Override
    public String getName() {
        return this.dataHandler.getName();
    }
}
