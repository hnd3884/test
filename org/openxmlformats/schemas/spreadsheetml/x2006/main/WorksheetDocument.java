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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface WorksheetDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(WorksheetDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("worksheetf539doctype");
    
    CTWorksheet getWorksheet();
    
    void setWorksheet(final CTWorksheet p0);
    
    CTWorksheet addNewWorksheet();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(WorksheetDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static WorksheetDocument newInstance() {
            return (WorksheetDocument)getTypeLoader().newInstance(WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument newInstance(final XmlOptions xmlOptions) {
            return (WorksheetDocument)getTypeLoader().newInstance(WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final String s) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(s, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(s, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final File file) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(file, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(file, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final URL url) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(url, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(url, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(inputStream, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(inputStream, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final Reader reader) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(reader, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorksheetDocument)getTypeLoader().parse(reader, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(xmlStreamReader, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(xmlStreamReader, WorksheetDocument.type, xmlOptions);
        }
        
        public static WorksheetDocument parse(final Node node) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(node, WorksheetDocument.type, (XmlOptions)null);
        }
        
        public static WorksheetDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (WorksheetDocument)getTypeLoader().parse(node, WorksheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static WorksheetDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (WorksheetDocument)getTypeLoader().parse(xmlInputStream, WorksheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static WorksheetDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (WorksheetDocument)getTypeLoader().parse(xmlInputStream, WorksheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, WorksheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, WorksheetDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
