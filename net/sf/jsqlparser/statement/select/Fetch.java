package net.sf.jsqlparser.statement.select;

public class Fetch
{
    private long rowCount;
    private boolean fetchJdbcParameter;
    private boolean isFetchParamFirst;
    private String fetchParam;
    
    public Fetch() {
        this.fetchJdbcParameter = false;
        this.isFetchParamFirst = false;
        this.fetchParam = "ROW";
    }
    
    public long getRowCount() {
        return this.rowCount;
    }
    
    public void setRowCount(final long l) {
        this.rowCount = l;
    }
    
    public boolean isFetchJdbcParameter() {
        return this.fetchJdbcParameter;
    }
    
    public String getFetchParam() {
        return this.fetchParam;
    }
    
    public boolean isFetchParamFirst() {
        return this.isFetchParamFirst;
    }
    
    public void setFetchJdbcParameter(final boolean b) {
        this.fetchJdbcParameter = b;
    }
    
    public void setFetchParam(final String s) {
        this.fetchParam = s;
    }
    
    public void setFetchParamFirst(final boolean b) {
        this.isFetchParamFirst = b;
    }
    
    @Override
    public String toString() {
        return " FETCH " + (this.isFetchParamFirst ? "FIRST" : "NEXT") + " " + (this.fetchJdbcParameter ? "?" : Long.toString(this.rowCount)) + " " + this.fetchParam + " ONLY";
    }
}
