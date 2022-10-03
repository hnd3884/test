package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontSchemeImpl extends XmlComplexContentImpl implements CTFontScheme
{
    private static final long serialVersionUID = 1L;
    private static final QName MAJORFONT$0;
    private static final QName MINORFONT$2;
    private static final QName EXTLST$4;
    private static final QName NAME$6;
    
    public CTFontSchemeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFontCollection getMajorFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontCollection collection = (CTFontCollection)this.get_store().find_element_user(CTFontSchemeImpl.MAJORFONT$0, 0);
            if (collection == null) {
                return null;
            }
            return collection;
        }
    }
    
    public void setMajorFont(final CTFontCollection collection) {
        this.generatedSetterHelperImpl((XmlObject)collection, CTFontSchemeImpl.MAJORFONT$0, 0, (short)1);
    }
    
    public CTFontCollection addNewMajorFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontCollection)this.get_store().add_element_user(CTFontSchemeImpl.MAJORFONT$0);
        }
    }
    
    public CTFontCollection getMinorFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontCollection collection = (CTFontCollection)this.get_store().find_element_user(CTFontSchemeImpl.MINORFONT$2, 0);
            if (collection == null) {
                return null;
            }
            return collection;
        }
    }
    
    public void setMinorFont(final CTFontCollection collection) {
        this.generatedSetterHelperImpl((XmlObject)collection, CTFontSchemeImpl.MINORFONT$2, 0, (short)1);
    }
    
    public CTFontCollection addNewMinorFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontCollection)this.get_store().add_element_user(CTFontSchemeImpl.MINORFONT$2);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTFontSchemeImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontSchemeImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTFontSchemeImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTFontSchemeImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontSchemeImpl.EXTLST$4, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSchemeImpl.NAME$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFontSchemeImpl.NAME$6);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSchemeImpl.NAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontSchemeImpl.NAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFontSchemeImpl.NAME$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFontSchemeImpl.NAME$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        MAJORFONT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "majorFont");
        MINORFONT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "minorFont");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        NAME$6 = new QName("", "name");
    }
}
