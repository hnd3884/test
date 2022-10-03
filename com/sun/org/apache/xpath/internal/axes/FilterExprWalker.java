package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.Expression;

public class FilterExprWalker extends AxesWalker
{
    static final long serialVersionUID = 5457182471424488375L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset;
    private boolean m_canDetachNodeset;
    
    public FilterExprWalker(final WalkingIterator locPathIterator) {
        super(locPathIterator, 20);
        this.m_mustHardReset = false;
        this.m_canDetachNodeset = true;
    }
    
    @Override
    public void init(final Compiler compiler, final int opPos, final int stepType) throws TransformerException {
        super.init(compiler, opPos, stepType);
        switch (stepType) {
            case 24:
            case 25: {
                this.m_mustHardReset = true;
            }
            case 22:
            case 23: {
                (this.m_expr = compiler.compileExpression(opPos)).exprSetParent(this);
                if (this.m_expr instanceof Variable) {
                    this.m_canDetachNodeset = false;
                    break;
                }
                break;
            }
            default: {
                (this.m_expr = compiler.compileExpression(opPos + 2)).exprSetParent(this);
                break;
            }
        }
    }
    
    @Override
    public void detach() {
        super.detach();
        if (this.m_canDetachNodeset) {
            this.m_exprObj.detach();
        }
        this.m_exprObj = null;
    }
    
    @Override
    public void setRoot(final int root) {
        super.setRoot(root);
        this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(root, this.m_lpi.getXPathContext(), this.m_lpi.getPrefixResolver(), this.m_lpi.getIsTopLevel(), this.m_lpi.m_stackFrame, this.m_expr);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final FilterExprWalker clone = (FilterExprWalker)super.clone();
        if (null != this.m_exprObj) {
            clone.m_exprObj = (XNodeSet)this.m_exprObj.clone();
        }
        return clone;
    }
    
    @Override
    public short acceptNode(final int n) {
        try {
            if (this.getPredicateCount() > 0) {
                this.countProximityPosition(0);
                if (!this.executePredicates(n, this.m_lpi.getXPathContext())) {
                    return 3;
                }
            }
            return 1;
        }
        catch (final TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
    }
    
    public int getNextNode() {
        if (null != this.m_exprObj) {
            final int next = this.m_exprObj.nextNode();
            return next;
        }
        return -1;
    }
    
    @Override
    public int getLastPos(final XPathContext xctxt) {
        return this.m_exprObj.getLength();
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
    public int getAxis() {
        return this.m_exprObj.getAxis();
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
        final FilterExprWalker walker = (FilterExprWalker)expr;
        return this.m_expr.deepEquals(walker.m_expr);
    }
    
    class filterExprOwner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return FilterExprWalker.this.m_expr;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(FilterExprWalker.this);
            FilterExprWalker.this.m_expr = exp;
        }
    }
}
