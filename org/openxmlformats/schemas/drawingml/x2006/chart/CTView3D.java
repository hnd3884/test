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

public interface CTView3D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTView3D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctview3daf66type");
    
    CTRotX getRotX();
    
    boolean isSetRotX();
    
    void setRotX(final CTRotX p0);
    
    CTRotX addNewRotX();
    
    void unsetRotX();
    
    CTHPercent getHPercent();
    
    boolean isSetHPercent();
    
    void setHPercent(final CTHPercent p0);
    
    CTHPercent addNewHPercent();
    
    void unsetHPercent();
    
    CTRotY getRotY();
    
    boolean isSetRotY();
    
    void setRotY(final CTRotY p0);
    
    CTRotY addNewRotY();
    
    void unsetRotY();
    
    CTDepthPercent getDepthPercent();
    
    boolean isSetDepthPercent();
    
    void setDepthPercent(final CTDepthPercent p0);
    
    CTDepthPercent addNewDepthPercent();
    
    void unsetDepthPercent();
    
    CTBoolean getRAngAx();
    
    boolean isSetRAngAx();
    
    void setRAngAx(final CTBoolean p0);
    
    CTBoolean addNewRAngAx();
    
    void unsetRAngAx();
    
    CTPerspective getPerspective();
    
    boolean isSetPerspective();
    
    void setPerspective(final CTPerspective p0);
    
    CTPerspective addNewPerspective();
    
    void unsetPerspective();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTView3D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTView3D newInstance() {
            return (CTView3D)getTypeLoader().newInstance(CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D newInstance(final XmlOptions xmlOptions) {
            return (CTView3D)getTypeLoader().newInstance(CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final String s) throws XmlException {
            return (CTView3D)getTypeLoader().parse(s, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTView3D)getTypeLoader().parse(s, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final File file) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(file, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(file, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final URL url) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(url, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(url, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(inputStream, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(inputStream, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final Reader reader) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(reader, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTView3D)getTypeLoader().parse(reader, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTView3D)getTypeLoader().parse(xmlStreamReader, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTView3D)getTypeLoader().parse(xmlStreamReader, CTView3D.type, xmlOptions);
        }
        
        public static CTView3D parse(final Node node) throws XmlException {
            return (CTView3D)getTypeLoader().parse(node, CTView3D.type, (XmlOptions)null);
        }
        
        public static CTView3D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTView3D)getTypeLoader().parse(node, CTView3D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTView3D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTView3D)getTypeLoader().parse(xmlInputStream, CTView3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTView3D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTView3D)getTypeLoader().parse(xmlInputStream, CTView3D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTView3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTView3D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
