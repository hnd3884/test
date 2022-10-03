package org.bouncycastle.operator.jcajce;

import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.OperatorException;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public class JceSymmetricKeyWrapper extends SymmetricKeyWrapper
{
    private OperatorHelper helper;
    private SecureRandom random;
    private SecretKey wrappingKey;
    
    public JceSymmetricKeyWrapper(final SecretKey wrappingKey) {
        super(determineKeyEncAlg(wrappingKey));
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.wrappingKey = wrappingKey;
    }
    
    public JceSymmetricKeyWrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceSymmetricKeyWrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JceSymmetricKeyWrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public byte[] generateWrappedKey(final GenericKey genericKey) throws OperatorException {
        final Key jceKey = OperatorUtils.getJceKey(genericKey);
        final Cipher symmetricWrapper = this.helper.createSymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
        try {
            symmetricWrapper.init(3, this.wrappingKey, this.random);
            return symmetricWrapper.wrap(jceKey);
        }
        catch (final GeneralSecurityException ex) {
            throw new OperatorException("cannot wrap key: " + ex.getMessage(), ex);
        }
    }
    
    private static AlgorithmIdentifier determineKeyEncAlg(final SecretKey secretKey) {
        return determineKeyEncAlg(secretKey.getAlgorithm(), secretKey.getEncoded().length * 8);
    }
    
    static AlgorithmIdentifier determineKeyEncAlg(final String s, final int n) {
        if (s.startsWith("DES") || s.startsWith("TripleDES")) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, (ASN1Encodable)DERNull.INSTANCE);
        }
        if (s.startsWith("RC2")) {
            return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.7"), (ASN1Encodable)new ASN1Integer(58L));
        }
        if (s.startsWith("AES")) {
            ASN1ObjectIdentifier asn1ObjectIdentifier;
            if (n == 128) {
                asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_wrap;
            }
            else if (n == 192) {
                asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes192_wrap;
            }
            else {
                if (n != 256) {
                    throw new IllegalArgumentException("illegal keysize in AES");
                }
                asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes256_wrap;
            }
            return new AlgorithmIdentifier(asn1ObjectIdentifier);
        }
        if (s.startsWith("SEED")) {
            return new AlgorithmIdentifier(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap);
        }
        if (s.startsWith("Camellia")) {
            ASN1ObjectIdentifier asn1ObjectIdentifier2;
            if (n == 128) {
                asn1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia128_wrap;
            }
            else if (n == 192) {
                asn1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia192_wrap;
            }
            else {
                if (n != 256) {
                    throw new IllegalArgumentException("illegal keysize in Camellia");
                }
                asn1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia256_wrap;
            }
            return new AlgorithmIdentifier(asn1ObjectIdentifier2);
        }
        throw new IllegalArgumentException("unknown algorithm");
    }
}
