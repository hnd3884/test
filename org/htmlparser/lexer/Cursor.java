package org.htmlparser.lexer;

import org.htmlparser.util.sort.Ordered;
import java.io.Serializable;

public class Cursor implements Serializable, Ordered, Cloneable
{
    protected int mPosition;
    protected Page mPage;
    
    public Cursor(final Page page, final int offset) {
        this.mPage = page;
        this.mPosition = offset;
    }
    
    public Page getPage() {
        return this.mPage;
    }
    
    public int getPosition() {
        return this.mPosition;
    }
    
    public void setPosition(final int position) {
        this.mPosition = position;
    }
    
    public void advance() {
        ++this.mPosition;
    }
    
    public void retreat() {
        --this.mPosition;
        if (0 > this.mPosition) {
            this.mPosition = 0;
        }
    }
    
    public Cursor dup() {
        try {
            return (Cursor)this.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            return new Cursor(this.getPage(), this.getPosition());
        }
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(30);
        ret.append(this.getPosition());
        ret.append("[");
        if (null != this.mPage) {
            ret.append(this.mPage.row(this));
        }
        else {
            ret.append("?");
        }
        ret.append(",");
        if (null != this.mPage) {
            ret.append(this.mPage.column(this));
        }
        else {
            ret.append("?");
        }
        ret.append("]");
        return ret.toString();
    }
    
    public int compare(final Object that) {
        final Cursor r = (Cursor)that;
        return this.getPosition() - r.getPosition();
    }
}
