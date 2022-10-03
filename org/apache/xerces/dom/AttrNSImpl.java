package org.apache.xerces.dom;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.w3c.dom.DOMException;
import org.apache.xerces.xni.NamespaceContext;

public class AttrNSImpl extends AttrImpl
{
    static final long serialVersionUID = -781906615369795414L;
    static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    protected String namespaceURI;
    protected String localName;
    
    public AttrNSImpl() {
    }
    
    protected AttrNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String s2) {
        super(coreDocumentImpl, s2);
        this.setName(s, s2);
    }
    
    private void setName(final String namespaceURI, final String localName) {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        this.namespaceURI = namespaceURI;
        if (namespaceURI != null) {
            this.namespaceURI = ((namespaceURI.length() == 0) ? null : namespaceURI);
        }
        final int index = localName.indexOf(58);
        final int lastIndex = localName.lastIndexOf(58);
        ownerDocument.checkNamespaceWF(localName, index, lastIndex);
        if (index < 0) {
            this.localName = localName;
            if (ownerDocument.errorChecking) {
                ownerDocument.checkQName(null, this.localName);
                if ((localName.equals("xmlns") && (namespaceURI == null || !namespaceURI.equals(NamespaceContext.XMLNS_URI))) || (namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI) && !localName.equals("xmlns"))) {
                    throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
                }
            }
        }
        else {
            final String substring = localName.substring(0, index);
            ownerDocument.checkQName(substring, this.localName = localName.substring(lastIndex + 1));
            ownerDocument.checkDOMNSErr(substring, namespaceURI);
        }
    }
    
    public AttrNSImpl(final CoreDocumentImpl coreDocumentImpl, final String namespaceURI, final String s, final String localName) {
        super(coreDocumentImpl, s);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }
    
    protected AttrNSImpl(final CoreDocumentImpl coreDocumentImpl, final String s) {
        super(coreDocumentImpl, s);
    }
    
    void rename(final String s, final String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setName(s, this.name = name);
    }
    
    public String getNamespaceURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.namespaceURI;
    }
    
    public String getPrefix() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int index = this.name.indexOf(58);
        return (index < 0) ? null : this.name.substring(0, index);
    }
    
    public void setPrefix(final String s) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument().errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (s != null && s.length() != 0) {
                if (!CoreDocumentImpl.isXMLName(s, this.ownerDocument().isXML11Version())) {
                    throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
                }
                if (this.namespaceURI == null || s.indexOf(58) >= 0) {
                    throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
                }
                if (s.equals("xmlns")) {
                    if (!this.namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                        throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
                    }
                }
                else if (s.equals("xml")) {
                    if (!this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                        throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
                    }
                }
                else if (this.name.equals("xmlns")) {
                    throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
                }
            }
        }
        if (s != null && s.length() != 0) {
            this.name = s + ":" + this.localName;
        }
        else {
            this.name = this.localName;
        }
    }
    
    public String getLocalName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.localName;
    }
    
    public String getTypeName() {
        if (this.type == null) {
            return null;
        }
        if (this.type instanceof XSSimpleTypeDecl) {
            return ((XSSimpleTypeDecl)this.type).getName();
        }
        return (String)this.type;
    }
    
    public boolean isDerivedFrom(final String s, final String s2, final int n) {
        return this.type != null && this.type instanceof XSSimpleTypeDecl && ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(s, s2, n);
    }
    
    public String getTypeNamespace() {
        if (this.type == null) {
            return null;
        }
        if (this.type instanceof XSSimpleTypeDecl) {
            return ((XSSimpleTypeDecl)this.type).getNamespace();
        }
        return "http://www.w3.org/TR/REC-xml";
    }
}
