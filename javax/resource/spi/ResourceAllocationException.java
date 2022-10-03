package javax.resource.spi;

import javax.resource.ResourceException;

public class ResourceAllocationException extends ResourceException
{
    public ResourceAllocationException(final String reason) {
        super(reason);
    }
    
    public ResourceAllocationException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
