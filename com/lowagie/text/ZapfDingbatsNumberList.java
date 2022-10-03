package com.lowagie.text;

public class ZapfDingbatsNumberList extends List
{
    protected int type;
    
    public ZapfDingbatsNumberList(final int type) {
        super(true);
        this.type = type;
        final float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }
    
    public ZapfDingbatsNumberList(final int type, final int symbolIndent) {
        super(true, (float)symbolIndent);
        this.type = type;
        final float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof ListItem) {
            final ListItem item = (ListItem)o;
            final Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            switch (this.type) {
                case 0: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 171)));
                    break;
                }
                case 1: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 181)));
                    break;
                }
                case 2: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 191)));
                    break;
                }
                default: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 201)));
                    break;
                }
            }
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
