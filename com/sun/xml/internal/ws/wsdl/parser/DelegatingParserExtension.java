package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

class DelegatingParserExtension extends WSDLParserExtension
{
    protected final WSDLParserExtension core;
    
    public DelegatingParserExtension(final WSDLParserExtension core) {
        this.core = core;
    }
    
    @Override
    public void start(final WSDLParserExtensionContext context) {
        this.core.start(context);
    }
    
    @Override
    public void serviceAttributes(final EditableWSDLService service, final XMLStreamReader reader) {
        this.core.serviceAttributes(service, reader);
    }
    
    @Override
    public boolean serviceElements(final EditableWSDLService service, final XMLStreamReader reader) {
        return this.core.serviceElements(service, reader);
    }
    
    @Override
    public void portAttributes(final EditableWSDLPort port, final XMLStreamReader reader) {
        this.core.portAttributes(port, reader);
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        return this.core.portElements(port, reader);
    }
    
    @Override
    public boolean portTypeOperationInput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return this.core.portTypeOperationInput(op, reader);
    }
    
    @Override
    public boolean portTypeOperationOutput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return this.core.portTypeOperationOutput(op, reader);
    }
    
    @Override
    public boolean portTypeOperationFault(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return this.core.portTypeOperationFault(op, reader);
    }
    
    @Override
    public boolean definitionsElements(final XMLStreamReader reader) {
        return this.core.definitionsElements(reader);
    }
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        return this.core.bindingElements(binding, reader);
    }
    
    @Override
    public void bindingAttributes(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        this.core.bindingAttributes(binding, reader);
    }
    
    @Override
    public boolean portTypeElements(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        return this.core.portTypeElements(portType, reader);
    }
    
    @Override
    public void portTypeAttributes(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        this.core.portTypeAttributes(portType, reader);
    }
    
    @Override
    public boolean portTypeOperationElements(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        return this.core.portTypeOperationElements(operation, reader);
    }
    
    @Override
    public void portTypeOperationAttributes(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        this.core.portTypeOperationAttributes(operation, reader);
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return this.core.bindingOperationElements(operation, reader);
    }
    
    @Override
    public void bindingOperationAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        this.core.bindingOperationAttributes(operation, reader);
    }
    
    @Override
    public boolean messageElements(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        return this.core.messageElements(msg, reader);
    }
    
    @Override
    public void messageAttributes(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        this.core.messageAttributes(msg, reader);
    }
    
    @Override
    public boolean portTypeOperationInputElements(final EditableWSDLInput input, final XMLStreamReader reader) {
        return this.core.portTypeOperationInputElements(input, reader);
    }
    
    @Override
    public void portTypeOperationInputAttributes(final EditableWSDLInput input, final XMLStreamReader reader) {
        this.core.portTypeOperationInputAttributes(input, reader);
    }
    
    @Override
    public boolean portTypeOperationOutputElements(final EditableWSDLOutput output, final XMLStreamReader reader) {
        return this.core.portTypeOperationOutputElements(output, reader);
    }
    
    @Override
    public void portTypeOperationOutputAttributes(final EditableWSDLOutput output, final XMLStreamReader reader) {
        this.core.portTypeOperationOutputAttributes(output, reader);
    }
    
    @Override
    public boolean portTypeOperationFaultElements(final EditableWSDLFault fault, final XMLStreamReader reader) {
        return this.core.portTypeOperationFaultElements(fault, reader);
    }
    
    @Override
    public void portTypeOperationFaultAttributes(final EditableWSDLFault fault, final XMLStreamReader reader) {
        this.core.portTypeOperationFaultAttributes(fault, reader);
    }
    
    @Override
    public boolean bindingOperationInputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return this.core.bindingOperationInputElements(operation, reader);
    }
    
    @Override
    public void bindingOperationInputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        this.core.bindingOperationInputAttributes(operation, reader);
    }
    
    @Override
    public boolean bindingOperationOutputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return this.core.bindingOperationOutputElements(operation, reader);
    }
    
    @Override
    public void bindingOperationOutputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        this.core.bindingOperationOutputAttributes(operation, reader);
    }
    
    @Override
    public boolean bindingOperationFaultElements(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        return this.core.bindingOperationFaultElements(fault, reader);
    }
    
    @Override
    public void bindingOperationFaultAttributes(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        this.core.bindingOperationFaultAttributes(fault, reader);
    }
    
    @Override
    public void finished(final WSDLParserExtensionContext context) {
        this.core.finished(context);
    }
    
    @Override
    public void postFinished(final WSDLParserExtensionContext context) {
        this.core.postFinished(context);
    }
}
