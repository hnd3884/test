package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Function;

public class FunctionItem
{
    private Function function;
    private Alias alias;
    
    public Alias getAlias() {
        return this.alias;
    }
    
    public void setAlias(final Alias alias) {
        this.alias = alias;
    }
    
    public Function getFunction() {
        return this.function;
    }
    
    public void setFunction(final Function function) {
        this.function = function;
    }
    
    @Override
    public String toString() {
        return this.function + ((this.alias != null) ? this.alias.toString() : "");
    }
}
