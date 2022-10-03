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

public interface CTTextSpacing extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextSpacing.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextspacingef87type");
    
    CTTextSpacingPercent getSpcPct();
    
    boolean isSetSpcPct();
    
    void setSpcPct(final CTTextSpacingPercent p0);
    
    CTTextSpacingPercent addNewSpcPct();
    
    void unsetSpcPct();
    
    CTTextSpacingPoint getSpcPts();
    
    boolean isSetSpcPts();
    
    void setSpcPts(final CTTextSpacingPoint p0);
    
    CTTextSpacingPoint addNewSpcPts();
    
    void unsetSpcPts();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextSpacing.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextSpacing newInstance() {
            return (CTTextSpacing)getTypeLoader().newInstance(CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing newInstance(final XmlOptions xmlOptions) {
            return (CTTextSpacing)getTypeLoader().newInstance(CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final String s) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(s, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(s, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final File file) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(file, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(file, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final URL url) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(url, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(url, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(inputStream, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(inputStream, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final Reader reader) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(reader, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacing)getTypeLoader().parse(reader, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(xmlStreamReader, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(xmlStreamReader, CTTextSpacing.type, xmlOptions);
        }
        
        public static CTTextSpacing parse(final Node node) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(node, CTTextSpacing.type, (XmlOptions)null);
        }
        
        public static CTTextSpacing parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacing)getTypeLoader().parse(node, CTTextSpacing.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextSpacing parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextSpacing)getTypeLoader().parse(xmlInputStream, CTTextSpacing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextSpacing parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextSpacing)getTypeLoader().parse(xmlInputStream, CTTextSpacing.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacing.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
