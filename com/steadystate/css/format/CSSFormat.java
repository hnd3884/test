package com.steadystate.css.format;

import java.util.Arrays;

public class CSSFormat
{
    private static final String NEW_LINE;
    private boolean rgbAsHex_;
    private boolean propertiesInSeparateLines_;
    private boolean useSourceStringValues_;
    private String propertiesIndent_;
    
    public CSSFormat() {
        this.propertiesIndent_ = "";
    }
    
    public boolean isRgbAsHex() {
        return this.rgbAsHex_;
    }
    
    public CSSFormat setRgbAsHex(final boolean rgbAsHex) {
        this.rgbAsHex_ = rgbAsHex;
        return this;
    }
    
    public boolean useSourceStringValues() {
        return this.useSourceStringValues_;
    }
    
    public CSSFormat setUseSourceStringValues(final boolean useSourceStringValues) {
        this.useSourceStringValues_ = useSourceStringValues;
        return this;
    }
    
    public boolean getPropertiesInSeparateLines() {
        return this.propertiesInSeparateLines_;
    }
    
    public String getPropertiesIndent() {
        return this.propertiesIndent_;
    }
    
    public CSSFormat setPropertiesInSeparateLines(final int anIndent) {
        this.propertiesInSeparateLines_ = (anIndent > -1);
        if (anIndent > 0) {
            final char[] chars = new char[anIndent];
            Arrays.fill(chars, ' ');
            this.propertiesIndent_ = new String(chars);
        }
        else {
            this.propertiesIndent_ = "";
        }
        return this;
    }
    
    public String getNewLine() {
        return CSSFormat.NEW_LINE;
    }
    
    static {
        NEW_LINE = System.getProperty("line.separator");
    }
}
