package org.bouncycastle.jcajce.provider.asymmetric.dh;

import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.parsers.DHIESPublicKeyParser;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import java.security.PrivateKey;
import javax.crypto.interfaces.DHPrivateKey;
import org.bouncycastle.jce.interfaces.IESKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.DHUtil;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.IESUtil;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.crypto.params.DHKeyParameters;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.Strings;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.interfaces.DHKey;
import java.security.Key;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.spec.IESParameterSpec;
import java.security.AlgorithmParameters;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import javax.crypto.CipherSpi;

public class IESCipher extends CipherSpi
{
    private final JcaJceHelper helper;
    private final int ivLength;
    private IESEngine engine;
    private int state;
    private ByteArrayOutputStream buffer;
    private AlgorithmParameters engineParam;
    private IESParameterSpec engineSpec;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode;
    private AsymmetricKeyParameter otherKeyParameter;
    
    public IESCipher(final IESEngine engine) {
        this.helper = new BCJcaJceHelper();
        this.state = -1;
        this.buffer = new ByteArrayOutputStream();
        this.engineParam = null;
        this.engineSpec = null;
        this.dhaesMode = false;
        this.otherKeyParameter = null;
        this.engine = engine;
        this.ivLength = 0;
    }
    
    public IESCipher(final IESEngine engine, final int ivLength) {
        this.helper = new BCJcaJceHelper();
        this.state = -1;
        this.buffer = new ByteArrayOutputStream();
        this.engineParam = null;
        this.engineSpec = null;
        this.dhaesMode = false;
        this.otherKeyParameter = null;
        this.engine = engine;
        this.ivLength = ivLength;
    }
    
    public int engineGetBlockSize() {
        if (this.engine.getCipher() != null) {
            return this.engine.getCipher().getBlockSize();
        }
        return 0;
    }
    
    public int engineGetKeySize(final Key key) {
        if (key instanceof DHKey) {
            return ((DHKey)key).getParams().getP().bitLength();
        }
        throw new IllegalArgumentException("not a DH key");
    }
    
    public byte[] engineGetIV() {
        if (this.engineSpec != null) {
            return this.engineSpec.getNonce();
        }
        return null;
    }
    
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParam == null && this.engineSpec != null) {
            try {
                (this.engineParam = this.helper.createAlgorithmParameters("IES")).init(this.engineSpec);
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        return this.engineParam;
    }
    
    public void engineSetMode(final String s) throws NoSuchAlgorithmException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NONE")) {
            this.dhaesMode = false;
        }
        else {
            if (!upperCase.equals("DHAES")) {
                throw new IllegalArgumentException("can't support mode " + s);
            }
            this.dhaesMode = true;
        }
    }
    
    public int engineGetOutputSize(final int n) {
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        final int macSize = this.engine.getMac().getMacSize();
        int n2;
        if (this.otherKeyParameter == null) {
            n2 = 1 + 2 * (((DHKeyParameters)this.key).getParameters().getP().bitLength() + 7) / 8;
        }
        else {
            n2 = 0;
        }
        int n3;
        if (this.engine.getCipher() == null) {
            n3 = n;
        }
        else if (this.state == 1 || this.state == 3) {
            n3 = this.engine.getCipher().getOutputSize(n);
        }
        else {
            if (this.state != 2 && this.state != 4) {
                throw new IllegalStateException("cipher not initialised");
            }
            n3 = this.engine.getCipher().getOutputSize(n - macSize - n2);
        }
        if (this.state == 1 || this.state == 3) {
            return this.buffer.size() + macSize + n2 + n3;
        }
        if (this.state == 2 || this.state == 4) {
            return this.buffer.size() - macSize - n2 + n3;
        }
        throw new IllegalStateException("IESCipher not initialised");
    }
    
    public void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String upperCase = Strings.toUpperCase(s);
        if (!upperCase.equals("NOPADDING")) {
            if (!upperCase.equals("PKCS5PADDING")) {
                if (!upperCase.equals("PKCS7PADDING")) {
                    throw new NoSuchPaddingException("padding not available with IESCipher");
                }
            }
        }
    }
    
    public void engineInit(final int n, final Key key, final AlgorithmParameters engineParam, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (engineParam != null) {
            try {
                parameterSpec = engineParam.getParameterSpec(IESParameterSpec.class);
            }
            catch (final Exception ex) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + ex.toString());
            }
        }
        this.engineParam = engineParam;
        this.engineInit(n, key, parameterSpec, secureRandom);
    }
    
    public void engineInit(final int state, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (algorithmParameterSpec == null) {
            byte[] array = null;
            if (this.ivLength != 0 && state == 1) {
                array = new byte[this.ivLength];
                random.nextBytes(array);
            }
            this.engineSpec = IESUtil.guessParameterSpec(this.engine.getCipher(), array);
        }
        else {
            if (!(algorithmParameterSpec instanceof IESParameterSpec)) {
                throw new InvalidAlgorithmParameterException("must be passed IES parameters");
            }
            this.engineSpec = (IESParameterSpec)algorithmParameterSpec;
        }
        final byte[] nonce = this.engineSpec.getNonce();
        if (this.ivLength != 0 && (nonce == null || nonce.length != this.ivLength)) {
            throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
        }
        if (state == 1 || state == 3) {
            if (key instanceof DHPublicKey) {
                this.key = DHUtil.generatePublicKeyParameter((PublicKey)key);
            }
            else {
                if (!(key instanceof IESKey)) {
                    throw new InvalidKeyException("must be passed recipient's public DH key for encryption");
                }
                final IESKey iesKey = (IESKey)key;
                this.key = DHUtil.generatePublicKeyParameter(iesKey.getPublic());
                this.otherKeyParameter = DHUtil.generatePrivateKeyParameter(iesKey.getPrivate());
            }
        }
        else {
            if (state != 2 && state != 4) {
                throw new InvalidKeyException("must be passed EC key");
            }
            if (key instanceof DHPrivateKey) {
                this.key = DHUtil.generatePrivateKeyParameter((PrivateKey)key);
            }
            else {
                if (!(key instanceof IESKey)) {
                    throw new InvalidKeyException("must be passed recipient's private DH key for decryption");
                }
                final IESKey iesKey2 = (IESKey)key;
                this.otherKeyParameter = DHUtil.generatePublicKeyParameter(iesKey2.getPublic());
                this.key = DHUtil.generatePrivateKeyParameter(iesKey2.getPrivate());
            }
        }
        this.random = random;
        this.state = state;
        this.buffer.reset();
    }
    
    public void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException("cannot handle supplied parameter spec: " + ex.getMessage());
        }
    }
    
    public byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.buffer.write(array, n, n2);
        return null;
    }
    
    public int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.buffer.write(array, n, n2);
        return 0;
    }
    
    public byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        if (n2 != 0) {
            this.buffer.write(array, n, n2);
        }
        final byte[] byteArray = this.buffer.toByteArray();
        this.buffer.reset();
        CipherParameters cipherParameters = new IESWithCipherParameters(this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize());
        if (this.engineSpec.getNonce() != null) {
            cipherParameters = new ParametersWithIV(cipherParameters, this.engineSpec.getNonce());
        }
        final DHParameters parameters = ((DHKeyParameters)this.key).getParameters();
        if (this.otherKeyParameter != null) {
            try {
                if (this.state == 1 || this.state == 3) {
                    this.engine.init(true, this.otherKeyParameter, this.key, cipherParameters);
                }
                else {
                    this.engine.init(false, this.key, this.otherKeyParameter, cipherParameters);
                }
                return this.engine.processBlock(byteArray, 0, byteArray.length);
            }
            catch (final Exception ex) {
                throw new BadBlockException("unable to process block", ex);
            }
        }
        if (this.state == 1 || this.state == 3) {
            final DHKeyPairGenerator dhKeyPairGenerator = new DHKeyPairGenerator();
            dhKeyPairGenerator.init(new DHKeyGenerationParameters(this.random, parameters));
            final EphemeralKeyPairGenerator ephemeralKeyPairGenerator = new EphemeralKeyPairGenerator(dhKeyPairGenerator, new KeyEncoder() {
                public byte[] getEncoded(final AsymmetricKeyParameter asymmetricKeyParameter) {
                    final byte[] array = new byte[(((DHKeyParameters)asymmetricKeyParameter).getParameters().getP().bitLength() + 7) / 8];
                    final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(((DHPublicKeyParameters)asymmetricKeyParameter).getY());
                    if (unsignedByteArray.length > array.length) {
                        throw new IllegalArgumentException("Senders's public key longer than expected.");
                    }
                    System.arraycopy(unsignedByteArray, 0, array, array.length - unsignedByteArray.length, unsignedByteArray.length);
                    return array;
                }
            });
            try {
                this.engine.init(this.key, cipherParameters, ephemeralKeyPairGenerator);
                return this.engine.processBlock(byteArray, 0, byteArray.length);
            }
            catch (final Exception ex2) {
                throw new BadBlockException("unable to process block", ex2);
            }
        }
        if (this.state != 2) {
            if (this.state != 4) {
                throw new IllegalStateException("IESCipher not initialised");
            }
        }
        try {
            this.engine.init(this.key, cipherParameters, new DHIESPublicKeyParser(((DHKeyParameters)this.key).getParameters()));
            return this.engine.processBlock(byteArray, 0, byteArray.length);
        }
        catch (final InvalidCipherTextException ex3) {
            throw new BadBlockException("unable to process block", ex3);
        }
        throw new IllegalStateException("IESCipher not initialised");
    }
    
    public int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        final byte[] engineDoFinal = this.engineDoFinal(array, n, n2);
        System.arraycopy(engineDoFinal, 0, array2, n3, engineDoFinal.length);
        return engineDoFinal.length;
    }
    
    public static class IES extends IESCipher
    {
        public IES() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1())));
        }
    }
    
    public static class IESwithAESCBC extends IESCipher
    {
        public IESwithAESCBC() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1()), new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()))), 16);
        }
    }
    
    public static class IESwithDESedeCBC extends IESCipher
    {
        public IESwithDESedeCBC() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1()), new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()))), 8);
        }
    }
}
