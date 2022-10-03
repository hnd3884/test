package org.jscep.message;

import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.TransactionId;
import org.jscep.asn1.IssuerAndSubject;

public class GetCertInitial extends PkiRequest<IssuerAndSubject>
{
    public GetCertInitial(final TransactionId transId, final Nonce senderNonce, final IssuerAndSubject messageData) {
        super(transId, MessageType.GET_CERT_INITIAL, senderNonce, messageData);
    }
}
