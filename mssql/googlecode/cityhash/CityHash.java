package mssql.googlecode.cityhash;

public final class CityHash
{
    private static final long k0 = -4348849565147123417L;
    private static final long k1 = -5435081209227447693L;
    private static final long k2 = -7286425919675154353L;
    private static final long k3 = -3942382747735136937L;
    private static final long kMul = -7070675565921424023L;
    
    private static long toLongLE(final byte[] b, final int i) {
        return ((long)b[i + 7] << 56) + ((long)(b[i + 6] & 0xFF) << 48) + ((long)(b[i + 5] & 0xFF) << 40) + ((long)(b[i + 4] & 0xFF) << 32) + ((long)(b[i + 3] & 0xFF) << 24) + ((b[i + 2] & 0xFF) << 16) + ((b[i + 1] & 0xFF) << 8) + ((b[i + 0] & 0xFF) << 0);
    }
    
    private static int toIntLE(final byte[] b, final int i) {
        return ((b[i + 3] & 0xFF) << 24) + ((b[i + 2] & 0xFF) << 16) + ((b[i + 1] & 0xFF) << 8) + ((b[i + 0] & 0xFF) << 0);
    }
    
    private static long fetch64(final byte[] s, final int pos) {
        return toLongLE(s, pos);
    }
    
    private static int fetch32(final byte[] s, final int pos) {
        return toIntLE(s, pos);
    }
    
    private static long rotate(final long val, final int shift) {
        return (shift == 0) ? val : (val >>> shift | val << 64 - shift);
    }
    
    private static long rotateByAtLeast1(final long val, final int shift) {
        return val >>> shift | val << 64 - shift;
    }
    
    private static long shiftMix(final long val) {
        return val ^ val >>> 47;
    }
    
    private static long hash128to64(final long u, final long v) {
        long a = (u ^ v) * -7070675565921424023L;
        a ^= a >>> 47;
        long b = (v ^ a) * -7070675565921424023L;
        b ^= b >>> 47;
        b *= -7070675565921424023L;
        return b;
    }
    
    private static long hashLen16(final long u, final long v) {
        return hash128to64(u, v);
    }
    
    private static long hashLen0to16(final byte[] s, final int pos, final int len) {
        if (len > 8) {
            final long a = fetch64(s, pos + 0);
            final long b = fetch64(s, pos + len - 8);
            return hashLen16(a, rotateByAtLeast1(b + len, len)) ^ b;
        }
        if (len >= 4) {
            final long a = 0xFFFFFFFFL & (long)fetch32(s, pos + 0);
            return hashLen16((a << 3) + len, 0xFFFFFFFFL & (long)fetch32(s, pos + len - 4));
        }
        if (len > 0) {
            final int a2 = s[pos + 0] & 0xFF;
            final int b2 = s[pos + (len >>> 1)] & 0xFF;
            final int c = s[pos + len - 1] & 0xFF;
            final int y = a2 + (b2 << 8);
            final int z = len + (c << 2);
            return shiftMix(y * -7286425919675154353L ^ z * -3942382747735136937L) * -7286425919675154353L;
        }
        return -7286425919675154353L;
    }
    
    private static long hashLen17to32(final byte[] s, final int pos, final int len) {
        final long a = fetch64(s, pos + 0) * -5435081209227447693L;
        final long b = fetch64(s, pos + 8);
        final long c = fetch64(s, pos + len - 8) * -7286425919675154353L;
        final long d = fetch64(s, pos + len - 16) * -4348849565147123417L;
        return hashLen16(rotate(a - b, 43) + rotate(c, 30) + d, a + rotate(b ^ 0xC949D7C7509E6557L, 20) - c + len);
    }
    
    private static long[] weakHashLen32WithSeeds(final long w, final long x, final long y, final long z, long a, long b) {
        a += w;
        b = rotate(b + a + z, 21);
        final long c = a;
        a += x;
        a += y;
        b += rotate(a, 44);
        return new long[] { a + z, b + c };
    }
    
    private static long[] weakHashLen32WithSeeds(final byte[] s, final int pos, final long a, final long b) {
        return weakHashLen32WithSeeds(fetch64(s, pos + 0), fetch64(s, pos + 8), fetch64(s, pos + 16), fetch64(s, pos + 24), a, b);
    }
    
    private static long hashLen33to64(final byte[] s, final int pos, final int len) {
        long z = fetch64(s, pos + 24);
        long a = fetch64(s, pos + 0) + (fetch64(s, pos + len - 16) + len) * -4348849565147123417L;
        long b = rotate(a + z, 52);
        long c = rotate(a, 37);
        a += fetch64(s, pos + 8);
        c += rotate(a, 7);
        a += fetch64(s, pos + 16);
        final long vf = a + z;
        final long vs = b + rotate(a, 31) + c;
        a = fetch64(s, pos + 16) + fetch64(s, pos + len - 32);
        z = fetch64(s, pos + len - 8);
        b = rotate(a + z, 52);
        c = rotate(a, 37);
        a += fetch64(s, pos + len - 24);
        c += rotate(a, 7);
        a += fetch64(s, pos + len - 16);
        final long wf = a + z;
        final long ws = b + rotate(a, 31) + c;
        final long r = shiftMix((vf + ws) * -7286425919675154353L + (wf + vs) * -4348849565147123417L);
        return shiftMix(r * -4348849565147123417L + vs) * -7286425919675154353L;
    }
    
    static long cityHash64(final byte[] s, int pos, int len) {
        if (len <= 32) {
            if (len <= 16) {
                return hashLen0to16(s, pos, len);
            }
            return hashLen17to32(s, pos, len);
        }
        else {
            if (len <= 64) {
                return hashLen33to64(s, pos, len);
            }
            long x = fetch64(s, pos + len - 40);
            long y = fetch64(s, pos + len - 16) + fetch64(s, pos + len - 56);
            long z = hashLen16(fetch64(s, pos + len - 48) + len, fetch64(s, pos + len - 24));
            long[] v = weakHashLen32WithSeeds(s, pos + len - 64, len, z);
            long[] w = weakHashLen32WithSeeds(s, pos + len - 32, y - 5435081209227447693L, x);
            x = x * -5435081209227447693L + fetch64(s, pos + 0);
            len = (len - 1 & 0xFFFFFFC0);
            do {
                x = rotate(x + y + v[0] + fetch64(s, pos + 8), 37) * -5435081209227447693L;
                y = rotate(y + v[1] + fetch64(s, pos + 48), 42) * -5435081209227447693L;
                x ^= w[1];
                y += v[0] + fetch64(s, pos + 40);
                z = rotate(z + w[0], 33) * -5435081209227447693L;
                v = weakHashLen32WithSeeds(s, pos + 0, v[1] * -5435081209227447693L, x + w[0]);
                w = weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + fetch64(s, pos + 16));
                final long swap = z;
                z = x;
                x = swap;
                pos += 64;
                len -= 64;
            } while (len != 0);
            return hashLen16(hashLen16(v[0], w[0]) + shiftMix(y) * -5435081209227447693L + z, hashLen16(v[1], w[1]) + x);
        }
    }
    
    static long cityHash64WithSeed(final byte[] s, final int pos, final int len, final long seed) {
        return cityHash64WithSeeds(s, pos, len, -7286425919675154353L, seed);
    }
    
    static long cityHash64WithSeeds(final byte[] s, final int pos, final int len, final long seed0, final long seed1) {
        return hashLen16(cityHash64(s, pos, len) - seed0, seed1);
    }
    
    static long[] cityMurmur(final byte[] s, int pos, final int len, final long seed0, final long seed1) {
        long a = seed0;
        long b = seed1;
        long c = 0L;
        long d = 0L;
        int l = len - 16;
        if (l <= 0) {
            a = shiftMix(a * -5435081209227447693L) * -5435081209227447693L;
            c = b * -5435081209227447693L + hashLen0to16(s, pos, len);
            d = shiftMix(a + ((len >= 8) ? fetch64(s, pos + 0) : c));
        }
        else {
            c = hashLen16(fetch64(s, pos + len - 8) - 5435081209227447693L, a);
            d = hashLen16(b + len, c + fetch64(s, pos + len - 16));
            a += d;
            do {
                a ^= shiftMix(fetch64(s, pos + 0) * -5435081209227447693L) * -5435081209227447693L;
                a *= -5435081209227447693L;
                b ^= a;
                c ^= shiftMix(fetch64(s, pos + 8) * -5435081209227447693L) * -5435081209227447693L;
                c *= -5435081209227447693L;
                d ^= c;
                pos += 16;
                l -= 16;
            } while (l > 0);
        }
        a = hashLen16(a, c);
        b = hashLen16(d, b);
        return new long[] { a ^ b, hashLen16(b, a) };
    }
    
    static long[] cityHash128WithSeed(final byte[] s, int pos, int len, final long seed0, final long seed1) {
        if (len < 128) {
            return cityMurmur(s, pos, len, seed0, seed1);
        }
        long[] v = new long[2];
        long[] w = new long[2];
        long x = seed0;
        long y = seed1;
        long z = -5435081209227447693L * len;
        v[0] = rotate(y ^ 0xB492B66FBE98F273L, 49) * -5435081209227447693L + fetch64(s, pos);
        v[1] = rotate(v[0], 42) * -5435081209227447693L + fetch64(s, pos + 8);
        w[0] = rotate(y + z, 35) * -5435081209227447693L + x;
        w[1] = rotate(x + fetch64(s, pos + 88), 53) * -5435081209227447693L;
        do {
            x = rotate(x + y + v[0] + fetch64(s, pos + 8), 37) * -5435081209227447693L;
            y = rotate(y + v[1] + fetch64(s, pos + 48), 42) * -5435081209227447693L;
            x ^= w[1];
            y += v[0] + fetch64(s, pos + 40);
            z = rotate(z + w[0], 33) * -5435081209227447693L;
            v = weakHashLen32WithSeeds(s, pos + 0, v[1] * -5435081209227447693L, x + w[0]);
            w = weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + fetch64(s, pos + 16));
            long swap = z;
            z = x;
            x = swap;
            pos += 64;
            x = rotate(x + y + v[0] + fetch64(s, pos + 8), 37) * -5435081209227447693L;
            y = rotate(y + v[1] + fetch64(s, pos + 48), 42) * -5435081209227447693L;
            x ^= w[1];
            y += v[0] + fetch64(s, pos + 40);
            z = rotate(z + w[0], 33) * -5435081209227447693L;
            v = weakHashLen32WithSeeds(s, pos, v[1] * -5435081209227447693L, x + w[0]);
            w = weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + fetch64(s, pos + 16));
            swap = z;
            z = x;
            x = swap;
            pos += 64;
            len -= 128;
        } while (len >= 128);
        x += rotate(v[0] + z, 49) * -4348849565147123417L;
        z += rotate(w[0], 37) * -4348849565147123417L;
        long[] array;
        int n;
        long[] array2;
        int n2;
        for (int tail_done = 0; tail_done < len; tail_done += 32, y = rotate(x + y, 42) * -4348849565147123417L + v[1], array = w, n = 0, array[n] += fetch64(s, pos + len - tail_done + 16), x = x * -4348849565147123417L + w[0], z += w[1] + fetch64(s, pos + len - tail_done), array2 = w, n2 = 1, array2[n2] += v[0], v = weakHashLen32WithSeeds(s, pos + len - tail_done, v[0] + z, v[1])) {}
        x = hashLen16(x, v[0]);
        y = hashLen16(y + z, w[0]);
        return new long[] { hashLen16(x + v[1], w[1]) + y, hashLen16(x + w[1], y + v[1]) };
    }
    
    public static long[] cityHash128(final byte[] s, final int pos, final int len) {
        if (len >= 16) {
            return cityHash128WithSeed(s, pos + 16, len - 16, fetch64(s, pos + 0) ^ 0xC949D7C7509E6557L, fetch64(s, pos + 8));
        }
        if (len >= 8) {
            return cityHash128WithSeed(new byte[0], 0, 0, fetch64(s, pos + 0) ^ len * -4348849565147123417L, fetch64(s, pos + len - 8) ^ 0xB492B66FBE98F273L);
        }
        return cityHash128WithSeed(s, pos, len, -4348849565147123417L, -5435081209227447693L);
    }
}
