package javax.swing.plaf.nimbus;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Insets;

class ImageScalingHelper
{
    private static final Insets EMPTY_INSETS;
    static final int PAINT_TOP_LEFT = 1;
    static final int PAINT_TOP = 2;
    static final int PAINT_TOP_RIGHT = 4;
    static final int PAINT_LEFT = 8;
    static final int PAINT_CENTER = 16;
    static final int PAINT_RIGHT = 32;
    static final int PAINT_BOTTOM_RIGHT = 64;
    static final int PAINT_BOTTOM = 128;
    static final int PAINT_BOTTOM_LEFT = 256;
    static final int PAINT_ALL = 512;
    
    public static void paint(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Image image, Insets empty_INSETS, Insets empty_INSETS2, final PaintType paintType, int n5) {
        if (image == null || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
            return;
        }
        if (empty_INSETS == null) {
            empty_INSETS = ImageScalingHelper.EMPTY_INSETS;
        }
        if (empty_INSETS2 == null) {
            empty_INSETS2 = ImageScalingHelper.EMPTY_INSETS;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (paintType == PaintType.CENTER) {
            graphics.drawImage(image, n + (n3 - width) / 2, n2 + (n4 - height) / 2, null);
        }
        else if (paintType == PaintType.TILE) {
            for (int n6 = 0, i = n2, n7 = n2 + n4; i < n7; i += height - n6, n6 = 0) {
                for (int n8 = 0, j = n, n9 = n + n3; j < n9; j += width - n8, n8 = 0) {
                    final int min = Math.min(n9, j + width - n8);
                    final int min2 = Math.min(n7, i + height - n6);
                    graphics.drawImage(image, j, i, min, min2, n8, n6, n8 + min - j, n6 + min2 - i, null);
                }
            }
        }
        else {
            int n10 = empty_INSETS.top;
            int left = empty_INSETS.left;
            int bottom = empty_INSETS.bottom;
            int n11 = empty_INSETS.right;
            int n12 = empty_INSETS2.top;
            int n13 = empty_INSETS2.left;
            int bottom2 = empty_INSETS2.bottom;
            int right = empty_INSETS2.right;
            if (n10 + bottom > height) {
                n12 = (bottom2 = (bottom = (n10 = Math.max(0, height / 2))));
            }
            if (left + n11 > width) {
                right = (n13 = (left = (n11 = Math.max(0, width / 2))));
            }
            if (n12 + bottom2 > n4) {
                bottom2 = (n12 = Math.max(0, n4 / 2 - 1));
            }
            if (n13 + right > n3) {
                right = (n13 = Math.max(0, n3 / 2 - 1));
            }
            final boolean b = paintType == PaintType.PAINT9_STRETCH;
            if ((n5 & 0x200) != 0x0) {
                n5 = (0x1FF & ~n5);
            }
            if ((n5 & 0x8) != 0x0) {
                drawChunk(image, graphics, b, n, n2 + n12, n + n13, n2 + n4 - bottom2, 0, n10, left, height - bottom, false);
            }
            if ((n5 & 0x1) != 0x0) {
                drawImage(image, graphics, n, n2, n + n13, n2 + n12, 0, 0, left, n10);
            }
            if ((n5 & 0x2) != 0x0) {
                drawChunk(image, graphics, b, n + n13, n2, n + n3 - right, n2 + n12, left, 0, width - n11, n10, true);
            }
            if ((n5 & 0x4) != 0x0) {
                drawImage(image, graphics, n + n3 - right, n2, n + n3, n2 + n12, width - n11, 0, width, n10);
            }
            if ((n5 & 0x20) != 0x0) {
                drawChunk(image, graphics, b, n + n3 - right, n2 + n12, n + n3, n2 + n4 - bottom2, width - n11, n10, width, height - bottom, false);
            }
            if ((n5 & 0x40) != 0x0) {
                drawImage(image, graphics, n + n3 - right, n2 + n4 - bottom2, n + n3, n2 + n4, width - n11, height - bottom, width, height);
            }
            if ((n5 & 0x80) != 0x0) {
                drawChunk(image, graphics, b, n + n13, n2 + n4 - bottom2, n + n3 - right, n2 + n4, left, height - bottom, width - n11, height, true);
            }
            if ((n5 & 0x100) != 0x0) {
                drawImage(image, graphics, n, n2 + n4 - bottom2, n + n13, n2 + n4, 0, height - bottom, left, height);
            }
            if ((n5 & 0x10) != 0x0) {
                drawImage(image, graphics, n + n13, n2 + n12, n + n3 - right, n2 + n4 - bottom2, left, n10, width - n11, height - bottom);
            }
        }
    }
    
    private static void drawChunk(final Image image, final Graphics graphics, final boolean b, int n, int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final boolean b2) {
        if (n3 - n <= 0 || n4 - n2 <= 0 || n7 - n5 <= 0 || n8 - n6 <= 0) {
            return;
        }
        if (b) {
            graphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, null);
        }
        else {
            final int n9 = n7 - n5;
            final int n10 = n8 - n6;
            int n11;
            int n12;
            if (b2) {
                n11 = n9;
                n12 = 0;
            }
            else {
                n11 = 0;
                n12 = n10;
            }
            while (n < n3 && n2 < n4) {
                final int min = Math.min(n3, n + n9);
                final int min2 = Math.min(n4, n2 + n10);
                graphics.drawImage(image, n, n2, min, min2, n5, n6, n5 + min - n, n6 + min2 - n2, null);
                n += n11;
                n2 += n12;
            }
        }
    }
    
    private static void drawImage(final Image image, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (n3 - n <= 0 || n4 - n2 <= 0 || n7 - n5 <= 0 || n8 - n6 <= 0) {
            return;
        }
        graphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, null);
    }
    
    static {
        EMPTY_INSETS = new Insets(0, 0, 0, 0);
    }
    
    enum PaintType
    {
        CENTER, 
        TILE, 
        PAINT9_STRETCH, 
        PAINT9_TILE;
    }
}
