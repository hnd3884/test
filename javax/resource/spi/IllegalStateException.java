package javax.resource.spi;

import javax.resource.ResourceException;

public class IllegalStateException extends ResourceException
{
    public IllegalStateException(final String reason) {
        super(reason);
    }
    
    public IllegalStateException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
