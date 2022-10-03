package com.adventnet.util.parser.regex;

import java.util.ArrayList;

public class ColumnObject
{
    private String columnName;
    private ArrayList columnValues;
    
    public ColumnObject() {
        this.columnName = null;
        this.columnValues = null;
        this.columnValues = new ArrayList();
    }
    
    public String getName() {
        return this.columnName;
    }
    
    public void setName(final String columnName) {
        this.columnName = columnName;
    }
    
    public ArrayList getValues() {
        return this.columnValues;
    }
    
    public void setValues(final ArrayList columnValues) {
        this.columnValues = columnValues;
    }
    
    public void addColumnEntry(final String s) {
        this.columnValues.add(s);
    }
    
    public String removeColumnEntry(final int n) {
        return this.columnValues.remove(n);
    }
    
    public String getPropertyValue() throws RegExParserException {
        if (this.columnValues.size() == 1) {
            return this.columnValues.get(0);
        }
        if (this.columnValues.size() > 1) {
            throw new RegExParserException("More than 1 value present for the property");
        }
        return null;
    }
    
    public String getEntry(final int n) throws RegExParserException {
        if (n < this.columnValues.size()) {
            return this.columnValues.get(n);
        }
        throw new RegExParserException("Row " + n + " does not exist");
    }
}
