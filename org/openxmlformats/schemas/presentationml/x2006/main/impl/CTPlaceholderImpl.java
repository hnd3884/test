package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderSize;
import org.openxmlformats.schemas.presentationml.x2006.main.STDirection;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPlaceholderImpl extends XmlComplexContentImpl implements CTPlaceholder
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName TYPE$2;
    private static final QName ORIENT$4;
    private static final QName SZ$6;
    private static final QName IDX$8;
    private static final QName HASCUSTOMPROMPT$10;
    
    public CTPlaceholderImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTPlaceholderImpl.EXTLST$0, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlaceholderImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTPlaceholderImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTPlaceholderImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlaceholderImpl.EXTLST$0, 0);
        }
    }
    
    public STPlaceholderType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPlaceholderImpl.TYPE$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPlaceholderType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPlaceholderType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPlaceholderType stPlaceholderType = (STPlaceholderType)this.get_store().find_attribute_user(CTPlaceholderImpl.TYPE$2);
            if (stPlaceholderType == null) {
                stPlaceholderType = (STPlaceholderType)this.get_default_attribute_value(CTPlaceholderImpl.TYPE$2);
            }
            return stPlaceholderType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPlaceholderImpl.TYPE$2) != null;
        }
    }
    
    public void setType(final STPlaceholderType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPlaceholderImpl.TYPE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STPlaceholderType stPlaceholderType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPlaceholderType stPlaceholderType2 = (STPlaceholderType)this.get_store().find_attribute_user(CTPlaceholderImpl.TYPE$2);
            if (stPlaceholderType2 == null) {
                stPlaceholderType2 = (STPlaceholderType)this.get_store().add_attribute_user(CTPlaceholderImpl.TYPE$2);
            }
            stPlaceholderType2.set((XmlObject)stPlaceholderType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPlaceholderImpl.TYPE$2);
        }
    }
    
    public STDirection.Enum getOrient() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.ORIENT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPlaceholderImpl.ORIENT$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDirection.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDirection xgetOrient() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDirection stDirection = (STDirection)this.get_store().find_attribute_user(CTPlaceholderImpl.ORIENT$4);
            if (stDirection == null) {
                stDirection = (STDirection)this.get_default_attribute_value(CTPlaceholderImpl.ORIENT$4);
            }
            return stDirection;
        }
    }
    
    public boolean isSetOrient() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPlaceholderImpl.ORIENT$4) != null;
        }
    }
    
    public void setOrient(final STDirection.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.ORIENT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPlaceholderImpl.ORIENT$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOrient(final STDirection stDirection) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDirection stDirection2 = (STDirection)this.get_store().find_attribute_user(CTPlaceholderImpl.ORIENT$4);
            if (stDirection2 == null) {
                stDirection2 = (STDirection)this.get_store().add_attribute_user(CTPlaceholderImpl.ORIENT$4);
            }
            stDirection2.set((XmlObject)stDirection);
        }
    }
    
    public void unsetOrient() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPlaceholderImpl.ORIENT$4);
        }
    }
    
    public STPlaceholderSize.Enum getSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.SZ$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPlaceholderImpl.SZ$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPlaceholderSize.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPlaceholderSize xgetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPlaceholderSize stPlaceholderSize = (STPlaceholderSize)this.get_store().find_attribute_user(CTPlaceholderImpl.SZ$6);
            if (stPlaceholderSize == null) {
                stPlaceholderSize = (STPlaceholderSize)this.get_default_attribute_value(CTPlaceholderImpl.SZ$6);
            }
            return stPlaceholderSize;
        }
    }
    
    public boolean isSetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPlaceholderImpl.SZ$6) != null;
        }
    }
    
    public void setSz(final STPlaceholderSize.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.SZ$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPlaceholderImpl.SZ$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSz(final STPlaceholderSize stPlaceholderSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPlaceholderSize stPlaceholderSize2 = (STPlaceholderSize)this.get_store().find_attribute_user(CTPlaceholderImpl.SZ$6);
            if (stPlaceholderSize2 == null) {
                stPlaceholderSize2 = (STPlaceholderSize)this.get_store().add_attribute_user(CTPlaceholderImpl.SZ$6);
            }
            stPlaceholderSize2.set((XmlObject)stPlaceholderSize);
        }
    }
    
    public void unsetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPlaceholderImpl.SZ$6);
        }
    }
    
    public long getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.IDX$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPlaceholderImpl.IDX$8);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPlaceholderImpl.IDX$8);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPlaceholderImpl.IDX$8);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPlaceholderImpl.IDX$8) != null;
        }
    }
    
    public void setIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.IDX$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPlaceholderImpl.IDX$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPlaceholderImpl.IDX$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPlaceholderImpl.IDX$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPlaceholderImpl.IDX$8);
        }
    }
    
    public boolean getHasCustomPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHasCustomPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHasCustomPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10) != null;
        }
    }
    
    public void setHasCustomPrompt(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHasCustomPrompt(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHasCustomPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPlaceholderImpl.HASCUSTOMPROMPT$10);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        TYPE$2 = new QName("", "type");
        ORIENT$4 = new QName("", "orient");
        SZ$6 = new QName("", "sz");
        IDX$8 = new QName("", "idx");
        HASCUSTOMPROMPT$10 = new QName("", "hasCustomPrompt");
    }
}
