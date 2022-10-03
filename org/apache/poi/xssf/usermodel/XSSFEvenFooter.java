package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFEvenFooter extends XSSFHeaderFooter implements Footer
{
    protected XSSFEvenFooter(final CTHeaderFooter headerFooter) {
        super(headerFooter);
        headerFooter.setDifferentOddEven(true);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getEvenFooter();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetEvenFooter();
            if (!this.getHeaderFooter().isSetEvenHeader()) {
                this.getHeaderFooter().unsetDifferentOddEven();
            }
        }
        else {
            this.getHeaderFooter().setEvenFooter(text);
        }
    }
}
