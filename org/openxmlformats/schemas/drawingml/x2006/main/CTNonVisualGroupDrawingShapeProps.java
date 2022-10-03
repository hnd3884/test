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

public interface CTNonVisualGroupDrawingShapeProps extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualGroupDrawingShapeProps.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualgroupdrawingshapeprops610ctype");
    
    CTGroupLocking getGrpSpLocks();
    
    boolean isSetGrpSpLocks();
    
    void setGrpSpLocks(final CTGroupLocking p0);
    
    CTGroupLocking addNewGrpSpLocks();
    
    void unsetGrpSpLocks();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualGroupDrawingShapeProps.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualGroupDrawingShapeProps newInstance() {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().newInstance(CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().newInstance(CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final String s) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(s, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(s, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final File file) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(file, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(file, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(url, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(url, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(inputStream, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(inputStream, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(reader, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(reader, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final Node node) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(node, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualGroupDrawingShapeProps parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(node, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualGroupDrawingShapeProps parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(xmlInputStream, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualGroupDrawingShapeProps parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualGroupDrawingShapeProps)getTypeLoader().parse(xmlInputStream, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualGroupDrawingShapeProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualGroupDrawingShapeProps.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
