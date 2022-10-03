package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;

public final class WSDLInputImpl extends AbstractExtensibleImpl implements EditableWSDLInput
{
    private String name;
    private QName messageName;
    private EditableWSDLOperation operation;
    private EditableWSDLMessage message;
    private String action;
    private boolean defaultAction;
    
    public WSDLInputImpl(final XMLStreamReader xsr, final String name, final QName messageName, final EditableWSDLOperation operation) {
        super(xsr);
        this.defaultAction = true;
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }
    
    @Override
    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.operation.isOneWay() ? this.operation.getName().getLocalPart() : (this.operation.getName().getLocalPart() + "Request");
    }
    
    @Override
    public EditableWSDLMessage getMessage() {
        return this.message;
    }
    
    @Override
    public String getAction() {
        return this.action;
    }
    
    @NotNull
    @Override
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }
    
    @Override
    public QName getQName() {
        return new QName(this.operation.getName().getNamespaceURI(), this.getName());
    }
    
    @Override
    public void setAction(final String action) {
        this.action = action;
    }
    
    @Override
    public boolean isDefaultAction() {
        return this.defaultAction;
    }
    
    @Override
    public void setDefaultAction(final boolean defaultAction) {
        this.defaultAction = defaultAction;
    }
    
    @Override
    public void freeze(final EditableWSDLModel parent) {
        this.message = parent.getMessage(this.messageName);
    }
}
