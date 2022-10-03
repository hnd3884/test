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

public interface CTR extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTR.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctr8120type");
    
    CTRPr getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTRPr p0);
    
    CTRPr addNewRPr();
    
    void unsetRPr();
    
    List<CTBr> getBrList();
    
    @Deprecated
    CTBr[] getBrArray();
    
    CTBr getBrArray(final int p0);
    
    int sizeOfBrArray();
    
    void setBrArray(final CTBr[] p0);
    
    void setBrArray(final int p0, final CTBr p1);
    
    CTBr insertNewBr(final int p0);
    
    CTBr addNewBr();
    
    void removeBr(final int p0);
    
    List<CTText> getTList();
    
    @Deprecated
    CTText[] getTArray();
    
    CTText getTArray(final int p0);
    
    int sizeOfTArray();
    
    void setTArray(final CTText[] p0);
    
    void setTArray(final int p0, final CTText p1);
    
    CTText insertNewT(final int p0);
    
    CTText addNewT();
    
    void removeT(final int p0);
    
    List<CTText> getDelTextList();
    
    @Deprecated
    CTText[] getDelTextArray();
    
    CTText getDelTextArray(final int p0);
    
    int sizeOfDelTextArray();
    
    void setDelTextArray(final CTText[] p0);
    
    void setDelTextArray(final int p0, final CTText p1);
    
    CTText insertNewDelText(final int p0);
    
    CTText addNewDelText();
    
    void removeDelText(final int p0);
    
    List<CTText> getInstrTextList();
    
    @Deprecated
    CTText[] getInstrTextArray();
    
    CTText getInstrTextArray(final int p0);
    
    int sizeOfInstrTextArray();
    
    void setInstrTextArray(final CTText[] p0);
    
    void setInstrTextArray(final int p0, final CTText p1);
    
    CTText insertNewInstrText(final int p0);
    
    CTText addNewInstrText();
    
    void removeInstrText(final int p0);
    
    List<CTText> getDelInstrTextList();
    
    @Deprecated
    CTText[] getDelInstrTextArray();
    
    CTText getDelInstrTextArray(final int p0);
    
    int sizeOfDelInstrTextArray();
    
    void setDelInstrTextArray(final CTText[] p0);
    
    void setDelInstrTextArray(final int p0, final CTText p1);
    
    CTText insertNewDelInstrText(final int p0);
    
    CTText addNewDelInstrText();
    
    void removeDelInstrText(final int p0);
    
    List<CTEmpty> getNoBreakHyphenList();
    
    @Deprecated
    CTEmpty[] getNoBreakHyphenArray();
    
    CTEmpty getNoBreakHyphenArray(final int p0);
    
    int sizeOfNoBreakHyphenArray();
    
    void setNoBreakHyphenArray(final CTEmpty[] p0);
    
    void setNoBreakHyphenArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewNoBreakHyphen(final int p0);
    
    CTEmpty addNewNoBreakHyphen();
    
    void removeNoBreakHyphen(final int p0);
    
    List<CTEmpty> getSoftHyphenList();
    
    @Deprecated
    CTEmpty[] getSoftHyphenArray();
    
    CTEmpty getSoftHyphenArray(final int p0);
    
    int sizeOfSoftHyphenArray();
    
    void setSoftHyphenArray(final CTEmpty[] p0);
    
    void setSoftHyphenArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewSoftHyphen(final int p0);
    
    CTEmpty addNewSoftHyphen();
    
    void removeSoftHyphen(final int p0);
    
    List<CTEmpty> getDayShortList();
    
    @Deprecated
    CTEmpty[] getDayShortArray();
    
    CTEmpty getDayShortArray(final int p0);
    
    int sizeOfDayShortArray();
    
    void setDayShortArray(final CTEmpty[] p0);
    
    void setDayShortArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewDayShort(final int p0);
    
    CTEmpty addNewDayShort();
    
    void removeDayShort(final int p0);
    
    List<CTEmpty> getMonthShortList();
    
    @Deprecated
    CTEmpty[] getMonthShortArray();
    
    CTEmpty getMonthShortArray(final int p0);
    
    int sizeOfMonthShortArray();
    
    void setMonthShortArray(final CTEmpty[] p0);
    
    void setMonthShortArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewMonthShort(final int p0);
    
    CTEmpty addNewMonthShort();
    
    void removeMonthShort(final int p0);
    
    List<CTEmpty> getYearShortList();
    
    @Deprecated
    CTEmpty[] getYearShortArray();
    
    CTEmpty getYearShortArray(final int p0);
    
    int sizeOfYearShortArray();
    
    void setYearShortArray(final CTEmpty[] p0);
    
    void setYearShortArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewYearShort(final int p0);
    
    CTEmpty addNewYearShort();
    
    void removeYearShort(final int p0);
    
    List<CTEmpty> getDayLongList();
    
    @Deprecated
    CTEmpty[] getDayLongArray();
    
    CTEmpty getDayLongArray(final int p0);
    
    int sizeOfDayLongArray();
    
    void setDayLongArray(final CTEmpty[] p0);
    
    void setDayLongArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewDayLong(final int p0);
    
    CTEmpty addNewDayLong();
    
    void removeDayLong(final int p0);
    
    List<CTEmpty> getMonthLongList();
    
    @Deprecated
    CTEmpty[] getMonthLongArray();
    
    CTEmpty getMonthLongArray(final int p0);
    
    int sizeOfMonthLongArray();
    
    void setMonthLongArray(final CTEmpty[] p0);
    
    void setMonthLongArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewMonthLong(final int p0);
    
    CTEmpty addNewMonthLong();
    
    void removeMonthLong(final int p0);
    
    List<CTEmpty> getYearLongList();
    
    @Deprecated
    CTEmpty[] getYearLongArray();
    
    CTEmpty getYearLongArray(final int p0);
    
    int sizeOfYearLongArray();
    
    void setYearLongArray(final CTEmpty[] p0);
    
    void setYearLongArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewYearLong(final int p0);
    
    CTEmpty addNewYearLong();
    
    void removeYearLong(final int p0);
    
    List<CTEmpty> getAnnotationRefList();
    
    @Deprecated
    CTEmpty[] getAnnotationRefArray();
    
    CTEmpty getAnnotationRefArray(final int p0);
    
    int sizeOfAnnotationRefArray();
    
    void setAnnotationRefArray(final CTEmpty[] p0);
    
    void setAnnotationRefArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewAnnotationRef(final int p0);
    
    CTEmpty addNewAnnotationRef();
    
    void removeAnnotationRef(final int p0);
    
    List<CTEmpty> getFootnoteRefList();
    
    @Deprecated
    CTEmpty[] getFootnoteRefArray();
    
    CTEmpty getFootnoteRefArray(final int p0);
    
    int sizeOfFootnoteRefArray();
    
    void setFootnoteRefArray(final CTEmpty[] p0);
    
    void setFootnoteRefArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewFootnoteRef(final int p0);
    
    CTEmpty addNewFootnoteRef();
    
    void removeFootnoteRef(final int p0);
    
    List<CTEmpty> getEndnoteRefList();
    
    @Deprecated
    CTEmpty[] getEndnoteRefArray();
    
    CTEmpty getEndnoteRefArray(final int p0);
    
    int sizeOfEndnoteRefArray();
    
    void setEndnoteRefArray(final CTEmpty[] p0);
    
    void setEndnoteRefArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewEndnoteRef(final int p0);
    
    CTEmpty addNewEndnoteRef();
    
    void removeEndnoteRef(final int p0);
    
    List<CTEmpty> getSeparatorList();
    
    @Deprecated
    CTEmpty[] getSeparatorArray();
    
    CTEmpty getSeparatorArray(final int p0);
    
    int sizeOfSeparatorArray();
    
    void setSeparatorArray(final CTEmpty[] p0);
    
    void setSeparatorArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewSeparator(final int p0);
    
    CTEmpty addNewSeparator();
    
    void removeSeparator(final int p0);
    
    List<CTEmpty> getContinuationSeparatorList();
    
    @Deprecated
    CTEmpty[] getContinuationSeparatorArray();
    
    CTEmpty getContinuationSeparatorArray(final int p0);
    
    int sizeOfContinuationSeparatorArray();
    
    void setContinuationSeparatorArray(final CTEmpty[] p0);
    
    void setContinuationSeparatorArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewContinuationSeparator(final int p0);
    
    CTEmpty addNewContinuationSeparator();
    
    void removeContinuationSeparator(final int p0);
    
    List<CTSym> getSymList();
    
    @Deprecated
    CTSym[] getSymArray();
    
    CTSym getSymArray(final int p0);
    
    int sizeOfSymArray();
    
    void setSymArray(final CTSym[] p0);
    
    void setSymArray(final int p0, final CTSym p1);
    
    CTSym insertNewSym(final int p0);
    
    CTSym addNewSym();
    
    void removeSym(final int p0);
    
    List<CTEmpty> getPgNumList();
    
    @Deprecated
    CTEmpty[] getPgNumArray();
    
    CTEmpty getPgNumArray(final int p0);
    
    int sizeOfPgNumArray();
    
    void setPgNumArray(final CTEmpty[] p0);
    
    void setPgNumArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewPgNum(final int p0);
    
    CTEmpty addNewPgNum();
    
    void removePgNum(final int p0);
    
    List<CTEmpty> getCrList();
    
    @Deprecated
    CTEmpty[] getCrArray();
    
    CTEmpty getCrArray(final int p0);
    
    int sizeOfCrArray();
    
    void setCrArray(final CTEmpty[] p0);
    
    void setCrArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewCr(final int p0);
    
    CTEmpty addNewCr();
    
    void removeCr(final int p0);
    
    List<CTEmpty> getTabList();
    
    @Deprecated
    CTEmpty[] getTabArray();
    
    CTEmpty getTabArray(final int p0);
    
    int sizeOfTabArray();
    
    void setTabArray(final CTEmpty[] p0);
    
    void setTabArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewTab(final int p0);
    
    CTEmpty addNewTab();
    
    void removeTab(final int p0);
    
    List<CTObject> getObjectList();
    
    @Deprecated
    CTObject[] getObjectArray();
    
    CTObject getObjectArray(final int p0);
    
    int sizeOfObjectArray();
    
    void setObjectArray(final CTObject[] p0);
    
    void setObjectArray(final int p0, final CTObject p1);
    
    CTObject insertNewObject(final int p0);
    
    CTObject addNewObject();
    
    void removeObject(final int p0);
    
    List<CTPicture> getPictList();
    
    @Deprecated
    CTPicture[] getPictArray();
    
    CTPicture getPictArray(final int p0);
    
    int sizeOfPictArray();
    
    void setPictArray(final CTPicture[] p0);
    
    void setPictArray(final int p0, final CTPicture p1);
    
    CTPicture insertNewPict(final int p0);
    
    CTPicture addNewPict();
    
    void removePict(final int p0);
    
    List<CTFldChar> getFldCharList();
    
    @Deprecated
    CTFldChar[] getFldCharArray();
    
    CTFldChar getFldCharArray(final int p0);
    
    int sizeOfFldCharArray();
    
    void setFldCharArray(final CTFldChar[] p0);
    
    void setFldCharArray(final int p0, final CTFldChar p1);
    
    CTFldChar insertNewFldChar(final int p0);
    
    CTFldChar addNewFldChar();
    
    void removeFldChar(final int p0);
    
    List<CTRuby> getRubyList();
    
    @Deprecated
    CTRuby[] getRubyArray();
    
    CTRuby getRubyArray(final int p0);
    
    int sizeOfRubyArray();
    
    void setRubyArray(final CTRuby[] p0);
    
    void setRubyArray(final int p0, final CTRuby p1);
    
    CTRuby insertNewRuby(final int p0);
    
    CTRuby addNewRuby();
    
    void removeRuby(final int p0);
    
    List<CTFtnEdnRef> getFootnoteReferenceList();
    
    @Deprecated
    CTFtnEdnRef[] getFootnoteReferenceArray();
    
    CTFtnEdnRef getFootnoteReferenceArray(final int p0);
    
    int sizeOfFootnoteReferenceArray();
    
    void setFootnoteReferenceArray(final CTFtnEdnRef[] p0);
    
    void setFootnoteReferenceArray(final int p0, final CTFtnEdnRef p1);
    
    CTFtnEdnRef insertNewFootnoteReference(final int p0);
    
    CTFtnEdnRef addNewFootnoteReference();
    
    void removeFootnoteReference(final int p0);
    
    List<CTFtnEdnRef> getEndnoteReferenceList();
    
    @Deprecated
    CTFtnEdnRef[] getEndnoteReferenceArray();
    
    CTFtnEdnRef getEndnoteReferenceArray(final int p0);
    
    int sizeOfEndnoteReferenceArray();
    
    void setEndnoteReferenceArray(final CTFtnEdnRef[] p0);
    
    void setEndnoteReferenceArray(final int p0, final CTFtnEdnRef p1);
    
    CTFtnEdnRef insertNewEndnoteReference(final int p0);
    
    CTFtnEdnRef addNewEndnoteReference();
    
    void removeEndnoteReference(final int p0);
    
    List<CTMarkup> getCommentReferenceList();
    
    @Deprecated
    CTMarkup[] getCommentReferenceArray();
    
    CTMarkup getCommentReferenceArray(final int p0);
    
    int sizeOfCommentReferenceArray();
    
    void setCommentReferenceArray(final CTMarkup[] p0);
    
    void setCommentReferenceArray(final int p0, final CTMarkup p1);
    
    CTMarkup insertNewCommentReference(final int p0);
    
    CTMarkup addNewCommentReference();
    
    void removeCommentReference(final int p0);
    
    List<CTDrawing> getDrawingList();
    
    @Deprecated
    CTDrawing[] getDrawingArray();
    
    CTDrawing getDrawingArray(final int p0);
    
    int sizeOfDrawingArray();
    
    void setDrawingArray(final CTDrawing[] p0);
    
    void setDrawingArray(final int p0, final CTDrawing p1);
    
    CTDrawing insertNewDrawing(final int p0);
    
    CTDrawing addNewDrawing();
    
    void removeDrawing(final int p0);
    
    List<CTPTab> getPtabList();
    
    @Deprecated
    CTPTab[] getPtabArray();
    
    CTPTab getPtabArray(final int p0);
    
    int sizeOfPtabArray();
    
    void setPtabArray(final CTPTab[] p0);
    
    void setPtabArray(final int p0, final CTPTab p1);
    
    CTPTab insertNewPtab(final int p0);
    
    CTPTab addNewPtab();
    
    void removePtab(final int p0);
    
    List<CTEmpty> getLastRenderedPageBreakList();
    
    @Deprecated
    CTEmpty[] getLastRenderedPageBreakArray();
    
    CTEmpty getLastRenderedPageBreakArray(final int p0);
    
    int sizeOfLastRenderedPageBreakArray();
    
    void setLastRenderedPageBreakArray(final CTEmpty[] p0);
    
    void setLastRenderedPageBreakArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewLastRenderedPageBreak(final int p0);
    
    CTEmpty addNewLastRenderedPageBreak();
    
    void removeLastRenderedPageBreak(final int p0);
    
    byte[] getRsidRPr();
    
    STLongHexNumber xgetRsidRPr();
    
    boolean isSetRsidRPr();
    
    void setRsidRPr(final byte[] p0);
    
    void xsetRsidRPr(final STLongHexNumber p0);
    
    void unsetRsidRPr();
    
    byte[] getRsidDel();
    
    STLongHexNumber xgetRsidDel();
    
    boolean isSetRsidDel();
    
    void setRsidDel(final byte[] p0);
    
    void xsetRsidDel(final STLongHexNumber p0);
    
    void unsetRsidDel();
    
    byte[] getRsidR();
    
    STLongHexNumber xgetRsidR();
    
    boolean isSetRsidR();
    
    void setRsidR(final byte[] p0);
    
    void xsetRsidR(final STLongHexNumber p0);
    
    void unsetRsidR();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTR.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTR newInstance() {
            return (CTR)getTypeLoader().newInstance(CTR.type, (XmlOptions)null);
        }
        
        public static CTR newInstance(final XmlOptions xmlOptions) {
            return (CTR)getTypeLoader().newInstance(CTR.type, xmlOptions);
        }
        
        public static CTR parse(final String s) throws XmlException {
            return (CTR)getTypeLoader().parse(s, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTR)getTypeLoader().parse(s, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final File file) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(file, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(file, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final URL url) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(url, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(url, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(inputStream, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(inputStream, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final Reader reader) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(reader, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTR)getTypeLoader().parse(reader, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTR)getTypeLoader().parse(xmlStreamReader, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTR)getTypeLoader().parse(xmlStreamReader, CTR.type, xmlOptions);
        }
        
        public static CTR parse(final Node node) throws XmlException {
            return (CTR)getTypeLoader().parse(node, CTR.type, (XmlOptions)null);
        }
        
        public static CTR parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTR)getTypeLoader().parse(node, CTR.type, xmlOptions);
        }
        
        @Deprecated
        public static CTR parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTR)getTypeLoader().parse(xmlInputStream, CTR.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTR parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTR)getTypeLoader().parse(xmlInputStream, CTR.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTR.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTR.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
