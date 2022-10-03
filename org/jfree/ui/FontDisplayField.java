package org.jfree.ui;

import java.util.ResourceBundle;
import java.awt.Font;
import javax.swing.JTextField;

public class FontDisplayField extends JTextField
{
    private Font displayFont;
    protected static final ResourceBundle localizationResources;
    
    static {
        localizationResources = ResourceBundle.getBundle("org.jfree.ui.LocalizationBundle");
    }
    
    public FontDisplayField(final Font font) {
        super("");
        this.setDisplayFont(font);
        this.setEnabled(false);
    }
    
    private String fontToString(final Font font) {
        if (font != null) {
            return String.valueOf(font.getFontName()) + ", " + font.getSize();
        }
        return FontDisplayField.localizationResources.getString("No_Font_Selected");
    }
    
    public Font getDisplayFont() {
        return this.displayFont;
    }
    
    public void setDisplayFont(final Font font) {
        this.displayFont = font;
        this.setText(this.fontToString(this.displayFont));
    }
}
