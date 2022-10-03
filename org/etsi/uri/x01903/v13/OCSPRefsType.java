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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface OCSPRefsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(OCSPRefsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ocsprefstypef13ftype");
    
    List<OCSPRefType> getOCSPRefList();
    
    @Deprecated
    OCSPRefType[] getOCSPRefArray();
    
    OCSPRefType getOCSPRefArray(final int p0);
    
    int sizeOfOCSPRefArray();
    
    void setOCSPRefArray(final OCSPRefType[] p0);
    
    void setOCSPRefArray(final int p0, final OCSPRefType p1);
    
    OCSPRefType insertNewOCSPRef(final int p0);
    
    OCSPRefType addNewOCSPRef();
    
    void removeOCSPRef(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(OCSPRefsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static OCSPRefsType newInstance() {
            return (OCSPRefsType)getTypeLoader().newInstance(OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType newInstance(final XmlOptions xmlOptions) {
            return (OCSPRefsType)getTypeLoader().newInstance(OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final String s) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(s, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(s, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final File file) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(file, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(file, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final URL url) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(url, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(url, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(inputStream, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(inputStream, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final Reader reader) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(reader, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefsType)getTypeLoader().parse(reader, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(xmlStreamReader, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(xmlStreamReader, OCSPRefsType.type, xmlOptions);
        }
        
        public static OCSPRefsType parse(final Node node) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(node, OCSPRefsType.type, (XmlOptions)null);
        }
        
        public static OCSPRefsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefsType)getTypeLoader().parse(node, OCSPRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static OCSPRefsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (OCSPRefsType)getTypeLoader().parse(xmlInputStream, OCSPRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static OCSPRefsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (OCSPRefsType)getTypeLoader().parse(xmlInputStream, OCSPRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPRefsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
