package javax.swing.plaf;

import java.awt.Font;

public class FontUIResource extends Font implements UIResource
{
    public FontUIResource(final String s, final int n, final int n2) {
        super(s, n, n2);
    }
    
    public FontUIResource(final Font font) {
        super(font);
    }
}
