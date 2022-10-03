package com.me.devicemanagement.framework.server.httpclient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.json.JSONObject;

public class DMHttpRequest
{
    public String url;
    public JSONObject headers;
    public JSONObject parameters;
    public byte[] data;
    public String method;
    public boolean useProxy;
    
    public DMHttpRequest() {
        this.url = null;
        this.headers = null;
        this.parameters = null;
        this.data = null;
        this.method = "GET";
        this.useProxy = true;
    }
    
    public InputStream getBody() {
        if (this.data != null) {
            return new ByteArrayInputStream(this.data);
        }
        return null;
    }
}
