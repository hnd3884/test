package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.alter.Alter;

public class AlterDeParser
{
    private StringBuilder buffer;
    
    public AlterDeParser(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Alter alter) {
        this.buffer.append(alter.toString());
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
