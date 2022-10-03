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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTScRgbColor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScRgbColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscrgbcolorf3e1type");
    
    List<CTPositiveFixedPercentage> getTintList();
    
    @Deprecated
    CTPositiveFixedPercentage[] getTintArray();
    
    CTPositiveFixedPercentage getTintArray(final int p0);
    
    int sizeOfTintArray();
    
    void setTintArray(final CTPositiveFixedPercentage[] p0);
    
    void setTintArray(final int p0, final CTPositiveFixedPercentage p1);
    
    CTPositiveFixedPercentage insertNewTint(final int p0);
    
    CTPositiveFixedPercentage addNewTint();
    
    void removeTint(final int p0);
    
    List<CTPositiveFixedPercentage> getShadeList();
    
    @Deprecated
    CTPositiveFixedPercentage[] getShadeArray();
    
    CTPositiveFixedPercentage getShadeArray(final int p0);
    
    int sizeOfShadeArray();
    
    void setShadeArray(final CTPositiveFixedPercentage[] p0);
    
    void setShadeArray(final int p0, final CTPositiveFixedPercentage p1);
    
    CTPositiveFixedPercentage insertNewShade(final int p0);
    
    CTPositiveFixedPercentage addNewShade();
    
    void removeShade(final int p0);
    
    List<CTComplementTransform> getCompList();
    
    @Deprecated
    CTComplementTransform[] getCompArray();
    
    CTComplementTransform getCompArray(final int p0);
    
    int sizeOfCompArray();
    
    void setCompArray(final CTComplementTransform[] p0);
    
    void setCompArray(final int p0, final CTComplementTransform p1);
    
    CTComplementTransform insertNewComp(final int p0);
    
    CTComplementTransform addNewComp();
    
    void removeComp(final int p0);
    
    List<CTInverseTransform> getInvList();
    
    @Deprecated
    CTInverseTransform[] getInvArray();
    
    CTInverseTransform getInvArray(final int p0);
    
    int sizeOfInvArray();
    
    void setInvArray(final CTInverseTransform[] p0);
    
    void setInvArray(final int p0, final CTInverseTransform p1);
    
    CTInverseTransform insertNewInv(final int p0);
    
    CTInverseTransform addNewInv();
    
    void removeInv(final int p0);
    
    List<CTGrayscaleTransform> getGrayList();
    
    @Deprecated
    CTGrayscaleTransform[] getGrayArray();
    
    CTGrayscaleTransform getGrayArray(final int p0);
    
    int sizeOfGrayArray();
    
    void setGrayArray(final CTGrayscaleTransform[] p0);
    
    void setGrayArray(final int p0, final CTGrayscaleTransform p1);
    
    CTGrayscaleTransform insertNewGray(final int p0);
    
    CTGrayscaleTransform addNewGray();
    
    void removeGray(final int p0);
    
    List<CTPositiveFixedPercentage> getAlphaList();
    
    @Deprecated
    CTPositiveFixedPercentage[] getAlphaArray();
    
    CTPositiveFixedPercentage getAlphaArray(final int p0);
    
    int sizeOfAlphaArray();
    
    void setAlphaArray(final CTPositiveFixedPercentage[] p0);
    
    void setAlphaArray(final int p0, final CTPositiveFixedPercentage p1);
    
    CTPositiveFixedPercentage insertNewAlpha(final int p0);
    
    CTPositiveFixedPercentage addNewAlpha();
    
    void removeAlpha(final int p0);
    
    List<CTFixedPercentage> getAlphaOffList();
    
    @Deprecated
    CTFixedPercentage[] getAlphaOffArray();
    
    CTFixedPercentage getAlphaOffArray(final int p0);
    
    int sizeOfAlphaOffArray();
    
    void setAlphaOffArray(final CTFixedPercentage[] p0);
    
    void setAlphaOffArray(final int p0, final CTFixedPercentage p1);
    
    CTFixedPercentage insertNewAlphaOff(final int p0);
    
    CTFixedPercentage addNewAlphaOff();
    
    void removeAlphaOff(final int p0);
    
    List<CTPositivePercentage> getAlphaModList();
    
    @Deprecated
    CTPositivePercentage[] getAlphaModArray();
    
    CTPositivePercentage getAlphaModArray(final int p0);
    
    int sizeOfAlphaModArray();
    
    void setAlphaModArray(final CTPositivePercentage[] p0);
    
    void setAlphaModArray(final int p0, final CTPositivePercentage p1);
    
    CTPositivePercentage insertNewAlphaMod(final int p0);
    
    CTPositivePercentage addNewAlphaMod();
    
    void removeAlphaMod(final int p0);
    
    List<CTPositiveFixedAngle> getHueList();
    
    @Deprecated
    CTPositiveFixedAngle[] getHueArray();
    
    CTPositiveFixedAngle getHueArray(final int p0);
    
    int sizeOfHueArray();
    
    void setHueArray(final CTPositiveFixedAngle[] p0);
    
    void setHueArray(final int p0, final CTPositiveFixedAngle p1);
    
    CTPositiveFixedAngle insertNewHue(final int p0);
    
    CTPositiveFixedAngle addNewHue();
    
    void removeHue(final int p0);
    
    List<CTAngle> getHueOffList();
    
    @Deprecated
    CTAngle[] getHueOffArray();
    
    CTAngle getHueOffArray(final int p0);
    
    int sizeOfHueOffArray();
    
    void setHueOffArray(final CTAngle[] p0);
    
    void setHueOffArray(final int p0, final CTAngle p1);
    
    CTAngle insertNewHueOff(final int p0);
    
    CTAngle addNewHueOff();
    
    void removeHueOff(final int p0);
    
    List<CTPositivePercentage> getHueModList();
    
    @Deprecated
    CTPositivePercentage[] getHueModArray();
    
    CTPositivePercentage getHueModArray(final int p0);
    
    int sizeOfHueModArray();
    
    void setHueModArray(final CTPositivePercentage[] p0);
    
    void setHueModArray(final int p0, final CTPositivePercentage p1);
    
    CTPositivePercentage insertNewHueMod(final int p0);
    
    CTPositivePercentage addNewHueMod();
    
    void removeHueMod(final int p0);
    
    List<CTPercentage> getSatList();
    
    @Deprecated
    CTPercentage[] getSatArray();
    
    CTPercentage getSatArray(final int p0);
    
    int sizeOfSatArray();
    
    void setSatArray(final CTPercentage[] p0);
    
    void setSatArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewSat(final int p0);
    
    CTPercentage addNewSat();
    
    void removeSat(final int p0);
    
    List<CTPercentage> getSatOffList();
    
    @Deprecated
    CTPercentage[] getSatOffArray();
    
    CTPercentage getSatOffArray(final int p0);
    
    int sizeOfSatOffArray();
    
    void setSatOffArray(final CTPercentage[] p0);
    
    void setSatOffArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewSatOff(final int p0);
    
    CTPercentage addNewSatOff();
    
    void removeSatOff(final int p0);
    
    List<CTPercentage> getSatModList();
    
    @Deprecated
    CTPercentage[] getSatModArray();
    
    CTPercentage getSatModArray(final int p0);
    
    int sizeOfSatModArray();
    
    void setSatModArray(final CTPercentage[] p0);
    
    void setSatModArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewSatMod(final int p0);
    
    CTPercentage addNewSatMod();
    
    void removeSatMod(final int p0);
    
    List<CTPercentage> getLumList();
    
    @Deprecated
    CTPercentage[] getLumArray();
    
    CTPercentage getLumArray(final int p0);
    
    int sizeOfLumArray();
    
    void setLumArray(final CTPercentage[] p0);
    
    void setLumArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewLum(final int p0);
    
    CTPercentage addNewLum();
    
    void removeLum(final int p0);
    
    List<CTPercentage> getLumOffList();
    
    @Deprecated
    CTPercentage[] getLumOffArray();
    
    CTPercentage getLumOffArray(final int p0);
    
    int sizeOfLumOffArray();
    
    void setLumOffArray(final CTPercentage[] p0);
    
    void setLumOffArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewLumOff(final int p0);
    
    CTPercentage addNewLumOff();
    
    void removeLumOff(final int p0);
    
    List<CTPercentage> getLumModList();
    
    @Deprecated
    CTPercentage[] getLumModArray();
    
    CTPercentage getLumModArray(final int p0);
    
    int sizeOfLumModArray();
    
    void setLumModArray(final CTPercentage[] p0);
    
    void setLumModArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewLumMod(final int p0);
    
    CTPercentage addNewLumMod();
    
    void removeLumMod(final int p0);
    
    List<CTPercentage> getRedList();
    
    @Deprecated
    CTPercentage[] getRedArray();
    
    CTPercentage getRedArray(final int p0);
    
    int sizeOfRedArray();
    
    void setRedArray(final CTPercentage[] p0);
    
    void setRedArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewRed(final int p0);
    
    CTPercentage addNewRed();
    
    void removeRed(final int p0);
    
    List<CTPercentage> getRedOffList();
    
    @Deprecated
    CTPercentage[] getRedOffArray();
    
    CTPercentage getRedOffArray(final int p0);
    
    int sizeOfRedOffArray();
    
    void setRedOffArray(final CTPercentage[] p0);
    
    void setRedOffArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewRedOff(final int p0);
    
    CTPercentage addNewRedOff();
    
    void removeRedOff(final int p0);
    
    List<CTPercentage> getRedModList();
    
    @Deprecated
    CTPercentage[] getRedModArray();
    
    CTPercentage getRedModArray(final int p0);
    
    int sizeOfRedModArray();
    
    void setRedModArray(final CTPercentage[] p0);
    
    void setRedModArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewRedMod(final int p0);
    
    CTPercentage addNewRedMod();
    
    void removeRedMod(final int p0);
    
    List<CTPercentage> getGreenList();
    
    @Deprecated
    CTPercentage[] getGreenArray();
    
    CTPercentage getGreenArray(final int p0);
    
    int sizeOfGreenArray();
    
    void setGreenArray(final CTPercentage[] p0);
    
    void setGreenArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewGreen(final int p0);
    
    CTPercentage addNewGreen();
    
    void removeGreen(final int p0);
    
    List<CTPercentage> getGreenOffList();
    
    @Deprecated
    CTPercentage[] getGreenOffArray();
    
    CTPercentage getGreenOffArray(final int p0);
    
    int sizeOfGreenOffArray();
    
    void setGreenOffArray(final CTPercentage[] p0);
    
    void setGreenOffArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewGreenOff(final int p0);
    
    CTPercentage addNewGreenOff();
    
    void removeGreenOff(final int p0);
    
    List<CTPercentage> getGreenModList();
    
    @Deprecated
    CTPercentage[] getGreenModArray();
    
    CTPercentage getGreenModArray(final int p0);
    
    int sizeOfGreenModArray();
    
    void setGreenModArray(final CTPercentage[] p0);
    
    void setGreenModArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewGreenMod(final int p0);
    
    CTPercentage addNewGreenMod();
    
    void removeGreenMod(final int p0);
    
    List<CTPercentage> getBlueList();
    
    @Deprecated
    CTPercentage[] getBlueArray();
    
    CTPercentage getBlueArray(final int p0);
    
    int sizeOfBlueArray();
    
    void setBlueArray(final CTPercentage[] p0);
    
    void setBlueArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewBlue(final int p0);
    
    CTPercentage addNewBlue();
    
    void removeBlue(final int p0);
    
    List<CTPercentage> getBlueOffList();
    
    @Deprecated
    CTPercentage[] getBlueOffArray();
    
    CTPercentage getBlueOffArray(final int p0);
    
    int sizeOfBlueOffArray();
    
    void setBlueOffArray(final CTPercentage[] p0);
    
    void setBlueOffArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewBlueOff(final int p0);
    
    CTPercentage addNewBlueOff();
    
    void removeBlueOff(final int p0);
    
    List<CTPercentage> getBlueModList();
    
    @Deprecated
    CTPercentage[] getBlueModArray();
    
    CTPercentage getBlueModArray(final int p0);
    
    int sizeOfBlueModArray();
    
    void setBlueModArray(final CTPercentage[] p0);
    
    void setBlueModArray(final int p0, final CTPercentage p1);
    
    CTPercentage insertNewBlueMod(final int p0);
    
    CTPercentage addNewBlueMod();
    
    void removeBlueMod(final int p0);
    
    List<CTGammaTransform> getGammaList();
    
    @Deprecated
    CTGammaTransform[] getGammaArray();
    
    CTGammaTransform getGammaArray(final int p0);
    
    int sizeOfGammaArray();
    
    void setGammaArray(final CTGammaTransform[] p0);
    
    void setGammaArray(final int p0, final CTGammaTransform p1);
    
    CTGammaTransform insertNewGamma(final int p0);
    
    CTGammaTransform addNewGamma();
    
    void removeGamma(final int p0);
    
    List<CTInverseGammaTransform> getInvGammaList();
    
    @Deprecated
    CTInverseGammaTransform[] getInvGammaArray();
    
    CTInverseGammaTransform getInvGammaArray(final int p0);
    
    int sizeOfInvGammaArray();
    
    void setInvGammaArray(final CTInverseGammaTransform[] p0);
    
    void setInvGammaArray(final int p0, final CTInverseGammaTransform p1);
    
    CTInverseGammaTransform insertNewInvGamma(final int p0);
    
    CTInverseGammaTransform addNewInvGamma();
    
    void removeInvGamma(final int p0);
    
    int getR();
    
    STPercentage xgetR();
    
    void setR(final int p0);
    
    void xsetR(final STPercentage p0);
    
    int getG();
    
    STPercentage xgetG();
    
    void setG(final int p0);
    
    void xsetG(final STPercentage p0);
    
    int getB();
    
    STPercentage xgetB();
    
    void setB(final int p0);
    
    void xsetB(final STPercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScRgbColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScRgbColor newInstance() {
            return (CTScRgbColor)getTypeLoader().newInstance(CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor newInstance(final XmlOptions xmlOptions) {
            return (CTScRgbColor)getTypeLoader().newInstance(CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final String s) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(s, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(s, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final File file) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(file, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(file, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final URL url) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(url, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(url, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(inputStream, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(inputStream, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final Reader reader) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(reader, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScRgbColor)getTypeLoader().parse(reader, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(xmlStreamReader, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(xmlStreamReader, CTScRgbColor.type, xmlOptions);
        }
        
        public static CTScRgbColor parse(final Node node) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(node, CTScRgbColor.type, (XmlOptions)null);
        }
        
        public static CTScRgbColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScRgbColor)getTypeLoader().parse(node, CTScRgbColor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScRgbColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScRgbColor)getTypeLoader().parse(xmlInputStream, CTScRgbColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScRgbColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScRgbColor)getTypeLoader().parse(xmlInputStream, CTScRgbColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScRgbColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScRgbColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
