package org.jscep.message;

import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.TransactionId;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class PkcsReq extends PkiRequest<PKCS10CertificationRequest>
{
    public PkcsReq(final TransactionId transId, final Nonce senderNonce, final PKCS10CertificationRequest messageData) {
        super(transId, MessageType.PKCS_REQ, senderNonce, messageData);
    }
}
