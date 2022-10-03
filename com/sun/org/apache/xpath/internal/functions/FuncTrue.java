package com.sun.org.apache.xpath.internal.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncTrue extends Function
{
    static final long serialVersionUID = 5663314547346339447L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return XBoolean.S_TRUE;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
    }
}
