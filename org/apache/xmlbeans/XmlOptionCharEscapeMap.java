package org.apache.xmlbeans;

import java.util.HashMap;

public class XmlOptionCharEscapeMap
{
    public static final int PREDEF_ENTITY = 0;
    public static final int DECIMAL = 1;
    public static final int HEXADECIMAL = 2;
    private HashMap _charMap;
    private static final HashMap _predefEntities;
    
    public XmlOptionCharEscapeMap() {
        this._charMap = new HashMap();
    }
    
    public boolean containsChar(final char ch) {
        return this._charMap.containsKey(new Character(ch));
    }
    
    public void addMapping(final char ch, final int mode) throws XmlException {
        final Character theChar = new Character(ch);
        switch (mode) {
            case 0: {
                final String replString = XmlOptionCharEscapeMap._predefEntities.get(theChar);
                if (replString == null) {
                    throw new XmlException("XmlOptionCharEscapeMap.addMapping(): the PREDEF_ENTITY mode can only be used for the following characters: <, >, &, \" and '");
                }
                this._charMap.put(theChar, replString);
                break;
            }
            case 1: {
                this._charMap.put(theChar, "&#" + (int)ch + ";");
                break;
            }
            case 2: {
                final String hexCharPoint = Integer.toHexString(ch);
                this._charMap.put(theChar, "&#x" + hexCharPoint + ";");
                break;
            }
            default: {
                throw new XmlException("XmlOptionCharEscapeMap.addMapping(): mode must be PREDEF_ENTITY, DECIMAL or HEXADECIMAL");
            }
        }
    }
    
    public void addMappings(final char ch1, final char ch2, final int mode) throws XmlException {
        if (ch1 > ch2) {
            throw new XmlException("XmlOptionCharEscapeMap.addMappings(): ch1 must be <= ch2");
        }
        for (char c = ch1; c <= ch2; ++c) {
            this.addMapping(c, mode);
        }
    }
    
    public String getEscapedString(final char ch) {
        return this._charMap.get(new Character(ch));
    }
    
    static {
        (_predefEntities = new HashMap()).put(new Character('<'), "&lt;");
        XmlOptionCharEscapeMap._predefEntities.put(new Character('>'), "&gt;");
        XmlOptionCharEscapeMap._predefEntities.put(new Character('&'), "&amp;");
        XmlOptionCharEscapeMap._predefEntities.put(new Character('\''), "&apos;");
        XmlOptionCharEscapeMap._predefEntities.put(new Character('\"'), "&quot;");
    }
}
