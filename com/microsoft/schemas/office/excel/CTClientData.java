package com.microsoft.schemas.office.excel;

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
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTClientData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTClientData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctclientdata433btype");
    
    List<STTrueFalseBlank.Enum> getMoveWithCellsList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getMoveWithCellsArray();
    
    STTrueFalseBlank.Enum getMoveWithCellsArray(final int p0);
    
    List<STTrueFalseBlank> xgetMoveWithCellsList();
    
    @Deprecated
    STTrueFalseBlank[] xgetMoveWithCellsArray();
    
    STTrueFalseBlank xgetMoveWithCellsArray(final int p0);
    
    int sizeOfMoveWithCellsArray();
    
    void setMoveWithCellsArray(final STTrueFalseBlank.Enum[] p0);
    
    void setMoveWithCellsArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetMoveWithCellsArray(final STTrueFalseBlank[] p0);
    
    void xsetMoveWithCellsArray(final int p0, final STTrueFalseBlank p1);
    
    void insertMoveWithCells(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addMoveWithCells(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewMoveWithCells(final int p0);
    
    STTrueFalseBlank addNewMoveWithCells();
    
    void removeMoveWithCells(final int p0);
    
    List<STTrueFalseBlank.Enum> getSizeWithCellsList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getSizeWithCellsArray();
    
    STTrueFalseBlank.Enum getSizeWithCellsArray(final int p0);
    
    List<STTrueFalseBlank> xgetSizeWithCellsList();
    
    @Deprecated
    STTrueFalseBlank[] xgetSizeWithCellsArray();
    
    STTrueFalseBlank xgetSizeWithCellsArray(final int p0);
    
    int sizeOfSizeWithCellsArray();
    
    void setSizeWithCellsArray(final STTrueFalseBlank.Enum[] p0);
    
    void setSizeWithCellsArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetSizeWithCellsArray(final STTrueFalseBlank[] p0);
    
    void xsetSizeWithCellsArray(final int p0, final STTrueFalseBlank p1);
    
    void insertSizeWithCells(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addSizeWithCells(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewSizeWithCells(final int p0);
    
    STTrueFalseBlank addNewSizeWithCells();
    
    void removeSizeWithCells(final int p0);
    
    List<String> getAnchorList();
    
    @Deprecated
    String[] getAnchorArray();
    
    String getAnchorArray(final int p0);
    
    List<XmlString> xgetAnchorList();
    
    @Deprecated
    XmlString[] xgetAnchorArray();
    
    XmlString xgetAnchorArray(final int p0);
    
    int sizeOfAnchorArray();
    
    void setAnchorArray(final String[] p0);
    
    void setAnchorArray(final int p0, final String p1);
    
    void xsetAnchorArray(final XmlString[] p0);
    
    void xsetAnchorArray(final int p0, final XmlString p1);
    
    void insertAnchor(final int p0, final String p1);
    
    void addAnchor(final String p0);
    
    XmlString insertNewAnchor(final int p0);
    
    XmlString addNewAnchor();
    
    void removeAnchor(final int p0);
    
    List<STTrueFalseBlank.Enum> getLockedList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getLockedArray();
    
    STTrueFalseBlank.Enum getLockedArray(final int p0);
    
    List<STTrueFalseBlank> xgetLockedList();
    
    @Deprecated
    STTrueFalseBlank[] xgetLockedArray();
    
    STTrueFalseBlank xgetLockedArray(final int p0);
    
    int sizeOfLockedArray();
    
    void setLockedArray(final STTrueFalseBlank.Enum[] p0);
    
    void setLockedArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetLockedArray(final STTrueFalseBlank[] p0);
    
    void xsetLockedArray(final int p0, final STTrueFalseBlank p1);
    
    void insertLocked(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addLocked(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewLocked(final int p0);
    
    STTrueFalseBlank addNewLocked();
    
    void removeLocked(final int p0);
    
    List<STTrueFalseBlank.Enum> getDefaultSizeList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getDefaultSizeArray();
    
    STTrueFalseBlank.Enum getDefaultSizeArray(final int p0);
    
    List<STTrueFalseBlank> xgetDefaultSizeList();
    
    @Deprecated
    STTrueFalseBlank[] xgetDefaultSizeArray();
    
    STTrueFalseBlank xgetDefaultSizeArray(final int p0);
    
    int sizeOfDefaultSizeArray();
    
    void setDefaultSizeArray(final STTrueFalseBlank.Enum[] p0);
    
    void setDefaultSizeArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetDefaultSizeArray(final STTrueFalseBlank[] p0);
    
    void xsetDefaultSizeArray(final int p0, final STTrueFalseBlank p1);
    
    void insertDefaultSize(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addDefaultSize(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewDefaultSize(final int p0);
    
    STTrueFalseBlank addNewDefaultSize();
    
    void removeDefaultSize(final int p0);
    
    List<STTrueFalseBlank.Enum> getPrintObjectList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getPrintObjectArray();
    
    STTrueFalseBlank.Enum getPrintObjectArray(final int p0);
    
    List<STTrueFalseBlank> xgetPrintObjectList();
    
    @Deprecated
    STTrueFalseBlank[] xgetPrintObjectArray();
    
    STTrueFalseBlank xgetPrintObjectArray(final int p0);
    
    int sizeOfPrintObjectArray();
    
    void setPrintObjectArray(final STTrueFalseBlank.Enum[] p0);
    
    void setPrintObjectArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetPrintObjectArray(final STTrueFalseBlank[] p0);
    
    void xsetPrintObjectArray(final int p0, final STTrueFalseBlank p1);
    
    void insertPrintObject(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addPrintObject(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewPrintObject(final int p0);
    
    STTrueFalseBlank addNewPrintObject();
    
    void removePrintObject(final int p0);
    
    List<STTrueFalseBlank.Enum> getDisabledList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getDisabledArray();
    
    STTrueFalseBlank.Enum getDisabledArray(final int p0);
    
    List<STTrueFalseBlank> xgetDisabledList();
    
    @Deprecated
    STTrueFalseBlank[] xgetDisabledArray();
    
    STTrueFalseBlank xgetDisabledArray(final int p0);
    
    int sizeOfDisabledArray();
    
    void setDisabledArray(final STTrueFalseBlank.Enum[] p0);
    
    void setDisabledArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetDisabledArray(final STTrueFalseBlank[] p0);
    
    void xsetDisabledArray(final int p0, final STTrueFalseBlank p1);
    
    void insertDisabled(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addDisabled(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewDisabled(final int p0);
    
    STTrueFalseBlank addNewDisabled();
    
    void removeDisabled(final int p0);
    
    List<STTrueFalseBlank.Enum> getAutoFillList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getAutoFillArray();
    
    STTrueFalseBlank.Enum getAutoFillArray(final int p0);
    
    List<STTrueFalseBlank> xgetAutoFillList();
    
    @Deprecated
    STTrueFalseBlank[] xgetAutoFillArray();
    
    STTrueFalseBlank xgetAutoFillArray(final int p0);
    
    int sizeOfAutoFillArray();
    
    void setAutoFillArray(final STTrueFalseBlank.Enum[] p0);
    
    void setAutoFillArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetAutoFillArray(final STTrueFalseBlank[] p0);
    
    void xsetAutoFillArray(final int p0, final STTrueFalseBlank p1);
    
    void insertAutoFill(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addAutoFill(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewAutoFill(final int p0);
    
    STTrueFalseBlank addNewAutoFill();
    
    void removeAutoFill(final int p0);
    
    List<STTrueFalseBlank.Enum> getAutoLineList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getAutoLineArray();
    
    STTrueFalseBlank.Enum getAutoLineArray(final int p0);
    
    List<STTrueFalseBlank> xgetAutoLineList();
    
    @Deprecated
    STTrueFalseBlank[] xgetAutoLineArray();
    
    STTrueFalseBlank xgetAutoLineArray(final int p0);
    
    int sizeOfAutoLineArray();
    
    void setAutoLineArray(final STTrueFalseBlank.Enum[] p0);
    
    void setAutoLineArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetAutoLineArray(final STTrueFalseBlank[] p0);
    
    void xsetAutoLineArray(final int p0, final STTrueFalseBlank p1);
    
    void insertAutoLine(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addAutoLine(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewAutoLine(final int p0);
    
    STTrueFalseBlank addNewAutoLine();
    
    void removeAutoLine(final int p0);
    
    List<STTrueFalseBlank.Enum> getAutoPictList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getAutoPictArray();
    
    STTrueFalseBlank.Enum getAutoPictArray(final int p0);
    
    List<STTrueFalseBlank> xgetAutoPictList();
    
    @Deprecated
    STTrueFalseBlank[] xgetAutoPictArray();
    
    STTrueFalseBlank xgetAutoPictArray(final int p0);
    
    int sizeOfAutoPictArray();
    
    void setAutoPictArray(final STTrueFalseBlank.Enum[] p0);
    
    void setAutoPictArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetAutoPictArray(final STTrueFalseBlank[] p0);
    
    void xsetAutoPictArray(final int p0, final STTrueFalseBlank p1);
    
    void insertAutoPict(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addAutoPict(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewAutoPict(final int p0);
    
    STTrueFalseBlank addNewAutoPict();
    
    void removeAutoPict(final int p0);
    
    List<String> getFmlaMacroList();
    
    @Deprecated
    String[] getFmlaMacroArray();
    
    String getFmlaMacroArray(final int p0);
    
    List<XmlString> xgetFmlaMacroList();
    
    @Deprecated
    XmlString[] xgetFmlaMacroArray();
    
    XmlString xgetFmlaMacroArray(final int p0);
    
    int sizeOfFmlaMacroArray();
    
    void setFmlaMacroArray(final String[] p0);
    
    void setFmlaMacroArray(final int p0, final String p1);
    
    void xsetFmlaMacroArray(final XmlString[] p0);
    
    void xsetFmlaMacroArray(final int p0, final XmlString p1);
    
    void insertFmlaMacro(final int p0, final String p1);
    
    void addFmlaMacro(final String p0);
    
    XmlString insertNewFmlaMacro(final int p0);
    
    XmlString addNewFmlaMacro();
    
    void removeFmlaMacro(final int p0);
    
    List<String> getTextHAlignList();
    
    @Deprecated
    String[] getTextHAlignArray();
    
    String getTextHAlignArray(final int p0);
    
    List<XmlString> xgetTextHAlignList();
    
    @Deprecated
    XmlString[] xgetTextHAlignArray();
    
    XmlString xgetTextHAlignArray(final int p0);
    
    int sizeOfTextHAlignArray();
    
    void setTextHAlignArray(final String[] p0);
    
    void setTextHAlignArray(final int p0, final String p1);
    
    void xsetTextHAlignArray(final XmlString[] p0);
    
    void xsetTextHAlignArray(final int p0, final XmlString p1);
    
    void insertTextHAlign(final int p0, final String p1);
    
    void addTextHAlign(final String p0);
    
    XmlString insertNewTextHAlign(final int p0);
    
    XmlString addNewTextHAlign();
    
    void removeTextHAlign(final int p0);
    
    List<String> getTextVAlignList();
    
    @Deprecated
    String[] getTextVAlignArray();
    
    String getTextVAlignArray(final int p0);
    
    List<XmlString> xgetTextVAlignList();
    
    @Deprecated
    XmlString[] xgetTextVAlignArray();
    
    XmlString xgetTextVAlignArray(final int p0);
    
    int sizeOfTextVAlignArray();
    
    void setTextVAlignArray(final String[] p0);
    
    void setTextVAlignArray(final int p0, final String p1);
    
    void xsetTextVAlignArray(final XmlString[] p0);
    
    void xsetTextVAlignArray(final int p0, final XmlString p1);
    
    void insertTextVAlign(final int p0, final String p1);
    
    void addTextVAlign(final String p0);
    
    XmlString insertNewTextVAlign(final int p0);
    
    XmlString addNewTextVAlign();
    
    void removeTextVAlign(final int p0);
    
    List<STTrueFalseBlank.Enum> getLockTextList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getLockTextArray();
    
    STTrueFalseBlank.Enum getLockTextArray(final int p0);
    
    List<STTrueFalseBlank> xgetLockTextList();
    
    @Deprecated
    STTrueFalseBlank[] xgetLockTextArray();
    
    STTrueFalseBlank xgetLockTextArray(final int p0);
    
    int sizeOfLockTextArray();
    
    void setLockTextArray(final STTrueFalseBlank.Enum[] p0);
    
    void setLockTextArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetLockTextArray(final STTrueFalseBlank[] p0);
    
    void xsetLockTextArray(final int p0, final STTrueFalseBlank p1);
    
    void insertLockText(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addLockText(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewLockText(final int p0);
    
    STTrueFalseBlank addNewLockText();
    
    void removeLockText(final int p0);
    
    List<STTrueFalseBlank.Enum> getJustLastXList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getJustLastXArray();
    
    STTrueFalseBlank.Enum getJustLastXArray(final int p0);
    
    List<STTrueFalseBlank> xgetJustLastXList();
    
    @Deprecated
    STTrueFalseBlank[] xgetJustLastXArray();
    
    STTrueFalseBlank xgetJustLastXArray(final int p0);
    
    int sizeOfJustLastXArray();
    
    void setJustLastXArray(final STTrueFalseBlank.Enum[] p0);
    
    void setJustLastXArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetJustLastXArray(final STTrueFalseBlank[] p0);
    
    void xsetJustLastXArray(final int p0, final STTrueFalseBlank p1);
    
    void insertJustLastX(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addJustLastX(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewJustLastX(final int p0);
    
    STTrueFalseBlank addNewJustLastX();
    
    void removeJustLastX(final int p0);
    
    List<STTrueFalseBlank.Enum> getSecretEditList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getSecretEditArray();
    
    STTrueFalseBlank.Enum getSecretEditArray(final int p0);
    
    List<STTrueFalseBlank> xgetSecretEditList();
    
    @Deprecated
    STTrueFalseBlank[] xgetSecretEditArray();
    
    STTrueFalseBlank xgetSecretEditArray(final int p0);
    
    int sizeOfSecretEditArray();
    
    void setSecretEditArray(final STTrueFalseBlank.Enum[] p0);
    
    void setSecretEditArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetSecretEditArray(final STTrueFalseBlank[] p0);
    
    void xsetSecretEditArray(final int p0, final STTrueFalseBlank p1);
    
    void insertSecretEdit(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addSecretEdit(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewSecretEdit(final int p0);
    
    STTrueFalseBlank addNewSecretEdit();
    
    void removeSecretEdit(final int p0);
    
    List<STTrueFalseBlank.Enum> getDefaultList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getDefaultArray();
    
    STTrueFalseBlank.Enum getDefaultArray(final int p0);
    
    List<STTrueFalseBlank> xgetDefaultList();
    
    @Deprecated
    STTrueFalseBlank[] xgetDefaultArray();
    
    STTrueFalseBlank xgetDefaultArray(final int p0);
    
    int sizeOfDefaultArray();
    
    void setDefaultArray(final STTrueFalseBlank.Enum[] p0);
    
    void setDefaultArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetDefaultArray(final STTrueFalseBlank[] p0);
    
    void xsetDefaultArray(final int p0, final STTrueFalseBlank p1);
    
    void insertDefault(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addDefault(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewDefault(final int p0);
    
    STTrueFalseBlank addNewDefault();
    
    void removeDefault(final int p0);
    
    List<STTrueFalseBlank.Enum> getHelpList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getHelpArray();
    
    STTrueFalseBlank.Enum getHelpArray(final int p0);
    
    List<STTrueFalseBlank> xgetHelpList();
    
    @Deprecated
    STTrueFalseBlank[] xgetHelpArray();
    
    STTrueFalseBlank xgetHelpArray(final int p0);
    
    int sizeOfHelpArray();
    
    void setHelpArray(final STTrueFalseBlank.Enum[] p0);
    
    void setHelpArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetHelpArray(final STTrueFalseBlank[] p0);
    
    void xsetHelpArray(final int p0, final STTrueFalseBlank p1);
    
    void insertHelp(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addHelp(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewHelp(final int p0);
    
    STTrueFalseBlank addNewHelp();
    
    void removeHelp(final int p0);
    
    List<STTrueFalseBlank.Enum> getCancelList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getCancelArray();
    
    STTrueFalseBlank.Enum getCancelArray(final int p0);
    
    List<STTrueFalseBlank> xgetCancelList();
    
    @Deprecated
    STTrueFalseBlank[] xgetCancelArray();
    
    STTrueFalseBlank xgetCancelArray(final int p0);
    
    int sizeOfCancelArray();
    
    void setCancelArray(final STTrueFalseBlank.Enum[] p0);
    
    void setCancelArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetCancelArray(final STTrueFalseBlank[] p0);
    
    void xsetCancelArray(final int p0, final STTrueFalseBlank p1);
    
    void insertCancel(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addCancel(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewCancel(final int p0);
    
    STTrueFalseBlank addNewCancel();
    
    void removeCancel(final int p0);
    
    List<STTrueFalseBlank.Enum> getDismissList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getDismissArray();
    
    STTrueFalseBlank.Enum getDismissArray(final int p0);
    
    List<STTrueFalseBlank> xgetDismissList();
    
    @Deprecated
    STTrueFalseBlank[] xgetDismissArray();
    
    STTrueFalseBlank xgetDismissArray(final int p0);
    
    int sizeOfDismissArray();
    
    void setDismissArray(final STTrueFalseBlank.Enum[] p0);
    
    void setDismissArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetDismissArray(final STTrueFalseBlank[] p0);
    
    void xsetDismissArray(final int p0, final STTrueFalseBlank p1);
    
    void insertDismiss(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addDismiss(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewDismiss(final int p0);
    
    STTrueFalseBlank addNewDismiss();
    
    void removeDismiss(final int p0);
    
    List<BigInteger> getAccelList();
    
    @Deprecated
    BigInteger[] getAccelArray();
    
    BigInteger getAccelArray(final int p0);
    
    List<XmlInteger> xgetAccelList();
    
    @Deprecated
    XmlInteger[] xgetAccelArray();
    
    XmlInteger xgetAccelArray(final int p0);
    
    int sizeOfAccelArray();
    
    void setAccelArray(final BigInteger[] p0);
    
    void setAccelArray(final int p0, final BigInteger p1);
    
    void xsetAccelArray(final XmlInteger[] p0);
    
    void xsetAccelArray(final int p0, final XmlInteger p1);
    
    void insertAccel(final int p0, final BigInteger p1);
    
    void addAccel(final BigInteger p0);
    
    XmlInteger insertNewAccel(final int p0);
    
    XmlInteger addNewAccel();
    
    void removeAccel(final int p0);
    
    List<BigInteger> getAccel2List();
    
    @Deprecated
    BigInteger[] getAccel2Array();
    
    BigInteger getAccel2Array(final int p0);
    
    List<XmlInteger> xgetAccel2List();
    
    @Deprecated
    XmlInteger[] xgetAccel2Array();
    
    XmlInteger xgetAccel2Array(final int p0);
    
    int sizeOfAccel2Array();
    
    void setAccel2Array(final BigInteger[] p0);
    
    void setAccel2Array(final int p0, final BigInteger p1);
    
    void xsetAccel2Array(final XmlInteger[] p0);
    
    void xsetAccel2Array(final int p0, final XmlInteger p1);
    
    void insertAccel2(final int p0, final BigInteger p1);
    
    void addAccel2(final BigInteger p0);
    
    XmlInteger insertNewAccel2(final int p0);
    
    XmlInteger addNewAccel2();
    
    void removeAccel2(final int p0);
    
    List<BigInteger> getRowList();
    
    @Deprecated
    BigInteger[] getRowArray();
    
    BigInteger getRowArray(final int p0);
    
    List<XmlInteger> xgetRowList();
    
    @Deprecated
    XmlInteger[] xgetRowArray();
    
    XmlInteger xgetRowArray(final int p0);
    
    int sizeOfRowArray();
    
    void setRowArray(final BigInteger[] p0);
    
    void setRowArray(final int p0, final BigInteger p1);
    
    void xsetRowArray(final XmlInteger[] p0);
    
    void xsetRowArray(final int p0, final XmlInteger p1);
    
    void insertRow(final int p0, final BigInteger p1);
    
    void addRow(final BigInteger p0);
    
    XmlInteger insertNewRow(final int p0);
    
    XmlInteger addNewRow();
    
    void removeRow(final int p0);
    
    List<BigInteger> getColumnList();
    
    @Deprecated
    BigInteger[] getColumnArray();
    
    BigInteger getColumnArray(final int p0);
    
    List<XmlInteger> xgetColumnList();
    
    @Deprecated
    XmlInteger[] xgetColumnArray();
    
    XmlInteger xgetColumnArray(final int p0);
    
    int sizeOfColumnArray();
    
    void setColumnArray(final BigInteger[] p0);
    
    void setColumnArray(final int p0, final BigInteger p1);
    
    void xsetColumnArray(final XmlInteger[] p0);
    
    void xsetColumnArray(final int p0, final XmlInteger p1);
    
    void insertColumn(final int p0, final BigInteger p1);
    
    void addColumn(final BigInteger p0);
    
    XmlInteger insertNewColumn(final int p0);
    
    XmlInteger addNewColumn();
    
    void removeColumn(final int p0);
    
    List<STTrueFalseBlank.Enum> getVisibleList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getVisibleArray();
    
    STTrueFalseBlank.Enum getVisibleArray(final int p0);
    
    List<STTrueFalseBlank> xgetVisibleList();
    
    @Deprecated
    STTrueFalseBlank[] xgetVisibleArray();
    
    STTrueFalseBlank xgetVisibleArray(final int p0);
    
    int sizeOfVisibleArray();
    
    void setVisibleArray(final STTrueFalseBlank.Enum[] p0);
    
    void setVisibleArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetVisibleArray(final STTrueFalseBlank[] p0);
    
    void xsetVisibleArray(final int p0, final STTrueFalseBlank p1);
    
    void insertVisible(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addVisible(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewVisible(final int p0);
    
    STTrueFalseBlank addNewVisible();
    
    void removeVisible(final int p0);
    
    List<STTrueFalseBlank.Enum> getRowHiddenList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getRowHiddenArray();
    
    STTrueFalseBlank.Enum getRowHiddenArray(final int p0);
    
    List<STTrueFalseBlank> xgetRowHiddenList();
    
    @Deprecated
    STTrueFalseBlank[] xgetRowHiddenArray();
    
    STTrueFalseBlank xgetRowHiddenArray(final int p0);
    
    int sizeOfRowHiddenArray();
    
    void setRowHiddenArray(final STTrueFalseBlank.Enum[] p0);
    
    void setRowHiddenArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetRowHiddenArray(final STTrueFalseBlank[] p0);
    
    void xsetRowHiddenArray(final int p0, final STTrueFalseBlank p1);
    
    void insertRowHidden(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addRowHidden(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewRowHidden(final int p0);
    
    STTrueFalseBlank addNewRowHidden();
    
    void removeRowHidden(final int p0);
    
    List<STTrueFalseBlank.Enum> getColHiddenList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getColHiddenArray();
    
    STTrueFalseBlank.Enum getColHiddenArray(final int p0);
    
    List<STTrueFalseBlank> xgetColHiddenList();
    
    @Deprecated
    STTrueFalseBlank[] xgetColHiddenArray();
    
    STTrueFalseBlank xgetColHiddenArray(final int p0);
    
    int sizeOfColHiddenArray();
    
    void setColHiddenArray(final STTrueFalseBlank.Enum[] p0);
    
    void setColHiddenArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetColHiddenArray(final STTrueFalseBlank[] p0);
    
    void xsetColHiddenArray(final int p0, final STTrueFalseBlank p1);
    
    void insertColHidden(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addColHidden(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewColHidden(final int p0);
    
    STTrueFalseBlank addNewColHidden();
    
    void removeColHidden(final int p0);
    
    List<BigInteger> getVTEditList();
    
    @Deprecated
    BigInteger[] getVTEditArray();
    
    BigInteger getVTEditArray(final int p0);
    
    List<XmlInteger> xgetVTEditList();
    
    @Deprecated
    XmlInteger[] xgetVTEditArray();
    
    XmlInteger xgetVTEditArray(final int p0);
    
    int sizeOfVTEditArray();
    
    void setVTEditArray(final BigInteger[] p0);
    
    void setVTEditArray(final int p0, final BigInteger p1);
    
    void xsetVTEditArray(final XmlInteger[] p0);
    
    void xsetVTEditArray(final int p0, final XmlInteger p1);
    
    void insertVTEdit(final int p0, final BigInteger p1);
    
    void addVTEdit(final BigInteger p0);
    
    XmlInteger insertNewVTEdit(final int p0);
    
    XmlInteger addNewVTEdit();
    
    void removeVTEdit(final int p0);
    
    List<STTrueFalseBlank.Enum> getMultiLineList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getMultiLineArray();
    
    STTrueFalseBlank.Enum getMultiLineArray(final int p0);
    
    List<STTrueFalseBlank> xgetMultiLineList();
    
    @Deprecated
    STTrueFalseBlank[] xgetMultiLineArray();
    
    STTrueFalseBlank xgetMultiLineArray(final int p0);
    
    int sizeOfMultiLineArray();
    
    void setMultiLineArray(final STTrueFalseBlank.Enum[] p0);
    
    void setMultiLineArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetMultiLineArray(final STTrueFalseBlank[] p0);
    
    void xsetMultiLineArray(final int p0, final STTrueFalseBlank p1);
    
    void insertMultiLine(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addMultiLine(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewMultiLine(final int p0);
    
    STTrueFalseBlank addNewMultiLine();
    
    void removeMultiLine(final int p0);
    
    List<STTrueFalseBlank.Enum> getVScrollList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getVScrollArray();
    
    STTrueFalseBlank.Enum getVScrollArray(final int p0);
    
    List<STTrueFalseBlank> xgetVScrollList();
    
    @Deprecated
    STTrueFalseBlank[] xgetVScrollArray();
    
    STTrueFalseBlank xgetVScrollArray(final int p0);
    
    int sizeOfVScrollArray();
    
    void setVScrollArray(final STTrueFalseBlank.Enum[] p0);
    
    void setVScrollArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetVScrollArray(final STTrueFalseBlank[] p0);
    
    void xsetVScrollArray(final int p0, final STTrueFalseBlank p1);
    
    void insertVScroll(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addVScroll(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewVScroll(final int p0);
    
    STTrueFalseBlank addNewVScroll();
    
    void removeVScroll(final int p0);
    
    List<STTrueFalseBlank.Enum> getValidIdsList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getValidIdsArray();
    
    STTrueFalseBlank.Enum getValidIdsArray(final int p0);
    
    List<STTrueFalseBlank> xgetValidIdsList();
    
    @Deprecated
    STTrueFalseBlank[] xgetValidIdsArray();
    
    STTrueFalseBlank xgetValidIdsArray(final int p0);
    
    int sizeOfValidIdsArray();
    
    void setValidIdsArray(final STTrueFalseBlank.Enum[] p0);
    
    void setValidIdsArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetValidIdsArray(final STTrueFalseBlank[] p0);
    
    void xsetValidIdsArray(final int p0, final STTrueFalseBlank p1);
    
    void insertValidIds(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addValidIds(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewValidIds(final int p0);
    
    STTrueFalseBlank addNewValidIds();
    
    void removeValidIds(final int p0);
    
    List<String> getFmlaRangeList();
    
    @Deprecated
    String[] getFmlaRangeArray();
    
    String getFmlaRangeArray(final int p0);
    
    List<XmlString> xgetFmlaRangeList();
    
    @Deprecated
    XmlString[] xgetFmlaRangeArray();
    
    XmlString xgetFmlaRangeArray(final int p0);
    
    int sizeOfFmlaRangeArray();
    
    void setFmlaRangeArray(final String[] p0);
    
    void setFmlaRangeArray(final int p0, final String p1);
    
    void xsetFmlaRangeArray(final XmlString[] p0);
    
    void xsetFmlaRangeArray(final int p0, final XmlString p1);
    
    void insertFmlaRange(final int p0, final String p1);
    
    void addFmlaRange(final String p0);
    
    XmlString insertNewFmlaRange(final int p0);
    
    XmlString addNewFmlaRange();
    
    void removeFmlaRange(final int p0);
    
    List<BigInteger> getWidthMinList();
    
    @Deprecated
    BigInteger[] getWidthMinArray();
    
    BigInteger getWidthMinArray(final int p0);
    
    List<XmlInteger> xgetWidthMinList();
    
    @Deprecated
    XmlInteger[] xgetWidthMinArray();
    
    XmlInteger xgetWidthMinArray(final int p0);
    
    int sizeOfWidthMinArray();
    
    void setWidthMinArray(final BigInteger[] p0);
    
    void setWidthMinArray(final int p0, final BigInteger p1);
    
    void xsetWidthMinArray(final XmlInteger[] p0);
    
    void xsetWidthMinArray(final int p0, final XmlInteger p1);
    
    void insertWidthMin(final int p0, final BigInteger p1);
    
    void addWidthMin(final BigInteger p0);
    
    XmlInteger insertNewWidthMin(final int p0);
    
    XmlInteger addNewWidthMin();
    
    void removeWidthMin(final int p0);
    
    List<BigInteger> getSelList();
    
    @Deprecated
    BigInteger[] getSelArray();
    
    BigInteger getSelArray(final int p0);
    
    List<XmlInteger> xgetSelList();
    
    @Deprecated
    XmlInteger[] xgetSelArray();
    
    XmlInteger xgetSelArray(final int p0);
    
    int sizeOfSelArray();
    
    void setSelArray(final BigInteger[] p0);
    
    void setSelArray(final int p0, final BigInteger p1);
    
    void xsetSelArray(final XmlInteger[] p0);
    
    void xsetSelArray(final int p0, final XmlInteger p1);
    
    void insertSel(final int p0, final BigInteger p1);
    
    void addSel(final BigInteger p0);
    
    XmlInteger insertNewSel(final int p0);
    
    XmlInteger addNewSel();
    
    void removeSel(final int p0);
    
    List<STTrueFalseBlank.Enum> getNoThreeD2List();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getNoThreeD2Array();
    
    STTrueFalseBlank.Enum getNoThreeD2Array(final int p0);
    
    List<STTrueFalseBlank> xgetNoThreeD2List();
    
    @Deprecated
    STTrueFalseBlank[] xgetNoThreeD2Array();
    
    STTrueFalseBlank xgetNoThreeD2Array(final int p0);
    
    int sizeOfNoThreeD2Array();
    
    void setNoThreeD2Array(final STTrueFalseBlank.Enum[] p0);
    
    void setNoThreeD2Array(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetNoThreeD2Array(final STTrueFalseBlank[] p0);
    
    void xsetNoThreeD2Array(final int p0, final STTrueFalseBlank p1);
    
    void insertNoThreeD2(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addNoThreeD2(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewNoThreeD2(final int p0);
    
    STTrueFalseBlank addNewNoThreeD2();
    
    void removeNoThreeD2(final int p0);
    
    List<String> getSelTypeList();
    
    @Deprecated
    String[] getSelTypeArray();
    
    String getSelTypeArray(final int p0);
    
    List<XmlString> xgetSelTypeList();
    
    @Deprecated
    XmlString[] xgetSelTypeArray();
    
    XmlString xgetSelTypeArray(final int p0);
    
    int sizeOfSelTypeArray();
    
    void setSelTypeArray(final String[] p0);
    
    void setSelTypeArray(final int p0, final String p1);
    
    void xsetSelTypeArray(final XmlString[] p0);
    
    void xsetSelTypeArray(final int p0, final XmlString p1);
    
    void insertSelType(final int p0, final String p1);
    
    void addSelType(final String p0);
    
    XmlString insertNewSelType(final int p0);
    
    XmlString addNewSelType();
    
    void removeSelType(final int p0);
    
    List<String> getMultiSelList();
    
    @Deprecated
    String[] getMultiSelArray();
    
    String getMultiSelArray(final int p0);
    
    List<XmlString> xgetMultiSelList();
    
    @Deprecated
    XmlString[] xgetMultiSelArray();
    
    XmlString xgetMultiSelArray(final int p0);
    
    int sizeOfMultiSelArray();
    
    void setMultiSelArray(final String[] p0);
    
    void setMultiSelArray(final int p0, final String p1);
    
    void xsetMultiSelArray(final XmlString[] p0);
    
    void xsetMultiSelArray(final int p0, final XmlString p1);
    
    void insertMultiSel(final int p0, final String p1);
    
    void addMultiSel(final String p0);
    
    XmlString insertNewMultiSel(final int p0);
    
    XmlString addNewMultiSel();
    
    void removeMultiSel(final int p0);
    
    List<String> getLCTList();
    
    @Deprecated
    String[] getLCTArray();
    
    String getLCTArray(final int p0);
    
    List<XmlString> xgetLCTList();
    
    @Deprecated
    XmlString[] xgetLCTArray();
    
    XmlString xgetLCTArray(final int p0);
    
    int sizeOfLCTArray();
    
    void setLCTArray(final String[] p0);
    
    void setLCTArray(final int p0, final String p1);
    
    void xsetLCTArray(final XmlString[] p0);
    
    void xsetLCTArray(final int p0, final XmlString p1);
    
    void insertLCT(final int p0, final String p1);
    
    void addLCT(final String p0);
    
    XmlString insertNewLCT(final int p0);
    
    XmlString addNewLCT();
    
    void removeLCT(final int p0);
    
    List<String> getListItemList();
    
    @Deprecated
    String[] getListItemArray();
    
    String getListItemArray(final int p0);
    
    List<XmlString> xgetListItemList();
    
    @Deprecated
    XmlString[] xgetListItemArray();
    
    XmlString xgetListItemArray(final int p0);
    
    int sizeOfListItemArray();
    
    void setListItemArray(final String[] p0);
    
    void setListItemArray(final int p0, final String p1);
    
    void xsetListItemArray(final XmlString[] p0);
    
    void xsetListItemArray(final int p0, final XmlString p1);
    
    void insertListItem(final int p0, final String p1);
    
    void addListItem(final String p0);
    
    XmlString insertNewListItem(final int p0);
    
    XmlString addNewListItem();
    
    void removeListItem(final int p0);
    
    List<String> getDropStyleList();
    
    @Deprecated
    String[] getDropStyleArray();
    
    String getDropStyleArray(final int p0);
    
    List<XmlString> xgetDropStyleList();
    
    @Deprecated
    XmlString[] xgetDropStyleArray();
    
    XmlString xgetDropStyleArray(final int p0);
    
    int sizeOfDropStyleArray();
    
    void setDropStyleArray(final String[] p0);
    
    void setDropStyleArray(final int p0, final String p1);
    
    void xsetDropStyleArray(final XmlString[] p0);
    
    void xsetDropStyleArray(final int p0, final XmlString p1);
    
    void insertDropStyle(final int p0, final String p1);
    
    void addDropStyle(final String p0);
    
    XmlString insertNewDropStyle(final int p0);
    
    XmlString addNewDropStyle();
    
    void removeDropStyle(final int p0);
    
    List<STTrueFalseBlank.Enum> getColoredList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getColoredArray();
    
    STTrueFalseBlank.Enum getColoredArray(final int p0);
    
    List<STTrueFalseBlank> xgetColoredList();
    
    @Deprecated
    STTrueFalseBlank[] xgetColoredArray();
    
    STTrueFalseBlank xgetColoredArray(final int p0);
    
    int sizeOfColoredArray();
    
    void setColoredArray(final STTrueFalseBlank.Enum[] p0);
    
    void setColoredArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetColoredArray(final STTrueFalseBlank[] p0);
    
    void xsetColoredArray(final int p0, final STTrueFalseBlank p1);
    
    void insertColored(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addColored(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewColored(final int p0);
    
    STTrueFalseBlank addNewColored();
    
    void removeColored(final int p0);
    
    List<BigInteger> getDropLinesList();
    
    @Deprecated
    BigInteger[] getDropLinesArray();
    
    BigInteger getDropLinesArray(final int p0);
    
    List<XmlInteger> xgetDropLinesList();
    
    @Deprecated
    XmlInteger[] xgetDropLinesArray();
    
    XmlInteger xgetDropLinesArray(final int p0);
    
    int sizeOfDropLinesArray();
    
    void setDropLinesArray(final BigInteger[] p0);
    
    void setDropLinesArray(final int p0, final BigInteger p1);
    
    void xsetDropLinesArray(final XmlInteger[] p0);
    
    void xsetDropLinesArray(final int p0, final XmlInteger p1);
    
    void insertDropLines(final int p0, final BigInteger p1);
    
    void addDropLines(final BigInteger p0);
    
    XmlInteger insertNewDropLines(final int p0);
    
    XmlInteger addNewDropLines();
    
    void removeDropLines(final int p0);
    
    List<BigInteger> getCheckedList();
    
    @Deprecated
    BigInteger[] getCheckedArray();
    
    BigInteger getCheckedArray(final int p0);
    
    List<XmlInteger> xgetCheckedList();
    
    @Deprecated
    XmlInteger[] xgetCheckedArray();
    
    XmlInteger xgetCheckedArray(final int p0);
    
    int sizeOfCheckedArray();
    
    void setCheckedArray(final BigInteger[] p0);
    
    void setCheckedArray(final int p0, final BigInteger p1);
    
    void xsetCheckedArray(final XmlInteger[] p0);
    
    void xsetCheckedArray(final int p0, final XmlInteger p1);
    
    void insertChecked(final int p0, final BigInteger p1);
    
    void addChecked(final BigInteger p0);
    
    XmlInteger insertNewChecked(final int p0);
    
    XmlInteger addNewChecked();
    
    void removeChecked(final int p0);
    
    List<String> getFmlaLinkList();
    
    @Deprecated
    String[] getFmlaLinkArray();
    
    String getFmlaLinkArray(final int p0);
    
    List<XmlString> xgetFmlaLinkList();
    
    @Deprecated
    XmlString[] xgetFmlaLinkArray();
    
    XmlString xgetFmlaLinkArray(final int p0);
    
    int sizeOfFmlaLinkArray();
    
    void setFmlaLinkArray(final String[] p0);
    
    void setFmlaLinkArray(final int p0, final String p1);
    
    void xsetFmlaLinkArray(final XmlString[] p0);
    
    void xsetFmlaLinkArray(final int p0, final XmlString p1);
    
    void insertFmlaLink(final int p0, final String p1);
    
    void addFmlaLink(final String p0);
    
    XmlString insertNewFmlaLink(final int p0);
    
    XmlString addNewFmlaLink();
    
    void removeFmlaLink(final int p0);
    
    List<String> getFmlaPictList();
    
    @Deprecated
    String[] getFmlaPictArray();
    
    String getFmlaPictArray(final int p0);
    
    List<XmlString> xgetFmlaPictList();
    
    @Deprecated
    XmlString[] xgetFmlaPictArray();
    
    XmlString xgetFmlaPictArray(final int p0);
    
    int sizeOfFmlaPictArray();
    
    void setFmlaPictArray(final String[] p0);
    
    void setFmlaPictArray(final int p0, final String p1);
    
    void xsetFmlaPictArray(final XmlString[] p0);
    
    void xsetFmlaPictArray(final int p0, final XmlString p1);
    
    void insertFmlaPict(final int p0, final String p1);
    
    void addFmlaPict(final String p0);
    
    XmlString insertNewFmlaPict(final int p0);
    
    XmlString addNewFmlaPict();
    
    void removeFmlaPict(final int p0);
    
    List<STTrueFalseBlank.Enum> getNoThreeDList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getNoThreeDArray();
    
    STTrueFalseBlank.Enum getNoThreeDArray(final int p0);
    
    List<STTrueFalseBlank> xgetNoThreeDList();
    
    @Deprecated
    STTrueFalseBlank[] xgetNoThreeDArray();
    
    STTrueFalseBlank xgetNoThreeDArray(final int p0);
    
    int sizeOfNoThreeDArray();
    
    void setNoThreeDArray(final STTrueFalseBlank.Enum[] p0);
    
    void setNoThreeDArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetNoThreeDArray(final STTrueFalseBlank[] p0);
    
    void xsetNoThreeDArray(final int p0, final STTrueFalseBlank p1);
    
    void insertNoThreeD(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addNoThreeD(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewNoThreeD(final int p0);
    
    STTrueFalseBlank addNewNoThreeD();
    
    void removeNoThreeD(final int p0);
    
    List<STTrueFalseBlank.Enum> getFirstButtonList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getFirstButtonArray();
    
    STTrueFalseBlank.Enum getFirstButtonArray(final int p0);
    
    List<STTrueFalseBlank> xgetFirstButtonList();
    
    @Deprecated
    STTrueFalseBlank[] xgetFirstButtonArray();
    
    STTrueFalseBlank xgetFirstButtonArray(final int p0);
    
    int sizeOfFirstButtonArray();
    
    void setFirstButtonArray(final STTrueFalseBlank.Enum[] p0);
    
    void setFirstButtonArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetFirstButtonArray(final STTrueFalseBlank[] p0);
    
    void xsetFirstButtonArray(final int p0, final STTrueFalseBlank p1);
    
    void insertFirstButton(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addFirstButton(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewFirstButton(final int p0);
    
    STTrueFalseBlank addNewFirstButton();
    
    void removeFirstButton(final int p0);
    
    List<String> getFmlaGroupList();
    
    @Deprecated
    String[] getFmlaGroupArray();
    
    String getFmlaGroupArray(final int p0);
    
    List<XmlString> xgetFmlaGroupList();
    
    @Deprecated
    XmlString[] xgetFmlaGroupArray();
    
    XmlString xgetFmlaGroupArray(final int p0);
    
    int sizeOfFmlaGroupArray();
    
    void setFmlaGroupArray(final String[] p0);
    
    void setFmlaGroupArray(final int p0, final String p1);
    
    void xsetFmlaGroupArray(final XmlString[] p0);
    
    void xsetFmlaGroupArray(final int p0, final XmlString p1);
    
    void insertFmlaGroup(final int p0, final String p1);
    
    void addFmlaGroup(final String p0);
    
    XmlString insertNewFmlaGroup(final int p0);
    
    XmlString addNewFmlaGroup();
    
    void removeFmlaGroup(final int p0);
    
    List<BigInteger> getValList();
    
    @Deprecated
    BigInteger[] getValArray();
    
    BigInteger getValArray(final int p0);
    
    List<XmlInteger> xgetValList();
    
    @Deprecated
    XmlInteger[] xgetValArray();
    
    XmlInteger xgetValArray(final int p0);
    
    int sizeOfValArray();
    
    void setValArray(final BigInteger[] p0);
    
    void setValArray(final int p0, final BigInteger p1);
    
    void xsetValArray(final XmlInteger[] p0);
    
    void xsetValArray(final int p0, final XmlInteger p1);
    
    void insertVal(final int p0, final BigInteger p1);
    
    void addVal(final BigInteger p0);
    
    XmlInteger insertNewVal(final int p0);
    
    XmlInteger addNewVal();
    
    void removeVal(final int p0);
    
    List<BigInteger> getMinList();
    
    @Deprecated
    BigInteger[] getMinArray();
    
    BigInteger getMinArray(final int p0);
    
    List<XmlInteger> xgetMinList();
    
    @Deprecated
    XmlInteger[] xgetMinArray();
    
    XmlInteger xgetMinArray(final int p0);
    
    int sizeOfMinArray();
    
    void setMinArray(final BigInteger[] p0);
    
    void setMinArray(final int p0, final BigInteger p1);
    
    void xsetMinArray(final XmlInteger[] p0);
    
    void xsetMinArray(final int p0, final XmlInteger p1);
    
    void insertMin(final int p0, final BigInteger p1);
    
    void addMin(final BigInteger p0);
    
    XmlInteger insertNewMin(final int p0);
    
    XmlInteger addNewMin();
    
    void removeMin(final int p0);
    
    List<BigInteger> getMaxList();
    
    @Deprecated
    BigInteger[] getMaxArray();
    
    BigInteger getMaxArray(final int p0);
    
    List<XmlInteger> xgetMaxList();
    
    @Deprecated
    XmlInteger[] xgetMaxArray();
    
    XmlInteger xgetMaxArray(final int p0);
    
    int sizeOfMaxArray();
    
    void setMaxArray(final BigInteger[] p0);
    
    void setMaxArray(final int p0, final BigInteger p1);
    
    void xsetMaxArray(final XmlInteger[] p0);
    
    void xsetMaxArray(final int p0, final XmlInteger p1);
    
    void insertMax(final int p0, final BigInteger p1);
    
    void addMax(final BigInteger p0);
    
    XmlInteger insertNewMax(final int p0);
    
    XmlInteger addNewMax();
    
    void removeMax(final int p0);
    
    List<BigInteger> getIncList();
    
    @Deprecated
    BigInteger[] getIncArray();
    
    BigInteger getIncArray(final int p0);
    
    List<XmlInteger> xgetIncList();
    
    @Deprecated
    XmlInteger[] xgetIncArray();
    
    XmlInteger xgetIncArray(final int p0);
    
    int sizeOfIncArray();
    
    void setIncArray(final BigInteger[] p0);
    
    void setIncArray(final int p0, final BigInteger p1);
    
    void xsetIncArray(final XmlInteger[] p0);
    
    void xsetIncArray(final int p0, final XmlInteger p1);
    
    void insertInc(final int p0, final BigInteger p1);
    
    void addInc(final BigInteger p0);
    
    XmlInteger insertNewInc(final int p0);
    
    XmlInteger addNewInc();
    
    void removeInc(final int p0);
    
    List<BigInteger> getPageList();
    
    @Deprecated
    BigInteger[] getPageArray();
    
    BigInteger getPageArray(final int p0);
    
    List<XmlInteger> xgetPageList();
    
    @Deprecated
    XmlInteger[] xgetPageArray();
    
    XmlInteger xgetPageArray(final int p0);
    
    int sizeOfPageArray();
    
    void setPageArray(final BigInteger[] p0);
    
    void setPageArray(final int p0, final BigInteger p1);
    
    void xsetPageArray(final XmlInteger[] p0);
    
    void xsetPageArray(final int p0, final XmlInteger p1);
    
    void insertPage(final int p0, final BigInteger p1);
    
    void addPage(final BigInteger p0);
    
    XmlInteger insertNewPage(final int p0);
    
    XmlInteger addNewPage();
    
    void removePage(final int p0);
    
    List<STTrueFalseBlank.Enum> getHorizList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getHorizArray();
    
    STTrueFalseBlank.Enum getHorizArray(final int p0);
    
    List<STTrueFalseBlank> xgetHorizList();
    
    @Deprecated
    STTrueFalseBlank[] xgetHorizArray();
    
    STTrueFalseBlank xgetHorizArray(final int p0);
    
    int sizeOfHorizArray();
    
    void setHorizArray(final STTrueFalseBlank.Enum[] p0);
    
    void setHorizArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetHorizArray(final STTrueFalseBlank[] p0);
    
    void xsetHorizArray(final int p0, final STTrueFalseBlank p1);
    
    void insertHoriz(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addHoriz(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewHoriz(final int p0);
    
    STTrueFalseBlank addNewHoriz();
    
    void removeHoriz(final int p0);
    
    List<BigInteger> getDxList();
    
    @Deprecated
    BigInteger[] getDxArray();
    
    BigInteger getDxArray(final int p0);
    
    List<XmlInteger> xgetDxList();
    
    @Deprecated
    XmlInteger[] xgetDxArray();
    
    XmlInteger xgetDxArray(final int p0);
    
    int sizeOfDxArray();
    
    void setDxArray(final BigInteger[] p0);
    
    void setDxArray(final int p0, final BigInteger p1);
    
    void xsetDxArray(final XmlInteger[] p0);
    
    void xsetDxArray(final int p0, final XmlInteger p1);
    
    void insertDx(final int p0, final BigInteger p1);
    
    void addDx(final BigInteger p0);
    
    XmlInteger insertNewDx(final int p0);
    
    XmlInteger addNewDx();
    
    void removeDx(final int p0);
    
    List<STTrueFalseBlank.Enum> getMapOCXList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getMapOCXArray();
    
    STTrueFalseBlank.Enum getMapOCXArray(final int p0);
    
    List<STTrueFalseBlank> xgetMapOCXList();
    
    @Deprecated
    STTrueFalseBlank[] xgetMapOCXArray();
    
    STTrueFalseBlank xgetMapOCXArray(final int p0);
    
    int sizeOfMapOCXArray();
    
    void setMapOCXArray(final STTrueFalseBlank.Enum[] p0);
    
    void setMapOCXArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetMapOCXArray(final STTrueFalseBlank[] p0);
    
    void xsetMapOCXArray(final int p0, final STTrueFalseBlank p1);
    
    void insertMapOCX(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addMapOCX(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewMapOCX(final int p0);
    
    STTrueFalseBlank addNewMapOCX();
    
    void removeMapOCX(final int p0);
    
    List<STCF.Enum> getCFList();
    
    @Deprecated
    STCF.Enum[] getCFArray();
    
    STCF.Enum getCFArray(final int p0);
    
    List<STCF> xgetCFList();
    
    @Deprecated
    STCF[] xgetCFArray();
    
    STCF xgetCFArray(final int p0);
    
    int sizeOfCFArray();
    
    void setCFArray(final STCF.Enum[] p0);
    
    void setCFArray(final int p0, final STCF.Enum p1);
    
    void xsetCFArray(final STCF[] p0);
    
    void xsetCFArray(final int p0, final STCF p1);
    
    void insertCF(final int p0, final STCF.Enum p1);
    
    void addCF(final STCF.Enum p0);
    
    STCF insertNewCF(final int p0);
    
    STCF addNewCF();
    
    void removeCF(final int p0);
    
    List<STTrueFalseBlank.Enum> getCameraList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getCameraArray();
    
    STTrueFalseBlank.Enum getCameraArray(final int p0);
    
    List<STTrueFalseBlank> xgetCameraList();
    
    @Deprecated
    STTrueFalseBlank[] xgetCameraArray();
    
    STTrueFalseBlank xgetCameraArray(final int p0);
    
    int sizeOfCameraArray();
    
    void setCameraArray(final STTrueFalseBlank.Enum[] p0);
    
    void setCameraArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetCameraArray(final STTrueFalseBlank[] p0);
    
    void xsetCameraArray(final int p0, final STTrueFalseBlank p1);
    
    void insertCamera(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addCamera(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewCamera(final int p0);
    
    STTrueFalseBlank addNewCamera();
    
    void removeCamera(final int p0);
    
    List<STTrueFalseBlank.Enum> getRecalcAlwaysList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getRecalcAlwaysArray();
    
    STTrueFalseBlank.Enum getRecalcAlwaysArray(final int p0);
    
    List<STTrueFalseBlank> xgetRecalcAlwaysList();
    
    @Deprecated
    STTrueFalseBlank[] xgetRecalcAlwaysArray();
    
    STTrueFalseBlank xgetRecalcAlwaysArray(final int p0);
    
    int sizeOfRecalcAlwaysArray();
    
    void setRecalcAlwaysArray(final STTrueFalseBlank.Enum[] p0);
    
    void setRecalcAlwaysArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetRecalcAlwaysArray(final STTrueFalseBlank[] p0);
    
    void xsetRecalcAlwaysArray(final int p0, final STTrueFalseBlank p1);
    
    void insertRecalcAlways(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addRecalcAlways(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewRecalcAlways(final int p0);
    
    STTrueFalseBlank addNewRecalcAlways();
    
    void removeRecalcAlways(final int p0);
    
    List<STTrueFalseBlank.Enum> getAutoScaleList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getAutoScaleArray();
    
    STTrueFalseBlank.Enum getAutoScaleArray(final int p0);
    
    List<STTrueFalseBlank> xgetAutoScaleList();
    
    @Deprecated
    STTrueFalseBlank[] xgetAutoScaleArray();
    
    STTrueFalseBlank xgetAutoScaleArray(final int p0);
    
    int sizeOfAutoScaleArray();
    
    void setAutoScaleArray(final STTrueFalseBlank.Enum[] p0);
    
    void setAutoScaleArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetAutoScaleArray(final STTrueFalseBlank[] p0);
    
    void xsetAutoScaleArray(final int p0, final STTrueFalseBlank p1);
    
    void insertAutoScale(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addAutoScale(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewAutoScale(final int p0);
    
    STTrueFalseBlank addNewAutoScale();
    
    void removeAutoScale(final int p0);
    
    List<STTrueFalseBlank.Enum> getDDEList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getDDEArray();
    
    STTrueFalseBlank.Enum getDDEArray(final int p0);
    
    List<STTrueFalseBlank> xgetDDEList();
    
    @Deprecated
    STTrueFalseBlank[] xgetDDEArray();
    
    STTrueFalseBlank xgetDDEArray(final int p0);
    
    int sizeOfDDEArray();
    
    void setDDEArray(final STTrueFalseBlank.Enum[] p0);
    
    void setDDEArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetDDEArray(final STTrueFalseBlank[] p0);
    
    void xsetDDEArray(final int p0, final STTrueFalseBlank p1);
    
    void insertDDE(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addDDE(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewDDE(final int p0);
    
    STTrueFalseBlank addNewDDE();
    
    void removeDDE(final int p0);
    
    List<STTrueFalseBlank.Enum> getUIObjList();
    
    @Deprecated
    STTrueFalseBlank.Enum[] getUIObjArray();
    
    STTrueFalseBlank.Enum getUIObjArray(final int p0);
    
    List<STTrueFalseBlank> xgetUIObjList();
    
    @Deprecated
    STTrueFalseBlank[] xgetUIObjArray();
    
    STTrueFalseBlank xgetUIObjArray(final int p0);
    
    int sizeOfUIObjArray();
    
    void setUIObjArray(final STTrueFalseBlank.Enum[] p0);
    
    void setUIObjArray(final int p0, final STTrueFalseBlank.Enum p1);
    
    void xsetUIObjArray(final STTrueFalseBlank[] p0);
    
    void xsetUIObjArray(final int p0, final STTrueFalseBlank p1);
    
    void insertUIObj(final int p0, final STTrueFalseBlank.Enum p1);
    
    void addUIObj(final STTrueFalseBlank.Enum p0);
    
    STTrueFalseBlank insertNewUIObj(final int p0);
    
    STTrueFalseBlank addNewUIObj();
    
    void removeUIObj(final int p0);
    
    List<String> getScriptTextList();
    
    @Deprecated
    String[] getScriptTextArray();
    
    String getScriptTextArray(final int p0);
    
    List<XmlString> xgetScriptTextList();
    
    @Deprecated
    XmlString[] xgetScriptTextArray();
    
    XmlString xgetScriptTextArray(final int p0);
    
    int sizeOfScriptTextArray();
    
    void setScriptTextArray(final String[] p0);
    
    void setScriptTextArray(final int p0, final String p1);
    
    void xsetScriptTextArray(final XmlString[] p0);
    
    void xsetScriptTextArray(final int p0, final XmlString p1);
    
    void insertScriptText(final int p0, final String p1);
    
    void addScriptText(final String p0);
    
    XmlString insertNewScriptText(final int p0);
    
    XmlString addNewScriptText();
    
    void removeScriptText(final int p0);
    
    List<String> getScriptExtendedList();
    
    @Deprecated
    String[] getScriptExtendedArray();
    
    String getScriptExtendedArray(final int p0);
    
    List<XmlString> xgetScriptExtendedList();
    
    @Deprecated
    XmlString[] xgetScriptExtendedArray();
    
    XmlString xgetScriptExtendedArray(final int p0);
    
    int sizeOfScriptExtendedArray();
    
    void setScriptExtendedArray(final String[] p0);
    
    void setScriptExtendedArray(final int p0, final String p1);
    
    void xsetScriptExtendedArray(final XmlString[] p0);
    
    void xsetScriptExtendedArray(final int p0, final XmlString p1);
    
    void insertScriptExtended(final int p0, final String p1);
    
    void addScriptExtended(final String p0);
    
    XmlString insertNewScriptExtended(final int p0);
    
    XmlString addNewScriptExtended();
    
    void removeScriptExtended(final int p0);
    
    List<BigInteger> getScriptLanguageList();
    
    @Deprecated
    BigInteger[] getScriptLanguageArray();
    
    BigInteger getScriptLanguageArray(final int p0);
    
    List<XmlNonNegativeInteger> xgetScriptLanguageList();
    
    @Deprecated
    XmlNonNegativeInteger[] xgetScriptLanguageArray();
    
    XmlNonNegativeInteger xgetScriptLanguageArray(final int p0);
    
    int sizeOfScriptLanguageArray();
    
    void setScriptLanguageArray(final BigInteger[] p0);
    
    void setScriptLanguageArray(final int p0, final BigInteger p1);
    
    void xsetScriptLanguageArray(final XmlNonNegativeInteger[] p0);
    
    void xsetScriptLanguageArray(final int p0, final XmlNonNegativeInteger p1);
    
    void insertScriptLanguage(final int p0, final BigInteger p1);
    
    void addScriptLanguage(final BigInteger p0);
    
    XmlNonNegativeInteger insertNewScriptLanguage(final int p0);
    
    XmlNonNegativeInteger addNewScriptLanguage();
    
    void removeScriptLanguage(final int p0);
    
    List<BigInteger> getScriptLocationList();
    
    @Deprecated
    BigInteger[] getScriptLocationArray();
    
    BigInteger getScriptLocationArray(final int p0);
    
    List<XmlNonNegativeInteger> xgetScriptLocationList();
    
    @Deprecated
    XmlNonNegativeInteger[] xgetScriptLocationArray();
    
    XmlNonNegativeInteger xgetScriptLocationArray(final int p0);
    
    int sizeOfScriptLocationArray();
    
    void setScriptLocationArray(final BigInteger[] p0);
    
    void setScriptLocationArray(final int p0, final BigInteger p1);
    
    void xsetScriptLocationArray(final XmlNonNegativeInteger[] p0);
    
    void xsetScriptLocationArray(final int p0, final XmlNonNegativeInteger p1);
    
    void insertScriptLocation(final int p0, final BigInteger p1);
    
    void addScriptLocation(final BigInteger p0);
    
    XmlNonNegativeInteger insertNewScriptLocation(final int p0);
    
    XmlNonNegativeInteger addNewScriptLocation();
    
    void removeScriptLocation(final int p0);
    
    List<String> getFmlaTxbxList();
    
    @Deprecated
    String[] getFmlaTxbxArray();
    
    String getFmlaTxbxArray(final int p0);
    
    List<XmlString> xgetFmlaTxbxList();
    
    @Deprecated
    XmlString[] xgetFmlaTxbxArray();
    
    XmlString xgetFmlaTxbxArray(final int p0);
    
    int sizeOfFmlaTxbxArray();
    
    void setFmlaTxbxArray(final String[] p0);
    
    void setFmlaTxbxArray(final int p0, final String p1);
    
    void xsetFmlaTxbxArray(final XmlString[] p0);
    
    void xsetFmlaTxbxArray(final int p0, final XmlString p1);
    
    void insertFmlaTxbx(final int p0, final String p1);
    
    void addFmlaTxbx(final String p0);
    
    XmlString insertNewFmlaTxbx(final int p0);
    
    XmlString addNewFmlaTxbx();
    
    void removeFmlaTxbx(final int p0);
    
    STObjectType.Enum getObjectType();
    
    STObjectType xgetObjectType();
    
    void setObjectType(final STObjectType.Enum p0);
    
    void xsetObjectType(final STObjectType p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTClientData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTClientData newInstance() {
            return (CTClientData)getTypeLoader().newInstance(CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData newInstance(final XmlOptions xmlOptions) {
            return (CTClientData)getTypeLoader().newInstance(CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final String s) throws XmlException {
            return (CTClientData)getTypeLoader().parse(s, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTClientData)getTypeLoader().parse(s, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final File file) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(file, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(file, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final URL url) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(url, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(url, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(inputStream, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(inputStream, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final Reader reader) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(reader, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTClientData)getTypeLoader().parse(reader, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTClientData)getTypeLoader().parse(xmlStreamReader, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTClientData)getTypeLoader().parse(xmlStreamReader, CTClientData.type, xmlOptions);
        }
        
        public static CTClientData parse(final Node node) throws XmlException {
            return (CTClientData)getTypeLoader().parse(node, CTClientData.type, (XmlOptions)null);
        }
        
        public static CTClientData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTClientData)getTypeLoader().parse(node, CTClientData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTClientData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTClientData)getTypeLoader().parse(xmlInputStream, CTClientData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTClientData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTClientData)getTypeLoader().parse(xmlInputStream, CTClientData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTClientData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTClientData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
