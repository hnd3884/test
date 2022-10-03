package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.office.office.STConnectType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTPath;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPathImpl extends XmlComplexContentImpl implements CTPath
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName V$2;
    private static final QName LIMO$4;
    private static final QName TEXTBOXRECT$6;
    private static final QName FILLOK$8;
    private static final QName STROKEOK$10;
    private static final QName SHADOWOK$12;
    private static final QName ARROWOK$14;
    private static final QName GRADIENTSHAPEOK$16;
    private static final QName TEXTPATHOK$18;
    private static final QName INSETPENOK$20;
    private static final QName CONNECTTYPE$22;
    private static final QName CONNECTLOCS$24;
    private static final QName CONNECTANGLES$26;
    private static final QName EXTRUSIONOK$28;
    
    public CTPathImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.ID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.ID$0);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.ID$0) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.ID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.ID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.ID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.ID$0);
        }
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.V$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.V$2);
        }
    }
    
    public boolean isSetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.V$2) != null;
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.V$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.V$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.V$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.V$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.V$2);
        }
    }
    
    public String getLimo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.LIMO$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetLimo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.LIMO$4);
        }
    }
    
    public boolean isSetLimo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.LIMO$4) != null;
        }
    }
    
    public void setLimo(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.LIMO$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.LIMO$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLimo(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.LIMO$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.LIMO$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetLimo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.LIMO$4);
        }
    }
    
    public String getTextboxrect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.TEXTBOXRECT$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTextboxrect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.TEXTBOXRECT$6);
        }
    }
    
    public boolean isSetTextboxrect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.TEXTBOXRECT$6) != null;
        }
    }
    
    public void setTextboxrect(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.TEXTBOXRECT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.TEXTBOXRECT$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTextboxrect(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.TEXTBOXRECT$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.TEXTBOXRECT$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTextboxrect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.TEXTBOXRECT$6);
        }
    }
    
    public STTrueFalse.Enum getFillok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.FILLOK$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFillok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.FILLOK$8);
        }
    }
    
    public boolean isSetFillok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.FILLOK$8) != null;
        }
    }
    
    public void setFillok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.FILLOK$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.FILLOK$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFillok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.FILLOK$8);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.FILLOK$8);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFillok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.FILLOK$8);
        }
    }
    
    public STTrueFalse.Enum getStrokeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.STROKEOK$10);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetStrokeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.STROKEOK$10);
        }
    }
    
    public boolean isSetStrokeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.STROKEOK$10) != null;
        }
    }
    
    public void setStrokeok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.STROKEOK$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.STROKEOK$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStrokeok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.STROKEOK$10);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.STROKEOK$10);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetStrokeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.STROKEOK$10);
        }
    }
    
    public STTrueFalse.Enum getShadowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.SHADOWOK$12);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetShadowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.SHADOWOK$12);
        }
    }
    
    public boolean isSetShadowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.SHADOWOK$12) != null;
        }
    }
    
    public void setShadowok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.SHADOWOK$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.SHADOWOK$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetShadowok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.SHADOWOK$12);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.SHADOWOK$12);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetShadowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.SHADOWOK$12);
        }
    }
    
    public STTrueFalse.Enum getArrowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.ARROWOK$14);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetArrowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.ARROWOK$14);
        }
    }
    
    public boolean isSetArrowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.ARROWOK$14) != null;
        }
    }
    
    public void setArrowok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.ARROWOK$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.ARROWOK$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetArrowok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.ARROWOK$14);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.ARROWOK$14);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetArrowok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.ARROWOK$14);
        }
    }
    
    public STTrueFalse.Enum getGradientshapeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetGradientshapeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
        }
    }
    
    public boolean isSetGradientshapeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16) != null;
        }
    }
    
    public void setGradientshapeok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetGradientshapeok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.GRADIENTSHAPEOK$16);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetGradientshapeok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.GRADIENTSHAPEOK$16);
        }
    }
    
    public STTrueFalse.Enum getTextpathok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.TEXTPATHOK$18);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetTextpathok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.TEXTPATHOK$18);
        }
    }
    
    public boolean isSetTextpathok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.TEXTPATHOK$18) != null;
        }
    }
    
    public void setTextpathok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.TEXTPATHOK$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.TEXTPATHOK$18);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTextpathok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.TEXTPATHOK$18);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.TEXTPATHOK$18);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetTextpathok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.TEXTPATHOK$18);
        }
    }
    
    public STTrueFalse.Enum getInsetpenok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.INSETPENOK$20);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInsetpenok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.INSETPENOK$20);
        }
    }
    
    public boolean isSetInsetpenok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.INSETPENOK$20) != null;
        }
    }
    
    public void setInsetpenok(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.INSETPENOK$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.INSETPENOK$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetpenok(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.INSETPENOK$20);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.INSETPENOK$20);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInsetpenok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.INSETPENOK$20);
        }
    }
    
    public STConnectType.Enum getConnecttype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTTYPE$22);
            if (simpleValue == null) {
                return null;
            }
            return (STConnectType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STConnectType xgetConnecttype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STConnectType)this.get_store().find_attribute_user(CTPathImpl.CONNECTTYPE$22);
        }
    }
    
    public boolean isSetConnecttype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.CONNECTTYPE$22) != null;
        }
    }
    
    public void setConnecttype(final STConnectType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTTYPE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.CONNECTTYPE$22);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetConnecttype(final STConnectType stConnectType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STConnectType stConnectType2 = (STConnectType)this.get_store().find_attribute_user(CTPathImpl.CONNECTTYPE$22);
            if (stConnectType2 == null) {
                stConnectType2 = (STConnectType)this.get_store().add_attribute_user(CTPathImpl.CONNECTTYPE$22);
            }
            stConnectType2.set((XmlObject)stConnectType);
        }
    }
    
    public void unsetConnecttype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.CONNECTTYPE$22);
        }
    }
    
    public String getConnectlocs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTLOCS$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetConnectlocs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.CONNECTLOCS$24);
        }
    }
    
    public boolean isSetConnectlocs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.CONNECTLOCS$24) != null;
        }
    }
    
    public void setConnectlocs(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTLOCS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.CONNECTLOCS$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetConnectlocs(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.CONNECTLOCS$24);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.CONNECTLOCS$24);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetConnectlocs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.CONNECTLOCS$24);
        }
    }
    
    public String getConnectangles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTANGLES$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetConnectangles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTPathImpl.CONNECTANGLES$26);
        }
    }
    
    public boolean isSetConnectangles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.CONNECTANGLES$26) != null;
        }
    }
    
    public void setConnectangles(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.CONNECTANGLES$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.CONNECTANGLES$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetConnectangles(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPathImpl.CONNECTANGLES$26);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPathImpl.CONNECTANGLES$26);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetConnectangles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.CONNECTANGLES$26);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getExtrusionok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.EXTRUSIONOK$28);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetExtrusionok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.EXTRUSIONOK$28);
        }
    }
    
    public boolean isSetExtrusionok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathImpl.EXTRUSIONOK$28) != null;
        }
    }
    
    public void setExtrusionok(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathImpl.EXTRUSIONOK$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathImpl.EXTRUSIONOK$28);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetExtrusionok(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTPathImpl.EXTRUSIONOK$28);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTPathImpl.EXTRUSIONOK$28);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetExtrusionok() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathImpl.EXTRUSIONOK$28);
        }
    }
    
    static {
        ID$0 = new QName("", "id");
        V$2 = new QName("", "v");
        LIMO$4 = new QName("", "limo");
        TEXTBOXRECT$6 = new QName("", "textboxrect");
        FILLOK$8 = new QName("", "fillok");
        STROKEOK$10 = new QName("", "strokeok");
        SHADOWOK$12 = new QName("", "shadowok");
        ARROWOK$14 = new QName("", "arrowok");
        GRADIENTSHAPEOK$16 = new QName("", "gradientshapeok");
        TEXTPATHOK$18 = new QName("", "textpathok");
        INSETPENOK$20 = new QName("", "insetpenok");
        CONNECTTYPE$22 = new QName("urn:schemas-microsoft-com:office:office", "connecttype");
        CONNECTLOCS$24 = new QName("urn:schemas-microsoft-com:office:office", "connectlocs");
        CONNECTANGLES$26 = new QName("urn:schemas-microsoft-com:office:office", "connectangles");
        EXTRUSIONOK$28 = new QName("urn:schemas-microsoft-com:office:office", "extrusionok");
    }
}
