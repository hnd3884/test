package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;

public class WSDLBoundFaultImpl extends AbstractExtensibleImpl implements EditableWSDLBoundFault
{
    private final String name;
    private EditableWSDLFault fault;
    private EditableWSDLBoundOperation owner;
    
    public WSDLBoundFaultImpl(final XMLStreamReader xsr, final String name, final EditableWSDLBoundOperation owner) {
        super(xsr);
        this.name = name;
        this.owner = owner;
    }
    
    @NotNull
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public QName getQName() {
        if (this.owner.getOperation() != null) {
            return new QName(this.owner.getOperation().getName().getNamespaceURI(), this.name);
        }
        return null;
    }
    
    @Override
    public EditableWSDLFault getFault() {
        return this.fault;
    }
    
    @NotNull
    @Override
    public EditableWSDLBoundOperation getBoundOperation() {
        return this.owner;
    }
    
    @Override
    public void freeze(final EditableWSDLBoundOperation root) {
        assert root != null;
        final EditableWSDLOperation op = root.getOperation();
        if (op != null) {
            for (final EditableWSDLFault f : op.getFaults()) {
                if (f.getName().equals(this.name)) {
                    this.fault = f;
                    break;
                }
            }
        }
    }
}
