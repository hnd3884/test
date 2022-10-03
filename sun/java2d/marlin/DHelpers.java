package sun.java2d.marlin;

import java.util.Arrays;
import sun.java2d.marlin.stats.Histogram;
import sun.java2d.marlin.stats.StatLong;

final class DHelpers implements MarlinConst
{
    private DHelpers() {
        throw new Error("This is a non instantiable class");
    }
    
    static boolean within(final double n, final double n2, final double n3) {
        final double n4 = n2 - n;
        return n4 <= n3 && n4 >= -n3;
    }
    
    static double evalCubic(final double n, final double n2, final double n3, final double n4, final double n5) {
        return n5 * (n5 * (n5 * n + n2) + n3) + n4;
    }
    
    static double evalQuad(final double n, final double n2, final double n3, final double n4) {
        return n4 * (n4 * n + n2) + n3;
    }
    
    static int quadraticRoots(final double n, final double n2, final double n3, final double[] array, final int n4) {
        int n5 = n4;
        if (n != 0.0) {
            final double n6 = n2 * n2 - 4.0 * n * n3;
            if (n6 > 0.0) {
                final double sqrt = Math.sqrt(n6);
                if (n2 >= 0.0) {
                    array[n5++] = 2.0 * n3 / (-n2 - sqrt);
                    array[n5++] = (-n2 - sqrt) / (2.0 * n);
                }
                else {
                    array[n5++] = (-n2 + sqrt) / (2.0 * n);
                    array[n5++] = 2.0 * n3 / (-n2 + sqrt);
                }
            }
            else if (n6 == 0.0) {
                array[n5++] = -n2 / (2.0 * n);
            }
        }
        else if (n2 != 0.0) {
            array[n5++] = -n3 / n2;
        }
        return n5 - n4;
    }
    
    static int cubicRootsInAB(final double n, double n2, double n3, double n4, final double[] array, final int n5, final double n6, final double n7) {
        if (n == 0.0) {
            return filterOutNotInAB(array, n5, quadraticRoots(n2, n3, n4, array, n5), n6, n7) - n5;
        }
        n2 /= n;
        n3 /= n;
        n4 /= n;
        final double n8 = 0.3333333333333333 * n2;
        final double n9 = n2 * n2;
        final double n10 = 0.3333333333333333 * (-0.3333333333333333 * n9 + n3);
        final double n11 = 0.5 * (0.07407407407407407 * n2 * n9 - n8 * n3 + n4);
        final double n12 = n10 * n10 * n10;
        final double n13 = n11 * n11 + n12;
        int n16;
        if (n13 < 0.0) {
            final double n14 = 0.3333333333333333 * Math.acos(-n11 / Math.sqrt(-n12));
            final double n15 = 2.0 * Math.sqrt(-n10);
            array[n5] = n15 * Math.cos(n14) - n8;
            array[n5 + 1] = -n15 * Math.cos(n14 + 1.0471975511965976) - n8;
            array[n5 + 2] = -n15 * Math.cos(n14 - 1.0471975511965976) - n8;
            n16 = 3;
        }
        else {
            final double sqrt = Math.sqrt(n13);
            final double cbrt = Math.cbrt(sqrt - n11);
            final double n17 = -Math.cbrt(sqrt + n11);
            array[n5] = cbrt + n17 - n8;
            n16 = 1;
            if (within(n13, 0.0, 1.0E-8)) {
                array[n5 + 1] = -0.5 * (cbrt + n17) - n8;
                n16 = 2;
            }
        }
        return filterOutNotInAB(array, n5, n16, n6, n7) - n5;
    }
    
    static int filterOutNotInAB(final double[] array, final int n, final int n2, final double n3, final double n4) {
        int n5 = n;
        for (int i = n; i < n + n2; ++i) {
            if (array[i] >= n3 && array[i] < n4) {
                array[n5++] = array[i];
            }
        }
        return n5;
    }
    
    static double fastLineLen(final double n, final double n2, final double n3, final double n4) {
        return Math.abs(n3 - n) + Math.abs(n4 - n2);
    }
    
    static double linelen(final double n, final double n2, final double n3, final double n4) {
        final double n5 = n3 - n;
        final double n6 = n4 - n2;
        return Math.sqrt(n5 * n5 + n6 * n6);
    }
    
    static double fastQuadLen(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return Math.abs(n3 - n) + Math.abs(n5 - n3) + Math.abs(n4 - n2) + Math.abs(n6 - n4);
    }
    
    static double quadlen(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return (linelen(n, n2, n3, n4) + linelen(n3, n4, n5, n6) + linelen(n, n2, n5, n6)) / 2.0;
    }
    
    static double fastCurvelen(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        return Math.abs(n3 - n) + Math.abs(n5 - n3) + Math.abs(n7 - n5) + Math.abs(n4 - n2) + Math.abs(n6 - n4) + Math.abs(n8 - n6);
    }
    
    static double curvelen(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        return (linelen(n, n2, n3, n4) + linelen(n3, n4, n5, n6) + linelen(n5, n6, n7, n8) + linelen(n, n2, n7, n8)) / 2.0;
    }
    
    static int findSubdivPoints(final DCurve dCurve, final double[] array, final double[] array2, final int n, final double n2) {
        final double n3 = array[2] - array[0];
        final double n4 = array[3] - array[1];
        if (n4 != 0.0 && n3 != 0.0) {
            final double sqrt = Math.sqrt(n3 * n3 + n4 * n4);
            final double n5 = n3 / sqrt;
            final double n6 = n4 / sqrt;
            final double n7 = n5 * array[0] + n6 * array[1];
            final double n8 = n5 * array[1] - n6 * array[0];
            final double n9 = n5 * array[2] + n6 * array[3];
            final double n10 = n5 * array[3] - n6 * array[2];
            final double n11 = n5 * array[4] + n6 * array[5];
            final double n12 = n5 * array[5] - n6 * array[4];
            switch (n) {
                case 8: {
                    dCurve.set(n7, n8, n9, n10, n11, n12, n5 * array[6] + n6 * array[7], n5 * array[7] - n6 * array[6]);
                    break;
                }
                case 6: {
                    dCurve.set(n7, n8, n9, n10, n11, n12);
                    break;
                }
            }
        }
        else {
            dCurve.set(array, n);
        }
        final int n13 = 0;
        final int n14 = n13 + dCurve.dxRoots(array2, n13);
        int n15 = n14 + dCurve.dyRoots(array2, n14);
        if (n == 8) {
            n15 += dCurve.infPoints(array2, n15);
        }
        final int filterOutNotInAB = filterOutNotInAB(array2, 0, n15 + dCurve.rootsOfROCMinusW(array2, n15, n2, 1.0E-4), 1.0E-4, 0.9999);
        isort(array2, filterOutNotInAB);
        return filterOutNotInAB;
    }
    
    static int findClipPoints(final DCurve dCurve, final double[] array, final double[] array2, final int n, final int n2, final double[] array3) {
        dCurve.set(array, n);
        int n3 = 0;
        if ((n2 & 0x4) != 0x0) {
            n3 += dCurve.xPoints(array2, n3, array3[2]);
        }
        if ((n2 & 0x8) != 0x0) {
            n3 += dCurve.xPoints(array2, n3, array3[3]);
        }
        if ((n2 & 0x1) != 0x0) {
            n3 += dCurve.yPoints(array2, n3, array3[0]);
        }
        if ((n2 & 0x2) != 0x0) {
            n3 += dCurve.yPoints(array2, n3, array3[1]);
        }
        isort(array2, n3);
        return n3;
    }
    
    static void subdivide(final double[] array, final double[] array2, final double[] array3, final int n) {
        switch (n) {
            case 8: {
                subdivideCubic(array, array2, array3);
                return;
            }
            case 6: {
                subdivideQuad(array, array2, array3);
                return;
            }
            default: {
                throw new InternalError("Unsupported curve type");
            }
        }
    }
    
    static void isort(final double[] array, final int n) {
        for (int i = 1; i < n; ++i) {
            double n2;
            int n3;
            for (n2 = array[i], n3 = i - 1; n3 >= 0 && array[n3] > n2; --n3) {
                array[n3 + 1] = array[n3];
            }
            array[n3 + 1] = n2;
        }
    }
    
    static void subdivideCubic(final double[] array, final double[] array2, final double[] array3) {
        final double n = array[0];
        final double n2 = array[1];
        final double n3 = array[2];
        final double n4 = array[3];
        final double n5 = array[4];
        final double n6 = array[5];
        final double n7 = array[6];
        final double n8 = array[7];
        array2[0] = n;
        array2[1] = n2;
        array3[6] = n7;
        array3[7] = n8;
        final double n9 = (n + n3) / 2.0;
        final double n10 = (n2 + n4) / 2.0;
        final double n11 = (n7 + n5) / 2.0;
        final double n12 = (n8 + n6) / 2.0;
        final double n13 = (n3 + n5) / 2.0;
        final double n14 = (n4 + n6) / 2.0;
        final double n15 = (n9 + n13) / 2.0;
        final double n16 = (n10 + n14) / 2.0;
        final double n17 = (n11 + n13) / 2.0;
        final double n18 = (n12 + n14) / 2.0;
        final double n19 = (n15 + n17) / 2.0;
        final double n20 = (n16 + n18) / 2.0;
        array2[2] = n9;
        array2[3] = n10;
        array2[4] = n15;
        array2[5] = n16;
        array2[6] = n19;
        array2[7] = n20;
        array3[0] = n19;
        array3[1] = n20;
        array3[2] = n17;
        array3[3] = n18;
        array3[4] = n11;
        array3[5] = n12;
    }
    
    static void subdivideCubicAt(final double n, final double[] array, final int n2, final double[] array2, final int n3, final int n4) {
        final double n5 = array[n2];
        final double n6 = array[n2 + 1];
        final double n7 = array[n2 + 2];
        final double n8 = array[n2 + 3];
        final double n9 = array[n2 + 4];
        final double n10 = array[n2 + 5];
        final double n11 = array[n2 + 6];
        final double n12 = array[n2 + 7];
        array2[n3] = n5;
        array2[n3 + 1] = n6;
        array2[n4 + 6] = n11;
        array2[n4 + 7] = n12;
        final double n13 = n5 + n * (n7 - n5);
        final double n14 = n6 + n * (n8 - n6);
        final double n15 = n9 + n * (n11 - n9);
        final double n16 = n10 + n * (n12 - n10);
        final double n17 = n7 + n * (n9 - n7);
        final double n18 = n8 + n * (n10 - n8);
        final double n19 = n13 + n * (n17 - n13);
        final double n20 = n14 + n * (n18 - n14);
        final double n21 = n17 + n * (n15 - n17);
        final double n22 = n18 + n * (n16 - n18);
        final double n23 = n19 + n * (n21 - n19);
        final double n24 = n20 + n * (n22 - n20);
        array2[n3 + 2] = n13;
        array2[n3 + 3] = n14;
        array2[n3 + 4] = n19;
        array2[n3 + 5] = n20;
        array2[n3 + 6] = n23;
        array2[n3 + 7] = n24;
        array2[n4] = n23;
        array2[n4 + 1] = n24;
        array2[n4 + 2] = n21;
        array2[n4 + 3] = n22;
        array2[n4 + 4] = n15;
        array2[n4 + 5] = n16;
    }
    
    static void subdivideQuad(final double[] array, final double[] array2, final double[] array3) {
        final double n = array[0];
        final double n2 = array[1];
        final double n3 = array[2];
        final double n4 = array[3];
        final double n5 = array[4];
        final double n6 = array[5];
        array2[0] = n;
        array2[1] = n2;
        array3[4] = n5;
        array3[5] = n6;
        final double n7 = (n + n3) / 2.0;
        final double n8 = (n2 + n4) / 2.0;
        final double n9 = (n5 + n3) / 2.0;
        final double n10 = (n6 + n4) / 2.0;
        final double n11 = (n7 + n9) / 2.0;
        final double n12 = (n8 + n10) / 2.0;
        array2[2] = n7;
        array2[3] = n8;
        array2[4] = n11;
        array2[5] = n12;
        array3[0] = n11;
        array3[1] = n12;
        array3[2] = n9;
        array3[3] = n10;
    }
    
    static void subdivideQuadAt(final double n, final double[] array, final int n2, final double[] array2, final int n3, final int n4) {
        final double n5 = array[n2];
        final double n6 = array[n2 + 1];
        final double n7 = array[n2 + 2];
        final double n8 = array[n2 + 3];
        final double n9 = array[n2 + 4];
        final double n10 = array[n2 + 5];
        array2[n3] = n5;
        array2[n3 + 1] = n6;
        array2[n4 + 4] = n9;
        array2[n4 + 5] = n10;
        final double n11 = n5 + n * (n7 - n5);
        final double n12 = n6 + n * (n8 - n6);
        final double n13 = n7 + n * (n9 - n7);
        final double n14 = n8 + n * (n10 - n8);
        final double n15 = n11 + n * (n13 - n11);
        final double n16 = n12 + n * (n14 - n12);
        array2[n3 + 2] = n11;
        array2[n3 + 3] = n12;
        array2[n3 + 4] = n15;
        array2[n3 + 5] = n16;
        array2[n4] = n15;
        array2[n4 + 1] = n16;
        array2[n4 + 2] = n13;
        array2[n4 + 3] = n14;
    }
    
    static void subdivideLineAt(final double n, final double[] array, final int n2, final double[] array2, final int n3, final int n4) {
        final double n5 = array[n2];
        final double n6 = array[n2 + 1];
        final double n7 = array[n2 + 2];
        final double n8 = array[n2 + 3];
        array2[n3] = n5;
        array2[n3 + 1] = n6;
        array2[n4 + 2] = n7;
        array2[n4 + 3] = n8;
        final double n9 = n5 + n * (n7 - n5);
        final double n10 = n6 + n * (n8 - n6);
        array2[n3 + 2] = n9;
        array2[n3 + 3] = n10;
        array2[n4] = n9;
        array2[n4 + 1] = n10;
    }
    
    static void subdivideAt(final double n, final double[] array, final int n2, final double[] array2, final int n3, final int n4) {
        if (n4 == 8) {
            subdivideCubicAt(n, array, n2, array2, n3, n3 + n4);
        }
        else if (n4 == 4) {
            subdivideLineAt(n, array, n2, array2, n3, n3 + n4);
        }
        else {
            subdivideQuadAt(n, array, n2, array2, n3, n3 + n4);
        }
    }
    
    static int outcode(final double n, final double n2, final double[] array) {
        int n3;
        if (n2 < array[0]) {
            n3 = 1;
        }
        else if (n2 >= array[1]) {
            n3 = 2;
        }
        else {
            n3 = 0;
        }
        if (n < array[2]) {
            n3 |= 0x4;
        }
        else if (n >= array[3]) {
            n3 |= 0x8;
        }
        return n3;
    }
    
    static final class PolyStack
    {
        private static final byte TYPE_LINETO = 0;
        private static final byte TYPE_QUADTO = 1;
        private static final byte TYPE_CUBICTO = 2;
        private static final int INITIAL_CURVES_COUNT;
        private static final int INITIAL_TYPES_COUNT;
        double[] curves;
        int end;
        byte[] curveTypes;
        int numCurves;
        final DoubleArrayCache.Reference curves_ref;
        final ByteArrayCache.Reference curveTypes_ref;
        int curveTypesUseMark;
        int curvesUseMark;
        private final StatLong stat_polystack_types;
        private final StatLong stat_polystack_curves;
        private final Histogram hist_polystack_curves;
        private final StatLong stat_array_polystack_curves;
        private final StatLong stat_array_polystack_curveTypes;
        
        PolyStack(final DRendererContext dRendererContext) {
            this(dRendererContext, null, null, null, null, null);
        }
        
        PolyStack(final DRendererContext dRendererContext, final StatLong stat_polystack_types, final StatLong stat_polystack_curves, final Histogram hist_polystack_curves, final StatLong stat_array_polystack_curves, final StatLong stat_array_polystack_curveTypes) {
            this.curves_ref = dRendererContext.newDirtyDoubleArrayRef(PolyStack.INITIAL_CURVES_COUNT);
            this.curves = this.curves_ref.initial;
            this.curveTypes_ref = dRendererContext.newDirtyByteArrayRef(PolyStack.INITIAL_TYPES_COUNT);
            this.curveTypes = this.curveTypes_ref.initial;
            this.numCurves = 0;
            this.end = 0;
            if (MarlinConst.DO_STATS) {
                this.curveTypesUseMark = 0;
                this.curvesUseMark = 0;
            }
            this.stat_polystack_types = stat_polystack_types;
            this.stat_polystack_curves = stat_polystack_curves;
            this.hist_polystack_curves = hist_polystack_curves;
            this.stat_array_polystack_curves = stat_array_polystack_curves;
            this.stat_array_polystack_curveTypes = stat_array_polystack_curveTypes;
        }
        
        void dispose() {
            this.end = 0;
            this.numCurves = 0;
            if (MarlinConst.DO_STATS) {
                this.stat_polystack_types.add(this.curveTypesUseMark);
                this.stat_polystack_curves.add(this.curvesUseMark);
                this.hist_polystack_curves.add(this.curvesUseMark);
                this.curveTypesUseMark = 0;
                this.curvesUseMark = 0;
            }
            this.curves = this.curves_ref.putArray(this.curves);
            this.curveTypes = this.curveTypes_ref.putArray(this.curveTypes);
        }
        
        private void ensureSpace(final int n) {
            if (this.curves.length - this.end < n) {
                if (MarlinConst.DO_STATS) {
                    this.stat_array_polystack_curves.add(this.end + n);
                }
                this.curves = this.curves_ref.widenArray(this.curves, this.end, this.end + n);
            }
            if (this.curveTypes.length <= this.numCurves) {
                if (MarlinConst.DO_STATS) {
                    this.stat_array_polystack_curveTypes.add(this.numCurves + 1);
                }
                this.curveTypes = this.curveTypes_ref.widenArray(this.curveTypes, this.numCurves, this.numCurves + 1);
            }
        }
        
        void pushCubic(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.ensureSpace(6);
            this.curveTypes[this.numCurves++] = 2;
            final double[] curves = this.curves;
            int end = this.end;
            curves[end++] = n5;
            curves[end++] = n6;
            curves[end++] = n3;
            curves[end++] = n4;
            curves[end++] = n;
            curves[end++] = n2;
            this.end = end;
        }
        
        void pushQuad(final double n, final double n2, final double n3, final double n4) {
            this.ensureSpace(4);
            this.curveTypes[this.numCurves++] = 1;
            final double[] curves = this.curves;
            int end = this.end;
            curves[end++] = n3;
            curves[end++] = n4;
            curves[end++] = n;
            curves[end++] = n2;
            this.end = end;
        }
        
        void pushLine(final double n, final double n2) {
            this.ensureSpace(2);
            this.curveTypes[this.numCurves++] = 0;
            this.curves[this.end++] = n;
            this.curves[this.end++] = n2;
        }
        
        void pullAll(final DPathConsumer2D dPathConsumer2D) {
            final int numCurves = this.numCurves;
            if (numCurves == 0) {
                return;
            }
            if (MarlinConst.DO_STATS) {
                if (this.numCurves > this.curveTypesUseMark) {
                    this.curveTypesUseMark = this.numCurves;
                }
                if (this.end > this.curvesUseMark) {
                    this.curvesUseMark = this.end;
                }
            }
            final byte[] curveTypes = this.curveTypes;
            final double[] curves = this.curves;
            int n = 0;
            for (int i = 0; i < numCurves; ++i) {
                switch (curveTypes[i]) {
                    case 0: {
                        dPathConsumer2D.lineTo(curves[n], curves[n + 1]);
                        n += 2;
                        break;
                    }
                    case 1: {
                        dPathConsumer2D.quadTo(curves[n], curves[n + 1], curves[n + 2], curves[n + 3]);
                        n += 4;
                        break;
                    }
                    case 2: {
                        dPathConsumer2D.curveTo(curves[n], curves[n + 1], curves[n + 2], curves[n + 3], curves[n + 4], curves[n + 5]);
                        n += 6;
                        break;
                    }
                }
            }
            this.numCurves = 0;
            this.end = 0;
        }
        
        void popAll(final DPathConsumer2D dPathConsumer2D) {
            int i = this.numCurves;
            if (i == 0) {
                return;
            }
            if (MarlinConst.DO_STATS) {
                if (this.numCurves > this.curveTypesUseMark) {
                    this.curveTypesUseMark = this.numCurves;
                }
                if (this.end > this.curvesUseMark) {
                    this.curvesUseMark = this.end;
                }
            }
            final byte[] curveTypes = this.curveTypes;
            final double[] curves = this.curves;
            int end = this.end;
            while (i != 0) {
                switch (curveTypes[--i]) {
                    case 0: {
                        end -= 2;
                        dPathConsumer2D.lineTo(curves[end], curves[end + 1]);
                        continue;
                    }
                    case 1: {
                        end -= 4;
                        dPathConsumer2D.quadTo(curves[end], curves[end + 1], curves[end + 2], curves[end + 3]);
                        continue;
                    }
                    case 2: {
                        end -= 6;
                        dPathConsumer2D.curveTo(curves[end], curves[end + 1], curves[end + 2], curves[end + 3], curves[end + 4], curves[end + 5]);
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            this.numCurves = 0;
            this.end = 0;
        }
        
        @Override
        public String toString() {
            String s = "";
            int i = this.numCurves;
            int end = this.end;
            while (i != 0) {
                int n = 0;
                switch (this.curveTypes[--i]) {
                    case 0: {
                        n = 2;
                        s += "line: ";
                        break;
                    }
                    case 1: {
                        n = 4;
                        s += "quad: ";
                        break;
                    }
                    case 2: {
                        n = 6;
                        s += "cubic: ";
                        break;
                    }
                    default: {
                        n = 0;
                        break;
                    }
                }
                end -= n;
                s = s + Arrays.toString(Arrays.copyOfRange(this.curves, end, end + n)) + "\n";
            }
            return s;
        }
        
        static {
            INITIAL_CURVES_COUNT = MarlinConst.INITIAL_EDGES_COUNT << 1;
            INITIAL_TYPES_COUNT = MarlinConst.INITIAL_EDGES_COUNT;
        }
    }
    
    static final class IndexStack
    {
        private static final int INITIAL_COUNT;
        private int end;
        private int[] indices;
        private final IntArrayCache.Reference indices_ref;
        private int indicesUseMark;
        private final StatLong stat_idxstack_indices;
        private final Histogram hist_idxstack_indices;
        private final StatLong stat_array_idxstack_indices;
        
        IndexStack(final DRendererContext dRendererContext) {
            this(dRendererContext, null, null, null);
        }
        
        IndexStack(final DRendererContext dRendererContext, final StatLong stat_idxstack_indices, final Histogram hist_idxstack_indices, final StatLong stat_array_idxstack_indices) {
            this.indices_ref = dRendererContext.newDirtyIntArrayRef(IndexStack.INITIAL_COUNT);
            this.indices = this.indices_ref.initial;
            this.end = 0;
            if (MarlinConst.DO_STATS) {
                this.indicesUseMark = 0;
            }
            this.stat_idxstack_indices = stat_idxstack_indices;
            this.hist_idxstack_indices = hist_idxstack_indices;
            this.stat_array_idxstack_indices = stat_array_idxstack_indices;
        }
        
        void dispose() {
            this.end = 0;
            if (MarlinConst.DO_STATS) {
                this.stat_idxstack_indices.add(this.indicesUseMark);
                this.hist_idxstack_indices.add(this.indicesUseMark);
                this.indicesUseMark = 0;
            }
            this.indices = this.indices_ref.putArray(this.indices);
        }
        
        boolean isEmpty() {
            return this.end == 0;
        }
        
        void reset() {
            this.end = 0;
        }
        
        void push(final int n) {
            int[] indices = this.indices;
            final int end = this.end;
            if (end != 0 && indices[end - 1] == n) {
                --this.end;
                return;
            }
            if (indices.length <= end) {
                if (MarlinConst.DO_STATS) {
                    this.stat_array_idxstack_indices.add(end + 1);
                }
                indices = (this.indices = this.indices_ref.widenArray(indices, end, end + 1));
            }
            indices[this.end++] = n;
            if (MarlinConst.DO_STATS && this.end > this.indicesUseMark) {
                this.indicesUseMark = this.end;
            }
        }
        
        void pullAll(final double[] array, final DPathConsumer2D dPathConsumer2D) {
            final int end = this.end;
            if (end == 0) {
                return;
            }
            final int[] indices = this.indices;
            for (int i = 0; i < end; ++i) {
                final int n = indices[i] << 1;
                dPathConsumer2D.lineTo(array[n], array[n + 1]);
            }
            this.end = 0;
        }
        
        static {
            INITIAL_COUNT = MarlinConst.INITIAL_EDGES_COUNT >> 2;
        }
    }
}
