package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSurfaceSer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSurfaceSer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsurfaceser3ca9type");
    
    CTUnsignedInt getIdx();
    
    void setIdx(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewIdx();
    
    CTUnsignedInt getOrder();
    
    void setOrder(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewOrder();
    
    CTSerTx getTx();
    
    boolean isSetTx();
    
    void setTx(final CTSerTx p0);
    
    CTSerTx addNewTx();
    
    void unsetTx();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTAxDataSource getCat();
    
    boolean isSetCat();
    
    void setCat(final CTAxDataSource p0);
    
    CTAxDataSource addNewCat();
    
    void unsetCat();
    
    CTNumDataSource getVal();
    
    boolean isSetVal();
    
    void setVal(final CTNumDataSource p0);
    
    CTNumDataSource addNewVal();
    
    void unsetVal();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSurfaceSer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSurfaceSer newInstance() {
            return (CTSurfaceSer)getTypeLoader().newInstance(CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer newInstance(final XmlOptions xmlOptions) {
            return (CTSurfaceSer)getTypeLoader().newInstance(CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final String s) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(s, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(s, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final File file) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(file, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(file, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final URL url) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(url, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(url, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(inputStream, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(inputStream, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final Reader reader) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(reader, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceSer)getTypeLoader().parse(reader, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(xmlStreamReader, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(xmlStreamReader, CTSurfaceSer.type, xmlOptions);
        }
        
        public static CTSurfaceSer parse(final Node node) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(node, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        public static CTSurfaceSer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceSer)getTypeLoader().parse(node, CTSurfaceSer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSurfaceSer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSurfaceSer)getTypeLoader().parse(xmlInputStream, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSurfaceSer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSurfaceSer)getTypeLoader().parse(xmlInputStream, CTSurfaceSer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurfaceSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurfaceSer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
