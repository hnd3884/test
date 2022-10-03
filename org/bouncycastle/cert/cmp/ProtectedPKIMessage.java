package org.bouncycastle.cert.cmp;

import java.io.IOException;
import org.bouncycastle.operator.ContentVerifier;
import java.io.OutputStream;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;

public class ProtectedPKIMessage
{
    private PKIMessage pkiMessage;
    
    public ProtectedPKIMessage(final GeneralPKIMessage generalPKIMessage) {
        if (!generalPKIMessage.hasProtection()) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = generalPKIMessage.toASN1Structure();
    }
    
    ProtectedPKIMessage(final PKIMessage pkiMessage) {
        if (pkiMessage.getHeader().getProtectionAlg() == null) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pkiMessage;
    }
    
    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }
    
    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }
    
    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }
    
    public boolean hasPasswordBasedMacProtection() {
        return this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm().equals((Object)CMPObjectIdentifiers.passwordBasedMac);
    }
    
    public X509CertificateHolder[] getCertificates() {
        final CMPCertificate[] extraCerts = this.pkiMessage.getExtraCerts();
        if (extraCerts == null) {
            return new X509CertificateHolder[0];
        }
        final X509CertificateHolder[] array = new X509CertificateHolder[extraCerts.length];
        for (int i = 0; i != extraCerts.length; ++i) {
            array[i] = new X509CertificateHolder(extraCerts[i].getX509v3PKCert());
        }
        return array;
    }
    
    public boolean verify(final ContentVerifierProvider contentVerifierProvider) throws CMPException {
        try {
            return this.verifySignature(this.pkiMessage.getProtection().getBytes(), contentVerifierProvider.get(this.pkiMessage.getHeader().getProtectionAlg()));
        }
        catch (final Exception ex) {
            throw new CMPException("unable to verify signature: " + ex.getMessage(), ex);
        }
    }
    
    public boolean verify(final PKMACBuilder pkmacBuilder, final char[] array) throws CMPException {
        if (!CMPObjectIdentifiers.passwordBasedMac.equals((Object)this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm())) {
            throw new CMPException("protection algorithm not mac based");
        }
        try {
            pkmacBuilder.setParameters(PBMParameter.getInstance((Object)this.pkiMessage.getHeader().getProtectionAlg().getParameters()));
            final MacCalculator build = pkmacBuilder.build(array);
            final OutputStream outputStream = build.getOutputStream();
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add((ASN1Encodable)this.pkiMessage.getHeader());
            asn1EncodableVector.add((ASN1Encodable)this.pkiMessage.getBody());
            outputStream.write(new DERSequence(asn1EncodableVector).getEncoded("DER"));
            outputStream.close();
            return Arrays.areEqual(build.getMac(), this.pkiMessage.getProtection().getBytes());
        }
        catch (final Exception ex) {
            throw new CMPException("unable to verify MAC: " + ex.getMessage(), ex);
        }
    }
    
    private boolean verifySignature(final byte[] array, final ContentVerifier contentVerifier) throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)this.pkiMessage.getHeader());
        asn1EncodableVector.add((ASN1Encodable)this.pkiMessage.getBody());
        final OutputStream outputStream = contentVerifier.getOutputStream();
        outputStream.write(new DERSequence(asn1EncodableVector).getEncoded("DER"));
        outputStream.close();
        return contentVerifier.verify(array);
    }
}
