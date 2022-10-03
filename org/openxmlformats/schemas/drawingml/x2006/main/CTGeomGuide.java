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

public interface CTGeomGuide extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGeomGuide.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgeomguidef191type");
    
    String getName();
    
    STGeomGuideName xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STGeomGuideName p0);
    
    String getFmla();
    
    STGeomGuideFormula xgetFmla();
    
    void setFmla(final String p0);
    
    void xsetFmla(final STGeomGuideFormula p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGeomGuide.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGeomGuide newInstance() {
            return (CTGeomGuide)getTypeLoader().newInstance(CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide newInstance(final XmlOptions xmlOptions) {
            return (CTGeomGuide)getTypeLoader().newInstance(CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final String s) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(s, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(s, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final File file) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(file, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(file, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final URL url) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(url, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(url, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(inputStream, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(inputStream, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final Reader reader) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(reader, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuide)getTypeLoader().parse(reader, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(xmlStreamReader, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(xmlStreamReader, CTGeomGuide.type, xmlOptions);
        }
        
        public static CTGeomGuide parse(final Node node) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(node, CTGeomGuide.type, (XmlOptions)null);
        }
        
        public static CTGeomGuide parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuide)getTypeLoader().parse(node, CTGeomGuide.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGeomGuide parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGeomGuide)getTypeLoader().parse(xmlInputStream, CTGeomGuide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGeomGuide parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGeomGuide)getTypeLoader().parse(xmlInputStream, CTGeomGuide.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGeomGuide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGeomGuide.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
