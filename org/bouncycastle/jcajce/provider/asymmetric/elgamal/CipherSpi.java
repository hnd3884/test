package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import java.security.PublicKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import java.security.SecureRandom;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.Strings;
import javax.crypto.interfaces.DHKey;
import org.bouncycastle.jce.interfaces.ElGamalKey;
import java.security.Key;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import javax.crypto.spec.PSource;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.BufferedAsymmetricBlockCipher;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;

public class CipherSpi extends BaseCipherSpi
{
    private BufferedAsymmetricBlockCipher cipher;
    private AlgorithmParameterSpec paramSpec;
    private AlgorithmParameters engineParams;
    
    public CipherSpi(final AsymmetricBlockCipher asymmetricBlockCipher) {
        this.cipher = new BufferedAsymmetricBlockCipher(asymmetricBlockCipher);
    }
    
    private void initFromSpec(final OAEPParameterSpec paramSpec) throws NoSuchPaddingException {
        final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)paramSpec.getMGFParameters();
        final Digest digest = DigestFactory.getDigest(mgf1ParameterSpec.getDigestAlgorithm());
        if (digest == null) {
            throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mgf1ParameterSpec.getDigestAlgorithm());
        }
        this.cipher = new BufferedAsymmetricBlockCipher(new OAEPEncoding(new ElGamalEngine(), digest, ((PSource.PSpecified)paramSpec.getPSource()).getValue()));
        this.paramSpec = paramSpec;
    }
    
    @Override
    protected int engineGetBlockSize() {
        return this.cipher.getInputBlockSize();
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        if (key instanceof ElGamalKey) {
            return ((ElGamalKey)key).getParameters().getP().bitLength();
        }
        if (key instanceof DHKey) {
            return ((DHKey)key).getParams().getP().bitLength();
        }
        throw new IllegalArgumentException("not an ElGamal key!");
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.cipher.getOutputBlockSize();
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                (this.engineParams = this.createParametersInstance("OAEP")).init(this.paramSpec);
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        return this.engineParams;
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NONE") || upperCase.equals("ECB")) {
            return;
        }
        throw new NoSuchAlgorithmException("can't support mode " + s);
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NOPADDING")) {
            this.cipher = new BufferedAsymmetricBlockCipher(new ElGamalEngine());
        }
        else if (upperCase.equals("PKCS1PADDING")) {
            this.cipher = new BufferedAsymmetricBlockCipher(new PKCS1Encoding(new ElGamalEngine()));
        }
        else if (upperCase.equals("ISO9796-1PADDING")) {
            this.cipher = new BufferedAsymmetricBlockCipher(new ISO9796d1Encoding(new ElGamalEngine()));
        }
        else if (upperCase.equals("OAEPPADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        }
        else if (upperCase.equals("OAEPWITHMD5ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA1ANDMGF1PADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        }
        else if (upperCase.equals("OAEPWITHSHA224ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA256ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA384ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
        }
        else {
            if (!upperCase.equals("OAEPWITHSHA512ANDMGF1PADDING")) {
                throw new NoSuchPaddingException(s + " unavailable with ElGamal.");
            }
            this.initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException {
        if (algorithmParameterSpec == null) {
            CipherParameters cipherParameters;
            if (key instanceof ElGamalPublicKey) {
                cipherParameters = ElGamalUtil.generatePublicKeyParameter((PublicKey)key);
            }
            else {
                if (!(key instanceof ElGamalPrivateKey)) {
                    throw new InvalidKeyException("unknown key type passed to ElGamal");
                }
                cipherParameters = ElGamalUtil.generatePrivateKeyParameter((PrivateKey)key);
            }
            if (secureRandom != null) {
                cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
            }
            switch (n) {
                case 1:
                case 3: {
                    this.cipher.init(true, cipherParameters);
                    break;
                }
                case 2:
                case 4: {
                    this.cipher.init(false, cipherParameters);
                    break;
                }
                default: {
                    throw new InvalidParameterException("unknown opmode " + n + " passed to ElGamal");
                }
            }
            return;
        }
        throw new IllegalArgumentException("unknown parameter type.");
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("can't handle parameters in ElGamal");
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.cipher.processBytes(array, n, n2);
        return null;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.cipher.processBytes(array, n, n2);
        return 0;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        this.cipher.processBytes(array, n, n2);
        return this.getOutput();
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, BadPaddingException {
        this.cipher.processBytes(array, n, n2);
        final byte[] output = this.getOutput();
        for (int i = 0; i != output.length; ++i) {
            array2[n3 + i] = output[i];
        }
        return output.length;
    }
    
    private byte[] getOutput() throws BadPaddingException {
        try {
            return this.cipher.doFinal();
        }
        catch (final InvalidCipherTextException ex) {
            throw new BadPaddingException("unable to decrypt block") {
                @Override
                public synchronized Throwable getCause() {
                    return ex;
                }
            };
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new BadBlockException("unable to decrypt block", ex2);
        }
    }
    
    public static class NoPadding extends CipherSpi
    {
        public NoPadding() {
            super(new ElGamalEngine());
        }
    }
    
    public static class PKCS1v1_5Padding extends CipherSpi
    {
        public PKCS1v1_5Padding() {
            super(new PKCS1Encoding(new ElGamalEngine()));
        }
    }
}
