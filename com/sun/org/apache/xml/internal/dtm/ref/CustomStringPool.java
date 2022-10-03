package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.HashMap;
import java.util.Map;

public class CustomStringPool extends DTMStringPool
{
    final Map<String, Integer> m_stringToInt;
    public static final int NULL = -1;
    
    public CustomStringPool() {
        this.m_stringToInt = new HashMap<String, Integer>();
    }
    
    @Override
    public void removeAllElements() {
        this.m_intToString.removeAllElements();
        if (this.m_stringToInt != null) {
            this.m_stringToInt.clear();
        }
    }
    
    @Override
    public String indexToString(final int i) throws ArrayIndexOutOfBoundsException {
        return this.m_intToString.elementAt(i);
    }
    
    @Override
    public int stringToIndex(final String s) {
        if (s == null) {
            return -1;
        }
        Integer iobj = this.m_stringToInt.get(s);
        if (iobj == null) {
            this.m_intToString.addElement(s);
            iobj = this.m_intToString.size();
            this.m_stringToInt.put(s, iobj);
        }
        return iobj;
    }
}
