package org.bouncycastle.cms;

import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.asn1.cms.KEKIdentifier;

public abstract class KEKRecipientInfoGenerator implements RecipientInfoGenerator
{
    private final KEKIdentifier kekIdentifier;
    protected final SymmetricKeyWrapper wrapper;
    
    protected KEKRecipientInfoGenerator(final KEKIdentifier kekIdentifier, final SymmetricKeyWrapper wrapper) {
        this.kekIdentifier = kekIdentifier;
        this.wrapper = wrapper;
    }
    
    public final RecipientInfo generate(final GenericKey genericKey) throws CMSException {
        try {
            return new RecipientInfo(new KEKRecipientInfo(this.kekIdentifier, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)new DEROctetString(this.wrapper.generateWrappedKey(genericKey))));
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception wrapping content key: " + ex.getMessage(), ex);
        }
    }
}
