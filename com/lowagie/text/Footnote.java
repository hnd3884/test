package com.lowagie.text;

import java.util.HashMap;

public class Footnote extends Phrase
{
    public static final int TEXT = 0;
    public static final String CONTENT = "content";
    public static final String FONT = "font";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    protected int footnoteType;
    protected HashMap footnoteAttributes;
    
    public Footnote() {
        this.footnoteAttributes = new HashMap();
    }
    
    public Footnote(final Chunk chunk) {
        super(chunk);
        this.footnoteAttributes = new HashMap();
    }
    
    public Footnote(final String text, final Font font) {
        super(text, font);
        this.footnoteAttributes = new HashMap();
    }
    
    public Footnote(final String text) {
        super(text);
        this.footnoteAttributes = new HashMap();
    }
    
    @Override
    public int type() {
        return 56;
    }
    
    public int footnoteType() {
        return this.footnoteType;
    }
}
