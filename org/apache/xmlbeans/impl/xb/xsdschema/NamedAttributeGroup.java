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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface NamedAttributeGroup extends AttributeGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NamedAttributeGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("namedattributegroup2e29type");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        public static NamedAttributeGroup newInstance() {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().newInstance(NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup newInstance(final XmlOptions options) {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().newInstance(NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final String xmlAsString) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final File file) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(file, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(file, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final URL u) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(u, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(u, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final InputStream is) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(is, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(is, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final Reader r) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(r, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(r, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final XMLStreamReader sr) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(sr, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(sr, NamedAttributeGroup.type, options);
        }
        
        public static NamedAttributeGroup parse(final Node node) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(node, NamedAttributeGroup.type, null);
        }
        
        public static NamedAttributeGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(node, NamedAttributeGroup.type, options);
        }
        
        @Deprecated
        public static NamedAttributeGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(xis, NamedAttributeGroup.type, null);
        }
        
        @Deprecated
        public static NamedAttributeGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NamedAttributeGroup)XmlBeans.getContextTypeLoader().parse(xis, NamedAttributeGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamedAttributeGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamedAttributeGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
