package net.sf.jsqlparser.util.cnfexpression;

import net.sf.jsqlparser.expression.Expression;
import java.util.List;

public final class MultiOrExpression extends MultipleExpression
{
    public MultiOrExpression(final List<Expression> childlist) {
        super(childlist);
    }
    
    @Override
    public String getStringExpression() {
        return "OR";
    }
}
