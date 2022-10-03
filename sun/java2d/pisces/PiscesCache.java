package sun.java2d.pisces;

import java.util.Arrays;

final class PiscesCache
{
    final int bboxX0;
    final int bboxY0;
    final int bboxX1;
    final int bboxY1;
    final int[][] rowAARLE;
    private int x0;
    private int y0;
    private final int[][] touchedTile;
    static final int TILE_SIZE_LG = 5;
    static final int TILE_SIZE = 32;
    private static final int INIT_ROW_SIZE = 8;
    
    PiscesCache(final int bboxX0, final int bboxY0, final int n, final int n2) {
        this.x0 = Integer.MIN_VALUE;
        this.y0 = Integer.MIN_VALUE;
        assert n2 >= bboxY0 && n >= bboxX0;
        this.bboxX0 = bboxX0;
        this.bboxY0 = bboxY0;
        this.bboxX1 = n + 1;
        this.bboxY1 = n2 + 1;
        this.rowAARLE = new int[this.bboxY1 - this.bboxY0 + 1][8];
        this.x0 = 0;
        this.y0 = -1;
        this.touchedTile = new int[n2 - bboxY0 + 32 >> 5][n - bboxX0 + 32 >> 5];
    }
    
    void addRLERun(final int n, final int n2) {
        if (n2 > 0) {
            this.addTupleToRow(this.y0, n, n2);
            if (n != 0) {
                int i = this.x0 >> 5;
                final int n3 = this.y0 >> 5;
                int n4 = this.x0 + n2 - 1 >> 5;
                if (n4 >= this.touchedTile[n3].length) {
                    n4 = this.touchedTile[n3].length - 1;
                }
                if (i <= n4) {
                    final int n5 = i + 1 << 5;
                    if (n5 > this.x0 + n2) {
                        final int[] array = this.touchedTile[n3];
                        final int n6 = i;
                        array[n6] += n * n2;
                    }
                    else {
                        final int[] array2 = this.touchedTile[n3];
                        final int n7 = i;
                        array2[n7] += n * (n5 - this.x0);
                    }
                    ++i;
                }
                while (i < n4) {
                    final int[] array3 = this.touchedTile[n3];
                    final int n8 = i;
                    array3[n8] += n << 5;
                    ++i;
                }
                if (i == n4) {
                    final int min = Math.min(this.x0 + n2, i + 1 << 5);
                    final int n9 = i << 5;
                    final int[] array4 = this.touchedTile[n3];
                    final int n10 = i;
                    array4[n10] += n * (min - n9);
                }
            }
            this.x0 += n2;
        }
    }
    
    void startRow(final int n, final int n2) {
        assert n - this.bboxY0 > this.y0;
        assert n <= this.bboxY1;
        this.y0 = n - this.bboxY0;
        assert this.rowAARLE[this.y0][1] == 0;
        this.x0 = n2 - this.bboxX0;
        assert this.x0 >= 0 : "Input must not be to the left of bbox bounds";
        this.rowAARLE[this.y0][0] = n2;
        this.rowAARLE[this.y0][1] = 2;
    }
    
    int alphaSumInTile(int n, int n2) {
        n -= this.bboxX0;
        n2 -= this.bboxY0;
        return this.touchedTile[n2 >> 5][n >> 5];
    }
    
    int minTouched(final int n) {
        return this.rowAARLE[n][0];
    }
    
    int rowLength(final int n) {
        return this.rowAARLE[n][1];
    }
    
    private void addTupleToRow(final int n, final int n2, final int n3) {
        int n4 = this.rowAARLE[n][1];
        (this.rowAARLE[n] = Helpers.widenArray(this.rowAARLE[n], n4, 2))[n4++] = n2;
        this.rowAARLE[n][n4++] = n3;
        this.rowAARLE[n][1] = n4;
    }
    
    @Override
    public String toString() {
        String s = "bbox = [" + this.bboxX0 + ", " + this.bboxY0 + " => " + this.bboxX1 + ", " + this.bboxY1 + "]\n";
        for (final int[] array : this.rowAARLE) {
            if (array != null) {
                s = s + "minTouchedX=" + array[0] + "\tRLE Entries: " + Arrays.toString(Arrays.copyOfRange(array, 2, array[1])) + "\n";
            }
            else {
                s += "[]\n";
            }
        }
        return s;
    }
}
