package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class JDKXPathAPI implements XPathAPI
{
    private XPathFactory xpf;
    private String xpathStr;
    private XPathExpression xpathExpression;
    
    @Override
    public NodeList selectNodeList(final Node node, final Node node2, final String xpathStr, final Node node3) throws TransformerException {
        if (!xpathStr.equals(this.xpathStr) || this.xpathExpression == null) {
            if (this.xpf == null) {
                this.xpf = XPathFactory.newInstance();
                try {
                    this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                }
                catch (final XPathFactoryConfigurationException e) {
                    throw new TransformerException(e);
                }
            }
            final XPath xPath = this.xpf.newXPath();
            xPath.setNamespaceContext(new DOMNamespaceContext(node3));
            this.xpathStr = xpathStr;
            try {
                this.xpathExpression = xPath.compile(this.xpathStr);
            }
            catch (final XPathExpressionException e2) {
                throw new TransformerException(e2);
            }
        }
        try {
            return (NodeList)this.xpathExpression.evaluate(node, XPathConstants.NODESET);
        }
        catch (final XPathExpressionException e3) {
            throw new TransformerException(e3);
        }
    }
    
    @Override
    public boolean evaluate(final Node node, final Node node2, final String xpathStr, final Node node3) throws TransformerException {
        if (!xpathStr.equals(this.xpathStr) || this.xpathExpression == null) {
            if (this.xpf == null) {
                this.xpf = XPathFactory.newInstance();
                try {
                    this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                }
                catch (final XPathFactoryConfigurationException e) {
                    throw new TransformerException(e);
                }
            }
            final XPath xPath = this.xpf.newXPath();
            xPath.setNamespaceContext(new DOMNamespaceContext(node3));
            this.xpathStr = xpathStr;
            try {
                this.xpathExpression = xPath.compile(this.xpathStr);
            }
            catch (final XPathExpressionException e2) {
                throw new TransformerException(e2);
            }
        }
        try {
            return (boolean)this.xpathExpression.evaluate(node, XPathConstants.BOOLEAN);
        }
        catch (final XPathExpressionException e3) {
            throw new TransformerException(e3);
        }
    }
    
    @Override
    public void clear() {
        this.xpathStr = null;
        this.xpathExpression = null;
        this.xpf = null;
    }
}
