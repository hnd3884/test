package net.sf.jsqlparser.statement.create.index;

import java.util.Iterator;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class CreateIndex implements Statement
{
    private Table table;
    private Index index;
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Index getIndex() {
        return this.index;
    }
    
    public void setIndex(final Index index) {
        this.index = index;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE ");
        if (this.index.getType() != null) {
            buffer.append(this.index.getType());
            buffer.append(" ");
        }
        buffer.append("INDEX ");
        buffer.append(this.index.getName());
        buffer.append(" ON ");
        buffer.append(this.table.getFullyQualifiedName());
        if (this.index.getColumnsNames() != null) {
            buffer.append(" (");
            final Iterator iter = this.index.getColumnsNames().iterator();
            while (iter.hasNext()) {
                final String columnName = iter.next();
                buffer.append(columnName);
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
        }
        return buffer.toString();
    }
}
