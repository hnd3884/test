package com.lowagie.text;

import java.awt.Color;

public interface FontProvider
{
    boolean isRegistered(final String p0);
    
    Font getFont(final String p0, final String p1, final boolean p2, final float p3, final int p4, final Color p5);
}
