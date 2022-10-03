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

public interface TopLevelSimpleType extends SimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TopLevelSimpleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("toplevelsimpletypec958type");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        public static TopLevelSimpleType newInstance() {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().newInstance(TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType newInstance(final XmlOptions options) {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().newInstance(TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final String xmlAsString) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final File file) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(file, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(file, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final URL u) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(u, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(u, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final InputStream is) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(is, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(is, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final Reader r) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(r, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(r, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final XMLStreamReader sr) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(sr, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(sr, TopLevelSimpleType.type, options);
        }
        
        public static TopLevelSimpleType parse(final Node node) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(node, TopLevelSimpleType.type, null);
        }
        
        public static TopLevelSimpleType parse(final Node node, final XmlOptions options) throws XmlException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(node, TopLevelSimpleType.type, options);
        }
        
        @Deprecated
        public static TopLevelSimpleType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(xis, TopLevelSimpleType.type, null);
        }
        
        @Deprecated
        public static TopLevelSimpleType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TopLevelSimpleType)XmlBeans.getContextTypeLoader().parse(xis, TopLevelSimpleType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelSimpleType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelSimpleType.type, options);
        }
        
        private Factory() {
        }
    }
}
