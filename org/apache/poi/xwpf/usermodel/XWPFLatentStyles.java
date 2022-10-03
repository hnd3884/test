package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;

public class XWPFLatentStyles
{
    protected XWPFStyles styles;
    private CTLatentStyles latentStyles;
    
    protected XWPFLatentStyles() {
    }
    
    protected XWPFLatentStyles(final CTLatentStyles latentStyles) {
        this(latentStyles, null);
    }
    
    protected XWPFLatentStyles(final CTLatentStyles latentStyles, final XWPFStyles styles) {
        this.latentStyles = latentStyles;
        this.styles = styles;
    }
    
    public int getNumberOfStyles() {
        return this.latentStyles.sizeOfLsdExceptionArray();
    }
    
    protected boolean isLatentStyle(final String latentStyleID) {
        for (final CTLsdException lsd : this.latentStyles.getLsdExceptionArray()) {
            if (lsd.getName().equals(latentStyleID)) {
                return true;
            }
        }
        return false;
    }
}
