package net.sf.jsqlparser.util.cnfexpression;

import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;

public abstract class MultipleExpression implements Expression
{
    private final List<Expression> childlist;
    
    public MultipleExpression(final List<Expression> childlist) {
        this.childlist = childlist;
    }
    
    public int size() {
        return this.childlist.size();
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(new NullValue());
    }
    
    public List<Expression> getList() {
        return this.childlist;
    }
    
    public Expression getChild(final int index) {
        return this.childlist.get(index);
    }
    
    public Expression removeChild(final int index) {
        return this.childlist.remove(index);
    }
    
    public void setChild(final int index, final Expression express) {
        this.childlist.set(index, express);
    }
    
    public int getIndex(final Expression express) {
        return this.childlist.indexOf(express);
    }
    
    public void addChild(final int index, final Expression express) {
        this.childlist.add(index, express);
    }
    
    public abstract String getStringExpression();
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < this.size(); ++i) {
            sb.append(this.getChild(i));
            if (i != this.size() - 1) {
                sb.append(" ").append(this.getStringExpression()).append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
