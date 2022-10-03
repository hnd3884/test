package com.me.devicemanagement.onpremise.start.util;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;

public class UpgradeUtil implements DataSource
{
    private InputStream is;
    private String typ;
    
    public UpgradeUtil(final InputStream inputstream, final String s) {
        this.is = inputstream;
        this.typ = s;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.is;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }
    
    @Override
    public String getContentType() {
        return this.typ;
    }
    
    @Override
    public String getName() {
        return null;
    }
}
