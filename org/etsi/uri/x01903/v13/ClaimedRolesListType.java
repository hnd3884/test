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

public interface ClaimedRolesListType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ClaimedRolesListType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("claimedroleslisttypef16etype");
    
    List<AnyType> getClaimedRoleList();
    
    @Deprecated
    AnyType[] getClaimedRoleArray();
    
    AnyType getClaimedRoleArray(final int p0);
    
    int sizeOfClaimedRoleArray();
    
    void setClaimedRoleArray(final AnyType[] p0);
    
    void setClaimedRoleArray(final int p0, final AnyType p1);
    
    AnyType insertNewClaimedRole(final int p0);
    
    AnyType addNewClaimedRole();
    
    void removeClaimedRole(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ClaimedRolesListType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ClaimedRolesListType newInstance() {
            return (ClaimedRolesListType)getTypeLoader().newInstance(ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType newInstance(final XmlOptions xmlOptions) {
            return (ClaimedRolesListType)getTypeLoader().newInstance(ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final String s) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(s, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(s, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final File file) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(file, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(file, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final URL url) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(url, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(url, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(inputStream, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(inputStream, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final Reader reader) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(reader, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ClaimedRolesListType)getTypeLoader().parse(reader, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(xmlStreamReader, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(xmlStreamReader, ClaimedRolesListType.type, xmlOptions);
        }
        
        public static ClaimedRolesListType parse(final Node node) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(node, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        public static ClaimedRolesListType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ClaimedRolesListType)getTypeLoader().parse(node, ClaimedRolesListType.type, xmlOptions);
        }
        
        @Deprecated
        public static ClaimedRolesListType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ClaimedRolesListType)getTypeLoader().parse(xmlInputStream, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ClaimedRolesListType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ClaimedRolesListType)getTypeLoader().parse(xmlInputStream, ClaimedRolesListType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ClaimedRolesListType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ClaimedRolesListType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
