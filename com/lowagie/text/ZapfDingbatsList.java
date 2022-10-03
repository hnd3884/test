package com.lowagie.text;

public class ZapfDingbatsList extends List
{
    protected int zn;
    
    public ZapfDingbatsList(final int zn) {
        super(true);
        this.zn = zn;
        final float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }
    
    public ZapfDingbatsList(final int zn, final int symbolIndent) {
        super(true, (float)symbolIndent);
        this.zn = zn;
        final float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }
    
    public void setCharNumber(final int zn) {
        this.zn = zn;
    }
    
    public int getCharNumber() {
        return this.zn;
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof ListItem) {
            final ListItem item = (ListItem)o;
            final Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(String.valueOf((char)this.zn));
            chunk.append(this.postSymbol);
            item.setListSymbol(chunk);
            item.setIndentationLeft(this.symbolIndent, this.autoindent);
            item.setIndentationRight(0.0f);
            this.list.add(item);
        }
        else {
            if (o instanceof List) {
                final List nested = (List)o;
                nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
                --this.first;
                return this.list.add(nested);
            }
            if (o instanceof String) {
                return this.add(new ListItem((String)o));
            }
        }
        return false;
    }
}
