package org.apache.axiom.om.util;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.Reader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.util.stax.xop.XOPUtils;
import org.apache.axiom.om.OMException;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMNamespace;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;

public class ElementHelper
{
    private OMElement element;
    
    public ElementHelper(final OMElement element) {
        this.element = element;
    }
    
    @Deprecated
    public QName resolveQName(final String qname, final boolean defaultToParentNameSpace) {
        final int colon = qname.indexOf(58);
        if (colon < 0) {
            if (defaultToParentNameSpace) {
                final OMNamespace namespace = this.element.getNamespace();
                if (namespace != null) {
                    if (namespace.getPrefix() == null) {
                        return new QName(namespace.getNamespaceURI(), qname);
                    }
                    return new QName(namespace.getNamespaceURI(), qname, namespace.getPrefix());
                }
            }
            return new QName(qname);
        }
        final String prefix = qname.substring(0, colon);
        final String local = qname.substring(colon + 1);
        if (local.length() == 0) {
            return null;
        }
        final OMNamespace namespace2 = this.element.findNamespaceURI(prefix);
        if (namespace2 == null) {
            return null;
        }
        return new QName(namespace2.getNamespaceURI(), local, prefix);
    }
    
    @Deprecated
    public QName resolveQName(final String qname) {
        return this.resolveQName(qname, true);
    }
    
    @Deprecated
    public static void setNewElement(final OMElement parent, final OMElement myElement, final OMElement newElement) {
        if (myElement != null) {
            myElement.discard();
        }
        parent.addChild(newElement);
    }
    
    @Deprecated
    public static OMElement getChildWithName(final OMElement parent, final String childName) {
        final Iterator childrenIter = parent.getChildren();
        while (childrenIter.hasNext()) {
            final OMNode node = childrenIter.next();
            if (node.getType() == 1 && childName.equals(((OMElement)node).getLocalName())) {
                return (OMElement)node;
            }
        }
        return null;
    }
    
    @Deprecated
    public static String getContentID(final XMLStreamReader parser, final String charsetEncoding) {
        return getContentID(parser);
    }
    
    public static String getContentID(final XMLStreamReader parser) {
        if (parser.getAttributeCount() > 0 && parser.getAttributeLocalName(0).equals("href")) {
            return getContentIDFromHref(parser.getAttributeValue(0));
        }
        throw new OMException("Href attribute not found in XOP:Include element");
    }
    
    public static String getContentIDFromHref(final String href) {
        return XOPUtils.getContentIDFromURL(href);
    }
    
    public static OMElement importOMElement(final OMElement omElement, final OMFactory omFactory) {
        if (omElement.getOMFactory().getMetaFactory() == omFactory.getMetaFactory()) {
            return omElement;
        }
        final OMElement documentElement = omFactory.getMetaFactory().createStAXOMBuilder(omFactory, omElement.getXMLStreamReader()).getDocumentElement();
        documentElement.build();
        return documentElement;
    }
    
    public static SOAPHeaderBlock toSOAPHeaderBlock(final OMElement omElement, final SOAPFactory factory) throws Exception {
        if (omElement instanceof SOAPHeaderBlock) {
            return (SOAPHeaderBlock)omElement;
        }
        final QName name = omElement.getQName();
        final String localName = name.getLocalPart();
        final OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        omElement.serialize(baos);
        final ByteArrayDataSource bads = new ByteArrayDataSource(baos.toByteArray(), "utf-8");
        final SOAPHeaderBlock block = factory.createSOAPHeaderBlock(localName, namespace, bads);
        return block;
    }
    
    @Deprecated
    public static Reader getTextAsStream(final OMElement element, final boolean cache) {
        return element.getTextAsStream(cache);
    }
    
    @Deprecated
    public static void writeTextTo(final OMElement element, final Writer out, final boolean cache) throws XMLStreamException, IOException {
        element.writeTextTo(out, cache);
    }
}
