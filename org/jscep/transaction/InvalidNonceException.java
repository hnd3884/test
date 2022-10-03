package org.jscep.transaction;

import net.jcip.annotations.Immutable;

@Immutable
public class InvalidNonceException extends TransactionException
{
    private static final String MISMATCH = "Nonce mismatch.  Sent: %s. Receieved: %s";
    private static final String REPLAY = "Nonce encountered before: %s";
    private static final long serialVersionUID = 3875364340108674893L;
    
    public InvalidNonceException(final Nonce sent, final Nonce recd) {
        super(String.format("Nonce mismatch.  Sent: %s. Receieved: %s", sent, recd));
    }
    
    public InvalidNonceException(final Nonce nonce) {
        super(String.format("Nonce encountered before: %s", nonce));
    }
}
