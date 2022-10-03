package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.ExtendedDigest;

public class KeccakDigest implements ExtendedDigest
{
    private static long[] KeccakRoundConstants;
    protected long[] state;
    protected byte[] dataQueue;
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;
    
    public KeccakDigest() {
        this(288);
    }
    
    public KeccakDigest(final int n) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        this.init(n);
    }
    
    public KeccakDigest(final KeccakDigest keccakDigest) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        System.arraycopy(keccakDigest.state, 0, this.state, 0, keccakDigest.state.length);
        System.arraycopy(keccakDigest.dataQueue, 0, this.dataQueue, 0, keccakDigest.dataQueue.length);
        this.rate = keccakDigest.rate;
        this.bitsInQueue = keccakDigest.bitsInQueue;
        this.fixedOutputLength = keccakDigest.fixedOutputLength;
        this.squeezing = keccakDigest.squeezing;
    }
    
    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }
    
    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }
    
    public void update(final byte b) {
        this.absorb(new byte[] { b }, 0, 1);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.absorb(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.squeeze(array, n, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }
    
    protected int doFinal(final byte[] array, final int n, final byte b, final int n2) {
        if (n2 > 0) {
            this.absorbBits(b, n2);
        }
        this.squeeze(array, n, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }
    
    public void reset() {
        this.init(this.fixedOutputLength);
    }
    
    public int getByteLength() {
        return this.rate / 8;
    }
    
    private void init(final int n) {
        switch (n) {
            case 128:
            case 224:
            case 256:
            case 288:
            case 384:
            case 512: {
                this.initSponge(1600 - (n << 1));
                return;
            }
            default: {
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
            }
        }
    }
    
    private void initSponge(final int rate) {
        if (rate <= 0 || rate >= 1600 || rate % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = rate;
        for (int i = 0; i < this.state.length; ++i) {
            this.state[i] = 0L;
        }
        Arrays.fill(this.dataQueue, (byte)0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.fixedOutputLength = (1600 - rate) / 2;
    }
    
    protected void absorb(final byte[] array, final int n, final int n2) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int n3 = this.bitsInQueue >> 3;
        final int n4 = this.rate >> 3;
        int i = 0;
        while (i < n2) {
            if (n3 == 0 && i <= n2 - n4) {
                do {
                    this.KeccakAbsorb(array, n + i);
                    i += n4;
                } while (i <= n2 - n4);
            }
            else {
                final int min = Math.min(n4 - n3, n2 - i);
                System.arraycopy(array, n + i, this.dataQueue, n3, min);
                n3 += min;
                i += min;
                if (n3 != n4) {
                    continue;
                }
                this.KeccakAbsorb(this.dataQueue, 0);
                n3 = 0;
            }
        }
        this.bitsInQueue = n3 << 3;
    }
    
    protected void absorbBits(final int n, final int n2) {
        if (n2 < 1 || n2 > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        this.dataQueue[this.bitsInQueue >> 3] = (byte)(n & (1 << n2) - 1);
        this.bitsInQueue += n2;
    }
    
    private void padAndSwitchToSqueezingPhase() {
        final byte[] dataQueue = this.dataQueue;
        final int n = this.bitsInQueue >> 3;
        dataQueue[n] |= (byte)(1L << (this.bitsInQueue & 0x7));
        if (++this.bitsInQueue == this.rate) {
            this.KeccakAbsorb(this.dataQueue, 0);
            this.bitsInQueue = 0;
        }
        final int n2 = this.bitsInQueue >> 6;
        final int n3 = this.bitsInQueue & 0x3F;
        int n4 = 0;
        for (int i = 0; i < n2; ++i) {
            final long[] state = this.state;
            final int n5 = i;
            state[n5] ^= Pack.littleEndianToLong(this.dataQueue, n4);
            n4 += 8;
        }
        if (n3 > 0) {
            final long n6 = (1L << n3) - 1L;
            final long[] state2 = this.state;
            final int n7 = n2;
            state2[n7] ^= (Pack.littleEndianToLong(this.dataQueue, n4) & n6);
        }
        final long[] state3 = this.state;
        final int n8 = this.rate - 1 >> 6;
        state3[n8] ^= Long.MIN_VALUE;
        this.KeccakPermutation();
        this.KeccakExtract();
        this.bitsInQueue = this.rate;
        this.squeezing = true;
    }
    
    protected void squeeze(final byte[] array, final int n, final long n2) {
        if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
        }
        if (n2 % 8L != 0L) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }
        int n4;
        for (long n3 = 0L; n3 < n2; n3 += n4) {
            if (this.bitsInQueue == 0) {
                this.KeccakPermutation();
                this.KeccakExtract();
                this.bitsInQueue = this.rate;
            }
            n4 = (int)Math.min(this.bitsInQueue, n2 - n3);
            System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, array, n + (int)(n3 / 8L), n4 / 8);
            this.bitsInQueue -= n4;
        }
    }
    
    private void KeccakAbsorb(final byte[] array, int n) {
        for (int n2 = this.rate >> 6, i = 0; i < n2; ++i) {
            final long[] state = this.state;
            final int n3 = i;
            state[n3] ^= Pack.littleEndianToLong(array, n);
            n += 8;
        }
        this.KeccakPermutation();
    }
    
    private void KeccakExtract() {
        Pack.longToLittleEndian(this.state, 0, this.rate >> 6, this.dataQueue, 0);
    }
    
    private void KeccakPermutation() {
        final long[] state = this.state;
        long n = state[0];
        long n2 = state[1];
        long n3 = state[2];
        long n4 = state[3];
        long n5 = state[4];
        long n6 = state[5];
        long n7 = state[6];
        long n8 = state[7];
        long n9 = state[8];
        long n10 = state[9];
        long n11 = state[10];
        long n12 = state[11];
        long n13 = state[12];
        long n14 = state[13];
        long n15 = state[14];
        long n16 = state[15];
        long n17 = state[16];
        long n18 = state[17];
        long n19 = state[18];
        long n20 = state[19];
        long n21 = state[20];
        long n22 = state[21];
        long n23 = state[22];
        long n24 = state[23];
        long n25 = state[24];
        for (int i = 0; i < 24; ++i) {
            final long n26 = n ^ n6 ^ n11 ^ n16 ^ n21;
            final long n27 = n2 ^ n7 ^ n12 ^ n17 ^ n22;
            final long n28 = n3 ^ n8 ^ n13 ^ n18 ^ n23;
            final long n29 = n4 ^ n9 ^ n14 ^ n19 ^ n24;
            final long n30 = n5 ^ n10 ^ n15 ^ n20 ^ n25;
            final long n31 = (n27 << 1 | n27 >>> -1) ^ n30;
            final long n32 = (n28 << 1 | n28 >>> -1) ^ n26;
            final long n33 = (n29 << 1 | n29 >>> -1) ^ n27;
            final long n34 = (n30 << 1 | n30 >>> -1) ^ n28;
            final long n35 = (n26 << 1 | n26 >>> -1) ^ n29;
            final long n36 = n ^ n31;
            final long n37 = n6 ^ n31;
            final long n38 = n11 ^ n31;
            final long n39 = n16 ^ n31;
            final long n40 = n21 ^ n31;
            final long n41 = n2 ^ n32;
            final long n42 = n7 ^ n32;
            final long n43 = n12 ^ n32;
            final long n44 = n17 ^ n32;
            final long n45 = n22 ^ n32;
            final long n46 = n3 ^ n33;
            final long n47 = n8 ^ n33;
            final long n48 = n13 ^ n33;
            final long n49 = n18 ^ n33;
            final long n50 = n23 ^ n33;
            final long n51 = n4 ^ n34;
            final long n52 = n9 ^ n34;
            final long n53 = n14 ^ n34;
            final long n54 = n19 ^ n34;
            final long n55 = n24 ^ n34;
            final long n56 = n5 ^ n35;
            final long n57 = n10 ^ n35;
            final long n58 = n15 ^ n35;
            final long n59 = n20 ^ n35;
            final long n60 = n25 ^ n35;
            final long n61 = n41 << 1 | n41 >>> 63;
            final long n62 = n42 << 44 | n42 >>> 20;
            final long n63 = n57 << 20 | n57 >>> 44;
            final long n64 = n50 << 61 | n50 >>> 3;
            final long n65 = n58 << 39 | n58 >>> 25;
            final long n66 = n40 << 18 | n40 >>> 46;
            final long n67 = n46 << 62 | n46 >>> 2;
            final long n68 = n48 << 43 | n48 >>> 21;
            final long n69 = n53 << 25 | n53 >>> 39;
            final long n70 = n59 << 8 | n59 >>> 56;
            final long n71 = n55 << 56 | n55 >>> 8;
            final long n72 = n39 << 41 | n39 >>> 23;
            final long n73 = n56 << 27 | n56 >>> 37;
            final long n74 = n60 << 14 | n60 >>> 50;
            final long n75 = n45 << 2 | n45 >>> 62;
            final long n76 = n52 << 55 | n52 >>> 9;
            final long n77 = n44 << 45 | n44 >>> 19;
            final long n78 = n37 << 36 | n37 >>> 28;
            final long n79 = n51 << 28 | n51 >>> 36;
            final long n80 = n54 << 21 | n54 >>> 43;
            final long n81 = n49 << 15 | n49 >>> 49;
            final long n82 = n43 << 10 | n43 >>> 54;
            final long n83 = n47 << 6 | n47 >>> 58;
            final long n84 = n38 << 3 | n38 >>> 61;
            final long n85 = n61;
            final long n86 = n36 ^ (~n62 & n68);
            final long n87 = n62 ^ (~n68 & n80);
            n3 = (n68 ^ (~n80 & n74));
            n4 = (n80 ^ (~n74 & n36));
            n5 = (n74 ^ (~n36 & n62));
            final long n88 = n86;
            n2 = n87;
            final long n89 = n79 ^ (~n63 & n84);
            final long n90 = n63 ^ (~n84 & n77);
            n8 = (n84 ^ (~n77 & n64));
            n9 = (n77 ^ (~n64 & n79));
            n10 = (n64 ^ (~n79 & n63));
            n6 = n89;
            n7 = n90;
            final long n91 = n85 ^ (~n83 & n69);
            final long n92 = n83 ^ (~n69 & n70);
            n13 = (n69 ^ (~n70 & n66));
            n14 = (n70 ^ (~n66 & n85));
            n15 = (n66 ^ (~n85 & n83));
            n11 = n91;
            n12 = n92;
            final long n93 = n73 ^ (~n78 & n82);
            final long n94 = n78 ^ (~n82 & n81);
            n18 = (n82 ^ (~n81 & n71));
            n19 = (n81 ^ (~n71 & n73));
            n20 = (n71 ^ (~n73 & n78));
            n16 = n93;
            n17 = n94;
            final long n95 = n67 ^ (~n76 & n65);
            final long n96 = n76 ^ (~n65 & n72);
            n23 = (n65 ^ (~n72 & n75));
            n24 = (n72 ^ (~n75 & n67));
            n25 = (n75 ^ (~n67 & n76));
            n21 = n95;
            n22 = n96;
            n = (n88 ^ KeccakDigest.KeccakRoundConstants[i]);
        }
        state[0] = n;
        state[1] = n2;
        state[2] = n3;
        state[3] = n4;
        state[4] = n5;
        state[5] = n6;
        state[6] = n7;
        state[7] = n8;
        state[8] = n9;
        state[9] = n10;
        state[10] = n11;
        state[11] = n12;
        state[12] = n13;
        state[13] = n14;
        state[14] = n15;
        state[15] = n16;
        state[16] = n17;
        state[17] = n18;
        state[18] = n19;
        state[19] = n20;
        state[20] = n21;
        state[21] = n22;
        state[22] = n23;
        state[23] = n24;
        state[24] = n25;
    }
    
    static {
        KeccakDigest.KeccakRoundConstants = new long[] { 1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L };
    }
}
