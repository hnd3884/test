package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerException;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.DOMException;
import javax.xml.transform.SourceLocator;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.Document;
import org.w3c.dom.xpath.XPathEvaluator;

public final class XPathEvaluatorImpl implements XPathEvaluator
{
    private final Document m_doc;
    
    public XPathEvaluatorImpl(final Document doc) {
        this.m_doc = doc;
    }
    
    public XPathEvaluatorImpl() {
        this.m_doc = null;
    }
    
    @Override
    public XPathExpression createExpression(final String expression, final XPathNSResolver resolver) throws XPathException, DOMException {
        try {
            final XPath xpath = new XPath(expression, null, (null == resolver) ? new DummyPrefixResolver() : ((PrefixResolver)resolver), 0);
            return new XPathExpressionImpl(xpath, this.m_doc);
        }
        catch (final TransformerException e) {
            if (e instanceof XPathStylesheetDOM3Exception) {
                throw new DOMException((short)14, e.getMessageAndLocation());
            }
            throw new XPathException((short)1, e.getMessageAndLocation());
        }
    }
    
    @Override
    public XPathNSResolver createNSResolver(final Node nodeResolver) {
        return new XPathNSResolverImpl((nodeResolver.getNodeType() == 9) ? ((Document)nodeResolver).getDocumentElement() : nodeResolver);
    }
    
    @Override
    public Object evaluate(final String expression, final Node contextNode, final XPathNSResolver resolver, final short type, final Object result) throws XPathException, DOMException {
        final XPathExpression xpathExpression = this.createExpression(expression, resolver);
        return xpathExpression.evaluate(contextNode, type, result);
    }
    
    private class DummyPrefixResolver implements PrefixResolver
    {
        DummyPrefixResolver() {
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix, final Node context) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_NULL_RESOLVER", null);
            throw new DOMException((short)14, fmsg);
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix) {
            return this.getNamespaceForPrefix(prefix, null);
        }
        
        @Override
        public boolean handlesNullPrefixes() {
            return false;
        }
        
        @Override
        public String getBaseIdentifier() {
            return null;
        }
    }
}
