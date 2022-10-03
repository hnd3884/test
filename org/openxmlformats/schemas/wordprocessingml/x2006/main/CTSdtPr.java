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

public interface CTSdtPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtpre24dtype");
    
    List<CTRPr> getRPrList();
    
    @Deprecated
    CTRPr[] getRPrArray();
    
    CTRPr getRPrArray(final int p0);
    
    int sizeOfRPrArray();
    
    void setRPrArray(final CTRPr[] p0);
    
    void setRPrArray(final int p0, final CTRPr p1);
    
    CTRPr insertNewRPr(final int p0);
    
    CTRPr addNewRPr();
    
    void removeRPr(final int p0);
    
    List<CTString> getAliasList();
    
    @Deprecated
    CTString[] getAliasArray();
    
    CTString getAliasArray(final int p0);
    
    int sizeOfAliasArray();
    
    void setAliasArray(final CTString[] p0);
    
    void setAliasArray(final int p0, final CTString p1);
    
    CTString insertNewAlias(final int p0);
    
    CTString addNewAlias();
    
    void removeAlias(final int p0);
    
    List<CTLock> getLockList();
    
    @Deprecated
    CTLock[] getLockArray();
    
    CTLock getLockArray(final int p0);
    
    int sizeOfLockArray();
    
    void setLockArray(final CTLock[] p0);
    
    void setLockArray(final int p0, final CTLock p1);
    
    CTLock insertNewLock(final int p0);
    
    CTLock addNewLock();
    
    void removeLock(final int p0);
    
    List<CTPlaceholder> getPlaceholderList();
    
    @Deprecated
    CTPlaceholder[] getPlaceholderArray();
    
    CTPlaceholder getPlaceholderArray(final int p0);
    
    int sizeOfPlaceholderArray();
    
    void setPlaceholderArray(final CTPlaceholder[] p0);
    
    void setPlaceholderArray(final int p0, final CTPlaceholder p1);
    
    CTPlaceholder insertNewPlaceholder(final int p0);
    
    CTPlaceholder addNewPlaceholder();
    
    void removePlaceholder(final int p0);
    
    List<CTOnOff> getShowingPlcHdrList();
    
    @Deprecated
    CTOnOff[] getShowingPlcHdrArray();
    
    CTOnOff getShowingPlcHdrArray(final int p0);
    
    int sizeOfShowingPlcHdrArray();
    
    void setShowingPlcHdrArray(final CTOnOff[] p0);
    
    void setShowingPlcHdrArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewShowingPlcHdr(final int p0);
    
    CTOnOff addNewShowingPlcHdr();
    
    void removeShowingPlcHdr(final int p0);
    
    List<CTDataBinding> getDataBindingList();
    
    @Deprecated
    CTDataBinding[] getDataBindingArray();
    
    CTDataBinding getDataBindingArray(final int p0);
    
    int sizeOfDataBindingArray();
    
    void setDataBindingArray(final CTDataBinding[] p0);
    
    void setDataBindingArray(final int p0, final CTDataBinding p1);
    
    CTDataBinding insertNewDataBinding(final int p0);
    
    CTDataBinding addNewDataBinding();
    
    void removeDataBinding(final int p0);
    
    List<CTOnOff> getTemporaryList();
    
    @Deprecated
    CTOnOff[] getTemporaryArray();
    
    CTOnOff getTemporaryArray(final int p0);
    
    int sizeOfTemporaryArray();
    
    void setTemporaryArray(final CTOnOff[] p0);
    
    void setTemporaryArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewTemporary(final int p0);
    
    CTOnOff addNewTemporary();
    
    void removeTemporary(final int p0);
    
    List<CTDecimalNumber> getIdList();
    
    @Deprecated
    CTDecimalNumber[] getIdArray();
    
    CTDecimalNumber getIdArray(final int p0);
    
    int sizeOfIdArray();
    
    void setIdArray(final CTDecimalNumber[] p0);
    
    void setIdArray(final int p0, final CTDecimalNumber p1);
    
    CTDecimalNumber insertNewId(final int p0);
    
    CTDecimalNumber addNewId();
    
    void removeId(final int p0);
    
    List<CTString> getTagList();
    
    @Deprecated
    CTString[] getTagArray();
    
    CTString getTagArray(final int p0);
    
    int sizeOfTagArray();
    
    void setTagArray(final CTString[] p0);
    
    void setTagArray(final int p0, final CTString p1);
    
    CTString insertNewTag(final int p0);
    
    CTString addNewTag();
    
    void removeTag(final int p0);
    
    List<CTEmpty> getEquationList();
    
    @Deprecated
    CTEmpty[] getEquationArray();
    
    CTEmpty getEquationArray(final int p0);
    
    int sizeOfEquationArray();
    
    void setEquationArray(final CTEmpty[] p0);
    
    void setEquationArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewEquation(final int p0);
    
    CTEmpty addNewEquation();
    
    void removeEquation(final int p0);
    
    List<CTSdtComboBox> getComboBoxList();
    
    @Deprecated
    CTSdtComboBox[] getComboBoxArray();
    
    CTSdtComboBox getComboBoxArray(final int p0);
    
    int sizeOfComboBoxArray();
    
    void setComboBoxArray(final CTSdtComboBox[] p0);
    
    void setComboBoxArray(final int p0, final CTSdtComboBox p1);
    
    CTSdtComboBox insertNewComboBox(final int p0);
    
    CTSdtComboBox addNewComboBox();
    
    void removeComboBox(final int p0);
    
    List<CTSdtDate> getDateList();
    
    @Deprecated
    CTSdtDate[] getDateArray();
    
    CTSdtDate getDateArray(final int p0);
    
    int sizeOfDateArray();
    
    void setDateArray(final CTSdtDate[] p0);
    
    void setDateArray(final int p0, final CTSdtDate p1);
    
    CTSdtDate insertNewDate(final int p0);
    
    CTSdtDate addNewDate();
    
    void removeDate(final int p0);
    
    List<CTSdtDocPart> getDocPartObjList();
    
    @Deprecated
    CTSdtDocPart[] getDocPartObjArray();
    
    CTSdtDocPart getDocPartObjArray(final int p0);
    
    int sizeOfDocPartObjArray();
    
    void setDocPartObjArray(final CTSdtDocPart[] p0);
    
    void setDocPartObjArray(final int p0, final CTSdtDocPart p1);
    
    CTSdtDocPart insertNewDocPartObj(final int p0);
    
    CTSdtDocPart addNewDocPartObj();
    
    void removeDocPartObj(final int p0);
    
    List<CTSdtDocPart> getDocPartListList();
    
    @Deprecated
    CTSdtDocPart[] getDocPartListArray();
    
    CTSdtDocPart getDocPartListArray(final int p0);
    
    int sizeOfDocPartListArray();
    
    void setDocPartListArray(final CTSdtDocPart[] p0);
    
    void setDocPartListArray(final int p0, final CTSdtDocPart p1);
    
    CTSdtDocPart insertNewDocPartList(final int p0);
    
    CTSdtDocPart addNewDocPartList();
    
    void removeDocPartList(final int p0);
    
    List<CTSdtDropDownList> getDropDownListList();
    
    @Deprecated
    CTSdtDropDownList[] getDropDownListArray();
    
    CTSdtDropDownList getDropDownListArray(final int p0);
    
    int sizeOfDropDownListArray();
    
    void setDropDownListArray(final CTSdtDropDownList[] p0);
    
    void setDropDownListArray(final int p0, final CTSdtDropDownList p1);
    
    CTSdtDropDownList insertNewDropDownList(final int p0);
    
    CTSdtDropDownList addNewDropDownList();
    
    void removeDropDownList(final int p0);
    
    List<CTEmpty> getPictureList();
    
    @Deprecated
    CTEmpty[] getPictureArray();
    
    CTEmpty getPictureArray(final int p0);
    
    int sizeOfPictureArray();
    
    void setPictureArray(final CTEmpty[] p0);
    
    void setPictureArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewPicture(final int p0);
    
    CTEmpty addNewPicture();
    
    void removePicture(final int p0);
    
    List<CTEmpty> getRichTextList();
    
    @Deprecated
    CTEmpty[] getRichTextArray();
    
    CTEmpty getRichTextArray(final int p0);
    
    int sizeOfRichTextArray();
    
    void setRichTextArray(final CTEmpty[] p0);
    
    void setRichTextArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewRichText(final int p0);
    
    CTEmpty addNewRichText();
    
    void removeRichText(final int p0);
    
    List<CTSdtText> getTextList();
    
    @Deprecated
    CTSdtText[] getTextArray();
    
    CTSdtText getTextArray(final int p0);
    
    int sizeOfTextArray();
    
    void setTextArray(final CTSdtText[] p0);
    
    void setTextArray(final int p0, final CTSdtText p1);
    
    CTSdtText insertNewText(final int p0);
    
    CTSdtText addNewText();
    
    void removeText(final int p0);
    
    List<CTEmpty> getCitationList();
    
    @Deprecated
    CTEmpty[] getCitationArray();
    
    CTEmpty getCitationArray(final int p0);
    
    int sizeOfCitationArray();
    
    void setCitationArray(final CTEmpty[] p0);
    
    void setCitationArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewCitation(final int p0);
    
    CTEmpty addNewCitation();
    
    void removeCitation(final int p0);
    
    List<CTEmpty> getGroupList();
    
    @Deprecated
    CTEmpty[] getGroupArray();
    
    CTEmpty getGroupArray(final int p0);
    
    int sizeOfGroupArray();
    
    void setGroupArray(final CTEmpty[] p0);
    
    void setGroupArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewGroup(final int p0);
    
    CTEmpty addNewGroup();
    
    void removeGroup(final int p0);
    
    List<CTEmpty> getBibliographyList();
    
    @Deprecated
    CTEmpty[] getBibliographyArray();
    
    CTEmpty getBibliographyArray(final int p0);
    
    int sizeOfBibliographyArray();
    
    void setBibliographyArray(final CTEmpty[] p0);
    
    void setBibliographyArray(final int p0, final CTEmpty p1);
    
    CTEmpty insertNewBibliography(final int p0);
    
    CTEmpty addNewBibliography();
    
    void removeBibliography(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtPr newInstance() {
            return (CTSdtPr)getTypeLoader().newInstance(CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr newInstance(final XmlOptions xmlOptions) {
            return (CTSdtPr)getTypeLoader().newInstance(CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final String s) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(s, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(s, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final File file) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(file, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(file, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final URL url) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(url, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(url, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(inputStream, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(inputStream, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(reader, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtPr)getTypeLoader().parse(reader, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(xmlStreamReader, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(xmlStreamReader, CTSdtPr.type, xmlOptions);
        }
        
        public static CTSdtPr parse(final Node node) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(node, CTSdtPr.type, (XmlOptions)null);
        }
        
        public static CTSdtPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtPr)getTypeLoader().parse(node, CTSdtPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtPr)getTypeLoader().parse(xmlInputStream, CTSdtPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtPr)getTypeLoader().parse(xmlInputStream, CTSdtPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
