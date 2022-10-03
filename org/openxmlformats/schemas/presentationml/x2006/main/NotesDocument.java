package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface NotesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NotesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("notes4a02doctype");
    
    CTNotesSlide getNotes();
    
    void setNotes(final CTNotesSlide p0);
    
    CTNotesSlide addNewNotes();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(NotesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static NotesDocument newInstance() {
            return (NotesDocument)getTypeLoader().newInstance(NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument newInstance(final XmlOptions xmlOptions) {
            return (NotesDocument)getTypeLoader().newInstance(NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final String s) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(s, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(s, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final File file) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(file, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(file, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final URL url) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(url, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(url, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(inputStream, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(inputStream, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final Reader reader) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(reader, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesDocument)getTypeLoader().parse(reader, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(xmlStreamReader, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(xmlStreamReader, NotesDocument.type, xmlOptions);
        }
        
        public static NotesDocument parse(final Node node) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(node, NotesDocument.type, (XmlOptions)null);
        }
        
        public static NotesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (NotesDocument)getTypeLoader().parse(node, NotesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static NotesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (NotesDocument)getTypeLoader().parse(xmlInputStream, NotesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static NotesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (NotesDocument)getTypeLoader().parse(xmlInputStream, NotesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NotesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NotesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
