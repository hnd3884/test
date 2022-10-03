package sun.font;

import java.util.Locale;
import java.util.TreeMap;
import java.awt.Font;

public interface FontManagerForSGE extends FontManager
{
    Font[] getCreatedFonts();
    
    TreeMap<String, String> getCreatedFontFamilyNames();
    
    Font[] getAllInstalledFonts();
    
    String[] getInstalledFontFamilyNames(final Locale p0);
    
    void useAlternateFontforJALocales();
}
