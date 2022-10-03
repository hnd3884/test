package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.InvalidCipherTextException;
import javax.crypto.BadPaddingException;
import org.bouncycastle.crypto.DataLengthException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.RC5Parameters;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.util.Strings;
import java.security.Key;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import java.security.AlgorithmParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.BufferedBlockCipher;

public class BrokenJCEBlockCipher implements BrokenPBE
{
    private Class[] availableSpecs;
    private BufferedBlockCipher cipher;
    private ParametersWithIV ivParam;
    private int pbeType;
    private int pbeHash;
    private int pbeKeySize;
    private int pbeIvSize;
    private int ivLength;
    private AlgorithmParameters engineParams;
    
    protected BrokenJCEBlockCipher(final BlockCipher blockCipher) {
        this.availableSpecs = new Class[] { IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class };
        this.pbeType = 2;
        this.pbeHash = 1;
        this.ivLength = 0;
        this.engineParams = null;
        this.cipher = new PaddedBufferedBlockCipher(blockCipher);
    }
    
    protected BrokenJCEBlockCipher(final BlockCipher blockCipher, final int pbeType, final int pbeHash, final int pbeKeySize, final int pbeIvSize) {
        this.availableSpecs = new Class[] { IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class };
        this.pbeType = 2;
        this.pbeHash = 1;
        this.ivLength = 0;
        this.engineParams = null;
        this.cipher = new PaddedBufferedBlockCipher(blockCipher);
        this.pbeType = pbeType;
        this.pbeHash = pbeHash;
        this.pbeKeySize = pbeKeySize;
        this.pbeIvSize = pbeIvSize;
    }
    
    protected int engineGetBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    protected byte[] engineGetIV() {
        return (byte[])((this.ivParam != null) ? this.ivParam.getIV() : null);
    }
    
    protected int engineGetKeySize(final Key key) {
        return key.getEncoded().length;
    }
    
    protected int engineGetOutputSize(final int n) {
        return this.cipher.getOutputSize(n);
    }
    
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.ivParam != null) {
            String s = this.cipher.getUnderlyingCipher().getAlgorithmName();
            if (s.indexOf(47) >= 0) {
                s = s.substring(0, s.indexOf(47));
            }
            try {
                (this.engineParams = AlgorithmParameters.getInstance(s, "BC")).init(this.ivParam.getIV());
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        return this.engineParams;
    }
    
    protected void engineSetMode(final String s) {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("ECB")) {
            this.ivLength = 0;
            this.cipher = new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
        }
        else if (upperCase.equals("CBC")) {
            this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
            this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(this.cipher.getUnderlyingCipher()));
        }
        else if (upperCase.startsWith("OFB")) {
            this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
            if (upperCase.length() != 3) {
                this.cipher = new PaddedBufferedBlockCipher(new OFBBlockCipher(this.cipher.getUnderlyingCipher(), Integer.parseInt(upperCase.substring(3))));
            }
            else {
                this.cipher = new PaddedBufferedBlockCipher(new OFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
            }
        }
        else {
            if (!upperCase.startsWith("CFB")) {
                throw new IllegalArgumentException("can't support mode " + s);
            }
            this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
            if (upperCase.length() != 3) {
                this.cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(this.cipher.getUnderlyingCipher(), Integer.parseInt(upperCase.substring(3))));
            }
            else {
                this.cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
            }
        }
    }
    
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NOPADDING")) {
            this.cipher = new BufferedBlockCipher(this.cipher.getUnderlyingCipher());
        }
        else if (upperCase.equals("PKCS5PADDING") || upperCase.equals("PKCS7PADDING") || upperCase.equals("ISO10126PADDING")) {
            this.cipher = new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
        }
        else {
            if (!upperCase.equals("WITHCTS")) {
                throw new NoSuchPaddingException("Padding " + s + " unknown.");
            }
            this.cipher = new CTSBlockCipher(this.cipher.getUnderlyingCipher());
        }
    }
    
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters pbeParameters;
        if (key instanceof BCPBEKey) {
            pbeParameters = Util.makePBEParameters((BCPBEKey)key, algorithmParameterSpec, this.pbeType, this.pbeHash, this.cipher.getUnderlyingCipher().getAlgorithmName(), this.pbeKeySize, this.pbeIvSize);
            if (this.pbeIvSize != 0) {
                this.ivParam = (ParametersWithIV)pbeParameters;
            }
        }
        else if (algorithmParameterSpec == null) {
            pbeParameters = new KeyParameter(key.getEncoded());
        }
        else if (algorithmParameterSpec instanceof IvParameterSpec) {
            if (this.ivLength != 0) {
                pbeParameters = new ParametersWithIV(new KeyParameter(key.getEncoded()), ((IvParameterSpec)algorithmParameterSpec).getIV());
                this.ivParam = (ParametersWithIV)pbeParameters;
            }
            else {
                pbeParameters = new KeyParameter(key.getEncoded());
            }
        }
        else if (algorithmParameterSpec instanceof RC2ParameterSpec) {
            final RC2ParameterSpec rc2ParameterSpec = (RC2ParameterSpec)algorithmParameterSpec;
            pbeParameters = new RC2Parameters(key.getEncoded(), ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits());
            if (rc2ParameterSpec.getIV() != null && this.ivLength != 0) {
                pbeParameters = new ParametersWithIV(pbeParameters, rc2ParameterSpec.getIV());
                this.ivParam = (ParametersWithIV)pbeParameters;
            }
        }
        else {
            if (!(algorithmParameterSpec instanceof RC5ParameterSpec)) {
                throw new InvalidAlgorithmParameterException("unknown parameter type.");
            }
            final RC5ParameterSpec rc5ParameterSpec = (RC5ParameterSpec)algorithmParameterSpec;
            pbeParameters = new RC5Parameters(key.getEncoded(), ((RC5ParameterSpec)algorithmParameterSpec).getRounds());
            if (rc5ParameterSpec.getWordSize() != 32) {
                throw new IllegalArgumentException("can only accept RC5 word size 32 (at the moment...)");
            }
            if (rc5ParameterSpec.getIV() != null && this.ivLength != 0) {
                pbeParameters = new ParametersWithIV(pbeParameters, rc5ParameterSpec.getIV());
                this.ivParam = (ParametersWithIV)pbeParameters;
            }
        }
        if (this.ivLength != 0 && !(pbeParameters instanceof ParametersWithIV)) {
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            if (n != 1 && n != 3) {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
            final byte[] array = new byte[this.ivLength];
            secureRandom.nextBytes(array);
            pbeParameters = new ParametersWithIV(pbeParameters, array);
            this.ivParam = (ParametersWithIV)pbeParameters;
        }
        switch (n) {
            case 1:
            case 3: {
                this.cipher.init(true, pbeParameters);
                break;
            }
            case 2:
            case 4: {
                this.cipher.init(false, pbeParameters);
                break;
            }
            default: {
                System.out.println("eeek!");
                break;
            }
        }
    }
    
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
    
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        final int updateOutputSize = this.cipher.getUpdateOutputSize(n2);
        if (updateOutputSize > 0) {
            final byte[] array2 = new byte[updateOutputSize];
            this.cipher.processBytes(array, n, n2, array2, 0);
            return array2;
        }
        this.cipher.processBytes(array, n, n2, null, 0);
        return null;
    }
    
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        return this.cipher.processBytes(array, n, n2, array2, n3);
    }
    
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        int processBytes = 0;
        final byte[] array2 = new byte[this.engineGetOutputSize(n2)];
        if (n2 != 0) {
            processBytes = this.cipher.processBytes(array, n, n2, array2, 0);
        }
        int n3;
        try {
            n3 = processBytes + this.cipher.doFinal(array2, processBytes);
        }
        catch (final DataLengthException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
        catch (final InvalidCipherTextException ex2) {
            throw new BadPaddingException(ex2.getMessage());
        }
        final byte[] array3 = new byte[n3];
        System.arraycopy(array2, 0, array3, 0, n3);
        return array3;
    }
    
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, BadPaddingException {
        int processBytes = 0;
        if (n2 != 0) {
            processBytes = this.cipher.processBytes(array, n, n2, array2, n3);
        }
        try {
            return processBytes + this.cipher.doFinal(array2, n3 + processBytes);
        }
        catch (final DataLengthException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
        catch (final InvalidCipherTextException ex2) {
            throw new BadPaddingException(ex2.getMessage());
        }
    }
    
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            return this.engineDoFinal(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
    }
    
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException {
        byte[] engineDoFinal;
        try {
            engineDoFinal = this.engineDoFinal(array, 0, array.length);
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
        catch (final IllegalBlockSizeException ex2) {
            throw new InvalidKeyException(ex2.getMessage());
        }
        if (n == 3) {
            return new SecretKeySpec(engineDoFinal, s);
        }
        try {
            final KeyFactory instance = KeyFactory.getInstance(s, "BC");
            if (n == 1) {
                return instance.generatePublic(new X509EncodedKeySpec(engineDoFinal));
            }
            if (n == 2) {
                return instance.generatePrivate(new PKCS8EncodedKeySpec(engineDoFinal));
            }
        }
        catch (final NoSuchProviderException ex3) {
            throw new InvalidKeyException("Unknown key type " + ex3.getMessage());
        }
        catch (final NoSuchAlgorithmException ex4) {
            throw new InvalidKeyException("Unknown key type " + ex4.getMessage());
        }
        catch (final InvalidKeySpecException ex5) {
            throw new InvalidKeyException("Unknown key type " + ex5.getMessage());
        }
        throw new InvalidKeyException("Unknown key type " + n);
    }
    
    public static class BrokePBEWithMD5AndDES extends BrokenJCEBlockCipher
    {
        public BrokePBEWithMD5AndDES() {
            super(new CBCBlockCipher(new DESEngine()), 0, 0, 64, 64);
        }
    }
    
    public static class BrokePBEWithSHA1AndDES extends BrokenJCEBlockCipher
    {
        public BrokePBEWithSHA1AndDES() {
            super(new CBCBlockCipher(new DESEngine()), 0, 1, 64, 64);
        }
    }
    
    public static class BrokePBEWithSHAAndDES2Key extends BrokenJCEBlockCipher
    {
        public BrokePBEWithSHAAndDES2Key() {
            super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 128, 64);
        }
    }
    
    public static class BrokePBEWithSHAAndDES3Key extends BrokenJCEBlockCipher
    {
        public BrokePBEWithSHAAndDES3Key() {
            super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 192, 64);
        }
    }
    
    public static class OldPBEWithSHAAndDES3Key extends BrokenJCEBlockCipher
    {
        public OldPBEWithSHAAndDES3Key() {
            super(new CBCBlockCipher(new DESedeEngine()), 3, 1, 192, 64);
        }
    }
    
    public static class OldPBEWithSHAAndTwofish extends BrokenJCEBlockCipher
    {
        public OldPBEWithSHAAndTwofish() {
            super(new CBCBlockCipher(new TwofishEngine()), 3, 1, 256, 128);
        }
    }
}
