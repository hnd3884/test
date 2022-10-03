package org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.impl;

import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTCf;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STClsid;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVstream;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STError;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STCy;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlUnsignedLong;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.XmlByte;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTNull;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTEmpty;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTArray;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVariant;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTVariantImpl extends XmlComplexContentImpl implements CTVariant
{
    private static final long serialVersionUID = 1L;
    private static final QName VARIANT$0;
    private static final QName VECTOR$2;
    private static final QName ARRAY$4;
    private static final QName BLOB$6;
    private static final QName OBLOB$8;
    private static final QName EMPTY$10;
    private static final QName NULL$12;
    private static final QName I1$14;
    private static final QName I2$16;
    private static final QName I4$18;
    private static final QName I8$20;
    private static final QName INT$22;
    private static final QName UI1$24;
    private static final QName UI2$26;
    private static final QName UI4$28;
    private static final QName UI8$30;
    private static final QName UINT$32;
    private static final QName R4$34;
    private static final QName R8$36;
    private static final QName DECIMAL$38;
    private static final QName LPSTR$40;
    private static final QName LPWSTR$42;
    private static final QName BSTR$44;
    private static final QName DATE$46;
    private static final QName FILETIME$48;
    private static final QName BOOL$50;
    private static final QName CY$52;
    private static final QName ERROR$54;
    private static final QName STREAM$56;
    private static final QName OSTREAM$58;
    private static final QName STORAGE$60;
    private static final QName OSTORAGE$62;
    private static final QName VSTREAM$64;
    private static final QName CLSID$66;
    private static final QName CF$68;
    
    public CTVariantImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTVariant getVariant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVariant ctVariant = (CTVariant)this.get_store().find_element_user(CTVariantImpl.VARIANT$0, 0);
            if (ctVariant == null) {
                return null;
            }
            return ctVariant;
        }
    }
    
    public boolean isSetVariant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.VARIANT$0) != 0;
        }
    }
    
    public void setVariant(final CTVariant ctVariant) {
        this.generatedSetterHelperImpl((XmlObject)ctVariant, CTVariantImpl.VARIANT$0, 0, (short)1);
    }
    
    public CTVariant addNewVariant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVariant)this.get_store().add_element_user(CTVariantImpl.VARIANT$0);
        }
    }
    
    public void unsetVariant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.VARIANT$0, 0);
        }
    }
    
    public CTVector getVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVector ctVector = (CTVector)this.get_store().find_element_user(CTVariantImpl.VECTOR$2, 0);
            if (ctVector == null) {
                return null;
            }
            return ctVector;
        }
    }
    
    public boolean isSetVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.VECTOR$2) != 0;
        }
    }
    
    public void setVector(final CTVector ctVector) {
        this.generatedSetterHelperImpl((XmlObject)ctVector, CTVariantImpl.VECTOR$2, 0, (short)1);
    }
    
    public CTVector addNewVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVector)this.get_store().add_element_user(CTVariantImpl.VECTOR$2);
        }
    }
    
    public void unsetVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.VECTOR$2, 0);
        }
    }
    
    public CTArray getArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTArray ctArray = (CTArray)this.get_store().find_element_user(CTVariantImpl.ARRAY$4, 0);
            if (ctArray == null) {
                return null;
            }
            return ctArray;
        }
    }
    
    public boolean isSetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.ARRAY$4) != 0;
        }
    }
    
    public void setArray(final CTArray ctArray) {
        this.generatedSetterHelperImpl((XmlObject)ctArray, CTVariantImpl.ARRAY$4, 0, (short)1);
    }
    
    public CTArray addNewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTArray)this.get_store().add_element_user(CTVariantImpl.ARRAY$4);
        }
    }
    
    public void unsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.ARRAY$4, 0);
        }
    }
    
    public byte[] getBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BLOB$6, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.BLOB$6, 0);
        }
    }
    
    public boolean isSetBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.BLOB$6) != 0;
        }
    }
    
    public void setBlob(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BLOB$6, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.BLOB$6);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetBlob(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.BLOB$6, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.BLOB$6);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.BLOB$6, 0);
        }
    }
    
    public byte[] getOblob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OBLOB$8, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetOblob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OBLOB$8, 0);
        }
    }
    
    public boolean isSetOblob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.OBLOB$8) != 0;
        }
    }
    
    public void setOblob(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OBLOB$8, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.OBLOB$8);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetOblob(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OBLOB$8, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.OBLOB$8);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetOblob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.OBLOB$8, 0);
        }
    }
    
    public CTEmpty getEmpty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTVariantImpl.EMPTY$10, 0);
            if (ctEmpty == null) {
                return null;
            }
            return ctEmpty;
        }
    }
    
    public boolean isSetEmpty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.EMPTY$10) != 0;
        }
    }
    
    public void setEmpty(final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTVariantImpl.EMPTY$10, 0, (short)1);
    }
    
    public CTEmpty addNewEmpty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTVariantImpl.EMPTY$10);
        }
    }
    
    public void unsetEmpty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.EMPTY$10, 0);
        }
    }
    
    public CTNull getNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNull ctNull = (CTNull)this.get_store().find_element_user(CTVariantImpl.NULL$12, 0);
            if (ctNull == null) {
                return null;
            }
            return ctNull;
        }
    }
    
    public boolean isSetNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.NULL$12) != 0;
        }
    }
    
    public void setNull(final CTNull ctNull) {
        this.generatedSetterHelperImpl((XmlObject)ctNull, CTVariantImpl.NULL$12, 0, (short)1);
    }
    
    public CTNull addNewNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNull)this.get_store().add_element_user(CTVariantImpl.NULL$12);
        }
    }
    
    public void unsetNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.NULL$12, 0);
        }
    }
    
    public byte getI1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I1$14, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getByteValue();
        }
    }
    
    public XmlByte xgetI1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlByte)this.get_store().find_element_user(CTVariantImpl.I1$14, 0);
        }
    }
    
    public boolean isSetI1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.I1$14) != 0;
        }
    }
    
    public void setI1(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I1$14, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.I1$14);
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetI1(final XmlByte xmlByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlByte xmlByte2 = (XmlByte)this.get_store().find_element_user(CTVariantImpl.I1$14, 0);
            if (xmlByte2 == null) {
                xmlByte2 = (XmlByte)this.get_store().add_element_user(CTVariantImpl.I1$14);
            }
            xmlByte2.set((XmlObject)xmlByte);
        }
    }
    
    public void unsetI1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.I1$14, 0);
        }
    }
    
    public short getI2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I2$16, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlShort xgetI2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlShort)this.get_store().find_element_user(CTVariantImpl.I2$16, 0);
        }
    }
    
    public boolean isSetI2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.I2$16) != 0;
        }
    }
    
    public void setI2(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I2$16, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.I2$16);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetI2(final XmlShort xmlShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlShort xmlShort2 = (XmlShort)this.get_store().find_element_user(CTVariantImpl.I2$16, 0);
            if (xmlShort2 == null) {
                xmlShort2 = (XmlShort)this.get_store().add_element_user(CTVariantImpl.I2$16);
            }
            xmlShort2.set((XmlObject)xmlShort);
        }
    }
    
    public void unsetI2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.I2$16, 0);
        }
    }
    
    public int getI4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I4$18, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetI4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTVariantImpl.I4$18, 0);
        }
    }
    
    public boolean isSetI4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.I4$18) != 0;
        }
    }
    
    public void setI4(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I4$18, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.I4$18);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetI4(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTVariantImpl.I4$18, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTVariantImpl.I4$18);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetI4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.I4$18, 0);
        }
    }
    
    public long getI8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I8$20, 0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlLong xgetI8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlLong)this.get_store().find_element_user(CTVariantImpl.I8$20, 0);
        }
    }
    
    public boolean isSetI8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.I8$20) != 0;
        }
    }
    
    public void setI8(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.I8$20, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.I8$20);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetI8(final XmlLong xmlLong) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlLong xmlLong2 = (XmlLong)this.get_store().find_element_user(CTVariantImpl.I8$20, 0);
            if (xmlLong2 == null) {
                xmlLong2 = (XmlLong)this.get_store().add_element_user(CTVariantImpl.I8$20);
            }
            xmlLong2.set((XmlObject)xmlLong);
        }
    }
    
    public void unsetI8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.I8$20, 0);
        }
    }
    
    public int getInt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.INT$22, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetInt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTVariantImpl.INT$22, 0);
        }
    }
    
    public boolean isSetInt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.INT$22) != 0;
        }
    }
    
    public void setInt(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.INT$22, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.INT$22);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetInt(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTVariantImpl.INT$22, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTVariantImpl.INT$22);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetInt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.INT$22, 0);
        }
    }
    
    public short getUi1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI1$24, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetUi1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedByte)this.get_store().find_element_user(CTVariantImpl.UI1$24, 0);
        }
    }
    
    public boolean isSetUi1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.UI1$24) != 0;
        }
    }
    
    public void setUi1(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI1$24, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.UI1$24);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetUi1(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_element_user(CTVariantImpl.UI1$24, 0);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_element_user(CTVariantImpl.UI1$24);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetUi1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.UI1$24, 0);
        }
    }
    
    public int getUi2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI2$26, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlUnsignedShort xgetUi2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().find_element_user(CTVariantImpl.UI2$26, 0);
        }
    }
    
    public boolean isSetUi2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.UI2$26) != 0;
        }
    }
    
    public void setUi2(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI2$26, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.UI2$26);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetUi2(final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_element_user(CTVariantImpl.UI2$26, 0);
            if (xmlUnsignedShort2 == null) {
                xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().add_element_user(CTVariantImpl.UI2$26);
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void unsetUi2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.UI2$26, 0);
        }
    }
    
    public long getUi4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI4$28, 0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetUi4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_element_user(CTVariantImpl.UI4$28, 0);
        }
    }
    
    public boolean isSetUi4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.UI4$28) != 0;
        }
    }
    
    public void setUi4(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI4$28, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.UI4$28);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetUi4(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_element_user(CTVariantImpl.UI4$28, 0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_element_user(CTVariantImpl.UI4$28);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetUi4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.UI4$28, 0);
        }
    }
    
    public BigInteger getUi8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI8$30, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlUnsignedLong xgetUi8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedLong)this.get_store().find_element_user(CTVariantImpl.UI8$30, 0);
        }
    }
    
    public boolean isSetUi8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.UI8$30) != 0;
        }
    }
    
    public void setUi8(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UI8$30, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.UI8$30);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetUi8(final XmlUnsignedLong xmlUnsignedLong) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedLong xmlUnsignedLong2 = (XmlUnsignedLong)this.get_store().find_element_user(CTVariantImpl.UI8$30, 0);
            if (xmlUnsignedLong2 == null) {
                xmlUnsignedLong2 = (XmlUnsignedLong)this.get_store().add_element_user(CTVariantImpl.UI8$30);
            }
            xmlUnsignedLong2.set((XmlObject)xmlUnsignedLong);
        }
    }
    
    public void unsetUi8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.UI8$30, 0);
        }
    }
    
    public long getUint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UINT$32, 0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetUint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_element_user(CTVariantImpl.UINT$32, 0);
        }
    }
    
    public boolean isSetUint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.UINT$32) != 0;
        }
    }
    
    public void setUint(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.UINT$32, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.UINT$32);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetUint(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_element_user(CTVariantImpl.UINT$32, 0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_element_user(CTVariantImpl.UINT$32);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetUint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.UINT$32, 0);
        }
    }
    
    public float getR4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.R4$34, 0);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetR4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_element_user(CTVariantImpl.R4$34, 0);
        }
    }
    
    public boolean isSetR4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.R4$34) != 0;
        }
    }
    
    public void setR4(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.R4$34, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.R4$34);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetR4(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_element_user(CTVariantImpl.R4$34, 0);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_element_user(CTVariantImpl.R4$34);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetR4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.R4$34, 0);
        }
    }
    
    public double getR8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.R8$36, 0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetR8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_element_user(CTVariantImpl.R8$36, 0);
        }
    }
    
    public boolean isSetR8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.R8$36) != 0;
        }
    }
    
    public void setR8(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.R8$36, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.R8$36);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetR8(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_element_user(CTVariantImpl.R8$36, 0);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_element_user(CTVariantImpl.R8$36);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetR8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.R8$36, 0);
        }
    }
    
    public BigDecimal getDecimal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.DECIMAL$38, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigDecimalValue();
        }
    }
    
    public XmlDecimal xgetDecimal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDecimal)this.get_store().find_element_user(CTVariantImpl.DECIMAL$38, 0);
        }
    }
    
    public boolean isSetDecimal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.DECIMAL$38) != 0;
        }
    }
    
    public void setDecimal(final BigDecimal bigDecimalValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.DECIMAL$38, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.DECIMAL$38);
            }
            simpleValue.setBigDecimalValue(bigDecimalValue);
        }
    }
    
    public void xsetDecimal(final XmlDecimal xmlDecimal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDecimal xmlDecimal2 = (XmlDecimal)this.get_store().find_element_user(CTVariantImpl.DECIMAL$38, 0);
            if (xmlDecimal2 == null) {
                xmlDecimal2 = (XmlDecimal)this.get_store().add_element_user(CTVariantImpl.DECIMAL$38);
            }
            xmlDecimal2.set((XmlObject)xmlDecimal);
        }
    }
    
    public void unsetDecimal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.DECIMAL$38, 0);
        }
    }
    
    public String getLpstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.LPSTR$40, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetLpstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTVariantImpl.LPSTR$40, 0);
        }
    }
    
    public boolean isSetLpstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.LPSTR$40) != 0;
        }
    }
    
    public void setLpstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.LPSTR$40, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.LPSTR$40);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLpstr(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVariantImpl.LPSTR$40, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTVariantImpl.LPSTR$40);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetLpstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.LPSTR$40, 0);
        }
    }
    
    public String getLpwstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.LPWSTR$42, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetLpwstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTVariantImpl.LPWSTR$42, 0);
        }
    }
    
    public boolean isSetLpwstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.LPWSTR$42) != 0;
        }
    }
    
    public void setLpwstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.LPWSTR$42, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.LPWSTR$42);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLpwstr(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVariantImpl.LPWSTR$42, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTVariantImpl.LPWSTR$42);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetLpwstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.LPWSTR$42, 0);
        }
    }
    
    public String getBstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BSTR$44, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTVariantImpl.BSTR$44, 0);
        }
    }
    
    public boolean isSetBstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.BSTR$44) != 0;
        }
    }
    
    public void setBstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BSTR$44, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.BSTR$44);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBstr(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVariantImpl.BSTR$44, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTVariantImpl.BSTR$44);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.BSTR$44, 0);
        }
    }
    
    public Calendar getDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.DATE$46, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_element_user(CTVariantImpl.DATE$46, 0);
        }
    }
    
    public boolean isSetDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.DATE$46) != 0;
        }
    }
    
    public void setDate(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.DATE$46, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.DATE$46);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetDate(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(CTVariantImpl.DATE$46, 0);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_element_user(CTVariantImpl.DATE$46);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.DATE$46, 0);
        }
    }
    
    public Calendar getFiletime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.FILETIME$48, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetFiletime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_element_user(CTVariantImpl.FILETIME$48, 0);
        }
    }
    
    public boolean isSetFiletime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.FILETIME$48) != 0;
        }
    }
    
    public void setFiletime(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.FILETIME$48, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.FILETIME$48);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetFiletime(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(CTVariantImpl.FILETIME$48, 0);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_element_user(CTVariantImpl.FILETIME$48);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetFiletime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.FILETIME$48, 0);
        }
    }
    
    public boolean getBool() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BOOL$50, 0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBool() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_element_user(CTVariantImpl.BOOL$50, 0);
        }
    }
    
    public boolean isSetBool() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.BOOL$50) != 0;
        }
    }
    
    public void setBool(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.BOOL$50, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.BOOL$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBool(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTVariantImpl.BOOL$50, 0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_element_user(CTVariantImpl.BOOL$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBool() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.BOOL$50, 0);
        }
    }
    
    public String getCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.CY$52, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCy xgetCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCy)this.get_store().find_element_user(CTVariantImpl.CY$52, 0);
        }
    }
    
    public boolean isSetCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.CY$52) != 0;
        }
    }
    
    public void setCy(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.CY$52, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.CY$52);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCy(final STCy stCy) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCy stCy2 = (STCy)this.get_store().find_element_user(CTVariantImpl.CY$52, 0);
            if (stCy2 == null) {
                stCy2 = (STCy)this.get_store().add_element_user(CTVariantImpl.CY$52);
            }
            stCy2.set((XmlObject)stCy);
        }
    }
    
    public void unsetCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.CY$52, 0);
        }
    }
    
    public String getError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.ERROR$54, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STError xgetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STError)this.get_store().find_element_user(CTVariantImpl.ERROR$54, 0);
        }
    }
    
    public boolean isSetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.ERROR$54) != 0;
        }
    }
    
    public void setError(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.ERROR$54, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.ERROR$54);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetError(final STError stError) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STError stError2 = (STError)this.get_store().find_element_user(CTVariantImpl.ERROR$54, 0);
            if (stError2 == null) {
                stError2 = (STError)this.get_store().add_element_user(CTVariantImpl.ERROR$54);
            }
            stError2.set((XmlObject)stError);
        }
    }
    
    public void unsetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.ERROR$54, 0);
        }
    }
    
    public byte[] getStream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.STREAM$56, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetStream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.STREAM$56, 0);
        }
    }
    
    public boolean isSetStream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.STREAM$56) != 0;
        }
    }
    
    public void setStream(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.STREAM$56, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.STREAM$56);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetStream(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.STREAM$56, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.STREAM$56);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetStream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.STREAM$56, 0);
        }
    }
    
    public byte[] getOstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OSTREAM$58, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetOstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OSTREAM$58, 0);
        }
    }
    
    public boolean isSetOstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.OSTREAM$58) != 0;
        }
    }
    
    public void setOstream(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OSTREAM$58, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.OSTREAM$58);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetOstream(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OSTREAM$58, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.OSTREAM$58);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetOstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.OSTREAM$58, 0);
        }
    }
    
    public byte[] getStorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.STORAGE$60, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetStorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.STORAGE$60, 0);
        }
    }
    
    public boolean isSetStorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.STORAGE$60) != 0;
        }
    }
    
    public void setStorage(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.STORAGE$60, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.STORAGE$60);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetStorage(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.STORAGE$60, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.STORAGE$60);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetStorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.STORAGE$60, 0);
        }
    }
    
    public byte[] getOstorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OSTORAGE$62, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetOstorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OSTORAGE$62, 0);
        }
    }
    
    public boolean isSetOstorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.OSTORAGE$62) != 0;
        }
    }
    
    public void setOstorage(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.OSTORAGE$62, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.OSTORAGE$62);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetOstorage(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTVariantImpl.OSTORAGE$62, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTVariantImpl.OSTORAGE$62);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetOstorage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.OSTORAGE$62, 0);
        }
    }
    
    public CTVstream getVstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVstream ctVstream = (CTVstream)this.get_store().find_element_user(CTVariantImpl.VSTREAM$64, 0);
            if (ctVstream == null) {
                return null;
            }
            return ctVstream;
        }
    }
    
    public boolean isSetVstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.VSTREAM$64) != 0;
        }
    }
    
    public void setVstream(final CTVstream ctVstream) {
        this.generatedSetterHelperImpl((XmlObject)ctVstream, CTVariantImpl.VSTREAM$64, 0, (short)1);
    }
    
    public CTVstream addNewVstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVstream)this.get_store().add_element_user(CTVariantImpl.VSTREAM$64);
        }
    }
    
    public void unsetVstream() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.VSTREAM$64, 0);
        }
    }
    
    public String getClsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.CLSID$66, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STClsid xgetClsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STClsid)this.get_store().find_element_user(CTVariantImpl.CLSID$66, 0);
        }
    }
    
    public boolean isSetClsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.CLSID$66) != 0;
        }
    }
    
    public void setClsid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVariantImpl.CLSID$66, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTVariantImpl.CLSID$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetClsid(final STClsid stClsid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STClsid stClsid2 = (STClsid)this.get_store().find_element_user(CTVariantImpl.CLSID$66, 0);
            if (stClsid2 == null) {
                stClsid2 = (STClsid)this.get_store().add_element_user(CTVariantImpl.CLSID$66);
            }
            stClsid2.set((XmlObject)stClsid);
        }
    }
    
    public void unsetClsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.CLSID$66, 0);
        }
    }
    
    public CTCf getCf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCf ctCf = (CTCf)this.get_store().find_element_user(CTVariantImpl.CF$68, 0);
            if (ctCf == null) {
                return null;
            }
            return ctCf;
        }
    }
    
    public boolean isSetCf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVariantImpl.CF$68) != 0;
        }
    }
    
    public void setCf(final CTCf ctCf) {
        this.generatedSetterHelperImpl((XmlObject)ctCf, CTVariantImpl.CF$68, 0, (short)1);
    }
    
    public CTCf addNewCf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCf)this.get_store().add_element_user(CTVariantImpl.CF$68);
        }
    }
    
    public void unsetCf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVariantImpl.CF$68, 0);
        }
    }
    
    static {
        VARIANT$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "variant");
        VECTOR$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "vector");
        ARRAY$4 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "array");
        BLOB$6 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "blob");
        OBLOB$8 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "oblob");
        EMPTY$10 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "empty");
        NULL$12 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "null");
        I1$14 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i1");
        I2$16 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i2");
        I4$18 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i4");
        I8$20 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i8");
        INT$22 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "int");
        UI1$24 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui1");
        UI2$26 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui2");
        UI4$28 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui4");
        UI8$30 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui8");
        UINT$32 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "uint");
        R4$34 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "r4");
        R8$36 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "r8");
        DECIMAL$38 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "decimal");
        LPSTR$40 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "lpstr");
        LPWSTR$42 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "lpwstr");
        BSTR$44 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "bstr");
        DATE$46 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "date");
        FILETIME$48 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "filetime");
        BOOL$50 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "bool");
        CY$52 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "cy");
        ERROR$54 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "error");
        STREAM$56 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "stream");
        OSTREAM$58 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ostream");
        STORAGE$60 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "storage");
        OSTORAGE$62 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ostorage");
        VSTREAM$64 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "vstream");
        CLSID$66 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "clsid");
        CF$68 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "cf");
    }
}
