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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotTableStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotTableStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivottablestyle0f84type");
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    boolean getShowRowHeaders();
    
    XmlBoolean xgetShowRowHeaders();
    
    boolean isSetShowRowHeaders();
    
    void setShowRowHeaders(final boolean p0);
    
    void xsetShowRowHeaders(final XmlBoolean p0);
    
    void unsetShowRowHeaders();
    
    boolean getShowColHeaders();
    
    XmlBoolean xgetShowColHeaders();
    
    boolean isSetShowColHeaders();
    
    void setShowColHeaders(final boolean p0);
    
    void xsetShowColHeaders(final XmlBoolean p0);
    
    void unsetShowColHeaders();
    
    boolean getShowRowStripes();
    
    XmlBoolean xgetShowRowStripes();
    
    boolean isSetShowRowStripes();
    
    void setShowRowStripes(final boolean p0);
    
    void xsetShowRowStripes(final XmlBoolean p0);
    
    void unsetShowRowStripes();
    
    boolean getShowColStripes();
    
    XmlBoolean xgetShowColStripes();
    
    boolean isSetShowColStripes();
    
    void setShowColStripes(final boolean p0);
    
    void xsetShowColStripes(final XmlBoolean p0);
    
    void unsetShowColStripes();
    
    boolean getShowLastColumn();
    
    XmlBoolean xgetShowLastColumn();
    
    boolean isSetShowLastColumn();
    
    void setShowLastColumn(final boolean p0);
    
    void xsetShowLastColumn(final XmlBoolean p0);
    
    void unsetShowLastColumn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotTableStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotTableStyle newInstance() {
            return (CTPivotTableStyle)getTypeLoader().newInstance(CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle newInstance(final XmlOptions xmlOptions) {
            return (CTPivotTableStyle)getTypeLoader().newInstance(CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final String s) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(s, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(s, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final File file) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(file, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(file, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final URL url) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(url, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(url, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(inputStream, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(inputStream, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(reader, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableStyle)getTypeLoader().parse(reader, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(xmlStreamReader, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(xmlStreamReader, CTPivotTableStyle.type, xmlOptions);
        }
        
        public static CTPivotTableStyle parse(final Node node) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(node, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        public static CTPivotTableStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableStyle)getTypeLoader().parse(node, CTPivotTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotTableStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotTableStyle)getTypeLoader().parse(xmlInputStream, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotTableStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotTableStyle)getTypeLoader().parse(xmlInputStream, CTPivotTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotTableStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
