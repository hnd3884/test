package com.microsoft.schemas.office.excel.impl;

import com.microsoft.schemas.office.excel.STObjectType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import com.microsoft.schemas.office.excel.STCF;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import com.microsoft.schemas.office.excel.STTrueFalseBlank;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.excel.CTClientData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTClientDataImpl extends XmlComplexContentImpl implements CTClientData
{
    private static final long serialVersionUID = 1L;
    private static final QName MOVEWITHCELLS$0;
    private static final QName SIZEWITHCELLS$2;
    private static final QName ANCHOR$4;
    private static final QName LOCKED$6;
    private static final QName DEFAULTSIZE$8;
    private static final QName PRINTOBJECT$10;
    private static final QName DISABLED$12;
    private static final QName AUTOFILL$14;
    private static final QName AUTOLINE$16;
    private static final QName AUTOPICT$18;
    private static final QName FMLAMACRO$20;
    private static final QName TEXTHALIGN$22;
    private static final QName TEXTVALIGN$24;
    private static final QName LOCKTEXT$26;
    private static final QName JUSTLASTX$28;
    private static final QName SECRETEDIT$30;
    private static final QName DEFAULT$32;
    private static final QName HELP$34;
    private static final QName CANCEL$36;
    private static final QName DISMISS$38;
    private static final QName ACCEL$40;
    private static final QName ACCEL2$42;
    private static final QName ROW$44;
    private static final QName COLUMN$46;
    private static final QName VISIBLE$48;
    private static final QName ROWHIDDEN$50;
    private static final QName COLHIDDEN$52;
    private static final QName VTEDIT$54;
    private static final QName MULTILINE$56;
    private static final QName VSCROLL$58;
    private static final QName VALIDIDS$60;
    private static final QName FMLARANGE$62;
    private static final QName WIDTHMIN$64;
    private static final QName SEL$66;
    private static final QName NOTHREED2$68;
    private static final QName SELTYPE$70;
    private static final QName MULTISEL$72;
    private static final QName LCT$74;
    private static final QName LISTITEM$76;
    private static final QName DROPSTYLE$78;
    private static final QName COLORED$80;
    private static final QName DROPLINES$82;
    private static final QName CHECKED$84;
    private static final QName FMLALINK$86;
    private static final QName FMLAPICT$88;
    private static final QName NOTHREED$90;
    private static final QName FIRSTBUTTON$92;
    private static final QName FMLAGROUP$94;
    private static final QName VAL$96;
    private static final QName MIN$98;
    private static final QName MAX$100;
    private static final QName INC$102;
    private static final QName PAGE$104;
    private static final QName HORIZ$106;
    private static final QName DX$108;
    private static final QName MAPOCX$110;
    private static final QName CF$112;
    private static final QName CAMERA$114;
    private static final QName RECALCALWAYS$116;
    private static final QName AUTOSCALE$118;
    private static final QName DDE$120;
    private static final QName UIOBJ$122;
    private static final QName SCRIPTTEXT$124;
    private static final QName SCRIPTEXTENDED$126;
    private static final QName SCRIPTLANGUAGE$128;
    private static final QName SCRIPTLOCATION$130;
    private static final QName FMLATXBX$132;
    private static final QName OBJECTTYPE$134;
    
    public CTClientDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<STTrueFalseBlank.Enum> getMoveWithCellsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveWithCellsList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getMoveWithCellsArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum moveWithCellsArray = CTClientDataImpl.this.getMoveWithCellsArray(n);
                    CTClientDataImpl.this.setMoveWithCellsArray(n, enum1);
                    return moveWithCellsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertMoveWithCells(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum moveWithCellsArray = CTClientDataImpl.this.getMoveWithCellsArray(n);
                    CTClientDataImpl.this.removeMoveWithCells(n);
                    return moveWithCellsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMoveWithCellsArray();
                }
            }
            return new MoveWithCellsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getMoveWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MOVEWITHCELLS$0, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getMoveWithCellsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetMoveWithCellsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveWithCellsList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetMoveWithCellsArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetMoveWithCellsArray = CTClientDataImpl.this.xgetMoveWithCellsArray(n);
                    CTClientDataImpl.this.xsetMoveWithCellsArray(n, stTrueFalseBlank);
                    return xgetMoveWithCellsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewMoveWithCells(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetMoveWithCellsArray = CTClientDataImpl.this.xgetMoveWithCellsArray(n);
                    CTClientDataImpl.this.removeMoveWithCells(n);
                    return xgetMoveWithCellsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMoveWithCellsArray();
                }
            }
            return new MoveWithCellsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetMoveWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MOVEWITHCELLS$0, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetMoveWithCellsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfMoveWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MOVEWITHCELLS$0);
        }
    }
    
    public void setMoveWithCellsArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.MOVEWITHCELLS$0);
        }
    }
    
    public void setMoveWithCellsArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetMoveWithCellsArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MOVEWITHCELLS$0);
        }
    }
    
    public void xsetMoveWithCellsArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertMoveWithCells(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addMoveWithCells(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MOVEWITHCELLS$0)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewMoveWithCells(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.MOVEWITHCELLS$0, n);
        }
    }
    
    public STTrueFalseBlank addNewMoveWithCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.MOVEWITHCELLS$0);
        }
    }
    
    public void removeMoveWithCells(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MOVEWITHCELLS$0, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getSizeWithCellsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SizeWithCellsList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getSizeWithCellsArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum sizeWithCellsArray = CTClientDataImpl.this.getSizeWithCellsArray(n);
                    CTClientDataImpl.this.setSizeWithCellsArray(n, enum1);
                    return sizeWithCellsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertSizeWithCells(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum sizeWithCellsArray = CTClientDataImpl.this.getSizeWithCellsArray(n);
                    CTClientDataImpl.this.removeSizeWithCells(n);
                    return sizeWithCellsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSizeWithCellsArray();
                }
            }
            return new SizeWithCellsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getSizeWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SIZEWITHCELLS$2, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getSizeWithCellsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetSizeWithCellsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SizeWithCellsList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetSizeWithCellsArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetSizeWithCellsArray = CTClientDataImpl.this.xgetSizeWithCellsArray(n);
                    CTClientDataImpl.this.xsetSizeWithCellsArray(n, stTrueFalseBlank);
                    return xgetSizeWithCellsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewSizeWithCells(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetSizeWithCellsArray = CTClientDataImpl.this.xgetSizeWithCellsArray(n);
                    CTClientDataImpl.this.removeSizeWithCells(n);
                    return xgetSizeWithCellsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSizeWithCellsArray();
                }
            }
            return new SizeWithCellsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetSizeWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SIZEWITHCELLS$2, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetSizeWithCellsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfSizeWithCellsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SIZEWITHCELLS$2);
        }
    }
    
    public void setSizeWithCellsArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.SIZEWITHCELLS$2);
        }
    }
    
    public void setSizeWithCellsArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSizeWithCellsArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SIZEWITHCELLS$2);
        }
    }
    
    public void xsetSizeWithCellsArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertSizeWithCells(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addSizeWithCells(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SIZEWITHCELLS$2)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewSizeWithCells(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.SIZEWITHCELLS$2, n);
        }
    }
    
    public STTrueFalseBlank addNewSizeWithCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.SIZEWITHCELLS$2);
        }
    }
    
    public void removeSizeWithCells(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SIZEWITHCELLS$2, n);
        }
    }
    
    public List<String> getAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getAnchorArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String anchorArray = CTClientDataImpl.this.getAnchorArray(n);
                    CTClientDataImpl.this.setAnchorArray(n, s);
                    return anchorArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertAnchor(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String anchorArray = CTClientDataImpl.this.getAnchorArray(n);
                    CTClientDataImpl.this.removeAnchor(n);
                    return anchorArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAnchorArray();
                }
            }
            return new AnchorList();
        }
    }
    
    @Deprecated
    public String[] getAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ANCHOR$4, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ANCHOR$4, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetAnchorArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetAnchorArray = CTClientDataImpl.this.xgetAnchorArray(n);
                    CTClientDataImpl.this.xsetAnchorArray(n, xmlString);
                    return xgetAnchorArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewAnchor(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetAnchorArray = CTClientDataImpl.this.xgetAnchorArray(n);
                    CTClientDataImpl.this.removeAnchor(n);
                    return xgetAnchorArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAnchorArray();
                }
            }
            return new AnchorList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ANCHOR$4, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.ANCHOR$4, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.ANCHOR$4);
        }
    }
    
    public void setAnchorArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.ANCHOR$4);
        }
    }
    
    public void setAnchorArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ANCHOR$4, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAnchorArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.ANCHOR$4);
        }
    }
    
    public void xsetAnchorArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.ANCHOR$4, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertAnchor(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.ANCHOR$4, n)).setStringValue(stringValue);
        }
    }
    
    public void addAnchor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.ANCHOR$4)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.ANCHOR$4, n);
        }
    }
    
    public XmlString addNewAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.ANCHOR$4);
        }
    }
    
    public void removeAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.ANCHOR$4, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getLockedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockedList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getLockedArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum lockedArray = CTClientDataImpl.this.getLockedArray(n);
                    CTClientDataImpl.this.setLockedArray(n, enum1);
                    return lockedArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertLocked(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum lockedArray = CTClientDataImpl.this.getLockedArray(n);
                    CTClientDataImpl.this.removeLocked(n);
                    return lockedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLockedArray();
                }
            }
            return new LockedList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getLockedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LOCKED$6, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getLockedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LOCKED$6, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetLockedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockedList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetLockedArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetLockedArray = CTClientDataImpl.this.xgetLockedArray(n);
                    CTClientDataImpl.this.xsetLockedArray(n, stTrueFalseBlank);
                    return xgetLockedArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewLocked(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetLockedArray = CTClientDataImpl.this.xgetLockedArray(n);
                    CTClientDataImpl.this.removeLocked(n);
                    return xgetLockedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLockedArray();
                }
            }
            return new LockedList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetLockedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LOCKED$6, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetLockedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.LOCKED$6, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfLockedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.LOCKED$6);
        }
    }
    
    public void setLockedArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.LOCKED$6);
        }
    }
    
    public void setLockedArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LOCKED$6, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLockedArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.LOCKED$6);
        }
    }
    
    public void xsetLockedArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.LOCKED$6, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertLocked(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.LOCKED$6, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addLocked(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.LOCKED$6)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewLocked(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.LOCKED$6, n);
        }
    }
    
    public STTrueFalseBlank addNewLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.LOCKED$6);
        }
    }
    
    public void removeLocked(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.LOCKED$6, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getDefaultSizeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefaultSizeList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getDefaultSizeArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum defaultSizeArray = CTClientDataImpl.this.getDefaultSizeArray(n);
                    CTClientDataImpl.this.setDefaultSizeArray(n, enum1);
                    return defaultSizeArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertDefaultSize(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum defaultSizeArray = CTClientDataImpl.this.getDefaultSizeArray(n);
                    CTClientDataImpl.this.removeDefaultSize(n);
                    return defaultSizeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDefaultSizeArray();
                }
            }
            return new DefaultSizeList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getDefaultSizeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DEFAULTSIZE$8, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getDefaultSizeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DEFAULTSIZE$8, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetDefaultSizeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefaultSizeList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetDefaultSizeArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetDefaultSizeArray = CTClientDataImpl.this.xgetDefaultSizeArray(n);
                    CTClientDataImpl.this.xsetDefaultSizeArray(n, stTrueFalseBlank);
                    return xgetDefaultSizeArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewDefaultSize(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetDefaultSizeArray = CTClientDataImpl.this.xgetDefaultSizeArray(n);
                    CTClientDataImpl.this.removeDefaultSize(n);
                    return xgetDefaultSizeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDefaultSizeArray();
                }
            }
            return new DefaultSizeList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetDefaultSizeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DEFAULTSIZE$8, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetDefaultSizeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DEFAULTSIZE$8, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfDefaultSizeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DEFAULTSIZE$8);
        }
    }
    
    public void setDefaultSizeArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.DEFAULTSIZE$8);
        }
    }
    
    public void setDefaultSizeArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DEFAULTSIZE$8, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefaultSizeArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DEFAULTSIZE$8);
        }
    }
    
    public void xsetDefaultSizeArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DEFAULTSIZE$8, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertDefaultSize(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DEFAULTSIZE$8, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addDefaultSize(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DEFAULTSIZE$8)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewDefaultSize(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.DEFAULTSIZE$8, n);
        }
    }
    
    public STTrueFalseBlank addNewDefaultSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.DEFAULTSIZE$8);
        }
    }
    
    public void removeDefaultSize(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DEFAULTSIZE$8, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getPrintObjectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PrintObjectList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getPrintObjectArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum printObjectArray = CTClientDataImpl.this.getPrintObjectArray(n);
                    CTClientDataImpl.this.setPrintObjectArray(n, enum1);
                    return printObjectArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertPrintObject(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum printObjectArray = CTClientDataImpl.this.getPrintObjectArray(n);
                    CTClientDataImpl.this.removePrintObject(n);
                    return printObjectArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfPrintObjectArray();
                }
            }
            return new PrintObjectList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getPrintObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.PRINTOBJECT$10, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getPrintObjectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.PRINTOBJECT$10, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetPrintObjectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PrintObjectList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetPrintObjectArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetPrintObjectArray = CTClientDataImpl.this.xgetPrintObjectArray(n);
                    CTClientDataImpl.this.xsetPrintObjectArray(n, stTrueFalseBlank);
                    return xgetPrintObjectArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewPrintObject(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetPrintObjectArray = CTClientDataImpl.this.xgetPrintObjectArray(n);
                    CTClientDataImpl.this.removePrintObject(n);
                    return xgetPrintObjectArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfPrintObjectArray();
                }
            }
            return new PrintObjectList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetPrintObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.PRINTOBJECT$10, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetPrintObjectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.PRINTOBJECT$10, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfPrintObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.PRINTOBJECT$10);
        }
    }
    
    public void setPrintObjectArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.PRINTOBJECT$10);
        }
    }
    
    public void setPrintObjectArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.PRINTOBJECT$10, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrintObjectArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.PRINTOBJECT$10);
        }
    }
    
    public void xsetPrintObjectArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.PRINTOBJECT$10, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertPrintObject(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.PRINTOBJECT$10, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addPrintObject(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.PRINTOBJECT$10)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewPrintObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.PRINTOBJECT$10, n);
        }
    }
    
    public STTrueFalseBlank addNewPrintObject() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.PRINTOBJECT$10);
        }
    }
    
    public void removePrintObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.PRINTOBJECT$10, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getDisabledList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DisabledList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getDisabledArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum disabledArray = CTClientDataImpl.this.getDisabledArray(n);
                    CTClientDataImpl.this.setDisabledArray(n, enum1);
                    return disabledArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertDisabled(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum disabledArray = CTClientDataImpl.this.getDisabledArray(n);
                    CTClientDataImpl.this.removeDisabled(n);
                    return disabledArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDisabledArray();
                }
            }
            return new DisabledList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getDisabledArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DISABLED$12, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getDisabledArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DISABLED$12, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetDisabledList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DisabledList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetDisabledArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetDisabledArray = CTClientDataImpl.this.xgetDisabledArray(n);
                    CTClientDataImpl.this.xsetDisabledArray(n, stTrueFalseBlank);
                    return xgetDisabledArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewDisabled(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetDisabledArray = CTClientDataImpl.this.xgetDisabledArray(n);
                    CTClientDataImpl.this.removeDisabled(n);
                    return xgetDisabledArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDisabledArray();
                }
            }
            return new DisabledList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetDisabledArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DISABLED$12, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetDisabledArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DISABLED$12, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfDisabledArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DISABLED$12);
        }
    }
    
    public void setDisabledArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.DISABLED$12);
        }
    }
    
    public void setDisabledArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DISABLED$12, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDisabledArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DISABLED$12);
        }
    }
    
    public void xsetDisabledArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DISABLED$12, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertDisabled(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DISABLED$12, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addDisabled(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DISABLED$12)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewDisabled(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.DISABLED$12, n);
        }
    }
    
    public STTrueFalseBlank addNewDisabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.DISABLED$12);
        }
    }
    
    public void removeDisabled(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DISABLED$12, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getAutoFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoFillList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getAutoFillArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum autoFillArray = CTClientDataImpl.this.getAutoFillArray(n);
                    CTClientDataImpl.this.setAutoFillArray(n, enum1);
                    return autoFillArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertAutoFill(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum autoFillArray = CTClientDataImpl.this.getAutoFillArray(n);
                    CTClientDataImpl.this.removeAutoFill(n);
                    return autoFillArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoFillArray();
                }
            }
            return new AutoFillList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getAutoFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOFILL$14, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getAutoFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOFILL$14, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetAutoFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoFillList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetAutoFillArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetAutoFillArray = CTClientDataImpl.this.xgetAutoFillArray(n);
                    CTClientDataImpl.this.xsetAutoFillArray(n, stTrueFalseBlank);
                    return xgetAutoFillArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewAutoFill(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetAutoFillArray = CTClientDataImpl.this.xgetAutoFillArray(n);
                    CTClientDataImpl.this.removeAutoFill(n);
                    return xgetAutoFillArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoFillArray();
                }
            }
            return new AutoFillList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetAutoFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOFILL$14, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetAutoFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOFILL$14, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfAutoFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.AUTOFILL$14);
        }
    }
    
    public void setAutoFillArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.AUTOFILL$14);
        }
    }
    
    public void setAutoFillArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOFILL$14, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAutoFillArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.AUTOFILL$14);
        }
    }
    
    public void xsetAutoFillArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOFILL$14, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertAutoFill(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.AUTOFILL$14, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addAutoFill(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.AUTOFILL$14)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewAutoFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.AUTOFILL$14, n);
        }
    }
    
    public STTrueFalseBlank addNewAutoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.AUTOFILL$14);
        }
    }
    
    public void removeAutoFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.AUTOFILL$14, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getAutoLineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoLineList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getAutoLineArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum autoLineArray = CTClientDataImpl.this.getAutoLineArray(n);
                    CTClientDataImpl.this.setAutoLineArray(n, enum1);
                    return autoLineArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertAutoLine(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum autoLineArray = CTClientDataImpl.this.getAutoLineArray(n);
                    CTClientDataImpl.this.removeAutoLine(n);
                    return autoLineArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoLineArray();
                }
            }
            return new AutoLineList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getAutoLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOLINE$16, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getAutoLineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOLINE$16, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetAutoLineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoLineList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetAutoLineArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetAutoLineArray = CTClientDataImpl.this.xgetAutoLineArray(n);
                    CTClientDataImpl.this.xsetAutoLineArray(n, stTrueFalseBlank);
                    return xgetAutoLineArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewAutoLine(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetAutoLineArray = CTClientDataImpl.this.xgetAutoLineArray(n);
                    CTClientDataImpl.this.removeAutoLine(n);
                    return xgetAutoLineArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoLineArray();
                }
            }
            return new AutoLineList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetAutoLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOLINE$16, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetAutoLineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOLINE$16, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfAutoLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.AUTOLINE$16);
        }
    }
    
    public void setAutoLineArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.AUTOLINE$16);
        }
    }
    
    public void setAutoLineArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOLINE$16, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAutoLineArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.AUTOLINE$16);
        }
    }
    
    public void xsetAutoLineArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOLINE$16, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertAutoLine(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.AUTOLINE$16, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addAutoLine(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.AUTOLINE$16)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewAutoLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.AUTOLINE$16, n);
        }
    }
    
    public STTrueFalseBlank addNewAutoLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.AUTOLINE$16);
        }
    }
    
    public void removeAutoLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.AUTOLINE$16, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getAutoPictList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoPictList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getAutoPictArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum autoPictArray = CTClientDataImpl.this.getAutoPictArray(n);
                    CTClientDataImpl.this.setAutoPictArray(n, enum1);
                    return autoPictArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertAutoPict(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum autoPictArray = CTClientDataImpl.this.getAutoPictArray(n);
                    CTClientDataImpl.this.removeAutoPict(n);
                    return autoPictArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoPictArray();
                }
            }
            return new AutoPictList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getAutoPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOPICT$18, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getAutoPictArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOPICT$18, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetAutoPictList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoPictList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetAutoPictArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetAutoPictArray = CTClientDataImpl.this.xgetAutoPictArray(n);
                    CTClientDataImpl.this.xsetAutoPictArray(n, stTrueFalseBlank);
                    return xgetAutoPictArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewAutoPict(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetAutoPictArray = CTClientDataImpl.this.xgetAutoPictArray(n);
                    CTClientDataImpl.this.removeAutoPict(n);
                    return xgetAutoPictArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoPictArray();
                }
            }
            return new AutoPictList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetAutoPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOPICT$18, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetAutoPictArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOPICT$18, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfAutoPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.AUTOPICT$18);
        }
    }
    
    public void setAutoPictArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.AUTOPICT$18);
        }
    }
    
    public void setAutoPictArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOPICT$18, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAutoPictArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.AUTOPICT$18);
        }
    }
    
    public void xsetAutoPictArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOPICT$18, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertAutoPict(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.AUTOPICT$18, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addAutoPict(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.AUTOPICT$18)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewAutoPict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.AUTOPICT$18, n);
        }
    }
    
    public STTrueFalseBlank addNewAutoPict() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.AUTOPICT$18);
        }
    }
    
    public void removeAutoPict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.AUTOPICT$18, n);
        }
    }
    
    public List<String> getFmlaMacroList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaMacroList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaMacroArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaMacroArray = CTClientDataImpl.this.getFmlaMacroArray(n);
                    CTClientDataImpl.this.setFmlaMacroArray(n, s);
                    return fmlaMacroArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaMacro(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaMacroArray = CTClientDataImpl.this.getFmlaMacroArray(n);
                    CTClientDataImpl.this.removeFmlaMacro(n);
                    return fmlaMacroArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaMacroArray();
                }
            }
            return new FmlaMacroList();
        }
    }
    
    @Deprecated
    public String[] getFmlaMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAMACRO$20, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaMacroArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAMACRO$20, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaMacroList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaMacroList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaMacroArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaMacroArray = CTClientDataImpl.this.xgetFmlaMacroArray(n);
                    CTClientDataImpl.this.xsetFmlaMacroArray(n, xmlString);
                    return xgetFmlaMacroArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaMacro(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaMacroArray = CTClientDataImpl.this.xgetFmlaMacroArray(n);
                    CTClientDataImpl.this.removeFmlaMacro(n);
                    return xgetFmlaMacroArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaMacroArray();
                }
            }
            return new FmlaMacroList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAMACRO$20, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaMacroArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAMACRO$20, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLAMACRO$20);
        }
    }
    
    public void setFmlaMacroArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLAMACRO$20);
        }
    }
    
    public void setFmlaMacroArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAMACRO$20, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaMacroArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLAMACRO$20);
        }
    }
    
    public void xsetFmlaMacroArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAMACRO$20, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaMacro(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLAMACRO$20, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaMacro(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLAMACRO$20)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLAMACRO$20, n);
        }
    }
    
    public XmlString addNewFmlaMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLAMACRO$20);
        }
    }
    
    public void removeFmlaMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLAMACRO$20, n);
        }
    }
    
    public List<String> getTextHAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextHAlignList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getTextHAlignArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String textHAlignArray = CTClientDataImpl.this.getTextHAlignArray(n);
                    CTClientDataImpl.this.setTextHAlignArray(n, s);
                    return textHAlignArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertTextHAlign(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String textHAlignArray = CTClientDataImpl.this.getTextHAlignArray(n);
                    CTClientDataImpl.this.removeTextHAlign(n);
                    return textHAlignArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfTextHAlignArray();
                }
            }
            return new TextHAlignList();
        }
    }
    
    @Deprecated
    public String[] getTextHAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.TEXTHALIGN$22, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getTextHAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.TEXTHALIGN$22, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetTextHAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextHAlignList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetTextHAlignArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetTextHAlignArray = CTClientDataImpl.this.xgetTextHAlignArray(n);
                    CTClientDataImpl.this.xsetTextHAlignArray(n, xmlString);
                    return xgetTextHAlignArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewTextHAlign(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetTextHAlignArray = CTClientDataImpl.this.xgetTextHAlignArray(n);
                    CTClientDataImpl.this.removeTextHAlign(n);
                    return xgetTextHAlignArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfTextHAlignArray();
                }
            }
            return new TextHAlignList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetTextHAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.TEXTHALIGN$22, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetTextHAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.TEXTHALIGN$22, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfTextHAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.TEXTHALIGN$22);
        }
    }
    
    public void setTextHAlignArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.TEXTHALIGN$22);
        }
    }
    
    public void setTextHAlignArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.TEXTHALIGN$22, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTextHAlignArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.TEXTHALIGN$22);
        }
    }
    
    public void xsetTextHAlignArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.TEXTHALIGN$22, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertTextHAlign(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.TEXTHALIGN$22, n)).setStringValue(stringValue);
        }
    }
    
    public void addTextHAlign(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.TEXTHALIGN$22)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewTextHAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.TEXTHALIGN$22, n);
        }
    }
    
    public XmlString addNewTextHAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.TEXTHALIGN$22);
        }
    }
    
    public void removeTextHAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.TEXTHALIGN$22, n);
        }
    }
    
    public List<String> getTextVAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextVAlignList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getTextVAlignArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String textVAlignArray = CTClientDataImpl.this.getTextVAlignArray(n);
                    CTClientDataImpl.this.setTextVAlignArray(n, s);
                    return textVAlignArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertTextVAlign(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String textVAlignArray = CTClientDataImpl.this.getTextVAlignArray(n);
                    CTClientDataImpl.this.removeTextVAlign(n);
                    return textVAlignArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfTextVAlignArray();
                }
            }
            return new TextVAlignList();
        }
    }
    
    @Deprecated
    public String[] getTextVAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.TEXTVALIGN$24, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getTextVAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.TEXTVALIGN$24, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetTextVAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextVAlignList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetTextVAlignArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetTextVAlignArray = CTClientDataImpl.this.xgetTextVAlignArray(n);
                    CTClientDataImpl.this.xsetTextVAlignArray(n, xmlString);
                    return xgetTextVAlignArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewTextVAlign(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetTextVAlignArray = CTClientDataImpl.this.xgetTextVAlignArray(n);
                    CTClientDataImpl.this.removeTextVAlign(n);
                    return xgetTextVAlignArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfTextVAlignArray();
                }
            }
            return new TextVAlignList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetTextVAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.TEXTVALIGN$24, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetTextVAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.TEXTVALIGN$24, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfTextVAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.TEXTVALIGN$24);
        }
    }
    
    public void setTextVAlignArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.TEXTVALIGN$24);
        }
    }
    
    public void setTextVAlignArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.TEXTVALIGN$24, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTextVAlignArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.TEXTVALIGN$24);
        }
    }
    
    public void xsetTextVAlignArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.TEXTVALIGN$24, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertTextVAlign(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.TEXTVALIGN$24, n)).setStringValue(stringValue);
        }
    }
    
    public void addTextVAlign(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.TEXTVALIGN$24)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewTextVAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.TEXTVALIGN$24, n);
        }
    }
    
    public XmlString addNewTextVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.TEXTVALIGN$24);
        }
    }
    
    public void removeTextVAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.TEXTVALIGN$24, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getLockTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockTextList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getLockTextArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum lockTextArray = CTClientDataImpl.this.getLockTextArray(n);
                    CTClientDataImpl.this.setLockTextArray(n, enum1);
                    return lockTextArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertLockText(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum lockTextArray = CTClientDataImpl.this.getLockTextArray(n);
                    CTClientDataImpl.this.removeLockText(n);
                    return lockTextArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLockTextArray();
                }
            }
            return new LockTextList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getLockTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LOCKTEXT$26, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getLockTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LOCKTEXT$26, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetLockTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockTextList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetLockTextArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetLockTextArray = CTClientDataImpl.this.xgetLockTextArray(n);
                    CTClientDataImpl.this.xsetLockTextArray(n, stTrueFalseBlank);
                    return xgetLockTextArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewLockText(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetLockTextArray = CTClientDataImpl.this.xgetLockTextArray(n);
                    CTClientDataImpl.this.removeLockText(n);
                    return xgetLockTextArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLockTextArray();
                }
            }
            return new LockTextList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetLockTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LOCKTEXT$26, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetLockTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.LOCKTEXT$26, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfLockTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.LOCKTEXT$26);
        }
    }
    
    public void setLockTextArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.LOCKTEXT$26);
        }
    }
    
    public void setLockTextArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LOCKTEXT$26, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLockTextArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.LOCKTEXT$26);
        }
    }
    
    public void xsetLockTextArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.LOCKTEXT$26, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertLockText(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.LOCKTEXT$26, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addLockText(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.LOCKTEXT$26)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewLockText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.LOCKTEXT$26, n);
        }
    }
    
    public STTrueFalseBlank addNewLockText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.LOCKTEXT$26);
        }
    }
    
    public void removeLockText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.LOCKTEXT$26, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getJustLastXList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class JustLastXList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getJustLastXArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum justLastXArray = CTClientDataImpl.this.getJustLastXArray(n);
                    CTClientDataImpl.this.setJustLastXArray(n, enum1);
                    return justLastXArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertJustLastX(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum justLastXArray = CTClientDataImpl.this.getJustLastXArray(n);
                    CTClientDataImpl.this.removeJustLastX(n);
                    return justLastXArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfJustLastXArray();
                }
            }
            return new JustLastXList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getJustLastXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.JUSTLASTX$28, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getJustLastXArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.JUSTLASTX$28, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetJustLastXList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class JustLastXList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetJustLastXArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetJustLastXArray = CTClientDataImpl.this.xgetJustLastXArray(n);
                    CTClientDataImpl.this.xsetJustLastXArray(n, stTrueFalseBlank);
                    return xgetJustLastXArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewJustLastX(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetJustLastXArray = CTClientDataImpl.this.xgetJustLastXArray(n);
                    CTClientDataImpl.this.removeJustLastX(n);
                    return xgetJustLastXArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfJustLastXArray();
                }
            }
            return new JustLastXList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetJustLastXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.JUSTLASTX$28, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetJustLastXArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.JUSTLASTX$28, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfJustLastXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.JUSTLASTX$28);
        }
    }
    
    public void setJustLastXArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.JUSTLASTX$28);
        }
    }
    
    public void setJustLastXArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.JUSTLASTX$28, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetJustLastXArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.JUSTLASTX$28);
        }
    }
    
    public void xsetJustLastXArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.JUSTLASTX$28, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertJustLastX(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.JUSTLASTX$28, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addJustLastX(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.JUSTLASTX$28)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewJustLastX(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.JUSTLASTX$28, n);
        }
    }
    
    public STTrueFalseBlank addNewJustLastX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.JUSTLASTX$28);
        }
    }
    
    public void removeJustLastX(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.JUSTLASTX$28, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getSecretEditList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SecretEditList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getSecretEditArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum secretEditArray = CTClientDataImpl.this.getSecretEditArray(n);
                    CTClientDataImpl.this.setSecretEditArray(n, enum1);
                    return secretEditArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertSecretEdit(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum secretEditArray = CTClientDataImpl.this.getSecretEditArray(n);
                    CTClientDataImpl.this.removeSecretEdit(n);
                    return secretEditArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSecretEditArray();
                }
            }
            return new SecretEditList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getSecretEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SECRETEDIT$30, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getSecretEditArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SECRETEDIT$30, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetSecretEditList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SecretEditList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetSecretEditArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetSecretEditArray = CTClientDataImpl.this.xgetSecretEditArray(n);
                    CTClientDataImpl.this.xsetSecretEditArray(n, stTrueFalseBlank);
                    return xgetSecretEditArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewSecretEdit(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetSecretEditArray = CTClientDataImpl.this.xgetSecretEditArray(n);
                    CTClientDataImpl.this.removeSecretEdit(n);
                    return xgetSecretEditArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSecretEditArray();
                }
            }
            return new SecretEditList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetSecretEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SECRETEDIT$30, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetSecretEditArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.SECRETEDIT$30, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfSecretEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SECRETEDIT$30);
        }
    }
    
    public void setSecretEditArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.SECRETEDIT$30);
        }
    }
    
    public void setSecretEditArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SECRETEDIT$30, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSecretEditArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SECRETEDIT$30);
        }
    }
    
    public void xsetSecretEditArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.SECRETEDIT$30, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertSecretEdit(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SECRETEDIT$30, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addSecretEdit(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SECRETEDIT$30)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewSecretEdit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.SECRETEDIT$30, n);
        }
    }
    
    public STTrueFalseBlank addNewSecretEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.SECRETEDIT$30);
        }
    }
    
    public void removeSecretEdit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SECRETEDIT$30, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getDefaultList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefaultList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getDefaultArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum defaultArray = CTClientDataImpl.this.getDefaultArray(n);
                    CTClientDataImpl.this.setDefaultArray(n, enum1);
                    return defaultArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertDefault(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum defaultArray = CTClientDataImpl.this.getDefaultArray(n);
                    CTClientDataImpl.this.removeDefault(n);
                    return defaultArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDefaultArray();
                }
            }
            return new DefaultList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getDefaultArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DEFAULT$32, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getDefaultArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DEFAULT$32, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetDefaultList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefaultList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetDefaultArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetDefaultArray = CTClientDataImpl.this.xgetDefaultArray(n);
                    CTClientDataImpl.this.xsetDefaultArray(n, stTrueFalseBlank);
                    return xgetDefaultArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewDefault(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetDefaultArray = CTClientDataImpl.this.xgetDefaultArray(n);
                    CTClientDataImpl.this.removeDefault(n);
                    return xgetDefaultArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDefaultArray();
                }
            }
            return new DefaultList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetDefaultArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DEFAULT$32, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetDefaultArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DEFAULT$32, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfDefaultArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DEFAULT$32);
        }
    }
    
    public void setDefaultArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.DEFAULT$32);
        }
    }
    
    public void setDefaultArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DEFAULT$32, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefaultArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DEFAULT$32);
        }
    }
    
    public void xsetDefaultArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DEFAULT$32, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertDefault(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DEFAULT$32, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addDefault(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DEFAULT$32)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewDefault(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.DEFAULT$32, n);
        }
    }
    
    public STTrueFalseBlank addNewDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.DEFAULT$32);
        }
    }
    
    public void removeDefault(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DEFAULT$32, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getHelpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HelpList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getHelpArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum helpArray = CTClientDataImpl.this.getHelpArray(n);
                    CTClientDataImpl.this.setHelpArray(n, enum1);
                    return helpArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertHelp(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum helpArray = CTClientDataImpl.this.getHelpArray(n);
                    CTClientDataImpl.this.removeHelp(n);
                    return helpArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfHelpArray();
                }
            }
            return new HelpList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getHelpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.HELP$34, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getHelpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.HELP$34, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetHelpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HelpList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetHelpArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetHelpArray = CTClientDataImpl.this.xgetHelpArray(n);
                    CTClientDataImpl.this.xsetHelpArray(n, stTrueFalseBlank);
                    return xgetHelpArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewHelp(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetHelpArray = CTClientDataImpl.this.xgetHelpArray(n);
                    CTClientDataImpl.this.removeHelp(n);
                    return xgetHelpArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfHelpArray();
                }
            }
            return new HelpList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetHelpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.HELP$34, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetHelpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.HELP$34, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfHelpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.HELP$34);
        }
    }
    
    public void setHelpArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.HELP$34);
        }
    }
    
    public void setHelpArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.HELP$34, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHelpArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.HELP$34);
        }
    }
    
    public void xsetHelpArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.HELP$34, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertHelp(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.HELP$34, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addHelp(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.HELP$34)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewHelp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.HELP$34, n);
        }
    }
    
    public STTrueFalseBlank addNewHelp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.HELP$34);
        }
    }
    
    public void removeHelp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.HELP$34, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getCancelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CancelList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getCancelArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum cancelArray = CTClientDataImpl.this.getCancelArray(n);
                    CTClientDataImpl.this.setCancelArray(n, enum1);
                    return cancelArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertCancel(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum cancelArray = CTClientDataImpl.this.getCancelArray(n);
                    CTClientDataImpl.this.removeCancel(n);
                    return cancelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCancelArray();
                }
            }
            return new CancelList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getCancelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CANCEL$36, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getCancelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CANCEL$36, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetCancelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CancelList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetCancelArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetCancelArray = CTClientDataImpl.this.xgetCancelArray(n);
                    CTClientDataImpl.this.xsetCancelArray(n, stTrueFalseBlank);
                    return xgetCancelArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewCancel(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetCancelArray = CTClientDataImpl.this.xgetCancelArray(n);
                    CTClientDataImpl.this.removeCancel(n);
                    return xgetCancelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCancelArray();
                }
            }
            return new CancelList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetCancelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CANCEL$36, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetCancelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.CANCEL$36, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfCancelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.CANCEL$36);
        }
    }
    
    public void setCancelArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.CANCEL$36);
        }
    }
    
    public void setCancelArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CANCEL$36, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCancelArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.CANCEL$36);
        }
    }
    
    public void xsetCancelArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.CANCEL$36, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertCancel(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.CANCEL$36, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addCancel(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.CANCEL$36)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewCancel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.CANCEL$36, n);
        }
    }
    
    public STTrueFalseBlank addNewCancel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.CANCEL$36);
        }
    }
    
    public void removeCancel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.CANCEL$36, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getDismissList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DismissList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getDismissArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum dismissArray = CTClientDataImpl.this.getDismissArray(n);
                    CTClientDataImpl.this.setDismissArray(n, enum1);
                    return dismissArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertDismiss(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum dismissArray = CTClientDataImpl.this.getDismissArray(n);
                    CTClientDataImpl.this.removeDismiss(n);
                    return dismissArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDismissArray();
                }
            }
            return new DismissList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getDismissArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DISMISS$38, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getDismissArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DISMISS$38, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetDismissList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DismissList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetDismissArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetDismissArray = CTClientDataImpl.this.xgetDismissArray(n);
                    CTClientDataImpl.this.xsetDismissArray(n, stTrueFalseBlank);
                    return xgetDismissArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewDismiss(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetDismissArray = CTClientDataImpl.this.xgetDismissArray(n);
                    CTClientDataImpl.this.removeDismiss(n);
                    return xgetDismissArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDismissArray();
                }
            }
            return new DismissList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetDismissArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DISMISS$38, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetDismissArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DISMISS$38, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfDismissArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DISMISS$38);
        }
    }
    
    public void setDismissArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.DISMISS$38);
        }
    }
    
    public void setDismissArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DISMISS$38, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDismissArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DISMISS$38);
        }
    }
    
    public void xsetDismissArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DISMISS$38, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertDismiss(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DISMISS$38, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addDismiss(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DISMISS$38)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewDismiss(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.DISMISS$38, n);
        }
    }
    
    public STTrueFalseBlank addNewDismiss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.DISMISS$38);
        }
    }
    
    public void removeDismiss(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DISMISS$38, n);
        }
    }
    
    public List<BigInteger> getAccelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AccelList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getAccelArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger accelArray = CTClientDataImpl.this.getAccelArray(n);
                    CTClientDataImpl.this.setAccelArray(n, bigInteger);
                    return accelArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertAccel(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger accelArray = CTClientDataImpl.this.getAccelArray(n);
                    CTClientDataImpl.this.removeAccel(n);
                    return accelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAccelArray();
                }
            }
            return new AccelList();
        }
    }
    
    @Deprecated
    public BigInteger[] getAccelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ACCEL$40, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getAccelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ACCEL$40, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetAccelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AccelList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetAccelArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetAccelArray = CTClientDataImpl.this.xgetAccelArray(n);
                    CTClientDataImpl.this.xsetAccelArray(n, xmlInteger);
                    return xgetAccelArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewAccel(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetAccelArray = CTClientDataImpl.this.xgetAccelArray(n);
                    CTClientDataImpl.this.removeAccel(n);
                    return xgetAccelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAccelArray();
                }
            }
            return new AccelList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetAccelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ACCEL$40, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetAccelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ACCEL$40, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfAccelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.ACCEL$40);
        }
    }
    
    public void setAccelArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.ACCEL$40);
        }
    }
    
    public void setAccelArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ACCEL$40, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetAccelArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.ACCEL$40);
        }
    }
    
    public void xsetAccelArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ACCEL$40, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertAccel(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.ACCEL$40, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addAccel(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.ACCEL$40)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewAccel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.ACCEL$40, n);
        }
    }
    
    public XmlInteger addNewAccel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.ACCEL$40);
        }
    }
    
    public void removeAccel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.ACCEL$40, n);
        }
    }
    
    public List<BigInteger> getAccel2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Accel2List extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getAccel2Array(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger accel2Array = CTClientDataImpl.this.getAccel2Array(n);
                    CTClientDataImpl.this.setAccel2Array(n, bigInteger);
                    return accel2Array;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertAccel2(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger accel2Array = CTClientDataImpl.this.getAccel2Array(n);
                    CTClientDataImpl.this.removeAccel2(n);
                    return accel2Array;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAccel2Array();
                }
            }
            return new Accel2List();
        }
    }
    
    @Deprecated
    public BigInteger[] getAccel2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ACCEL2$42, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getAccel2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ACCEL2$42, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetAccel2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Accel2List extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetAccel2Array(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetAccel2Array = CTClientDataImpl.this.xgetAccel2Array(n);
                    CTClientDataImpl.this.xsetAccel2Array(n, xmlInteger);
                    return xgetAccel2Array;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewAccel2(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetAccel2Array = CTClientDataImpl.this.xgetAccel2Array(n);
                    CTClientDataImpl.this.removeAccel2(n);
                    return xgetAccel2Array;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAccel2Array();
                }
            }
            return new Accel2List();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetAccel2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ACCEL2$42, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetAccel2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ACCEL2$42, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfAccel2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.ACCEL2$42);
        }
    }
    
    public void setAccel2Array(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.ACCEL2$42);
        }
    }
    
    public void setAccel2Array(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ACCEL2$42, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetAccel2Array(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.ACCEL2$42);
        }
    }
    
    public void xsetAccel2Array(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ACCEL2$42, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertAccel2(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.ACCEL2$42, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addAccel2(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.ACCEL2$42)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewAccel2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.ACCEL2$42, n);
        }
    }
    
    public XmlInteger addNewAccel2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.ACCEL2$42);
        }
    }
    
    public void removeAccel2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.ACCEL2$42, n);
        }
    }
    
    public List<BigInteger> getRowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getRowArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger rowArray = CTClientDataImpl.this.getRowArray(n);
                    CTClientDataImpl.this.setRowArray(n, bigInteger);
                    return rowArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertRow(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger rowArray = CTClientDataImpl.this.getRowArray(n);
                    CTClientDataImpl.this.removeRow(n);
                    return rowArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRowArray();
                }
            }
            return new RowList();
        }
    }
    
    @Deprecated
    public BigInteger[] getRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ROW$44, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getRowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ROW$44, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetRowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetRowArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetRowArray = CTClientDataImpl.this.xgetRowArray(n);
                    CTClientDataImpl.this.xsetRowArray(n, xmlInteger);
                    return xgetRowArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewRow(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetRowArray = CTClientDataImpl.this.xgetRowArray(n);
                    CTClientDataImpl.this.removeRow(n);
                    return xgetRowArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRowArray();
                }
            }
            return new RowList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ROW$44, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetRowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ROW$44, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.ROW$44);
        }
    }
    
    public void setRowArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.ROW$44);
        }
    }
    
    public void setRowArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ROW$44, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRowArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.ROW$44);
        }
    }
    
    public void xsetRowArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.ROW$44, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertRow(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.ROW$44, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addRow(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.ROW$44)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.ROW$44, n);
        }
    }
    
    public XmlInteger addNewRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.ROW$44);
        }
    }
    
    public void removeRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.ROW$44, n);
        }
    }
    
    public List<BigInteger> getColumnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColumnList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getColumnArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger columnArray = CTClientDataImpl.this.getColumnArray(n);
                    CTClientDataImpl.this.setColumnArray(n, bigInteger);
                    return columnArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertColumn(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger columnArray = CTClientDataImpl.this.getColumnArray(n);
                    CTClientDataImpl.this.removeColumn(n);
                    return columnArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColumnArray();
                }
            }
            return new ColumnList();
        }
    }
    
    @Deprecated
    public BigInteger[] getColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLUMN$46, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getColumnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLUMN$46, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetColumnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColumnList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetColumnArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetColumnArray = CTClientDataImpl.this.xgetColumnArray(n);
                    CTClientDataImpl.this.xsetColumnArray(n, xmlInteger);
                    return xgetColumnArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewColumn(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetColumnArray = CTClientDataImpl.this.xgetColumnArray(n);
                    CTClientDataImpl.this.removeColumn(n);
                    return xgetColumnArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColumnArray();
                }
            }
            return new ColumnList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLUMN$46, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetColumnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.COLUMN$46, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.COLUMN$46);
        }
    }
    
    public void setColumnArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.COLUMN$46);
        }
    }
    
    public void setColumnArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLUMN$46, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetColumnArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.COLUMN$46);
        }
    }
    
    public void xsetColumnArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.COLUMN$46, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertColumn(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.COLUMN$46, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addColumn(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.COLUMN$46)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.COLUMN$46, n);
        }
    }
    
    public XmlInteger addNewColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.COLUMN$46);
        }
    }
    
    public void removeColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.COLUMN$46, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getVisibleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VisibleList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getVisibleArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum visibleArray = CTClientDataImpl.this.getVisibleArray(n);
                    CTClientDataImpl.this.setVisibleArray(n, enum1);
                    return visibleArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertVisible(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum visibleArray = CTClientDataImpl.this.getVisibleArray(n);
                    CTClientDataImpl.this.removeVisible(n);
                    return visibleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVisibleArray();
                }
            }
            return new VisibleList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getVisibleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VISIBLE$48, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getVisibleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VISIBLE$48, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetVisibleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VisibleList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetVisibleArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetVisibleArray = CTClientDataImpl.this.xgetVisibleArray(n);
                    CTClientDataImpl.this.xsetVisibleArray(n, stTrueFalseBlank);
                    return xgetVisibleArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewVisible(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetVisibleArray = CTClientDataImpl.this.xgetVisibleArray(n);
                    CTClientDataImpl.this.removeVisible(n);
                    return xgetVisibleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVisibleArray();
                }
            }
            return new VisibleList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetVisibleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VISIBLE$48, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetVisibleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VISIBLE$48, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfVisibleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.VISIBLE$48);
        }
    }
    
    public void setVisibleArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.VISIBLE$48);
        }
    }
    
    public void setVisibleArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VISIBLE$48, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVisibleArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.VISIBLE$48);
        }
    }
    
    public void xsetVisibleArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VISIBLE$48, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertVisible(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.VISIBLE$48, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addVisible(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.VISIBLE$48)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewVisible(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.VISIBLE$48, n);
        }
    }
    
    public STTrueFalseBlank addNewVisible() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.VISIBLE$48);
        }
    }
    
    public void removeVisible(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.VISIBLE$48, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getRowHiddenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowHiddenList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getRowHiddenArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum rowHiddenArray = CTClientDataImpl.this.getRowHiddenArray(n);
                    CTClientDataImpl.this.setRowHiddenArray(n, enum1);
                    return rowHiddenArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertRowHidden(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum rowHiddenArray = CTClientDataImpl.this.getRowHiddenArray(n);
                    CTClientDataImpl.this.removeRowHidden(n);
                    return rowHiddenArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRowHiddenArray();
                }
            }
            return new RowHiddenList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getRowHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ROWHIDDEN$50, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getRowHiddenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ROWHIDDEN$50, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetRowHiddenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowHiddenList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetRowHiddenArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetRowHiddenArray = CTClientDataImpl.this.xgetRowHiddenArray(n);
                    CTClientDataImpl.this.xsetRowHiddenArray(n, stTrueFalseBlank);
                    return xgetRowHiddenArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewRowHidden(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetRowHiddenArray = CTClientDataImpl.this.xgetRowHiddenArray(n);
                    CTClientDataImpl.this.removeRowHidden(n);
                    return xgetRowHiddenArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRowHiddenArray();
                }
            }
            return new RowHiddenList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetRowHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.ROWHIDDEN$50, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetRowHiddenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.ROWHIDDEN$50, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfRowHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.ROWHIDDEN$50);
        }
    }
    
    public void setRowHiddenArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.ROWHIDDEN$50);
        }
    }
    
    public void setRowHiddenArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.ROWHIDDEN$50, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRowHiddenArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.ROWHIDDEN$50);
        }
    }
    
    public void xsetRowHiddenArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.ROWHIDDEN$50, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertRowHidden(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.ROWHIDDEN$50, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addRowHidden(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.ROWHIDDEN$50)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewRowHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.ROWHIDDEN$50, n);
        }
    }
    
    public STTrueFalseBlank addNewRowHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.ROWHIDDEN$50);
        }
    }
    
    public void removeRowHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.ROWHIDDEN$50, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getColHiddenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColHiddenList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getColHiddenArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum colHiddenArray = CTClientDataImpl.this.getColHiddenArray(n);
                    CTClientDataImpl.this.setColHiddenArray(n, enum1);
                    return colHiddenArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertColHidden(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum colHiddenArray = CTClientDataImpl.this.getColHiddenArray(n);
                    CTClientDataImpl.this.removeColHidden(n);
                    return colHiddenArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColHiddenArray();
                }
            }
            return new ColHiddenList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getColHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLHIDDEN$52, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getColHiddenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLHIDDEN$52, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetColHiddenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColHiddenList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetColHiddenArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetColHiddenArray = CTClientDataImpl.this.xgetColHiddenArray(n);
                    CTClientDataImpl.this.xsetColHiddenArray(n, stTrueFalseBlank);
                    return xgetColHiddenArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewColHidden(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetColHiddenArray = CTClientDataImpl.this.xgetColHiddenArray(n);
                    CTClientDataImpl.this.removeColHidden(n);
                    return xgetColHiddenArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColHiddenArray();
                }
            }
            return new ColHiddenList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetColHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLHIDDEN$52, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetColHiddenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.COLHIDDEN$52, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfColHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.COLHIDDEN$52);
        }
    }
    
    public void setColHiddenArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.COLHIDDEN$52);
        }
    }
    
    public void setColHiddenArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLHIDDEN$52, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetColHiddenArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.COLHIDDEN$52);
        }
    }
    
    public void xsetColHiddenArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.COLHIDDEN$52, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertColHidden(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.COLHIDDEN$52, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addColHidden(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.COLHIDDEN$52)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewColHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.COLHIDDEN$52, n);
        }
    }
    
    public STTrueFalseBlank addNewColHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.COLHIDDEN$52);
        }
    }
    
    public void removeColHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.COLHIDDEN$52, n);
        }
    }
    
    public List<BigInteger> getVTEditList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VTEditList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getVTEditArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger vtEditArray = CTClientDataImpl.this.getVTEditArray(n);
                    CTClientDataImpl.this.setVTEditArray(n, bigInteger);
                    return vtEditArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertVTEdit(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger vtEditArray = CTClientDataImpl.this.getVTEditArray(n);
                    CTClientDataImpl.this.removeVTEdit(n);
                    return vtEditArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVTEditArray();
                }
            }
            return new VTEditList();
        }
    }
    
    @Deprecated
    public BigInteger[] getVTEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VTEDIT$54, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getVTEditArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VTEDIT$54, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetVTEditList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VTEditList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetVTEditArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetVTEditArray = CTClientDataImpl.this.xgetVTEditArray(n);
                    CTClientDataImpl.this.xsetVTEditArray(n, xmlInteger);
                    return xgetVTEditArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewVTEdit(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetVTEditArray = CTClientDataImpl.this.xgetVTEditArray(n);
                    CTClientDataImpl.this.removeVTEdit(n);
                    return xgetVTEditArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVTEditArray();
                }
            }
            return new VTEditList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetVTEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VTEDIT$54, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetVTEditArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.VTEDIT$54, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfVTEditArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.VTEDIT$54);
        }
    }
    
    public void setVTEditArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.VTEDIT$54);
        }
    }
    
    public void setVTEditArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VTEDIT$54, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetVTEditArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.VTEDIT$54);
        }
    }
    
    public void xsetVTEditArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.VTEDIT$54, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertVTEdit(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.VTEDIT$54, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addVTEdit(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.VTEDIT$54)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewVTEdit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.VTEDIT$54, n);
        }
    }
    
    public XmlInteger addNewVTEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.VTEDIT$54);
        }
    }
    
    public void removeVTEdit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.VTEDIT$54, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getMultiLineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MultiLineList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getMultiLineArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum multiLineArray = CTClientDataImpl.this.getMultiLineArray(n);
                    CTClientDataImpl.this.setMultiLineArray(n, enum1);
                    return multiLineArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertMultiLine(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum multiLineArray = CTClientDataImpl.this.getMultiLineArray(n);
                    CTClientDataImpl.this.removeMultiLine(n);
                    return multiLineArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMultiLineArray();
                }
            }
            return new MultiLineList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getMultiLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MULTILINE$56, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getMultiLineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MULTILINE$56, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetMultiLineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MultiLineList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetMultiLineArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetMultiLineArray = CTClientDataImpl.this.xgetMultiLineArray(n);
                    CTClientDataImpl.this.xsetMultiLineArray(n, stTrueFalseBlank);
                    return xgetMultiLineArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewMultiLine(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetMultiLineArray = CTClientDataImpl.this.xgetMultiLineArray(n);
                    CTClientDataImpl.this.removeMultiLine(n);
                    return xgetMultiLineArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMultiLineArray();
                }
            }
            return new MultiLineList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetMultiLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MULTILINE$56, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetMultiLineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MULTILINE$56, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfMultiLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MULTILINE$56);
        }
    }
    
    public void setMultiLineArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.MULTILINE$56);
        }
    }
    
    public void setMultiLineArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MULTILINE$56, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetMultiLineArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MULTILINE$56);
        }
    }
    
    public void xsetMultiLineArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MULTILINE$56, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertMultiLine(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MULTILINE$56, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addMultiLine(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MULTILINE$56)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewMultiLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.MULTILINE$56, n);
        }
    }
    
    public STTrueFalseBlank addNewMultiLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.MULTILINE$56);
        }
    }
    
    public void removeMultiLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MULTILINE$56, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getVScrollList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VScrollList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getVScrollArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum vScrollArray = CTClientDataImpl.this.getVScrollArray(n);
                    CTClientDataImpl.this.setVScrollArray(n, enum1);
                    return vScrollArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertVScroll(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum vScrollArray = CTClientDataImpl.this.getVScrollArray(n);
                    CTClientDataImpl.this.removeVScroll(n);
                    return vScrollArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVScrollArray();
                }
            }
            return new VScrollList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getVScrollArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VSCROLL$58, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getVScrollArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VSCROLL$58, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetVScrollList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VScrollList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetVScrollArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetVScrollArray = CTClientDataImpl.this.xgetVScrollArray(n);
                    CTClientDataImpl.this.xsetVScrollArray(n, stTrueFalseBlank);
                    return xgetVScrollArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewVScroll(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetVScrollArray = CTClientDataImpl.this.xgetVScrollArray(n);
                    CTClientDataImpl.this.removeVScroll(n);
                    return xgetVScrollArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfVScrollArray();
                }
            }
            return new VScrollList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetVScrollArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VSCROLL$58, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetVScrollArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VSCROLL$58, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfVScrollArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.VSCROLL$58);
        }
    }
    
    public void setVScrollArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.VSCROLL$58);
        }
    }
    
    public void setVScrollArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VSCROLL$58, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVScrollArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.VSCROLL$58);
        }
    }
    
    public void xsetVScrollArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VSCROLL$58, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertVScroll(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.VSCROLL$58, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addVScroll(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.VSCROLL$58)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewVScroll(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.VSCROLL$58, n);
        }
    }
    
    public STTrueFalseBlank addNewVScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.VSCROLL$58);
        }
    }
    
    public void removeVScroll(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.VSCROLL$58, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getValidIdsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ValidIdsList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getValidIdsArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum validIdsArray = CTClientDataImpl.this.getValidIdsArray(n);
                    CTClientDataImpl.this.setValidIdsArray(n, enum1);
                    return validIdsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertValidIds(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum validIdsArray = CTClientDataImpl.this.getValidIdsArray(n);
                    CTClientDataImpl.this.removeValidIds(n);
                    return validIdsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfValidIdsArray();
                }
            }
            return new ValidIdsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getValidIdsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VALIDIDS$60, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getValidIdsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VALIDIDS$60, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetValidIdsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ValidIdsList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetValidIdsArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetValidIdsArray = CTClientDataImpl.this.xgetValidIdsArray(n);
                    CTClientDataImpl.this.xsetValidIdsArray(n, stTrueFalseBlank);
                    return xgetValidIdsArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewValidIds(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetValidIdsArray = CTClientDataImpl.this.xgetValidIdsArray(n);
                    CTClientDataImpl.this.removeValidIds(n);
                    return xgetValidIdsArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfValidIdsArray();
                }
            }
            return new ValidIdsList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetValidIdsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VALIDIDS$60, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetValidIdsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VALIDIDS$60, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfValidIdsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.VALIDIDS$60);
        }
    }
    
    public void setValidIdsArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.VALIDIDS$60);
        }
    }
    
    public void setValidIdsArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VALIDIDS$60, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetValidIdsArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.VALIDIDS$60);
        }
    }
    
    public void xsetValidIdsArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.VALIDIDS$60, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertValidIds(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.VALIDIDS$60, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addValidIds(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.VALIDIDS$60)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewValidIds(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.VALIDIDS$60, n);
        }
    }
    
    public STTrueFalseBlank addNewValidIds() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.VALIDIDS$60);
        }
    }
    
    public void removeValidIds(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.VALIDIDS$60, n);
        }
    }
    
    public List<String> getFmlaRangeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaRangeList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaRangeArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaRangeArray = CTClientDataImpl.this.getFmlaRangeArray(n);
                    CTClientDataImpl.this.setFmlaRangeArray(n, s);
                    return fmlaRangeArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaRange(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaRangeArray = CTClientDataImpl.this.getFmlaRangeArray(n);
                    CTClientDataImpl.this.removeFmlaRange(n);
                    return fmlaRangeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaRangeArray();
                }
            }
            return new FmlaRangeList();
        }
    }
    
    @Deprecated
    public String[] getFmlaRangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLARANGE$62, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaRangeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLARANGE$62, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaRangeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaRangeList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaRangeArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaRangeArray = CTClientDataImpl.this.xgetFmlaRangeArray(n);
                    CTClientDataImpl.this.xsetFmlaRangeArray(n, xmlString);
                    return xgetFmlaRangeArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaRange(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaRangeArray = CTClientDataImpl.this.xgetFmlaRangeArray(n);
                    CTClientDataImpl.this.removeFmlaRange(n);
                    return xgetFmlaRangeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaRangeArray();
                }
            }
            return new FmlaRangeList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaRangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLARANGE$62, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaRangeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLARANGE$62, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaRangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLARANGE$62);
        }
    }
    
    public void setFmlaRangeArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLARANGE$62);
        }
    }
    
    public void setFmlaRangeArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLARANGE$62, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaRangeArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLARANGE$62);
        }
    }
    
    public void xsetFmlaRangeArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLARANGE$62, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaRange(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLARANGE$62, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaRange(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLARANGE$62)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaRange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLARANGE$62, n);
        }
    }
    
    public XmlString addNewFmlaRange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLARANGE$62);
        }
    }
    
    public void removeFmlaRange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLARANGE$62, n);
        }
    }
    
    public List<BigInteger> getWidthMinList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WidthMinList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getWidthMinArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger widthMinArray = CTClientDataImpl.this.getWidthMinArray(n);
                    CTClientDataImpl.this.setWidthMinArray(n, bigInteger);
                    return widthMinArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertWidthMin(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger widthMinArray = CTClientDataImpl.this.getWidthMinArray(n);
                    CTClientDataImpl.this.removeWidthMin(n);
                    return widthMinArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfWidthMinArray();
                }
            }
            return new WidthMinList();
        }
    }
    
    @Deprecated
    public BigInteger[] getWidthMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.WIDTHMIN$64, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getWidthMinArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.WIDTHMIN$64, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetWidthMinList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WidthMinList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetWidthMinArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetWidthMinArray = CTClientDataImpl.this.xgetWidthMinArray(n);
                    CTClientDataImpl.this.xsetWidthMinArray(n, xmlInteger);
                    return xgetWidthMinArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewWidthMin(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetWidthMinArray = CTClientDataImpl.this.xgetWidthMinArray(n);
                    CTClientDataImpl.this.removeWidthMin(n);
                    return xgetWidthMinArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfWidthMinArray();
                }
            }
            return new WidthMinList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetWidthMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.WIDTHMIN$64, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetWidthMinArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.WIDTHMIN$64, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfWidthMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.WIDTHMIN$64);
        }
    }
    
    public void setWidthMinArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.WIDTHMIN$64);
        }
    }
    
    public void setWidthMinArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.WIDTHMIN$64, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetWidthMinArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.WIDTHMIN$64);
        }
    }
    
    public void xsetWidthMinArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.WIDTHMIN$64, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertWidthMin(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.WIDTHMIN$64, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addWidthMin(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.WIDTHMIN$64)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewWidthMin(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.WIDTHMIN$64, n);
        }
    }
    
    public XmlInteger addNewWidthMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.WIDTHMIN$64);
        }
    }
    
    public void removeWidthMin(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.WIDTHMIN$64, n);
        }
    }
    
    public List<BigInteger> getSelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SelList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getSelArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger selArray = CTClientDataImpl.this.getSelArray(n);
                    CTClientDataImpl.this.setSelArray(n, bigInteger);
                    return selArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertSel(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger selArray = CTClientDataImpl.this.getSelArray(n);
                    CTClientDataImpl.this.removeSel(n);
                    return selArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSelArray();
                }
            }
            return new SelList();
        }
    }
    
    @Deprecated
    public BigInteger[] getSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SEL$66, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getSelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SEL$66, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetSelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SelList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetSelArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetSelArray = CTClientDataImpl.this.xgetSelArray(n);
                    CTClientDataImpl.this.xsetSelArray(n, xmlInteger);
                    return xgetSelArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewSel(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetSelArray = CTClientDataImpl.this.xgetSelArray(n);
                    CTClientDataImpl.this.removeSel(n);
                    return xgetSelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSelArray();
                }
            }
            return new SelList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SEL$66, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetSelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.SEL$66, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SEL$66);
        }
    }
    
    public void setSelArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SEL$66);
        }
    }
    
    public void setSelArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SEL$66, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetSelArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SEL$66);
        }
    }
    
    public void xsetSelArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.SEL$66, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertSel(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SEL$66, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addSel(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SEL$66)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewSel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.SEL$66, n);
        }
    }
    
    public XmlInteger addNewSel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.SEL$66);
        }
    }
    
    public void removeSel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SEL$66, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getNoThreeD2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoThreeD2List extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getNoThreeD2Array(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum noThreeD2Array = CTClientDataImpl.this.getNoThreeD2Array(n);
                    CTClientDataImpl.this.setNoThreeD2Array(n, enum1);
                    return noThreeD2Array;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertNoThreeD2(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum noThreeD2Array = CTClientDataImpl.this.getNoThreeD2Array(n);
                    CTClientDataImpl.this.removeNoThreeD2(n);
                    return noThreeD2Array;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfNoThreeD2Array();
                }
            }
            return new NoThreeD2List();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getNoThreeD2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.NOTHREED2$68, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getNoThreeD2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.NOTHREED2$68, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetNoThreeD2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoThreeD2List extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetNoThreeD2Array(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetNoThreeD2Array = CTClientDataImpl.this.xgetNoThreeD2Array(n);
                    CTClientDataImpl.this.xsetNoThreeD2Array(n, stTrueFalseBlank);
                    return xgetNoThreeD2Array;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewNoThreeD2(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetNoThreeD2Array = CTClientDataImpl.this.xgetNoThreeD2Array(n);
                    CTClientDataImpl.this.removeNoThreeD2(n);
                    return xgetNoThreeD2Array;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfNoThreeD2Array();
                }
            }
            return new NoThreeD2List();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetNoThreeD2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.NOTHREED2$68, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetNoThreeD2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.NOTHREED2$68, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfNoThreeD2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.NOTHREED2$68);
        }
    }
    
    public void setNoThreeD2Array(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.NOTHREED2$68);
        }
    }
    
    public void setNoThreeD2Array(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.NOTHREED2$68, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetNoThreeD2Array(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.NOTHREED2$68);
        }
    }
    
    public void xsetNoThreeD2Array(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.NOTHREED2$68, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertNoThreeD2(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.NOTHREED2$68, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addNoThreeD2(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.NOTHREED2$68)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewNoThreeD2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.NOTHREED2$68, n);
        }
    }
    
    public STTrueFalseBlank addNewNoThreeD2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.NOTHREED2$68);
        }
    }
    
    public void removeNoThreeD2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.NOTHREED2$68, n);
        }
    }
    
    public List<String> getSelTypeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SelTypeList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getSelTypeArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String selTypeArray = CTClientDataImpl.this.getSelTypeArray(n);
                    CTClientDataImpl.this.setSelTypeArray(n, s);
                    return selTypeArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertSelType(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String selTypeArray = CTClientDataImpl.this.getSelTypeArray(n);
                    CTClientDataImpl.this.removeSelType(n);
                    return selTypeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSelTypeArray();
                }
            }
            return new SelTypeList();
        }
    }
    
    @Deprecated
    public String[] getSelTypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SELTYPE$70, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getSelTypeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SELTYPE$70, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetSelTypeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SelTypeList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetSelTypeArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetSelTypeArray = CTClientDataImpl.this.xgetSelTypeArray(n);
                    CTClientDataImpl.this.xsetSelTypeArray(n, xmlString);
                    return xgetSelTypeArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewSelType(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetSelTypeArray = CTClientDataImpl.this.xgetSelTypeArray(n);
                    CTClientDataImpl.this.removeSelType(n);
                    return xgetSelTypeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfSelTypeArray();
                }
            }
            return new SelTypeList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetSelTypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SELTYPE$70, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetSelTypeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SELTYPE$70, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfSelTypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SELTYPE$70);
        }
    }
    
    public void setSelTypeArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SELTYPE$70);
        }
    }
    
    public void setSelTypeArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SELTYPE$70, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSelTypeArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SELTYPE$70);
        }
    }
    
    public void xsetSelTypeArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SELTYPE$70, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertSelType(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SELTYPE$70, n)).setStringValue(stringValue);
        }
    }
    
    public void addSelType(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SELTYPE$70)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewSelType(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.SELTYPE$70, n);
        }
    }
    
    public XmlString addNewSelType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.SELTYPE$70);
        }
    }
    
    public void removeSelType(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SELTYPE$70, n);
        }
    }
    
    public List<String> getMultiSelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MultiSelList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getMultiSelArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String multiSelArray = CTClientDataImpl.this.getMultiSelArray(n);
                    CTClientDataImpl.this.setMultiSelArray(n, s);
                    return multiSelArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertMultiSel(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String multiSelArray = CTClientDataImpl.this.getMultiSelArray(n);
                    CTClientDataImpl.this.removeMultiSel(n);
                    return multiSelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMultiSelArray();
                }
            }
            return new MultiSelList();
        }
    }
    
    @Deprecated
    public String[] getMultiSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MULTISEL$72, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getMultiSelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MULTISEL$72, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetMultiSelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MultiSelList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetMultiSelArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetMultiSelArray = CTClientDataImpl.this.xgetMultiSelArray(n);
                    CTClientDataImpl.this.xsetMultiSelArray(n, xmlString);
                    return xgetMultiSelArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewMultiSel(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetMultiSelArray = CTClientDataImpl.this.xgetMultiSelArray(n);
                    CTClientDataImpl.this.removeMultiSel(n);
                    return xgetMultiSelArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMultiSelArray();
                }
            }
            return new MultiSelList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetMultiSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MULTISEL$72, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetMultiSelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.MULTISEL$72, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfMultiSelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MULTISEL$72);
        }
    }
    
    public void setMultiSelArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.MULTISEL$72);
        }
    }
    
    public void setMultiSelArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MULTISEL$72, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMultiSelArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MULTISEL$72);
        }
    }
    
    public void xsetMultiSelArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.MULTISEL$72, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertMultiSel(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MULTISEL$72, n)).setStringValue(stringValue);
        }
    }
    
    public void addMultiSel(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MULTISEL$72)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewMultiSel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.MULTISEL$72, n);
        }
    }
    
    public XmlString addNewMultiSel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.MULTISEL$72);
        }
    }
    
    public void removeMultiSel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MULTISEL$72, n);
        }
    }
    
    public List<String> getLCTList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LCTList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getLCTArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String lctArray = CTClientDataImpl.this.getLCTArray(n);
                    CTClientDataImpl.this.setLCTArray(n, s);
                    return lctArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertLCT(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String lctArray = CTClientDataImpl.this.getLCTArray(n);
                    CTClientDataImpl.this.removeLCT(n);
                    return lctArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLCTArray();
                }
            }
            return new LCTList();
        }
    }
    
    @Deprecated
    public String[] getLCTArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LCT$74, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getLCTArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LCT$74, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetLCTList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LCTList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetLCTArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetLCTArray = CTClientDataImpl.this.xgetLCTArray(n);
                    CTClientDataImpl.this.xsetLCTArray(n, xmlString);
                    return xgetLCTArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewLCT(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetLCTArray = CTClientDataImpl.this.xgetLCTArray(n);
                    CTClientDataImpl.this.removeLCT(n);
                    return xgetLCTArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfLCTArray();
                }
            }
            return new LCTList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetLCTArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LCT$74, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetLCTArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.LCT$74, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfLCTArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.LCT$74);
        }
    }
    
    public void setLCTArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.LCT$74);
        }
    }
    
    public void setLCTArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LCT$74, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLCTArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.LCT$74);
        }
    }
    
    public void xsetLCTArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.LCT$74, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertLCT(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.LCT$74, n)).setStringValue(stringValue);
        }
    }
    
    public void addLCT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.LCT$74)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewLCT(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.LCT$74, n);
        }
    }
    
    public XmlString addNewLCT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.LCT$74);
        }
    }
    
    public void removeLCT(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.LCT$74, n);
        }
    }
    
    public List<String> getListItemList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ListItemList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getListItemArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String listItemArray = CTClientDataImpl.this.getListItemArray(n);
                    CTClientDataImpl.this.setListItemArray(n, s);
                    return listItemArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertListItem(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String listItemArray = CTClientDataImpl.this.getListItemArray(n);
                    CTClientDataImpl.this.removeListItem(n);
                    return listItemArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfListItemArray();
                }
            }
            return new ListItemList();
        }
    }
    
    @Deprecated
    public String[] getListItemArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LISTITEM$76, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getListItemArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LISTITEM$76, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetListItemList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ListItemList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetListItemArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetListItemArray = CTClientDataImpl.this.xgetListItemArray(n);
                    CTClientDataImpl.this.xsetListItemArray(n, xmlString);
                    return xgetListItemArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewListItem(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetListItemArray = CTClientDataImpl.this.xgetListItemArray(n);
                    CTClientDataImpl.this.removeListItem(n);
                    return xgetListItemArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfListItemArray();
                }
            }
            return new ListItemList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetListItemArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.LISTITEM$76, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetListItemArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.LISTITEM$76, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfListItemArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.LISTITEM$76);
        }
    }
    
    public void setListItemArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.LISTITEM$76);
        }
    }
    
    public void setListItemArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.LISTITEM$76, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetListItemArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.LISTITEM$76);
        }
    }
    
    public void xsetListItemArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.LISTITEM$76, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertListItem(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.LISTITEM$76, n)).setStringValue(stringValue);
        }
    }
    
    public void addListItem(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.LISTITEM$76)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewListItem(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.LISTITEM$76, n);
        }
    }
    
    public XmlString addNewListItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.LISTITEM$76);
        }
    }
    
    public void removeListItem(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.LISTITEM$76, n);
        }
    }
    
    public List<String> getDropStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DropStyleList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getDropStyleArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String dropStyleArray = CTClientDataImpl.this.getDropStyleArray(n);
                    CTClientDataImpl.this.setDropStyleArray(n, s);
                    return dropStyleArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertDropStyle(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String dropStyleArray = CTClientDataImpl.this.getDropStyleArray(n);
                    CTClientDataImpl.this.removeDropStyle(n);
                    return dropStyleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDropStyleArray();
                }
            }
            return new DropStyleList();
        }
    }
    
    @Deprecated
    public String[] getDropStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DROPSTYLE$78, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getDropStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DROPSTYLE$78, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetDropStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DropStyleList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetDropStyleArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetDropStyleArray = CTClientDataImpl.this.xgetDropStyleArray(n);
                    CTClientDataImpl.this.xsetDropStyleArray(n, xmlString);
                    return xgetDropStyleArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewDropStyle(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetDropStyleArray = CTClientDataImpl.this.xgetDropStyleArray(n);
                    CTClientDataImpl.this.removeDropStyle(n);
                    return xgetDropStyleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDropStyleArray();
                }
            }
            return new DropStyleList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetDropStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DROPSTYLE$78, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetDropStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.DROPSTYLE$78, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfDropStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DROPSTYLE$78);
        }
    }
    
    public void setDropStyleArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.DROPSTYLE$78);
        }
    }
    
    public void setDropStyleArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DROPSTYLE$78, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDropStyleArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DROPSTYLE$78);
        }
    }
    
    public void xsetDropStyleArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.DROPSTYLE$78, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertDropStyle(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DROPSTYLE$78, n)).setStringValue(stringValue);
        }
    }
    
    public void addDropStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DROPSTYLE$78)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewDropStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.DROPSTYLE$78, n);
        }
    }
    
    public XmlString addNewDropStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.DROPSTYLE$78);
        }
    }
    
    public void removeDropStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DROPSTYLE$78, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getColoredList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColoredList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getColoredArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum coloredArray = CTClientDataImpl.this.getColoredArray(n);
                    CTClientDataImpl.this.setColoredArray(n, enum1);
                    return coloredArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertColored(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum coloredArray = CTClientDataImpl.this.getColoredArray(n);
                    CTClientDataImpl.this.removeColored(n);
                    return coloredArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColoredArray();
                }
            }
            return new ColoredList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getColoredArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLORED$80, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getColoredArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLORED$80, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetColoredList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColoredList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetColoredArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetColoredArray = CTClientDataImpl.this.xgetColoredArray(n);
                    CTClientDataImpl.this.xsetColoredArray(n, stTrueFalseBlank);
                    return xgetColoredArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewColored(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetColoredArray = CTClientDataImpl.this.xgetColoredArray(n);
                    CTClientDataImpl.this.removeColored(n);
                    return xgetColoredArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfColoredArray();
                }
            }
            return new ColoredList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetColoredArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.COLORED$80, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetColoredArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.COLORED$80, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfColoredArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.COLORED$80);
        }
    }
    
    public void setColoredArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.COLORED$80);
        }
    }
    
    public void setColoredArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.COLORED$80, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetColoredArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.COLORED$80);
        }
    }
    
    public void xsetColoredArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.COLORED$80, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertColored(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.COLORED$80, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addColored(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.COLORED$80)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewColored(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.COLORED$80, n);
        }
    }
    
    public STTrueFalseBlank addNewColored() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.COLORED$80);
        }
    }
    
    public void removeColored(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.COLORED$80, n);
        }
    }
    
    public List<BigInteger> getDropLinesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DropLinesList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getDropLinesArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger dropLinesArray = CTClientDataImpl.this.getDropLinesArray(n);
                    CTClientDataImpl.this.setDropLinesArray(n, bigInteger);
                    return dropLinesArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertDropLines(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger dropLinesArray = CTClientDataImpl.this.getDropLinesArray(n);
                    CTClientDataImpl.this.removeDropLines(n);
                    return dropLinesArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDropLinesArray();
                }
            }
            return new DropLinesList();
        }
    }
    
    @Deprecated
    public BigInteger[] getDropLinesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DROPLINES$82, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getDropLinesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DROPLINES$82, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetDropLinesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DropLinesList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetDropLinesArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetDropLinesArray = CTClientDataImpl.this.xgetDropLinesArray(n);
                    CTClientDataImpl.this.xsetDropLinesArray(n, xmlInteger);
                    return xgetDropLinesArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewDropLines(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetDropLinesArray = CTClientDataImpl.this.xgetDropLinesArray(n);
                    CTClientDataImpl.this.removeDropLines(n);
                    return xgetDropLinesArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDropLinesArray();
                }
            }
            return new DropLinesList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetDropLinesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DROPLINES$82, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetDropLinesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.DROPLINES$82, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfDropLinesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DROPLINES$82);
        }
    }
    
    public void setDropLinesArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.DROPLINES$82);
        }
    }
    
    public void setDropLinesArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DROPLINES$82, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDropLinesArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DROPLINES$82);
        }
    }
    
    public void xsetDropLinesArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.DROPLINES$82, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertDropLines(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DROPLINES$82, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addDropLines(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DROPLINES$82)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewDropLines(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.DROPLINES$82, n);
        }
    }
    
    public XmlInteger addNewDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.DROPLINES$82);
        }
    }
    
    public void removeDropLines(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DROPLINES$82, n);
        }
    }
    
    public List<BigInteger> getCheckedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CheckedList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getCheckedArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger checkedArray = CTClientDataImpl.this.getCheckedArray(n);
                    CTClientDataImpl.this.setCheckedArray(n, bigInteger);
                    return checkedArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertChecked(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger checkedArray = CTClientDataImpl.this.getCheckedArray(n);
                    CTClientDataImpl.this.removeChecked(n);
                    return checkedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCheckedArray();
                }
            }
            return new CheckedList();
        }
    }
    
    @Deprecated
    public BigInteger[] getCheckedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CHECKED$84, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getCheckedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CHECKED$84, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetCheckedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CheckedList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetCheckedArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetCheckedArray = CTClientDataImpl.this.xgetCheckedArray(n);
                    CTClientDataImpl.this.xsetCheckedArray(n, xmlInteger);
                    return xgetCheckedArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewChecked(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetCheckedArray = CTClientDataImpl.this.xgetCheckedArray(n);
                    CTClientDataImpl.this.removeChecked(n);
                    return xgetCheckedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCheckedArray();
                }
            }
            return new CheckedList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetCheckedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CHECKED$84, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetCheckedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.CHECKED$84, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfCheckedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.CHECKED$84);
        }
    }
    
    public void setCheckedArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.CHECKED$84);
        }
    }
    
    public void setCheckedArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CHECKED$84, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetCheckedArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.CHECKED$84);
        }
    }
    
    public void xsetCheckedArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.CHECKED$84, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertChecked(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.CHECKED$84, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addChecked(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.CHECKED$84)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewChecked(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.CHECKED$84, n);
        }
    }
    
    public XmlInteger addNewChecked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.CHECKED$84);
        }
    }
    
    public void removeChecked(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.CHECKED$84, n);
        }
    }
    
    public List<String> getFmlaLinkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaLinkList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaLinkArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaLinkArray = CTClientDataImpl.this.getFmlaLinkArray(n);
                    CTClientDataImpl.this.setFmlaLinkArray(n, s);
                    return fmlaLinkArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaLink(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaLinkArray = CTClientDataImpl.this.getFmlaLinkArray(n);
                    CTClientDataImpl.this.removeFmlaLink(n);
                    return fmlaLinkArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaLinkArray();
                }
            }
            return new FmlaLinkList();
        }
    }
    
    @Deprecated
    public String[] getFmlaLinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLALINK$86, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaLinkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLALINK$86, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaLinkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaLinkList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaLinkArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaLinkArray = CTClientDataImpl.this.xgetFmlaLinkArray(n);
                    CTClientDataImpl.this.xsetFmlaLinkArray(n, xmlString);
                    return xgetFmlaLinkArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaLink(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaLinkArray = CTClientDataImpl.this.xgetFmlaLinkArray(n);
                    CTClientDataImpl.this.removeFmlaLink(n);
                    return xgetFmlaLinkArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaLinkArray();
                }
            }
            return new FmlaLinkList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaLinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLALINK$86, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaLinkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLALINK$86, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaLinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLALINK$86);
        }
    }
    
    public void setFmlaLinkArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLALINK$86);
        }
    }
    
    public void setFmlaLinkArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLALINK$86, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaLinkArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLALINK$86);
        }
    }
    
    public void xsetFmlaLinkArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLALINK$86, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaLink(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLALINK$86, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaLink(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLALINK$86)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaLink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLALINK$86, n);
        }
    }
    
    public XmlString addNewFmlaLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLALINK$86);
        }
    }
    
    public void removeFmlaLink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLALINK$86, n);
        }
    }
    
    public List<String> getFmlaPictList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaPictList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaPictArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaPictArray = CTClientDataImpl.this.getFmlaPictArray(n);
                    CTClientDataImpl.this.setFmlaPictArray(n, s);
                    return fmlaPictArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaPict(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaPictArray = CTClientDataImpl.this.getFmlaPictArray(n);
                    CTClientDataImpl.this.removeFmlaPict(n);
                    return fmlaPictArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaPictArray();
                }
            }
            return new FmlaPictList();
        }
    }
    
    @Deprecated
    public String[] getFmlaPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAPICT$88, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaPictArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAPICT$88, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaPictList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaPictList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaPictArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaPictArray = CTClientDataImpl.this.xgetFmlaPictArray(n);
                    CTClientDataImpl.this.xsetFmlaPictArray(n, xmlString);
                    return xgetFmlaPictArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaPict(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaPictArray = CTClientDataImpl.this.xgetFmlaPictArray(n);
                    CTClientDataImpl.this.removeFmlaPict(n);
                    return xgetFmlaPictArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaPictArray();
                }
            }
            return new FmlaPictList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAPICT$88, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaPictArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAPICT$88, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLAPICT$88);
        }
    }
    
    public void setFmlaPictArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLAPICT$88);
        }
    }
    
    public void setFmlaPictArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAPICT$88, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaPictArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLAPICT$88);
        }
    }
    
    public void xsetFmlaPictArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAPICT$88, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaPict(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLAPICT$88, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaPict(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLAPICT$88)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaPict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLAPICT$88, n);
        }
    }
    
    public XmlString addNewFmlaPict() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLAPICT$88);
        }
    }
    
    public void removeFmlaPict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLAPICT$88, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getNoThreeDList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoThreeDList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getNoThreeDArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum noThreeDArray = CTClientDataImpl.this.getNoThreeDArray(n);
                    CTClientDataImpl.this.setNoThreeDArray(n, enum1);
                    return noThreeDArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertNoThreeD(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum noThreeDArray = CTClientDataImpl.this.getNoThreeDArray(n);
                    CTClientDataImpl.this.removeNoThreeD(n);
                    return noThreeDArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfNoThreeDArray();
                }
            }
            return new NoThreeDList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getNoThreeDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.NOTHREED$90, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getNoThreeDArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.NOTHREED$90, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetNoThreeDList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoThreeDList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetNoThreeDArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetNoThreeDArray = CTClientDataImpl.this.xgetNoThreeDArray(n);
                    CTClientDataImpl.this.xsetNoThreeDArray(n, stTrueFalseBlank);
                    return xgetNoThreeDArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewNoThreeD(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetNoThreeDArray = CTClientDataImpl.this.xgetNoThreeDArray(n);
                    CTClientDataImpl.this.removeNoThreeD(n);
                    return xgetNoThreeDArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfNoThreeDArray();
                }
            }
            return new NoThreeDList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetNoThreeDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.NOTHREED$90, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetNoThreeDArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.NOTHREED$90, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfNoThreeDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.NOTHREED$90);
        }
    }
    
    public void setNoThreeDArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.NOTHREED$90);
        }
    }
    
    public void setNoThreeDArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.NOTHREED$90, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetNoThreeDArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.NOTHREED$90);
        }
    }
    
    public void xsetNoThreeDArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.NOTHREED$90, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertNoThreeD(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.NOTHREED$90, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addNoThreeD(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.NOTHREED$90)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewNoThreeD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.NOTHREED$90, n);
        }
    }
    
    public STTrueFalseBlank addNewNoThreeD() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.NOTHREED$90);
        }
    }
    
    public void removeNoThreeD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.NOTHREED$90, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getFirstButtonList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FirstButtonList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getFirstButtonArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum firstButtonArray = CTClientDataImpl.this.getFirstButtonArray(n);
                    CTClientDataImpl.this.setFirstButtonArray(n, enum1);
                    return firstButtonArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertFirstButton(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum firstButtonArray = CTClientDataImpl.this.getFirstButtonArray(n);
                    CTClientDataImpl.this.removeFirstButton(n);
                    return firstButtonArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFirstButtonArray();
                }
            }
            return new FirstButtonList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getFirstButtonArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FIRSTBUTTON$92, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getFirstButtonArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FIRSTBUTTON$92, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetFirstButtonList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FirstButtonList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetFirstButtonArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetFirstButtonArray = CTClientDataImpl.this.xgetFirstButtonArray(n);
                    CTClientDataImpl.this.xsetFirstButtonArray(n, stTrueFalseBlank);
                    return xgetFirstButtonArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewFirstButton(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetFirstButtonArray = CTClientDataImpl.this.xgetFirstButtonArray(n);
                    CTClientDataImpl.this.removeFirstButton(n);
                    return xgetFirstButtonArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFirstButtonArray();
                }
            }
            return new FirstButtonList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetFirstButtonArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FIRSTBUTTON$92, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetFirstButtonArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.FIRSTBUTTON$92, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfFirstButtonArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FIRSTBUTTON$92);
        }
    }
    
    public void setFirstButtonArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.FIRSTBUTTON$92);
        }
    }
    
    public void setFirstButtonArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FIRSTBUTTON$92, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFirstButtonArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FIRSTBUTTON$92);
        }
    }
    
    public void xsetFirstButtonArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.FIRSTBUTTON$92, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertFirstButton(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FIRSTBUTTON$92, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addFirstButton(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FIRSTBUTTON$92)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewFirstButton(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.FIRSTBUTTON$92, n);
        }
    }
    
    public STTrueFalseBlank addNewFirstButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.FIRSTBUTTON$92);
        }
    }
    
    public void removeFirstButton(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FIRSTBUTTON$92, n);
        }
    }
    
    public List<String> getFmlaGroupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaGroupList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaGroupArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaGroupArray = CTClientDataImpl.this.getFmlaGroupArray(n);
                    CTClientDataImpl.this.setFmlaGroupArray(n, s);
                    return fmlaGroupArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaGroup(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaGroupArray = CTClientDataImpl.this.getFmlaGroupArray(n);
                    CTClientDataImpl.this.removeFmlaGroup(n);
                    return fmlaGroupArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaGroupArray();
                }
            }
            return new FmlaGroupList();
        }
    }
    
    @Deprecated
    public String[] getFmlaGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAGROUP$94, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaGroupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAGROUP$94, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaGroupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaGroupList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaGroupArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaGroupArray = CTClientDataImpl.this.xgetFmlaGroupArray(n);
                    CTClientDataImpl.this.xsetFmlaGroupArray(n, xmlString);
                    return xgetFmlaGroupArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaGroup(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaGroupArray = CTClientDataImpl.this.xgetFmlaGroupArray(n);
                    CTClientDataImpl.this.removeFmlaGroup(n);
                    return xgetFmlaGroupArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaGroupArray();
                }
            }
            return new FmlaGroupList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLAGROUP$94, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaGroupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAGROUP$94, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLAGROUP$94);
        }
    }
    
    public void setFmlaGroupArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLAGROUP$94);
        }
    }
    
    public void setFmlaGroupArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLAGROUP$94, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaGroupArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLAGROUP$94);
        }
    }
    
    public void xsetFmlaGroupArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLAGROUP$94, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaGroup(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLAGROUP$94, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaGroup(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLAGROUP$94)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLAGROUP$94, n);
        }
    }
    
    public XmlString addNewFmlaGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLAGROUP$94);
        }
    }
    
    public void removeFmlaGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLAGROUP$94, n);
        }
    }
    
    public List<BigInteger> getValList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ValList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getValArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger valArray = CTClientDataImpl.this.getValArray(n);
                    CTClientDataImpl.this.setValArray(n, bigInteger);
                    return valArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertVal(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger valArray = CTClientDataImpl.this.getValArray(n);
                    CTClientDataImpl.this.removeVal(n);
                    return valArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfValArray();
                }
            }
            return new ValList();
        }
    }
    
    @Deprecated
    public BigInteger[] getValArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VAL$96, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getValArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VAL$96, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetValList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ValList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetValArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetValArray = CTClientDataImpl.this.xgetValArray(n);
                    CTClientDataImpl.this.xsetValArray(n, xmlInteger);
                    return xgetValArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewVal(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetValArray = CTClientDataImpl.this.xgetValArray(n);
                    CTClientDataImpl.this.removeVal(n);
                    return xgetValArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfValArray();
                }
            }
            return new ValList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetValArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.VAL$96, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetValArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.VAL$96, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfValArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.VAL$96);
        }
    }
    
    public void setValArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.VAL$96);
        }
    }
    
    public void setValArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.VAL$96, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetValArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.VAL$96);
        }
    }
    
    public void xsetValArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.VAL$96, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertVal(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.VAL$96, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addVal(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.VAL$96)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewVal(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.VAL$96, n);
        }
    }
    
    public XmlInteger addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.VAL$96);
        }
    }
    
    public void removeVal(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.VAL$96, n);
        }
    }
    
    public List<BigInteger> getMinList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MinList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getMinArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger minArray = CTClientDataImpl.this.getMinArray(n);
                    CTClientDataImpl.this.setMinArray(n, bigInteger);
                    return minArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertMin(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger minArray = CTClientDataImpl.this.getMinArray(n);
                    CTClientDataImpl.this.removeMin(n);
                    return minArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMinArray();
                }
            }
            return new MinList();
        }
    }
    
    @Deprecated
    public BigInteger[] getMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MIN$98, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getMinArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MIN$98, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetMinList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MinList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetMinArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetMinArray = CTClientDataImpl.this.xgetMinArray(n);
                    CTClientDataImpl.this.xsetMinArray(n, xmlInteger);
                    return xgetMinArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewMin(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetMinArray = CTClientDataImpl.this.xgetMinArray(n);
                    CTClientDataImpl.this.removeMin(n);
                    return xgetMinArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMinArray();
                }
            }
            return new MinList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MIN$98, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetMinArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.MIN$98, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfMinArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MIN$98);
        }
    }
    
    public void setMinArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.MIN$98);
        }
    }
    
    public void setMinArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MIN$98, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetMinArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MIN$98);
        }
    }
    
    public void xsetMinArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.MIN$98, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertMin(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MIN$98, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addMin(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MIN$98)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewMin(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.MIN$98, n);
        }
    }
    
    public XmlInteger addNewMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.MIN$98);
        }
    }
    
    public void removeMin(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MIN$98, n);
        }
    }
    
    public List<BigInteger> getMaxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MaxList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getMaxArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger maxArray = CTClientDataImpl.this.getMaxArray(n);
                    CTClientDataImpl.this.setMaxArray(n, bigInteger);
                    return maxArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertMax(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger maxArray = CTClientDataImpl.this.getMaxArray(n);
                    CTClientDataImpl.this.removeMax(n);
                    return maxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMaxArray();
                }
            }
            return new MaxList();
        }
    }
    
    @Deprecated
    public BigInteger[] getMaxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MAX$100, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getMaxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MAX$100, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetMaxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MaxList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetMaxArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetMaxArray = CTClientDataImpl.this.xgetMaxArray(n);
                    CTClientDataImpl.this.xsetMaxArray(n, xmlInteger);
                    return xgetMaxArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewMax(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetMaxArray = CTClientDataImpl.this.xgetMaxArray(n);
                    CTClientDataImpl.this.removeMax(n);
                    return xgetMaxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMaxArray();
                }
            }
            return new MaxList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetMaxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MAX$100, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetMaxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.MAX$100, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfMaxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MAX$100);
        }
    }
    
    public void setMaxArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.MAX$100);
        }
    }
    
    public void setMaxArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MAX$100, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetMaxArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MAX$100);
        }
    }
    
    public void xsetMaxArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.MAX$100, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertMax(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MAX$100, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addMax(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MAX$100)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewMax(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.MAX$100, n);
        }
    }
    
    public XmlInteger addNewMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.MAX$100);
        }
    }
    
    public void removeMax(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MAX$100, n);
        }
    }
    
    public List<BigInteger> getIncList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IncList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getIncArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger incArray = CTClientDataImpl.this.getIncArray(n);
                    CTClientDataImpl.this.setIncArray(n, bigInteger);
                    return incArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertInc(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger incArray = CTClientDataImpl.this.getIncArray(n);
                    CTClientDataImpl.this.removeInc(n);
                    return incArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfIncArray();
                }
            }
            return new IncList();
        }
    }
    
    @Deprecated
    public BigInteger[] getIncArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.INC$102, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getIncArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.INC$102, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetIncList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IncList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetIncArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetIncArray = CTClientDataImpl.this.xgetIncArray(n);
                    CTClientDataImpl.this.xsetIncArray(n, xmlInteger);
                    return xgetIncArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewInc(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetIncArray = CTClientDataImpl.this.xgetIncArray(n);
                    CTClientDataImpl.this.removeInc(n);
                    return xgetIncArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfIncArray();
                }
            }
            return new IncList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetIncArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.INC$102, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetIncArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.INC$102, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfIncArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.INC$102);
        }
    }
    
    public void setIncArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.INC$102);
        }
    }
    
    public void setIncArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.INC$102, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetIncArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.INC$102);
        }
    }
    
    public void xsetIncArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.INC$102, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertInc(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.INC$102, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addInc(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.INC$102)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewInc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.INC$102, n);
        }
    }
    
    public XmlInteger addNewInc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.INC$102);
        }
    }
    
    public void removeInc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.INC$102, n);
        }
    }
    
    public List<BigInteger> getPageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PageList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getPageArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger pageArray = CTClientDataImpl.this.getPageArray(n);
                    CTClientDataImpl.this.setPageArray(n, bigInteger);
                    return pageArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertPage(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger pageArray = CTClientDataImpl.this.getPageArray(n);
                    CTClientDataImpl.this.removePage(n);
                    return pageArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfPageArray();
                }
            }
            return new PageList();
        }
    }
    
    @Deprecated
    public BigInteger[] getPageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.PAGE$104, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getPageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.PAGE$104, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetPageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PageList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetPageArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetPageArray = CTClientDataImpl.this.xgetPageArray(n);
                    CTClientDataImpl.this.xsetPageArray(n, xmlInteger);
                    return xgetPageArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewPage(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetPageArray = CTClientDataImpl.this.xgetPageArray(n);
                    CTClientDataImpl.this.removePage(n);
                    return xgetPageArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfPageArray();
                }
            }
            return new PageList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetPageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.PAGE$104, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetPageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.PAGE$104, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfPageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.PAGE$104);
        }
    }
    
    public void setPageArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.PAGE$104);
        }
    }
    
    public void setPageArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.PAGE$104, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetPageArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.PAGE$104);
        }
    }
    
    public void xsetPageArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.PAGE$104, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertPage(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.PAGE$104, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addPage(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.PAGE$104)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewPage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.PAGE$104, n);
        }
    }
    
    public XmlInteger addNewPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.PAGE$104);
        }
    }
    
    public void removePage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.PAGE$104, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getHorizList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HorizList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getHorizArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum horizArray = CTClientDataImpl.this.getHorizArray(n);
                    CTClientDataImpl.this.setHorizArray(n, enum1);
                    return horizArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertHoriz(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum horizArray = CTClientDataImpl.this.getHorizArray(n);
                    CTClientDataImpl.this.removeHoriz(n);
                    return horizArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfHorizArray();
                }
            }
            return new HorizList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getHorizArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.HORIZ$106, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getHorizArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.HORIZ$106, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetHorizList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HorizList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetHorizArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetHorizArray = CTClientDataImpl.this.xgetHorizArray(n);
                    CTClientDataImpl.this.xsetHorizArray(n, stTrueFalseBlank);
                    return xgetHorizArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewHoriz(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetHorizArray = CTClientDataImpl.this.xgetHorizArray(n);
                    CTClientDataImpl.this.removeHoriz(n);
                    return xgetHorizArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfHorizArray();
                }
            }
            return new HorizList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetHorizArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.HORIZ$106, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetHorizArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.HORIZ$106, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfHorizArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.HORIZ$106);
        }
    }
    
    public void setHorizArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.HORIZ$106);
        }
    }
    
    public void setHorizArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.HORIZ$106, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHorizArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.HORIZ$106);
        }
    }
    
    public void xsetHorizArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.HORIZ$106, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertHoriz(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.HORIZ$106, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addHoriz(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.HORIZ$106)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewHoriz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.HORIZ$106, n);
        }
    }
    
    public STTrueFalseBlank addNewHoriz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.HORIZ$106);
        }
    }
    
    public void removeHoriz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.HORIZ$106, n);
        }
    }
    
    public List<BigInteger> getDxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DxList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getDxArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger dxArray = CTClientDataImpl.this.getDxArray(n);
                    CTClientDataImpl.this.setDxArray(n, bigInteger);
                    return dxArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertDx(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger dxArray = CTClientDataImpl.this.getDxArray(n);
                    CTClientDataImpl.this.removeDx(n);
                    return dxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDxArray();
                }
            }
            return new DxList();
        }
    }
    
    @Deprecated
    public BigInteger[] getDxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DX$108, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getDxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DX$108, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlInteger> xgetDxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DxList extends AbstractList<XmlInteger>
            {
                @Override
                public XmlInteger get(final int n) {
                    return CTClientDataImpl.this.xgetDxArray(n);
                }
                
                @Override
                public XmlInteger set(final int n, final XmlInteger xmlInteger) {
                    final XmlInteger xgetDxArray = CTClientDataImpl.this.xgetDxArray(n);
                    CTClientDataImpl.this.xsetDxArray(n, xmlInteger);
                    return xgetDxArray;
                }
                
                @Override
                public void add(final int n, final XmlInteger xmlInteger) {
                    CTClientDataImpl.this.insertNewDx(n).set((XmlObject)xmlInteger);
                }
                
                @Override
                public XmlInteger remove(final int n) {
                    final XmlInteger xgetDxArray = CTClientDataImpl.this.xgetDxArray(n);
                    CTClientDataImpl.this.removeDx(n);
                    return xgetDxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDxArray();
                }
            }
            return new DxList();
        }
    }
    
    @Deprecated
    public XmlInteger[] xgetDxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DX$108, (List)list);
            final XmlInteger[] array = new XmlInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInteger xgetDxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.DX$108, n);
            if (xmlInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInteger;
        }
    }
    
    public int sizeOfDxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DX$108);
        }
    }
    
    public void setDxArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.DX$108);
        }
    }
    
    public void setDxArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DX$108, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDxArray(final XmlInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DX$108);
        }
    }
    
    public void xsetDxArray(final int n, final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_element_user(CTClientDataImpl.DX$108, n);
            if (xmlInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void insertDx(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DX$108, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addDx(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DX$108)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlInteger insertNewDx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().insert_element_user(CTClientDataImpl.DX$108, n);
        }
    }
    
    public XmlInteger addNewDx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().add_element_user(CTClientDataImpl.DX$108);
        }
    }
    
    public void removeDx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DX$108, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getMapOCXList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MapOCXList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getMapOCXArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum mapOCXArray = CTClientDataImpl.this.getMapOCXArray(n);
                    CTClientDataImpl.this.setMapOCXArray(n, enum1);
                    return mapOCXArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertMapOCX(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum mapOCXArray = CTClientDataImpl.this.getMapOCXArray(n);
                    CTClientDataImpl.this.removeMapOCX(n);
                    return mapOCXArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMapOCXArray();
                }
            }
            return new MapOCXList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getMapOCXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MAPOCX$110, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getMapOCXArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MAPOCX$110, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetMapOCXList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MapOCXList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetMapOCXArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetMapOCXArray = CTClientDataImpl.this.xgetMapOCXArray(n);
                    CTClientDataImpl.this.xsetMapOCXArray(n, stTrueFalseBlank);
                    return xgetMapOCXArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewMapOCX(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetMapOCXArray = CTClientDataImpl.this.xgetMapOCXArray(n);
                    CTClientDataImpl.this.removeMapOCX(n);
                    return xgetMapOCXArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfMapOCXArray();
                }
            }
            return new MapOCXList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetMapOCXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.MAPOCX$110, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetMapOCXArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MAPOCX$110, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfMapOCXArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.MAPOCX$110);
        }
    }
    
    public void setMapOCXArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.MAPOCX$110);
        }
    }
    
    public void setMapOCXArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.MAPOCX$110, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetMapOCXArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.MAPOCX$110);
        }
    }
    
    public void xsetMapOCXArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.MAPOCX$110, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertMapOCX(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.MAPOCX$110, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addMapOCX(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.MAPOCX$110)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewMapOCX(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.MAPOCX$110, n);
        }
    }
    
    public STTrueFalseBlank addNewMapOCX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.MAPOCX$110);
        }
    }
    
    public void removeMapOCX(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.MAPOCX$110, n);
        }
    }
    
    public List<STCF.Enum> getCFList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CFList extends AbstractList<STCF.Enum>
            {
                @Override
                public STCF.Enum get(final int n) {
                    return CTClientDataImpl.this.getCFArray(n);
                }
                
                @Override
                public STCF.Enum set(final int n, final STCF.Enum enum1) {
                    final STCF.Enum cfArray = CTClientDataImpl.this.getCFArray(n);
                    CTClientDataImpl.this.setCFArray(n, enum1);
                    return cfArray;
                }
                
                @Override
                public void add(final int n, final STCF.Enum enum1) {
                    CTClientDataImpl.this.insertCF(n, enum1);
                }
                
                @Override
                public STCF.Enum remove(final int n) {
                    final STCF.Enum cfArray = CTClientDataImpl.this.getCFArray(n);
                    CTClientDataImpl.this.removeCF(n);
                    return cfArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCFArray();
                }
            }
            return new CFList();
        }
    }
    
    @Deprecated
    public STCF.Enum[] getCFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CF$112, (List)list);
            final STCF.Enum[] array = new STCF.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STCF.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STCF.Enum getCFArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CF$112, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STCF.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STCF> xgetCFList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CFList extends AbstractList<STCF>
            {
                @Override
                public STCF get(final int n) {
                    return CTClientDataImpl.this.xgetCFArray(n);
                }
                
                @Override
                public STCF set(final int n, final STCF stcf) {
                    final STCF xgetCFArray = CTClientDataImpl.this.xgetCFArray(n);
                    CTClientDataImpl.this.xsetCFArray(n, stcf);
                    return xgetCFArray;
                }
                
                @Override
                public void add(final int n, final STCF stcf) {
                    CTClientDataImpl.this.insertNewCF(n).set((XmlObject)stcf);
                }
                
                @Override
                public STCF remove(final int n) {
                    final STCF xgetCFArray = CTClientDataImpl.this.xgetCFArray(n);
                    CTClientDataImpl.this.removeCF(n);
                    return xgetCFArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCFArray();
                }
            }
            return new CFList();
        }
    }
    
    @Deprecated
    public STCF[] xgetCFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CF$112, (List)list);
            final STCF[] array = new STCF[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STCF xgetCFArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STCF stcf = (STCF)this.get_store().find_element_user(CTClientDataImpl.CF$112, n);
            if (stcf == null) {
                throw new IndexOutOfBoundsException();
            }
            return stcf;
        }
    }
    
    public int sizeOfCFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.CF$112);
        }
    }
    
    public void setCFArray(final STCF.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.CF$112);
        }
    }
    
    public void setCFArray(final int n, final STCF.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CF$112, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCFArray(final STCF[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.CF$112);
        }
    }
    
    public void xsetCFArray(final int n, final STCF stcf) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STCF stcf2 = (STCF)this.get_store().find_element_user(CTClientDataImpl.CF$112, n);
            if (stcf2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stcf2.set((XmlObject)stcf);
        }
    }
    
    public void insertCF(final int n, final STCF.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.CF$112, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addCF(final STCF.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.CF$112)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STCF insertNewCF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCF)this.get_store().insert_element_user(CTClientDataImpl.CF$112, n);
        }
    }
    
    public STCF addNewCF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCF)this.get_store().add_element_user(CTClientDataImpl.CF$112);
        }
    }
    
    public void removeCF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.CF$112, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getCameraList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CameraList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getCameraArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum cameraArray = CTClientDataImpl.this.getCameraArray(n);
                    CTClientDataImpl.this.setCameraArray(n, enum1);
                    return cameraArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertCamera(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum cameraArray = CTClientDataImpl.this.getCameraArray(n);
                    CTClientDataImpl.this.removeCamera(n);
                    return cameraArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCameraArray();
                }
            }
            return new CameraList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getCameraArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CAMERA$114, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getCameraArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CAMERA$114, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetCameraList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CameraList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetCameraArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetCameraArray = CTClientDataImpl.this.xgetCameraArray(n);
                    CTClientDataImpl.this.xsetCameraArray(n, stTrueFalseBlank);
                    return xgetCameraArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewCamera(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetCameraArray = CTClientDataImpl.this.xgetCameraArray(n);
                    CTClientDataImpl.this.removeCamera(n);
                    return xgetCameraArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfCameraArray();
                }
            }
            return new CameraList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetCameraArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.CAMERA$114, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetCameraArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.CAMERA$114, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfCameraArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.CAMERA$114);
        }
    }
    
    public void setCameraArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.CAMERA$114);
        }
    }
    
    public void setCameraArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.CAMERA$114, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCameraArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.CAMERA$114);
        }
    }
    
    public void xsetCameraArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.CAMERA$114, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertCamera(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.CAMERA$114, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addCamera(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.CAMERA$114)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewCamera(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.CAMERA$114, n);
        }
    }
    
    public STTrueFalseBlank addNewCamera() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.CAMERA$114);
        }
    }
    
    public void removeCamera(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.CAMERA$114, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getRecalcAlwaysList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RecalcAlwaysList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getRecalcAlwaysArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum recalcAlwaysArray = CTClientDataImpl.this.getRecalcAlwaysArray(n);
                    CTClientDataImpl.this.setRecalcAlwaysArray(n, enum1);
                    return recalcAlwaysArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertRecalcAlways(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum recalcAlwaysArray = CTClientDataImpl.this.getRecalcAlwaysArray(n);
                    CTClientDataImpl.this.removeRecalcAlways(n);
                    return recalcAlwaysArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRecalcAlwaysArray();
                }
            }
            return new RecalcAlwaysList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getRecalcAlwaysArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.RECALCALWAYS$116, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getRecalcAlwaysArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.RECALCALWAYS$116, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetRecalcAlwaysList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RecalcAlwaysList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetRecalcAlwaysArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetRecalcAlwaysArray = CTClientDataImpl.this.xgetRecalcAlwaysArray(n);
                    CTClientDataImpl.this.xsetRecalcAlwaysArray(n, stTrueFalseBlank);
                    return xgetRecalcAlwaysArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewRecalcAlways(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetRecalcAlwaysArray = CTClientDataImpl.this.xgetRecalcAlwaysArray(n);
                    CTClientDataImpl.this.removeRecalcAlways(n);
                    return xgetRecalcAlwaysArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfRecalcAlwaysArray();
                }
            }
            return new RecalcAlwaysList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetRecalcAlwaysArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.RECALCALWAYS$116, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetRecalcAlwaysArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.RECALCALWAYS$116, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfRecalcAlwaysArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.RECALCALWAYS$116);
        }
    }
    
    public void setRecalcAlwaysArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.RECALCALWAYS$116);
        }
    }
    
    public void setRecalcAlwaysArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.RECALCALWAYS$116, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRecalcAlwaysArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.RECALCALWAYS$116);
        }
    }
    
    public void xsetRecalcAlwaysArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.RECALCALWAYS$116, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertRecalcAlways(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.RECALCALWAYS$116, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addRecalcAlways(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.RECALCALWAYS$116)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewRecalcAlways(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.RECALCALWAYS$116, n);
        }
    }
    
    public STTrueFalseBlank addNewRecalcAlways() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.RECALCALWAYS$116);
        }
    }
    
    public void removeRecalcAlways(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.RECALCALWAYS$116, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getAutoScaleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoScaleList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getAutoScaleArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum autoScaleArray = CTClientDataImpl.this.getAutoScaleArray(n);
                    CTClientDataImpl.this.setAutoScaleArray(n, enum1);
                    return autoScaleArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertAutoScale(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum autoScaleArray = CTClientDataImpl.this.getAutoScaleArray(n);
                    CTClientDataImpl.this.removeAutoScale(n);
                    return autoScaleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoScaleArray();
                }
            }
            return new AutoScaleList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getAutoScaleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOSCALE$118, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getAutoScaleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOSCALE$118, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetAutoScaleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AutoScaleList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetAutoScaleArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetAutoScaleArray = CTClientDataImpl.this.xgetAutoScaleArray(n);
                    CTClientDataImpl.this.xsetAutoScaleArray(n, stTrueFalseBlank);
                    return xgetAutoScaleArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewAutoScale(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetAutoScaleArray = CTClientDataImpl.this.xgetAutoScaleArray(n);
                    CTClientDataImpl.this.removeAutoScale(n);
                    return xgetAutoScaleArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfAutoScaleArray();
                }
            }
            return new AutoScaleList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetAutoScaleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.AUTOSCALE$118, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetAutoScaleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOSCALE$118, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfAutoScaleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.AUTOSCALE$118);
        }
    }
    
    public void setAutoScaleArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.AUTOSCALE$118);
        }
    }
    
    public void setAutoScaleArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.AUTOSCALE$118, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAutoScaleArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.AUTOSCALE$118);
        }
    }
    
    public void xsetAutoScaleArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.AUTOSCALE$118, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertAutoScale(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.AUTOSCALE$118, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addAutoScale(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.AUTOSCALE$118)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewAutoScale(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.AUTOSCALE$118, n);
        }
    }
    
    public STTrueFalseBlank addNewAutoScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.AUTOSCALE$118);
        }
    }
    
    public void removeAutoScale(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.AUTOSCALE$118, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getDDEList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DDEList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getDDEArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum ddeArray = CTClientDataImpl.this.getDDEArray(n);
                    CTClientDataImpl.this.setDDEArray(n, enum1);
                    return ddeArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertDDE(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum ddeArray = CTClientDataImpl.this.getDDEArray(n);
                    CTClientDataImpl.this.removeDDE(n);
                    return ddeArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDDEArray();
                }
            }
            return new DDEList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getDDEArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DDE$120, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getDDEArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DDE$120, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetDDEList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DDEList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetDDEArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetDDEArray = CTClientDataImpl.this.xgetDDEArray(n);
                    CTClientDataImpl.this.xsetDDEArray(n, stTrueFalseBlank);
                    return xgetDDEArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewDDE(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetDDEArray = CTClientDataImpl.this.xgetDDEArray(n);
                    CTClientDataImpl.this.removeDDE(n);
                    return xgetDDEArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfDDEArray();
                }
            }
            return new DDEList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetDDEArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.DDE$120, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetDDEArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DDE$120, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfDDEArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.DDE$120);
        }
    }
    
    public void setDDEArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.DDE$120);
        }
    }
    
    public void setDDEArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.DDE$120, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDDEArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.DDE$120);
        }
    }
    
    public void xsetDDEArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.DDE$120, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertDDE(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.DDE$120, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addDDE(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.DDE$120)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewDDE(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.DDE$120, n);
        }
    }
    
    public STTrueFalseBlank addNewDDE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.DDE$120);
        }
    }
    
    public void removeDDE(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.DDE$120, n);
        }
    }
    
    public List<STTrueFalseBlank.Enum> getUIObjList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class UIObjList extends AbstractList<STTrueFalseBlank.Enum>
            {
                @Override
                public STTrueFalseBlank.Enum get(final int n) {
                    return CTClientDataImpl.this.getUIObjArray(n);
                }
                
                @Override
                public STTrueFalseBlank.Enum set(final int n, final STTrueFalseBlank.Enum enum1) {
                    final STTrueFalseBlank.Enum uiObjArray = CTClientDataImpl.this.getUIObjArray(n);
                    CTClientDataImpl.this.setUIObjArray(n, enum1);
                    return uiObjArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank.Enum enum1) {
                    CTClientDataImpl.this.insertUIObj(n, enum1);
                }
                
                @Override
                public STTrueFalseBlank.Enum remove(final int n) {
                    final STTrueFalseBlank.Enum uiObjArray = CTClientDataImpl.this.getUIObjArray(n);
                    CTClientDataImpl.this.removeUIObj(n);
                    return uiObjArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfUIObjArray();
                }
            }
            return new UIObjList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank.Enum[] getUIObjArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.UIOBJ$122, (List)list);
            final STTrueFalseBlank.Enum[] array = new STTrueFalseBlank.Enum[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = (STTrueFalseBlank.Enum)((SimpleValue)list.get(i)).getEnumValue();
            }
            return array;
        }
    }
    
    public STTrueFalseBlank.Enum getUIObjArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.UIOBJ$122, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public List<STTrueFalseBlank> xgetUIObjList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class UIObjList extends AbstractList<STTrueFalseBlank>
            {
                @Override
                public STTrueFalseBlank get(final int n) {
                    return CTClientDataImpl.this.xgetUIObjArray(n);
                }
                
                @Override
                public STTrueFalseBlank set(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    final STTrueFalseBlank xgetUIObjArray = CTClientDataImpl.this.xgetUIObjArray(n);
                    CTClientDataImpl.this.xsetUIObjArray(n, stTrueFalseBlank);
                    return xgetUIObjArray;
                }
                
                @Override
                public void add(final int n, final STTrueFalseBlank stTrueFalseBlank) {
                    CTClientDataImpl.this.insertNewUIObj(n).set((XmlObject)stTrueFalseBlank);
                }
                
                @Override
                public STTrueFalseBlank remove(final int n) {
                    final STTrueFalseBlank xgetUIObjArray = CTClientDataImpl.this.xgetUIObjArray(n);
                    CTClientDataImpl.this.removeUIObj(n);
                    return xgetUIObjArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfUIObjArray();
                }
            }
            return new UIObjList();
        }
    }
    
    @Deprecated
    public STTrueFalseBlank[] xgetUIObjArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.UIOBJ$122, (List)list);
            final STTrueFalseBlank[] array = new STTrueFalseBlank[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STTrueFalseBlank xgetUIObjArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.UIOBJ$122, n);
            if (stTrueFalseBlank == null) {
                throw new IndexOutOfBoundsException();
            }
            return stTrueFalseBlank;
        }
    }
    
    public int sizeOfUIObjArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.UIOBJ$122);
        }
    }
    
    public void setUIObjArray(final STTrueFalseBlank.Enum[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((StringEnumAbstractBase[])array, CTClientDataImpl.UIOBJ$122);
        }
    }
    
    public void setUIObjArray(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.UIOBJ$122, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUIObjArray(final STTrueFalseBlank[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.UIOBJ$122);
        }
    }
    
    public void xsetUIObjArray(final int n, final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_element_user(CTClientDataImpl.UIOBJ$122, n);
            if (stTrueFalseBlank2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void insertUIObj(final int n, final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.UIOBJ$122, n)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void addUIObj(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.UIOBJ$122)).setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public STTrueFalseBlank insertNewUIObj(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().insert_element_user(CTClientDataImpl.UIOBJ$122, n);
        }
    }
    
    public STTrueFalseBlank addNewUIObj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().add_element_user(CTClientDataImpl.UIOBJ$122);
        }
    }
    
    public void removeUIObj(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.UIOBJ$122, n);
        }
    }
    
    public List<String> getScriptTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptTextList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getScriptTextArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String scriptTextArray = CTClientDataImpl.this.getScriptTextArray(n);
                    CTClientDataImpl.this.setScriptTextArray(n, s);
                    return scriptTextArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertScriptText(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String scriptTextArray = CTClientDataImpl.this.getScriptTextArray(n);
                    CTClientDataImpl.this.removeScriptText(n);
                    return scriptTextArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptTextArray();
                }
            }
            return new ScriptTextList();
        }
    }
    
    @Deprecated
    public String[] getScriptTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTTEXT$124, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getScriptTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTTEXT$124, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetScriptTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptTextList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetScriptTextArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetScriptTextArray = CTClientDataImpl.this.xgetScriptTextArray(n);
                    CTClientDataImpl.this.xsetScriptTextArray(n, xmlString);
                    return xgetScriptTextArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewScriptText(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetScriptTextArray = CTClientDataImpl.this.xgetScriptTextArray(n);
                    CTClientDataImpl.this.removeScriptText(n);
                    return xgetScriptTextArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptTextArray();
                }
            }
            return new ScriptTextList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetScriptTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTTEXT$124, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetScriptTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SCRIPTTEXT$124, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfScriptTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SCRIPTTEXT$124);
        }
    }
    
    public void setScriptTextArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SCRIPTTEXT$124);
        }
    }
    
    public void setScriptTextArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTTEXT$124, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetScriptTextArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SCRIPTTEXT$124);
        }
    }
    
    public void xsetScriptTextArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SCRIPTTEXT$124, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertScriptText(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTTEXT$124, n)).setStringValue(stringValue);
        }
    }
    
    public void addScriptText(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SCRIPTTEXT$124)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewScriptText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTTEXT$124, n);
        }
    }
    
    public XmlString addNewScriptText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.SCRIPTTEXT$124);
        }
    }
    
    public void removeScriptText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SCRIPTTEXT$124, n);
        }
    }
    
    public List<String> getScriptExtendedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptExtendedList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getScriptExtendedArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String scriptExtendedArray = CTClientDataImpl.this.getScriptExtendedArray(n);
                    CTClientDataImpl.this.setScriptExtendedArray(n, s);
                    return scriptExtendedArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertScriptExtended(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String scriptExtendedArray = CTClientDataImpl.this.getScriptExtendedArray(n);
                    CTClientDataImpl.this.removeScriptExtended(n);
                    return scriptExtendedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptExtendedArray();
                }
            }
            return new ScriptExtendedList();
        }
    }
    
    @Deprecated
    public String[] getScriptExtendedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTEXTENDED$126, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getScriptExtendedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetScriptExtendedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptExtendedList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetScriptExtendedArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetScriptExtendedArray = CTClientDataImpl.this.xgetScriptExtendedArray(n);
                    CTClientDataImpl.this.xsetScriptExtendedArray(n, xmlString);
                    return xgetScriptExtendedArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewScriptExtended(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetScriptExtendedArray = CTClientDataImpl.this.xgetScriptExtendedArray(n);
                    CTClientDataImpl.this.removeScriptExtended(n);
                    return xgetScriptExtendedArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptExtendedArray();
                }
            }
            return new ScriptExtendedList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetScriptExtendedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTEXTENDED$126, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetScriptExtendedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfScriptExtendedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SCRIPTEXTENDED$126);
        }
    }
    
    public void setScriptExtendedArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SCRIPTEXTENDED$126);
        }
    }
    
    public void setScriptExtendedArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetScriptExtendedArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SCRIPTEXTENDED$126);
        }
    }
    
    public void xsetScriptExtendedArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertScriptExtended(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n)).setStringValue(stringValue);
        }
    }
    
    public void addScriptExtended(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SCRIPTEXTENDED$126)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewScriptExtended(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTEXTENDED$126, n);
        }
    }
    
    public XmlString addNewScriptExtended() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.SCRIPTEXTENDED$126);
        }
    }
    
    public void removeScriptExtended(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SCRIPTEXTENDED$126, n);
        }
    }
    
    public List<BigInteger> getScriptLanguageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptLanguageList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getScriptLanguageArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger scriptLanguageArray = CTClientDataImpl.this.getScriptLanguageArray(n);
                    CTClientDataImpl.this.setScriptLanguageArray(n, bigInteger);
                    return scriptLanguageArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertScriptLanguage(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger scriptLanguageArray = CTClientDataImpl.this.getScriptLanguageArray(n);
                    CTClientDataImpl.this.removeScriptLanguage(n);
                    return scriptLanguageArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptLanguageArray();
                }
            }
            return new ScriptLanguageList();
        }
    }
    
    @Deprecated
    public BigInteger[] getScriptLanguageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTLANGUAGE$128, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getScriptLanguageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlNonNegativeInteger> xgetScriptLanguageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptLanguageList extends AbstractList<XmlNonNegativeInteger>
            {
                @Override
                public XmlNonNegativeInteger get(final int n) {
                    return CTClientDataImpl.this.xgetScriptLanguageArray(n);
                }
                
                @Override
                public XmlNonNegativeInteger set(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
                    final XmlNonNegativeInteger xgetScriptLanguageArray = CTClientDataImpl.this.xgetScriptLanguageArray(n);
                    CTClientDataImpl.this.xsetScriptLanguageArray(n, xmlNonNegativeInteger);
                    return xgetScriptLanguageArray;
                }
                
                @Override
                public void add(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
                    CTClientDataImpl.this.insertNewScriptLanguage(n).set((XmlObject)xmlNonNegativeInteger);
                }
                
                @Override
                public XmlNonNegativeInteger remove(final int n) {
                    final XmlNonNegativeInteger xgetScriptLanguageArray = CTClientDataImpl.this.xgetScriptLanguageArray(n);
                    CTClientDataImpl.this.removeScriptLanguage(n);
                    return xgetScriptLanguageArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptLanguageArray();
                }
            }
            return new ScriptLanguageList();
        }
    }
    
    @Deprecated
    public XmlNonNegativeInteger[] xgetScriptLanguageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTLANGUAGE$128, (List)list);
            final XmlNonNegativeInteger[] array = new XmlNonNegativeInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlNonNegativeInteger xgetScriptLanguageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlNonNegativeInteger xmlNonNegativeInteger = (XmlNonNegativeInteger)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
            if (xmlNonNegativeInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlNonNegativeInteger;
        }
    }
    
    public int sizeOfScriptLanguageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SCRIPTLANGUAGE$128);
        }
    }
    
    public void setScriptLanguageArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SCRIPTLANGUAGE$128);
        }
    }
    
    public void setScriptLanguageArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetScriptLanguageArray(final XmlNonNegativeInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SCRIPTLANGUAGE$128);
        }
    }
    
    public void xsetScriptLanguageArray(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlNonNegativeInteger xmlNonNegativeInteger2 = (XmlNonNegativeInteger)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
            if (xmlNonNegativeInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlNonNegativeInteger2.set((XmlObject)xmlNonNegativeInteger);
        }
    }
    
    public void insertScriptLanguage(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addScriptLanguage(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlNonNegativeInteger insertNewScriptLanguage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlNonNegativeInteger)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
        }
    }
    
    public XmlNonNegativeInteger addNewScriptLanguage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlNonNegativeInteger)this.get_store().add_element_user(CTClientDataImpl.SCRIPTLANGUAGE$128);
        }
    }
    
    public void removeScriptLanguage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SCRIPTLANGUAGE$128, n);
        }
    }
    
    public List<BigInteger> getScriptLocationList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptLocationList extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTClientDataImpl.this.getScriptLocationArray(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger scriptLocationArray = CTClientDataImpl.this.getScriptLocationArray(n);
                    CTClientDataImpl.this.setScriptLocationArray(n, bigInteger);
                    return scriptLocationArray;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTClientDataImpl.this.insertScriptLocation(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger scriptLocationArray = CTClientDataImpl.this.getScriptLocationArray(n);
                    CTClientDataImpl.this.removeScriptLocation(n);
                    return scriptLocationArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptLocationArray();
                }
            }
            return new ScriptLocationList();
        }
    }
    
    @Deprecated
    public BigInteger[] getScriptLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTLOCATION$130, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getScriptLocationArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlNonNegativeInteger> xgetScriptLocationList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScriptLocationList extends AbstractList<XmlNonNegativeInteger>
            {
                @Override
                public XmlNonNegativeInteger get(final int n) {
                    return CTClientDataImpl.this.xgetScriptLocationArray(n);
                }
                
                @Override
                public XmlNonNegativeInteger set(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
                    final XmlNonNegativeInteger xgetScriptLocationArray = CTClientDataImpl.this.xgetScriptLocationArray(n);
                    CTClientDataImpl.this.xsetScriptLocationArray(n, xmlNonNegativeInteger);
                    return xgetScriptLocationArray;
                }
                
                @Override
                public void add(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
                    CTClientDataImpl.this.insertNewScriptLocation(n).set((XmlObject)xmlNonNegativeInteger);
                }
                
                @Override
                public XmlNonNegativeInteger remove(final int n) {
                    final XmlNonNegativeInteger xgetScriptLocationArray = CTClientDataImpl.this.xgetScriptLocationArray(n);
                    CTClientDataImpl.this.removeScriptLocation(n);
                    return xgetScriptLocationArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfScriptLocationArray();
                }
            }
            return new ScriptLocationList();
        }
    }
    
    @Deprecated
    public XmlNonNegativeInteger[] xgetScriptLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.SCRIPTLOCATION$130, (List)list);
            final XmlNonNegativeInteger[] array = new XmlNonNegativeInteger[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlNonNegativeInteger xgetScriptLocationArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlNonNegativeInteger xmlNonNegativeInteger = (XmlNonNegativeInteger)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n);
            if (xmlNonNegativeInteger == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlNonNegativeInteger;
        }
    }
    
    public int sizeOfScriptLocationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.SCRIPTLOCATION$130);
        }
    }
    
    public void setScriptLocationArray(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.SCRIPTLOCATION$130);
        }
    }
    
    public void setScriptLocationArray(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetScriptLocationArray(final XmlNonNegativeInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.SCRIPTLOCATION$130);
        }
    }
    
    public void xsetScriptLocationArray(final int n, final XmlNonNegativeInteger xmlNonNegativeInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlNonNegativeInteger xmlNonNegativeInteger2 = (XmlNonNegativeInteger)this.get_store().find_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n);
            if (xmlNonNegativeInteger2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlNonNegativeInteger2.set((XmlObject)xmlNonNegativeInteger);
        }
    }
    
    public void insertScriptLocation(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addScriptLocation(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.SCRIPTLOCATION$130)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlNonNegativeInteger insertNewScriptLocation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlNonNegativeInteger)this.get_store().insert_element_user(CTClientDataImpl.SCRIPTLOCATION$130, n);
        }
    }
    
    public XmlNonNegativeInteger addNewScriptLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlNonNegativeInteger)this.get_store().add_element_user(CTClientDataImpl.SCRIPTLOCATION$130);
        }
    }
    
    public void removeScriptLocation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.SCRIPTLOCATION$130, n);
        }
    }
    
    public List<String> getFmlaTxbxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaTxbxList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTClientDataImpl.this.getFmlaTxbxArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String fmlaTxbxArray = CTClientDataImpl.this.getFmlaTxbxArray(n);
                    CTClientDataImpl.this.setFmlaTxbxArray(n, s);
                    return fmlaTxbxArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTClientDataImpl.this.insertFmlaTxbx(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String fmlaTxbxArray = CTClientDataImpl.this.getFmlaTxbxArray(n);
                    CTClientDataImpl.this.removeFmlaTxbx(n);
                    return fmlaTxbxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaTxbxArray();
                }
            }
            return new FmlaTxbxList();
        }
    }
    
    @Deprecated
    public String[] getFmlaTxbxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLATXBX$132, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFmlaTxbxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLATXBX$132, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetFmlaTxbxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FmlaTxbxList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTClientDataImpl.this.xgetFmlaTxbxArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetFmlaTxbxArray = CTClientDataImpl.this.xgetFmlaTxbxArray(n);
                    CTClientDataImpl.this.xsetFmlaTxbxArray(n, xmlString);
                    return xgetFmlaTxbxArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTClientDataImpl.this.insertNewFmlaTxbx(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetFmlaTxbxArray = CTClientDataImpl.this.xgetFmlaTxbxArray(n);
                    CTClientDataImpl.this.removeFmlaTxbx(n);
                    return xgetFmlaTxbxArray;
                }
                
                @Override
                public int size() {
                    return CTClientDataImpl.this.sizeOfFmlaTxbxArray();
                }
            }
            return new FmlaTxbxList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetFmlaTxbxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTClientDataImpl.FMLATXBX$132, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetFmlaTxbxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLATXBX$132, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfFmlaTxbxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTClientDataImpl.FMLATXBX$132);
        }
    }
    
    public void setFmlaTxbxArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTClientDataImpl.FMLATXBX$132);
        }
    }
    
    public void setFmlaTxbxArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTClientDataImpl.FMLATXBX$132, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmlaTxbxArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTClientDataImpl.FMLATXBX$132);
        }
    }
    
    public void xsetFmlaTxbxArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTClientDataImpl.FMLATXBX$132, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertFmlaTxbx(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTClientDataImpl.FMLATXBX$132, n)).setStringValue(stringValue);
        }
    }
    
    public void addFmlaTxbx(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTClientDataImpl.FMLATXBX$132)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewFmlaTxbx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTClientDataImpl.FMLATXBX$132, n);
        }
    }
    
    public XmlString addNewFmlaTxbx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTClientDataImpl.FMLATXBX$132);
        }
    }
    
    public void removeFmlaTxbx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTClientDataImpl.FMLATXBX$132, n);
        }
    }
    
    public STObjectType.Enum getObjectType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
            if (simpleValue == null) {
                return null;
            }
            return (STObjectType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STObjectType xgetObjectType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STObjectType)this.get_store().find_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
        }
    }
    
    public void setObjectType(final STObjectType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetObjectType(final STObjectType stObjectType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STObjectType stObjectType2 = (STObjectType)this.get_store().find_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
            if (stObjectType2 == null) {
                stObjectType2 = (STObjectType)this.get_store().add_attribute_user(CTClientDataImpl.OBJECTTYPE$134);
            }
            stObjectType2.set((XmlObject)stObjectType);
        }
    }
    
    static {
        MOVEWITHCELLS$0 = new QName("urn:schemas-microsoft-com:office:excel", "MoveWithCells");
        SIZEWITHCELLS$2 = new QName("urn:schemas-microsoft-com:office:excel", "SizeWithCells");
        ANCHOR$4 = new QName("urn:schemas-microsoft-com:office:excel", "Anchor");
        LOCKED$6 = new QName("urn:schemas-microsoft-com:office:excel", "Locked");
        DEFAULTSIZE$8 = new QName("urn:schemas-microsoft-com:office:excel", "DefaultSize");
        PRINTOBJECT$10 = new QName("urn:schemas-microsoft-com:office:excel", "PrintObject");
        DISABLED$12 = new QName("urn:schemas-microsoft-com:office:excel", "Disabled");
        AUTOFILL$14 = new QName("urn:schemas-microsoft-com:office:excel", "AutoFill");
        AUTOLINE$16 = new QName("urn:schemas-microsoft-com:office:excel", "AutoLine");
        AUTOPICT$18 = new QName("urn:schemas-microsoft-com:office:excel", "AutoPict");
        FMLAMACRO$20 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaMacro");
        TEXTHALIGN$22 = new QName("urn:schemas-microsoft-com:office:excel", "TextHAlign");
        TEXTVALIGN$24 = new QName("urn:schemas-microsoft-com:office:excel", "TextVAlign");
        LOCKTEXT$26 = new QName("urn:schemas-microsoft-com:office:excel", "LockText");
        JUSTLASTX$28 = new QName("urn:schemas-microsoft-com:office:excel", "JustLastX");
        SECRETEDIT$30 = new QName("urn:schemas-microsoft-com:office:excel", "SecretEdit");
        DEFAULT$32 = new QName("urn:schemas-microsoft-com:office:excel", "Default");
        HELP$34 = new QName("urn:schemas-microsoft-com:office:excel", "Help");
        CANCEL$36 = new QName("urn:schemas-microsoft-com:office:excel", "Cancel");
        DISMISS$38 = new QName("urn:schemas-microsoft-com:office:excel", "Dismiss");
        ACCEL$40 = new QName("urn:schemas-microsoft-com:office:excel", "Accel");
        ACCEL2$42 = new QName("urn:schemas-microsoft-com:office:excel", "Accel2");
        ROW$44 = new QName("urn:schemas-microsoft-com:office:excel", "Row");
        COLUMN$46 = new QName("urn:schemas-microsoft-com:office:excel", "Column");
        VISIBLE$48 = new QName("urn:schemas-microsoft-com:office:excel", "Visible");
        ROWHIDDEN$50 = new QName("urn:schemas-microsoft-com:office:excel", "RowHidden");
        COLHIDDEN$52 = new QName("urn:schemas-microsoft-com:office:excel", "ColHidden");
        VTEDIT$54 = new QName("urn:schemas-microsoft-com:office:excel", "VTEdit");
        MULTILINE$56 = new QName("urn:schemas-microsoft-com:office:excel", "MultiLine");
        VSCROLL$58 = new QName("urn:schemas-microsoft-com:office:excel", "VScroll");
        VALIDIDS$60 = new QName("urn:schemas-microsoft-com:office:excel", "ValidIds");
        FMLARANGE$62 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaRange");
        WIDTHMIN$64 = new QName("urn:schemas-microsoft-com:office:excel", "WidthMin");
        SEL$66 = new QName("urn:schemas-microsoft-com:office:excel", "Sel");
        NOTHREED2$68 = new QName("urn:schemas-microsoft-com:office:excel", "NoThreeD2");
        SELTYPE$70 = new QName("urn:schemas-microsoft-com:office:excel", "SelType");
        MULTISEL$72 = new QName("urn:schemas-microsoft-com:office:excel", "MultiSel");
        LCT$74 = new QName("urn:schemas-microsoft-com:office:excel", "LCT");
        LISTITEM$76 = new QName("urn:schemas-microsoft-com:office:excel", "ListItem");
        DROPSTYLE$78 = new QName("urn:schemas-microsoft-com:office:excel", "DropStyle");
        COLORED$80 = new QName("urn:schemas-microsoft-com:office:excel", "Colored");
        DROPLINES$82 = new QName("urn:schemas-microsoft-com:office:excel", "DropLines");
        CHECKED$84 = new QName("urn:schemas-microsoft-com:office:excel", "Checked");
        FMLALINK$86 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaLink");
        FMLAPICT$88 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaPict");
        NOTHREED$90 = new QName("urn:schemas-microsoft-com:office:excel", "NoThreeD");
        FIRSTBUTTON$92 = new QName("urn:schemas-microsoft-com:office:excel", "FirstButton");
        FMLAGROUP$94 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaGroup");
        VAL$96 = new QName("urn:schemas-microsoft-com:office:excel", "Val");
        MIN$98 = new QName("urn:schemas-microsoft-com:office:excel", "Min");
        MAX$100 = new QName("urn:schemas-microsoft-com:office:excel", "Max");
        INC$102 = new QName("urn:schemas-microsoft-com:office:excel", "Inc");
        PAGE$104 = new QName("urn:schemas-microsoft-com:office:excel", "Page");
        HORIZ$106 = new QName("urn:schemas-microsoft-com:office:excel", "Horiz");
        DX$108 = new QName("urn:schemas-microsoft-com:office:excel", "Dx");
        MAPOCX$110 = new QName("urn:schemas-microsoft-com:office:excel", "MapOCX");
        CF$112 = new QName("urn:schemas-microsoft-com:office:excel", "CF");
        CAMERA$114 = new QName("urn:schemas-microsoft-com:office:excel", "Camera");
        RECALCALWAYS$116 = new QName("urn:schemas-microsoft-com:office:excel", "RecalcAlways");
        AUTOSCALE$118 = new QName("urn:schemas-microsoft-com:office:excel", "AutoScale");
        DDE$120 = new QName("urn:schemas-microsoft-com:office:excel", "DDE");
        UIOBJ$122 = new QName("urn:schemas-microsoft-com:office:excel", "UIObj");
        SCRIPTTEXT$124 = new QName("urn:schemas-microsoft-com:office:excel", "ScriptText");
        SCRIPTEXTENDED$126 = new QName("urn:schemas-microsoft-com:office:excel", "ScriptExtended");
        SCRIPTLANGUAGE$128 = new QName("urn:schemas-microsoft-com:office:excel", "ScriptLanguage");
        SCRIPTLOCATION$130 = new QName("urn:schemas-microsoft-com:office:excel", "ScriptLocation");
        FMLATXBX$132 = new QName("urn:schemas-microsoft-com:office:excel", "FmlaTxbx");
        OBJECTTYPE$134 = new QName("", "ObjectType");
    }
}
