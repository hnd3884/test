package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.bind.api.TypeReference;
import java.util.Iterator;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import javax.jws.WebMethod;
import javax.xml.ws.Action;
import java.util.Collections;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import java.lang.reflect.Method;
import java.util.List;
import com.sun.xml.internal.ws.api.model.JavaMethod;

public final class JavaMethodImpl implements JavaMethod
{
    private String inputAction;
    private String outputAction;
    private final List<CheckedExceptionImpl> exceptions;
    private final Method method;
    final List<ParameterImpl> requestParams;
    final List<ParameterImpl> responseParams;
    private final List<ParameterImpl> unmReqParams;
    private final List<ParameterImpl> unmResParams;
    private SOAPBinding binding;
    private MEP mep;
    private QName operationName;
    private WSDLBoundOperation wsdlOperation;
    final AbstractSEIModelImpl owner;
    private final Method seiMethod;
    private QName requestPayloadName;
    private String soapAction;
    private static final Logger LOGGER;
    
    public JavaMethodImpl(final AbstractSEIModelImpl owner, final Method method, final Method seiMethod, final MetadataReader metadataReader) {
        this.inputAction = "";
        this.outputAction = "";
        this.exceptions = new ArrayList<CheckedExceptionImpl>();
        this.requestParams = new ArrayList<ParameterImpl>();
        this.responseParams = new ArrayList<ParameterImpl>();
        this.unmReqParams = Collections.unmodifiableList((List<? extends ParameterImpl>)this.requestParams);
        this.unmResParams = Collections.unmodifiableList((List<? extends ParameterImpl>)this.responseParams);
        this.owner = owner;
        this.method = method;
        this.seiMethod = seiMethod;
        this.setWsaActions(metadataReader);
    }
    
    private void setWsaActions(final MetadataReader metadataReader) {
        final Action action = (metadataReader != null) ? metadataReader.getAnnotation(Action.class, this.seiMethod) : this.seiMethod.getAnnotation(Action.class);
        if (action != null) {
            this.inputAction = action.input();
            this.outputAction = action.output();
        }
        final WebMethod webMethod = (metadataReader != null) ? metadataReader.getAnnotation(WebMethod.class, this.seiMethod) : this.seiMethod.getAnnotation(WebMethod.class);
        this.soapAction = "";
        if (webMethod != null) {
            this.soapAction = webMethod.action();
        }
        if (!this.soapAction.equals("")) {
            if (this.inputAction.equals("")) {
                this.inputAction = this.soapAction;
            }
            else if (!this.inputAction.equals(this.soapAction)) {}
        }
    }
    
    public ActionBasedOperationSignature getOperationSignature() {
        QName qname = this.getRequestPayloadName();
        if (qname == null) {
            qname = new QName("", "");
        }
        return new ActionBasedOperationSignature(this.getInputAction(), qname);
    }
    
    @Override
    public SEIModel getOwner() {
        return this.owner;
    }
    
    @Override
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public Method getSEIMethod() {
        return this.seiMethod;
    }
    
    @Override
    public MEP getMEP() {
        return this.mep;
    }
    
    void setMEP(final MEP mep) {
        this.mep = mep;
    }
    
    @Override
    public SOAPBinding getBinding() {
        if (this.binding == null) {
            return new SOAPBindingImpl();
        }
        return this.binding;
    }
    
    void setBinding(final SOAPBinding binding) {
        this.binding = binding;
    }
    
    @Deprecated
    public WSDLBoundOperation getOperation() {
        return this.wsdlOperation;
    }
    
    public void setOperationQName(final QName name) {
        this.operationName = name;
    }
    
    public QName getOperationQName() {
        return (this.wsdlOperation != null) ? this.wsdlOperation.getName() : this.operationName;
    }
    
    public String getSOAPAction() {
        return (this.wsdlOperation != null) ? this.wsdlOperation.getSOAPAction() : this.soapAction;
    }
    
    @Override
    public String getOperationName() {
        return this.operationName.getLocalPart();
    }
    
    @Override
    public String getRequestMessageName() {
        return this.getOperationName();
    }
    
    @Override
    public String getResponseMessageName() {
        if (this.mep.isOneWay()) {
            return null;
        }
        return this.getOperationName() + "Response";
    }
    
    public void setRequestPayloadName(final QName n) {
        this.requestPayloadName = n;
    }
    
    @Nullable
    @Override
    public QName getRequestPayloadName() {
        return (this.wsdlOperation != null) ? this.wsdlOperation.getRequestPayloadName() : this.requestPayloadName;
    }
    
    @Nullable
    @Override
    public QName getResponsePayloadName() {
        return (this.mep == MEP.ONE_WAY) ? null : this.wsdlOperation.getResponsePayloadName();
    }
    
    public List<ParameterImpl> getRequestParameters() {
        return this.unmReqParams;
    }
    
    public List<ParameterImpl> getResponseParameters() {
        return this.unmResParams;
    }
    
    void addParameter(final ParameterImpl p) {
        if (p.isIN() || p.isINOUT()) {
            assert !this.requestParams.contains(p);
            this.requestParams.add(p);
        }
        if (p.isOUT() || p.isINOUT()) {
            assert !this.responseParams.contains(p);
            this.responseParams.add(p);
        }
    }
    
    void addRequestParameter(final ParameterImpl p) {
        if (p.isIN() || p.isINOUT()) {
            this.requestParams.add(p);
        }
    }
    
    void addResponseParameter(final ParameterImpl p) {
        if (p.isOUT() || p.isINOUT()) {
            this.responseParams.add(p);
        }
    }
    
    @Deprecated
    public int getInputParametersCount() {
        int count = 0;
        for (final ParameterImpl param : this.requestParams) {
            if (param.isWrapperStyle()) {
                count += ((WrapperParameter)param).getWrapperChildren().size();
            }
            else {
                ++count;
            }
        }
        for (final ParameterImpl param : this.responseParams) {
            if (param.isWrapperStyle()) {
                for (final ParameterImpl wc : ((WrapperParameter)param).getWrapperChildren()) {
                    if (!wc.isResponse() && wc.isOUT()) {
                        ++count;
                    }
                }
            }
            else {
                if (param.isResponse() || !param.isOUT()) {
                    continue;
                }
                ++count;
            }
        }
        return count;
    }
    
    void addException(final CheckedExceptionImpl ce) {
        if (!this.exceptions.contains(ce)) {
            this.exceptions.add(ce);
        }
    }
    
    public CheckedExceptionImpl getCheckedException(final Class exceptionClass) {
        for (final CheckedExceptionImpl ce : this.exceptions) {
            if (ce.getExceptionClass() == exceptionClass) {
                return ce;
            }
        }
        return null;
    }
    
    public List<CheckedExceptionImpl> getCheckedExceptions() {
        return Collections.unmodifiableList((List<? extends CheckedExceptionImpl>)this.exceptions);
    }
    
    public String getInputAction() {
        return this.inputAction;
    }
    
    public String getOutputAction() {
        return this.outputAction;
    }
    
    @Deprecated
    public CheckedExceptionImpl getCheckedException(final TypeReference detailType) {
        for (final CheckedExceptionImpl ce : this.exceptions) {
            final TypeInfo actual = ce.getDetailType();
            if (actual.tagName.equals(detailType.tagName) && actual.type == detailType.type) {
                return ce;
            }
        }
        return null;
    }
    
    public boolean isAsync() {
        return this.mep.isAsync;
    }
    
    void freeze(final WSDLPort portType) {
        this.wsdlOperation = portType.getBinding().get(new QName(portType.getBinding().getPortType().getName().getNamespaceURI(), this.getOperationName()));
        if (this.wsdlOperation == null) {
            throw new WebServiceException("Method " + this.seiMethod.getName() + " is exposed as WebMethod, but there is no corresponding wsdl operation with name " + this.operationName + " in the wsdl:portType" + portType.getBinding().getPortType().getName());
        }
        if (this.inputAction.equals("")) {
            this.inputAction = this.wsdlOperation.getOperation().getInput().getAction();
        }
        else if (!this.inputAction.equals(this.wsdlOperation.getOperation().getInput().getAction())) {
            JavaMethodImpl.LOGGER.warning("Input Action on WSDL operation " + this.wsdlOperation.getName().getLocalPart() + " and @Action on its associated Web Method " + this.seiMethod.getName() + " did not match and will cause problems in dispatching the requests");
        }
        if (!this.mep.isOneWay()) {
            if (this.outputAction.equals("")) {
                this.outputAction = this.wsdlOperation.getOperation().getOutput().getAction();
            }
            for (final CheckedExceptionImpl ce : this.exceptions) {
                if (ce.getFaultAction().equals("")) {
                    final QName detailQName = ce.getDetailType().tagName;
                    final WSDLFault wsdlfault = this.wsdlOperation.getOperation().getFault(detailQName);
                    if (wsdlfault == null) {
                        JavaMethodImpl.LOGGER.warning("Mismatch between Java model and WSDL model found, For wsdl operation " + this.wsdlOperation.getName() + ",There is no matching wsdl fault with detail QName " + ce.getDetailType().tagName);
                        ce.setFaultAction(ce.getDefaultFaultAction());
                    }
                    else {
                        ce.setFaultAction(wsdlfault.getAction());
                    }
                }
            }
        }
    }
    
    final void fillTypes(final List<TypeInfo> types) {
        this.fillTypes(this.requestParams, types);
        this.fillTypes(this.responseParams, types);
        for (final CheckedExceptionImpl ce : this.exceptions) {
            types.add(ce.getDetailType());
        }
    }
    
    private void fillTypes(final List<ParameterImpl> params, final List<TypeInfo> types) {
        for (final ParameterImpl p : params) {
            p.fillTypes(types);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(JavaMethodImpl.class.getName());
    }
}
