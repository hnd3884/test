package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;

public class XSSFFirstHeader extends XSSFHeaderFooter implements Header
{
    protected XSSFFirstHeader(final CTHeaderFooter headerFooter) {
        super(headerFooter);
        headerFooter.setDifferentFirst(true);
    }
    
    @Override
    public String getText() {
        return this.getHeaderFooter().getFirstHeader();
    }
    
    public void setText(final String text) {
        if (text == null) {
            this.getHeaderFooter().unsetFirstHeader();
            if (!this.getHeaderFooter().isSetFirstFooter()) {
                this.getHeaderFooter().unsetDifferentFirst();
            }
        }
        else {
            this.getHeaderFooter().setFirstHeader(text);
        }
    }
}
