package javax.resource.spi;

import javax.resource.ResourceException;

public class ResourceAdapterInternalException extends ResourceException
{
    public ResourceAdapterInternalException(final String reason) {
        super(reason);
    }
    
    public ResourceAdapterInternalException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
