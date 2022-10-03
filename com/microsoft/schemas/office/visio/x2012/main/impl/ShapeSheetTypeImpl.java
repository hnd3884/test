package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import com.microsoft.schemas.office.visio.x2012.main.ForeignDataType;
import com.microsoft.schemas.office.visio.x2012.main.DataType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.TextType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;

public class ShapeSheetTypeImpl extends SheetTypeImpl implements ShapeSheetType
{
    private static final long serialVersionUID = 1L;
    private static final QName TEXT$0;
    private static final QName DATA1$2;
    private static final QName DATA2$4;
    private static final QName DATA3$6;
    private static final QName FOREIGNDATA$8;
    private static final QName SHAPES$10;
    private static final QName ID$12;
    private static final QName ORIGINALID$14;
    private static final QName DEL$16;
    private static final QName MASTERSHAPE$18;
    private static final QName UNIQUEID$20;
    private static final QName NAME$22;
    private static final QName NAMEU$24;
    private static final QName ISCUSTOMNAME$26;
    private static final QName ISCUSTOMNAMEU$28;
    private static final QName MASTER$30;
    private static final QName TYPE$32;
    
    public ShapeSheetTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public TextType getText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TextType textType = (TextType)this.get_store().find_element_user(ShapeSheetTypeImpl.TEXT$0, 0);
            if (textType == null) {
                return null;
            }
            return textType;
        }
    }
    
    @Override
    public boolean isSetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.TEXT$0) != 0;
        }
    }
    
    @Override
    public void setText(final TextType textType) {
        this.generatedSetterHelperImpl((XmlObject)textType, ShapeSheetTypeImpl.TEXT$0, 0, (short)1);
    }
    
    @Override
    public TextType addNewText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TextType)this.get_store().add_element_user(ShapeSheetTypeImpl.TEXT$0);
        }
    }
    
    @Override
    public void unsetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.TEXT$0, 0);
        }
    }
    
    @Override
    public DataType getData1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DataType dataType = (DataType)this.get_store().find_element_user(ShapeSheetTypeImpl.DATA1$2, 0);
            if (dataType == null) {
                return null;
            }
            return dataType;
        }
    }
    
    @Override
    public boolean isSetData1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.DATA1$2) != 0;
        }
    }
    
    @Override
    public void setData1(final DataType dataType) {
        this.generatedSetterHelperImpl((XmlObject)dataType, ShapeSheetTypeImpl.DATA1$2, 0, (short)1);
    }
    
    @Override
    public DataType addNewData1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DataType)this.get_store().add_element_user(ShapeSheetTypeImpl.DATA1$2);
        }
    }
    
    @Override
    public void unsetData1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.DATA1$2, 0);
        }
    }
    
    @Override
    public DataType getData2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DataType dataType = (DataType)this.get_store().find_element_user(ShapeSheetTypeImpl.DATA2$4, 0);
            if (dataType == null) {
                return null;
            }
            return dataType;
        }
    }
    
    @Override
    public boolean isSetData2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.DATA2$4) != 0;
        }
    }
    
    @Override
    public void setData2(final DataType dataType) {
        this.generatedSetterHelperImpl((XmlObject)dataType, ShapeSheetTypeImpl.DATA2$4, 0, (short)1);
    }
    
    @Override
    public DataType addNewData2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DataType)this.get_store().add_element_user(ShapeSheetTypeImpl.DATA2$4);
        }
    }
    
    @Override
    public void unsetData2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.DATA2$4, 0);
        }
    }
    
    @Override
    public DataType getData3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DataType dataType = (DataType)this.get_store().find_element_user(ShapeSheetTypeImpl.DATA3$6, 0);
            if (dataType == null) {
                return null;
            }
            return dataType;
        }
    }
    
    @Override
    public boolean isSetData3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.DATA3$6) != 0;
        }
    }
    
    @Override
    public void setData3(final DataType dataType) {
        this.generatedSetterHelperImpl((XmlObject)dataType, ShapeSheetTypeImpl.DATA3$6, 0, (short)1);
    }
    
    @Override
    public DataType addNewData3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DataType)this.get_store().add_element_user(ShapeSheetTypeImpl.DATA3$6);
        }
    }
    
    @Override
    public void unsetData3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.DATA3$6, 0);
        }
    }
    
    @Override
    public ForeignDataType getForeignData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ForeignDataType foreignDataType = (ForeignDataType)this.get_store().find_element_user(ShapeSheetTypeImpl.FOREIGNDATA$8, 0);
            if (foreignDataType == null) {
                return null;
            }
            return foreignDataType;
        }
    }
    
    @Override
    public boolean isSetForeignData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.FOREIGNDATA$8) != 0;
        }
    }
    
    @Override
    public void setForeignData(final ForeignDataType foreignDataType) {
        this.generatedSetterHelperImpl((XmlObject)foreignDataType, ShapeSheetTypeImpl.FOREIGNDATA$8, 0, (short)1);
    }
    
    @Override
    public ForeignDataType addNewForeignData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ForeignDataType)this.get_store().add_element_user(ShapeSheetTypeImpl.FOREIGNDATA$8);
        }
    }
    
    @Override
    public void unsetForeignData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.FOREIGNDATA$8, 0);
        }
    }
    
    @Override
    public ShapesType getShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ShapesType shapesType = (ShapesType)this.get_store().find_element_user(ShapeSheetTypeImpl.SHAPES$10, 0);
            if (shapesType == null) {
                return null;
            }
            return shapesType;
        }
    }
    
    @Override
    public boolean isSetShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapeSheetTypeImpl.SHAPES$10) != 0;
        }
    }
    
    @Override
    public void setShapes(final ShapesType shapesType) {
        this.generatedSetterHelperImpl((XmlObject)shapesType, ShapeSheetTypeImpl.SHAPES$10, 0, (short)1);
    }
    
    @Override
    public ShapesType addNewShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ShapesType)this.get_store().add_element_user(ShapeSheetTypeImpl.SHAPES$10);
        }
    }
    
    @Override
    public void unsetShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapeSheetTypeImpl.SHAPES$10, 0);
        }
    }
    
    @Override
    public long getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ID$12);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    @Override
    public XmlUnsignedInt xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ID$12);
        }
    }
    
    @Override
    public void setID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ID$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    @Override
    public void xsetID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ID$12);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ID$12);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    @Override
    public long getOriginalID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    @Override
    public XmlUnsignedInt xgetOriginalID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
        }
    }
    
    @Override
    public boolean isSetOriginalID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14) != null;
        }
    }
    
    @Override
    public void setOriginalID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    @Override
    public void xsetOriginalID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ORIGINALID$14);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    @Override
    public void unsetOriginalID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.ORIGINALID$14);
        }
    }
    
    @Override
    public boolean getDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.DEL$16);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.DEL$16);
        }
    }
    
    @Override
    public boolean isSetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.DEL$16) != null;
        }
    }
    
    @Override
    public void setDel(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.DEL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.DEL$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    @Override
    public void xsetDel(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.DEL$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(ShapeSheetTypeImpl.DEL$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    @Override
    public void unsetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.DEL$16);
        }
    }
    
    @Override
    public long getMasterShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    @Override
    public XmlUnsignedInt xgetMasterShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
        }
    }
    
    @Override
    public boolean isSetMasterShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18) != null;
        }
    }
    
    @Override
    public void setMasterShape(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    @Override
    public void xsetMasterShape(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ShapeSheetTypeImpl.MASTERSHAPE$18);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    @Override
    public void unsetMasterShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.MASTERSHAPE$18);
        }
    }
    
    @Override
    public String getUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
        }
    }
    
    @Override
    public boolean isSetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20) != null;
        }
    }
    
    @Override
    public void setUniqueID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetUniqueID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ShapeSheetTypeImpl.UNIQUEID$20);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetUniqueID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.UNIQUEID$20);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAME$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAME$22);
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAME$22) != null;
        }
    }
    
    @Override
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAME$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.NAME$22);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAME$22);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ShapeSheetTypeImpl.NAME$22);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.NAME$22);
        }
    }
    
    @Override
    public String getNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
        }
    }
    
    @Override
    public boolean isSetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAMEU$24) != null;
        }
    }
    
    @Override
    public void setNameU(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetNameU(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(ShapeSheetTypeImpl.NAMEU$24);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.NAMEU$24);
        }
    }
    
    @Override
    public boolean getIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
        }
    }
    
    @Override
    public boolean isSetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26) != null;
        }
    }
    
    @Override
    public void setIsCustomName(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    @Override
    public void xsetIsCustomName(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    @Override
    public void unsetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.ISCUSTOMNAME$26);
        }
    }
    
    @Override
    public boolean getIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
        }
    }
    
    @Override
    public boolean isSetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28) != null;
        }
    }
    
    @Override
    public void setIsCustomNameU(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    @Override
    public void xsetIsCustomNameU(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    @Override
    public void unsetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.ISCUSTOMNAMEU$28);
        }
    }
    
    @Override
    public long getMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTER$30);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    @Override
    public XmlUnsignedInt xgetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTER$30);
        }
    }
    
    @Override
    public boolean isSetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTER$30) != null;
        }
    }
    
    @Override
    public void setMaster(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTER$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.MASTER$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    @Override
    public void xsetMaster(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(ShapeSheetTypeImpl.MASTER$30);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(ShapeSheetTypeImpl.MASTER$30);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    @Override
    public void unsetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.MASTER$30);
        }
    }
    
    @Override
    public String getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.TYPE$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlToken)this.get_store().find_attribute_user(ShapeSheetTypeImpl.TYPE$32);
        }
    }
    
    @Override
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ShapeSheetTypeImpl.TYPE$32) != null;
        }
    }
    
    @Override
    public void setType(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ShapeSheetTypeImpl.TYPE$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ShapeSheetTypeImpl.TYPE$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetType(final XmlToken xmlToken) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken xmlToken2 = (XmlToken)this.get_store().find_attribute_user(ShapeSheetTypeImpl.TYPE$32);
            if (xmlToken2 == null) {
                xmlToken2 = (XmlToken)this.get_store().add_attribute_user(ShapeSheetTypeImpl.TYPE$32);
            }
            xmlToken2.set((XmlObject)xmlToken);
        }
    }
    
    @Override
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ShapeSheetTypeImpl.TYPE$32);
        }
    }
    
    static {
        TEXT$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Text");
        DATA1$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Data1");
        DATA2$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Data2");
        DATA3$6 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Data3");
        FOREIGNDATA$8 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "ForeignData");
        SHAPES$10 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Shapes");
        ID$12 = new QName("", "ID");
        ORIGINALID$14 = new QName("", "OriginalID");
        DEL$16 = new QName("", "Del");
        MASTERSHAPE$18 = new QName("", "MasterShape");
        UNIQUEID$20 = new QName("", "UniqueID");
        NAME$22 = new QName("", "Name");
        NAMEU$24 = new QName("", "NameU");
        ISCUSTOMNAME$26 = new QName("", "IsCustomName");
        ISCUSTOMNAMEU$28 = new QName("", "IsCustomNameU");
        MASTER$30 = new QName("", "Master");
        TYPE$32 = new QName("", "Type");
    }
}
