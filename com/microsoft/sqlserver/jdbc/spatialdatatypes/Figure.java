package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Figure
{
    private byte figuresAttribute;
    private int pointOffset;
    
    public Figure(final byte figuresAttribute, final int pointOffset) {
        this.figuresAttribute = figuresAttribute;
        this.pointOffset = pointOffset;
    }
    
    public byte getFiguresAttribute() {
        return this.figuresAttribute;
    }
    
    public int getPointOffset() {
        return this.pointOffset;
    }
    
    public void setFiguresAttribute(final byte fa) {
        this.figuresAttribute = fa;
    }
}
