package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.AsyncHandler;
import java.util.concurrent.Future;
import java.lang.reflect.Method;

final class CallbackMethodHandler extends AsyncMethodHandler
{
    private final int handlerPos;
    
    CallbackMethodHandler(final SEIStub owner, final Method m, final int handlerPos) {
        super(owner, m);
        this.handlerPos = handlerPos;
    }
    
    @Override
    Future<?> invoke(final Object proxy, final Object[] args) throws WebServiceException {
        final AsyncHandler handler = (AsyncHandler)args[this.handlerPos];
        return this.doInvoke(proxy, args, handler);
    }
}
