package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

final class FoolProofParserExtension extends DelegatingParserExtension
{
    public FoolProofParserExtension(final WSDLParserExtension core) {
        super(core);
    }
    
    private QName pre(final XMLStreamReader xsr) {
        return xsr.getName();
    }
    
    private boolean post(final QName tagName, final XMLStreamReader xsr, final boolean result) {
        if (!tagName.equals(xsr.getName())) {
            return this.foundFool();
        }
        if (result) {
            if (xsr.getEventType() != 2) {
                this.foundFool();
            }
        }
        else if (xsr.getEventType() != 1) {
            this.foundFool();
        }
        return result;
    }
    
    private boolean foundFool() {
        throw new AssertionError((Object)("XMLStreamReader is placed at the wrong place after invoking " + this.core));
    }
    
    @Override
    public boolean serviceElements(final EditableWSDLService service, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.serviceElements(service, reader));
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portElements(port, reader));
    }
    
    @Override
    public boolean definitionsElements(final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.definitionsElements(reader));
    }
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingElements(binding, reader));
    }
    
    @Override
    public boolean portTypeElements(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeElements(portType, reader));
    }
    
    @Override
    public boolean portTypeOperationElements(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationElements(operation, reader));
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationElements(operation, reader));
    }
    
    @Override
    public boolean messageElements(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.messageElements(msg, reader));
    }
    
    @Override
    public boolean portTypeOperationInputElements(final EditableWSDLInput input, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationInputElements(input, reader));
    }
    
    @Override
    public boolean portTypeOperationOutputElements(final EditableWSDLOutput output, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationOutputElements(output, reader));
    }
    
    @Override
    public boolean portTypeOperationFaultElements(final EditableWSDLFault fault, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationFaultElements(fault, reader));
    }
    
    @Override
    public boolean bindingOperationInputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return super.bindingOperationInputElements(operation, reader);
    }
    
    @Override
    public boolean bindingOperationOutputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationOutputElements(operation, reader));
    }
    
    @Override
    public boolean bindingOperationFaultElements(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationFaultElements(fault, reader));
    }
}
