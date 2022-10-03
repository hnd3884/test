package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPane extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPane.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpaneaab1type");
    
    double getXSplit();
    
    XmlDouble xgetXSplit();
    
    boolean isSetXSplit();
    
    void setXSplit(final double p0);
    
    void xsetXSplit(final XmlDouble p0);
    
    void unsetXSplit();
    
    double getYSplit();
    
    XmlDouble xgetYSplit();
    
    boolean isSetYSplit();
    
    void setYSplit(final double p0);
    
    void xsetYSplit(final XmlDouble p0);
    
    void unsetYSplit();
    
    String getTopLeftCell();
    
    STCellRef xgetTopLeftCell();
    
    boolean isSetTopLeftCell();
    
    void setTopLeftCell(final String p0);
    
    void xsetTopLeftCell(final STCellRef p0);
    
    void unsetTopLeftCell();
    
    STPane.Enum getActivePane();
    
    STPane xgetActivePane();
    
    boolean isSetActivePane();
    
    void setActivePane(final STPane.Enum p0);
    
    void xsetActivePane(final STPane p0);
    
    void unsetActivePane();
    
    STPaneState.Enum getState();
    
    STPaneState xgetState();
    
    boolean isSetState();
    
    void setState(final STPaneState.Enum p0);
    
    void xsetState(final STPaneState p0);
    
    void unsetState();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPane.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPane newInstance() {
            return (CTPane)getTypeLoader().newInstance(CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane newInstance(final XmlOptions xmlOptions) {
            return (CTPane)getTypeLoader().newInstance(CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final String s) throws XmlException {
            return (CTPane)getTypeLoader().parse(s, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPane)getTypeLoader().parse(s, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final File file) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(file, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(file, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final URL url) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(url, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(url, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(inputStream, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(inputStream, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final Reader reader) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(reader, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPane)getTypeLoader().parse(reader, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPane)getTypeLoader().parse(xmlStreamReader, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPane)getTypeLoader().parse(xmlStreamReader, CTPane.type, xmlOptions);
        }
        
        public static CTPane parse(final Node node) throws XmlException {
            return (CTPane)getTypeLoader().parse(node, CTPane.type, (XmlOptions)null);
        }
        
        public static CTPane parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPane)getTypeLoader().parse(node, CTPane.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPane parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPane)getTypeLoader().parse(xmlInputStream, CTPane.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPane parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPane)getTypeLoader().parse(xmlInputStream, CTPane.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPane.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPane.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
