package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.CMapAwareDocumentFont;

public class GraphicsState
{
    Matrix ctm;
    float characterSpacing;
    float wordSpacing;
    float horizontalScaling;
    float leading;
    CMapAwareDocumentFont font;
    float fontSize;
    int renderMode;
    float rise;
    boolean knockout;
    
    public GraphicsState() {
        this.ctm = new Matrix();
        this.characterSpacing = 0.0f;
        this.wordSpacing = 0.0f;
        this.horizontalScaling = 1.0f;
        this.leading = 0.0f;
        this.font = null;
        this.fontSize = 0.0f;
        this.renderMode = 0;
        this.rise = 0.0f;
        this.knockout = true;
    }
    
    public GraphicsState(final GraphicsState source) {
        this.ctm = source.ctm;
        this.characterSpacing = source.characterSpacing;
        this.wordSpacing = source.wordSpacing;
        this.horizontalScaling = source.horizontalScaling;
        this.leading = source.leading;
        this.font = source.font;
        this.fontSize = source.fontSize;
        this.renderMode = source.renderMode;
        this.rise = source.rise;
        this.knockout = source.knockout;
    }
}
