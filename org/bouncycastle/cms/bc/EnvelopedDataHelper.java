package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Collections;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import java.util.Map;

class EnvelopedDataHelper
{
    protected static final Map BASE_CIPHER_NAMES;
    protected static final Map MAC_ALG_NAMES;
    private static final Map prfs;
    private static final short[] rc2Table;
    private static final short[] rc2Ekb;
    
    private static Map createTable() {
        final HashMap hashMap = new HashMap();
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA1, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA1Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA224, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA224Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA256, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA256Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA384, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA384Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA512, new BcDigestProvider() {
            public ExtendedDigest get(final AlgorithmIdentifier algorithmIdentifier) {
                return (ExtendedDigest)new SHA512Digest();
            }
        });
        return Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    String getBaseCipherName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s == null) {
            return asn1ObjectIdentifier.getId();
        }
        return s;
    }
    
    static ExtendedDigest getPRF(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return EnvelopedDataHelper.prfs.get(algorithmIdentifier.getAlgorithm()).get(null);
    }
    
    static BufferedBlockCipher createCipher(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        CBCBlockCipher cbcBlockCipher;
        if (NISTObjectIdentifiers.id_aes128_CBC.equals((Object)asn1ObjectIdentifier) || NISTObjectIdentifiers.id_aes192_CBC.equals((Object)asn1ObjectIdentifier) || NISTObjectIdentifiers.id_aes256_CBC.equals((Object)asn1ObjectIdentifier)) {
            cbcBlockCipher = new CBCBlockCipher((BlockCipher)new AESEngine());
        }
        else if (PKCSObjectIdentifiers.des_EDE3_CBC.equals((Object)asn1ObjectIdentifier)) {
            cbcBlockCipher = new CBCBlockCipher((BlockCipher)new DESedeEngine());
        }
        else if (OIWObjectIdentifiers.desCBC.equals((Object)asn1ObjectIdentifier)) {
            cbcBlockCipher = new CBCBlockCipher((BlockCipher)new DESEngine());
        }
        else if (PKCSObjectIdentifiers.RC2_CBC.equals((Object)asn1ObjectIdentifier)) {
            cbcBlockCipher = new CBCBlockCipher((BlockCipher)new RC2Engine());
        }
        else {
            if (!MiscObjectIdentifiers.cast5CBC.equals((Object)asn1ObjectIdentifier)) {
                throw new CMSException("cannot recognise cipher: " + asn1ObjectIdentifier);
            }
            cbcBlockCipher = new CBCBlockCipher((BlockCipher)new CAST5Engine());
        }
        return (BufferedBlockCipher)new PaddedBufferedBlockCipher((BlockCipher)cbcBlockCipher, (BlockCipherPadding)new PKCS7Padding());
    }
    
    static Wrapper createRFC3211Wrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        if (NISTObjectIdentifiers.id_aes128_CBC.equals((Object)asn1ObjectIdentifier) || NISTObjectIdentifiers.id_aes192_CBC.equals((Object)asn1ObjectIdentifier) || NISTObjectIdentifiers.id_aes256_CBC.equals((Object)asn1ObjectIdentifier)) {
            return (Wrapper)new RFC3211WrapEngine((BlockCipher)new AESEngine());
        }
        if (PKCSObjectIdentifiers.des_EDE3_CBC.equals((Object)asn1ObjectIdentifier)) {
            return (Wrapper)new RFC3211WrapEngine((BlockCipher)new DESedeEngine());
        }
        if (OIWObjectIdentifiers.desCBC.equals((Object)asn1ObjectIdentifier)) {
            return (Wrapper)new RFC3211WrapEngine((BlockCipher)new DESEngine());
        }
        if (PKCSObjectIdentifiers.RC2_CBC.equals((Object)asn1ObjectIdentifier)) {
            return (Wrapper)new RFC3211WrapEngine((BlockCipher)new RC2Engine());
        }
        throw new CMSException("cannot recognise wrapper: " + asn1ObjectIdentifier);
    }
    
    static Object createContentCipher(final boolean b, final CipherParameters cipherParameters, final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        if (algorithm.equals((Object)PKCSObjectIdentifiers.rc4)) {
            final RC4Engine rc4Engine = new RC4Engine();
            ((StreamCipher)rc4Engine).init(b, cipherParameters);
            return rc4Engine;
        }
        final BufferedBlockCipher cipher = createCipher(algorithmIdentifier.getAlgorithm());
        final ASN1Primitive asn1Primitive = algorithmIdentifier.getParameters().toASN1Primitive();
        if (asn1Primitive != null && !(asn1Primitive instanceof ASN1Null)) {
            if (algorithm.equals((Object)CMSAlgorithm.DES_EDE3_CBC) || algorithm.equals((Object)CMSAlgorithm.IDEA_CBC) || algorithm.equals((Object)CMSAlgorithm.AES128_CBC) || algorithm.equals((Object)CMSAlgorithm.AES192_CBC) || algorithm.equals((Object)CMSAlgorithm.AES256_CBC) || algorithm.equals((Object)CMSAlgorithm.CAMELLIA128_CBC) || algorithm.equals((Object)CMSAlgorithm.CAMELLIA192_CBC) || algorithm.equals((Object)CMSAlgorithm.CAMELLIA256_CBC) || algorithm.equals((Object)CMSAlgorithm.SEED_CBC) || algorithm.equals((Object)OIWObjectIdentifiers.desCBC)) {
                cipher.init(b, (CipherParameters)new ParametersWithIV(cipherParameters, ASN1OctetString.getInstance((Object)asn1Primitive).getOctets()));
            }
            else if (algorithm.equals((Object)CMSAlgorithm.CAST5_CBC)) {
                cipher.init(b, (CipherParameters)new ParametersWithIV(cipherParameters, CAST5CBCParameters.getInstance((Object)asn1Primitive).getIV()));
            }
            else {
                if (!algorithm.equals((Object)CMSAlgorithm.RC2_CBC)) {
                    throw new CMSException("cannot match parameters");
                }
                final RC2CBCParameter instance = RC2CBCParameter.getInstance((Object)asn1Primitive);
                cipher.init(b, (CipherParameters)new ParametersWithIV((CipherParameters)new RC2Parameters(((KeyParameter)cipherParameters).getKey(), (int)EnvelopedDataHelper.rc2Ekb[instance.getRC2ParameterVersion().intValue()]), instance.getIV()));
            }
        }
        else if (algorithm.equals((Object)CMSAlgorithm.DES_EDE3_CBC) || algorithm.equals((Object)CMSAlgorithm.IDEA_CBC) || algorithm.equals((Object)CMSAlgorithm.CAST5_CBC)) {
            cipher.init(b, (CipherParameters)new ParametersWithIV(cipherParameters, new byte[8]));
        }
        else {
            cipher.init(b, cipherParameters);
        }
        return cipher;
    }
    
    AlgorithmIdentifier generateAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final CipherParameters cipherParameters, final SecureRandom secureRandom) throws CMSException {
        if (asn1ObjectIdentifier.equals((Object)CMSAlgorithm.AES128_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.AES192_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.AES256_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.CAMELLIA128_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.CAMELLIA192_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.CAMELLIA256_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.SEED_CBC)) {
            final byte[] array = new byte[16];
            secureRandom.nextBytes(array);
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new DEROctetString(array));
        }
        if (asn1ObjectIdentifier.equals((Object)CMSAlgorithm.DES_EDE3_CBC) || asn1ObjectIdentifier.equals((Object)CMSAlgorithm.IDEA_CBC) || asn1ObjectIdentifier.equals((Object)OIWObjectIdentifiers.desCBC)) {
            final byte[] array2 = new byte[8];
            secureRandom.nextBytes(array2);
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new DEROctetString(array2));
        }
        if (asn1ObjectIdentifier.equals((Object)CMSAlgorithm.CAST5_CBC)) {
            final byte[] array3 = new byte[8];
            secureRandom.nextBytes(array3);
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new CAST5CBCParameters(array3, ((KeyParameter)cipherParameters).getKey().length * 8));
        }
        if (asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.rc4)) {
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
        }
        if (asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.RC2_CBC)) {
            final byte[] array4 = new byte[8];
            secureRandom.nextBytes(array4);
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new RC2CBCParameter((int)EnvelopedDataHelper.rc2Table[128], array4));
        }
        throw new CMSException("unable to match algorithm");
    }
    
    CipherKeyGenerator createKeyGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final SecureRandom secureRandom) throws CMSException {
        if (NISTObjectIdentifiers.id_aes128_CBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        if (NISTObjectIdentifiers.id_aes192_CBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 192);
        }
        if (NISTObjectIdentifiers.id_aes256_CBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 256);
        }
        if (PKCSObjectIdentifiers.des_EDE3_CBC.equals((Object)asn1ObjectIdentifier)) {
            final DESedeKeyGenerator deSedeKeyGenerator = new DESedeKeyGenerator();
            deSedeKeyGenerator.init(new KeyGenerationParameters(secureRandom, 192));
            return (CipherKeyGenerator)deSedeKeyGenerator;
        }
        if (NTTObjectIdentifiers.id_camellia128_cbc.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        if (NTTObjectIdentifiers.id_camellia192_cbc.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 192);
        }
        if (NTTObjectIdentifiers.id_camellia256_cbc.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 256);
        }
        if (KISAObjectIdentifiers.id_seedCBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        if (CMSAlgorithm.CAST5_CBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        if (OIWObjectIdentifiers.desCBC.equals((Object)asn1ObjectIdentifier)) {
            final DESKeyGenerator desKeyGenerator = new DESKeyGenerator();
            desKeyGenerator.init(new KeyGenerationParameters(secureRandom, 64));
            return (CipherKeyGenerator)desKeyGenerator;
        }
        if (PKCSObjectIdentifiers.rc4.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        if (PKCSObjectIdentifiers.RC2_CBC.equals((Object)asn1ObjectIdentifier)) {
            return this.createCipherKeyGenerator(secureRandom, 128);
        }
        throw new CMSException("cannot recognise cipher: " + asn1ObjectIdentifier);
    }
    
    private CipherKeyGenerator createCipherKeyGenerator(final SecureRandom secureRandom, final int n) {
        final CipherKeyGenerator cipherKeyGenerator = new CipherKeyGenerator();
        cipherKeyGenerator.init(new KeyGenerationParameters(secureRandom, n));
        return cipherKeyGenerator;
    }
    
    static {
        BASE_CIPHER_NAMES = new HashMap();
        MAC_ALG_NAMES = new HashMap();
        prfs = createTable();
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
        rc2Table = new short[] { 189, 86, 234, 242, 162, 241, 172, 42, 176, 147, 209, 156, 27, 51, 253, 208, 48, 4, 182, 220, 125, 223, 50, 75, 247, 203, 69, 155, 49, 187, 33, 90, 65, 159, 225, 217, 74, 77, 158, 218, 160, 104, 44, 195, 39, 95, 128, 54, 62, 238, 251, 149, 26, 254, 206, 168, 52, 169, 19, 240, 166, 63, 216, 12, 120, 36, 175, 35, 82, 193, 103, 23, 245, 102, 144, 231, 232, 7, 184, 96, 72, 230, 30, 83, 243, 146, 164, 114, 140, 8, 21, 110, 134, 0, 132, 250, 244, 127, 138, 66, 25, 246, 219, 205, 20, 141, 80, 18, 186, 60, 6, 78, 236, 179, 53, 17, 161, 136, 142, 43, 148, 153, 183, 113, 116, 211, 228, 191, 58, 222, 150, 14, 188, 10, 237, 119, 252, 55, 107, 3, 121, 137, 98, 198, 215, 192, 210, 124, 106, 139, 34, 163, 91, 5, 93, 2, 117, 213, 97, 227, 24, 143, 85, 81, 173, 31, 11, 94, 133, 229, 194, 87, 99, 202, 61, 108, 180, 197, 204, 112, 178, 145, 89, 13, 71, 32, 200, 79, 88, 224, 1, 226, 22, 56, 196, 111, 59, 15, 101, 70, 190, 126, 45, 123, 130, 249, 64, 181, 29, 115, 248, 235, 38, 199, 135, 151, 37, 84, 177, 40, 170, 152, 157, 165, 100, 109, 122, 212, 16, 129, 68, 239, 73, 214, 174, 46, 221, 118, 92, 47, 167, 28, 201, 9, 105, 154, 131, 207, 41, 57, 185, 233, 76, 255, 67, 171 };
        rc2Ekb = new short[] { 93, 190, 155, 139, 17, 153, 110, 77, 89, 243, 133, 166, 63, 183, 131, 197, 228, 115, 107, 58, 104, 90, 192, 71, 160, 100, 52, 12, 241, 208, 82, 165, 185, 30, 150, 67, 65, 216, 212, 44, 219, 248, 7, 119, 42, 202, 235, 239, 16, 28, 22, 13, 56, 114, 47, 137, 193, 249, 128, 196, 109, 174, 48, 61, 206, 32, 99, 254, 230, 26, 199, 184, 80, 232, 36, 23, 252, 37, 111, 187, 106, 163, 68, 83, 217, 162, 1, 171, 188, 182, 31, 152, 238, 154, 167, 45, 79, 158, 142, 172, 224, 198, 73, 70, 41, 244, 148, 138, 175, 225, 91, 195, 179, 123, 87, 209, 124, 156, 237, 135, 64, 140, 226, 203, 147, 20, 201, 97, 46, 229, 204, 246, 94, 168, 92, 214, 117, 141, 98, 149, 88, 105, 118, 161, 74, 181, 85, 9, 120, 51, 130, 215, 221, 121, 245, 27, 11, 222, 38, 33, 40, 116, 4, 151, 86, 223, 60, 240, 55, 57, 220, 255, 6, 164, 234, 66, 8, 218, 180, 113, 176, 207, 18, 122, 78, 250, 108, 29, 132, 0, 200, 127, 145, 69, 170, 43, 194, 177, 143, 213, 186, 242, 173, 25, 178, 103, 54, 247, 15, 10, 146, 125, 227, 157, 233, 144, 62, 35, 39, 102, 19, 236, 129, 21, 189, 34, 191, 159, 126, 169, 81, 75, 76, 251, 2, 211, 112, 134, 49, 231, 59, 5, 3, 84, 96, 72, 101, 24, 210, 205, 95, 50, 136, 14, 53, 253 };
    }
}
