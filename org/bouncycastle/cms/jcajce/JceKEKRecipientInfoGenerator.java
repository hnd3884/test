package org.bouncycastle.cms.jcajce;

import java.security.SecureRandom;
import java.security.Provider;
import org.bouncycastle.asn1.cms.OtherKeyAttribute;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyWrapper;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;

public class JceKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator
{
    public JceKEKRecipientInfoGenerator(final KEKIdentifier kekIdentifier, final SecretKey secretKey) {
        super(kekIdentifier, new JceSymmetricKeyWrapper(secretKey));
    }
    
    public JceKEKRecipientInfoGenerator(final byte[] array, final SecretKey secretKey) {
        this(new KEKIdentifier(array, (ASN1GeneralizedTime)null, (OtherKeyAttribute)null), secretKey);
    }
    
    public JceKEKRecipientInfoGenerator setProvider(final Provider provider) {
        ((JceSymmetricKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    public JceKEKRecipientInfoGenerator setProvider(final String provider) {
        ((JceSymmetricKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    public JceKEKRecipientInfoGenerator setSecureRandom(final SecureRandom secureRandom) {
        ((JceSymmetricKeyWrapper)this.wrapper).setSecureRandom(secureRandom);
        return this;
    }
}
