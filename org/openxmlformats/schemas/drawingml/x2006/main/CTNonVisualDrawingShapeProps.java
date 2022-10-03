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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNonVisualDrawingShapeProps extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualDrawingShapeProps.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualdrawingshapepropsf17btype");
    
    CTShapeLocking getSpLocks();
    
    boolean isSetSpLocks();
    
    void setSpLocks(final CTShapeLocking p0);
    
    CTShapeLocking addNewSpLocks();
    
    void unsetSpLocks();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getTxBox();
    
    XmlBoolean xgetTxBox();
    
    boolean isSetTxBox();
    
    void setTxBox(final boolean p0);
    
    void xsetTxBox(final XmlBoolean p0);
    
    void unsetTxBox();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualDrawingShapeProps.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualDrawingShapeProps newInstance() {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().newInstance(CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().newInstance(CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final String s) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(s, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(s, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final File file) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(file, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(file, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(url, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(url, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(inputStream, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(inputStream, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(reader, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(reader, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final Node node) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(node, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingShapeProps parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(node, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualDrawingShapeProps parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(xmlInputStream, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualDrawingShapeProps parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualDrawingShapeProps)getTypeLoader().parse(xmlInputStream, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualDrawingShapeProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualDrawingShapeProps.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
