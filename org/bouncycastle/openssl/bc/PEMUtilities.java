package org.bouncycastle.openssl.bc;

import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.util.HashSet;
import java.util.HashMap;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import java.util.Map;

class PEMUtilities
{
    private static final Map KEYSIZES;
    private static final Set PKCS5_SCHEME_1;
    private static final Set PKCS5_SCHEME_2;
    
    static int getKeySize(final String s) {
        if (!PEMUtilities.KEYSIZES.containsKey(s)) {
            throw new IllegalStateException("no key size for algorithm: " + s);
        }
        return PEMUtilities.KEYSIZES.get(s);
    }
    
    static boolean isPKCS5Scheme1(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_1.contains(asn1ObjectIdentifier);
    }
    
    static boolean isPKCS5Scheme2(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_2.contains(asn1ObjectIdentifier);
    }
    
    public static boolean isPKCS12(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }
    
    public static KeyParameter generateSecretKeyForPKCS5Scheme2(final String s, final char[] array, final byte[] array2, final int n) {
        final PKCS5S2ParametersGenerator pkcs5S2ParametersGenerator = new PKCS5S2ParametersGenerator((Digest)new SHA1Digest());
        ((PBEParametersGenerator)pkcs5S2ParametersGenerator).init(PBEParametersGenerator.PKCS5PasswordToBytes(array), array2, n);
        return (KeyParameter)((PBEParametersGenerator)pkcs5S2ParametersGenerator).generateDerivedParameters(getKeySize(s));
    }
    
    static byte[] crypt(final boolean b, final byte[] array, final char[] array2, final String s, final byte[] array3) throws PEMException {
        byte[] array4 = array3;
        String s2 = "CBC";
        Object o = new PKCS7Padding();
        if (s.endsWith("-CFB")) {
            s2 = "CFB";
            o = null;
        }
        if (s.endsWith("-ECB") || "DES-EDE".equals(s) || "DES-EDE3".equals(s)) {
            s2 = "ECB";
            array4 = null;
        }
        if (s.endsWith("-OFB")) {
            s2 = "OFB";
            o = null;
        }
        Object o2;
        Object o3;
        if (s.startsWith("DES-EDE")) {
            o2 = getKey(array2, 24, array3, !s.startsWith("DES-EDE3"));
            o3 = new DESedeEngine();
        }
        else if (s.startsWith("DES-")) {
            o2 = getKey(array2, 8, array3);
            o3 = new DESEngine();
        }
        else if (s.startsWith("BF-")) {
            o2 = getKey(array2, 16, array3);
            o3 = new BlowfishEngine();
        }
        else if (s.startsWith("RC2-")) {
            int n = 128;
            if (s.startsWith("RC2-40-")) {
                n = 40;
            }
            else if (s.startsWith("RC2-64-")) {
                n = 64;
            }
            o2 = new RC2Parameters(getKey(array2, n / 8, array3).getKey(), n);
            o3 = new RC2Engine();
        }
        else {
            if (!s.startsWith("AES-")) {
                throw new EncryptionException("unknown encryption with private key: " + s);
            }
            byte[] array5 = array3;
            if (array5.length > 8) {
                array5 = new byte[8];
                System.arraycopy(array3, 0, array5, 0, 8);
            }
            int n2;
            if (s.startsWith("AES-128-")) {
                n2 = 128;
            }
            else if (s.startsWith("AES-192-")) {
                n2 = 192;
            }
            else {
                if (!s.startsWith("AES-256-")) {
                    throw new EncryptionException("unknown AES encryption with private key: " + s);
                }
                n2 = 256;
            }
            o2 = getKey(array2, n2 / 8, array5);
            o3 = new AESEngine();
        }
        if (s2.equals("CBC")) {
            o3 = new CBCBlockCipher((BlockCipher)o3);
        }
        else if (s2.equals("CFB")) {
            o3 = new CFBBlockCipher((BlockCipher)o3, ((BlockCipher)o3).getBlockSize() * 8);
        }
        else if (s2.equals("OFB")) {
            o3 = new OFBBlockCipher((BlockCipher)o3, ((BlockCipher)o3).getBlockSize() * 8);
        }
        try {
            Object o4;
            if (o == null) {
                o4 = new BufferedBlockCipher((BlockCipher)o3);
            }
            else {
                o4 = new PaddedBufferedBlockCipher((BlockCipher)o3, (BlockCipherPadding)o);
            }
            if (array4 == null) {
                ((BufferedBlockCipher)o4).init(b, (CipherParameters)o2);
            }
            else {
                ((BufferedBlockCipher)o4).init(b, (CipherParameters)new ParametersWithIV((CipherParameters)o2, array4));
            }
            final byte[] array6 = new byte[((BufferedBlockCipher)o4).getOutputSize(array.length)];
            final int processBytes = ((BufferedBlockCipher)o4).processBytes(array, 0, array.length, array6, 0);
            final int n3 = processBytes + ((BufferedBlockCipher)o4).doFinal(array6, processBytes);
            if (n3 == array6.length) {
                return array6;
            }
            final byte[] array7 = new byte[n3];
            System.arraycopy(array6, 0, array7, 0, n3);
            return array7;
        }
        catch (final Exception ex) {
            throw new EncryptionException("exception using cipher - please check password and data.", (Throwable)ex);
        }
    }
    
    private static KeyParameter getKey(final char[] array, final int n, final byte[] array2) throws PEMException {
        return getKey(array, n, array2, false);
    }
    
    private static KeyParameter getKey(final char[] array, final int n, final byte[] array2, final boolean b) throws PEMException {
        final OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
        ((PBEParametersGenerator)openSSLPBEParametersGenerator).init(PBEParametersGenerator.PKCS5PasswordToBytes(array), array2, 1);
        final KeyParameter keyParameter = (KeyParameter)((PBEParametersGenerator)openSSLPBEParametersGenerator).generateDerivedParameters(n * 8);
        if (b && keyParameter.getKey().length == 24) {
            final byte[] key = keyParameter.getKey();
            System.arraycopy(key, 0, key, 16, 8);
            return new KeyParameter(key);
        }
        return keyParameter;
    }
    
    static {
        KEYSIZES = new HashMap();
        PKCS5_SCHEME_1 = new HashSet();
        PKCS5_SCHEME_2 = new HashSet();
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
    }
}
