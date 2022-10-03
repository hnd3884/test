package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class NaccacheSternEngine implements AsymmetricBlockCipher
{
    private boolean forEncryption;
    private NaccacheSternKeyParameters key;
    private Vector[] lookup;
    private boolean debug;
    private static BigInteger ZERO;
    private static BigInteger ONE;
    
    public NaccacheSternEngine() {
        this.lookup = null;
        this.debug = false;
    }
    
    public void init(final boolean forEncryption, CipherParameters parameters) {
        this.forEncryption = forEncryption;
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        this.key = (NaccacheSternKeyParameters)parameters;
        if (!this.forEncryption) {
            if (this.debug) {
                System.out.println("Constructing lookup Array");
            }
            final NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters)this.key;
            final Vector smallPrimes = naccacheSternPrivateKeyParameters.getSmallPrimes();
            this.lookup = new Vector[smallPrimes.size()];
            for (int i = 0; i < smallPrimes.size(); ++i) {
                final BigInteger bigInteger = smallPrimes.elementAt(i);
                final int intValue = bigInteger.intValue();
                (this.lookup[i] = new Vector()).addElement(NaccacheSternEngine.ONE);
                if (this.debug) {
                    System.out.println("Constructing lookup ArrayList for " + intValue);
                }
                BigInteger bigInteger2 = NaccacheSternEngine.ZERO;
                for (int j = 1; j < intValue; ++j) {
                    bigInteger2 = bigInteger2.add(naccacheSternPrivateKeyParameters.getPhi_n());
                    this.lookup[i].addElement(naccacheSternPrivateKeyParameters.getG().modPow(bigInteger2.divide(bigInteger), naccacheSternPrivateKeyParameters.getModulus()));
                }
            }
        }
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public int getInputBlockSize() {
        if (this.forEncryption) {
            return (this.key.getLowerSigmaBound() + 7) / 8 - 1;
        }
        return this.key.getModulus().toByteArray().length;
    }
    
    public int getOutputBlockSize() {
        if (this.forEncryption) {
            return this.key.getModulus().toByteArray().length;
        }
        return (this.key.getLowerSigmaBound() + 7) / 8 - 1;
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.key == null) {
            throw new IllegalStateException("NaccacheStern engine not initialised");
        }
        if (n2 > this.getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for Naccache-Stern cipher.\n");
        }
        if (!this.forEncryption && n2 < this.getInputBlockSize()) {
            throw new InvalidCipherTextException("BlockLength does not match modulus for Naccache-Stern cipher.\n");
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
        if (this.debug) {
            System.out.println("input as BigInteger: " + bigInteger);
        }
        byte[] array3;
        if (this.forEncryption) {
            array3 = this.encrypt(bigInteger);
        }
        else {
            final Vector<BigInteger> vector = new Vector<BigInteger>();
            final NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters)this.key;
            final Vector smallPrimes = naccacheSternPrivateKeyParameters.getSmallPrimes();
            for (int i = 0; i < smallPrimes.size(); ++i) {
                final BigInteger modPow = bigInteger.modPow(naccacheSternPrivateKeyParameters.getPhi_n().divide(smallPrimes.elementAt(i)), naccacheSternPrivateKeyParameters.getModulus());
                final Vector vector2 = this.lookup[i];
                if (this.lookup[i].size() != ((BigInteger)smallPrimes.elementAt(i)).intValue()) {
                    if (this.debug) {
                        System.out.println("Prime is " + smallPrimes.elementAt(i) + ", lookup table has size " + vector2.size());
                    }
                    throw new InvalidCipherTextException("Error in lookup Array for " + ((BigInteger)smallPrimes.elementAt(i)).intValue() + ": Size mismatch. Expected ArrayList with length " + ((BigInteger)smallPrimes.elementAt(i)).intValue() + " but found ArrayList of length " + this.lookup[i].size());
                }
                final int index = vector2.indexOf(modPow);
                if (index == -1) {
                    if (this.debug) {
                        System.out.println("Actual prime is " + smallPrimes.elementAt(i));
                        System.out.println("Decrypted value is " + modPow);
                        System.out.println("LookupList for " + smallPrimes.elementAt(i) + " with size " + this.lookup[i].size() + " is: ");
                        for (int j = 0; j < this.lookup[i].size(); ++j) {
                            System.out.println(this.lookup[i].elementAt(j));
                        }
                    }
                    throw new InvalidCipherTextException("Lookup failed");
                }
                vector.addElement(BigInteger.valueOf(index));
            }
            array3 = chineseRemainder(vector, smallPrimes).toByteArray();
        }
        return array3;
    }
    
    public byte[] encrypt(final BigInteger bigInteger) {
        final byte[] byteArray = this.key.getModulus().toByteArray();
        Arrays.fill(byteArray, (byte)0);
        final byte[] byteArray2 = this.key.getG().modPow(bigInteger, this.key.getModulus()).toByteArray();
        System.arraycopy(byteArray2, 0, byteArray, byteArray.length - byteArray2.length, byteArray2.length);
        if (this.debug) {
            System.out.println("Encrypted value is:  " + new BigInteger(byteArray));
        }
        return byteArray;
    }
    
    public byte[] addCryptedBlocks(final byte[] array, final byte[] array2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            if (array.length > this.getOutputBlockSize() || array2.length > this.getOutputBlockSize()) {
                throw new InvalidCipherTextException("BlockLength too large for simple addition.\n");
            }
        }
        else if (array.length > this.getInputBlockSize() || array2.length > this.getInputBlockSize()) {
            throw new InvalidCipherTextException("BlockLength too large for simple addition.\n");
        }
        final BigInteger bigInteger = new BigInteger(1, array);
        final BigInteger bigInteger2 = new BigInteger(1, array2);
        final BigInteger mod = bigInteger.multiply(bigInteger2).mod(this.key.getModulus());
        if (this.debug) {
            System.out.println("c(m1) as BigInteger:....... " + bigInteger);
            System.out.println("c(m2) as BigInteger:....... " + bigInteger2);
            System.out.println("c(m1)*c(m2)%n = c(m1+m2)%n: " + mod);
        }
        final byte[] byteArray = this.key.getModulus().toByteArray();
        Arrays.fill(byteArray, (byte)0);
        System.arraycopy(mod.toByteArray(), 0, byteArray, byteArray.length - mod.toByteArray().length, mod.toByteArray().length);
        return byteArray;
    }
    
    public byte[] processData(final byte[] array) throws InvalidCipherTextException {
        if (this.debug) {
            System.out.println();
        }
        if (array.length > this.getInputBlockSize()) {
            final int inputBlockSize = this.getInputBlockSize();
            final int outputBlockSize = this.getOutputBlockSize();
            if (this.debug) {
                System.out.println("Input blocksize is:  " + inputBlockSize + " bytes");
                System.out.println("Output blocksize is: " + outputBlockSize + " bytes");
                System.out.println("Data has length:.... " + array.length + " bytes");
            }
            int i = 0;
            int n = 0;
            final byte[] array2 = new byte[(array.length / inputBlockSize + 1) * outputBlockSize];
            while (i < array.length) {
                byte[] array3;
                if (i + inputBlockSize < array.length) {
                    array3 = this.processBlock(array, i, inputBlockSize);
                    i += inputBlockSize;
                }
                else {
                    array3 = this.processBlock(array, i, array.length - i);
                    i += array.length - i;
                }
                if (this.debug) {
                    System.out.println("new datapos is " + i);
                }
                if (array3 == null) {
                    if (this.debug) {
                        System.out.println("cipher returned null");
                    }
                    throw new InvalidCipherTextException("cipher returned null");
                }
                System.arraycopy(array3, 0, array2, n, array3.length);
                n += array3.length;
            }
            final byte[] array4 = new byte[n];
            System.arraycopy(array2, 0, array4, 0, n);
            if (this.debug) {
                System.out.println("returning " + array4.length + " bytes");
            }
            return array4;
        }
        if (this.debug) {
            System.out.println("data size is less then input block size, processing directly");
        }
        return this.processBlock(array, 0, array.length);
    }
    
    private static BigInteger chineseRemainder(final Vector vector, final Vector vector2) {
        BigInteger bigInteger = NaccacheSternEngine.ZERO;
        BigInteger bigInteger2 = NaccacheSternEngine.ONE;
        for (int i = 0; i < vector2.size(); ++i) {
            bigInteger2 = bigInteger2.multiply((BigInteger)vector2.elementAt(i));
        }
        for (int j = 0; j < vector2.size(); ++j) {
            final BigInteger bigInteger3 = vector2.elementAt(j);
            final BigInteger divide = bigInteger2.divide(bigInteger3);
            bigInteger = bigInteger.add(divide.multiply(divide.modInverse(bigInteger3)).multiply(vector.elementAt(j)));
        }
        return bigInteger.mod(bigInteger2);
    }
    
    static {
        NaccacheSternEngine.ZERO = BigInteger.valueOf(0L);
        NaccacheSternEngine.ONE = BigInteger.valueOf(1L);
    }
}
