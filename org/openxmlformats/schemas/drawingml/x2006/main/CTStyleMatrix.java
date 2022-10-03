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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStyleMatrix extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStyleMatrix.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstylematrix1903type");
    
    CTFillStyleList getFillStyleLst();
    
    void setFillStyleLst(final CTFillStyleList p0);
    
    CTFillStyleList addNewFillStyleLst();
    
    CTLineStyleList getLnStyleLst();
    
    void setLnStyleLst(final CTLineStyleList p0);
    
    CTLineStyleList addNewLnStyleLst();
    
    CTEffectStyleList getEffectStyleLst();
    
    void setEffectStyleLst(final CTEffectStyleList p0);
    
    CTEffectStyleList addNewEffectStyleLst();
    
    CTBackgroundFillStyleList getBgFillStyleLst();
    
    void setBgFillStyleLst(final CTBackgroundFillStyleList p0);
    
    CTBackgroundFillStyleList addNewBgFillStyleLst();
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStyleMatrix.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStyleMatrix newInstance() {
            return (CTStyleMatrix)getTypeLoader().newInstance(CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix newInstance(final XmlOptions xmlOptions) {
            return (CTStyleMatrix)getTypeLoader().newInstance(CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final String s) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(s, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(s, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final File file) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(file, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(file, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final URL url) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(url, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(url, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(inputStream, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(inputStream, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final Reader reader) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(reader, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrix)getTypeLoader().parse(reader, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(xmlStreamReader, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(xmlStreamReader, CTStyleMatrix.type, xmlOptions);
        }
        
        public static CTStyleMatrix parse(final Node node) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(node, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrix parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrix)getTypeLoader().parse(node, CTStyleMatrix.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStyleMatrix parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStyleMatrix)getTypeLoader().parse(xmlInputStream, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStyleMatrix parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStyleMatrix)getTypeLoader().parse(xmlInputStream, CTStyleMatrix.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyleMatrix.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyleMatrix.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
