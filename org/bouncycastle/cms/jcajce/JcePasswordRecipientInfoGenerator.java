package org.bouncycastle.cms.jcajce;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;

public class JcePasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator
{
    private EnvelopedDataHelper helper;
    
    public JcePasswordRecipientInfoGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final char[] array) {
        super(asn1ObjectIdentifier, array);
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }
    
    public JcePasswordRecipientInfoGenerator setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JcePasswordRecipientInfoGenerator setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    @Override
    protected byte[] calculateDerivedKey(final int n, final AlgorithmIdentifier algorithmIdentifier, final int n2) throws CMSException {
        return this.helper.calculateDerivedKey(n, this.password, algorithmIdentifier, n2);
    }
    
    public byte[] generateEncryptedBytes(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final GenericKey genericKey) throws CMSException {
        final Key jceKey = this.helper.getJceKey(genericKey);
        final Cipher rfc3211Wrapper = this.helper.createRFC3211Wrapper(algorithmIdentifier.getAlgorithm());
        try {
            rfc3211Wrapper.init(3, new SecretKeySpec(array, rfc3211Wrapper.getAlgorithm()), new IvParameterSpec(ASN1OctetString.getInstance((Object)algorithmIdentifier.getParameters()).getOctets()));
            return rfc3211Wrapper.wrap(jceKey);
        }
        catch (final GeneralSecurityException ex) {
            throw new CMSException("cannot process content encryption key: " + ex.getMessage(), ex);
        }
    }
}
