package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import org.w3c.dom.Node;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Locale;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;

public class Fault1_1Impl extends FaultImpl
{
    protected static final Logger log;
    
    public Fault1_1Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createFault1_1Name(prefix));
    }
    
    @Override
    protected NameImpl getDetailName() {
        return NameImpl.createDetail1_1Name();
    }
    
    @Override
    protected NameImpl getFaultCodeName() {
        return NameImpl.createFromUnqualifiedName("faultcode");
    }
    
    @Override
    protected NameImpl getFaultStringName() {
        return NameImpl.createFromUnqualifiedName("faultstring");
    }
    
    @Override
    protected NameImpl getFaultActorName() {
        return NameImpl.createFromUnqualifiedName("faultactor");
    }
    
    @Override
    protected DetailImpl createDetail() {
        return new Detail1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument());
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final String localName) {
        return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), localName);
    }
    
    @Override
    protected void checkIfStandardFaultCode(final String faultCode, final String uri) throws SOAPException {
    }
    
    @Override
    protected void finallySetFaultCode(final String faultcode) throws SOAPException {
        this.faultCodeElement.addTextNode(faultcode);
    }
    
    @Override
    public String getFaultCode() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        return this.faultCodeElement.getValue();
    }
    
    @Override
    public Name getFaultCodeAsName() {
        final String faultcodeString = this.getFaultCode();
        if (faultcodeString == null) {
            return null;
        }
        final int prefixIndex = faultcodeString.indexOf(58);
        if (prefixIndex == -1) {
            return NameImpl.createFromUnqualifiedName(faultcodeString);
        }
        final String prefix = faultcodeString.substring(0, prefixIndex);
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final String nsName = this.faultCodeElement.getNamespaceURI(prefix);
        return NameImpl.createFromQualifiedName(faultcodeString, nsName);
    }
    
    @Override
    public QName getFaultCodeAsQName() {
        final String faultcodeString = this.getFaultCode();
        if (faultcodeString == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        return FaultImpl.convertCodeToQName(faultcodeString, this.faultCodeElement);
    }
    
    @Override
    public void setFaultString(final String faultString) throws SOAPException {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement("faultstring");
        }
        else {
            this.faultStringElement.removeContents();
            this.faultStringElement.removeAttribute("xml:lang");
        }
        this.faultStringElement.addTextNode(faultString);
    }
    
    @Override
    public String getFaultString() {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        return this.faultStringElement.getValue();
    }
    
    @Override
    public Locale getFaultStringLocale() {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement != null) {
            final String xmlLangAttr = this.faultStringElement.getAttributeValue(NameImpl.createFromUnqualifiedName("xml:lang"));
            if (xmlLangAttr != null) {
                return FaultImpl.xmlLangToLocale(xmlLangAttr);
            }
        }
        return null;
    }
    
    @Override
    public void setFaultString(final String faultString, final Locale locale) throws SOAPException {
        this.setFaultString(faultString);
        this.faultStringElement.addAttribute(NameImpl.createFromTagName("xml:lang"), FaultImpl.localeToXmlLang(locale));
    }
    
    @Override
    protected boolean isStandardFaultElement(final String localName) {
        return localName.equalsIgnoreCase("detail") || localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor");
    }
    
    @Override
    public void appendFaultSubcode(final QName subcode) {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "appendFaultSubcode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public void removeAllFaultSubcodes() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "removeAllFaultSubcodes");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public Iterator getFaultSubcodes() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultSubcodes");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public String getFaultReasonText(final Locale locale) {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonText");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public Iterator getFaultReasonTexts() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonTexts");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public Iterator getFaultReasonLocales() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonLocales");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public void addFaultReasonText(final String text, final Locale locale) throws SOAPException {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "addFaultReasonText");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public String getFaultRole() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultRole");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public void setFaultRole(final String uri) {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultRole");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public String getFaultNode() {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultNode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    public void setFaultNode(final String uri) {
        Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultNode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    protected QName getDefaultFaultCode() {
        return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    }
    
    @Override
    public SOAPElement addChildElement(final SOAPElement element) throws SOAPException {
        final String localName = element.getLocalName();
        if ("Detail".equalsIgnoreCase(localName) && this.hasDetail()) {
            Fault1_1Impl.log.severe("SAAJ0305.ver1_2.detail.exists.error");
            throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
        }
        return super.addChildElement(element);
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final QName qname) {
        return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname);
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final Name qname) {
        return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), (NameImpl)qname);
    }
    
    @Override
    public void setFaultCode(final String faultCode, String prefix, String uri) throws SOAPException {
        if ((prefix == null || "".equals(prefix)) && uri != null && !"".equals(uri)) {
            prefix = this.getNamespacePrefix(uri);
            if (prefix == null || "".equals(prefix)) {
                prefix = "ns0";
            }
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        if (this.faultCodeElement == null) {
            this.faultCodeElement = this.addFaultCodeElement();
        }
        else {
            this.faultCodeElement.removeContents();
        }
        if ((uri == null || "".equals(uri)) && prefix != null && !"".equals("prefix")) {
            uri = this.faultCodeElement.getNamespaceURI(prefix);
        }
        if (uri == null || "".equals(uri)) {
            if (prefix != null && !"".equals(prefix)) {
                Fault1_1Impl.log.log(Level.SEVERE, "SAAJ0307.impl.no.ns.URI", new Object[] { prefix + ":" + faultCode });
                throw new SOAPExceptionImpl("Empty/Null NamespaceURI specified for faultCode \"" + prefix + ":" + faultCode + "\"");
            }
            uri = "";
        }
        this.checkIfStandardFaultCode(faultCode, uri);
        ((FaultElementImpl)this.faultCodeElement).ensureNamespaceIsDeclared(prefix, uri);
        if (prefix == null || "".equals(prefix)) {
            this.finallySetFaultCode(faultCode);
        }
        else {
            this.finallySetFaultCode(prefix + ":" + faultCode);
        }
    }
    
    private boolean standardFaultCode(final String faultCode) {
        return faultCode.equals("VersionMismatch") || faultCode.equals("MustUnderstand") || faultCode.equals("Client") || faultCode.equals("Server") || (faultCode.startsWith("VersionMismatch.") || faultCode.startsWith("MustUnderstand.") || faultCode.startsWith("Client.") || faultCode.startsWith("Server."));
    }
    
    @Override
    public void setFaultActor(final String faultActor) throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            this.faultActorElement.detachNode();
        }
        if (faultActor == null) {
            return;
        }
        (this.faultActorElement = this.createSOAPFaultElement(this.getFaultActorName())).addTextNode(faultActor);
        if (this.hasDetail()) {
            this.insertBefore(this.faultActorElement, this.detail);
            return;
        }
        this.addNode(this.faultActorElement);
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
    }
}
