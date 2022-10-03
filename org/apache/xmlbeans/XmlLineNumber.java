package org.apache.xmlbeans;

public class XmlLineNumber extends XmlCursor.XmlBookmark
{
    private int _line;
    private int _column;
    private int _offset;
    
    public XmlLineNumber(final int line) {
        this(line, -1, -1);
    }
    
    public XmlLineNumber(final int line, final int column) {
        this(line, column, -1);
    }
    
    public XmlLineNumber(final int line, final int column, final int offset) {
        super(false);
        this._line = line;
        this._column = column;
        this._offset = offset;
    }
    
    public int getLine() {
        return this._line;
    }
    
    public int getColumn() {
        return this._column;
    }
    
    public int getOffset() {
        return this._offset;
    }
}
