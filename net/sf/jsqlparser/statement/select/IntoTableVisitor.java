package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Table;

public interface IntoTableVisitor
{
    void visit(final Table p0);
}
