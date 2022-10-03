package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.crmf.CertTemplate;
import java.util.Iterator;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.operator.ContentSigner;
import java.util.List;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import java.math.BigInteger;

public class CertificateRequestMessageBuilder
{
    private final BigInteger certReqId;
    private ExtensionsGenerator extGenerator;
    private CertTemplateBuilder templateBuilder;
    private List controls;
    private ContentSigner popSigner;
    private PKMACBuilder pkmacBuilder;
    private char[] password;
    private GeneralName sender;
    private POPOPrivKey popoPrivKey;
    private ASN1Null popRaVerified;
    
    public CertificateRequestMessageBuilder(final BigInteger certReqId) {
        this.certReqId = certReqId;
        this.extGenerator = new ExtensionsGenerator();
        this.templateBuilder = new CertTemplateBuilder();
        this.controls = new ArrayList();
    }
    
    public CertificateRequestMessageBuilder setPublicKey(final SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }
    
    public CertificateRequestMessageBuilder setIssuer(final X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }
    
    public CertificateRequestMessageBuilder setSubject(final X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }
    
    public CertificateRequestMessageBuilder setSerialNumber(final BigInteger bigInteger) {
        if (bigInteger != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(bigInteger));
        }
        return this;
    }
    
    public CertificateRequestMessageBuilder setValidity(final Date date, final Date date2) {
        this.templateBuilder.setValidity(new OptionalValidity(this.createTime(date), this.createTime(date2)));
        return this;
    }
    
    private Time createTime(final Date date) {
        if (date != null) {
            return new Time(date);
        }
        return null;
    }
    
    public CertificateRequestMessageBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        CRMFUtil.addExtension(this.extGenerator, asn1ObjectIdentifier, b, asn1Encodable);
        return this;
    }
    
    public CertificateRequestMessageBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) {
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, array);
        return this;
    }
    
    public CertificateRequestMessageBuilder addControl(final Control control) {
        this.controls.add(control);
        return this;
    }
    
    public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(final ContentSigner popSigner) {
        if (this.popoPrivKey != null || this.popRaVerified != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popSigner = popSigner;
        return this;
    }
    
    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(final SubsequentMessage subsequentMessage) {
        if (this.popSigner != null || this.popRaVerified != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popoPrivKey = new POPOPrivKey(subsequentMessage);
        return this;
    }
    
    public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
        if (this.popSigner != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popRaVerified = (ASN1Null)DERNull.INSTANCE;
        return this;
    }
    
    public CertificateRequestMessageBuilder setAuthInfoPKMAC(final PKMACBuilder pkmacBuilder, final char[] password) {
        this.pkmacBuilder = pkmacBuilder;
        this.password = password;
        return this;
    }
    
    public CertificateRequestMessageBuilder setAuthInfoSender(final X500Name x500Name) {
        return this.setAuthInfoSender(new GeneralName(x500Name));
    }
    
    public CertificateRequestMessageBuilder setAuthInfoSender(final GeneralName sender) {
        this.sender = sender;
        return this;
    }
    
    public CertificateRequestMessage build() throws CRMFException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.certReqId));
        if (!this.extGenerator.isEmpty()) {
            this.templateBuilder.setExtensions(this.extGenerator.generate());
        }
        asn1EncodableVector.add((ASN1Encodable)this.templateBuilder.build());
        if (!this.controls.isEmpty()) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            for (final Control control : this.controls) {
                asn1EncodableVector2.add((ASN1Encodable)new AttributeTypeAndValue(control.getType(), control.getValue()));
            }
            asn1EncodableVector.add((ASN1Encodable)new DERSequence(asn1EncodableVector2));
        }
        final CertRequest instance = CertRequest.getInstance((Object)new DERSequence(asn1EncodableVector));
        final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
        asn1EncodableVector3.add((ASN1Encodable)instance);
        if (this.popSigner != null) {
            final CertTemplate certTemplate = instance.getCertTemplate();
            if (certTemplate.getSubject() == null || certTemplate.getPublicKey() == null) {
                final ProofOfPossessionSigningKeyBuilder proofOfPossessionSigningKeyBuilder = new ProofOfPossessionSigningKeyBuilder(instance.getCertTemplate().getPublicKey());
                if (this.sender != null) {
                    proofOfPossessionSigningKeyBuilder.setSender(this.sender);
                }
                else {
                    proofOfPossessionSigningKeyBuilder.setPublicKeyMac(new PKMACValueGenerator(this.pkmacBuilder), this.password);
                }
                asn1EncodableVector3.add((ASN1Encodable)new ProofOfPossession(proofOfPossessionSigningKeyBuilder.build(this.popSigner)));
            }
            else {
                asn1EncodableVector3.add((ASN1Encodable)new ProofOfPossession(new ProofOfPossessionSigningKeyBuilder(instance).build(this.popSigner)));
            }
        }
        else if (this.popoPrivKey != null) {
            asn1EncodableVector3.add((ASN1Encodable)new ProofOfPossession(2, this.popoPrivKey));
        }
        else if (this.popRaVerified != null) {
            asn1EncodableVector3.add((ASN1Encodable)new ProofOfPossession());
        }
        return new CertificateRequestMessage(CertReqMsg.getInstance((Object)new DERSequence(asn1EncodableVector3)));
    }
}
