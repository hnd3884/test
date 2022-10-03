package org.jscep.message;

import org.jscep.transaction.MessageType;
import org.jscep.transaction.TransactionId;
import org.jscep.transaction.FailInfo;
import org.jscep.transaction.PkiStatus;
import org.jscep.transaction.Nonce;
import org.bouncycastle.cms.CMSSignedData;

public final class CertRep extends PkiMessage<CMSSignedData>
{
    private final Nonce recipientNonce;
    private final PkiStatus pkiStatus;
    private final FailInfo failInfo;
    
    public CertRep(final TransactionId transId, final Nonce senderNonce, final Nonce recipientNonce, final CMSSignedData messageData) {
        super(transId, MessageType.CERT_REP, senderNonce, messageData);
        this.recipientNonce = recipientNonce;
        this.pkiStatus = PkiStatus.SUCCESS;
        this.failInfo = null;
    }
    
    public CertRep(final TransactionId transId, final Nonce senderNonce, final Nonce recipientNonce, final FailInfo failInfo) {
        super(transId, MessageType.CERT_REP, senderNonce, null);
        this.recipientNonce = recipientNonce;
        this.pkiStatus = PkiStatus.FAILURE;
        this.failInfo = failInfo;
    }
    
    public CertRep(final TransactionId transId, final Nonce senderNonce, final Nonce recipientNonce) {
        super(transId, MessageType.CERT_REP, senderNonce, null);
        this.recipientNonce = recipientNonce;
        this.pkiStatus = PkiStatus.PENDING;
        this.failInfo = null;
    }
    
    public Nonce getRecipientNonce() {
        return this.recipientNonce;
    }
    
    public PkiStatus getPkiStatus() {
        return this.pkiStatus;
    }
    
    public FailInfo getFailInfo() {
        if (this.pkiStatus != PkiStatus.FAILURE) {
            throw new IllegalStateException();
        }
        return this.failInfo;
    }
    
    @Override
    public CMSSignedData getMessageData() {
        if (this.pkiStatus != PkiStatus.SUCCESS) {
            throw new IllegalStateException();
        }
        return super.getMessageData();
    }
}
