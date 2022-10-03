package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;

@Internal
class XSSFBHeaderFooters
{
    private XSSFBHeaderFooter header;
    private XSSFBHeaderFooter footer;
    private XSSFBHeaderFooter headerEven;
    private XSSFBHeaderFooter footerEven;
    private XSSFBHeaderFooter headerFirst;
    private XSSFBHeaderFooter footerFirst;
    
    public static XSSFBHeaderFooters parse(final byte[] data) {
        final boolean diffOddEven = false;
        final boolean diffFirst = false;
        final boolean scaleWDoc = false;
        final boolean alignMargins = false;
        int offset = 2;
        final XSSFBHeaderFooters xssfbHeaderFooter = new XSSFBHeaderFooters();
        xssfbHeaderFooter.header = new XSSFBHeaderFooter("header", true);
        xssfbHeaderFooter.footer = new XSSFBHeaderFooter("footer", false);
        xssfbHeaderFooter.headerEven = new XSSFBHeaderFooter("evenHeader", true);
        xssfbHeaderFooter.footerEven = new XSSFBHeaderFooter("evenFooter", false);
        xssfbHeaderFooter.headerFirst = new XSSFBHeaderFooter("firstHeader", true);
        xssfbHeaderFooter.footerFirst = new XSSFBHeaderFooter("firstFooter", false);
        offset += readHeaderFooter(data, offset, xssfbHeaderFooter.header);
        offset += readHeaderFooter(data, offset, xssfbHeaderFooter.footer);
        offset += readHeaderFooter(data, offset, xssfbHeaderFooter.headerEven);
        offset += readHeaderFooter(data, offset, xssfbHeaderFooter.footerEven);
        offset += readHeaderFooter(data, offset, xssfbHeaderFooter.headerFirst);
        readHeaderFooter(data, offset, xssfbHeaderFooter.footerFirst);
        return xssfbHeaderFooter;
    }
    
    private static int readHeaderFooter(final byte[] data, final int offset, final XSSFBHeaderFooter headerFooter) {
        if (offset + 4 >= data.length) {
            return 0;
        }
        final StringBuilder sb = new StringBuilder();
        final int bytesRead = XSSFBUtils.readXLNullableWideString(data, offset, sb);
        headerFooter.setRawString(sb.toString());
        return bytesRead;
    }
    
    public XSSFBHeaderFooter getHeader() {
        return this.header;
    }
    
    public XSSFBHeaderFooter getFooter() {
        return this.footer;
    }
    
    public XSSFBHeaderFooter getHeaderEven() {
        return this.headerEven;
    }
    
    public XSSFBHeaderFooter getFooterEven() {
        return this.footerEven;
    }
    
    public XSSFBHeaderFooter getHeaderFirst() {
        return this.headerFirst;
    }
    
    public XSSFBHeaderFooter getFooterFirst() {
        return this.footerFirst;
    }
}
