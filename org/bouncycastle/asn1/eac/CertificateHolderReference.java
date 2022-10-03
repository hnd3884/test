package org.bouncycastle.asn1.eac;

import java.io.UnsupportedEncodingException;

public class CertificateHolderReference
{
    private static final String ReferenceEncoding = "ISO-8859-1";
    private String countryCode;
    private String holderMnemonic;
    private String sequenceNumber;
    
    public CertificateHolderReference(final String countryCode, final String holderMnemonic, final String sequenceNumber) {
        this.countryCode = countryCode;
        this.holderMnemonic = holderMnemonic;
        this.sequenceNumber = sequenceNumber;
    }
    
    CertificateHolderReference(final byte[] array) {
        try {
            final String s = new String(array, "ISO-8859-1");
            this.countryCode = s.substring(0, 2);
            this.holderMnemonic = s.substring(2, s.length() - 5);
            this.sequenceNumber = s.substring(s.length() - 5);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex.toString());
        }
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }
    
    public String getHolderMnemonic() {
        return this.holderMnemonic;
    }
    
    public String getSequenceNumber() {
        return this.sequenceNumber;
    }
    
    public byte[] getEncoded() {
        final String string = this.countryCode + this.holderMnemonic + this.sequenceNumber;
        try {
            return string.getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex.toString());
        }
    }
}
