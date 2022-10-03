package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncConcat extends FunctionMultiArgs
{
    static final long serialVersionUID = 1737228885202314413L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.m_arg0.execute(xctxt).str());
        sb.append(this.m_arg1.execute(xctxt).str());
        if (null != this.m_arg2) {
            sb.append(this.m_arg2.execute(xctxt).str());
        }
        if (null != this.m_args) {
            for (int i = 0; i < this.m_args.length; ++i) {
                sb.append(this.m_args[i].execute(xctxt).str());
            }
        }
        return new XString(sb.toString());
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum < 2) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("gtone", null));
    }
}
