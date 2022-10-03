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

public interface CTManualLayout extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTManualLayout.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmanuallayout872ctype");
    
    CTLayoutTarget getLayoutTarget();
    
    boolean isSetLayoutTarget();
    
    void setLayoutTarget(final CTLayoutTarget p0);
    
    CTLayoutTarget addNewLayoutTarget();
    
    void unsetLayoutTarget();
    
    CTLayoutMode getXMode();
    
    boolean isSetXMode();
    
    void setXMode(final CTLayoutMode p0);
    
    CTLayoutMode addNewXMode();
    
    void unsetXMode();
    
    CTLayoutMode getYMode();
    
    boolean isSetYMode();
    
    void setYMode(final CTLayoutMode p0);
    
    CTLayoutMode addNewYMode();
    
    void unsetYMode();
    
    CTLayoutMode getWMode();
    
    boolean isSetWMode();
    
    void setWMode(final CTLayoutMode p0);
    
    CTLayoutMode addNewWMode();
    
    void unsetWMode();
    
    CTLayoutMode getHMode();
    
    boolean isSetHMode();
    
    void setHMode(final CTLayoutMode p0);
    
    CTLayoutMode addNewHMode();
    
    void unsetHMode();
    
    CTDouble getX();
    
    boolean isSetX();
    
    void setX(final CTDouble p0);
    
    CTDouble addNewX();
    
    void unsetX();
    
    CTDouble getY();
    
    boolean isSetY();
    
    void setY(final CTDouble p0);
    
    CTDouble addNewY();
    
    void unsetY();
    
    CTDouble getW();
    
    boolean isSetW();
    
    void setW(final CTDouble p0);
    
    CTDouble addNewW();
    
    void unsetW();
    
    CTDouble getH();
    
    boolean isSetH();
    
    void setH(final CTDouble p0);
    
    CTDouble addNewH();
    
    void unsetH();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTManualLayout.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTManualLayout newInstance() {
            return (CTManualLayout)getTypeLoader().newInstance(CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout newInstance(final XmlOptions xmlOptions) {
            return (CTManualLayout)getTypeLoader().newInstance(CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final String s) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(s, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(s, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final File file) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(file, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(file, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final URL url) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(url, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(url, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(inputStream, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(inputStream, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final Reader reader) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(reader, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTManualLayout)getTypeLoader().parse(reader, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(xmlStreamReader, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(xmlStreamReader, CTManualLayout.type, xmlOptions);
        }
        
        public static CTManualLayout parse(final Node node) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(node, CTManualLayout.type, (XmlOptions)null);
        }
        
        public static CTManualLayout parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTManualLayout)getTypeLoader().parse(node, CTManualLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static CTManualLayout parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTManualLayout)getTypeLoader().parse(xmlInputStream, CTManualLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTManualLayout parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTManualLayout)getTypeLoader().parse(xmlInputStream, CTManualLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTManualLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTManualLayout.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
