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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPath2DList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath2DList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath2dlistb010type");
    
    List<CTPath2D> getPathList();
    
    @Deprecated
    CTPath2D[] getPathArray();
    
    CTPath2D getPathArray(final int p0);
    
    int sizeOfPathArray();
    
    void setPathArray(final CTPath2D[] p0);
    
    void setPathArray(final int p0, final CTPath2D p1);
    
    CTPath2D insertNewPath(final int p0);
    
    CTPath2D addNewPath();
    
    void removePath(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath2DList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath2DList newInstance() {
            return (CTPath2DList)getTypeLoader().newInstance(CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList newInstance(final XmlOptions xmlOptions) {
            return (CTPath2DList)getTypeLoader().newInstance(CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final String s) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(s, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(s, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final File file) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(file, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(file, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final URL url) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(url, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(url, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(inputStream, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(inputStream, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final Reader reader) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(reader, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DList)getTypeLoader().parse(reader, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(xmlStreamReader, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(xmlStreamReader, CTPath2DList.type, xmlOptions);
        }
        
        public static CTPath2DList parse(final Node node) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(node, CTPath2DList.type, (XmlOptions)null);
        }
        
        public static CTPath2DList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DList)getTypeLoader().parse(node, CTPath2DList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath2DList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath2DList)getTypeLoader().parse(xmlInputStream, CTPath2DList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath2DList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath2DList)getTypeLoader().parse(xmlInputStream, CTPath2DList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
