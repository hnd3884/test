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

public interface TopLevelAttribute extends Attribute
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TopLevelAttribute.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("toplevelattributeb338type");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        public static TopLevelAttribute newInstance() {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().newInstance(TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute newInstance(final XmlOptions options) {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().newInstance(TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final String xmlAsString) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final File file) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(file, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(file, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final URL u) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(u, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(u, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final InputStream is) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(is, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(is, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final Reader r) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(r, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(r, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final XMLStreamReader sr) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(sr, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(sr, TopLevelAttribute.type, options);
        }
        
        public static TopLevelAttribute parse(final Node node) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(node, TopLevelAttribute.type, null);
        }
        
        public static TopLevelAttribute parse(final Node node, final XmlOptions options) throws XmlException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(node, TopLevelAttribute.type, options);
        }
        
        @Deprecated
        public static TopLevelAttribute parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(xis, TopLevelAttribute.type, null);
        }
        
        @Deprecated
        public static TopLevelAttribute parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TopLevelAttribute)XmlBeans.getContextTypeLoader().parse(xis, TopLevelAttribute.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelAttribute.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelAttribute.type, options);
        }
        
        private Factory() {
        }
    }
}
