package org.bouncycastle.cert.selector;

import org.bouncycastle.util.Pack;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

class MSOutlookKeyIdCalculator
{
    static byte[] calculateKeyId(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final SHA1Digest sha1Digest = new SHA1Digest();
        final byte[] array = new byte[sha1Digest.getDigestSize()];
        final byte[] array2 = new byte[0];
        byte[] encoded;
        try {
            encoded = subjectPublicKeyInfo.getEncoded("DER");
        }
        catch (final IOException ex) {
            return new byte[0];
        }
        sha1Digest.update(encoded, 0, encoded.length);
        sha1Digest.doFinal(array, 0);
        return array;
    }
    
    private abstract static class GeneralDigest
    {
        private static final int BYTE_LENGTH = 64;
        private byte[] xBuf;
        private int xBufOff;
        private long byteCount;
        
        protected GeneralDigest() {
            this.xBuf = new byte[4];
            this.xBufOff = 0;
        }
        
        protected GeneralDigest(final GeneralDigest generalDigest) {
            this.xBuf = new byte[generalDigest.xBuf.length];
            this.copyIn(generalDigest);
        }
        
        protected void copyIn(final GeneralDigest generalDigest) {
            System.arraycopy(generalDigest.xBuf, 0, this.xBuf, 0, generalDigest.xBuf.length);
            this.xBufOff = generalDigest.xBufOff;
            this.byteCount = generalDigest.byteCount;
        }
        
        public void update(final byte b) {
            this.xBuf[this.xBufOff++] = b;
            if (this.xBufOff == this.xBuf.length) {
                this.processWord(this.xBuf, 0);
                this.xBufOff = 0;
            }
            ++this.byteCount;
        }
        
        public void update(final byte[] array, int n, int i) {
            while (this.xBufOff != 0 && i > 0) {
                this.update(array[n]);
                ++n;
                --i;
            }
            while (i > this.xBuf.length) {
                this.processWord(array, n);
                n += this.xBuf.length;
                i -= this.xBuf.length;
                this.byteCount += this.xBuf.length;
            }
            while (i > 0) {
                this.update(array[n]);
                ++n;
                --i;
            }
        }
        
        public void finish() {
            final long n = this.byteCount << 3;
            this.update((byte)(-128));
            while (this.xBufOff != 0) {
                this.update((byte)0);
            }
            this.processLength(n);
            this.processBlock();
        }
        
        public void reset() {
            this.byteCount = 0L;
            this.xBufOff = 0;
            for (int i = 0; i < this.xBuf.length; ++i) {
                this.xBuf[i] = 0;
            }
        }
        
        protected abstract void processWord(final byte[] p0, final int p1);
        
        protected abstract void processLength(final long p0);
        
        protected abstract void processBlock();
    }
    
    private static class SHA1Digest extends GeneralDigest
    {
        private static final int DIGEST_LENGTH = 20;
        private int H1;
        private int H2;
        private int H3;
        private int H4;
        private int H5;
        private int[] X;
        private int xOff;
        private static final int Y1 = 1518500249;
        private static final int Y2 = 1859775393;
        private static final int Y3 = -1894007588;
        private static final int Y4 = -899497514;
        
        public SHA1Digest() {
            this.X = new int[80];
            this.reset();
        }
        
        public String getAlgorithmName() {
            return "SHA-1";
        }
        
        public int getDigestSize() {
            return 20;
        }
        
        @Override
        protected void processWord(final byte[] array, int n) {
            this.X[this.xOff] = (array[n] << 24 | (array[++n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF));
            if (++this.xOff == 16) {
                this.processBlock();
            }
        }
        
        @Override
        protected void processLength(final long n) {
            if (this.xOff > 14) {
                this.processBlock();
            }
            this.X[14] = (int)(n >>> 32);
            this.X[15] = (int)(n & -1L);
        }
        
        public int doFinal(final byte[] array, final int n) {
            this.finish();
            Pack.intToBigEndian(this.H1, array, n);
            Pack.intToBigEndian(this.H2, array, n + 4);
            Pack.intToBigEndian(this.H3, array, n + 8);
            Pack.intToBigEndian(this.H4, array, n + 12);
            Pack.intToBigEndian(this.H5, array, n + 16);
            this.reset();
            return 20;
        }
        
        @Override
        public void reset() {
            super.reset();
            this.H1 = 1732584193;
            this.H2 = -271733879;
            this.H3 = -1732584194;
            this.H4 = 271733878;
            this.H5 = -1009589776;
            this.xOff = 0;
            for (int i = 0; i != this.X.length; ++i) {
                this.X[i] = 0;
            }
        }
        
        private int f(final int n, final int n2, final int n3) {
            return (n & n2) | (~n & n3);
        }
        
        private int h(final int n, final int n2, final int n3) {
            return n ^ n2 ^ n3;
        }
        
        private int g(final int n, final int n2, final int n3) {
            return (n & n2) | (n & n3) | (n2 & n3);
        }
        
        @Override
        protected void processBlock() {
            for (int i = 16; i < 80; ++i) {
                final int n = this.X[i - 3] ^ this.X[i - 8] ^ this.X[i - 14] ^ this.X[i - 16];
                this.X[i] = (n << 1 | n >>> 31);
            }
            int h1 = this.H1;
            int h2 = this.H2;
            int h3 = this.H3;
            int h4 = this.H4;
            int h5 = this.H5;
            int n2 = 0;
            for (int j = 0; j < 4; ++j) {
                final int n3 = h5 + ((h1 << 5 | h1 >>> 27) + this.f(h2, h3, h4) + this.X[n2++] + 1518500249);
                final int n4 = h2 << 30 | h2 >>> 2;
                final int n5 = h4 + ((n3 << 5 | n3 >>> 27) + this.f(h1, n4, h3) + this.X[n2++] + 1518500249);
                final int n6 = h1 << 30 | h1 >>> 2;
                final int n7 = h3 + ((n5 << 5 | n5 >>> 27) + this.f(n3, n6, n4) + this.X[n2++] + 1518500249);
                h5 = (n3 << 30 | n3 >>> 2);
                h2 = n4 + ((n7 << 5 | n7 >>> 27) + this.f(n5, h5, n6) + this.X[n2++] + 1518500249);
                h4 = (n5 << 30 | n5 >>> 2);
                h1 = n6 + ((h2 << 5 | h2 >>> 27) + this.f(n7, h4, h5) + this.X[n2++] + 1518500249);
                h3 = (n7 << 30 | n7 >>> 2);
            }
            for (int k = 0; k < 4; ++k) {
                final int n8 = h5 + ((h1 << 5 | h1 >>> 27) + this.h(h2, h3, h4) + this.X[n2++] + 1859775393);
                final int n9 = h2 << 30 | h2 >>> 2;
                final int n10 = h4 + ((n8 << 5 | n8 >>> 27) + this.h(h1, n9, h3) + this.X[n2++] + 1859775393);
                final int n11 = h1 << 30 | h1 >>> 2;
                final int n12 = h3 + ((n10 << 5 | n10 >>> 27) + this.h(n8, n11, n9) + this.X[n2++] + 1859775393);
                h5 = (n8 << 30 | n8 >>> 2);
                h2 = n9 + ((n12 << 5 | n12 >>> 27) + this.h(n10, h5, n11) + this.X[n2++] + 1859775393);
                h4 = (n10 << 30 | n10 >>> 2);
                h1 = n11 + ((h2 << 5 | h2 >>> 27) + this.h(n12, h4, h5) + this.X[n2++] + 1859775393);
                h3 = (n12 << 30 | n12 >>> 2);
            }
            for (int l = 0; l < 4; ++l) {
                final int n13 = h5 + ((h1 << 5 | h1 >>> 27) + this.g(h2, h3, h4) + this.X[n2++] - 1894007588);
                final int n14 = h2 << 30 | h2 >>> 2;
                final int n15 = h4 + ((n13 << 5 | n13 >>> 27) + this.g(h1, n14, h3) + this.X[n2++] - 1894007588);
                final int n16 = h1 << 30 | h1 >>> 2;
                final int n17 = h3 + ((n15 << 5 | n15 >>> 27) + this.g(n13, n16, n14) + this.X[n2++] - 1894007588);
                h5 = (n13 << 30 | n13 >>> 2);
                h2 = n14 + ((n17 << 5 | n17 >>> 27) + this.g(n15, h5, n16) + this.X[n2++] - 1894007588);
                h4 = (n15 << 30 | n15 >>> 2);
                h1 = n16 + ((h2 << 5 | h2 >>> 27) + this.g(n17, h4, h5) + this.X[n2++] - 1894007588);
                h3 = (n17 << 30 | n17 >>> 2);
            }
            for (int n18 = 0; n18 <= 3; ++n18) {
                final int n19 = h5 + ((h1 << 5 | h1 >>> 27) + this.h(h2, h3, h4) + this.X[n2++] - 899497514);
                final int n20 = h2 << 30 | h2 >>> 2;
                final int n21 = h4 + ((n19 << 5 | n19 >>> 27) + this.h(h1, n20, h3) + this.X[n2++] - 899497514);
                final int n22 = h1 << 30 | h1 >>> 2;
                final int n23 = h3 + ((n21 << 5 | n21 >>> 27) + this.h(n19, n22, n20) + this.X[n2++] - 899497514);
                h5 = (n19 << 30 | n19 >>> 2);
                h2 = n20 + ((n23 << 5 | n23 >>> 27) + this.h(n21, h5, n22) + this.X[n2++] - 899497514);
                h4 = (n21 << 30 | n21 >>> 2);
                h1 = n22 + ((h2 << 5 | h2 >>> 27) + this.h(n23, h4, h5) + this.X[n2++] - 899497514);
                h3 = (n23 << 30 | n23 >>> 2);
            }
            this.H1 += h1;
            this.H2 += h2;
            this.H3 += h3;
            this.H4 += h4;
            this.H5 += h5;
            this.xOff = 0;
            for (int n24 = 0; n24 < 16; ++n24) {
                this.X[n24] = 0;
            }
        }
    }
}
