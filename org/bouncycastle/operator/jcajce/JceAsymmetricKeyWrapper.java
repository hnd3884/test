package org.bouncycastle.operator.jcajce;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.spec.MGF1ParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import javax.crypto.spec.PSource;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import org.bouncycastle.operator.OperatorException;
import java.security.ProviderException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.SecureRandom;
import java.security.PublicKey;
import java.util.Map;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public class JceAsymmetricKeyWrapper extends AsymmetricKeyWrapper
{
    private OperatorHelper helper;
    private Map extraMappings;
    private PublicKey publicKey;
    private SecureRandom random;
    private static final Map digests;
    
    public JceAsymmetricKeyWrapper(final PublicKey publicKey) {
        super(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()).getAlgorithm());
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }
    
    public JceAsymmetricKeyWrapper(final X509Certificate x509Certificate) {
        this(x509Certificate.getPublicKey());
    }
    
    public JceAsymmetricKeyWrapper(final AlgorithmIdentifier algorithmIdentifier, final PublicKey publicKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }
    
    public JceAsymmetricKeyWrapper(final AlgorithmParameterSpec algorithmParameterSpec, final PublicKey publicKey) {
        super(extractFromSpec(algorithmParameterSpec));
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }
    
    public JceAsymmetricKeyWrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceAsymmetricKeyWrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JceAsymmetricKeyWrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JceAsymmetricKeyWrapper setAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        this.extraMappings.put(asn1ObjectIdentifier, s);
        return this;
    }
    
    public byte[] generateWrappedKey(final GenericKey genericKey) throws OperatorException {
        final Cipher asymmetricWrapper = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
        final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(this.getAlgorithmIdentifier());
        byte[] array = null;
        try {
            if (algorithmParameters != null) {
                asymmetricWrapper.init(3, this.publicKey, algorithmParameters, this.random);
            }
            else {
                asymmetricWrapper.init(3, this.publicKey, this.random);
            }
            array = asymmetricWrapper.wrap(OperatorUtils.getJceKey(genericKey));
        }
        catch (final InvalidKeyException ex) {}
        catch (final GeneralSecurityException ex2) {}
        catch (final IllegalStateException ex3) {}
        catch (final UnsupportedOperationException ex4) {}
        catch (final ProviderException ex5) {}
        if (array == null) {
            try {
                asymmetricWrapper.init(1, this.publicKey, this.random);
                array = asymmetricWrapper.doFinal(OperatorUtils.getJceKey(genericKey).getEncoded());
            }
            catch (final InvalidKeyException ex6) {
                throw new OperatorException("unable to encrypt contents key", ex6);
            }
            catch (final GeneralSecurityException ex7) {
                throw new OperatorException("unable to encrypt contents key", ex7);
            }
        }
        return array;
    }
    
    private static AlgorithmIdentifier extractFromSpec(final AlgorithmParameterSpec algorithmParameterSpec) {
        if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
            throw new IllegalArgumentException("unknown spec: " + algorithmParameterSpec.getClass().getName());
        }
        final OAEPParameterSpec oaepParameterSpec = (OAEPParameterSpec)algorithmParameterSpec;
        if (!oaepParameterSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
            throw new IllegalArgumentException("unknown MGF: " + oaepParameterSpec.getMGFAlgorithm());
        }
        if (oaepParameterSpec.getPSource() instanceof PSource.PSpecified) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, (ASN1Encodable)new RSAESOAEPparams(getDigest(oaepParameterSpec.getDigestAlgorithm()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)getDigest(((MGF1ParameterSpec)oaepParameterSpec.getMGFParameters()).getDigestAlgorithm())), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(((PSource.PSpecified)oaepParameterSpec.getPSource()).getValue()))));
        }
        throw new IllegalArgumentException("unknown PSource: " + oaepParameterSpec.getPSource().getAlgorithm());
    }
    
    private static AlgorithmIdentifier getDigest(final String s) {
        final AlgorithmIdentifier algorithmIdentifier = JceAsymmetricKeyWrapper.digests.get(s);
        if (algorithmIdentifier != null) {
            return algorithmIdentifier;
        }
        throw new IllegalArgumentException("unknown digest name: " + s);
    }
    
    static {
        (digests = new HashMap()).put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-512(224)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
        JceAsymmetricKeyWrapper.digests.put("SHA-512(256)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
    }
}
