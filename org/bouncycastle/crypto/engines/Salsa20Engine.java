package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.SkippingStreamCipher;

public class Salsa20Engine implements SkippingStreamCipher
{
    public static final int DEFAULT_ROUNDS = 20;
    private static final int STATE_SIZE = 16;
    private static final int[] TAU_SIGMA;
    @Deprecated
    protected static final byte[] sigma;
    @Deprecated
    protected static final byte[] tau;
    protected int rounds;
    private int index;
    protected int[] engineState;
    protected int[] x;
    private byte[] keyStream;
    private boolean initialised;
    private int cW0;
    private int cW1;
    private int cW2;
    
    protected void packTauOrSigma(final int n, final int[] array, final int n2) {
        final int n3 = (n - 16) / 4;
        array[n2] = Salsa20Engine.TAU_SIGMA[n3];
        array[n2 + 1] = Salsa20Engine.TAU_SIGMA[n3 + 1];
        array[n2 + 2] = Salsa20Engine.TAU_SIGMA[n3 + 2];
        array[n2 + 3] = Salsa20Engine.TAU_SIGMA[n3 + 3];
    }
    
    public Salsa20Engine() {
        this(20);
    }
    
    public Salsa20Engine(final int rounds) {
        this.index = 0;
        this.engineState = new int[16];
        this.x = new int[16];
        this.keyStream = new byte[64];
        this.initialised = false;
        if (rounds <= 0 || (rounds & 0x1) != 0x0) {
            throw new IllegalArgumentException("'rounds' must be a positive, even number");
        }
        this.rounds = rounds;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must include an IV");
        }
        final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        final byte[] iv = parametersWithIV.getIV();
        if (iv == null || iv.length != this.getNonceSize()) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires exactly " + this.getNonceSize() + " bytes of IV");
        }
        final CipherParameters parameters = parametersWithIV.getParameters();
        if (parameters == null) {
            if (!this.initialised) {
                throw new IllegalStateException(this.getAlgorithmName() + " KeyParameter can not be null for first initialisation");
            }
            this.setKey(null, iv);
        }
        else {
            if (!(parameters instanceof KeyParameter)) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
            }
            this.setKey(((KeyParameter)parameters).getKey(), iv);
        }
        this.reset();
        this.initialised = true;
    }
    
    protected int getNonceSize() {
        return 8;
    }
    
    public String getAlgorithmName() {
        String string = "Salsa20";
        if (this.rounds != 20) {
            string = string + "/" + this.rounds;
        }
        return string;
    }
    
    public byte returnByte(final byte b) {
        if (this.limitExceeded()) {
            throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
        }
        final byte b2 = (byte)(this.keyStream[this.index] ^ b);
        this.index = (this.index + 1 & 0x3F);
        if (this.index == 0) {
            this.advanceCounter();
            this.generateKeyStream(this.keyStream);
        }
        return b2;
    }
    
    protected void advanceCounter(final long n) {
        final int n2 = (int)(n >>> 32);
        final int n3 = (int)n;
        if (n2 > 0) {
            final int[] engineState = this.engineState;
            final int n4 = 9;
            engineState[n4] += n2;
        }
        final int n5 = this.engineState[8];
        final int[] engineState2 = this.engineState;
        final int n6 = 8;
        engineState2[n6] += n3;
        if (n5 != 0 && this.engineState[8] < n5) {
            final int[] engineState3 = this.engineState;
            final int n7 = 9;
            ++engineState3[n7];
        }
    }
    
    protected void advanceCounter() {
        if (++this.engineState[8] == 0) {
            final int[] engineState = this.engineState;
            final int n = 9;
            ++engineState[n];
        }
    }
    
    protected void retreatCounter(final long n) {
        final int n2 = (int)(n >>> 32);
        final int n3 = (int)n;
        if (n2 != 0) {
            if (((long)this.engineState[9] & 0xFFFFFFFFL) < ((long)n2 & 0xFFFFFFFFL)) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            final int[] engineState = this.engineState;
            final int n4 = 9;
            engineState[n4] -= n2;
        }
        if (((long)this.engineState[8] & 0xFFFFFFFFL) >= ((long)n3 & 0xFFFFFFFFL)) {
            final int[] engineState2 = this.engineState;
            final int n5 = 8;
            engineState2[n5] -= n3;
        }
        else {
            if (this.engineState[9] == 0) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            final int[] engineState3 = this.engineState;
            final int n6 = 9;
            --engineState3[n6];
            final int[] engineState4 = this.engineState;
            final int n7 = 8;
            engineState4[n7] -= n3;
        }
    }
    
    protected void retreatCounter() {
        if (this.engineState[8] == 0 && this.engineState[9] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        final int[] engineState = this.engineState;
        final int n = 8;
        if (--engineState[n] == -1) {
            final int[] engineState2 = this.engineState;
            final int n2 = 9;
            --engineState2[n2];
        }
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.limitExceeded(n2)) {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
        for (int i = 0; i < n2; ++i) {
            array2[i + n3] = (byte)(this.keyStream[this.index] ^ array[i + n]);
            this.index = (this.index + 1 & 0x3F);
            if (this.index == 0) {
                this.advanceCounter();
                this.generateKeyStream(this.keyStream);
            }
        }
        return n2;
    }
    
    public long skip(final long n) {
        if (n >= 0L) {
            long n2 = n;
            if (n2 >= 64L) {
                final long n3 = n2 / 64L;
                this.advanceCounter(n3);
                n2 -= n3 * 64L;
            }
            final int index = this.index;
            this.index = (this.index + (int)n2 & 0x3F);
            if (this.index < index) {
                this.advanceCounter();
            }
        }
        else {
            long n4 = -n;
            if (n4 >= 64L) {
                final long n5 = n4 / 64L;
                this.retreatCounter(n5);
                n4 -= n5 * 64L;
            }
            for (long n6 = 0L; n6 < n4; ++n6) {
                if (this.index == 0) {
                    this.retreatCounter();
                }
                this.index = (this.index - 1 & 0x3F);
            }
        }
        this.generateKeyStream(this.keyStream);
        return n;
    }
    
    public long seekTo(final long n) {
        this.reset();
        return this.skip(n);
    }
    
    public long getPosition() {
        return this.getCounter() * 64L + this.index;
    }
    
    public void reset() {
        this.index = 0;
        this.resetLimitCounter();
        this.resetCounter();
        this.generateKeyStream(this.keyStream);
    }
    
    protected long getCounter() {
        return (long)this.engineState[9] << 32 | ((long)this.engineState[8] & 0xFFFFFFFFL);
    }
    
    protected void resetCounter() {
        this.engineState[8] = (this.engineState[9] = 0);
    }
    
    protected void setKey(final byte[] array, final byte[] array2) {
        if (array != null) {
            if (array.length != 16 && array.length != 32) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            final int n = (array.length - 16) / 4;
            this.engineState[0] = Salsa20Engine.TAU_SIGMA[n];
            this.engineState[5] = Salsa20Engine.TAU_SIGMA[n + 1];
            this.engineState[10] = Salsa20Engine.TAU_SIGMA[n + 2];
            this.engineState[15] = Salsa20Engine.TAU_SIGMA[n + 3];
            Pack.littleEndianToInt(array, 0, this.engineState, 1, 4);
            Pack.littleEndianToInt(array, array.length - 16, this.engineState, 11, 4);
        }
        Pack.littleEndianToInt(array2, 0, this.engineState, 6, 2);
    }
    
    protected void generateKeyStream(final byte[] array) {
        salsaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, array, 0);
    }
    
    public static void salsaCore(final int n, final int[] array, final int[] array2) {
        if (array.length != 16) {
            throw new IllegalArgumentException();
        }
        if (array2.length != 16) {
            throw new IllegalArgumentException();
        }
        if (n % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int n2 = array[0];
        int n3 = array[1];
        int n4 = array[2];
        int n5 = array[3];
        int n6 = array[4];
        int n7 = array[5];
        int n8 = array[6];
        int n9 = array[7];
        int n10 = array[8];
        int n11 = array[9];
        int n12 = array[10];
        int n13 = array[11];
        int n14 = array[12];
        int n15 = array[13];
        int n16 = array[14];
        int n17 = array[15];
        for (int i = n; i > 0; i -= 2) {
            final int n18 = n6 ^ rotl(n2 + n14, 7);
            final int n19 = n10 ^ rotl(n18 + n2, 9);
            final int n20 = n14 ^ rotl(n19 + n18, 13);
            final int n21 = n2 ^ rotl(n20 + n19, 18);
            final int n22 = n11 ^ rotl(n7 + n3, 7);
            final int n23 = n15 ^ rotl(n22 + n7, 9);
            final int n24 = n3 ^ rotl(n23 + n22, 13);
            final int n25 = n7 ^ rotl(n24 + n23, 18);
            final int n26 = n16 ^ rotl(n12 + n8, 7);
            final int n27 = n4 ^ rotl(n26 + n12, 9);
            final int n28 = n8 ^ rotl(n27 + n26, 13);
            final int n29 = n12 ^ rotl(n28 + n27, 18);
            final int n30 = n5 ^ rotl(n17 + n13, 7);
            final int n31 = n9 ^ rotl(n30 + n17, 9);
            final int n32 = n13 ^ rotl(n31 + n30, 13);
            final int n33 = n17 ^ rotl(n32 + n31, 18);
            n3 = (n24 ^ rotl(n21 + n30, 7));
            n4 = (n27 ^ rotl(n3 + n21, 9));
            n5 = (n30 ^ rotl(n4 + n3, 13));
            n2 = (n21 ^ rotl(n5 + n4, 18));
            n8 = (n28 ^ rotl(n25 + n18, 7));
            n9 = (n31 ^ rotl(n8 + n25, 9));
            n6 = (n18 ^ rotl(n9 + n8, 13));
            n7 = (n25 ^ rotl(n6 + n9, 18));
            n13 = (n32 ^ rotl(n29 + n22, 7));
            n10 = (n19 ^ rotl(n13 + n29, 9));
            n11 = (n22 ^ rotl(n10 + n13, 13));
            n12 = (n29 ^ rotl(n11 + n10, 18));
            n14 = (n20 ^ rotl(n33 + n26, 7));
            n15 = (n23 ^ rotl(n14 + n33, 9));
            n16 = (n26 ^ rotl(n15 + n14, 13));
            n17 = (n33 ^ rotl(n16 + n15, 18));
        }
        array2[0] = n2 + array[0];
        array2[1] = n3 + array[1];
        array2[2] = n4 + array[2];
        array2[3] = n5 + array[3];
        array2[4] = n6 + array[4];
        array2[5] = n7 + array[5];
        array2[6] = n8 + array[6];
        array2[7] = n9 + array[7];
        array2[8] = n10 + array[8];
        array2[9] = n11 + array[9];
        array2[10] = n12 + array[10];
        array2[11] = n13 + array[11];
        array2[12] = n14 + array[12];
        array2[13] = n15 + array[13];
        array2[14] = n16 + array[14];
        array2[15] = n17 + array[15];
    }
    
    protected static int rotl(final int n, final int n2) {
        return n << n2 | n >>> -n2;
    }
    
    private void resetLimitCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }
    
    private boolean limitExceeded() {
        return ++this.cW0 == 0 && ++this.cW1 == 0 && (++this.cW2 & 0x20) != 0x0;
    }
    
    private boolean limitExceeded(final int n) {
        this.cW0 += n;
        return this.cW0 < n && this.cW0 >= 0 && ++this.cW1 == 0 && (++this.cW2 & 0x20) != 0x0;
    }
    
    static {
        TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
        sigma = Strings.toByteArray("expand 32-byte k");
        tau = Strings.toByteArray("expand 16-byte k");
    }
}
