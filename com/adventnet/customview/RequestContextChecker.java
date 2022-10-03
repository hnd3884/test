package com.adventnet.customview;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RequestContextChecker
{
    private static int instanceIdCounter;
    private int instanceId;
    private static byte[] lockObj;
    private int sessionId;
    private static final long TWO_MINUTES = 120000L;
    private static final Logger OUT;
    List requestContexts;
    boolean started;
    private int requestID;
    
    public RequestContextChecker() throws CustomViewException {
        this.requestContexts = new ArrayList();
        this.started = false;
        this.requestID = 0;
        synchronized (RequestContextChecker.lockObj) {
            this.instanceId = RequestContextChecker.instanceIdCounter++;
        }
        try {
            RequestContextChecker.OUT.log(Level.FINER, " RequestContextChecker[{0}] Inside constructor...", new Integer(this.instanceId));
        }
        catch (final Exception e) {
            throw new CustomViewException(e);
        }
    }
    
    public int getSessionID() {
        return this.sessionId;
    }
    
    public synchronized CustomViewRequest validate(final CustomViewRequest customViewRequest) throws CustomViewException {
        if (!this.started) {
            this.started = true;
        }
        final RequestContext requestContext = customViewRequest.getRequestContext();
        for (int i = this.requestContexts.size() - 1; i > -1; --i) {
            if (requestContext == this.requestContexts.get(i)) {
                customViewRequest.setNew(false);
                return customViewRequest;
            }
        }
        final CustomViewRequest clonedCustomViewRequest = (CustomViewRequest)customViewRequest.clone();
        clonedCustomViewRequest.setRequestContext(this.generateNewRequestContext());
        clonedCustomViewRequest.setNew(true);
        return clonedCustomViewRequest;
    }
    
    private synchronized RequestContext generateNewRequestContext() {
        final RequestContext requestContext = new RequestContext(this.sessionId, this.requestID++);
        this.requestContexts.add(requestContext);
        return requestContext;
    }
    
    public synchronized void cleanup() {
        try {
            if (this.started) {
                this.started = false;
            }
        }
        catch (final Exception e) {
            RequestContextChecker.OUT.log(Level.INFO, "Exception during cleanup : {0}", e);
        }
    }
    
    @Override
    public String toString() {
        return "<RequestContextChecker>" + this.requestContexts + "</RequestContextChecker>";
    }
    
    public void finalize() {
        this.cleanup();
    }
    
    static {
        RequestContextChecker.instanceIdCounter = 0;
        RequestContextChecker.lockObj = new byte[0];
        OUT = Logger.getLogger(RequestContextChecker.class.getName());
    }
}
