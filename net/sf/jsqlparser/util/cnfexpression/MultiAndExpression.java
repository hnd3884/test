package net.sf.jsqlparser.util.cnfexpression;

import net.sf.jsqlparser.expression.Expression;
import java.util.List;

public final class MultiAndExpression extends MultipleExpression
{
    public MultiAndExpression(final List<Expression> childlist) {
        super(childlist);
    }
    
    @Override
    public String getStringExpression() {
        return "AND";
    }
}
