package org.jscep.transport.request;

public final class GetCaCertRequest extends Request
{
    private final String profile;
    
    public GetCaCertRequest(final String profile) {
        super(Operation.GET_CA_CERT);
        this.profile = profile;
    }
    
    public GetCaCertRequest() {
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
            return "GetCACert(" + this.profile + ")";
        }
        return "GetCACert";
    }
}
