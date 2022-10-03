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

public interface CTShapeProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShapeProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapeproperties30e5type");
    
    CTTransform2D getXfrm();
    
    boolean isSetXfrm();
    
    void setXfrm(final CTTransform2D p0);
    
    CTTransform2D addNewXfrm();
    
    void unsetXfrm();
    
    CTCustomGeometry2D getCustGeom();
    
    boolean isSetCustGeom();
    
    void setCustGeom(final CTCustomGeometry2D p0);
    
    CTCustomGeometry2D addNewCustGeom();
    
    void unsetCustGeom();
    
    CTPresetGeometry2D getPrstGeom();
    
    boolean isSetPrstGeom();
    
    void setPrstGeom(final CTPresetGeometry2D p0);
    
    CTPresetGeometry2D addNewPrstGeom();
    
    void unsetPrstGeom();
    
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
    
    CTLineProperties getLn();
    
    boolean isSetLn();
    
    void setLn(final CTLineProperties p0);
    
    CTLineProperties addNewLn();
    
    void unsetLn();
    
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
    
    CTShape3D getSp3D();
    
    boolean isSetSp3D();
    
    void setSp3D(final CTShape3D p0);
    
    CTShape3D addNewSp3D();
    
    void unsetSp3D();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShapeProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShapeProperties newInstance() {
            return (CTShapeProperties)getTypeLoader().newInstance(CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties newInstance(final XmlOptions xmlOptions) {
            return (CTShapeProperties)getTypeLoader().newInstance(CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final String s) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(s, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(s, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final File file) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(file, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(file, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final URL url) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(url, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(url, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(inputStream, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(inputStream, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(reader, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeProperties)getTypeLoader().parse(reader, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(xmlStreamReader, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(xmlStreamReader, CTShapeProperties.type, xmlOptions);
        }
        
        public static CTShapeProperties parse(final Node node) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(node, CTShapeProperties.type, (XmlOptions)null);
        }
        
        public static CTShapeProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeProperties)getTypeLoader().parse(node, CTShapeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShapeProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShapeProperties)getTypeLoader().parse(xmlInputStream, CTShapeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShapeProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShapeProperties)getTypeLoader().parse(xmlInputStream, CTShapeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
