package org.bouncycastle.operator.jcajce;

import javax.crypto.Cipher;
import org.bouncycastle.operator.OperatorException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.crypto.util.DEROtherInfo;
import java.util.Map;
import java.util.HashMap;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.SecureRandom;
import java.security.PublicKey;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public class JceKTSKeyWrapper extends AsymmetricKeyWrapper
{
    private final String symmetricWrappingAlg;
    private final int keySizeInBits;
    private final byte[] partyUInfo;
    private final byte[] partyVInfo;
    private OperatorHelper helper;
    private PublicKey publicKey;
    private SecureRandom random;
    
    public JceKTSKeyWrapper(final PublicKey publicKey, final String symmetricWrappingAlg, final int keySizeInBits, final byte[] array, final byte[] array2) {
        super(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_rsa_KEM, (ASN1Encodable)new GenericHybridParameters(new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa, (ASN1Encodable)new RsaKemParameters(new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, (ASN1Encodable)new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)), (keySizeInBits + 7) / 8)), JceSymmetricKeyWrapper.determineKeyEncAlg(symmetricWrappingAlg, keySizeInBits))));
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.publicKey = publicKey;
        this.symmetricWrappingAlg = symmetricWrappingAlg;
        this.keySizeInBits = keySizeInBits;
        this.partyUInfo = Arrays.clone(array);
        this.partyVInfo = Arrays.clone(array2);
    }
    
    public JceKTSKeyWrapper(final X509Certificate x509Certificate, final String s, final int n, final byte[] array, final byte[] array2) {
        this(x509Certificate.getPublicKey(), s, n, array, array2);
    }
    
    public JceKTSKeyWrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceKTSKeyWrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JceKTSKeyWrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public byte[] generateWrappedKey(final GenericKey genericKey) throws OperatorException {
        final Cipher asymmetricWrapper = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
        try {
            asymmetricWrapper.init(3, this.publicKey, (AlgorithmParameterSpec)new KTSParameterSpec.Builder(this.symmetricWrappingAlg, this.keySizeInBits, new DEROtherInfo.Builder(JceSymmetricKeyWrapper.determineKeyEncAlg(this.symmetricWrappingAlg, this.keySizeInBits), this.partyUInfo, this.partyVInfo).build().getEncoded()).build(), this.random);
            return asymmetricWrapper.wrap(OperatorUtils.getJceKey(genericKey));
        }
        catch (final Exception ex) {
            throw new OperatorException("Unable to wrap contents key: " + ex.getMessage(), ex);
        }
    }
}
