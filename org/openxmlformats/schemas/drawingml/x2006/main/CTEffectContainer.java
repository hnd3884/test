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
import org.apache.xmlbeans.XmlToken;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEffectContainer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEffectContainer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cteffectcontainer2e21type");
    
    List<CTEffectContainer> getContList();
    
    @Deprecated
    CTEffectContainer[] getContArray();
    
    CTEffectContainer getContArray(final int p0);
    
    int sizeOfContArray();
    
    void setContArray(final CTEffectContainer[] p0);
    
    void setContArray(final int p0, final CTEffectContainer p1);
    
    CTEffectContainer insertNewCont(final int p0);
    
    CTEffectContainer addNewCont();
    
    void removeCont(final int p0);
    
    List<CTEffectReference> getEffectList();
    
    @Deprecated
    CTEffectReference[] getEffectArray();
    
    CTEffectReference getEffectArray(final int p0);
    
    int sizeOfEffectArray();
    
    void setEffectArray(final CTEffectReference[] p0);
    
    void setEffectArray(final int p0, final CTEffectReference p1);
    
    CTEffectReference insertNewEffect(final int p0);
    
    CTEffectReference addNewEffect();
    
    void removeEffect(final int p0);
    
    List<CTAlphaBiLevelEffect> getAlphaBiLevelList();
    
    @Deprecated
    CTAlphaBiLevelEffect[] getAlphaBiLevelArray();
    
    CTAlphaBiLevelEffect getAlphaBiLevelArray(final int p0);
    
    int sizeOfAlphaBiLevelArray();
    
    void setAlphaBiLevelArray(final CTAlphaBiLevelEffect[] p0);
    
    void setAlphaBiLevelArray(final int p0, final CTAlphaBiLevelEffect p1);
    
    CTAlphaBiLevelEffect insertNewAlphaBiLevel(final int p0);
    
    CTAlphaBiLevelEffect addNewAlphaBiLevel();
    
    void removeAlphaBiLevel(final int p0);
    
    List<CTAlphaCeilingEffect> getAlphaCeilingList();
    
    @Deprecated
    CTAlphaCeilingEffect[] getAlphaCeilingArray();
    
    CTAlphaCeilingEffect getAlphaCeilingArray(final int p0);
    
    int sizeOfAlphaCeilingArray();
    
    void setAlphaCeilingArray(final CTAlphaCeilingEffect[] p0);
    
    void setAlphaCeilingArray(final int p0, final CTAlphaCeilingEffect p1);
    
    CTAlphaCeilingEffect insertNewAlphaCeiling(final int p0);
    
    CTAlphaCeilingEffect addNewAlphaCeiling();
    
    void removeAlphaCeiling(final int p0);
    
    List<CTAlphaFloorEffect> getAlphaFloorList();
    
    @Deprecated
    CTAlphaFloorEffect[] getAlphaFloorArray();
    
    CTAlphaFloorEffect getAlphaFloorArray(final int p0);
    
    int sizeOfAlphaFloorArray();
    
    void setAlphaFloorArray(final CTAlphaFloorEffect[] p0);
    
    void setAlphaFloorArray(final int p0, final CTAlphaFloorEffect p1);
    
    CTAlphaFloorEffect insertNewAlphaFloor(final int p0);
    
    CTAlphaFloorEffect addNewAlphaFloor();
    
    void removeAlphaFloor(final int p0);
    
    List<CTAlphaInverseEffect> getAlphaInvList();
    
    @Deprecated
    CTAlphaInverseEffect[] getAlphaInvArray();
    
    CTAlphaInverseEffect getAlphaInvArray(final int p0);
    
    int sizeOfAlphaInvArray();
    
    void setAlphaInvArray(final CTAlphaInverseEffect[] p0);
    
    void setAlphaInvArray(final int p0, final CTAlphaInverseEffect p1);
    
    CTAlphaInverseEffect insertNewAlphaInv(final int p0);
    
    CTAlphaInverseEffect addNewAlphaInv();
    
    void removeAlphaInv(final int p0);
    
    List<CTAlphaModulateEffect> getAlphaModList();
    
    @Deprecated
    CTAlphaModulateEffect[] getAlphaModArray();
    
    CTAlphaModulateEffect getAlphaModArray(final int p0);
    
    int sizeOfAlphaModArray();
    
    void setAlphaModArray(final CTAlphaModulateEffect[] p0);
    
    void setAlphaModArray(final int p0, final CTAlphaModulateEffect p1);
    
    CTAlphaModulateEffect insertNewAlphaMod(final int p0);
    
    CTAlphaModulateEffect addNewAlphaMod();
    
    void removeAlphaMod(final int p0);
    
    List<CTAlphaModulateFixedEffect> getAlphaModFixList();
    
    @Deprecated
    CTAlphaModulateFixedEffect[] getAlphaModFixArray();
    
    CTAlphaModulateFixedEffect getAlphaModFixArray(final int p0);
    
    int sizeOfAlphaModFixArray();
    
    void setAlphaModFixArray(final CTAlphaModulateFixedEffect[] p0);
    
    void setAlphaModFixArray(final int p0, final CTAlphaModulateFixedEffect p1);
    
    CTAlphaModulateFixedEffect insertNewAlphaModFix(final int p0);
    
    CTAlphaModulateFixedEffect addNewAlphaModFix();
    
    void removeAlphaModFix(final int p0);
    
    List<CTAlphaOutsetEffect> getAlphaOutsetList();
    
    @Deprecated
    CTAlphaOutsetEffect[] getAlphaOutsetArray();
    
    CTAlphaOutsetEffect getAlphaOutsetArray(final int p0);
    
    int sizeOfAlphaOutsetArray();
    
    void setAlphaOutsetArray(final CTAlphaOutsetEffect[] p0);
    
    void setAlphaOutsetArray(final int p0, final CTAlphaOutsetEffect p1);
    
    CTAlphaOutsetEffect insertNewAlphaOutset(final int p0);
    
    CTAlphaOutsetEffect addNewAlphaOutset();
    
    void removeAlphaOutset(final int p0);
    
    List<CTAlphaReplaceEffect> getAlphaReplList();
    
    @Deprecated
    CTAlphaReplaceEffect[] getAlphaReplArray();
    
    CTAlphaReplaceEffect getAlphaReplArray(final int p0);
    
    int sizeOfAlphaReplArray();
    
    void setAlphaReplArray(final CTAlphaReplaceEffect[] p0);
    
    void setAlphaReplArray(final int p0, final CTAlphaReplaceEffect p1);
    
    CTAlphaReplaceEffect insertNewAlphaRepl(final int p0);
    
    CTAlphaReplaceEffect addNewAlphaRepl();
    
    void removeAlphaRepl(final int p0);
    
    List<CTBiLevelEffect> getBiLevelList();
    
    @Deprecated
    CTBiLevelEffect[] getBiLevelArray();
    
    CTBiLevelEffect getBiLevelArray(final int p0);
    
    int sizeOfBiLevelArray();
    
    void setBiLevelArray(final CTBiLevelEffect[] p0);
    
    void setBiLevelArray(final int p0, final CTBiLevelEffect p1);
    
    CTBiLevelEffect insertNewBiLevel(final int p0);
    
    CTBiLevelEffect addNewBiLevel();
    
    void removeBiLevel(final int p0);
    
    List<CTBlendEffect> getBlendList();
    
    @Deprecated
    CTBlendEffect[] getBlendArray();
    
    CTBlendEffect getBlendArray(final int p0);
    
    int sizeOfBlendArray();
    
    void setBlendArray(final CTBlendEffect[] p0);
    
    void setBlendArray(final int p0, final CTBlendEffect p1);
    
    CTBlendEffect insertNewBlend(final int p0);
    
    CTBlendEffect addNewBlend();
    
    void removeBlend(final int p0);
    
    List<CTBlurEffect> getBlurList();
    
    @Deprecated
    CTBlurEffect[] getBlurArray();
    
    CTBlurEffect getBlurArray(final int p0);
    
    int sizeOfBlurArray();
    
    void setBlurArray(final CTBlurEffect[] p0);
    
    void setBlurArray(final int p0, final CTBlurEffect p1);
    
    CTBlurEffect insertNewBlur(final int p0);
    
    CTBlurEffect addNewBlur();
    
    void removeBlur(final int p0);
    
    List<CTColorChangeEffect> getClrChangeList();
    
    @Deprecated
    CTColorChangeEffect[] getClrChangeArray();
    
    CTColorChangeEffect getClrChangeArray(final int p0);
    
    int sizeOfClrChangeArray();
    
    void setClrChangeArray(final CTColorChangeEffect[] p0);
    
    void setClrChangeArray(final int p0, final CTColorChangeEffect p1);
    
    CTColorChangeEffect insertNewClrChange(final int p0);
    
    CTColorChangeEffect addNewClrChange();
    
    void removeClrChange(final int p0);
    
    List<CTColorReplaceEffect> getClrReplList();
    
    @Deprecated
    CTColorReplaceEffect[] getClrReplArray();
    
    CTColorReplaceEffect getClrReplArray(final int p0);
    
    int sizeOfClrReplArray();
    
    void setClrReplArray(final CTColorReplaceEffect[] p0);
    
    void setClrReplArray(final int p0, final CTColorReplaceEffect p1);
    
    CTColorReplaceEffect insertNewClrRepl(final int p0);
    
    CTColorReplaceEffect addNewClrRepl();
    
    void removeClrRepl(final int p0);
    
    List<CTDuotoneEffect> getDuotoneList();
    
    @Deprecated
    CTDuotoneEffect[] getDuotoneArray();
    
    CTDuotoneEffect getDuotoneArray(final int p0);
    
    int sizeOfDuotoneArray();
    
    void setDuotoneArray(final CTDuotoneEffect[] p0);
    
    void setDuotoneArray(final int p0, final CTDuotoneEffect p1);
    
    CTDuotoneEffect insertNewDuotone(final int p0);
    
    CTDuotoneEffect addNewDuotone();
    
    void removeDuotone(final int p0);
    
    List<CTFillEffect> getFillList();
    
    @Deprecated
    CTFillEffect[] getFillArray();
    
    CTFillEffect getFillArray(final int p0);
    
    int sizeOfFillArray();
    
    void setFillArray(final CTFillEffect[] p0);
    
    void setFillArray(final int p0, final CTFillEffect p1);
    
    CTFillEffect insertNewFill(final int p0);
    
    CTFillEffect addNewFill();
    
    void removeFill(final int p0);
    
    List<CTFillOverlayEffect> getFillOverlayList();
    
    @Deprecated
    CTFillOverlayEffect[] getFillOverlayArray();
    
    CTFillOverlayEffect getFillOverlayArray(final int p0);
    
    int sizeOfFillOverlayArray();
    
    void setFillOverlayArray(final CTFillOverlayEffect[] p0);
    
    void setFillOverlayArray(final int p0, final CTFillOverlayEffect p1);
    
    CTFillOverlayEffect insertNewFillOverlay(final int p0);
    
    CTFillOverlayEffect addNewFillOverlay();
    
    void removeFillOverlay(final int p0);
    
    List<CTGlowEffect> getGlowList();
    
    @Deprecated
    CTGlowEffect[] getGlowArray();
    
    CTGlowEffect getGlowArray(final int p0);
    
    int sizeOfGlowArray();
    
    void setGlowArray(final CTGlowEffect[] p0);
    
    void setGlowArray(final int p0, final CTGlowEffect p1);
    
    CTGlowEffect insertNewGlow(final int p0);
    
    CTGlowEffect addNewGlow();
    
    void removeGlow(final int p0);
    
    List<CTGrayscaleEffect> getGraysclList();
    
    @Deprecated
    CTGrayscaleEffect[] getGraysclArray();
    
    CTGrayscaleEffect getGraysclArray(final int p0);
    
    int sizeOfGraysclArray();
    
    void setGraysclArray(final CTGrayscaleEffect[] p0);
    
    void setGraysclArray(final int p0, final CTGrayscaleEffect p1);
    
    CTGrayscaleEffect insertNewGrayscl(final int p0);
    
    CTGrayscaleEffect addNewGrayscl();
    
    void removeGrayscl(final int p0);
    
    List<CTHSLEffect> getHslList();
    
    @Deprecated
    CTHSLEffect[] getHslArray();
    
    CTHSLEffect getHslArray(final int p0);
    
    int sizeOfHslArray();
    
    void setHslArray(final CTHSLEffect[] p0);
    
    void setHslArray(final int p0, final CTHSLEffect p1);
    
    CTHSLEffect insertNewHsl(final int p0);
    
    CTHSLEffect addNewHsl();
    
    void removeHsl(final int p0);
    
    List<CTInnerShadowEffect> getInnerShdwList();
    
    @Deprecated
    CTInnerShadowEffect[] getInnerShdwArray();
    
    CTInnerShadowEffect getInnerShdwArray(final int p0);
    
    int sizeOfInnerShdwArray();
    
    void setInnerShdwArray(final CTInnerShadowEffect[] p0);
    
    void setInnerShdwArray(final int p0, final CTInnerShadowEffect p1);
    
    CTInnerShadowEffect insertNewInnerShdw(final int p0);
    
    CTInnerShadowEffect addNewInnerShdw();
    
    void removeInnerShdw(final int p0);
    
    List<CTLuminanceEffect> getLumList();
    
    @Deprecated
    CTLuminanceEffect[] getLumArray();
    
    CTLuminanceEffect getLumArray(final int p0);
    
    int sizeOfLumArray();
    
    void setLumArray(final CTLuminanceEffect[] p0);
    
    void setLumArray(final int p0, final CTLuminanceEffect p1);
    
    CTLuminanceEffect insertNewLum(final int p0);
    
    CTLuminanceEffect addNewLum();
    
    void removeLum(final int p0);
    
    List<CTOuterShadowEffect> getOuterShdwList();
    
    @Deprecated
    CTOuterShadowEffect[] getOuterShdwArray();
    
    CTOuterShadowEffect getOuterShdwArray(final int p0);
    
    int sizeOfOuterShdwArray();
    
    void setOuterShdwArray(final CTOuterShadowEffect[] p0);
    
    void setOuterShdwArray(final int p0, final CTOuterShadowEffect p1);
    
    CTOuterShadowEffect insertNewOuterShdw(final int p0);
    
    CTOuterShadowEffect addNewOuterShdw();
    
    void removeOuterShdw(final int p0);
    
    List<CTPresetShadowEffect> getPrstShdwList();
    
    @Deprecated
    CTPresetShadowEffect[] getPrstShdwArray();
    
    CTPresetShadowEffect getPrstShdwArray(final int p0);
    
    int sizeOfPrstShdwArray();
    
    void setPrstShdwArray(final CTPresetShadowEffect[] p0);
    
    void setPrstShdwArray(final int p0, final CTPresetShadowEffect p1);
    
    CTPresetShadowEffect insertNewPrstShdw(final int p0);
    
    CTPresetShadowEffect addNewPrstShdw();
    
    void removePrstShdw(final int p0);
    
    List<CTReflectionEffect> getReflectionList();
    
    @Deprecated
    CTReflectionEffect[] getReflectionArray();
    
    CTReflectionEffect getReflectionArray(final int p0);
    
    int sizeOfReflectionArray();
    
    void setReflectionArray(final CTReflectionEffect[] p0);
    
    void setReflectionArray(final int p0, final CTReflectionEffect p1);
    
    CTReflectionEffect insertNewReflection(final int p0);
    
    CTReflectionEffect addNewReflection();
    
    void removeReflection(final int p0);
    
    List<CTRelativeOffsetEffect> getRelOffList();
    
    @Deprecated
    CTRelativeOffsetEffect[] getRelOffArray();
    
    CTRelativeOffsetEffect getRelOffArray(final int p0);
    
    int sizeOfRelOffArray();
    
    void setRelOffArray(final CTRelativeOffsetEffect[] p0);
    
    void setRelOffArray(final int p0, final CTRelativeOffsetEffect p1);
    
    CTRelativeOffsetEffect insertNewRelOff(final int p0);
    
    CTRelativeOffsetEffect addNewRelOff();
    
    void removeRelOff(final int p0);
    
    List<CTSoftEdgesEffect> getSoftEdgeList();
    
    @Deprecated
    CTSoftEdgesEffect[] getSoftEdgeArray();
    
    CTSoftEdgesEffect getSoftEdgeArray(final int p0);
    
    int sizeOfSoftEdgeArray();
    
    void setSoftEdgeArray(final CTSoftEdgesEffect[] p0);
    
    void setSoftEdgeArray(final int p0, final CTSoftEdgesEffect p1);
    
    CTSoftEdgesEffect insertNewSoftEdge(final int p0);
    
    CTSoftEdgesEffect addNewSoftEdge();
    
    void removeSoftEdge(final int p0);
    
    List<CTTintEffect> getTintList();
    
    @Deprecated
    CTTintEffect[] getTintArray();
    
    CTTintEffect getTintArray(final int p0);
    
    int sizeOfTintArray();
    
    void setTintArray(final CTTintEffect[] p0);
    
    void setTintArray(final int p0, final CTTintEffect p1);
    
    CTTintEffect insertNewTint(final int p0);
    
    CTTintEffect addNewTint();
    
    void removeTint(final int p0);
    
    List<CTTransformEffect> getXfrmList();
    
    @Deprecated
    CTTransformEffect[] getXfrmArray();
    
    CTTransformEffect getXfrmArray(final int p0);
    
    int sizeOfXfrmArray();
    
    void setXfrmArray(final CTTransformEffect[] p0);
    
    void setXfrmArray(final int p0, final CTTransformEffect p1);
    
    CTTransformEffect insertNewXfrm(final int p0);
    
    CTTransformEffect addNewXfrm();
    
    void removeXfrm(final int p0);
    
    STEffectContainerType.Enum getType();
    
    STEffectContainerType xgetType();
    
    boolean isSetType();
    
    void setType(final STEffectContainerType.Enum p0);
    
    void xsetType(final STEffectContainerType p0);
    
    void unsetType();
    
    String getName();
    
    XmlToken xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlToken p0);
    
    void unsetName();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEffectContainer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEffectContainer newInstance() {
            return (CTEffectContainer)getTypeLoader().newInstance(CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer newInstance(final XmlOptions xmlOptions) {
            return (CTEffectContainer)getTypeLoader().newInstance(CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final String s) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(s, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(s, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final File file) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(file, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(file, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final URL url) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(url, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(url, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(inputStream, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(inputStream, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final Reader reader) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(reader, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectContainer)getTypeLoader().parse(reader, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(xmlStreamReader, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(xmlStreamReader, CTEffectContainer.type, xmlOptions);
        }
        
        public static CTEffectContainer parse(final Node node) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(node, CTEffectContainer.type, (XmlOptions)null);
        }
        
        public static CTEffectContainer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectContainer)getTypeLoader().parse(node, CTEffectContainer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEffectContainer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEffectContainer)getTypeLoader().parse(xmlInputStream, CTEffectContainer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEffectContainer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEffectContainer)getTypeLoader().parse(xmlInputStream, CTEffectContainer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectContainer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectContainer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
