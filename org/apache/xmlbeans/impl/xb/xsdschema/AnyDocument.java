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
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface AnyDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AnyDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anye729doctype");
    
    Any getAny();
    
    void setAny(final Any p0);
    
    Any addNewAny();
    
    public interface Any extends Wildcard
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Any.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anye9d1elemtype");
        
        BigInteger getMinOccurs();
        
        XmlNonNegativeInteger xgetMinOccurs();
        
        boolean isSetMinOccurs();
        
        void setMinOccurs(final BigInteger p0);
        
        void xsetMinOccurs(final XmlNonNegativeInteger p0);
        
        void unsetMinOccurs();
        
        Object getMaxOccurs();
        
        AllNNI xgetMaxOccurs();
        
        boolean isSetMaxOccurs();
        
        void setMaxOccurs(final Object p0);
        
        void xsetMaxOccurs(final AllNNI p0);
        
        void unsetMaxOccurs();
        
        public static final class Factory
        {
            public static Any newInstance() {
                return (Any)XmlBeans.getContextTypeLoader().newInstance(Any.type, null);
            }
            
            public static Any newInstance(final XmlOptions options) {
                return (Any)XmlBeans.getContextTypeLoader().newInstance(Any.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static AnyDocument newInstance() {
            return (AnyDocument)XmlBeans.getContextTypeLoader().newInstance(AnyDocument.type, null);
        }
        
        public static AnyDocument newInstance(final XmlOptions options) {
            return (AnyDocument)XmlBeans.getContextTypeLoader().newInstance(AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final String xmlAsString) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final File file) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(file, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(file, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final URL u) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(u, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(u, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final InputStream is) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(is, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(is, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final Reader r) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(r, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(r, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(sr, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(sr, AnyDocument.type, options);
        }
        
        public static AnyDocument parse(final Node node) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(node, AnyDocument.type, null);
        }
        
        public static AnyDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(node, AnyDocument.type, options);
        }
        
        @Deprecated
        public static AnyDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(xis, AnyDocument.type, null);
        }
        
        @Deprecated
        public static AnyDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AnyDocument)XmlBeans.getContextTypeLoader().parse(xis, AnyDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnyDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnyDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
