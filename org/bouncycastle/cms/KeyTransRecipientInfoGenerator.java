package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public abstract class KeyTransRecipientInfoGenerator implements RecipientInfoGenerator
{
    protected final AsymmetricKeyWrapper wrapper;
    private IssuerAndSerialNumber issuerAndSerial;
    private byte[] subjectKeyIdentifier;
    
    protected KeyTransRecipientInfoGenerator(final IssuerAndSerialNumber issuerAndSerial, final AsymmetricKeyWrapper wrapper) {
        this.issuerAndSerial = issuerAndSerial;
        this.wrapper = wrapper;
    }
    
    protected KeyTransRecipientInfoGenerator(final byte[] subjectKeyIdentifier, final AsymmetricKeyWrapper wrapper) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.wrapper = wrapper;
    }
    
    public final RecipientInfo generate(final GenericKey genericKey) throws CMSException {
        byte[] generateWrappedKey;
        try {
            generateWrappedKey = this.wrapper.generateWrappedKey(genericKey);
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception wrapping content key: " + ex.getMessage(), ex);
        }
        RecipientIdentifier recipientIdentifier;
        if (this.issuerAndSerial != null) {
            recipientIdentifier = new RecipientIdentifier(this.issuerAndSerial);
        }
        else {
            recipientIdentifier = new RecipientIdentifier((ASN1OctetString)new DEROctetString(this.subjectKeyIdentifier));
        }
        return new RecipientInfo(new KeyTransRecipientInfo(recipientIdentifier, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)new DEROctetString(generateWrappedKey)));
    }
}
