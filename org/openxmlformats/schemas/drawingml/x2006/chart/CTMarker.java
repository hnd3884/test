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
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTMarker extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMarker.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmarkera682type");
    
    CTMarkerStyle getSymbol();
    
    boolean isSetSymbol();
    
    void setSymbol(final CTMarkerStyle p0);
    
    CTMarkerStyle addNewSymbol();
    
    void unsetSymbol();
    
    CTMarkerSize getSize();
    
    boolean isSetSize();
    
    void setSize(final CTMarkerSize p0);
    
    CTMarkerSize addNewSize();
    
    void unsetSize();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMarker.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMarker newInstance() {
            return (CTMarker)getTypeLoader().newInstance(CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker newInstance(final XmlOptions xmlOptions) {
            return (CTMarker)getTypeLoader().newInstance(CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final String s) throws XmlException {
            return (CTMarker)getTypeLoader().parse(s, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarker)getTypeLoader().parse(s, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final File file) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(file, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(file, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final URL url) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(url, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(url, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(inputStream, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(inputStream, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final Reader reader) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(reader, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarker)getTypeLoader().parse(reader, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMarker)getTypeLoader().parse(xmlStreamReader, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarker)getTypeLoader().parse(xmlStreamReader, CTMarker.type, xmlOptions);
        }
        
        public static CTMarker parse(final Node node) throws XmlException {
            return (CTMarker)getTypeLoader().parse(node, CTMarker.type, (XmlOptions)null);
        }
        
        public static CTMarker parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarker)getTypeLoader().parse(node, CTMarker.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMarker parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMarker)getTypeLoader().parse(xmlInputStream, CTMarker.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMarker parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMarker)getTypeLoader().parse(xmlInputStream, CTMarker.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarker.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarker.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
