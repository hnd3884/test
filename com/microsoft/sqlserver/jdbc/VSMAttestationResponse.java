package com.microsoft.sqlserver.jdbc;

import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Iterator;
import java.util.Collection;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;

class VSMAttestationResponse extends BaseAttestationResponse
{
    private byte[] healthReportCertificate;
    private byte[] enclaveReportPackage;
    private X509Certificate healthCert;
    
    VSMAttestationResponse(final byte[] b) throws SQLServerException {
        final ByteBuffer response = (null != b) ? ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN) : null;
        if (null != response) {
            this.totalSize = response.getInt();
            this.identitySize = response.getInt();
            final int healthReportSize = response.getInt();
            final int enclaveReportSize = response.getInt();
            this.enclavePK = new byte[this.identitySize];
            this.healthReportCertificate = new byte[healthReportSize];
            this.enclaveReportPackage = new byte[enclaveReportSize];
            response.get(this.enclavePK, 0, this.identitySize);
            response.get(this.healthReportCertificate, 0, healthReportSize);
            response.get(this.enclaveReportPackage, 0, enclaveReportSize);
            this.sessionInfoSize = response.getInt();
            response.get(this.sessionID, 0, 8);
            this.DHPKsize = response.getInt();
            this.DHPKSsize = response.getInt();
            this.DHpublicKey = new byte[this.DHPKsize];
            this.publicKeySig = new byte[this.DHPKSsize];
            response.get(this.DHpublicKey, 0, this.DHPKsize);
            response.get(this.publicKeySig, 0, this.DHPKSsize);
        }
        if (null == response || 0 != response.remaining()) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclaveResponseLengthError"), "0", false);
        }
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            this.healthCert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(this.healthReportCertificate));
        }
        catch (final CertificateException ce) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_HealthCertError"));
            final Object[] msgArgs = { ce.getLocalizedMessage() };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        }
    }
    
    void validateCert(final byte[] b) throws SQLServerException {
        if (null != b) {
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final Collection<X509Certificate> certs = (Collection<X509Certificate>)cf.generateCertificates(new ByteArrayInputStream(b));
                for (final X509Certificate cert : certs) {
                    try {
                        this.healthCert.verify(cert.getPublicKey());
                        return;
                    }
                    catch (final SignatureException ex) {
                        continue;
                    }
                    break;
                }
            }
            catch (final GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
        SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidHealthCert"), "0", false);
    }
    
    void validateStatementSignature() throws SQLServerException, GeneralSecurityException {
        final ByteBuffer enclaveReportPackageBuffer = ByteBuffer.wrap(this.enclaveReportPackage).order(ByteOrder.LITTLE_ENDIAN);
        final int packageSize = enclaveReportPackageBuffer.getInt();
        final int version = enclaveReportPackageBuffer.getInt();
        final int signatureScheme = enclaveReportPackageBuffer.getInt();
        final int signedStatementSize = enclaveReportPackageBuffer.getInt();
        final int signatureSize = enclaveReportPackageBuffer.getInt();
        final int reserved = enclaveReportPackageBuffer.getInt();
        final byte[] signedStatement = new byte[signedStatementSize];
        enclaveReportPackageBuffer.get(signedStatement, 0, signedStatementSize);
        final byte[] signatureBlob = new byte[signatureSize];
        enclaveReportPackageBuffer.get(signatureBlob, 0, signatureSize);
        if (enclaveReportPackageBuffer.remaining() != 0) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclavePackageLengthError"), "0", false);
        }
        Signature sig = null;
        try {
            sig = Signature.getInstance("RSASSA-PSS");
        }
        catch (final NoSuchAlgorithmException e) {
            SQLServerBouncyCastleLoader.loadBouncyCastle();
            sig = Signature.getInstance("RSASSA-PSS");
        }
        final PSSParameterSpec pss = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
        sig.setParameter(pss);
        sig.initVerify(this.healthCert);
        sig.update(signedStatement);
        if (!sig.verify(signatureBlob)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidSignedStatement"), "0", false);
        }
    }
}
