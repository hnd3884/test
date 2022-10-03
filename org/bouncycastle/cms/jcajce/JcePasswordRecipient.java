package org.bouncycastle.cms.jcajce;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import org.bouncycastle.cms.CMSException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import org.bouncycastle.cms.PasswordRecipient;

public abstract class JcePasswordRecipient implements PasswordRecipient
{
    private int schemeID;
    protected EnvelopedDataHelper helper;
    private char[] password;
    
    JcePasswordRecipient(final char[] password) {
        this.schemeID = 1;
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.password = password;
    }
    
    public JcePasswordRecipient setPasswordConversionScheme(final int schemeID) {
        this.schemeID = schemeID;
        return this;
    }
    
    public JcePasswordRecipient setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JcePasswordRecipient setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    protected Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array, final byte[] array2) throws CMSException {
        final Cipher rfc3211Wrapper = this.helper.createRFC3211Wrapper(algorithmIdentifier.getAlgorithm());
        try {
            rfc3211Wrapper.init(4, new SecretKeySpec(array, rfc3211Wrapper.getAlgorithm()), new IvParameterSpec(ASN1OctetString.getInstance((Object)algorithmIdentifier.getParameters()).getOctets()));
            return rfc3211Wrapper.unwrap(array2, algorithmIdentifier2.getAlgorithm().getId(), 3);
        }
        catch (final GeneralSecurityException ex) {
            throw new CMSException("cannot process content encryption key: " + ex.getMessage(), ex);
        }
    }
    
    public byte[] calculateDerivedKey(final int n, final AlgorithmIdentifier algorithmIdentifier, final int n2) throws CMSException {
        return this.helper.calculateDerivedKey(n, this.password, algorithmIdentifier, n2);
    }
    
    public int getPasswordConversionScheme() {
        return this.schemeID;
    }
    
    public char[] getPassword() {
        return this.password;
    }
}
