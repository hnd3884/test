package sun.font;

import java.awt.FontFormatException;
import java.io.File;
import java.awt.Font;

public interface FontManager
{
    public static final int NO_FALLBACK = 0;
    public static final int PHYSICAL_FALLBACK = 1;
    public static final int LOGICAL_FALLBACK = 2;
    
    boolean registerFont(final Font p0);
    
    void deRegisterBadFont(final Font2D p0);
    
    Font2D findFont2D(final String p0, final int p1, final int p2);
    
    Font2D createFont2D(final File p0, final int p1, final boolean p2, final CreatedFontTracker p3) throws FontFormatException;
    
    boolean usingPerAppContextComposites();
    
    Font2DHandle getNewComposite(final String p0, final int p1, final Font2DHandle p2);
    
    void preferLocaleFonts();
    
    void preferProportionalFonts();
}
