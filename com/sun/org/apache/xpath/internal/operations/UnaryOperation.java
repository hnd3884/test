package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.Expression;

public abstract class UnaryOperation extends Expression implements ExpressionOwner
{
    static final long serialVersionUID = 6536083808424286166L;
    protected Expression m_right;
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        this.m_right.fixupVariables(vars, globalsSize);
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return null != this.m_right && this.m_right.canTraverseOutsideSubtree();
    }
    
    public void setRight(final Expression r) {
        (this.m_right = r).exprSetParent(this);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.operate(this.m_right.execute(xctxt));
    }
    
    public abstract XObject operate(final XObject p0) throws TransformerException;
    
    public Expression getOperand() {
        return this.m_right;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitUnaryOperation(owner, this)) {
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
        return this.isSameClass(expr) && this.m_right.deepEquals(((UnaryOperation)expr).m_right);
    }
}
