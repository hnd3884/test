package net.sf.jsqlparser.statement.select;

public class Offset
{
    private long offset;
    private boolean offsetJdbcParameter;
    private String offsetParam;
    
    public Offset() {
        this.offsetJdbcParameter = false;
        this.offsetParam = null;
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public String getOffsetParam() {
        return this.offsetParam;
    }
    
    public void setOffset(final long l) {
        this.offset = l;
    }
    
    public void setOffsetParam(final String s) {
        this.offsetParam = s;
    }
    
    public boolean isOffsetJdbcParameter() {
        return this.offsetJdbcParameter;
    }
    
    public void setOffsetJdbcParameter(final boolean b) {
        this.offsetJdbcParameter = b;
    }
    
    @Override
    public String toString() {
        return " OFFSET " + (this.offsetJdbcParameter ? "?" : Long.valueOf(this.offset)) + ((this.offsetParam != null) ? (" " + this.offsetParam) : "");
    }
}
