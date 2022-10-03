package com.sun.xml.internal.messaging.saaj.soap.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import java.util.logging.Level;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import javax.xml.soap.SOAPEnvelope;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import java.util.Iterator;
import java.util.Locale;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPBody;

public abstract class BodyImpl extends ElementImpl implements SOAPBody
{
    private SOAPFault fault;
    
    protected BodyImpl(final SOAPDocumentImpl ownerDoc, final NameImpl bodyName) {
        super(ownerDoc, bodyName);
    }
    
    protected abstract NameImpl getFaultName(final String p0);
    
    protected abstract boolean isFault(final SOAPElement p0);
    
    protected abstract SOAPBodyElement createBodyElement(final Name p0);
    
    protected abstract SOAPBodyElement createBodyElement(final QName p0);
    
    protected abstract SOAPFault createFaultElement();
    
    protected abstract QName getDefaultFaultCode();
    
    @Override
    public SOAPFault addFault() throws SOAPException {
        if (this.hasFault()) {
            BodyImpl.log.severe("SAAJ0110.impl.fault.already.exists");
            throw new SOAPExceptionImpl("Error: Fault already exists");
        }
        this.addNode(this.fault = this.createFaultElement());
        this.fault.setFaultCode(this.getDefaultFaultCode());
        this.fault.setFaultString("Fault string, and possibly fault code, not set");
        return this.fault;
    }
    
    @Override
    public SOAPFault addFault(final Name faultCode, final String faultString, final Locale locale) throws SOAPException {
        final SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString, locale);
        return fault;
    }
    
    @Override
    public SOAPFault addFault(final QName faultCode, final String faultString, final Locale locale) throws SOAPException {
        final SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString, locale);
        return fault;
    }
    
    @Override
    public SOAPFault addFault(final Name faultCode, final String faultString) throws SOAPException {
        final SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString);
        return fault;
    }
    
    @Override
    public SOAPFault addFault(final QName faultCode, final String faultString) throws SOAPException {
        final SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString);
        return fault;
    }
    
    void initializeFault() {
        final FaultImpl flt = (FaultImpl)this.findFault();
        this.fault = flt;
    }
    
    protected SOAPElement findFault() {
        final Iterator eachChild = this.getChildElementNodes();
        while (eachChild.hasNext()) {
            final SOAPElement child = eachChild.next();
            if (this.isFault(child)) {
                return child;
            }
        }
        return null;
    }
    
    @Override
    public boolean hasFault() {
        this.initializeFault();
        return this.fault != null;
    }
    
    @Override
    public SOAPFault getFault() {
        if (this.hasFault()) {
            return this.fault;
        }
        return null;
    }
    
    @Override
    public SOAPBodyElement addBodyElement(final Name name) throws SOAPException {
        SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
        if (newBodyElement == null) {
            newBodyElement = this.createBodyElement(name);
        }
        this.addNode(newBodyElement);
        return newBodyElement;
    }
    
    @Override
    public SOAPBodyElement addBodyElement(final QName qname) throws SOAPException {
        SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
        if (newBodyElement == null) {
            newBodyElement = this.createBodyElement(qname);
        }
        this.addNode(newBodyElement);
        return newBodyElement;
    }
    
    @Override
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPEnvelope)) {
            BodyImpl.log.severe("SAAJ0111.impl.body.parent.must.be.envelope");
            throw new SOAPException("Parent of SOAPBody has to be a SOAPEnvelope");
        }
        super.setParentElement(element);
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        return this.addBodyElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        return this.addBodyElement(name);
    }
    
    @Override
    public SOAPBodyElement addDocument(final Document document) throws SOAPException {
        SOAPBodyElement newBodyElement = null;
        final DocumentFragment docFrag = document.createDocumentFragment();
        final Element rootElement = document.getDocumentElement();
        if (rootElement != null) {
            docFrag.appendChild(rootElement);
            final Document ownerDoc = this.getOwnerDocument();
            final Node replacingNode = ownerDoc.importNode(docFrag, true);
            this.addNode(replacingNode);
            final Iterator i = this.getChildElements(NameImpl.copyElementName(rootElement));
            while (i.hasNext()) {
                newBodyElement = i.next();
            }
        }
        return newBodyElement;
    }
    
    @Override
    protected SOAPElement convertToSoapElement(final Element element) {
        if (element instanceof SOAPBodyElement && !element.getClass().equals(ElementImpl.class)) {
            return (SOAPElement)element;
        }
        return ElementImpl.replaceElementWithSOAPElement(element, (ElementImpl)this.createBodyElement(NameImpl.copyElementName(element)));
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        BodyImpl.log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), newName.getLocalPart() });
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
    
    @Override
    public Document extractContentAsDocument() throws SOAPException {
        Iterator eachChild;
        javax.xml.soap.Node firstBodyElement;
        for (eachChild = this.getChildElements(), firstBodyElement = null; eachChild.hasNext() && !(firstBodyElement instanceof SOAPElement); firstBodyElement = eachChild.next()) {}
        boolean exactlyOneChildElement = true;
        if (firstBodyElement == null) {
            exactlyOneChildElement = false;
        }
        else {
            for (Node node = firstBodyElement.getNextSibling(); node != null; node = node.getNextSibling()) {
                if (node instanceof Element) {
                    exactlyOneChildElement = false;
                    break;
                }
            }
        }
        if (!exactlyOneChildElement) {
            BodyImpl.log.log(Level.SEVERE, "SAAJ0250.impl.body.should.have.exactly.one.child");
            throw new SOAPException("Cannot extract Document from body");
        }
        Document document = null;
        try {
            final DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            final Element rootElement = (Element)document.importNode(firstBodyElement, true);
            document.appendChild(rootElement);
        }
        catch (final Exception e) {
            BodyImpl.log.log(Level.SEVERE, "SAAJ0251.impl.cannot.extract.document.from.body");
            throw new SOAPExceptionImpl("Unable to extract Document from body", e);
        }
        firstBodyElement.detachNode();
        return document;
    }
}
