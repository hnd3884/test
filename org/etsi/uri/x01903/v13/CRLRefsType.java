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

public interface CRLRefsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CRLRefsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("crlrefstype2a59type");
    
    List<CRLRefType> getCRLRefList();
    
    @Deprecated
    CRLRefType[] getCRLRefArray();
    
    CRLRefType getCRLRefArray(final int p0);
    
    int sizeOfCRLRefArray();
    
    void setCRLRefArray(final CRLRefType[] p0);
    
    void setCRLRefArray(final int p0, final CRLRefType p1);
    
    CRLRefType insertNewCRLRef(final int p0);
    
    CRLRefType addNewCRLRef();
    
    void removeCRLRef(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CRLRefsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CRLRefsType newInstance() {
            return (CRLRefsType)getTypeLoader().newInstance(CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType newInstance(final XmlOptions xmlOptions) {
            return (CRLRefsType)getTypeLoader().newInstance(CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final String s) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(s, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(s, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final File file) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(file, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(file, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final URL url) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(url, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(url, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(inputStream, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(inputStream, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final Reader reader) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(reader, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefsType)getTypeLoader().parse(reader, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(xmlStreamReader, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(xmlStreamReader, CRLRefsType.type, xmlOptions);
        }
        
        public static CRLRefsType parse(final Node node) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(node, CRLRefsType.type, (XmlOptions)null);
        }
        
        public static CRLRefsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefsType)getTypeLoader().parse(node, CRLRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static CRLRefsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CRLRefsType)getTypeLoader().parse(xmlInputStream, CRLRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CRLRefsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CRLRefsType)getTypeLoader().parse(xmlInputStream, CRLRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLRefsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
