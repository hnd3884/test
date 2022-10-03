package org.apache.poi.xssf.binary;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

@Internal
class XSSFBRichTextString extends XSSFRichTextString
{
    private final String string;
    
    XSSFBRichTextString(final String string) {
        this.string = string;
    }
    
    @NotImplemented
    @Override
    public void applyFont(final int startIndex, final int endIndex, final short fontIndex) {
    }
    
    @NotImplemented
    @Override
    public void applyFont(final int startIndex, final int endIndex, final Font font) {
    }
    
    @NotImplemented
    @Override
    public void applyFont(final Font font) {
    }
    
    @NotImplemented
    @Override
    public void clearFormatting() {
    }
    
    @Override
    public String getString() {
        return this.string;
    }
    
    @Override
    public int length() {
        return this.string.length();
    }
    
    @NotImplemented
    @Override
    public int numFormattingRuns() {
        return 0;
    }
    
    @NotImplemented
    @Override
    public int getIndexOfFormattingRun(final int index) {
        return 0;
    }
    
    @NotImplemented
    @Override
    public void applyFont(final short fontIndex) {
    }
}
