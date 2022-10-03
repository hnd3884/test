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

public interface CTPPr extends CTPPrBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctppr01c0type");
    
    CTParaRPr getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTParaRPr p0);
    
    CTParaRPr addNewRPr();
    
    void unsetRPr();
    
    CTSectPr getSectPr();
    
    boolean isSetSectPr();
    
    void setSectPr(final CTSectPr p0);
    
    CTSectPr addNewSectPr();
    
    void unsetSectPr();
    
    CTPPrChange getPPrChange();
    
    boolean isSetPPrChange();
    
    void setPPrChange(final CTPPrChange p0);
    
    CTPPrChange addNewPPrChange();
    
    void unsetPPrChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPPr newInstance() {
            return (CTPPr)getTypeLoader().newInstance(CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr newInstance(final XmlOptions xmlOptions) {
            return (CTPPr)getTypeLoader().newInstance(CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final String s) throws XmlException {
            return (CTPPr)getTypeLoader().parse(s, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPr)getTypeLoader().parse(s, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final File file) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(file, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(file, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final URL url) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(url, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(url, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(inputStream, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(inputStream, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final Reader reader) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(reader, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPr)getTypeLoader().parse(reader, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPPr)getTypeLoader().parse(xmlStreamReader, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPr)getTypeLoader().parse(xmlStreamReader, CTPPr.type, xmlOptions);
        }
        
        public static CTPPr parse(final Node node) throws XmlException {
            return (CTPPr)getTypeLoader().parse(node, CTPPr.type, (XmlOptions)null);
        }
        
        public static CTPPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPr)getTypeLoader().parse(node, CTPPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPPr)getTypeLoader().parse(xmlInputStream, CTPPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPPr)getTypeLoader().parse(xmlInputStream, CTPPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
