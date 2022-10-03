package org.jscep.transaction;

import org.bouncycastle.cms.CMSSignedData;
import org.jscep.message.MessageDecodingException;
import org.jscep.message.CertRep;
import org.jscep.transport.request.Request;
import org.jscep.transport.request.PkiOperationRequest;
import org.jscep.message.MessageEncodingException;
import org.jscep.message.PkiMessage;
import org.jscep.transport.response.PkiOperationResponseHandler;
import org.jscep.message.GetCrl;
import org.jscep.message.GetCert;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkiMessageEncoder;
import org.jscep.transport.Transport;
import org.bouncycastle.asn1.ASN1Encodable;
import org.jscep.message.PkiRequest;

public final class NonEnrollmentTransaction extends Transaction
{
    private final TransactionId transId;
    private final PkiRequest<? extends ASN1Encodable> request;
    
    public NonEnrollmentTransaction(final Transport transport, final PkiMessageEncoder encoder, final PkiMessageDecoder decoder, final IssuerAndSerialNumber iasn, final MessageType msgType) {
        super(transport, encoder, decoder);
        this.transId = TransactionId.createTransactionId();
        if (msgType == MessageType.GET_CERT) {
            this.request = (PkiRequest<? extends ASN1Encodable>)new GetCert(this.transId, Nonce.nextNonce(), iasn);
        }
        else {
            if (msgType != MessageType.GET_CRL) {
                throw new IllegalArgumentException(msgType.toString());
            }
            this.request = (PkiRequest<? extends ASN1Encodable>)new GetCrl(this.transId, Nonce.nextNonce(), iasn);
        }
    }
    
    @Override
    public TransactionId getId() {
        return this.transId;
    }
    
    @Override
    public State send() throws TransactionException {
        final PkiOperationResponseHandler handler = new PkiOperationResponseHandler();
        CMSSignedData signedData;
        try {
            signedData = this.encode(this.request);
        }
        catch (final MessageEncodingException e) {
            throw new TransactionException(e);
        }
        final CMSSignedData res = this.send(handler, new PkiOperationRequest(signedData));
        CertRep response;
        try {
            response = (CertRep)this.decode(res);
        }
        catch (final MessageDecodingException e2) {
            throw new TransactionException(e2);
        }
        if (response.getPkiStatus() == PkiStatus.FAILURE) {
            return this.failure(response.getFailInfo());
        }
        if (response.getPkiStatus() == PkiStatus.SUCCESS) {
            return this.success(this.extractCertStore(response));
        }
        throw new TransactionException("Invalid Response");
    }
}
