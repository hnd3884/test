package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;

public final class WSDLFaultImpl extends AbstractExtensibleImpl implements EditableWSDLFault
{
    private final String name;
    private final QName messageName;
    private EditableWSDLMessage message;
    private EditableWSDLOperation operation;
    private String action;
    private boolean defaultAction;
    
    public WSDLFaultImpl(final XMLStreamReader xsr, final String name, final QName messageName, final EditableWSDLOperation operation) {
        super(xsr);
        this.action = "";
        this.defaultAction = true;
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public EditableWSDLMessage getMessage() {
        return this.message;
    }
    
    @NotNull
    @Override
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }
    
    @NotNull
    @Override
    public QName getQName() {
        return new QName(this.operation.getName().getNamespaceURI(), this.name);
    }
    
    @NotNull
    @Override
    public String getAction() {
        return this.action;
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
    public void freeze(final EditableWSDLModel root) {
        this.message = root.getMessage(this.messageName);
    }
}
