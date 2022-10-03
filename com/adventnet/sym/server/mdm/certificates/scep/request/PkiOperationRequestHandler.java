package com.adventnet.sym.server.mdm.certificates.scep.request;

import java.security.PrivateKey;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.certificates.MdmCertAuthUtil;
import org.jscep.message.PkcsPkiEnvelopeDecoder;
import org.jscep.message.PkcsPkiEnvelopeEncoder;
import java.security.GeneralSecurityException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSAbsentContent;
import org.bouncycastle.util.Store;
import java.util.Collection;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import java.util.List;
import org.jscep.message.PkiMessageEncoder;
import org.bouncycastle.cms.CMSSignedData;
import org.jscep.message.PkiMessage;
import org.jscep.message.CertRep;
import java.util.Collections;
import java.security.cert.X509Certificate;
import org.jscep.transaction.OperationFailureException;
import org.jscep.transaction.FailInfo;
import com.adventnet.sym.server.mdm.certificates.scep.response.ScepResponse;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.SigningCertificateAuthority;
import java.util.logging.Logger;

public class PkiOperationRequestHandler implements ScepRequestHandler
{
    private final Logger logger;
    final MdmScepRequest mdmScepRequest;
    final SigningCertificateAuthority signingCertificateAuthority;
    final ScepEnrollmentRequestValidator scepEnrollmentRequestValidator;
    
    public PkiOperationRequestHandler(final MdmScepRequest mdmScepRequest) throws Exception {
        (this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger")).log(Level.INFO, "PkiOperationRequestHandler DATA-IN: {0}", new Object[] { mdmScepRequest.getEnrollmentRequestId() });
        this.mdmScepRequest = mdmScepRequest;
        this.signingCertificateAuthority = this.getSigningCertificateAuthority(mdmScepRequest.getCustomerId());
        this.scepEnrollmentRequestValidator = MdmScepRequestValidatorFactory.getValidator(mdmScepRequest);
    }
    
    @Override
    public ScepResponse handleRequest() throws Exception {
        final ScepRequest scepRequest = new ScepRequest(this.mdmScepRequest, this.getPkcsPkiEnvelopeDecoder());
        if (!this.isValidScepRequest(scepRequest)) {
            this.logger.log(Level.SEVERE, "PkiOperationRequestHandler: Integrity check failed for : {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            throw new OperationFailureException(FailInfo.badMessageCheck);
        }
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Request validated for : {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        final X509Certificate issuedCertificate = this.signingCertificateAuthority.issueCertificate(scepRequest.getCertificateSigningRequest(), 5);
        final ScepResponse scepResponse = this.encodeIssuedCertificate(issuedCertificate, scepRequest);
        return scepResponse;
    }
    
    private boolean isValidScepRequest(final ScepRequest scepRequest) {
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Verifying the integrity of the incoming SCEP request for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        boolean isValidScepRequest;
        if (scepRequest.getScepEnrollmentPasscode() != null) {
            this.logger.log(Level.INFO, "PkiOperationRequestHandler: The requester is using challenge password. Checking whether the SCEP enrollment passcode is valid:: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            isValidScepRequest = this.scepEnrollmentRequestValidator.isValidPasscode(scepRequest.getScepEnrollmentPasscode());
        }
        else {
            this.logger.log(Level.INFO, "PkiOperationRequestHandler: The requester is using signing certificate. Checking whether the signing certificate used is issued by our CA:: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            isValidScepRequest = this.scepEnrollmentRequestValidator.isEligibleForRenewal(scepRequest.getSigningCertificate());
        }
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Is valid request: {0} for {1}", new Object[] { isValidScepRequest, this.mdmScepRequest.getEnrollmentRequestId() });
        return isValidScepRequest;
    }
    
    private ScepResponse encodeIssuedCertificate(final X509Certificate issuedCertificate, final ScepRequest scepRequest) throws Exception {
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Encoding client certificate: {0}", new Object[] { issuedCertificate.getSubjectDN() });
        final CMSSignedData messageData = this.getMessageData(Collections.singletonList(issuedCertificate));
        final CertRep certResponse = new CertRep(scepRequest.getTransactionId(), scepRequest.getSenderNonce(), scepRequest.getRecipientNonce(), messageData);
        final PkiMessageEncoder messageDataEncoder = this.getPkiMessageEncoder(scepRequest.getSigningCertificate());
        final CMSSignedData signedData = messageDataEncoder.encode((PkiMessage)certResponse);
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Client certificate added to CMS signed data for {0}", new Object[] { issuedCertificate.getSubjectDN() });
        final ScepResponse scepResponse = new ScepResponse();
        scepResponse.setContentType("application/x-pki-message");
        scepResponse.setResponse(signedData.getEncoded());
        return scepResponse;
    }
    
    private CMSSignedData getMessageData(final List<X509Certificate> certs) throws CMSException, GeneralSecurityException {
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Adding certs to pki message data for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        final JcaCertStore store = new JcaCertStore((Collection)certs);
        generator.addCertificates((Store)store);
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Successfully added certs to pki message data {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        return generator.generate((CMSTypedData)new CMSAbsentContent());
    }
    
    private PkiMessageEncoder getPkiMessageEncoder(final X509Certificate requesterCertificate) {
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Constructing PKI envelope encoder for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        final PkcsPkiEnvelopeEncoder envEncoder = new PkcsPkiEnvelopeEncoder(requesterCertificate, "DESede");
        final PkiMessageEncoder pkiMessageEncoder = new PkiMessageEncoder(this.signingCertificateAuthority.getPrivateKey(), (X509Certificate)this.signingCertificateAuthority.getCertificates().get(0), new X509Certificate[] { this.signingCertificateAuthority.getCertificates().get(0) }, envEncoder);
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Successfully constructed PKI envelope encoder for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        return pkiMessageEncoder;
    }
    
    private PkcsPkiEnvelopeDecoder getPkcsPkiEnvelopeDecoder() {
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Constructing PKI envelope decoder for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        final PkcsPkiEnvelopeDecoder envDecoder = new PkcsPkiEnvelopeDecoder((X509Certificate)this.signingCertificateAuthority.getCertificates().get(0), this.signingCertificateAuthority.getPrivateKey());
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: Successfully constructed PKI envelope decoder for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        return envDecoder;
    }
    
    private SigningCertificateAuthority getSigningCertificateAuthority(final long customerId) throws Exception {
        final String rootCaCertificatePath = MdmCertAuthUtil.Scep.getScepRootCACertificatePath(customerId);
        final String rootCaPrivateKeyPath = MdmCertAuthUtil.Scep.getScepRootCAPrivateKeyPath(customerId);
        this.logger.log(Level.INFO, "PkiOperationRequestHandler: ca certificate path {0}, key: {1} for {2}", new Object[] { rootCaCertificatePath, rootCaPrivateKeyPath, this.mdmScepRequest.getEnrollmentRequestId() });
        try (final InputStream certificateStream = ApiFactoryProvider.getFileAccessAPI().readFile(rootCaCertificatePath);
             final InputStream privateKeyStream = ApiFactoryProvider.getFileAccessAPI().readFile(rootCaPrivateKeyPath)) {
            this.logger.log(Level.INFO, "PkiOperationRequestHandler: Getting certificates and private key for customer: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            final X509Certificate cACertificate = (X509Certificate)CertificateUtil.convertInputStreamToX509CertificateChain(certificateStream)[0];
            final PrivateKey privateKey = CertificateUtil.convertInputStreamToRsaPrivateKey(privateKeyStream);
            final SigningCertificateAuthority signingCertificateAuthority = new SigningCertificateAuthority(cACertificate, privateKey);
            this.logger.log(Level.INFO, "PkiOperationRequestHandler: Successfully created signing certificate authority for: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            return signingCertificateAuthority;
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, e, () -> "PkiOperationRequestHandler: Exception while getting certificates and private key for customer: " + this.mdmScepRequest.getEnrollmentRequestId());
            throw e;
        }
    }
}
