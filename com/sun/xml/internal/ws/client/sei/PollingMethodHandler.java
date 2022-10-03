package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.lang.reflect.Method;

final class PollingMethodHandler extends AsyncMethodHandler
{
    PollingMethodHandler(final SEIStub owner, final Method m) {
        super(owner, m);
    }
    
    @Override
    Response<?> invoke(final Object proxy, final Object[] args) throws WebServiceException {
        return this.doInvoke(proxy, args, null);
    }
}
