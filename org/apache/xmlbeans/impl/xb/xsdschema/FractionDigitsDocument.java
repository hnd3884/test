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

public interface FractionDigitsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FractionDigitsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("fractiondigitsed7bdoctype");
    
    NumFacet getFractionDigits();
    
    void setFractionDigits(final NumFacet p0);
    
    NumFacet addNewFractionDigits();
    
    public static final class Factory
    {
        public static FractionDigitsDocument newInstance() {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().newInstance(FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument newInstance(final XmlOptions options) {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().newInstance(FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final String xmlAsString) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final File file) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(file, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(file, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final URL u) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(u, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(u, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final InputStream is) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(is, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(is, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final Reader r) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(r, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(r, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final XMLStreamReader sr) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(sr, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(sr, FractionDigitsDocument.type, options);
        }
        
        public static FractionDigitsDocument parse(final Node node) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(node, FractionDigitsDocument.type, null);
        }
        
        public static FractionDigitsDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(node, FractionDigitsDocument.type, options);
        }
        
        @Deprecated
        public static FractionDigitsDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(xis, FractionDigitsDocument.type, null);
        }
        
        @Deprecated
        public static FractionDigitsDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (FractionDigitsDocument)XmlBeans.getContextTypeLoader().parse(xis, FractionDigitsDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FractionDigitsDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FractionDigitsDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
