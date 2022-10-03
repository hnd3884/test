package javax.resource.spi;

import javax.resource.ResourceException;

public class LocalTransactionException extends ResourceException
{
    public LocalTransactionException(final String reason) {
        super(reason);
    }
    
    public LocalTransactionException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
