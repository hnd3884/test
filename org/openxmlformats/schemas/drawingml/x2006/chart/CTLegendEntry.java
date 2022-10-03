package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLegendEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLegendEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlegendentrya8e1type");
    
    CTUnsignedInt getIdx();
    
    void setIdx(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewIdx();
    
    CTBoolean getDelete();
    
    boolean isSetDelete();
    
    void setDelete(final CTBoolean p0);
    
    CTBoolean addNewDelete();
    
    void unsetDelete();
    
    CTTextBody getTxPr();
    
    boolean isSetTxPr();
    
    void setTxPr(final CTTextBody p0);
    
    CTTextBody addNewTxPr();
    
    void unsetTxPr();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLegendEntry.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLegendEntry newInstance() {
            return (CTLegendEntry)getTypeLoader().newInstance(CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry newInstance(final XmlOptions xmlOptions) {
            return (CTLegendEntry)getTypeLoader().newInstance(CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final String s) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(s, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(s, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final File file) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(file, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(file, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final URL url) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(url, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(url, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(inputStream, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(inputStream, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final Reader reader) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(reader, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendEntry)getTypeLoader().parse(reader, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(xmlStreamReader, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(xmlStreamReader, CTLegendEntry.type, xmlOptions);
        }
        
        public static CTLegendEntry parse(final Node node) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(node, CTLegendEntry.type, (XmlOptions)null);
        }
        
        public static CTLegendEntry parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendEntry)getTypeLoader().parse(node, CTLegendEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLegendEntry parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLegendEntry)getTypeLoader().parse(xmlInputStream, CTLegendEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLegendEntry parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLegendEntry)getTypeLoader().parse(xmlInputStream, CTLegendEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegendEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegendEntry.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
