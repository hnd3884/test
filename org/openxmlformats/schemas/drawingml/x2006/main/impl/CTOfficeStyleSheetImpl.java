package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomColorList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorSchemeList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTObjectStyleDefaults;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOfficeStyleSheetImpl extends XmlComplexContentImpl implements CTOfficeStyleSheet
{
    private static final long serialVersionUID = 1L;
    private static final QName THEMEELEMENTS$0;
    private static final QName OBJECTDEFAULTS$2;
    private static final QName EXTRACLRSCHEMELST$4;
    private static final QName CUSTCLRLST$6;
    private static final QName EXTLST$8;
    private static final QName NAME$10;
    
    public CTOfficeStyleSheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBaseStyles getThemeElements() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBaseStyles ctBaseStyles = (CTBaseStyles)this.get_store().find_element_user(CTOfficeStyleSheetImpl.THEMEELEMENTS$0, 0);
            if (ctBaseStyles == null) {
                return null;
            }
            return ctBaseStyles;
        }
    }
    
    public void setThemeElements(final CTBaseStyles ctBaseStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctBaseStyles, CTOfficeStyleSheetImpl.THEMEELEMENTS$0, 0, (short)1);
    }
    
    public CTBaseStyles addNewThemeElements() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBaseStyles)this.get_store().add_element_user(CTOfficeStyleSheetImpl.THEMEELEMENTS$0);
        }
    }
    
    public CTObjectStyleDefaults getObjectDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTObjectStyleDefaults ctObjectStyleDefaults = (CTObjectStyleDefaults)this.get_store().find_element_user(CTOfficeStyleSheetImpl.OBJECTDEFAULTS$2, 0);
            if (ctObjectStyleDefaults == null) {
                return null;
            }
            return ctObjectStyleDefaults;
        }
    }
    
    public boolean isSetObjectDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOfficeStyleSheetImpl.OBJECTDEFAULTS$2) != 0;
        }
    }
    
    public void setObjectDefaults(final CTObjectStyleDefaults ctObjectStyleDefaults) {
        this.generatedSetterHelperImpl((XmlObject)ctObjectStyleDefaults, CTOfficeStyleSheetImpl.OBJECTDEFAULTS$2, 0, (short)1);
    }
    
    public CTObjectStyleDefaults addNewObjectDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTObjectStyleDefaults)this.get_store().add_element_user(CTOfficeStyleSheetImpl.OBJECTDEFAULTS$2);
        }
    }
    
    public void unsetObjectDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOfficeStyleSheetImpl.OBJECTDEFAULTS$2, 0);
        }
    }
    
    public CTColorSchemeList getExtraClrSchemeLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorSchemeList list = (CTColorSchemeList)this.get_store().find_element_user(CTOfficeStyleSheetImpl.EXTRACLRSCHEMELST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtraClrSchemeLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOfficeStyleSheetImpl.EXTRACLRSCHEMELST$4) != 0;
        }
    }
    
    public void setExtraClrSchemeLst(final CTColorSchemeList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTOfficeStyleSheetImpl.EXTRACLRSCHEMELST$4, 0, (short)1);
    }
    
    public CTColorSchemeList addNewExtraClrSchemeLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorSchemeList)this.get_store().add_element_user(CTOfficeStyleSheetImpl.EXTRACLRSCHEMELST$4);
        }
    }
    
    public void unsetExtraClrSchemeLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOfficeStyleSheetImpl.EXTRACLRSCHEMELST$4, 0);
        }
    }
    
    public CTCustomColorList getCustClrLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomColorList list = (CTCustomColorList)this.get_store().find_element_user(CTOfficeStyleSheetImpl.CUSTCLRLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustClrLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOfficeStyleSheetImpl.CUSTCLRLST$6) != 0;
        }
    }
    
    public void setCustClrLst(final CTCustomColorList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTOfficeStyleSheetImpl.CUSTCLRLST$6, 0, (short)1);
    }
    
    public CTCustomColorList addNewCustClrLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomColorList)this.get_store().add_element_user(CTOfficeStyleSheetImpl.CUSTCLRLST$6);
        }
    }
    
    public void unsetCustClrLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOfficeStyleSheetImpl.CUSTCLRLST$6, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTOfficeStyleSheetImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOfficeStyleSheetImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTOfficeStyleSheetImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTOfficeStyleSheetImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOfficeStyleSheetImpl.EXTLST$8, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOfficeStyleSheetImpl.NAME$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTOfficeStyleSheetImpl.NAME$10);
            }
            return xmlString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOfficeStyleSheetImpl.NAME$10) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTOfficeStyleSheetImpl.NAME$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOfficeStyleSheetImpl.NAME$10);
        }
    }
    
    static {
        THEMEELEMENTS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "themeElements");
        OBJECTDEFAULTS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "objectDefaults");
        EXTRACLRSCHEMELST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extraClrSchemeLst");
        CUSTCLRLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "custClrLst");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        NAME$10 = new QName("", "name");
    }
}
