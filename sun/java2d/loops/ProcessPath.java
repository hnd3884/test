package sun.java2d.loops;

import java.util.Vector;
import java.util.List;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Path2D;

public class ProcessPath
{
    public static final int PH_MODE_DRAW_CLIP = 0;
    public static final int PH_MODE_FILL_CLIP = 1;
    public static EndSubPathHandler noopEndSubPathHandler;
    private static final float UPPER_BND = 8.5070587E37f;
    private static final float LOWER_BND = -8.5070587E37f;
    private static final int FWD_PREC = 7;
    private static final int MDP_PREC = 10;
    private static final int MDP_MULT = 1024;
    private static final int MDP_HALF_MULT = 512;
    private static final int UPPER_OUT_BND = 1048576;
    private static final int LOWER_OUT_BND = -1048576;
    private static final float CALC_UBND = 1048576.0f;
    private static final float CALC_LBND = -1048576.0f;
    public static final int EPSFX = 1;
    public static final float EPSF = 9.765625E-4f;
    private static final int MDP_W_MASK = -1024;
    private static final int MDP_F_MASK = 1023;
    private static final int MAX_CUB_SIZE = 256;
    private static final int MAX_QUAD_SIZE = 1024;
    private static final int DF_CUB_STEPS = 3;
    private static final int DF_QUAD_STEPS = 2;
    private static final int DF_CUB_SHIFT = 6;
    private static final int DF_QUAD_SHIFT = 1;
    private static final int DF_CUB_COUNT = 8;
    private static final int DF_QUAD_COUNT = 4;
    private static final int DF_CUB_DEC_BND = 262144;
    private static final int DF_CUB_INC_BND = 32768;
    private static final int DF_QUAD_DEC_BND = 8192;
    private static final int DF_QUAD_INC_BND = 1024;
    private static final int CUB_A_SHIFT = 7;
    private static final int CUB_B_SHIFT = 11;
    private static final int CUB_C_SHIFT = 13;
    private static final int CUB_A_MDP_MULT = 128;
    private static final int CUB_B_MDP_MULT = 2048;
    private static final int CUB_C_MDP_MULT = 8192;
    private static final int QUAD_A_SHIFT = 7;
    private static final int QUAD_B_SHIFT = 9;
    private static final int QUAD_A_MDP_MULT = 128;
    private static final int QUAD_B_MDP_MULT = 512;
    private static final int CRES_MIN_CLIPPED = 0;
    private static final int CRES_MAX_CLIPPED = 1;
    private static final int CRES_NOT_CLIPPED = 3;
    private static final int CRES_INVISIBLE = 4;
    private static final int DF_MAX_POINT = 256;
    
    public static boolean fillPath(final DrawHandler drawHandler, final Path2D.Float float1, final int n, final int n2) {
        final FillProcessHandler fillProcessHandler = new FillProcessHandler(drawHandler);
        if (!doProcessPath(fillProcessHandler, float1, (float)n, (float)n2)) {
            return false;
        }
        FillPolygon(fillProcessHandler, float1.getWindingRule());
        return true;
    }
    
    public static boolean drawPath(final DrawHandler drawHandler, final EndSubPathHandler endSubPathHandler, final Path2D.Float float1, final int n, final int n2) {
        return doProcessPath(new DrawProcessHandler(drawHandler, endSubPathHandler), float1, (float)n, (float)n2);
    }
    
    public static boolean drawPath(final DrawHandler drawHandler, final Path2D.Float float1, final int n, final int n2) {
        return doProcessPath(new DrawProcessHandler(drawHandler, ProcessPath.noopEndSubPathHandler), float1, (float)n, (float)n2);
    }
    
    private static float CLIP(final float n, final float n2, final float n3, final float n4, final double n5) {
        return (float)(n2 + (n5 - n) * (n4 - n2) / (n3 - n));
    }
    
    private static int CLIP(final int n, final int n2, final int n3, final int n4, final double n5) {
        return (int)(n2 + (n5 - n) * (n4 - n2) / (n3 - n));
    }
    
    private static boolean IS_CLIPPED(final int n) {
        return n == 0 || n == 1;
    }
    
    private static int TESTANDCLIP(final float n, final float n2, final float[] array, final int n3, final int n4, final int n5, final int n6) {
        int n7 = 3;
        if (array[n3] < n || array[n3] > n2) {
            double n8;
            if (array[n3] < n) {
                if (array[n5] < n) {
                    return 4;
                }
                n7 = 0;
                n8 = n;
            }
            else {
                if (array[n5] > n2) {
                    return 4;
                }
                n7 = 1;
                n8 = n2;
            }
            array[n4] = CLIP(array[n3], array[n4], array[n5], array[n6], n8);
            array[n3] = (float)n8;
        }
        return n7;
    }
    
    private static int TESTANDCLIP(final int n, final int n2, final int[] array, final int n3, final int n4, final int n5, final int n6) {
        int n7 = 3;
        if (array[n3] < n || array[n3] > n2) {
            double n8;
            if (array[n3] < n) {
                if (array[n5] < n) {
                    return 4;
                }
                n7 = 0;
                n8 = n;
            }
            else {
                if (array[n5] > n2) {
                    return 4;
                }
                n7 = 1;
                n8 = n2;
            }
            array[n4] = CLIP(array[n3], array[n4], array[n5], array[n6], n8);
            array[n3] = (int)n8;
        }
        return n7;
    }
    
    private static int CLIPCLAMP(final float n, final float n2, final float[] array, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        array[n7] = array[n3];
        array[n8] = array[n4];
        int testandclip = TESTANDCLIP(n, n2, array, n3, n4, n5, n6);
        if (testandclip == 0) {
            array[n7] = array[n3];
        }
        else if (testandclip == 1) {
            array[n7] = array[n3];
            testandclip = 1;
        }
        else if (testandclip == 4) {
            if (array[n3] > n2) {
                testandclip = 4;
            }
            else {
                array[n5] = (array[n3] = n);
                testandclip = 3;
            }
        }
        return testandclip;
    }
    
    private static int CLIPCLAMP(final int n, final int n2, final int[] array, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        array[n7] = array[n3];
        array[n8] = array[n4];
        int testandclip = TESTANDCLIP(n, n2, array, n3, n4, n5, n6);
        if (testandclip == 0) {
            array[n7] = array[n3];
        }
        else if (testandclip == 1) {
            array[n7] = array[n3];
            testandclip = 1;
        }
        else if (testandclip == 4) {
            if (array[n3] > n2) {
                testandclip = 4;
            }
            else {
                array[n5] = (array[n3] = n);
                testandclip = 3;
            }
        }
        return testandclip;
    }
    
    private static void DrawMonotonicQuad(final ProcessHandler processHandler, final float[] array, final boolean b, final int[] array2) {
        final int n = (int)(array[0] * 1024.0f);
        final int n2 = (int)(array[1] * 1024.0f);
        final int n3 = (int)(array[4] * 1024.0f);
        final int n4 = (int)(array[5] * 1024.0f);
        int n5 = (n & 0x3FF) << 1;
        int n6 = (n2 & 0x3FF) << 1;
        int n7 = 4;
        int n8 = 1;
        final int n9 = (int)((array[0] - 2.0f * array[2] + array[4]) * 128.0f);
        final int n10 = (int)((array[1] - 2.0f * array[3] + array[5]) * 128.0f);
        final int n11 = (int)((-2.0f * array[0] + 2.0f * array[2]) * 512.0f);
        final int n12 = (int)((-2.0f * array[1] + 2.0f * array[3]) * 512.0f);
        final int n13 = 2 * n9;
        final int n14 = 2 * n10;
        int n15 = n9 + n11;
        int n16 = n10 + n12;
        int n17 = n;
        int n18 = n2;
        int i = Math.max(Math.abs(n13), Math.abs(n14));
        final int n19 = n3 - n;
        final int n20 = n4 - n2;
        final int n21 = n & 0xFFFFFC00;
        final int n22 = n2 & 0xFFFFFC00;
        while (i > 8192) {
            n15 = (n15 << 1) - n9;
            n16 = (n16 << 1) - n10;
            n7 <<= 1;
            i >>= 2;
            n5 <<= 2;
            n6 <<= 2;
            n8 += 2;
        }
        while (n7-- > 1) {
            n5 += n15;
            n6 += n16;
            n15 += n13;
            n16 += n14;
            final int n23 = n17;
            final int n24 = n18;
            n17 = n21 + (n5 >> n8);
            n18 = n22 + (n6 >> n8);
            if ((n3 - n17 ^ n19) < 0) {
                n17 = n3;
            }
            if ((n4 - n18 ^ n20) < 0) {
                n18 = n4;
            }
            processHandler.processFixedLine(n23, n24, n17, n18, array2, b, false);
        }
        processHandler.processFixedLine(n17, n18, n3, n4, array2, b, false);
    }
    
    private static void ProcessMonotonicQuad(final ProcessHandler processHandler, final float[] array, final int[] array2) {
        final float[] array3 = new float[6];
        float n2;
        float n = n2 = array[0];
        float n4;
        float n3 = n4 = array[1];
        for (int i = 2; i < 6; i += 2) {
            n2 = ((n2 > array[i]) ? array[i] : n2);
            n = ((n < array[i]) ? array[i] : n);
            n4 = ((n4 > array[i + 1]) ? array[i + 1] : n4);
            n3 = ((n3 < array[i + 1]) ? array[i + 1] : n3);
        }
        if (processHandler.clipMode == 0) {
            if (processHandler.dhnd.xMaxf < n2 || processHandler.dhnd.xMinf > n || processHandler.dhnd.yMaxf < n4 || processHandler.dhnd.yMinf > n3) {
                return;
            }
        }
        else {
            if (processHandler.dhnd.yMaxf < n4 || processHandler.dhnd.yMinf > n3 || processHandler.dhnd.xMaxf < n2) {
                return;
            }
            if (processHandler.dhnd.xMinf > n) {
                final int n5 = 0;
                final int n6 = 2;
                final int n7 = 4;
                final float xMinf = processHandler.dhnd.xMinf;
                array[n7] = xMinf;
                array[n5] = (array[n6] = xMinf);
            }
        }
        if (n - n2 > 1024.0f || n3 - n4 > 1024.0f) {
            array3[4] = array[4];
            array3[5] = array[5];
            array3[2] = (array[2] + array[4]) / 2.0f;
            array3[3] = (array[3] + array[5]) / 2.0f;
            array[2] = (array[0] + array[2]) / 2.0f;
            array[3] = (array[1] + array[3]) / 2.0f;
            array[4] = (array3[0] = (array[2] + array3[2]) / 2.0f);
            array[5] = (array3[1] = (array[3] + array3[3]) / 2.0f);
            ProcessMonotonicQuad(processHandler, array, array2);
            ProcessMonotonicQuad(processHandler, array3, array2);
        }
        else {
            DrawMonotonicQuad(processHandler, array, processHandler.dhnd.xMinf >= n2 || processHandler.dhnd.xMaxf <= n || processHandler.dhnd.yMinf >= n4 || processHandler.dhnd.yMaxf <= n3, array2);
        }
    }
    
    private static void ProcessQuad(final ProcessHandler processHandler, final float[] array, final int[] array2) {
        final double[] array3 = new double[2];
        int n = 0;
        if ((array[0] > array[2] || array[2] > array[4]) && (array[0] < array[2] || array[2] < array[4])) {
            final double n2 = array[0] - 2.0f * array[2] + array[4];
            if (n2 != 0.0) {
                final double n3 = (array[0] - array[2]) / n2;
                if (n3 < 1.0 && n3 > 0.0) {
                    array3[n++] = n3;
                }
            }
        }
        if ((array[1] > array[3] || array[3] > array[5]) && (array[1] < array[3] || array[3] < array[5])) {
            final double n4 = array[1] - 2.0f * array[3] + array[5];
            if (n4 != 0.0) {
                final double n5 = (array[1] - array[3]) / n4;
                if (n5 < 1.0 && n5 > 0.0) {
                    if (n > 0) {
                        if (array3[0] > n5) {
                            array3[n++] = array3[0];
                            array3[0] = n5;
                        }
                        else if (array3[0] < n5) {
                            array3[n++] = n5;
                        }
                    }
                    else {
                        array3[n++] = n5;
                    }
                }
            }
        }
        switch (n) {
            case 1: {
                ProcessFirstMonotonicPartOfQuad(processHandler, array, array2, (float)array3[0]);
                break;
            }
            case 2: {
                ProcessFirstMonotonicPartOfQuad(processHandler, array, array2, (float)array3[0]);
                final double n6 = array3[1] - array3[0];
                if (n6 > 0.0) {
                    ProcessFirstMonotonicPartOfQuad(processHandler, array, array2, (float)(n6 / (1.0 - array3[0])));
                    break;
                }
                break;
            }
        }
        ProcessMonotonicQuad(processHandler, array, array2);
    }
    
    private static void ProcessFirstMonotonicPartOfQuad(final ProcessHandler processHandler, final float[] array, final int[] array2, final float n) {
        final float[] array3 = { array[0], array[1], array[0] + n * (array[2] - array[0]), array[1] + n * (array[3] - array[1]), 0.0f, 0.0f };
        array[2] += n * (array[4] - array[2]);
        array[3] += n * (array[5] - array[3]);
        array[0] = (array3[4] = array3[2] + n * (array[2] - array3[2]));
        array[1] = (array3[5] = array3[3] + n * (array[3] - array3[3]));
        ProcessMonotonicQuad(processHandler, array3, array2);
    }
    
    private static void DrawMonotonicCubic(final ProcessHandler processHandler, final float[] array, final boolean b, final int[] array2) {
        final int n = (int)(array[0] * 1024.0f);
        final int n2 = (int)(array[1] * 1024.0f);
        final int n3 = (int)(array[6] * 1024.0f);
        final int n4 = (int)(array[7] * 1024.0f);
        int n5 = (n & 0x3FF) << 6;
        int n6 = (n2 & 0x3FF) << 6;
        int n7 = 32768;
        int n8 = 262144;
        int i = 8;
        int n9 = 6;
        final int n10 = (int)((-array[0] + 3.0f * array[2] - 3.0f * array[4] + array[6]) * 128.0f);
        final int n11 = (int)((-array[1] + 3.0f * array[3] - 3.0f * array[5] + array[7]) * 128.0f);
        final int n12 = (int)((3.0f * array[0] - 6.0f * array[2] + 3.0f * array[4]) * 2048.0f);
        final int n13 = (int)((3.0f * array[1] - 6.0f * array[3] + 3.0f * array[5]) * 2048.0f);
        final int n14 = (int)((-3.0f * array[0] + 3.0f * array[2]) * 8192.0f);
        final int n15 = (int)((-3.0f * array[1] + 3.0f * array[3]) * 8192.0f);
        final int n16 = 6 * n10;
        final int n17 = 6 * n11;
        int n18 = n16 + n12;
        int n19 = n17 + n13;
        int n20 = n10 + (n12 >> 1) + n14;
        int n21 = n11 + (n13 >> 1) + n15;
        int n22 = n;
        int n23 = n2;
        final int n24 = n & 0xFFFFFC00;
        final int n25 = n2 & 0xFFFFFC00;
        final int n26 = n3 - n;
        final int n27 = n4 - n2;
        while (i > 0) {
            while (Math.abs(n18) > n8 || Math.abs(n19) > n8) {
                n18 = (n18 << 1) - n16;
                n19 = (n19 << 1) - n17;
                n20 = (n20 << 2) - (n18 >> 1);
                n21 = (n21 << 2) - (n19 >> 1);
                i <<= 1;
                n8 <<= 3;
                n7 <<= 3;
                n5 <<= 3;
                n6 <<= 3;
                n9 += 3;
            }
            while ((i & 0x1) == 0x0 && n9 > 6 && Math.abs(n20) <= n7 && Math.abs(n21) <= n7) {
                n20 = (n20 >> 2) + (n18 >> 3);
                n21 = (n21 >> 2) + (n19 >> 3);
                n18 = n18 + n16 >> 1;
                n19 = n19 + n17 >> 1;
                i >>= 1;
                n8 >>= 3;
                n7 >>= 3;
                n5 >>= 3;
                n6 >>= 3;
                n9 -= 3;
            }
            if (--i > 0) {
                n5 += n20;
                n6 += n21;
                n20 += n18;
                n21 += n19;
                n18 += n16;
                n19 += n17;
                final int n28 = n22;
                final int n29 = n23;
                n22 = n24 + (n5 >> n9);
                n23 = n25 + (n6 >> n9);
                if ((n3 - n22 ^ n26) < 0) {
                    n22 = n3;
                }
                if ((n4 - n23 ^ n27) < 0) {
                    n23 = n4;
                }
                processHandler.processFixedLine(n28, n29, n22, n23, array2, b, false);
            }
            else {
                processHandler.processFixedLine(n22, n23, n3, n4, array2, b, false);
            }
        }
    }
    
    private static void ProcessMonotonicCubic(final ProcessHandler processHandler, final float[] array, final int[] array2) {
        final float[] array3 = new float[8];
        float n2;
        float n = n2 = array[0];
        float n4;
        float n3 = n4 = array[1];
        for (int i = 2; i < 8; i += 2) {
            n2 = ((n2 > array[i]) ? array[i] : n2);
            n = ((n < array[i]) ? array[i] : n);
            n4 = ((n4 > array[i + 1]) ? array[i + 1] : n4);
            n3 = ((n3 < array[i + 1]) ? array[i + 1] : n3);
        }
        if (processHandler.clipMode == 0) {
            if (processHandler.dhnd.xMaxf < n2 || processHandler.dhnd.xMinf > n || processHandler.dhnd.yMaxf < n4 || processHandler.dhnd.yMinf > n3) {
                return;
            }
        }
        else {
            if (processHandler.dhnd.yMaxf < n4 || processHandler.dhnd.yMinf > n3 || processHandler.dhnd.xMaxf < n2) {
                return;
            }
            if (processHandler.dhnd.xMinf > n) {
                final int n5 = 0;
                final int n6 = 2;
                final int n7 = 4;
                final int n8 = 6;
                final float xMinf = processHandler.dhnd.xMinf;
                array[n7] = (array[n8] = xMinf);
                array[n5] = (array[n6] = xMinf);
            }
        }
        if (n - n2 > 256.0f || n3 - n4 > 256.0f) {
            array3[6] = array[6];
            array3[7] = array[7];
            array3[4] = (array[4] + array[6]) / 2.0f;
            array3[5] = (array[5] + array[7]) / 2.0f;
            final float n9 = (array[2] + array[4]) / 2.0f;
            final float n10 = (array[3] + array[5]) / 2.0f;
            array3[2] = (n9 + array3[4]) / 2.0f;
            array3[3] = (n10 + array3[5]) / 2.0f;
            array[2] = (array[0] + array[2]) / 2.0f;
            array[3] = (array[1] + array[3]) / 2.0f;
            array[4] = (array[2] + n9) / 2.0f;
            array[5] = (array[3] + n10) / 2.0f;
            array[6] = (array3[0] = (array[4] + array3[2]) / 2.0f);
            array[7] = (array3[1] = (array[5] + array3[3]) / 2.0f);
            ProcessMonotonicCubic(processHandler, array, array2);
            ProcessMonotonicCubic(processHandler, array3, array2);
        }
        else {
            DrawMonotonicCubic(processHandler, array, processHandler.dhnd.xMinf > n2 || processHandler.dhnd.xMaxf < n || processHandler.dhnd.yMinf > n4 || processHandler.dhnd.yMaxf < n3, array2);
        }
    }
    
    private static void ProcessCubic(final ProcessHandler processHandler, final float[] array, final int[] array2) {
        final double[] array3 = new double[4];
        final double[] array4 = new double[3];
        final double[] array5 = new double[2];
        int n = 0;
        if ((array[0] > array[2] || array[2] > array[4] || array[4] > array[6]) && (array[0] < array[2] || array[2] < array[4] || array[4] < array[6])) {
            array4[2] = -array[0] + 3.0f * array[2] - 3.0f * array[4] + array[6];
            array4[1] = 2.0f * (array[0] - 2.0f * array[2] + array[4]);
            array4[0] = -array[0] + array[2];
            for (int solveQuadratic = QuadCurve2D.solveQuadratic(array4, array5), i = 0; i < solveQuadratic; ++i) {
                if (array5[i] > 0.0 && array5[i] < 1.0) {
                    array3[n++] = array5[i];
                }
            }
        }
        if ((array[1] > array[3] || array[3] > array[5] || array[5] > array[7]) && (array[1] < array[3] || array[3] < array[5] || array[5] < array[7])) {
            array4[2] = -array[1] + 3.0f * array[3] - 3.0f * array[5] + array[7];
            array4[1] = 2.0f * (array[1] - 2.0f * array[3] + array[5]);
            array4[0] = -array[1] + array[3];
            for (int solveQuadratic2 = QuadCurve2D.solveQuadratic(array4, array5), j = 0; j < solveQuadratic2; ++j) {
                if (array5[j] > 0.0 && array5[j] < 1.0) {
                    array3[n++] = array5[j];
                }
            }
        }
        if (n > 0) {
            Arrays.sort(array3, 0, n);
            ProcessFirstMonotonicPartOfCubic(processHandler, array, array2, (float)array3[0]);
            for (int k = 1; k < n; ++k) {
                final double n2 = array3[k] - array3[k - 1];
                if (n2 > 0.0) {
                    ProcessFirstMonotonicPartOfCubic(processHandler, array, array2, (float)(n2 / (1.0 - array3[k - 1])));
                }
            }
        }
        ProcessMonotonicCubic(processHandler, array, array2);
    }
    
    private static void ProcessFirstMonotonicPartOfCubic(final ProcessHandler processHandler, final float[] array, final int[] array2, final float n) {
        final float[] array3 = new float[8];
        array3[0] = array[0];
        array3[1] = array[1];
        final float n2 = array[2] + n * (array[4] - array[2]);
        final float n3 = array[3] + n * (array[5] - array[3]);
        array3[2] = array[0] + n * (array[2] - array[0]);
        array3[3] = array[1] + n * (array[3] - array[1]);
        array3[4] = array3[2] + n * (n2 - array3[2]);
        array3[5] = array3[3] + n * (n3 - array3[3]);
        array[4] += n * (array[6] - array[4]);
        array[5] += n * (array[7] - array[5]);
        array[2] = n2 + n * (array[4] - n2);
        array[3] = n3 + n * (array[5] - n3);
        array[0] = (array3[6] = array3[4] + n * (array[2] - array3[4]));
        array[1] = (array3[7] = array3[5] + n * (array[3] - array3[5]));
        ProcessMonotonicCubic(processHandler, array3, array2);
    }
    
    private static void ProcessLine(final ProcessHandler processHandler, final float n, final float n2, final float n3, final float n4, final int[] array) {
        final float[] array2 = { n, n2, n3, n4, 0.0f, 0.0f };
        final float xMinf = processHandler.dhnd.xMinf;
        final float yMinf = processHandler.dhnd.yMinf;
        final float xMaxf = processHandler.dhnd.xMaxf;
        final float yMaxf = processHandler.dhnd.yMaxf;
        final int testandclip = TESTANDCLIP(yMinf, yMaxf, array2, 1, 0, 3, 2);
        if (testandclip == 4) {
            return;
        }
        final boolean is_CLIPPED = IS_CLIPPED(testandclip);
        final int testandclip2 = TESTANDCLIP(yMinf, yMaxf, array2, 3, 2, 1, 0);
        if (testandclip2 == 4) {
            return;
        }
        final boolean is_CLIPPED2 = IS_CLIPPED(testandclip2);
        final boolean b = is_CLIPPED || is_CLIPPED2;
        if (processHandler.clipMode == 0) {
            final int testandclip3 = TESTANDCLIP(xMinf, xMaxf, array2, 0, 1, 2, 3);
            if (testandclip3 == 4) {
                return;
            }
            final boolean b2 = b || IS_CLIPPED(testandclip3);
            final int testandclip4 = TESTANDCLIP(xMinf, xMaxf, array2, 2, 3, 0, 1);
            if (testandclip4 == 4) {
                return;
            }
            final boolean b3 = is_CLIPPED2 || IS_CLIPPED(testandclip4);
            processHandler.processFixedLine((int)(array2[0] * 1024.0f), (int)(array2[1] * 1024.0f), (int)(array2[2] * 1024.0f), (int)(array2[3] * 1024.0f), array, b2 || b3, b3);
        }
        else {
            final int clipclamp = CLIPCLAMP(xMinf, xMaxf, array2, 0, 1, 2, 3, 4, 5);
            final int n5 = (int)(array2[0] * 1024.0f);
            final int n6 = (int)(array2[1] * 1024.0f);
            if (clipclamp == 0) {
                processHandler.processFixedLine((int)(array2[4] * 1024.0f), (int)(array2[5] * 1024.0f), n5, n6, array, false, is_CLIPPED2);
            }
            else if (clipclamp == 4) {
                return;
            }
            final int clipclamp2 = CLIPCLAMP(xMinf, xMaxf, array2, 2, 3, 0, 1, 4, 5);
            final boolean b4 = is_CLIPPED2 || clipclamp2 == 1;
            final int n7 = (int)(array2[2] * 1024.0f);
            final int n8 = (int)(array2[3] * 1024.0f);
            processHandler.processFixedLine(n5, n6, n7, n8, array, false, b4);
            if (clipclamp2 == 0) {
                processHandler.processFixedLine(n7, n8, (int)(array2[4] * 1024.0f), (int)(array2[5] * 1024.0f), array, false, b4);
            }
        }
    }
    
    private static boolean doProcessPath(final ProcessHandler processHandler, final Path2D.Float float1, float n, float n2) {
        final float[] array = new float[8];
        final float[] array2 = new float[8];
        final float[] array3 = { 0.0f, 0.0f };
        final float[] array4 = new float[2];
        final int[] array5 = new int[5];
        boolean b = false;
        int n3 = 0;
        array5[0] = 0;
        processHandler.dhnd.adjustBounds(-1048576, -1048576, 1048576, 1048576);
        if (processHandler.dhnd.strokeControl == 2) {
            array3[1] = (array3[0] = -0.5f);
            n -= 0.5;
            n2 -= 0.5;
        }
        final PathIterator pathIterator = float1.getPathIterator(null);
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (b && n3 == 0) {
                        if (processHandler.clipMode == 1 && (array2[0] != array3[0] || array2[1] != array3[1])) {
                            ProcessLine(processHandler, array2[0], array2[1], array3[0], array3[1], array5);
                        }
                        processHandler.processEndSubPath();
                    }
                    array2[0] = array[0] + n;
                    array2[1] = array[1] + n2;
                    if (array2[0] < 8.5070587E37f && array2[0] > -8.5070587E37f && array2[1] < 8.5070587E37f && array2[1] > -8.5070587E37f) {
                        b = true;
                        n3 = 0;
                        array3[0] = array2[0];
                        array3[1] = array2[1];
                    }
                    else {
                        n3 = 1;
                    }
                    array5[0] = 0;
                    break;
                }
                case 1: {
                    final float[] array6 = array2;
                    final int n4 = 2;
                    final float n5 = array[0] + n;
                    array6[n4] = n5;
                    final float n6 = n5;
                    final float[] array7 = array2;
                    final int n7 = 3;
                    final float n8 = array[1] + n2;
                    array7[n7] = n8;
                    final float n9 = n8;
                    if (n6 >= 8.5070587E37f || n6 <= -8.5070587E37f || n9 >= 8.5070587E37f || n9 <= -8.5070587E37f) {
                        break;
                    }
                    if (n3 != 0) {
                        array2[0] = (array3[0] = n6);
                        array2[1] = (array3[1] = n9);
                        b = true;
                        n3 = 0;
                        break;
                    }
                    ProcessLine(processHandler, array2[0], array2[1], array2[2], array2[3], array5);
                    array2[0] = n6;
                    array2[1] = n9;
                    break;
                }
                case 2: {
                    array2[2] = array[0] + n;
                    array2[3] = array[1] + n2;
                    final float[] array8 = array2;
                    final int n10 = 4;
                    final float n11 = array[2] + n;
                    array8[n10] = n11;
                    final float n12 = n11;
                    final float[] array9 = array2;
                    final int n13 = 5;
                    final float n14 = array[3] + n2;
                    array9[n13] = n14;
                    final float n15 = n14;
                    if (n12 >= 8.5070587E37f || n12 <= -8.5070587E37f || n15 >= 8.5070587E37f || n15 <= -8.5070587E37f) {
                        break;
                    }
                    if (n3 != 0) {
                        array2[0] = (array3[0] = n12);
                        array2[1] = (array3[1] = n15);
                        b = true;
                        n3 = 0;
                        break;
                    }
                    if (array2[2] < 8.5070587E37f && array2[2] > -8.5070587E37f && array2[3] < 8.5070587E37f && array2[3] > -8.5070587E37f) {
                        ProcessQuad(processHandler, array2, array5);
                    }
                    else {
                        ProcessLine(processHandler, array2[0], array2[1], array2[4], array2[5], array5);
                    }
                    array2[0] = n12;
                    array2[1] = n15;
                    break;
                }
                case 3: {
                    array2[2] = array[0] + n;
                    array2[3] = array[1] + n2;
                    array2[4] = array[2] + n;
                    array2[5] = array[3] + n2;
                    final float[] array10 = array2;
                    final int n16 = 6;
                    final float n17 = array[4] + n;
                    array10[n16] = n17;
                    final float n18 = n17;
                    final float[] array11 = array2;
                    final int n19 = 7;
                    final float n20 = array[5] + n2;
                    array11[n19] = n20;
                    final float n21 = n20;
                    if (n18 >= 8.5070587E37f || n18 <= -8.5070587E37f || n21 >= 8.5070587E37f || n21 <= -8.5070587E37f) {
                        break;
                    }
                    if (n3 != 0) {
                        array2[0] = (array3[0] = array2[6]);
                        array2[1] = (array3[1] = array2[7]);
                        b = true;
                        n3 = 0;
                        break;
                    }
                    if (array2[2] < 8.5070587E37f && array2[2] > -8.5070587E37f && array2[3] < 8.5070587E37f && array2[3] > -8.5070587E37f && array2[4] < 8.5070587E37f && array2[4] > -8.5070587E37f && array2[5] < 8.5070587E37f && array2[5] > -8.5070587E37f) {
                        ProcessCubic(processHandler, array2, array5);
                    }
                    else {
                        ProcessLine(processHandler, array2[0], array2[1], array2[6], array2[7], array5);
                    }
                    array2[0] = n18;
                    array2[1] = n21;
                    break;
                }
                case 4: {
                    if (b && n3 == 0) {
                        n3 = 0;
                        if (array2[0] != array3[0] || array2[1] != array3[1]) {
                            ProcessLine(processHandler, array2[0], array2[1], array3[0], array3[1], array5);
                            array2[0] = array3[0];
                            array2[1] = array3[1];
                        }
                        processHandler.processEndSubPath();
                        break;
                    }
                    break;
                }
            }
            pathIterator.next();
        }
        if (b & n3 == 0) {
            if (processHandler.clipMode == 1 && (array2[0] != array3[0] || array2[1] != array3[1])) {
                ProcessLine(processHandler, array2[0], array2[1], array3[0], array3[1], array5);
            }
            processHandler.processEndSubPath();
        }
        return true;
    }
    
    private static void FillPolygon(final FillProcessHandler fillProcessHandler, final int n) {
        final int n2 = fillProcessHandler.dhnd.xMax - 1;
        final FillData fd = fillProcessHandler.fd;
        final int plgYMin = fd.plgYMin;
        final int plgYMax = fd.plgYMax;
        final int n3 = (plgYMax - plgYMin >> 10) + 4;
        final int n4 = plgYMin - 1 & 0xFFFFFC00;
        final int n5 = (n == 1) ? -1 : 1;
        final List<Point> plgPnts = fd.plgPnts;
        final int size = plgPnts.size();
        if (size <= 1) {
            return;
        }
        final Point[] array = new Point[n3];
        ((Point)plgPnts.get(0)).prev = null;
        for (int i = 0; i < size - 1; ++i) {
            final Point prev = plgPnts.get(i);
            final Point next = plgPnts.get(i + 1);
            final int n6 = prev.y - n4 - 1 >> 10;
            prev.nextByY = array[n6];
            array[n6] = prev;
            prev.next = next;
            next.prev = prev;
        }
        final Point point = plgPnts.get(size - 1);
        final int n7 = point.y - n4 - 1 >> 10;
        point.nextByY = array[n7];
        array[n7] = point;
        final ActiveEdgeList list = new ActiveEdgeList();
        for (int n8 = n4 + 1024, n9 = 0; n8 <= plgYMax && n9 < n3; n8 += 1024, ++n9) {
            for (Point nextByY = array[n9]; nextByY != null; nextByY = nextByY.nextByY) {
                if (nextByY.prev != null && !nextByY.prev.lastPoint) {
                    if (nextByY.prev.edge != null && nextByY.prev.y <= n8) {
                        list.delete(nextByY.prev.edge);
                        nextByY.prev.edge = null;
                    }
                    else if (nextByY.prev.y > n8) {
                        list.insert(nextByY.prev, n8);
                    }
                }
                if (!nextByY.lastPoint && nextByY.next != null) {
                    if (nextByY.edge != null && nextByY.next.y <= n8) {
                        list.delete(nextByY.edge);
                        nextByY.edge = null;
                    }
                    else if (nextByY.next.y > n8) {
                        list.insert(nextByY, n8);
                    }
                }
            }
            if (!list.isEmpty()) {
                list.sort();
                int n10 = 0;
                int n11 = 0;
                int xMin = fillProcessHandler.dhnd.xMin;
                for (Edge edge = list.head; edge != null; edge = edge.next) {
                    n10 += edge.dir;
                    if ((n10 & n5) != 0x0 && n11 == 0) {
                        xMin = edge.x + 1024 - 1 >> 10;
                        n11 = 1;
                    }
                    if ((n10 & n5) == 0x0 && n11 != 0) {
                        final int n12 = edge.x - 1 >> 10;
                        if (xMin <= n12) {
                            fillProcessHandler.dhnd.drawScanline(xMin, n12, n8 >> 10);
                        }
                        n11 = 0;
                    }
                    final Edge edge2 = edge;
                    edge2.x += edge.dx;
                }
                if (n11 != 0 && xMin <= n2) {
                    fillProcessHandler.dhnd.drawScanline(xMin, n2, n8 >> 10);
                }
            }
        }
    }
    
    static {
        ProcessPath.noopEndSubPathHandler = new EndSubPathHandler() {
            @Override
            public void processEndSubPath() {
            }
        };
    }
    
    public abstract static class DrawHandler
    {
        public int xMin;
        public int yMin;
        public int xMax;
        public int yMax;
        public float xMinf;
        public float yMinf;
        public float xMaxf;
        public float yMaxf;
        public int strokeControl;
        
        public DrawHandler(final int n, final int n2, final int n3, final int n4, final int n5) {
            this.setBounds(n, n2, n3, n4, n5);
        }
        
        public void setBounds(final int xMin, final int yMin, final int xMax, final int yMax) {
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
            this.xMinf = xMin - 0.5f;
            this.yMinf = yMin - 0.5f;
            this.xMaxf = xMax - 0.5f - 9.765625E-4f;
            this.yMaxf = yMax - 0.5f - 9.765625E-4f;
        }
        
        public void setBounds(final int n, final int n2, final int n3, final int n4, final int strokeControl) {
            this.strokeControl = strokeControl;
            this.setBounds(n, n2, n3, n4);
        }
        
        public void adjustBounds(int xMin, int yMin, int xMax, int yMax) {
            if (this.xMin > xMin) {
                xMin = this.xMin;
            }
            if (this.xMax < xMax) {
                xMax = this.xMax;
            }
            if (this.yMin > yMin) {
                yMin = this.yMin;
            }
            if (this.yMax < yMax) {
                yMax = this.yMax;
            }
            this.setBounds(xMin, yMin, xMax, yMax);
        }
        
        public DrawHandler(final int n, final int n2, final int n3, final int n4) {
            this(n, n2, n3, n4, 0);
        }
        
        public abstract void drawLine(final int p0, final int p1, final int p2, final int p3);
        
        public abstract void drawPixel(final int p0, final int p1);
        
        public abstract void drawScanline(final int p0, final int p1, final int p2);
    }
    
    public abstract static class ProcessHandler implements EndSubPathHandler
    {
        DrawHandler dhnd;
        int clipMode;
        
        public ProcessHandler(final DrawHandler dhnd, final int clipMode) {
            this.dhnd = dhnd;
            this.clipMode = clipMode;
        }
        
        public abstract void processFixedLine(final int p0, final int p1, final int p2, final int p3, final int[] p4, final boolean p5, final boolean p6);
    }
    
    private static class DrawProcessHandler extends ProcessHandler
    {
        EndSubPathHandler processESP;
        
        public DrawProcessHandler(final DrawHandler dhnd, final EndSubPathHandler processESP) {
            super(dhnd, 0);
            this.dhnd = dhnd;
            this.processESP = processESP;
        }
        
        @Override
        public void processEndSubPath() {
            this.processESP.processEndSubPath();
        }
        
        void PROCESS_LINE(final int n, final int n2, final int n3, final int n4, final boolean b, final int[] array) {
            final int n5 = n >> 10;
            final int n6 = n2 >> 10;
            final int n7 = n3 >> 10;
            final int n8 = n4 >> 10;
            if (((n5 ^ n7) | (n6 ^ n8)) != 0x0) {
                if ((!b || (this.dhnd.yMin <= n6 && this.dhnd.yMax > n6 && this.dhnd.xMin <= n5 && this.dhnd.xMax > n5)) && array[0] == 1 && ((array[1] == n5 && array[2] == n6) || (array[3] == n5 && array[4] == n6))) {
                    this.dhnd.drawPixel(n5, n6);
                }
                this.dhnd.drawLine(n5, n6, n7, n8);
                if (array[0] == 0) {
                    array[array[0] = 1] = n5;
                    array[2] = n6;
                    array[3] = n5;
                    array[4] = n6;
                }
                if ((array[1] == n7 && array[2] == n8) || (array[3] == n7 && array[4] == n8)) {
                    if (b && (this.dhnd.yMin > n8 || this.dhnd.yMax <= n8 || this.dhnd.xMin > n7 || this.dhnd.xMax <= n7)) {
                        return;
                    }
                    this.dhnd.drawPixel(n7, n8);
                }
                array[3] = n7;
                array[4] = n8;
                return;
            }
            if (b && (this.dhnd.yMin > n6 || this.dhnd.yMax <= n6 || this.dhnd.xMin > n5 || this.dhnd.xMax <= n5)) {
                return;
            }
            if (array[0] == 0) {
                array[array[0] = 1] = n5;
                array[2] = n6;
                array[3] = n5;
                array[4] = n6;
                this.dhnd.drawPixel(n5, n6);
            }
            else if ((n5 != array[3] || n6 != array[4]) && (n5 != array[1] || n6 != array[2])) {
                this.dhnd.drawPixel(n5, n6);
                array[3] = n5;
                array[4] = n6;
            }
        }
        
        void PROCESS_POINT(final int n, final int n2, final boolean b, final int[] array) {
            final int n3 = n >> 10;
            final int n4 = n2 >> 10;
            if (b && (this.dhnd.yMin > n4 || this.dhnd.yMax <= n4 || this.dhnd.xMin > n3 || this.dhnd.xMax <= n3)) {
                return;
            }
            if (array[0] == 0) {
                array[array[0] = 1] = n3;
                array[2] = n4;
                array[3] = n3;
                array[4] = n4;
                this.dhnd.drawPixel(n3, n4);
            }
            else if ((n3 != array[3] || n4 != array[4]) && (n3 != array[1] || n4 != array[2])) {
                this.dhnd.drawPixel(n3, n4);
                array[3] = n3;
                array[4] = n4;
            }
        }
        
        @Override
        public void processFixedLine(final int n, final int n2, final int n3, final int n4, final int[] array, final boolean b, final boolean b2) {
            final int n5 = (n ^ n3) | (n2 ^ n4);
            if ((n5 & 0xFFFFFC00) == 0x0) {
                if (n5 == 0) {
                    this.PROCESS_POINT(n + 512, n2 + 512, b, array);
                }
                return;
            }
            int n6;
            int n7;
            int n8;
            int n9;
            if (n == n3 || n2 == n4) {
                n6 = n + 512;
                n7 = n3 + 512;
                n8 = n2 + 512;
                n9 = n4 + 512;
            }
            else {
                final int n10 = n3 - n;
                final int n11 = n4 - n2;
                final int n12 = n & 0xFFFFFC00;
                final int n13 = n2 & 0xFFFFFC00;
                final int n14 = n3 & 0xFFFFFC00;
                final int n15 = n4 & 0xFFFFFC00;
                if (n12 == n || n13 == n2) {
                    n6 = n + 512;
                    n8 = n2 + 512;
                }
                else {
                    final int n16 = (n < n3) ? (n12 + 1024) : n12;
                    final int n17 = (n2 < n4) ? (n13 + 1024) : n13;
                    final int n18 = n2 + (n16 - n) * n11 / n10;
                    if (n18 >= n13 && n18 <= n13 + 1024) {
                        n6 = n16;
                        n8 = n18 + 512;
                    }
                    else {
                        n6 = n + (n17 - n2) * n10 / n11 + 512;
                        n8 = n17;
                    }
                }
                if (n14 == n3 || n15 == n4) {
                    n7 = n3 + 512;
                    n9 = n4 + 512;
                }
                else {
                    final int n19 = (n > n3) ? (n14 + 1024) : n14;
                    final int n20 = (n2 > n4) ? (n15 + 1024) : n15;
                    final int n21 = n4 + (n19 - n3) * n11 / n10;
                    if (n21 >= n15 && n21 <= n15 + 1024) {
                        n7 = n19;
                        n9 = n21 + 512;
                    }
                    else {
                        n7 = n3 + (n20 - n4) * n10 / n11 + 512;
                        n9 = n20;
                    }
                }
            }
            this.PROCESS_LINE(n6, n8, n7, n9, b, array);
        }
    }
    
    private static class Point
    {
        public int x;
        public int y;
        public boolean lastPoint;
        public Point prev;
        public Point next;
        public Point nextByY;
        public Edge edge;
        
        public Point(final int x, final int y, final boolean lastPoint) {
            this.x = x;
            this.y = y;
            this.lastPoint = lastPoint;
        }
    }
    
    private static class Edge
    {
        int x;
        int dx;
        Point p;
        int dir;
        Edge prev;
        Edge next;
        
        public Edge(final Point p4, final int x, final int dx, final int dir) {
            this.p = p4;
            this.x = x;
            this.dx = dx;
            this.dir = dir;
        }
    }
    
    private static class FillData
    {
        List<Point> plgPnts;
        public int plgYMin;
        public int plgYMax;
        
        public FillData() {
            this.plgPnts = new Vector<Point>(256);
        }
        
        public void addPoint(final int n, final int n2, final boolean b) {
            if (this.plgPnts.size() == 0) {
                this.plgYMax = n2;
                this.plgYMin = n2;
            }
            else {
                this.plgYMin = ((this.plgYMin > n2) ? n2 : this.plgYMin);
                this.plgYMax = ((this.plgYMax < n2) ? n2 : this.plgYMax);
            }
            this.plgPnts.add(new Point(n, n2, b));
        }
        
        public boolean isEmpty() {
            return this.plgPnts.size() == 0;
        }
        
        public boolean isEnded() {
            return this.plgPnts.get(this.plgPnts.size() - 1).lastPoint;
        }
        
        public boolean setEnded() {
            return this.plgPnts.get(this.plgPnts.size() - 1).lastPoint = true;
        }
    }
    
    private static class ActiveEdgeList
    {
        Edge head;
        
        public boolean isEmpty() {
            return this.head == null;
        }
        
        public void insert(final Point point, final int n) {
            final Point next = point.next;
            final int x = point.x;
            final int y = point.y;
            final int x2 = next.x;
            final int y2 = next.y;
            if (y == y2) {
                return;
            }
            final int n2 = x2 - x;
            final int n3 = y2 - y;
            int n4;
            int n5;
            int n6;
            if (y < y2) {
                n4 = x;
                n5 = n - y;
                n6 = -1;
            }
            else {
                n4 = x2;
                n5 = n - y2;
                n6 = 1;
            }
            int n7;
            int n8;
            if (n2 > 1048576.0f || n2 < -1048576.0f) {
                n7 = (int)(n2 * 1024.0 / n3);
                n8 = n4 + (int)(n2 * (double)n5 / n3);
            }
            else {
                n7 = (n2 << 10) / n3;
                n8 = n4 + n2 * n5 / n3;
            }
            final Edge prev = new Edge(point, n8, n7, n6);
            prev.next = this.head;
            prev.prev = null;
            if (this.head != null) {
                this.head.prev = prev;
            }
            final Edge edge = prev;
            point.edge = edge;
            this.head = edge;
        }
        
        public void delete(final Edge edge) {
            final Edge prev = edge.prev;
            final Edge next = edge.next;
            if (prev != null) {
                prev.next = next;
            }
            else {
                this.head = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
        
        public void sort() {
            Edge edge = null;
            int n = 1;
            while (edge != this.head.next && n != 0) {
                Edge head;
                Edge next = head = this.head;
                Edge edge2 = next.next;
                n = 0;
                while (next != edge) {
                    if (next.x >= edge2.x) {
                        n = 1;
                        if (next == this.head) {
                            final Edge next2 = edge2.next;
                            edge2.next = next;
                            next.next = next2;
                            this.head = edge2;
                            head = edge2;
                        }
                        else {
                            final Edge next3 = edge2.next;
                            edge2.next = next;
                            next.next = next3;
                            head.next = edge2;
                            head = edge2;
                        }
                    }
                    else {
                        head = next;
                        next = next.next;
                    }
                    edge2 = next.next;
                    if (edge2 == edge) {
                        edge = next;
                    }
                }
            }
            Edge edge3 = this.head;
            Edge prev = null;
            while (edge3 != null) {
                edge3.prev = prev;
                prev = edge3;
                edge3 = edge3.next;
            }
        }
    }
    
    private static class FillProcessHandler extends ProcessHandler
    {
        FillData fd;
        
        @Override
        public void processFixedLine(final int n, final int n2, final int n3, final int n4, final int[] array, final boolean b, final boolean b2) {
            if (!b) {
                if (this.fd.isEmpty() || this.fd.isEnded()) {
                    this.fd.addPoint(n, n2, false);
                }
                this.fd.addPoint(n3, n4, false);
                if (b2) {
                    this.fd.setEnded();
                }
                return;
            }
            final int[] array2 = { n, n2, n3, n4, 0, 0 };
            final int n5 = (int)(this.dhnd.xMinf * 1024.0f);
            final int n6 = (int)(this.dhnd.xMaxf * 1024.0f);
            final int n7 = (int)(this.dhnd.yMinf * 1024.0f);
            final int n8 = (int)(this.dhnd.yMaxf * 1024.0f);
            if (TESTANDCLIP(n7, n8, array2, 1, 0, 3, 2) == 4) {
                return;
            }
            final int access$100 = TESTANDCLIP(n7, n8, array2, 3, 2, 1, 0);
            if (access$100 == 4) {
                return;
            }
            final boolean access$101 = IS_CLIPPED(access$100);
            final int access$102 = CLIPCLAMP(n5, n6, array2, 0, 1, 2, 3, 4, 5);
            if (access$102 == 0) {
                this.processFixedLine(array2[4], array2[5], array2[0], array2[1], array, false, access$101);
            }
            else if (access$102 == 4) {
                return;
            }
            final int access$103 = CLIPCLAMP(n5, n6, array2, 2, 3, 0, 1, 4, 5);
            final boolean b3 = access$101 || access$103 == 1;
            this.processFixedLine(array2[0], array2[1], array2[2], array2[3], array, false, b3);
            if (access$103 == 0) {
                this.processFixedLine(array2[2], array2[3], array2[4], array2[5], array, false, b3);
            }
        }
        
        FillProcessHandler(final DrawHandler drawHandler) {
            super(drawHandler, 1);
            this.fd = new FillData();
        }
        
        @Override
        public void processEndSubPath() {
            if (!this.fd.isEmpty()) {
                this.fd.setEnded();
            }
        }
    }
    
    public interface EndSubPathHandler
    {
        void processEndSubPath();
    }
}
