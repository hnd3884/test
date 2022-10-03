package org.dom4j.util;

import org.dom4j.rule.Pattern;
import org.dom4j.NodeFilter;
import org.jaxen.VariableContext;
import org.dom4j.XPath;
import java.util.Map;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Namespace;
import org.dom4j.Entity;
import org.dom4j.Text;
import org.dom4j.Comment;
import org.dom4j.CDATA;
import org.dom4j.Attribute;
import org.dom4j.QName;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;

public abstract class ProxyDocumentFactory
{
    private DocumentFactory proxy;
    
    public ProxyDocumentFactory() {
        this.proxy = DocumentFactory.getInstance();
    }
    
    public ProxyDocumentFactory(final DocumentFactory proxy) {
        this.proxy = proxy;
    }
    
    public Document createDocument() {
        return this.proxy.createDocument();
    }
    
    public Document createDocument(final Element rootElement) {
        return this.proxy.createDocument(rootElement);
    }
    
    public DocumentType createDocType(final String name, final String publicId, final String systemId) {
        return this.proxy.createDocType(name, publicId, systemId);
    }
    
    public Element createElement(final QName qname) {
        return this.proxy.createElement(qname);
    }
    
    public Element createElement(final String name) {
        return this.proxy.createElement(name);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return this.proxy.createAttribute(owner, qname, value);
    }
    
    public Attribute createAttribute(final Element owner, final String name, final String value) {
        return this.proxy.createAttribute(owner, name, value);
    }
    
    public CDATA createCDATA(final String text) {
        return this.proxy.createCDATA(text);
    }
    
    public Comment createComment(final String text) {
        return this.proxy.createComment(text);
    }
    
    public Text createText(final String text) {
        return this.proxy.createText(text);
    }
    
    public Entity createEntity(final String name, final String text) {
        return this.proxy.createEntity(name, text);
    }
    
    public Namespace createNamespace(final String prefix, final String uri) {
        return this.proxy.createNamespace(prefix, uri);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
        return this.proxy.createProcessingInstruction(target, data);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final Map data) {
        return this.proxy.createProcessingInstruction(target, data);
    }
    
    public QName createQName(final String localName, final Namespace namespace) {
        return this.proxy.createQName(localName, namespace);
    }
    
    public QName createQName(final String localName) {
        return this.proxy.createQName(localName);
    }
    
    public QName createQName(final String name, final String prefix, final String uri) {
        return this.proxy.createQName(name, prefix, uri);
    }
    
    public QName createQName(final String qualifiedName, final String uri) {
        return this.proxy.createQName(qualifiedName, uri);
    }
    
    public XPath createXPath(final String xpathExpression) {
        return this.proxy.createXPath(xpathExpression);
    }
    
    public XPath createXPath(final String xpathExpression, final VariableContext variableContext) {
        return this.proxy.createXPath(xpathExpression, variableContext);
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression, final VariableContext variableContext) {
        return this.proxy.createXPathFilter(xpathFilterExpression, variableContext);
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression) {
        return this.proxy.createXPathFilter(xpathFilterExpression);
    }
    
    public Pattern createPattern(final String xpathPattern) {
        return this.proxy.createPattern(xpathPattern);
    }
    
    protected DocumentFactory getProxy() {
        return this.proxy;
    }
    
    protected void setProxy(DocumentFactory proxy) {
        if (proxy == null) {
            proxy = DocumentFactory.getInstance();
        }
        this.proxy = proxy;
    }
}
