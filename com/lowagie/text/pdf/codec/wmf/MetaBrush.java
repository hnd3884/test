package com.lowagie.text.pdf.codec.wmf;

import java.io.IOException;
import java.awt.Color;

public class MetaBrush extends MetaObject
{
    public static final int BS_SOLID = 0;
    public static final int BS_NULL = 1;
    public static final int BS_HATCHED = 2;
    public static final int BS_PATTERN = 3;
    public static final int BS_DIBPATTERN = 5;
    public static final int HS_HORIZONTAL = 0;
    public static final int HS_VERTICAL = 1;
    public static final int HS_FDIAGONAL = 2;
    public static final int HS_BDIAGONAL = 3;
    public static final int HS_CROSS = 4;
    public static final int HS_DIAGCROSS = 5;
    int style;
    int hatch;
    Color color;
    
    public MetaBrush() {
        this.style = 0;
        this.color = Color.white;
        this.type = 2;
    }
    
    public void init(final InputMeta in) throws IOException {
        this.style = in.readWord();
        this.color = in.readColor();
        this.hatch = in.readWord();
    }
    
    public int getStyle() {
        return this.style;
    }
    
    public int getHatch() {
        return this.hatch;
    }
    
    public Color getColor() {
        return this.color;
    }
}
