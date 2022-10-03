package com.me.mdm.server.inv.ios.DeviceAttestation;

import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import org.bouncycastle.util.encoders.Base64;
import java.security.cert.CertificateFactory;
import com.me.mdm.framework.certificate.AppleRootCA;
import org.bouncycastle.asn1.ASN1OctetString;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class DeviceAttestationCertificateHandler
{
    private static Logger logger;
    
    public void getDeviceAttestationDetails(final String leafCertificate, final String intermediateCertificate, final DeviceAttestationModel deviceAttestationDetails) throws Exception {
        deviceAttestationDetails.setSignedByApple(this.verifyDeviceAttestationCertificate(leafCertificate, intermediateCertificate));
        if (deviceAttestationDetails.isSignedByApple()) {
            this.getDetailsFromLeafCertificate(this.generateCertificate(leafCertificate), deviceAttestationDetails);
            return;
        }
        throw new Exception("Certificate not signed by Apple Enterprise Root CA");
    }
    
    private void getDetailsFromLeafCertificate(final X509Certificate leafCertificate, final DeviceAttestationModel deviceAttestationDetails) {
        final byte[] serialNumberByteArray = ASN1OctetString.getInstance((Object)leafCertificate.getExtensionValue("1.2.840.113635.100.8.9.1")).getOctets();
        final byte[] udidByteArray = ASN1OctetString.getInstance((Object)leafCertificate.getExtensionValue("1.2.840.113635.100.8.9.2")).getOctets();
        final byte[] osVersionByteArray = ASN1OctetString.getInstance((Object)leafCertificate.getExtensionValue("1.2.840.113635.100.8.10.2")).getOctets();
        final byte[] nonceByteArray = ASN1OctetString.getInstance((Object)leafCertificate.getExtensionValue("1.2.840.113635.100.8.11.1")).getOctets();
        if (serialNumberByteArray != null) {
            deviceAttestationDetails.setSerialNumber(new String(serialNumberByteArray));
        }
        if (udidByteArray != null) {
            deviceAttestationDetails.setUdid(new String(udidByteArray));
        }
        if (osVersionByteArray != null) {
            deviceAttestationDetails.setOsVersion(new String(osVersionByteArray));
        }
        if (nonceByteArray != null) {
            deviceAttestationDetails.setNonce(new String(nonceByteArray));
        }
        if (deviceAttestationDetails.getOsVersion() == null || deviceAttestationDetails.getNonce() == null) {
            deviceAttestationDetails.setDeviceInformationCommandStatus(false);
        }
        else {
            deviceAttestationDetails.setDeviceInformationCommandStatus(true);
        }
    }
    
    private boolean verifyDeviceAttestationCertificate(final String leafCertificate, final String intermediateCertificate) {
        final boolean isLeafCertificateVerified = this.verifyChainedCertificateSignature(this.generateCertificate(leafCertificate), this.generateCertificate(intermediateCertificate));
        final boolean isIntermediateCertificateVerified = this.verifyChainedCertificateSignature(this.generateCertificate(intermediateCertificate), AppleRootCA.getAppleEnterpriseRootCA());
        return isLeafCertificateVerified && isIntermediateCertificateVerified;
    }
    
    private X509Certificate generateCertificate(final String certificate) {
        X509Certificate rootCertificate = null;
        InputStream newCertStream = null;
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            newCertStream = new ByteArrayInputStream(Base64.decode(certificate));
            rootCertificate = (X509Certificate)cf.generateCertificate(newCertStream);
            if (rootCertificate != null) {
                DeviceAttestationCertificateHandler.logger.log(Level.INFO, "Certificate Generated successfully");
            }
        }
        catch (final Exception ex) {
            DeviceAttestationCertificateHandler.logger.log(Level.SEVERE, "Exception while generate Certificate {0}", ex);
            try {
                if (newCertStream != null) {
                    newCertStream.close();
                }
            }
            catch (final IOException e) {
                DeviceAttestationCertificateHandler.logger.log(Level.SEVERE, "generateCertificate Error while closing stream..");
            }
        }
        finally {
            try {
                if (newCertStream != null) {
                    newCertStream.close();
                }
            }
            catch (final IOException e2) {
                DeviceAttestationCertificateHandler.logger.log(Level.SEVERE, "generateCertificate Error while closing stream..");
            }
        }
        return rootCertificate;
    }
    
    private boolean verifyChainedCertificateSignature(final X509Certificate lowerChainCertificate, final X509Certificate higherChainCertificate) {
        boolean isVerified = true;
        try {
            DeviceAttestationCertificateHandler.logger.log(Level.INFO, "===================Digital signature verifier Starts================");
            lowerChainCertificate.verify(higherChainCertificate.getPublicKey());
        }
        catch (final Exception ex) {
            DeviceAttestationCertificateHandler.logger.log(Level.SEVERE, "Exception in verifyChainedCertificateSignature, {0}", ex);
            isVerified = false;
            return isVerified;
        }
        finally {
            DeviceAttestationCertificateHandler.logger.log(Level.INFO, "===================Digital signature verifier Ends================");
            return isVerified;
        }
    }
    
    static {
        DeviceAttestationCertificateHandler.logger = Logger.getLogger("MDMLogger");
    }
}
