package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.IconType;
import com.microsoft.schemas.office.visio.x2012.main.RelType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MasterTypeImpl extends XmlComplexContentImpl implements MasterType
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGESHEET$0;
    private static final QName REL$2;
    private static final QName ICON$4;
    private static final QName ID$6;
    private static final QName BASEID$8;
    private static final QName UNIQUEID$10;
    private static final QName MATCHBYNAME$12;
    private static final QName NAME$14;
    private static final QName NAMEU$16;
    private static final QName ISCUSTOMNAME$18;
    private static final QName ISCUSTOMNAMEU$20;
    private static final QName ICONSIZE$22;
    private static final QName PATTERNFLAGS$24;
    private static final QName PROMPT$26;
    private static final QName HIDDEN$28;
    private static final QName ICONUPDATE$30;
    private static final QName ALIGNNAME$32;
    private static final QName MASTERTYPE$34;
    
    public MasterTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public PageSheetType getPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PageSheetType pageSheetType = (PageSheetType)this.get_store().find_element_user(MasterTypeImpl.PAGESHEET$0, 0);
            if (pageSheetType == null) {
                return null;
            }
            return pageSheetType;
        }
    }
    
    public boolean isSetPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(MasterTypeImpl.PAGESHEET$0) != 0;
        }
    }
    
    public void setPageSheet(final PageSheetType pageSheetType) {
        this.generatedSetterHelperImpl((XmlObject)pageSheetType, MasterTypeImpl.PAGESHEET$0, 0, (short)1);
    }
    
    public PageSheetType addNewPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageSheetType)this.get_store().add_element_user(MasterTypeImpl.PAGESHEET$0);
        }
    }
    
    public void unsetPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(MasterTypeImpl.PAGESHEET$0, 0);
        }
    }
    
    public RelType getRel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RelType relType = (RelType)this.get_store().find_element_user(MasterTypeImpl.REL$2, 0);
            if (relType == null) {
                return null;
            }
            return relType;
        }
    }
    
    public void setRel(final RelType relType) {
        this.generatedSetterHelperImpl((XmlObject)relType, MasterTypeImpl.REL$2, 0, (short)1);
    }
    
    public RelType addNewRel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RelType)this.get_store().add_element_user(MasterTypeImpl.REL$2);
        }
    }
    
    public IconType getIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final IconType iconType = (IconType)this.get_store().find_element_user(MasterTypeImpl.ICON$4, 0);
            if (iconType == null) {
                return null;
            }
            return iconType;
        }
    }
    
    public boolean isSetIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(MasterTypeImpl.ICON$4) != 0;
        }
    }
    
    public void setIcon(final IconType iconType) {
        this.generatedSetterHelperImpl((XmlObject)iconType, MasterTypeImpl.ICON$4, 0, (short)1);
    }
    
    public IconType addNewIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (IconType)this.get_store().add_element_user(MasterTypeImpl.ICON$4);
        }
    }
    
    public void unsetIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(MasterTypeImpl.ICON$4, 0);
        }
    }
    
    public long getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ID$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(MasterTypeImpl.ID$6);
        }
    }
    
    public void setID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ID$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(MasterTypeImpl.ID$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(MasterTypeImpl.ID$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getBaseID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.BASEID$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBaseID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.BASEID$8);
        }
    }
    
    public boolean isSetBaseID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.BASEID$8) != null;
        }
    }
    
    public void setBaseID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.BASEID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.BASEID$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBaseID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.BASEID$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(MasterTypeImpl.BASEID$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBaseID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.BASEID$8);
        }
    }
    
    public String getUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.UNIQUEID$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.UNIQUEID$10);
        }
    }
    
    public boolean isSetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.UNIQUEID$10) != null;
        }
    }
    
    public void setUniqueID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.UNIQUEID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.UNIQUEID$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUniqueID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.UNIQUEID$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(MasterTypeImpl.UNIQUEID$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.UNIQUEID$10);
        }
    }
    
    public boolean getMatchByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMatchByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
        }
    }
    
    public boolean isSetMatchByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.MATCHBYNAME$12) != null;
        }
    }
    
    public void setMatchByName(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMatchByName(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(MasterTypeImpl.MATCHBYNAME$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMatchByName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.MATCHBYNAME$12);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.NAME$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.NAME$14);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.NAME$14) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.NAME$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.NAME$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.NAME$14);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(MasterTypeImpl.NAME$14);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.NAME$14);
        }
    }
    
    public String getNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.NAMEU$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.NAMEU$16);
        }
    }
    
    public boolean isSetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.NAMEU$16) != null;
        }
    }
    
    public void setNameU(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.NAMEU$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.NAMEU$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetNameU(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.NAMEU$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(MasterTypeImpl.NAMEU$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.NAMEU$16);
        }
    }
    
    public boolean getIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
        }
    }
    
    public boolean isSetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18) != null;
        }
    }
    
    public void setIsCustomName(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIsCustomName(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(MasterTypeImpl.ISCUSTOMNAME$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.ISCUSTOMNAME$18);
        }
    }
    
    public boolean getIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
        }
    }
    
    public boolean isSetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20) != null;
        }
    }
    
    public void setIsCustomNameU(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIsCustomNameU(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(MasterTypeImpl.ISCUSTOMNAMEU$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.ISCUSTOMNAMEU$20);
        }
    }
    
    public int getIconSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ICONSIZE$22);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlUnsignedShort xgetIconSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.ICONSIZE$22);
        }
    }
    
    public boolean isSetIconSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.ICONSIZE$22) != null;
        }
    }
    
    public void setIconSize(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ICONSIZE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ICONSIZE$22);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetIconSize(final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.ICONSIZE$22);
            if (xmlUnsignedShort2 == null) {
                xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().add_attribute_user(MasterTypeImpl.ICONSIZE$22);
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void unsetIconSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.ICONSIZE$22);
        }
    }
    
    public int getPatternFlags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlUnsignedShort xgetPatternFlags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
        }
    }
    
    public boolean isSetPatternFlags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.PATTERNFLAGS$24) != null;
        }
    }
    
    public void setPatternFlags(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetPatternFlags(final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
            if (xmlUnsignedShort2 == null) {
                xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().add_attribute_user(MasterTypeImpl.PATTERNFLAGS$24);
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void unsetPatternFlags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.PATTERNFLAGS$24);
        }
    }
    
    public String getPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.PROMPT$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.PROMPT$26);
        }
    }
    
    public boolean isSetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.PROMPT$26) != null;
        }
    }
    
    public void setPrompt(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.PROMPT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.PROMPT$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPrompt(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(MasterTypeImpl.PROMPT$26);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(MasterTypeImpl.PROMPT$26);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.PROMPT$26);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.HIDDEN$28);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.HIDDEN$28);
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.HIDDEN$28) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.HIDDEN$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.HIDDEN$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.HIDDEN$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(MasterTypeImpl.HIDDEN$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.HIDDEN$28);
        }
    }
    
    public boolean getIconUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ICONUPDATE$30);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIconUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ICONUPDATE$30);
        }
    }
    
    public boolean isSetIconUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.ICONUPDATE$30) != null;
        }
    }
    
    public void setIconUpdate(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ICONUPDATE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ICONUPDATE$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIconUpdate(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(MasterTypeImpl.ICONUPDATE$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(MasterTypeImpl.ICONUPDATE$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIconUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.ICONUPDATE$30);
        }
    }
    
    public int getAlignName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ALIGNNAME$32);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlUnsignedShort xgetAlignName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.ALIGNNAME$32);
        }
    }
    
    public boolean isSetAlignName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.ALIGNNAME$32) != null;
        }
    }
    
    public void setAlignName(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.ALIGNNAME$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.ALIGNNAME$32);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetAlignName(final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.ALIGNNAME$32);
            if (xmlUnsignedShort2 == null) {
                xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().add_attribute_user(MasterTypeImpl.ALIGNNAME$32);
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void unsetAlignName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.ALIGNNAME$32);
        }
    }
    
    public int getMasterType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.MASTERTYPE$34);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlUnsignedShort xgetMasterType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.MASTERTYPE$34);
        }
    }
    
    public boolean isSetMasterType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(MasterTypeImpl.MASTERTYPE$34) != null;
        }
    }
    
    public void setMasterType(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(MasterTypeImpl.MASTERTYPE$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(MasterTypeImpl.MASTERTYPE$34);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMasterType(final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_attribute_user(MasterTypeImpl.MASTERTYPE$34);
            if (xmlUnsignedShort2 == null) {
                xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().add_attribute_user(MasterTypeImpl.MASTERTYPE$34);
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void unsetMasterType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(MasterTypeImpl.MASTERTYPE$34);
        }
    }
    
    static {
        PAGESHEET$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "PageSheet");
        REL$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Rel");
        ICON$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Icon");
        ID$6 = new QName("", "ID");
        BASEID$8 = new QName("", "BaseID");
        UNIQUEID$10 = new QName("", "UniqueID");
        MATCHBYNAME$12 = new QName("", "MatchByName");
        NAME$14 = new QName("", "Name");
        NAMEU$16 = new QName("", "NameU");
        ISCUSTOMNAME$18 = new QName("", "IsCustomName");
        ISCUSTOMNAMEU$20 = new QName("", "IsCustomNameU");
        ICONSIZE$22 = new QName("", "IconSize");
        PATTERNFLAGS$24 = new QName("", "PatternFlags");
        PROMPT$26 = new QName("", "Prompt");
        HIDDEN$28 = new QName("", "Hidden");
        ICONUPDATE$30 = new QName("", "IconUpdate");
        ALIGNNAME$32 = new QName("", "AlignName");
        MASTERTYPE$34 = new QName("", "MasterType");
    }
}
