package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.POPOSigningKeyInput;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.crmf.CertRequest;

public class ProofOfPossessionSigningKeyBuilder
{
    private CertRequest certRequest;
    private SubjectPublicKeyInfo pubKeyInfo;
    private GeneralName name;
    private PKMACValue publicKeyMAC;
    
    public ProofOfPossessionSigningKeyBuilder(final CertRequest certRequest) {
        this.certRequest = certRequest;
    }
    
    public ProofOfPossessionSigningKeyBuilder(final SubjectPublicKeyInfo pubKeyInfo) {
        this.pubKeyInfo = pubKeyInfo;
    }
    
    public ProofOfPossessionSigningKeyBuilder setSender(final GeneralName name) {
        this.name = name;
        return this;
    }
    
    public ProofOfPossessionSigningKeyBuilder setPublicKeyMac(final PKMACValueGenerator pkmacValueGenerator, final char[] array) throws CRMFException {
        this.publicKeyMAC = pkmacValueGenerator.generate(array, this.pubKeyInfo);
        return this;
    }
    
    public POPOSigningKey build(final ContentSigner contentSigner) {
        if (this.name != null && this.publicKeyMAC != null) {
            throw new IllegalStateException("name and publicKeyMAC cannot both be set.");
        }
        POPOSigningKeyInput popoSigningKeyInput;
        if (this.certRequest != null) {
            popoSigningKeyInput = null;
            CRMFUtil.derEncodeToStream((ASN1Encodable)this.certRequest, contentSigner.getOutputStream());
        }
        else if (this.name != null) {
            popoSigningKeyInput = new POPOSigningKeyInput(this.name, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream((ASN1Encodable)popoSigningKeyInput, contentSigner.getOutputStream());
        }
        else {
            popoSigningKeyInput = new POPOSigningKeyInput(this.publicKeyMAC, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream((ASN1Encodable)popoSigningKeyInput, contentSigner.getOutputStream());
        }
        return new POPOSigningKey(popoSigningKeyInput, contentSigner.getAlgorithmIdentifier(), new DERBitString(contentSigner.getSignature()));
    }
}
