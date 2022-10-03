package org.jscep.message;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.MessageType;
import org.jscep.transaction.TransactionId;

public abstract class PkiRequest<T> extends PkiMessage<T>
{
    public PkiRequest(final TransactionId transId, final MessageType messageType, final Nonce senderNonce, final T messageData) {
        super(transId, messageType, senderNonce, messageData);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals((Object)this, obj);
    }
}
