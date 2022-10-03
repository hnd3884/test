package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellSpans;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRowImpl extends XmlComplexContentImpl implements CTRow
{
    private static final long serialVersionUID = 1L;
    private static final QName C$0;
    private static final QName EXTLST$2;
    private static final QName R$4;
    private static final QName SPANS$6;
    private static final QName S$8;
    private static final QName CUSTOMFORMAT$10;
    private static final QName HT$12;
    private static final QName HIDDEN$14;
    private static final QName CUSTOMHEIGHT$16;
    private static final QName OUTLINELEVEL$18;
    private static final QName COLLAPSED$20;
    private static final QName THICKTOP$22;
    private static final QName THICKBOT$24;
    private static final QName PH$26;
    
    public CTRowImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCell> getCList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CList extends AbstractList<CTCell>
            {
                @Override
                public CTCell get(final int n) {
                    return CTRowImpl.this.getCArray(n);
                }
                
                @Override
                public CTCell set(final int n, final CTCell ctCell) {
                    final CTCell cArray = CTRowImpl.this.getCArray(n);
                    CTRowImpl.this.setCArray(n, ctCell);
                    return cArray;
                }
                
                @Override
                public void add(final int n, final CTCell ctCell) {
                    CTRowImpl.this.insertNewC(n).set((XmlObject)ctCell);
                }
                
                @Override
                public CTCell remove(final int n) {
                    final CTCell cArray = CTRowImpl.this.getCArray(n);
                    CTRowImpl.this.removeC(n);
                    return cArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCArray();
                }
            }
            return new CList();
        }
    }
    
    @Deprecated
    public CTCell[] getCArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRowImpl.C$0, (List)list);
            final CTCell[] array = new CTCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCell getCArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCell ctCell = (CTCell)this.get_store().find_element_user(CTRowImpl.C$0, n);
            if (ctCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCell;
        }
    }
    
    public int sizeOfCArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.C$0);
        }
    }
    
    public void setCArray(final CTCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.C$0);
    }
    
    public void setCArray(final int n, final CTCell ctCell) {
        this.generatedSetterHelperImpl((XmlObject)ctCell, CTRowImpl.C$0, n, (short)2);
    }
    
    public CTCell insertNewC(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCell)this.get_store().insert_element_user(CTRowImpl.C$0, n);
        }
    }
    
    public CTCell addNewC() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCell)this.get_store().add_element_user(CTRowImpl.C$0);
        }
    }
    
    public void removeC(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.C$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTRowImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTRowImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTRowImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.EXTLST$2, 0);
        }
    }
    
    public long getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.R$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowImpl.R$4);
        }
    }
    
    public boolean isSetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.R$4) != null;
        }
    }
    
    public void setR(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.R$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.R$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetR(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowImpl.R$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTRowImpl.R$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.R$4);
        }
    }
    
    public List getSpans() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.SPANS$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getListValue();
        }
    }
    
    public STCellSpans xgetSpans() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellSpans)this.get_store().find_attribute_user(CTRowImpl.SPANS$6);
        }
    }
    
    public boolean isSetSpans() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.SPANS$6) != null;
        }
    }
    
    public void setSpans(final List listValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.SPANS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.SPANS$6);
            }
            simpleValue.setListValue(listValue);
        }
    }
    
    public void xsetSpans(final STCellSpans stCellSpans) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellSpans stCellSpans2 = (STCellSpans)this.get_store().find_attribute_user(CTRowImpl.SPANS$6);
            if (stCellSpans2 == null) {
                stCellSpans2 = (STCellSpans)this.get_store().add_attribute_user(CTRowImpl.SPANS$6);
            }
            stCellSpans2.set((XmlObject)stCellSpans);
        }
    }
    
    public void unsetSpans() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.SPANS$6);
        }
    }
    
    public long getS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.S$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.S$8);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowImpl.S$8);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTRowImpl.S$8);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.S$8) != null;
        }
    }
    
    public void setS(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.S$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.S$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetS(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowImpl.S$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTRowImpl.S$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.S$8);
        }
    }
    
    public boolean getCustomFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.CUSTOMFORMAT$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCustomFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.CUSTOMFORMAT$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCustomFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.CUSTOMFORMAT$10) != null;
        }
    }
    
    public void setCustomFormat(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCustomFormat(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.CUSTOMFORMAT$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCustomFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.CUSTOMFORMAT$10);
        }
    }
    
    public double getHt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.HT$12);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetHt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTRowImpl.HT$12);
        }
    }
    
    public boolean isSetHt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.HT$12) != null;
        }
    }
    
    public void setHt(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.HT$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.HT$12);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetHt(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTRowImpl.HT$12);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTRowImpl.HT$12);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetHt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.HT$12);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.HIDDEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.HIDDEN$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.HIDDEN$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.HIDDEN$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.HIDDEN$14) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.HIDDEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.HIDDEN$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.HIDDEN$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.HIDDEN$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.HIDDEN$14);
        }
    }
    
    public boolean getCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.CUSTOMHEIGHT$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.CUSTOMHEIGHT$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.CUSTOMHEIGHT$16) != null;
        }
    }
    
    public void setCustomHeight(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCustomHeight(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.CUSTOMHEIGHT$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.CUSTOMHEIGHT$16);
        }
    }
    
    public short getOutlineLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.OUTLINELEVEL$18);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetOutlineLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTRowImpl.OUTLINELEVEL$18);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetOutlineLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.OUTLINELEVEL$18) != null;
        }
    }
    
    public void setOutlineLevel(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetOutlineLevel(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTRowImpl.OUTLINELEVEL$18);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetOutlineLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.OUTLINELEVEL$18);
        }
    }
    
    public boolean getCollapsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.COLLAPSED$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.COLLAPSED$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCollapsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.COLLAPSED$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.COLLAPSED$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCollapsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.COLLAPSED$20) != null;
        }
    }
    
    public void setCollapsed(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.COLLAPSED$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.COLLAPSED$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCollapsed(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.COLLAPSED$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.COLLAPSED$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCollapsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.COLLAPSED$20);
        }
    }
    
    public boolean getThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.THICKTOP$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.THICKTOP$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.THICKTOP$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.THICKTOP$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.THICKTOP$22) != null;
        }
    }
    
    public void setThickTop(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.THICKTOP$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.THICKTOP$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetThickTop(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.THICKTOP$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.THICKTOP$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.THICKTOP$22);
        }
    }
    
    public boolean getThickBot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.THICKBOT$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.THICKBOT$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetThickBot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.THICKBOT$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.THICKBOT$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetThickBot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.THICKBOT$24) != null;
        }
    }
    
    public void setThickBot(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.THICKBOT$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.THICKBOT$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetThickBot(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.THICKBOT$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.THICKBOT$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetThickBot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.THICKBOT$24);
        }
    }
    
    public boolean getPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.PH$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowImpl.PH$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.PH$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTRowImpl.PH$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.PH$26) != null;
        }
    }
    
    public void setPh(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.PH$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.PH$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPh(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTRowImpl.PH$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTRowImpl.PH$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.PH$26);
        }
    }
    
    static {
        C$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "c");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        R$4 = new QName("", "r");
        SPANS$6 = new QName("", "spans");
        S$8 = new QName("", "s");
        CUSTOMFORMAT$10 = new QName("", "customFormat");
        HT$12 = new QName("", "ht");
        HIDDEN$14 = new QName("", "hidden");
        CUSTOMHEIGHT$16 = new QName("", "customHeight");
        OUTLINELEVEL$18 = new QName("", "outlineLevel");
        COLLAPSED$20 = new QName("", "collapsed");
        THICKTOP$22 = new QName("", "thickTop");
        THICKBOT$24 = new QName("", "thickBot");
        PH$26 = new QName("", "ph");
    }
}
