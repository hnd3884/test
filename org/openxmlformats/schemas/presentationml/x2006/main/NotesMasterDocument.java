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

public interface NotesMasterDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NotesMasterDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("notesmaster8840doctype");
    
    CTNotesMaster getNotesMaster();
    
    void setNotesMaster(final CTNotesMaster p0);
    
    CTNotesMaster addNewNotesMaster();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(NotesMasterDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static NotesMasterDocument newInstance() {
            return (NotesMasterDocument)getTypeLoader().newInstance(NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument newInstance(final XmlOptions xmlOptions) {
            return (NotesMasterDocument)getTypeLoader().newInstance(NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final String s) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(s, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(s, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final File file) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(file, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(file, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final URL url) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(url, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(url, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(inputStream, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(inputStream, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final Reader reader) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(reader, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NotesMasterDocument)getTypeLoader().parse(reader, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(xmlStreamReader, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(xmlStreamReader, NotesMasterDocument.type, xmlOptions);
        }
        
        public static NotesMasterDocument parse(final Node node) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(node, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        public static NotesMasterDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (NotesMasterDocument)getTypeLoader().parse(node, NotesMasterDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static NotesMasterDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (NotesMasterDocument)getTypeLoader().parse(xmlInputStream, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static NotesMasterDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (NotesMasterDocument)getTypeLoader().parse(xmlInputStream, NotesMasterDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NotesMasterDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NotesMasterDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
