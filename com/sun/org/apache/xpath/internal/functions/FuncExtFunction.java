package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.Expression;
import java.util.Vector;

public class FuncExtFunction extends Function
{
    static final long serialVersionUID = 5196115554693708718L;
    String m_namespace;
    String m_extensionName;
    Object m_methodKey;
    Vector m_argVec;
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        if (null != this.m_argVec) {
            for (int nArgs = this.m_argVec.size(), i = 0; i < nArgs; ++i) {
                final Expression arg = this.m_argVec.elementAt(i);
                arg.fixupVariables(vars, globalsSize);
            }
        }
    }
    
    public String getNamespace() {
        return this.m_namespace;
    }
    
    public String getFunctionName() {
        return this.m_extensionName;
    }
    
    public Object getMethodKey() {
        return this.m_methodKey;
    }
    
    public Expression getArg(final int n) {
        if (n >= 0 && n < this.m_argVec.size()) {
            return this.m_argVec.elementAt(n);
        }
        return null;
    }
    
    public int getArgCount() {
        return this.m_argVec.size();
    }
    
    public FuncExtFunction(final String namespace, final String extensionName, final Object methodKey) {
        this.m_argVec = new Vector();
        this.m_namespace = namespace;
        this.m_extensionName = extensionName;
        this.m_methodKey = methodKey;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        if (xctxt.isSecureProcessing()) {
            throw new TransformerException(XPATHMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { this.toString() }));
        }
        final Vector argVec = new Vector();
        for (int nArgs = this.m_argVec.size(), i = 0; i < nArgs; ++i) {
            final Expression arg = this.m_argVec.elementAt(i);
            final XObject xobj = arg.execute(xctxt);
            xobj.allowDetachToRelease(false);
            argVec.addElement(xobj);
        }
        final ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();
        final Object val = extProvider.extFunction(this, argVec);
        XObject result;
        if (null != val) {
            result = XObject.create(val, xctxt);
        }
        else {
            result = new XNull();
        }
        return result;
    }
    
    @Override
    public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
        this.m_argVec.addElement(arg);
        arg.exprSetParent(this);
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
    }
    
    @Override
    public void callArgVisitors(final XPathVisitor visitor) {
        for (int i = 0; i < this.m_argVec.size(); ++i) {
            final Expression exp = this.m_argVec.elementAt(i);
            exp.callVisitors(new ArgExtOwner(exp), visitor);
        }
    }
    
    @Override
    public void exprSetParent(final ExpressionNode n) {
        super.exprSetParent(n);
        for (int nArgs = this.m_argVec.size(), i = 0; i < nArgs; ++i) {
            final Expression arg = this.m_argVec.elementAt(i);
            arg.exprSetParent(n);
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        final String fMsg = XPATHMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
        throw new RuntimeException(fMsg);
    }
    
    @Override
    public String toString() {
        if (this.m_namespace != null && this.m_namespace.length() > 0) {
            return "{" + this.m_namespace + "}" + this.m_extensionName;
        }
        return this.m_extensionName;
    }
    
    class ArgExtOwner implements ExpressionOwner
    {
        Expression m_exp;
        
        ArgExtOwner(final Expression exp) {
            this.m_exp = exp;
        }
        
        @Override
        public Expression getExpression() {
            return this.m_exp;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(FuncExtFunction.this);
            this.m_exp = exp;
        }
    }
}
