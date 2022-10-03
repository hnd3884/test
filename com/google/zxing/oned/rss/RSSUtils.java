package com.google.zxing.oned.rss;

public final class RSSUtils
{
    private RSSUtils() {
    }
    
    static int[] getRSSwidths(int val, int n, final int elements, final int maxWidth, final boolean noNarrow) {
        final int[] widths = new int[elements];
        int narrowMask = 0;
        int bar;
        for (bar = 0; bar < elements - 1; ++bar) {
            narrowMask |= 1 << bar;
            int elmWidth = 1;
            int subVal;
            while (true) {
                subVal = combins(n - elmWidth - 1, elements - bar - 2);
                if (noNarrow && narrowMask == 0 && n - elmWidth - (elements - bar - 1) >= elements - bar - 1) {
                    subVal -= combins(n - elmWidth - (elements - bar), elements - bar - 2);
                }
                if (elements - bar - 1 > 1) {
                    int lessVal = 0;
                    for (int mxwElement = n - elmWidth - (elements - bar - 2); mxwElement > maxWidth; --mxwElement) {
                        lessVal += combins(n - elmWidth - mxwElement - 1, elements - bar - 3);
                    }
                    subVal -= lessVal * (elements - 1 - bar);
                }
                else if (n - elmWidth > maxWidth) {
                    --subVal;
                }
                val -= subVal;
                if (val < 0) {
                    break;
                }
                ++elmWidth;
                narrowMask &= ~(1 << bar);
            }
            val += subVal;
            n -= elmWidth;
            widths[bar] = elmWidth;
        }
        widths[bar] = n;
        return widths;
    }
    
    public static int getRSSvalue(final int[] widths, final int maxWidth, final boolean noNarrow) {
        final int elements = widths.length;
        int n = 0;
        for (int i = 0; i < elements; ++i) {
            n += widths[i];
        }
        int val = 0;
        int narrowMask = 0;
        for (int bar = 0; bar < elements - 1; ++bar) {
            int elmWidth;
            for (elmWidth = 1, narrowMask |= 1 << bar; elmWidth < widths[bar]; ++elmWidth, narrowMask &= ~(1 << bar)) {
                int subVal = combins(n - elmWidth - 1, elements - bar - 2);
                if (noNarrow && narrowMask == 0 && n - elmWidth - (elements - bar - 1) >= elements - bar - 1) {
                    subVal -= combins(n - elmWidth - (elements - bar), elements - bar - 2);
                }
                if (elements - bar - 1 > 1) {
                    int lessVal = 0;
                    for (int mxwElement = n - elmWidth - (elements - bar - 2); mxwElement > maxWidth; --mxwElement) {
                        lessVal += combins(n - elmWidth - mxwElement - 1, elements - bar - 3);
                    }
                    subVal -= lessVal * (elements - 1 - bar);
                }
                else if (n - elmWidth > maxWidth) {
                    --subVal;
                }
                val += subVal;
            }
            n -= elmWidth;
        }
        return val;
    }
    
    private static int combins(final int n, final int r) {
        int minDenom;
        int maxDenom;
        if (n - r > r) {
            minDenom = r;
            maxDenom = n - r;
        }
        else {
            minDenom = n - r;
            maxDenom = r;
        }
        int val = 1;
        int j = 1;
        for (int i = n; i > maxDenom; --i) {
            val *= i;
            if (j <= minDenom) {
                val /= j;
                ++j;
            }
        }
        while (j <= minDenom) {
            val /= j;
            ++j;
        }
        return val;
    }
    
    static int[] elements(final int[] eDist, final int N, final int K) {
        final int[] widths = new int[eDist.length + 2];
        final int twoK = K << 1;
        widths[0] = 1;
        int minEven = 10;
        int barSum = 1;
        for (int i = 1; i < twoK - 2; i += 2) {
            widths[i] = eDist[i - 1] - widths[i - 1];
            widths[i + 1] = eDist[i] - widths[i];
            barSum += widths[i] + widths[i + 1];
            if (widths[i] < minEven) {
                minEven = widths[i];
            }
        }
        widths[twoK - 1] = N - barSum;
        if (widths[twoK - 1] < minEven) {
            minEven = widths[twoK - 1];
        }
        if (minEven > 1) {
            for (int i = 0; i < twoK; i += 2) {
                final int[] array = widths;
                final int n = i;
                array[n] += minEven - 1;
                final int[] array2 = widths;
                final int n2 = i + 1;
                array2[n2] -= minEven - 1;
            }
        }
        return widths;
    }
}
