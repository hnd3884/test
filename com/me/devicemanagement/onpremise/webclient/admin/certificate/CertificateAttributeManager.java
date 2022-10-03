package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.DERIA5String;
import java.io.IOException;
import java.util.logging.Level;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.AccessDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class CertificateAttributeManager
{
    private static final Logger LOGGER;
    private static CertificateAttributeManager certificateAttributeManager;
    List<String> oidExcludeListForUrls;
    
    private CertificateAttributeManager() {
        this.oidExcludeListForUrls = null;
        this.oidExcludeListForUrls = new ArrayList<String>() {
            {
                this.add(AccessDescription.id_ad_ocsp.getId());
            }
        };
    }
    
    public static CertificateAttributeManager getInstance() {
        if (CertificateAttributeManager.certificateAttributeManager == null) {
            CertificateAttributeManager.certificateAttributeManager = new CertificateAttributeManager();
        }
        return CertificateAttributeManager.certificateAttributeManager;
    }
    
    public List<String> getAIALocations(final X509Certificate certificate) {
        final List<String> caDownloadUrlList = new ArrayList<String>();
        final byte[] aiaExtensionValue = certificate.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
        if (aiaExtensionValue == null) {
            CertificateAttributeManager.LOGGER.severe("Certificate doesn't have authority information access points");
        }
        if (aiaExtensionValue != null) {
            final ASN1InputStream asn1In = new ASN1InputStream(aiaExtensionValue);
            AuthorityInformationAccess authorityInformationAccess = null;
            ASN1InputStream asn1InOctets = null;
            try {
                final DEROctetString aiaDEROctetString = (DEROctetString)asn1In.readObject();
                asn1InOctets = new ASN1InputStream(aiaDEROctetString.getOctets());
                final ASN1Sequence aiaASN1Sequence = (ASN1Sequence)asn1InOctets.readObject();
                authorityInformationAccess = AuthorityInformationAccess.getInstance((Object)aiaASN1Sequence);
            }
            catch (final IOException e) {
                CertificateAttributeManager.LOGGER.severe("Cannot read certificate to get OCSP URLs");
                try {
                    if (asn1In != null) {
                        asn1In.close();
                    }
                    if (asn1InOctets != null) {
                        asn1InOctets.close();
                    }
                }
                catch (final IOException ex) {
                    CertificateAttributeManager.LOGGER.log(Level.SEVERE, "failed in closing the io exception", ex);
                }
            }
            finally {
                try {
                    if (asn1In != null) {
                        asn1In.close();
                    }
                    if (asn1InOctets != null) {
                        asn1InOctets.close();
                    }
                }
                catch (final IOException ex2) {
                    CertificateAttributeManager.LOGGER.log(Level.SEVERE, "failed in closing the io exception", ex2);
                }
            }
            final AccessDescription[] accessDescriptions2;
            final AccessDescription[] accessDescriptions = accessDescriptions2 = authorityInformationAccess.getAccessDescriptions();
            for (final AccessDescription accessDescription : accessDescriptions2) {
                final String oid = accessDescription.getAccessMethod().getId();
                if (!this.oidExcludeListForUrls.contains(oid)) {
                    final GeneralName gn = accessDescription.getAccessLocation();
                    if (gn.getTagNo() == 6) {
                        final DERIA5String str = DERIA5String.getInstance((Object)gn.getName());
                        final String accessLocation = str.getString();
                        caDownloadUrlList.add(accessLocation);
                    }
                }
            }
        }
        if (caDownloadUrlList.isEmpty()) {
            CertificateAttributeManager.LOGGER.severe("Cannot read certificate to get OCSP URLs");
        }
        return caDownloadUrlList;
    }
    
    public String getIntermediateFileLink(final X509Certificate certificate) {
        final List<String> allUrlsIncertificate = this.getAIALocations(certificate);
        try {
            CertificateAttributeManager.LOGGER.log(Level.INFO, "All Url values from certificate : " + allUrlsIncertificate.toString());
            if (allUrlsIncertificate.isEmpty()) {
                return null;
            }
            String intermediateUrl = null;
            for (int i = 0; i < allUrlsIncertificate.size(); ++i) {
                intermediateUrl = allUrlsIncertificate.get(i);
                if (intermediateUrl != null) {
                    return intermediateUrl;
                }
            }
        }
        catch (final Exception ex) {
            CertificateAttributeManager.LOGGER.log(Level.SEVERE, "Getting ocsp url from the certificate failed.. Internet may not be there.. Certificate may have been corrupted..", ex);
        }
        return null;
    }
    
    public boolean isCertificateChainEnd(final X509Certificate certificateObj) {
        return this.getIntermediateFileLink(certificateObj) == null;
    }
    
    static {
        LOGGER = Logger.getLogger("ImportCertificateLogger");
        CertificateAttributeManager.certificateAttributeManager = null;
    }
}
