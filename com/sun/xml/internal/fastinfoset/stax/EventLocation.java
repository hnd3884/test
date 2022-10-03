package com.sun.xml.internal.fastinfoset.stax;

import javax.xml.stream.Location;

public class EventLocation implements Location
{
    String _systemId;
    String _publicId;
    int _column;
    int _line;
    int _charOffset;
    
    EventLocation() {
        this._systemId = null;
        this._publicId = null;
        this._column = -1;
        this._line = -1;
        this._charOffset = -1;
    }
    
    public static Location getNilLocation() {
        return new EventLocation();
    }
    
    @Override
    public int getLineNumber() {
        return this._line;
    }
    
    @Override
    public int getColumnNumber() {
        return this._column;
    }
    
    @Override
    public int getCharacterOffset() {
        return this._charOffset;
    }
    
    @Override
    public String getPublicId() {
        return this._publicId;
    }
    
    @Override
    public String getSystemId() {
        return this._systemId;
    }
    
    public void setLineNumber(final int line) {
        this._line = line;
    }
    
    public void setColumnNumber(final int col) {
        this._column = col;
    }
    
    public void setCharacterOffset(final int offset) {
        this._charOffset = offset;
    }
    
    public void setPublicId(final String id) {
        this._publicId = id;
    }
    
    public void setSystemId(final String id) {
        this._systemId = id;
    }
    
    @Override
    public String toString() {
        final StringBuffer sbuffer = new StringBuffer();
        sbuffer.append("Line number = " + this._line);
        sbuffer.append("\n");
        sbuffer.append("Column number = " + this._column);
        sbuffer.append("\n");
        sbuffer.append("System Id = " + this._systemId);
        sbuffer.append("\n");
        sbuffer.append("Public Id = " + this._publicId);
        sbuffer.append("\n");
        sbuffer.append("CharacterOffset = " + this._charOffset);
        sbuffer.append("\n");
        return sbuffer.toString();
    }
}
