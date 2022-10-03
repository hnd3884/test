package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Message;
import java.util.Collection;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import java.util.Iterator;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.model.ParameterImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.model.JavaMethodImpl;

abstract class SEIMethodHandler extends MethodHandler
{
    private BodyBuilder bodyBuilder;
    private MessageFiller[] inFillers;
    protected String soapAction;
    protected boolean isOneWay;
    protected JavaMethodImpl javaMethod;
    protected Map<QName, CheckedExceptionImpl> checkedExceptions;
    
    SEIMethodHandler(final SEIStub owner) {
        super(owner, null);
    }
    
    SEIMethodHandler(final SEIStub owner, final JavaMethodImpl method) {
        super(owner, null);
        this.checkedExceptions = new HashMap<QName, CheckedExceptionImpl>();
        for (final CheckedExceptionImpl ce : method.getCheckedExceptions()) {
            this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
        }
        if (method.getInputAction() != null && !method.getBinding().getSOAPAction().equals("")) {
            this.soapAction = method.getInputAction();
        }
        else {
            this.soapAction = method.getBinding().getSOAPAction();
        }
        this.javaMethod = method;
        final List<ParameterImpl> rp = method.getRequestParameters();
        BodyBuilder tmpBodyBuilder = null;
        final List<MessageFiller> fillers = new ArrayList<MessageFiller>();
        for (final ParameterImpl param : rp) {
            final ValueGetter getter = this.getValueGetterFactory().get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (!param.isWrapperStyle()) {
                        tmpBodyBuilder = new BodyBuilder.Bare(param, owner.soapVersion, getter);
                        continue;
                    }
                    if (param.getParent().getBinding().isRpcLit()) {
                        tmpBodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
                        continue;
                    }
                    tmpBodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
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
            switch (owner.soapVersion) {
                case SOAP_11: {
                    tmpBodyBuilder = BodyBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    tmpBodyBuilder = BodyBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        this.bodyBuilder = tmpBodyBuilder;
        this.inFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
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
                    builders.add(new ResponseBuilder.Header(this.owner.soapVersion, param, setter));
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
    
    Message createRequestMessage(final Object[] args) {
        final Message msg = this.bodyBuilder.createMessage(args);
        for (final MessageFiller filler : this.inFillers) {
            filler.fillIn(args, msg);
        }
        return msg;
    }
    
    abstract ValueGetterFactory getValueGetterFactory();
}
