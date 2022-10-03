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
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;

public final class WSDLOutputImpl extends AbstractExtensibleImpl implements EditableWSDLOutput
{
    private String name;
    private QName messageName;
    private EditableWSDLOperation operation;
    private EditableWSDLMessage message;
    private String action;
    private boolean defaultAction;
    
    public WSDLOutputImpl(final XMLStreamReader xsr, final String name, final QName messageName, final EditableWSDLOperation operation) {
        super(xsr);
        this.defaultAction = true;
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }
    
    @Override
    public String getName() {
        return (this.name == null) ? (this.operation.getName().getLocalPart() + "Response") : this.name;
    }
    
    @Override
    public EditableWSDLMessage getMessage() {
        return this.message;
    }
    
    @Override
    public String getAction() {
        return this.action;
    }
    
    @Override
    public boolean isDefaultAction() {
        return this.defaultAction;
    }
    
    @Override
    public void setDefaultAction(final boolean defaultAction) {
        this.defaultAction = defaultAction;
    }
    
    @NotNull
    @Override
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }
    
    @NotNull
    @Override
    public QName getQName() {
        return new QName(this.operation.getName().getNamespaceURI(), this.getName());
    }
    
    @Override
    public void setAction(final String action) {
        this.action = action;
    }
    
    @Override
    public void freeze(final EditableWSDLModel root) {
        this.message = root.getMessage(this.messageName);
    }
}
