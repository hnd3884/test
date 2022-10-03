package sun.java2d.pisces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import sun.java2d.pipe.AATileGenerator;

final class PiscesTileGenerator implements AATileGenerator
{
    public static final int TILE_SIZE = 32;
    private static final Map<Integer, byte[]> alphaMapsCache;
    PiscesCache cache;
    int x;
    int y;
    final int maxalpha;
    private final int maxTileAlphaSum;
    byte[] alphaMap;
    
    public PiscesTileGenerator(final Renderer renderer, final int maxalpha) {
        this.cache = renderer.getCache();
        this.x = this.cache.bboxX0;
        this.y = this.cache.bboxY0;
        this.alphaMap = getAlphaMap(maxalpha);
        this.maxalpha = maxalpha;
        this.maxTileAlphaSum = 1024 * maxalpha;
    }
    
    private static byte[] buildAlphaMap(final int n) {
        final byte[] array = new byte[n + 1];
        final int n2 = n >> 2;
        for (int i = 0; i <= n; ++i) {
            array[i] = (byte)((i * 255 + n2) / n);
        }
        return array;
    }
    
    public static byte[] getAlphaMap(final int n) {
        if (!PiscesTileGenerator.alphaMapsCache.containsKey(n)) {
            PiscesTileGenerator.alphaMapsCache.put(n, buildAlphaMap(n));
        }
        return PiscesTileGenerator.alphaMapsCache.get(n);
    }
    
    public void getBbox(final int[] array) {
        array[0] = this.cache.bboxX0;
        array[1] = this.cache.bboxY0;
        array[2] = this.cache.bboxX1;
        array[3] = this.cache.bboxY1;
    }
    
    @Override
    public int getTileWidth() {
        return 32;
    }
    
    @Override
    public int getTileHeight() {
        return 32;
    }
    
    @Override
    public int getTypicalAlpha() {
        final int alphaSumInTile = this.cache.alphaSumInTile(this.x, this.y);
        return (alphaSumInTile == 0) ? 0 : ((alphaSumInTile == this.maxTileAlphaSum) ? 255 : 128);
    }
    
    @Override
    public void nextTile() {
        final int x = this.x + 32;
        this.x = x;
        if (x >= this.cache.bboxX1) {
            this.x = this.cache.bboxX0;
            this.y += 32;
        }
    }
    
    @Override
    public void getAlpha(final byte[] array, final int n, final int n2) {
        final int x = this.x;
        int bboxX1 = x + 32;
        final int y = this.y;
        int bboxY1 = y + 32;
        if (bboxX1 > this.cache.bboxX1) {
            bboxX1 = this.cache.bboxX1;
        }
        if (bboxY1 > this.cache.bboxY1) {
            bboxY1 = this.cache.bboxY1;
        }
        final int n3 = y - this.cache.bboxY0;
        final int n4 = bboxY1 - this.cache.bboxY0;
        int n5 = n;
        for (int i = n3; i < n4; ++i) {
            final int[] array2 = this.cache.rowAARLE[i];
            assert array2 != null;
            int j = this.cache.minTouched(i);
            if (j > bboxX1) {
                j = bboxX1;
            }
            for (int k = x; k < j; ++k) {
                array[n5++] = 0;
            }
            for (int n6 = 2; j < bboxX1 && n6 < array2[1]; n6 += 2) {
                int n7 = 0;
                assert array2[1] > 2;
                byte b;
                try {
                    b = this.alphaMap[array2[n6]];
                    n7 = array2[n6 + 1];
                    assert n7 > 0;
                }
                catch (final RuntimeException ex) {
                    System.out.println("maxalpha = " + this.maxalpha);
                    System.out.println("tile[" + x + ", " + n3 + " => " + bboxX1 + ", " + n4 + "]");
                    System.out.println("cx = " + j + ", cy = " + i);
                    System.out.println("idx = " + n5 + ", pos = " + n6);
                    System.out.println("len = " + n7);
                    System.out.print(this.cache.toString());
                    ex.printStackTrace();
                    throw ex;
                }
                int n8 = j;
                int n9;
                j = (n9 = j + n7);
                if (n8 < x) {
                    n8 = x;
                }
                if (n9 > bboxX1) {
                    n9 = bboxX1;
                }
                int n10 = n9 - n8;
                while (--n10 >= 0) {
                    try {
                        array[n5++] = b;
                        continue;
                    }
                    catch (final RuntimeException ex2) {
                        System.out.println("maxalpha = " + this.maxalpha);
                        System.out.println("tile[" + x + ", " + n3 + " => " + bboxX1 + ", " + n4 + "]");
                        System.out.println("cx = " + j + ", cy = " + i);
                        System.out.println("idx = " + n5 + ", pos = " + n6);
                        System.out.println("rx0 = " + n8 + ", rx1 = " + n9);
                        System.out.println("len = " + n10);
                        System.out.print(this.cache.toString());
                        ex2.printStackTrace();
                        throw ex2;
                    }
                    break;
                }
            }
            if (j < x) {
                j = x;
            }
            while (j < bboxX1) {
                array[n5++] = 0;
                ++j;
            }
            n5 += n2 - (bboxX1 - x);
        }
        this.nextTile();
    }
    
    static String hex(final int n, final int n2) {
        String s;
        for (s = Integer.toHexString(n); s.length() < n2; s = "0" + s) {}
        return s.substring(0, n2);
    }
    
    @Override
    public void dispose() {
    }
    
    static {
        alphaMapsCache = new ConcurrentHashMap<Integer, byte[]>();
    }
}
