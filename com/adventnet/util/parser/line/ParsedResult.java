package com.adventnet.util.parser.line;

import java.util.Hashtable;
import java.util.Properties;

public class ParsedResult
{
    Properties scalarValues;
    Hashtable table;
    
    ParsedResult() {
        this.scalarValues = null;
        this.table = null;
    }
    
    public void setResult(final Properties scalarValues) {
        this.scalarValues = scalarValues;
    }
    
    public Properties getResult() {
        return this.scalarValues;
    }
    
    public void setTableResult(final Hashtable table) {
        this.table = table;
    }
    
    public Hashtable getTableResult() {
        return this.table;
    }
}
