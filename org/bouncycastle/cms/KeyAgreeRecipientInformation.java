package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.List;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;

public class KeyAgreeRecipientInformation extends RecipientInformation
{
    private KeyAgreeRecipientInfo info;
    private ASN1OctetString encryptedKey;
    
    static void readRecipientInfo(final List list, final KeyAgreeRecipientInfo keyAgreeRecipientInfo, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        final ASN1Sequence recipientEncryptedKeys = keyAgreeRecipientInfo.getRecipientEncryptedKeys();
        for (int i = 0; i < recipientEncryptedKeys.size(); ++i) {
            final RecipientEncryptedKey instance = RecipientEncryptedKey.getInstance((Object)recipientEncryptedKeys.getObjectAt(i));
            final KeyAgreeRecipientIdentifier identifier = instance.getIdentifier();
            final IssuerAndSerialNumber issuerAndSerialNumber = identifier.getIssuerAndSerialNumber();
            KeyAgreeRecipientId keyAgreeRecipientId;
            if (issuerAndSerialNumber != null) {
                keyAgreeRecipientId = new KeyAgreeRecipientId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
            }
            else {
                keyAgreeRecipientId = new KeyAgreeRecipientId(identifier.getRKeyID().getSubjectKeyIdentifier().getOctets());
            }
            list.add(new KeyAgreeRecipientInformation(keyAgreeRecipientInfo, keyAgreeRecipientId, instance.getEncryptedKey(), algorithmIdentifier, cmsSecureReadable, authAttributesProvider));
        }
    }
    
    KeyAgreeRecipientInformation(final KeyAgreeRecipientInfo info, final RecipientId rid, final ASN1OctetString encryptedKey, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        super(info.getKeyEncryptionAlgorithm(), algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        this.info = info;
        this.rid = rid;
        this.encryptedKey = encryptedKey;
    }
    
    private SubjectPublicKeyInfo getSenderPublicKeyInfo(final AlgorithmIdentifier algorithmIdentifier, final OriginatorIdentifierOrKey originatorIdentifierOrKey) throws CMSException, IOException {
        final OriginatorPublicKey originatorKey = originatorIdentifierOrKey.getOriginatorKey();
        if (originatorKey != null) {
            return this.getPublicKeyInfoFromOriginatorPublicKey(algorithmIdentifier, originatorKey);
        }
        final IssuerAndSerialNumber issuerAndSerialNumber = originatorIdentifierOrKey.getIssuerAndSerialNumber();
        OriginatorId originatorId;
        if (issuerAndSerialNumber != null) {
            originatorId = new OriginatorId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
        }
        else {
            originatorId = new OriginatorId(originatorIdentifierOrKey.getSubjectKeyIdentifier().getKeyIdentifier());
        }
        return this.getPublicKeyInfoFromOriginatorId(originatorId);
    }
    
    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(final AlgorithmIdentifier algorithmIdentifier, final OriginatorPublicKey originatorPublicKey) {
        return new SubjectPublicKeyInfo(algorithmIdentifier, originatorPublicKey.getPublicKey().getBytes());
    }
    
    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(final OriginatorId originatorId) throws CMSException {
        throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
    }
    
    @Override
    protected RecipientOperator getRecipientOperator(final Recipient recipient) throws CMSException, IOException {
        return ((KeyAgreeRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.getSenderPublicKeyInfo(((KeyAgreeRecipient)recipient).getPrivateKeyAlgorithmIdentifier(), this.info.getOriginator()), this.info.getUserKeyingMaterial(), this.encryptedKey.getOctets());
    }
}
