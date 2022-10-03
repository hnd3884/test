package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.Expression;

public class Operation extends Expression implements ExpressionOwner
{
    static final long serialVersionUID = -3037139537171050430L;
    protected Expression m_left;
    protected Expression m_right;
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        this.m_left.fixupVariables(vars, globalsSize);
        this.m_right.fixupVariables(vars, globalsSize);
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return (null != this.m_left && this.m_left.canTraverseOutsideSubtree()) || (null != this.m_right && this.m_right.canTraverseOutsideSubtree());
    }
    
    public void setLeftRight(final Expression l, final Expression r) {
        this.m_left = l;
        this.m_right = r;
        l.exprSetParent(this);
        r.exprSetParent(this);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XObject left = this.m_left.execute(xctxt, true);
        final XObject right = this.m_right.execute(xctxt, true);
        final XObject result = this.operate(left, right);
        left.detach();
        right.detach();
        return result;
    }
    
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return null;
    }
    
    public Expression getLeftOperand() {
        return this.m_left;
    }
    
    public Expression getRightOperand() {
        return this.m_right;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitBinaryOperation(owner, this)) {
            this.m_left.callVisitors(new LeftExprOwner(), visitor);
            this.m_right.callVisitors(this, visitor);
        }
    }
    
    @Override
    public Expression getExpression() {
        return this.m_right;
    }
    
    @Override
    public void setExpression(final Expression exp) {
        exp.exprSetParent(this);
        this.m_right = exp;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return this.isSameClass(expr) && this.m_left.deepEquals(((Operation)expr).m_left) && this.m_right.deepEquals(((Operation)expr).m_right);
    }
    
    class LeftExprOwner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return Operation.this.m_left;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(Operation.this);
            Operation.this.m_left = exp;
        }
    }
}
