package net.sf.jsqlparser.statement;

public interface Statement
{
    void accept(final StatementVisitor p0);
}
