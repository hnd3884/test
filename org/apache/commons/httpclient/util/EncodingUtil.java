package org.apache.commons.httpclient.util;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.net.URLCodec;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;

public class EncodingUtil
{
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final Log LOG;
    
    public static String formUrlEncode(final NameValuePair[] pairs, final String charset) {
        try {
            return doFormUrlEncode(pairs, charset);
        }
        catch (final UnsupportedEncodingException e) {
            EncodingUtil.LOG.error((Object)("Encoding not supported: " + charset));
            try {
                return doFormUrlEncode(pairs, "ISO-8859-1");
            }
            catch (final UnsupportedEncodingException fatal) {
                throw new HttpClientError("Encoding not supported: ISO-8859-1");
            }
        }
    }
    
    private static String doFormUrlEncode(final NameValuePair[] pairs, final String charset) throws UnsupportedEncodingException {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pairs.length; ++i) {
            final URLCodec codec = new URLCodec();
            final NameValuePair pair = pairs[i];
            if (pair.getName() != null) {
                if (i > 0) {
                    buf.append("&");
                }
                buf.append(codec.encode(pair.getName(), charset));
                buf.append("=");
                if (pair.getValue() != null) {
                    buf.append(codec.encode(pair.getValue(), charset));
                }
            }
        }
        return buf.toString();
    }
    
    public static String getString(final byte[] data, final int offset, final int length, final String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return new String(data, offset, length, charset);
        }
        catch (final UnsupportedEncodingException e) {
            if (EncodingUtil.LOG.isWarnEnabled()) {
                EncodingUtil.LOG.warn((Object)("Unsupported encoding: " + charset + ". System encoding used"));
            }
            return new String(data, offset, length);
        }
    }
    
    public static String getString(final byte[] data, final String charset) {
        return getString(data, 0, data.length, charset);
    }
    
    public static byte[] getBytes(final String data, final String charset) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return data.getBytes(charset);
        }
        catch (final UnsupportedEncodingException e) {
            if (EncodingUtil.LOG.isWarnEnabled()) {
                EncodingUtil.LOG.warn((Object)("Unsupported encoding: " + charset + ". System encoding used."));
            }
            return data.getBytes();
        }
    }
    
    public static byte[] getAsciiBytes(final String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            throw new HttpClientError("HttpClient requires ASCII support");
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
            throw new HttpClientError("HttpClient requires ASCII support");
        }
    }
    
    public static String getAsciiString(final byte[] data) {
        return getAsciiString(data, 0, data.length);
    }
    
    private EncodingUtil() {
    }
    
    static {
        LOG = LogFactory.getLog(EncodingUtil.class);
    }
}
