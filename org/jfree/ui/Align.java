package org.jfree.ui;

import java.awt.geom.Rectangle2D;

public final class Align
{
    public static final int CENTER = 0;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP_LEFT = 5;
    public static final int TOP_RIGHT = 9;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_RIGHT = 10;
    public static final int FIT_HORIZONTAL = 12;
    public static final int FIT_VERTICAL = 3;
    public static final int FIT = 15;
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 4;
    public static final int EAST = 8;
    public static final int NORTH_WEST = 5;
    public static final int NORTH_EAST = 9;
    public static final int SOUTH_WEST = 6;
    public static final int SOUTH_EAST = 10;
    
    private Align() {
    }
    
    public static void align(final Rectangle2D rect, final Rectangle2D frame, final int align) {
        double x = frame.getCenterX() - rect.getWidth() / 2.0;
        double y = frame.getCenterY() - rect.getHeight() / 2.0;
        double w = rect.getWidth();
        double h = rect.getHeight();
        if ((align & 0x3) == 0x3) {
            h = frame.getHeight();
        }
        if ((align & 0xC) == 0xC) {
            w = frame.getWidth();
        }
        if ((align & 0x1) == 0x1) {
            y = frame.getMinY();
        }
        if ((align & 0x2) == 0x2) {
            y = frame.getMaxY() - h;
        }
        if ((align & 0x4) == 0x4) {
            x = frame.getX();
        }
        if ((align & 0x8) == 0x8) {
            x = frame.getMaxX() - w;
        }
        rect.setRect(x, y, w, h);
    }
}
