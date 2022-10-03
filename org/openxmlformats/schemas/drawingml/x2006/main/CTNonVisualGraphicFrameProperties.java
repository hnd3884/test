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

public interface CTNonVisualGraphicFrameProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualGraphicFrameProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualgraphicframeproperties43b6type");
    
    CTGraphicalObjectFrameLocking getGraphicFrameLocks();
    
    boolean isSetGraphicFrameLocks();
    
    void setGraphicFrameLocks(final CTGraphicalObjectFrameLocking p0);
    
    CTGraphicalObjectFrameLocking addNewGraphicFrameLocks();
    
    void unsetGraphicFrameLocks();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualGraphicFrameProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualGraphicFrameProperties newInstance() {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().newInstance(CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().newInstance(CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final String s) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(s, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(s, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final File file) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(file, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(file, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(url, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(url, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(inputStream, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(inputStream, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(reader, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(reader, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final Node node) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(node, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGraphicFrameProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(node, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualGraphicFrameProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualGraphicFrameProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualGraphicFrameProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualGraphicFrameProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualGraphicFrameProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
