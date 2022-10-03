package com.me.devicemanagement.framework.server.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;

public class ByteArrayDataSource implements DataSource
{
    private final String m_strName;
    private final String m_strType;
    private byte[] m_baData;
    
    public ByteArrayDataSource(final String strName, final byte[] baData, final String strType) {
        this.m_strType = strType;
        this.m_baData = baData;
        this.m_strName = strName;
    }
    
    public ByteArrayDataSource(final String strName, final String strData, final String strType) {
        this.m_strType = strType;
        this.m_baData = strData.getBytes();
        this.m_strName = strName;
    }
    
    @Override
    public String getContentType() {
        return this.m_strType;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.m_baData);
    }
    
    @Override
    public String getName() {
        return this.m_strName;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new MyByteArrayOutputStream(this.m_baData);
    }
    
    private class MyByteArrayOutputStream extends ByteArrayOutputStream
    {
        MyByteArrayOutputStream(final byte[] baData) {
            this.buf = baData;
        }
    }
}
