package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.istack.internal.NotNull;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import java.util.List;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;

public final class WSDLOperationImpl extends AbstractExtensibleImpl implements EditableWSDLOperation
{
    private final QName name;
    private String parameterOrder;
    private EditableWSDLInput input;
    private EditableWSDLOutput output;
    private final List<EditableWSDLFault> faults;
    private final QNameMap<EditableWSDLFault> faultMap;
    protected Iterable<EditableWSDLMessage> messages;
    private final EditableWSDLPortType owner;
    
    public WSDLOperationImpl(final XMLStreamReader xsr, final EditableWSDLPortType owner, final QName name) {
        super(xsr);
        this.name = name;
        this.faults = new ArrayList<EditableWSDLFault>();
        this.faultMap = new QNameMap<EditableWSDLFault>();
        this.owner = owner;
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @Override
    public String getParameterOrder() {
        return this.parameterOrder;
    }
    
    @Override
    public void setParameterOrder(final String parameterOrder) {
        this.parameterOrder = parameterOrder;
    }
    
    @Override
    public EditableWSDLInput getInput() {
        return this.input;
    }
    
    @Override
    public void setInput(final EditableWSDLInput input) {
        this.input = input;
    }
    
    @Override
    public EditableWSDLOutput getOutput() {
        return this.output;
    }
    
    @Override
    public boolean isOneWay() {
        return this.output == null;
    }
    
    @Override
    public void setOutput(final EditableWSDLOutput output) {
        this.output = output;
    }
    
    @Override
    public Iterable<EditableWSDLFault> getFaults() {
        return this.faults;
    }
    
    @Override
    public EditableWSDLFault getFault(final QName faultDetailName) {
        final EditableWSDLFault fault = this.faultMap.get(faultDetailName);
        if (fault != null) {
            return fault;
        }
        for (final EditableWSDLFault fi : this.faults) {
            assert fi.getMessage().parts().iterator().hasNext();
            final EditableWSDLPart part = (EditableWSDLPart)fi.getMessage().parts().iterator().next();
            if (part.getDescriptor().name().equals(faultDetailName)) {
                this.faultMap.put(faultDetailName, fi);
                return fi;
            }
        }
        return null;
    }
    
    @NotNull
    @Override
    public QName getPortTypeName() {
        return this.owner.getName();
    }
    
    @Override
    public void addFault(final EditableWSDLFault fault) {
        this.faults.add(fault);
    }
    
    @Override
    public void freeze(final EditableWSDLModel root) {
        assert this.input != null;
        this.input.freeze(root);
        if (this.output != null) {
            this.output.freeze(root);
        }
        for (final EditableWSDLFault fault : this.faults) {
            fault.freeze(root);
        }
    }
}
