package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import java.util.Calendar;
import org.apache.xmlbeans.SchemaType;

public interface CTTrackChange extends CTMarkup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTrackChange.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttrackchangec317type");
    
    String getAuthor();
    
    STString xgetAuthor();
    
    void setAuthor(final String p0);
    
    void xsetAuthor(final STString p0);
    
    Calendar getDate();
    
    STDateTime xgetDate();
    
    boolean isSetDate();
    
    void setDate(final Calendar p0);
    
    void xsetDate(final STDateTime p0);
    
    void unsetDate();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTrackChange.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTrackChange newInstance() {
            return (CTTrackChange)getTypeLoader().newInstance(CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange newInstance(final XmlOptions xmlOptions) {
            return (CTTrackChange)getTypeLoader().newInstance(CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final String s) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(s, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(s, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final File file) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(file, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(file, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final URL url) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(url, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(url, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(inputStream, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(inputStream, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final Reader reader) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(reader, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrackChange)getTypeLoader().parse(reader, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(xmlStreamReader, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(xmlStreamReader, CTTrackChange.type, xmlOptions);
        }
        
        public static CTTrackChange parse(final Node node) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(node, CTTrackChange.type, (XmlOptions)null);
        }
        
        public static CTTrackChange parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrackChange)getTypeLoader().parse(node, CTTrackChange.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTrackChange parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTrackChange)getTypeLoader().parse(xmlInputStream, CTTrackChange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTrackChange parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTrackChange)getTypeLoader().parse(xmlInputStream, CTTrackChange.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrackChange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrackChange.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
