package org.jscep.transport.request;

public final class GetNextCaCertRequest extends Request
{
    private final String profile;
    
    public GetNextCaCertRequest(final String profile) {
        super(Operation.GET_NEXT_CA_CERT);
        this.profile = profile;
    }
    
    public GetNextCaCertRequest() {
        this((String)null);
    }
    
    @Override
    public String getMessage() {
        if (this.profile == null) {
            return "";
        }
        return this.profile;
    }
    
    @Override
    public String toString() {
        if (this.profile != null) {
            return "GetNextCACert(" + this.profile + ")";
        }
        return "GetNextCACert";
    }
}
