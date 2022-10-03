package org.jfree.ui;

import java.awt.Dimension;

public interface ExtendedDrawable extends Drawable
{
    Dimension getPreferredSize();
    
    boolean isPreserveAspectRatio();
}
