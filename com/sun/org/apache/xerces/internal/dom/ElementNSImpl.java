package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class ElementNSImpl extends ElementImpl
{
    static final long serialVersionUID = -9142310625494392642L;
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    protected String namespaceURI;
    protected String localName;
    transient XSTypeDefinition type;
    
    protected ElementNSImpl() {
    }
    
    protected ElementNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName) throws DOMException {
        super(ownerDocument, qualifiedName);
        this.setName(namespaceURI, qualifiedName);
    }
    
    private void setName(final String namespaceURI, final String qname) {
        this.namespaceURI = namespaceURI;
        if (namespaceURI != null) {
            this.namespaceURI = ((namespaceURI.length() == 0) ? null : namespaceURI);
        }
        if (qname == null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, msg);
        }
        final int colon1 = qname.indexOf(58);
        final int colon2 = qname.lastIndexOf(58);
        this.ownerDocument.checkNamespaceWF(qname, colon1, colon2);
        if (colon1 < 0) {
            this.localName = qname;
            if (this.ownerDocument.errorChecking) {
                this.ownerDocument.checkQName(null, this.localName);
                if ((qname.equals("xmlns") && (namespaceURI == null || !namespaceURI.equals(NamespaceContext.XMLNS_URI))) || (namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI) && !qname.equals("xmlns"))) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
            }
        }
        else {
            final String prefix = qname.substring(0, colon1);
            this.localName = qname.substring(colon2 + 1);
            if (this.ownerDocument.errorChecking) {
                if (namespaceURI == null || (prefix.equals("xml") && !namespaceURI.equals(NamespaceContext.XML_URI))) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
                this.ownerDocument.checkQName(prefix, this.localName);
                this.ownerDocument.checkDOMNSErr(prefix, namespaceURI);
            }
        }
    }
    
    protected ElementNSImpl(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) throws DOMException {
        super(ownerDocument, qualifiedName);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }
    
    protected ElementNSImpl(final CoreDocumentImpl ownerDocument, final String value) {
        super(ownerDocument, value);
    }
    
    void rename(final String namespaceURI, final String qualifiedName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setName(namespaceURI, this.name = qualifiedName);
        this.reconcileDefaultAttributes();
    }
    
    protected void setValues(final CoreDocumentImpl ownerDocument, final String namespaceURI, final String qualifiedName, final String localName) {
        this.firstChild = null;
        this.previousSibling = null;
        this.nextSibling = null;
        this.fNodeListCache = null;
        this.attributes = null;
        super.flags = 0;
        this.setOwnerDocument(ownerDocument);
        this.needsSyncData(true);
        super.name = qualifiedName;
        this.localName = localName;
        this.namespaceURI = namespaceURI;
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
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (prefix != null && prefix.length() != 0) {
                if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument.isXML11Version())) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                    throw new DOMException((short)5, msg);
                }
                if (this.namespaceURI == null || prefix.indexOf(58) >= 0) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException((short)14, msg);
                }
                if (prefix.equals("xml") && !this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
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
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes != null) {
            final Attr attrNode = (Attr)this.attributes.getNamedItemNS("http://www.w3.org/XML/1998/namespace", "base");
            if (attrNode != null) {
                String uri = attrNode.getNodeValue();
                if (uri.length() != 0) {
                    try {
                        uri = new URI(uri).toString();
                    }
                    catch (final URI.MalformedURIException e) {
                        final NodeImpl parentOrOwner = (this.parentNode() != null) ? this.parentNode() : this.ownerNode;
                        final String parentBaseURI = (parentOrOwner != null) ? parentOrOwner.getBaseURI() : null;
                        if (parentBaseURI != null) {
                            try {
                                uri = new URI(new URI(parentBaseURI), uri).toString();
                            }
                            catch (final URI.MalformedURIException ex) {
                                return null;
                            }
                            return uri;
                        }
                        return null;
                    }
                    return uri;
                }
            }
        }
        final String parentElementBaseURI = (this.parentNode() != null) ? this.parentNode().getBaseURI() : null;
        if (parentElementBaseURI != null) {
            try {
                return new URI(parentElementBaseURI).toString();
            }
            catch (final URI.MalformedURIException e2) {
                return null;
            }
        }
        final String baseURI = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
        if (baseURI != null) {
            try {
                return new URI(baseURI).toString();
            }
            catch (final URI.MalformedURIException e) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public String getTypeName() {
        if (this.type != null) {
            if (this.type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl)this.type).getTypeName();
            }
            if (this.type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl)this.type).getTypeName();
            }
        }
        return null;
    }
    
    @Override
    public String getTypeNamespace() {
        if (this.type != null) {
            return this.type.getNamespace();
        }
        return null;
    }
    
    @Override
    public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.type != null) {
            if (this.type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
            }
            if (this.type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
            }
        }
        return false;
    }
    
    public void setType(final XSTypeDefinition type) {
        this.type = type;
    }
}
