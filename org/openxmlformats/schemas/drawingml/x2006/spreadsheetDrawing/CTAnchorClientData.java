package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAnchorClientData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAnchorClientData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctanchorclientdata02betype");
    
    boolean getFLocksWithSheet();
    
    XmlBoolean xgetFLocksWithSheet();
    
    boolean isSetFLocksWithSheet();
    
    void setFLocksWithSheet(final boolean p0);
    
    void xsetFLocksWithSheet(final XmlBoolean p0);
    
    void unsetFLocksWithSheet();
    
    boolean getFPrintsWithSheet();
    
    XmlBoolean xgetFPrintsWithSheet();
    
    boolean isSetFPrintsWithSheet();
    
    void setFPrintsWithSheet(final boolean p0);
    
    void xsetFPrintsWithSheet(final XmlBoolean p0);
    
    void unsetFPrintsWithSheet();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAnchorClientData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAnchorClientData newInstance() {
            return (CTAnchorClientData)getTypeLoader().newInstance(CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData newInstance(final XmlOptions xmlOptions) {
            return (CTAnchorClientData)getTypeLoader().newInstance(CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final String s) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(s, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(s, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final File file) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(file, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(file, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final URL url) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(url, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(url, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(inputStream, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(inputStream, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final Reader reader) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(reader, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchorClientData)getTypeLoader().parse(reader, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(xmlStreamReader, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(xmlStreamReader, CTAnchorClientData.type, xmlOptions);
        }
        
        public static CTAnchorClientData parse(final Node node) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(node, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        public static CTAnchorClientData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchorClientData)getTypeLoader().parse(node, CTAnchorClientData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAnchorClientData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAnchorClientData)getTypeLoader().parse(xmlInputStream, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAnchorClientData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAnchorClientData)getTypeLoader().parse(xmlInputStream, CTAnchorClientData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAnchorClientData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAnchorClientData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
