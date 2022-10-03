package com.sshtools.zlib;

final class e
{
    int h;
    int b;
    long[] d;
    long j;
    int g;
    int e;
    int i;
    c c;
    private static byte[] f;
    
    e() {
        this.d = new long[1];
    }
    
    int c(final ZStream zStream) {
        if (zStream == null || zStream.c == null) {
            return -2;
        }
        final long n = 0L;
        zStream.total_out = n;
        zStream.total_in = n;
        zStream.msg = null;
        zStream.c.h = ((zStream.c.e != 0) ? 7 : 0);
        zStream.c.c.b(zStream, null);
        return 0;
    }
    
    int b(final ZStream zStream) {
        if (this.c != null) {
            this.c.b(zStream);
        }
        this.c = null;
        return 0;
    }
    
    int c(final ZStream zStream, int i) {
        zStream.msg = null;
        this.c = null;
        this.e = 0;
        if (i < 0) {
            i = -i;
            this.e = 1;
        }
        if (i < 8 || i > 15) {
            this.b(zStream);
            return -2;
        }
        this.i = i;
        zStream.c.c = new c(zStream, (zStream.c.e != 0) ? null : this, 1 << i);
        this.c(zStream);
        return 0;
    }
    
    int b(final ZStream zStream, int n) {
        if (zStream == null || zStream.c == null || zStream.next_in == null) {
            return -2;
        }
        n = ((n == 4) ? -5 : 0);
        int c = -5;
        Label_0615: {
            Label_0536: {
                Label_0457: {
                Label_0383:
                    while (true) {
                        switch (zStream.c.h) {
                            case 0: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                final e c2 = zStream.c;
                                final byte b = zStream.next_in[zStream.next_in_index++];
                                c2.b = b;
                                if ((b & 0xF) != 0x8) {
                                    zStream.c.h = 13;
                                    zStream.msg = "unknown compression method";
                                    zStream.c.g = 5;
                                    continue;
                                }
                                if ((zStream.c.b >> 4) + 8 > zStream.c.i) {
                                    zStream.c.h = 13;
                                    zStream.msg = "invalid window size";
                                    zStream.c.g = 5;
                                    continue;
                                }
                                zStream.c.h = 1;
                            }
                            case 1: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                final int n2 = zStream.next_in[zStream.next_in_index++] & 0xFF;
                                if (((zStream.c.b << 8) + n2) % 31 != 0) {
                                    zStream.c.h = 13;
                                    zStream.msg = "incorrect header check";
                                    zStream.c.g = 5;
                                    continue;
                                }
                                if ((n2 & 0x20) == 0x0) {
                                    zStream.c.h = 7;
                                    continue;
                                }
                                zStream.c.h = 2;
                                break Label_0383;
                            }
                            case 2: {
                                break Label_0383;
                            }
                            case 3: {
                                break Label_0457;
                            }
                            case 4: {
                                break Label_0536;
                            }
                            case 5: {
                                break Label_0615;
                            }
                            case 6: {
                                zStream.c.h = 13;
                                zStream.msg = "need dictionary";
                                zStream.c.g = 0;
                                return -2;
                            }
                            case 7: {
                                c = zStream.c.c.c(zStream, c);
                                if (c == -3) {
                                    zStream.c.h = 13;
                                    zStream.c.g = 0;
                                    continue;
                                }
                                if (c == 0) {
                                    c = n;
                                }
                                if (c != 1) {
                                    return c;
                                }
                                c = n;
                                zStream.c.c.b(zStream, zStream.c.d);
                                if (zStream.c.e != 0) {
                                    zStream.c.h = 12;
                                    continue;
                                }
                                zStream.c.h = 8;
                            }
                            case 8: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                zStream.c.j = ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 24) & 0xFF000000L);
                                zStream.c.h = 9;
                            }
                            case 9: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                final e c3 = zStream.c;
                                c3.j += ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 16) & 0xFF0000L);
                                zStream.c.h = 10;
                            }
                            case 10: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                final e c4 = zStream.c;
                                c4.j += ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 8) & 0xFF00L);
                                zStream.c.h = 11;
                            }
                            case 11: {
                                if (zStream.avail_in == 0) {
                                    return c;
                                }
                                c = n;
                                --zStream.avail_in;
                                ++zStream.total_in;
                                final e c5 = zStream.c;
                                c5.j += ((long)zStream.next_in[zStream.next_in_index++] & 0xFFL);
                                if ((int)zStream.c.d[0] != (int)zStream.c.j) {
                                    zStream.c.h = 13;
                                    zStream.msg = "incorrect data check";
                                    zStream.c.g = 5;
                                    continue;
                                }
                                zStream.c.h = 12;
                                return 1;
                            }
                            case 12: {
                                return 1;
                            }
                            case 13: {
                                return -3;
                            }
                            default: {
                                return -2;
                            }
                        }
                    }
                    if (zStream.avail_in == 0) {
                        return c;
                    }
                    c = n;
                    --zStream.avail_in;
                    ++zStream.total_in;
                    zStream.c.j = ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 24) & 0xFF000000L);
                    zStream.c.h = 3;
                }
                if (zStream.avail_in == 0) {
                    return c;
                }
                c = n;
                --zStream.avail_in;
                ++zStream.total_in;
                final e c6 = zStream.c;
                c6.j += ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 16) & 0xFF0000L);
                zStream.c.h = 4;
            }
            if (zStream.avail_in == 0) {
                return c;
            }
            c = n;
            --zStream.avail_in;
            ++zStream.total_in;
            final e c7 = zStream.c;
            c7.j += ((long)((zStream.next_in[zStream.next_in_index++] & 0xFF) << 8) & 0xFF00L);
            zStream.c.h = 5;
        }
        if (zStream.avail_in == 0) {
            return c;
        }
        --zStream.avail_in;
        ++zStream.total_in;
        final e c8 = zStream.c;
        c8.j += ((long)zStream.next_in[zStream.next_in_index++] & 0xFFL);
        zStream.adler = zStream.c.j;
        zStream.c.h = 6;
        return 2;
    }
    
    int b(final ZStream zStream, final byte[] array, final int n) {
        int n2 = 0;
        int n3 = n;
        if (zStream == null || zStream.c == null || zStream.c.h != 6) {
            return -2;
        }
        if (zStream.b.b(1L, array, 0, n) != zStream.adler) {
            return -3;
        }
        zStream.adler = zStream.b.b(0L, null, 0, 0);
        if (n3 >= 1 << zStream.c.i) {
            n3 = (1 << zStream.c.i) - 1;
            n2 = n - n3;
        }
        zStream.c.c.b(array, n2, n3);
        zStream.c.h = 7;
        return 0;
    }
    
    int d(final ZStream zStream) {
        if (zStream == null || zStream.c == null) {
            return -2;
        }
        if (zStream.c.h != 13) {
            zStream.c.h = 13;
            zStream.c.g = 0;
        }
        int avail_in;
        if ((avail_in = zStream.avail_in) == 0) {
            return -5;
        }
        int next_in_index = zStream.next_in_index;
        int g;
        for (g = zStream.c.g; avail_in != 0 && g < 4; --avail_in) {
            if (zStream.next_in[next_in_index] == com.sshtools.zlib.e.f[g]) {
                ++g;
            }
            else if (zStream.next_in[next_in_index] != 0) {
                g = 0;
            }
            else {
                g = 4 - g;
            }
            ++next_in_index;
        }
        zStream.total_in += next_in_index - zStream.next_in_index;
        zStream.next_in_index = next_in_index;
        zStream.avail_in = avail_in;
        if ((zStream.c.g = g) != 4) {
            return -3;
        }
        final long total_in = zStream.total_in;
        final long total_out = zStream.total_out;
        this.c(zStream);
        zStream.total_in = total_in;
        zStream.total_out = total_out;
        zStream.c.h = 7;
        return 0;
    }
    
    static {
        e.f = new byte[] { 0, 0, -1, -1 };
    }
}
