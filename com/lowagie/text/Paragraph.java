package com.lowagie.text;

public class Paragraph extends Phrase
{
    private static final long serialVersionUID = 7852314969733375514L;
    protected int alignment;
    protected float multipliedLeading;
    protected float indentationLeft;
    protected float indentationRight;
    private float firstLineIndent;
    protected float spacingBefore;
    protected float spacingAfter;
    private float extraParagraphSpace;
    protected boolean keeptogether;
    
    public Paragraph() {
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final float leading) {
        super(leading);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final Chunk chunk) {
        super(chunk);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final float leading, final Chunk chunk) {
        super(leading, chunk);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final String string) {
        super(string);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final String string, final Font font) {
        super(string, font);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final float leading, final String string) {
        super(leading, string);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final float leading, final String string, final Font font) {
        super(leading, string, font);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
    }
    
    public Paragraph(final Phrase phrase) {
        super(phrase);
        this.alignment = -1;
        this.multipliedLeading = 0.0f;
        this.firstLineIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.keeptogether = false;
        if (phrase instanceof Paragraph) {
            final Paragraph p = (Paragraph)phrase;
            this.setAlignment(p.alignment);
            this.setLeading(phrase.getLeading(), p.multipliedLeading);
            this.setIndentationLeft(p.getIndentationLeft());
            this.setIndentationRight(p.getIndentationRight());
            this.setFirstLineIndent(p.getFirstLineIndent());
            this.setSpacingAfter(p.spacingAfter());
            this.setSpacingBefore(p.spacingBefore());
            this.setExtraParagraphSpace(p.getExtraParagraphSpace());
        }
    }
    
    @Override
    public int type() {
        return 12;
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof com.lowagie.text.List) {
            final com.lowagie.text.List list = (com.lowagie.text.List)o;
            list.setIndentationLeft(list.getIndentationLeft() + this.indentationLeft);
            list.setIndentationRight(this.indentationRight);
            return super.add(list);
        }
        if (o instanceof Image) {
            super.addSpecial(o);
            return true;
        }
        if (o instanceof Paragraph) {
            super.add(o);
            final List chunks = this.getChunks();
            if (!chunks.isEmpty()) {
                final Chunk tmp = chunks.get(chunks.size() - 1);
                super.add(new Chunk("\n", tmp.getFont()));
            }
            else {
                super.add(Chunk.NEWLINE);
            }
            return true;
        }
        return super.add(o);
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public void setAlignment(final String alignment) {
        if ("Center".equalsIgnoreCase(alignment)) {
            this.alignment = 1;
            return;
        }
        if ("Right".equalsIgnoreCase(alignment)) {
            this.alignment = 2;
            return;
        }
        if ("Justify".equalsIgnoreCase(alignment)) {
            this.alignment = 3;
            return;
        }
        if ("JustifyAll".equalsIgnoreCase(alignment)) {
            this.alignment = 8;
            return;
        }
        this.alignment = 0;
    }
    
    @Override
    public void setLeading(final float fixedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = 0.0f;
    }
    
    public void setMultipliedLeading(final float multipliedLeading) {
        this.leading = 0.0f;
        this.multipliedLeading = multipliedLeading;
    }
    
    public void setLeading(final float fixedLeading, final float multipliedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }
    
    public void setIndentationLeft(final float indentation) {
        this.indentationLeft = indentation;
    }
    
    public void setIndentationRight(final float indentation) {
        this.indentationRight = indentation;
    }
    
    public void setFirstLineIndent(final float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }
    
    public void setSpacingBefore(final float spacing) {
        this.spacingBefore = spacing;
    }
    
    public void setSpacingAfter(final float spacing) {
        this.spacingAfter = spacing;
    }
    
    public void setKeepTogether(final boolean keeptogether) {
        this.keeptogether = keeptogether;
    }
    
    public boolean getKeepTogether() {
        return this.keeptogether;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }
    
    public float getTotalLeading() {
        final float m = (this.font == null) ? (12.0f * this.multipliedLeading) : this.font.getCalculatedLeading(this.multipliedLeading);
        if (m > 0.0f && !this.hasLeading()) {
            return m;
        }
        return this.getLeading() + m;
    }
    
    public float getIndentationLeft() {
        return this.indentationLeft;
    }
    
    public float getIndentationRight() {
        return this.indentationRight;
    }
    
    public float getFirstLineIndent() {
        return this.firstLineIndent;
    }
    
    public float getSpacingBefore() {
        return this.spacingBefore;
    }
    
    public float getSpacingAfter() {
        return this.spacingAfter;
    }
    
    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }
    
    public void setExtraParagraphSpace(final float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }
    
    @Deprecated
    public float spacingBefore() {
        return this.getSpacingBefore();
    }
    
    @Deprecated
    public float spacingAfter() {
        return this.spacingAfter;
    }
}
