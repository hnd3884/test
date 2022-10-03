package org.apache.axiom.om.xpath;

import java.io.InputStream;
import org.jaxen.FunctionCallException;
import java.io.IOException;
import org.apache.axiom.om.OMXMLBuilderFactory;
import java.net.URL;
import java.io.FileInputStream;
import org.jaxen.util.SingleObjectIterator;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.JaxenConstants;
import org.apache.axiom.om.OMContainer;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.Navigator;
import org.jaxen.BaseXPath;
import org.jaxen.XPath;
import org.jaxen.DefaultNavigator;

public class DocumentNavigator extends DefaultNavigator
{
    private static final long serialVersionUID = 7325116153349780805L;
    
    public XPath parseXPath(final String xpath) throws SAXPathException {
        return (XPath)new BaseXPath(xpath, (Navigator)this);
    }
    
    public String getElementNamespaceUri(final Object object) {
        final OMElement attr = (OMElement)object;
        return attr.getQName().getNamespaceURI();
    }
    
    public String getElementName(final Object object) {
        return ((OMElement)object).getLocalName();
    }
    
    public String getElementQName(final Object object) {
        final OMElement attr = (OMElement)object;
        String prefix = null;
        final OMNamespace namespace = attr.getNamespace();
        if (namespace != null) {
            prefix = namespace.getPrefix();
        }
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + namespace.getNamespaceURI();
    }
    
    public String getAttributeNamespaceUri(final Object object) {
        final OMAttribute attr = (OMAttribute)object;
        return attr.getQName().getNamespaceURI();
    }
    
    public String getAttributeName(final Object object) {
        return ((OMAttribute)object).getLocalName();
    }
    
    public String getAttributeQName(final Object object) {
        final OMAttribute attr = (OMAttribute)object;
        final String prefix = attr.getNamespace().getPrefix();
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + attr.getLocalName();
    }
    
    public boolean isDocument(final Object object) {
        return object instanceof OMDocument;
    }
    
    public boolean isElement(final Object object) {
        return object instanceof OMElement;
    }
    
    public boolean isAttribute(final Object object) {
        return object instanceof OMAttribute;
    }
    
    public boolean isNamespace(final Object object) {
        return object instanceof OMNamespace;
    }
    
    public boolean isComment(final Object object) {
        return object instanceof OMComment;
    }
    
    public boolean isText(final Object object) {
        return object instanceof OMText;
    }
    
    public boolean isProcessingInstruction(final Object object) {
        return object instanceof OMProcessingInstruction;
    }
    
    public String getCommentStringValue(final Object object) {
        return ((OMComment)object).getValue();
    }
    
    public String getElementStringValue(final Object object) {
        if (this.isElement(object)) {
            return this.getStringValue((OMNode)object, new StringBuffer()).toString();
        }
        return null;
    }
    
    private StringBuffer getStringValue(final OMNode node, final StringBuffer buffer) {
        if (this.isText(node)) {
            buffer.append(((OMText)node).getText());
        }
        else if (node instanceof OMElement) {
            final Iterator children = ((OMElement)node).getChildren();
            while (children.hasNext()) {
                this.getStringValue(children.next(), buffer);
            }
        }
        return buffer;
    }
    
    public String getAttributeStringValue(final Object object) {
        return ((OMAttribute)object).getAttributeValue();
    }
    
    public String getNamespaceStringValue(final Object object) {
        return ((OMNamespace)object).getNamespaceURI();
    }
    
    public String getTextStringValue(final Object object) {
        return ((OMText)object).getText();
    }
    
    public String getNamespacePrefix(final Object object) {
        return ((OMNamespace)object).getPrefix();
    }
    
    public Iterator getChildAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMContainer) {
            return ((OMContainer)contextNode).getChildren();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }
    
    public Iterator getDescendantAxisIterator(final Object object) throws UnsupportedAxisException {
        return super.getDescendantAxisIterator(object);
    }
    
    public Iterator getAttributeAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        if (this.isElement(contextNode)) {
            return ((OMElement)contextNode).getAllAttributes();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }
    
    public Iterator getNamespaceAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        if (!(contextNode instanceof OMContainer) || !(contextNode instanceof OMElement)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        final OMContainer omContextNode = (OMContainer)contextNode;
        final List nsList = new ArrayList();
        final HashSet prefixes = new HashSet();
        for (OMContainer context = omContextNode; context != null && !(context instanceof OMDocument); context = ((OMElement)context).getParent()) {
            final OMElement element = (OMElement)context;
            final ArrayList declaredNS = new ArrayList();
            final Iterator i = element.getAllDeclaredNamespaces();
            while (i != null && i.hasNext()) {
                declaredNS.add(i.next());
            }
            declaredNS.add(element.getNamespace());
            Iterator iter = element.getAllAttributes();
            while (iter != null && iter.hasNext()) {
                final OMAttribute attr = iter.next();
                final OMNamespace namespace = attr.getNamespace();
                if (namespace != null) {
                    declaredNS.add(namespace);
                }
            }
            iter = declaredNS.iterator();
            while (iter != null && iter.hasNext()) {
                final OMNamespace namespace2 = iter.next();
                if (namespace2 != null) {
                    final String prefix = namespace2.getPrefix();
                    if (prefix == null || prefixes.contains(prefix)) {
                        continue;
                    }
                    prefixes.add(prefix);
                    nsList.add(new OMNamespaceEx(namespace2, context));
                }
            }
        }
        nsList.add(new OMNamespaceEx(omContextNode.getOMFactory().createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml"), omContextNode));
        return nsList.iterator();
    }
    
    public Iterator getSelfAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getSelfAxisIterator(contextNode);
    }
    
    public Iterator getDescendantOrSelfAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getDescendantOrSelfAxisIterator(contextNode);
    }
    
    public Iterator getAncestorOrSelfAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getAncestorOrSelfAxisIterator(contextNode);
    }
    
    public Iterator getParentAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMNode) {
            return (Iterator)new SingleObjectIterator((Object)((OMNode)contextNode).getParent());
        }
        if (contextNode instanceof OMNamespaceEx) {
            return (Iterator)new SingleObjectIterator((Object)((OMNamespaceEx)contextNode).getParent());
        }
        if (contextNode instanceof OMAttribute) {
            return (Iterator)new SingleObjectIterator((Object)((OMAttribute)contextNode).getOwner());
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }
    
    public Iterator getAncestorAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getAncestorAxisIterator(contextNode);
    }
    
    public Iterator getFollowingSiblingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        final ArrayList list = new ArrayList();
        if (contextNode != null && contextNode instanceof OMNode) {
            while (contextNode != null && contextNode instanceof OMNode) {
                contextNode = ((OMNode)contextNode).getNextOMSibling();
                if (contextNode != null) {
                    list.add(contextNode);
                }
            }
        }
        return list.iterator();
    }
    
    public Iterator getPrecedingSiblingAxisIterator(Object contextNode) throws UnsupportedAxisException {
        final ArrayList list = new ArrayList();
        if (contextNode != null && contextNode instanceof OMNode) {
            while (contextNode != null && contextNode instanceof OMNode) {
                contextNode = ((OMNode)contextNode).getPreviousOMSibling();
                if (contextNode != null) {
                    list.add(contextNode);
                }
            }
        }
        return list.iterator();
    }
    
    public Iterator getFollowingAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getFollowingAxisIterator(contextNode);
    }
    
    public Iterator getPrecedingAxisIterator(final Object contextNode) throws UnsupportedAxisException {
        return super.getPrecedingAxisIterator(contextNode);
    }
    
    public Object getDocument(final String uri) throws FunctionCallException {
        InputStream in = null;
        try {
            if (uri.indexOf(58) == -1) {
                in = new FileInputStream(uri);
            }
            else {
                final URL url = new URL(uri);
                in = url.openStream();
            }
            return OMXMLBuilderFactory.createOMBuilder(in).getDocument();
        }
        catch (final Exception e) {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final IOException ex) {}
            }
            throw new FunctionCallException((Throwable)e);
        }
    }
    
    public Object getElementById(final Object contextNode, final String elementId) {
        return super.getElementById(contextNode, elementId);
    }
    
    public Object getDocumentNode(final Object contextNode) {
        if (contextNode instanceof OMDocument) {
            return contextNode;
        }
        final OMContainer parent = ((OMNode)contextNode).getParent();
        if (parent == null) {
            return contextNode;
        }
        return this.getDocumentNode(parent);
    }
    
    public String translateNamespacePrefixToUri(final String prefix, final Object element) {
        return super.translateNamespacePrefixToUri(prefix, element);
    }
    
    public String getProcessingInstructionTarget(final Object object) {
        return ((OMProcessingInstruction)object).getTarget();
    }
    
    public String getProcessingInstructionData(final Object object) {
        return ((OMProcessingInstruction)object).getValue();
    }
    
    public short getNodeType(final Object node) {
        return super.getNodeType(node);
    }
    
    public Object getParentNode(final Object contextNode) throws UnsupportedAxisException {
        if (contextNode == null || contextNode instanceof OMDocument) {
            return null;
        }
        if (contextNode instanceof OMAttribute) {
            return ((OMAttribute)contextNode).getOwner();
        }
        if (contextNode instanceof OMNamespaceEx) {
            return ((OMNamespaceEx)contextNode).getParent();
        }
        return ((OMNode)contextNode).getParent();
    }
    
    class OMNamespaceEx implements OMNamespace
    {
        final OMNamespace originalNsp;
        final OMContainer parent;
        
        OMNamespaceEx(final OMNamespace nsp, final OMContainer parent) {
            this.originalNsp = nsp;
            this.parent = parent;
        }
        
        public boolean equals(final String uri, final String prefix) {
            return this.originalNsp.equals(uri, prefix);
        }
        
        public String getPrefix() {
            return this.originalNsp.getPrefix();
        }
        
        public String getName() {
            return this.originalNsp.getNamespaceURI();
        }
        
        public String getNamespaceURI() {
            return this.originalNsp.getNamespaceURI();
        }
        
        public OMContainer getParent() {
            return this.parent;
        }
    }
}
