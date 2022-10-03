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

public interface CTErrBars extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTErrBars.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cterrbarsa201type");
    
    CTErrDir getErrDir();
    
    boolean isSetErrDir();
    
    void setErrDir(final CTErrDir p0);
    
    CTErrDir addNewErrDir();
    
    void unsetErrDir();
    
    CTErrBarType getErrBarType();
    
    void setErrBarType(final CTErrBarType p0);
    
    CTErrBarType addNewErrBarType();
    
    CTErrValType getErrValType();
    
    void setErrValType(final CTErrValType p0);
    
    CTErrValType addNewErrValType();
    
    CTBoolean getNoEndCap();
    
    boolean isSetNoEndCap();
    
    void setNoEndCap(final CTBoolean p0);
    
    CTBoolean addNewNoEndCap();
    
    void unsetNoEndCap();
    
    CTNumDataSource getPlus();
    
    boolean isSetPlus();
    
    void setPlus(final CTNumDataSource p0);
    
    CTNumDataSource addNewPlus();
    
    void unsetPlus();
    
    CTNumDataSource getMinus();
    
    boolean isSetMinus();
    
    void setMinus(final CTNumDataSource p0);
    
    CTNumDataSource addNewMinus();
    
    void unsetMinus();
    
    CTDouble getVal();
    
    boolean isSetVal();
    
    void setVal(final CTDouble p0);
    
    CTDouble addNewVal();
    
    void unsetVal();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTErrBars.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTErrBars newInstance() {
            return (CTErrBars)getTypeLoader().newInstance(CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars newInstance(final XmlOptions xmlOptions) {
            return (CTErrBars)getTypeLoader().newInstance(CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final String s) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(s, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(s, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final File file) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(file, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(file, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final URL url) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(url, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(url, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(inputStream, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(inputStream, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final Reader reader) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(reader, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTErrBars)getTypeLoader().parse(reader, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(xmlStreamReader, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(xmlStreamReader, CTErrBars.type, xmlOptions);
        }
        
        public static CTErrBars parse(final Node node) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(node, CTErrBars.type, (XmlOptions)null);
        }
        
        public static CTErrBars parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTErrBars)getTypeLoader().parse(node, CTErrBars.type, xmlOptions);
        }
        
        @Deprecated
        public static CTErrBars parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTErrBars)getTypeLoader().parse(xmlInputStream, CTErrBars.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTErrBars parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTErrBars)getTypeLoader().parse(xmlInputStream, CTErrBars.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTErrBars.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTErrBars.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
