package org.bouncycastle.openssl;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class PKCS8Generator implements PemObjectGenerator
{
    public static final ASN1ObjectIdentifier AES_128_CBC;
    public static final ASN1ObjectIdentifier AES_192_CBC;
    public static final ASN1ObjectIdentifier AES_256_CBC;
    public static final ASN1ObjectIdentifier DES3_CBC;
    public static final ASN1ObjectIdentifier PBE_SHA1_RC4_128;
    public static final ASN1ObjectIdentifier PBE_SHA1_RC4_40;
    public static final ASN1ObjectIdentifier PBE_SHA1_3DES;
    public static final ASN1ObjectIdentifier PBE_SHA1_2DES;
    public static final ASN1ObjectIdentifier PBE_SHA1_RC2_128;
    public static final ASN1ObjectIdentifier PBE_SHA1_RC2_40;
    public static final AlgorithmIdentifier PRF_HMACSHA1;
    public static final AlgorithmIdentifier PRF_HMACSHA224;
    public static final AlgorithmIdentifier PRF_HMACSHA256;
    public static final AlgorithmIdentifier PRF_HMACSHA384;
    public static final AlgorithmIdentifier PRF_HMACSHA512;
    public static final AlgorithmIdentifier PRF_HMACGOST3411;
    public static final AlgorithmIdentifier PRF_HMACSHA3_224;
    public static final AlgorithmIdentifier PRF_HMACSHA3_256;
    public static final AlgorithmIdentifier PRF_HMACSHA3_384;
    public static final AlgorithmIdentifier PRF_HMACSHA3_512;
    private PrivateKeyInfo key;
    private OutputEncryptor outputEncryptor;
    
    public PKCS8Generator(final PrivateKeyInfo key, final OutputEncryptor outputEncryptor) {
        this.key = key;
        this.outputEncryptor = outputEncryptor;
    }
    
    public PemObject generate() throws PemGenerationException {
        if (this.outputEncryptor != null) {
            return this.generate(this.key, this.outputEncryptor);
        }
        return this.generate(this.key, null);
    }
    
    private PemObject generate(final PrivateKeyInfo privateKeyInfo, final OutputEncryptor outputEncryptor) throws PemGenerationException {
        try {
            final byte[] encoded = privateKeyInfo.getEncoded();
            if (outputEncryptor == null) {
                return new PemObject("PRIVATE KEY", encoded);
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStream outputStream = outputEncryptor.getOutputStream(byteArrayOutputStream);
            outputStream.write(privateKeyInfo.getEncoded());
            outputStream.close();
            return new PemObject("ENCRYPTED PRIVATE KEY", new EncryptedPrivateKeyInfo(outputEncryptor.getAlgorithmIdentifier(), byteArrayOutputStream.toByteArray()).getEncoded());
        }
        catch (final IOException ex) {
            throw new PemGenerationException("unable to process encoded key data: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    static {
        AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
        AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
        AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
        DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
        PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4;
        PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4;
        PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;
        PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC;
        PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC;
        PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC;
        PRF_HMACSHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA224 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA384 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA512 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACGOST3411 = new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411Hmac, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA3_224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA3_384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384, (ASN1Encodable)DERNull.INSTANCE);
        PRF_HMACSHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, (ASN1Encodable)DERNull.INSTANCE);
    }
}
