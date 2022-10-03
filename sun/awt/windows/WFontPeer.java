package sun.awt.windows;

import sun.awt.PlatformFont;

final class WFontPeer extends PlatformFont
{
    private String textComponentFontName;
    
    public WFontPeer(final String s, final int n) {
        super(s, n);
        if (this.fontConfig != null) {
            this.textComponentFontName = ((WFontConfiguration)this.fontConfig).getTextComponentFontName(this.familyName, n);
        }
    }
    
    @Override
    protected char getMissingGlyphCharacter() {
        return '\u2751';
    }
    
    private static native void initIDs();
    
    static {
        initIDs();
    }
}
