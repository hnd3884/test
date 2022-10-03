package org.bouncycastle.crypto.signers;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import java.io.IOException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Signer;

public class RSADigestSigner implements Signer
{
    private final AsymmetricBlockCipher rsaEngine;
    private final AlgorithmIdentifier algId;
    private final Digest digest;
    private boolean forSigning;
    private static final Hashtable oidMap;
    
    public RSADigestSigner(final Digest digest) {
        this(digest, RSADigestSigner.oidMap.get(digest.getAlgorithmName()));
    }
    
    public RSADigestSigner(final Digest digest, final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this.rsaEngine = new PKCS1Encoding(new RSABlindedEngine());
        this.digest = digest;
        this.algId = new AlgorithmIdentifier(asn1ObjectIdentifier, DERNull.INSTANCE);
    }
    
    @Deprecated
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "withRSA";
    }
    
    public void init(final boolean forSigning, final CipherParameters cipherParameters) {
        this.forSigning = forSigning;
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
        }
        if (forSigning && !asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        this.reset();
        this.rsaEngine.init(forSigning, cipherParameters);
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (!this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for signature generation.");
        }
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        try {
            final byte[] derEncode = this.derEncode(array);
            return this.rsaEngine.processBlock(derEncode, 0, derEncode.length);
        }
        catch (final IOException ex) {
            throw new CryptoException("unable to encode signature: " + ex.getMessage(), ex);
        }
    }
    
    public boolean verifySignature(final byte[] array) {
        if (this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for verification");
        }
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        byte[] processBlock;
        byte[] derEncode;
        try {
            processBlock = this.rsaEngine.processBlock(array, 0, array.length);
            derEncode = this.derEncode(array2);
        }
        catch (final Exception ex) {
            return false;
        }
        if (processBlock.length == derEncode.length) {
            return Arrays.constantTimeAreEqual(processBlock, derEncode);
        }
        if (processBlock.length == derEncode.length - 2) {
            final int n = processBlock.length - array2.length - 2;
            final int n2 = derEncode.length - array2.length - 2;
            final byte[] array3 = derEncode;
            final int n3 = 1;
            array3[n3] -= 2;
            final byte[] array4 = derEncode;
            final int n4 = 3;
            array4[n4] -= 2;
            int n5 = 0;
            for (int i = 0; i < array2.length; ++i) {
                n5 |= (processBlock[n + i] ^ derEncode[n2 + i]);
            }
            for (int j = 0; j < n; ++j) {
                n5 |= (processBlock[j] ^ derEncode[j]);
            }
            return n5 == 0;
        }
        Arrays.constantTimeAreEqual(derEncode, derEncode);
        return false;
    }
    
    public void reset() {
        this.digest.reset();
    }
    
    private byte[] derEncode(final byte[] array) throws IOException {
        return new DigestInfo(this.algId, array).getEncoded("DER");
    }
    
    static {
        (oidMap = new Hashtable()).put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        RSADigestSigner.oidMap.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        RSADigestSigner.oidMap.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        RSADigestSigner.oidMap.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        RSADigestSigner.oidMap.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        RSADigestSigner.oidMap.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        RSADigestSigner.oidMap.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        RSADigestSigner.oidMap.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        RSADigestSigner.oidMap.put("SHA-512/224", NISTObjectIdentifiers.id_sha512_224);
        RSADigestSigner.oidMap.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
        RSADigestSigner.oidMap.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        RSADigestSigner.oidMap.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        RSADigestSigner.oidMap.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        RSADigestSigner.oidMap.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        RSADigestSigner.oidMap.put("MD2", PKCSObjectIdentifiers.md2);
        RSADigestSigner.oidMap.put("MD4", PKCSObjectIdentifiers.md4);
        RSADigestSigner.oidMap.put("MD5", PKCSObjectIdentifiers.md5);
    }
}
