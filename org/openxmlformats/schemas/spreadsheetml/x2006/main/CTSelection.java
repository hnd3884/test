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
import java.util.List;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSelection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSelection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctselectionca2btype");
    
    STPane.Enum getPane();
    
    STPane xgetPane();
    
    boolean isSetPane();
    
    void setPane(final STPane.Enum p0);
    
    void xsetPane(final STPane p0);
    
    void unsetPane();
    
    String getActiveCell();
    
    STCellRef xgetActiveCell();
    
    boolean isSetActiveCell();
    
    void setActiveCell(final String p0);
    
    void xsetActiveCell(final STCellRef p0);
    
    void unsetActiveCell();
    
    long getActiveCellId();
    
    XmlUnsignedInt xgetActiveCellId();
    
    boolean isSetActiveCellId();
    
    void setActiveCellId(final long p0);
    
    void xsetActiveCellId(final XmlUnsignedInt p0);
    
    void unsetActiveCellId();
    
    List getSqref();
    
    STSqref xgetSqref();
    
    boolean isSetSqref();
    
    void setSqref(final List p0);
    
    void xsetSqref(final STSqref p0);
    
    void unsetSqref();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSelection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSelection newInstance() {
            return (CTSelection)getTypeLoader().newInstance(CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection newInstance(final XmlOptions xmlOptions) {
            return (CTSelection)getTypeLoader().newInstance(CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final String s) throws XmlException {
            return (CTSelection)getTypeLoader().parse(s, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSelection)getTypeLoader().parse(s, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final File file) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(file, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(file, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final URL url) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(url, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(url, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(inputStream, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(inputStream, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final Reader reader) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(reader, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSelection)getTypeLoader().parse(reader, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSelection)getTypeLoader().parse(xmlStreamReader, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSelection)getTypeLoader().parse(xmlStreamReader, CTSelection.type, xmlOptions);
        }
        
        public static CTSelection parse(final Node node) throws XmlException {
            return (CTSelection)getTypeLoader().parse(node, CTSelection.type, (XmlOptions)null);
        }
        
        public static CTSelection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSelection)getTypeLoader().parse(node, CTSelection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSelection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSelection)getTypeLoader().parse(xmlInputStream, CTSelection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSelection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSelection)getTypeLoader().parse(xmlInputStream, CTSelection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSelection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSelection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
