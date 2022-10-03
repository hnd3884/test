package org.apache.xerces.impl.xs.alternative;

import java.util.Enumeration;
import org.w3c.dom.Element;
import java.util.Map;
import org.w3c.dom.Document;
import org.apache.xerces.xs.XSModel;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Attr;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.NamespaceSupport;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.apache.xerces.impl.xpath.XPath20;
import org.apache.xerces.impl.xs.AbstractPsychoPathXPath2Impl;

public class Test extends AbstractPsychoPathXPath2Impl
{
    protected final XSTypeAlternativeImpl fTypeAlternative;
    protected final String fExpression;
    protected final XPath20 fXPath;
    protected final XPath fXPathPsychoPath;
    protected final NamespaceSupport fXPath2NamespaceContext;
    
    public Test(final XPath20 fxPath, final XSTypeAlternativeImpl fTypeAlternative, final NamespaceSupport fxPath2NamespaceContext) {
        this.fXPath = fxPath;
        this.fExpression = ((fxPath == null) ? "" : fxPath.getXPathStrValue());
        this.fXPathPsychoPath = null;
        this.fTypeAlternative = fTypeAlternative;
        this.fXPath2NamespaceContext = fxPath2NamespaceContext;
    }
    
    public Test(final XPath fxPathPsychoPath, final String s, final XSTypeAlternativeImpl fTypeAlternative, final NamespaceSupport fxPath2NamespaceContext) {
        this.fXPath = null;
        this.fExpression = ((s == null) ? "" : s);
        this.fXPathPsychoPath = fxPathPsychoPath;
        this.fTypeAlternative = fTypeAlternative;
        this.fXPath2NamespaceContext = fxPath2NamespaceContext;
    }
    
    public NamespaceSupport getNamespaceContext() {
        return this.fXPath2NamespaceContext;
    }
    
    public XSTypeAlternativeImpl getTypeAlternative() {
        return this.fTypeAlternative;
    }
    
    public Object getXPath() {
        Object o = null;
        if (this.fXPath != null) {
            o = this.fXPath;
        }
        else if (this.fXPathPsychoPath != null) {
            o = this.fXPathPsychoPath;
        }
        return o;
    }
    
    public boolean evaluateTest(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext, final String s) {
        if (this.fXPath != null) {
            return this.fXPath.evaluateTest(qName, xmlAttributes);
        }
        return this.fXPathPsychoPath != null && this.evaluateTestWithPsychoPathXPathEngine(qName, xmlAttributes, namespaceContext, s);
    }
    
    public String toString() {
        return this.fExpression;
    }
    
    private boolean evaluateTestWithPsychoPathXPathEngine(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext, final String documentURI) {
        boolean evaluateXPathExpr;
        try {
            final PSVIDocumentImpl psviDocumentImpl = new PSVIDocumentImpl();
            psviDocumentImpl.setDocumentURI(documentURI);
            final PSVIElementNSImpl psviElementNSImpl = new PSVIElementNSImpl(psviDocumentImpl, qName.uri, qName.rawname);
            for (int i = 0; i < xmlAttributes.getLength(); ++i) {
                final PSVIAttrNSImpl attributeNode = new PSVIAttrNSImpl(psviDocumentImpl, xmlAttributes.getURI(i), xmlAttributes.getQName(i));
                attributeNode.setNodeValue(xmlAttributes.getValue(i));
                psviElementNSImpl.setAttributeNode(attributeNode);
            }
            final Enumeration allPrefixes = namespaceContext.getAllPrefixes();
            while (allPrefixes.hasMoreElements()) {
                final String s = allPrefixes.nextElement();
                final String uri = namespaceContext.getURI(s);
                if (!"xml".equals(s) && !"xmlns".equals(s)) {
                    psviElementNSImpl.setAttribute((s != null && !SchemaSymbols.EMPTY_STRING.equals(s)) ? ("xmlns:" + s) : "xmlns", uri);
                }
            }
            psviDocumentImpl.appendChild(psviElementNSImpl);
            final HashMap hashMap = new HashMap();
            hashMap.put("XPATH2_NS_CONTEXT", this.fXPath2NamespaceContext);
            hashMap.put("CTA-EVALUATOR", Boolean.TRUE);
            this.initXPath2DynamicContext(null, psviDocumentImpl, hashMap).set_base_uri(this.fTypeAlternative.getBaseURI());
            if (this.fTypeAlternative.fXPathDefaultNamespace != null) {
                this.addNamespaceBindingToXPath2DynamicContext(null, this.fTypeAlternative.fXPathDefaultNamespace);
            }
            evaluateXPathExpr = this.evaluateXPathExpr(this.fXPathPsychoPath, psviElementNSImpl);
        }
        catch (final Exception ex) {
            evaluateXPathExpr = false;
        }
        return evaluateXPathExpr;
    }
}
