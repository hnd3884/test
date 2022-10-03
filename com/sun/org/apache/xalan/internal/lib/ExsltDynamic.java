package com.sun.org.apache.xalan.internal.lib;

import org.w3c.dom.Text;
import org.w3c.dom.Element;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.NodeSet;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPathContext;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;

public class ExsltDynamic extends ExsltBase
{
    public static final String EXSL_URI = "http://exslt.org/common";
    
    public static double max(final ExpressionContext myContext, final NodeList nl, final String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        final NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double maxValue = -1.7976931348623157E308;
        for (int i = 0; i < contextNodes.getLength(); ++i) {
            final int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                final XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (final TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            if (result > maxValue) {
                maxValue = result;
            }
        }
        xctxt.popContextNodeList();
        return maxValue;
    }
    
    public static double min(final ExpressionContext myContext, final NodeList nl, final String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        final NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < nl.getLength(); ++i) {
            final int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                final XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (final TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            if (result < minValue) {
                minValue = result;
            }
        }
        xctxt.popContextNodeList();
        return minValue;
    }
    
    public static double sum(final ExpressionContext myContext, final NodeList nl, final String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        final NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double sum = 0.0;
        for (int i = 0; i < nl.getLength(); ++i) {
            final int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                final XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (final TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            sum += result;
        }
        xctxt.popContextNodeList();
        return sum;
    }
    
    public static NodeList map(final ExpressionContext myContext, final NodeList nl, final String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        Document lDoc = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return new NodeSet();
        }
        final NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        final NodeSet resultSet = new NodeSet();
        resultSet.setShouldCacheNodes(true);
        for (int i = 0; i < nl.getLength(); ++i) {
            final int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            XObject object = null;
            try {
                final XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                object = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                if (object instanceof XNodeSet) {
                    NodeList nodelist = null;
                    nodelist = ((XNodeSet)object).nodelist();
                    for (int k = 0; k < nodelist.getLength(); ++k) {
                        final Node n = nodelist.item(k);
                        if (!resultSet.contains(n)) {
                            resultSet.addNode(n);
                        }
                    }
                }
                else {
                    if (lDoc == null) {
                        lDoc = JdkXmlUtils.getDOMDocument();
                    }
                    Element element = null;
                    if (object instanceof XNumber) {
                        element = lDoc.createElementNS("http://exslt.org/common", "exsl:number");
                    }
                    else if (object instanceof XBoolean) {
                        element = lDoc.createElementNS("http://exslt.org/common", "exsl:boolean");
                    }
                    else {
                        element = lDoc.createElementNS("http://exslt.org/common", "exsl:string");
                    }
                    final Text textNode = lDoc.createTextNode(object.str());
                    element.appendChild(textNode);
                    resultSet.addNode(element);
                }
            }
            catch (final Exception e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return new NodeSet();
            }
            xctxt.popCurrentNode();
        }
        xctxt.popContextNodeList();
        return resultSet;
    }
    
    public static XObject evaluate(final ExpressionContext myContext, final String xpathExpr) throws SAXNotSupportedException {
        if (myContext instanceof XPathContext.XPathExpressionContext) {
            XPathContext xctxt = null;
            try {
                xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
                final XPath dynamicXPath = new XPath(xpathExpr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                return dynamicXPath.execute(xctxt, myContext.getContextNode(), xctxt.getNamespaceContext());
            }
            catch (final TransformerException e) {
                return new XNodeSet(xctxt.getDTMManager());
            }
        }
        throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
    }
    
    public static NodeList closure(final ExpressionContext myContext, final NodeList nl, final String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { myContext }));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return new NodeSet();
        }
        final NodeSet closureSet = new NodeSet();
        closureSet.setShouldCacheNodes(true);
        NodeList iterationList = nl;
        do {
            final NodeSet iterationSet = new NodeSet();
            final NodeSetDTM contextNodes = new NodeSetDTM(iterationList, xctxt);
            xctxt.pushContextNodeList(contextNodes);
            for (int i = 0; i < iterationList.getLength(); ++i) {
                final int contextNode = contextNodes.item(i);
                xctxt.pushCurrentNode(contextNode);
                XObject object = null;
                try {
                    final XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                    object = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                    if (!(object instanceof XNodeSet)) {
                        xctxt.popCurrentNode();
                        xctxt.popContextNodeList();
                        return new NodeSet();
                    }
                    NodeList nodelist = null;
                    nodelist = ((XNodeSet)object).nodelist();
                    for (int k = 0; k < nodelist.getLength(); ++k) {
                        final Node n = nodelist.item(k);
                        if (!iterationSet.contains(n)) {
                            iterationSet.addNode(n);
                        }
                    }
                }
                catch (final TransformerException e) {
                    xctxt.popCurrentNode();
                    xctxt.popContextNodeList();
                    return new NodeSet();
                }
                xctxt.popCurrentNode();
            }
            xctxt.popContextNodeList();
            iterationList = iterationSet;
            for (int i = 0; i < iterationList.getLength(); ++i) {
                final Node n2 = iterationList.item(i);
                if (!closureSet.contains(n2)) {
                    closureSet.addNode(n2);
                }
            }
        } while (iterationList.getLength() > 0);
        return closureSet;
    }
}
