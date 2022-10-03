package com.sun.awt;

import java.awt.geom.Point2D;
import sun.awt.AWTAccessor;
import java.awt.Dimension;
import java.awt.Window;

public final class SecurityWarning
{
    private SecurityWarning() {
    }
    
    public static Dimension getSize(final Window window) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        if (window.getWarningString() == null) {
            throw new IllegalArgumentException("The window must have a non-null warning string.");
        }
        return AWTAccessor.getWindowAccessor().getSecurityWarningSize(window);
    }
    
    public static void setPosition(final Window window, final Point2D point2D, final float n, final float n2) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        if (window.getWarningString() == null) {
            throw new IllegalArgumentException("The window must have a non-null warning string.");
        }
        if (point2D == null) {
            throw new NullPointerException("The point argument must not be null");
        }
        if (n < 0.0f || n > 1.0f) {
            throw new IllegalArgumentException("alignmentX must be in the range [0.0f ... 1.0f].");
        }
        if (n2 < 0.0f || n2 > 1.0f) {
            throw new IllegalArgumentException("alignmentY must be in the range [0.0f ... 1.0f].");
        }
        AWTAccessor.getWindowAccessor().setSecurityWarningPosition(window, point2D, n, n2);
    }
}
