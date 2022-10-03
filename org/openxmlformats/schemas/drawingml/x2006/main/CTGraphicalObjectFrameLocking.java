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

public interface CTGraphicalObjectFrameLocking extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGraphicalObjectFrameLocking.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgraphicalobjectframelocking42adtype");
    
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
    
    boolean getNoDrilldown();
    
    XmlBoolean xgetNoDrilldown();
    
    boolean isSetNoDrilldown();
    
    void setNoDrilldown(final boolean p0);
    
    void xsetNoDrilldown(final XmlBoolean p0);
    
    void unsetNoDrilldown();
    
    boolean getNoSelect();
    
    XmlBoolean xgetNoSelect();
    
    boolean isSetNoSelect();
    
    void setNoSelect(final boolean p0);
    
    void xsetNoSelect(final XmlBoolean p0);
    
    void unsetNoSelect();
    
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
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGraphicalObjectFrameLocking.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGraphicalObjectFrameLocking newInstance() {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().newInstance(CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking newInstance(final XmlOptions xmlOptions) {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().newInstance(CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final String s) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(s, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(s, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final File file) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(file, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(file, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final URL url) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(url, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(url, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(inputStream, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(inputStream, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final Reader reader) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(reader, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(reader, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final Node node) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(node, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameLocking parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(node, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrameLocking parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrameLocking parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrameLocking)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrameLocking.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrameLocking.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
