package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableImpl extends XmlComplexContentImpl implements CTTable
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTOFILTER$0;
    private static final QName SORTSTATE$2;
    private static final QName TABLECOLUMNS$4;
    private static final QName TABLESTYLEINFO$6;
    private static final QName EXTLST$8;
    private static final QName ID$10;
    private static final QName NAME$12;
    private static final QName DISPLAYNAME$14;
    private static final QName COMMENT$16;
    private static final QName REF$18;
    private static final QName TABLETYPE$20;
    private static final QName HEADERROWCOUNT$22;
    private static final QName INSERTROW$24;
    private static final QName INSERTROWSHIFT$26;
    private static final QName TOTALSROWCOUNT$28;
    private static final QName TOTALSROWSHOWN$30;
    private static final QName PUBLISHED$32;
    private static final QName HEADERROWDXFID$34;
    private static final QName DATADXFID$36;
    private static final QName TOTALSROWDXFID$38;
    private static final QName HEADERROWBORDERDXFID$40;
    private static final QName TABLEBORDERDXFID$42;
    private static final QName TOTALSROWBORDERDXFID$44;
    private static final QName HEADERROWCELLSTYLE$46;
    private static final QName DATACELLSTYLE$48;
    private static final QName TOTALSROWCELLSTYLE$50;
    private static final QName CONNECTIONID$52;
    
    public CTTableImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAutoFilter getAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAutoFilter ctAutoFilter = (CTAutoFilter)this.get_store().find_element_user(CTTableImpl.AUTOFILTER$0, 0);
            if (ctAutoFilter == null) {
                return null;
            }
            return ctAutoFilter;
        }
    }
    
    public boolean isSetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.AUTOFILTER$0) != 0;
        }
    }
    
    public void setAutoFilter(final CTAutoFilter ctAutoFilter) {
        this.generatedSetterHelperImpl((XmlObject)ctAutoFilter, CTTableImpl.AUTOFILTER$0, 0, (short)1);
    }
    
    public CTAutoFilter addNewAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAutoFilter)this.get_store().add_element_user(CTTableImpl.AUTOFILTER$0);
        }
    }
    
    public void unsetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.AUTOFILTER$0, 0);
        }
    }
    
    public CTSortState getSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSortState ctSortState = (CTSortState)this.get_store().find_element_user(CTTableImpl.SORTSTATE$2, 0);
            if (ctSortState == null) {
                return null;
            }
            return ctSortState;
        }
    }
    
    public boolean isSetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.SORTSTATE$2) != 0;
        }
    }
    
    public void setSortState(final CTSortState ctSortState) {
        this.generatedSetterHelperImpl((XmlObject)ctSortState, CTTableImpl.SORTSTATE$2, 0, (short)1);
    }
    
    public CTSortState addNewSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSortState)this.get_store().add_element_user(CTTableImpl.SORTSTATE$2);
        }
    }
    
    public void unsetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.SORTSTATE$2, 0);
        }
    }
    
    public CTTableColumns getTableColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableColumns ctTableColumns = (CTTableColumns)this.get_store().find_element_user(CTTableImpl.TABLECOLUMNS$4, 0);
            if (ctTableColumns == null) {
                return null;
            }
            return ctTableColumns;
        }
    }
    
    public void setTableColumns(final CTTableColumns ctTableColumns) {
        this.generatedSetterHelperImpl((XmlObject)ctTableColumns, CTTableImpl.TABLECOLUMNS$4, 0, (short)1);
    }
    
    public CTTableColumns addNewTableColumns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableColumns)this.get_store().add_element_user(CTTableImpl.TABLECOLUMNS$4);
        }
    }
    
    public CTTableStyleInfo getTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyleInfo ctTableStyleInfo = (CTTableStyleInfo)this.get_store().find_element_user(CTTableImpl.TABLESTYLEINFO$6, 0);
            if (ctTableStyleInfo == null) {
                return null;
            }
            return ctTableStyleInfo;
        }
    }
    
    public boolean isSetTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.TABLESTYLEINFO$6) != 0;
        }
    }
    
    public void setTableStyleInfo(final CTTableStyleInfo ctTableStyleInfo) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyleInfo, CTTableImpl.TABLESTYLEINFO$6, 0, (short)1);
    }
    
    public CTTableStyleInfo addNewTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleInfo)this.get_store().add_element_user(CTTableImpl.TABLESTYLEINFO$6);
        }
    }
    
    public void unsetTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.TABLESTYLEINFO$6, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTTableImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTTableImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.EXTLST$8, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.ID$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.ID$10);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.ID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.ID$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.ID$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableImpl.ID$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.NAME$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.NAME$12);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.NAME$12) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.NAME$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.NAME$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.NAME$12);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.NAME$12);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.NAME$12);
        }
    }
    
    public String getDisplayName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DISPLAYNAME$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDisplayName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.DISPLAYNAME$14);
        }
    }
    
    public void setDisplayName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DISPLAYNAME$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.DISPLAYNAME$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDisplayName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.DISPLAYNAME$14);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.DISPLAYNAME$14);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public String getComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.COMMENT$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.COMMENT$16);
        }
    }
    
    public boolean isSetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.COMMENT$16) != null;
        }
    }
    
    public void setComment(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.COMMENT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.COMMENT$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetComment(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.COMMENT$16);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.COMMENT$16);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.COMMENT$16);
        }
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.REF$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTTableImpl.REF$18);
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.REF$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.REF$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTTableImpl.REF$18);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTTableImpl.REF$18);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public STTableType.Enum getTableType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TABLETYPE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.TABLETYPE$20);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTableType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTableType xgetTableType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTableType stTableType = (STTableType)this.get_store().find_attribute_user(CTTableImpl.TABLETYPE$20);
            if (stTableType == null) {
                stTableType = (STTableType)this.get_default_attribute_value(CTTableImpl.TABLETYPE$20);
            }
            return stTableType;
        }
    }
    
    public boolean isSetTableType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TABLETYPE$20) != null;
        }
    }
    
    public void setTableType(final STTableType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TABLETYPE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TABLETYPE$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTableType(final STTableType stTableType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTableType stTableType2 = (STTableType)this.get_store().find_attribute_user(CTTableImpl.TABLETYPE$20);
            if (stTableType2 == null) {
                stTableType2 = (STTableType)this.get_store().add_attribute_user(CTTableImpl.TABLETYPE$20);
            }
            stTableType2.set((XmlObject)stTableType);
        }
    }
    
    public void unsetTableType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TABLETYPE$20);
        }
    }
    
    public long getHeaderRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.HEADERROWCOUNT$22);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetHeaderRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTTableImpl.HEADERROWCOUNT$22);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetHeaderRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.HEADERROWCOUNT$22) != null;
        }
    }
    
    public void setHeaderRowCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetHeaderRowCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableImpl.HEADERROWCOUNT$22);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetHeaderRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.HEADERROWCOUNT$22);
        }
    }
    
    public boolean getInsertRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.INSERTROW$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.INSERTROW$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.INSERTROW$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableImpl.INSERTROW$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.INSERTROW$24) != null;
        }
    }
    
    public void setInsertRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.INSERTROW$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.INSERTROW$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.INSERTROW$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableImpl.INSERTROW$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.INSERTROW$24);
        }
    }
    
    public boolean getInsertRowShift() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.INSERTROWSHIFT$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertRowShift() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableImpl.INSERTROWSHIFT$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertRowShift() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.INSERTROWSHIFT$26) != null;
        }
    }
    
    public void setInsertRowShift(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertRowShift(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableImpl.INSERTROWSHIFT$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertRowShift() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.INSERTROWSHIFT$26);
        }
    }
    
    public long getTotalsRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.TOTALSROWCOUNT$28);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTotalsRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTTableImpl.TOTALSROWCOUNT$28);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetTotalsRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCOUNT$28) != null;
        }
    }
    
    public void setTotalsRowCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTotalsRowCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWCOUNT$28);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTotalsRowCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TOTALSROWCOUNT$28);
        }
    }
    
    public boolean getTotalsRowShown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.TOTALSROWSHOWN$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTotalsRowShown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableImpl.TOTALSROWSHOWN$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTotalsRowShown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TOTALSROWSHOWN$30) != null;
        }
    }
    
    public void setTotalsRowShown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTotalsRowShown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWSHOWN$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTotalsRowShown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TOTALSROWSHOWN$30);
        }
    }
    
    public boolean getPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.PUBLISHED$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableImpl.PUBLISHED$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.PUBLISHED$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableImpl.PUBLISHED$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.PUBLISHED$32) != null;
        }
    }
    
    public void setPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.PUBLISHED$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.PUBLISHED$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableImpl.PUBLISHED$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableImpl.PUBLISHED$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.PUBLISHED$32);
        }
    }
    
    public long getHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWDXFID$34);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.HEADERROWDXFID$34);
        }
    }
    
    public boolean isSetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.HEADERROWDXFID$34) != null;
        }
    }
    
    public void setHeaderRowDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWDXFID$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.HEADERROWDXFID$34);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetHeaderRowDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.HEADERROWDXFID$34);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.HEADERROWDXFID$34);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.HEADERROWDXFID$34);
        }
    }
    
    public long getDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DATADXFID$36);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.DATADXFID$36);
        }
    }
    
    public boolean isSetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.DATADXFID$36) != null;
        }
    }
    
    public void setDataDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DATADXFID$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.DATADXFID$36);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDataDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.DATADXFID$36);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.DATADXFID$36);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.DATADXFID$36);
        }
    }
    
    public long getTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
        }
    }
    
    public boolean isSetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TOTALSROWDXFID$38) != null;
        }
    }
    
    public void setTotalsRowDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTotalsRowDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWDXFID$38);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TOTALSROWDXFID$38);
        }
    }
    
    public long getHeaderRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetHeaderRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
        }
    }
    
    public boolean isSetHeaderRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40) != null;
        }
    }
    
    public void setHeaderRowBorderDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetHeaderRowBorderDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.HEADERROWBORDERDXFID$40);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetHeaderRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.HEADERROWBORDERDXFID$40);
        }
    }
    
    public long getTableBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetTableBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
        }
    }
    
    public boolean isSetTableBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TABLEBORDERDXFID$42) != null;
        }
    }
    
    public void setTableBorderDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTableBorderDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.TABLEBORDERDXFID$42);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetTableBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TABLEBORDERDXFID$42);
        }
    }
    
    public long getTotalsRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetTotalsRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
        }
    }
    
    public boolean isSetTotalsRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44) != null;
        }
    }
    
    public void setTotalsRowBorderDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTotalsRowBorderDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWBORDERDXFID$44);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetTotalsRowBorderDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TOTALSROWBORDERDXFID$44);
        }
    }
    
    public String getHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
        }
    }
    
    public boolean isSetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46) != null;
        }
    }
    
    public void setHeaderRowCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHeaderRowCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.HEADERROWCELLSTYLE$46);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.HEADERROWCELLSTYLE$46);
        }
    }
    
    public String getDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DATACELLSTYLE$48);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.DATACELLSTYLE$48);
        }
    }
    
    public boolean isSetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.DATACELLSTYLE$48) != null;
        }
    }
    
    public void setDataCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.DATACELLSTYLE$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.DATACELLSTYLE$48);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDataCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.DATACELLSTYLE$48);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.DATACELLSTYLE$48);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.DATACELLSTYLE$48);
        }
    }
    
    public String getTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
        }
    }
    
    public boolean isSetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50) != null;
        }
    }
    
    public void setTotalsRowCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTotalsRowCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableImpl.TOTALSROWCELLSTYLE$50);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.TOTALSROWCELLSTYLE$50);
        }
    }
    
    public long getConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.CONNECTIONID$52);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.CONNECTIONID$52);
        }
    }
    
    public boolean isSetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableImpl.CONNECTIONID$52) != null;
        }
    }
    
    public void setConnectionId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableImpl.CONNECTIONID$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableImpl.CONNECTIONID$52);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetConnectionId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableImpl.CONNECTIONID$52);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableImpl.CONNECTIONID$52);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableImpl.CONNECTIONID$52);
        }
    }
    
    static {
        AUTOFILTER$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "autoFilter");
        SORTSTATE$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sortState");
        TABLECOLUMNS$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableColumns");
        TABLESTYLEINFO$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableStyleInfo");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        ID$10 = new QName("", "id");
        NAME$12 = new QName("", "name");
        DISPLAYNAME$14 = new QName("", "displayName");
        COMMENT$16 = new QName("", "comment");
        REF$18 = new QName("", "ref");
        TABLETYPE$20 = new QName("", "tableType");
        HEADERROWCOUNT$22 = new QName("", "headerRowCount");
        INSERTROW$24 = new QName("", "insertRow");
        INSERTROWSHIFT$26 = new QName("", "insertRowShift");
        TOTALSROWCOUNT$28 = new QName("", "totalsRowCount");
        TOTALSROWSHOWN$30 = new QName("", "totalsRowShown");
        PUBLISHED$32 = new QName("", "published");
        HEADERROWDXFID$34 = new QName("", "headerRowDxfId");
        DATADXFID$36 = new QName("", "dataDxfId");
        TOTALSROWDXFID$38 = new QName("", "totalsRowDxfId");
        HEADERROWBORDERDXFID$40 = new QName("", "headerRowBorderDxfId");
        TABLEBORDERDXFID$42 = new QName("", "tableBorderDxfId");
        TOTALSROWBORDERDXFID$44 = new QName("", "totalsRowBorderDxfId");
        HEADERROWCELLSTYLE$46 = new QName("", "headerRowCellStyle");
        DATACELLSTYLE$48 = new QName("", "dataCellStyle");
        TOTALSROWCELLSTYLE$50 = new QName("", "totalsRowCellStyle");
        CONNECTIONID$52 = new QName("", "connectionId");
    }
}
