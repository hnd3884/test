package javax.resource.cci;

import javax.resource.ResourceException;

public class ResourceWarning extends ResourceException
{
    public ResourceWarning(final String reason) {
        super(reason);
    }
    
    public ResourceWarning(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public void setLinkedWarning(final ResourceWarning linkedWarning) {
        this.setLinkedException(linkedWarning);
    }
    
    public ResourceWarning getLinkedWarning() {
        return (ResourceWarning)this.getLinkedException();
    }
}
