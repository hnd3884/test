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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTOleObjects extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOleObjects.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctoleobjects1455type");
    
    List<CTOleObject> getOleObjectList();
    
    @Deprecated
    CTOleObject[] getOleObjectArray();
    
    CTOleObject getOleObjectArray(final int p0);
    
    int sizeOfOleObjectArray();
    
    void setOleObjectArray(final CTOleObject[] p0);
    
    void setOleObjectArray(final int p0, final CTOleObject p1);
    
    CTOleObject insertNewOleObject(final int p0);
    
    CTOleObject addNewOleObject();
    
    void removeOleObject(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOleObjects.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOleObjects newInstance() {
            return (CTOleObjects)getTypeLoader().newInstance(CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects newInstance(final XmlOptions xmlOptions) {
            return (CTOleObjects)getTypeLoader().newInstance(CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final String s) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(s, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(s, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final File file) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(file, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(file, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final URL url) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(url, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(url, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(inputStream, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(inputStream, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final Reader reader) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(reader, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObjects)getTypeLoader().parse(reader, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(xmlStreamReader, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(xmlStreamReader, CTOleObjects.type, xmlOptions);
        }
        
        public static CTOleObjects parse(final Node node) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(node, CTOleObjects.type, (XmlOptions)null);
        }
        
        public static CTOleObjects parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObjects)getTypeLoader().parse(node, CTOleObjects.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOleObjects parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOleObjects)getTypeLoader().parse(xmlInputStream, CTOleObjects.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOleObjects parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOleObjects)getTypeLoader().parse(xmlInputStream, CTOleObjects.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOleObjects.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOleObjects.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
