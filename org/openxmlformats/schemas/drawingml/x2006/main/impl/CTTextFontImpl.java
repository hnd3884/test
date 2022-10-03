package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlByte;
import org.openxmlformats.schemas.drawingml.x2006.main.STPanose;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTypeface;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextFontImpl extends XmlComplexContentImpl implements CTTextFont
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPEFACE$0;
    private static final QName PANOSE$2;
    private static final QName PITCHFAMILY$4;
    private static final QName CHARSET$6;
    
    public CTTextFontImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getTypeface() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.TYPEFACE$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STTextTypeface xgetTypeface() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextTypeface)this.get_store().find_attribute_user(CTTextFontImpl.TYPEFACE$0);
        }
    }
    
    public boolean isSetTypeface() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextFontImpl.TYPEFACE$0) != null;
        }
    }
    
    public void setTypeface(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.TYPEFACE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFontImpl.TYPEFACE$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTypeface(final STTextTypeface stTextTypeface) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextTypeface stTextTypeface2 = (STTextTypeface)this.get_store().find_attribute_user(CTTextFontImpl.TYPEFACE$0);
            if (stTextTypeface2 == null) {
                stTextTypeface2 = (STTextTypeface)this.get_store().add_attribute_user(CTTextFontImpl.TYPEFACE$0);
            }
            stTextTypeface2.set((XmlObject)stTextTypeface);
        }
    }
    
    public void unsetTypeface() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextFontImpl.TYPEFACE$0);
        }
    }
    
    public byte[] getPanose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.PANOSE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STPanose xgetPanose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPanose)this.get_store().find_attribute_user(CTTextFontImpl.PANOSE$2);
        }
    }
    
    public boolean isSetPanose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextFontImpl.PANOSE$2) != null;
        }
    }
    
    public void setPanose(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.PANOSE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFontImpl.PANOSE$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetPanose(final STPanose stPanose) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPanose stPanose2 = (STPanose)this.get_store().find_attribute_user(CTTextFontImpl.PANOSE$2);
            if (stPanose2 == null) {
                stPanose2 = (STPanose)this.get_store().add_attribute_user(CTTextFontImpl.PANOSE$2);
            }
            stPanose2.set((XmlObject)stPanose);
        }
    }
    
    public void unsetPanose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextFontImpl.PANOSE$2);
        }
    }
    
    public byte getPitchFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextFontImpl.PITCHFAMILY$4);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getByteValue();
        }
    }
    
    public XmlByte xgetPitchFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlByte xmlByte = (XmlByte)this.get_store().find_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            if (xmlByte == null) {
                xmlByte = (XmlByte)this.get_default_attribute_value(CTTextFontImpl.PITCHFAMILY$4);
            }
            return xmlByte;
        }
    }
    
    public boolean isSetPitchFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextFontImpl.PITCHFAMILY$4) != null;
        }
    }
    
    public void setPitchFamily(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetPitchFamily(final XmlByte xmlByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlByte xmlByte2 = (XmlByte)this.get_store().find_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            if (xmlByte2 == null) {
                xmlByte2 = (XmlByte)this.get_store().add_attribute_user(CTTextFontImpl.PITCHFAMILY$4);
            }
            xmlByte2.set((XmlObject)xmlByte);
        }
    }
    
    public void unsetPitchFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextFontImpl.PITCHFAMILY$4);
        }
    }
    
    public byte getCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.CHARSET$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextFontImpl.CHARSET$6);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getByteValue();
        }
    }
    
    public XmlByte xgetCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlByte xmlByte = (XmlByte)this.get_store().find_attribute_user(CTTextFontImpl.CHARSET$6);
            if (xmlByte == null) {
                xmlByte = (XmlByte)this.get_default_attribute_value(CTTextFontImpl.CHARSET$6);
            }
            return xmlByte;
        }
    }
    
    public boolean isSetCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextFontImpl.CHARSET$6) != null;
        }
    }
    
    public void setCharset(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFontImpl.CHARSET$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFontImpl.CHARSET$6);
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetCharset(final XmlByte xmlByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlByte xmlByte2 = (XmlByte)this.get_store().find_attribute_user(CTTextFontImpl.CHARSET$6);
            if (xmlByte2 == null) {
                xmlByte2 = (XmlByte)this.get_store().add_attribute_user(CTTextFontImpl.CHARSET$6);
            }
            xmlByte2.set((XmlObject)xmlByte);
        }
    }
    
    public void unsetCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextFontImpl.CHARSET$6);
        }
    }
    
    static {
        TYPEFACE$0 = new QName("", "typeface");
        PANOSE$2 = new QName("", "panose");
        PITCHFAMILY$4 = new QName("", "pitchFamily");
        CHARSET$6 = new QName("", "charset");
    }
}
