package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface CTRPrElt extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRPrElt.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrpreltecc2type");
    
    List<CTFontName> getRFontList();
    
    @Deprecated
    CTFontName[] getRFontArray();
    
    CTFontName getRFontArray(final int p0);
    
    int sizeOfRFontArray();
    
    void setRFontArray(final CTFontName[] p0);
    
    void setRFontArray(final int p0, final CTFontName p1);
    
    CTFontName insertNewRFont(final int p0);
    
    CTFontName addNewRFont();
    
    void removeRFont(final int p0);
    
    List<CTIntProperty> getCharsetList();
    
    @Deprecated
    CTIntProperty[] getCharsetArray();
    
    CTIntProperty getCharsetArray(final int p0);
    
    int sizeOfCharsetArray();
    
    void setCharsetArray(final CTIntProperty[] p0);
    
    void setCharsetArray(final int p0, final CTIntProperty p1);
    
    CTIntProperty insertNewCharset(final int p0);
    
    CTIntProperty addNewCharset();
    
    void removeCharset(final int p0);
    
    List<CTIntProperty> getFamilyList();
    
    @Deprecated
    CTIntProperty[] getFamilyArray();
    
    CTIntProperty getFamilyArray(final int p0);
    
    int sizeOfFamilyArray();
    
    void setFamilyArray(final CTIntProperty[] p0);
    
    void setFamilyArray(final int p0, final CTIntProperty p1);
    
    CTIntProperty insertNewFamily(final int p0);
    
    CTIntProperty addNewFamily();
    
    void removeFamily(final int p0);
    
    List<CTBooleanProperty> getBList();
    
    @Deprecated
    CTBooleanProperty[] getBArray();
    
    CTBooleanProperty getBArray(final int p0);
    
    int sizeOfBArray();
    
    void setBArray(final CTBooleanProperty[] p0);
    
    void setBArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewB(final int p0);
    
    CTBooleanProperty addNewB();
    
    void removeB(final int p0);
    
    List<CTBooleanProperty> getIList();
    
    @Deprecated
    CTBooleanProperty[] getIArray();
    
    CTBooleanProperty getIArray(final int p0);
    
    int sizeOfIArray();
    
    void setIArray(final CTBooleanProperty[] p0);
    
    void setIArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewI(final int p0);
    
    CTBooleanProperty addNewI();
    
    void removeI(final int p0);
    
    List<CTBooleanProperty> getStrikeList();
    
    @Deprecated
    CTBooleanProperty[] getStrikeArray();
    
    CTBooleanProperty getStrikeArray(final int p0);
    
    int sizeOfStrikeArray();
    
    void setStrikeArray(final CTBooleanProperty[] p0);
    
    void setStrikeArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewStrike(final int p0);
    
    CTBooleanProperty addNewStrike();
    
    void removeStrike(final int p0);
    
    List<CTBooleanProperty> getOutlineList();
    
    @Deprecated
    CTBooleanProperty[] getOutlineArray();
    
    CTBooleanProperty getOutlineArray(final int p0);
    
    int sizeOfOutlineArray();
    
    void setOutlineArray(final CTBooleanProperty[] p0);
    
    void setOutlineArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewOutline(final int p0);
    
    CTBooleanProperty addNewOutline();
    
    void removeOutline(final int p0);
    
    List<CTBooleanProperty> getShadowList();
    
    @Deprecated
    CTBooleanProperty[] getShadowArray();
    
    CTBooleanProperty getShadowArray(final int p0);
    
    int sizeOfShadowArray();
    
    void setShadowArray(final CTBooleanProperty[] p0);
    
    void setShadowArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewShadow(final int p0);
    
    CTBooleanProperty addNewShadow();
    
    void removeShadow(final int p0);
    
    List<CTBooleanProperty> getCondenseList();
    
    @Deprecated
    CTBooleanProperty[] getCondenseArray();
    
    CTBooleanProperty getCondenseArray(final int p0);
    
    int sizeOfCondenseArray();
    
    void setCondenseArray(final CTBooleanProperty[] p0);
    
    void setCondenseArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewCondense(final int p0);
    
    CTBooleanProperty addNewCondense();
    
    void removeCondense(final int p0);
    
    List<CTBooleanProperty> getExtendList();
    
    @Deprecated
    CTBooleanProperty[] getExtendArray();
    
    CTBooleanProperty getExtendArray(final int p0);
    
    int sizeOfExtendArray();
    
    void setExtendArray(final CTBooleanProperty[] p0);
    
    void setExtendArray(final int p0, final CTBooleanProperty p1);
    
    CTBooleanProperty insertNewExtend(final int p0);
    
    CTBooleanProperty addNewExtend();
    
    void removeExtend(final int p0);
    
    List<CTColor> getColorList();
    
    @Deprecated
    CTColor[] getColorArray();
    
    CTColor getColorArray(final int p0);
    
    int sizeOfColorArray();
    
    void setColorArray(final CTColor[] p0);
    
    void setColorArray(final int p0, final CTColor p1);
    
    CTColor insertNewColor(final int p0);
    
    CTColor addNewColor();
    
    void removeColor(final int p0);
    
    List<CTFontSize> getSzList();
    
    @Deprecated
    CTFontSize[] getSzArray();
    
    CTFontSize getSzArray(final int p0);
    
    int sizeOfSzArray();
    
    void setSzArray(final CTFontSize[] p0);
    
    void setSzArray(final int p0, final CTFontSize p1);
    
    CTFontSize insertNewSz(final int p0);
    
    CTFontSize addNewSz();
    
    void removeSz(final int p0);
    
    List<CTUnderlineProperty> getUList();
    
    @Deprecated
    CTUnderlineProperty[] getUArray();
    
    CTUnderlineProperty getUArray(final int p0);
    
    int sizeOfUArray();
    
    void setUArray(final CTUnderlineProperty[] p0);
    
    void setUArray(final int p0, final CTUnderlineProperty p1);
    
    CTUnderlineProperty insertNewU(final int p0);
    
    CTUnderlineProperty addNewU();
    
    void removeU(final int p0);
    
    List<CTVerticalAlignFontProperty> getVertAlignList();
    
    @Deprecated
    CTVerticalAlignFontProperty[] getVertAlignArray();
    
    CTVerticalAlignFontProperty getVertAlignArray(final int p0);
    
    int sizeOfVertAlignArray();
    
    void setVertAlignArray(final CTVerticalAlignFontProperty[] p0);
    
    void setVertAlignArray(final int p0, final CTVerticalAlignFontProperty p1);
    
    CTVerticalAlignFontProperty insertNewVertAlign(final int p0);
    
    CTVerticalAlignFontProperty addNewVertAlign();
    
    void removeVertAlign(final int p0);
    
    List<CTFontScheme> getSchemeList();
    
    @Deprecated
    CTFontScheme[] getSchemeArray();
    
    CTFontScheme getSchemeArray(final int p0);
    
    int sizeOfSchemeArray();
    
    void setSchemeArray(final CTFontScheme[] p0);
    
    void setSchemeArray(final int p0, final CTFontScheme p1);
    
    CTFontScheme insertNewScheme(final int p0);
    
    CTFontScheme addNewScheme();
    
    void removeScheme(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRPrElt.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRPrElt newInstance() {
            return (CTRPrElt)getTypeLoader().newInstance(CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt newInstance(final XmlOptions xmlOptions) {
            return (CTRPrElt)getTypeLoader().newInstance(CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final String s) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(s, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(s, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final File file) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(file, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(file, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final URL url) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(url, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(url, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(inputStream, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(inputStream, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final Reader reader) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(reader, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrElt)getTypeLoader().parse(reader, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(xmlStreamReader, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(xmlStreamReader, CTRPrElt.type, xmlOptions);
        }
        
        public static CTRPrElt parse(final Node node) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(node, CTRPrElt.type, (XmlOptions)null);
        }
        
        public static CTRPrElt parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrElt)getTypeLoader().parse(node, CTRPrElt.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRPrElt parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRPrElt)getTypeLoader().parse(xmlInputStream, CTRPrElt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRPrElt parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRPrElt)getTypeLoader().parse(xmlInputStream, CTRPrElt.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPrElt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPrElt.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
