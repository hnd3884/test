package org.apache.xmlbeans.impl.xb.xmlconfig;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public interface NamespacePrefixList extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NamespacePrefixList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("namespaceprefixlistec0ctype");
    
    List getListValue();
    
    List xgetListValue();
    
    void setListValue(final List p0);
    
    @Deprecated
    List listValue();
    
    @Deprecated
    List xlistValue();
    
    @Deprecated
    void set(final List p0);
    
    public static final class Factory
    {
        public static NamespacePrefixList newValue(final Object obj) {
            return (NamespacePrefixList)NamespacePrefixList.type.newValue(obj);
        }
        
        public static NamespacePrefixList newInstance() {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().newInstance(NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList newInstance(final XmlOptions options) {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().newInstance(NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final String xmlAsString) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final File file) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(file, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(file, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final URL u) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(u, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(u, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final InputStream is) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(is, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(is, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final Reader r) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(r, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(r, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final XMLStreamReader sr) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(sr, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(sr, NamespacePrefixList.type, options);
        }
        
        public static NamespacePrefixList parse(final Node node) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(node, NamespacePrefixList.type, null);
        }
        
        public static NamespacePrefixList parse(final Node node, final XmlOptions options) throws XmlException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(node, NamespacePrefixList.type, options);
        }
        
        @Deprecated
        public static NamespacePrefixList parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(xis, NamespacePrefixList.type, null);
        }
        
        @Deprecated
        public static NamespacePrefixList parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NamespacePrefixList)XmlBeans.getContextTypeLoader().parse(xis, NamespacePrefixList.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamespacePrefixList.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamespacePrefixList.type, options);
        }
        
        private Factory() {
        }
    }
}
