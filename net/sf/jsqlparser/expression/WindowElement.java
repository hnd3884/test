package net.sf.jsqlparser.expression;

public class WindowElement
{
    private Type type;
    private WindowOffset offset;
    private WindowRange range;
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(final Type type) {
        this.type = type;
    }
    
    public WindowOffset getOffset() {
        return this.offset;
    }
    
    public void setOffset(final WindowOffset offset) {
        this.offset = offset;
    }
    
    public WindowRange getRange() {
        return this.range;
    }
    
    public void setRange(final WindowRange range) {
        this.range = range;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(this.type.toString());
        if (this.offset != null) {
            buffer.append(this.offset.toString());
        }
        else if (this.range != null) {
            buffer.append(this.range.toString());
        }
        return buffer.toString();
    }
    
    public enum Type
    {
        ROWS, 
        RANGE;
    }
}
