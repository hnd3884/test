package net.sf.jsqlparser.util.cnfexpression;

import java.util.List;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.Expression;

class CloneHelper
{
    public Expression modify(final Expression express) {
        if (express instanceof NotExpression) {
            return new NotExpression(this.modify(((NotExpression)express).getExpression()));
        }
        if (express instanceof Parenthesis) {
            final Parenthesis parenthesis = (Parenthesis)express;
            final Expression result = this.modify(parenthesis.getExpression());
            if (parenthesis.isNot()) {
                return new NotExpression(result);
            }
            return result;
        }
        else if (express instanceof AndExpression) {
            final AndExpression and = (AndExpression)express;
            final List<Expression> list = new ArrayList<Expression>();
            list.add(this.modify(and.getLeftExpression()));
            list.add(this.modify(and.getRightExpression()));
            final MultiAndExpression result2 = new MultiAndExpression(list);
            if (and.isNot()) {
                return new NotExpression(result2);
            }
            return result2;
        }
        else {
            if (!(express instanceof OrExpression)) {
                if (express instanceof BinaryExpression) {
                    final BinaryExpression binary = (BinaryExpression)express;
                    if (binary.isNot()) {
                        binary.removeNot();
                        return new NotExpression(this.modify(binary));
                    }
                }
                return express;
            }
            final OrExpression or = (OrExpression)express;
            final List<Expression> list = new ArrayList<Expression>();
            list.add(this.modify(or.getLeftExpression()));
            list.add(this.modify(or.getRightExpression()));
            final MultiOrExpression result3 = new MultiOrExpression(list);
            if (or.isNot()) {
                return new NotExpression(result3);
            }
            return result3;
        }
    }
    
    public Expression shallowCopy(final Expression express) {
        if (!(express instanceof MultipleExpression)) {
            return express;
        }
        final MultipleExpression multi = (MultipleExpression)express;
        final List<Expression> list = new ArrayList<Expression>();
        for (int i = 0; i < multi.size(); ++i) {
            list.add(this.shallowCopy(multi.getChild(i)));
        }
        if (express instanceof MultiAndExpression) {
            return new MultiAndExpression(list);
        }
        return new MultiOrExpression(list);
    }
    
    public Expression changeBack(final Boolean isMultiOr, final Expression exp) {
        if (!(exp instanceof MultipleExpression)) {
            return exp;
        }
        final MultipleExpression changed = (MultipleExpression)exp;
        Expression result = changed.getChild(0);
        for (int i = 1; i < changed.size(); ++i) {
            final Expression left = result;
            final Expression right = changed.getChild(i);
            if (isMultiOr) {
                result = new OrExpression(left, right);
            }
            else {
                result = new AndExpression(left, right);
            }
        }
        if (isMultiOr) {
            return new Parenthesis(result);
        }
        return result;
    }
}
