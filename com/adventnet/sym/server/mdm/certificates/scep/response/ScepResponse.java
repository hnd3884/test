package com.adventnet.sym.server.mdm.certificates.scep.response;

public class ScepResponse
{
    private String contentType;
    private byte[] response;
    
    public void setContentType(final String mimeType) {
        this.contentType = mimeType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setResponse(final byte[] response) {
        this.response = response;
    }
    
    public byte[] getResponse() {
        return this.response;
    }
}
