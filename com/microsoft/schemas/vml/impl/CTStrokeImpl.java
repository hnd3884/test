package com.microsoft.schemas.vml.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import com.microsoft.schemas.vml.STStrokeArrowLength;
import com.microsoft.schemas.vml.STStrokeArrowWidth;
import com.microsoft.schemas.vml.STStrokeArrowType;
import com.microsoft.schemas.vml.STImageAspect;
import com.microsoft.schemas.vml.STFillType;
import com.microsoft.schemas.vml.STStrokeEndCap;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import com.microsoft.schemas.vml.STStrokeLineStyle;
import com.microsoft.schemas.vml.STColorType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.office.CTStrokeChild;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTStroke;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStrokeImpl extends XmlComplexContentImpl implements CTStroke
{
    private static final long serialVersionUID = 1L;
    private static final QName LEFT$0;
    private static final QName TOP$2;
    private static final QName RIGHT$4;
    private static final QName BOTTOM$6;
    private static final QName COLUMN$8;
    private static final QName ID$10;
    private static final QName ON$12;
    private static final QName WEIGHT$14;
    private static final QName COLOR$16;
    private static final QName OPACITY$18;
    private static final QName LINESTYLE$20;
    private static final QName MITERLIMIT$22;
    private static final QName JOINSTYLE$24;
    private static final QName ENDCAP$26;
    private static final QName DASHSTYLE$28;
    private static final QName FILLTYPE$30;
    private static final QName SRC$32;
    private static final QName IMAGEASPECT$34;
    private static final QName IMAGESIZE$36;
    private static final QName IMAGEALIGNSHAPE$38;
    private static final QName COLOR2$40;
    private static final QName STARTARROW$42;
    private static final QName STARTARROWWIDTH$44;
    private static final QName STARTARROWLENGTH$46;
    private static final QName ENDARROW$48;
    private static final QName ENDARROWWIDTH$50;
    private static final QName ENDARROWLENGTH$52;
    private static final QName HREF$54;
    private static final QName ALTHREF$56;
    private static final QName TITLE$58;
    private static final QName FORCEDASH$60;
    private static final QName ID2$62;
    private static final QName INSETPEN$64;
    private static final QName RELID$66;
    
    public CTStrokeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStrokeChild getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrokeChild ctStrokeChild = (CTStrokeChild)this.get_store().find_element_user(CTStrokeImpl.LEFT$0, 0);
            if (ctStrokeChild == null) {
                return null;
            }
            return ctStrokeChild;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrokeImpl.LEFT$0) != 0;
        }
    }
    
    public void setLeft(final CTStrokeChild ctStrokeChild) {
        this.generatedSetterHelperImpl((XmlObject)ctStrokeChild, CTStrokeImpl.LEFT$0, 0, (short)1);
    }
    
    public CTStrokeChild addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrokeChild)this.get_store().add_element_user(CTStrokeImpl.LEFT$0);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrokeImpl.LEFT$0, 0);
        }
    }
    
    public CTStrokeChild getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrokeChild ctStrokeChild = (CTStrokeChild)this.get_store().find_element_user(CTStrokeImpl.TOP$2, 0);
            if (ctStrokeChild == null) {
                return null;
            }
            return ctStrokeChild;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrokeImpl.TOP$2) != 0;
        }
    }
    
    public void setTop(final CTStrokeChild ctStrokeChild) {
        this.generatedSetterHelperImpl((XmlObject)ctStrokeChild, CTStrokeImpl.TOP$2, 0, (short)1);
    }
    
    public CTStrokeChild addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrokeChild)this.get_store().add_element_user(CTStrokeImpl.TOP$2);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrokeImpl.TOP$2, 0);
        }
    }
    
    public CTStrokeChild getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrokeChild ctStrokeChild = (CTStrokeChild)this.get_store().find_element_user(CTStrokeImpl.RIGHT$4, 0);
            if (ctStrokeChild == null) {
                return null;
            }
            return ctStrokeChild;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrokeImpl.RIGHT$4) != 0;
        }
    }
    
    public void setRight(final CTStrokeChild ctStrokeChild) {
        this.generatedSetterHelperImpl((XmlObject)ctStrokeChild, CTStrokeImpl.RIGHT$4, 0, (short)1);
    }
    
    public CTStrokeChild addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrokeChild)this.get_store().add_element_user(CTStrokeImpl.RIGHT$4);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrokeImpl.RIGHT$4, 0);
        }
    }
    
    public CTStrokeChild getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrokeChild ctStrokeChild = (CTStrokeChild)this.get_store().find_element_user(CTStrokeImpl.BOTTOM$6, 0);
            if (ctStrokeChild == null) {
                return null;
            }
            return ctStrokeChild;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrokeImpl.BOTTOM$6) != 0;
        }
    }
    
    public void setBottom(final CTStrokeChild ctStrokeChild) {
        this.generatedSetterHelperImpl((XmlObject)ctStrokeChild, CTStrokeImpl.BOTTOM$6, 0, (short)1);
    }
    
    public CTStrokeChild addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrokeChild)this.get_store().add_element_user(CTStrokeImpl.BOTTOM$6);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrokeImpl.BOTTOM$6, 0);
        }
    }
    
    public CTStrokeChild getColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrokeChild ctStrokeChild = (CTStrokeChild)this.get_store().find_element_user(CTStrokeImpl.COLUMN$8, 0);
            if (ctStrokeChild == null) {
                return null;
            }
            return ctStrokeChild;
        }
    }
    
    public boolean isSetColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrokeImpl.COLUMN$8) != 0;
        }
    }
    
    public void setColumn(final CTStrokeChild ctStrokeChild) {
        this.generatedSetterHelperImpl((XmlObject)ctStrokeChild, CTStrokeImpl.COLUMN$8, 0, (short)1);
    }
    
    public CTStrokeChild addNewColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrokeChild)this.get_store().add_element_user(CTStrokeImpl.COLUMN$8);
        }
    }
    
    public void unsetColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrokeImpl.COLUMN$8, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ID$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.ID$10);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ID$10) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ID$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.ID$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.ID$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ID$10);
        }
    }
    
    public STTrueFalse.Enum getOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ON$12);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.ON$12);
        }
    }
    
    public boolean isSetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ON$12) != null;
        }
    }
    
    public void setOn(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ON$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ON$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOn(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.ON$12);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTStrokeImpl.ON$12);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ON$12);
        }
    }
    
    public String getWeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.WEIGHT$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetWeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.WEIGHT$14);
        }
    }
    
    public boolean isSetWeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.WEIGHT$14) != null;
        }
    }
    
    public void setWeight(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.WEIGHT$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.WEIGHT$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetWeight(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.WEIGHT$14);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.WEIGHT$14);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetWeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.WEIGHT$14);
        }
    }
    
    public String getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.COLOR$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTStrokeImpl.COLOR$16);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.COLOR$16) != null;
        }
    }
    
    public void setColor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.COLOR$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.COLOR$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTStrokeImpl.COLOR$16);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTStrokeImpl.COLOR$16);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.COLOR$16);
        }
    }
    
    public String getOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.OPACITY$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.OPACITY$18);
        }
    }
    
    public boolean isSetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.OPACITY$18) != null;
        }
    }
    
    public void setOpacity(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.OPACITY$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.OPACITY$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.OPACITY$18);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.OPACITY$18);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.OPACITY$18);
        }
    }
    
    public STStrokeLineStyle.Enum getLinestyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.LINESTYLE$20);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeLineStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeLineStyle xgetLinestyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeLineStyle)this.get_store().find_attribute_user(CTStrokeImpl.LINESTYLE$20);
        }
    }
    
    public boolean isSetLinestyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.LINESTYLE$20) != null;
        }
    }
    
    public void setLinestyle(final STStrokeLineStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.LINESTYLE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.LINESTYLE$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLinestyle(final STStrokeLineStyle stStrokeLineStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeLineStyle stStrokeLineStyle2 = (STStrokeLineStyle)this.get_store().find_attribute_user(CTStrokeImpl.LINESTYLE$20);
            if (stStrokeLineStyle2 == null) {
                stStrokeLineStyle2 = (STStrokeLineStyle)this.get_store().add_attribute_user(CTStrokeImpl.LINESTYLE$20);
            }
            stStrokeLineStyle2.set((XmlObject)stStrokeLineStyle);
        }
    }
    
    public void unsetLinestyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.LINESTYLE$20);
        }
    }
    
    public BigDecimal getMiterlimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.MITERLIMIT$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigDecimalValue();
        }
    }
    
    public XmlDecimal xgetMiterlimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDecimal)this.get_store().find_attribute_user(CTStrokeImpl.MITERLIMIT$22);
        }
    }
    
    public boolean isSetMiterlimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.MITERLIMIT$22) != null;
        }
    }
    
    public void setMiterlimit(final BigDecimal bigDecimalValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.MITERLIMIT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.MITERLIMIT$22);
            }
            simpleValue.setBigDecimalValue(bigDecimalValue);
        }
    }
    
    public void xsetMiterlimit(final XmlDecimal xmlDecimal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDecimal xmlDecimal2 = (XmlDecimal)this.get_store().find_attribute_user(CTStrokeImpl.MITERLIMIT$22);
            if (xmlDecimal2 == null) {
                xmlDecimal2 = (XmlDecimal)this.get_store().add_attribute_user(CTStrokeImpl.MITERLIMIT$22);
            }
            xmlDecimal2.set((XmlObject)xmlDecimal);
        }
    }
    
    public void unsetMiterlimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.MITERLIMIT$22);
        }
    }
    
    public STStrokeJoinStyle.Enum getJoinstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.JOINSTYLE$24);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeJoinStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeJoinStyle xgetJoinstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeJoinStyle)this.get_store().find_attribute_user(CTStrokeImpl.JOINSTYLE$24);
        }
    }
    
    public boolean isSetJoinstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.JOINSTYLE$24) != null;
        }
    }
    
    public void setJoinstyle(final STStrokeJoinStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.JOINSTYLE$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.JOINSTYLE$24);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetJoinstyle(final STStrokeJoinStyle stStrokeJoinStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeJoinStyle stStrokeJoinStyle2 = (STStrokeJoinStyle)this.get_store().find_attribute_user(CTStrokeImpl.JOINSTYLE$24);
            if (stStrokeJoinStyle2 == null) {
                stStrokeJoinStyle2 = (STStrokeJoinStyle)this.get_store().add_attribute_user(CTStrokeImpl.JOINSTYLE$24);
            }
            stStrokeJoinStyle2.set((XmlObject)stStrokeJoinStyle);
        }
    }
    
    public void unsetJoinstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.JOINSTYLE$24);
        }
    }
    
    public STStrokeEndCap.Enum getEndcap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDCAP$26);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeEndCap.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeEndCap xgetEndcap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeEndCap)this.get_store().find_attribute_user(CTStrokeImpl.ENDCAP$26);
        }
    }
    
    public boolean isSetEndcap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ENDCAP$26) != null;
        }
    }
    
    public void setEndcap(final STStrokeEndCap.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDCAP$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ENDCAP$26);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEndcap(final STStrokeEndCap stStrokeEndCap) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeEndCap stStrokeEndCap2 = (STStrokeEndCap)this.get_store().find_attribute_user(CTStrokeImpl.ENDCAP$26);
            if (stStrokeEndCap2 == null) {
                stStrokeEndCap2 = (STStrokeEndCap)this.get_store().add_attribute_user(CTStrokeImpl.ENDCAP$26);
            }
            stStrokeEndCap2.set((XmlObject)stStrokeEndCap);
        }
    }
    
    public void unsetEndcap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ENDCAP$26);
        }
    }
    
    public String getDashstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.DASHSTYLE$28);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDashstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.DASHSTYLE$28);
        }
    }
    
    public boolean isSetDashstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.DASHSTYLE$28) != null;
        }
    }
    
    public void setDashstyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.DASHSTYLE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.DASHSTYLE$28);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDashstyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.DASHSTYLE$28);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.DASHSTYLE$28);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDashstyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.DASHSTYLE$28);
        }
    }
    
    public STFillType.Enum getFilltype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.FILLTYPE$30);
            if (simpleValue == null) {
                return null;
            }
            return (STFillType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFillType xgetFilltype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFillType)this.get_store().find_attribute_user(CTStrokeImpl.FILLTYPE$30);
        }
    }
    
    public boolean isSetFilltype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.FILLTYPE$30) != null;
        }
    }
    
    public void setFilltype(final STFillType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.FILLTYPE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.FILLTYPE$30);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFilltype(final STFillType stFillType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFillType stFillType2 = (STFillType)this.get_store().find_attribute_user(CTStrokeImpl.FILLTYPE$30);
            if (stFillType2 == null) {
                stFillType2 = (STFillType)this.get_store().add_attribute_user(CTStrokeImpl.FILLTYPE$30);
            }
            stFillType2.set((XmlObject)stFillType);
        }
    }
    
    public void unsetFilltype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.FILLTYPE$30);
        }
    }
    
    public String getSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.SRC$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.SRC$32);
        }
    }
    
    public boolean isSetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.SRC$32) != null;
        }
    }
    
    public void setSrc(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.SRC$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.SRC$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSrc(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.SRC$32);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.SRC$32);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.SRC$32);
        }
    }
    
    public STImageAspect.Enum getImageaspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
            if (simpleValue == null) {
                return null;
            }
            return (STImageAspect.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STImageAspect xgetImageaspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STImageAspect)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
        }
    }
    
    public boolean isSetImageaspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.IMAGEASPECT$34) != null;
        }
    }
    
    public void setImageaspect(final STImageAspect.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetImageaspect(final STImageAspect stImageAspect) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STImageAspect stImageAspect2 = (STImageAspect)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
            if (stImageAspect2 == null) {
                stImageAspect2 = (STImageAspect)this.get_store().add_attribute_user(CTStrokeImpl.IMAGEASPECT$34);
            }
            stImageAspect2.set((XmlObject)stImageAspect);
        }
    }
    
    public void unsetImageaspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.IMAGEASPECT$34);
        }
    }
    
    public String getImagesize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGESIZE$36);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetImagesize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.IMAGESIZE$36);
        }
    }
    
    public boolean isSetImagesize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.IMAGESIZE$36) != null;
        }
    }
    
    public void setImagesize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGESIZE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.IMAGESIZE$36);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetImagesize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.IMAGESIZE$36);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.IMAGESIZE$36);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetImagesize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.IMAGESIZE$36);
        }
    }
    
    public STTrueFalse.Enum getImagealignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetImagealignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
        }
    }
    
    public boolean isSetImagealignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38) != null;
        }
    }
    
    public void setImagealignshape(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetImagealignshape(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTStrokeImpl.IMAGEALIGNSHAPE$38);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetImagealignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.IMAGEALIGNSHAPE$38);
        }
    }
    
    public String getColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.COLOR2$40);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTStrokeImpl.COLOR2$40);
        }
    }
    
    public boolean isSetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.COLOR2$40) != null;
        }
    }
    
    public void setColor2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.COLOR2$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.COLOR2$40);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor2(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTStrokeImpl.COLOR2$40);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTStrokeImpl.COLOR2$40);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.COLOR2$40);
        }
    }
    
    public STStrokeArrowType.Enum getStartarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROW$42);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowType xgetStartarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowType)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROW$42);
        }
    }
    
    public boolean isSetStartarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.STARTARROW$42) != null;
        }
    }
    
    public void setStartarrow(final STStrokeArrowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROW$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROW$42);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStartarrow(final STStrokeArrowType stStrokeArrowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowType stStrokeArrowType2 = (STStrokeArrowType)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROW$42);
            if (stStrokeArrowType2 == null) {
                stStrokeArrowType2 = (STStrokeArrowType)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROW$42);
            }
            stStrokeArrowType2.set((XmlObject)stStrokeArrowType);
        }
    }
    
    public void unsetStartarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.STARTARROW$42);
        }
    }
    
    public STStrokeArrowWidth.Enum getStartarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowWidth.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowWidth xgetStartarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowWidth)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
        }
    }
    
    public boolean isSetStartarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44) != null;
        }
    }
    
    public void setStartarrowwidth(final STStrokeArrowWidth.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStartarrowwidth(final STStrokeArrowWidth stStrokeArrowWidth) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowWidth stStrokeArrowWidth2 = (STStrokeArrowWidth)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
            if (stStrokeArrowWidth2 == null) {
                stStrokeArrowWidth2 = (STStrokeArrowWidth)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROWWIDTH$44);
            }
            stStrokeArrowWidth2.set((XmlObject)stStrokeArrowWidth);
        }
    }
    
    public void unsetStartarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.STARTARROWWIDTH$44);
        }
    }
    
    public STStrokeArrowLength.Enum getStartarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowLength.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowLength xgetStartarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowLength)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
        }
    }
    
    public boolean isSetStartarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46) != null;
        }
    }
    
    public void setStartarrowlength(final STStrokeArrowLength.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStartarrowlength(final STStrokeArrowLength stStrokeArrowLength) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowLength stStrokeArrowLength2 = (STStrokeArrowLength)this.get_store().find_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
            if (stStrokeArrowLength2 == null) {
                stStrokeArrowLength2 = (STStrokeArrowLength)this.get_store().add_attribute_user(CTStrokeImpl.STARTARROWLENGTH$46);
            }
            stStrokeArrowLength2.set((XmlObject)stStrokeArrowLength);
        }
    }
    
    public void unsetStartarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.STARTARROWLENGTH$46);
        }
    }
    
    public STStrokeArrowType.Enum getEndarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROW$48);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowType xgetEndarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowType)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROW$48);
        }
    }
    
    public boolean isSetEndarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ENDARROW$48) != null;
        }
    }
    
    public void setEndarrow(final STStrokeArrowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROW$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROW$48);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEndarrow(final STStrokeArrowType stStrokeArrowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowType stStrokeArrowType2 = (STStrokeArrowType)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROW$48);
            if (stStrokeArrowType2 == null) {
                stStrokeArrowType2 = (STStrokeArrowType)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROW$48);
            }
            stStrokeArrowType2.set((XmlObject)stStrokeArrowType);
        }
    }
    
    public void unsetEndarrow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ENDARROW$48);
        }
    }
    
    public STStrokeArrowWidth.Enum getEndarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowWidth.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowWidth xgetEndarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowWidth)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
        }
    }
    
    public boolean isSetEndarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50) != null;
        }
    }
    
    public void setEndarrowwidth(final STStrokeArrowWidth.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEndarrowwidth(final STStrokeArrowWidth stStrokeArrowWidth) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowWidth stStrokeArrowWidth2 = (STStrokeArrowWidth)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
            if (stStrokeArrowWidth2 == null) {
                stStrokeArrowWidth2 = (STStrokeArrowWidth)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROWWIDTH$50);
            }
            stStrokeArrowWidth2.set((XmlObject)stStrokeArrowWidth);
        }
    }
    
    public void unsetEndarrowwidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ENDARROWWIDTH$50);
        }
    }
    
    public STStrokeArrowLength.Enum getEndarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
            if (simpleValue == null) {
                return null;
            }
            return (STStrokeArrowLength.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStrokeArrowLength xgetEndarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStrokeArrowLength)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
        }
    }
    
    public boolean isSetEndarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52) != null;
        }
    }
    
    public void setEndarrowlength(final STStrokeArrowLength.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEndarrowlength(final STStrokeArrowLength stStrokeArrowLength) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStrokeArrowLength stStrokeArrowLength2 = (STStrokeArrowLength)this.get_store().find_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
            if (stStrokeArrowLength2 == null) {
                stStrokeArrowLength2 = (STStrokeArrowLength)this.get_store().add_attribute_user(CTStrokeImpl.ENDARROWLENGTH$52);
            }
            stStrokeArrowLength2.set((XmlObject)stStrokeArrowLength);
        }
    }
    
    public void unsetEndarrowlength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ENDARROWLENGTH$52);
        }
    }
    
    public String getHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.HREF$54);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.HREF$54);
        }
    }
    
    public boolean isSetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.HREF$54) != null;
        }
    }
    
    public void setHref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.HREF$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.HREF$54);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.HREF$54);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.HREF$54);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.HREF$54);
        }
    }
    
    public String getAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ALTHREF$56);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.ALTHREF$56);
        }
    }
    
    public boolean isSetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ALTHREF$56) != null;
        }
    }
    
    public void setAlthref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ALTHREF$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ALTHREF$56);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlthref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.ALTHREF$56);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.ALTHREF$56);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ALTHREF$56);
        }
    }
    
    public String getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.TITLE$58);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.TITLE$58);
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.TITLE$58) != null;
        }
    }
    
    public void setTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.TITLE$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.TITLE$58);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTitle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStrokeImpl.TITLE$58);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStrokeImpl.TITLE$58);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.TITLE$58);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.FORCEDASH$60);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.FORCEDASH$60);
        }
    }
    
    public boolean isSetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.FORCEDASH$60) != null;
        }
    }
    
    public void setForcedash(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.FORCEDASH$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.FORCEDASH$60);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetForcedash(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.FORCEDASH$60);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTStrokeImpl.FORCEDASH$60);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.FORCEDASH$60);
        }
    }
    
    public String getId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ID2$62);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTStrokeImpl.ID2$62);
        }
    }
    
    public boolean isSetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.ID2$62) != null;
        }
    }
    
    public void setId2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.ID2$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.ID2$62);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId2(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTStrokeImpl.ID2$62);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTStrokeImpl.ID2$62);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.ID2$62);
        }
    }
    
    public STTrueFalse.Enum getInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.INSETPEN$64);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.INSETPEN$64);
        }
    }
    
    public boolean isSetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.INSETPEN$64) != null;
        }
    }
    
    public void setInsetpen(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.INSETPEN$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.INSETPEN$64);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetpen(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTStrokeImpl.INSETPEN$64);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTStrokeImpl.INSETPEN$64);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.INSETPEN$64);
        }
    }
    
    public String getRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.RELID$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STRelationshipId xgetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().find_attribute_user(CTStrokeImpl.RELID$66);
        }
    }
    
    public boolean isSetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStrokeImpl.RELID$66) != null;
        }
    }
    
    public void setRelid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStrokeImpl.RELID$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStrokeImpl.RELID$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRelid(final com.microsoft.schemas.office.office.STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STRelationshipId stRelationshipId2 = (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().find_attribute_user(CTStrokeImpl.RELID$66);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().add_attribute_user(CTStrokeImpl.RELID$66);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStrokeImpl.RELID$66);
        }
    }
    
    static {
        LEFT$0 = new QName("urn:schemas-microsoft-com:office:office", "left");
        TOP$2 = new QName("urn:schemas-microsoft-com:office:office", "top");
        RIGHT$4 = new QName("urn:schemas-microsoft-com:office:office", "right");
        BOTTOM$6 = new QName("urn:schemas-microsoft-com:office:office", "bottom");
        COLUMN$8 = new QName("urn:schemas-microsoft-com:office:office", "column");
        ID$10 = new QName("", "id");
        ON$12 = new QName("", "on");
        WEIGHT$14 = new QName("", "weight");
        COLOR$16 = new QName("", "color");
        OPACITY$18 = new QName("", "opacity");
        LINESTYLE$20 = new QName("", "linestyle");
        MITERLIMIT$22 = new QName("", "miterlimit");
        JOINSTYLE$24 = new QName("", "joinstyle");
        ENDCAP$26 = new QName("", "endcap");
        DASHSTYLE$28 = new QName("", "dashstyle");
        FILLTYPE$30 = new QName("", "filltype");
        SRC$32 = new QName("", "src");
        IMAGEASPECT$34 = new QName("", "imageaspect");
        IMAGESIZE$36 = new QName("", "imagesize");
        IMAGEALIGNSHAPE$38 = new QName("", "imagealignshape");
        COLOR2$40 = new QName("", "color2");
        STARTARROW$42 = new QName("", "startarrow");
        STARTARROWWIDTH$44 = new QName("", "startarrowwidth");
        STARTARROWLENGTH$46 = new QName("", "startarrowlength");
        ENDARROW$48 = new QName("", "endarrow");
        ENDARROWWIDTH$50 = new QName("", "endarrowwidth");
        ENDARROWLENGTH$52 = new QName("", "endarrowlength");
        HREF$54 = new QName("urn:schemas-microsoft-com:office:office", "href");
        ALTHREF$56 = new QName("urn:schemas-microsoft-com:office:office", "althref");
        TITLE$58 = new QName("urn:schemas-microsoft-com:office:office", "title");
        FORCEDASH$60 = new QName("urn:schemas-microsoft-com:office:office", "forcedash");
        ID2$62 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        INSETPEN$64 = new QName("", "insetpen");
        RELID$66 = new QName("urn:schemas-microsoft-com:office:office", "relid");
    }
}
