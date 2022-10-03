package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.Strings;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.Key;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import javax.crypto.spec.PSource;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;

public class CipherSpi extends BaseCipherSpi
{
    private final JcaJceHelper helper;
    private AsymmetricBlockCipher cipher;
    private AlgorithmParameterSpec paramSpec;
    private AlgorithmParameters engineParams;
    private boolean publicKeyOnly;
    private boolean privateKeyOnly;
    private ByteArrayOutputStream bOut;
    
    public CipherSpi(final AsymmetricBlockCipher cipher) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        this.cipher = cipher;
    }
    
    public CipherSpi(final OAEPParameterSpec oaepParameterSpec) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        try {
            this.initFromSpec(oaepParameterSpec);
        }
        catch (final NoSuchPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    public CipherSpi(final boolean publicKeyOnly, final boolean privateKeyOnly, final AsymmetricBlockCipher cipher) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        this.publicKeyOnly = publicKeyOnly;
        this.privateKeyOnly = privateKeyOnly;
        this.cipher = cipher;
    }
    
    private void initFromSpec(final OAEPParameterSpec paramSpec) throws NoSuchPaddingException {
        final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)paramSpec.getMGFParameters();
        final Digest digest = DigestFactory.getDigest(mgf1ParameterSpec.getDigestAlgorithm());
        if (digest == null) {
            throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mgf1ParameterSpec.getDigestAlgorithm());
        }
        this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, ((PSource.PSpecified)paramSpec.getPSource()).getValue());
        this.paramSpec = paramSpec;
    }
    
    @Override
    protected int engineGetBlockSize() {
        try {
            return this.cipher.getInputBlockSize();
        }
        catch (final NullPointerException ex) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        if (key instanceof RSAPrivateKey) {
            return ((RSAPrivateKey)key).getModulus().bitLength();
        }
        if (key instanceof RSAPublicKey) {
            return ((RSAPublicKey)key).getModulus().bitLength();
        }
        throw new IllegalArgumentException("not an RSA key!");
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        try {
            return this.cipher.getOutputBlockSize();
        }
        catch (final NullPointerException ex) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                (this.engineParams = this.helper.createAlgorithmParameters("OAEP")).init(this.paramSpec);
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
        if (upperCase.equals("1")) {
            this.privateKeyOnly = true;
            this.publicKeyOnly = false;
            return;
        }
        if (upperCase.equals("2")) {
            this.privateKeyOnly = false;
            this.publicKeyOnly = true;
            return;
        }
        throw new NoSuchAlgorithmException("can't support mode " + s);
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String upperCase = Strings.toUpperCase(s);
        if (upperCase.equals("NOPADDING")) {
            this.cipher = new RSABlindedEngine();
        }
        else if (upperCase.equals("PKCS1PADDING")) {
            this.cipher = new PKCS1Encoding(new RSABlindedEngine());
        }
        else if (upperCase.equals("ISO9796-1PADDING")) {
            this.cipher = new ISO9796d1Encoding(new RSABlindedEngine());
        }
        else if (upperCase.equals("OAEPWITHMD5ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPPADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        }
        else if (upperCase.equals("OAEPWITHSHA1ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-1ANDMGF1PADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        }
        else if (upperCase.equals("OAEPWITHSHA224ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-224ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA256ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-256ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA384ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-384ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA512ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-512ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSource.PSpecified.DEFAULT));
        }
        else if (upperCase.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), PSource.PSpecified.DEFAULT));
        }
        else {
            if (!upperCase.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
                throw new NoSuchPaddingException(s + " unavailable with RSA.");
            }
            this.initFromSpec(new OAEPParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), PSource.PSpecified.DEFAULT));
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec paramSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (paramSpec == null || paramSpec instanceof OAEPParameterSpec) {
            CipherParameters cipherParameters;
            if (key instanceof RSAPublicKey) {
                if (this.privateKeyOnly && n == 1) {
                    throw new InvalidKeyException("mode 1 requires RSAPrivateKey");
                }
                cipherParameters = RSAUtil.generatePublicKeyParameter((RSAPublicKey)key);
            }
            else {
                if (!(key instanceof RSAPrivateKey)) {
                    throw new InvalidKeyException("unknown key type passed to RSA");
                }
                if (this.publicKeyOnly && n == 1) {
                    throw new InvalidKeyException("mode 2 requires RSAPublicKey");
                }
                cipherParameters = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)key);
            }
            if (paramSpec != null) {
                final OAEPParameterSpec oaepParameterSpec = (OAEPParameterSpec)paramSpec;
                this.paramSpec = paramSpec;
                if (!oaepParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !oaepParameterSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                    throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
                }
                if (!(oaepParameterSpec.getMGFParameters() instanceof MGF1ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unkown MGF parameters");
                }
                final Digest digest = DigestFactory.getDigest(oaepParameterSpec.getDigestAlgorithm());
                if (digest == null) {
                    throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + oaepParameterSpec.getDigestAlgorithm());
                }
                final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)oaepParameterSpec.getMGFParameters();
                final Digest digest2 = DigestFactory.getDigest(mgf1ParameterSpec.getDigestAlgorithm());
                if (digest2 == null) {
                    throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mgf1ParameterSpec.getDigestAlgorithm());
                }
                this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, digest2, ((PSource.PSpecified)oaepParameterSpec.getPSource()).getValue());
            }
            if (!(this.cipher instanceof RSABlindedEngine)) {
                if (secureRandom != null) {
                    cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
                }
                else {
                    cipherParameters = new ParametersWithRandom(cipherParameters, new SecureRandom());
                }
            }
            this.bOut.reset();
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
                    throw new InvalidParameterException("unknown opmode " + n + " passed to RSA");
                }
            }
            return;
        }
        throw new InvalidAlgorithmParameterException("unknown parameter type: " + paramSpec.getClass().getName());
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters engineParams, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (engineParams != null) {
            try {
                parameterSpec = engineParams.getParameterSpec(OAEPParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + ex.toString(), ex);
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
            throw new InvalidKeyException("Eeeek! " + ex.toString(), ex);
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.bOut.write(array, n, n2);
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        }
        else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return null;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.bOut.write(array, n, n2);
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        }
        else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return 0;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        if (array != null) {
            this.bOut.write(array, n, n2);
        }
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        }
        else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return this.getOutput();
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, BadPaddingException {
        if (array != null) {
            this.bOut.write(array, n, n2);
        }
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        }
        else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        final byte[] output = this.getOutput();
        for (int i = 0; i != output.length; ++i) {
            array2[n3 + i] = output[i];
        }
        return output.length;
    }
    
    private byte[] getOutput() throws BadPaddingException {
        try {
            final byte[] byteArray = this.bOut.toByteArray();
            return this.cipher.processBlock(byteArray, 0, byteArray.length);
        }
        catch (final InvalidCipherTextException ex) {
            throw new BadBlockException("unable to decrypt block", ex);
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new BadBlockException("unable to decrypt block", ex2);
        }
        finally {
            this.bOut.reset();
        }
    }
    
    public static class ISO9796d1Padding extends CipherSpi
    {
        public ISO9796d1Padding() {
            super(new ISO9796d1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class NoPadding extends CipherSpi
    {
        public NoPadding() {
            super(new RSABlindedEngine());
        }
    }
    
    public static class OAEPPadding extends CipherSpi
    {
        public OAEPPadding() {
            super(OAEPParameterSpec.DEFAULT);
        }
    }
    
    public static class PKCS1v1_5Padding extends CipherSpi
    {
        public PKCS1v1_5Padding() {
            super(new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class PKCS1v1_5Padding_PrivateOnly extends CipherSpi
    {
        public PKCS1v1_5Padding_PrivateOnly() {
            super(false, true, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
    
    public static class PKCS1v1_5Padding_PublicOnly extends CipherSpi
    {
        public PKCS1v1_5Padding_PublicOnly() {
            super(true, false, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
}
