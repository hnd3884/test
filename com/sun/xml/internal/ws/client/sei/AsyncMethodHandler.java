package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceException;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import javax.xml.ws.Response;
import javax.xml.ws.AsyncHandler;
import java.lang.reflect.Method;

abstract class AsyncMethodHandler extends MethodHandler
{
    AsyncMethodHandler(final SEIStub owner, final Method m) {
        super(owner, m);
    }
    
    protected final Response<Object> doInvoke(final Object proxy, final Object[] args, final AsyncHandler handler) {
        final AsyncInvoker invoker = new SEIAsyncInvoker(proxy, args);
        invoker.setNonNullAsyncHandlerGiven(handler != null);
        final AsyncResponseImpl<Object> ft = new AsyncResponseImpl<Object>(invoker, handler);
        invoker.setReceiver(ft);
        ft.run();
        return ft;
    }
    
    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.ASYNC;
    }
    
    private class SEIAsyncInvoker extends AsyncInvoker
    {
        private final RequestContext rc;
        private final Object[] args;
        
        SEIAsyncInvoker(final Object proxy, final Object[] args) {
            this.rc = AsyncMethodHandler.this.owner.requestContext.copy();
            this.args = args;
        }
        
        @Override
        public void do_run() {
            final JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, this.args);
            final Packet req = (Packet)AsyncMethodHandler.this.owner.databinding.serializeRequest(call);
            final Fiber.CompletionCallback callback = new Fiber.CompletionCallback() {
                @Override
                public void onCompletion(@NotNull final Packet response) {
                    SEIAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
                    final Message msg = response.getMessage();
                    if (msg == null) {
                        return;
                    }
                    try {
                        final Object[] rargs = { null };
                        JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, rargs);
                        call = AsyncMethodHandler.this.owner.databinding.deserializeResponse(response, call);
                        if (call.getException() != null) {
                            throw call.getException();
                        }
                        SEIAsyncInvoker.this.responseImpl.set(rargs[0], null);
                    }
                    catch (final Throwable t) {
                        if (t instanceof RuntimeException) {
                            if (t instanceof WebServiceException) {
                                SEIAsyncInvoker.this.responseImpl.set(null, t);
                                return;
                            }
                        }
                        else if (t instanceof Exception) {
                            SEIAsyncInvoker.this.responseImpl.set(null, t);
                            return;
                        }
                        SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(t));
                    }
                }
                
                @Override
                public void onCompletion(@NotNull final Throwable error) {
                    if (error instanceof WebServiceException) {
                        SEIAsyncInvoker.this.responseImpl.set(null, error);
                    }
                    else {
                        SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(error));
                    }
                }
            };
            AsyncMethodHandler.this.owner.doProcessAsync(this.responseImpl, req, this.rc, callback);
        }
    }
}
