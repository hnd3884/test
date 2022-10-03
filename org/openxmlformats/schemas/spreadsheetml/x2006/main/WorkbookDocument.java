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

public interface WorkbookDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(WorkbookDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("workbookec17doctype");
    
    CTWorkbook getWorkbook();
    
    void setWorkbook(final CTWorkbook p0);
    
    CTWorkbook addNewWorkbook();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(WorkbookDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static WorkbookDocument newInstance() {
            return (WorkbookDocument)getTypeLoader().newInstance(WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument newInstance(final XmlOptions xmlOptions) {
            return (WorkbookDocument)getTypeLoader().newInstance(WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final String s) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(s, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(s, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final File file) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(file, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(file, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final URL url) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(url, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(url, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(inputStream, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(inputStream, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final Reader reader) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(reader, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (WorkbookDocument)getTypeLoader().parse(reader, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(xmlStreamReader, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(xmlStreamReader, WorkbookDocument.type, xmlOptions);
        }
        
        public static WorkbookDocument parse(final Node node) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(node, WorkbookDocument.type, (XmlOptions)null);
        }
        
        public static WorkbookDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (WorkbookDocument)getTypeLoader().parse(node, WorkbookDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static WorkbookDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (WorkbookDocument)getTypeLoader().parse(xmlInputStream, WorkbookDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static WorkbookDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (WorkbookDocument)getTypeLoader().parse(xmlInputStream, WorkbookDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, WorkbookDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, WorkbookDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
