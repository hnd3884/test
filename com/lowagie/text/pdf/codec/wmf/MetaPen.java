package com.lowagie.text.pdf.codec.wmf;

import java.io.IOException;
import java.awt.Color;

public class MetaPen extends MetaObject
{
    public static final int PS_SOLID = 0;
    public static final int PS_DASH = 1;
    public static final int PS_DOT = 2;
    public static final int PS_DASHDOT = 3;
    public static final int PS_DASHDOTDOT = 4;
    public static final int PS_NULL = 5;
    public static final int PS_INSIDEFRAME = 6;
    int style;
    int penWidth;
    Color color;
    
    public MetaPen() {
        this.style = 0;
        this.penWidth = 1;
        this.color = Color.black;
        this.type = 1;
    }
    
    public void init(final InputMeta in) throws IOException {
        this.style = in.readWord();
        this.penWidth = in.readShort();
        in.readWord();
        this.color = in.readColor();
    }
    
    public int getStyle() {
        return this.style;
    }
    
    public int getPenWidth() {
        return this.penWidth;
    }
    
    public Color getColor() {
        return this.color;
    }
}
