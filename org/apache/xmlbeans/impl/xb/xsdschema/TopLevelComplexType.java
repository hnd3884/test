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

public interface TopLevelComplexType extends ComplexType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TopLevelComplexType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("toplevelcomplextypee58atype");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        public static TopLevelComplexType newInstance() {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().newInstance(TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType newInstance(final XmlOptions options) {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().newInstance(TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final String xmlAsString) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final File file) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(file, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(file, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final URL u) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(u, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(u, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final InputStream is) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(is, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(is, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final Reader r) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(r, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(r, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final XMLStreamReader sr) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(sr, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(sr, TopLevelComplexType.type, options);
        }
        
        public static TopLevelComplexType parse(final Node node) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(node, TopLevelComplexType.type, null);
        }
        
        public static TopLevelComplexType parse(final Node node, final XmlOptions options) throws XmlException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(node, TopLevelComplexType.type, options);
        }
        
        @Deprecated
        public static TopLevelComplexType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(xis, TopLevelComplexType.type, null);
        }
        
        @Deprecated
        public static TopLevelComplexType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TopLevelComplexType)XmlBeans.getContextTypeLoader().parse(xis, TopLevelComplexType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelComplexType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelComplexType.type, options);
        }
        
        private Factory() {
        }
    }
}
