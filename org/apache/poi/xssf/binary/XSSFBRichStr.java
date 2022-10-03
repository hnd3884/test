package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;

@Internal
class XSSFBRichStr
{
    private final String string;
    private final String phoneticString;
    
    public static XSSFBRichStr build(final byte[] bytes, final int offset) throws XSSFBParseException {
        final byte first = bytes[offset];
        final boolean dwSizeStrRunExists = (first >> 7 & 0x1) == 0x1;
        final boolean phoneticExists = (first >> 6 & 0x1) == 0x1;
        final StringBuilder sb = new StringBuilder();
        final int read = XSSFBUtils.readXLWideString(bytes, offset + 1, sb);
        return new XSSFBRichStr(sb.toString(), "");
    }
    
    XSSFBRichStr(final String string, final String phoneticString) {
        this.string = string;
        this.phoneticString = phoneticString;
    }
    
    public String getString() {
        return this.string;
    }
}
