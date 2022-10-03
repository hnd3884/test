package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import java.util.Iterator;
import java.util.Hashtable;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;

public final class WSDLPortTypeImpl extends AbstractExtensibleImpl implements EditableWSDLPortType
{
    private QName name;
    private final Map<String, EditableWSDLOperation> portTypeOperations;
    private EditableWSDLModel owner;
    
    public WSDLPortTypeImpl(final XMLStreamReader xsr, final EditableWSDLModel owner, final QName name) {
        super(xsr);
        this.name = name;
        this.owner = owner;
        this.portTypeOperations = new Hashtable<String, EditableWSDLOperation>();
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @Override
    public EditableWSDLOperation get(final String operationName) {
        return this.portTypeOperations.get(operationName);
    }
    
    @Override
    public Iterable<EditableWSDLOperation> getOperations() {
        return this.portTypeOperations.values();
    }
    
    @Override
    public void put(final String opName, final EditableWSDLOperation ptOp) {
        this.portTypeOperations.put(opName, ptOp);
    }
    
    EditableWSDLModel getOwner() {
        return this.owner;
    }
    
    @Override
    public void freeze() {
        for (final EditableWSDLOperation op : this.portTypeOperations.values()) {
            op.freeze(this.owner);
        }
    }
}
