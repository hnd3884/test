package net.sf.jsqlparser.util.deparser;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.drop.Drop;

public class DropDeParser
{
    private StringBuilder buffer;
    
    public DropDeParser(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Drop drop) {
        this.buffer.append("DROP ");
        this.buffer.append(drop.getType());
        if (drop.isIfExists()) {
            this.buffer.append(" IF EXISTS");
        }
        this.buffer.append(" ").append(drop.getName());
        if (drop.getParameters() != null && !drop.getParameters().isEmpty()) {
            this.buffer.append(" ").append(PlainSelect.getStringList(drop.getParameters()));
        }
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
