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

public interface CTRelativeRect extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRelativeRect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrelativerecta4ebtype");
    
    int getL();
    
    STPercentage xgetL();
    
    boolean isSetL();
    
    void setL(final int p0);
    
    void xsetL(final STPercentage p0);
    
    void unsetL();
    
    int getT();
    
    STPercentage xgetT();
    
    boolean isSetT();
    
    void setT(final int p0);
    
    void xsetT(final STPercentage p0);
    
    void unsetT();
    
    int getR();
    
    STPercentage xgetR();
    
    boolean isSetR();
    
    void setR(final int p0);
    
    void xsetR(final STPercentage p0);
    
    void unsetR();
    
    int getB();
    
    STPercentage xgetB();
    
    boolean isSetB();
    
    void setB(final int p0);
    
    void xsetB(final STPercentage p0);
    
    void unsetB();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRelativeRect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRelativeRect newInstance() {
            return (CTRelativeRect)getTypeLoader().newInstance(CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect newInstance(final XmlOptions xmlOptions) {
            return (CTRelativeRect)getTypeLoader().newInstance(CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final String s) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(s, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(s, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final File file) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(file, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(file, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final URL url) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(url, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(url, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(inputStream, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(inputStream, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final Reader reader) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(reader, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelativeRect)getTypeLoader().parse(reader, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(xmlStreamReader, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(xmlStreamReader, CTRelativeRect.type, xmlOptions);
        }
        
        public static CTRelativeRect parse(final Node node) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(node, CTRelativeRect.type, (XmlOptions)null);
        }
        
        public static CTRelativeRect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelativeRect)getTypeLoader().parse(node, CTRelativeRect.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRelativeRect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRelativeRect)getTypeLoader().parse(xmlInputStream, CTRelativeRect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRelativeRect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRelativeRect)getTypeLoader().parse(xmlInputStream, CTRelativeRect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelativeRect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelativeRect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
