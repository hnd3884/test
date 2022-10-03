package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.cms.OtherKeyAttribute;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.bc.BcSymmetricKeyWrapper;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;

public class BcKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator
{
    public BcKEKRecipientInfoGenerator(final KEKIdentifier kekIdentifier, final BcSymmetricKeyWrapper bcSymmetricKeyWrapper) {
        super(kekIdentifier, bcSymmetricKeyWrapper);
    }
    
    public BcKEKRecipientInfoGenerator(final byte[] array, final BcSymmetricKeyWrapper bcSymmetricKeyWrapper) {
        this(new KEKIdentifier(array, (ASN1GeneralizedTime)null, (OtherKeyAttribute)null), bcSymmetricKeyWrapper);
    }
}
