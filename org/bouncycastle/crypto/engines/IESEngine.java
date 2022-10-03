package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.BigIntegers;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.BasicAgreement;

public class IESEngine
{
    BasicAgreement agree;
    DerivationFunction kdf;
    Mac mac;
    BufferedBlockCipher cipher;
    byte[] macBuf;
    boolean forEncryption;
    CipherParameters privParam;
    CipherParameters pubParam;
    IESParameters param;
    byte[] V;
    private EphemeralKeyPairGenerator keyPairGenerator;
    private KeyParser keyParser;
    private byte[] IV;
    
    public IESEngine(final BasicAgreement agree, final DerivationFunction kdf, final Mac mac) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = null;
    }
    
    public IESEngine(final BasicAgreement agree, final DerivationFunction kdf, final Mac mac, final BufferedBlockCipher cipher) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = cipher;
    }
    
    public void init(final boolean forEncryption, final CipherParameters privParam, final CipherParameters pubParam, final CipherParameters cipherParameters) {
        this.forEncryption = forEncryption;
        this.privParam = privParam;
        this.pubParam = pubParam;
        this.V = new byte[0];
        this.extractParams(cipherParameters);
    }
    
    public void init(final AsymmetricKeyParameter pubParam, final CipherParameters cipherParameters, final EphemeralKeyPairGenerator keyPairGenerator) {
        this.forEncryption = true;
        this.pubParam = pubParam;
        this.keyPairGenerator = keyPairGenerator;
        this.extractParams(cipherParameters);
    }
    
    public void init(final AsymmetricKeyParameter privParam, final CipherParameters cipherParameters, final KeyParser keyParser) {
        this.forEncryption = false;
        this.privParam = privParam;
        this.keyParser = keyParser;
        this.extractParams(cipherParameters);
    }
    
    private void extractParams(final CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithIV) {
            this.IV = ((ParametersWithIV)cipherParameters).getIV();
            this.param = (IESParameters)((ParametersWithIV)cipherParameters).getParameters();
        }
        else {
            this.IV = null;
            this.param = (IESParameters)cipherParameters;
        }
    }
    
    public BufferedBlockCipher getCipher() {
        return this.cipher;
    }
    
    public Mac getMac() {
        return this.mac;
    }
    
    private byte[] encryptBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        byte[] array3;
        byte[] array5;
        int n3;
        if (this.cipher == null) {
            final byte[] array2 = new byte[n2];
            array3 = new byte[this.param.getMacKeySize() / 8];
            final byte[] array4 = new byte[array2.length + array3.length];
            this.kdf.generateBytes(array4, 0, array4.length);
            if (this.V.length != 0) {
                System.arraycopy(array4, 0, array3, 0, array3.length);
                System.arraycopy(array4, array3.length, array2, 0, array2.length);
            }
            else {
                System.arraycopy(array4, 0, array2, 0, array2.length);
                System.arraycopy(array4, n2, array3, 0, array3.length);
            }
            array5 = new byte[n2];
            for (int i = 0; i != n2; ++i) {
                array5[i] = (byte)(array[n + i] ^ array2[i]);
            }
            n3 = n2;
        }
        else {
            final byte[] array6 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
            array3 = new byte[this.param.getMacKeySize() / 8];
            final byte[] array7 = new byte[array6.length + array3.length];
            this.kdf.generateBytes(array7, 0, array7.length);
            System.arraycopy(array7, 0, array6, 0, array6.length);
            System.arraycopy(array7, array6.length, array3, 0, array3.length);
            if (this.IV != null) {
                this.cipher.init(true, new ParametersWithIV(new KeyParameter(array6), this.IV));
            }
            else {
                this.cipher.init(true, new KeyParameter(array6));
            }
            array5 = new byte[this.cipher.getOutputSize(n2)];
            final int processBytes = this.cipher.processBytes(array, n, n2, array5, 0);
            n3 = processBytes + this.cipher.doFinal(array5, processBytes);
        }
        final byte[] encodingV = this.param.getEncodingV();
        byte[] lengthTag = null;
        if (this.V.length != 0) {
            lengthTag = this.getLengthTag(encodingV);
        }
        final byte[] array8 = new byte[this.mac.getMacSize()];
        this.mac.init(new KeyParameter(array3));
        this.mac.update(array5, 0, array5.length);
        if (encodingV != null) {
            this.mac.update(encodingV, 0, encodingV.length);
        }
        if (this.V.length != 0) {
            this.mac.update(lengthTag, 0, lengthTag.length);
        }
        this.mac.doFinal(array8, 0);
        final byte[] array9 = new byte[this.V.length + n3 + array8.length];
        System.arraycopy(this.V, 0, array9, 0, this.V.length);
        System.arraycopy(array5, 0, array9, this.V.length, n3);
        System.arraycopy(array8, 0, array9, this.V.length + n3, array8.length);
        return array9;
    }
    
    private byte[] decryptBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        int processBytes = 0;
        if (n2 < this.V.length + this.mac.getMacSize()) {
            throw new InvalidCipherTextException("Length of input must be greater than the MAC and V combined");
        }
        byte[] array3;
        byte[] array5;
        if (this.cipher == null) {
            final byte[] array2 = new byte[n2 - this.V.length - this.mac.getMacSize()];
            array3 = new byte[this.param.getMacKeySize() / 8];
            final byte[] array4 = new byte[array2.length + array3.length];
            this.kdf.generateBytes(array4, 0, array4.length);
            if (this.V.length != 0) {
                System.arraycopy(array4, 0, array3, 0, array3.length);
                System.arraycopy(array4, array3.length, array2, 0, array2.length);
            }
            else {
                System.arraycopy(array4, 0, array2, 0, array2.length);
                System.arraycopy(array4, array2.length, array3, 0, array3.length);
            }
            array5 = new byte[array2.length];
            for (int i = 0; i != array2.length; ++i) {
                array5[i] = (byte)(array[n + this.V.length + i] ^ array2[i]);
            }
        }
        else {
            final byte[] array6 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
            array3 = new byte[this.param.getMacKeySize() / 8];
            final byte[] array7 = new byte[array6.length + array3.length];
            this.kdf.generateBytes(array7, 0, array7.length);
            System.arraycopy(array7, 0, array6, 0, array6.length);
            System.arraycopy(array7, array6.length, array3, 0, array3.length);
            CipherParameters cipherParameters = new KeyParameter(array6);
            if (this.IV != null) {
                cipherParameters = new ParametersWithIV(cipherParameters, this.IV);
            }
            this.cipher.init(false, cipherParameters);
            array5 = new byte[this.cipher.getOutputSize(n2 - this.V.length - this.mac.getMacSize())];
            processBytes = this.cipher.processBytes(array, n + this.V.length, n2 - this.V.length - this.mac.getMacSize(), array5, 0);
        }
        final byte[] encodingV = this.param.getEncodingV();
        byte[] lengthTag = null;
        if (this.V.length != 0) {
            lengthTag = this.getLengthTag(encodingV);
        }
        final int n3 = n + n2;
        final byte[] copyOfRange = Arrays.copyOfRange(array, n3 - this.mac.getMacSize(), n3);
        final byte[] array8 = new byte[copyOfRange.length];
        this.mac.init(new KeyParameter(array3));
        this.mac.update(array, n + this.V.length, n2 - this.V.length - array8.length);
        if (encodingV != null) {
            this.mac.update(encodingV, 0, encodingV.length);
        }
        if (this.V.length != 0) {
            this.mac.update(lengthTag, 0, lengthTag.length);
        }
        this.mac.doFinal(array8, 0);
        if (!Arrays.constantTimeAreEqual(copyOfRange, array8)) {
            throw new InvalidCipherTextException("invalid MAC");
        }
        if (this.cipher == null) {
            return array5;
        }
        return Arrays.copyOfRange(array5, 0, processBytes + this.cipher.doFinal(array5, processBytes));
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            if (this.keyPairGenerator != null) {
                final EphemeralKeyPair generate = this.keyPairGenerator.generate();
                this.privParam = generate.getKeyPair().getPrivate();
                this.V = generate.getEncodedPublicKey();
            }
        }
        else if (this.keyParser != null) {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array, n, n2);
            try {
                this.pubParam = this.keyParser.readKey(byteArrayInputStream);
            }
            catch (final IOException ex) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + ex.getMessage(), ex);
            }
            catch (final IllegalArgumentException ex2) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + ex2.getMessage(), ex2);
            }
            this.V = Arrays.copyOfRange(array, n, n + (n2 - byteArrayInputStream.available()));
        }
        this.agree.init(this.privParam);
        byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(this.agree.getFieldSize(), this.agree.calculateAgreement(this.pubParam));
        if (this.V.length != 0) {
            final byte[] concatenate = Arrays.concatenate(this.V, unsignedByteArray);
            Arrays.fill(unsignedByteArray, (byte)0);
            unsignedByteArray = concatenate;
        }
        try {
            this.kdf.init(new KDFParameters(unsignedByteArray, this.param.getDerivationV()));
            return this.forEncryption ? this.encryptBlock(array, n, n2) : this.decryptBlock(array, n, n2);
        }
        finally {
            Arrays.fill(unsignedByteArray, (byte)0);
        }
    }
    
    protected byte[] getLengthTag(final byte[] array) {
        final byte[] array2 = new byte[8];
        if (array != null) {
            Pack.longToBigEndian(array.length * 8L, array2, 0);
        }
        return array2;
    }
}
