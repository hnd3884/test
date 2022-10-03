package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGraphicalObjectFrame extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGraphicalObjectFrame.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgraphicalobjectframebfeatype");
    
    CTGraphicalObjectFrameNonVisual getNvGraphicFramePr();
    
    void setNvGraphicFramePr(final CTGraphicalObjectFrameNonVisual p0);
    
    CTGraphicalObjectFrameNonVisual addNewNvGraphicFramePr();
    
    CTTransform2D getXfrm();
    
    void setXfrm(final CTTransform2D p0);
    
    CTTransform2D addNewXfrm();
    
    CTGraphicalObject getGraphic();
    
    void setGraphic(final CTGraphicalObject p0);
    
    CTGraphicalObject addNewGraphic();
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGraphicalObjectFrame.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGraphicalObjectFrame newInstance() {
            return (CTGraphicalObjectFrame)getTypeLoader().newInstance(CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame newInstance(final XmlOptions xmlOptions) {
            return (CTGraphicalObjectFrame)getTypeLoader().newInstance(CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final String s) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(s, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(s, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final File file) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(file, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(file, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final URL url) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(url, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(url, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(inputStream, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(inputStream, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final Reader reader) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(reader, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(reader, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrame parse(final Node node) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(node, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrame parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(node, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrame parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrame parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrame)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrame.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrame.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
