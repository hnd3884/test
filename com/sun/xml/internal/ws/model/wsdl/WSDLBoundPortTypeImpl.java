package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceFeature;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam;
import javax.xml.stream.XMLStreamReader;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;

public final class WSDLBoundPortTypeImpl extends AbstractFeaturedObjectImpl implements EditableWSDLBoundPortType
{
    private final QName name;
    private final QName portTypeName;
    private EditableWSDLPortType portType;
    private BindingID bindingId;
    @NotNull
    private final EditableWSDLModel owner;
    private final QNameMap<EditableWSDLBoundOperation> bindingOperations;
    private QNameMap<EditableWSDLBoundOperation> payloadMap;
    private EditableWSDLBoundOperation emptyPayloadOperation;
    private SOAPBinding.Style style;
    
    public WSDLBoundPortTypeImpl(final XMLStreamReader xsr, @NotNull final EditableWSDLModel owner, final QName name, final QName portTypeName) {
        super(xsr);
        this.bindingOperations = new QNameMap<EditableWSDLBoundOperation>();
        this.style = SOAPBinding.Style.DOCUMENT;
        this.owner = owner;
        this.name = name;
        this.portTypeName = portTypeName;
        owner.addBinding(this);
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @NotNull
    @Override
    public EditableWSDLModel getOwner() {
        return this.owner;
    }
    
    @Override
    public EditableWSDLBoundOperation get(final QName operationName) {
        return this.bindingOperations.get(operationName);
    }
    
    @Override
    public void put(final QName opName, final EditableWSDLBoundOperation ptOp) {
        this.bindingOperations.put(opName, ptOp);
    }
    
    @Override
    public QName getPortTypeName() {
        return this.portTypeName;
    }
    
    @Override
    public EditableWSDLPortType getPortType() {
        return this.portType;
    }
    
    @Override
    public Iterable<EditableWSDLBoundOperation> getBindingOperations() {
        return this.bindingOperations.values();
    }
    
    @Override
    public BindingID getBindingId() {
        return (this.bindingId == null) ? BindingID.SOAP11_HTTP : this.bindingId;
    }
    
    @Override
    public void setBindingId(final BindingID bindingId) {
        this.bindingId = bindingId;
    }
    
    @Override
    public void setStyle(final SOAPBinding.Style style) {
        this.style = style;
    }
    
    @Override
    public SOAPBinding.Style getStyle() {
        return this.style;
    }
    
    public boolean isRpcLit() {
        return SOAPBinding.Style.RPC == this.style;
    }
    
    public boolean isDoclit() {
        return SOAPBinding.Style.DOCUMENT == this.style;
    }
    
    @Override
    public ParameterBinding getBinding(final QName operation, final String part, final WebParam.Mode mode) {
        final EditableWSDLBoundOperation op = this.get(operation);
        if (op == null) {
            return null;
        }
        if (WebParam.Mode.IN == mode || WebParam.Mode.INOUT == mode) {
            return op.getInputBinding(part);
        }
        return op.getOutputBinding(part);
    }
    
    @Override
    public EditableWSDLBoundOperation getOperation(final String namespaceUri, final String localName) {
        if (namespaceUri == null && localName == null) {
            return this.emptyPayloadOperation;
        }
        return this.payloadMap.get((namespaceUri == null) ? "" : namespaceUri, localName);
    }
    
    @Override
    public void freeze() {
        this.portType = this.owner.getPortType(this.portTypeName);
        if (this.portType == null) {
            throw new LocatableWebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(this.portTypeName), new Locator[] { this.getLocation() });
        }
        this.portType.freeze();
        for (final EditableWSDLBoundOperation op : this.bindingOperations.values()) {
            op.freeze(this.owner);
        }
        this.freezePayloadMap();
        this.owner.finalizeRpcLitBinding(this);
    }
    
    private void freezePayloadMap() {
        if (this.style == SOAPBinding.Style.RPC) {
            this.payloadMap = new QNameMap<EditableWSDLBoundOperation>();
            for (final EditableWSDLBoundOperation op : this.bindingOperations.values()) {
                this.payloadMap.put(op.getRequestPayloadName(), op);
            }
        }
        else {
            this.payloadMap = new QNameMap<EditableWSDLBoundOperation>();
            for (final EditableWSDLBoundOperation op : this.bindingOperations.values()) {
                final QName name = op.getRequestPayloadName();
                if (name == null) {
                    this.emptyPayloadOperation = op;
                }
                else {
                    this.payloadMap.put(name, op);
                }
            }
        }
    }
}
