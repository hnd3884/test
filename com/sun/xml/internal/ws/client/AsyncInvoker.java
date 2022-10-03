package com.sun.xml.internal.ws.client;

import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker implements Runnable
{
    protected AsyncResponseImpl responseImpl;
    protected boolean nonNullAsyncHandlerGiven;
    
    public void setReceiver(final AsyncResponseImpl responseImpl) {
        this.responseImpl = responseImpl;
    }
    
    public AsyncResponseImpl getResponseImpl() {
        return this.responseImpl;
    }
    
    public void setResponseImpl(final AsyncResponseImpl responseImpl) {
        this.responseImpl = responseImpl;
    }
    
    public boolean isNonNullAsyncHandlerGiven() {
        return this.nonNullAsyncHandlerGiven;
    }
    
    public void setNonNullAsyncHandlerGiven(final boolean nonNullAsyncHandlerGiven) {
        this.nonNullAsyncHandlerGiven = nonNullAsyncHandlerGiven;
    }
    
    @Override
    public void run() {
        try {
            this.do_run();
        }
        catch (final WebServiceException e) {
            throw e;
        }
        catch (final Throwable t) {
            throw new WebServiceException(t);
        }
    }
    
    public abstract void do_run();
}
