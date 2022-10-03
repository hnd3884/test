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

public interface CTFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfillproperties2371type");
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFillProperties newInstance() {
            return (CTFillProperties)getTypeLoader().newInstance(CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTFillProperties)getTypeLoader().newInstance(CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final String s) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(s, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(s, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final File file) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(file, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(file, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(url, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(url, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(inputStream, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(inputStream, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(reader, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFillProperties)getTypeLoader().parse(reader, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(xmlStreamReader, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(xmlStreamReader, CTFillProperties.type, xmlOptions);
        }
        
        public static CTFillProperties parse(final Node node) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(node, CTFillProperties.type, (XmlOptions)null);
        }
        
        public static CTFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFillProperties)getTypeLoader().parse(node, CTFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFillProperties)getTypeLoader().parse(xmlInputStream, CTFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFillProperties)getTypeLoader().parse(xmlInputStream, CTFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
