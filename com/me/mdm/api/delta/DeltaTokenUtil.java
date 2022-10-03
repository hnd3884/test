package com.me.mdm.api.delta;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.mdm.api.error.APIHTTPException;
import org.bouncycastle.util.encoders.Base64;

public class DeltaTokenUtil
{
    private Long requestTimestamp;
    private String requestURL;
    
    public DeltaTokenUtil(final String requestURL) {
        this.requestURL = null;
        this.requestURL = requestURL.split("\\?")[0];
        this.requestTimestamp = System.currentTimeMillis();
    }
    
    public DeltaTokenUtil(final String token, final String requestURL) throws APIHTTPException {
        this.requestURL = null;
        final String text = new String(Base64.decode(token));
        final String[] values = text.split("::");
        if (values.length != 2) {
            throw new APIHTTPException("COM0021", new Object[0]);
        }
        this.requestTimestamp = Long.valueOf(values[1]);
        this.requestURL = String.valueOf(values[0]);
        if (!requestURL.split("\\?")[0].equals(this.requestURL.split("\\?")[0])) {
            throw new APIHTTPException("COM0021", new Object[0]);
        }
    }
    
    public String getDeltaToken() {
        final String text = String.valueOf(this.requestURL) + "::" + String.valueOf(this.requestTimestamp);
        try {
            return URLEncoder.encode(Base64.toBase64String(text.getBytes()), "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Long getRequestTimestamp() {
        return this.requestTimestamp;
    }
}
