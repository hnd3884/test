package org.bouncycastle.est;

public class CSRRequestResponse
{
    private final CSRAttributesResponse attributesResponse;
    private final Source source;
    
    public CSRRequestResponse(final CSRAttributesResponse attributesResponse, final Source source) {
        this.attributesResponse = attributesResponse;
        this.source = source;
    }
    
    public boolean hasAttributesResponse() {
        return this.attributesResponse != null;
    }
    
    public CSRAttributesResponse getAttributesResponse() {
        if (this.attributesResponse == null) {
            throw new IllegalStateException("Response has no CSRAttributesResponse.");
        }
        return this.attributesResponse;
    }
    
    public Object getSession() {
        return this.source.getSession();
    }
    
    public Source getSource() {
        return this.source;
    }
}
