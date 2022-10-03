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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTScaling extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScaling.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscaling1dfftype");
    
    CTLogBase getLogBase();
    
    boolean isSetLogBase();
    
    void setLogBase(final CTLogBase p0);
    
    CTLogBase addNewLogBase();
    
    void unsetLogBase();
    
    CTOrientation getOrientation();
    
    boolean isSetOrientation();
    
    void setOrientation(final CTOrientation p0);
    
    CTOrientation addNewOrientation();
    
    void unsetOrientation();
    
    CTDouble getMax();
    
    boolean isSetMax();
    
    void setMax(final CTDouble p0);
    
    CTDouble addNewMax();
    
    void unsetMax();
    
    CTDouble getMin();
    
    boolean isSetMin();
    
    void setMin(final CTDouble p0);
    
    CTDouble addNewMin();
    
    void unsetMin();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScaling.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScaling newInstance() {
            return (CTScaling)getTypeLoader().newInstance(CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling newInstance(final XmlOptions xmlOptions) {
            return (CTScaling)getTypeLoader().newInstance(CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final String s) throws XmlException {
            return (CTScaling)getTypeLoader().parse(s, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScaling)getTypeLoader().parse(s, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final File file) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(file, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(file, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final URL url) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(url, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(url, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(inputStream, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(inputStream, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final Reader reader) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(reader, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScaling)getTypeLoader().parse(reader, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScaling)getTypeLoader().parse(xmlStreamReader, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScaling)getTypeLoader().parse(xmlStreamReader, CTScaling.type, xmlOptions);
        }
        
        public static CTScaling parse(final Node node) throws XmlException {
            return (CTScaling)getTypeLoader().parse(node, CTScaling.type, (XmlOptions)null);
        }
        
        public static CTScaling parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScaling)getTypeLoader().parse(node, CTScaling.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScaling parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScaling)getTypeLoader().parse(xmlInputStream, CTScaling.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScaling parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScaling)getTypeLoader().parse(xmlInputStream, CTScaling.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScaling.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScaling.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
