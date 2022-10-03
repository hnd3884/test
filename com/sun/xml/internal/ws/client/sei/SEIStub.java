package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.api.message.Packet;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import com.sun.xml.internal.ws.api.server.Container;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import java.lang.reflect.Method;
import java.util.Map;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import java.lang.reflect.InvocationHandler;
import com.sun.xml.internal.ws.client.Stub;

public final class SEIStub extends Stub implements InvocationHandler
{
    Databinding databinding;
    public final SOAPSEIModel seiModel;
    public final SOAPVersion soapVersion;
    private final Map<Method, MethodHandler> methodHandlers;
    
    @Deprecated
    public SEIStub(final WSServiceDelegate owner, final BindingImpl binding, final SOAPSEIModel seiModel, final Tube master, final WSEndpointReference epr) {
        super(owner, master, binding, seiModel.getPort(), seiModel.getPort().getAddress(), epr);
        this.methodHandlers = new HashMap<Method, MethodHandler>();
        this.seiModel = seiModel;
        this.soapVersion = binding.getSOAPVersion();
        this.databinding = seiModel.getDatabinding();
        this.initMethodHandlers();
    }
    
    public SEIStub(final WSPortInfo portInfo, final BindingImpl binding, final SOAPSEIModel seiModel, final WSEndpointReference epr) {
        super(portInfo, binding, seiModel.getPort().getAddress(), epr);
        this.methodHandlers = new HashMap<Method, MethodHandler>();
        this.seiModel = seiModel;
        this.soapVersion = binding.getSOAPVersion();
        this.databinding = seiModel.getDatabinding();
        this.initMethodHandlers();
    }
    
    private void initMethodHandlers() {
        final Map<WSDLBoundOperation, JavaMethodImpl> syncs = new HashMap<WSDLBoundOperation, JavaMethodImpl>();
        for (final JavaMethodImpl m : this.seiModel.getJavaMethods()) {
            if (!m.getMEP().isAsync) {
                final SyncMethodHandler handler = new SyncMethodHandler(this, m);
                syncs.put(m.getOperation(), m);
                this.methodHandlers.put(m.getMethod(), handler);
            }
        }
        for (final JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            final JavaMethodImpl sync = syncs.get(jm.getOperation());
            if (jm.getMEP() == MEP.ASYNC_CALLBACK) {
                final Method i = jm.getMethod();
                final CallbackMethodHandler handler2 = new CallbackMethodHandler(this, i, i.getParameterTypes().length - 1);
                this.methodHandlers.put(i, handler2);
            }
            if (jm.getMEP() == MEP.ASYNC_POLL) {
                final Method i = jm.getMethod();
                final PollingMethodHandler handler3 = new PollingMethodHandler(this, i);
                this.methodHandlers.put(i, handler3);
            }
        }
    }
    
    @Nullable
    @Override
    public OperationDispatcher getOperationDispatcher() {
        if (this.operationDispatcher == null && this.wsdlPort != null) {
            this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel);
        }
        return this.operationDispatcher;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        this.validateInputs(proxy, method);
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            final MethodHandler handler = this.methodHandlers.get(method);
            if (handler != null) {
                return handler.invoke(proxy, args);
            }
            try {
                return method.invoke(this, args);
            }
            catch (final IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
            catch (final IllegalArgumentException e2) {
                throw new AssertionError((Object)e2);
            }
            catch (final InvocationTargetException e3) {
                throw e3.getCause();
            }
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    private void validateInputs(final Object proxy, final Method method) {
        if (proxy == null || !Proxy.isProxyClass(proxy.getClass())) {
            throw new IllegalStateException("Passed object is not proxy!");
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        if (method == null || declaringClass == null || Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Invoking static method is not allowed!");
        }
    }
    
    public final Packet doProcess(final Packet request, final RequestContext rc, final ResponseContextReceiver receiver) {
        return super.process(request, rc, receiver);
    }
    
    public final void doProcessAsync(final AsyncResponseImpl<?> receiver, final Packet request, final RequestContext rc, final Fiber.CompletionCallback callback) {
        super.processAsync(receiver, request, rc, callback);
    }
    
    @NotNull
    @Override
    protected final QName getPortName() {
        return this.wsdlPort.getName();
    }
    
    @Override
    public void setOutboundHeaders(final Object... headers) {
        if (headers == null) {
            throw new IllegalArgumentException();
        }
        final Header[] hl = new Header[headers.length];
        for (int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
                throw new IllegalArgumentException();
            }
            hl[i] = Headers.create(this.seiModel.getBindingContext(), headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}
