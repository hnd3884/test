package org.apache.taglibs.standard.tag.common.xml;

import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.VariableStack;
import org.w3c.dom.Node;
import org.apache.taglibs.standard.util.XmlUtil;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.xpath.XPathContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class XalanUtil
{
    public static XPathContext getContext(final Tag child, final PageContext pageContext) {
        final ForEachTag forEachTag = (ForEachTag)TagSupport.findAncestorWithClass(child, (Class)ForEachTag.class);
        if (forEachTag != null) {
            return forEachTag.getContext();
        }
        final XPathContext context = new XPathContext(false);
        final VariableStack variableStack = new JSTLVariableStack(pageContext);
        context.setVarStack(variableStack);
        final int dtm = context.getDTMHandleFromNode((Node)XmlUtil.newEmptyDocument());
        context.pushCurrentNodeAndExpression(dtm, dtm);
        return context;
    }
    
    static Object coerceToJava(final XObject xo) throws TransformerException {
        if (xo instanceof XBoolean) {
            return xo.bool();
        }
        if (xo instanceof XNumber) {
            return xo.num();
        }
        if (xo instanceof XString) {
            return xo.str();
        }
        if (!(xo instanceof XNodeSet)) {
            throw new AssertionError();
        }
        final NodeList nodes = xo.nodelist();
        if (nodes.getLength() == 1) {
            return nodes.item(0);
        }
        return nodes;
    }
}
