package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;

public class Function3Args extends Function2Args
{
    static final long serialVersionUID = 7915240747161506646L;
    Expression m_arg2;
    
    public Expression getArg2() {
        return this.m_arg2;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_arg2) {
            this.m_arg2.fixupVariables(vars, globalsSize);
        }
    }
    
    @Override
    public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
        if (argNum < 2) {
            super.setArg(arg, argNum);
        }
        else if (2 == argNum) {
            (this.m_arg2 = arg).exprSetParent(this);
        }
        else {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum != 3) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("three", null));
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return super.canTraverseOutsideSubtree() || this.m_arg2.canTraverseOutsideSubtree();
    }
    
    @Override
    public void callArgVisitors(final XPathVisitor visitor) {
        super.callArgVisitors(visitor);
        if (null != this.m_arg2) {
            this.m_arg2.callVisitors(new Arg2Owner(), visitor);
        }
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg2) {
            if (null == ((Function3Args)expr).m_arg2) {
                return false;
            }
            if (!this.m_arg2.deepEquals(((Function3Args)expr).m_arg2)) {
                return false;
            }
        }
        else if (null != ((Function3Args)expr).m_arg2) {
            return false;
        }
        return true;
    }
    
    class Arg2Owner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return Function3Args.this.m_arg2;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(Function3Args.this);
            Function3Args.this.m_arg2 = exp;
        }
    }
}
