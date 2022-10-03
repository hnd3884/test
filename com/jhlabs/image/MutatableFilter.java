package com.jhlabs.image;

import java.awt.image.BufferedImageOp;

public interface MutatableFilter
{
    void mutate(final float p0, final BufferedImageOp p1, final boolean p2, final boolean p3);
}
