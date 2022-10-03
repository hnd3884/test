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
import org.bouncycastle.pqc.crypto.mceliece.McElieceFujisakiCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricHybridCipher;

public class McElieceFujisakiCipherSpi extends AsymmetricHybridCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private McElieceFujisakiCipher cipher;
    private ByteArrayOutputStream buf;
    
    protected McElieceFujisakiCipherSpi(final Digest digest, final McElieceFujisakiCipher cipher) {
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
        final byte[] byteArray = this.buf.toByteArray();
        this.buf.reset();
        if (this.opMode == 1) {
            return this.cipher.messageEncrypt(byteArray);
        }
        if (this.opMode == 2) {
            try {
                return this.cipher.messageDecrypt(byteArray);
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
        final ParametersWithRandom parametersWithRandom = new ParametersWithRandom(McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)key), secureRandom);
        this.digest.reset();
        this.cipher.init(true, parametersWithRandom);
    }
    
    @Override
    protected void initCipherDecrypt(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        final AsymmetricKeyParameter generatePrivateKeyParameter = McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)key);
        this.digest.reset();
        this.cipher.init(false, generatePrivateKeyParameter);
    }
    
    @Override
    public String getName() {
        return "McElieceFujisakiCipher";
    }
    
    @Override
    public int getKeySize(final Key key) throws InvalidKeyException {
        McElieceCCA2KeyParameters mcElieceCCA2KeyParameters;
        if (key instanceof PublicKey) {
            mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePublicKeyParameter((PublicKey)key);
        }
        else {
            mcElieceCCA2KeyParameters = (McElieceCCA2KeyParameters)McElieceCCA2KeysToParams.generatePrivateKeyParameter((PrivateKey)key);
        }
        return this.cipher.getKeySize(mcElieceCCA2KeyParameters);
    }
    
    public static class McElieceFujisaki extends McElieceFujisakiCipherSpi
    {
        public McElieceFujisaki() {
            super(DigestFactory.createSHA1(), new McElieceFujisakiCipher());
        }
    }
}
