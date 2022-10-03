package com.lowagie.text;

import com.lowagie.text.factories.GreekAlphabetFactory;

public class GreekList extends List
{
    public GreekList() {
        super(true);
        this.setGreekFont();
    }
    
    public GreekList(final int symbolIndent) {
        super(true, (float)symbolIndent);
        this.setGreekFont();
    }
    
    public GreekList(final boolean greeklower, final int symbolIndent) {
        super(true, (float)symbolIndent);
        this.lowercase = greeklower;
        this.setGreekFont();
    }
    
    protected void setGreekFont() {
        final float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("Symbol", fontsize, 0));
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof ListItem) {
            final ListItem item = (ListItem)o;
            final Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(GreekAlphabetFactory.getString(this.first + this.list.size(), this.lowercase));
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
