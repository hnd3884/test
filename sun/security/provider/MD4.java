package sun.security.provider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.MessageDigest;
import java.security.Provider;

public final class MD4 extends DigestBase
{
    private int[] state;
    private int[] x;
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;
    private static final Provider md4Provider;
    
    public static MessageDigest getInstance() {
        try {
            return MessageDigest.getInstance("MD4", MD4.md4Provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new ProviderException(ex);
        }
    }
    
    public MD4() {
        super("MD4", 16, 64);
        this.state = new int[4];
        this.x = new int[16];
        this.resetHashes();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final MD4 md4 = (MD4)super.clone();
        md4.state = md4.state.clone();
        md4.x = new int[16];
        return md4;
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
        this.engineUpdate(MD4.padding, 0, (n3 < 56) ? (56 - n3) : (120 - n3));
        ByteArrayAccess.i2bLittle4((int)n2, this.buffer, 56);
        ByteArrayAccess.i2bLittle4((int)(n2 >>> 32), this.buffer, 60);
        this.implCompress(this.buffer, 0);
        ByteArrayAccess.i2bLittle(this.state, 0, array, n, 16);
    }
    
    private static int FF(int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        n += ((n2 & n3) | (~n2 & n4)) + n5;
        return n << n6 | n >>> 32 - n6;
    }
    
    private static int GG(int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        n += ((n2 & n3) | (n2 & n4) | (n3 & n4)) + n5 + 1518500249;
        return n << n6 | n >>> 32 - n6;
    }
    
    private static int HH(int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        n += (n2 ^ n3 ^ n4) + n5 + 1859775393;
        return n << n6 | n >>> 32 - n6;
    }
    
    @Override
    void implCompress(final byte[] array, final int n) {
        ByteArrayAccess.b2iLittle64(array, n, this.x);
        final int n2 = this.state[0];
        final int n3 = this.state[1];
        final int n4 = this.state[2];
        final int n5 = this.state[3];
        final int ff = FF(n2, n3, n4, n5, this.x[0], 3);
        final int ff2 = FF(n5, ff, n3, n4, this.x[1], 7);
        final int ff3 = FF(n4, ff2, ff, n3, this.x[2], 11);
        final int ff4 = FF(n3, ff3, ff2, ff, this.x[3], 19);
        final int ff5 = FF(ff, ff4, ff3, ff2, this.x[4], 3);
        final int ff6 = FF(ff2, ff5, ff4, ff3, this.x[5], 7);
        final int ff7 = FF(ff3, ff6, ff5, ff4, this.x[6], 11);
        final int ff8 = FF(ff4, ff7, ff6, ff5, this.x[7], 19);
        final int ff9 = FF(ff5, ff8, ff7, ff6, this.x[8], 3);
        final int ff10 = FF(ff6, ff9, ff8, ff7, this.x[9], 7);
        final int ff11 = FF(ff7, ff10, ff9, ff8, this.x[10], 11);
        final int ff12 = FF(ff8, ff11, ff10, ff9, this.x[11], 19);
        final int ff13 = FF(ff9, ff12, ff11, ff10, this.x[12], 3);
        final int ff14 = FF(ff10, ff13, ff12, ff11, this.x[13], 7);
        final int ff15 = FF(ff11, ff14, ff13, ff12, this.x[14], 11);
        final int ff16 = FF(ff12, ff15, ff14, ff13, this.x[15], 19);
        final int gg = GG(ff13, ff16, ff15, ff14, this.x[0], 3);
        final int gg2 = GG(ff14, gg, ff16, ff15, this.x[4], 5);
        final int gg3 = GG(ff15, gg2, gg, ff16, this.x[8], 9);
        final int gg4 = GG(ff16, gg3, gg2, gg, this.x[12], 13);
        final int gg5 = GG(gg, gg4, gg3, gg2, this.x[1], 3);
        final int gg6 = GG(gg2, gg5, gg4, gg3, this.x[5], 5);
        final int gg7 = GG(gg3, gg6, gg5, gg4, this.x[9], 9);
        final int gg8 = GG(gg4, gg7, gg6, gg5, this.x[13], 13);
        final int gg9 = GG(gg5, gg8, gg7, gg6, this.x[2], 3);
        final int gg10 = GG(gg6, gg9, gg8, gg7, this.x[6], 5);
        final int gg11 = GG(gg7, gg10, gg9, gg8, this.x[10], 9);
        final int gg12 = GG(gg8, gg11, gg10, gg9, this.x[14], 13);
        final int gg13 = GG(gg9, gg12, gg11, gg10, this.x[3], 3);
        final int gg14 = GG(gg10, gg13, gg12, gg11, this.x[7], 5);
        final int gg15 = GG(gg11, gg14, gg13, gg12, this.x[11], 9);
        final int gg16 = GG(gg12, gg15, gg14, gg13, this.x[15], 13);
        final int hh = HH(gg13, gg16, gg15, gg14, this.x[0], 3);
        final int hh2 = HH(gg14, hh, gg16, gg15, this.x[8], 9);
        final int hh3 = HH(gg15, hh2, hh, gg16, this.x[4], 11);
        final int hh4 = HH(gg16, hh3, hh2, hh, this.x[12], 15);
        final int hh5 = HH(hh, hh4, hh3, hh2, this.x[2], 3);
        final int hh6 = HH(hh2, hh5, hh4, hh3, this.x[10], 9);
        final int hh7 = HH(hh3, hh6, hh5, hh4, this.x[6], 11);
        final int hh8 = HH(hh4, hh7, hh6, hh5, this.x[14], 15);
        final int hh9 = HH(hh5, hh8, hh7, hh6, this.x[1], 3);
        final int hh10 = HH(hh6, hh9, hh8, hh7, this.x[9], 9);
        final int hh11 = HH(hh7, hh10, hh9, hh8, this.x[5], 11);
        final int hh12 = HH(hh8, hh11, hh10, hh9, this.x[13], 15);
        final int hh13 = HH(hh9, hh12, hh11, hh10, this.x[3], 3);
        final int hh14 = HH(hh10, hh13, hh12, hh11, this.x[11], 9);
        final int hh15 = HH(hh11, hh14, hh13, hh12, this.x[7], 11);
        final int hh16 = HH(hh12, hh15, hh14, hh13, this.x[15], 15);
        final int[] state = this.state;
        final int n6 = 0;
        state[n6] += hh13;
        final int[] state2 = this.state;
        final int n7 = 1;
        state2[n7] += hh16;
        final int[] state3 = this.state;
        final int n8 = 2;
        state3[n8] += hh15;
        final int[] state4 = this.state;
        final int n9 = 3;
        state4[n9] += hh14;
    }
    
    static {
        md4Provider = new Provider("MD4Provider", 1.8, "MD4 MessageDigest") {
            private static final long serialVersionUID = -8850464997518327965L;
        };
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                MD4.md4Provider.put("MessageDigest.MD4", "sun.security.provider.MD4");
                return null;
            }
        });
    }
}
