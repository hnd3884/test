package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;

public class MemberSubmissionAddressingWSDLParserExtension extends W3CAddressingWSDLParserExtension
{
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
        if (ua.equals(AddressingVersion.MEMBER.wsdlExtensionTag)) {
            final String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
            binding.addFeature(new MemberSubmissionAddressingFeature(Boolean.parseBoolean(required)));
            XMLStreamReaderUtil.skipElement(reader);
            return true;
        }
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
        return AddressingVersion.MEMBER.wsdlNsUri;
    }
    
    @Override
    protected QName getWsdlActionTag() {
        return AddressingVersion.MEMBER.wsdlActionTag;
    }
}
