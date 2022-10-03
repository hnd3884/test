package org.apache.xerces.dom;

import org.w3c.dom.Node;
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
    
    public EntityImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.name = name;
        this.isReadOnly(true);
    }
    
    public short getNodeType() {
        return 6;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public Node cloneNode(final boolean b) {
        final EntityImpl entityImpl = (EntityImpl)super.cloneNode(b);
        entityImpl.setReadOnly(true, b);
        return entityImpl;
    }
    
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicId;
    }
    
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemId;
    }
    
    public String getXmlVersion() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.version;
    }
    
    public String getXmlEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.encoding;
    }
    
    public String getNotationName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.notationName;
    }
    
    public void setPublicId(final String publicId) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = publicId;
    }
    
    public void setXmlEncoding(final String encoding) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.encoding = encoding;
    }
    
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
    
    public void setXmlVersion(final String version) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.version = version;
    }
    
    public void setSystemId(final String systemId) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = systemId;
    }
    
    public void setNotationName(final String notationName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.notationName = notationName;
    }
    
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return (this.baseURI != null) ? this.baseURI : ((CoreDocumentImpl)this.getOwnerDocument()).getBaseURI();
    }
    
    public void setBaseURI(final String baseURI) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = baseURI;
    }
}
