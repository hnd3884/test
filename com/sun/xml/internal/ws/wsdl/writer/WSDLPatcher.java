package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;

public final class WSDLPatcher extends XMLStreamReaderToXMLStreamWriter
{
    private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    private static final QName SCHEMA_INCLUDE_QNAME;
    private static final QName SCHEMA_IMPORT_QNAME;
    private static final QName SCHEMA_REDEFINE_QNAME;
    private static final Logger logger;
    private final DocumentLocationResolver docResolver;
    private final PortAddressResolver portAddressResolver;
    private String targetNamespace;
    private QName serviceName;
    private QName portName;
    private String portAddress;
    private boolean inEpr;
    private boolean inEprAddress;
    
    public WSDLPatcher(@NotNull final PortAddressResolver portAddressResolver, @NotNull final DocumentLocationResolver docResolver) {
        this.portAddressResolver = portAddressResolver;
        this.docResolver = docResolver;
    }
    
    @Override
    protected void handleAttribute(final int i) throws XMLStreamException {
        final QName name = this.in.getName();
        final String attLocalName = this.in.getAttributeLocalName(i);
        if ((!name.equals(WSDLPatcher.SCHEMA_INCLUDE_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(WSDLPatcher.SCHEMA_IMPORT_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(WSDLPatcher.SCHEMA_REDEFINE_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(WSDLConstants.QNAME_IMPORT) || !attLocalName.equals("location"))) {
            if ((name.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS) || name.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS)) && attLocalName.equals("location")) {
                this.portAddress = this.in.getAttributeValue(i);
                final String value = this.getAddressLocation();
                if (value != null) {
                    WSDLPatcher.logger.fine("Service:" + this.serviceName + " port:" + this.portName + " current address " + this.portAddress + " Patching it with " + value);
                    this.writeAttribute(i, value);
                    return;
                }
            }
            super.handleAttribute(i);
            return;
        }
        final String relPath = this.in.getAttributeValue(i);
        final String actualPath = this.getPatchedImportLocation(relPath);
        if (actualPath == null) {
            return;
        }
        WSDLPatcher.logger.fine("Fixing the relative location:" + relPath + " with absolute location:" + actualPath);
        this.writeAttribute(i, actualPath);
    }
    
    private void writeAttribute(final int i, final String value) throws XMLStreamException {
        final String nsUri = this.in.getAttributeNamespace(i);
        if (nsUri != null) {
            this.out.writeAttribute(this.in.getAttributePrefix(i), nsUri, this.in.getAttributeLocalName(i), value);
        }
        else {
            this.out.writeAttribute(this.in.getAttributeLocalName(i), value);
        }
    }
    
    @Override
    protected void handleStartElement() throws XMLStreamException {
        final QName name = this.in.getName();
        if (name.equals(WSDLConstants.QNAME_DEFINITIONS)) {
            final String value = this.in.getAttributeValue(null, "targetNamespace");
            if (value != null) {
                this.targetNamespace = value;
            }
        }
        else if (name.equals(WSDLConstants.QNAME_SERVICE)) {
            final String value = this.in.getAttributeValue(null, "name");
            if (value != null) {
                this.serviceName = new QName(this.targetNamespace, value);
            }
        }
        else if (name.equals(WSDLConstants.QNAME_PORT)) {
            final String value = this.in.getAttributeValue(null, "name");
            if (value != null) {
                this.portName = new QName(this.targetNamespace, value);
            }
        }
        else if (name.equals(W3CAddressingConstants.WSA_EPR_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)) {
            if (this.serviceName != null && this.portName != null) {
                this.inEpr = true;
            }
        }
        else if ((name.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME)) && this.inEpr) {
            this.inEprAddress = true;
        }
        super.handleStartElement();
    }
    
    @Override
    protected void handleEndElement() throws XMLStreamException {
        final QName name = this.in.getName();
        if (name.equals(WSDLConstants.QNAME_SERVICE)) {
            this.serviceName = null;
        }
        else if (name.equals(WSDLConstants.QNAME_PORT)) {
            this.portName = null;
        }
        else if (name.equals(W3CAddressingConstants.WSA_EPR_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)) {
            if (this.inEpr) {
                this.inEpr = false;
            }
        }
        else if ((name.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME)) && this.inEprAddress) {
            final String value = this.getAddressLocation();
            if (value != null) {
                WSDLPatcher.logger.fine("Fixing EPR Address for service:" + this.serviceName + " port:" + this.portName + " address with " + value);
                this.out.writeCharacters(value);
            }
            this.inEprAddress = false;
        }
        super.handleEndElement();
    }
    
    @Override
    protected void handleCharacters() throws XMLStreamException {
        if (this.inEprAddress) {
            final String value = this.getAddressLocation();
            if (value != null) {
                return;
            }
        }
        super.handleCharacters();
    }
    
    @Nullable
    private String getPatchedImportLocation(final String relPath) {
        return this.docResolver.getLocationFor(null, relPath);
    }
    
    private String getAddressLocation() {
        return (this.portAddressResolver == null || this.portName == null) ? null : this.portAddressResolver.getAddressFor(this.serviceName, this.portName.getLocalPart(), this.portAddress);
    }
    
    static {
        SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
        SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
        SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
        logger = Logger.getLogger("com.sun.xml.internal.ws.wsdl.patcher");
    }
}
