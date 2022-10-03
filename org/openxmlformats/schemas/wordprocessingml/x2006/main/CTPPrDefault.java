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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPPrDefault extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPPrDefault.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpprdefaultf839type");
    
    CTPPr getPPr();
    
    boolean isSetPPr();
    
    void setPPr(final CTPPr p0);
    
    CTPPr addNewPPr();
    
    void unsetPPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPPrDefault.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPPrDefault newInstance() {
            return (CTPPrDefault)getTypeLoader().newInstance(CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault newInstance(final XmlOptions xmlOptions) {
            return (CTPPrDefault)getTypeLoader().newInstance(CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final String s) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(s, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(s, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final File file) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(file, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(file, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final URL url) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(url, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(url, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(inputStream, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(inputStream, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final Reader reader) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(reader, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrDefault)getTypeLoader().parse(reader, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(xmlStreamReader, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(xmlStreamReader, CTPPrDefault.type, xmlOptions);
        }
        
        public static CTPPrDefault parse(final Node node) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(node, CTPPrDefault.type, (XmlOptions)null);
        }
        
        public static CTPPrDefault parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrDefault)getTypeLoader().parse(node, CTPPrDefault.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPPrDefault parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPPrDefault)getTypeLoader().parse(xmlInputStream, CTPPrDefault.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPPrDefault parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPPrDefault)getTypeLoader().parse(xmlInputStream, CTPPrDefault.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPrDefault.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPrDefault.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
