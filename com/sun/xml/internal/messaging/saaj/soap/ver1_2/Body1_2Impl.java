package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import org.w3c.dom.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPConstants;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;

public class Body1_2Impl extends BodyImpl
{
    protected static final Logger log;
    
    public Body1_2Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createBody1_2Name(prefix));
    }
    
    @Override
    protected NameImpl getFaultName(final String name) {
        return NameImpl.createFault1_2Name(name, null);
    }
    
    @Override
    protected SOAPBodyElement createBodyElement(final Name name) {
        return new BodyElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected SOAPBodyElement createBodyElement(final QName name) {
        return new BodyElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected QName getDefaultFaultCode() {
        return SOAPConstants.SOAP_RECEIVER_FAULT;
    }
    
    @Override
    public SOAPFault addFault() throws SOAPException {
        if (this.hasAnyChildElement()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addFault();
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        Body1_2Impl.log.severe("SAAJ0401.ver1_2.no.encodingstyle.in.body");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Body");
    }
    
    @Override
    public SOAPElement addAttribute(final Name name, final String value) throws SOAPException {
        if (name.getLocalName().equals("encodingStyle") && name.getURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
    
    @Override
    public SOAPElement addAttribute(final QName name, final String value) throws SOAPException {
        if (name.getLocalPart().equals("encodingStyle") && name.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
    
    @Override
    protected boolean isFault(final SOAPElement child) {
        return child.getElementName().getURI().equals("http://www.w3.org/2003/05/soap-envelope") && child.getElementName().getLocalName().equals("Fault");
    }
    
    @Override
    protected SOAPFault createFaultElement() {
        return new Fault1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), this.getPrefix());
    }
    
    @Override
    public SOAPBodyElement addBodyElement(final Name name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addBodyElement(name);
    }
    
    @Override
    public SOAPBodyElement addBodyElement(final QName name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addBodyElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addElement(name);
    }
    
    @Override
    public SOAPElement addChildElement(final Name name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addChildElement(name);
    }
    
    @Override
    public SOAPElement addChildElement(final QName name) throws SOAPException {
        if (this.hasFault()) {
            Body1_2Impl.log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addChildElement(name);
    }
    
    private boolean hasAnyChildElement() {
        for (Node currentNode = this.getFirstChild(); currentNode != null; currentNode = currentNode.getNextSibling()) {
            if (currentNode.getNodeType() == 1) {
                return true;
            }
        }
        return false;
    }
    
    static {
        log = Logger.getLogger(Body1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
