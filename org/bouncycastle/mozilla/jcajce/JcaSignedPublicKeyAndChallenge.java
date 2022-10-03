package org.bouncycastle.mozilla.jcajce;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.mozilla.SignedPublicKeyAndChallenge;

public class JcaSignedPublicKeyAndChallenge extends SignedPublicKeyAndChallenge
{
    JcaJceHelper helper;
    
    private JcaSignedPublicKeyAndChallenge(final org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge signedPublicKeyAndChallenge, final JcaJceHelper helper) {
        super(signedPublicKeyAndChallenge);
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.helper = helper;
    }
    
    public JcaSignedPublicKeyAndChallenge(final byte[] array) {
        super(array);
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcaSignedPublicKeyAndChallenge setProvider(final String s) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, (JcaJceHelper)new NamedJcaJceHelper(s));
    }
    
    public JcaSignedPublicKeyAndChallenge setProvider(final Provider provider) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, (JcaJceHelper)new ProviderJcaJceHelper(provider));
    }
    
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        try {
            final SubjectPublicKeyInfo subjectPublicKeyInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
            return this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("error encoding public key");
        }
    }
}
