package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.Expression;

public class FilterExprIterator extends BasicTestIterator
{
    static final long serialVersionUID = 2552176105165737614L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset;
    private boolean m_canDetachNodeset;
    
    public FilterExprIterator() {
        super((PrefixResolver)null);
        this.m_mustHardReset = false;
        this.m_canDetachNodeset = true;
    }
    
    public FilterExprIterator(final Expression expr) {
        super((PrefixResolver)null);
        this.m_mustHardReset = false;
        this.m_canDetachNodeset = true;
        this.m_expr = expr;
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(context, this.m_execContext, this.getPrefixResolver(), this.getIsTopLevel(), this.m_stackFrame, this.m_expr);
    }
    
    @Override
    protected int getNextNode() {
        if (null != this.m_exprObj) {
            this.m_lastFetched = this.m_exprObj.nextNode();
        }
        else {
            this.m_lastFetched = -1;
        }
        return this.m_lastFetched;
    }
    
    @Override
    public void detach() {
        super.detach();
        this.m_exprObj.detach();
        this.m_exprObj = null;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        this.m_expr.fixupVariables(vars, globalsSize);
    }
    
    public Expression getInnerExpression() {
        return this.m_expr;
    }
    
    public void setInnerExpression(final Expression expr) {
        expr.exprSetParent(this);
        this.m_expr = expr;
    }
    
    @Override
    public int getAnalysisBits() {
        if (null != this.m_expr && this.m_expr instanceof PathComponent) {
            return ((PathComponent)this.m_expr).getAnalysisBits();
        }
        return 67108864;
    }
    
    @Override
    public boolean isDocOrdered() {
        return this.m_exprObj.isDocOrdered();
    }
    
    @Override
    public void callPredicateVisitors(final XPathVisitor visitor) {
        this.m_expr.callVisitors(new filterExprOwner(), visitor);
        super.callPredicateVisitors(visitor);
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        final FilterExprIterator fet = (FilterExprIterator)expr;
        return this.m_expr.deepEquals(fet.m_expr);
    }
    
    class filterExprOwner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return FilterExprIterator.this.m_expr;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(FilterExprIterator.this);
            FilterExprIterator.this.m_expr = exp;
        }
    }
}
