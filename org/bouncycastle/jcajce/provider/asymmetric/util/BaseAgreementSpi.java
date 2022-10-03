package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.util.Integers;
import java.util.HashMap;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.DerivationFunction;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;

public abstract class BaseAgreementSpi extends KeyAgreementSpi
{
    private static final Map<String, ASN1ObjectIdentifier> defaultOids;
    private static final Map<String, Integer> keySizes;
    private static final Map<String, String> nameTable;
    private static final Hashtable oids;
    private static final Hashtable des;
    private final String kaAlgorithm;
    private final DerivationFunction kdf;
    protected byte[] ukmParameters;
    
    public BaseAgreementSpi(final String kaAlgorithm, final DerivationFunction kdf) {
        this.kaAlgorithm = kaAlgorithm;
        this.kdf = kdf;
    }
    
    protected static String getAlgorithm(final String s) {
        if (s.indexOf(91) > 0) {
            return s.substring(0, s.indexOf(91));
        }
        if (s.startsWith(NISTObjectIdentifiers.aes.getId())) {
            return "AES";
        }
        if (s.startsWith(GNUObjectIdentifiers.Serpent.getId())) {
            return "Serpent";
        }
        final String s2 = BaseAgreementSpi.nameTable.get(Strings.toUpperCase(s));
        if (s2 != null) {
            return s2;
        }
        return s;
    }
    
    protected static int getKeySize(final String s) {
        if (s.indexOf(91) > 0) {
            return Integer.parseInt(s.substring(s.indexOf(91) + 1, s.indexOf(93)));
        }
        final String upperCase = Strings.toUpperCase(s);
        if (!BaseAgreementSpi.keySizes.containsKey(upperCase)) {
            return -1;
        }
        return BaseAgreementSpi.keySizes.get(upperCase);
    }
    
    protected static byte[] trimZeroes(final byte[] array) {
        if (array[0] != 0) {
            return array;
        }
        int n;
        for (n = 0; n < array.length && array[n] == 0; ++n) {}
        final byte[] array2 = new byte[array.length - n];
        System.arraycopy(array, n, array2, 0, array2.length);
        return array2;
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.kdf != null) {
            throw new UnsupportedOperationException("KDF can only be used when algorithm is known");
        }
        return this.calcSecret();
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        if (array.length - n < engineGenerateSecret.length) {
            throw new ShortBufferException(this.kaAlgorithm + " key agreement: need " + engineGenerateSecret.length + " bytes");
        }
        System.arraycopy(engineGenerateSecret, 0, array, n, engineGenerateSecret.length);
        return engineGenerateSecret.length;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws NoSuchAlgorithmException {
        byte[] calcSecret = this.calcSecret();
        final String upperCase = Strings.toUpperCase(s);
        String id = s;
        if (BaseAgreementSpi.oids.containsKey(upperCase)) {
            id = BaseAgreementSpi.oids.get(upperCase).getId();
        }
        final int keySize = getKeySize(id);
        if (this.kdf != null) {
            if (keySize < 0) {
                throw new NoSuchAlgorithmException("unknown algorithm encountered: " + id);
            }
            final byte[] array = new byte[keySize / 8];
            if (this.kdf instanceof DHKEKGenerator) {
                ASN1ObjectIdentifier asn1ObjectIdentifier;
                try {
                    asn1ObjectIdentifier = new ASN1ObjectIdentifier(id);
                }
                catch (final IllegalArgumentException ex) {
                    throw new NoSuchAlgorithmException("no OID for algorithm: " + id);
                }
                this.kdf.init(new DHKDFParameters(asn1ObjectIdentifier, keySize, calcSecret, this.ukmParameters));
            }
            else {
                this.kdf.init(new KDFParameters(calcSecret, this.ukmParameters));
            }
            this.kdf.generateBytes(array, 0, array.length);
            calcSecret = array;
        }
        else if (keySize > 0) {
            final byte[] array2 = new byte[keySize / 8];
            System.arraycopy(calcSecret, 0, array2, 0, array2.length);
            calcSecret = array2;
        }
        final String algorithm = getAlgorithm(s);
        if (BaseAgreementSpi.des.containsKey(algorithm)) {
            DESParameters.setOddParity(calcSecret);
        }
        return new SecretKeySpec(calcSecret, algorithm);
    }
    
    protected abstract byte[] calcSecret();
    
    static {
        defaultOids = new HashMap<String, ASN1ObjectIdentifier>();
        keySizes = new HashMap<String, Integer>();
        nameTable = new HashMap<String, String>();
        oids = new Hashtable();
        des = new Hashtable();
        final Integer value = Integers.valueOf(64);
        final Integer value2 = Integers.valueOf(128);
        final Integer value3 = Integers.valueOf(192);
        final Integer value4 = Integers.valueOf(256);
        BaseAgreementSpi.keySizes.put("DES", value);
        BaseAgreementSpi.keySizes.put("DESEDE", value3);
        BaseAgreementSpi.keySizes.put("BLOWFISH", value2);
        BaseAgreementSpi.keySizes.put("AES", value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), value4);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), value2);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), value3);
        BaseAgreementSpi.keySizes.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), value4);
        BaseAgreementSpi.keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), value2);
        BaseAgreementSpi.keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), value3);
        BaseAgreementSpi.keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), value4);
        BaseAgreementSpi.keySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), value2);
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), value3);
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), value3);
        BaseAgreementSpi.keySizes.put(OIWObjectIdentifiers.desCBC.getId(), value);
        BaseAgreementSpi.keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), value4);
        BaseAgreementSpi.keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap.getId(), value4);
        BaseAgreementSpi.keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId(), value4);
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(160));
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), value4);
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
        BaseAgreementSpi.keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
        BaseAgreementSpi.defaultOids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        BaseAgreementSpi.defaultOids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
        BaseAgreementSpi.defaultOids.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
        BaseAgreementSpi.defaultOids.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        BaseAgreementSpi.defaultOids.put("DES", OIWObjectIdentifiers.desCBC);
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), "Blowfish");
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), "Blowfish");
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), "Blowfish");
        BaseAgreementSpi.nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), "Blowfish");
        BaseAgreementSpi.nameTable.put(OIWObjectIdentifiers.desECB.getId(), "DES");
        BaseAgreementSpi.nameTable.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        BaseAgreementSpi.nameTable.put(OIWObjectIdentifiers.desCFB.getId(), "DES");
        BaseAgreementSpi.nameTable.put(OIWObjectIdentifiers.desOFB.getId(), "DES");
        BaseAgreementSpi.nameTable.put(OIWObjectIdentifiers.desEDE.getId(), "DESede");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESede");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DESede");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), "HmacSHA1");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), "HmacSHA224");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), "HmacSHA256");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), "HmacSHA384");
        BaseAgreementSpi.nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), "HmacSHA512");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), "Camellia");
        BaseAgreementSpi.nameTable.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), "SEED");
        BaseAgreementSpi.nameTable.put(KISAObjectIdentifiers.id_seedCBC.getId(), "SEED");
        BaseAgreementSpi.nameTable.put(KISAObjectIdentifiers.id_seedMAC.getId(), "SEED");
        BaseAgreementSpi.nameTable.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), "GOST28147");
        BaseAgreementSpi.nameTable.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), "AES");
        BaseAgreementSpi.nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
        BaseAgreementSpi.nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
        BaseAgreementSpi.oids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        BaseAgreementSpi.oids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
        BaseAgreementSpi.oids.put("DES", OIWObjectIdentifiers.desCBC);
        BaseAgreementSpi.des.put("DES", "DES");
        BaseAgreementSpi.des.put("DESEDE", "DES");
        BaseAgreementSpi.des.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        BaseAgreementSpi.des.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DES");
        BaseAgreementSpi.des.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DES");
    }
}
