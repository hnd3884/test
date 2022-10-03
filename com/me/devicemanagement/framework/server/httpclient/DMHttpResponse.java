package com.me.devicemanagement.framework.server.httpclient;

import org.json.JSONObject;
import java.io.InputStream;

public class DMHttpResponse
{
    public String responseBodyAsString;
    public InputStream responseBodyAsStream;
    public byte[] responseBodyAsBytes;
    public JSONObject responseHeaders;
    public int status;
    
    public DMHttpResponse() {
        this.responseBodyAsString = null;
        this.responseBodyAsStream = null;
        this.responseBodyAsBytes = null;
        this.responseHeaders = null;
        this.status = 200;
    }
}
