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

public interface SingleXmlCellsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SingleXmlCellsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("singlexmlcells33bfdoctype");
    
    CTSingleXmlCells getSingleXmlCells();
    
    void setSingleXmlCells(final CTSingleXmlCells p0);
    
    CTSingleXmlCells addNewSingleXmlCells();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SingleXmlCellsDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SingleXmlCellsDocument newInstance() {
            return (SingleXmlCellsDocument)getTypeLoader().newInstance(SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument newInstance(final XmlOptions xmlOptions) {
            return (SingleXmlCellsDocument)getTypeLoader().newInstance(SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final String s) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(s, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(s, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final File file) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(file, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(file, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final URL url) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(url, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(url, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(inputStream, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(inputStream, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final Reader reader) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(reader, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(reader, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(xmlStreamReader, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(xmlStreamReader, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        public static SingleXmlCellsDocument parse(final Node node) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(node, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        public static SingleXmlCellsDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(node, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SingleXmlCellsDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(xmlInputStream, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SingleXmlCellsDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SingleXmlCellsDocument)getTypeLoader().parse(xmlInputStream, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SingleXmlCellsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SingleXmlCellsDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
