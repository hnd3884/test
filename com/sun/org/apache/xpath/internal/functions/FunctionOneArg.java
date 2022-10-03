package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;

public class FunctionOneArg extends Function implements ExpressionOwner
{
    static final long serialVersionUID = -5180174180765609758L;
    Expression m_arg0;
    
    public Expression getArg0() {
        return this.m_arg0;
    }
    
    @Override
    public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
        if (0 == argNum) {
            (this.m_arg0 = arg).exprSetParent(this);
        }
        else {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum != 1) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("one", null));
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return this.m_arg0.canTraverseOutsideSubtree();
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        if (null != this.m_arg0) {
            this.m_arg0.fixupVariables(vars, globalsSize);
        }
    }
    
    @Override
    public void callArgVisitors(final XPathVisitor visitor) {
        if (null != this.m_arg0) {
            this.m_arg0.callVisitors(this, visitor);
        }
    }
    
    @Override
    public Expression getExpression() {
        return this.m_arg0;
    }
    
    @Override
    public void setExpression(final Expression exp) {
        exp.exprSetParent(this);
        this.m_arg0 = exp;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg0) {
            if (null == ((FunctionOneArg)expr).m_arg0) {
                return false;
            }
            if (!this.m_arg0.deepEquals(((FunctionOneArg)expr).m_arg0)) {
                return false;
            }
        }
        else if (null != ((FunctionOneArg)expr).m_arg0) {
            return false;
        }
        return true;
    }
}
