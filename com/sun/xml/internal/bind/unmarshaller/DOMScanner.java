package com.sun.xml.internal.bind.unmarshaller;

import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Enumeration;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;

public class DOMScanner implements LocatorEx, InfosetScanner
{
    private Node currentNode;
    private final AttributesImpl atts;
    private ContentHandler receiver;
    private Locator locator;
    
    public DOMScanner() {
        this.currentNode = null;
        this.atts = new AttributesImpl();
        this.receiver = null;
        this.locator = this;
    }
    
    public void setLocator(final Locator loc) {
        this.locator = loc;
    }
    
    @Override
    public void scan(final Object node) throws SAXException {
        if (node instanceof Document) {
            this.scan((Document)node);
        }
        else {
            this.scan((Element)node);
        }
    }
    
    public void scan(final Document doc) throws SAXException {
        this.scan(doc.getDocumentElement());
    }
    
    public void scan(final Element e) throws SAXException {
        this.setCurrentLocation(e);
        this.receiver.setDocumentLocator(this.locator);
        this.receiver.startDocument();
        final NamespaceSupport nss = new NamespaceSupport();
        this.buildNamespaceSupport(nss, e.getParentNode());
        Enumeration en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            final String prefix = en.nextElement();
            this.receiver.startPrefixMapping(prefix, nss.getURI(prefix));
        }
        this.visit(e);
        en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            final String prefix = en.nextElement();
            this.receiver.endPrefixMapping(prefix);
        }
        this.setCurrentLocation(e);
        this.receiver.endDocument();
    }
    
    @Deprecated
    public void parse(final Element e, final ContentHandler handler) throws SAXException {
        this.receiver = handler;
        this.setCurrentLocation(e);
        this.receiver.startDocument();
        this.receiver.setDocumentLocator(this.locator);
        this.visit(e);
        this.setCurrentLocation(e);
        this.receiver.endDocument();
    }
    
    @Deprecated
    public void parseWithContext(final Element e, final ContentHandler handler) throws SAXException {
        this.setContentHandler(handler);
        this.scan(e);
    }
    
    private void buildNamespaceSupport(final NamespaceSupport nss, final Node node) {
        if (node == null || node.getNodeType() != 1) {
            return;
        }
        this.buildNamespaceSupport(nss, node.getParentNode());
        nss.pushContext();
        final NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); ++i) {
            final Attr a = (Attr)atts.item(i);
            if ("xmlns".equals(a.getPrefix())) {
                nss.declarePrefix(a.getLocalName(), a.getValue());
            }
            else if ("xmlns".equals(a.getName())) {
                nss.declarePrefix("", a.getValue());
            }
        }
    }
    
    public void visit(final Element e) throws SAXException {
        this.setCurrentLocation(e);
        final NamedNodeMap attributes = e.getAttributes();
        this.atts.clear();
        final int len = (attributes == null) ? 0 : attributes.getLength();
        for (int i = len - 1; i >= 0; --i) {
            final Attr a = (Attr)attributes.item(i);
            final String name = a.getName();
            if (name.startsWith("xmlns")) {
                if (name.length() == 5) {
                    this.receiver.startPrefixMapping("", a.getValue());
                }
                else {
                    String localName = a.getLocalName();
                    if (localName == null) {
                        localName = name.substring(6);
                    }
                    this.receiver.startPrefixMapping(localName, a.getValue());
                }
            }
            else {
                String uri = a.getNamespaceURI();
                if (uri == null) {
                    uri = "";
                }
                String local = a.getLocalName();
                if (local == null) {
                    local = a.getName();
                }
                this.atts.addAttribute(uri, local, a.getName(), "CDATA", a.getValue());
            }
        }
        String uri2 = e.getNamespaceURI();
        if (uri2 == null) {
            uri2 = "";
        }
        String local2 = e.getLocalName();
        final String qname = e.getTagName();
        if (local2 == null) {
            local2 = qname;
        }
        this.receiver.startElement(uri2, local2, qname, this.atts);
        final NodeList children = e.getChildNodes();
        for (int clen = children.getLength(), j = 0; j < clen; ++j) {
            this.visit(children.item(j));
        }
        this.setCurrentLocation(e);
        this.receiver.endElement(uri2, local2, qname);
        for (int j = len - 1; j >= 0; --j) {
            final Attr a2 = (Attr)attributes.item(j);
            final String name2 = a2.getName();
            if (name2.startsWith("xmlns")) {
                if (name2.length() == 5) {
                    this.receiver.endPrefixMapping("");
                }
                else {
                    this.receiver.endPrefixMapping(a2.getLocalName());
                }
            }
        }
    }
    
    private void visit(final Node n) throws SAXException {
        this.setCurrentLocation(n);
        switch (n.getNodeType()) {
            case 3:
            case 4: {
                final String value = n.getNodeValue();
                this.receiver.characters(value.toCharArray(), 0, value.length());
                break;
            }
            case 1: {
                this.visit((Element)n);
                break;
            }
            case 5: {
                this.receiver.skippedEntity(n.getNodeName());
                break;
            }
            case 7: {
                final ProcessingInstruction pi = (ProcessingInstruction)n;
                this.receiver.processingInstruction(pi.getTarget(), pi.getData());
                break;
            }
        }
    }
    
    private void setCurrentLocation(final Node currNode) {
        this.currentNode = currNode;
    }
    
    public Node getCurrentLocation() {
        return this.currentNode;
    }
    
    @Override
    public Object getCurrentElement() {
        return this.currentNode;
    }
    
    @Override
    public LocatorEx getLocator() {
        return this;
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this.receiver = handler;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this.receiver;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getSystemId() {
        return null;
    }
    
    @Override
    public int getLineNumber() {
        return -1;
    }
    
    @Override
    public int getColumnNumber() {
        return -1;
    }
    
    @Override
    public ValidationEventLocator getLocation() {
        return new ValidationEventLocatorImpl(this.getCurrentLocation());
    }
}
