package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFOddHeader extends XSSFHeaderFooter implements Header
{
    protected XSSFOddHeader(final CTHeaderFooter headerFooter) {
        super(headerFooter);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getOddHeader();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetOddHeader();
        }
        else {
            this.getHeaderFooter().setOddHeader(text);
        }
    }
}
