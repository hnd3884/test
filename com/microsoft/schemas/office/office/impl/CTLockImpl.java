package com.microsoft.schemas.office.office.impl;

import com.microsoft.schemas.office.office.STTrueFalse;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.office.CTLock;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLockImpl extends XmlComplexContentImpl implements CTLock
{
    private static final long serialVersionUID = 1L;
    private static final QName EXT$0;
    private static final QName POSITION$2;
    private static final QName SELECTION$4;
    private static final QName GROUPING$6;
    private static final QName UNGROUPING$8;
    private static final QName ROTATION$10;
    private static final QName CROPPING$12;
    private static final QName VERTICIES$14;
    private static final QName ADJUSTHANDLES$16;
    private static final QName TEXT$18;
    private static final QName ASPECTRATIO$20;
    private static final QName SHAPETYPE$22;
    
    public CTLockImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STExt.Enum getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.EXT$0);
            if (simpleValue == null) {
                return null;
            }
            return (STExt.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STExt xgetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STExt)this.get_store().find_attribute_user(CTLockImpl.EXT$0);
        }
    }
    
    public boolean isSetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.EXT$0) != null;
        }
    }
    
    public void setExt(final STExt.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.EXT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.EXT$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetExt(final STExt stExt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STExt stExt2 = (STExt)this.get_store().find_attribute_user(CTLockImpl.EXT$0);
            if (stExt2 == null) {
                stExt2 = (STExt)this.get_store().add_attribute_user(CTLockImpl.EXT$0);
            }
            stExt2.set((XmlObject)stExt);
        }
    }
    
    public void unsetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.EXT$0);
        }
    }
    
    public STTrueFalse.Enum getPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.POSITION$2);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.POSITION$2);
        }
    }
    
    public boolean isSetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.POSITION$2) != null;
        }
    }
    
    public void setPosition(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.POSITION$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.POSITION$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPosition(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.POSITION$2);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.POSITION$2);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.POSITION$2);
        }
    }
    
    public STTrueFalse.Enum getSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.SELECTION$4);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.SELECTION$4);
        }
    }
    
    public boolean isSetSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.SELECTION$4) != null;
        }
    }
    
    public void setSelection(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.SELECTION$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.SELECTION$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSelection(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.SELECTION$4);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.SELECTION$4);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.SELECTION$4);
        }
    }
    
    public STTrueFalse.Enum getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.GROUPING$6);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.GROUPING$6);
        }
    }
    
    public boolean isSetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.GROUPING$6) != null;
        }
    }
    
    public void setGrouping(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.GROUPING$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.GROUPING$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetGrouping(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.GROUPING$6);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.GROUPING$6);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.GROUPING$6);
        }
    }
    
    public STTrueFalse.Enum getUngrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.UNGROUPING$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetUngrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.UNGROUPING$8);
        }
    }
    
    public boolean isSetUngrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.UNGROUPING$8) != null;
        }
    }
    
    public void setUngrouping(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.UNGROUPING$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.UNGROUPING$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUngrouping(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.UNGROUPING$8);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.UNGROUPING$8);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUngrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.UNGROUPING$8);
        }
    }
    
    public STTrueFalse.Enum getRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ROTATION$10);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ROTATION$10);
        }
    }
    
    public boolean isSetRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.ROTATION$10) != null;
        }
    }
    
    public void setRotation(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ROTATION$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.ROTATION$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRotation(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ROTATION$10);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.ROTATION$10);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.ROTATION$10);
        }
    }
    
    public STTrueFalse.Enum getCropping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.CROPPING$12);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetCropping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.CROPPING$12);
        }
    }
    
    public boolean isSetCropping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.CROPPING$12) != null;
        }
    }
    
    public void setCropping(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.CROPPING$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.CROPPING$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCropping(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.CROPPING$12);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.CROPPING$12);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetCropping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.CROPPING$12);
        }
    }
    
    public STTrueFalse.Enum getVerticies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.VERTICIES$14);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetVerticies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.VERTICIES$14);
        }
    }
    
    public boolean isSetVerticies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.VERTICIES$14) != null;
        }
    }
    
    public void setVerticies(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.VERTICIES$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.VERTICIES$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVerticies(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.VERTICIES$14);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.VERTICIES$14);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetVerticies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.VERTICIES$14);
        }
    }
    
    public STTrueFalse.Enum getAdjusthandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetAdjusthandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
        }
    }
    
    public boolean isSetAdjusthandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.ADJUSTHANDLES$16) != null;
        }
    }
    
    public void setAdjusthandles(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAdjusthandles(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.ADJUSTHANDLES$16);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAdjusthandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.ADJUSTHANDLES$16);
        }
    }
    
    public STTrueFalse.Enum getText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.TEXT$18);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.TEXT$18);
        }
    }
    
    public boolean isSetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.TEXT$18) != null;
        }
    }
    
    public void setText(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.TEXT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.TEXT$18);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetText(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.TEXT$18);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.TEXT$18);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.TEXT$18);
        }
    }
    
    public STTrueFalse.Enum getAspectratio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ASPECTRATIO$20);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetAspectratio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ASPECTRATIO$20);
        }
    }
    
    public boolean isSetAspectratio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.ASPECTRATIO$20) != null;
        }
    }
    
    public void setAspectratio(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.ASPECTRATIO$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.ASPECTRATIO$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAspectratio(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.ASPECTRATIO$20);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.ASPECTRATIO$20);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAspectratio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.ASPECTRATIO$20);
        }
    }
    
    public STTrueFalse.Enum getShapetype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.SHAPETYPE$22);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetShapetype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.SHAPETYPE$22);
        }
    }
    
    public boolean isSetShapetype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLockImpl.SHAPETYPE$22) != null;
        }
    }
    
    public void setShapetype(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLockImpl.SHAPETYPE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLockImpl.SHAPETYPE$22);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetShapetype(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTLockImpl.SHAPETYPE$22);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTLockImpl.SHAPETYPE$22);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetShapetype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLockImpl.SHAPETYPE$22);
        }
    }
    
    static {
        EXT$0 = new QName("urn:schemas-microsoft-com:vml", "ext");
        POSITION$2 = new QName("", "position");
        SELECTION$4 = new QName("", "selection");
        GROUPING$6 = new QName("", "grouping");
        UNGROUPING$8 = new QName("", "ungrouping");
        ROTATION$10 = new QName("", "rotation");
        CROPPING$12 = new QName("", "cropping");
        VERTICIES$14 = new QName("", "verticies");
        ADJUSTHANDLES$16 = new QName("", "adjusthandles");
        TEXT$18 = new QName("", "text");
        ASPECTRATIO$20 = new QName("", "aspectratio");
        SHAPETYPE$22 = new QName("", "shapetype");
    }
}
