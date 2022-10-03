package org.jscep.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.MessageType;
import org.jscep.transaction.TransactionId;

public abstract class PkiMessage<T>
{
    private final TransactionId transId;
    private final MessageType messageType;
    private final Nonce senderNonce;
    private final T messageData;
    
    public PkiMessage(final TransactionId transId, final MessageType messageType, final Nonce senderNonce, final T messageData) {
        this.transId = transId;
        this.messageType = messageType;
        this.senderNonce = senderNonce;
        this.messageData = messageData;
    }
    
    public final TransactionId getTransactionId() {
        return this.transId;
    }
    
    public final MessageType getMessageType() {
        return this.messageType;
    }
    
    public final Nonce getSenderNonce() {
        return this.senderNonce;
    }
    
    public T getMessageData() {
        return this.messageData;
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, new String[] { "messageData" });
    }
    
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals((Object)this, obj, new String[] { "messageData" });
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}
