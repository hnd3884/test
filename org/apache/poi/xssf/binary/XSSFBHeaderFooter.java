package org.apache.poi.xssf.binary;

import org.apache.poi.xssf.usermodel.helpers.HeaderFooterHelper;
import org.apache.poi.util.Internal;

@Internal
class XSSFBHeaderFooter
{
    private static final HeaderFooterHelper HEADER_FOOTER_HELPER;
    private final String headerFooterTypeLabel;
    private final boolean isHeader;
    private String rawString;
    
    XSSFBHeaderFooter(final String headerFooterTypeLabel, final boolean isHeader) {
        this.headerFooterTypeLabel = headerFooterTypeLabel;
        this.isHeader = isHeader;
    }
    
    String getHeaderFooterTypeLabel() {
        return this.headerFooterTypeLabel;
    }
    
    String getRawString() {
        return this.rawString;
    }
    
    String getString() {
        final StringBuilder sb = new StringBuilder();
        final String left = XSSFBHeaderFooter.HEADER_FOOTER_HELPER.getLeftSection(this.rawString);
        final String center = XSSFBHeaderFooter.HEADER_FOOTER_HELPER.getCenterSection(this.rawString);
        final String right = XSSFBHeaderFooter.HEADER_FOOTER_HELPER.getRightSection(this.rawString);
        if (left != null && left.length() > 0) {
            sb.append(left);
        }
        if (center != null && center.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(center);
        }
        if (right != null && right.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(right);
        }
        return sb.toString();
    }
    
    void setRawString(final String rawString) {
        this.rawString = rawString;
    }
    
    boolean isHeader() {
        return this.isHeader;
    }
    
    static {
        HEADER_FOOTER_HELPER = new HeaderFooterHelper();
    }
}
