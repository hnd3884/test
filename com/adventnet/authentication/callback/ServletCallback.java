package com.adventnet.authentication.callback;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class ServletCallback implements Callback, Serializable
{
    private HttpServletRequest request;
    
    public ServletCallback() {
        this.request = null;
    }
    
    public void setHttpServletRequest(final HttpServletRequest request) {
        this.request = request;
    }
    
    public HttpServletRequest getHttpServletRequest() {
        return this.request;
    }
}
