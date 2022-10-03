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

public interface CTTextNormalAutofit extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextNormalAutofit.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextnormalautofitbbdftype");
    
    int getFontScale();
    
    STTextFontScalePercent xgetFontScale();
    
    boolean isSetFontScale();
    
    void setFontScale(final int p0);
    
    void xsetFontScale(final STTextFontScalePercent p0);
    
    void unsetFontScale();
    
    int getLnSpcReduction();
    
    STTextSpacingPercent xgetLnSpcReduction();
    
    boolean isSetLnSpcReduction();
    
    void setLnSpcReduction(final int p0);
    
    void xsetLnSpcReduction(final STTextSpacingPercent p0);
    
    void unsetLnSpcReduction();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextNormalAutofit.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextNormalAutofit newInstance() {
            return (CTTextNormalAutofit)getTypeLoader().newInstance(CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit newInstance(final XmlOptions xmlOptions) {
            return (CTTextNormalAutofit)getTypeLoader().newInstance(CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final String s) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(s, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(s, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final File file) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(file, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(file, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final URL url) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(url, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(url, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(inputStream, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(inputStream, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final Reader reader) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(reader, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNormalAutofit)getTypeLoader().parse(reader, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(xmlStreamReader, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(xmlStreamReader, CTTextNormalAutofit.type, xmlOptions);
        }
        
        public static CTTextNormalAutofit parse(final Node node) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(node, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNormalAutofit parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNormalAutofit)getTypeLoader().parse(node, CTTextNormalAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextNormalAutofit parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextNormalAutofit)getTypeLoader().parse(xmlInputStream, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextNormalAutofit parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextNormalAutofit)getTypeLoader().parse(xmlInputStream, CTTextNormalAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNormalAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNormalAutofit.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
