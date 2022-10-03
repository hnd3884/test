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
import org.apache.xmlbeans.XmlAnySimpleType;

public interface STAdjCoordinate extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAdjCoordinate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stadjcoordinated920type");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAdjCoordinate newValue(final Object o) {
            return (STAdjCoordinate)STAdjCoordinate.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAdjCoordinate.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAdjCoordinate newInstance() {
            return (STAdjCoordinate)getTypeLoader().newInstance(STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate newInstance(final XmlOptions xmlOptions) {
            return (STAdjCoordinate)getTypeLoader().newInstance(STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final String s) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(s, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(s, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final File file) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(file, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(file, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final URL url) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(url, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(url, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(inputStream, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(inputStream, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final Reader reader) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(reader, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAdjCoordinate)getTypeLoader().parse(reader, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(xmlStreamReader, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(xmlStreamReader, STAdjCoordinate.type, xmlOptions);
        }
        
        public static STAdjCoordinate parse(final Node node) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(node, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        public static STAdjCoordinate parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAdjCoordinate)getTypeLoader().parse(node, STAdjCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static STAdjCoordinate parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAdjCoordinate)getTypeLoader().parse(xmlInputStream, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAdjCoordinate parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAdjCoordinate)getTypeLoader().parse(xmlInputStream, STAdjCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAdjCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAdjCoordinate.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
