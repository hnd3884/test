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

public interface CTTableStyleCellStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyleCellStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestylecellstyle1fddtype");
    
    CTTableCellBorderStyle getTcBdr();
    
    boolean isSetTcBdr();
    
    void setTcBdr(final CTTableCellBorderStyle p0);
    
    CTTableCellBorderStyle addNewTcBdr();
    
    void unsetTcBdr();
    
    CTFillProperties getFill();
    
    boolean isSetFill();
    
    void setFill(final CTFillProperties p0);
    
    CTFillProperties addNewFill();
    
    void unsetFill();
    
    CTStyleMatrixReference getFillRef();
    
    boolean isSetFillRef();
    
    void setFillRef(final CTStyleMatrixReference p0);
    
    CTStyleMatrixReference addNewFillRef();
    
    void unsetFillRef();
    
    CTCell3D getCell3D();
    
    boolean isSetCell3D();
    
    void setCell3D(final CTCell3D p0);
    
    CTCell3D addNewCell3D();
    
    void unsetCell3D();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyleCellStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyleCellStyle newInstance() {
            return (CTTableStyleCellStyle)getTypeLoader().newInstance(CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyleCellStyle)getTypeLoader().newInstance(CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final String s) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(s, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(s, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final File file) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(file, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(file, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final URL url) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(url, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(url, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(inputStream, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(inputStream, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(reader, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(reader, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        public static CTTableStyleCellStyle parse(final Node node) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(node, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleCellStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(node, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyleCellStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(xmlInputStream, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyleCellStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyleCellStyle)getTypeLoader().parse(xmlInputStream, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleCellStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleCellStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
