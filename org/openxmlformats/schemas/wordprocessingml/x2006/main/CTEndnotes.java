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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEndnotes extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEndnotes.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctendnotescee2type");
    
    List<CTFtnEdn> getEndnoteList();
    
    @Deprecated
    CTFtnEdn[] getEndnoteArray();
    
    CTFtnEdn getEndnoteArray(final int p0);
    
    int sizeOfEndnoteArray();
    
    void setEndnoteArray(final CTFtnEdn[] p0);
    
    void setEndnoteArray(final int p0, final CTFtnEdn p1);
    
    CTFtnEdn insertNewEndnote(final int p0);
    
    CTFtnEdn addNewEndnote();
    
    void removeEndnote(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEndnotes.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEndnotes newInstance() {
            return (CTEndnotes)getTypeLoader().newInstance(CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes newInstance(final XmlOptions xmlOptions) {
            return (CTEndnotes)getTypeLoader().newInstance(CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final String s) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(s, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(s, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final File file) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(file, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(file, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final URL url) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(url, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(url, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(inputStream, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(inputStream, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final Reader reader) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(reader, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEndnotes)getTypeLoader().parse(reader, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(xmlStreamReader, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(xmlStreamReader, CTEndnotes.type, xmlOptions);
        }
        
        public static CTEndnotes parse(final Node node) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(node, CTEndnotes.type, (XmlOptions)null);
        }
        
        public static CTEndnotes parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEndnotes)getTypeLoader().parse(node, CTEndnotes.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEndnotes parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEndnotes)getTypeLoader().parse(xmlInputStream, CTEndnotes.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEndnotes parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEndnotes)getTypeLoader().parse(xmlInputStream, CTEndnotes.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEndnotes.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEndnotes.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
