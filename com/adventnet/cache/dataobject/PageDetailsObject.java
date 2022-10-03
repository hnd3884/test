package com.adventnet.cache.dataobject;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.Cookie;
import java.util.List;
import java.io.Serializable;

public class PageDetailsObject implements Serializable
{
    private List<String[]> headerList;
    private List<Cookie> cookieList;
    private String type;
    private int status;
    private byte[] compressedBodyContent;
    private final int packetLen = 4196;
    
    public PageDetailsObject(final List<String[]> headerList, final List<Cookie> cookieList, final String type, final int status, final byte[] unCompressedBodyContent) throws IOException {
        this.headerList = headerList;
        this.cookieList = cookieList;
        this.type = type;
        this.status = status;
        this.compressedBodyContent = this.compress(unCompressedBodyContent);
    }
    
    private byte[] compress(final byte[] unCompressedBodyContent) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final GZIPOutputStream gos = new GZIPOutputStream(bytes);
        gos.write(unCompressedBodyContent);
        gos.close();
        return bytes.toByteArray();
    }
    
    public List<String[]> getHeaderList() {
        return this.headerList;
    }
    
    public void setHeaderList(final List<String[]> headerList) {
        this.headerList = headerList;
    }
    
    public List<Cookie> getCookieList() {
        return this.cookieList;
    }
    
    public void setCookieList(final List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public byte[] getCompressedBodyContent() {
        return this.compressedBodyContent;
    }
    
    public void setCompressedBodyContent(final byte[] compressedBodyContent) {
        this.compressedBodyContent = compressedBodyContent;
    }
    
    public byte[] getUnCompressedBodyContent() throws IOException {
        final GZIPInputStream zipStream = new GZIPInputStream(new ByteArrayInputStream(this.compressedBodyContent));
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream(this.compressedBodyContent.length);
        final byte[] buffer = new byte[4196];
        int read = 0;
        while ((read = zipStream.read(buffer, 0, 4196)) != -1) {
            outStream.write(buffer, 0, read);
        }
        return outStream.toByteArray();
    }
}
