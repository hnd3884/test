package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public abstract class KeyAgreeRecipientInfoGenerator implements RecipientInfoGenerator
{
    private ASN1ObjectIdentifier keyAgreementOID;
    private ASN1ObjectIdentifier keyEncryptionOID;
    private SubjectPublicKeyInfo originatorKeyInfo;
    
    protected KeyAgreeRecipientInfoGenerator(final ASN1ObjectIdentifier keyAgreementOID, final SubjectPublicKeyInfo originatorKeyInfo, final ASN1ObjectIdentifier keyEncryptionOID) {
        this.originatorKeyInfo = originatorKeyInfo;
        this.keyAgreementOID = keyAgreementOID;
        this.keyEncryptionOID = keyEncryptionOID;
    }
    
    public RecipientInfo generate(final GenericKey genericKey) throws CMSException {
        final OriginatorIdentifierOrKey originatorIdentifierOrKey = new OriginatorIdentifierOrKey(this.createOriginatorPublicKey(this.originatorKeyInfo));
        AlgorithmIdentifier algorithmIdentifier;
        if (CMSUtils.isDES(this.keyEncryptionOID.getId()) || this.keyEncryptionOID.equals((Object)PKCSObjectIdentifiers.id_alg_CMSRC2wrap)) {
            algorithmIdentifier = new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)DERNull.INSTANCE);
        }
        else if (CMSUtils.isGOST(this.keyAgreementOID)) {
            algorithmIdentifier = new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet));
        }
        else {
            algorithmIdentifier = new AlgorithmIdentifier(this.keyEncryptionOID);
        }
        final AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(this.keyAgreementOID, (ASN1Encodable)algorithmIdentifier);
        final ASN1Sequence generateRecipientEncryptedKeys = this.generateRecipientEncryptedKeys(algorithmIdentifier2, algorithmIdentifier, genericKey);
        final byte[] userKeyingMaterial = this.getUserKeyingMaterial(algorithmIdentifier2);
        if (userKeyingMaterial != null) {
            return new RecipientInfo(new KeyAgreeRecipientInfo(originatorIdentifierOrKey, (ASN1OctetString)new DEROctetString(userKeyingMaterial), algorithmIdentifier2, generateRecipientEncryptedKeys));
        }
        return new RecipientInfo(new KeyAgreeRecipientInfo(originatorIdentifierOrKey, (ASN1OctetString)null, algorithmIdentifier2, generateRecipientEncryptedKeys));
    }
    
    protected OriginatorPublicKey createOriginatorPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new OriginatorPublicKey(new AlgorithmIdentifier(subjectPublicKeyInfo.getAlgorithm().getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), subjectPublicKeyInfo.getPublicKeyData().getBytes());
    }
    
    protected abstract ASN1Sequence generateRecipientEncryptedKeys(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1, final GenericKey p2) throws CMSException;
    
    protected abstract byte[] getUserKeyingMaterial(final AlgorithmIdentifier p0) throws CMSException;
}
