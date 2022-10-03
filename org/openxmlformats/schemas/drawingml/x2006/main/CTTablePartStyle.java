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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTablePartStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTablePartStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablepartstylef22btype");
    
    CTTableStyleTextStyle getTcTxStyle();
    
    boolean isSetTcTxStyle();
    
    void setTcTxStyle(final CTTableStyleTextStyle p0);
    
    CTTableStyleTextStyle addNewTcTxStyle();
    
    void unsetTcTxStyle();
    
    CTTableStyleCellStyle getTcStyle();
    
    boolean isSetTcStyle();
    
    void setTcStyle(final CTTableStyleCellStyle p0);
    
    CTTableStyleCellStyle addNewTcStyle();
    
    void unsetTcStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTablePartStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTablePartStyle newInstance() {
            return (CTTablePartStyle)getTypeLoader().newInstance(CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTablePartStyle)getTypeLoader().newInstance(CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final String s) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(s, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(s, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final File file) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(file, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(file, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final URL url) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(url, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(url, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(inputStream, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(inputStream, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(reader, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePartStyle)getTypeLoader().parse(reader, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(xmlStreamReader, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(xmlStreamReader, CTTablePartStyle.type, xmlOptions);
        }
        
        public static CTTablePartStyle parse(final Node node) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(node, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        public static CTTablePartStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePartStyle)getTypeLoader().parse(node, CTTablePartStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTablePartStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTablePartStyle)getTypeLoader().parse(xmlInputStream, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTablePartStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTablePartStyle)getTypeLoader().parse(xmlInputStream, CTTablePartStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTablePartStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTablePartStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
