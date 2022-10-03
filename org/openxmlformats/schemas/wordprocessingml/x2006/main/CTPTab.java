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

public interface CTPTab extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPTab.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctptaba283type");
    
    STPTabAlignment.Enum getAlignment();
    
    STPTabAlignment xgetAlignment();
    
    void setAlignment(final STPTabAlignment.Enum p0);
    
    void xsetAlignment(final STPTabAlignment p0);
    
    STPTabRelativeTo.Enum getRelativeTo();
    
    STPTabRelativeTo xgetRelativeTo();
    
    void setRelativeTo(final STPTabRelativeTo.Enum p0);
    
    void xsetRelativeTo(final STPTabRelativeTo p0);
    
    STPTabLeader.Enum getLeader();
    
    STPTabLeader xgetLeader();
    
    void setLeader(final STPTabLeader.Enum p0);
    
    void xsetLeader(final STPTabLeader p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPTab.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPTab newInstance() {
            return (CTPTab)getTypeLoader().newInstance(CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab newInstance(final XmlOptions xmlOptions) {
            return (CTPTab)getTypeLoader().newInstance(CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final String s) throws XmlException {
            return (CTPTab)getTypeLoader().parse(s, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPTab)getTypeLoader().parse(s, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final File file) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(file, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(file, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final URL url) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(url, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(url, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(inputStream, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(inputStream, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final Reader reader) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(reader, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPTab)getTypeLoader().parse(reader, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPTab)getTypeLoader().parse(xmlStreamReader, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPTab)getTypeLoader().parse(xmlStreamReader, CTPTab.type, xmlOptions);
        }
        
        public static CTPTab parse(final Node node) throws XmlException {
            return (CTPTab)getTypeLoader().parse(node, CTPTab.type, (XmlOptions)null);
        }
        
        public static CTPTab parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPTab)getTypeLoader().parse(node, CTPTab.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPTab parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPTab)getTypeLoader().parse(xmlInputStream, CTPTab.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPTab parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPTab)getTypeLoader().parse(xmlInputStream, CTPTab.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPTab.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPTab.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
