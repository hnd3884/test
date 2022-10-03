package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.x509.DigestInfo;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.PrivateKey;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import java.security.SignatureSpi;

public class DigestSignatureSpi extends SignatureSpi
{
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private AlgorithmIdentifier algId;
    
    protected DigestSignatureSpi(final Digest digest, final AsymmetricBlockCipher cipher) {
        this.digest = digest;
        this.cipher = cipher;
        this.algId = null;
    }
    
    protected DigestSignatureSpi(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Digest digest, final AsymmetricBlockCipher cipher) {
        this.digest = digest;
        this.cipher = cipher;
        this.algId = new AlgorithmIdentifier(asn1ObjectIdentifier, DERNull.INSTANCE);
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Supplied key (" + this.getType(publicKey) + ") is not a RSAPublicKey instance");
        }
        final RSAKeyParameters generatePublicKeyParameter = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);
        this.digest.reset();
        this.cipher.init(false, generatePublicKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Supplied key (" + this.getType(privateKey) + ") is not a RSAPrivateKey instance");
        }
        final RSAKeyParameters generatePrivateKeyParameter = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);
        this.digest.reset();
        this.cipher.init(true, generatePrivateKeyParameter);
    }
    
    private String getType(final Object o) {
        if (o == null) {
            return null;
        }
        return o.getClass().getName();
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.digest.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.digest.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        try {
            final byte[] derEncode = this.derEncode(array);
            return this.cipher.processBlock(derEncode, 0, derEncode.length);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new SignatureException("key too small for signature type");
        }
        catch (final Exception ex2) {
            throw new SignatureException(ex2.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        byte[] processBlock;
        byte[] derEncode;
        try {
            processBlock = this.cipher.processBlock(array, 0, array.length);
            derEncode = this.derEncode(array2);
        }
        catch (final Exception ex) {
            return false;
        }
        if (processBlock.length == derEncode.length) {
            return Arrays.constantTimeAreEqual(processBlock, derEncode);
        }
        if (processBlock.length == derEncode.length - 2) {
            final byte[] array3 = derEncode;
            final int n = 1;
            array3[n] -= 2;
            final byte[] array4 = derEncode;
            final int n2 = 3;
            array4[n2] -= 2;
            final int n3 = 4 + derEncode[3];
            final int n4 = n3 + 2;
            int n5 = 0;
            for (int i = 0; i < derEncode.length - n4; ++i) {
                n5 |= (processBlock[n3 + i] ^ derEncode[n4 + i]);
            }
            for (int j = 0; j < n3; ++j) {
                n5 |= (processBlock[j] ^ derEncode[j]);
            }
            return n5 == 0;
        }
        Arrays.constantTimeAreEqual(derEncode, derEncode);
        return false;
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected void engineSetParameter(final String s, final Object o) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected Object engineGetParameter(final String s) {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    private byte[] derEncode(final byte[] array) throws IOException {
        if (this.algId == null) {
            return array;
        }
        return new DigestInfo(this.algId, array).getEncoded("DER");
    }
    
    public static class MD2 extends DigestSignatureSpi
    {
        public MD2() {
            super(PKCSObjectIdentifiers.md2, new MD2Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class MD4 extends DigestSignatureSpi
    {
        public MD4() {
            super(PKCSObjectIdentifiers.md4, new MD4Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class MD5 extends DigestSignatureSpi
    {
        public MD5() {
            super(PKCSObjectIdentifiers.md5, DigestFactory.createMD5(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class RIPEMD128 extends DigestSignatureSpi
    {
        public RIPEMD128() {
            super(TeleTrusTObjectIdentifiers.ripemd128, new RIPEMD128Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class RIPEMD160 extends DigestSignatureSpi
    {
        public RIPEMD160() {
            super(TeleTrusTObjectIdentifiers.ripemd160, new RIPEMD160Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class RIPEMD256 extends DigestSignatureSpi
    {
        public RIPEMD256() {
            super(TeleTrusTObjectIdentifiers.ripemd256, new RIPEMD256Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA1 extends DigestSignatureSpi
    {
        public SHA1() {
            super(OIWObjectIdentifiers.idSHA1, DigestFactory.createSHA1(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA224 extends DigestSignatureSpi
    {
        public SHA224() {
            super(NISTObjectIdentifiers.id_sha224, DigestFactory.createSHA224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA256 extends DigestSignatureSpi
    {
        public SHA256() {
            super(NISTObjectIdentifiers.id_sha256, DigestFactory.createSHA256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA384 extends DigestSignatureSpi
    {
        public SHA384() {
            super(NISTObjectIdentifiers.id_sha384, DigestFactory.createSHA384(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA3_224 extends DigestSignatureSpi
    {
        public SHA3_224() {
            super(NISTObjectIdentifiers.id_sha3_224, DigestFactory.createSHA3_224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA3_256 extends DigestSignatureSpi
    {
        public SHA3_256() {
            super(NISTObjectIdentifiers.id_sha3_256, DigestFactory.createSHA3_256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA3_384 extends DigestSignatureSpi
    {
        public SHA3_384() {
            super(NISTObjectIdentifiers.id_sha3_384, DigestFactory.createSHA3_384(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA3_512 extends DigestSignatureSpi
    {
        public SHA3_512() {
            super(NISTObjectIdentifiers.id_sha3_512, DigestFactory.createSHA3_512(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA512 extends DigestSignatureSpi
    {
        public SHA512() {
            super(NISTObjectIdentifiers.id_sha512, DigestFactory.createSHA512(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA512_224 extends DigestSignatureSpi
    {
        public SHA512_224() {
            super(NISTObjectIdentifiers.id_sha512_224, DigestFactory.createSHA512_224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class SHA512_256 extends DigestSignatureSpi
    {
        public SHA512_256() {
            super(NISTObjectIdentifiers.id_sha512_256, DigestFactory.createSHA512_256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class noneRSA extends DigestSignatureSpi
    {
        public noneRSA() {
            super(new NullDigest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
}
