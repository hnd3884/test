package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.DifferentialStyleProvider;

public class XSSFDxfStyleProvider implements DifferentialStyleProvider
{
    private final IndexedColorMap colorMap;
    private final BorderFormatting border;
    private final FontFormatting font;
    private final ExcelNumberFormat number;
    private final PatternFormatting fill;
    private final int stripeSize;
    
    public XSSFDxfStyleProvider(final CTDxf dxf, final int stripeSize, final IndexedColorMap colorMap) {
        this.stripeSize = stripeSize;
        this.colorMap = colorMap;
        if (dxf == null) {
            this.border = null;
            this.font = null;
            this.number = null;
            this.fill = null;
        }
        else {
            this.border = (BorderFormatting)(dxf.isSetBorder() ? new XSSFBorderFormatting(dxf.getBorder(), colorMap) : null);
            this.font = (FontFormatting)(dxf.isSetFont() ? new XSSFFontFormatting(dxf.getFont(), colorMap) : null);
            if (dxf.isSetNumFmt()) {
                final CTNumFmt numFmt = dxf.getNumFmt();
                this.number = new ExcelNumberFormat((int)numFmt.getNumFmtId(), numFmt.getFormatCode());
            }
            else {
                this.number = null;
            }
            this.fill = (PatternFormatting)(dxf.isSetFill() ? new XSSFPatternFormatting(dxf.getFill(), colorMap) : null);
        }
    }
    
    public BorderFormatting getBorderFormatting() {
        return this.border;
    }
    
    public FontFormatting getFontFormatting() {
        return this.font;
    }
    
    public ExcelNumberFormat getNumberFormat() {
        return this.number;
    }
    
    public PatternFormatting getPatternFormatting() {
        return this.fill;
    }
    
    public int getStripeSize() {
        return this.stripeSize;
    }
}
