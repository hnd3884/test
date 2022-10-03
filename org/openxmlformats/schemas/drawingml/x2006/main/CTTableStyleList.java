package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTTableStyleList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyleList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestylelist4bdctype");
    
    List<CTTableStyle> getTblStyleList();
    
    @Deprecated
    CTTableStyle[] getTblStyleArray();
    
    CTTableStyle getTblStyleArray(final int p0);
    
    int sizeOfTblStyleArray();
    
    void setTblStyleArray(final CTTableStyle[] p0);
    
    void setTblStyleArray(final int p0, final CTTableStyle p1);
    
    CTTableStyle insertNewTblStyle(final int p0);
    
    CTTableStyle addNewTblStyle();
    
    void removeTblStyle(final int p0);
    
    String getDef();
    
    STGuid xgetDef();
    
    void setDef(final String p0);
    
    void xsetDef(final STGuid p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyleList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyleList newInstance() {
            return (CTTableStyleList)getTypeLoader().newInstance(CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyleList)getTypeLoader().newInstance(CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final String s) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(s, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(s, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final File file) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(file, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(file, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final URL url) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(url, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(url, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(inputStream, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(inputStream, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(reader, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleList)getTypeLoader().parse(reader, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(xmlStreamReader, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(xmlStreamReader, CTTableStyleList.type, xmlOptions);
        }
        
        public static CTTableStyleList parse(final Node node) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(node, CTTableStyleList.type, (XmlOptions)null);
        }
        
        public static CTTableStyleList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleList)getTypeLoader().parse(node, CTTableStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyleList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyleList)getTypeLoader().parse(xmlInputStream, CTTableStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyleList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyleList)getTypeLoader().parse(xmlInputStream, CTTableStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
