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

public interface LocalComplexType extends ComplexType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(LocalComplexType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("localcomplextype6494type");
    
    public static final class Factory
    {
        public static LocalComplexType newInstance() {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().newInstance(LocalComplexType.type, null);
        }
        
        public static LocalComplexType newInstance(final XmlOptions options) {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().newInstance(LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final String xmlAsString) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final File file) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(file, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(file, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final URL u) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(u, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(u, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final InputStream is) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(is, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(is, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final Reader r) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(r, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(r, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final XMLStreamReader sr) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(sr, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(sr, LocalComplexType.type, options);
        }
        
        public static LocalComplexType parse(final Node node) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(node, LocalComplexType.type, null);
        }
        
        public static LocalComplexType parse(final Node node, final XmlOptions options) throws XmlException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(node, LocalComplexType.type, options);
        }
        
        @Deprecated
        public static LocalComplexType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(xis, LocalComplexType.type, null);
        }
        
        @Deprecated
        public static LocalComplexType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (LocalComplexType)XmlBeans.getContextTypeLoader().parse(xis, LocalComplexType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalComplexType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalComplexType.type, options);
        }
        
        private Factory() {
        }
    }
}
