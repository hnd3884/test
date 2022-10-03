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
import org.apache.xmlbeans.XmlObject;

public interface TblStyleLstDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TblStyleLstDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("tblstylelst4997doctype");
    
    CTTableStyleList getTblStyleLst();
    
    void setTblStyleLst(final CTTableStyleList p0);
    
    CTTableStyleList addNewTblStyleLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(TblStyleLstDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static TblStyleLstDocument newInstance() {
            return (TblStyleLstDocument)getTypeLoader().newInstance(TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument newInstance(final XmlOptions xmlOptions) {
            return (TblStyleLstDocument)getTypeLoader().newInstance(TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final String s) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(s, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(s, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final File file) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(file, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(file, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final URL url) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(url, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(url, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(inputStream, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(inputStream, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final Reader reader) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(reader, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TblStyleLstDocument)getTypeLoader().parse(reader, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(xmlStreamReader, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(xmlStreamReader, TblStyleLstDocument.type, xmlOptions);
        }
        
        public static TblStyleLstDocument parse(final Node node) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(node, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        public static TblStyleLstDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (TblStyleLstDocument)getTypeLoader().parse(node, TblStyleLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static TblStyleLstDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (TblStyleLstDocument)getTypeLoader().parse(xmlInputStream, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static TblStyleLstDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (TblStyleLstDocument)getTypeLoader().parse(xmlInputStream, TblStyleLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TblStyleLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TblStyleLstDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
