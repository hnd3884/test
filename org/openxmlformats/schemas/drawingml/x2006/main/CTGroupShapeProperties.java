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

public interface CTGroupShapeProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGroupShapeProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgroupshapeproperties8690type");
    
    CTGroupTransform2D getXfrm();
    
    boolean isSetXfrm();
    
    void setXfrm(final CTGroupTransform2D p0);
    
    CTGroupTransform2D addNewXfrm();
    
    void unsetXfrm();
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    CTEffectList getEffectLst();
    
    boolean isSetEffectLst();
    
    void setEffectLst(final CTEffectList p0);
    
    CTEffectList addNewEffectLst();
    
    void unsetEffectLst();
    
    CTEffectContainer getEffectDag();
    
    boolean isSetEffectDag();
    
    void setEffectDag(final CTEffectContainer p0);
    
    CTEffectContainer addNewEffectDag();
    
    void unsetEffectDag();
    
    CTScene3D getScene3D();
    
    boolean isSetScene3D();
    
    void setScene3D(final CTScene3D p0);
    
    CTScene3D addNewScene3D();
    
    void unsetScene3D();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STBlackWhiteMode.Enum getBwMode();
    
    STBlackWhiteMode xgetBwMode();
    
    boolean isSetBwMode();
    
    void setBwMode(final STBlackWhiteMode.Enum p0);
    
    void xsetBwMode(final STBlackWhiteMode p0);
    
    void unsetBwMode();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGroupShapeProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGroupShapeProperties newInstance() {
            return (CTGroupShapeProperties)getTypeLoader().newInstance(CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties newInstance(final XmlOptions xmlOptions) {
            return (CTGroupShapeProperties)getTypeLoader().newInstance(CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final String s) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(s, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(s, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final File file) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(file, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(file, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final URL url) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(url, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(url, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(inputStream, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(inputStream, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(reader, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeProperties)getTypeLoader().parse(reader, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(xmlStreamReader, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(xmlStreamReader, CTGroupShapeProperties.type, xmlOptions);
        }
        
        public static CTGroupShapeProperties parse(final Node node) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(node, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeProperties)getTypeLoader().parse(node, CTGroupShapeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGroupShapeProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGroupShapeProperties)getTypeLoader().parse(xmlInputStream, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGroupShapeProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGroupShapeProperties)getTypeLoader().parse(xmlInputStream, CTGroupShapeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShapeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShapeProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
