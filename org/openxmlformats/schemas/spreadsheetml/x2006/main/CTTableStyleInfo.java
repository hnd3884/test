package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableStyleInfo extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyleInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestyleinfo499atype");
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    boolean getShowFirstColumn();
    
    XmlBoolean xgetShowFirstColumn();
    
    boolean isSetShowFirstColumn();
    
    void setShowFirstColumn(final boolean p0);
    
    void xsetShowFirstColumn(final XmlBoolean p0);
    
    void unsetShowFirstColumn();
    
    boolean getShowLastColumn();
    
    XmlBoolean xgetShowLastColumn();
    
    boolean isSetShowLastColumn();
    
    void setShowLastColumn(final boolean p0);
    
    void xsetShowLastColumn(final XmlBoolean p0);
    
    void unsetShowLastColumn();
    
    boolean getShowRowStripes();
    
    XmlBoolean xgetShowRowStripes();
    
    boolean isSetShowRowStripes();
    
    void setShowRowStripes(final boolean p0);
    
    void xsetShowRowStripes(final XmlBoolean p0);
    
    void unsetShowRowStripes();
    
    boolean getShowColumnStripes();
    
    XmlBoolean xgetShowColumnStripes();
    
    boolean isSetShowColumnStripes();
    
    void setShowColumnStripes(final boolean p0);
    
    void xsetShowColumnStripes(final XmlBoolean p0);
    
    void unsetShowColumnStripes();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyleInfo.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyleInfo newInstance() {
            return (CTTableStyleInfo)getTypeLoader().newInstance(CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyleInfo)getTypeLoader().newInstance(CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final String s) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(s, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(s, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final File file) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(file, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(file, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final URL url) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(url, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(url, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(inputStream, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(inputStream, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(reader, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleInfo)getTypeLoader().parse(reader, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(xmlStreamReader, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(xmlStreamReader, CTTableStyleInfo.type, xmlOptions);
        }
        
        public static CTTableStyleInfo parse(final Node node) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(node, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        public static CTTableStyleInfo parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleInfo)getTypeLoader().parse(node, CTTableStyleInfo.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyleInfo parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyleInfo)getTypeLoader().parse(xmlInputStream, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyleInfo parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyleInfo)getTypeLoader().parse(xmlInputStream, CTTableStyleInfo.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleInfo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleInfo.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
