package com.microsoft.schemas.vml.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import com.microsoft.schemas.vml.STFillMethod;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import com.microsoft.schemas.vml.STImageAspect;
import com.microsoft.schemas.vml.STColorType;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STFillType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTFill;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFillImpl extends XmlComplexContentImpl implements CTFill
{
    private static final long serialVersionUID = 1L;
    private static final QName FILL$0;
    private static final QName ID$2;
    private static final QName TYPE$4;
    private static final QName ON$6;
    private static final QName COLOR$8;
    private static final QName OPACITY$10;
    private static final QName COLOR2$12;
    private static final QName SRC$14;
    private static final QName HREF$16;
    private static final QName ALTHREF$18;
    private static final QName SIZE$20;
    private static final QName ORIGIN$22;
    private static final QName POSITION$24;
    private static final QName ASPECT$26;
    private static final QName COLORS$28;
    private static final QName ANGLE$30;
    private static final QName ALIGNSHAPE$32;
    private static final QName FOCUS$34;
    private static final QName FOCUSSIZE$36;
    private static final QName FOCUSPOSITION$38;
    private static final QName METHOD$40;
    private static final QName DETECTMOUSECLICK$42;
    private static final QName TITLE$44;
    private static final QName OPACITY2$46;
    private static final QName RECOLOR$48;
    private static final QName ROTATE$50;
    private static final QName ID2$52;
    private static final QName RELID$54;
    
    public CTFillImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public com.microsoft.schemas.office.office.CTFill getFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final com.microsoft.schemas.office.office.CTFill ctFill = (com.microsoft.schemas.office.office.CTFill)this.get_store().find_element_user(CTFillImpl.FILL$0, 0);
            if (ctFill == null) {
                return null;
            }
            return ctFill;
        }
    }
    
    public boolean isSetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillImpl.FILL$0) != 0;
        }
    }
    
    public void setFill(final com.microsoft.schemas.office.office.CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTFillImpl.FILL$0, 0, (short)1);
    }
    
    public com.microsoft.schemas.office.office.CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.CTFill)this.get_store().add_element_user(CTFillImpl.FILL$0);
        }
    }
    
    public void unsetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillImpl.FILL$0, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.ID$2);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ID$2) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.ID$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.ID$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ID$2);
        }
    }
    
    public STFillType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.TYPE$4);
            if (simpleValue == null) {
                return null;
            }
            return (STFillType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFillType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFillType)this.get_store().find_attribute_user(CTFillImpl.TYPE$4);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.TYPE$4) != null;
        }
    }
    
    public void setType(final STFillType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.TYPE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STFillType stFillType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFillType stFillType2 = (STFillType)this.get_store().find_attribute_user(CTFillImpl.TYPE$4);
            if (stFillType2 == null) {
                stFillType2 = (STFillType)this.get_store().add_attribute_user(CTFillImpl.TYPE$4);
            }
            stFillType2.set((XmlObject)stFillType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.TYPE$4);
        }
    }
    
    public STTrueFalse.Enum getOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ON$6);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ON$6);
        }
    }
    
    public boolean isSetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ON$6) != null;
        }
    }
    
    public void setOn(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ON$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ON$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOn(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ON$6);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTFillImpl.ON$6);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ON$6);
        }
    }
    
    public String getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLOR$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTFillImpl.COLOR$8);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.COLOR$8) != null;
        }
    }
    
    public void setColor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLOR$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.COLOR$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTFillImpl.COLOR$8);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTFillImpl.COLOR$8);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.COLOR$8);
        }
    }
    
    public String getOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.OPACITY$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.OPACITY$10);
        }
    }
    
    public boolean isSetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.OPACITY$10) != null;
        }
    }
    
    public void setOpacity(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.OPACITY$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.OPACITY$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.OPACITY$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.OPACITY$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.OPACITY$10);
        }
    }
    
    public String getColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLOR2$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTFillImpl.COLOR2$12);
        }
    }
    
    public boolean isSetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.COLOR2$12) != null;
        }
    }
    
    public void setColor2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLOR2$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.COLOR2$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor2(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTFillImpl.COLOR2$12);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTFillImpl.COLOR2$12);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.COLOR2$12);
        }
    }
    
    public String getSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.SRC$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.SRC$14);
        }
    }
    
    public boolean isSetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.SRC$14) != null;
        }
    }
    
    public void setSrc(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.SRC$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.SRC$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSrc(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.SRC$14);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.SRC$14);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.SRC$14);
        }
    }
    
    public String getHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.HREF$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.HREF$16);
        }
    }
    
    public boolean isSetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.HREF$16) != null;
        }
    }
    
    public void setHref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.HREF$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.HREF$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.HREF$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.HREF$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.HREF$16);
        }
    }
    
    public String getAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ALTHREF$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.ALTHREF$18);
        }
    }
    
    public boolean isSetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ALTHREF$18) != null;
        }
    }
    
    public void setAlthref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ALTHREF$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ALTHREF$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlthref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.ALTHREF$18);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.ALTHREF$18);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAlthref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ALTHREF$18);
        }
    }
    
    public String getSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.SIZE$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.SIZE$20);
        }
    }
    
    public boolean isSetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.SIZE$20) != null;
        }
    }
    
    public void setSize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.SIZE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.SIZE$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.SIZE$20);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.SIZE$20);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.SIZE$20);
        }
    }
    
    public String getOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ORIGIN$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.ORIGIN$22);
        }
    }
    
    public boolean isSetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ORIGIN$22) != null;
        }
    }
    
    public void setOrigin(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ORIGIN$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ORIGIN$22);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOrigin(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.ORIGIN$22);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.ORIGIN$22);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ORIGIN$22);
        }
    }
    
    public String getPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.POSITION$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.POSITION$24);
        }
    }
    
    public boolean isSetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.POSITION$24) != null;
        }
    }
    
    public void setPosition(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.POSITION$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.POSITION$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPosition(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.POSITION$24);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.POSITION$24);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.POSITION$24);
        }
    }
    
    public STImageAspect.Enum getAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ASPECT$26);
            if (simpleValue == null) {
                return null;
            }
            return (STImageAspect.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STImageAspect xgetAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STImageAspect)this.get_store().find_attribute_user(CTFillImpl.ASPECT$26);
        }
    }
    
    public boolean isSetAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ASPECT$26) != null;
        }
    }
    
    public void setAspect(final STImageAspect.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ASPECT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ASPECT$26);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAspect(final STImageAspect stImageAspect) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STImageAspect stImageAspect2 = (STImageAspect)this.get_store().find_attribute_user(CTFillImpl.ASPECT$26);
            if (stImageAspect2 == null) {
                stImageAspect2 = (STImageAspect)this.get_store().add_attribute_user(CTFillImpl.ASPECT$26);
            }
            stImageAspect2.set((XmlObject)stImageAspect);
        }
    }
    
    public void unsetAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ASPECT$26);
        }
    }
    
    public String getColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLORS$28);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.COLORS$28);
        }
    }
    
    public boolean isSetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.COLORS$28) != null;
        }
    }
    
    public void setColors(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.COLORS$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.COLORS$28);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColors(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.COLORS$28);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.COLORS$28);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.COLORS$28);
        }
    }
    
    public BigDecimal getAngle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ANGLE$30);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigDecimalValue();
        }
    }
    
    public XmlDecimal xgetAngle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDecimal)this.get_store().find_attribute_user(CTFillImpl.ANGLE$30);
        }
    }
    
    public boolean isSetAngle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ANGLE$30) != null;
        }
    }
    
    public void setAngle(final BigDecimal bigDecimalValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ANGLE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ANGLE$30);
            }
            simpleValue.setBigDecimalValue(bigDecimalValue);
        }
    }
    
    public void xsetAngle(final XmlDecimal xmlDecimal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDecimal xmlDecimal2 = (XmlDecimal)this.get_store().find_attribute_user(CTFillImpl.ANGLE$30);
            if (xmlDecimal2 == null) {
                xmlDecimal2 = (XmlDecimal)this.get_store().add_attribute_user(CTFillImpl.ANGLE$30);
            }
            xmlDecimal2.set((XmlObject)xmlDecimal);
        }
    }
    
    public void unsetAngle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ANGLE$30);
        }
    }
    
    public STTrueFalse.Enum getAlignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ALIGNSHAPE$32);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetAlignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ALIGNSHAPE$32);
        }
    }
    
    public boolean isSetAlignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ALIGNSHAPE$32) != null;
        }
    }
    
    public void setAlignshape(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ALIGNSHAPE$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ALIGNSHAPE$32);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlignshape(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ALIGNSHAPE$32);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTFillImpl.ALIGNSHAPE$32);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAlignshape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ALIGNSHAPE$32);
        }
    }
    
    public String getFocus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUS$34);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetFocus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUS$34);
        }
    }
    
    public boolean isSetFocus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.FOCUS$34) != null;
        }
    }
    
    public void setFocus(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUS$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.FOCUS$34);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFocus(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUS$34);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.FOCUS$34);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetFocus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.FOCUS$34);
        }
    }
    
    public String getFocussize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUSSIZE$36);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetFocussize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUSSIZE$36);
        }
    }
    
    public boolean isSetFocussize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.FOCUSSIZE$36) != null;
        }
    }
    
    public void setFocussize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUSSIZE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.FOCUSSIZE$36);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFocussize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUSSIZE$36);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.FOCUSSIZE$36);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetFocussize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.FOCUSSIZE$36);
        }
    }
    
    public String getFocusposition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUSPOSITION$38);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetFocusposition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUSPOSITION$38);
        }
    }
    
    public boolean isSetFocusposition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.FOCUSPOSITION$38) != null;
        }
    }
    
    public void setFocusposition(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.FOCUSPOSITION$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.FOCUSPOSITION$38);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFocusposition(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.FOCUSPOSITION$38);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.FOCUSPOSITION$38);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetFocusposition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.FOCUSPOSITION$38);
        }
    }
    
    public STFillMethod.Enum getMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.METHOD$40);
            if (simpleValue == null) {
                return null;
            }
            return (STFillMethod.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFillMethod xgetMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFillMethod)this.get_store().find_attribute_user(CTFillImpl.METHOD$40);
        }
    }
    
    public boolean isSetMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.METHOD$40) != null;
        }
    }
    
    public void setMethod(final STFillMethod.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.METHOD$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.METHOD$40);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetMethod(final STFillMethod stFillMethod) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFillMethod stFillMethod2 = (STFillMethod)this.get_store().find_attribute_user(CTFillImpl.METHOD$40);
            if (stFillMethod2 == null) {
                stFillMethod2 = (STFillMethod)this.get_store().add_attribute_user(CTFillImpl.METHOD$40);
            }
            stFillMethod2.set((XmlObject)stFillMethod);
        }
    }
    
    public void unsetMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.METHOD$40);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getDetectmouseclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetDetectmouseclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
        }
    }
    
    public boolean isSetDetectmouseclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.DETECTMOUSECLICK$42) != null;
        }
    }
    
    public void setDetectmouseclick(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDetectmouseclick(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTFillImpl.DETECTMOUSECLICK$42);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetDetectmouseclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.DETECTMOUSECLICK$42);
        }
    }
    
    public String getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.TITLE$44);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.TITLE$44);
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.TITLE$44) != null;
        }
    }
    
    public void setTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.TITLE$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.TITLE$44);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTitle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.TITLE$44);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.TITLE$44);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.TITLE$44);
        }
    }
    
    public String getOpacity2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.OPACITY2$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFillImpl.OPACITY2$46);
        }
    }
    
    public boolean isSetOpacity2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.OPACITY2$46) != null;
        }
    }
    
    public void setOpacity2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.OPACITY2$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.OPACITY2$46);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity2(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFillImpl.OPACITY2$46);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFillImpl.OPACITY2$46);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.OPACITY2$46);
        }
    }
    
    public STTrueFalse.Enum getRecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.RECOLOR$48);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetRecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.RECOLOR$48);
        }
    }
    
    public boolean isSetRecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.RECOLOR$48) != null;
        }
    }
    
    public void setRecolor(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.RECOLOR$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.RECOLOR$48);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRecolor(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.RECOLOR$48);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTFillImpl.RECOLOR$48);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetRecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.RECOLOR$48);
        }
    }
    
    public STTrueFalse.Enum getRotate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ROTATE$50);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetRotate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ROTATE$50);
        }
    }
    
    public boolean isSetRotate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ROTATE$50) != null;
        }
    }
    
    public void setRotate(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ROTATE$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ROTATE$50);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRotate(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTFillImpl.ROTATE$50);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTFillImpl.ROTATE$50);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetRotate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ROTATE$50);
        }
    }
    
    public String getId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ID2$52);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTFillImpl.ID2$52);
        }
    }
    
    public boolean isSetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.ID2$52) != null;
        }
    }
    
    public void setId2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.ID2$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.ID2$52);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId2(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTFillImpl.ID2$52);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTFillImpl.ID2$52);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.ID2$52);
        }
    }
    
    public String getRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.RELID$54);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STRelationshipId xgetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().find_attribute_user(CTFillImpl.RELID$54);
        }
    }
    
    public boolean isSetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillImpl.RELID$54) != null;
        }
    }
    
    public void setRelid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillImpl.RELID$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillImpl.RELID$54);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRelid(final com.microsoft.schemas.office.office.STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STRelationshipId stRelationshipId2 = (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().find_attribute_user(CTFillImpl.RELID$54);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (com.microsoft.schemas.office.office.STRelationshipId)this.get_store().add_attribute_user(CTFillImpl.RELID$54);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetRelid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillImpl.RELID$54);
        }
    }
    
    static {
        FILL$0 = new QName("urn:schemas-microsoft-com:office:office", "fill");
        ID$2 = new QName("", "id");
        TYPE$4 = new QName("", "type");
        ON$6 = new QName("", "on");
        COLOR$8 = new QName("", "color");
        OPACITY$10 = new QName("", "opacity");
        COLOR2$12 = new QName("", "color2");
        SRC$14 = new QName("", "src");
        HREF$16 = new QName("urn:schemas-microsoft-com:office:office", "href");
        ALTHREF$18 = new QName("urn:schemas-microsoft-com:office:office", "althref");
        SIZE$20 = new QName("", "size");
        ORIGIN$22 = new QName("", "origin");
        POSITION$24 = new QName("", "position");
        ASPECT$26 = new QName("", "aspect");
        COLORS$28 = new QName("", "colors");
        ANGLE$30 = new QName("", "angle");
        ALIGNSHAPE$32 = new QName("", "alignshape");
        FOCUS$34 = new QName("", "focus");
        FOCUSSIZE$36 = new QName("", "focussize");
        FOCUSPOSITION$38 = new QName("", "focusposition");
        METHOD$40 = new QName("", "method");
        DETECTMOUSECLICK$42 = new QName("urn:schemas-microsoft-com:office:office", "detectmouseclick");
        TITLE$44 = new QName("urn:schemas-microsoft-com:office:office", "title");
        OPACITY2$46 = new QName("urn:schemas-microsoft-com:office:office", "opacity2");
        RECOLOR$48 = new QName("", "recolor");
        ROTATE$50 = new QName("", "rotate");
        ID2$52 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        RELID$54 = new QName("urn:schemas-microsoft-com:office:office", "relid");
    }
}
