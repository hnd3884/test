package org.jscep.transport.request;

public final class GetCaCapsRequest extends Request
{
    private final String profile;
    
    public GetCaCapsRequest() {
        this((String)null);
    }
    
    public GetCaCapsRequest(final String profile) {
        super(Operation.GET_CA_CAPS);
        this.profile = profile;
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
            return "GetCACaps(" + this.profile + ")";
        }
        return "GetCACaps";
    }
}
