package org.bouncycastle.jcajce.provider.symmetric.util;

import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import java.lang.reflect.Constructor;
import org.bouncycastle.crypto.OutputLengthException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.bouncycastle.crypto.DataLengthException;
import javax.crypto.ShortBufferException;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.PKCS12Key;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.PGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.cms.GCMParameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.BlockCipher;

public class BaseBlockCipher extends BaseWrapCipher implements PBE
{
    private static final Class gcmSpecClass;
    private Class[] availableSpecs;
    private BlockCipher baseEngine;
    private BlockCipherProvider engineProvider;
    private GenericBlockCipher cipher;
    private ParametersWithIV ivParam;
    private AEADParameters aeadParams;
    private int keySizeInBits;
    private int scheme;
    private int digest;
    private int ivLength;
    private boolean padded;
    private boolean fixedIv;
    private PBEParameterSpec pbeSpec;
    private String pbeAlgorithm;
    private String modeName;
    
    protected BaseBlockCipher(final BlockCipher baseEngine) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = baseEngine;
        this.cipher = new BufferedGenericBlockCipher(baseEngine);
    }
    
    protected BaseBlockCipher(final BlockCipher baseEngine, final int scheme, final int digest, final int keySizeInBits, final int ivLength) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = baseEngine;
        this.scheme = scheme;
        this.digest = digest;
        this.keySizeInBits = keySizeInBits;
        this.ivLength = ivLength;
        this.cipher = new BufferedGenericBlockCipher(baseEngine);
    }
    
    protected BaseBlockCipher(final BlockCipherProvider engineProvider) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engineProvider.get();
        this.engineProvider = engineProvider;
        this.cipher = new BufferedGenericBlockCipher(engineProvider.get());
    }
    
    protected BaseBlockCipher(final AEADBlockCipher aeadBlockCipher) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = aeadBlockCipher.getUnderlyingCipher();
        this.ivLength = this.baseEngine.getBlockSize();
        this.cipher = new AEADGenericBlockCipher(aeadBlockCipher);
    }
    
    protected BaseBlockCipher(final AEADBlockCipher aeadBlockCipher, final boolean fixedIv, final int ivLength) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = aeadBlockCipher.getUnderlyingCipher();
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(aeadBlockCipher);
    }
    
    protected BaseBlockCipher(final BlockCipher blockCipher, final int n) {
        this(blockCipher, true, n);
    }
    
    protected BaseBlockCipher(final BlockCipher baseEngine, final boolean fixedIv, final int n) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = baseEngine;
        this.fixedIv = fixedIv;
        this.cipher = new BufferedGenericBlockCipher(baseEngine);
        this.ivLength = n / 8;
    }
    
    protected BaseBlockCipher(final BufferedBlockCipher bufferedBlockCipher, final int n) {
        this(bufferedBlockCipher, true, n);
    }
    
    protected BaseBlockCipher(final BufferedBlockCipher bufferedBlockCipher, final boolean fixedIv, final int n) {
        this.availableSpecs = new Class[] { RC2ParameterSpec.class, RC5ParameterSpec.class, BaseBlockCipher.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class };
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = bufferedBlockCipher.getUnderlyingCipher();
        this.cipher = new BufferedGenericBlockCipher(bufferedBlockCipher);
        this.fixedIv = fixedIv;
        this.ivLength = n / 8;
    }
    
    @Override
    protected int engineGetBlockSize() {
        return this.baseEngine.getBlockSize();
    }
    
    @Override
    protected byte[] engineGetIV() {
        if (this.aeadParams != null) {
            return this.aeadParams.getNonce();
        }
        return (byte[])((this.ivParam != null) ? this.ivParam.getIV() : null);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        return key.getEncoded().length * 8;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.cipher.getOutputSize(n);
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    (this.engineParams = this.createParametersInstance(this.pbeAlgorithm)).init(this.pbeSpec);
                    return this.engineParams;
                }
                catch (final Exception ex) {
                    return null;
                }
            }
            if (this.aeadParams != null) {
                try {
                    (this.engineParams = this.createParametersInstance("GCM")).init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
                    return this.engineParams;
                }
                catch (final Exception ex2) {
                    throw new RuntimeException(ex2.toString());
                }
            }
            if (this.ivParam != null) {
                String s = this.cipher.getUnderlyingCipher().getAlgorithmName();
                if (s.indexOf(47) >= 0) {
                    s = s.substring(0, s.indexOf(47));
                }
                try {
                    (this.engineParams = this.createParametersInstance(s)).init(new IvParameterSpec(this.ivParam.getIV()));
                }
                catch (final Exception ex3) {
                    throw new RuntimeException(ex3.toString());
                }
            }
        }
        return this.engineParams;
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        this.modeName = Strings.toUpperCase(s);
        if (this.modeName.equals("ECB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(this.baseEngine);
        }
        else if (this.modeName.equals("CBC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CBCBlockCipher(this.baseEngine));
        }
        else if (this.modeName.startsWith("OFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, Integer.parseInt(this.modeName.substring(3))));
            }
            else {
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
        }
        else if (this.modeName.startsWith("CFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(this.baseEngine, Integer.parseInt(this.modeName.substring(3))));
            }
            else {
                this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
        }
        else if (this.modeName.startsWith("PGP")) {
            final boolean equalsIgnoreCase = this.modeName.equalsIgnoreCase("PGPCFBwithIV");
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new PGPCFBBlockCipher(this.baseEngine, equalsIgnoreCase));
        }
        else if (this.modeName.equalsIgnoreCase("OpenPGPCFB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(new OpenPGPCFBBlockCipher(this.baseEngine));
        }
        else if (this.modeName.startsWith("SIC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.ivLength < 16) {
                throw new IllegalArgumentException("Warning: SIC-Mode can become a twotime-pad if the blocksize of the cipher is too small. Use a cipher with a block size of at least 128 bits (e.g. AES)");
            }
            this.fixedIv = false;
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
        }
        else if (this.modeName.startsWith("CTR")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.fixedIv = false;
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new KCTRBlockCipher(this.baseEngine)));
            }
            else {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
            }
        }
        else if (this.modeName.startsWith("GOFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GOFBBlockCipher(this.baseEngine)));
        }
        else if (this.modeName.startsWith("GCFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GCFBBlockCipher(this.baseEngine)));
        }
        else if (this.modeName.startsWith("CTS")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(new CBCBlockCipher(this.baseEngine)));
        }
        else if (this.modeName.startsWith("CCM")) {
            this.ivLength = 12;
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.cipher = new AEADGenericBlockCipher(new KCCMBlockCipher(this.baseEngine));
            }
            else {
                this.cipher = new AEADGenericBlockCipher(new CCMBlockCipher(this.baseEngine));
            }
        }
        else if (this.modeName.startsWith("OCB")) {
            if (this.engineProvider == null) {
                throw new NoSuchAlgorithmException("can't support mode " + s);
            }
            this.ivLength = 15;
            this.cipher = new AEADGenericBlockCipher(new OCBBlockCipher(this.baseEngine, this.engineProvider.get()));
        }
        else if (this.modeName.startsWith("EAX")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new AEADGenericBlockCipher(new EAXBlockCipher(this.baseEngine));
        }
        else {
            if (!this.modeName.startsWith("GCM")) {
                throw new NoSuchAlgorithmException("can't support mode " + s);
            }
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.cipher = new AEADGenericBlockCipher(new KGCMBlockCipher(this.baseEngine));
            }
            else {
                this.cipher = new AEADGenericBlockCipher(new GCMBlockCipher(this.baseEngine));
            }
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(this.cipher.getUnderlyingCipher()));
            }
        }
        else if (upperCase.equals("WITHCTS")) {
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
        }
        else {
            this.padded = true;
            if (this.isAEADModeName(this.modeName)) {
                throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
            }
            if (upperCase.equals("PKCS5PADDING") || upperCase.equals("PKCS7PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            }
            else if (upperCase.equals("ZEROBYTEPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            }
            else if (upperCase.equals("ISO10126PADDING") || upperCase.equals("ISO10126-2PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            }
            else if (upperCase.equals("X9.23PADDING") || upperCase.equals("X923PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            }
            else if (upperCase.equals("ISO7816-4PADDING") || upperCase.equals("ISO9797-1PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
            }
            else {
                if (!upperCase.equals("TBCPADDING")) {
                    throw new NoSuchPaddingException("Padding " + s + " unknown.");
                }
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
            }
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        this.aeadParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + ((key != null) ? key.getAlgorithm() : null) + " not suitable for symmetric enryption.");
        }
        if (algorithmParameterSpec == null && this.baseEngine.getAlgorithmName().startsWith("RC5-64")) {
            throw new InvalidAlgorithmParameterException("RC5 requires an RC5ParametersSpec to be passed in.");
        }
        CipherParameters cipherParameters = null;
        Label_0861: {
            if (this.scheme != 2) {
                if (!(key instanceof PKCS12Key)) {
                    if (key instanceof PBKDF1Key) {
                        final PBKDF1Key pbkdf1Key = (PBKDF1Key)key;
                        if (algorithmParameterSpec instanceof PBEParameterSpec) {
                            this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
                        }
                        if (pbkdf1Key instanceof PBKDF1KeyWithParameters && this.pbeSpec == null) {
                            this.pbeSpec = new PBEParameterSpec(((PBKDF1KeyWithParameters)pbkdf1Key).getSalt(), ((PBKDF1KeyWithParameters)pbkdf1Key).getIterationCount());
                        }
                        cipherParameters = Util.makePBEParameters(pbkdf1Key.getEncoded(), 0, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                        if (cipherParameters instanceof ParametersWithIV) {
                            this.ivParam = (ParametersWithIV)cipherParameters;
                        }
                        break Label_0861;
                    }
                    if (key instanceof BCPBEKey) {
                        final BCPBEKey bcpbeKey = (BCPBEKey)key;
                        if (bcpbeKey.getOID() != null) {
                            this.pbeAlgorithm = bcpbeKey.getOID().getId();
                        }
                        else {
                            this.pbeAlgorithm = bcpbeKey.getAlgorithm();
                        }
                        if (bcpbeKey.getParam() != null) {
                            cipherParameters = this.adjustParameters(algorithmParameterSpec, bcpbeKey.getParam());
                        }
                        else {
                            if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                            }
                            this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
                            cipherParameters = Util.makePBEParameters(bcpbeKey, algorithmParameterSpec, this.cipher.getUnderlyingCipher().getAlgorithmName());
                        }
                        if (cipherParameters instanceof ParametersWithIV) {
                            this.ivParam = (ParametersWithIV)cipherParameters;
                        }
                        break Label_0861;
                    }
                    if (key instanceof PBEKey) {
                        final PBEKey pbeKey = (PBEKey)key;
                        this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
                        if (pbeKey instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
                            this.pbeSpec = new PBEParameterSpec(pbeKey.getSalt(), pbeKey.getIterationCount());
                        }
                        cipherParameters = Util.makePBEParameters(pbeKey.getEncoded(), this.scheme, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                        if (cipherParameters instanceof ParametersWithIV) {
                            this.ivParam = (ParametersWithIV)cipherParameters;
                        }
                        break Label_0861;
                    }
                    if (key instanceof RepeatedSecretKeySpec) {
                        cipherParameters = null;
                        break Label_0861;
                    }
                    if (this.scheme == 0 || this.scheme == 4 || this.scheme == 1 || this.scheme == 5) {
                        throw new InvalidKeyException("Algorithm requires a PBE key");
                    }
                    cipherParameters = new KeyParameter(key.getEncoded());
                    break Label_0861;
                }
            }
            SecretKey secretKey;
            try {
                secretKey = (SecretKey)key;
            }
            catch (final Exception ex) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                this.pbeSpec = (PBEParameterSpec)algorithmParameterSpec;
            }
            if (secretKey instanceof PBEKey && this.pbeSpec == null) {
                final PBEKey pbeKey2 = (PBEKey)secretKey;
                if (pbeKey2.getSalt() == null) {
                    throw new InvalidAlgorithmParameterException("PBEKey requires parameters to specify salt");
                }
                this.pbeSpec = new PBEParameterSpec(pbeKey2.getSalt(), pbeKey2.getIterationCount());
            }
            if (this.pbeSpec == null && !(secretKey instanceof PBEKey)) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            if (key instanceof BCPBEKey) {
                final CipherParameters param = ((BCPBEKey)key).getParam();
                if (param instanceof ParametersWithIV) {
                    cipherParameters = param;
                }
                else {
                    if (param != null) {
                        throw new InvalidKeyException("Algorithm requires a PBE key suitable for PKCS12");
                    }
                    cipherParameters = Util.makePBEParameters(secretKey.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                }
            }
            else {
                cipherParameters = Util.makePBEParameters(secretKey.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
            }
            if (cipherParameters instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV)cipherParameters;
            }
        }
        Label_1828: {
            if (algorithmParameterSpec instanceof AEADParameterSpec) {
                if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                    throw new InvalidAlgorithmParameterException("AEADParameterSpec can only be used with AEAD modes.");
                }
                final AEADParameterSpec aeadParameterSpec = (AEADParameterSpec)algorithmParameterSpec;
                KeyParameter keyParameter;
                if (cipherParameters instanceof ParametersWithIV) {
                    keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
                }
                else {
                    keyParameter = (KeyParameter)cipherParameters;
                }
                final AEADParameters aeadParams = new AEADParameters(keyParameter, aeadParameterSpec.getMacSizeInBits(), aeadParameterSpec.getNonce(), aeadParameterSpec.getAssociatedData());
                this.aeadParams = aeadParams;
                cipherParameters = aeadParams;
            }
            else if (algorithmParameterSpec instanceof IvParameterSpec) {
                if (this.ivLength != 0) {
                    final IvParameterSpec ivParameterSpec = (IvParameterSpec)algorithmParameterSpec;
                    if (ivParameterSpec.getIV().length != this.ivLength && !(this.cipher instanceof AEADGenericBlockCipher) && this.fixedIv) {
                        throw new InvalidAlgorithmParameterException("IV must be " + this.ivLength + " bytes long.");
                    }
                    if (cipherParameters instanceof ParametersWithIV) {
                        cipherParameters = new ParametersWithIV(((ParametersWithIV)cipherParameters).getParameters(), ivParameterSpec.getIV());
                    }
                    else {
                        cipherParameters = new ParametersWithIV(cipherParameters, ivParameterSpec.getIV());
                    }
                    this.ivParam = (ParametersWithIV)cipherParameters;
                }
                else if (this.modeName != null && this.modeName.equals("ECB")) {
                    throw new InvalidAlgorithmParameterException("ECB mode does not use an IV");
                }
            }
            else if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                final GOST28147ParameterSpec gost28147ParameterSpec = (GOST28147ParameterSpec)algorithmParameterSpec;
                cipherParameters = new ParametersWithSBox(new KeyParameter(key.getEncoded()), ((GOST28147ParameterSpec)algorithmParameterSpec).getSbox());
                if (gost28147ParameterSpec.getIV() != null && this.ivLength != 0) {
                    if (cipherParameters instanceof ParametersWithIV) {
                        cipherParameters = new ParametersWithIV(((ParametersWithIV)cipherParameters).getParameters(), gost28147ParameterSpec.getIV());
                    }
                    else {
                        cipherParameters = new ParametersWithIV(cipherParameters, gost28147ParameterSpec.getIV());
                    }
                    this.ivParam = (ParametersWithIV)cipherParameters;
                }
            }
            else if (algorithmParameterSpec instanceof RC2ParameterSpec) {
                final RC2ParameterSpec rc2ParameterSpec = (RC2ParameterSpec)algorithmParameterSpec;
                cipherParameters = new RC2Parameters(key.getEncoded(), ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits());
                if (rc2ParameterSpec.getIV() != null && this.ivLength != 0) {
                    if (cipherParameters instanceof ParametersWithIV) {
                        cipherParameters = new ParametersWithIV(((ParametersWithIV)cipherParameters).getParameters(), rc2ParameterSpec.getIV());
                    }
                    else {
                        cipherParameters = new ParametersWithIV(cipherParameters, rc2ParameterSpec.getIV());
                    }
                    this.ivParam = (ParametersWithIV)cipherParameters;
                }
            }
            else if (algorithmParameterSpec instanceof RC5ParameterSpec) {
                final RC5ParameterSpec rc5ParameterSpec = (RC5ParameterSpec)algorithmParameterSpec;
                cipherParameters = new RC5Parameters(key.getEncoded(), ((RC5ParameterSpec)algorithmParameterSpec).getRounds());
                if (!this.baseEngine.getAlgorithmName().startsWith("RC5")) {
                    throw new InvalidAlgorithmParameterException("RC5 parameters passed to a cipher that is not RC5.");
                }
                if (this.baseEngine.getAlgorithmName().equals("RC5-32")) {
                    if (rc5ParameterSpec.getWordSize() != 32) {
                        throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 32 not " + rc5ParameterSpec.getWordSize() + ".");
                    }
                }
                else if (this.baseEngine.getAlgorithmName().equals("RC5-64") && rc5ParameterSpec.getWordSize() != 64) {
                    throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 64 not " + rc5ParameterSpec.getWordSize() + ".");
                }
                if (rc5ParameterSpec.getIV() != null && this.ivLength != 0) {
                    if (cipherParameters instanceof ParametersWithIV) {
                        cipherParameters = new ParametersWithIV(((ParametersWithIV)cipherParameters).getParameters(), rc5ParameterSpec.getIV());
                    }
                    else {
                        cipherParameters = new ParametersWithIV(cipherParameters, rc5ParameterSpec.getIV());
                    }
                    this.ivParam = (ParametersWithIV)cipherParameters;
                }
            }
            else {
                if (BaseBlockCipher.gcmSpecClass != null && BaseBlockCipher.gcmSpecClass.isInstance(algorithmParameterSpec)) {
                    if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                        throw new InvalidAlgorithmParameterException("GCMParameterSpec can only be used with AEAD modes.");
                    }
                    try {
                        final Method declaredMethod = BaseBlockCipher.gcmSpecClass.getDeclaredMethod("getTLen", (Class[])new Class[0]);
                        final Method declaredMethod2 = BaseBlockCipher.gcmSpecClass.getDeclaredMethod("getIV", (Class[])new Class[0]);
                        KeyParameter keyParameter2;
                        if (cipherParameters instanceof ParametersWithIV) {
                            keyParameter2 = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
                        }
                        else {
                            keyParameter2 = (KeyParameter)cipherParameters;
                        }
                        final AEADParameters aeadParams2 = new AEADParameters(keyParameter2, (int)declaredMethod.invoke(algorithmParameterSpec, new Object[0]), (byte[])declaredMethod2.invoke(algorithmParameterSpec, new Object[0]));
                        this.aeadParams = aeadParams2;
                        cipherParameters = aeadParams2;
                        break Label_1828;
                    }
                    catch (final Exception ex2) {
                        throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
                    }
                }
                if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unknown parameter type.");
                }
            }
        }
        if (this.ivLength != 0 && !(cipherParameters instanceof ParametersWithIV) && !(cipherParameters instanceof AEADParameters)) {
            SecureRandom secureRandom2 = secureRandom;
            if (secureRandom2 == null) {
                secureRandom2 = new SecureRandom();
            }
            if (n == 1 || n == 3) {
                final byte[] array = new byte[this.ivLength];
                secureRandom2.nextBytes(array);
                cipherParameters = new ParametersWithIV(cipherParameters, array);
                this.ivParam = (ParametersWithIV)cipherParameters;
            }
            else if (this.cipher.getUnderlyingCipher().getAlgorithmName().indexOf("PGPCFB") < 0) {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
        }
        if (secureRandom != null && this.padded) {
            cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
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
            if (this.cipher instanceof AEADGenericBlockCipher && this.aeadParams == null) {
                this.aeadParams = new AEADParameters((KeyParameter)this.ivParam.getParameters(), ((AEADGenericBlockCipher)this.cipher).cipher.getMac().length * 8, this.ivParam.getIV());
            }
        }
        catch (final Exception ex3) {
            throw new InvalidKeyOrParametersException(ex3.getMessage(), ex3);
        }
    }
    
    private CipherParameters adjustParameters(final AlgorithmParameterSpec algorithmParameterSpec, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithIV) {
            final CipherParameters parameters = ((ParametersWithIV)cipherParameters).getParameters();
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                this.ivParam = new ParametersWithIV(parameters, ((IvParameterSpec)algorithmParameterSpec).getIV());
                cipherParameters = this.ivParam;
            }
            else if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                final GOST28147ParameterSpec gost28147ParameterSpec = (GOST28147ParameterSpec)algorithmParameterSpec;
                cipherParameters = new ParametersWithSBox(cipherParameters, gost28147ParameterSpec.getSbox());
                if (gost28147ParameterSpec.getIV() != null && this.ivLength != 0) {
                    this.ivParam = new ParametersWithIV(parameters, gost28147ParameterSpec.getIV());
                    cipherParameters = this.ivParam;
                }
            }
        }
        else if (algorithmParameterSpec instanceof IvParameterSpec) {
            this.ivParam = new ParametersWithIV(cipherParameters, ((IvParameterSpec)algorithmParameterSpec).getIV());
            cipherParameters = this.ivParam;
        }
        else if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
            final GOST28147ParameterSpec gost28147ParameterSpec2 = (GOST28147ParameterSpec)algorithmParameterSpec;
            cipherParameters = new ParametersWithSBox(cipherParameters, gost28147ParameterSpec2.getSbox());
            if (gost28147ParameterSpec2.getIV() != null && this.ivLength != 0) {
                cipherParameters = new ParametersWithIV(cipherParameters, gost28147ParameterSpec2.getIV());
            }
        }
        return cipherParameters;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters engineParams, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (engineParams != null) {
            for (int i = 0; i != this.availableSpecs.length; ++i) {
                if (this.availableSpecs[i] != null) {
                    try {
                        parameterSpec = engineParams.getParameterSpec((Class<AlgorithmParameterSpec>)this.availableSpecs[i]);
                        break;
                    }
                    catch (final Exception ex) {}
                }
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
    protected void engineUpdateAAD(final byte[] array, final int n, final int n2) {
        this.cipher.updateAAD(array, n, n2);
    }
    
    @Override
    protected void engineUpdateAAD(final ByteBuffer byteBuffer) {
        this.engineUpdateAAD(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        final int updateOutputSize = this.cipher.getUpdateOutputSize(n2);
        if (updateOutputSize <= 0) {
            this.cipher.processBytes(array, n, n2, null, 0);
            return null;
        }
        final byte[] array2 = new byte[updateOutputSize];
        final int processBytes = this.cipher.processBytes(array, n, n2, array2, 0);
        if (processBytes == 0) {
            return null;
        }
        if (processBytes != array2.length) {
            final byte[] array3 = new byte[processBytes];
            System.arraycopy(array2, 0, array3, 0, processBytes);
            return array3;
        }
        return array2;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        if (n3 + this.cipher.getUpdateOutputSize(n2) > array2.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            return this.cipher.processBytes(array, n, n2, array2, n3);
        }
        catch (final DataLengthException ex) {
            throw new IllegalStateException(ex.toString());
        }
    }
    
    @Override
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
        if (n3 == array2.length) {
            return array2;
        }
        final byte[] array3 = new byte[n3];
        System.arraycopy(array2, 0, array3, 0, n3);
        return array3;
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        int processBytes = 0;
        if (n3 + this.engineGetOutputSize(n2) > array2.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            if (n2 != 0) {
                processBytes = this.cipher.processBytes(array, n, n2, array2, n3);
            }
            return processBytes + this.cipher.doFinal(array2, n3 + processBytes);
        }
        catch (final OutputLengthException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
        catch (final DataLengthException ex2) {
            throw new IllegalBlockSizeException(ex2.getMessage());
        }
    }
    
    private boolean isAEADModeName(final String s) {
        return "CCM".equals(s) || "EAX".equals(s) || "GCM".equals(s) || "OCB".equals(s);
    }
    
    static {
        gcmSpecClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.spec.GCMParameterSpec");
    }
    
    private static class AEADGenericBlockCipher implements GenericBlockCipher
    {
        private static final Constructor aeadBadTagConstructor;
        private AEADBlockCipher cipher;
        
        private static Constructor findExceptionConstructor(final Class clazz) {
            try {
                return clazz.getConstructor(String.class);
            }
            catch (final Exception ex) {
                return null;
            }
        }
        
        AEADGenericBlockCipher(final AEADBlockCipher cipher) {
            this.cipher = cipher;
        }
        
        public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
            this.cipher.init(b, cipherParameters);
        }
        
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }
        
        public boolean wrapOnNoPadding() {
            return false;
        }
        
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }
        
        public int getOutputSize(final int n) {
            return this.cipher.getOutputSize(n);
        }
        
        public int getUpdateOutputSize(final int n) {
            return this.cipher.getUpdateOutputSize(n);
        }
        
        public void updateAAD(final byte[] array, final int n, final int n2) {
            this.cipher.processAADBytes(array, n, n2);
        }
        
        public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException {
            return this.cipher.processByte(b, array, n);
        }
        
        public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
            return this.cipher.processBytes(array, n, n2, array2, n3);
        }
        
        public int doFinal(final byte[] array, final int n) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(array, n);
            }
            catch (final InvalidCipherTextException ex) {
                if (AEADGenericBlockCipher.aeadBadTagConstructor != null) {
                    BadPaddingException ex2 = null;
                    try {
                        ex2 = AEADGenericBlockCipher.aeadBadTagConstructor.newInstance(ex.getMessage());
                    }
                    catch (final Exception ex3) {}
                    if (ex2 != null) {
                        throw ex2;
                    }
                }
                throw new BadPaddingException(ex.getMessage());
            }
        }
        
        static {
            final Class loadClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
            if (loadClass != null) {
                aeadBadTagConstructor = findExceptionConstructor(loadClass);
            }
            else {
                aeadBadTagConstructor = null;
            }
        }
    }
    
    private interface GenericBlockCipher
    {
        void init(final boolean p0, final CipherParameters p1) throws IllegalArgumentException;
        
        boolean wrapOnNoPadding();
        
        String getAlgorithmName();
        
        BlockCipher getUnderlyingCipher();
        
        int getOutputSize(final int p0);
        
        int getUpdateOutputSize(final int p0);
        
        void updateAAD(final byte[] p0, final int p1, final int p2);
        
        int processByte(final byte p0, final byte[] p1, final int p2) throws DataLengthException;
        
        int processBytes(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws DataLengthException;
        
        int doFinal(final byte[] p0, final int p1) throws IllegalStateException, BadPaddingException;
    }
    
    private static class BufferedGenericBlockCipher implements GenericBlockCipher
    {
        private BufferedBlockCipher cipher;
        
        BufferedGenericBlockCipher(final BufferedBlockCipher cipher) {
            this.cipher = cipher;
        }
        
        BufferedGenericBlockCipher(final BlockCipher blockCipher) {
            this.cipher = new PaddedBufferedBlockCipher(blockCipher);
        }
        
        BufferedGenericBlockCipher(final BlockCipher blockCipher, final BlockCipherPadding blockCipherPadding) {
            this.cipher = new PaddedBufferedBlockCipher(blockCipher, blockCipherPadding);
        }
        
        public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
            this.cipher.init(b, cipherParameters);
        }
        
        public boolean wrapOnNoPadding() {
            return !(this.cipher instanceof CTSBlockCipher);
        }
        
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }
        
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }
        
        public int getOutputSize(final int n) {
            return this.cipher.getOutputSize(n);
        }
        
        public int getUpdateOutputSize(final int n) {
            return this.cipher.getUpdateOutputSize(n);
        }
        
        public void updateAAD(final byte[] array, final int n, final int n2) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }
        
        public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException {
            return this.cipher.processByte(b, array, n);
        }
        
        public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
            return this.cipher.processBytes(array, n, n2, array2, n3);
        }
        
        public int doFinal(final byte[] array, final int n) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(array, n);
            }
            catch (final InvalidCipherTextException ex) {
                throw new BadPaddingException(ex.getMessage());
            }
        }
    }
    
    private static class InvalidKeyOrParametersException extends InvalidKeyException
    {
        private final Throwable cause;
        
        InvalidKeyOrParametersException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
