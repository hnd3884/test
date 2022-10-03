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

public interface CalcChainDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CalcChainDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("calcchainfc37doctype");
    
    CTCalcChain getCalcChain();
    
    void setCalcChain(final CTCalcChain p0);
    
    CTCalcChain addNewCalcChain();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CalcChainDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CalcChainDocument newInstance() {
            return (CalcChainDocument)getTypeLoader().newInstance(CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument newInstance(final XmlOptions xmlOptions) {
            return (CalcChainDocument)getTypeLoader().newInstance(CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final String s) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(s, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(s, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final File file) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(file, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(file, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final URL url) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(url, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(url, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(inputStream, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(inputStream, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final Reader reader) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(reader, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CalcChainDocument)getTypeLoader().parse(reader, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(xmlStreamReader, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(xmlStreamReader, CalcChainDocument.type, xmlOptions);
        }
        
        public static CalcChainDocument parse(final Node node) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(node, CalcChainDocument.type, (XmlOptions)null);
        }
        
        public static CalcChainDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CalcChainDocument)getTypeLoader().parse(node, CalcChainDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static CalcChainDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CalcChainDocument)getTypeLoader().parse(xmlInputStream, CalcChainDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CalcChainDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CalcChainDocument)getTypeLoader().parse(xmlInputStream, CalcChainDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CalcChainDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CalcChainDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
