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

public interface CTPictureLocking extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPictureLocking.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpicturelockinga414type");
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getNoGrp();
    
    XmlBoolean xgetNoGrp();
    
    boolean isSetNoGrp();
    
    void setNoGrp(final boolean p0);
    
    void xsetNoGrp(final XmlBoolean p0);
    
    void unsetNoGrp();
    
    boolean getNoSelect();
    
    XmlBoolean xgetNoSelect();
    
    boolean isSetNoSelect();
    
    void setNoSelect(final boolean p0);
    
    void xsetNoSelect(final XmlBoolean p0);
    
    void unsetNoSelect();
    
    boolean getNoRot();
    
    XmlBoolean xgetNoRot();
    
    boolean isSetNoRot();
    
    void setNoRot(final boolean p0);
    
    void xsetNoRot(final XmlBoolean p0);
    
    void unsetNoRot();
    
    boolean getNoChangeAspect();
    
    XmlBoolean xgetNoChangeAspect();
    
    boolean isSetNoChangeAspect();
    
    void setNoChangeAspect(final boolean p0);
    
    void xsetNoChangeAspect(final XmlBoolean p0);
    
    void unsetNoChangeAspect();
    
    boolean getNoMove();
    
    XmlBoolean xgetNoMove();
    
    boolean isSetNoMove();
    
    void setNoMove(final boolean p0);
    
    void xsetNoMove(final XmlBoolean p0);
    
    void unsetNoMove();
    
    boolean getNoResize();
    
    XmlBoolean xgetNoResize();
    
    boolean isSetNoResize();
    
    void setNoResize(final boolean p0);
    
    void xsetNoResize(final XmlBoolean p0);
    
    void unsetNoResize();
    
    boolean getNoEditPoints();
    
    XmlBoolean xgetNoEditPoints();
    
    boolean isSetNoEditPoints();
    
    void setNoEditPoints(final boolean p0);
    
    void xsetNoEditPoints(final XmlBoolean p0);
    
    void unsetNoEditPoints();
    
    boolean getNoAdjustHandles();
    
    XmlBoolean xgetNoAdjustHandles();
    
    boolean isSetNoAdjustHandles();
    
    void setNoAdjustHandles(final boolean p0);
    
    void xsetNoAdjustHandles(final XmlBoolean p0);
    
    void unsetNoAdjustHandles();
    
    boolean getNoChangeArrowheads();
    
    XmlBoolean xgetNoChangeArrowheads();
    
    boolean isSetNoChangeArrowheads();
    
    void setNoChangeArrowheads(final boolean p0);
    
    void xsetNoChangeArrowheads(final XmlBoolean p0);
    
    void unsetNoChangeArrowheads();
    
    boolean getNoChangeShapeType();
    
    XmlBoolean xgetNoChangeShapeType();
    
    boolean isSetNoChangeShapeType();
    
    void setNoChangeShapeType(final boolean p0);
    
    void xsetNoChangeShapeType(final XmlBoolean p0);
    
    void unsetNoChangeShapeType();
    
    boolean getNoCrop();
    
    XmlBoolean xgetNoCrop();
    
    boolean isSetNoCrop();
    
    void setNoCrop(final boolean p0);
    
    void xsetNoCrop(final XmlBoolean p0);
    
    void unsetNoCrop();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPictureLocking.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPictureLocking newInstance() {
            return (CTPictureLocking)getTypeLoader().newInstance(CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking newInstance(final XmlOptions xmlOptions) {
            return (CTPictureLocking)getTypeLoader().newInstance(CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final String s) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(s, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(s, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final File file) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(file, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(file, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final URL url) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(url, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(url, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(inputStream, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(inputStream, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final Reader reader) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(reader, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureLocking)getTypeLoader().parse(reader, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(xmlStreamReader, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(xmlStreamReader, CTPictureLocking.type, xmlOptions);
        }
        
        public static CTPictureLocking parse(final Node node) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(node, CTPictureLocking.type, (XmlOptions)null);
        }
        
        public static CTPictureLocking parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureLocking)getTypeLoader().parse(node, CTPictureLocking.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPictureLocking parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPictureLocking)getTypeLoader().parse(xmlInputStream, CTPictureLocking.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPictureLocking parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPictureLocking)getTypeLoader().parse(xmlInputStream, CTPictureLocking.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureLocking.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureLocking.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
