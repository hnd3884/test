package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;

public class Function2Args extends FunctionOneArg
{
    static final long serialVersionUID = 5574294996842710641L;
    Expression m_arg1;
    
    public Expression getArg1() {
        return this.m_arg1;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_arg1) {
            this.m_arg1.fixupVariables(vars, globalsSize);
        }
    }
    
    @Override
    public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
        if (argNum == 0) {
            super.setArg(arg, argNum);
        }
        else if (1 == argNum) {
            (this.m_arg1 = arg).exprSetParent(this);
        }
        else {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum != 2) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("two", null));
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return super.canTraverseOutsideSubtree() || this.m_arg1.canTraverseOutsideSubtree();
    }
    
    @Override
    public void callArgVisitors(final XPathVisitor visitor) {
        super.callArgVisitors(visitor);
        if (null != this.m_arg1) {
            this.m_arg1.callVisitors(new Arg1Owner(), visitor);
        }
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg1) {
            if (null == ((Function2Args)expr).m_arg1) {
                return false;
            }
            if (!this.m_arg1.deepEquals(((Function2Args)expr).m_arg1)) {
                return false;
            }
        }
        else if (null != ((Function2Args)expr).m_arg1) {
            return false;
        }
        return true;
    }
    
    class Arg1Owner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return Function2Args.this.m_arg1;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(Function2Args.this);
            Function2Args.this.m_arg1 = exp;
        }
    }
}
