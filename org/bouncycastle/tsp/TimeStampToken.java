package org.bouncycastle.tsp;

import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.util.Store;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSTypedData;
import java.util.Collection;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.tsp.TSTInfo;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import java.util.Date;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.CMSSignedData;

public class TimeStampToken
{
    CMSSignedData tsToken;
    SignerInformation tsaSignerInfo;
    Date genTime;
    TimeStampTokenInfo tstInfo;
    CertID certID;
    
    public TimeStampToken(final ContentInfo contentInfo) throws TSPException, IOException {
        this(getSignedData(contentInfo));
    }
    
    private static CMSSignedData getSignedData(final ContentInfo contentInfo) throws TSPException {
        try {
            return new CMSSignedData(contentInfo);
        }
        catch (final CMSException ex) {
            throw new TSPException("TSP parsing error: " + ex.getMessage(), ex.getCause());
        }
    }
    
    public TimeStampToken(final CMSSignedData tsToken) throws TSPException, IOException {
        this.tsToken = tsToken;
        if (!this.tsToken.getSignedContentTypeOID().equals(PKCSObjectIdentifiers.id_ct_TSTInfo.getId())) {
            throw new TSPValidationException("ContentInfo object not for a time stamp.");
        }
        final Collection<SignerInformation> signers = this.tsToken.getSignerInfos().getSigners();
        if (signers.size() != 1) {
            throw new IllegalArgumentException("Time-stamp token signed by " + signers.size() + " signers, but it must contain just the TSA signature.");
        }
        this.tsaSignerInfo = signers.iterator().next();
        try {
            final CMSTypedData signedContent = this.tsToken.getSignedContent();
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            signedContent.write(byteArrayOutputStream);
            this.tstInfo = new TimeStampTokenInfo(TSTInfo.getInstance((Object)new ASN1InputStream((InputStream)new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject()));
            final Attribute value = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            if (value != null) {
                this.certID = new CertID(ESSCertID.getInstance((Object)SigningCertificate.getInstance((Object)value.getAttrValues().getObjectAt(0)).getCerts()[0]));
            }
            else {
                final Attribute value2 = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
                if (value2 == null) {
                    throw new TSPValidationException("no signing certificate attribute found, time stamp invalid.");
                }
                this.certID = new CertID(ESSCertIDv2.getInstance((Object)SigningCertificateV2.getInstance((Object)value2.getAttrValues().getObjectAt(0)).getCerts()[0]));
            }
        }
        catch (final CMSException ex) {
            throw new TSPException(ex.getMessage(), ex.getUnderlyingException());
        }
    }
    
    public TimeStampTokenInfo getTimeStampInfo() {
        return this.tstInfo;
    }
    
    public SignerId getSID() {
        return this.tsaSignerInfo.getSID();
    }
    
    public AttributeTable getSignedAttributes() {
        return this.tsaSignerInfo.getSignedAttributes();
    }
    
    public AttributeTable getUnsignedAttributes() {
        return this.tsaSignerInfo.getUnsignedAttributes();
    }
    
    public Store getCertificates() {
        return this.tsToken.getCertificates();
    }
    
    public Store getCRLs() {
        return this.tsToken.getCRLs();
    }
    
    public Store getAttributeCertificates() {
        return this.tsToken.getAttributeCertificates();
    }
    
    public void validate(final SignerInformationVerifier signerInformationVerifier) throws TSPException, TSPValidationException {
        if (!signerInformationVerifier.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("verifier provider needs an associated certificate");
        }
        try {
            final X509CertificateHolder associatedCertificate = signerInformationVerifier.getAssociatedCertificate();
            final DigestCalculator digestCalculator = signerInformationVerifier.getDigestCalculator(this.certID.getHashAlgorithm());
            final OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(associatedCertificate.getEncoded());
            outputStream.close();
            if (!Arrays.constantTimeAreEqual(this.certID.getCertHash(), digestCalculator.getDigest())) {
                throw new TSPValidationException("certificate hash does not match certID hash.");
            }
            if (this.certID.getIssuerSerial() != null) {
                final IssuerAndSerialNumber issuerAndSerialNumber = new IssuerAndSerialNumber(associatedCertificate.toASN1Structure());
                if (!this.certID.getIssuerSerial().getSerial().equals((Object)issuerAndSerialNumber.getSerialNumber())) {
                    throw new TSPValidationException("certificate serial number does not match certID for signature.");
                }
                final GeneralName[] names = this.certID.getIssuerSerial().getIssuer().getNames();
                boolean b = false;
                for (int i = 0; i != names.length; ++i) {
                    if (names[i].getTagNo() == 4 && X500Name.getInstance((Object)names[i].getName()).equals((Object)X500Name.getInstance((Object)issuerAndSerialNumber.getName()))) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    throw new TSPValidationException("certificate name does not match certID for signature. ");
                }
            }
            TSPUtil.validateCertificate(associatedCertificate);
            if (!associatedCertificate.isValidOn(this.tstInfo.getGenTime())) {
                throw new TSPValidationException("certificate not valid when time stamp created.");
            }
            if (!this.tsaSignerInfo.verify(signerInformationVerifier)) {
                throw new TSPValidationException("signature not created by certificate.");
            }
        }
        catch (final CMSException ex) {
            if (ex.getUnderlyingException() != null) {
                throw new TSPException(ex.getMessage(), ex.getUnderlyingException());
            }
            throw new TSPException("CMS exception: " + ex, ex);
        }
        catch (final IOException ex2) {
            throw new TSPException("problem processing certificate: " + ex2, ex2);
        }
        catch (final OperatorCreationException ex3) {
            throw new TSPException("unable to create digest: " + ex3.getMessage(), ex3);
        }
    }
    
    public boolean isSignatureValid(final SignerInformationVerifier signerInformationVerifier) throws TSPException {
        try {
            return this.tsaSignerInfo.verify(signerInformationVerifier);
        }
        catch (final CMSException ex) {
            if (ex.getUnderlyingException() != null) {
                throw new TSPException(ex.getMessage(), ex.getUnderlyingException());
            }
            throw new TSPException("CMS exception: " + ex, ex);
        }
    }
    
    public CMSSignedData toCMSSignedData() {
        return this.tsToken;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.tsToken.getEncoded();
    }
    
    private class CertID
    {
        private ESSCertID certID;
        private ESSCertIDv2 certIDv2;
        
        CertID(final ESSCertID certID) {
            this.certID = certID;
            this.certIDv2 = null;
        }
        
        CertID(final ESSCertIDv2 certIDv2) {
            this.certIDv2 = certIDv2;
            this.certID = null;
        }
        
        public String getHashAlgorithmName() {
            if (this.certID != null) {
                return "SHA-1";
            }
            if (NISTObjectIdentifiers.id_sha256.equals((Object)this.certIDv2.getHashAlgorithm().getAlgorithm())) {
                return "SHA-256";
            }
            return this.certIDv2.getHashAlgorithm().getAlgorithm().getId();
        }
        
        public AlgorithmIdentifier getHashAlgorithm() {
            if (this.certID != null) {
                return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
            }
            return this.certIDv2.getHashAlgorithm();
        }
        
        public byte[] getCertHash() {
            if (this.certID != null) {
                return this.certID.getCertHash();
            }
            return this.certIDv2.getCertHash();
        }
        
        public IssuerSerial getIssuerSerial() {
            if (this.certID != null) {
                return this.certID.getIssuerSerial();
            }
            return this.certIDv2.getIssuerSerial();
        }
    }
}
