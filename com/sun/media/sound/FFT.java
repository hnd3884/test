package com.sun.media.sound;

public final class FFT
{
    private final double[] w;
    private final int fftFrameSize;
    private final int sign;
    private final int[] bitm_array;
    private final int fftFrameSize2;
    
    public FFT(final int fftFrameSize, final int sign) {
        this.w = computeTwiddleFactors(fftFrameSize, sign);
        this.fftFrameSize = fftFrameSize;
        this.sign = sign;
        this.fftFrameSize2 = fftFrameSize << 1;
        this.bitm_array = new int[this.fftFrameSize2];
        for (int i = 2; i < this.fftFrameSize2; i += 2) {
            int j = 2;
            int n = 0;
            while (j < this.fftFrameSize2) {
                if ((i & j) != 0x0) {
                    ++n;
                }
                n <<= 1;
                j <<= 1;
            }
            this.bitm_array[i] = n;
        }
    }
    
    public void transform(final double[] array) {
        this.bitreversal(array);
        calc(this.fftFrameSize, array, this.sign, this.w);
    }
    
    private static final double[] computeTwiddleFactors(final int n, final int n2) {
        final int n3 = (int)(Math.log(n) / Math.log(2.0));
        final double[] array = new double[(n - 1) * 4];
        int n4 = 0;
        int i = 0;
        int n5 = 2;
        while (i < n3) {
            final int n6 = n5;
            n5 <<= 1;
            double n7 = 1.0;
            double n8 = 0.0;
            final double n9 = 3.141592653589793 / (n6 >> 1);
            final double cos = Math.cos(n9);
            final double n10 = n2 * Math.sin(n9);
            for (int j = 0; j < n6; j += 2) {
                array[n4++] = n7;
                array[n4++] = n8;
                final double n11 = n7;
                n7 = n11 * cos - n8 * n10;
                n8 = n11 * n10 + n8 * cos;
            }
            ++i;
        }
        int n12 = 0;
        int n13 = array.length >> 1;
        int k = 0;
        int n14 = 2;
        while (k < n3 - 1) {
            final int n15 = n14;
            n14 *= 2;
            int n16 = n12 + n15;
            for (int l = 0; l < n15; l += 2) {
                final double n17 = array[n12++];
                final double n18 = array[n12++];
                final double n19 = array[n16++];
                final double n20 = array[n16++];
                array[n13++] = n17 * n19 - n18 * n20;
                array[n13++] = n17 * n20 + n18 * n19;
            }
            ++k;
        }
        return array;
    }
    
    private static final void calc(final int n, final double[] array, final int n2, final double[] array2) {
        final int n3 = n << 1;
        final int n4 = 2;
        if (n4 >= n3) {
            return;
        }
        final int n5 = n4 - 2;
        if (n2 == -1) {
            calcF4F(n, array, n5, n4, array2);
        }
        else {
            calcF4I(n, array, n5, n4, array2);
        }
    }
    
    private static final void calcF2E(final int n, final double[] array, int n2, final int n3, final double[] array2) {
        for (int i = 0; i < n3; i += 2) {
            final double n4 = array2[n2++];
            final double n5 = array2[n2++];
            final int n6 = i + n3;
            final double n7 = array[n6];
            final double n8 = array[n6 + 1];
            final double n9 = array[i];
            final double n10 = array[i + 1];
            final double n11 = n7 * n4 - n8 * n5;
            final double n12 = n7 * n5 + n8 * n4;
            array[n6] = n9 - n11;
            array[n6 + 1] = n10 - n12;
            array[i] = n9 + n11;
            array[i + 1] = n10 + n12;
        }
    }
    
    private static final void calcF4F(final int n, final double[] array, int n2, int i, final double[] array2) {
        final int n3 = n << 1;
        final int n4 = array2.length >> 1;
        while (i < n3) {
            if (i << 2 == n3) {
                calcF4FE(n, array, n2, i, array2);
                return;
            }
            final int n5 = i;
            final int n6 = i << 1;
            if (n6 == n3) {
                calcF2E(n, array, n2, i, array2);
                return;
            }
            i <<= 2;
            int n7 = n2 + n5;
            int n8 = n2 + n4;
            n2 += 2;
            n7 += 2;
            n8 += 2;
            int n42;
            for (int j = 0; j < n3; j = n42 + i) {
                final int n9 = j + n5;
                final double n10 = array[n9];
                final double n11 = array[n9 + 1];
                final double n12 = array[j];
                final double n13 = array[j + 1];
                final int n14 = j + n6;
                final int n15 = n9 + n6;
                final double n16 = array[n15];
                final double n17 = array[n15 + 1];
                final double n18 = array[n14];
                final double n19 = array[n14 + 1];
                final double n20 = n10;
                final double n21 = n11;
                final double n22 = n12 - n20;
                final double n23 = n13 - n21;
                final double n24 = n12 + n20;
                final double n25 = n13 + n21;
                final double n26 = n18;
                final double n27 = n19;
                final double n28 = n16;
                final double n29 = n17;
                final double n30 = n28 - n26;
                final double n31 = n29 - n27;
                final double n32 = n22 + n31;
                final double n33 = n23 - n30;
                final double n34 = n22 - n31;
                final double n35 = n23 + n30;
                final double n36 = n26 + n28;
                final double n37 = n27 + n29;
                final double n38 = n24 - n36;
                final double n39 = n25 - n37;
                final double n40 = n24 + n36;
                final double n41 = n25 + n37;
                array[n15] = n32;
                array[n15 + 1] = n33;
                array[n14] = n38;
                array[n14 + 1] = n39;
                n42 = n14 - n6;
                final int n43 = n15 - n6;
                array[n43] = n34;
                array[n43 + 1] = n35;
                array[n42] = n40;
                array[n42 + 1] = n41;
            }
            for (int k = 2; k < n5; k += 2) {
                final double n44 = array2[n2++];
                final double n45 = array2[n2++];
                final double n46 = array2[n7++];
                final double n47 = array2[n7++];
                final double n48 = array2[n8++];
                final double n49 = array2[n8++];
                int n83;
                for (int l = k; l < n3; l = n83 + i) {
                    final int n50 = l + n5;
                    final double n51 = array[n50];
                    final double n52 = array[n50 + 1];
                    final double n53 = array[l];
                    final double n54 = array[l + 1];
                    final int n55 = l + n6;
                    final int n56 = n50 + n6;
                    final double n57 = array[n56];
                    final double n58 = array[n56 + 1];
                    final double n59 = array[n55];
                    final double n60 = array[n55 + 1];
                    final double n61 = n51 * n44 - n52 * n45;
                    final double n62 = n51 * n45 + n52 * n44;
                    final double n63 = n53 - n61;
                    final double n64 = n54 - n62;
                    final double n65 = n53 + n61;
                    final double n66 = n54 + n62;
                    final double n67 = n59 * n46 - n60 * n47;
                    final double n68 = n59 * n47 + n60 * n46;
                    final double n69 = n57 * n48 - n58 * n49;
                    final double n70 = n57 * n49 + n58 * n48;
                    final double n71 = n69 - n67;
                    final double n72 = n70 - n68;
                    final double n73 = n63 + n72;
                    final double n74 = n64 - n71;
                    final double n75 = n63 - n72;
                    final double n76 = n64 + n71;
                    final double n77 = n67 + n69;
                    final double n78 = n68 + n70;
                    final double n79 = n65 - n77;
                    final double n80 = n66 - n78;
                    final double n81 = n65 + n77;
                    final double n82 = n66 + n78;
                    array[n56] = n73;
                    array[n56 + 1] = n74;
                    array[n55] = n79;
                    array[n55 + 1] = n80;
                    n83 = n55 - n6;
                    final int n84 = n56 - n6;
                    array[n84] = n75;
                    array[n84 + 1] = n76;
                    array[n83] = n81;
                    array[n83 + 1] = n82;
                }
            }
            n2 += n5 << 1;
        }
        calcF2E(n, array, n2, i, array2);
    }
    
    private static final void calcF4I(final int n, final double[] array, int n2, int i, final double[] array2) {
        final int n3 = n << 1;
        final int n4 = array2.length >> 1;
        while (i < n3) {
            if (i << 2 == n3) {
                calcF4IE(n, array, n2, i, array2);
                return;
            }
            final int n5 = i;
            final int n6 = i << 1;
            if (n6 == n3) {
                calcF2E(n, array, n2, i, array2);
                return;
            }
            i <<= 2;
            int n7 = n2 + n5;
            int n8 = n2 + n4;
            n2 += 2;
            n7 += 2;
            n8 += 2;
            int n42;
            for (int j = 0; j < n3; j = n42 + i) {
                final int n9 = j + n5;
                final double n10 = array[n9];
                final double n11 = array[n9 + 1];
                final double n12 = array[j];
                final double n13 = array[j + 1];
                final int n14 = j + n6;
                final int n15 = n9 + n6;
                final double n16 = array[n15];
                final double n17 = array[n15 + 1];
                final double n18 = array[n14];
                final double n19 = array[n14 + 1];
                final double n20 = n10;
                final double n21 = n11;
                final double n22 = n12 - n20;
                final double n23 = n13 - n21;
                final double n24 = n12 + n20;
                final double n25 = n13 + n21;
                final double n26 = n18;
                final double n27 = n19;
                final double n28 = n16;
                final double n29 = n17;
                final double n30 = n26 - n28;
                final double n31 = n27 - n29;
                final double n32 = n22 + n31;
                final double n33 = n23 - n30;
                final double n34 = n22 - n31;
                final double n35 = n23 + n30;
                final double n36 = n26 + n28;
                final double n37 = n27 + n29;
                final double n38 = n24 - n36;
                final double n39 = n25 - n37;
                final double n40 = n24 + n36;
                final double n41 = n25 + n37;
                array[n15] = n32;
                array[n15 + 1] = n33;
                array[n14] = n38;
                array[n14 + 1] = n39;
                n42 = n14 - n6;
                final int n43 = n15 - n6;
                array[n43] = n34;
                array[n43 + 1] = n35;
                array[n42] = n40;
                array[n42 + 1] = n41;
            }
            for (int k = 2; k < n5; k += 2) {
                final double n44 = array2[n2++];
                final double n45 = array2[n2++];
                final double n46 = array2[n7++];
                final double n47 = array2[n7++];
                final double n48 = array2[n8++];
                final double n49 = array2[n8++];
                int n83;
                for (int l = k; l < n3; l = n83 + i) {
                    final int n50 = l + n5;
                    final double n51 = array[n50];
                    final double n52 = array[n50 + 1];
                    final double n53 = array[l];
                    final double n54 = array[l + 1];
                    final int n55 = l + n6;
                    final int n56 = n50 + n6;
                    final double n57 = array[n56];
                    final double n58 = array[n56 + 1];
                    final double n59 = array[n55];
                    final double n60 = array[n55 + 1];
                    final double n61 = n51 * n44 - n52 * n45;
                    final double n62 = n51 * n45 + n52 * n44;
                    final double n63 = n53 - n61;
                    final double n64 = n54 - n62;
                    final double n65 = n53 + n61;
                    final double n66 = n54 + n62;
                    final double n67 = n59 * n46 - n60 * n47;
                    final double n68 = n59 * n47 + n60 * n46;
                    final double n69 = n57 * n48 - n58 * n49;
                    final double n70 = n57 * n49 + n58 * n48;
                    final double n71 = n67 - n69;
                    final double n72 = n68 - n70;
                    final double n73 = n63 + n72;
                    final double n74 = n64 - n71;
                    final double n75 = n63 - n72;
                    final double n76 = n64 + n71;
                    final double n77 = n67 + n69;
                    final double n78 = n68 + n70;
                    final double n79 = n65 - n77;
                    final double n80 = n66 - n78;
                    final double n81 = n65 + n77;
                    final double n82 = n66 + n78;
                    array[n56] = n73;
                    array[n56 + 1] = n74;
                    array[n55] = n79;
                    array[n55 + 1] = n80;
                    n83 = n55 - n6;
                    final int n84 = n56 - n6;
                    array[n84] = n75;
                    array[n84 + 1] = n76;
                    array[n83] = n81;
                    array[n83 + 1] = n82;
                }
            }
            n2 += n5 << 1;
        }
        calcF2E(n, array, n2, i, array2);
    }
    
    private static final void calcF4FE(final int n, final double[] array, int n2, int i, final double[] array2) {
        final int n3 = n << 1;
        final int n4 = array2.length >> 1;
        while (i < n3) {
            final int n5 = i;
            final int n6 = i << 1;
            if (n6 == n3) {
                calcF2E(n, array, n2, i, array2);
                return;
            }
            i <<= 2;
            int n7 = n2 + n5;
            int n8 = n2 + n4;
            int n20;
            int n21;
            double n40;
            double n41;
            double n46;
            double n47;
            int n48;
            for (int j = 0; j < n5; j = n20 - n6, n48 = n21 - n6, array[n48] = n40, array[n48 + 1] = n41, array[j] = n46, array[j + 1] = n47, j += 2) {
                final double n9 = array2[n2++];
                final double n10 = array2[n2++];
                final double n11 = array2[n7++];
                final double n12 = array2[n7++];
                final double n13 = array2[n8++];
                final double n14 = array2[n8++];
                final int n15 = j + n5;
                final double n16 = array[n15];
                final double n17 = array[n15 + 1];
                final double n18 = array[j];
                final double n19 = array[j + 1];
                n20 = j + n6;
                n21 = n15 + n6;
                final double n22 = array[n21];
                final double n23 = array[n21 + 1];
                final double n24 = array[n20];
                final double n25 = array[n20 + 1];
                final double n26 = n16 * n9 - n17 * n10;
                final double n27 = n16 * n10 + n17 * n9;
                final double n28 = n18 - n26;
                final double n29 = n19 - n27;
                final double n30 = n18 + n26;
                final double n31 = n19 + n27;
                final double n32 = n24 * n11 - n25 * n12;
                final double n33 = n24 * n12 + n25 * n11;
                final double n34 = n22 * n13 - n23 * n14;
                final double n35 = n22 * n14 + n23 * n13;
                final double n36 = n34 - n32;
                final double n37 = n35 - n33;
                final double n38 = n28 + n37;
                final double n39 = n29 - n36;
                n40 = n28 - n37;
                n41 = n29 + n36;
                final double n42 = n32 + n34;
                final double n43 = n33 + n35;
                final double n44 = n30 - n42;
                final double n45 = n31 - n43;
                n46 = n30 + n42;
                n47 = n31 + n43;
                array[n21] = n38;
                array[n21 + 1] = n39;
                array[n20] = n44;
                array[n20 + 1] = n45;
            }
            n2 += n5 << 1;
        }
    }
    
    private static final void calcF4IE(final int n, final double[] array, int n2, int i, final double[] array2) {
        final int n3 = n << 1;
        final int n4 = array2.length >> 1;
        while (i < n3) {
            final int n5 = i;
            final int n6 = i << 1;
            if (n6 == n3) {
                calcF2E(n, array, n2, i, array2);
                return;
            }
            i <<= 2;
            int n7 = n2 + n5;
            int n8 = n2 + n4;
            int n20;
            int n21;
            double n40;
            double n41;
            double n46;
            double n47;
            int n48;
            for (int j = 0; j < n5; j = n20 - n6, n48 = n21 - n6, array[n48] = n40, array[n48 + 1] = n41, array[j] = n46, array[j + 1] = n47, j += 2) {
                final double n9 = array2[n2++];
                final double n10 = array2[n2++];
                final double n11 = array2[n7++];
                final double n12 = array2[n7++];
                final double n13 = array2[n8++];
                final double n14 = array2[n8++];
                final int n15 = j + n5;
                final double n16 = array[n15];
                final double n17 = array[n15 + 1];
                final double n18 = array[j];
                final double n19 = array[j + 1];
                n20 = j + n6;
                n21 = n15 + n6;
                final double n22 = array[n21];
                final double n23 = array[n21 + 1];
                final double n24 = array[n20];
                final double n25 = array[n20 + 1];
                final double n26 = n16 * n9 - n17 * n10;
                final double n27 = n16 * n10 + n17 * n9;
                final double n28 = n18 - n26;
                final double n29 = n19 - n27;
                final double n30 = n18 + n26;
                final double n31 = n19 + n27;
                final double n32 = n24 * n11 - n25 * n12;
                final double n33 = n24 * n12 + n25 * n11;
                final double n34 = n22 * n13 - n23 * n14;
                final double n35 = n22 * n14 + n23 * n13;
                final double n36 = n32 - n34;
                final double n37 = n33 - n35;
                final double n38 = n28 + n37;
                final double n39 = n29 - n36;
                n40 = n28 - n37;
                n41 = n29 + n36;
                final double n42 = n32 + n34;
                final double n43 = n33 + n35;
                final double n44 = n30 - n42;
                final double n45 = n31 - n43;
                n46 = n30 + n42;
                n47 = n31 + n43;
                array[n21] = n38;
                array[n21 + 1] = n39;
                array[n20] = n44;
                array[n20 + 1] = n45;
            }
            n2 += n5 << 1;
        }
    }
    
    private final void bitreversal(final double[] array) {
        if (this.fftFrameSize < 4) {
            return;
        }
        final int n = this.fftFrameSize2 - 2;
        for (int i = 0; i < this.fftFrameSize; i += 4) {
            final int n2 = this.bitm_array[i];
            if (i < n2) {
                int n3 = i;
                int n4 = n2;
                final double n5 = array[n3];
                array[n3] = array[n4];
                array[n4] = n5;
                ++n3;
                ++n4;
                final double n6 = array[n3];
                array[n3] = array[n4];
                array[n4] = n6;
                int n7 = n - i;
                int n8 = n - n2;
                final double n9 = array[n7];
                array[n7] = array[n8];
                array[n8] = n9;
                ++n7;
                ++n8;
                final double n10 = array[n7];
                array[n7] = array[n8];
                array[n8] = n10;
            }
            int n11 = n2 + this.fftFrameSize;
            int n12 = i + 2;
            final double n13 = array[n12];
            array[n12] = array[n11];
            array[n11] = n13;
            ++n12;
            ++n11;
            final double n14 = array[n12];
            array[n12] = array[n11];
            array[n11] = n14;
        }
    }
}
