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

public interface CTTxbxContent extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTxbxContent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttxbxcontent1133type");
    
    List<CTCustomXmlBlock> getCustomXmlList();
    
    @Deprecated
    CTCustomXmlBlock[] getCustomXmlArray();
    
    CTCustomXmlBlock getCustomXmlArray(final int p0);
    
    int sizeOfCustomXmlArray();
    
    void setCustomXmlArray(final CTCustomXmlBlock[] p0);
    
    void setCustomXmlArray(final int p0, final CTCustomXmlBlock p1);
    
    CTCustomXmlBlock insertNewCustomXml(final int p0);
    
    CTCustomXmlBlock addNewCustomXml();
    
    void removeCustomXml(final int p0);
    
    List<CTSdtBlock> getSdtList();
    
    @Deprecated
    CTSdtBlock[] getSdtArray();
    
    CTSdtBlock getSdtArray(final int p0);
    
    int sizeOfSdtArray();
    
    void setSdtArray(final CTSdtBlock[] p0);
    
    void setSdtArray(final int p0, final CTSdtBlock p1);
    
    CTSdtBlock insertNewSdt(final int p0);
    
    CTSdtBlock addNewSdt();
    
    void removeSdt(final int p0);
    
    List<CTP> getPList();
    
    @Deprecated
    CTP[] getPArray();
    
    CTP getPArray(final int p0);
    
    int sizeOfPArray();
    
    void setPArray(final CTP[] p0);
    
    void setPArray(final int p0, final CTP p1);
    
    CTP insertNewP(final int p0);
    
    CTP addNewP();
    
    void removeP(final int p0);
    
    List<CTTbl> getTblList();
    
    @Deprecated
    CTTbl[] getTblArray();
    
    CTTbl getTblArray(final int p0);
    
    int sizeOfTblArray();
    
    void setTblArray(final CTTbl[] p0);
    
    void setTblArray(final int p0, final CTTbl p1);
    
    CTTbl insertNewTbl(final int p0);
    
    CTTbl addNewTbl();
    
    void removeTbl(final int p0);
    
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
    
    List<CTAltChunk> getAltChunkList();
    
    @Deprecated
    CTAltChunk[] getAltChunkArray();
    
    CTAltChunk getAltChunkArray(final int p0);
    
    int sizeOfAltChunkArray();
    
    void setAltChunkArray(final CTAltChunk[] p0);
    
    void setAltChunkArray(final int p0, final CTAltChunk p1);
    
    CTAltChunk insertNewAltChunk(final int p0);
    
    CTAltChunk addNewAltChunk();
    
    void removeAltChunk(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTxbxContent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTxbxContent newInstance() {
            return (CTTxbxContent)getTypeLoader().newInstance(CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent newInstance(final XmlOptions xmlOptions) {
            return (CTTxbxContent)getTypeLoader().newInstance(CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final String s) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(s, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(s, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final File file) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(file, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(file, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final URL url) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(url, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(url, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(inputStream, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(inputStream, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final Reader reader) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(reader, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTxbxContent)getTypeLoader().parse(reader, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(xmlStreamReader, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(xmlStreamReader, CTTxbxContent.type, xmlOptions);
        }
        
        public static CTTxbxContent parse(final Node node) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(node, CTTxbxContent.type, (XmlOptions)null);
        }
        
        public static CTTxbxContent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTxbxContent)getTypeLoader().parse(node, CTTxbxContent.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTxbxContent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTxbxContent)getTypeLoader().parse(xmlInputStream, CTTxbxContent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTxbxContent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTxbxContent)getTypeLoader().parse(xmlInputStream, CTTxbxContent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTxbxContent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTxbxContent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
