package javax.resource.spi;

import javax.resource.ResourceException;

public class ApplicationServerInternalException extends ResourceException
{
    public ApplicationServerInternalException(final String reason) {
        super(reason);
    }
    
    public ApplicationServerInternalException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
