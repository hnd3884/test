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

public interface CTFFData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFFData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctffdataaa7etype");
    
    List<CTFFName> getNameList();
    
    @Deprecated
    CTFFName[] getNameArray();
    
    CTFFName getNameArray(final int p0);
    
    int sizeOfNameArray();
    
    void setNameArray(final CTFFName[] p0);
    
    void setNameArray(final int p0, final CTFFName p1);
    
    CTFFName insertNewName(final int p0);
    
    CTFFName addNewName();
    
    void removeName(final int p0);
    
    List<CTOnOff> getEnabledList();
    
    @Deprecated
    CTOnOff[] getEnabledArray();
    
    CTOnOff getEnabledArray(final int p0);
    
    int sizeOfEnabledArray();
    
    void setEnabledArray(final CTOnOff[] p0);
    
    void setEnabledArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewEnabled(final int p0);
    
    CTOnOff addNewEnabled();
    
    void removeEnabled(final int p0);
    
    List<CTOnOff> getCalcOnExitList();
    
    @Deprecated
    CTOnOff[] getCalcOnExitArray();
    
    CTOnOff getCalcOnExitArray(final int p0);
    
    int sizeOfCalcOnExitArray();
    
    void setCalcOnExitArray(final CTOnOff[] p0);
    
    void setCalcOnExitArray(final int p0, final CTOnOff p1);
    
    CTOnOff insertNewCalcOnExit(final int p0);
    
    CTOnOff addNewCalcOnExit();
    
    void removeCalcOnExit(final int p0);
    
    List<CTMacroName> getEntryMacroList();
    
    @Deprecated
    CTMacroName[] getEntryMacroArray();
    
    CTMacroName getEntryMacroArray(final int p0);
    
    int sizeOfEntryMacroArray();
    
    void setEntryMacroArray(final CTMacroName[] p0);
    
    void setEntryMacroArray(final int p0, final CTMacroName p1);
    
    CTMacroName insertNewEntryMacro(final int p0);
    
    CTMacroName addNewEntryMacro();
    
    void removeEntryMacro(final int p0);
    
    List<CTMacroName> getExitMacroList();
    
    @Deprecated
    CTMacroName[] getExitMacroArray();
    
    CTMacroName getExitMacroArray(final int p0);
    
    int sizeOfExitMacroArray();
    
    void setExitMacroArray(final CTMacroName[] p0);
    
    void setExitMacroArray(final int p0, final CTMacroName p1);
    
    CTMacroName insertNewExitMacro(final int p0);
    
    CTMacroName addNewExitMacro();
    
    void removeExitMacro(final int p0);
    
    List<CTFFHelpText> getHelpTextList();
    
    @Deprecated
    CTFFHelpText[] getHelpTextArray();
    
    CTFFHelpText getHelpTextArray(final int p0);
    
    int sizeOfHelpTextArray();
    
    void setHelpTextArray(final CTFFHelpText[] p0);
    
    void setHelpTextArray(final int p0, final CTFFHelpText p1);
    
    CTFFHelpText insertNewHelpText(final int p0);
    
    CTFFHelpText addNewHelpText();
    
    void removeHelpText(final int p0);
    
    List<CTFFStatusText> getStatusTextList();
    
    @Deprecated
    CTFFStatusText[] getStatusTextArray();
    
    CTFFStatusText getStatusTextArray(final int p0);
    
    int sizeOfStatusTextArray();
    
    void setStatusTextArray(final CTFFStatusText[] p0);
    
    void setStatusTextArray(final int p0, final CTFFStatusText p1);
    
    CTFFStatusText insertNewStatusText(final int p0);
    
    CTFFStatusText addNewStatusText();
    
    void removeStatusText(final int p0);
    
    List<CTFFCheckBox> getCheckBoxList();
    
    @Deprecated
    CTFFCheckBox[] getCheckBoxArray();
    
    CTFFCheckBox getCheckBoxArray(final int p0);
    
    int sizeOfCheckBoxArray();
    
    void setCheckBoxArray(final CTFFCheckBox[] p0);
    
    void setCheckBoxArray(final int p0, final CTFFCheckBox p1);
    
    CTFFCheckBox insertNewCheckBox(final int p0);
    
    CTFFCheckBox addNewCheckBox();
    
    void removeCheckBox(final int p0);
    
    List<CTFFDDList> getDdListList();
    
    @Deprecated
    CTFFDDList[] getDdListArray();
    
    CTFFDDList getDdListArray(final int p0);
    
    int sizeOfDdListArray();
    
    void setDdListArray(final CTFFDDList[] p0);
    
    void setDdListArray(final int p0, final CTFFDDList p1);
    
    CTFFDDList insertNewDdList(final int p0);
    
    CTFFDDList addNewDdList();
    
    void removeDdList(final int p0);
    
    List<CTFFTextInput> getTextInputList();
    
    @Deprecated
    CTFFTextInput[] getTextInputArray();
    
    CTFFTextInput getTextInputArray(final int p0);
    
    int sizeOfTextInputArray();
    
    void setTextInputArray(final CTFFTextInput[] p0);
    
    void setTextInputArray(final int p0, final CTFFTextInput p1);
    
    CTFFTextInput insertNewTextInput(final int p0);
    
    CTFFTextInput addNewTextInput();
    
    void removeTextInput(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFFData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFFData newInstance() {
            return (CTFFData)getTypeLoader().newInstance(CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData newInstance(final XmlOptions xmlOptions) {
            return (CTFFData)getTypeLoader().newInstance(CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final String s) throws XmlException {
            return (CTFFData)getTypeLoader().parse(s, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFData)getTypeLoader().parse(s, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final File file) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(file, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(file, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final URL url) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(url, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(url, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(inputStream, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(inputStream, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final Reader reader) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(reader, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFData)getTypeLoader().parse(reader, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFFData)getTypeLoader().parse(xmlStreamReader, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFData)getTypeLoader().parse(xmlStreamReader, CTFFData.type, xmlOptions);
        }
        
        public static CTFFData parse(final Node node) throws XmlException {
            return (CTFFData)getTypeLoader().parse(node, CTFFData.type, (XmlOptions)null);
        }
        
        public static CTFFData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFData)getTypeLoader().parse(node, CTFFData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFFData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFFData)getTypeLoader().parse(xmlInputStream, CTFFData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFFData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFFData)getTypeLoader().parse(xmlInputStream, CTFFData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFFData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFFData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
