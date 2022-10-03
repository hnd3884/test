package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PrivateKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.crypto.InvalidCipherTextException;
import javax.crypto.BadPaddingException;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKobaraImaiCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricHybridCipher;

public class McElieceKobaraImaiCipherSpi extends AsymmetricHybridCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private McElieceKobaraImaiCipher cipher;
    private ByteArrayOutputStream buf;
    
    public McElieceKobaraImaiCipherSpi() {
        this.buf = new ByteArrayOutputStream();
        this.buf = new ByteArrayOutputStream();
    }
    
    protected McElieceKobaraImaiCipherSpi(final Digest digest, final McElieceKobaraImaiCipher cipher) {
        this.buf = new ByteArrayOutputStream();
        this.digest = digest;
        this.cipher = cipher;
        this.buf = new ByteArrayOutputStream();
    }
    
    @Override
    public byte[] update(final byte[] array, final int n, final int n2) {
        this.buf.write(array, n, n2);
        return new byte[0];
    }
    
    @Override
    public byte[] doFinal(final byte[] array, final int n, final int n2) throws BadPaddingException {
        this.update(array, n, n2);
        if (this.opMode == 1) {
            return this.cipher.messageEncrypt(this.pad());
        }
        if (this.opMode == 2) {
            try {
                final byte[] byteArray = this.buf.toByteArray();
                this.buf.reset();
                return this.unpad(this.cipher.messageDecrypt(byteArray));
            }
            catch (final InvalidCipherTextException ex) {
                throw new BadPaddingException(ex.getMessage());
            }
        }
        throw new IllegalStateException("unknown mode in doFinal");
    }
    
    @Override
    protected int encryptOutputSize(final int n) {
        return 0;
    }
    
    @Override
    protected int decryptOutputSize(final int n) {
        return 0;
    }
    
    @Override
    protected void initCipherEncrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.buf.reset();
        final ParametersWithRandom parametersWithRandom = new ParametersWithRandom(McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)key), secureRandom);
        this.digest.reset();
        this.cipher.init(true, parametersWithRandom);
    }
    
    @Override
    protected void initCipherDecrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.buf.reset();
        final AsymmetricKeyParameter generatePrivateKeyParameter = McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)key);
        this.digest.reset();
        this.cipher.init(false, generatePrivateKeyParameter);
    }
    
    @Override
    public String getName() {
        return "McElieceKobaraImaiCipher";
    }
    
    @Override
    public int getKeySize(final Key key) throws InvalidKeyException {
        if (key instanceof PublicKey) {
            return this.cipher.getKeySize((McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)key));
        }
        if (key instanceof PrivateKey) {
            return this.cipher.getKeySize((McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)key));
        }
        throw new InvalidKeyException();
    }
    
    private byte[] pad() {
        this.buf.write(1);
        final byte[] byteArray = this.buf.toByteArray();
        this.buf.reset();
        return byteArray;
    }
    
    private byte[] unpad(final byte[] array) throws BadPaddingException {
        int n;
        for (n = array.length - 1; n >= 0 && array[n] == 0; --n) {}
        if (array[n] != 1) {
            throw new BadPaddingException("invalid ciphertext");
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public static class McElieceKobaraImai extends McElieceKobaraImaiCipherSpi
    {
        public McElieceKobaraImai() {
            super(DigestFactory.createSHA1(), new McElieceKobaraImaiCipher());
        }
    }
    
    public static class McElieceKobaraImai224 extends McElieceKobaraImaiCipherSpi
    {
        public McElieceKobaraImai224() {
            super(DigestFactory.createSHA224(), new McElieceKobaraImaiCipher());
        }
    }
    
    public static class McElieceKobaraImai256 extends McElieceKobaraImaiCipherSpi
    {
        public McElieceKobaraImai256() {
            super(DigestFactory.createSHA256(), new McElieceKobaraImaiCipher());
        }
    }
    
    public static class McElieceKobaraImai384 extends McElieceKobaraImaiCipherSpi
    {
        public McElieceKobaraImai384() {
            super(DigestFactory.createSHA384(), new McElieceKobaraImaiCipher());
        }
    }
    
    public static class McElieceKobaraImai512 extends McElieceKobaraImaiCipherSpi
    {
        public McElieceKobaraImai512() {
            super(DigestFactory.createSHA512(), new McElieceKobaraImaiCipher());
        }
    }
}
