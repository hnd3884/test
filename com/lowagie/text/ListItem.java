package com.lowagie.text;

public class ListItem extends Paragraph
{
    private static final long serialVersionUID = 1970670787169329006L;
    protected Chunk symbol;
    
    public ListItem() {
    }
    
    public ListItem(final float leading) {
        super(leading);
    }
    
    public ListItem(final Chunk chunk) {
        super(chunk);
    }
    
    public ListItem(final String string) {
        super(string);
    }
    
    public ListItem(final String string, final Font font) {
        super(string, font);
    }
    
    public ListItem(final float leading, final Chunk chunk) {
        super(leading, chunk);
    }
    
    public ListItem(final float leading, final String string) {
        super(leading, string);
    }
    
    public ListItem(final float leading, final String string, final Font font) {
        super(leading, string, font);
    }
    
    public ListItem(final Phrase phrase) {
        super(phrase);
    }
    
    @Override
    public int type() {
        return 15;
    }
    
    public void setListSymbol(final Chunk symbol) {
        if (this.symbol == null) {
            this.symbol = symbol;
            if (this.symbol.getFont().isStandardFont()) {
                this.symbol.setFont(this.font);
            }
        }
    }
    
    public void setIndentationLeft(final float indentation, final boolean autoindent) {
        if (autoindent) {
            this.setIndentationLeft(this.getListSymbol().getWidthPoint());
        }
        else {
            this.setIndentationLeft(indentation);
        }
    }
    
    public Chunk getListSymbol() {
        return this.symbol;
    }
}
