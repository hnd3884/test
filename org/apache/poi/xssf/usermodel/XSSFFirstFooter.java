package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFFirstFooter extends XSSFHeaderFooter implements Footer
{
    protected XSSFFirstFooter(final CTHeaderFooter headerFooter) {
        super(headerFooter);
        headerFooter.setDifferentFirst(true);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getFirstFooter();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetFirstFooter();
            if (!this.getHeaderFooter().isSetFirstHeader()) {
                this.getHeaderFooter().unsetDifferentFirst();
            }
        }
        else {
            this.getHeaderFooter().setFirstFooter(text);
        }
    }
}
