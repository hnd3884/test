package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.Font;

public abstract class TextSource
{
    public static final boolean WITHOUT_CONTEXT = false;
    public static final boolean WITH_CONTEXT = true;
    
    public abstract char[] getChars();
    
    public abstract int getStart();
    
    public abstract int getLength();
    
    public abstract int getContextStart();
    
    public abstract int getContextLength();
    
    public abstract int getLayoutFlags();
    
    public abstract int getBidiLevel();
    
    public abstract Font getFont();
    
    public abstract FontRenderContext getFRC();
    
    public abstract CoreMetrics getCoreMetrics();
    
    public abstract TextSource getSubSource(final int p0, final int p1, final int p2);
    
    public abstract String toString(final boolean p0);
}
