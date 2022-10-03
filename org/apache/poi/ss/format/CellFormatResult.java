package org.apache.poi.ss.format;

import java.awt.Color;

public class CellFormatResult
{
    public final boolean applies;
    public final String text;
    public final Color textColor;
    
    public CellFormatResult(final boolean applies, final String text, final Color textColor) throws IllegalArgumentException {
        this.applies = applies;
        if (text == null) {
            throw new IllegalArgumentException("CellFormatResult text may not be null");
        }
        this.text = text;
        this.textColor = (applies ? textColor : null);
    }
}
