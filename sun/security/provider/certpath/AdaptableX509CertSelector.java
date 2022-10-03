package sun.security.provider.certpath;

import java.util.Arrays;
import sun.security.util.DerInputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.io.IOException;
import sun.security.x509.SerialNumber;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import java.math.BigInteger;
import java.util.Date;
import sun.security.util.Debug;
import java.security.cert.X509CertSelector;

class AdaptableX509CertSelector extends X509CertSelector
{
    private static final Debug debug;
    private Date startDate;
    private Date endDate;
    private byte[] ski;
    private BigInteger serial;
    
    void setValidityPeriod(final Date startDate, final Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    @Override
    public void setSubjectKeyIdentifier(final byte[] array) {
        throw new IllegalArgumentException();
    }
    
    @Override
    public void setSerialNumber(final BigInteger bigInteger) {
        throw new IllegalArgumentException();
    }
    
    void setSkiAndSerialNumber(final AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension) throws IOException {
        this.ski = null;
        this.serial = null;
        if (authorityKeyIdentifierExtension != null) {
            this.ski = authorityKeyIdentifierExtension.getEncodedKeyIdentifier();
            final SerialNumber serialNumber = (SerialNumber)authorityKeyIdentifierExtension.get("serial_number");
            if (serialNumber != null) {
                this.serial = serialNumber.getNumber();
            }
        }
    }
    
    @Override
    public boolean match(final Certificate certificate) {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        if (!this.matchSubjectKeyID(x509Certificate)) {
            return false;
        }
        final int version = x509Certificate.getVersion();
        if (this.serial != null && version > 2 && !this.serial.equals(x509Certificate.getSerialNumber())) {
            return false;
        }
        if (version < 3) {
            if (this.startDate != null) {
                try {
                    x509Certificate.checkValidity(this.startDate);
                }
                catch (final CertificateException ex) {
                    return false;
                }
            }
            if (this.endDate != null) {
                try {
                    x509Certificate.checkValidity(this.endDate);
                }
                catch (final CertificateException ex2) {
                    return false;
                }
            }
        }
        return super.match(certificate);
    }
    
    private boolean matchSubjectKeyID(final X509Certificate x509Certificate) {
        if (this.ski == null) {
            return true;
        }
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.14");
            if (extensionValue == null) {
                if (AdaptableX509CertSelector.debug != null) {
                    AdaptableX509CertSelector.debug.println("AdaptableX509CertSelector.match: no subject key ID extension. Subject: " + x509Certificate.getSubjectX500Principal());
                }
                return true;
            }
            final byte[] octetString = new DerInputStream(extensionValue).getOctetString();
            if (octetString == null || !Arrays.equals(this.ski, octetString)) {
                if (AdaptableX509CertSelector.debug != null) {
                    AdaptableX509CertSelector.debug.println("AdaptableX509CertSelector.match: subject key IDs don't match. Expected: " + Arrays.toString(this.ski) + " Cert's: " + Arrays.toString(octetString));
                }
                return false;
            }
        }
        catch (final IOException ex) {
            if (AdaptableX509CertSelector.debug != null) {
                AdaptableX509CertSelector.debug.println("AdaptableX509CertSelector.match: exception in subject key ID check");
            }
            return false;
        }
        return true;
    }
    
    @Override
    public Object clone() {
        final AdaptableX509CertSelector adaptableX509CertSelector = (AdaptableX509CertSelector)super.clone();
        if (this.startDate != null) {
            adaptableX509CertSelector.startDate = (Date)this.startDate.clone();
        }
        if (this.endDate != null) {
            adaptableX509CertSelector.endDate = (Date)this.endDate.clone();
        }
        if (this.ski != null) {
            adaptableX509CertSelector.ski = this.ski.clone();
        }
        return adaptableX509CertSelector;
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
