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

public interface CTScene3D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScene3D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscene3d736etype");
    
    CTCamera getCamera();
    
    void setCamera(final CTCamera p0);
    
    CTCamera addNewCamera();
    
    CTLightRig getLightRig();
    
    void setLightRig(final CTLightRig p0);
    
    CTLightRig addNewLightRig();
    
    CTBackdrop getBackdrop();
    
    boolean isSetBackdrop();
    
    void setBackdrop(final CTBackdrop p0);
    
    CTBackdrop addNewBackdrop();
    
    void unsetBackdrop();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScene3D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScene3D newInstance() {
            return (CTScene3D)getTypeLoader().newInstance(CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D newInstance(final XmlOptions xmlOptions) {
            return (CTScene3D)getTypeLoader().newInstance(CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final String s) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(s, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(s, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final File file) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(file, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(file, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final URL url) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(url, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(url, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(inputStream, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(inputStream, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final Reader reader) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(reader, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScene3D)getTypeLoader().parse(reader, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(xmlStreamReader, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(xmlStreamReader, CTScene3D.type, xmlOptions);
        }
        
        public static CTScene3D parse(final Node node) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(node, CTScene3D.type, (XmlOptions)null);
        }
        
        public static CTScene3D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScene3D)getTypeLoader().parse(node, CTScene3D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScene3D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScene3D)getTypeLoader().parse(xmlInputStream, CTScene3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScene3D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScene3D)getTypeLoader().parse(xmlInputStream, CTScene3D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScene3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScene3D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
