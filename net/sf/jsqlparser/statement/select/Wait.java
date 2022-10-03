package net.sf.jsqlparser.statement.select;

public class Wait
{
    private long timeout;
    
    public long getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public String toString() {
        return " WAIT " + this.timeout;
    }
}
