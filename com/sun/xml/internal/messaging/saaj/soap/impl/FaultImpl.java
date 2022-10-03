package com.sun.xml.internal.messaging.saaj.soap.impl;

import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPElement;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFaultElement;
import javax.xml.soap.SOAPFault;

public abstract class FaultImpl extends ElementImpl implements SOAPFault
{
    protected SOAPFaultElement faultStringElement;
    protected SOAPFaultElement faultActorElement;
    protected SOAPFaultElement faultCodeElement;
    protected Detail detail;
    
    protected FaultImpl(final SOAPDocumentImpl ownerDoc, final NameImpl name) {
        super(ownerDoc, name);
    }
    
    protected abstract NameImpl getDetailName();
    
    protected abstract NameImpl getFaultCodeName();
    
    protected abstract NameImpl getFaultStringName();
    
    protected abstract NameImpl getFaultActorName();
    
    protected abstract DetailImpl createDetail();
    
    protected abstract FaultElementImpl createSOAPFaultElement(final String p0);
    
    protected abstract FaultElementImpl createSOAPFaultElement(final QName p0);
    
    protected abstract FaultElementImpl createSOAPFaultElement(final Name p0);
    
    protected abstract void checkIfStandardFaultCode(final String p0, final String p1) throws SOAPException;
    
    protected abstract void finallySetFaultCode(final String p0) throws SOAPException;
    
    protected abstract boolean isStandardFaultElement(final String p0);
    
    protected abstract QName getDefaultFaultCode();
    
    protected void findFaultCodeElement() {
        this.faultCodeElement = (SOAPFaultElement)this.findChild(this.getFaultCodeName());
    }
    
    protected void findFaultActorElement() {
        this.faultActorElement = (SOAPFaultElement)this.findChild(this.getFaultActorName());
    }
    
    protected void findFaultStringElement() {
        this.faultStringElement = (SOAPFaultElement)this.findChild(this.getFaultStringName());
    }
    
    @Override
    public void setFaultCode(final String faultCode) throws SOAPException {
        this.setFaultCode(NameImpl.getLocalNameFromTagName(faultCode), NameImpl.getPrefixFromTagName(faultCode), null);
    }
    
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
        if (uri == null || "".equals(uri)) {
            uri = this.faultCodeElement.getNamespaceURI(prefix);
        }
        if (uri == null || "".equals(uri)) {
            if (prefix != null && !"".equals(prefix)) {
                FaultImpl.log.log(Level.SEVERE, "SAAJ0140.impl.no.ns.URI", new Object[] { prefix + ":" + faultCode });
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
    
    @Override
    public void setFaultCode(final Name faultCodeQName) throws SOAPException {
        this.setFaultCode(faultCodeQName.getLocalName(), faultCodeQName.getPrefix(), faultCodeQName.getURI());
    }
    
    @Override
    public void setFaultCode(final QName faultCodeQName) throws SOAPException {
        this.setFaultCode(faultCodeQName.getLocalPart(), faultCodeQName.getPrefix(), faultCodeQName.getNamespaceURI());
    }
    
    protected static QName convertCodeToQName(final String code, final SOAPElement codeContainingElement) {
        final int prefixIndex = code.indexOf(58);
        if (prefixIndex == -1) {
            return new QName(code);
        }
        final String prefix = code.substring(0, prefixIndex);
        final String nsName = ((ElementImpl)codeContainingElement).lookupNamespaceURI(prefix);
        return new QName(nsName, ElementImpl.getLocalPart(code), prefix);
    }
    
    protected void initializeDetail() {
        final NameImpl detailName = this.getDetailName();
        this.detail = (Detail)this.findChild(detailName);
    }
    
    @Override
    public Detail getDetail() {
        if (this.detail == null) {
            this.initializeDetail();
        }
        if (this.detail != null && this.detail.getParentNode() == null) {
            this.detail = null;
        }
        return this.detail;
    }
    
    @Override
    public Detail addDetail() throws SOAPException {
        if (this.detail == null) {
            this.initializeDetail();
        }
        if (this.detail == null) {
            this.addNode(this.detail = this.createDetail());
            return this.detail;
        }
        throw new SOAPExceptionImpl("Error: Detail already exists");
    }
    
    @Override
    public boolean hasDetail() {
        return this.getDetail() != null;
    }
    
    @Override
    public abstract void setFaultActor(final String p0) throws SOAPException;
    
    @Override
    public String getFaultActor() {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            return this.faultActorElement.getValue();
        }
        return null;
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        FaultImpl.log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), newName.getLocalPart() });
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
    
    @Override
    protected SOAPElement convertToSoapElement(final Element element) {
        if (element instanceof SOAPFaultElement) {
            return (SOAPElement)element;
        }
        if (!(element instanceof SOAPElement)) {
            final Name elementName = NameImpl.copyElementName(element);
            ElementImpl newElement;
            if (this.getDetailName().equals(elementName)) {
                newElement = this.createDetail();
            }
            else {
                final String localName = elementName.getLocalName();
                if (this.isStandardFaultElement(localName)) {
                    newElement = this.createSOAPFaultElement(elementName);
                }
                else {
                    newElement = (ElementImpl)this.createElement(elementName);
                }
            }
            return ElementImpl.replaceElementWithSOAPElement(element, newElement);
        }
        final SOAPElement soapElement = (SOAPElement)element;
        if (this.getDetailName().equals(soapElement.getElementName())) {
            return ElementImpl.replaceElementWithSOAPElement(element, this.createDetail());
        }
        final String localName2 = soapElement.getElementName().getLocalName();
        if (this.isStandardFaultElement(localName2)) {
            return ElementImpl.replaceElementWithSOAPElement(element, this.createSOAPFaultElement(soapElement.getElementQName()));
        }
        return soapElement;
    }
    
    protected SOAPFaultElement addFaultCodeElement() throws SOAPException {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        if (this.faultCodeElement == null) {
            return this.faultCodeElement = this.addSOAPFaultElement(this.getFaultCodeName().getLocalName());
        }
        throw new SOAPExceptionImpl("Error: Faultcode already exists");
    }
    
    private SOAPFaultElement addFaultStringElement() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement == null) {
            return this.faultStringElement = this.addSOAPFaultElement(this.getFaultStringName().getLocalName());
        }
        throw new SOAPExceptionImpl("Error: Faultstring already exists");
    }
    
    private SOAPFaultElement addFaultActorElement() throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement == null) {
            return this.faultActorElement = this.addSOAPFaultElement(this.getFaultActorName().getLocalName());
        }
        throw new SOAPExceptionImpl("Error: Faultactor already exists");
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        if (this.getDetailName().equals(name)) {
            return this.addDetail();
        }
        if (this.getFaultCodeName().equals(name)) {
            return this.addFaultCodeElement();
        }
        if (this.getFaultStringName().equals(name)) {
            return this.addFaultStringElement();
        }
        if (this.getFaultActorName().equals(name)) {
            return this.addFaultActorElement();
        }
        return super.addElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        return this.addElement(NameImpl.convertToName(name));
    }
    
    protected FaultElementImpl addSOAPFaultElement(final String localName) throws SOAPException {
        final FaultElementImpl faultElem = this.createSOAPFaultElement(localName);
        this.addNode(faultElem);
        return faultElem;
    }
    
    protected static Locale xmlLangToLocale(final String xmlLang) {
        if (xmlLang == null) {
            return null;
        }
        int index = xmlLang.indexOf("-");
        if (index == -1) {
            index = xmlLang.indexOf("_");
        }
        if (index == -1) {
            return new Locale(xmlLang, "");
        }
        final String language = xmlLang.substring(0, index);
        final String country = xmlLang.substring(index + 1);
        return new Locale(language, country);
    }
    
    protected static String localeToXmlLang(final Locale locale) {
        String xmlLang = locale.getLanguage();
        final String country = locale.getCountry();
        if (!"".equals(country)) {
            xmlLang = xmlLang + "-" + country;
        }
        return xmlLang;
    }
}
