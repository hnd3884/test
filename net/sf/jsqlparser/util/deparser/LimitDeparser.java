package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.select.Limit;

public class LimitDeparser
{
    private final StringBuilder buffer;
    
    public LimitDeparser(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Limit limit) {
        this.buffer.append(" LIMIT ");
        if (limit.isLimitNull()) {
            this.buffer.append("NULL");
        }
        else {
            if (null != limit.getOffset()) {
                this.buffer.append(limit.getOffset()).append(", ");
            }
            if (null != limit.getRowCount()) {
                this.buffer.append(limit.getRowCount());
            }
        }
    }
}
