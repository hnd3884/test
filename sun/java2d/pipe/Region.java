package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import java.awt.Shape;

public class Region
{
    static final int INIT_SIZE = 50;
    static final int GROW_SIZE = 50;
    public static final Region EMPTY_REGION;
    public static final Region WHOLE_REGION;
    int lox;
    int loy;
    int hix;
    int hiy;
    int endIndex;
    int[] bands;
    static final int INCLUDE_A = 1;
    static final int INCLUDE_B = 2;
    static final int INCLUDE_COMMON = 4;
    
    private static native void initIDs();
    
    public static int dimAdd(final int n, int n2) {
        if (n2 <= 0) {
            return n;
        }
        if ((n2 += n) < n) {
            return Integer.MAX_VALUE;
        }
        return n2;
    }
    
    public static int clipAdd(final int n, final int n2) {
        int n3 = n + n2;
        if (n3 > n != n2 > 0) {
            n3 = ((n2 < 0) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }
        return n3;
    }
    
    public static int clipScale(final int n, final double n2) {
        if (n2 == 1.0) {
            return n;
        }
        final double n3 = n * n2;
        if (n3 < -2.147483648E9) {
            return Integer.MIN_VALUE;
        }
        if (n3 > 2.147483647E9) {
            return Integer.MAX_VALUE;
        }
        return (int)Math.round(n3);
    }
    
    protected Region(final int lox, final int loy, final int hix, final int hiy) {
        this.lox = lox;
        this.loy = loy;
        this.hix = hix;
        this.hiy = hiy;
    }
    
    private Region(final int lox, final int loy, final int hix, final int hiy, final int[] bands, final int endIndex) {
        this.lox = lox;
        this.loy = loy;
        this.hix = hix;
        this.hiy = hiy;
        this.bands = bands;
        this.endIndex = endIndex;
    }
    
    public static Region getInstance(final Shape shape, final AffineTransform affineTransform) {
        return getInstance(Region.WHOLE_REGION, false, shape, affineTransform);
    }
    
    public static Region getInstance(final Region region, final Shape shape, final AffineTransform affineTransform) {
        return getInstance(region, false, shape, affineTransform);
    }
    
    public static Region getInstance(final Region outputArea, final boolean b, final Shape shape, final AffineTransform affineTransform) {
        if (shape instanceof RectangularShape && ((RectangularShape)shape).isEmpty()) {
            return Region.EMPTY_REGION;
        }
        final int[] array = new int[4];
        final ShapeSpanIterator shapeSpanIterator = new ShapeSpanIterator(b);
        try {
            shapeSpanIterator.setOutputArea(outputArea);
            shapeSpanIterator.appendPath(shape.getPathIterator(affineTransform));
            shapeSpanIterator.getPathBox(array);
            final Region instance = getInstance(array);
            instance.appendSpans(shapeSpanIterator);
            return instance;
        }
        finally {
            shapeSpanIterator.dispose();
        }
    }
    
    static Region getInstance(final int n, final int n2, final int n3, final int n4, final int[] array) {
        final int n5 = array[0];
        final int n6 = array[1];
        if (n4 <= n2 || n3 <= n || n6 <= n5) {
            return Region.EMPTY_REGION;
        }
        final int[] array2 = new int[(n6 - n5) * 5];
        int n7 = 0;
        int n8 = 2;
        for (int i = n5; i < n6; ++i) {
            final int max = Math.max(clipAdd(n, array[n8++]), n);
            final int min = Math.min(clipAdd(n, array[n8++]), n3);
            if (max < min) {
                final int max2 = Math.max(clipAdd(n2, i), n2);
                final int min2 = Math.min(clipAdd(max2, 1), n4);
                if (max2 < min2) {
                    array2[n7++] = max2;
                    array2[n7++] = min2;
                    array2[n7++] = 1;
                    array2[n7++] = max;
                    array2[n7++] = min;
                }
            }
        }
        return (n7 != 0) ? new Region(n, n2, n3, n4, array2, n7) : Region.EMPTY_REGION;
    }
    
    public static Region getInstance(final Rectangle rectangle) {
        return getInstanceXYWH(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public static Region getInstanceXYWH(final int n, final int n2, final int n3, final int n4) {
        return getInstanceXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public static Region getInstance(final int[] array) {
        return new Region(array[0], array[1], array[2], array[3]);
    }
    
    public static Region getInstanceXYXY(final int n, final int n2, final int n3, final int n4) {
        return new Region(n, n2, n3, n4);
    }
    
    public void setOutputArea(final Rectangle rectangle) {
        this.setOutputAreaXYWH(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void setOutputAreaXYWH(final int n, final int n2, final int n3, final int n4) {
        this.setOutputAreaXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public void setOutputArea(final int[] array) {
        this.lox = array[0];
        this.loy = array[1];
        this.hix = array[2];
        this.hiy = array[3];
    }
    
    public void setOutputAreaXYXY(final int lox, final int loy, final int hix, final int hiy) {
        this.lox = lox;
        this.loy = loy;
        this.hix = hix;
        this.hiy = hiy;
    }
    
    public void appendSpans(final SpanIterator spanIterator) {
        final int[] array = new int[6];
        while (spanIterator.nextSpan(array)) {
            this.appendSpan(array);
        }
        this.endRow(array);
        this.calcBBox();
    }
    
    public Region getScaledRegion(final double n, final double n2) {
        if (n == 0.0 || n2 == 0.0 || this == Region.EMPTY_REGION) {
            return Region.EMPTY_REGION;
        }
        if ((n == 1.0 && n2 == 1.0) || this == Region.WHOLE_REGION) {
            return this;
        }
        final Region region = new Region(clipScale(this.lox, n), clipScale(this.loy, n2), clipScale(this.hix, n), clipScale(this.hiy, n2));
        final int[] bands = this.bands;
        if (bands != null) {
            final int endIndex = this.endIndex;
            final int[] bands2 = new int[endIndex];
            int i = 0;
            int endIndex2 = 0;
            while (i < endIndex) {
                final int n3 = bands2[endIndex2++] = clipScale(bands[i++], n2);
                final int n4 = bands2[endIndex2++] = clipScale(bands[i++], n2);
                int n5 = bands2[endIndex2++] = bands[i++];
                final int n6 = endIndex2;
                if (n3 < n4) {
                    while (--n5 >= 0) {
                        final int clipScale = clipScale(bands[i++], n);
                        final int clipScale2 = clipScale(bands[i++], n);
                        if (clipScale < clipScale2) {
                            bands2[endIndex2++] = clipScale;
                            bands2[endIndex2++] = clipScale2;
                        }
                    }
                }
                else {
                    i += n5 * 2;
                }
                if (endIndex2 > n6) {
                    bands2[n6 - 1] = (endIndex2 - n6) / 2;
                }
                else {
                    endIndex2 = n6 - 3;
                }
            }
            if (endIndex2 <= 5) {
                if (endIndex2 < 5) {
                    final Region region2 = region;
                    final Region region3 = region;
                    final Region region4 = region;
                    final Region region5 = region;
                    final int n7 = 0;
                    region5.hiy = n7;
                    region4.hix = n7;
                    region3.loy = n7;
                    region2.lox = n7;
                }
                else {
                    region.loy = bands2[0];
                    region.hiy = bands2[1];
                    region.lox = bands2[3];
                    region.hix = bands2[4];
                }
            }
            else {
                region.endIndex = endIndex2;
                region.bands = bands2;
            }
        }
        return region;
    }
    
    public Region getTranslatedRegion(final int n, final int n2) {
        if ((n | n2) == 0x0) {
            return this;
        }
        final int n3 = this.lox + n;
        final int n4 = this.loy + n2;
        final int n5 = this.hix + n;
        final int n6 = this.hiy + n2;
        if (n3 > this.lox != n > 0 || n4 > this.loy != n2 > 0 || n5 > this.hix != n > 0 || n6 > this.hiy != n2 > 0) {
            return this.getSafeTranslatedRegion(n, n2);
        }
        final Region region = new Region(n3, n4, n5, n6);
        final int[] bands = this.bands;
        if (bands != null) {
            final int endIndex = this.endIndex;
            region.endIndex = endIndex;
            final int[] bands2 = new int[endIndex];
            region.bands = bands2;
            int i = 0;
            while (i < endIndex) {
                bands2[i] = bands[i] + n2;
                ++i;
                bands2[i] = bands[i] + n2;
                ++i;
                int n7 = bands2[i] = bands[i];
                ++i;
                while (--n7 >= 0) {
                    bands2[i] = bands[i] + n;
                    ++i;
                    bands2[i] = bands[i] + n;
                    ++i;
                }
            }
        }
        return region;
    }
    
    private Region getSafeTranslatedRegion(final int n, final int n2) {
        final Region region = new Region(clipAdd(this.lox, n), clipAdd(this.loy, n2), clipAdd(this.hix, n), clipAdd(this.hiy, n2));
        final int[] bands = this.bands;
        if (bands != null) {
            final int endIndex = this.endIndex;
            final int[] bands2 = new int[endIndex];
            int i = 0;
            int endIndex2 = 0;
            while (i < endIndex) {
                final int n3 = bands2[endIndex2++] = clipAdd(bands[i++], n2);
                final int n4 = bands2[endIndex2++] = clipAdd(bands[i++], n2);
                int n5 = bands2[endIndex2++] = bands[i++];
                final int n6 = endIndex2;
                if (n3 < n4) {
                    while (--n5 >= 0) {
                        final int clipAdd = clipAdd(bands[i++], n);
                        final int clipAdd2 = clipAdd(bands[i++], n);
                        if (clipAdd < clipAdd2) {
                            bands2[endIndex2++] = clipAdd;
                            bands2[endIndex2++] = clipAdd2;
                        }
                    }
                }
                else {
                    i += n5 * 2;
                }
                if (endIndex2 > n6) {
                    bands2[n6 - 1] = (endIndex2 - n6) / 2;
                }
                else {
                    endIndex2 = n6 - 3;
                }
            }
            if (endIndex2 <= 5) {
                if (endIndex2 < 5) {
                    final Region region2 = region;
                    final Region region3 = region;
                    final Region region4 = region;
                    final Region region5 = region;
                    final int n7 = 0;
                    region5.hiy = n7;
                    region4.hix = n7;
                    region3.loy = n7;
                    region2.lox = n7;
                }
                else {
                    region.loy = bands2[0];
                    region.hiy = bands2[1];
                    region.lox = bands2[3];
                    region.hix = bands2[4];
                }
            }
            else {
                region.endIndex = endIndex2;
                region.bands = bands2;
            }
        }
        return region;
    }
    
    public Region getIntersection(final Rectangle rectangle) {
        return this.getIntersectionXYWH(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public Region getIntersectionXYWH(final int n, final int n2, final int n3, final int n4) {
        return this.getIntersectionXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public Region getIntersectionXYXY(final int n, final int n2, final int n3, final int n4) {
        if (this.isInsideXYXY(n, n2, n3, n4)) {
            return this;
        }
        final Region region = new Region((n < this.lox) ? this.lox : n, (n2 < this.loy) ? this.loy : n2, (n3 > this.hix) ? this.hix : n3, (n4 > this.hiy) ? this.hiy : n4);
        if (this.bands != null) {
            region.appendSpans(this.getSpanIterator());
        }
        return region;
    }
    
    public Region getIntersection(final Region region) {
        if (this.isInsideQuickCheck(region)) {
            return this;
        }
        if (region.isInsideQuickCheck(this)) {
            return region;
        }
        final Region region2 = new Region((region.lox < this.lox) ? this.lox : region.lox, (region.loy < this.loy) ? this.loy : region.loy, (region.hix > this.hix) ? this.hix : region.hix, (region.hiy > this.hiy) ? this.hiy : region.hiy);
        if (!region2.isEmpty()) {
            region2.filterSpans(this, region, 4);
        }
        return region2;
    }
    
    public Region getUnion(final Region region) {
        if (region.isEmpty() || region.isInsideQuickCheck(this)) {
            return this;
        }
        if (this.isEmpty() || this.isInsideQuickCheck(region)) {
            return region;
        }
        final Region region2 = new Region((region.lox > this.lox) ? this.lox : region.lox, (region.loy > this.loy) ? this.loy : region.loy, (region.hix < this.hix) ? this.hix : region.hix, (region.hiy < this.hiy) ? this.hiy : region.hiy);
        region2.filterSpans(this, region, 7);
        return region2;
    }
    
    public Region getDifference(final Region region) {
        if (!region.intersectsQuickCheck(this)) {
            return this;
        }
        if (this.isInsideQuickCheck(region)) {
            return Region.EMPTY_REGION;
        }
        final Region region2 = new Region(this.lox, this.loy, this.hix, this.hiy);
        region2.filterSpans(this, region, 1);
        return region2;
    }
    
    public Region getExclusiveOr(final Region region) {
        if (region.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return region;
        }
        final Region region2 = new Region((region.lox > this.lox) ? this.lox : region.lox, (region.loy > this.loy) ? this.loy : region.loy, (region.hix < this.hix) ? this.hix : region.hix, (region.hiy < this.hiy) ? this.hiy : region.hiy);
        region2.filterSpans(this, region, 3);
        return region2;
    }
    
    private void filterSpans(final Region region, final Region region2, final int n) {
        int[] bands = region.bands;
        int[] bands2 = region2.bands;
        if (bands == null) {
            bands = new int[] { region.loy, region.hiy, 1, region.lox, region.hix };
        }
        if (bands2 == null) {
            bands2 = new int[] { region2.loy, region2.hiy, 1, region2.lox, region2.hix };
        }
        final int[] array = new int[6];
        int n2 = 0;
        int hiy = bands[n2++];
        int n3 = bands[n2++];
        int n4 = n2 + 2 * bands[n2++];
        int n5 = 0;
        int hiy2 = bands2[n5++];
        int n6 = bands2[n5++];
        int n7 = n5 + 2 * bands2[n5++];
        int i = this.loy;
        while (i < this.hiy) {
            if (i >= n3) {
                if (n4 < region.endIndex) {
                    n2 = n4;
                    hiy = bands[n2++];
                    n3 = bands[n2++];
                    n4 = n2 + 2 * bands[n2++];
                }
                else {
                    if ((n & 0x2) == 0x0) {
                        break;
                    }
                    n3 = (hiy = this.hiy);
                }
            }
            else if (i >= n6) {
                if (n7 < region2.endIndex) {
                    n5 = n7;
                    hiy2 = bands2[n5++];
                    n6 = bands2[n5++];
                    n7 = n5 + 2 * bands2[n5++];
                }
                else {
                    if ((n & 0x1) == 0x0) {
                        break;
                    }
                    n6 = (hiy2 = this.hiy);
                }
            }
            else {
                int n8;
                if (i < hiy2) {
                    if (i < hiy) {
                        i = Math.min(hiy, hiy2);
                        continue;
                    }
                    n8 = Math.min(n3, hiy2);
                    if ((n & 0x1) != 0x0) {
                        array[1] = i;
                        array[3] = n8;
                        int j = n2;
                        while (j < n4) {
                            array[0] = bands[j++];
                            array[2] = bands[j++];
                            this.appendSpan(array);
                        }
                    }
                }
                else if (i < hiy) {
                    n8 = Math.min(n6, hiy);
                    if ((n & 0x2) != 0x0) {
                        array[1] = i;
                        array[3] = n8;
                        int k = n5;
                        while (k < n7) {
                            array[0] = bands2[k++];
                            array[2] = bands2[k++];
                            this.appendSpan(array);
                        }
                    }
                }
                else {
                    n8 = Math.min(n3, n6);
                    array[1] = i;
                    array[3] = n8;
                    int n9 = n2;
                    int n10 = n5;
                    int hix = bands[n9++];
                    int n11 = bands[n9++];
                    int hix2 = bands2[n10++];
                    int n12 = bands2[n10++];
                    int l = Math.min(hix, hix2);
                    if (l < this.lox) {
                        l = this.lox;
                    }
                    while (l < this.hix) {
                        if (l >= n11) {
                            if (n9 < n4) {
                                hix = bands[n9++];
                                n11 = bands[n9++];
                            }
                            else {
                                if ((n & 0x2) == 0x0) {
                                    break;
                                }
                                n11 = (hix = this.hix);
                            }
                        }
                        else if (l >= n12) {
                            if (n10 < n7) {
                                hix2 = bands2[n10++];
                                n12 = bands2[n10++];
                            }
                            else {
                                if ((n & 0x1) == 0x0) {
                                    break;
                                }
                                n12 = (hix2 = this.hix);
                            }
                        }
                        else {
                            int n13;
                            int n14;
                            if (l < hix2) {
                                if (l < hix) {
                                    n13 = Math.min(hix, hix2);
                                    n14 = 0;
                                }
                                else {
                                    n13 = Math.min(n11, hix2);
                                    n14 = (((n & 0x1) != 0x0) ? 1 : 0);
                                }
                            }
                            else if (l < hix) {
                                n13 = Math.min(hix, n12);
                                n14 = (((n & 0x2) != 0x0) ? 1 : 0);
                            }
                            else {
                                n13 = Math.min(n11, n12);
                                n14 = (((n & 0x4) != 0x0) ? 1 : 0);
                            }
                            if (n14 != 0) {
                                array[0] = l;
                                array[2] = n13;
                                this.appendSpan(array);
                            }
                            l = n13;
                        }
                    }
                }
                i = n8;
            }
        }
        this.endRow(array);
        this.calcBBox();
    }
    
    public Region getBoundsIntersection(final Rectangle rectangle) {
        return this.getBoundsIntersectionXYWH(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public Region getBoundsIntersectionXYWH(final int n, final int n2, final int n3, final int n4) {
        return this.getBoundsIntersectionXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public Region getBoundsIntersectionXYXY(final int n, final int n2, final int n3, final int n4) {
        if (this.bands == null && this.lox >= n && this.loy >= n2 && this.hix <= n3 && this.hiy <= n4) {
            return this;
        }
        return new Region((n < this.lox) ? this.lox : n, (n2 < this.loy) ? this.loy : n2, (n3 > this.hix) ? this.hix : n3, (n4 > this.hiy) ? this.hiy : n4);
    }
    
    public Region getBoundsIntersection(final Region region) {
        if (this.encompasses(region)) {
            return region;
        }
        if (region.encompasses(this)) {
            return this;
        }
        return new Region((region.lox < this.lox) ? this.lox : region.lox, (region.loy < this.loy) ? this.loy : region.loy, (region.hix > this.hix) ? this.hix : region.hix, (region.hiy > this.hiy) ? this.hiy : region.hiy);
    }
    
    private void appendSpan(final int[] array) {
        int lox;
        if ((lox = array[0]) < this.lox) {
            lox = this.lox;
        }
        int loy;
        if ((loy = array[1]) < this.loy) {
            loy = this.loy;
        }
        int hix;
        if ((hix = array[2]) > this.hix) {
            hix = this.hix;
        }
        int hiy;
        if ((hiy = array[3]) > this.hiy) {
            hiy = this.hiy;
        }
        if (hix <= lox || hiy <= loy) {
            return;
        }
        int n = array[4];
        if (this.endIndex == 0 || loy >= this.bands[n + 1]) {
            if (this.bands == null) {
                this.bands = new int[50];
            }
            else {
                this.needSpace(5);
                this.endRow(array);
                n = array[4];
            }
            this.bands[this.endIndex++] = loy;
            this.bands[this.endIndex++] = hiy;
            this.bands[this.endIndex++] = 0;
        }
        else {
            if (loy != this.bands[n] || hiy != this.bands[n + 1] || lox < this.bands[this.endIndex - 1]) {
                throw new InternalError("bad span");
            }
            if (lox == this.bands[this.endIndex - 1]) {
                this.bands[this.endIndex - 1] = hix;
                return;
            }
            this.needSpace(2);
        }
        this.bands[this.endIndex++] = lox;
        this.bands[this.endIndex++] = hix;
        final int[] bands = this.bands;
        final int n2 = n + 2;
        ++bands[n2];
    }
    
    private void needSpace(final int n) {
        if (this.endIndex + n >= this.bands.length) {
            final int[] bands = new int[this.bands.length + 50];
            System.arraycopy(this.bands, 0, bands, 0, this.endIndex);
            this.bands = bands;
        }
    }
    
    private void endRow(final int[] array) {
        int n = array[4];
        int endIndex = array[5];
        if (n > endIndex) {
            final int[] bands = this.bands;
            if (bands[endIndex + 1] == bands[n] && bands[endIndex + 2] == bands[n + 2]) {
                int n2;
                for (n2 = bands[n + 2] * 2, n += 3, endIndex += 3; n2 > 0 && bands[n++] == bands[endIndex++]; --n2) {}
                if (n2 == 0) {
                    bands[array[5] + 1] = bands[endIndex + 1];
                    this.endIndex = endIndex;
                    return;
                }
            }
        }
        array[5] = array[4];
        array[4] = this.endIndex;
    }
    
    private void calcBBox() {
        final int[] bands = this.bands;
        if (this.endIndex <= 5) {
            if (this.endIndex == 0) {
                final int n = 0;
                this.hiy = n;
                this.hix = n;
                this.loy = n;
                this.lox = n;
            }
            else {
                this.loy = bands[0];
                this.hiy = bands[1];
                this.lox = bands[3];
                this.hix = bands[4];
                this.endIndex = 0;
            }
            this.bands = null;
            return;
        }
        int hix = this.hix;
        int lox = this.lox;
        int n2 = 0;
        int i = 0;
        while (i < this.endIndex) {
            n2 = i;
            final int n3 = bands[i + 2];
            i += 3;
            if (hix > bands[i]) {
                hix = bands[i];
            }
            i += n3 * 2;
            if (lox < bands[i - 1]) {
                lox = bands[i - 1];
            }
        }
        this.lox = hix;
        this.loy = bands[0];
        this.hix = lox;
        this.hiy = bands[n2 + 1];
    }
    
    public final int getLoX() {
        return this.lox;
    }
    
    public final int getLoY() {
        return this.loy;
    }
    
    public final int getHiX() {
        return this.hix;
    }
    
    public final int getHiY() {
        return this.hiy;
    }
    
    public final int getWidth() {
        if (this.hix < this.lox) {
            return 0;
        }
        int n;
        if ((n = this.hix - this.lox) < 0) {
            n = Integer.MAX_VALUE;
        }
        return n;
    }
    
    public final int getHeight() {
        if (this.hiy < this.loy) {
            return 0;
        }
        int n;
        if ((n = this.hiy - this.loy) < 0) {
            n = Integer.MAX_VALUE;
        }
        return n;
    }
    
    public boolean isEmpty() {
        return this.hix <= this.lox || this.hiy <= this.loy;
    }
    
    public boolean isRectangular() {
        return this.bands == null;
    }
    
    public boolean contains(final int n, final int n2) {
        if (n < this.lox || n >= this.hix || n2 < this.loy || n2 >= this.hiy) {
            return false;
        }
        if (this.bands == null) {
            return true;
        }
        for (int i = 0; i < this.endIndex; i += this.bands[i++] * 2) {
            if (n2 < this.bands[i++]) {
                return false;
            }
            if (n2 < this.bands[i++]) {
                while (i < i + this.bands[i++] * 2) {
                    if (n < this.bands[i++]) {
                        return false;
                    }
                    if (n < this.bands[i++]) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    
    public boolean isInsideXYWH(final int n, final int n2, final int n3, final int n4) {
        return this.isInsideXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public boolean isInsideXYXY(final int n, final int n2, final int n3, final int n4) {
        return this.lox >= n && this.loy >= n2 && this.hix <= n3 && this.hiy <= n4;
    }
    
    public boolean isInsideQuickCheck(final Region region) {
        return region.bands == null && region.lox <= this.lox && region.loy <= this.loy && region.hix >= this.hix && region.hiy >= this.hiy;
    }
    
    public boolean intersectsQuickCheckXYXY(final int n, final int n2, final int n3, final int n4) {
        return n3 > this.lox && n < this.hix && n4 > this.loy && n2 < this.hiy;
    }
    
    public boolean intersectsQuickCheck(final Region region) {
        return region.hix > this.lox && region.lox < this.hix && region.hiy > this.loy && region.loy < this.hiy;
    }
    
    public boolean encompasses(final Region region) {
        return this.bands == null && this.lox <= region.lox && this.loy <= region.loy && this.hix >= region.hix && this.hiy >= region.hiy;
    }
    
    public boolean encompassesXYWH(final int n, final int n2, final int n3, final int n4) {
        return this.encompassesXYXY(n, n2, dimAdd(n, n3), dimAdd(n2, n4));
    }
    
    public boolean encompassesXYXY(final int n, final int n2, final int n3, final int n4) {
        return this.bands == null && this.lox <= n && this.loy <= n2 && this.hix >= n3 && this.hiy >= n4;
    }
    
    public void getBounds(final int[] array) {
        array[0] = this.lox;
        array[1] = this.loy;
        array[2] = this.hix;
        array[3] = this.hiy;
    }
    
    public void clipBoxToBounds(final int[] array) {
        if (array[0] < this.lox) {
            array[0] = this.lox;
        }
        if (array[1] < this.loy) {
            array[1] = this.loy;
        }
        if (array[2] > this.hix) {
            array[2] = this.hix;
        }
        if (array[3] > this.hiy) {
            array[3] = this.hiy;
        }
    }
    
    public RegionIterator getIterator() {
        return new RegionIterator(this);
    }
    
    public SpanIterator getSpanIterator() {
        return new RegionSpanIterator(this);
    }
    
    public SpanIterator getSpanIterator(final int[] array) {
        final SpanIterator spanIterator = this.getSpanIterator();
        spanIterator.intersectClipBox(array[0], array[1], array[2], array[3]);
        return spanIterator;
    }
    
    public SpanIterator filter(SpanIterator spanIterator) {
        if (this.bands == null) {
            spanIterator.intersectClipBox(this.lox, this.loy, this.hix, this.hiy);
        }
        else {
            spanIterator = new RegionClipSpanIterator(this, spanIterator);
        }
        return spanIterator;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Region[[");
        sb.append(this.lox);
        sb.append(", ");
        sb.append(this.loy);
        sb.append(" => ");
        sb.append(this.hix);
        sb.append(", ");
        sb.append(this.hiy);
        sb.append("]");
        if (this.bands != null) {
            int i = 0;
            while (i < this.endIndex) {
                sb.append("y{");
                sb.append(this.bands[i++]);
                sb.append(",");
                sb.append(this.bands[i++]);
                sb.append("}[");
                while (i < i + this.bands[i++] * 2) {
                    sb.append("x(");
                    sb.append(this.bands[i++]);
                    sb.append(", ");
                    sb.append(this.bands[i++]);
                    sb.append(")");
                }
                sb.append("]");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.isEmpty() ? 0 : (this.lox * 3 + this.loy * 5 + this.hix * 7 + this.hiy * 9);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Region)) {
            return false;
        }
        final Region region = (Region)o;
        if (this.isEmpty()) {
            return region.isEmpty();
        }
        if (region.isEmpty()) {
            return false;
        }
        if (region.lox != this.lox || region.loy != this.loy || region.hix != this.hix || region.hiy != this.hiy) {
            return false;
        }
        if (this.bands == null) {
            return region.bands == null;
        }
        if (region.bands == null) {
            return false;
        }
        if (this.endIndex != region.endIndex) {
            return false;
        }
        final int[] bands = this.bands;
        final int[] bands2 = region.bands;
        for (int i = 0; i < this.endIndex; ++i) {
            if (bands[i] != bands2[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        EMPTY_REGION = new ImmutableRegion(0, 0, 0, 0);
        WHOLE_REGION = new ImmutableRegion(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        initIDs();
    }
    
    private static final class ImmutableRegion extends Region
    {
        protected ImmutableRegion(final int n, final int n2, final int n3, final int n4) {
            super(n, n2, n3, n4);
        }
        
        @Override
        public void appendSpans(final SpanIterator spanIterator) {
        }
        
        @Override
        public void setOutputArea(final Rectangle rectangle) {
        }
        
        @Override
        public void setOutputAreaXYWH(final int n, final int n2, final int n3, final int n4) {
        }
        
        @Override
        public void setOutputArea(final int[] array) {
        }
        
        @Override
        public void setOutputAreaXYXY(final int n, final int n2, final int n3, final int n4) {
        }
    }
}
