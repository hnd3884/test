package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.vml.STColorType;
import com.microsoft.schemas.vml.STShadowType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTShadow;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShadowImpl extends XmlComplexContentImpl implements CTShadow
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName ON$2;
    private static final QName TYPE$4;
    private static final QName OBSCURED$6;
    private static final QName COLOR$8;
    private static final QName OPACITY$10;
    private static final QName OFFSET$12;
    private static final QName COLOR2$14;
    private static final QName OFFSET2$16;
    private static final QName ORIGIN$18;
    private static final QName MATRIX$20;
    
    public CTShadowImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.ID$0);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.ID$0) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.ID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.ID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.ID$0);
        }
    }
    
    public STTrueFalse.Enum getOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ON$2);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShadowImpl.ON$2);
        }
    }
    
    public boolean isSetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.ON$2) != null;
        }
    }
    
    public void setOn(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ON$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.ON$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOn(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShadowImpl.ON$2);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShadowImpl.ON$2);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.ON$2);
        }
    }
    
    public STShadowType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.TYPE$4);
            if (simpleValue == null) {
                return null;
            }
            return (STShadowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STShadowType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STShadowType)this.get_store().find_attribute_user(CTShadowImpl.TYPE$4);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.TYPE$4) != null;
        }
    }
    
    public void setType(final STShadowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.TYPE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STShadowType stShadowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShadowType stShadowType2 = (STShadowType)this.get_store().find_attribute_user(CTShadowImpl.TYPE$4);
            if (stShadowType2 == null) {
                stShadowType2 = (STShadowType)this.get_store().add_attribute_user(CTShadowImpl.TYPE$4);
            }
            stShadowType2.set((XmlObject)stShadowType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.TYPE$4);
        }
    }
    
    public STTrueFalse.Enum getObscured() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OBSCURED$6);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetObscured() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShadowImpl.OBSCURED$6);
        }
    }
    
    public boolean isSetObscured() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.OBSCURED$6) != null;
        }
    }
    
    public void setObscured(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OBSCURED$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.OBSCURED$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetObscured(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShadowImpl.OBSCURED$6);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShadowImpl.OBSCURED$6);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetObscured() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.OBSCURED$6);
        }
    }
    
    public String getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.COLOR$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTShadowImpl.COLOR$8);
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.COLOR$8) != null;
        }
    }
    
    public void setColor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.COLOR$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.COLOR$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTShadowImpl.COLOR$8);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTShadowImpl.COLOR$8);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.COLOR$8);
        }
    }
    
    public String getOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OPACITY$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OPACITY$10);
        }
    }
    
    public boolean isSetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.OPACITY$10) != null;
        }
    }
    
    public void setOpacity(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OPACITY$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.OPACITY$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OPACITY$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.OPACITY$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.OPACITY$10);
        }
    }
    
    public String getOffset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OFFSET$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOffset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OFFSET$12);
        }
    }
    
    public boolean isSetOffset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.OFFSET$12) != null;
        }
    }
    
    public void setOffset(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OFFSET$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.OFFSET$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOffset(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OFFSET$12);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.OFFSET$12);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOffset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.OFFSET$12);
        }
    }
    
    public String getColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.COLOR2$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTShadowImpl.COLOR2$14);
        }
    }
    
    public boolean isSetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.COLOR2$14) != null;
        }
    }
    
    public void setColor2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.COLOR2$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.COLOR2$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColor2(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTShadowImpl.COLOR2$14);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTShadowImpl.COLOR2$14);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetColor2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.COLOR2$14);
        }
    }
    
    public String getOffset2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OFFSET2$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOffset2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OFFSET2$16);
        }
    }
    
    public boolean isSetOffset2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.OFFSET2$16) != null;
        }
    }
    
    public void setOffset2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.OFFSET2$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.OFFSET2$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOffset2(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.OFFSET2$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.OFFSET2$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOffset2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.OFFSET2$16);
        }
    }
    
    public String getOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ORIGIN$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.ORIGIN$18);
        }
    }
    
    public boolean isSetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.ORIGIN$18) != null;
        }
    }
    
    public void setOrigin(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.ORIGIN$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.ORIGIN$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOrigin(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.ORIGIN$18);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.ORIGIN$18);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.ORIGIN$18);
        }
    }
    
    public String getMatrix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.MATRIX$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMatrix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShadowImpl.MATRIX$20);
        }
    }
    
    public boolean isSetMatrix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShadowImpl.MATRIX$20) != null;
        }
    }
    
    public void setMatrix(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShadowImpl.MATRIX$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShadowImpl.MATRIX$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMatrix(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShadowImpl.MATRIX$20);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShadowImpl.MATRIX$20);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMatrix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShadowImpl.MATRIX$20);
        }
    }
    
    static {
        ID$0 = new QName("", "id");
        ON$2 = new QName("", "on");
        TYPE$4 = new QName("", "type");
        OBSCURED$6 = new QName("", "obscured");
        COLOR$8 = new QName("", "color");
        OPACITY$10 = new QName("", "opacity");
        OFFSET$12 = new QName("", "offset");
        COLOR2$14 = new QName("", "color2");
        OFFSET2$16 = new QName("", "offset2");
        ORIGIN$18 = new QName("", "origin");
        MATRIX$20 = new QName("", "matrix");
    }
}
