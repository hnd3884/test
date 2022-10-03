package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.index.CreateIndex;

public class CreateIndexDeParser
{
    private StringBuilder buffer;
    
    public CreateIndexDeParser(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final CreateIndex createIndex) {
        final Index index = createIndex.getIndex();
        this.buffer.append("CREATE ");
        if (index.getType() != null) {
            this.buffer.append(index.getType());
            this.buffer.append(" ");
        }
        this.buffer.append("INDEX ");
        this.buffer.append(index.getName());
        this.buffer.append(" ON ");
        this.buffer.append(createIndex.getTable().getFullyQualifiedName());
        if (index.getColumnsNames() != null) {
            this.buffer.append(" (");
            final Iterator iter = index.getColumnsNames().iterator();
            while (iter.hasNext()) {
                final String columnName = iter.next();
                this.buffer.append(columnName);
                if (iter.hasNext()) {
                    this.buffer.append(", ");
                }
            }
            this.buffer.append(")");
        }
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
