package com.sun.xml.internal.ws.util.xml;

public final class CDATA
{
    private String _text;
    
    public CDATA(final String text) {
        this._text = text;
    }
    
    public String getText() {
        return this._text;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CDATA)) {
            return false;
        }
        final CDATA cdata = (CDATA)obj;
        return this._text.equals(cdata._text);
    }
    
    @Override
    public int hashCode() {
        return this._text.hashCode();
    }
}
