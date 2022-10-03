package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.awt.Point;
import java.awt.Rectangle;

public class ReaderUtil
{
    private static void computeUpdatedPixels(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int[] array, final int n10) {
        int n11 = 0;
        int n12 = -1;
        int n13 = -1;
        int n14 = -1;
        for (int i = 0; i < n8; ++i) {
            final int n15 = n7 + i * n9;
            if (n15 >= n) {
                if ((n15 - n) % n6 == 0) {
                    if (n15 >= n + n2) {
                        break;
                    }
                    final int n16 = n3 + (n15 - n) / n6;
                    if (n16 >= n4) {
                        if (n16 > n5) {
                            break;
                        }
                        if (n11 == 0) {
                            n12 = n16;
                            n11 = 1;
                        }
                        else if (n13 == -1) {
                            n13 = n16;
                        }
                        n14 = n16;
                    }
                }
            }
        }
        array[n10] = n12;
        if (n11 == 0) {
            array[n10 + 2] = 0;
        }
        else {
            array[n10 + 2] = n14 - n12 + 1;
        }
        array[n10 + 4] = Math.max(n13 - n12, 1);
    }
    
    public static int[] computeUpdatedPixels(final Rectangle rectangle, final Point point, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final int n11, final int n12) {
        final int[] array = new int[6];
        computeUpdatedPixels(rectangle.x, rectangle.width, point.x, n, n3, n5, n7, n9, n11, array, 0);
        computeUpdatedPixels(rectangle.y, rectangle.height, point.y, n2, n4, n6, n8, n10, n12, array, 1);
        return array;
    }
    
    public static int readMultiByteInteger(final ImageInputStream imageInputStream) throws IOException {
        byte b = imageInputStream.readByte();
        int n = b & 0x7F;
        while ((b & 0x80) == 0x80) {
            final int n2 = n << 7;
            b = imageInputStream.readByte();
            n = (n2 | (b & 0x7F));
        }
        return n;
    }
}
