package com.sun.org.apache.xalan.internal.lib;

import java.util.StringTokenizer;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.NodeList;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import org.w3c.dom.traversal.NodeIterator;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;

public class Extensions
{
    private Extensions() {
    }
    
    public static NodeSet nodeset(final ExpressionContext myProcessor, final Object rtf) {
        if (rtf instanceof NodeIterator) {
            return new NodeSet((NodeIterator)rtf);
        }
        String textNodeValue;
        if (rtf instanceof String) {
            textNodeValue = (String)rtf;
        }
        else if (rtf instanceof Boolean) {
            textNodeValue = new XBoolean((boolean)rtf).str();
        }
        else if (rtf instanceof Double) {
            textNodeValue = new XNumber((double)rtf).str();
        }
        else {
            textNodeValue = rtf.toString();
        }
        final Document myDoc = JdkXmlUtils.getDOMDocument();
        final Text textNode = myDoc.createTextNode(textNodeValue);
        final DocumentFragment docFrag = myDoc.createDocumentFragment();
        docFrag.appendChild(textNode);
        return new NodeSet(docFrag);
    }
    
    public static NodeList intersection(final NodeList nl1, final NodeList nl2) {
        return ExsltSets.intersection(nl1, nl2);
    }
    
    public static NodeList difference(final NodeList nl1, final NodeList nl2) {
        return ExsltSets.difference(nl1, nl2);
    }
    
    public static NodeList distinct(final NodeList nl) {
        return ExsltSets.distinct(nl);
    }
    
    public static boolean hasSameNodes(final NodeList nl1, final NodeList nl2) {
        final NodeSet ns1 = new NodeSet(nl1);
        final NodeSet ns2 = new NodeSet(nl2);
        if (ns1.getLength() != ns2.getLength()) {
            return false;
        }
        for (int i = 0; i < ns1.getLength(); ++i) {
            final Node n = ns1.elementAt(i);
            if (!ns2.contains(n)) {
                return false;
            }
        }
        return true;
    }
    
    public static XObject evaluate(final ExpressionContext myContext, final String xpathExpr) throws SAXNotSupportedException {
        return ExsltDynamic.evaluate(myContext, xpathExpr);
    }
    
    public static NodeList tokenize(final String toTokenize, final String delims) {
        final Document doc = JdkXmlUtils.getDOMDocument();
        final StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
        final NodeSet resultSet = new NodeSet();
        synchronized (doc) {
            while (lTokenizer.hasMoreTokens()) {
                resultSet.addNode(doc.createTextNode(lTokenizer.nextToken()));
            }
        }
        return resultSet;
    }
    
    public static NodeList tokenize(final String toTokenize) {
        return tokenize(toTokenize, " \t\n\r");
    }
}
