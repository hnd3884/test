package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import java.util.Collection;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import java.util.Iterator;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.model.ParameterImpl;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;

public class StubHandler implements ClientCallBridge
{
    private final BodyBuilder bodyBuilder;
    private final MessageFiller[] inFillers;
    protected final String soapAction;
    protected final boolean isOneWay;
    protected final JavaMethodImpl javaMethod;
    protected final Map<QName, CheckedExceptionImpl> checkedExceptions;
    protected SOAPVersion soapVersion;
    protected ResponseBuilder responseBuilder;
    protected MessageContextFactory packetFactory;
    
    public StubHandler(final JavaMethodImpl method, final MessageContextFactory mcf) {
        this.soapVersion = SOAPVersion.SOAP_11;
        this.checkedExceptions = new HashMap<QName, CheckedExceptionImpl>();
        for (final CheckedExceptionImpl ce : method.getCheckedExceptions()) {
            this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
        }
        final String soapActionFromBinding = method.getBinding().getSOAPAction();
        if (method.getInputAction() != null && soapActionFromBinding != null && !soapActionFromBinding.equals("")) {
            this.soapAction = method.getInputAction();
        }
        else {
            this.soapAction = soapActionFromBinding;
        }
        this.javaMethod = method;
        this.packetFactory = mcf;
        this.soapVersion = this.javaMethod.getBinding().getSOAPVersion();
        final List<ParameterImpl> rp = method.getRequestParameters();
        BodyBuilder bodyBuilder = null;
        final List<MessageFiller> fillers = new ArrayList<MessageFiller>();
        for (final ParameterImpl param : rp) {
            final ValueGetter getter = this.getValueGetterFactory().get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (!param.isWrapperStyle()) {
                        bodyBuilder = new BodyBuilder.Bare(param, this.soapVersion, getter);
                        continue;
                    }
                    if (param.getParent().getBinding().isRpcLit()) {
                        bodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
                        continue;
                    }
                    bodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
                    continue;
                }
                case HEADER: {
                    fillers.add(new MessageFiller.Header(param.getIndex(), param.getXMLBridge(), getter));
                    continue;
                }
                case ATTACHMENT: {
                    fillers.add(MessageFiller.AttachmentFiller.createAttachmentFiller(param, getter));
                    continue;
                }
                case UNBOUND: {
                    continue;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        if (bodyBuilder == null) {
            switch (this.soapVersion) {
                case SOAP_11: {
                    bodyBuilder = BodyBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    bodyBuilder = BodyBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        this.bodyBuilder = bodyBuilder;
        this.inFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
        this.responseBuilder = this.buildResponseBuilder(method, ValueSetterFactory.SYNC);
    }
    
    ResponseBuilder buildResponseBuilder(final JavaMethodImpl method, final ValueSetterFactory setterFactory) {
        final List<ParameterImpl> rp = method.getResponseParameters();
        final List<ResponseBuilder> builders = new ArrayList<ResponseBuilder>();
        for (final ParameterImpl param : rp) {
            switch (param.getOutBinding().kind) {
                case BODY: {
                    if (!param.isWrapperStyle()) {
                        final ValueSetter setter = setterFactory.get(param);
                        builders.add(new ResponseBuilder.Body(param.getXMLBridge(), setter));
                        continue;
                    }
                    if (param.getParent().getBinding().isRpcLit()) {
                        builders.add(new ResponseBuilder.RpcLit((WrapperParameter)param, setterFactory));
                        continue;
                    }
                    builders.add(new ResponseBuilder.DocLit((WrapperParameter)param, setterFactory));
                    continue;
                }
                case HEADER: {
                    final ValueSetter setter = setterFactory.get(param);
                    builders.add(new ResponseBuilder.Header(this.soapVersion, param, setter));
                    continue;
                }
                case ATTACHMENT: {
                    final ValueSetter setter = setterFactory.get(param);
                    builders.add(ResponseBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
                    continue;
                }
                case UNBOUND: {
                    final ValueSetter setter = setterFactory.get(param);
                    builders.add(new ResponseBuilder.NullSetter(setter, ResponseBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
                    continue;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        ResponseBuilder rb = null;
        switch (builders.size()) {
            case 0: {
                rb = ResponseBuilder.NONE;
                break;
            }
            case 1: {
                rb = builders.get(0);
                break;
            }
            default: {
                rb = new ResponseBuilder.Composite(builders);
                break;
            }
        }
        return rb;
    }
    
    @Override
    public Packet createRequestPacket(final JavaCallInfo args) {
        final Message msg = this.bodyBuilder.createMessage(args.getParameters());
        for (final MessageFiller filler : this.inFillers) {
            filler.fillIn(args.getParameters(), msg);
        }
        final Packet req = (Packet)this.packetFactory.createContext(msg);
        req.setState(Packet.State.ClientRequest);
        req.soapAction = this.soapAction;
        req.expectReply = !this.isOneWay;
        req.getMessage().assertOneWay(this.isOneWay);
        req.setWSDLOperation(this.getOperationName());
        return req;
    }
    
    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.SYNC;
    }
    
    @Override
    public JavaCallInfo readResponse(final Packet p, final JavaCallInfo call) throws Throwable {
        final Message msg = p.getMessage();
        if (msg.isFault()) {
            final SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
            final Throwable t = faultBuilder.createException(this.checkedExceptions);
            call.setException(t);
            throw t;
        }
        this.initArgs(call.getParameters());
        final Object ret = this.responseBuilder.readResponse(msg, call.getParameters());
        call.setReturnValue(ret);
        return call;
    }
    
    public QName getOperationName() {
        return this.javaMethod.getOperationQName();
    }
    
    public String getSoapAction() {
        return this.soapAction;
    }
    
    public boolean isOneWay() {
        return this.isOneWay;
    }
    
    protected void initArgs(final Object[] args) throws Exception {
    }
    
    @Override
    public Method getMethod() {
        return this.javaMethod.getMethod();
    }
    
    @Override
    public JavaMethod getOperationModel() {
        return this.javaMethod;
    }
}
