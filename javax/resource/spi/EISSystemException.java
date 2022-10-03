package javax.resource.spi;

import javax.resource.ResourceException;

public class EISSystemException extends ResourceException
{
    public EISSystemException(final String reason) {
        super(reason);
    }
    
    public EISSystemException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
