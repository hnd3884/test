package org.bouncycastle.cert.crmf;

import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.crmf.CertTemplate;
import java.io.IOException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.util.Encodable;

public class CertificateRequestMessage implements Encodable
{
    public static final int popRaVerified = 0;
    public static final int popSigningKey = 1;
    public static final int popKeyEncipherment = 2;
    public static final int popKeyAgreement = 3;
    private final CertReqMsg certReqMsg;
    private final Controls controls;
    
    private static CertReqMsg parseBytes(final byte[] array) throws IOException {
        try {
            return CertReqMsg.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final ClassCastException ex) {
            throw new CertIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CertIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public CertificateRequestMessage(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public CertificateRequestMessage(final CertReqMsg certReqMsg) {
        this.certReqMsg = certReqMsg;
        this.controls = certReqMsg.getCertReq().getControls();
    }
    
    public CertReqMsg toASN1Structure() {
        return this.certReqMsg;
    }
    
    public CertTemplate getCertTemplate() {
        return this.certReqMsg.getCertReq().getCertTemplate();
    }
    
    public boolean hasControls() {
        return this.controls != null;
    }
    
    public boolean hasControl(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.findControl(asn1ObjectIdentifier) != null;
    }
    
    public Control getControl(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final AttributeTypeAndValue control = this.findControl(asn1ObjectIdentifier);
        if (control != null) {
            if (control.getType().equals((Object)CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions)) {
                return new PKIArchiveControl(PKIArchiveOptions.getInstance((Object)control.getValue()));
            }
            if (control.getType().equals((Object)CRMFObjectIdentifiers.id_regCtrl_regToken)) {
                return new RegTokenControl(DERUTF8String.getInstance((Object)control.getValue()));
            }
            if (control.getType().equals((Object)CRMFObjectIdentifiers.id_regCtrl_authenticator)) {
                return new AuthenticatorControl(DERUTF8String.getInstance((Object)control.getValue()));
            }
        }
        return null;
    }
    
    private AttributeTypeAndValue findControl(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (this.controls == null) {
            return null;
        }
        final AttributeTypeAndValue[] attributeTypeAndValueArray = this.controls.toAttributeTypeAndValueArray();
        AttributeTypeAndValue attributeTypeAndValue = null;
        for (int i = 0; i != attributeTypeAndValueArray.length; ++i) {
            if (attributeTypeAndValueArray[i].getType().equals((Object)asn1ObjectIdentifier)) {
                attributeTypeAndValue = attributeTypeAndValueArray[i];
                break;
            }
        }
        return attributeTypeAndValue;
    }
    
    public boolean hasProofOfPossession() {
        return this.certReqMsg.getPopo() != null;
    }
    
    public int getProofOfPossessionType() {
        return this.certReqMsg.getPopo().getType();
    }
    
    public boolean hasSigningKeyProofOfPossessionWithPKMAC() {
        final ProofOfPossession popo = this.certReqMsg.getPopo();
        return popo.getType() == 1 && POPOSigningKey.getInstance((Object)popo.getObject()).getPoposkInput().getPublicKeyMAC() != null;
    }
    
    public boolean isValidSigningKeyPOP(final ContentVerifierProvider contentVerifierProvider) throws CRMFException, IllegalStateException {
        final ProofOfPossession popo = this.certReqMsg.getPopo();
        if (popo.getType() != 1) {
            throw new IllegalStateException("not Signing Key type of proof of possession");
        }
        final POPOSigningKey instance = POPOSigningKey.getInstance((Object)popo.getObject());
        if (instance.getPoposkInput() != null && instance.getPoposkInput().getPublicKeyMAC() != null) {
            throw new IllegalStateException("verification requires password check");
        }
        return this.verifySignature(contentVerifierProvider, instance);
    }
    
    public boolean isValidSigningKeyPOP(final ContentVerifierProvider contentVerifierProvider, final PKMACBuilder pkmacBuilder, final char[] array) throws CRMFException, IllegalStateException {
        final ProofOfPossession popo = this.certReqMsg.getPopo();
        if (popo.getType() != 1) {
            throw new IllegalStateException("not Signing Key type of proof of possession");
        }
        final POPOSigningKey instance = POPOSigningKey.getInstance((Object)popo.getObject());
        if (instance.getPoposkInput() == null || instance.getPoposkInput().getSender() != null) {
            throw new IllegalStateException("no PKMAC present in proof of possession");
        }
        return new PKMACValueVerifier(pkmacBuilder).isValid(instance.getPoposkInput().getPublicKeyMAC(), array, this.getCertTemplate().getPublicKey()) && this.verifySignature(contentVerifierProvider, instance);
    }
    
    private boolean verifySignature(final ContentVerifierProvider contentVerifierProvider, final POPOSigningKey popoSigningKey) throws CRMFException {
        ContentVerifier value;
        try {
            value = contentVerifierProvider.get(popoSigningKey.getAlgorithmIdentifier());
        }
        catch (final OperatorCreationException ex) {
            throw new CRMFException("unable to create verifier: " + ex.getMessage(), ex);
        }
        if (popoSigningKey.getPoposkInput() != null) {
            CRMFUtil.derEncodeToStream((ASN1Encodable)popoSigningKey.getPoposkInput(), value.getOutputStream());
        }
        else {
            CRMFUtil.derEncodeToStream((ASN1Encodable)this.certReqMsg.getCertReq(), value.getOutputStream());
        }
        return value.verify(popoSigningKey.getSignature().getOctets());
    }
    
    public byte[] getEncoded() throws IOException {
        return this.certReqMsg.getEncoded();
    }
}
