package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.VariableStack;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.Expression;

public class FilterExprIteratorSimple extends LocPathIterator
{
    static final long serialVersionUID = -6978977187025375579L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset;
    private boolean m_canDetachNodeset;
    
    public FilterExprIteratorSimple() {
        super((PrefixResolver)null);
        this.m_mustHardReset = false;
        this.m_canDetachNodeset = true;
    }
    
    public FilterExprIteratorSimple(final Expression expr) {
        super((PrefixResolver)null);
        this.m_mustHardReset = false;
        this.m_canDetachNodeset = true;
        this.m_expr = expr;
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        this.m_exprObj = executeFilterExpr(context, this.m_execContext, this.getPrefixResolver(), this.getIsTopLevel(), this.m_stackFrame, this.m_expr);
    }
    
    public static XNodeSet executeFilterExpr(final int context, final XPathContext xctxt, final PrefixResolver prefixResolver, final boolean isTopLevel, final int stackFrame, final Expression expr) throws WrappedRuntimeException {
        final PrefixResolver savedResolver = xctxt.getNamespaceContext();
        XNodeSet result = null;
        try {
            xctxt.pushCurrentNode(context);
            xctxt.setNamespaceContext(prefixResolver);
            if (isTopLevel) {
                final VariableStack vars = xctxt.getVarStack();
                final int savedStart = vars.getStackFrame();
                vars.setStackFrame(stackFrame);
                result = (XNodeSet)expr.execute(xctxt);
                result.setShouldCacheNodes(true);
                vars.setStackFrame(savedStart);
            }
            else {
                result = (XNodeSet)expr.execute(xctxt);
            }
        }
        catch (final TransformerException se) {
            throw new WrappedRuntimeException(se);
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.setNamespaceContext(savedResolver);
        }
        return result;
    }
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        int next;
        if (null != this.m_exprObj) {
            next = (this.m_lastFetched = this.m_exprObj.nextNode());
        }
        else {
            next = (this.m_lastFetched = -1);
        }
        if (-1 != next) {
            ++this.m_pos;
            return next;
        }
        this.m_foundLast = true;
        return -1;
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            super.detach();
            this.m_exprObj.detach();
            this.m_exprObj = null;
        }
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
        final FilterExprIteratorSimple fet = (FilterExprIteratorSimple)expr;
        return this.m_expr.deepEquals(fet.m_expr);
    }
    
    @Override
    public int getAxis() {
        if (null != this.m_exprObj) {
            return this.m_exprObj.getAxis();
        }
        return 20;
    }
    
    class filterExprOwner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return FilterExprIteratorSimple.this.m_expr;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(FilterExprIteratorSimple.this);
            FilterExprIteratorSimple.this.m_expr = exp;
        }
    }
}
