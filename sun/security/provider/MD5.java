package sun.security.provider;

import java.util.Arrays;

public final class MD5 extends DigestBase
{
    private int[] state;
    private int[] x;
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;
    
    public MD5() {
        super("MD5", 16, 64);
        this.state = new int[4];
        this.x = new int[16];
        this.resetHashes();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final MD5 md5 = (MD5)super.clone();
        md5.state = md5.state.clone();
        md5.x = new int[16];
        return md5;
    }
    
    @Override
    void implReset() {
        this.resetHashes();
        Arrays.fill(this.x, 0);
    }
    
    private void resetHashes() {
        this.state[0] = 1732584193;
        this.state[1] = -271733879;
        this.state[2] = -1732584194;
        this.state[3] = 271733878;
    }
    
    @Override
    void implDigest(final byte[] array, final int n) {
        final long n2 = this.bytesProcessed << 3;
        final int n3 = (int)this.bytesProcessed & 0x3F;
        this.engineUpdate(MD5.padding, 0, (n3 < 56) ? (56 - n3) : (120 - n3));
        ByteArrayAccess.i2bLittle4((int)n2, this.buffer, 56);
        ByteArrayAccess.i2bLittle4((int)(n2 >>> 32), this.buffer, 60);
        this.implCompress(this.buffer, 0);
        ByteArrayAccess.i2bLittle(this.state, 0, array, n, 16);
    }
    
    private static int FF(int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        n += ((n2 & n3) | (~n2 & n4)) + n5 + n7;
        return (n << n6 | n >>> 32 - n6) + n2;
    }
    
    private static int GG(int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        n += ((n2 & n4) | (n3 & ~n4)) + n5 + n7;
        return (n << n6 | n >>> 32 - n6) + n2;
    }
    
    private static int HH(int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        n += (n2 ^ n3 ^ n4) + n5 + n7;
        return (n << n6 | n >>> 32 - n6) + n2;
    }
    
    private static int II(int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        n += (n3 ^ (n2 | ~n4)) + n5 + n7;
        return (n << n6 | n >>> 32 - n6) + n2;
    }
    
    @Override
    void implCompress(final byte[] array, final int n) {
        ByteArrayAccess.b2iLittle64(array, n, this.x);
        final int n2 = this.state[0];
        final int n3 = this.state[1];
        final int n4 = this.state[2];
        final int n5 = this.state[3];
        final int ff = FF(n2, n3, n4, n5, this.x[0], 7, -680876936);
        final int ff2 = FF(n5, ff, n3, n4, this.x[1], 12, -389564586);
        final int ff3 = FF(n4, ff2, ff, n3, this.x[2], 17, 606105819);
        final int ff4 = FF(n3, ff3, ff2, ff, this.x[3], 22, -1044525330);
        final int ff5 = FF(ff, ff4, ff3, ff2, this.x[4], 7, -176418897);
        final int ff6 = FF(ff2, ff5, ff4, ff3, this.x[5], 12, 1200080426);
        final int ff7 = FF(ff3, ff6, ff5, ff4, this.x[6], 17, -1473231341);
        final int ff8 = FF(ff4, ff7, ff6, ff5, this.x[7], 22, -45705983);
        final int ff9 = FF(ff5, ff8, ff7, ff6, this.x[8], 7, 1770035416);
        final int ff10 = FF(ff6, ff9, ff8, ff7, this.x[9], 12, -1958414417);
        final int ff11 = FF(ff7, ff10, ff9, ff8, this.x[10], 17, -42063);
        final int ff12 = FF(ff8, ff11, ff10, ff9, this.x[11], 22, -1990404162);
        final int ff13 = FF(ff9, ff12, ff11, ff10, this.x[12], 7, 1804603682);
        final int ff14 = FF(ff10, ff13, ff12, ff11, this.x[13], 12, -40341101);
        final int ff15 = FF(ff11, ff14, ff13, ff12, this.x[14], 17, -1502002290);
        final int ff16 = FF(ff12, ff15, ff14, ff13, this.x[15], 22, 1236535329);
        final int gg = GG(ff13, ff16, ff15, ff14, this.x[1], 5, -165796510);
        final int gg2 = GG(ff14, gg, ff16, ff15, this.x[6], 9, -1069501632);
        final int gg3 = GG(ff15, gg2, gg, ff16, this.x[11], 14, 643717713);
        final int gg4 = GG(ff16, gg3, gg2, gg, this.x[0], 20, -373897302);
        final int gg5 = GG(gg, gg4, gg3, gg2, this.x[5], 5, -701558691);
        final int gg6 = GG(gg2, gg5, gg4, gg3, this.x[10], 9, 38016083);
        final int gg7 = GG(gg3, gg6, gg5, gg4, this.x[15], 14, -660478335);
        final int gg8 = GG(gg4, gg7, gg6, gg5, this.x[4], 20, -405537848);
        final int gg9 = GG(gg5, gg8, gg7, gg6, this.x[9], 5, 568446438);
        final int gg10 = GG(gg6, gg9, gg8, gg7, this.x[14], 9, -1019803690);
        final int gg11 = GG(gg7, gg10, gg9, gg8, this.x[3], 14, -187363961);
        final int gg12 = GG(gg8, gg11, gg10, gg9, this.x[8], 20, 1163531501);
        final int gg13 = GG(gg9, gg12, gg11, gg10, this.x[13], 5, -1444681467);
        final int gg14 = GG(gg10, gg13, gg12, gg11, this.x[2], 9, -51403784);
        final int gg15 = GG(gg11, gg14, gg13, gg12, this.x[7], 14, 1735328473);
        final int gg16 = GG(gg12, gg15, gg14, gg13, this.x[12], 20, -1926607734);
        final int hh = HH(gg13, gg16, gg15, gg14, this.x[5], 4, -378558);
        final int hh2 = HH(gg14, hh, gg16, gg15, this.x[8], 11, -2022574463);
        final int hh3 = HH(gg15, hh2, hh, gg16, this.x[11], 16, 1839030562);
        final int hh4 = HH(gg16, hh3, hh2, hh, this.x[14], 23, -35309556);
        final int hh5 = HH(hh, hh4, hh3, hh2, this.x[1], 4, -1530992060);
        final int hh6 = HH(hh2, hh5, hh4, hh3, this.x[4], 11, 1272893353);
        final int hh7 = HH(hh3, hh6, hh5, hh4, this.x[7], 16, -155497632);
        final int hh8 = HH(hh4, hh7, hh6, hh5, this.x[10], 23, -1094730640);
        final int hh9 = HH(hh5, hh8, hh7, hh6, this.x[13], 4, 681279174);
        final int hh10 = HH(hh6, hh9, hh8, hh7, this.x[0], 11, -358537222);
        final int hh11 = HH(hh7, hh10, hh9, hh8, this.x[3], 16, -722521979);
        final int hh12 = HH(hh8, hh11, hh10, hh9, this.x[6], 23, 76029189);
        final int hh13 = HH(hh9, hh12, hh11, hh10, this.x[9], 4, -640364487);
        final int hh14 = HH(hh10, hh13, hh12, hh11, this.x[12], 11, -421815835);
        final int hh15 = HH(hh11, hh14, hh13, hh12, this.x[15], 16, 530742520);
        final int hh16 = HH(hh12, hh15, hh14, hh13, this.x[2], 23, -995338651);
        final int ii = II(hh13, hh16, hh15, hh14, this.x[0], 6, -198630844);
        final int ii2 = II(hh14, ii, hh16, hh15, this.x[7], 10, 1126891415);
        final int ii3 = II(hh15, ii2, ii, hh16, this.x[14], 15, -1416354905);
        final int ii4 = II(hh16, ii3, ii2, ii, this.x[5], 21, -57434055);
        final int ii5 = II(ii, ii4, ii3, ii2, this.x[12], 6, 1700485571);
        final int ii6 = II(ii2, ii5, ii4, ii3, this.x[3], 10, -1894986606);
        final int ii7 = II(ii3, ii6, ii5, ii4, this.x[10], 15, -1051523);
        final int ii8 = II(ii4, ii7, ii6, ii5, this.x[1], 21, -2054922799);
        final int ii9 = II(ii5, ii8, ii7, ii6, this.x[8], 6, 1873313359);
        final int ii10 = II(ii6, ii9, ii8, ii7, this.x[15], 10, -30611744);
        final int ii11 = II(ii7, ii10, ii9, ii8, this.x[6], 15, -1560198380);
        final int ii12 = II(ii8, ii11, ii10, ii9, this.x[13], 21, 1309151649);
        final int ii13 = II(ii9, ii12, ii11, ii10, this.x[4], 6, -145523070);
        final int ii14 = II(ii10, ii13, ii12, ii11, this.x[11], 10, -1120210379);
        final int ii15 = II(ii11, ii14, ii13, ii12, this.x[2], 15, 718787259);
        final int ii16 = II(ii12, ii15, ii14, ii13, this.x[9], 21, -343485551);
        final int[] state = this.state;
        final int n6 = 0;
        state[n6] += ii13;
        final int[] state2 = this.state;
        final int n7 = 1;
        state2[n7] += ii16;
        final int[] state3 = this.state;
        final int n8 = 2;
        state3[n8] += ii15;
        final int[] state4 = this.state;
        final int n9 = 3;
        state4[n9] += ii14;
    }
}
