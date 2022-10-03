package com.sun.xml.internal.ws.api.wsdl.parser;

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

public abstract class WSDLParserExtension
{
    public void start(final WSDLParserExtensionContext context) {
    }
    
    public void serviceAttributes(final EditableWSDLService service, final XMLStreamReader reader) {
    }
    
    public boolean serviceElements(final EditableWSDLService service, final XMLStreamReader reader) {
        return false;
    }
    
    public void portAttributes(final EditableWSDLPort port, final XMLStreamReader reader) {
    }
    
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        return false;
    }
    
    public boolean portTypeOperationInput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return false;
    }
    
    public boolean portTypeOperationOutput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return false;
    }
    
    public boolean portTypeOperationFault(final EditableWSDLOperation op, final XMLStreamReader reader) {
        return false;
    }
    
    public boolean definitionsElements(final XMLStreamReader reader) {
        return false;
    }
    
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        return false;
    }
    
    public void bindingAttributes(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
    }
    
    public boolean portTypeElements(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        return false;
    }
    
    public void portTypeAttributes(final EditableWSDLPortType portType, final XMLStreamReader reader) {
    }
    
    public boolean portTypeOperationElements(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        return false;
    }
    
    public void portTypeOperationAttributes(final EditableWSDLOperation operation, final XMLStreamReader reader) {
    }
    
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return false;
    }
    
    public void bindingOperationAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
    }
    
    public boolean messageElements(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        return false;
    }
    
    public void messageAttributes(final EditableWSDLMessage msg, final XMLStreamReader reader) {
    }
    
    public boolean portTypeOperationInputElements(final EditableWSDLInput input, final XMLStreamReader reader) {
        return false;
    }
    
    public void portTypeOperationInputAttributes(final EditableWSDLInput input, final XMLStreamReader reader) {
    }
    
    public boolean portTypeOperationOutputElements(final EditableWSDLOutput output, final XMLStreamReader reader) {
        return false;
    }
    
    public void portTypeOperationOutputAttributes(final EditableWSDLOutput output, final XMLStreamReader reader) {
    }
    
    public boolean portTypeOperationFaultElements(final EditableWSDLFault fault, final XMLStreamReader reader) {
        return false;
    }
    
    public void portTypeOperationFaultAttributes(final EditableWSDLFault fault, final XMLStreamReader reader) {
    }
    
    public boolean bindingOperationInputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return false;
    }
    
    public void bindingOperationInputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
    }
    
    public boolean bindingOperationOutputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return false;
    }
    
    public void bindingOperationOutputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
    }
    
    public boolean bindingOperationFaultElements(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        return false;
    }
    
    public void bindingOperationFaultAttributes(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
    }
    
    public void finished(final WSDLParserExtensionContext context) {
    }
    
    public void postFinished(final WSDLParserExtensionContext context) {
    }
}
