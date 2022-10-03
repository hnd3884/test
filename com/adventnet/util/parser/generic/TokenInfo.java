package com.adventnet.util.parser.generic;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class TokenInfo
{
    static final int NONE = 0;
    static final int CUT = 1;
    static final int REPLACE = 2;
    Vector delimiterList;
    String version;
    Hashtable matchDefn;
    
    void addDelimiterList(final Vector vector) {
        this.delimiterList.addElement(vector);
    }
    
    Vector getDelimiterList() {
        return this.delimiterList;
    }
    
    TokenInfo() {
        this.delimiterList = null;
        this.version = null;
        this.matchDefn = null;
        this.matchDefn = new Hashtable();
        this.delimiterList = new Vector();
    }
    
    void addMatchDefinition(final String s, final String s2) {
        this.matchDefn.put(s, s2);
    }
    
    Enumeration getMatchDefinitions() {
        return this.matchDefn.elements();
    }
    
    String getMatchDefinition(final String s) {
        return this.matchDefn.get(s);
    }
}
