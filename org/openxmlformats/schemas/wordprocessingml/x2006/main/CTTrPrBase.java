package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTTrPrBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTrPrBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttrprbase5d77type");
    
    List<CTCnf> getCnfStyleList();
    
    @Deprecated
    CTCnf[] getCnfStyleArray();
    
    CTCnf getCnfStyleArray(final int p0);
    
    int sizeOfCnfStyleArray();
    
    void setCnfStyleArray(final CTCnf[] p0);
    
    void setCnfStyleArray(final int p0, final CTCnf p1);
    
    CTCnf insertNewCnfStyle(final int p0);
    
    CTCnf addNewCnfStyle();
    
    void removeCnfStyle(final int p0);
    
    List<CTDecimalNumber> getDivIdList();
    
    @Deprecated
    CTDecimalNumber[] getDivIdArray();
    
    CTDecimalNumber getDivIdArray(final int p0);
    
    int sizeOfDivIdArray();
    
    void setDivIdArray(final CTDecimalNumber[] p0);
    
    void setDivIdArray(final int p0, final CTDecimalNumber p1);
    
    CTDecimalNumber insertNewDivId(final int p0);
    
    CTDecimalNumber addNewDivId();
    
    void removeDivId(final int p0);
    
    List<CTDecimalNumber> getGridBeforeList();
    
    @Deprecated
    CTDecimalNumber[] getGridBeforeArray();
    
    CTDecimalNumber getGridBeforeArray(final int p0);
    
    int sizeOfGridBeforeArray();
    
    void setGridBeforeArray(final CTDecimalNumber[] p0);
    
    void setGridBeforeArray(final int p0, final CTDecimalNumber p1);
    
    CTDecimalNumber insertNewGridBefore(final int p0);
    
    CTDecimalNumber addNewGridBefore();
    
    void removeGridBefore(final int p0);
    
    List<CTDecimalNumber> getGridAfterList();
    
    @Deprecated
    CTDecimalNumber[] getGridAfterArray();
    
    CTDecimalNumber getGridAfterArray(final int p0);
    
    int sizeOfGridAfterArray();
    
    void setGridAfterArray(final CTDecimalNumber[] p0);
    
    void setGridAfterArray(final int p0, final CTDecimalNumber p1);
    
    CTDecimalNumber insertNewGridAfter(final int p0);
    
    CTDecimalNumber addNewGridAfter();
    
    void removeGridAfter(final int p0);
    
    List<CTTblWidth> getWBeforeList();
    
    @Deprecated
    CTTblWidth[] getWBeforeArray();
    
    CTTblWidth getWBeforeArray(final int p0);
    
    int sizeOfWBeforeArray();
    
    void setWBeforeArray(final CTTblWidth[] p0);
    
    void setWBeforeArray(final int p0, final CTTblWidth p1);
    
    CTTblWidth insertNewWBefore(final int p0);
    
    CTTblWidth addNewWBefore();
    
    void removeWBefore(final int p0);
    
    List<CTTblWidth> getWAfterList();
    
    @Deprecated
    CTTblWidth[] getWAfterArray();
    
    CTTblWidth getWAfterArray(final int p0);
    
    int sizeOfWAfterArray();
    
    void setWAfterArray(final CTTblWidth[] p0);
    
    void setWAfterArray(final int p0, final CTTblWidth p1);
    
    CTTblWidth insertNewWAfter(final int p0);
    
    CTTblWidth addNewWAfter();
    
    void removeWAfter(final int p0);
    
    List<CTOnOff> getCantSplitList();
    
    @Deprecated
    CTOnOff[] getCantSplitArray();
    
    CTOnOff getCantSplitArray(final int p0);
    
    int sizeOfCantSplitArray();
    
    void setCantSplitArray(final CTOnOff[] p0);
    
    void setCantSplitArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewCantSplit(final int p0);
    
    CTOnOff addNewCantSplit();
    
    void removeCantSplit(final int p0);
    
    List<CTHeight> getTrHeightList();
    
    @Deprecated
    CTHeight[] getTrHeightArray();
    
    CTHeight getTrHeightArray(final int p0);
    
    int sizeOfTrHeightArray();
    
    void setTrHeightArray(final CTHeight[] p0);
    
    void setTrHeightArray(final int p0, final CTHeight p1);
    
    CTHeight insertNewTrHeight(final int p0);
    
    CTHeight addNewTrHeight();
    
    void removeTrHeight(final int p0);
    
    List<CTOnOff> getTblHeaderList();
    
    @Deprecated
    CTOnOff[] getTblHeaderArray();
    
    CTOnOff getTblHeaderArray(final int p0);
    
    int sizeOfTblHeaderArray();
    
    void setTblHeaderArray(final CTOnOff[] p0);
    
    void setTblHeaderArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewTblHeader(final int p0);
    
    CTOnOff addNewTblHeader();
    
    void removeTblHeader(final int p0);
    
    List<CTTblWidth> getTblCellSpacingList();
    
    @Deprecated
    CTTblWidth[] getTblCellSpacingArray();
    
    CTTblWidth getTblCellSpacingArray(final int p0);
    
    int sizeOfTblCellSpacingArray();
    
    void setTblCellSpacingArray(final CTTblWidth[] p0);
    
    void setTblCellSpacingArray(final int p0, final CTTblWidth p1);
    
    CTTblWidth insertNewTblCellSpacing(final int p0);
    
    CTTblWidth addNewTblCellSpacing();
    
    void removeTblCellSpacing(final int p0);
    
    List<CTJc> getJcList();
    
    @Deprecated
    CTJc[] getJcArray();
    
    CTJc getJcArray(final int p0);
    
    int sizeOfJcArray();
    
    void setJcArray(final CTJc[] p0);
    
    void setJcArray(final int p0, final CTJc p1);
    
    CTJc insertNewJc(final int p0);
    
    CTJc addNewJc();
    
    void removeJc(final int p0);
    
    List<CTOnOff> getHiddenList();
    
    @Deprecated
    CTOnOff[] getHiddenArray();
    
    CTOnOff getHiddenArray(final int p0);
    
    int sizeOfHiddenArray();
    
    void setHiddenArray(final CTOnOff[] p0);
    
    void setHiddenArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewHidden(final int p0);
    
    CTOnOff addNewHidden();
    
    void removeHidden(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTrPrBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTrPrBase newInstance() {
            return (CTTrPrBase)getTypeLoader().newInstance(CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase newInstance(final XmlOptions xmlOptions) {
            return (CTTrPrBase)getTypeLoader().newInstance(CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final String s) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(s, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(s, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final File file) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(file, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(file, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final URL url) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(url, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(url, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(inputStream, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(inputStream, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final Reader reader) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(reader, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPrBase)getTypeLoader().parse(reader, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(xmlStreamReader, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(xmlStreamReader, CTTrPrBase.type, xmlOptions);
        }
        
        public static CTTrPrBase parse(final Node node) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(node, CTTrPrBase.type, (XmlOptions)null);
        }
        
        public static CTTrPrBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPrBase)getTypeLoader().parse(node, CTTrPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTrPrBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTrPrBase)getTypeLoader().parse(xmlInputStream, CTTrPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTrPrBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTrPrBase)getTypeLoader().parse(xmlInputStream, CTTrPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrPrBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
