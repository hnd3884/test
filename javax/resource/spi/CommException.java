package javax.resource.spi;

import javax.resource.ResourceException;

public class CommException extends ResourceException
{
    public CommException(final String reason) {
        super(reason);
    }
    
    public CommException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
