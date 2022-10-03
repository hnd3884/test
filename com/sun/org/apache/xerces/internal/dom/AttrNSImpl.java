package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

public class AttrNSImpl extends AttrImpl
{
    static final long serialVersionUID = -781906615369795414L;
    static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    protected String namespaceURI;
    protected String localName;
    
    public AttrNSImpl() {
    }
    
    protected AttrNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName) {
        super(ownerDocument, qualifiedName);
        this.setName(namespaceURI, qualifiedName);
    }
    
    private void setName(final String namespaceURI, final String qname) {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        this.namespaceURI = namespaceURI;
        if (namespaceURI != null) {
            this.namespaceURI = ((namespaceURI.length() == 0) ? null : namespaceURI);
        }
        final int colon1 = qname.indexOf(58);
        final int colon2 = qname.lastIndexOf(58);
        ownerDocument.checkNamespaceWF(qname, colon1, colon2);
        if (colon1 < 0) {
            this.localName = qname;
            if (ownerDocument.errorChecking) {
                ownerDocument.checkQName(null, this.localName);
                if ((qname.equals("xmlns") && (namespaceURI == null || !namespaceURI.equals(NamespaceContext.XMLNS_URI))) || (namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI) && !qname.equals("xmlns"))) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
            }
        }
        else {
            final String prefix = qname.substring(0, colon1);
            ownerDocument.checkQName(prefix, this.localName = qname.substring(colon2 + 1));
            ownerDocument.checkDOMNSErr(prefix, namespaceURI);
        }
    }
    
    public AttrNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) {
        super(ownerDocument, qualifiedName);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }
    
    protected AttrNSImpl(final CoreDocumentImpl ownerDocument, final String value) {
        super(ownerDocument, value);
    }
    
    void rename(final String namespaceURI, final String qualifiedName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setName(namespaceURI, this.name = qualifiedName);
    }
    
    public void setValues(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) {
        super.textNode = null;
        super.flags = 0;
        this.isSpecified(true);
        this.hasStringValue(true);
        super.setOwnerDocument(ownerDocument);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        super.name = qualifiedName;
        super.value = null;
    }
    
    @Override
    public String getNamespaceURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.namespaceURI;
    }
    
    @Override
    public String getPrefix() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int index = this.name.indexOf(58);
        return (index < 0) ? null : this.name.substring(0, index);
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument().errorChecking) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (prefix != null && prefix.length() != 0) {
                if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument().isXML11Version())) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                    throw new DOMException((short)5, msg);
                }
                if (this.namespaceURI == null || prefix.indexOf(58) >= 0) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
                if (prefix.equals("xmlns")) {
                    if (!this.namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                        throw new DOMException((short)14, msg);
                    }
                }
                else if (prefix.equals("xml")) {
                    if (!this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                        throw new DOMException((short)14, msg);
                    }
                }
                else if (this.name.equals("xmlns")) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
            }
        }
        if (prefix != null && prefix.length() != 0) {
            this.name = prefix + ":" + this.localName;
        }
        else {
            this.name = this.localName;
        }
    }
    
    @Override
    public String getLocalName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.localName;
    }
    
    @Override
    public String getTypeName() {
        if (this.type == null) {
            return null;
        }
        if (this.type instanceof XSSimpleTypeDecl) {
            return ((XSSimpleTypeDecl)this.type).getName();
        }
        return (String)this.type;
    }
    
    @Override
    public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
        return this.type != null && this.type instanceof XSSimpleTypeDecl && ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
    }
    
    @Override
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
