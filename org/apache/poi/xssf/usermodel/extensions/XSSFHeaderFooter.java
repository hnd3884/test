package org.apache.poi.xssf.usermodel.extensions;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.apache.poi.xssf.usermodel.helpers.HeaderFooterHelper;
import org.apache.poi.ss.usermodel.HeaderFooter;

public abstract class XSSFHeaderFooter implements HeaderFooter
{
    private HeaderFooterHelper helper;
    private CTHeaderFooter headerFooter;
    private boolean stripFields;
    
    public XSSFHeaderFooter(final CTHeaderFooter headerFooter) {
        this.headerFooter = headerFooter;
        this.helper = new HeaderFooterHelper();
    }
    
    @Internal
    public CTHeaderFooter getHeaderFooter() {
        return this.headerFooter;
    }
    
    public String getValue() {
        final String value = this.getText();
        if (value == null) {
            return "";
        }
        return value;
    }
    
    public boolean areFieldsStripped() {
        return this.stripFields;
    }
    
    public void setAreFieldsStripped(final boolean stripFields) {
        this.stripFields = stripFields;
    }
    
    public static String stripFields(final String text) {
        return org.apache.poi.hssf.usermodel.HeaderFooter.stripFields(text);
    }
    
    public abstract String getText();
    
    protected abstract void setText(final String p0);
    
    public String getCenter() {
        final String text = this.helper.getCenterSection(this.getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }
    
    public String getLeft() {
        final String text = this.helper.getLeftSection(this.getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }
    
    public String getRight() {
        final String text = this.helper.getRightSection(this.getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }
    
    public void setCenter(final String newCenter) {
        this.setText(this.helper.setCenterSection(this.getText(), newCenter));
    }
    
    public void setLeft(final String newLeft) {
        this.setText(this.helper.setLeftSection(this.getText(), newLeft));
    }
    
    public void setRight(final String newRight) {
        this.setText(this.helper.setRightSection(this.getText(), newRight));
    }
}
