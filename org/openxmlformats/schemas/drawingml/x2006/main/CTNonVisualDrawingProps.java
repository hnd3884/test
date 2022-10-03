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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNonVisualDrawingProps extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualDrawingProps.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualdrawingprops8fb0type");
    
    CTHyperlink getHlinkClick();
    
    boolean isSetHlinkClick();
    
    void setHlinkClick(final CTHyperlink p0);
    
    CTHyperlink addNewHlinkClick();
    
    void unsetHlinkClick();
    
    CTHyperlink getHlinkHover();
    
    boolean isSetHlinkHover();
    
    void setHlinkHover(final CTHyperlink p0);
    
    CTHyperlink addNewHlinkHover();
    
    void unsetHlinkHover();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    STDrawingElementId xgetId();
    
    void setId(final long p0);
    
    void xsetId(final STDrawingElementId p0);
    
    String getName();
    
    XmlString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    String getDescr();
    
    XmlString xgetDescr();
    
    boolean isSetDescr();
    
    void setDescr(final String p0);
    
    void xsetDescr(final XmlString p0);
    
    void unsetDescr();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualDrawingProps.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualDrawingProps newInstance() {
            return (CTNonVisualDrawingProps)getTypeLoader().newInstance(CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualDrawingProps)getTypeLoader().newInstance(CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final String s) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(s, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(s, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final File file) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(file, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(file, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(url, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(url, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(inputStream, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(inputStream, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(reader, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(reader, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(xmlStreamReader, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTNonVisualDrawingProps parse(final Node node) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(node, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTNonVisualDrawingProps parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(node, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualDrawingProps parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(xmlInputStream, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualDrawingProps parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualDrawingProps)getTypeLoader().parse(xmlInputStream, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualDrawingProps.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
