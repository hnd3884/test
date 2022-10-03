package com.sun.org.apache.xpath.internal.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.axes.LocPathIterator;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.axes.PredicatedNodeTest;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncCurrent extends Function
{
    static final long serialVersionUID = 5715316804877715008L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final SubContextList subContextList = xctxt.getCurrentNodeList();
        int currentNode = -1;
        if (null != subContextList) {
            if (subContextList instanceof PredicatedNodeTest) {
                final LocPathIterator iter = ((PredicatedNodeTest)subContextList).getLocPathIterator();
                currentNode = iter.getCurrentContextNode();
            }
            else if (subContextList instanceof StepPattern) {
                throw new RuntimeException(XSLMessages.createMessage("ER_PROCESSOR_ERROR", null));
            }
        }
        else {
            currentNode = xctxt.getContextNode();
        }
        return new XNodeSet(currentNode, xctxt.getDTMManager());
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
    }
}
