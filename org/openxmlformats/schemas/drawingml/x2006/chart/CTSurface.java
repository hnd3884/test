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

public interface CTSurface extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSurface.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsurface5a19type");
    
    CTUnsignedInt getThickness();
    
    boolean isSetThickness();
    
    void setThickness(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewThickness();
    
    void unsetThickness();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTPictureOptions getPictureOptions();
    
    boolean isSetPictureOptions();
    
    void setPictureOptions(final CTPictureOptions p0);
    
    CTPictureOptions addNewPictureOptions();
    
    void unsetPictureOptions();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSurface.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSurface newInstance() {
            return (CTSurface)getTypeLoader().newInstance(CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface newInstance(final XmlOptions xmlOptions) {
            return (CTSurface)getTypeLoader().newInstance(CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final String s) throws XmlException {
            return (CTSurface)getTypeLoader().parse(s, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurface)getTypeLoader().parse(s, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final File file) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(file, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(file, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final URL url) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(url, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(url, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(inputStream, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(inputStream, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final Reader reader) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(reader, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurface)getTypeLoader().parse(reader, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSurface)getTypeLoader().parse(xmlStreamReader, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurface)getTypeLoader().parse(xmlStreamReader, CTSurface.type, xmlOptions);
        }
        
        public static CTSurface parse(final Node node) throws XmlException {
            return (CTSurface)getTypeLoader().parse(node, CTSurface.type, (XmlOptions)null);
        }
        
        public static CTSurface parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurface)getTypeLoader().parse(node, CTSurface.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSurface parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSurface)getTypeLoader().parse(xmlInputStream, CTSurface.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSurface parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSurface)getTypeLoader().parse(xmlInputStream, CTSurface.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurface.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurface.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
