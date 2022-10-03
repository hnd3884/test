package org.bouncycastle.cert.cmp;

import java.io.OutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.cmp.PKIHeader;
import java.io.IOException;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import java.util.Date;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.List;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;

public class ProtectedPKIMessageBuilder
{
    private PKIHeaderBuilder hdrBuilder;
    private PKIBody body;
    private List generalInfos;
    private List extraCerts;
    
    public ProtectedPKIMessageBuilder(final GeneralName generalName, final GeneralName generalName2) {
        this(2, generalName, generalName2);
    }
    
    public ProtectedPKIMessageBuilder(final int n, final GeneralName generalName, final GeneralName generalName2) {
        this.generalInfos = new ArrayList();
        this.extraCerts = new ArrayList();
        this.hdrBuilder = new PKIHeaderBuilder(n, generalName, generalName2);
    }
    
    public ProtectedPKIMessageBuilder setTransactionID(final byte[] transactionID) {
        this.hdrBuilder.setTransactionID(transactionID);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setFreeText(final PKIFreeText freeText) {
        this.hdrBuilder.setFreeText(freeText);
        return this;
    }
    
    public ProtectedPKIMessageBuilder addGeneralInfo(final InfoTypeAndValue infoTypeAndValue) {
        this.generalInfos.add(infoTypeAndValue);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setMessageTime(final Date date) {
        this.hdrBuilder.setMessageTime(new ASN1GeneralizedTime(date));
        return this;
    }
    
    public ProtectedPKIMessageBuilder setRecipKID(final byte[] recipKID) {
        this.hdrBuilder.setRecipKID(recipKID);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setRecipNonce(final byte[] recipNonce) {
        this.hdrBuilder.setRecipNonce(recipNonce);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setSenderKID(final byte[] senderKID) {
        this.hdrBuilder.setSenderKID(senderKID);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setSenderNonce(final byte[] senderNonce) {
        this.hdrBuilder.setSenderNonce(senderNonce);
        return this;
    }
    
    public ProtectedPKIMessageBuilder setBody(final PKIBody body) {
        this.body = body;
        return this;
    }
    
    public ProtectedPKIMessageBuilder addCMPCertificate(final X509CertificateHolder x509CertificateHolder) {
        this.extraCerts.add(x509CertificateHolder);
        return this;
    }
    
    public ProtectedPKIMessage build(final MacCalculator macCalculator) throws CMPException {
        this.finaliseHeader(macCalculator.getAlgorithmIdentifier());
        final PKIHeader build = this.hdrBuilder.build();
        try {
            return this.finaliseMessage(build, new DERBitString(this.calculateMac(macCalculator, build, this.body)));
        }
        catch (final IOException ex) {
            throw new CMPException("unable to encode MAC input: " + ex.getMessage(), ex);
        }
    }
    
    public ProtectedPKIMessage build(final ContentSigner contentSigner) throws CMPException {
        this.finaliseHeader(contentSigner.getAlgorithmIdentifier());
        final PKIHeader build = this.hdrBuilder.build();
        try {
            return this.finaliseMessage(build, new DERBitString(this.calculateSignature(contentSigner, build, this.body)));
        }
        catch (final IOException ex) {
            throw new CMPException("unable to encode signature input: " + ex.getMessage(), ex);
        }
    }
    
    private void finaliseHeader(final AlgorithmIdentifier protectionAlg) {
        this.hdrBuilder.setProtectionAlg(protectionAlg);
        if (!this.generalInfos.isEmpty()) {
            this.hdrBuilder.setGeneralInfo((InfoTypeAndValue[])this.generalInfos.toArray(new InfoTypeAndValue[this.generalInfos.size()]));
        }
    }
    
    private ProtectedPKIMessage finaliseMessage(final PKIHeader pkiHeader, final DERBitString derBitString) {
        if (!this.extraCerts.isEmpty()) {
            final CMPCertificate[] array = new CMPCertificate[this.extraCerts.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = new CMPCertificate(((X509CertificateHolder)this.extraCerts.get(i)).toASN1Structure());
            }
            return new ProtectedPKIMessage(new PKIMessage(pkiHeader, this.body, derBitString, array));
        }
        return new ProtectedPKIMessage(new PKIMessage(pkiHeader, this.body, derBitString));
    }
    
    private byte[] calculateSignature(final ContentSigner contentSigner, final PKIHeader pkiHeader, final PKIBody pkiBody) throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)pkiHeader);
        asn1EncodableVector.add((ASN1Encodable)pkiBody);
        final OutputStream outputStream = contentSigner.getOutputStream();
        outputStream.write(new DERSequence(asn1EncodableVector).getEncoded("DER"));
        outputStream.close();
        return contentSigner.getSignature();
    }
    
    private byte[] calculateMac(final MacCalculator macCalculator, final PKIHeader pkiHeader, final PKIBody pkiBody) throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)pkiHeader);
        asn1EncodableVector.add((ASN1Encodable)pkiBody);
        final OutputStream outputStream = macCalculator.getOutputStream();
        outputStream.write(new DERSequence(asn1EncodableVector).getEncoded("DER"));
        outputStream.close();
        return macCalculator.getMac();
    }
}
