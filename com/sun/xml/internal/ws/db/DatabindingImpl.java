package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.message.MessageContext;
import java.io.InputStream;
import java.io.IOException;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import java.io.OutputStream;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.wsdl.writer.WSDLGenerator;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceFeature;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.sei.StubAsyncHandler;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.server.sei.TieHandler;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.client.sei.StubHandler;
import java.lang.reflect.Method;
import java.util.Map;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.api.databinding.Databinding;

public final class DatabindingImpl implements Databinding
{
    AbstractSEIModelImpl seiModel;
    Map<Method, StubHandler> stubHandlers;
    Map<JavaMethodImpl, TieHandler> wsdlOpMap;
    Map<Method, TieHandler> tieHandlers;
    OperationDispatcher operationDispatcher;
    OperationDispatcher operationDispatcherNoWsdl;
    boolean clientConfig;
    Codec codec;
    MessageContextFactory packetFactory;
    
    public DatabindingImpl(final DatabindingProviderImpl p, final DatabindingConfig config) {
        this.wsdlOpMap = new HashMap<JavaMethodImpl, TieHandler>();
        this.tieHandlers = new HashMap<Method, TieHandler>();
        this.clientConfig = false;
        this.packetFactory = null;
        final RuntimeModeler modeler = new RuntimeModeler(config);
        modeler.setClassLoader(config.getClassLoader());
        this.seiModel = modeler.buildRuntimeModel();
        final WSDLPort wsdlport = config.getWsdlPort();
        this.packetFactory = new MessageContextFactory(this.seiModel.getWSBinding().getFeatures());
        this.clientConfig = this.isClientConfig(config);
        if (this.clientConfig) {
            this.initStubHandlers();
        }
        this.seiModel.setDatabinding(this);
        if (wsdlport != null) {
            this.freeze(wsdlport);
        }
        if (this.operationDispatcher == null) {
            this.operationDispatcherNoWsdl = new OperationDispatcher(null, this.seiModel.getWSBinding(), this.seiModel);
        }
        for (final JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            if (!jm.isAsync()) {
                final TieHandler th = new TieHandler(jm, this.seiModel.getWSBinding(), this.packetFactory);
                this.wsdlOpMap.put(jm, th);
                this.tieHandlers.put(th.getMethod(), th);
            }
        }
    }
    
    private boolean isClientConfig(final DatabindingConfig config) {
        return config.getContractClass() != null && config.getContractClass().isInterface() && (config.getEndpointClass() == null || config.getEndpointClass().isInterface());
    }
    
    public void freeze(final WSDLPort port) {
        if (this.clientConfig) {
            return;
        }
        synchronized (this) {
            if (this.operationDispatcher == null) {
                this.operationDispatcher = ((port == null) ? null : new OperationDispatcher(port, this.seiModel.getWSBinding(), this.seiModel));
            }
        }
    }
    
    public SEIModel getModel() {
        return this.seiModel;
    }
    
    private void initStubHandlers() {
        this.stubHandlers = new HashMap<Method, StubHandler>();
        final Map<ActionBasedOperationSignature, JavaMethodImpl> syncs = new HashMap<ActionBasedOperationSignature, JavaMethodImpl>();
        for (final JavaMethodImpl m : this.seiModel.getJavaMethods()) {
            if (!m.getMEP().isAsync) {
                final StubHandler handler = new StubHandler(m, this.packetFactory);
                syncs.put(m.getOperationSignature(), m);
                this.stubHandlers.put(m.getMethod(), handler);
            }
        }
        for (final JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            final JavaMethodImpl sync = syncs.get(jm.getOperationSignature());
            if (jm.getMEP() == MEP.ASYNC_CALLBACK || jm.getMEP() == MEP.ASYNC_POLL) {
                final Method i = jm.getMethod();
                final StubAsyncHandler handler2 = new StubAsyncHandler(jm, sync, this.packetFactory);
                this.stubHandlers.put(i, handler2);
            }
        }
    }
    
    JavaMethodImpl resolveJavaMethod(final Packet req) throws DispatchException {
        WSDLOperationMapping m = req.getWSDLOperationMapping();
        if (m == null) {
            synchronized (this) {
                m = ((this.operationDispatcher != null) ? this.operationDispatcher.getWSDLOperationMapping(req) : this.operationDispatcherNoWsdl.getWSDLOperationMapping(req));
            }
        }
        return (JavaMethodImpl)m.getJavaMethod();
    }
    
    public JavaCallInfo deserializeRequest(final Packet req) {
        final com.sun.xml.internal.ws.api.databinding.JavaCallInfo call = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();
        try {
            final JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
            final TieHandler tie = this.wsdlOpMap.get(wsdlOp);
            call.setMethod(tie.getMethod());
            final Object[] args = tie.readRequest(req.getMessage());
            call.setParameters(args);
        }
        catch (final DispatchException e) {
            call.setException(e);
        }
        return call;
    }
    
    public JavaCallInfo deserializeResponse(final Packet res, final JavaCallInfo call) {
        final StubHandler stubHandler = this.stubHandlers.get(call.getMethod());
        try {
            return stubHandler.readResponse(res, call);
        }
        catch (final Throwable e) {
            call.setException(e);
            return call;
        }
    }
    
    public WebServiceFeature[] getFeatures() {
        return null;
    }
    
    @Override
    public Packet serializeRequest(final JavaCallInfo call) {
        final StubHandler stubHandler = this.stubHandlers.get(call.getMethod());
        final Packet p = stubHandler.createRequestPacket(call);
        p.setState(Packet.State.ClientRequest);
        return p;
    }
    
    @Override
    public Packet serializeResponse(final JavaCallInfo call) {
        final Method method = call.getMethod();
        Message message = null;
        if (method != null) {
            final TieHandler th = this.tieHandlers.get(method);
            if (th != null) {
                return th.serializeResponse(call);
            }
        }
        if (call.getException() instanceof DispatchException) {
            message = ((DispatchException)call.getException()).fault;
        }
        final Packet p = (Packet)this.packetFactory.createContext(message);
        p.setState(Packet.State.ServerResponse);
        return p;
    }
    
    @Override
    public ClientCallBridge getClientBridge(final Method method) {
        return this.stubHandlers.get(method);
    }
    
    @Override
    public void generateWSDL(final WSDLGenInfo info) {
        final WSDLGenerator wsdlGen = new WSDLGenerator(this.seiModel, info.getWsdlResolver(), this.seiModel.getWSBinding(), info.getContainer(), this.seiModel.getEndpointClass(), info.isInlineSchemas(), info.isSecureXmlProcessingDisabled(), info.getExtensions());
        wsdlGen.doGeneration();
    }
    
    @Override
    public EndpointCallBridge getEndpointBridge(final Packet req) throws DispatchException {
        final JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
        return this.wsdlOpMap.get(wsdlOp);
    }
    
    Codec getCodec() {
        if (this.codec == null) {
            this.codec = ((BindingImpl)this.seiModel.getWSBinding()).createCodec();
        }
        return this.codec;
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) throws IOException {
        return this.getCodec().encode(packet, out);
    }
    
    @Override
    public void decode(final InputStream in, final String ct, final Packet p) throws IOException {
        this.getCodec().decode(in, ct, p);
    }
    
    @Override
    public JavaCallInfo createJavaCallInfo(final Method method, final Object[] args) {
        return new com.sun.xml.internal.ws.api.databinding.JavaCallInfo(method, args);
    }
    
    @Override
    public JavaCallInfo deserializeResponse(final MessageContext message, final JavaCallInfo call) {
        return this.deserializeResponse((Packet)message, call);
    }
    
    @Override
    public JavaCallInfo deserializeRequest(final MessageContext message) {
        return this.deserializeRequest((Packet)message);
    }
    
    @Override
    public MessageContextFactory getMessageContextFactory() {
        return this.packetFactory;
    }
}
