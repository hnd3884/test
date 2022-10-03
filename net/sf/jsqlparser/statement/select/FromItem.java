package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;

public interface FromItem
{
    void accept(final FromItemVisitor p0);
    
    Alias getAlias();
    
    void setAlias(final Alias p0);
    
    Pivot getPivot();
    
    void setPivot(final Pivot p0);
}
