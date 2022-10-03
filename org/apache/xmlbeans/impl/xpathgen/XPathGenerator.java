package org.apache.xmlbeans.impl.xpathgen;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.apache.xmlbeans.XmlCursor;

public class XPathGenerator
{
    public static String generateXPath(final XmlCursor node, final XmlCursor context, final NamespaceContext nsctx) throws XPathGenerationException {
        if (node == null) {
            throw new IllegalArgumentException("Null node");
        }
        if (nsctx == null) {
            throw new IllegalArgumentException("Null namespace context");
        }
        final XmlCursor.TokenType tt = node.currentTokenType();
        if (context != null && node.isAtSamePositionAs(context)) {
            return ".";
        }
        switch (tt.intValue()) {
            case 6: {
                final QName name = node.getName();
                node.toParent();
                final String pathToParent = generateInternal(node, context, nsctx);
                return pathToParent + '/' + '@' + qnameToString(name, nsctx);
            }
            case 7: {
                final QName name = node.getName();
                node.toParent();
                final String pathToParent = generateInternal(node, context, nsctx);
                final String prefix = name.getLocalPart();
                if (prefix.length() == 0) {
                    return pathToParent + "/@xmlns";
                }
                return pathToParent + "/@xmlns:" + prefix;
            }
            case 1:
            case 3: {
                return generateInternal(node, context, nsctx);
            }
            case 5: {
                final int nrOfTextTokens = countTextTokens(node);
                node.toParent();
                final String pathToParent = generateInternal(node, context, nsctx);
                if (nrOfTextTokens == 0) {
                    return pathToParent + "/text()";
                }
                return pathToParent + "/text()[position()=" + nrOfTextTokens + ']';
            }
            default: {
                throw new XPathGenerationException("Cannot generate XPath for cursor position: " + tt.toString());
            }
        }
    }
    
    private static String generateInternal(final XmlCursor node, final XmlCursor context, final NamespaceContext nsctx) throws XPathGenerationException {
        if (node.isStartdoc()) {
            return "";
        }
        if (context != null && node.isAtSamePositionAs(context)) {
            return ".";
        }
        assert node.isStart();
        final QName name = node.getName();
        final XmlCursor d = node.newCursor();
        if (!node.toParent()) {
            return "/" + name;
        }
        int elemIndex = 0;
        int i = 1;
        node.push();
        if (!node.toChild(name)) {
            throw new IllegalStateException("Must have at least one child with name: " + name);
        }
        do {
            if (node.isAtSamePositionAs(d)) {
                elemIndex = i;
            }
            else {
                ++i;
            }
        } while (node.toNextSibling(name));
        node.pop();
        d.dispose();
        final String pathToParent = generateInternal(node, context, nsctx);
        return (i == 1) ? (pathToParent + '/' + qnameToString(name, nsctx)) : (pathToParent + '/' + qnameToString(name, nsctx) + '[' + elemIndex + ']');
    }
    
    private static String qnameToString(final QName qname, final NamespaceContext ctx) throws XPathGenerationException {
        final String localName = qname.getLocalPart();
        final String uri = qname.getNamespaceURI();
        if (uri.length() == 0) {
            return localName;
        }
        String prefix = qname.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            final String mappedUri = ctx.getNamespaceURI(prefix);
            if (uri.equals(mappedUri)) {
                return prefix + ':' + localName;
            }
        }
        prefix = ctx.getPrefix(uri);
        if (prefix == null) {
            throw new XPathGenerationException("Could not obtain a prefix for URI: " + uri);
        }
        if (prefix.length() == 0) {
            throw new XPathGenerationException("Can not use default prefix in XPath for URI: " + uri);
        }
        return prefix + ':' + localName;
    }
    
    private static int countTextTokens(final XmlCursor c) {
        int k = 0;
        int l = 0;
        final XmlCursor d = c.newCursor();
        c.push();
        c.toParent();
        for (XmlCursor.TokenType tt = c.toFirstContentToken(); !tt.isEnd(); tt = c.toNextToken()) {
            if (tt.isText()) {
                if (c.comparePosition(d) > 0) {
                    ++l;
                }
                else {
                    ++k;
                }
            }
            else if (tt.isStart()) {
                c.toEndToken();
            }
        }
        c.pop();
        return (l == 0) ? 0 : k;
    }
    
    public static void main(final String[] args) throws XmlException {
        final String xml = "<root>\n<ns:a xmlns:ns=\"http://a.com\"><b foo=\"value\">text1<c/>text2<c/>text3<c>text</c>text4</b></ns:a>\n</root>";
        final NamespaceContext ns = new NamespaceContext() {
            @Override
            public String getNamespaceURI(final String prefix) {
                if ("ns".equals(prefix)) {
                    return "http://a.com";
                }
                return null;
            }
            
            @Override
            public String getPrefix(final String namespaceUri) {
                return null;
            }
            
            @Override
            public Iterator getPrefixes(final String namespaceUri) {
                return null;
            }
        };
        final XmlCursor c = XmlObject.Factory.parse(xml).newCursor();
        c.toFirstContentToken();
        c.toFirstContentToken();
        c.toFirstChild();
        c.toFirstChild();
        c.push();
        System.out.println(generateXPath(c, null, ns));
        c.pop();
        c.toNextSibling();
        c.toNextSibling();
        c.push();
        System.out.println(generateXPath(c, null, ns));
        c.pop();
        final XmlCursor d = c.newCursor();
        d.toParent();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        d.toParent();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.toFirstContentToken();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.toParent();
        c.toPrevToken();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.toParent();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.toFirstAttribute();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.toParent();
        c.toParent();
        c.toNextToken();
        c.push();
        System.out.println(generateXPath(c, d, ns));
        c.pop();
        c.push();
        System.out.println(generateXPath(c, null, ns));
        c.pop();
    }
}
