package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import java.util.Map;
import org.apache.axiom.om.OMContainer;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public abstract class OMContentHandler implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler
{
    private final boolean expandEntityReferences;
    private OMContainer root;
    private String dtdName;
    private String dtdPublicId;
    private String dtdSystemId;
    private StringBuilder internalSubset;
    private Map entities;
    private boolean inExternalSubset;
    private OMContainer target;
    private String[] namespaces;
    private int namespaceCount;
    private int textNodeType;
    private boolean inEntityReference;
    private int entityReferenceDepth;
    
    public OMContentHandler(final boolean expandEntityReferences) {
        this.namespaces = new String[16];
        this.textNodeType = 4;
        this.expandEntityReferences = expandEntityReferences;
    }
    
    public final void setDocumentLocator(final Locator locator) {
    }
    
    public final void startDocument() throws SAXException {
        final OMContainer doStartDocument = this.doStartDocument();
        this.root = doStartDocument;
        this.target = doStartDocument;
    }
    
    public final void endDocument() throws SAXException {
        if (this.target != this.root) {
            throw new IllegalStateException();
        }
        this.doEndDocument();
        this.target = null;
    }
    
    public final void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.dtdName = name;
        this.dtdPublicId = publicId;
        this.dtdSystemId = systemId;
        this.internalSubset = new StringBuilder();
    }
    
    public final void elementDecl(final String name, final String model) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ELEMENT ");
            this.internalSubset.append(name);
            this.internalSubset.append(' ');
            this.internalSubset.append(model);
            this.internalSubset.append(">\n");
        }
    }
    
    public final void attributeDecl(final String eName, final String aName, final String type, final String mode, final String value) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ATTLIST ");
            this.internalSubset.append(eName);
            this.internalSubset.append(' ');
            this.internalSubset.append(aName);
            this.internalSubset.append(' ');
            this.internalSubset.append(type);
            if (value != null) {
                this.internalSubset.append(' ');
                this.internalSubset.append(value);
            }
            this.internalSubset.append(">\n");
        }
    }
    
    public final void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            }
            else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\">\n");
        }
    }
    
    public final void internalEntityDecl(final String name, final String value) throws SAXException {
        if (this.entities == null) {
            this.entities = new HashMap();
        }
        this.entities.put(name, value);
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            this.internalSubset.append(" \"");
            this.internalSubset.append(value);
            this.internalSubset.append("\">\n");
        }
    }
    
    public final void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!NOTATION ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            }
            else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\">\n");
        }
    }
    
    public final void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            }
            else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\" NDATA ");
            this.internalSubset.append(notationName);
            this.internalSubset.append(">\n");
        }
    }
    
    public final void endDTD() throws SAXException {
        this.createOMDocType(this.target, this.dtdName, this.dtdPublicId, this.dtdSystemId, (this.internalSubset.length() == 0) ? null : this.internalSubset.toString());
        this.internalSubset = null;
    }
    
    public final void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (!this.inEntityReference) {
            final int index = this.namespaceCount * 2;
            if (index == this.namespaces.length) {
                final String[] newNamespaces = new String[this.namespaces.length * 2];
                System.arraycopy(this.namespaces, 0, newNamespaces, 0, this.namespaces.length);
                this.namespaces = newNamespaces;
            }
            this.namespaces[index] = prefix;
            this.namespaces[index + 1] = uri;
            ++this.namespaceCount;
        }
    }
    
    public final void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    public final void startElement(final String namespaceURI, String localName, final String qName, final Attributes atts) throws SAXException {
        if (!this.inEntityReference) {
            if (localName == null || localName.trim().equals("")) {
                localName = qName.substring(qName.indexOf(58) + 1);
            }
            int idx = qName.indexOf(58);
            final String prefix = (idx == -1) ? "" : qName.substring(0, idx);
            final OMElement element = this.createOMElement(this.target, localName, namespaceURI, prefix, this.namespaces, this.namespaceCount);
            this.namespaceCount = 0;
            for (int j = atts.getLength(), i = 0; i < j; ++i) {
                final String attrQName = atts.getQName(i);
                if (!attrQName.startsWith("xmlns")) {
                    final String attrNamespaceURI = atts.getURI(i);
                    idx = attrQName.indexOf(58);
                    final String attrPrefix = (idx == -1) ? "" : attrQName.substring(0, idx);
                    OMNamespace ns;
                    if (attrNamespaceURI.length() > 0) {
                        ns = element.findNamespace(attrNamespaceURI, attrPrefix);
                        if (ns == null) {
                            throw new SAXException("Unbound namespace " + attrNamespaceURI);
                        }
                    }
                    else {
                        ns = null;
                    }
                    final OMAttribute attr = element.addAttribute(atts.getLocalName(i), atts.getValue(i), ns);
                    attr.setAttributeType(atts.getType(i));
                }
            }
            this.target = (OMContainer)element;
        }
    }
    
    public final void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (!this.inEntityReference) {
            this.completed((OMElement)this.target);
            this.target = ((OMNode)this.target).getParent();
        }
    }
    
    public final void startCDATA() throws SAXException {
        if (!this.inEntityReference) {
            this.textNodeType = 12;
        }
    }
    
    public final void endCDATA() throws SAXException {
        if (!this.inEntityReference) {
            this.textNodeType = 4;
        }
    }
    
    private void characterData(final char[] ch, final int start, final int length, final int nodeType) throws SAXException {
        if (!this.inEntityReference) {
            this.createOMText(this.target, new String(ch, start, length), nodeType);
        }
    }
    
    public final void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (!this.inEntityReference) {
            this.characterData(ch, start, length, this.textNodeType);
        }
    }
    
    public final void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (!this.inEntityReference) {
            this.characterData(ch, start, length, 6);
        }
    }
    
    public final void processingInstruction(final String piTarget, final String data) throws SAXException {
        if (!this.inEntityReference) {
            this.createOMProcessingInstruction(this.target, piTarget, data);
        }
    }
    
    public final void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (!this.inEntityReference) {
            this.createOMComment(this.target, new String(ch, start, length));
        }
    }
    
    public final void skippedEntity(final String name) throws SAXException {
        this.createOMEntityReference(this.target, name, null);
    }
    
    public final void startEntity(final String name) throws SAXException {
        if (this.inEntityReference) {
            ++this.entityReferenceDepth;
        }
        else if (name.equals("[dtd]")) {
            this.inExternalSubset = true;
        }
        else if (!this.expandEntityReferences) {
            this.createOMEntityReference(this.target, name, (this.entities == null) ? null : this.entities.get(name));
            this.inEntityReference = true;
            this.entityReferenceDepth = 1;
        }
    }
    
    public final void endEntity(final String name) throws SAXException {
        if (this.inEntityReference) {
            --this.entityReferenceDepth;
            if (this.entityReferenceDepth == 0) {
                this.inEntityReference = false;
            }
        }
        else if (name.equals("[dtd]")) {
            this.inExternalSubset = false;
        }
    }
    
    protected abstract OMContainer doStartDocument();
    
    protected abstract void doEndDocument();
    
    protected abstract void createOMDocType(final OMContainer p0, final String p1, final String p2, final String p3, final String p4);
    
    protected abstract OMElement createOMElement(final OMContainer p0, final String p1, final String p2, final String p3, final String[] p4, final int p5);
    
    protected abstract void completed(final OMElement p0);
    
    protected abstract void createOMText(final OMContainer p0, final String p1, final int p2);
    
    protected abstract void createOMProcessingInstruction(final OMContainer p0, final String p1, final String p2);
    
    protected abstract void createOMComment(final OMContainer p0, final String p1);
    
    protected abstract void createOMEntityReference(final OMContainer p0, final String p1, final String p2);
}
