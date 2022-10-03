package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

public class W3CAddressingWSDLParserExtension extends WSDLParserExtension
{
    protected static final String COLON_DELIMITER = ":";
    protected static final String SLASH_DELIMITER = "/";
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        return this.addressibleElement(reader, binding);
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        return this.addressibleElement(reader, port);
    }
    
    private boolean addressibleElement(final XMLStreamReader reader, final WSDLFeaturedObject binding) {
        final QName ua = reader.getName();
        if (ua.equals(AddressingVersion.W3C.wsdlExtensionTag)) {
            final String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
            binding.addFeature(new AddressingFeature(true, Boolean.parseBoolean(required)));
            XMLStreamReaderUtil.skipElement(reader);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        final EditableWSDLBoundOperation edit = operation;
        final QName anon = reader.getName();
        if (anon.equals(AddressingVersion.W3C.wsdlAnonymousTag)) {
            try {
                final String value = reader.getElementText();
                if (value == null || value.trim().equals("")) {
                    throw new WebServiceException("Null values not permitted in wsaw:Anonymous.");
                }
                if (value.equals("optional")) {
                    edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
                }
                else if (value.equals("required")) {
                    edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.required);
                }
                else {
                    if (!value.equals("prohibited")) {
                        throw new WebServiceException("wsaw:Anonymous value \"" + value + "\" not understood.");
                    }
                    edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.prohibited);
                }
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void portTypeOperationInputAttributes(final EditableWSDLInput input, final XMLStreamReader reader) {
        final String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            input.setAction(action);
            input.setDefaultAction(false);
        }
    }
    
    @Override
    public void portTypeOperationOutputAttributes(final EditableWSDLOutput output, final XMLStreamReader reader) {
        final String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            output.setAction(action);
            output.setDefaultAction(false);
        }
    }
    
    @Override
    public void portTypeOperationFaultAttributes(final EditableWSDLFault fault, final XMLStreamReader reader) {
        final String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            fault.setAction(action);
            fault.setDefaultAction(false);
        }
    }
    
    @Override
    public void finished(final WSDLParserExtensionContext context) {
        final EditableWSDLModel model = context.getWSDLModel();
        for (final EditableWSDLService service : model.getServices().values()) {
            for (final EditableWSDLPort port : service.getPorts()) {
                final EditableWSDLBoundPortType binding = port.getBinding();
                this.populateActions(binding);
                this.patchAnonymousDefault(binding);
            }
        }
    }
    
    protected String getNamespaceURI() {
        return AddressingVersion.W3C.wsdlNsUri;
    }
    
    protected QName getWsdlActionTag() {
        return AddressingVersion.W3C.wsdlActionTag;
    }
    
    private void populateActions(final EditableWSDLBoundPortType binding) {
        final EditableWSDLPortType porttype = binding.getPortType();
        for (final EditableWSDLOperation o : porttype.getOperations()) {
            final EditableWSDLBoundOperation wboi = binding.get(o.getName());
            if (wboi == null) {
                o.getInput().setAction(this.defaultInputAction(o));
            }
            else {
                final String soapAction = wboi.getSOAPAction();
                if (o.getInput().getAction() == null || o.getInput().getAction().equals("")) {
                    if (soapAction != null && !soapAction.equals("")) {
                        o.getInput().setAction(soapAction);
                    }
                    else {
                        o.getInput().setAction(this.defaultInputAction(o));
                    }
                }
                if (o.getOutput() == null) {
                    continue;
                }
                if (o.getOutput().getAction() == null || o.getOutput().getAction().equals("")) {
                    o.getOutput().setAction(this.defaultOutputAction(o));
                }
                if (o.getFaults() == null) {
                    continue;
                }
                if (!o.getFaults().iterator().hasNext()) {
                    continue;
                }
                for (final EditableWSDLFault f : o.getFaults()) {
                    if (f.getAction() == null || f.getAction().equals("")) {
                        f.setAction(this.defaultFaultAction(f.getName(), o));
                    }
                }
            }
        }
    }
    
    protected void patchAnonymousDefault(final EditableWSDLBoundPortType binding) {
        for (final EditableWSDLBoundOperation wbo : binding.getBindingOperations()) {
            if (wbo.getAnonymous() == null) {
                wbo.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
            }
        }
    }
    
    private String defaultInputAction(final EditableWSDLOperation o) {
        return buildAction(o.getInput().getName(), o, false);
    }
    
    private String defaultOutputAction(final EditableWSDLOperation o) {
        return buildAction(o.getOutput().getName(), o, false);
    }
    
    private String defaultFaultAction(final String name, final EditableWSDLOperation o) {
        return buildAction(name, o, true);
    }
    
    protected static final String buildAction(final String name, final EditableWSDLOperation o, final boolean isFault) {
        String tns = o.getName().getNamespaceURI();
        String delim = "/";
        if (!tns.startsWith("http")) {
            delim = ":";
        }
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        if (o.getPortTypeName() == null) {
            throw new WebServiceException("\"" + o.getName() + "\" operation's owning portType name is null.");
        }
        return tns + delim + o.getPortTypeName().getLocalPart() + delim + (isFault ? (o.getName().getLocalPart() + delim + "Fault" + delim) : "") + name;
    }
}
