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

public interface CTFootnotes extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFootnotes.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfootnotes691ftype");
    
    List<CTFtnEdn> getFootnoteList();
    
    @Deprecated
    CTFtnEdn[] getFootnoteArray();
    
    CTFtnEdn getFootnoteArray(final int p0);
    
    int sizeOfFootnoteArray();
    
    void setFootnoteArray(final CTFtnEdn[] p0);
    
    void setFootnoteArray(final int p0, final CTFtnEdn p1);
    
    CTFtnEdn insertNewFootnote(final int p0);
    
    CTFtnEdn addNewFootnote();
    
    void removeFootnote(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFootnotes.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFootnotes newInstance() {
            return (CTFootnotes)getTypeLoader().newInstance(CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes newInstance(final XmlOptions xmlOptions) {
            return (CTFootnotes)getTypeLoader().newInstance(CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final String s) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(s, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(s, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final File file) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(file, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(file, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final URL url) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(url, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(url, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(inputStream, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(inputStream, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final Reader reader) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(reader, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFootnotes)getTypeLoader().parse(reader, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(xmlStreamReader, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(xmlStreamReader, CTFootnotes.type, xmlOptions);
        }
        
        public static CTFootnotes parse(final Node node) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(node, CTFootnotes.type, (XmlOptions)null);
        }
        
        public static CTFootnotes parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFootnotes)getTypeLoader().parse(node, CTFootnotes.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFootnotes parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFootnotes)getTypeLoader().parse(xmlInputStream, CTFootnotes.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFootnotes parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFootnotes)getTypeLoader().parse(xmlInputStream, CTFootnotes.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFootnotes.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFootnotes.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
