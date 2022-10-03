package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public class NotationImpl extends NodeImpl implements Notation
{
    static final long serialVersionUID = -764632195890658402L;
    protected String name;
    protected String publicId;
    protected String systemId;
    protected String baseURI;
    
    public NotationImpl(final CoreDocumentImpl ownerDoc, final String name) {
        super(ownerDoc);
        this.name = name;
    }
    
    @Override
    public short getNodeType() {
        return 12;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
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
    
    public void setPublicId(final String id) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = id;
    }
    
    public void setSystemId(final String id) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = id;
    }
    
    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI != null && this.baseURI.length() != 0) {
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
}
