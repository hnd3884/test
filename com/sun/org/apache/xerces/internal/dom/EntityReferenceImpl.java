package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;
import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.Node;
import org.w3c.dom.EntityReference;

public class EntityReferenceImpl extends ParentNode implements EntityReference
{
    static final long serialVersionUID = -7381452955687102062L;
    protected String name;
    protected String baseURI;
    
    public EntityReferenceImpl(final CoreDocumentImpl ownerDoc, final String name) {
        super(ownerDoc);
        this.name = name;
        this.isReadOnly(true);
        this.needsSyncChildren(true);
    }
    
    @Override
    public short getNodeType() {
        return 5;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final EntityReferenceImpl er = (EntityReferenceImpl)super.cloneNode(deep);
        er.setReadOnly(true, deep);
        return er;
    }
    
    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI == null) {
            final DocumentType doctype;
            final NamedNodeMap entities;
            if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
                final EntityImpl entDef = (EntityImpl)entities.getNamedItem(this.getNodeName());
                if (entDef != null) {
                    return entDef.getBaseURI();
                }
            }
        }
        else if (this.baseURI != null && this.baseURI.length() != 0) {
            try {
                return new URI(this.baseURI).toString();
            }
            catch (final URI.MalformedURIException e) {
                return null;
            }
        }
        return this.baseURI;
    }
    
    public void setBaseURI(final String uri) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = uri;
    }
    
    protected String getEntityRefValue() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        String value = "";
        if (this.firstChild == null) {
            return "";
        }
        if (this.firstChild.getNodeType() == 5) {
            value = ((EntityReferenceImpl)this.firstChild).getEntityRefValue();
        }
        else {
            if (this.firstChild.getNodeType() != 3) {
                return null;
            }
            value = this.firstChild.getNodeValue();
        }
        if (this.firstChild.nextSibling == null) {
            return value;
        }
        final StringBuffer buff = new StringBuffer(value);
        for (ChildNode next = this.firstChild.nextSibling; next != null; next = next.nextSibling) {
            if (next.getNodeType() == 5) {
                value = ((EntityReferenceImpl)next).getEntityRefValue();
            }
            else {
                if (next.getNodeType() != 3) {
                    return null;
                }
                value = next.getNodeValue();
            }
            buff.append(value);
        }
        return buff.toString();
    }
    
    @Override
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        final DocumentType doctype;
        final NamedNodeMap entities;
        if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
            final EntityImpl entDef = (EntityImpl)entities.getNamedItem(this.getNodeName());
            if (entDef == null) {
                return;
            }
            this.isReadOnly(false);
            for (Node defkid = entDef.getFirstChild(); defkid != null; defkid = defkid.getNextSibling()) {
                final Node newkid = defkid.cloneNode(true);
                this.insertBefore(newkid, null);
            }
            this.setReadOnly(true, true);
        }
    }
    
    @Override
    public void setReadOnly(final boolean readOnly, final boolean deep) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (deep) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            for (ChildNode mykid = this.firstChild; mykid != null; mykid = mykid.nextSibling) {
                mykid.setReadOnly(readOnly, true);
            }
        }
        this.isReadOnly(readOnly);
    }
}
