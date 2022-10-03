package net.sf.jsqlparser.statement.select;

public interface SelectItem
{
    void accept(final SelectItemVisitor p0);
}
