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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBlip extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBlip.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctblip034ctype");
    
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
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getEmbed();
    
    STRelationshipId xgetEmbed();
    
    boolean isSetEmbed();
    
    void setEmbed(final String p0);
    
    void xsetEmbed(final STRelationshipId p0);
    
    void unsetEmbed();
    
    String getLink();
    
    STRelationshipId xgetLink();
    
    boolean isSetLink();
    
    void setLink(final String p0);
    
    void xsetLink(final STRelationshipId p0);
    
    void unsetLink();
    
    STBlipCompression.Enum getCstate();
    
    STBlipCompression xgetCstate();
    
    boolean isSetCstate();
    
    void setCstate(final STBlipCompression.Enum p0);
    
    void xsetCstate(final STBlipCompression p0);
    
    void unsetCstate();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBlip.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBlip newInstance() {
            return (CTBlip)getTypeLoader().newInstance(CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip newInstance(final XmlOptions xmlOptions) {
            return (CTBlip)getTypeLoader().newInstance(CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final String s) throws XmlException {
            return (CTBlip)getTypeLoader().parse(s, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBlip)getTypeLoader().parse(s, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final File file) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(file, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(file, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final URL url) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(url, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(url, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(inputStream, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(inputStream, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final Reader reader) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(reader, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBlip)getTypeLoader().parse(reader, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBlip)getTypeLoader().parse(xmlStreamReader, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBlip)getTypeLoader().parse(xmlStreamReader, CTBlip.type, xmlOptions);
        }
        
        public static CTBlip parse(final Node node) throws XmlException {
            return (CTBlip)getTypeLoader().parse(node, CTBlip.type, (XmlOptions)null);
        }
        
        public static CTBlip parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBlip)getTypeLoader().parse(node, CTBlip.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBlip parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBlip)getTypeLoader().parse(xmlInputStream, CTBlip.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBlip parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBlip)getTypeLoader().parse(xmlInputStream, CTBlip.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBlip.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBlip.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
