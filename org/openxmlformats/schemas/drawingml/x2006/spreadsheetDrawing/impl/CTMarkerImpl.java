package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STRowID;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STColID;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMarkerImpl extends XmlComplexContentImpl implements CTMarker
{
    private static final long serialVersionUID = 1L;
    private static final QName COL$0;
    private static final QName COLOFF$2;
    private static final QName ROW$4;
    private static final QName ROWOFF$6;
    
    public CTMarkerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.COL$0, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STColID xgetCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColID)this.get_store().find_element_user(CTMarkerImpl.COL$0, 0);
        }
    }
    
    public void setCol(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.COL$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTMarkerImpl.COL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetCol(final STColID stColID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColID stColID2 = (STColID)this.get_store().find_element_user(CTMarkerImpl.COL$0, 0);
            if (stColID2 == null) {
                stColID2 = (STColID)this.get_store().add_element_user(CTMarkerImpl.COL$0);
            }
            stColID2.set((XmlObject)stColID);
        }
    }
    
    public long getColOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.COLOFF$2, 0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetColOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_element_user(CTMarkerImpl.COLOFF$2, 0);
        }
    }
    
    public void setColOff(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.COLOFF$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTMarkerImpl.COLOFF$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetColOff(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_element_user(CTMarkerImpl.COLOFF$2, 0);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_element_user(CTMarkerImpl.COLOFF$2);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    public int getRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.ROW$4, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STRowID xgetRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRowID)this.get_store().find_element_user(CTMarkerImpl.ROW$4, 0);
        }
    }
    
    public void setRow(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.ROW$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTMarkerImpl.ROW$4);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRow(final STRowID stRowID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRowID stRowID2 = (STRowID)this.get_store().find_element_user(CTMarkerImpl.ROW$4, 0);
            if (stRowID2 == null) {
                stRowID2 = (STRowID)this.get_store().add_element_user(CTMarkerImpl.ROW$4);
            }
            stRowID2.set((XmlObject)stRowID);
        }
    }
    
    public long getRowOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.ROWOFF$6, 0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetRowOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_element_user(CTMarkerImpl.ROWOFF$6, 0);
        }
    }
    
    public void setRowOff(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTMarkerImpl.ROWOFF$6, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTMarkerImpl.ROWOFF$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRowOff(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_element_user(CTMarkerImpl.ROWOFF$6, 0);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_element_user(CTMarkerImpl.ROWOFF$6);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    static {
        COL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "col");
        COLOFF$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "colOff");
        ROW$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "row");
        ROWOFF$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "rowOff");
    }
}
