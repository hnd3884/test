package org.apache.xmlbeans.impl.xb.xmlschema;

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
import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface LangAttribute extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(LangAttribute.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLLANG").resolveHandle("lange126attrtypetype");
    
    String getLang();
    
    XmlLanguage xgetLang();
    
    boolean isSetLang();
    
    void setLang(final String p0);
    
    void xsetLang(final XmlLanguage p0);
    
    void unsetLang();
    
    public static final class Factory
    {
        public static LangAttribute newInstance() {
            return (LangAttribute)XmlBeans.getContextTypeLoader().newInstance(LangAttribute.type, null);
        }
        
        public static LangAttribute newInstance(final XmlOptions options) {
            return (LangAttribute)XmlBeans.getContextTypeLoader().newInstance(LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final String xmlAsString) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final File file) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(file, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(file, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final URL u) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(u, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(u, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final InputStream is) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(is, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(is, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final Reader r) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(r, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(r, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final XMLStreamReader sr) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(sr, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(sr, LangAttribute.type, options);
        }
        
        public static LangAttribute parse(final Node node) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(node, LangAttribute.type, null);
        }
        
        public static LangAttribute parse(final Node node, final XmlOptions options) throws XmlException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(node, LangAttribute.type, options);
        }
        
        @Deprecated
        public static LangAttribute parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(xis, LangAttribute.type, null);
        }
        
        @Deprecated
        public static LangAttribute parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (LangAttribute)XmlBeans.getContextTypeLoader().parse(xis, LangAttribute.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LangAttribute.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LangAttribute.type, options);
        }
        
        private Factory() {
        }
    }
}
