package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;

public class EntityImpl extends ParentNode implements Entity
{
    static final long serialVersionUID = -3575760943444303423L;
    protected String name;
    protected String publicId;
    protected String systemId;
    protected String encoding;
    protected String inputEncoding;
    protected String version;
    protected String notationName;
    protected String baseURI;
    
    public EntityImpl(final CoreDocumentImpl ownerDoc, final String name) {
        super(ownerDoc);
        this.name = name;
        this.isReadOnly(true);
    }
    
    @Override
    public short getNodeType() {
        return 6;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    @Override
    public void setNodeValue(final String x) throws DOMException {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final EntityImpl newentity = (EntityImpl)super.cloneNode(deep);
        newentity.setReadOnly(true, deep);
        return newentity;
    }
    
    @Override
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicId;
    }
    
    @Override
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemId;
    }
    
    @Override
    public String getXmlVersion() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.version;
    }
    
    @Override
    public String getXmlEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.encoding;
    }
    
    @Override
    public String getNotationName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.notationName;
    }
    
    public void setPublicId(final String id) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = id;
    }
    
    public void setXmlEncoding(final String value) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.encoding = value;
    }
    
    @Override
    public String getInputEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.inputEncoding;
    }
    
    public void setInputEncoding(final String inputEncoding) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.inputEncoding = inputEncoding;
    }
    
    public void setXmlVersion(final String value) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.version = value;
    }
    
    public void setSystemId(final String id) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = id;
    }
    
    public void setNotationName(final String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.notationName = name;
    }
    
    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return (this.baseURI != null) ? this.baseURI : ((CoreDocumentImpl)this.getOwnerDocument()).getBaseURI();
    }
    
    public void setBaseURI(final String uri) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = uri;
    }
}
