package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import javax.xml.ws.ProtocolException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import java.util.logging.Level;
import java.lang.reflect.InvocationTargetException;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Message;
import java.util.Iterator;
import java.util.Collection;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.model.ParameterImpl;
import java.util.List;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;

public final class TieHandler implements EndpointCallBridge
{
    private final SOAPVersion soapVersion;
    private final Method method;
    private final int noOfArgs;
    private final JavaMethodImpl javaMethodModel;
    private final Boolean isOneWay;
    private final EndpointArgumentsBuilder argumentsBuilder;
    private final EndpointResponseMessageBuilder bodyBuilder;
    private final MessageFiller[] outFillers;
    protected MessageContextFactory packetFactory;
    private static final Logger LOGGER;
    
    public TieHandler(final JavaMethodImpl method, final WSBinding binding, final MessageContextFactory mcf) {
        this.soapVersion = binding.getSOAPVersion();
        this.method = method.getMethod();
        this.javaMethodModel = method;
        this.argumentsBuilder = this.createArgumentsBuilder();
        final List<MessageFiller> fillers = new ArrayList<MessageFiller>();
        this.bodyBuilder = this.createResponseMessageBuilder(fillers);
        this.outFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
        this.noOfArgs = this.method.getParameterTypes().length;
        this.packetFactory = mcf;
    }
    
    private EndpointArgumentsBuilder createArgumentsBuilder() {
        final List<ParameterImpl> rp = this.javaMethodModel.getRequestParameters();
        final List<EndpointArgumentsBuilder> builders = new ArrayList<EndpointArgumentsBuilder>();
        for (final ParameterImpl param : rp) {
            final EndpointValueSetter setter = EndpointValueSetter.get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (!param.isWrapperStyle()) {
                        builders.add(new EndpointArgumentsBuilder.Body(param.getXMLBridge(), setter));
                        continue;
                    }
                    if (param.getParent().getBinding().isRpcLit()) {
                        builders.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)param));
                        continue;
                    }
                    builders.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)param, WebParam.Mode.OUT));
                    continue;
                }
                case HEADER: {
                    builders.add(new EndpointArgumentsBuilder.Header(this.soapVersion, param, setter));
                    continue;
                }
                case ATTACHMENT: {
                    builders.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
                    continue;
                }
                case UNBOUND: {
                    builders.add(new EndpointArgumentsBuilder.NullSetter(setter, EndpointArgumentsBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
                    continue;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        final List<ParameterImpl> resp = this.javaMethodModel.getResponseParameters();
        for (final ParameterImpl param2 : resp) {
            if (param2.isWrapperStyle()) {
                final WrapperParameter wp = (WrapperParameter)param2;
                final List<ParameterImpl> children = wp.getWrapperChildren();
                for (final ParameterImpl p : children) {
                    if (p.isOUT() && p.getIndex() != -1) {
                        final EndpointValueSetter setter2 = EndpointValueSetter.get(p);
                        builders.add(new EndpointArgumentsBuilder.NullSetter(setter2, null));
                    }
                }
            }
            else {
                if (!param2.isOUT() || param2.getIndex() == -1) {
                    continue;
                }
                final EndpointValueSetter setter3 = EndpointValueSetter.get(param2);
                builders.add(new EndpointArgumentsBuilder.NullSetter(setter3, null));
            }
        }
        EndpointArgumentsBuilder argsBuilder = null;
        switch (builders.size()) {
            case 0: {
                argsBuilder = EndpointArgumentsBuilder.NONE;
                break;
            }
            case 1: {
                argsBuilder = builders.get(0);
                break;
            }
            default: {
                argsBuilder = new EndpointArgumentsBuilder.Composite(builders);
                break;
            }
        }
        return argsBuilder;
    }
    
    private EndpointResponseMessageBuilder createResponseMessageBuilder(final List<MessageFiller> fillers) {
        EndpointResponseMessageBuilder tmpBodyBuilder = null;
        final List<ParameterImpl> rp = this.javaMethodModel.getResponseParameters();
        for (final ParameterImpl param : rp) {
            final ValueGetter getter = ValueGetter.get(param);
            switch (param.getOutBinding().kind) {
                case BODY: {
                    if (!param.isWrapperStyle()) {
                        tmpBodyBuilder = new EndpointResponseMessageBuilder.Bare(param, this.soapVersion);
                        continue;
                    }
                    if (param.getParent().getBinding().isRpcLit()) {
                        tmpBodyBuilder = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)param, this.soapVersion);
                        continue;
                    }
                    tmpBodyBuilder = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)param, this.soapVersion);
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
        if (tmpBodyBuilder == null) {
            switch (this.soapVersion) {
                case SOAP_11: {
                    tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        return tmpBodyBuilder;
    }
    
    public Object[] readRequest(final Message reqMsg) {
        final Object[] args = new Object[this.noOfArgs];
        try {
            this.argumentsBuilder.readRequest(reqMsg, args);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
        return args;
    }
    
    public Message createResponse(final JavaCallInfo call) {
        Message responseMessage;
        if (call.getException() == null) {
            responseMessage = (((boolean)this.isOneWay) ? null : this.createResponseMessage(call.getParameters(), call.getReturnValue()));
        }
        else {
            final Throwable e = call.getException();
            final Throwable serviceException = this.getServiceException(e);
            if (e instanceof InvocationTargetException || serviceException != null) {
                if (serviceException != null) {
                    TieHandler.LOGGER.log(Level.FINE, serviceException.getMessage(), serviceException);
                    responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.javaMethodModel.getCheckedException(serviceException.getClass()), serviceException);
                }
                else {
                    final Throwable cause = e.getCause();
                    if (cause instanceof ProtocolException) {
                        TieHandler.LOGGER.log(Level.FINE, cause.getMessage(), cause);
                    }
                    else {
                        TieHandler.LOGGER.log(Level.SEVERE, cause.getMessage(), cause);
                    }
                    responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, cause);
                }
            }
            else if (e instanceof DispatchException) {
                responseMessage = ((DispatchException)e).fault;
            }
            else {
                TieHandler.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
            }
        }
        return responseMessage;
    }
    
    Throwable getServiceException(final Throwable throwable) {
        if (this.javaMethodModel.getCheckedException(throwable.getClass()) != null) {
            return throwable;
        }
        if (throwable.getCause() != null) {
            final Throwable cause = throwable.getCause();
            if (this.javaMethodModel.getCheckedException(cause.getClass()) != null) {
                return cause;
            }
        }
        return null;
    }
    
    private Message createResponseMessage(final Object[] args, final Object returnValue) {
        final Message msg = this.bodyBuilder.createMessage(args, returnValue);
        for (final MessageFiller filler : this.outFillers) {
            filler.fillIn(args, returnValue, msg);
        }
        return msg;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public JavaCallInfo deserializeRequest(final Packet req) {
        final com.sun.xml.internal.ws.api.databinding.JavaCallInfo call = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();
        call.setMethod(this.getMethod());
        final Object[] args = this.readRequest(req.getMessage());
        call.setParameters(args);
        return call;
    }
    
    @Override
    public Packet serializeResponse(final JavaCallInfo call) {
        final Message msg = this.createResponse(call);
        final Packet p = (Packet)((msg == null) ? this.packetFactory.createContext() : ((Packet)this.packetFactory.createContext(msg)));
        p.setState(Packet.State.ServerResponse);
        return p;
    }
    
    @Override
    public JavaMethod getOperationModel() {
        return this.javaMethodModel;
    }
    
    static {
        LOGGER = Logger.getLogger(TieHandler.class.getName());
    }
}
