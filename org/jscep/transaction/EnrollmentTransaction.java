package org.jscep.transaction;

import org.slf4j.LoggerFactory;
import org.bouncycastle.cms.CMSSignedData;
import org.jscep.message.MessageDecodingException;
import org.jscep.message.CertRep;
import org.jscep.transport.request.Request;
import org.jscep.transport.request.PkiOperationRequest;
import org.jscep.transport.response.PkiOperationResponseHandler;
import org.jscep.message.MessageEncodingException;
import org.jscep.message.PkiMessage;
import org.jscep.message.GetCertInitial;
import org.jscep.asn1.IssuerAndSubject;
import org.jscep.message.PkcsReq;
import java.io.IOException;
import org.jscep.util.CertificationRequestUtils;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkiMessageEncoder;
import org.jscep.transport.Transport;
import org.jscep.message.PkiRequest;
import org.slf4j.Logger;

public final class EnrollmentTransaction extends Transaction
{
    private static final Logger LOGGER;
    private static final NonceQueue QUEUE;
    private final TransactionId transId;
    private final PkiRequest<?> request;
    
    public EnrollmentTransaction(final Transport transport, final PkiMessageEncoder encoder, final PkiMessageDecoder decoder, final PKCS10CertificationRequest csr) throws TransactionException {
        super(transport, encoder, decoder);
        try {
            this.transId = TransactionId.createTransactionId(CertificationRequestUtils.getPublicKey(csr), "SHA-1");
        }
        catch (final IOException e) {
            throw new TransactionException(e);
        }
        this.request = new PkcsReq(this.transId, Nonce.nextNonce(), csr);
    }
    
    public EnrollmentTransaction(final Transport transport, final PkiMessageEncoder encoder, final PkiMessageDecoder decoder, final IssuerAndSubject ias, final TransactionId transId) {
        super(transport, encoder, decoder);
        this.transId = transId;
        this.request = new GetCertInitial(transId, Nonce.nextNonce(), ias);
    }
    
    @Override
    public TransactionId getId() {
        return this.transId;
    }
    
    @Override
    public State send() throws TransactionException {
        CMSSignedData signedData;
        try {
            signedData = this.encode(this.request);
        }
        catch (final MessageEncodingException e) {
            throw new TransactionException(e);
        }
        EnrollmentTransaction.LOGGER.debug("Sending {}", (Object)signedData);
        final PkiOperationResponseHandler handler = new PkiOperationResponseHandler();
        final CMSSignedData res = this.send(handler, new PkiOperationRequest(signedData));
        EnrollmentTransaction.LOGGER.debug("Received response {}", (Object)res);
        CertRep response;
        try {
            response = (CertRep)this.decode(res);
        }
        catch (final MessageDecodingException e2) {
            throw new TransactionException(e2);
        }
        this.validateExchange(this.request, response);
        EnrollmentTransaction.LOGGER.debug("Response: {}", (Object)response);
        if (response.getPkiStatus() == PkiStatus.FAILURE) {
            return this.failure(response.getFailInfo());
        }
        if (response.getPkiStatus() == PkiStatus.SUCCESS) {
            return this.success(this.extractCertStore(response));
        }
        return this.pending();
    }
    
    private void validateExchange(final PkiMessage<?> req, final CertRep res) throws TransactionException {
        EnrollmentTransaction.LOGGER.debug("Validating SCEP message exchange");
        if (!res.getTransactionId().equals(req.getTransactionId())) {
            throw new TransactionException("Transaction ID Mismatch");
        }
        EnrollmentTransaction.LOGGER.debug("Matched transaction IDs");
        if (!res.getRecipientNonce().equals(req.getSenderNonce())) {
            throw new InvalidNonceException(req.getSenderNonce(), res.getRecipientNonce());
        }
        EnrollmentTransaction.LOGGER.debug("Matched request senderNonce and response recipientNonce");
        if (res.getSenderNonce() == null) {
            EnrollmentTransaction.LOGGER.warn("Response senderNonce is null");
            return;
        }
        if (EnrollmentTransaction.QUEUE.contains(res.getSenderNonce())) {
            throw new InvalidNonceException(res.getSenderNonce());
        }
        EnrollmentTransaction.QUEUE.add(res.getSenderNonce());
        EnrollmentTransaction.LOGGER.debug("{} has not been encountered before", (Object)res.getSenderNonce());
        EnrollmentTransaction.LOGGER.debug("SCEP message exchange validated successfully");
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EnrollmentTransaction.class);
        QUEUE = new NonceQueue();
    }
}
