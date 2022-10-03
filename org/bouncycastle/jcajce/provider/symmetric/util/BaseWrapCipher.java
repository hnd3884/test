package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.InvalidCipherTextException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.crypto.Wrapper;
import java.security.AlgorithmParameters;
import javax.crypto.CipherSpi;

public abstract class BaseWrapCipher extends CipherSpi implements PBE
{
    private Class[] availableSpecs;
    protected int pbeType;
    protected int pbeHash;
    protected int pbeKeySize;
    protected int pbeIvSize;
    protected AlgorithmParameters engineParams;
    protected Wrapper wrapEngine;
    private int ivSize;
    private byte[] iv;
    private final JcaJceHelper helper;
    
    protected BaseWrapCipher() {
        this.availableSpecs = new Class[] { GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class };
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.helper = new BCJcaJceHelper();
    }
    
    protected BaseWrapCipher(final Wrapper wrapper) {
        this(wrapper, 0);
    }
    
    protected BaseWrapCipher(final Wrapper wrapEngine, final int ivSize) {
        this.availableSpecs = new Class[] { GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class };
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.helper = new BCJcaJceHelper();
        this.wrapEngine = wrapEngine;
        this.ivSize = ivSize;
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return Arrays.clone(this.iv);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        return key.getEncoded().length * 8;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return -1;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    protected final AlgorithmParameters createParametersInstance(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(s);
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("can't support mode " + s);
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + s + " unknown.");
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters cipherParameters;
        if (key instanceof BCPBEKey) {
            final BCPBEKey bcpbeKey = (BCPBEKey)key;
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                cipherParameters = Util.makePBEParameters(bcpbeKey, algorithmParameterSpec, this.wrapEngine.getAlgorithmName());
            }
            else {
                if (bcpbeKey.getParam() == null) {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
                cipherParameters = bcpbeKey.getParam();
            }
        }
        else {
            cipherParameters = new KeyParameter(key.getEncoded());
        }
        if (algorithmParameterSpec instanceof IvParameterSpec) {
            cipherParameters = new ParametersWithIV(cipherParameters, ((IvParameterSpec)algorithmParameterSpec).getIV());
        }
        if (algorithmParameterSpec instanceof GOST28147WrapParameterSpec) {
            final GOST28147WrapParameterSpec gost28147WrapParameterSpec = (GOST28147WrapParameterSpec)algorithmParameterSpec;
            final byte[] sBox = gost28147WrapParameterSpec.getSBox();
            if (sBox != null) {
                cipherParameters = new ParametersWithSBox(cipherParameters, sBox);
            }
            cipherParameters = new ParametersWithUKM(cipherParameters, gost28147WrapParameterSpec.getUKM());
        }
        if (cipherParameters instanceof KeyParameter && this.ivSize != 0) {
            secureRandom.nextBytes(this.iv = new byte[this.ivSize]);
            cipherParameters = new ParametersWithIV(cipherParameters, this.iv);
        }
        if (secureRandom != null) {
            cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
        }
        switch (n) {
            case 3: {
                this.wrapEngine.init(true, cipherParameters);
                break;
            }
            case 4: {
                this.wrapEngine.init(false, cipherParameters);
                break;
            }
            case 1:
            case 2: {
                throw new IllegalArgumentException("engine only valid for wrapping");
            }
            default: {
                System.out.println("eeek!");
                break;
            }
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters engineParams, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (engineParams != null) {
            int i = 0;
            while (i != this.availableSpecs.length) {
                try {
                    parameterSpec = engineParams.getParameterSpec((Class<AlgorithmParameterSpec>)this.availableSpecs[i]);
                }
                catch (final Exception ex) {
                    ++i;
                    continue;
                }
                break;
            }
            if (parameterSpec == null) {
                throw new InvalidAlgorithmParameterException("can't handle parameter " + engineParams.toString());
            }
        }
        this.engineParams = engineParams;
        this.engineInit(n, key, parameterSpec, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        throw new RuntimeException("not supported for wrapping");
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        throw new RuntimeException("not supported for wrapping");
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return null;
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        return 0;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            if (this.wrapEngine == null) {
                return this.engineDoFinal(encoded, 0, encoded.length);
            }
            return this.wrapEngine.wrap(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] array2;
        try {
            if (this.wrapEngine == null) {
                array2 = this.engineDoFinal(array, 0, array.length);
            }
            else {
                array2 = this.wrapEngine.unwrap(array, 0, array.length);
            }
        }
        catch (final InvalidCipherTextException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
        catch (final BadPaddingException ex2) {
            throw new InvalidKeyException(ex2.getMessage());
        }
        catch (final IllegalBlockSizeException ex3) {
            throw new InvalidKeyException(ex3.getMessage());
        }
        if (n == 3) {
            return new SecretKeySpec(array2, s);
        }
        if (s.equals("") && n == 2) {
            try {
                final PrivateKeyInfo instance = PrivateKeyInfo.getInstance(array2);
                final PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(instance);
                if (privateKey != null) {
                    return privateKey;
                }
                throw new InvalidKeyException("algorithm " + instance.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
            }
            catch (final Exception ex4) {
                throw new InvalidKeyException("Invalid key encoding.");
            }
        }
        try {
            final KeyFactory keyFactory = this.helper.createKeyFactory(s);
            if (n == 1) {
                return keyFactory.generatePublic(new X509EncodedKeySpec(array2));
            }
            if (n == 2) {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(array2));
            }
        }
        catch (final NoSuchProviderException ex5) {
            throw new InvalidKeyException("Unknown key type " + ex5.getMessage());
        }
        catch (final InvalidKeySpecException ex6) {
            throw new InvalidKeyException("Unknown key type " + ex6.getMessage());
        }
        throw new InvalidKeyException("Unknown key type " + n);
    }
}
