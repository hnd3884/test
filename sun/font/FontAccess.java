package sun.font;

import java.awt.Font;

public abstract class FontAccess
{
    private static FontAccess access;
    
    public static synchronized void setFontAccess(final FontAccess access) {
        if (FontAccess.access != null) {
            throw new InternalError("Attempt to set FontAccessor twice");
        }
        FontAccess.access = access;
    }
    
    public static synchronized FontAccess getFontAccess() {
        return FontAccess.access;
    }
    
    public abstract Font2D getFont2D(final Font p0);
    
    public abstract void setFont2D(final Font p0, final Font2DHandle p1);
    
    public abstract void setCreatedFont(final Font p0);
    
    public abstract boolean isCreatedFont(final Font p0);
}
