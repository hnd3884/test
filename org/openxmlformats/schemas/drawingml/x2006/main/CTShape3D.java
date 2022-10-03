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

public interface CTShape3D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShape3D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshape3d6783type");
    
    CTBevel getBevelT();
    
    boolean isSetBevelT();
    
    void setBevelT(final CTBevel p0);
    
    CTBevel addNewBevelT();
    
    void unsetBevelT();
    
    CTBevel getBevelB();
    
    boolean isSetBevelB();
    
    void setBevelB(final CTBevel p0);
    
    CTBevel addNewBevelB();
    
    void unsetBevelB();
    
    CTColor getExtrusionClr();
    
    boolean isSetExtrusionClr();
    
    void setExtrusionClr(final CTColor p0);
    
    CTColor addNewExtrusionClr();
    
    void unsetExtrusionClr();
    
    CTColor getContourClr();
    
    boolean isSetContourClr();
    
    void setContourClr(final CTColor p0);
    
    CTColor addNewContourClr();
    
    void unsetContourClr();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getZ();
    
    STCoordinate xgetZ();
    
    boolean isSetZ();
    
    void setZ(final long p0);
    
    void xsetZ(final STCoordinate p0);
    
    void unsetZ();
    
    long getExtrusionH();
    
    STPositiveCoordinate xgetExtrusionH();
    
    boolean isSetExtrusionH();
    
    void setExtrusionH(final long p0);
    
    void xsetExtrusionH(final STPositiveCoordinate p0);
    
    void unsetExtrusionH();
    
    long getContourW();
    
    STPositiveCoordinate xgetContourW();
    
    boolean isSetContourW();
    
    void setContourW(final long p0);
    
    void xsetContourW(final STPositiveCoordinate p0);
    
    void unsetContourW();
    
    STPresetMaterialType.Enum getPrstMaterial();
    
    STPresetMaterialType xgetPrstMaterial();
    
    boolean isSetPrstMaterial();
    
    void setPrstMaterial(final STPresetMaterialType.Enum p0);
    
    void xsetPrstMaterial(final STPresetMaterialType p0);
    
    void unsetPrstMaterial();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShape3D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShape3D newInstance() {
            return (CTShape3D)getTypeLoader().newInstance(CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D newInstance(final XmlOptions xmlOptions) {
            return (CTShape3D)getTypeLoader().newInstance(CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final String s) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(s, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(s, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final File file) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(file, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(file, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final URL url) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(url, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(url, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(inputStream, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(inputStream, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final Reader reader) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(reader, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape3D)getTypeLoader().parse(reader, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(xmlStreamReader, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(xmlStreamReader, CTShape3D.type, xmlOptions);
        }
        
        public static CTShape3D parse(final Node node) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(node, CTShape3D.type, (XmlOptions)null);
        }
        
        public static CTShape3D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape3D)getTypeLoader().parse(node, CTShape3D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShape3D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShape3D)getTypeLoader().parse(xmlInputStream, CTShape3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShape3D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShape3D)getTypeLoader().parse(xmlInputStream, CTShape3D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape3D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape3D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
