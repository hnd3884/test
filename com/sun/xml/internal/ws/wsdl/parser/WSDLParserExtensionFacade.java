package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.stream.Location;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.Locator;
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
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

final class WSDLParserExtensionFacade extends WSDLParserExtension
{
    private final WSDLParserExtension[] extensions;
    
    WSDLParserExtensionFacade(final WSDLParserExtension... extensions) {
        assert extensions != null;
        this.extensions = extensions;
    }
    
    @Override
    public void start(final WSDLParserExtensionContext context) {
        for (final WSDLParserExtension e : this.extensions) {
            e.start(context);
        }
    }
    
    @Override
    public boolean serviceElements(final EditableWSDLService service, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.serviceElements(service, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void serviceAttributes(final EditableWSDLService service, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.serviceAttributes(service, reader);
        }
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portElements(port, reader)) {
                return true;
            }
        }
        if (this.isRequiredExtension(reader)) {
            port.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public boolean portTypeOperationInput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationInput(op, reader);
        }
        return false;
    }
    
    @Override
    public boolean portTypeOperationOutput(final EditableWSDLOperation op, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationOutput(op, reader);
        }
        return false;
    }
    
    @Override
    public boolean portTypeOperationFault(final EditableWSDLOperation op, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationFault(op, reader);
        }
        return false;
    }
    
    @Override
    public void portAttributes(final EditableWSDLPort port, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portAttributes(port, reader);
        }
    }
    
    @Override
    public boolean definitionsElements(final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.definitionsElements(reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.bindingElements(binding, reader)) {
                return true;
            }
        }
        if (this.isRequiredExtension(reader)) {
            binding.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void bindingAttributes(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.bindingAttributes(binding, reader);
        }
    }
    
    @Override
    public boolean portTypeElements(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portTypeElements(portType, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void portTypeAttributes(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeAttributes(portType, reader);
        }
    }
    
    @Override
    public boolean portTypeOperationElements(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portTypeOperationElements(operation, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void portTypeOperationAttributes(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationAttributes(operation, reader);
        }
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.bindingOperationElements(operation, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void bindingOperationAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.bindingOperationAttributes(operation, reader);
        }
    }
    
    @Override
    public boolean messageElements(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.messageElements(msg, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void messageAttributes(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.messageAttributes(msg, reader);
        }
    }
    
    @Override
    public boolean portTypeOperationInputElements(final EditableWSDLInput input, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portTypeOperationInputElements(input, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void portTypeOperationInputAttributes(final EditableWSDLInput input, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationInputAttributes(input, reader);
        }
    }
    
    @Override
    public boolean portTypeOperationOutputElements(final EditableWSDLOutput output, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portTypeOperationOutputElements(output, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void portTypeOperationOutputAttributes(final EditableWSDLOutput output, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationOutputAttributes(output, reader);
        }
    }
    
    @Override
    public boolean portTypeOperationFaultElements(final EditableWSDLFault fault, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.portTypeOperationFaultElements(fault, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void portTypeOperationFaultAttributes(final EditableWSDLFault fault, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.portTypeOperationFaultAttributes(fault, reader);
        }
    }
    
    @Override
    public boolean bindingOperationInputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.bindingOperationInputElements(operation, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void bindingOperationInputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.bindingOperationInputAttributes(operation, reader);
        }
    }
    
    @Override
    public boolean bindingOperationOutputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.bindingOperationOutputElements(operation, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void bindingOperationOutputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.bindingOperationOutputAttributes(operation, reader);
        }
    }
    
    @Override
    public boolean bindingOperationFaultElements(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            if (e.bindingOperationFaultElements(fault, reader)) {
                return true;
            }
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }
    
    @Override
    public void bindingOperationFaultAttributes(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        for (final WSDLParserExtension e : this.extensions) {
            e.bindingOperationFaultAttributes(fault, reader);
        }
    }
    
    @Override
    public void finished(final WSDLParserExtensionContext context) {
        for (final WSDLParserExtension e : this.extensions) {
            e.finished(context);
        }
    }
    
    @Override
    public void postFinished(final WSDLParserExtensionContext context) {
        for (final WSDLParserExtension e : this.extensions) {
            e.postFinished(context);
        }
    }
    
    private boolean isRequiredExtension(final XMLStreamReader reader) {
        final String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
        return required != null && Boolean.parseBoolean(required);
    }
    
    private Locator getLocator(final XMLStreamReader reader) {
        final Location location = reader.getLocation();
        final LocatorImpl loc = new LocatorImpl();
        loc.setSystemId(location.getSystemId());
        loc.setLineNumber(location.getLineNumber());
        return loc;
    }
}
