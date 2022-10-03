package org.bouncycastle.jcajce.provider.symmetric.util;

import org.bouncycastle.crypto.DataLengthException;
import javax.crypto.ShortBufferException;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12Key;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.StreamCipher;

public class BaseStreamCipher extends BaseWrapCipher implements PBE
{
    private Class[] availableSpecs;
    private StreamCipher cipher;
    private int keySizeInBits;
    private int digest;
    private ParametersWithIV ivParam;
    private int ivLength;
    private PBEParameterSpec pbeSpec;
    private String pbeAlgorithm;
    
    protected BaseStreamCipher(final StreamCipher streamCipher, final int n) {
        this(streamCipher, n, -1, -1);
    }
    
    protected BaseStreamCipher(final StreamCipher cipher, final int ivLength, final int keySizeInBits, final int digest) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.ivLength = 0;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.cipher = cipher;
        this.ivLength = ivLength;
        this.keySizeInBits = keySizeInBits;
        this.digest = digest;
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return (byte[])((this.ivParam != null) ? this.ivParam.getIV() : null);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        return key.getEncoded().length * 8;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return n;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.pbeSpec != null) {
            try {
                final AlgorithmParameters parametersInstance = this.createParametersInstance(this.pbeAlgorithm);
                parametersInstance.init(this.pbeSpec);
                return parametersInstance;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        return this.engineParams;
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("can't support mode " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (!s.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException("Padding " + s + " unknown.");
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + key.getAlgorithm() + " not suitable for symmetric enryption.");
        }
        CipherParameters cipherParameters;
        if (key instanceof PKCS12Key) {
            final PKCS12Key pkcs12Key = (PKCS12Key)key;
            this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
            if (pkcs12Key instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(((PKCS12KeyWithParameters)pkcs12Key).getSalt(), ((PKCS12KeyWithParameters)pkcs12Key).getIterationCount());
            }
            cipherParameters = Util.makePBEParameters(pkcs12Key.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
        }
        else if (key instanceof BCPBEKey) {
            final BCPBEKey bcpbeKey = (BCPBEKey)key;
            if (bcpbeKey.getOID() != null) {
                this.pbeAlgorithm = bcpbeKey.getOID().getId();
            }
            else {
                this.pbeAlgorithm = bcpbeKey.getAlgorithm();
            }
            if (bcpbeKey.getParam() != null) {
                cipherParameters = bcpbeKey.getParam();
                this.pbeSpec = new PBEParameterSpec(bcpbeKey.getSalt(), bcpbeKey.getIterationCount());
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
                cipherParameters = Util.makePBEParameters(bcpbeKey, algorithmParameterSpec, this.cipher.getAlgorithmName());
                this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
            }
            if (bcpbeKey.getIvSize() != 0) {
                this.ivParam = (ParametersWithIV)cipherParameters;
            }
        }
        else if (algorithmParameterSpec == null) {
            if (this.digest > 0) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            cipherParameters = new KeyParameter(key.getEncoded());
        }
        else {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                throw new InvalidAlgorithmParameterException("unknown parameter type.");
            }
            cipherParameters = new ParametersWithIV(new KeyParameter(key.getEncoded()), ((IvParameterSpec)algorithmParameterSpec).getIV());
            this.ivParam = (ParametersWithIV)cipherParameters;
        }
        if (this.ivLength != 0 && !(cipherParameters instanceof ParametersWithIV)) {
            SecureRandom secureRandom2 = secureRandom;
            if (secureRandom2 == null) {
                secureRandom2 = new SecureRandom();
            }
            if (n != 1 && n != 3) {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
            final byte[] array = new byte[this.ivLength];
            secureRandom2.nextBytes(array);
            cipherParameters = new ParametersWithIV(cipherParameters, array);
            this.ivParam = (ParametersWithIV)cipherParameters;
        }
        try {
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
                    throw new InvalidParameterException("unknown opmode " + n + " passed");
                }
            }
        }
        catch (final Exception ex) {
            throw new InvalidKeyException(ex.getMessage());
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
        this.engineInit(n, key, parameterSpec, secureRandom);
        this.engineParams = engineParams;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2];
        this.cipher.processBytes(array, n, n2, array2, 0);
        return array2;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        if (n3 + n2 > array2.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            this.cipher.processBytes(array, n, n2, array2, n3);
            return n2;
        }
        catch (final DataLengthException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) {
        if (n2 != 0) {
            final byte[] engineUpdate = this.engineUpdate(array, n, n2);
            this.cipher.reset();
            return engineUpdate;
        }
        this.cipher.reset();
        return new byte[0];
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        if (n3 + n2 > array2.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (n2 != 0) {
            this.cipher.processBytes(array, n, n2, array2, n3);
        }
        this.cipher.reset();
        return n2;
    }
}
