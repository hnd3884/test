package net.sf.jsqlparser.expression;

public interface Expression
{
    void accept(final ExpressionVisitor p0);
}
