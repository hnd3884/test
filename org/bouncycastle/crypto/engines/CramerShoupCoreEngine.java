package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.CramerShoupKeyParameters;
import java.math.BigInteger;

public class CramerShoupCoreEngine
{
    private static final BigInteger ONE;
    private CramerShoupKeyParameters key;
    private SecureRandom random;
    private boolean forEncryption;
    private String label;
    
    public CramerShoupCoreEngine() {
        this.label = null;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters, final String label) {
        this.init(b, cipherParameters);
        this.label = label;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        SecureRandom random = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.key = (CramerShoupKeyParameters)parametersWithRandom.getParameters();
            random = parametersWithRandom.getRandom();
        }
        else {
            this.key = (CramerShoupKeyParameters)cipherParameters;
        }
        this.random = this.initSecureRandom(forEncryption, random);
        this.forEncryption = forEncryption;
    }
    
    public int getInputBlockSize() {
        final int bitLength = this.key.getParameters().getP().bitLength();
        if (this.forEncryption) {
            return (bitLength + 7) / 8 - 1;
        }
        return (bitLength + 7) / 8;
    }
    
    public int getOutputBlockSize() {
        final int bitLength = this.key.getParameters().getP().bitLength();
        if (this.forEncryption) {
            return (bitLength + 7) / 8;
        }
        return (bitLength + 7) / 8 - 1;
    }
    
    public BigInteger convertInput(final byte[] array, final int n, final int n2) {
        if (n2 > this.getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for Cramer Shoup cipher.");
        }
        if (n2 == this.getInputBlockSize() + 1 && this.forEncryption) {
            throw new DataLengthException("input too large for Cramer Shoup cipher.");
        }
        byte[] array2;
        if (n != 0 || n2 != array.length) {
            array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
        }
        else {
            array2 = array;
        }
        final BigInteger bigInteger = new BigInteger(1, array2);
        if (bigInteger.compareTo(this.key.getParameters().getP()) >= 0) {
            throw new DataLengthException("input too large for Cramer Shoup cipher.");
        }
        return bigInteger;
    }
    
    public byte[] convertOutput(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (!this.forEncryption) {
            if (byteArray[0] == 0 && byteArray.length > this.getOutputBlockSize()) {
                final byte[] array = new byte[byteArray.length - 1];
                System.arraycopy(byteArray, 1, array, 0, array.length);
                return array;
            }
            if (byteArray.length < this.getOutputBlockSize()) {
                final byte[] array2 = new byte[this.getOutputBlockSize()];
                System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
                return array2;
            }
        }
        else if (byteArray[0] == 0) {
            final byte[] array3 = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array3, 0, array3.length);
            return array3;
        }
        return byteArray;
    }
    
    public CramerShoupCiphertext encryptBlock(final BigInteger bigInteger) {
        CramerShoupCiphertext cramerShoupCiphertext = null;
        if (!this.key.isPrivate() && this.forEncryption && this.key instanceof CramerShoupPublicKeyParameters) {
            final CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = (CramerShoupPublicKeyParameters)this.key;
            final BigInteger p = cramerShoupPublicKeyParameters.getParameters().getP();
            final BigInteger g1 = cramerShoupPublicKeyParameters.getParameters().getG1();
            final BigInteger g2 = cramerShoupPublicKeyParameters.getParameters().getG2();
            final BigInteger h = cramerShoupPublicKeyParameters.getH();
            if (!this.isValidMessage(bigInteger, p)) {
                return cramerShoupCiphertext;
            }
            final BigInteger generateRandomElement = this.generateRandomElement(p, this.random);
            final BigInteger modPow = g1.modPow(generateRandomElement, p);
            final BigInteger modPow2 = g2.modPow(generateRandomElement, p);
            final BigInteger mod = h.modPow(generateRandomElement, p).multiply(bigInteger).mod(p);
            final Digest h2 = cramerShoupPublicKeyParameters.getParameters().getH();
            final byte[] byteArray = modPow.toByteArray();
            h2.update(byteArray, 0, byteArray.length);
            final byte[] byteArray2 = modPow2.toByteArray();
            h2.update(byteArray2, 0, byteArray2.length);
            final byte[] byteArray3 = mod.toByteArray();
            h2.update(byteArray3, 0, byteArray3.length);
            if (this.label != null) {
                final byte[] bytes = this.label.getBytes();
                h2.update(bytes, 0, bytes.length);
            }
            final byte[] array = new byte[h2.getDigestSize()];
            h2.doFinal(array, 0);
            cramerShoupCiphertext = new CramerShoupCiphertext(modPow, modPow2, mod, cramerShoupPublicKeyParameters.getC().modPow(generateRandomElement, p).multiply(cramerShoupPublicKeyParameters.getD().modPow(generateRandomElement.multiply(new BigInteger(1, array)), p)).mod(p));
        }
        return cramerShoupCiphertext;
    }
    
    public BigInteger decryptBlock(final CramerShoupCiphertext cramerShoupCiphertext) throws CramerShoupCiphertextException {
        BigInteger mod = null;
        if (this.key.isPrivate() && !this.forEncryption && this.key instanceof CramerShoupPrivateKeyParameters) {
            final CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters = (CramerShoupPrivateKeyParameters)this.key;
            final BigInteger p = cramerShoupPrivateKeyParameters.getParameters().getP();
            final Digest h = cramerShoupPrivateKeyParameters.getParameters().getH();
            final byte[] byteArray = cramerShoupCiphertext.getU1().toByteArray();
            h.update(byteArray, 0, byteArray.length);
            final byte[] byteArray2 = cramerShoupCiphertext.getU2().toByteArray();
            h.update(byteArray2, 0, byteArray2.length);
            final byte[] byteArray3 = cramerShoupCiphertext.getE().toByteArray();
            h.update(byteArray3, 0, byteArray3.length);
            if (this.label != null) {
                final byte[] bytes = this.label.getBytes();
                h.update(bytes, 0, bytes.length);
            }
            final byte[] array = new byte[h.getDigestSize()];
            h.doFinal(array, 0);
            final BigInteger bigInteger = new BigInteger(1, array);
            if (!cramerShoupCiphertext.v.equals(cramerShoupCiphertext.u1.modPow(cramerShoupPrivateKeyParameters.getX1().add(cramerShoupPrivateKeyParameters.getY1().multiply(bigInteger)), p).multiply(cramerShoupCiphertext.u2.modPow(cramerShoupPrivateKeyParameters.getX2().add(cramerShoupPrivateKeyParameters.getY2().multiply(bigInteger)), p)).mod(p))) {
                throw new CramerShoupCiphertextException("Sorry, that ciphertext is not correct");
            }
            mod = cramerShoupCiphertext.e.multiply(cramerShoupCiphertext.u1.modPow(cramerShoupPrivateKeyParameters.getZ(), p).modInverse(p)).mod(p);
        }
        return mod;
    }
    
    private BigInteger generateRandomElement(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(CramerShoupCoreEngine.ONE, bigInteger.subtract(CramerShoupCoreEngine.ONE), secureRandom);
    }
    
    private boolean isValidMessage(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return bigInteger.compareTo(bigInteger2) < 0;
    }
    
    protected SecureRandom initSecureRandom(final boolean b, final SecureRandom secureRandom) {
        return b ? ((secureRandom != null) ? secureRandom : new SecureRandom()) : null;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
    
    public static class CramerShoupCiphertextException extends Exception
    {
        private static final long serialVersionUID = -6360977166495345076L;
        
        public CramerShoupCiphertextException(final String s) {
            super(s);
        }
    }
}
