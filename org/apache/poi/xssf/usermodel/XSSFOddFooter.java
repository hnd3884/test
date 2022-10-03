package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFOddFooter extends XSSFHeaderFooter implements Footer
{
    protected XSSFOddFooter(final CTHeaderFooter headerFooter) {
        super(headerFooter);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getOddFooter();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetOddFooter();
        }
        else {
            this.getHeaderFooter().setOddFooter(text);
        }
    }
}
