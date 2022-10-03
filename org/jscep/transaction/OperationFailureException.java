package org.jscep.transaction;

import net.jcip.annotations.Immutable;

@Immutable
public final class OperationFailureException extends TransactionException
{
    private static final long serialVersionUID = 326478648151473741L;
    private final FailInfo failInfo;
    
    public OperationFailureException(final FailInfo failInfo) {
        super("Operation failed due to " + failInfo);
        this.failInfo = failInfo;
    }
    
    public FailInfo getFailInfo() {
        return this.failInfo;
    }
}
