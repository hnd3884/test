package org.apache.xerces.dom;

import java.io.Reader;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;

public class DOMInputImpl implements LSInput
{
    protected String fPublicId;
    protected String fSystemId;
    protected String fBaseSystemId;
    protected InputStream fByteStream;
    protected Reader fCharStream;
    protected String fData;
    protected String fEncoding;
    protected boolean fCertifiedText;
    
    public DOMInputImpl() {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
    }
    
    public DOMInputImpl(final String fPublicId, final String fSystemId, final String fBaseSystemId) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
    }
    
    public DOMInputImpl(final String fPublicId, final String fSystemId, final String fBaseSystemId, final InputStream fByteStream, final String fEncoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fByteStream = fByteStream;
        this.fEncoding = fEncoding;
    }
    
    public DOMInputImpl(final String fPublicId, final String fSystemId, final String fBaseSystemId, final Reader fCharStream, final String fEncoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fCharStream = fCharStream;
        this.fEncoding = fEncoding;
    }
    
    public DOMInputImpl(final String fPublicId, final String fSystemId, final String fBaseSystemId, final String fData, final String fEncoding) {
        this.fPublicId = null;
        this.fSystemId = null;
        this.fBaseSystemId = null;
        this.fByteStream = null;
        this.fCharStream = null;
        this.fData = null;
        this.fEncoding = null;
        this.fCertifiedText = false;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fBaseSystemId = fBaseSystemId;
        this.fData = fData;
        this.fEncoding = fEncoding;
    }
    
    public InputStream getByteStream() {
        return this.fByteStream;
    }
    
    public void setByteStream(final InputStream fByteStream) {
        this.fByteStream = fByteStream;
    }
    
    public Reader getCharacterStream() {
        return this.fCharStream;
    }
    
    public void setCharacterStream(final Reader fCharStream) {
        this.fCharStream = fCharStream;
    }
    
    public String getStringData() {
        return this.fData;
    }
    
    public void setStringData(final String fData) {
        this.fData = fData;
    }
    
    public String getEncoding() {
        return this.fEncoding;
    }
    
    public void setEncoding(final String fEncoding) {
        this.fEncoding = fEncoding;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public void setPublicId(final String fPublicId) {
        this.fPublicId = fPublicId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public void setSystemId(final String fSystemId) {
        this.fSystemId = fSystemId;
    }
    
    public String getBaseURI() {
        return this.fBaseSystemId;
    }
    
    public void setBaseURI(final String fBaseSystemId) {
        this.fBaseSystemId = fBaseSystemId;
    }
    
    public boolean getCertifiedText() {
        return this.fCertifiedText;
    }
    
    public void setCertifiedText(final boolean fCertifiedText) {
        this.fCertifiedText = fCertifiedText;
    }
}
