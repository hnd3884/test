package com.me.mdm.chrome.agent.core;

import com.me.mdm.chrome.agent.Context;

public class MDMContainer
{
    private Request request;
    private Response response;
    private MDMServerContext serverContext;
    private Context context;
    
    public MDMContainer(final Context context) {
        this.request = null;
        this.response = null;
        this.serverContext = null;
        this.context = null;
        this.context = context;
    }
    
    public void setRequest(final Request request) {
        this.request = request;
    }
    
    public Request getRequest() {
        return this.request;
    }
    
    public void setResponse(final Response response) {
        this.response = response;
    }
    
    public Response getResponse() {
        return this.response;
    }
    
    public void setServerContext(final MDMServerContext serverContext) {
        this.serverContext = serverContext;
    }
    
    public MDMServerContext getServerContext() {
        return this.serverContext;
    }
    
    public Context getContext() {
        return this.context;
    }
}
