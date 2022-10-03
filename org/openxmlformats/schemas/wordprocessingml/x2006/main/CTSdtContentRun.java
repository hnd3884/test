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
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSdtContentRun extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtContentRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtcontentruna0fdtype");
    
    List<CTCustomXmlRun> getCustomXmlList();
    
    @Deprecated
    CTCustomXmlRun[] getCustomXmlArray();
    
    CTCustomXmlRun getCustomXmlArray(final int p0);
    
    int sizeOfCustomXmlArray();
    
    void setCustomXmlArray(final CTCustomXmlRun[] p0);
    
    void setCustomXmlArray(final int p0, final CTCustomXmlRun p1);
    
    CTCustomXmlRun insertNewCustomXml(final int p0);
    
    CTCustomXmlRun addNewCustomXml();
    
    void removeCustomXml(final int p0);
    
    List<CTSmartTagRun> getSmartTagList();
    
    @Deprecated
    CTSmartTagRun[] getSmartTagArray();
    
    CTSmartTagRun getSmartTagArray(final int p0);
    
    int sizeOfSmartTagArray();
    
    void setSmartTagArray(final CTSmartTagRun[] p0);
    
    void setSmartTagArray(final int p0, final CTSmartTagRun p1);
    
    CTSmartTagRun insertNewSmartTag(final int p0);
    
    CTSmartTagRun addNewSmartTag();
    
    void removeSmartTag(final int p0);
    
    List<CTSdtRun> getSdtList();
    
    @Deprecated
    CTSdtRun[] getSdtArray();
    
    CTSdtRun getSdtArray(final int p0);
    
    int sizeOfSdtArray();
    
    void setSdtArray(final CTSdtRun[] p0);
    
    void setSdtArray(final int p0, final CTSdtRun p1);
    
    CTSdtRun insertNewSdt(final int p0);
    
    CTSdtRun addNewSdt();
    
    void removeSdt(final int p0);
    
    List<CTR> getRList();
    
    @Deprecated
    CTR[] getRArray();
    
    CTR getRArray(final int p0);
    
    int sizeOfRArray();
    
    void setRArray(final CTR[] p0);
    
    void setRArray(final int p0, final CTR p1);
    
    CTR insertNewR(final int p0);
    
    CTR addNewR();
    
    void removeR(final int p0);
    
    List<CTProofErr> getProofErrList();
    
    @Deprecated
    CTProofErr[] getProofErrArray();
    
    CTProofErr getProofErrArray(final int p0);
    
    int sizeOfProofErrArray();
    
    void setProofErrArray(final CTProofErr[] p0);
    
    void setProofErrArray(final int p0, final CTProofErr p1);
    
    CTProofErr insertNewProofErr(final int p0);
    
    CTProofErr addNewProofErr();
    
    void removeProofErr(final int p0);
    
    List<CTPermStart> getPermStartList();
    
    @Deprecated
    CTPermStart[] getPermStartArray();
    
    CTPermStart getPermStartArray(final int p0);
    
    int sizeOfPermStartArray();
    
    void setPermStartArray(final CTPermStart[] p0);
    
    void setPermStartArray(final int p0, final CTPermStart p1);
    
    CTPermStart insertNewPermStart(final int p0);
    
    CTPermStart addNewPermStart();
    
    void removePermStart(final int p0);
    
    List<CTPerm> getPermEndList();
    
    @Deprecated
    CTPerm[] getPermEndArray();
    
    CTPerm getPermEndArray(final int p0);
    
    int sizeOfPermEndArray();
    
    void setPermEndArray(final CTPerm[] p0);
    
    void setPermEndArray(final int p0, final CTPerm p1);
    
    CTPerm insertNewPermEnd(final int p0);
    
    CTPerm addNewPermEnd();
    
    void removePermEnd(final int p0);
    
    List<CTBookmark> getBookmarkStartList();
    
    @Deprecated
    CTBookmark[] getBookmarkStartArray();
    
    CTBookmark getBookmarkStartArray(final int p0);
    
    int sizeOfBookmarkStartArray();
    
    void setBookmarkStartArray(final CTBookmark[] p0);
    
    void setBookmarkStartArray(final int p0, final CTBookmark p1);
    
    CTBookmark insertNewBookmarkStart(final int p0);
    
    CTBookmark addNewBookmarkStart();
    
    void removeBookmarkStart(final int p0);
    
    List<CTMarkupRange> getBookmarkEndList();
    
    @Deprecated
    CTMarkupRange[] getBookmarkEndArray();
    
    CTMarkupRange getBookmarkEndArray(final int p0);
    
    int sizeOfBookmarkEndArray();
    
    void setBookmarkEndArray(final CTMarkupRange[] p0);
    
    void setBookmarkEndArray(final int p0, final CTMarkupRange p1);
    
    CTMarkupRange insertNewBookmarkEnd(final int p0);
    
    CTMarkupRange addNewBookmarkEnd();
    
    void removeBookmarkEnd(final int p0);
    
    List<CTMoveBookmark> getMoveFromRangeStartList();
    
    @Deprecated
    CTMoveBookmark[] getMoveFromRangeStartArray();
    
    CTMoveBookmark getMoveFromRangeStartArray(final int p0);
    
    int sizeOfMoveFromRangeStartArray();
    
    void setMoveFromRangeStartArray(final CTMoveBookmark[] p0);
    
    void setMoveFromRangeStartArray(final int p0, final CTMoveBookmark p1);
    
    CTMoveBookmark insertNewMoveFromRangeStart(final int p0);
    
    CTMoveBookmark addNewMoveFromRangeStart();
    
    void removeMoveFromRangeStart(final int p0);
    
    List<CTMarkupRange> getMoveFromRangeEndList();
    
    @Deprecated
    CTMarkupRange[] getMoveFromRangeEndArray();
    
    CTMarkupRange getMoveFromRangeEndArray(final int p0);
    
    int sizeOfMoveFromRangeEndArray();
    
    void setMoveFromRangeEndArray(final CTMarkupRange[] p0);
    
    void setMoveFromRangeEndArray(final int p0, final CTMarkupRange p1);
    
    CTMarkupRange insertNewMoveFromRangeEnd(final int p0);
    
    CTMarkupRange addNewMoveFromRangeEnd();
    
    void removeMoveFromRangeEnd(final int p0);
    
    List<CTMoveBookmark> getMoveToRangeStartList();
    
    @Deprecated
    CTMoveBookmark[] getMoveToRangeStartArray();
    
    CTMoveBookmark getMoveToRangeStartArray(final int p0);
    
    int sizeOfMoveToRangeStartArray();
    
    void setMoveToRangeStartArray(final CTMoveBookmark[] p0);
    
    void setMoveToRangeStartArray(final int p0, final CTMoveBookmark p1);
    
    CTMoveBookmark insertNewMoveToRangeStart(final int p0);
    
    CTMoveBookmark addNewMoveToRangeStart();
    
    void removeMoveToRangeStart(final int p0);
    
    List<CTMarkupRange> getMoveToRangeEndList();
    
    @Deprecated
    CTMarkupRange[] getMoveToRangeEndArray();
    
    CTMarkupRange getMoveToRangeEndArray(final int p0);
    
    int sizeOfMoveToRangeEndArray();
    
    void setMoveToRangeEndArray(final CTMarkupRange[] p0);
    
    void setMoveToRangeEndArray(final int p0, final CTMarkupRange p1);
    
    CTMarkupRange insertNewMoveToRangeEnd(final int p0);
    
    CTMarkupRange addNewMoveToRangeEnd();
    
    void removeMoveToRangeEnd(final int p0);
    
    List<CTMarkupRange> getCommentRangeStartList();
    
    @Deprecated
    CTMarkupRange[] getCommentRangeStartArray();
    
    CTMarkupRange getCommentRangeStartArray(final int p0);
    
    int sizeOfCommentRangeStartArray();
    
    void setCommentRangeStartArray(final CTMarkupRange[] p0);
    
    void setCommentRangeStartArray(final int p0, final CTMarkupRange p1);
    
    CTMarkupRange insertNewCommentRangeStart(final int p0);
    
    CTMarkupRange addNewCommentRangeStart();
    
    void removeCommentRangeStart(final int p0);
    
    List<CTMarkupRange> getCommentRangeEndList();
    
    @Deprecated
    CTMarkupRange[] getCommentRangeEndArray();
    
    CTMarkupRange getCommentRangeEndArray(final int p0);
    
    int sizeOfCommentRangeEndArray();
    
    void setCommentRangeEndArray(final CTMarkupRange[] p0);
    
    void setCommentRangeEndArray(final int p0, final CTMarkupRange p1);
    
    CTMarkupRange insertNewCommentRangeEnd(final int p0);
    
    CTMarkupRange addNewCommentRangeEnd();
    
    void removeCommentRangeEnd(final int p0);
    
    List<CTTrackChange> getCustomXmlInsRangeStartList();
    
    @Deprecated
    CTTrackChange[] getCustomXmlInsRangeStartArray();
    
    CTTrackChange getCustomXmlInsRangeStartArray(final int p0);
    
    int sizeOfCustomXmlInsRangeStartArray();
    
    void setCustomXmlInsRangeStartArray(final CTTrackChange[] p0);
    
    void setCustomXmlInsRangeStartArray(final int p0, final CTTrackChange p1);
    
    CTTrackChange insertNewCustomXmlInsRangeStart(final int p0);
    
    CTTrackChange addNewCustomXmlInsRangeStart();
    
    void removeCustomXmlInsRangeStart(final int p0);
    
    List<CTMarkup> getCustomXmlInsRangeEndList();
    
    @Deprecated
    CTMarkup[] getCustomXmlInsRangeEndArray();
    
    CTMarkup getCustomXmlInsRangeEndArray(final int p0);
    
    int sizeOfCustomXmlInsRangeEndArray();
    
    void setCustomXmlInsRangeEndArray(final CTMarkup[] p0);
    
    void setCustomXmlInsRangeEndArray(final int p0, final CTMarkup p1);
    
    CTMarkup insertNewCustomXmlInsRangeEnd(final int p0);
    
    CTMarkup addNewCustomXmlInsRangeEnd();
    
    void removeCustomXmlInsRangeEnd(final int p0);
    
    List<CTTrackChange> getCustomXmlDelRangeStartList();
    
    @Deprecated
    CTTrackChange[] getCustomXmlDelRangeStartArray();
    
    CTTrackChange getCustomXmlDelRangeStartArray(final int p0);
    
    int sizeOfCustomXmlDelRangeStartArray();
    
    void setCustomXmlDelRangeStartArray(final CTTrackChange[] p0);
    
    void setCustomXmlDelRangeStartArray(final int p0, final CTTrackChange p1);
    
    CTTrackChange insertNewCustomXmlDelRangeStart(final int p0);
    
    CTTrackChange addNewCustomXmlDelRangeStart();
    
    void removeCustomXmlDelRangeStart(final int p0);
    
    List<CTMarkup> getCustomXmlDelRangeEndList();
    
    @Deprecated
    CTMarkup[] getCustomXmlDelRangeEndArray();
    
    CTMarkup getCustomXmlDelRangeEndArray(final int p0);
    
    int sizeOfCustomXmlDelRangeEndArray();
    
    void setCustomXmlDelRangeEndArray(final CTMarkup[] p0);
    
    void setCustomXmlDelRangeEndArray(final int p0, final CTMarkup p1);
    
    CTMarkup insertNewCustomXmlDelRangeEnd(final int p0);
    
    CTMarkup addNewCustomXmlDelRangeEnd();
    
    void removeCustomXmlDelRangeEnd(final int p0);
    
    List<CTTrackChange> getCustomXmlMoveFromRangeStartList();
    
    @Deprecated
    CTTrackChange[] getCustomXmlMoveFromRangeStartArray();
    
    CTTrackChange getCustomXmlMoveFromRangeStartArray(final int p0);
    
    int sizeOfCustomXmlMoveFromRangeStartArray();
    
    void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] p0);
    
    void setCustomXmlMoveFromRangeStartArray(final int p0, final CTTrackChange p1);
    
    CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int p0);
    
    CTTrackChange addNewCustomXmlMoveFromRangeStart();
    
    void removeCustomXmlMoveFromRangeStart(final int p0);
    
    List<CTMarkup> getCustomXmlMoveFromRangeEndList();
    
    @Deprecated
    CTMarkup[] getCustomXmlMoveFromRangeEndArray();
    
    CTMarkup getCustomXmlMoveFromRangeEndArray(final int p0);
    
    int sizeOfCustomXmlMoveFromRangeEndArray();
    
    void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] p0);
    
    void setCustomXmlMoveFromRangeEndArray(final int p0, final CTMarkup p1);
    
    CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int p0);
    
    CTMarkup addNewCustomXmlMoveFromRangeEnd();
    
    void removeCustomXmlMoveFromRangeEnd(final int p0);
    
    List<CTTrackChange> getCustomXmlMoveToRangeStartList();
    
    @Deprecated
    CTTrackChange[] getCustomXmlMoveToRangeStartArray();
    
    CTTrackChange getCustomXmlMoveToRangeStartArray(final int p0);
    
    int sizeOfCustomXmlMoveToRangeStartArray();
    
    void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] p0);
    
    void setCustomXmlMoveToRangeStartArray(final int p0, final CTTrackChange p1);
    
    CTTrackChange insertNewCustomXmlMoveToRangeStart(final int p0);
    
    CTTrackChange addNewCustomXmlMoveToRangeStart();
    
    void removeCustomXmlMoveToRangeStart(final int p0);
    
    List<CTMarkup> getCustomXmlMoveToRangeEndList();
    
    @Deprecated
    CTMarkup[] getCustomXmlMoveToRangeEndArray();
    
    CTMarkup getCustomXmlMoveToRangeEndArray(final int p0);
    
    int sizeOfCustomXmlMoveToRangeEndArray();
    
    void setCustomXmlMoveToRangeEndArray(final CTMarkup[] p0);
    
    void setCustomXmlMoveToRangeEndArray(final int p0, final CTMarkup p1);
    
    CTMarkup insertNewCustomXmlMoveToRangeEnd(final int p0);
    
    CTMarkup addNewCustomXmlMoveToRangeEnd();
    
    void removeCustomXmlMoveToRangeEnd(final int p0);
    
    List<CTRunTrackChange> getInsList();
    
    @Deprecated
    CTRunTrackChange[] getInsArray();
    
    CTRunTrackChange getInsArray(final int p0);
    
    int sizeOfInsArray();
    
    void setInsArray(final CTRunTrackChange[] p0);
    
    void setInsArray(final int p0, final CTRunTrackChange p1);
    
    CTRunTrackChange insertNewIns(final int p0);
    
    CTRunTrackChange addNewIns();
    
    void removeIns(final int p0);
    
    List<CTRunTrackChange> getDelList();
    
    @Deprecated
    CTRunTrackChange[] getDelArray();
    
    CTRunTrackChange getDelArray(final int p0);
    
    int sizeOfDelArray();
    
    void setDelArray(final CTRunTrackChange[] p0);
    
    void setDelArray(final int p0, final CTRunTrackChange p1);
    
    CTRunTrackChange insertNewDel(final int p0);
    
    CTRunTrackChange addNewDel();
    
    void removeDel(final int p0);
    
    List<CTRunTrackChange> getMoveFromList();
    
    @Deprecated
    CTRunTrackChange[] getMoveFromArray();
    
    CTRunTrackChange getMoveFromArray(final int p0);
    
    int sizeOfMoveFromArray();
    
    void setMoveFromArray(final CTRunTrackChange[] p0);
    
    void setMoveFromArray(final int p0, final CTRunTrackChange p1);
    
    CTRunTrackChange insertNewMoveFrom(final int p0);
    
    CTRunTrackChange addNewMoveFrom();
    
    void removeMoveFrom(final int p0);
    
    List<CTRunTrackChange> getMoveToList();
    
    @Deprecated
    CTRunTrackChange[] getMoveToArray();
    
    CTRunTrackChange getMoveToArray(final int p0);
    
    int sizeOfMoveToArray();
    
    void setMoveToArray(final CTRunTrackChange[] p0);
    
    void setMoveToArray(final int p0, final CTRunTrackChange p1);
    
    CTRunTrackChange insertNewMoveTo(final int p0);
    
    CTRunTrackChange addNewMoveTo();
    
    void removeMoveTo(final int p0);
    
    List<CTOMathPara> getOMathParaList();
    
    @Deprecated
    CTOMathPara[] getOMathParaArray();
    
    CTOMathPara getOMathParaArray(final int p0);
    
    int sizeOfOMathParaArray();
    
    void setOMathParaArray(final CTOMathPara[] p0);
    
    void setOMathParaArray(final int p0, final CTOMathPara p1);
    
    CTOMathPara insertNewOMathPara(final int p0);
    
    CTOMathPara addNewOMathPara();
    
    void removeOMathPara(final int p0);
    
    List<CTOMath> getOMathList();
    
    @Deprecated
    CTOMath[] getOMathArray();
    
    CTOMath getOMathArray(final int p0);
    
    int sizeOfOMathArray();
    
    void setOMathArray(final CTOMath[] p0);
    
    void setOMathArray(final int p0, final CTOMath p1);
    
    CTOMath insertNewOMath(final int p0);
    
    CTOMath addNewOMath();
    
    void removeOMath(final int p0);
    
    List<CTSimpleField> getFldSimpleList();
    
    @Deprecated
    CTSimpleField[] getFldSimpleArray();
    
    CTSimpleField getFldSimpleArray(final int p0);
    
    int sizeOfFldSimpleArray();
    
    void setFldSimpleArray(final CTSimpleField[] p0);
    
    void setFldSimpleArray(final int p0, final CTSimpleField p1);
    
    CTSimpleField insertNewFldSimple(final int p0);
    
    CTSimpleField addNewFldSimple();
    
    void removeFldSimple(final int p0);
    
    List<CTHyperlink> getHyperlinkList();
    
    @Deprecated
    CTHyperlink[] getHyperlinkArray();
    
    CTHyperlink getHyperlinkArray(final int p0);
    
    int sizeOfHyperlinkArray();
    
    void setHyperlinkArray(final CTHyperlink[] p0);
    
    void setHyperlinkArray(final int p0, final CTHyperlink p1);
    
    CTHyperlink insertNewHyperlink(final int p0);
    
    CTHyperlink addNewHyperlink();
    
    void removeHyperlink(final int p0);
    
    List<CTRel> getSubDocList();
    
    @Deprecated
    CTRel[] getSubDocArray();
    
    CTRel getSubDocArray(final int p0);
    
    int sizeOfSubDocArray();
    
    void setSubDocArray(final CTRel[] p0);
    
    void setSubDocArray(final int p0, final CTRel p1);
    
    CTRel insertNewSubDoc(final int p0);
    
    CTRel addNewSubDoc();
    
    void removeSubDoc(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtContentRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtContentRun newInstance() {
            return (CTSdtContentRun)getTypeLoader().newInstance(CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun newInstance(final XmlOptions xmlOptions) {
            return (CTSdtContentRun)getTypeLoader().newInstance(CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final String s) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(s, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(s, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final File file) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(file, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(file, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final URL url) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(url, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(url, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(inputStream, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(inputStream, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(reader, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtContentRun)getTypeLoader().parse(reader, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(xmlStreamReader, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(xmlStreamReader, CTSdtContentRun.type, xmlOptions);
        }
        
        public static CTSdtContentRun parse(final Node node) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(node, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        public static CTSdtContentRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtContentRun)getTypeLoader().parse(node, CTSdtContentRun.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtContentRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtContentRun)getTypeLoader().parse(xmlInputStream, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtContentRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtContentRun)getTypeLoader().parse(xmlInputStream, CTSdtContentRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtContentRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtContentRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
