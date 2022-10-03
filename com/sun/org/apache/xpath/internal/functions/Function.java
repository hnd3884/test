package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.Expression;

public abstract class Function extends Expression
{
    static final long serialVersionUID = 6927661240854599768L;
    
    public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
        this.reportWrongNumberArgs();
    }
    
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum != 0) {
            this.reportWrongNumberArgs();
        }
    }
    
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("zero", null));
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        System.out.println("Error! Function.execute should not be called!");
        return null;
    }
    
    public void callArgVisitors(final XPathVisitor visitor) {
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitFunction(owner, this)) {
            this.callArgVisitors(visitor);
        }
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return this.isSameClass(expr);
    }
    
    public void postCompileStep(final Compiler compiler) {
    }
}
