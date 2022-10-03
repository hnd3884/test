package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.operations.Number;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.org.apache.xpath.internal.operations.Quo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.org.apache.xpath.internal.operations.Minus;
import com.sun.org.apache.xpath.internal.operations.Plus;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.functions.FuncLast;
import com.sun.org.apache.xpath.internal.functions.FuncPosition;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;

public class HasPositionalPredChecker extends XPathVisitor
{
    private boolean m_hasPositionalPred;
    private int m_predDepth;
    
    public HasPositionalPredChecker() {
        this.m_hasPositionalPred = false;
        this.m_predDepth = 0;
    }
    
    public static boolean check(final LocPathIterator path) {
        final HasPositionalPredChecker hppc = new HasPositionalPredChecker();
        path.callVisitors(null, hppc);
        return hppc.m_hasPositionalPred;
    }
    
    @Override
    public boolean visitFunction(final ExpressionOwner owner, final Function func) {
        if (func instanceof FuncPosition || func instanceof FuncLast) {
            this.m_hasPositionalPred = true;
        }
        return true;
    }
    
    @Override
    public boolean visitPredicate(final ExpressionOwner owner, final Expression pred) {
        ++this.m_predDepth;
        if (this.m_predDepth == 1) {
            if (pred instanceof Variable || pred instanceof XNumber || pred instanceof Div || pred instanceof Plus || pred instanceof Minus || pred instanceof Mod || pred instanceof Quo || pred instanceof Mult || pred instanceof Number || pred instanceof Function) {
                this.m_hasPositionalPred = true;
            }
            else {
                pred.callVisitors(owner, this);
            }
        }
        --this.m_predDepth;
        return false;
    }
}
