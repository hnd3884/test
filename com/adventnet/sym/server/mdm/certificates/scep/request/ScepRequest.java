package com.adventnet.sym.server.mdm.certificates.scep.request;

import java.util.Collection;
import org.bouncycastle.util.Store;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.jscep.message.PkiMessage;
import org.bouncycastle.cms.CMSException;
import org.jscep.message.MessageDecodingException;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.jscep.transaction.OperationFailureException;
import org.jscep.transaction.FailInfo;
import org.jscep.util.CertificationRequestUtils;
import org.jscep.message.PkiMessageDecoder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cms.CMSSignedData;
import java.util.logging.Level;
import org.jscep.message.PkcsPkiEnvelopeDecoder;
import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.TransactionId;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

class ScepRequest
{
    private final Logger logger;
    private final X509Certificate signingCertificateUsedByRequester;
    private final String scepEnrollmentPasscode;
    private final PKCS10CertificationRequest certificateSigningRequest;
    private final TransactionId transactionId;
    private final Nonce senderNonce;
    private final Nonce recipientNonce;
    private final MessageType messageType;
    
    public ScepRequest(final MdmScepRequest mdmScepRequest, final PkcsPkiEnvelopeDecoder enrollmentRequestDecoder) throws OperationFailureException {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        try {
            this.logger.log(Level.INFO, "ScepRequest: Constructing the SCEP request for Enrollment request: {0}, Customer: {1}", new Object[] { mdmScepRequest.getEnrollmentRequestId(), mdmScepRequest.getCustomerId() });
            final byte[] bytes = mdmScepRequest.getPkiMessage();
            final CMSSignedData signedData = new CMSSignedData(bytes);
            this.signingCertificateUsedByRequester = this.getCertificateUsedForSigningData(signedData, null);
            this.logger.log(Level.INFO, "ScepRequest: Certificate used for signing: {0}, Enrollment request Id:  Customer: {1}", new Object[] { this.signingCertificateUsedByRequester.getSubjectDN(), mdmScepRequest.getEnrollmentRequestId(), mdmScepRequest.getCustomerId() });
            final PkiMessageDecoder decoder = new PkiMessageDecoder(this.signingCertificateUsedByRequester, enrollmentRequestDecoder);
            final PkiMessage<?> pkiMessage = (PkiMessage<?>)decoder.decode(signedData);
            this.logger.log(Level.INFO, "ScepRequest: SCEP request decoded: Enrollment request Id: {0}, Customer: {1}", new Object[] { mdmScepRequest.getEnrollmentRequestId(), mdmScepRequest.getCustomerId() });
            this.logger.log(Level.INFO, "ScepRequest: Getting the SCEP request params for Enrollment request: {0}, Customer: {1}", new Object[] { mdmScepRequest.getEnrollmentRequestId(), mdmScepRequest.getCustomerId() });
            final Object msgData = pkiMessage.getMessageData();
            this.certificateSigningRequest = (PKCS10CertificationRequest)msgData;
            this.transactionId = pkiMessage.getTransactionId();
            this.recipientNonce = pkiMessage.getSenderNonce();
            this.messageType = pkiMessage.getMessageType();
            this.scepEnrollmentPasscode = CertificationRequestUtils.getChallengePassword(this.certificateSigningRequest);
            this.senderNonce = Nonce.nextNonce();
            this.logger.log(Level.INFO, "ScepRequest: Successfully constructed the SCEP request: Transaction Id: {0}, Enrollment request: {1}, Customer: {2}", new Object[] { this.transactionId, mdmScepRequest.getEnrollmentRequestId(), mdmScepRequest.getCustomerId() });
        }
        catch (final CertificateException | IOException | MessageDecodingException | CMSException e) {
            this.logger.log(Level.SEVERE, e, () -> "ScepRequest: Exception while constructing the SCEP request for Enrollment request: " + mdmScepRequest2.getEnrollmentRequestId() + ", Customer: " + mdmScepRequest2.getCustomerId());
            throw new OperationFailureException(FailInfo.badRequest);
        }
    }
    
    private X509Certificate getCertificateUsedForSigningData(final CMSSignedData signedData, final Selector<X509CertificateHolder> certificateSelector) throws CertificateException, IOException {
        this.logger.log(Level.INFO, "ScepRequest: Getting the certificate used for signing the data");
        final Store<X509CertificateHolder> availableCertificateHolders = (Store<X509CertificateHolder>)signedData.getCertificates();
        final Collection<X509CertificateHolder> reqCerts = availableCertificateHolders.getMatches((Selector)certificateSelector);
        final X509CertificateHolder certificateHolder = reqCerts.iterator().next();
        final CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(certificateHolder.getEncoded())) {
            return (X509Certificate)factory.generateCertificate(bais);
        }
    }
    
    public X509Certificate getSigningCertificate() {
        return this.signingCertificateUsedByRequester;
    }
    
    public String getScepEnrollmentPasscode() {
        return this.scepEnrollmentPasscode;
    }
    
    public PKCS10CertificationRequest getCertificateSigningRequest() {
        return this.certificateSigningRequest;
    }
    
    public Nonce getSenderNonce() {
        return this.senderNonce;
    }
    
    public Nonce getRecipientNonce() {
        return this.recipientNonce;
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public MessageType getMessageType() {
        return this.messageType;
    }
}
