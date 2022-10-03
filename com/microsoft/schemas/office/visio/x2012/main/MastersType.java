package com.microsoft.schemas.office.visio.x2012.main;

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

public interface MastersType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MastersType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("masterstypeaebatype");
    
    List<MasterType> getMasterList();
    
    @Deprecated
    MasterType[] getMasterArray();
    
    MasterType getMasterArray(final int p0);
    
    int sizeOfMasterArray();
    
    void setMasterArray(final MasterType[] p0);
    
    void setMasterArray(final int p0, final MasterType p1);
    
    MasterType insertNewMaster(final int p0);
    
    MasterType addNewMaster();
    
    void removeMaster(final int p0);
    
    List<MasterShortcutType> getMasterShortcutList();
    
    @Deprecated
    MasterShortcutType[] getMasterShortcutArray();
    
    MasterShortcutType getMasterShortcutArray(final int p0);
    
    int sizeOfMasterShortcutArray();
    
    void setMasterShortcutArray(final MasterShortcutType[] p0);
    
    void setMasterShortcutArray(final int p0, final MasterShortcutType p1);
    
    MasterShortcutType insertNewMasterShortcut(final int p0);
    
    MasterShortcutType addNewMasterShortcut();
    
    void removeMasterShortcut(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(MastersType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static MastersType newInstance() {
            return (MastersType)getTypeLoader().newInstance(MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType newInstance(final XmlOptions xmlOptions) {
            return (MastersType)getTypeLoader().newInstance(MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final String s) throws XmlException {
            return (MastersType)getTypeLoader().parse(s, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (MastersType)getTypeLoader().parse(s, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final File file) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(file, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(file, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final URL url) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(url, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(url, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final InputStream inputStream) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(inputStream, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(inputStream, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final Reader reader) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(reader, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MastersType)getTypeLoader().parse(reader, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (MastersType)getTypeLoader().parse(xmlStreamReader, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (MastersType)getTypeLoader().parse(xmlStreamReader, MastersType.type, xmlOptions);
        }
        
        public static MastersType parse(final Node node) throws XmlException {
            return (MastersType)getTypeLoader().parse(node, MastersType.type, (XmlOptions)null);
        }
        
        public static MastersType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (MastersType)getTypeLoader().parse(node, MastersType.type, xmlOptions);
        }
        
        @Deprecated
        public static MastersType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (MastersType)getTypeLoader().parse(xmlInputStream, MastersType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static MastersType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (MastersType)getTypeLoader().parse(xmlInputStream, MastersType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MastersType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MastersType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
