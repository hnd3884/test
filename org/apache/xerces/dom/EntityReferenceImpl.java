package org.apache.xerces.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;
import org.apache.xerces.util.URI;
import org.w3c.dom.Node;
import org.w3c.dom.EntityReference;

public class EntityReferenceImpl extends ParentNode implements EntityReference
{
    static final long serialVersionUID = -7381452955687102062L;
    protected String name;
    protected String baseURI;
    
    public EntityReferenceImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.name = name;
        this.isReadOnly(true);
        this.needsSyncChildren(true);
    }
    
    public short getNodeType() {
        return 5;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public Node cloneNode(final boolean b) {
        final EntityReferenceImpl entityReferenceImpl = (EntityReferenceImpl)super.cloneNode(b);
        entityReferenceImpl.setReadOnly(true, b);
        return entityReferenceImpl;
    }
    
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI == null) {
            final DocumentType doctype;
            final NamedNodeMap entities;
            if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
                final EntityImpl entityImpl = (EntityImpl)entities.getNamedItem(this.getNodeName());
                if (entityImpl != null) {
                    return entityImpl.getBaseURI();
                }
            }
        }
        else if (this.baseURI != null && this.baseURI.length() != 0) {
            try {
                return new URI(this.baseURI).toString();
            }
            catch (final URI.MalformedURIException ex) {
                return null;
            }
        }
        return this.baseURI;
    }
    
    public void setBaseURI(final String baseURI) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = baseURI;
    }
    
    protected String getEntityRefValue() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.firstChild == null) {
            return "";
        }
        String s;
        if (this.firstChild.getNodeType() == 5) {
            s = ((EntityReferenceImpl)this.firstChild).getEntityRefValue();
        }
        else {
            if (this.firstChild.getNodeType() != 3) {
                return null;
            }
            s = this.firstChild.getNodeValue();
        }
        if (this.firstChild.nextSibling == null) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s);
        for (ChildNode childNode = this.firstChild.nextSibling; childNode != null; childNode = childNode.nextSibling) {
            String s2;
            if (childNode.getNodeType() == 5) {
                s2 = ((EntityReferenceImpl)childNode).getEntityRefValue();
            }
            else {
                if (childNode.getNodeType() != 3) {
                    return null;
                }
                s2 = childNode.getNodeValue();
            }
            sb.append(s2);
        }
        return sb.toString();
    }
    
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        final DocumentType doctype;
        final NamedNodeMap entities;
        if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
            final EntityImpl entityImpl = (EntityImpl)entities.getNamedItem(this.getNodeName());
            if (entityImpl == null) {
                return;
            }
            this.isReadOnly(false);
            for (Node node = entityImpl.getFirstChild(); node != null; node = node.getNextSibling()) {
                this.insertBefore(node.cloneNode(true), null);
            }
            this.setReadOnly(true, true);
        }
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (b2) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
                childNode.setReadOnly(b, true);
            }
        }
        this.isReadOnly(b);
    }
}
