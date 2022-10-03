package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XmlVisitor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.SAXConnector;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.InterningXmlVisitor;
import javax.xml.validation.Schema;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.v2.runtime.output.DOMOutput;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.Binder;

public class BinderImpl<XmlNode> extends Binder<XmlNode>
{
    private final JAXBContextImpl context;
    private UnmarshallerImpl unmarshaller;
    private MarshallerImpl marshaller;
    private final InfosetScanner<XmlNode> scanner;
    private final AssociationMap<XmlNode> assoc;
    
    BinderImpl(final JAXBContextImpl _context, final InfosetScanner<XmlNode> scanner) {
        this.assoc = new AssociationMap<XmlNode>();
        this.context = _context;
        this.scanner = scanner;
    }
    
    private UnmarshallerImpl getUnmarshaller() {
        if (this.unmarshaller == null) {
            this.unmarshaller = new UnmarshallerImpl(this.context, this.assoc);
        }
        return this.unmarshaller;
    }
    
    private MarshallerImpl getMarshaller() {
        if (this.marshaller == null) {
            this.marshaller = new MarshallerImpl(this.context, this.assoc);
        }
        return this.marshaller;
    }
    
    @Override
    public void marshal(final Object jaxbObject, final XmlNode xmlNode) throws JAXBException {
        if (xmlNode == null || jaxbObject == null) {
            throw new IllegalArgumentException();
        }
        this.getMarshaller().marshal(jaxbObject, this.createOutput(xmlNode));
    }
    
    private DOMOutput createOutput(final XmlNode xmlNode) {
        return new DOMOutput((Node)xmlNode, this.assoc);
    }
    
    @Override
    public Object updateJAXB(final XmlNode xmlNode) throws JAXBException {
        return this.associativeUnmarshal(xmlNode, true, null);
    }
    
    @Override
    public Object unmarshal(final XmlNode xmlNode) throws JAXBException {
        return this.associativeUnmarshal(xmlNode, false, null);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshal(final XmlNode xmlNode, final Class<T> expectedType) throws JAXBException {
        if (expectedType == null) {
            throw new IllegalArgumentException();
        }
        return (JAXBElement)this.associativeUnmarshal(xmlNode, true, expectedType);
    }
    
    @Override
    public void setSchema(final Schema schema) {
        this.getMarshaller().setSchema(schema);
        this.getUnmarshaller().setSchema(schema);
    }
    
    @Override
    public Schema getSchema() {
        return this.getUnmarshaller().getSchema();
    }
    
    private Object associativeUnmarshal(final XmlNode xmlNode, final boolean inplace, final Class expectedType) throws JAXBException {
        if (xmlNode == null) {
            throw new IllegalArgumentException();
        }
        JaxBeanInfo bi = null;
        if (expectedType != null) {
            bi = this.context.getBeanInfo((Class<Object>)expectedType, true);
        }
        final InterningXmlVisitor handler = new InterningXmlVisitor(this.getUnmarshaller().createUnmarshallerHandler(this.scanner, inplace, bi));
        this.scanner.setContentHandler(new SAXConnector(handler, this.scanner.getLocator()));
        try {
            this.scanner.scan(xmlNode);
        }
        catch (final SAXException e) {
            throw this.unmarshaller.createUnmarshalException(e);
        }
        return handler.getContext().getResult();
    }
    
    @Override
    public XmlNode getXMLNode(final Object jaxbObject) {
        if (jaxbObject == null) {
            throw new IllegalArgumentException();
        }
        final AssociationMap.Entry<XmlNode> e = this.assoc.byPeer(jaxbObject);
        if (e == null) {
            return null;
        }
        return e.element();
    }
    
    @Override
    public Object getJAXBNode(final XmlNode xmlNode) {
        if (xmlNode == null) {
            throw new IllegalArgumentException();
        }
        final AssociationMap.Entry e = this.assoc.byElement(xmlNode);
        if (e == null) {
            return null;
        }
        if (e.outer() != null) {
            return e.outer();
        }
        return e.inner();
    }
    
    @Override
    public XmlNode updateXML(final Object jaxbObject) throws JAXBException {
        return this.updateXML(jaxbObject, this.getXMLNode(jaxbObject));
    }
    
    @Override
    public XmlNode updateXML(Object jaxbObject, final XmlNode xmlNode) throws JAXBException {
        if (jaxbObject == null || xmlNode == null) {
            throw new IllegalArgumentException();
        }
        final Element e = (Element)xmlNode;
        final Node ns = e.getNextSibling();
        final Node p = e.getParentNode();
        p.removeChild(e);
        final JaxBeanInfo bi = this.context.getBeanInfo(jaxbObject, true);
        if (!bi.isElement()) {
            jaxbObject = new JAXBElement(new QName(e.getNamespaceURI(), e.getLocalName()), (Class<Object>)bi.jaxbType, jaxbObject);
        }
        this.getMarshaller().marshal(jaxbObject, p);
        final Node newNode = p.getLastChild();
        p.removeChild(newNode);
        p.insertBefore(newNode, ns);
        return (XmlNode)newNode;
    }
    
    @Override
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        this.getUnmarshaller().setEventHandler(handler);
        this.getMarshaller().setEventHandler(handler);
    }
    
    @Override
    public ValidationEventHandler getEventHandler() {
        return this.getUnmarshaller().getEventHandler();
    }
    
    @Override
    public Object getProperty(final String name) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0]));
        }
        if (this.excludeProperty(name)) {
            throw new PropertyException(name);
        }
        Object prop = null;
        PropertyException pe = null;
        try {
            prop = this.getMarshaller().getProperty(name);
            return prop;
        }
        catch (final PropertyException p) {
            pe = p;
            try {
                prop = this.getUnmarshaller().getProperty(name);
                return prop;
            }
            catch (final PropertyException p) {
                pe = p;
                pe.setStackTrace(Thread.currentThread().getStackTrace());
                throw pe;
            }
        }
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0]));
        }
        if (this.excludeProperty(name)) {
            throw new PropertyException(name, value);
        }
        PropertyException pe = null;
        try {
            this.getMarshaller().setProperty(name, value);
        }
        catch (final PropertyException p) {
            pe = p;
            try {
                this.getUnmarshaller().setProperty(name, value);
            }
            catch (final PropertyException p) {
                pe = p;
                pe.setStackTrace(Thread.currentThread().getStackTrace());
                throw pe;
            }
        }
    }
    
    private boolean excludeProperty(final String name) {
        return name.equals("com.sun.xml.internal.bind.characterEscapeHandler") || name.equals("com.sun.xml.internal.bind.xmlDeclaration") || name.equals("com.sun.xml.internal.bind.xmlHeaders");
    }
}
