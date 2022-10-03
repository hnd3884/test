package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFEvenHeader extends XSSFHeaderFooter implements Header
{
    protected XSSFEvenHeader(final CTHeaderFooter headerFooter) {
        super(headerFooter);
        headerFooter.setDifferentOddEven(true);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getEvenHeader();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetEvenHeader();
            if (!this.getHeaderFooter().isSetEvenFooter()) {
                this.getHeaderFooter().unsetDifferentOddEven();
            }
        }
        else {
            this.getHeaderFooter().setEvenHeader(text);
        }
    }
}
