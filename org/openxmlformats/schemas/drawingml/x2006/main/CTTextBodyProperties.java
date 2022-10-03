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

public interface CTTextBodyProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBodyProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextbodyproperties87ddtype");
    
    CTPresetTextShape getPrstTxWarp();
    
    boolean isSetPrstTxWarp();
    
    void setPrstTxWarp(final CTPresetTextShape p0);
    
    CTPresetTextShape addNewPrstTxWarp();
    
    void unsetPrstTxWarp();
    
    CTTextNoAutofit getNoAutofit();
    
    boolean isSetNoAutofit();
    
    void setNoAutofit(final CTTextNoAutofit p0);
    
    CTTextNoAutofit addNewNoAutofit();
    
    void unsetNoAutofit();
    
    CTTextNormalAutofit getNormAutofit();
    
    boolean isSetNormAutofit();
    
    void setNormAutofit(final CTTextNormalAutofit p0);
    
    CTTextNormalAutofit addNewNormAutofit();
    
    void unsetNormAutofit();
    
    CTTextShapeAutofit getSpAutoFit();
    
    boolean isSetSpAutoFit();
    
    void setSpAutoFit(final CTTextShapeAutofit p0);
    
    CTTextShapeAutofit addNewSpAutoFit();
    
    void unsetSpAutoFit();
    
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
    
    CTFlatText getFlatTx();
    
    boolean isSetFlatTx();
    
    void setFlatTx(final CTFlatText p0);
    
    CTFlatText addNewFlatTx();
    
    void unsetFlatTx();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getRot();
    
    STAngle xgetRot();
    
    boolean isSetRot();
    
    void setRot(final int p0);
    
    void xsetRot(final STAngle p0);
    
    void unsetRot();
    
    boolean getSpcFirstLastPara();
    
    XmlBoolean xgetSpcFirstLastPara();
    
    boolean isSetSpcFirstLastPara();
    
    void setSpcFirstLastPara(final boolean p0);
    
    void xsetSpcFirstLastPara(final XmlBoolean p0);
    
    void unsetSpcFirstLastPara();
    
    STTextVertOverflowType.Enum getVertOverflow();
    
    STTextVertOverflowType xgetVertOverflow();
    
    boolean isSetVertOverflow();
    
    void setVertOverflow(final STTextVertOverflowType.Enum p0);
    
    void xsetVertOverflow(final STTextVertOverflowType p0);
    
    void unsetVertOverflow();
    
    STTextHorzOverflowType.Enum getHorzOverflow();
    
    STTextHorzOverflowType xgetHorzOverflow();
    
    boolean isSetHorzOverflow();
    
    void setHorzOverflow(final STTextHorzOverflowType.Enum p0);
    
    void xsetHorzOverflow(final STTextHorzOverflowType p0);
    
    void unsetHorzOverflow();
    
    STTextVerticalType.Enum getVert();
    
    STTextVerticalType xgetVert();
    
    boolean isSetVert();
    
    void setVert(final STTextVerticalType.Enum p0);
    
    void xsetVert(final STTextVerticalType p0);
    
    void unsetVert();
    
    STTextWrappingType.Enum getWrap();
    
    STTextWrappingType xgetWrap();
    
    boolean isSetWrap();
    
    void setWrap(final STTextWrappingType.Enum p0);
    
    void xsetWrap(final STTextWrappingType p0);
    
    void unsetWrap();
    
    int getLIns();
    
    STCoordinate32 xgetLIns();
    
    boolean isSetLIns();
    
    void setLIns(final int p0);
    
    void xsetLIns(final STCoordinate32 p0);
    
    void unsetLIns();
    
    int getTIns();
    
    STCoordinate32 xgetTIns();
    
    boolean isSetTIns();
    
    void setTIns(final int p0);
    
    void xsetTIns(final STCoordinate32 p0);
    
    void unsetTIns();
    
    int getRIns();
    
    STCoordinate32 xgetRIns();
    
    boolean isSetRIns();
    
    void setRIns(final int p0);
    
    void xsetRIns(final STCoordinate32 p0);
    
    void unsetRIns();
    
    int getBIns();
    
    STCoordinate32 xgetBIns();
    
    boolean isSetBIns();
    
    void setBIns(final int p0);
    
    void xsetBIns(final STCoordinate32 p0);
    
    void unsetBIns();
    
    int getNumCol();
    
    STTextColumnCount xgetNumCol();
    
    boolean isSetNumCol();
    
    void setNumCol(final int p0);
    
    void xsetNumCol(final STTextColumnCount p0);
    
    void unsetNumCol();
    
    int getSpcCol();
    
    STPositiveCoordinate32 xgetSpcCol();
    
    boolean isSetSpcCol();
    
    void setSpcCol(final int p0);
    
    void xsetSpcCol(final STPositiveCoordinate32 p0);
    
    void unsetSpcCol();
    
    boolean getRtlCol();
    
    XmlBoolean xgetRtlCol();
    
    boolean isSetRtlCol();
    
    void setRtlCol(final boolean p0);
    
    void xsetRtlCol(final XmlBoolean p0);
    
    void unsetRtlCol();
    
    boolean getFromWordArt();
    
    XmlBoolean xgetFromWordArt();
    
    boolean isSetFromWordArt();
    
    void setFromWordArt(final boolean p0);
    
    void xsetFromWordArt(final XmlBoolean p0);
    
    void unsetFromWordArt();
    
    STTextAnchoringType.Enum getAnchor();
    
    STTextAnchoringType xgetAnchor();
    
    boolean isSetAnchor();
    
    void setAnchor(final STTextAnchoringType.Enum p0);
    
    void xsetAnchor(final STTextAnchoringType p0);
    
    void unsetAnchor();
    
    boolean getAnchorCtr();
    
    XmlBoolean xgetAnchorCtr();
    
    boolean isSetAnchorCtr();
    
    void setAnchorCtr(final boolean p0);
    
    void xsetAnchorCtr(final XmlBoolean p0);
    
    void unsetAnchorCtr();
    
    boolean getForceAA();
    
    XmlBoolean xgetForceAA();
    
    boolean isSetForceAA();
    
    void setForceAA(final boolean p0);
    
    void xsetForceAA(final XmlBoolean p0);
    
    void unsetForceAA();
    
    boolean getUpright();
    
    XmlBoolean xgetUpright();
    
    boolean isSetUpright();
    
    void setUpright(final boolean p0);
    
    void xsetUpright(final XmlBoolean p0);
    
    void unsetUpright();
    
    boolean getCompatLnSpc();
    
    XmlBoolean xgetCompatLnSpc();
    
    boolean isSetCompatLnSpc();
    
    void setCompatLnSpc(final boolean p0);
    
    void xsetCompatLnSpc(final XmlBoolean p0);
    
    void unsetCompatLnSpc();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBodyProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBodyProperties newInstance() {
            return (CTTextBodyProperties)getTypeLoader().newInstance(CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTextBodyProperties)getTypeLoader().newInstance(CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final String s) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(s, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(s, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final File file) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(file, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(file, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final URL url) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(url, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(url, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(inputStream, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(inputStream, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(reader, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBodyProperties)getTypeLoader().parse(reader, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(xmlStreamReader, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(xmlStreamReader, CTTextBodyProperties.type, xmlOptions);
        }
        
        public static CTTextBodyProperties parse(final Node node) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(node, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        public static CTTextBodyProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBodyProperties)getTypeLoader().parse(node, CTTextBodyProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBodyProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBodyProperties)getTypeLoader().parse(xmlInputStream, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBodyProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBodyProperties)getTypeLoader().parse(xmlInputStream, CTTextBodyProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBodyProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBodyProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
