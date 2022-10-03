package org.jscep.message;

import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.TransactionId;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

public class GetCert extends PkiRequest<IssuerAndSerialNumber>
{
    public GetCert(final TransactionId transId, final Nonce senderNonce, final IssuerAndSerialNumber messageData) {
        super(transId, MessageType.GET_CERT, senderNonce, messageData);
    }
}
