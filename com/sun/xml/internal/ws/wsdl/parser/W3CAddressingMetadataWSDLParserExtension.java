package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import javax.xml.namespace.QName;

public class W3CAddressingMetadataWSDLParserExtension extends W3CAddressingWSDLParserExtension
{
    String METADATA_WSDL_EXTN_NS;
    QName METADATA_WSDL_ACTION_TAG;
    
    public W3CAddressingMetadataWSDLParserExtension() {
        this.METADATA_WSDL_EXTN_NS = "http://www.w3.org/2007/05/addressing/metadata";
        this.METADATA_WSDL_ACTION_TAG = new QName(this.METADATA_WSDL_EXTN_NS, "Action", "wsam");
    }
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        return false;
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        return false;
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        return false;
    }
    
    @Override
    protected void patchAnonymousDefault(final EditableWSDLBoundPortType binding) {
    }
    
    @Override
    protected String getNamespaceURI() {
        return this.METADATA_WSDL_EXTN_NS;
    }
    
    @Override
    protected QName getWsdlActionTag() {
        return this.METADATA_WSDL_ACTION_TAG;
    }
}
