package com.sun.xml.internal.messaging.saaj.soap.impl;

import java.util.NoSuchElementException;
import java.util.Iterator;
import org.w3c.dom.Element;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.Detail;

public abstract class DetailImpl extends FaultElementImpl implements Detail
{
    public DetailImpl(final SOAPDocumentImpl ownerDoc, final NameImpl detailName) {
        super(ownerDoc, detailName);
    }
    
    protected abstract DetailEntry createDetailEntry(final Name p0);
    
    protected abstract DetailEntry createDetailEntry(final QName p0);
    
    @Override
    public DetailEntry addDetailEntry(final Name name) throws SOAPException {
        final DetailEntry entry = this.createDetailEntry(name);
        this.addNode(entry);
        return entry;
    }
    
    @Override
    public DetailEntry addDetailEntry(final QName qname) throws SOAPException {
        final DetailEntry entry = this.createDetailEntry(qname);
        this.addNode(entry);
        return entry;
    }
    
    @Override
    protected SOAPElement addElement(final Name name) throws SOAPException {
        return this.addDetailEntry(name);
    }
    
    @Override
    protected SOAPElement addElement(final QName name) throws SOAPException {
        return this.addDetailEntry(name);
    }
    
    @Override
    protected SOAPElement convertToSoapElement(final Element element) {
        if (element instanceof DetailEntry) {
            return (SOAPElement)element;
        }
        final DetailEntry detailEntry = this.createDetailEntry(NameImpl.copyElementName(element));
        return ElementImpl.replaceElementWithSOAPElement(element, (ElementImpl)detailEntry);
    }
    
    @Override
    public Iterator getDetailEntries() {
        return new Iterator() {
            Iterator eachNode = DetailImpl.this.getChildElementNodes();
            SOAPElement next = null;
            SOAPElement last = null;
            
            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachNode.hasNext()) {
                        this.next = this.eachNode.next();
                        if (this.next instanceof DetailEntry) {
                            break;
                        }
                        this.next = null;
                    }
                }
                return this.next != null;
            }
            
            @Override
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return this.last;
            }
            
            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                final SOAPElement target = this.last;
                DetailImpl.this.removeChild(target);
                this.last = null;
            }
        };
    }
    
    @Override
    protected boolean isStandardFaultElement() {
        return true;
    }
}
