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
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSup;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubSup;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSub;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSPre;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTRad;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTPhant;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTNary;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTM;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimUpp;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimLow;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTGroupChr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTFunc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTF;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTEqArr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTD;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBorderBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBar;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTAcc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import java.util.List;
import org.apache.xmlbeans.SchemaType;

public interface CTRunTrackChange extends CTTrackChange
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRunTrackChange.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctruntrackchangea458type");
    
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
    
    List<CTAcc> getAccList();
    
    @Deprecated
    CTAcc[] getAccArray();
    
    CTAcc getAccArray(final int p0);
    
    int sizeOfAccArray();
    
    void setAccArray(final CTAcc[] p0);
    
    void setAccArray(final int p0, final CTAcc p1);
    
    CTAcc insertNewAcc(final int p0);
    
    CTAcc addNewAcc();
    
    void removeAcc(final int p0);
    
    List<CTBar> getBarList();
    
    @Deprecated
    CTBar[] getBarArray();
    
    CTBar getBarArray(final int p0);
    
    int sizeOfBarArray();
    
    void setBarArray(final CTBar[] p0);
    
    void setBarArray(final int p0, final CTBar p1);
    
    CTBar insertNewBar(final int p0);
    
    CTBar addNewBar();
    
    void removeBar(final int p0);
    
    List<CTBox> getBoxList();
    
    @Deprecated
    CTBox[] getBoxArray();
    
    CTBox getBoxArray(final int p0);
    
    int sizeOfBoxArray();
    
    void setBoxArray(final CTBox[] p0);
    
    void setBoxArray(final int p0, final CTBox p1);
    
    CTBox insertNewBox(final int p0);
    
    CTBox addNewBox();
    
    void removeBox(final int p0);
    
    List<CTBorderBox> getBorderBoxList();
    
    @Deprecated
    CTBorderBox[] getBorderBoxArray();
    
    CTBorderBox getBorderBoxArray(final int p0);
    
    int sizeOfBorderBoxArray();
    
    void setBorderBoxArray(final CTBorderBox[] p0);
    
    void setBorderBoxArray(final int p0, final CTBorderBox p1);
    
    CTBorderBox insertNewBorderBox(final int p0);
    
    CTBorderBox addNewBorderBox();
    
    void removeBorderBox(final int p0);
    
    List<CTD> getDList();
    
    @Deprecated
    CTD[] getDArray();
    
    CTD getDArray(final int p0);
    
    int sizeOfDArray();
    
    void setDArray(final CTD[] p0);
    
    void setDArray(final int p0, final CTD p1);
    
    CTD insertNewD(final int p0);
    
    CTD addNewD();
    
    void removeD(final int p0);
    
    List<CTEqArr> getEqArrList();
    
    @Deprecated
    CTEqArr[] getEqArrArray();
    
    CTEqArr getEqArrArray(final int p0);
    
    int sizeOfEqArrArray();
    
    void setEqArrArray(final CTEqArr[] p0);
    
    void setEqArrArray(final int p0, final CTEqArr p1);
    
    CTEqArr insertNewEqArr(final int p0);
    
    CTEqArr addNewEqArr();
    
    void removeEqArr(final int p0);
    
    List<CTF> getFList();
    
    @Deprecated
    CTF[] getFArray();
    
    CTF getFArray(final int p0);
    
    int sizeOfFArray();
    
    void setFArray(final CTF[] p0);
    
    void setFArray(final int p0, final CTF p1);
    
    CTF insertNewF(final int p0);
    
    CTF addNewF();
    
    void removeF(final int p0);
    
    List<CTFunc> getFuncList();
    
    @Deprecated
    CTFunc[] getFuncArray();
    
    CTFunc getFuncArray(final int p0);
    
    int sizeOfFuncArray();
    
    void setFuncArray(final CTFunc[] p0);
    
    void setFuncArray(final int p0, final CTFunc p1);
    
    CTFunc insertNewFunc(final int p0);
    
    CTFunc addNewFunc();
    
    void removeFunc(final int p0);
    
    List<CTGroupChr> getGroupChrList();
    
    @Deprecated
    CTGroupChr[] getGroupChrArray();
    
    CTGroupChr getGroupChrArray(final int p0);
    
    int sizeOfGroupChrArray();
    
    void setGroupChrArray(final CTGroupChr[] p0);
    
    void setGroupChrArray(final int p0, final CTGroupChr p1);
    
    CTGroupChr insertNewGroupChr(final int p0);
    
    CTGroupChr addNewGroupChr();
    
    void removeGroupChr(final int p0);
    
    List<CTLimLow> getLimLowList();
    
    @Deprecated
    CTLimLow[] getLimLowArray();
    
    CTLimLow getLimLowArray(final int p0);
    
    int sizeOfLimLowArray();
    
    void setLimLowArray(final CTLimLow[] p0);
    
    void setLimLowArray(final int p0, final CTLimLow p1);
    
    CTLimLow insertNewLimLow(final int p0);
    
    CTLimLow addNewLimLow();
    
    void removeLimLow(final int p0);
    
    List<CTLimUpp> getLimUppList();
    
    @Deprecated
    CTLimUpp[] getLimUppArray();
    
    CTLimUpp getLimUppArray(final int p0);
    
    int sizeOfLimUppArray();
    
    void setLimUppArray(final CTLimUpp[] p0);
    
    void setLimUppArray(final int p0, final CTLimUpp p1);
    
    CTLimUpp insertNewLimUpp(final int p0);
    
    CTLimUpp addNewLimUpp();
    
    void removeLimUpp(final int p0);
    
    List<CTM> getMList();
    
    @Deprecated
    CTM[] getMArray();
    
    CTM getMArray(final int p0);
    
    int sizeOfMArray();
    
    void setMArray(final CTM[] p0);
    
    void setMArray(final int p0, final CTM p1);
    
    CTM insertNewM(final int p0);
    
    CTM addNewM();
    
    void removeM(final int p0);
    
    List<CTNary> getNaryList();
    
    @Deprecated
    CTNary[] getNaryArray();
    
    CTNary getNaryArray(final int p0);
    
    int sizeOfNaryArray();
    
    void setNaryArray(final CTNary[] p0);
    
    void setNaryArray(final int p0, final CTNary p1);
    
    CTNary insertNewNary(final int p0);
    
    CTNary addNewNary();
    
    void removeNary(final int p0);
    
    List<CTPhant> getPhantList();
    
    @Deprecated
    CTPhant[] getPhantArray();
    
    CTPhant getPhantArray(final int p0);
    
    int sizeOfPhantArray();
    
    void setPhantArray(final CTPhant[] p0);
    
    void setPhantArray(final int p0, final CTPhant p1);
    
    CTPhant insertNewPhant(final int p0);
    
    CTPhant addNewPhant();
    
    void removePhant(final int p0);
    
    List<CTRad> getRadList();
    
    @Deprecated
    CTRad[] getRadArray();
    
    CTRad getRadArray(final int p0);
    
    int sizeOfRadArray();
    
    void setRadArray(final CTRad[] p0);
    
    void setRadArray(final int p0, final CTRad p1);
    
    CTRad insertNewRad(final int p0);
    
    CTRad addNewRad();
    
    void removeRad(final int p0);
    
    List<CTSPre> getSPreList();
    
    @Deprecated
    CTSPre[] getSPreArray();
    
    CTSPre getSPreArray(final int p0);
    
    int sizeOfSPreArray();
    
    void setSPreArray(final CTSPre[] p0);
    
    void setSPreArray(final int p0, final CTSPre p1);
    
    CTSPre insertNewSPre(final int p0);
    
    CTSPre addNewSPre();
    
    void removeSPre(final int p0);
    
    List<CTSSub> getSSubList();
    
    @Deprecated
    CTSSub[] getSSubArray();
    
    CTSSub getSSubArray(final int p0);
    
    int sizeOfSSubArray();
    
    void setSSubArray(final CTSSub[] p0);
    
    void setSSubArray(final int p0, final CTSSub p1);
    
    CTSSub insertNewSSub(final int p0);
    
    CTSSub addNewSSub();
    
    void removeSSub(final int p0);
    
    List<CTSSubSup> getSSubSupList();
    
    @Deprecated
    CTSSubSup[] getSSubSupArray();
    
    CTSSubSup getSSubSupArray(final int p0);
    
    int sizeOfSSubSupArray();
    
    void setSSubSupArray(final CTSSubSup[] p0);
    
    void setSSubSupArray(final int p0, final CTSSubSup p1);
    
    CTSSubSup insertNewSSubSup(final int p0);
    
    CTSSubSup addNewSSubSup();
    
    void removeSSubSup(final int p0);
    
    List<CTSSup> getSSupList();
    
    @Deprecated
    CTSSup[] getSSupArray();
    
    CTSSup getSSupArray(final int p0);
    
    int sizeOfSSupArray();
    
    void setSSupArray(final CTSSup[] p0);
    
    void setSSupArray(final int p0, final CTSSup p1);
    
    CTSSup insertNewSSup(final int p0);
    
    CTSSup addNewSSup();
    
    void removeSSup(final int p0);
    
    List<org.openxmlformats.schemas.officeDocument.x2006.math.CTR> getR2List();
    
    @Deprecated
    org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] getR2Array();
    
    org.openxmlformats.schemas.officeDocument.x2006.math.CTR getR2Array(final int p0);
    
    int sizeOfR2Array();
    
    void setR2Array(final org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] p0);
    
    void setR2Array(final int p0, final org.openxmlformats.schemas.officeDocument.x2006.math.CTR p1);
    
    org.openxmlformats.schemas.officeDocument.x2006.math.CTR insertNewR2(final int p0);
    
    org.openxmlformats.schemas.officeDocument.x2006.math.CTR addNewR2();
    
    void removeR2(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRunTrackChange.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRunTrackChange newInstance() {
            return (CTRunTrackChange)getTypeLoader().newInstance(CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange newInstance(final XmlOptions xmlOptions) {
            return (CTRunTrackChange)getTypeLoader().newInstance(CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final String s) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(s, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(s, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final File file) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(file, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(file, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final URL url) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(url, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(url, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(inputStream, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(inputStream, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final Reader reader) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(reader, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRunTrackChange)getTypeLoader().parse(reader, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(xmlStreamReader, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(xmlStreamReader, CTRunTrackChange.type, xmlOptions);
        }
        
        public static CTRunTrackChange parse(final Node node) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(node, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        public static CTRunTrackChange parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRunTrackChange)getTypeLoader().parse(node, CTRunTrackChange.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRunTrackChange parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRunTrackChange)getTypeLoader().parse(xmlInputStream, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRunTrackChange parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRunTrackChange)getTypeLoader().parse(xmlInputStream, CTRunTrackChange.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRunTrackChange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRunTrackChange.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
