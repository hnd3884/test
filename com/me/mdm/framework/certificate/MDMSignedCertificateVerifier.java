package com.me.mdm.framework.certificate;

import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Store;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.CMSSignedData;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class MDMSignedCertificateVerifier
{
    private static Logger logger;
    private static Logger mdmEnrollmentLogger;
    
    private static boolean verifySignature(final byte[] requestBytes, final X509Certificate verifyingPublicKey) {
        try {
            MDMSignedCertificateVerifier.logger.log(Level.INFO, "===================Digital signature verifier starts================");
            MDMSignedCertificateVerifier.logger.log(Level.INFO, "Inside MDMSignedCertificateVerifier verifySignature");
            MDMSignedCertificateVerifier.logger.log(Level.INFO, "Going to verify if data is signed by certificate {0}", verifyingPublicKey.getSubjectDN());
            final CMSSignedData cmsSignedData = new CMSSignedData((byte[])requestBytes.clone());
            final SignerInformationStore signers = cmsSignedData.getSignerInfos();
            final Collection<SignerInformation> signersList = signers.getSigners();
            final Iterator<SignerInformation> signerIterator = signersList.iterator();
            while (signerIterator.hasNext()) {
                X509CertificateHolder cert = null;
                final SignerInformation signer = signerIterator.next();
                final Store certs = cmsSignedData.getCertificates();
                final Collection certCollection = certs.getMatches((Selector)signer.getSID());
                final Iterator<X509CertificateHolder> certIt = certCollection.iterator();
                cert = certIt.next();
                MDMSignedCertificateVerifier.logger.log(Level.INFO, "Certificate issued by :  {0}", cert.getIssuer());
                MDMSignedCertificateVerifier.logger.log(Level.INFO, "Certificate Subject by :  {0}", cert.getSubject());
                MDMSignedCertificateVerifier.logger.log(Level.INFO, "Certificate Validity :  {0} to {1}", new Object[] { cert.getNotBefore(), cert.getNotAfter() });
                MDMSignedCertificateVerifier.logger.log(Level.INFO, "Certificate SerialNumber :  {0}", cert.getSerialNumber());
                try {
                    final ContentVerifierProvider prov = new JcaContentVerifierProviderBuilder().build(verifyingPublicKey);
                    if (!cert.isSignatureValid(prov)) {
                        MDMSignedCertificateVerifier.logger.log(Level.INFO, "{0} Not verified by {1}", new Object[] { cert.getIssuer(), verifyingPublicKey.getSubjectDN() });
                        return false;
                    }
                    continue;
                }
                catch (final Exception ex) {
                    MDMSignedCertificateVerifier.logger.log(Level.INFO, "Not signed Verifier ", ex);
                    return false;
                }
            }
            return true;
        }
        catch (final Exception ex2) {
            MDMSignedCertificateVerifier.logger.log(Level.SEVERE, "Exception in MDMSignedCertificateVerifier", ex2);
        }
        finally {
            MDMSignedCertificateVerifier.logger.log(Level.INFO, "===================Digital signature verifier Ends================");
        }
        return false;
    }
    
    public static boolean isAppleDeviceRequest(final byte[] clonedRequestBody) {
        if (clonedRequestBody == null || clonedRequestBody.length == 0) {
            MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Request body is null or empty... Returning false");
            return false;
        }
        try {
            MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Inside MDMSignedCertificateVerifier isAppleDeviceRequest");
            if (verifySignature(clonedRequestBody.clone(), AppleRootCA.getAppleRootCA())) {
                MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Given request is signed by Main Apple root CA as expected....");
                return true;
            }
            if (verifySignature(clonedRequestBody.clone(), AppleRootCA.getAppleRootCANew())) {
                MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Given request is not siged by main root CA but insted signed by new Apple root CA ....(Please check)");
                return true;
            }
            MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Given request is not siged by main root CA nor by new root CA");
            return false;
        }
        catch (final Exception ex) {
            MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.SEVERE, "Exception in isAppleDeviceRequest MDMSignedCertificateVerifier", ex);
            MDMSignedCertificateVerifier.mdmEnrollmentLogger.log(Level.INFO, "Some error has occured , returning  isAppleDeviceRequest false for security reasons.");
            return false;
        }
    }
    
    static {
        MDMSignedCertificateVerifier.logger = Logger.getLogger("MDMLogger");
        MDMSignedCertificateVerifier.mdmEnrollmentLogger = Logger.getLogger("MDMEnrollment");
    }
}
