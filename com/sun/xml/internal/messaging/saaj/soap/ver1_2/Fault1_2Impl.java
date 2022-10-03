package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import org.w3c.dom.Node;
import javax.xml.soap.Name;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;

public class Fault1_2Impl extends FaultImpl
{
    protected static final Logger log;
    private static final QName textName;
    private final QName valueName;
    private final QName subcodeName;
    private SOAPElement innermostSubCodeElement;
    
    public Fault1_2Impl(final SOAPDocumentImpl ownerDoc, final String name, final String prefix) {
        super(ownerDoc, NameImpl.createFault1_2Name(name, prefix));
        this.valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", this.getPrefix());
        this.subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", this.getPrefix());
        this.innermostSubCodeElement = null;
    }
    
    public Fault1_2Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createFault1_2Name(null, prefix));
        this.valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", this.getPrefix());
        this.subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", this.getPrefix());
        this.innermostSubCodeElement = null;
    }
    
    @Override
    protected NameImpl getDetailName() {
        return NameImpl.createSOAP12Name("Detail", this.getPrefix());
    }
    
    @Override
    protected NameImpl getFaultCodeName() {
        return NameImpl.createSOAP12Name("Code", this.getPrefix());
    }
    
    @Override
    protected NameImpl getFaultStringName() {
        return this.getFaultReasonName();
    }
    
    @Override
    protected NameImpl getFaultActorName() {
        return this.getFaultRoleName();
    }
    
    private NameImpl getFaultRoleName() {
        return NameImpl.createSOAP12Name("Role", this.getPrefix());
    }
    
    private NameImpl getFaultReasonName() {
        return NameImpl.createSOAP12Name("Reason", this.getPrefix());
    }
    
    private NameImpl getFaultReasonTextName() {
        return NameImpl.createSOAP12Name("Text", this.getPrefix());
    }
    
    private NameImpl getFaultNodeName() {
        return NameImpl.createSOAP12Name("Node", this.getPrefix());
    }
    
    private static NameImpl getXmlLangName() {
        return NameImpl.createXmlName("lang");
    }
    
    @Override
    protected DetailImpl createDetail() {
        return new Detail1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument());
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final String localName) {
        return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), localName);
    }
    
    @Override
    protected void checkIfStandardFaultCode(final String faultCode, final String uri) throws SOAPException {
        final QName qname = new QName(uri, faultCode);
        if (SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(qname) || SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(qname) || SOAPConstants.SOAP_RECEIVER_FAULT.equals(qname) || SOAPConstants.SOAP_SENDER_FAULT.equals(qname) || SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(qname)) {
            return;
        }
        Fault1_2Impl.log.log(Level.SEVERE, "SAAJ0435.ver1_2.code.not.standard", qname);
        throw new SOAPExceptionImpl(qname + " is not a standard Code value");
    }
    
    @Override
    protected void finallySetFaultCode(final String faultcode) throws SOAPException {
        final SOAPElement value = this.faultCodeElement.addChildElement(this.valueName);
        value.addTextNode(faultcode);
    }
    
    private void findReasonElement() {
        this.findFaultStringElement();
    }
    
    @Override
    public Iterator getFaultReasonTexts() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        final Iterator eachTextElement = this.faultStringElement.getChildElements(Fault1_2Impl.textName);
        final List texts = new ArrayList();
        while (eachTextElement.hasNext()) {
            final SOAPElement textElement = eachTextElement.next();
            final Locale thisLocale = getLocale(textElement);
            if (thisLocale == null) {
                Fault1_2Impl.log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            texts.add(textElement.getValue());
        }
        if (texts.isEmpty()) {
            Fault1_2Impl.log.severe("SAAJ0434.ver1_2.text.element.not.present");
            throw new SOAPExceptionImpl("env:Text must be present inside env:Reason");
        }
        return texts.iterator();
    }
    
    @Override
    public void addFaultReasonText(final String text, final Locale locale) throws SOAPException {
        if (locale == null) {
            Fault1_2Impl.log.severe("SAAJ0430.ver1_2.locale.required");
            throw new SOAPException("locale is required and must not be null");
        }
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        SOAPElement reasonText;
        if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement("Reason");
            reasonText = this.faultStringElement.addChildElement(this.getFaultReasonTextName());
        }
        else {
            this.removeDefaultFaultString();
            reasonText = this.getFaultReasonTextElement(locale);
            if (reasonText != null) {
                reasonText.removeContents();
            }
            else {
                reasonText = this.faultStringElement.addChildElement(this.getFaultReasonTextName());
            }
        }
        final String xmlLang = FaultImpl.localeToXmlLang(locale);
        reasonText.addAttribute(getXmlLangName(), xmlLang);
        reasonText.addTextNode(text);
    }
    
    private void removeDefaultFaultString() throws SOAPException {
        final SOAPElement reasonText = this.getFaultReasonTextElement(Locale.getDefault());
        if (reasonText != null) {
            final String defaultFaultString = "Fault string, and possibly fault code, not set";
            if (defaultFaultString.equals(reasonText.getValue())) {
                reasonText.detachNode();
            }
        }
    }
    
    @Override
    public String getFaultReasonText(final Locale locale) throws SOAPException {
        if (locale == null) {
            return null;
        }
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        if (this.faultStringElement != null) {
            final SOAPElement textElement = this.getFaultReasonTextElement(locale);
            if (textElement != null) {
                textElement.normalize();
                return textElement.getFirstChild().getNodeValue();
            }
        }
        return null;
    }
    
    @Override
    public Iterator getFaultReasonLocales() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        final Iterator eachTextElement = this.faultStringElement.getChildElements(Fault1_2Impl.textName);
        final List localeSet = new ArrayList();
        while (eachTextElement.hasNext()) {
            final SOAPElement textElement = eachTextElement.next();
            final Locale thisLocale = getLocale(textElement);
            if (thisLocale == null) {
                Fault1_2Impl.log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            localeSet.add(thisLocale);
        }
        if (localeSet.isEmpty()) {
            Fault1_2Impl.log.severe("SAAJ0434.ver1_2.text.element.not.present");
            throw new SOAPExceptionImpl("env:Text elements with mandatory xml:lang attributes must be present inside env:Reason");
        }
        return localeSet.iterator();
    }
    
    @Override
    public Locale getFaultStringLocale() {
        Locale locale = null;
        try {
            locale = this.getFaultReasonLocales().next();
        }
        catch (final SOAPException ex) {}
        return locale;
    }
    
    private SOAPElement getFaultReasonTextElement(final Locale locale) throws SOAPException {
        final Iterator eachTextElement = this.faultStringElement.getChildElements(Fault1_2Impl.textName);
        while (eachTextElement.hasNext()) {
            final SOAPElement textElement = eachTextElement.next();
            final Locale thisLocale = getLocale(textElement);
            if (thisLocale == null) {
                Fault1_2Impl.log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            if (thisLocale.equals(locale)) {
                return textElement;
            }
        }
        return null;
    }
    
    @Override
    public String getFaultNode() {
        final SOAPElement faultNode = this.findChild(this.getFaultNodeName());
        if (faultNode == null) {
            return null;
        }
        return faultNode.getValue();
    }
    
    @Override
    public void setFaultNode(final String uri) throws SOAPException {
        SOAPElement faultNode = this.findChild(this.getFaultNodeName());
        if (faultNode != null) {
            faultNode.detachNode();
        }
        faultNode = this.createSOAPFaultElement(this.getFaultNodeName());
        faultNode = faultNode.addTextNode(uri);
        if (this.getFaultRole() != null) {
            this.insertBefore(faultNode, this.faultActorElement);
            return;
        }
        if (this.hasDetail()) {
            this.insertBefore(faultNode, this.detail);
            return;
        }
        this.addNode(faultNode);
    }
    
    @Override
    public String getFaultRole() {
        return this.getFaultActor();
    }
    
    @Override
    public void setFaultRole(final String uri) throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            this.faultActorElement.detachNode();
        }
        (this.faultActorElement = this.createSOAPFaultElement(this.getFaultActorName())).addTextNode(uri);
        if (this.hasDetail()) {
            this.insertBefore(this.faultActorElement, this.detail);
            return;
        }
        this.addNode(this.faultActorElement);
    }
    
    @Override
    public String getFaultCode() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final Iterator codeValues = this.faultCodeElement.getChildElements(this.valueName);
        return codeValues.next().getValue();
    }
    
    @Override
    public QName getFaultCodeAsQName() {
        final String faultcode = this.getFaultCode();
        if (faultcode == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
        return FaultImpl.convertCodeToQName(faultcode, valueElements.next());
    }
    
    @Override
    public Name getFaultCodeAsName() {
        final String faultcode = this.getFaultCode();
        if (faultcode == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
        return NameImpl.convertToName(FaultImpl.convertCodeToQName(faultcode, valueElements.next()));
    }
    
    @Override
    public String getFaultString() {
        String reason = null;
        try {
            reason = this.getFaultReasonTexts().next();
        }
        catch (final SOAPException ex) {}
        return reason;
    }
    
    @Override
    public void setFaultString(final String faultString) throws SOAPException {
        this.addFaultReasonText(faultString, Locale.getDefault());
    }
    
    @Override
    public void setFaultString(final String faultString, final Locale locale) throws SOAPException {
        this.addFaultReasonText(faultString, locale);
    }
    
    @Override
    public void appendFaultSubcode(final QName subcode) throws SOAPException {
        if (subcode == null) {
            return;
        }
        if (subcode.getNamespaceURI() == null || "".equals(subcode.getNamespaceURI())) {
            Fault1_2Impl.log.severe("SAAJ0432.ver1_2.subcode.not.ns.qualified");
            throw new SOAPExceptionImpl("A Subcode must be namespace-qualified");
        }
        if (this.innermostSubCodeElement == null) {
            if (this.faultCodeElement == null) {
                this.findFaultCodeElement();
            }
            this.innermostSubCodeElement = this.faultCodeElement;
        }
        String prefix = null;
        if (subcode.getPrefix() == null || "".equals(subcode.getPrefix())) {
            prefix = ((ElementImpl)this.innermostSubCodeElement).getNamespacePrefix(subcode.getNamespaceURI());
        }
        else {
            prefix = subcode.getPrefix();
        }
        if (prefix == null || "".equals(prefix)) {
            prefix = "ns1";
        }
        this.innermostSubCodeElement = this.innermostSubCodeElement.addChildElement(this.subcodeName);
        final SOAPElement subcodeValueElement = this.innermostSubCodeElement.addChildElement(this.valueName);
        ((ElementImpl)subcodeValueElement).ensureNamespaceIsDeclared(prefix, subcode.getNamespaceURI());
        subcodeValueElement.addTextNode(prefix + ":" + subcode.getLocalPart());
    }
    
    @Override
    public void removeAllFaultSubcodes() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final Iterator subcodeElements = this.faultCodeElement.getChildElements(this.subcodeName);
        if (subcodeElements.hasNext()) {
            final SOAPElement subcode = subcodeElements.next();
            subcode.detachNode();
        }
    }
    
    @Override
    public Iterator getFaultSubcodes() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final List subcodeList = new ArrayList();
        SOAPElement currentCodeElement = this.faultCodeElement;
        for (Iterator subcodeElements = currentCodeElement.getChildElements(this.subcodeName); subcodeElements.hasNext(); subcodeElements = currentCodeElement.getChildElements(this.subcodeName)) {
            currentCodeElement = subcodeElements.next();
            final Iterator valueElements = currentCodeElement.getChildElements(this.valueName);
            final SOAPElement valueElement = valueElements.next();
            final String code = valueElement.getValue();
            subcodeList.add(FaultImpl.convertCodeToQName(code, valueElement));
        }
        return new Iterator() {
            Iterator subCodeIter = subcodeList.iterator();
            
            @Override
            public boolean hasNext() {
                return this.subCodeIter.hasNext();
            }
            
            @Override
            public Object next() {
                return this.subCodeIter.next();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Method remove() not supported on SubCodes Iterator");
            }
        };
    }
    
    private static Locale getLocale(final SOAPElement reasonText) {
        return FaultImpl.xmlLangToLocale(reasonText.getAttributeValue(getXmlLangName()));
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        Fault1_2Impl.log.severe("SAAJ0407.ver1_2.no.encodingStyle.in.fault");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Fault");
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
    public SOAPElement addTextNode(final String text) throws SOAPException {
        Fault1_2Impl.log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", this.getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Fault is not legal");
    }
    
    @Override
    public SOAPElement addChildElement(final SOAPElement element) throws SOAPException {
        final String localName = element.getLocalName();
        if ("Detail".equalsIgnoreCase(localName)) {
            if (this.hasDetail()) {
                Fault1_2Impl.log.severe("SAAJ0436.ver1_2.detail.exists.error");
                throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
            }
            final String uri = element.getElementQName().getNamespaceURI();
            if (!uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
                Fault1_2Impl.log.severe("SAAJ0437.ver1_2.version.mismatch.error");
                throw new SOAPExceptionImpl("Cannot add Detail, Incorrect SOAP version specified for Detail element");
            }
        }
        if (element instanceof Detail1_2Impl) {
            final ElementImpl importedElement = (ElementImpl)this.importElement(element);
            this.addNode(importedElement);
            return this.convertToSoapElement(importedElement);
        }
        return super.addChildElement(element);
    }
    
    @Override
    protected boolean isStandardFaultElement(final String localName) {
        return localName.equalsIgnoreCase("code") || localName.equalsIgnoreCase("reason") || localName.equalsIgnoreCase("node") || localName.equalsIgnoreCase("role") || localName.equalsIgnoreCase("detail");
    }
    
    @Override
    protected QName getDefaultFaultCode() {
        return SOAPConstants.SOAP_SENDER_FAULT;
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final QName qname) {
        return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname);
    }
    
    @Override
    protected FaultElementImpl createSOAPFaultElement(final Name qname) {
        return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), (NameImpl)qname);
    }
    
    @Override
    public void setFaultActor(final String faultActor) throws SOAPException {
        this.setFaultRole(faultActor);
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
        textName = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
    }
}
