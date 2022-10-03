package com.lowagie.text;

import com.lowagie.text.factories.RomanAlphabetFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class List implements TextElementArray
{
    public static final boolean ORDERED = true;
    public static final boolean UNORDERED = false;
    public static final boolean NUMERICAL = false;
    public static final boolean ALPHABETICAL = true;
    public static final boolean UPPERCASE = false;
    public static final boolean LOWERCASE = true;
    protected ArrayList list;
    protected boolean numbered;
    protected boolean lettered;
    protected boolean lowercase;
    protected boolean autoindent;
    protected boolean alignindent;
    protected int first;
    protected Chunk symbol;
    protected String preSymbol;
    protected String postSymbol;
    protected float indentationLeft;
    protected float indentationRight;
    protected float symbolIndent;
    
    public List() {
        this(false, false);
    }
    
    public List(final float symbolIndent) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = 0.0f;
        this.indentationRight = 0.0f;
        this.symbolIndent = 0.0f;
        this.symbolIndent = symbolIndent;
    }
    
    public List(final boolean numbered) {
        this(numbered, false);
    }
    
    public List(final boolean numbered, final boolean lettered) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = 0.0f;
        this.indentationRight = 0.0f;
        this.symbolIndent = 0.0f;
        this.numbered = numbered;
        this.lettered = lettered;
        this.autoindent = true;
        this.alignindent = true;
    }
    
    public List(final boolean numbered, final float symbolIndent) {
        this(numbered, false, symbolIndent);
    }
    
    public List(final boolean numbered, final boolean lettered, final float symbolIndent) {
        this.list = new ArrayList();
        this.numbered = false;
        this.lettered = false;
        this.lowercase = false;
        this.autoindent = false;
        this.alignindent = false;
        this.first = 1;
        this.symbol = new Chunk("- ");
        this.preSymbol = "";
        this.postSymbol = ". ";
        this.indentationLeft = 0.0f;
        this.indentationRight = 0.0f;
        this.symbolIndent = 0.0f;
        this.numbered = numbered;
        this.lettered = lettered;
        this.symbolIndent = symbolIndent;
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            final Iterator i = this.list.iterator();
            while (i.hasNext()) {
                listener.add(i.next());
            }
            return true;
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 14;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        final Iterator i = this.list.iterator();
        while (i.hasNext()) {
            tmp.addAll(i.next().getChunks());
        }
        return tmp;
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof ListItem) {
            final ListItem item = (ListItem)o;
            if (this.numbered || this.lettered) {
                final Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
                final int index = this.first + this.list.size();
                if (this.lettered) {
                    chunk.append(RomanAlphabetFactory.getString(index, this.lowercase));
                }
                else {
                    chunk.append(String.valueOf(index));
                }
                chunk.append(this.postSymbol);
                item.setListSymbol(chunk);
            }
            else {
                item.setListSymbol(this.symbol);
            }
            item.setIndentationLeft(this.symbolIndent, this.autoindent);
            item.setIndentationRight(0.0f);
            return this.list.add(item);
        }
        if (o instanceof List) {
            final List nested = (List)o;
            nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
            --this.first;
            return this.list.add(nested);
        }
        return o instanceof String && this.add(new ListItem((String)o));
    }
    
    public void normalizeIndentation() {
        float max = 0.0f;
        for (final Element o : this.list) {
            if (o instanceof ListItem) {
                max = Math.max(max, ((ListItem)o).getIndentationLeft());
            }
        }
        for (final Element o : this.list) {
            if (o instanceof ListItem) {
                ((ListItem)o).setIndentationLeft(max);
            }
        }
    }
    
    public void setNumbered(final boolean numbered) {
        this.numbered = numbered;
    }
    
    public void setLettered(final boolean lettered) {
        this.lettered = lettered;
    }
    
    public void setLowercase(final boolean uppercase) {
        this.lowercase = uppercase;
    }
    
    public void setAutoindent(final boolean autoindent) {
        this.autoindent = autoindent;
    }
    
    public void setAlignindent(final boolean alignindent) {
        this.alignindent = alignindent;
    }
    
    public void setFirst(final int first) {
        this.first = first;
    }
    
    public void setListSymbol(final Chunk symbol) {
        this.symbol = symbol;
    }
    
    public void setListSymbol(final String symbol) {
        this.symbol = new Chunk(symbol);
    }
    
    public void setIndentationLeft(final float indentation) {
        this.indentationLeft = indentation;
    }
    
    public void setIndentationRight(final float indentation) {
        this.indentationRight = indentation;
    }
    
    public void setSymbolIndent(final float symbolIndent) {
        this.symbolIndent = symbolIndent;
    }
    
    public ArrayList getItems() {
        return this.list;
    }
    
    public int size() {
        return this.list.size();
    }
    
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
    
    public float getTotalLeading() {
        if (this.list.size() < 1) {
            return -1.0f;
        }
        final ListItem item = this.list.get(0);
        return item.getTotalLeading();
    }
    
    public boolean isNumbered() {
        return this.numbered;
    }
    
    public boolean isLettered() {
        return this.lettered;
    }
    
    public boolean isLowercase() {
        return this.lowercase;
    }
    
    public boolean isAutoindent() {
        return this.autoindent;
    }
    
    public boolean isAlignindent() {
        return this.alignindent;
    }
    
    public int getFirst() {
        return this.first;
    }
    
    public Chunk getSymbol() {
        return this.symbol;
    }
    
    public float getIndentationLeft() {
        return this.indentationLeft;
    }
    
    public float getIndentationRight() {
        return this.indentationRight;
    }
    
    public float getSymbolIndent() {
        return this.symbolIndent;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    public String getPostSymbol() {
        return this.postSymbol;
    }
    
    public void setPostSymbol(final String postSymbol) {
        this.postSymbol = postSymbol;
    }
    
    public String getPreSymbol() {
        return this.preSymbol;
    }
    
    public void setPreSymbol(final String preSymbol) {
        this.preSymbol = preSymbol;
    }
}
