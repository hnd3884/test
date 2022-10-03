package org.jscep.message;

import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.TransactionId;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

public class GetCrl extends PkiRequest<IssuerAndSerialNumber>
{
    public GetCrl(final TransactionId transId, final Nonce senderNonce, final IssuerAndSerialNumber messageData) {
        super(transId, MessageType.GET_CRL, senderNonce, messageData);
    }
}
