package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.ss.usermodel.DataFormat;

public class XSSFDataFormat implements DataFormat
{
    private final StylesTable stylesSource;
    
    protected XSSFDataFormat(final StylesTable stylesSource) {
        this.stylesSource = stylesSource;
    }
    
    public short getFormat(final String format) {
        int idx = BuiltinFormats.getBuiltinFormat(format);
        if (idx == -1) {
            idx = this.stylesSource.putNumberFormat(format);
        }
        return (short)idx;
    }
    
    public String getFormat(final short index) {
        String fmt = this.stylesSource.getNumberFormatAt(index);
        if (fmt == null) {
            fmt = BuiltinFormats.getBuiltinFormat((int)index);
        }
        return fmt;
    }
    
    public void putFormat(final short index, final String format) {
        this.stylesSource.putNumberFormat(index, format);
    }
}
