package com.sun.xml.internal.messaging.saaj.soap.impl;

import java.util.logging.Level;
import org.w3c.dom.Element;
import javax.xml.soap.SOAPEnvelope;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.Node;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.SOAPHeader;

public abstract class HeaderImpl extends ElementImpl implements SOAPHeader
{
    protected static final boolean MUST_UNDERSTAND_ONLY = false;
    
    protected HeaderImpl(final SOAPDocumentImpl ownerDoc, final NameImpl name) {
        super(ownerDoc, name);
    }
    
    protected abstract SOAPHeaderElement createHeaderElement(final Name p0) throws SOAPException;
    
    protected abstract SOAPHeaderElement createHeaderElement(final QName p0) throws SOAPException;
    
    protected abstract NameImpl getNotUnderstoodName();
    
    protected abstract NameImpl getUpgradeName();
    
    protected abstract NameImpl getSupportedEnvelopeName();
    
    @Override
    public SOAPHeaderElement addHeaderElement(final Name name) throws SOAPException {
        SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
        if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
            newHeaderElement = this.createHeaderElement(name);
        }
        final String uri = newHeaderElement.getElementQName().getNamespaceURI();
        if (uri == null || "".equals(uri)) {
            HeaderImpl.log.severe("SAAJ0131.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        this.addNode(newHeaderElement);
        return (SOAPHeaderElement)newHeaderElement;
    }
    
    @Override
    public SOAPHeaderElement addHeaderElement(final QName name) throws SOAPException {
        SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalPart(), name.getPrefix(), name.getNamespaceURI());
        if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
            newHeaderElement = this.createHeaderElement(name);
        }
        final String uri = newHeaderElement.getElementQName().getNamespaceURI();
        if (uri == null || "".equals(uri)) {
            HeaderImpl.log.severe("SAAJ0131.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        this.addNode(newHeaderElement);
        return (SOAPHeaderElement)newHeaderElement;
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        return this.addHeaderElement(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        return this.addHeaderElement(name);
    }
    
    @Override
    public Iterator examineHeaderElements(final String actor) {
        return this.getHeaderElementsForActor(actor, false, false);
    }
    
    @Override
    public Iterator extractHeaderElements(final String actor) {
        return this.getHeaderElementsForActor(actor, true, false);
    }
    
    protected Iterator getHeaderElementsForActor(final String actor, final boolean detach, final boolean mustUnderstand) {
        if (actor == null || actor.equals("")) {
            HeaderImpl.log.severe("SAAJ0132.impl.invalid.value.for.actor.or.role");
            throw new IllegalArgumentException("Invalid value for actor or role");
        }
        return this.getHeaderElements(actor, detach, mustUnderstand);
    }
    
    protected Iterator getHeaderElements(final String actor, final boolean detach, final boolean mustUnderstand) {
        final List elementList = new ArrayList();
        final Iterator eachChild = this.getChildElements();
        Object currentChild = this.iterate(eachChild);
        while (currentChild != null) {
            if (!(currentChild instanceof SOAPHeaderElement)) {
                currentChild = this.iterate(eachChild);
            }
            else {
                final HeaderElementImpl currentElement = (HeaderElementImpl)currentChild;
                currentChild = this.iterate(eachChild);
                final boolean isMustUnderstandMatching = !mustUnderstand || currentElement.getMustUnderstand();
                boolean doAdd = false;
                if (actor == null && isMustUnderstandMatching) {
                    doAdd = true;
                }
                else {
                    String currentActor = currentElement.getActorOrRole();
                    if (currentActor == null) {
                        currentActor = "";
                    }
                    if (currentActor.equalsIgnoreCase(actor) && isMustUnderstandMatching) {
                        doAdd = true;
                    }
                }
                if (!doAdd) {
                    continue;
                }
                elementList.add(currentElement);
                if (!detach) {
                    continue;
                }
                currentElement.detachNode();
            }
        }
        return elementList.listIterator();
    }
    
    private Object iterate(final Iterator each) {
        return each.hasNext() ? each.next() : null;
    }
    
    @Override
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPEnvelope)) {
            HeaderImpl.log.severe("SAAJ0133.impl.header.parent.mustbe.envelope");
            throw new SOAPException("Parent of SOAPHeader has to be a SOAPEnvelope");
        }
        super.setParentElement(element);
    }
    
    @Override
    public SOAPElement addChildElement(final String localName) throws SOAPException {
        final SOAPElement element = super.addChildElement(localName);
        final String uri = element.getElementName().getURI();
        if (uri == null || "".equals(uri)) {
            HeaderImpl.log.severe("SAAJ0134.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        return element;
    }
    
    @Override
    public Iterator examineAllHeaderElements() {
        return this.getHeaderElements(null, false, false);
    }
    
    @Override
    public Iterator examineMustUnderstandHeaderElements(final String actor) {
        return this.getHeaderElements(actor, false, true);
    }
    
    @Override
    public Iterator extractAllHeaderElements() {
        return this.getHeaderElements(null, true, false);
    }
    
    @Override
    public SOAPHeaderElement addUpgradeHeaderElement(final Iterator supportedSoapUris) throws SOAPException {
        if (supportedSoapUris == null) {
            HeaderImpl.log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
            throw new SOAPException("Argument cannot be null; iterator of supportedURIs cannot be null");
        }
        if (!supportedSoapUris.hasNext()) {
            HeaderImpl.log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
            throw new SOAPException("List of supported URIs cannot be empty");
        }
        final Name upgradeName = this.getUpgradeName();
        final SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
        final Name supportedEnvelopeName = this.getSupportedEnvelopeName();
        int i = 0;
        while (supportedSoapUris.hasNext()) {
            final SOAPElement subElement = upgradeHeaderElement.addChildElement(supportedEnvelopeName);
            final String ns = "ns" + Integer.toString(i);
            subElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, supportedSoapUris.next());
            ++i;
        }
        return upgradeHeaderElement;
    }
    
    @Override
    public SOAPHeaderElement addUpgradeHeaderElement(final String supportedSoapUri) throws SOAPException {
        return this.addUpgradeHeaderElement(new String[] { supportedSoapUri });
    }
    
    @Override
    public SOAPHeaderElement addUpgradeHeaderElement(final String[] supportedSoapUris) throws SOAPException {
        if (supportedSoapUris == null) {
            HeaderImpl.log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
            throw new SOAPException("Argument cannot be null; array of supportedURIs cannot be null");
        }
        if (supportedSoapUris.length == 0) {
            HeaderImpl.log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
            throw new SOAPException("List of supported URIs cannot be empty");
        }
        final Name upgradeName = this.getUpgradeName();
        final SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
        final Name supportedEnvelopeName = this.getSupportedEnvelopeName();
        for (int i = 0; i < supportedSoapUris.length; ++i) {
            final SOAPElement subElement = upgradeHeaderElement.addChildElement(supportedEnvelopeName);
            final String ns = "ns" + Integer.toString(i);
            subElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, supportedSoapUris[i]);
        }
        return upgradeHeaderElement;
    }
    
    @Override
    protected SOAPElement convertToSoapElement(final Element element) {
        if (element instanceof SOAPHeaderElement) {
            return (SOAPElement)element;
        }
        SOAPHeaderElement headerElement;
        try {
            headerElement = this.createHeaderElement(NameImpl.copyElementName(element));
        }
        catch (final SOAPException e) {
            throw new ClassCastException("Could not convert Element to SOAPHeaderElement: " + e.getMessage());
        }
        return ElementImpl.replaceElementWithSOAPElement(element, (ElementImpl)headerElement);
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        HeaderImpl.log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), newName.getLocalPart() });
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
}
