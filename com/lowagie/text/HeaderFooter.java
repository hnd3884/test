package com.lowagie.text;

public class HeaderFooter extends Rectangle
{
    private boolean numbered;
    private Phrase before;
    private int pageN;
    private Phrase after;
    private int alignment;
    
    public HeaderFooter(final Phrase before, final Phrase after) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.before = null;
        this.after = null;
        this.setBorder(3);
        this.setBorderWidth(1.0f);
        this.numbered = true;
        this.before = before;
        this.after = after;
    }
    
    public HeaderFooter(final Phrase before, final boolean numbered) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.before = null;
        this.after = null;
        this.setBorder(3);
        this.setBorderWidth(1.0f);
        this.numbered = numbered;
        this.before = before;
    }
    
    public boolean isNumbered() {
        return this.numbered;
    }
    
    public Phrase getBefore() {
        return this.before;
    }
    
    public Phrase getAfter() {
        return this.after;
    }
    
    public void setPageNumber(final int pageN) {
        this.pageN = pageN;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public Paragraph paragraph() {
        final Paragraph paragraph = new Paragraph(this.before.getLeading());
        paragraph.add(this.before);
        if (this.numbered) {
            paragraph.addSpecial(new Chunk(String.valueOf(this.pageN), this.before.getFont()));
        }
        if (this.after != null) {
            paragraph.addSpecial(this.after);
        }
        paragraph.setAlignment(this.alignment);
        return paragraph;
    }
    
    public int alignment() {
        return this.alignment;
    }
}
