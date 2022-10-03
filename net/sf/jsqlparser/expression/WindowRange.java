package net.sf.jsqlparser.expression;

public class WindowRange
{
    private WindowOffset start;
    private WindowOffset end;
    
    public WindowOffset getEnd() {
        return this.end;
    }
    
    public void setEnd(final WindowOffset end) {
        this.end = end;
    }
    
    public WindowOffset getStart() {
        return this.start;
    }
    
    public void setStart(final WindowOffset start) {
        this.start = start;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" BETWEEN");
        buffer.append(this.start);
        buffer.append(" AND");
        buffer.append(this.end);
        return buffer.toString();
    }
}
