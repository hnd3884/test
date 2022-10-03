package java.awt.image;

public class AreaAveragingScaleFilter extends ReplicateScaleFilter
{
    private static final ColorModel rgbmodel;
    private static final int neededHints = 6;
    private boolean passthrough;
    private float[] reds;
    private float[] greens;
    private float[] blues;
    private float[] alphas;
    private int savedy;
    private int savedyrem;
    
    public AreaAveragingScaleFilter(final int n, final int n2) {
        super(n, n2);
    }
    
    @Override
    public void setHints(final int hints) {
        this.passthrough = ((hints & 0x6) != 0x6);
        super.setHints(hints);
    }
    
    private void makeAccumBuffers() {
        this.reds = new float[this.destWidth];
        this.greens = new float[this.destWidth];
        this.blues = new float[this.destWidth];
        this.alphas = new float[this.destWidth];
    }
    
    private int[] calcRow() {
        final float n = this.srcWidth * (float)this.srcHeight;
        if (this.outpixbuf == null || !(this.outpixbuf instanceof int[])) {
            this.outpixbuf = new int[this.destWidth];
        }
        final int[] array = (int[])this.outpixbuf;
        for (int i = 0; i < this.destWidth; ++i) {
            float n2 = n;
            int round = Math.round(this.alphas[i] / n2);
            if (round <= 0) {
                round = 0;
            }
            else if (round >= 255) {
                round = 255;
            }
            else {
                n2 = this.alphas[i] / 255.0f;
            }
            int round2 = Math.round(this.reds[i] / n2);
            int round3 = Math.round(this.greens[i] / n2);
            int round4 = Math.round(this.blues[i] / n2);
            if (round2 < 0) {
                round2 = 0;
            }
            else if (round2 > 255) {
                round2 = 255;
            }
            if (round3 < 0) {
                round3 = 0;
            }
            else if (round3 > 255) {
                round3 = 255;
            }
            if (round4 < 0) {
                round4 = 0;
            }
            else if (round4 > 255) {
                round4 = 255;
            }
            array[i] = (round << 24 | round2 << 16 | round3 << 8 | round4);
        }
        return array;
    }
    
    private void accumPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final Object o, int n5, final int n6) {
        if (this.reds == null) {
            this.makeAccumBuffers();
        }
        int i = n2;
        int n7 = this.destHeight;
        int savedy;
        int savedyrem;
        if (i == 0) {
            savedy = 0;
            savedyrem = 0;
        }
        else {
            savedy = this.savedy;
            savedyrem = this.savedyrem;
        }
        while (i < n2 + n4) {
            if (savedyrem == 0) {
                for (int j = 0; j < this.destWidth; ++j) {
                    final float[] alphas = this.alphas;
                    final int n8 = j;
                    final float[] reds = this.reds;
                    final int n9 = j;
                    final float[] greens = this.greens;
                    final int n10 = j;
                    final float[] blues = this.blues;
                    final int n11 = j;
                    final float n12 = 0.0f;
                    greens[n10] = (blues[n11] = n12);
                    alphas[n8] = (reds[n9] = n12);
                }
                savedyrem = this.srcHeight;
            }
            int n13;
            if (n7 < savedyrem) {
                n13 = n7;
            }
            else {
                n13 = savedyrem;
            }
            int k = 0;
            int n14 = 0;
            int destWidth = 0;
            int n15 = this.srcWidth;
            float n16 = 0.0f;
            float n17 = 0.0f;
            float n18 = 0.0f;
            float n19 = 0.0f;
            while (k < n3) {
                if (destWidth == 0) {
                    destWidth = this.destWidth;
                    int n20;
                    if (o instanceof byte[]) {
                        n20 = (((byte[])o)[n5 + k] & 0xFF);
                    }
                    else {
                        n20 = ((int[])o)[n5 + k];
                    }
                    final int rgb = colorModel.getRGB(n20);
                    n16 = (float)(rgb >>> 24);
                    n17 = (float)(rgb >> 16 & 0xFF);
                    n18 = (float)(rgb >> 8 & 0xFF);
                    n19 = (float)(rgb & 0xFF);
                    if (n16 != 255.0f) {
                        final float n21 = n16 / 255.0f;
                        n17 *= n21;
                        n18 *= n21;
                        n19 *= n21;
                    }
                }
                int n22;
                if (destWidth < n15) {
                    n22 = destWidth;
                }
                else {
                    n22 = n15;
                }
                final float n23 = n22 * (float)n13;
                final float[] alphas2 = this.alphas;
                final int n24 = n14;
                alphas2[n24] += n23 * n16;
                final float[] reds2 = this.reds;
                final int n25 = n14;
                reds2[n25] += n23 * n17;
                final float[] greens2 = this.greens;
                final int n26 = n14;
                greens2[n26] += n23 * n18;
                final float[] blues2 = this.blues;
                final int n27 = n14;
                blues2[n27] += n23 * n19;
                if ((destWidth -= n22) == 0) {
                    ++k;
                }
                if ((n15 -= n22) == 0) {
                    ++n14;
                    n15 = this.srcWidth;
                }
            }
            if ((savedyrem -= n13) == 0) {
                final int[] calcRow = this.calcRow();
                do {
                    this.consumer.setPixels(0, savedy, this.destWidth, 1, AreaAveragingScaleFilter.rgbmodel, calcRow, 0, this.destWidth);
                    ++savedy;
                } while ((n7 -= n13) >= n13 && n13 == this.srcHeight);
            }
            else {
                n7 -= n13;
            }
            if (n7 == 0) {
                n7 = this.destHeight;
                ++i;
                n5 += n6;
            }
        }
        this.savedyrem = savedyrem;
        this.savedy = savedy;
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        if (this.passthrough) {
            super.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
        }
        else {
            this.accumPixels(n, n2, n3, n4, colorModel, array, n5, n6);
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        if (this.passthrough) {
            super.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
        }
        else {
            this.accumPixels(n, n2, n3, n4, colorModel, array, n5, n6);
        }
    }
    
    static {
        rgbmodel = ColorModel.getRGBdefault();
    }
}
