package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.PrivateKey;

public abstract class PdfSigGenericPKCS extends PdfSignature
{
    protected String hashAlgorithm;
    protected String provider;
    protected PdfPKCS7 pkcs;
    protected String name;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;
    
    public PdfSigGenericPKCS(final PdfName filter, final PdfName subFilter) {
        super(filter, subFilter);
        this.provider = null;
    }
    
    public void setSignInfo(final PrivateKey privKey, final Certificate[] certChain, final CRL[] crlList) {
        try {
            (this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)))).setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                for (int k = 0; k < certChain.length; ++k) {
                    bout.write(certChain[k].getEncoded());
                }
                bout.close();
                this.setCert(bout.toByteArray());
                this.setContents(this.pkcs.getEncodedPKCS1());
            }
            else {
                this.setContents(this.pkcs.getEncodedPKCS7());
            }
            this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
            if (this.name != null) {
                this.put(PdfName.NAME, new PdfString(this.name, "UnicodeBig"));
            }
            (this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)))).setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public void setExternalDigest(final byte[] digest, final byte[] RSAdata, final String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PdfPKCS7 getSigner() {
        return this.pkcs;
    }
    
    public byte[] getSignerContents() {
        if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
            return this.pkcs.getEncodedPKCS1();
        }
        return this.pkcs.getEncodedPKCS7();
    }
    
    public static class VeriSign extends PdfSigGenericPKCS
    {
        public VeriSign() {
            super(PdfName.VERISIGN_PPKVS, PdfName.ADBE_PKCS7_DETACHED);
            this.hashAlgorithm = "MD5";
            this.put(PdfName.R, new PdfNumber(65537));
        }
        
        public VeriSign(final String provider) {
            this();
            this.provider = provider;
        }
    }
    
    public static class PPKLite extends PdfSigGenericPKCS
    {
        public PPKLite() {
            super(PdfName.ADOBE_PPKLITE, PdfName.ADBE_X509_RSA_SHA1);
            this.hashAlgorithm = "SHA1";
            this.put(PdfName.R, new PdfNumber(65541));
        }
        
        public PPKLite(final String provider) {
            this();
            this.provider = provider;
        }
    }
    
    public static class PPKMS extends PdfSigGenericPKCS
    {
        public PPKMS() {
            super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
            this.hashAlgorithm = "SHA1";
        }
        
        public PPKMS(final String provider) {
            this();
            this.provider = provider;
        }
    }
}
