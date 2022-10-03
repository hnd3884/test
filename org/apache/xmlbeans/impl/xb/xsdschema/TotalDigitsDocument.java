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

public interface TotalDigitsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TotalDigitsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("totaldigits4a8bdoctype");
    
    TotalDigits getTotalDigits();
    
    void setTotalDigits(final TotalDigits p0);
    
    TotalDigits addNewTotalDigits();
    
    public interface TotalDigits extends NumFacet
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TotalDigits.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("totaldigits2615elemtype");
        
        public static final class Factory
        {
            public static TotalDigits newInstance() {
                return (TotalDigits)XmlBeans.getContextTypeLoader().newInstance(TotalDigits.type, null);
            }
            
            public static TotalDigits newInstance(final XmlOptions options) {
                return (TotalDigits)XmlBeans.getContextTypeLoader().newInstance(TotalDigits.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static TotalDigitsDocument newInstance() {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().newInstance(TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument newInstance(final XmlOptions options) {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().newInstance(TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final String xmlAsString) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final File file) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(file, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(file, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final URL u) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(u, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(u, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final InputStream is) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(is, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(is, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final Reader r) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(r, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(r, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final XMLStreamReader sr) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(sr, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(sr, TotalDigitsDocument.type, options);
        }
        
        public static TotalDigitsDocument parse(final Node node) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(node, TotalDigitsDocument.type, null);
        }
        
        public static TotalDigitsDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(node, TotalDigitsDocument.type, options);
        }
        
        @Deprecated
        public static TotalDigitsDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(xis, TotalDigitsDocument.type, null);
        }
        
        @Deprecated
        public static TotalDigitsDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TotalDigitsDocument)XmlBeans.getContextTypeLoader().parse(xis, TotalDigitsDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TotalDigitsDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TotalDigitsDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
