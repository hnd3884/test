package com.adventnet.util.parser.regex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

public class ParserResponseObject extends HashMap
{
    public String getColumnName(final int n) throws RegExParserException {
        final ColumnObject columnObject = this.get(new Integer(n));
        if (columnObject == null) {
            throw new RegExParserException("column " + n + " does not exist");
        }
        return columnObject.getName();
    }
    
    public ColumnObject getColumnObject(final String s) {
        if (s != null) {
            final Iterator<Integer> iterator = this.keySet().iterator();
            while (iterator.hasNext()) {
                final ColumnObject columnObject = this.get(new Integer(iterator.next()));
                if (columnObject.getName().equals(s)) {
                    return columnObject;
                }
            }
        }
        return null;
    }
    
    public ArrayList getColumnValues(final String s) throws RegExParserException {
        final ColumnObject columnObject = this.getColumnObject(s);
        if (columnObject == null) {
            throw new RegExParserException("column " + s + " does not exist");
        }
        return columnObject.getValues();
    }
    
    public String getPropertyValue(final String s) throws RegExParserException {
        final ColumnObject columnObject = this.getColumnObject(s);
        if (columnObject == null) {
            throw new RegExParserException("property " + s + " does not exist");
        }
        return columnObject.getPropertyValue();
    }
    
    public String getCellValue(final int n, final int n2) throws RegExParserException {
        final ColumnObject columnObject = this.get(new Integer(n2));
        if (columnObject == null) {
            throw new RegExParserException("column " + n2 + " does not exist");
        }
        return columnObject.getEntry(n);
    }
}
