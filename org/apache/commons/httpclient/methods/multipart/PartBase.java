package org.apache.commons.httpclient.methods.multipart;

public abstract class PartBase extends Part
{
    private String name;
    private String contentType;
    private String charSet;
    private String transferEncoding;
    
    public PartBase(final String name, final String contentType, final String charSet, final String transferEncoding) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
        this.contentType = contentType;
        this.charSet = charSet;
        this.transferEncoding = transferEncoding;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public String getCharSet() {
        return this.charSet;
    }
    
    public String getTransferEncoding() {
        return this.transferEncoding;
    }
    
    public void setCharSet(final String charSet) {
        this.charSet = charSet;
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public void setName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
    }
    
    public void setTransferEncoding(final String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }
}
