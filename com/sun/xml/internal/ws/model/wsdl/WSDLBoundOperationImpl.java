package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import java.util.Collections;
import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import java.util.List;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;

public final class WSDLBoundOperationImpl extends AbstractExtensibleImpl implements EditableWSDLBoundOperation
{
    private final QName name;
    private final Map<String, ParameterBinding> inputParts;
    private final Map<String, ParameterBinding> outputParts;
    private final Map<String, ParameterBinding> faultParts;
    private final Map<String, String> inputMimeTypes;
    private final Map<String, String> outputMimeTypes;
    private final Map<String, String> faultMimeTypes;
    private boolean explicitInputSOAPBodyParts;
    private boolean explicitOutputSOAPBodyParts;
    private boolean explicitFaultSOAPBodyParts;
    private Boolean emptyInputBody;
    private Boolean emptyOutputBody;
    private Boolean emptyFaultBody;
    private final Map<String, EditableWSDLPart> inParts;
    private final Map<String, EditableWSDLPart> outParts;
    private final List<EditableWSDLBoundFault> wsdlBoundFaults;
    private EditableWSDLOperation operation;
    private String soapAction;
    private WSDLBoundOperation.ANONYMOUS anonymous;
    private final EditableWSDLBoundPortType owner;
    private SOAPBinding.Style style;
    private String reqNamespace;
    private String respNamespace;
    private QName requestPayloadName;
    private QName responsePayloadName;
    private boolean emptyRequestPayload;
    private boolean emptyResponsePayload;
    private Map<QName, ? extends EditableWSDLMessage> messages;
    
    public WSDLBoundOperationImpl(final XMLStreamReader xsr, final EditableWSDLBoundPortType owner, final QName name) {
        super(xsr);
        this.explicitInputSOAPBodyParts = false;
        this.explicitOutputSOAPBodyParts = false;
        this.explicitFaultSOAPBodyParts = false;
        this.style = SOAPBinding.Style.DOCUMENT;
        this.name = name;
        this.inputParts = new HashMap<String, ParameterBinding>();
        this.outputParts = new HashMap<String, ParameterBinding>();
        this.faultParts = new HashMap<String, ParameterBinding>();
        this.inputMimeTypes = new HashMap<String, String>();
        this.outputMimeTypes = new HashMap<String, String>();
        this.faultMimeTypes = new HashMap<String, String>();
        this.inParts = new HashMap<String, EditableWSDLPart>();
        this.outParts = new HashMap<String, EditableWSDLPart>();
        this.wsdlBoundFaults = new ArrayList<EditableWSDLBoundFault>();
        this.owner = owner;
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @Override
    public String getSOAPAction() {
        return this.soapAction;
    }
    
    @Override
    public void setSoapAction(final String soapAction) {
        this.soapAction = ((soapAction != null) ? soapAction : "");
    }
    
    @Override
    public EditableWSDLPart getPart(final String partName, final WebParam.Mode mode) {
        if (mode == WebParam.Mode.IN) {
            return this.inParts.get(partName);
        }
        if (mode == WebParam.Mode.OUT) {
            return this.outParts.get(partName);
        }
        return null;
    }
    
    @Override
    public void addPart(final EditableWSDLPart part, final WebParam.Mode mode) {
        if (mode == WebParam.Mode.IN) {
            this.inParts.put(part.getName(), part);
        }
        else if (mode == WebParam.Mode.OUT) {
            this.outParts.put(part.getName(), part);
        }
    }
    
    @Override
    public Map<String, ParameterBinding> getInputParts() {
        return this.inputParts;
    }
    
    @Override
    public Map<String, ParameterBinding> getOutputParts() {
        return this.outputParts;
    }
    
    @Override
    public Map<String, ParameterBinding> getFaultParts() {
        return this.faultParts;
    }
    
    @Override
    public Map<String, ? extends EditableWSDLPart> getInParts() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends EditableWSDLPart>)this.inParts);
    }
    
    @Override
    public Map<String, ? extends EditableWSDLPart> getOutParts() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends EditableWSDLPart>)this.outParts);
    }
    
    @NotNull
    @Override
    public List<? extends EditableWSDLBoundFault> getFaults() {
        return this.wsdlBoundFaults;
    }
    
    @Override
    public void addFault(@NotNull final EditableWSDLBoundFault fault) {
        this.wsdlBoundFaults.add(fault);
    }
    
    @Override
    public ParameterBinding getInputBinding(final String part) {
        if (this.emptyInputBody == null) {
            if (this.inputParts.get(" ") != null) {
                this.emptyInputBody = true;
            }
            else {
                this.emptyInputBody = false;
            }
        }
        final ParameterBinding block = this.inputParts.get(part);
        if (block != null) {
            return block;
        }
        if (this.explicitInputSOAPBodyParts || this.emptyInputBody) {
            return ParameterBinding.UNBOUND;
        }
        return ParameterBinding.BODY;
    }
    
    @Override
    public ParameterBinding getOutputBinding(final String part) {
        if (this.emptyOutputBody == null) {
            if (this.outputParts.get(" ") != null) {
                this.emptyOutputBody = true;
            }
            else {
                this.emptyOutputBody = false;
            }
        }
        final ParameterBinding block = this.outputParts.get(part);
        if (block != null) {
            return block;
        }
        if (this.explicitOutputSOAPBodyParts || this.emptyOutputBody) {
            return ParameterBinding.UNBOUND;
        }
        return ParameterBinding.BODY;
    }
    
    @Override
    public ParameterBinding getFaultBinding(final String part) {
        if (this.emptyFaultBody == null) {
            if (this.faultParts.get(" ") != null) {
                this.emptyFaultBody = true;
            }
            else {
                this.emptyFaultBody = false;
            }
        }
        final ParameterBinding block = this.faultParts.get(part);
        if (block != null) {
            return block;
        }
        if (this.explicitFaultSOAPBodyParts || this.emptyFaultBody) {
            return ParameterBinding.UNBOUND;
        }
        return ParameterBinding.BODY;
    }
    
    @Override
    public String getMimeTypeForInputPart(final String part) {
        return this.inputMimeTypes.get(part);
    }
    
    @Override
    public String getMimeTypeForOutputPart(final String part) {
        return this.outputMimeTypes.get(part);
    }
    
    @Override
    public String getMimeTypeForFaultPart(final String part) {
        return this.faultMimeTypes.get(part);
    }
    
    @Override
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }
    
    @Override
    public EditableWSDLBoundPortType getBoundPortType() {
        return this.owner;
    }
    
    @Override
    public void setInputExplicitBodyParts(final boolean b) {
        this.explicitInputSOAPBodyParts = b;
    }
    
    @Override
    public void setOutputExplicitBodyParts(final boolean b) {
        this.explicitOutputSOAPBodyParts = b;
    }
    
    @Override
    public void setFaultExplicitBodyParts(final boolean b) {
        this.explicitFaultSOAPBodyParts = b;
    }
    
    @Override
    public void setStyle(final SOAPBinding.Style style) {
        this.style = style;
    }
    
    @Nullable
    @Override
    public QName getRequestPayloadName() {
        if (this.emptyRequestPayload) {
            return null;
        }
        if (this.requestPayloadName != null) {
            return this.requestPayloadName;
        }
        if (this.style.equals(SOAPBinding.Style.RPC)) {
            final String ns = (this.getRequestNamespace() != null) ? this.getRequestNamespace() : this.name.getNamespaceURI();
            return this.requestPayloadName = new QName(ns, this.name.getLocalPart());
        }
        final QName inMsgName = this.operation.getInput().getMessage().getName();
        final EditableWSDLMessage message = (EditableWSDLMessage)this.messages.get(inMsgName);
        for (final EditableWSDLPart part : message.parts()) {
            final ParameterBinding binding = this.getInputBinding(part.getName());
            if (binding.isBody()) {
                return this.requestPayloadName = part.getDescriptor().name();
            }
        }
        this.emptyRequestPayload = true;
        return null;
    }
    
    @Nullable
    @Override
    public QName getResponsePayloadName() {
        if (this.emptyResponsePayload) {
            return null;
        }
        if (this.responsePayloadName != null) {
            return this.responsePayloadName;
        }
        if (this.style.equals(SOAPBinding.Style.RPC)) {
            final String ns = (this.getResponseNamespace() != null) ? this.getResponseNamespace() : this.name.getNamespaceURI();
            return this.responsePayloadName = new QName(ns, this.name.getLocalPart() + "Response");
        }
        final QName outMsgName = this.operation.getOutput().getMessage().getName();
        final EditableWSDLMessage message = (EditableWSDLMessage)this.messages.get(outMsgName);
        for (final EditableWSDLPart part : message.parts()) {
            final ParameterBinding binding = this.getOutputBinding(part.getName());
            if (binding.isBody()) {
                return this.responsePayloadName = part.getDescriptor().name();
            }
        }
        this.emptyResponsePayload = true;
        return null;
    }
    
    @Override
    public String getRequestNamespace() {
        return (this.reqNamespace != null) ? this.reqNamespace : this.name.getNamespaceURI();
    }
    
    @Override
    public void setRequestNamespace(final String ns) {
        this.reqNamespace = ns;
    }
    
    @Override
    public String getResponseNamespace() {
        return (this.respNamespace != null) ? this.respNamespace : this.name.getNamespaceURI();
    }
    
    @Override
    public void setResponseNamespace(final String ns) {
        this.respNamespace = ns;
    }
    
    EditableWSDLBoundPortType getOwner() {
        return this.owner;
    }
    
    @Override
    public void freeze(final EditableWSDLModel parent) {
        this.messages = parent.getMessages();
        this.operation = this.owner.getPortType().get(this.name.getLocalPart());
        for (final EditableWSDLBoundFault bf : this.wsdlBoundFaults) {
            bf.freeze(this);
        }
    }
    
    @Override
    public void setAnonymous(final WSDLBoundOperation.ANONYMOUS anonymous) {
        this.anonymous = anonymous;
    }
    
    @Override
    public WSDLBoundOperation.ANONYMOUS getAnonymous() {
        return this.anonymous;
    }
}
