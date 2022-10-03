package org.apache.xerces.dom;

import org.apache.xerces.util.URI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public class NotationImpl extends NodeImpl implements Notation
{
    static final long serialVersionUID = -764632195890658402L;
    protected String name;
    protected String publicId;
    protected String systemId;
    protected String baseURI;
    
    public NotationImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.name = name;
    }
    
    public short getNodeType() {
        return 12;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
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
    
    public void setPublicId(final String publicId) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = publicId;
    }
    
    public void setSystemId(final String systemId) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = systemId;
    }
    
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI != null && this.baseURI.length() != 0) {
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
}
