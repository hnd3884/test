package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;

public class HttpConstants
{
    public static final String HTTP_ELEMENT_CHARSET = "US-ASCII";
    public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    private static final Log LOG;
    
    public static byte[] getBytes(final String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            if (HttpConstants.LOG.isWarnEnabled()) {
                HttpConstants.LOG.warn((Object)"Unsupported encoding: US-ASCII. System default encoding used");
            }
            return data.getBytes();
        }
    }
    
    public static String getString(final byte[] data, final int offset, final int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, "US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            if (HttpConstants.LOG.isWarnEnabled()) {
                HttpConstants.LOG.warn((Object)"Unsupported encoding: US-ASCII. System default encoding used");
            }
            return new String(data, offset, length);
        }
    }
    
    public static String getString(final byte[] data) {
        return getString(data, 0, data.length);
    }
    
    public static byte[] getContentBytes(final String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.equals("")) {
            charset = "ISO-8859-1";
        }
        try {
            return data.getBytes(charset);
        }
        catch (final UnsupportedEncodingException e) {
            if (HttpConstants.LOG.isWarnEnabled()) {
                HttpConstants.LOG.warn((Object)("Unsupported encoding: " + charset + ". HTTP default encoding used"));
            }
            try {
                return data.getBytes("ISO-8859-1");
            }
            catch (final UnsupportedEncodingException e2) {
                if (HttpConstants.LOG.isWarnEnabled()) {
                    HttpConstants.LOG.warn((Object)"Unsupported encoding: ISO-8859-1. System encoding used");
                }
                return data.getBytes();
            }
        }
    }
    
    public static String getContentString(final byte[] data, final int offset, final int length, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.equals("")) {
            charset = "ISO-8859-1";
        }
        try {
            return new String(data, offset, length, charset);
        }
        catch (final UnsupportedEncodingException e) {
            if (HttpConstants.LOG.isWarnEnabled()) {
                HttpConstants.LOG.warn((Object)("Unsupported encoding: " + charset + ". Default HTTP encoding used"));
            }
            try {
                return new String(data, offset, length, "ISO-8859-1");
            }
            catch (final UnsupportedEncodingException e2) {
                if (HttpConstants.LOG.isWarnEnabled()) {
                    HttpConstants.LOG.warn((Object)"Unsupported encoding: ISO-8859-1. System encoding used");
                }
                return new String(data, offset, length);
            }
        }
    }
    
    public static String getContentString(final byte[] data, final String charset) {
        return getContentString(data, 0, data.length, charset);
    }
    
    public static byte[] getContentBytes(final String data) {
        return getContentBytes(data, null);
    }
    
    public static String getContentString(final byte[] data, final int offset, final int length) {
        return getContentString(data, offset, length, null);
    }
    
    public static String getContentString(final byte[] data) {
        return getContentString(data, null);
    }
    
    public static byte[] getAsciiBytes(final String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }
    
    public static String getAsciiString(final byte[] data, final int offset, final int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, "US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }
    
    public static String getAsciiString(final byte[] data) {
        return getAsciiString(data, 0, data.length);
    }
    
    static {
        LOG = LogFactory.getLog(HttpConstants.class);
    }
}
