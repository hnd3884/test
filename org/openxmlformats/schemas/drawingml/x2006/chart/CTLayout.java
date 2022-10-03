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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLayout extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLayout.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlayout3192type");
    
    CTManualLayout getManualLayout();
    
    boolean isSetManualLayout();
    
    void setManualLayout(final CTManualLayout p0);
    
    CTManualLayout addNewManualLayout();
    
    void unsetManualLayout();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLayout.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLayout newInstance() {
            return (CTLayout)getTypeLoader().newInstance(CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout newInstance(final XmlOptions xmlOptions) {
            return (CTLayout)getTypeLoader().newInstance(CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final String s) throws XmlException {
            return (CTLayout)getTypeLoader().parse(s, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayout)getTypeLoader().parse(s, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final File file) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(file, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(file, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final URL url) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(url, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(url, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(inputStream, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(inputStream, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final Reader reader) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(reader, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayout)getTypeLoader().parse(reader, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLayout)getTypeLoader().parse(xmlStreamReader, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayout)getTypeLoader().parse(xmlStreamReader, CTLayout.type, xmlOptions);
        }
        
        public static CTLayout parse(final Node node) throws XmlException {
            return (CTLayout)getTypeLoader().parse(node, CTLayout.type, (XmlOptions)null);
        }
        
        public static CTLayout parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayout)getTypeLoader().parse(node, CTLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLayout parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLayout)getTypeLoader().parse(xmlInputStream, CTLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLayout parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLayout)getTypeLoader().parse(xmlInputStream, CTLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayout.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
