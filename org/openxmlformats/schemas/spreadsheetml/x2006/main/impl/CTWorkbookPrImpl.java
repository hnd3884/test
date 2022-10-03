package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUpdateLinks;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STObjects;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTWorkbookPrImpl extends XmlComplexContentImpl implements CTWorkbookPr
{
    private static final long serialVersionUID = 1L;
    private static final QName DATE1904$0;
    private static final QName SHOWOBJECTS$2;
    private static final QName SHOWBORDERUNSELECTEDTABLES$4;
    private static final QName FILTERPRIVACY$6;
    private static final QName PROMPTEDSOLUTIONS$8;
    private static final QName SHOWINKANNOTATION$10;
    private static final QName BACKUPFILE$12;
    private static final QName SAVEEXTERNALLINKVALUES$14;
    private static final QName UPDATELINKS$16;
    private static final QName CODENAME$18;
    private static final QName HIDEPIVOTFIELDLIST$20;
    private static final QName SHOWPIVOTCHARTFILTER$22;
    private static final QName ALLOWREFRESHQUERY$24;
    private static final QName PUBLISHITEMS$26;
    private static final QName CHECKCOMPATIBILITY$28;
    private static final QName AUTOCOMPRESSPICTURES$30;
    private static final QName REFRESHALLCONNECTIONS$32;
    private static final QName DEFAULTTHEMEVERSION$34;
    
    public CTWorkbookPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.DATE1904$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.DATE1904$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.DATE1904$0) != null;
        }
    }
    
    public void setDate1904(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDate1904(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.DATE1904$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.DATE1904$0);
        }
    }
    
    public STObjects.Enum getShowObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWOBJECTS$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STObjects.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STObjects xgetShowObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STObjects stObjects = (STObjects)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            if (stObjects == null) {
                stObjects = (STObjects)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWOBJECTS$2);
            }
            return stObjects;
        }
    }
    
    public boolean isSetShowObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2) != null;
        }
    }
    
    public void setShowObjects(final STObjects.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetShowObjects(final STObjects stObjects) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STObjects stObjects2 = (STObjects)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            if (stObjects2 == null) {
                stObjects2 = (STObjects)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWOBJECTS$2);
            }
            stObjects2.set((XmlObject)stObjects);
        }
    }
    
    public void unsetShowObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.SHOWOBJECTS$2);
        }
    }
    
    public boolean getShowBorderUnselectedTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowBorderUnselectedTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowBorderUnselectedTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4) != null;
        }
    }
    
    public void setShowBorderUnselectedTables(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowBorderUnselectedTables(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowBorderUnselectedTables() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.SHOWBORDERUNSELECTEDTABLES$4);
        }
    }
    
    public boolean getFilterPrivacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.FILTERPRIVACY$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFilterPrivacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.FILTERPRIVACY$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFilterPrivacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6) != null;
        }
    }
    
    public void setFilterPrivacy(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFilterPrivacy(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.FILTERPRIVACY$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFilterPrivacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.FILTERPRIVACY$6);
        }
    }
    
    public boolean getPromptedSolutions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPromptedSolutions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPromptedSolutions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8) != null;
        }
    }
    
    public void setPromptedSolutions(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPromptedSolutions(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPromptedSolutions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.PROMPTEDSOLUTIONS$8);
        }
    }
    
    public boolean getShowInkAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowInkAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowInkAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10) != null;
        }
    }
    
    public void setShowInkAnnotation(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowInkAnnotation(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowInkAnnotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.SHOWINKANNOTATION$10);
        }
    }
    
    public boolean getBackupFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.BACKUPFILE$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBackupFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.BACKUPFILE$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBackupFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12) != null;
        }
    }
    
    public void setBackupFile(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBackupFile(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.BACKUPFILE$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBackupFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.BACKUPFILE$12);
        }
    }
    
    public boolean getSaveExternalLinkValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSaveExternalLinkValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSaveExternalLinkValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14) != null;
        }
    }
    
    public void setSaveExternalLinkValues(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSaveExternalLinkValues(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSaveExternalLinkValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.SAVEEXTERNALLINKVALUES$14);
        }
    }
    
    public STUpdateLinks.Enum getUpdateLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.UPDATELINKS$16);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STUpdateLinks.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STUpdateLinks xgetUpdateLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUpdateLinks stUpdateLinks = (STUpdateLinks)this.get_store().find_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            if (stUpdateLinks == null) {
                stUpdateLinks = (STUpdateLinks)this.get_default_attribute_value(CTWorkbookPrImpl.UPDATELINKS$16);
            }
            return stUpdateLinks;
        }
    }
    
    public boolean isSetUpdateLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16) != null;
        }
    }
    
    public void setUpdateLinks(final STUpdateLinks.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUpdateLinks(final STUpdateLinks stUpdateLinks) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUpdateLinks stUpdateLinks2 = (STUpdateLinks)this.get_store().find_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            if (stUpdateLinks2 == null) {
                stUpdateLinks2 = (STUpdateLinks)this.get_store().add_attribute_user(CTWorkbookPrImpl.UPDATELINKS$16);
            }
            stUpdateLinks2.set((XmlObject)stUpdateLinks);
        }
    }
    
    public void unsetUpdateLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.UPDATELINKS$16);
        }
    }
    
    public String getCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.CODENAME$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTWorkbookPrImpl.CODENAME$18);
        }
    }
    
    public boolean isSetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.CODENAME$18) != null;
        }
    }
    
    public void setCodeName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.CODENAME$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.CODENAME$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCodeName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTWorkbookPrImpl.CODENAME$18);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTWorkbookPrImpl.CODENAME$18);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.CODENAME$18);
        }
    }
    
    public boolean getHidePivotFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidePivotFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHidePivotFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20) != null;
        }
    }
    
    public void setHidePivotFieldList(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidePivotFieldList(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidePivotFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.HIDEPIVOTFIELDLIST$20);
        }
    }
    
    public boolean getShowPivotChartFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowPivotChartFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowPivotChartFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22) != null;
        }
    }
    
    public void setShowPivotChartFilter(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowPivotChartFilter(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowPivotChartFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.SHOWPIVOTCHARTFILTER$22);
        }
    }
    
    public boolean getAllowRefreshQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAllowRefreshQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAllowRefreshQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24) != null;
        }
    }
    
    public void setAllowRefreshQuery(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAllowRefreshQuery(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAllowRefreshQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.ALLOWREFRESHQUERY$24);
        }
    }
    
    public boolean getPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.PUBLISHITEMS$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.PUBLISHITEMS$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26) != null;
        }
    }
    
    public void setPublishItems(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPublishItems(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.PUBLISHITEMS$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.PUBLISHITEMS$26);
        }
    }
    
    public boolean getCheckCompatibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCheckCompatibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCheckCompatibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28) != null;
        }
    }
    
    public void setCheckCompatibility(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCheckCompatibility(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCheckCompatibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.CHECKCOMPATIBILITY$28);
        }
    }
    
    public boolean getAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30) != null;
        }
    }
    
    public void setAutoCompressPictures(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoCompressPictures(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.AUTOCOMPRESSPICTURES$30);
        }
    }
    
    public boolean getRefreshAllConnections() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRefreshAllConnections() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRefreshAllConnections() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32) != null;
        }
    }
    
    public void setRefreshAllConnections(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRefreshAllConnections(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRefreshAllConnections() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.REFRESHALLCONNECTIONS$32);
        }
    }
    
    public long getDefaultThemeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDefaultThemeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
        }
    }
    
    public boolean isSetDefaultThemeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34) != null;
        }
    }
    
    public void setDefaultThemeVersion(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDefaultThemeVersion(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDefaultThemeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTWorkbookPrImpl.DEFAULTTHEMEVERSION$34);
        }
    }
    
    static {
        DATE1904$0 = new QName("", "date1904");
        SHOWOBJECTS$2 = new QName("", "showObjects");
        SHOWBORDERUNSELECTEDTABLES$4 = new QName("", "showBorderUnselectedTables");
        FILTERPRIVACY$6 = new QName("", "filterPrivacy");
        PROMPTEDSOLUTIONS$8 = new QName("", "promptedSolutions");
        SHOWINKANNOTATION$10 = new QName("", "showInkAnnotation");
        BACKUPFILE$12 = new QName("", "backupFile");
        SAVEEXTERNALLINKVALUES$14 = new QName("", "saveExternalLinkValues");
        UPDATELINKS$16 = new QName("", "updateLinks");
        CODENAME$18 = new QName("", "codeName");
        HIDEPIVOTFIELDLIST$20 = new QName("", "hidePivotFieldList");
        SHOWPIVOTCHARTFILTER$22 = new QName("", "showPivotChartFilter");
        ALLOWREFRESHQUERY$24 = new QName("", "allowRefreshQuery");
        PUBLISHITEMS$26 = new QName("", "publishItems");
        CHECKCOMPATIBILITY$28 = new QName("", "checkCompatibility");
        AUTOCOMPRESSPICTURES$30 = new QName("", "autoCompressPictures");
        REFRESHALLCONNECTIONS$32 = new QName("", "refreshAllConnections");
        DEFAULTTHEMEVERSION$34 = new QName("", "defaultThemeVersion");
    }
}
