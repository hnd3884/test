package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface AttributeGroupDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AttributeGroupDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("attributegroup4520doctype");
    
    NamedAttributeGroup getAttributeGroup();
    
    void setAttributeGroup(final NamedAttributeGroup p0);
    
    NamedAttributeGroup addNewAttributeGroup();
    
    public static final class Factory
    {
        public static AttributeGroupDocument newInstance() {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().newInstance(AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument newInstance(final XmlOptions options) {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().newInstance(AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final String xmlAsString) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final File file) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(file, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(file, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final URL u) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(u, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(u, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final InputStream is) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(is, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(is, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final Reader r) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(r, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(r, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroupDocument.type, options);
        }
        
        public static AttributeGroupDocument parse(final Node node) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(node, AttributeGroupDocument.type, null);
        }
        
        public static AttributeGroupDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(node, AttributeGroupDocument.type, options);
        }
        
        @Deprecated
        public static AttributeGroupDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroupDocument.type, null);
        }
        
        @Deprecated
        public static AttributeGroupDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AttributeGroupDocument)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroupDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroupDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroupDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
