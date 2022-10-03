package org.etsi.uri.x01903.v13;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CRLIdentifierType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CRLIdentifierType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("crlidentifiertypeb702type");
    
    String getIssuer();
    
    XmlString xgetIssuer();
    
    void setIssuer(final String p0);
    
    void xsetIssuer(final XmlString p0);
    
    Calendar getIssueTime();
    
    XmlDateTime xgetIssueTime();
    
    void setIssueTime(final Calendar p0);
    
    void xsetIssueTime(final XmlDateTime p0);
    
    BigInteger getNumber();
    
    XmlInteger xgetNumber();
    
    boolean isSetNumber();
    
    void setNumber(final BigInteger p0);
    
    void xsetNumber(final XmlInteger p0);
    
    void unsetNumber();
    
    String getURI();
    
    XmlAnyURI xgetURI();
    
    boolean isSetURI();
    
    void setURI(final String p0);
    
    void xsetURI(final XmlAnyURI p0);
    
    void unsetURI();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CRLIdentifierType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CRLIdentifierType newInstance() {
            return (CRLIdentifierType)getTypeLoader().newInstance(CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType newInstance(final XmlOptions xmlOptions) {
            return (CRLIdentifierType)getTypeLoader().newInstance(CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final String s) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(s, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(s, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final File file) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(file, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(file, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final URL url) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(url, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(url, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(inputStream, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(inputStream, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final Reader reader) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(reader, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLIdentifierType)getTypeLoader().parse(reader, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(xmlStreamReader, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(xmlStreamReader, CRLIdentifierType.type, xmlOptions);
        }
        
        public static CRLIdentifierType parse(final Node node) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(node, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        public static CRLIdentifierType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CRLIdentifierType)getTypeLoader().parse(node, CRLIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static CRLIdentifierType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CRLIdentifierType)getTypeLoader().parse(xmlInputStream, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CRLIdentifierType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CRLIdentifierType)getTypeLoader().parse(xmlInputStream, CRLIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLIdentifierType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
