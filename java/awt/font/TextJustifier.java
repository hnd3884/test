package java.awt.font;

class TextJustifier
{
    private GlyphJustificationInfo[] info;
    private int start;
    private int limit;
    static boolean DEBUG;
    public static final int MAX_PRIORITY = 3;
    
    TextJustifier(final GlyphJustificationInfo[] info, final int start, final int limit) {
        this.info = info;
        this.start = start;
        this.limit = limit;
        if (TextJustifier.DEBUG) {
            System.out.println("start: " + start + ", limit: " + limit);
            for (int i = start; i < limit; ++i) {
                final GlyphJustificationInfo glyphJustificationInfo = info[i];
                System.out.println("w: " + glyphJustificationInfo.weight + ", gp: " + glyphJustificationInfo.growPriority + ", gll: " + glyphJustificationInfo.growLeftLimit + ", grl: " + glyphJustificationInfo.growRightLimit);
            }
        }
    }
    
    public float[] justify(float n) {
        final float[] array = new float[this.info.length * 2];
        final boolean b = n > 0.0f;
        if (TextJustifier.DEBUG) {
            System.out.println("delta: " + n);
        }
        int n2 = -1;
        int n3 = 0;
        while (n != 0.0f) {
            final boolean b2 = n3 > 3;
            if (b2) {
                n3 = n2;
            }
            float n4 = 0.0f;
            float n5 = 0.0f;
            float n6 = 0.0f;
            for (int i = this.start; i < this.limit; ++i) {
                final GlyphJustificationInfo glyphJustificationInfo = this.info[i];
                if ((b ? glyphJustificationInfo.growPriority : glyphJustificationInfo.shrinkPriority) == n3) {
                    if (n2 == -1) {
                        n2 = n3;
                    }
                    if (i != this.start) {
                        n4 += glyphJustificationInfo.weight;
                        if (b) {
                            n5 += glyphJustificationInfo.growLeftLimit;
                            if (glyphJustificationInfo.growAbsorb) {
                                n6 += glyphJustificationInfo.weight;
                            }
                        }
                        else {
                            n5 += glyphJustificationInfo.shrinkLeftLimit;
                            if (glyphJustificationInfo.shrinkAbsorb) {
                                n6 += glyphJustificationInfo.weight;
                            }
                        }
                    }
                    if (i + 1 != this.limit) {
                        n4 += glyphJustificationInfo.weight;
                        if (b) {
                            n5 += glyphJustificationInfo.growRightLimit;
                            if (glyphJustificationInfo.growAbsorb) {
                                n6 += glyphJustificationInfo.weight;
                            }
                        }
                        else {
                            n5 += glyphJustificationInfo.shrinkRightLimit;
                            if (glyphJustificationInfo.shrinkAbsorb) {
                                n6 += glyphJustificationInfo.weight;
                            }
                        }
                    }
                }
            }
            if (!b) {
                n5 = -n5;
            }
            final boolean b3 = n4 == 0.0f || (!b2 && n < 0.0f == n < n5);
            final boolean b4 = b3 && n6 > 0.0f;
            final float n7 = n / n4;
            float n8 = 0.0f;
            if (b3 && n6 > 0.0f) {
                n8 = (n - n5) / n6;
            }
            if (TextJustifier.DEBUG) {
                System.out.println("pass: " + n3 + ", d: " + n + ", l: " + n5 + ", w: " + n4 + ", aw: " + n6 + ", wd: " + n7 + ", wa: " + n8 + ", hit: " + (b3 ? "y" : "n"));
            }
            int n9 = this.start * 2;
            for (int j = this.start; j < this.limit; ++j) {
                final GlyphJustificationInfo glyphJustificationInfo2 = this.info[j];
                if ((b ? glyphJustificationInfo2.growPriority : glyphJustificationInfo2.shrinkPriority) == n3) {
                    if (j != this.start) {
                        float n10;
                        if (b3) {
                            n10 = (b ? glyphJustificationInfo2.growLeftLimit : (-glyphJustificationInfo2.shrinkLeftLimit));
                            if (b4) {
                                n10 += glyphJustificationInfo2.weight * n8;
                            }
                        }
                        else {
                            n10 = glyphJustificationInfo2.weight * n7;
                        }
                        final float[] array2 = array;
                        final int n11 = n9;
                        array2[n11] += n10;
                    }
                    ++n9;
                    if (j + 1 != this.limit) {
                        float n12;
                        if (b3) {
                            n12 = (b ? glyphJustificationInfo2.growRightLimit : (-glyphJustificationInfo2.shrinkRightLimit));
                            if (b4) {
                                n12 += glyphJustificationInfo2.weight * n8;
                            }
                        }
                        else {
                            n12 = glyphJustificationInfo2.weight * n7;
                        }
                        final float[] array3 = array;
                        final int n13 = n9;
                        array3[n13] += n12;
                    }
                    ++n9;
                }
                else {
                    n9 += 2;
                }
            }
            if (!b2 && b3 && !b4) {
                n -= n5;
            }
            else {
                n = 0.0f;
            }
            ++n3;
        }
        if (TextJustifier.DEBUG) {
            float n14 = 0.0f;
            for (int k = 0; k < array.length; ++k) {
                n14 += array[k];
                System.out.print(array[k] + ", ");
                if (k % 20 == 9) {
                    System.out.println();
                }
            }
            System.out.println("\ntotal: " + n14);
            System.out.println();
        }
        return array;
    }
    
    static {
        TextJustifier.DEBUG = false;
    }
}
