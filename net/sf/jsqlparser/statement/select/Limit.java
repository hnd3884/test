package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Expression;

public class Limit
{
    private Expression rowCount;
    private Expression offset;
    private boolean limitAll;
    private boolean limitNull;
    
    public Limit() {
        this.limitNull = false;
    }
    
    public Expression getOffset() {
        return this.offset;
    }
    
    public Expression getRowCount() {
        return this.rowCount;
    }
    
    public void setOffset(final Expression l) {
        this.offset = l;
    }
    
    public void setRowCount(final Expression l) {
        this.rowCount = l;
    }
    
    public boolean isLimitAll() {
        return this.limitAll;
    }
    
    public void setLimitAll(final boolean b) {
        this.limitAll = b;
    }
    
    public boolean isLimitNull() {
        return this.limitNull;
    }
    
    public void setLimitNull(final boolean b) {
        this.limitNull = b;
    }
    
    @Override
    public String toString() {
        String retVal = " LIMIT ";
        if (this.limitNull) {
            retVal += "NULL";
        }
        else {
            if (null != this.offset) {
                retVal = retVal + this.offset + ", ";
            }
            if (null != this.rowCount) {
                retVal += this.rowCount;
            }
        }
        return retVal;
    }
}
