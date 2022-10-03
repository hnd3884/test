package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.create.table.CreateTable;

public class CreateTableDeParser
{
    private StringBuilder buffer;
    
    public CreateTableDeParser(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final CreateTable createTable) {
        this.buffer.append("CREATE ");
        if (createTable.isUnlogged()) {
            this.buffer.append("UNLOGGED ");
        }
        String params = PlainSelect.getStringList(createTable.getCreateOptionsStrings(), false, false);
        if (!"".equals(params)) {
            this.buffer.append(params).append(' ');
        }
        this.buffer.append("TABLE ");
        if (createTable.isIfNotExists()) {
            this.buffer.append("IF NOT EXISTS ");
        }
        this.buffer.append(createTable.getTable().getFullyQualifiedName());
        if (createTable.getSelect() != null) {
            this.buffer.append(" AS ");
            if (createTable.isSelectParenthesis()) {
                this.buffer.append("(");
            }
            this.buffer.append(createTable.getSelect().toString());
            if (createTable.isSelectParenthesis()) {
                this.buffer.append(")");
            }
        }
        else if (createTable.getColumnDefinitions() != null) {
            this.buffer.append(" (");
            final Iterator<ColumnDefinition> iter = createTable.getColumnDefinitions().iterator();
            while (iter.hasNext()) {
                final ColumnDefinition columnDefinition = iter.next();
                this.buffer.append(columnDefinition.getColumnName());
                this.buffer.append(" ");
                this.buffer.append(columnDefinition.getColDataType().toString());
                if (columnDefinition.getColumnSpecStrings() != null) {
                    for (final String s : columnDefinition.getColumnSpecStrings()) {
                        this.buffer.append(" ");
                        this.buffer.append(s);
                    }
                }
                if (iter.hasNext()) {
                    this.buffer.append(", ");
                }
            }
            if (createTable.getIndexes() != null) {
                final Iterator<Index> iter2 = createTable.getIndexes().iterator();
                while (iter2.hasNext()) {
                    this.buffer.append(", ");
                    final Index index = iter2.next();
                    this.buffer.append(index.toString());
                }
            }
            this.buffer.append(")");
        }
        params = PlainSelect.getStringList(createTable.getTableOptionsStrings(), false, false);
        if (!"".equals(params)) {
            this.buffer.append(' ').append(params);
        }
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
