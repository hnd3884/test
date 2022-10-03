package com.adventnet.util.parser.regex.table;

public class TableObject
{
    private TableRegEx regex;
    
    public TableObject() {
        this.regex = null;
    }
    
    public TableRegEx getRegExpression() {
        return this.regex;
    }
    
    public void setRegExpression(final TableRegEx regex) {
        this.regex = regex;
    }
}
