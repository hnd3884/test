package com.me.devicemanagement.framework.server.httpclient;

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

public class DMDeleteMethod extends EntityEnclosingMethod
{
    public DMDeleteMethod(final String uri) {
        super(uri);
    }
    
    public String getName() {
        return "DELETE";
    }
}
