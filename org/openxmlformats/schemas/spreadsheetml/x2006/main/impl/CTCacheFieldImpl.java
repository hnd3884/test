package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTX;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFieldGroup;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSharedItems;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCacheFieldImpl extends XmlComplexContentImpl implements CTCacheField
{
    private static final long serialVersionUID = 1L;
    private static final QName SHAREDITEMS$0;
    private static final QName FIELDGROUP$2;
    private static final QName MPMAP$4;
    private static final QName EXTLST$6;
    private static final QName NAME$8;
    private static final QName CAPTION$10;
    private static final QName PROPERTYNAME$12;
    private static final QName SERVERFIELD$14;
    private static final QName UNIQUELIST$16;
    private static final QName NUMFMTID$18;
    private static final QName FORMULA$20;
    private static final QName SQLTYPE$22;
    private static final QName HIERARCHY$24;
    private static final QName LEVEL$26;
    private static final QName DATABASEFIELD$28;
    private static final QName MAPPINGCOUNT$30;
    private static final QName MEMBERPROPERTYFIELD$32;
    
    public CTCacheFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSharedItems getSharedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSharedItems ctSharedItems = (CTSharedItems)this.get_store().find_element_user(CTCacheFieldImpl.SHAREDITEMS$0, 0);
            if (ctSharedItems == null) {
                return null;
            }
            return ctSharedItems;
        }
    }
    
    public boolean isSetSharedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheFieldImpl.SHAREDITEMS$0) != 0;
        }
    }
    
    public void setSharedItems(final CTSharedItems ctSharedItems) {
        this.generatedSetterHelperImpl((XmlObject)ctSharedItems, CTCacheFieldImpl.SHAREDITEMS$0, 0, (short)1);
    }
    
    public CTSharedItems addNewSharedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSharedItems)this.get_store().add_element_user(CTCacheFieldImpl.SHAREDITEMS$0);
        }
    }
    
    public void unsetSharedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheFieldImpl.SHAREDITEMS$0, 0);
        }
    }
    
    public CTFieldGroup getFieldGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFieldGroup ctFieldGroup = (CTFieldGroup)this.get_store().find_element_user(CTCacheFieldImpl.FIELDGROUP$2, 0);
            if (ctFieldGroup == null) {
                return null;
            }
            return ctFieldGroup;
        }
    }
    
    public boolean isSetFieldGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheFieldImpl.FIELDGROUP$2) != 0;
        }
    }
    
    public void setFieldGroup(final CTFieldGroup ctFieldGroup) {
        this.generatedSetterHelperImpl((XmlObject)ctFieldGroup, CTCacheFieldImpl.FIELDGROUP$2, 0, (short)1);
    }
    
    public CTFieldGroup addNewFieldGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFieldGroup)this.get_store().add_element_user(CTCacheFieldImpl.FIELDGROUP$2);
        }
    }
    
    public void unsetFieldGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheFieldImpl.FIELDGROUP$2, 0);
        }
    }
    
    public List<CTX> getMpMapList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MpMapList extends AbstractList<CTX>
            {
                @Override
                public CTX get(final int n) {
                    return CTCacheFieldImpl.this.getMpMapArray(n);
                }
                
                @Override
                public CTX set(final int n, final CTX ctx) {
                    final CTX mpMapArray = CTCacheFieldImpl.this.getMpMapArray(n);
                    CTCacheFieldImpl.this.setMpMapArray(n, ctx);
                    return mpMapArray;
                }
                
                @Override
                public void add(final int n, final CTX ctx) {
                    CTCacheFieldImpl.this.insertNewMpMap(n).set((XmlObject)ctx);
                }
                
                @Override
                public CTX remove(final int n) {
                    final CTX mpMapArray = CTCacheFieldImpl.this.getMpMapArray(n);
                    CTCacheFieldImpl.this.removeMpMap(n);
                    return mpMapArray;
                }
                
                @Override
                public int size() {
                    return CTCacheFieldImpl.this.sizeOfMpMapArray();
                }
            }
            return new MpMapList();
        }
    }
    
    @Deprecated
    public CTX[] getMpMapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCacheFieldImpl.MPMAP$4, (List)list);
            final CTX[] array = new CTX[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTX getMpMapArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTX ctx = (CTX)this.get_store().find_element_user(CTCacheFieldImpl.MPMAP$4, n);
            if (ctx == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctx;
        }
    }
    
    public int sizeOfMpMapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheFieldImpl.MPMAP$4);
        }
    }
    
    public void setMpMapArray(final CTX[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCacheFieldImpl.MPMAP$4);
    }
    
    public void setMpMapArray(final int n, final CTX ctx) {
        this.generatedSetterHelperImpl((XmlObject)ctx, CTCacheFieldImpl.MPMAP$4, n, (short)2);
    }
    
    public CTX insertNewMpMap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTX)this.get_store().insert_element_user(CTCacheFieldImpl.MPMAP$4, n);
        }
    }
    
    public CTX addNewMpMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTX)this.get_store().add_element_user(CTCacheFieldImpl.MPMAP$4);
        }
    }
    
    public void removeMpMap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheFieldImpl.MPMAP$4, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCacheFieldImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheFieldImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCacheFieldImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCacheFieldImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheFieldImpl.EXTLST$6, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.NAME$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.NAME$8);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.NAME$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.NAME$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.NAME$8);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTCacheFieldImpl.NAME$8);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public String getCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.CAPTION$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.CAPTION$10);
        }
    }
    
    public boolean isSetCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.CAPTION$10) != null;
        }
    }
    
    public void setCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.CAPTION$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.CAPTION$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.CAPTION$10);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTCacheFieldImpl.CAPTION$10);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.CAPTION$10);
        }
    }
    
    public String getPropertyName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetPropertyName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
        }
    }
    
    public boolean isSetPropertyName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12) != null;
        }
    }
    
    public void setPropertyName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPropertyName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTCacheFieldImpl.PROPERTYNAME$12);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetPropertyName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.PROPERTYNAME$12);
        }
    }
    
    public boolean getServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.SERVERFIELD$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCacheFieldImpl.SERVERFIELD$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.SERVERFIELD$14) != null;
        }
    }
    
    public void setServerField(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetServerField(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCacheFieldImpl.SERVERFIELD$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.SERVERFIELD$14);
        }
    }
    
    public boolean getUniqueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.UNIQUELIST$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUniqueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCacheFieldImpl.UNIQUELIST$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUniqueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.UNIQUELIST$16) != null;
        }
    }
    
    public void setUniqueList(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUniqueList(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCacheFieldImpl.UNIQUELIST$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUniqueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.UNIQUELIST$16);
        }
    }
    
    public long getNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STNumFmtId xgetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumFmtId)this.get_store().find_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
        }
    }
    
    public boolean isSetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.NUMFMTID$18) != null;
        }
    }
    
    public void setNumFmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetNumFmtId(final STNumFmtId stNumFmtId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumFmtId stNumFmtId2 = (STNumFmtId)this.get_store().find_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
            if (stNumFmtId2 == null) {
                stNumFmtId2 = (STNumFmtId)this.get_store().add_attribute_user(CTCacheFieldImpl.NUMFMTID$18);
            }
            stNumFmtId2.set((XmlObject)stNumFmtId);
        }
    }
    
    public void unsetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.NUMFMTID$18);
        }
    }
    
    public String getFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.FORMULA$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.FORMULA$20);
        }
    }
    
    public boolean isSetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.FORMULA$20) != null;
        }
    }
    
    public void setFormula(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.FORMULA$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.FORMULA$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormula(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTCacheFieldImpl.FORMULA$20);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTCacheFieldImpl.FORMULA$20);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.FORMULA$20);
        }
    }
    
    public int getSqlType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.SQLTYPE$22);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetSqlType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTCacheFieldImpl.SQLTYPE$22);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetSqlType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.SQLTYPE$22) != null;
        }
    }
    
    public void setSqlType(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSqlType(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCacheFieldImpl.SQLTYPE$22);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetSqlType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.SQLTYPE$22);
        }
    }
    
    public int getHierarchy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.HIERARCHY$24);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetHierarchy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTCacheFieldImpl.HIERARCHY$24);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetHierarchy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.HIERARCHY$24) != null;
        }
    }
    
    public void setHierarchy(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetHierarchy(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCacheFieldImpl.HIERARCHY$24);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetHierarchy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.HIERARCHY$24);
        }
    }
    
    public long getLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.LEVEL$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.LEVEL$26);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldImpl.LEVEL$26);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCacheFieldImpl.LEVEL$26);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.LEVEL$26) != null;
        }
    }
    
    public void setLevel(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.LEVEL$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.LEVEL$26);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetLevel(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldImpl.LEVEL$26);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCacheFieldImpl.LEVEL$26);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.LEVEL$26);
        }
    }
    
    public boolean getDatabaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.DATABASEFIELD$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDatabaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCacheFieldImpl.DATABASEFIELD$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDatabaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28) != null;
        }
    }
    
    public void setDatabaseField(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDatabaseField(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCacheFieldImpl.DATABASEFIELD$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDatabaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.DATABASEFIELD$28);
        }
    }
    
    public long getMappingCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMappingCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
        }
    }
    
    public boolean isSetMappingCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30) != null;
        }
    }
    
    public void setMappingCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMappingCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCacheFieldImpl.MAPPINGCOUNT$30);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMappingCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.MAPPINGCOUNT$30);
        }
    }
    
    public boolean getMemberPropertyField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMemberPropertyField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMemberPropertyField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32) != null;
        }
    }
    
    public void setMemberPropertyField(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMemberPropertyField(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMemberPropertyField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldImpl.MEMBERPROPERTYFIELD$32);
        }
    }
    
    static {
        SHAREDITEMS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sharedItems");
        FIELDGROUP$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fieldGroup");
        MPMAP$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "mpMap");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        NAME$8 = new QName("", "name");
        CAPTION$10 = new QName("", "caption");
        PROPERTYNAME$12 = new QName("", "propertyName");
        SERVERFIELD$14 = new QName("", "serverField");
        UNIQUELIST$16 = new QName("", "uniqueList");
        NUMFMTID$18 = new QName("", "numFmtId");
        FORMULA$20 = new QName("", "formula");
        SQLTYPE$22 = new QName("", "sqlType");
        HIERARCHY$24 = new QName("", "hierarchy");
        LEVEL$26 = new QName("", "level");
        DATABASEFIELD$28 = new QName("", "databaseField");
        MAPPINGCOUNT$30 = new QName("", "mappingCount");
        MEMBERPROPERTYFIELD$32 = new QName("", "memberPropertyField");
    }
}
