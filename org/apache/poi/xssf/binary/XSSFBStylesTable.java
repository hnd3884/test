package org.apache.poi.xssf.binary;

import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.io.InputStream;
import java.util.List;
import java.util.SortedMap;
import org.apache.poi.util.Internal;

@Internal
public class XSSFBStylesTable extends XSSFBParser
{
    private final SortedMap<Short, String> numberFormats;
    private final List<Short> styleIds;
    private boolean inCellXFS;
    private boolean inFmts;
    
    public XSSFBStylesTable(final InputStream is) throws IOException {
        super(is);
        this.numberFormats = new TreeMap<Short, String>();
        this.styleIds = new ArrayList<Short>();
        this.parse();
    }
    
    String getNumberFormatString(final int idx) {
        final short numberFormatIdx = this.getNumberFormatIndex(idx);
        if (this.numberFormats.containsKey(numberFormatIdx)) {
            return this.numberFormats.get(numberFormatIdx);
        }
        return BuiltinFormats.getBuiltinFormat((int)numberFormatIdx);
    }
    
    short getNumberFormatIndex(final int idx) {
        return this.styleIds.get(idx);
    }
    
    @Override
    public void handleRecord(final int recordType, final byte[] data) throws XSSFBParseException {
        final XSSFBRecordType type = XSSFBRecordType.lookup(recordType);
        switch (type) {
            case BrtBeginCellXFs: {
                this.inCellXFS = true;
                break;
            }
            case BrtEndCellXFs: {
                this.inCellXFS = false;
                break;
            }
            case BrtXf: {
                if (this.inCellXFS) {
                    this.handleBrtXFInCellXF(data);
                    break;
                }
                break;
            }
            case BrtBeginFmts: {
                this.inFmts = true;
                break;
            }
            case BrtEndFmts: {
                this.inFmts = false;
                break;
            }
            case BrtFmt: {
                if (this.inFmts) {
                    this.handleFormat(data);
                    break;
                }
                break;
            }
        }
    }
    
    private void handleFormat(final byte[] data) {
        final int ifmt = data[0] & 0xFF;
        if (ifmt > 32767) {
            throw new POIXMLException("Format id must be a short");
        }
        final StringBuilder sb = new StringBuilder();
        XSSFBUtils.readXLWideString(data, 2, sb);
        final String fmt = sb.toString();
        this.numberFormats.put((short)ifmt, fmt);
    }
    
    private void handleBrtXFInCellXF(final byte[] data) {
        final int ifmtOffset = 2;
        final int ifmt = data[ifmtOffset] & 0xFF;
        this.styleIds.add((short)ifmt);
    }
}
