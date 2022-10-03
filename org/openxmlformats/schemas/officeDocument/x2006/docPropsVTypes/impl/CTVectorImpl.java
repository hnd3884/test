package org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STVectorBaseType;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTCf;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STClsid;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STError;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STCy;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlString;
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
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVariant;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTVectorImpl extends XmlComplexContentImpl implements CTVector
{
    private static final long serialVersionUID = 1L;
    private static final QName VARIANT$0;
    private static final QName I1$2;
    private static final QName I2$4;
    private static final QName I4$6;
    private static final QName I8$8;
    private static final QName UI1$10;
    private static final QName UI2$12;
    private static final QName UI4$14;
    private static final QName UI8$16;
    private static final QName R4$18;
    private static final QName R8$20;
    private static final QName LPSTR$22;
    private static final QName LPWSTR$24;
    private static final QName BSTR$26;
    private static final QName DATE$28;
    private static final QName FILETIME$30;
    private static final QName BOOL$32;
    private static final QName CY$34;
    private static final QName ERROR$36;
    private static final QName CLSID$38;
    private static final QName CF$40;
    private static final QName BASETYPE$42;
    private static final QName SIZE$44;
    
    public CTVectorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTVariant> getVariantList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VariantList extends AbstractList<CTVariant>
            {
                @Override
                public CTVariant get(final int n) {
                    return CTVectorImpl.this.getVariantArray(n);
                }
                
                @Override
                public CTVariant set(final int n, final CTVariant ctVariant) {
                    final CTVariant variantArray = CTVectorImpl.this.getVariantArray(n);
                    CTVectorImpl.this.setVariantArray(n, ctVariant);
                    return variantArray;
                }
                
                @Override
                public void add(final int n, final CTVariant ctVariant) {
                    CTVectorImpl.this.insertNewVariant(n).set((XmlObject)ctVariant);
                }
                
                @Override
                public CTVariant remove(final int n) {
                    final CTVariant variantArray = CTVectorImpl.this.getVariantArray(n);
                    CTVectorImpl.this.removeVariant(n);
                    return variantArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfVariantArray();
                }
            }
            return new VariantList();
        }
    }
    
    @Deprecated
    public CTVariant[] getVariantArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.VARIANT$0, (List)list);
            final CTVariant[] array = new CTVariant[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTVariant getVariantArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVariant ctVariant = (CTVariant)this.get_store().find_element_user(CTVectorImpl.VARIANT$0, n);
            if (ctVariant == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctVariant;
        }
    }
    
    public int sizeOfVariantArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.VARIANT$0);
        }
    }
    
    public void setVariantArray(final CTVariant[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTVectorImpl.VARIANT$0);
    }
    
    public void setVariantArray(final int n, final CTVariant ctVariant) {
        this.generatedSetterHelperImpl((XmlObject)ctVariant, CTVectorImpl.VARIANT$0, n, (short)2);
    }
    
    public CTVariant insertNewVariant(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVariant)this.get_store().insert_element_user(CTVectorImpl.VARIANT$0, n);
        }
    }
    
    public CTVariant addNewVariant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVariant)this.get_store().add_element_user(CTVectorImpl.VARIANT$0);
        }
    }
    
    public void removeVariant(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.VARIANT$0, n);
        }
    }
    
    public List<Byte> getI1List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I1List extends AbstractList<Byte>
            {
                @Override
                public Byte get(final int n) {
                    return CTVectorImpl.this.getI1Array(n);
                }
                
                @Override
                public Byte set(final int n, final Byte b) {
                    final Byte value = CTVectorImpl.this.getI1Array(n);
                    CTVectorImpl.this.setI1Array(n, b);
                    return value;
                }
                
                @Override
                public void add(final int n, final Byte b) {
                    CTVectorImpl.this.insertI1(n, b);
                }
                
                @Override
                public Byte remove(final int n) {
                    final Byte value = CTVectorImpl.this.getI1Array(n);
                    CTVectorImpl.this.removeI1(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI1Array();
                }
            }
            return new I1List();
        }
    }
    
    @Deprecated
    public byte[] getI1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I1$2, (List)list);
            final byte[] array = new byte[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getByteValue();
            }
            return array;
        }
    }
    
    public byte getI1Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I1$2, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getByteValue();
        }
    }
    
    public List<XmlByte> xgetI1List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I1List extends AbstractList<XmlByte>
            {
                @Override
                public XmlByte get(final int n) {
                    return CTVectorImpl.this.xgetI1Array(n);
                }
                
                @Override
                public XmlByte set(final int n, final XmlByte xmlByte) {
                    final XmlByte xgetI1Array = CTVectorImpl.this.xgetI1Array(n);
                    CTVectorImpl.this.xsetI1Array(n, xmlByte);
                    return xgetI1Array;
                }
                
                @Override
                public void add(final int n, final XmlByte xmlByte) {
                    CTVectorImpl.this.insertNewI1(n).set((XmlObject)xmlByte);
                }
                
                @Override
                public XmlByte remove(final int n) {
                    final XmlByte xgetI1Array = CTVectorImpl.this.xgetI1Array(n);
                    CTVectorImpl.this.removeI1(n);
                    return xgetI1Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI1Array();
                }
            }
            return new I1List();
        }
    }
    
    @Deprecated
    public XmlByte[] xgetI1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I1$2, (List)list);
            final XmlByte[] array = new XmlByte[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlByte xgetI1Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlByte xmlByte = (XmlByte)this.get_store().find_element_user(CTVectorImpl.I1$2, n);
            if (xmlByte == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlByte;
        }
    }
    
    public int sizeOfI1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.I1$2);
        }
    }
    
    public void setI1Array(final byte[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.I1$2);
        }
    }
    
    public void setI1Array(final int n, final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I1$2, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetI1Array(final XmlByte[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.I1$2);
        }
    }
    
    public void xsetI1Array(final int n, final XmlByte xmlByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlByte xmlByte2 = (XmlByte)this.get_store().find_element_user(CTVectorImpl.I1$2, n);
            if (xmlByte2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlByte2.set((XmlObject)xmlByte);
        }
    }
    
    public void insertI1(final int n, final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.I1$2, n)).setByteValue(byteValue);
        }
    }
    
    public void addI1(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.I1$2)).setByteValue(byteValue);
        }
    }
    
    public XmlByte insertNewI1(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlByte)this.get_store().insert_element_user(CTVectorImpl.I1$2, n);
        }
    }
    
    public XmlByte addNewI1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlByte)this.get_store().add_element_user(CTVectorImpl.I1$2);
        }
    }
    
    public void removeI1(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.I1$2, n);
        }
    }
    
    public List<Short> getI2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I2List extends AbstractList<Short>
            {
                @Override
                public Short get(final int n) {
                    return CTVectorImpl.this.getI2Array(n);
                }
                
                @Override
                public Short set(final int n, final Short n2) {
                    final Short value = CTVectorImpl.this.getI2Array(n);
                    CTVectorImpl.this.setI2Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Short n2) {
                    CTVectorImpl.this.insertI2(n, n2);
                }
                
                @Override
                public Short remove(final int n) {
                    final Short value = CTVectorImpl.this.getI2Array(n);
                    CTVectorImpl.this.removeI2(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI2Array();
                }
            }
            return new I2List();
        }
    }
    
    @Deprecated
    public short[] getI2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I2$4, (List)list);
            final short[] array = new short[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getShortValue();
            }
            return array;
        }
    }
    
    public short getI2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I2$4, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getShortValue();
        }
    }
    
    public List<XmlShort> xgetI2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I2List extends AbstractList<XmlShort>
            {
                @Override
                public XmlShort get(final int n) {
                    return CTVectorImpl.this.xgetI2Array(n);
                }
                
                @Override
                public XmlShort set(final int n, final XmlShort xmlShort) {
                    final XmlShort xgetI2Array = CTVectorImpl.this.xgetI2Array(n);
                    CTVectorImpl.this.xsetI2Array(n, xmlShort);
                    return xgetI2Array;
                }
                
                @Override
                public void add(final int n, final XmlShort xmlShort) {
                    CTVectorImpl.this.insertNewI2(n).set((XmlObject)xmlShort);
                }
                
                @Override
                public XmlShort remove(final int n) {
                    final XmlShort xgetI2Array = CTVectorImpl.this.xgetI2Array(n);
                    CTVectorImpl.this.removeI2(n);
                    return xgetI2Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI2Array();
                }
            }
            return new I2List();
        }
    }
    
    @Deprecated
    public XmlShort[] xgetI2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I2$4, (List)list);
            final XmlShort[] array = new XmlShort[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlShort xgetI2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlShort xmlShort = (XmlShort)this.get_store().find_element_user(CTVectorImpl.I2$4, n);
            if (xmlShort == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlShort;
        }
    }
    
    public int sizeOfI2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.I2$4);
        }
    }
    
    public void setI2Array(final short[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.I2$4);
        }
    }
    
    public void setI2Array(final int n, final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I2$4, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetI2Array(final XmlShort[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.I2$4);
        }
    }
    
    public void xsetI2Array(final int n, final XmlShort xmlShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlShort xmlShort2 = (XmlShort)this.get_store().find_element_user(CTVectorImpl.I2$4, n);
            if (xmlShort2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlShort2.set((XmlObject)xmlShort);
        }
    }
    
    public void insertI2(final int n, final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.I2$4, n)).setShortValue(shortValue);
        }
    }
    
    public void addI2(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.I2$4)).setShortValue(shortValue);
        }
    }
    
    public XmlShort insertNewI2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlShort)this.get_store().insert_element_user(CTVectorImpl.I2$4, n);
        }
    }
    
    public XmlShort addNewI2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlShort)this.get_store().add_element_user(CTVectorImpl.I2$4);
        }
    }
    
    public void removeI2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.I2$4, n);
        }
    }
    
    public List<Integer> getI4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I4List extends AbstractList<Integer>
            {
                @Override
                public Integer get(final int n) {
                    return CTVectorImpl.this.getI4Array(n);
                }
                
                @Override
                public Integer set(final int n, final Integer n2) {
                    final Integer value = CTVectorImpl.this.getI4Array(n);
                    CTVectorImpl.this.setI4Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Integer n2) {
                    CTVectorImpl.this.insertI4(n, n2);
                }
                
                @Override
                public Integer remove(final int n) {
                    final Integer value = CTVectorImpl.this.getI4Array(n);
                    CTVectorImpl.this.removeI4(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI4Array();
                }
            }
            return new I4List();
        }
    }
    
    @Deprecated
    public int[] getI4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I4$6, (List)list);
            final int[] array = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getIntValue();
            }
            return array;
        }
    }
    
    public int getI4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I4$6, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getIntValue();
        }
    }
    
    public List<XmlInt> xgetI4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I4List extends AbstractList<XmlInt>
            {
                @Override
                public XmlInt get(final int n) {
                    return CTVectorImpl.this.xgetI4Array(n);
                }
                
                @Override
                public XmlInt set(final int n, final XmlInt xmlInt) {
                    final XmlInt xgetI4Array = CTVectorImpl.this.xgetI4Array(n);
                    CTVectorImpl.this.xsetI4Array(n, xmlInt);
                    return xgetI4Array;
                }
                
                @Override
                public void add(final int n, final XmlInt xmlInt) {
                    CTVectorImpl.this.insertNewI4(n).set((XmlObject)xmlInt);
                }
                
                @Override
                public XmlInt remove(final int n) {
                    final XmlInt xgetI4Array = CTVectorImpl.this.xgetI4Array(n);
                    CTVectorImpl.this.removeI4(n);
                    return xgetI4Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI4Array();
                }
            }
            return new I4List();
        }
    }
    
    @Deprecated
    public XmlInt[] xgetI4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I4$6, (List)list);
            final XmlInt[] array = new XmlInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlInt xgetI4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInt xmlInt = (XmlInt)this.get_store().find_element_user(CTVectorImpl.I4$6, n);
            if (xmlInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlInt;
        }
    }
    
    public int sizeOfI4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.I4$6);
        }
    }
    
    public void setI4Array(final int[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.I4$6);
        }
    }
    
    public void setI4Array(final int n, final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I4$6, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetI4Array(final XmlInt[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.I4$6);
        }
    }
    
    public void xsetI4Array(final int n, final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTVectorImpl.I4$6, n);
            if (xmlInt2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void insertI4(final int n, final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.I4$6, n)).setIntValue(intValue);
        }
    }
    
    public void addI4(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.I4$6)).setIntValue(intValue);
        }
    }
    
    public XmlInt insertNewI4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().insert_element_user(CTVectorImpl.I4$6, n);
        }
    }
    
    public XmlInt addNewI4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().add_element_user(CTVectorImpl.I4$6);
        }
    }
    
    public void removeI4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.I4$6, n);
        }
    }
    
    public List<Long> getI8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I8List extends AbstractList<Long>
            {
                @Override
                public Long get(final int n) {
                    return CTVectorImpl.this.getI8Array(n);
                }
                
                @Override
                public Long set(final int n, final Long n2) {
                    final Long value = CTVectorImpl.this.getI8Array(n);
                    CTVectorImpl.this.setI8Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Long n2) {
                    CTVectorImpl.this.insertI8(n, n2);
                }
                
                @Override
                public Long remove(final int n) {
                    final Long value = CTVectorImpl.this.getI8Array(n);
                    CTVectorImpl.this.removeI8(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI8Array();
                }
            }
            return new I8List();
        }
    }
    
    @Deprecated
    public long[] getI8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I8$8, (List)list);
            final long[] array = new long[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getLongValue();
            }
            return array;
        }
    }
    
    public long getI8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I8$8, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getLongValue();
        }
    }
    
    public List<XmlLong> xgetI8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class I8List extends AbstractList<XmlLong>
            {
                @Override
                public XmlLong get(final int n) {
                    return CTVectorImpl.this.xgetI8Array(n);
                }
                
                @Override
                public XmlLong set(final int n, final XmlLong xmlLong) {
                    final XmlLong xgetI8Array = CTVectorImpl.this.xgetI8Array(n);
                    CTVectorImpl.this.xsetI8Array(n, xmlLong);
                    return xgetI8Array;
                }
                
                @Override
                public void add(final int n, final XmlLong xmlLong) {
                    CTVectorImpl.this.insertNewI8(n).set((XmlObject)xmlLong);
                }
                
                @Override
                public XmlLong remove(final int n) {
                    final XmlLong xgetI8Array = CTVectorImpl.this.xgetI8Array(n);
                    CTVectorImpl.this.removeI8(n);
                    return xgetI8Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfI8Array();
                }
            }
            return new I8List();
        }
    }
    
    @Deprecated
    public XmlLong[] xgetI8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.I8$8, (List)list);
            final XmlLong[] array = new XmlLong[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlLong xgetI8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlLong xmlLong = (XmlLong)this.get_store().find_element_user(CTVectorImpl.I8$8, n);
            if (xmlLong == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlLong;
        }
    }
    
    public int sizeOfI8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.I8$8);
        }
    }
    
    public void setI8Array(final long[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.I8$8);
        }
    }
    
    public void setI8Array(final int n, final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.I8$8, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetI8Array(final XmlLong[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.I8$8);
        }
    }
    
    public void xsetI8Array(final int n, final XmlLong xmlLong) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlLong xmlLong2 = (XmlLong)this.get_store().find_element_user(CTVectorImpl.I8$8, n);
            if (xmlLong2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlLong2.set((XmlObject)xmlLong);
        }
    }
    
    public void insertI8(final int n, final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.I8$8, n)).setLongValue(longValue);
        }
    }
    
    public void addI8(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.I8$8)).setLongValue(longValue);
        }
    }
    
    public XmlLong insertNewI8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlLong)this.get_store().insert_element_user(CTVectorImpl.I8$8, n);
        }
    }
    
    public XmlLong addNewI8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlLong)this.get_store().add_element_user(CTVectorImpl.I8$8);
        }
    }
    
    public void removeI8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.I8$8, n);
        }
    }
    
    public List<Short> getUi1List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui1List extends AbstractList<Short>
            {
                @Override
                public Short get(final int n) {
                    return CTVectorImpl.this.getUi1Array(n);
                }
                
                @Override
                public Short set(final int n, final Short n2) {
                    final Short value = CTVectorImpl.this.getUi1Array(n);
                    CTVectorImpl.this.setUi1Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Short n2) {
                    CTVectorImpl.this.insertUi1(n, n2);
                }
                
                @Override
                public Short remove(final int n) {
                    final Short value = CTVectorImpl.this.getUi1Array(n);
                    CTVectorImpl.this.removeUi1(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi1Array();
                }
            }
            return new Ui1List();
        }
    }
    
    @Deprecated
    public short[] getUi1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI1$10, (List)list);
            final short[] array = new short[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getShortValue();
            }
            return array;
        }
    }
    
    public short getUi1Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI1$10, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getShortValue();
        }
    }
    
    public List<XmlUnsignedByte> xgetUi1List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui1List extends AbstractList<XmlUnsignedByte>
            {
                @Override
                public XmlUnsignedByte get(final int n) {
                    return CTVectorImpl.this.xgetUi1Array(n);
                }
                
                @Override
                public XmlUnsignedByte set(final int n, final XmlUnsignedByte xmlUnsignedByte) {
                    final XmlUnsignedByte xgetUi1Array = CTVectorImpl.this.xgetUi1Array(n);
                    CTVectorImpl.this.xsetUi1Array(n, xmlUnsignedByte);
                    return xgetUi1Array;
                }
                
                @Override
                public void add(final int n, final XmlUnsignedByte xmlUnsignedByte) {
                    CTVectorImpl.this.insertNewUi1(n).set((XmlObject)xmlUnsignedByte);
                }
                
                @Override
                public XmlUnsignedByte remove(final int n) {
                    final XmlUnsignedByte xgetUi1Array = CTVectorImpl.this.xgetUi1Array(n);
                    CTVectorImpl.this.removeUi1(n);
                    return xgetUi1Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi1Array();
                }
            }
            return new Ui1List();
        }
    }
    
    @Deprecated
    public XmlUnsignedByte[] xgetUi1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI1$10, (List)list);
            final XmlUnsignedByte[] array = new XmlUnsignedByte[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlUnsignedByte xgetUi1Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_element_user(CTVectorImpl.UI1$10, n);
            if (xmlUnsignedByte == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlUnsignedByte;
        }
    }
    
    public int sizeOfUi1Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.UI1$10);
        }
    }
    
    public void setUi1Array(final short[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.UI1$10);
        }
    }
    
    public void setUi1Array(final int n, final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI1$10, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetUi1Array(final XmlUnsignedByte[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.UI1$10);
        }
    }
    
    public void xsetUi1Array(final int n, final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_element_user(CTVectorImpl.UI1$10, n);
            if (xmlUnsignedByte2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void insertUi1(final int n, final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.UI1$10, n)).setShortValue(shortValue);
        }
    }
    
    public void addUi1(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.UI1$10)).setShortValue(shortValue);
        }
    }
    
    public XmlUnsignedByte insertNewUi1(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedByte)this.get_store().insert_element_user(CTVectorImpl.UI1$10, n);
        }
    }
    
    public XmlUnsignedByte addNewUi1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedByte)this.get_store().add_element_user(CTVectorImpl.UI1$10);
        }
    }
    
    public void removeUi1(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.UI1$10, n);
        }
    }
    
    public List<Integer> getUi2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui2List extends AbstractList<Integer>
            {
                @Override
                public Integer get(final int n) {
                    return CTVectorImpl.this.getUi2Array(n);
                }
                
                @Override
                public Integer set(final int n, final Integer n2) {
                    final Integer value = CTVectorImpl.this.getUi2Array(n);
                    CTVectorImpl.this.setUi2Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Integer n2) {
                    CTVectorImpl.this.insertUi2(n, n2);
                }
                
                @Override
                public Integer remove(final int n) {
                    final Integer value = CTVectorImpl.this.getUi2Array(n);
                    CTVectorImpl.this.removeUi2(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi2Array();
                }
            }
            return new Ui2List();
        }
    }
    
    @Deprecated
    public int[] getUi2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI2$12, (List)list);
            final int[] array = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getIntValue();
            }
            return array;
        }
    }
    
    public int getUi2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI2$12, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getIntValue();
        }
    }
    
    public List<XmlUnsignedShort> xgetUi2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui2List extends AbstractList<XmlUnsignedShort>
            {
                @Override
                public XmlUnsignedShort get(final int n) {
                    return CTVectorImpl.this.xgetUi2Array(n);
                }
                
                @Override
                public XmlUnsignedShort set(final int n, final XmlUnsignedShort xmlUnsignedShort) {
                    final XmlUnsignedShort xgetUi2Array = CTVectorImpl.this.xgetUi2Array(n);
                    CTVectorImpl.this.xsetUi2Array(n, xmlUnsignedShort);
                    return xgetUi2Array;
                }
                
                @Override
                public void add(final int n, final XmlUnsignedShort xmlUnsignedShort) {
                    CTVectorImpl.this.insertNewUi2(n).set((XmlObject)xmlUnsignedShort);
                }
                
                @Override
                public XmlUnsignedShort remove(final int n) {
                    final XmlUnsignedShort xgetUi2Array = CTVectorImpl.this.xgetUi2Array(n);
                    CTVectorImpl.this.removeUi2(n);
                    return xgetUi2Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi2Array();
                }
            }
            return new Ui2List();
        }
    }
    
    @Deprecated
    public XmlUnsignedShort[] xgetUi2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI2$12, (List)list);
            final XmlUnsignedShort[] array = new XmlUnsignedShort[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlUnsignedShort xgetUi2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedShort xmlUnsignedShort = (XmlUnsignedShort)this.get_store().find_element_user(CTVectorImpl.UI2$12, n);
            if (xmlUnsignedShort == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlUnsignedShort;
        }
    }
    
    public int sizeOfUi2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.UI2$12);
        }
    }
    
    public void setUi2Array(final int[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.UI2$12);
        }
    }
    
    public void setUi2Array(final int n, final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI2$12, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetUi2Array(final XmlUnsignedShort[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.UI2$12);
        }
    }
    
    public void xsetUi2Array(final int n, final XmlUnsignedShort xmlUnsignedShort) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedShort xmlUnsignedShort2 = (XmlUnsignedShort)this.get_store().find_element_user(CTVectorImpl.UI2$12, n);
            if (xmlUnsignedShort2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlUnsignedShort2.set((XmlObject)xmlUnsignedShort);
        }
    }
    
    public void insertUi2(final int n, final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.UI2$12, n)).setIntValue(intValue);
        }
    }
    
    public void addUi2(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.UI2$12)).setIntValue(intValue);
        }
    }
    
    public XmlUnsignedShort insertNewUi2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().insert_element_user(CTVectorImpl.UI2$12, n);
        }
    }
    
    public XmlUnsignedShort addNewUi2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedShort)this.get_store().add_element_user(CTVectorImpl.UI2$12);
        }
    }
    
    public void removeUi2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.UI2$12, n);
        }
    }
    
    public List<Long> getUi4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui4List extends AbstractList<Long>
            {
                @Override
                public Long get(final int n) {
                    return CTVectorImpl.this.getUi4Array(n);
                }
                
                @Override
                public Long set(final int n, final Long n2) {
                    final Long value = CTVectorImpl.this.getUi4Array(n);
                    CTVectorImpl.this.setUi4Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Long n2) {
                    CTVectorImpl.this.insertUi4(n, n2);
                }
                
                @Override
                public Long remove(final int n) {
                    final Long value = CTVectorImpl.this.getUi4Array(n);
                    CTVectorImpl.this.removeUi4(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi4Array();
                }
            }
            return new Ui4List();
        }
    }
    
    @Deprecated
    public long[] getUi4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI4$14, (List)list);
            final long[] array = new long[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getLongValue();
            }
            return array;
        }
    }
    
    public long getUi4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI4$14, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getLongValue();
        }
    }
    
    public List<XmlUnsignedInt> xgetUi4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui4List extends AbstractList<XmlUnsignedInt>
            {
                @Override
                public XmlUnsignedInt get(final int n) {
                    return CTVectorImpl.this.xgetUi4Array(n);
                }
                
                @Override
                public XmlUnsignedInt set(final int n, final XmlUnsignedInt xmlUnsignedInt) {
                    final XmlUnsignedInt xgetUi4Array = CTVectorImpl.this.xgetUi4Array(n);
                    CTVectorImpl.this.xsetUi4Array(n, xmlUnsignedInt);
                    return xgetUi4Array;
                }
                
                @Override
                public void add(final int n, final XmlUnsignedInt xmlUnsignedInt) {
                    CTVectorImpl.this.insertNewUi4(n).set((XmlObject)xmlUnsignedInt);
                }
                
                @Override
                public XmlUnsignedInt remove(final int n) {
                    final XmlUnsignedInt xgetUi4Array = CTVectorImpl.this.xgetUi4Array(n);
                    CTVectorImpl.this.removeUi4(n);
                    return xgetUi4Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi4Array();
                }
            }
            return new Ui4List();
        }
    }
    
    @Deprecated
    public XmlUnsignedInt[] xgetUi4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI4$14, (List)list);
            final XmlUnsignedInt[] array = new XmlUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlUnsignedInt xgetUi4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_element_user(CTVectorImpl.UI4$14, n);
            if (xmlUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlUnsignedInt;
        }
    }
    
    public int sizeOfUi4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.UI4$14);
        }
    }
    
    public void setUi4Array(final long[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.UI4$14);
        }
    }
    
    public void setUi4Array(final int n, final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI4$14, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetUi4Array(final XmlUnsignedInt[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.UI4$14);
        }
    }
    
    public void xsetUi4Array(final int n, final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_element_user(CTVectorImpl.UI4$14, n);
            if (xmlUnsignedInt2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void insertUi4(final int n, final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.UI4$14, n)).setLongValue(longValue);
        }
    }
    
    public void addUi4(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.UI4$14)).setLongValue(longValue);
        }
    }
    
    public XmlUnsignedInt insertNewUi4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().insert_element_user(CTVectorImpl.UI4$14, n);
        }
    }
    
    public XmlUnsignedInt addNewUi4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().add_element_user(CTVectorImpl.UI4$14);
        }
    }
    
    public void removeUi4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.UI4$14, n);
        }
    }
    
    public List<BigInteger> getUi8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui8List extends AbstractList<BigInteger>
            {
                @Override
                public BigInteger get(final int n) {
                    return CTVectorImpl.this.getUi8Array(n);
                }
                
                @Override
                public BigInteger set(final int n, final BigInteger bigInteger) {
                    final BigInteger ui8Array = CTVectorImpl.this.getUi8Array(n);
                    CTVectorImpl.this.setUi8Array(n, bigInteger);
                    return ui8Array;
                }
                
                @Override
                public void add(final int n, final BigInteger bigInteger) {
                    CTVectorImpl.this.insertUi8(n, bigInteger);
                }
                
                @Override
                public BigInteger remove(final int n) {
                    final BigInteger ui8Array = CTVectorImpl.this.getUi8Array(n);
                    CTVectorImpl.this.removeUi8(n);
                    return ui8Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi8Array();
                }
            }
            return new Ui8List();
        }
    }
    
    @Deprecated
    public BigInteger[] getUi8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI8$16, (List)list);
            final BigInteger[] array = new BigInteger[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBigIntegerValue();
            }
            return array;
        }
    }
    
    public BigInteger getUi8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI8$16, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public List<XmlUnsignedLong> xgetUi8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Ui8List extends AbstractList<XmlUnsignedLong>
            {
                @Override
                public XmlUnsignedLong get(final int n) {
                    return CTVectorImpl.this.xgetUi8Array(n);
                }
                
                @Override
                public XmlUnsignedLong set(final int n, final XmlUnsignedLong xmlUnsignedLong) {
                    final XmlUnsignedLong xgetUi8Array = CTVectorImpl.this.xgetUi8Array(n);
                    CTVectorImpl.this.xsetUi8Array(n, xmlUnsignedLong);
                    return xgetUi8Array;
                }
                
                @Override
                public void add(final int n, final XmlUnsignedLong xmlUnsignedLong) {
                    CTVectorImpl.this.insertNewUi8(n).set((XmlObject)xmlUnsignedLong);
                }
                
                @Override
                public XmlUnsignedLong remove(final int n) {
                    final XmlUnsignedLong xgetUi8Array = CTVectorImpl.this.xgetUi8Array(n);
                    CTVectorImpl.this.removeUi8(n);
                    return xgetUi8Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfUi8Array();
                }
            }
            return new Ui8List();
        }
    }
    
    @Deprecated
    public XmlUnsignedLong[] xgetUi8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.UI8$16, (List)list);
            final XmlUnsignedLong[] array = new XmlUnsignedLong[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlUnsignedLong xgetUi8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedLong xmlUnsignedLong = (XmlUnsignedLong)this.get_store().find_element_user(CTVectorImpl.UI8$16, n);
            if (xmlUnsignedLong == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlUnsignedLong;
        }
    }
    
    public int sizeOfUi8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.UI8$16);
        }
    }
    
    public void setUi8Array(final BigInteger[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.UI8$16);
        }
    }
    
    public void setUi8Array(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.UI8$16, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetUi8Array(final XmlUnsignedLong[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.UI8$16);
        }
    }
    
    public void xsetUi8Array(final int n, final XmlUnsignedLong xmlUnsignedLong) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlUnsignedLong xmlUnsignedLong2 = (XmlUnsignedLong)this.get_store().find_element_user(CTVectorImpl.UI8$16, n);
            if (xmlUnsignedLong2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlUnsignedLong2.set((XmlObject)xmlUnsignedLong);
        }
    }
    
    public void insertUi8(final int n, final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.UI8$16, n)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void addUi8(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.UI8$16)).setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public XmlUnsignedLong insertNewUi8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedLong)this.get_store().insert_element_user(CTVectorImpl.UI8$16, n);
        }
    }
    
    public XmlUnsignedLong addNewUi8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedLong)this.get_store().add_element_user(CTVectorImpl.UI8$16);
        }
    }
    
    public void removeUi8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.UI8$16, n);
        }
    }
    
    public List<Float> getR4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class R4List extends AbstractList<Float>
            {
                @Override
                public Float get(final int n) {
                    return CTVectorImpl.this.getR4Array(n);
                }
                
                @Override
                public Float set(final int n, final Float n2) {
                    final Float value = CTVectorImpl.this.getR4Array(n);
                    CTVectorImpl.this.setR4Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Float n2) {
                    CTVectorImpl.this.insertR4(n, n2);
                }
                
                @Override
                public Float remove(final int n) {
                    final Float value = CTVectorImpl.this.getR4Array(n);
                    CTVectorImpl.this.removeR4(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfR4Array();
                }
            }
            return new R4List();
        }
    }
    
    @Deprecated
    public float[] getR4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.R4$18, (List)list);
            final float[] array = new float[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getFloatValue();
            }
            return array;
        }
    }
    
    public float getR4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.R4$18, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public List<XmlFloat> xgetR4List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class R4List extends AbstractList<XmlFloat>
            {
                @Override
                public XmlFloat get(final int n) {
                    return CTVectorImpl.this.xgetR4Array(n);
                }
                
                @Override
                public XmlFloat set(final int n, final XmlFloat xmlFloat) {
                    final XmlFloat xgetR4Array = CTVectorImpl.this.xgetR4Array(n);
                    CTVectorImpl.this.xsetR4Array(n, xmlFloat);
                    return xgetR4Array;
                }
                
                @Override
                public void add(final int n, final XmlFloat xmlFloat) {
                    CTVectorImpl.this.insertNewR4(n).set((XmlObject)xmlFloat);
                }
                
                @Override
                public XmlFloat remove(final int n) {
                    final XmlFloat xgetR4Array = CTVectorImpl.this.xgetR4Array(n);
                    CTVectorImpl.this.removeR4(n);
                    return xgetR4Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfR4Array();
                }
            }
            return new R4List();
        }
    }
    
    @Deprecated
    public XmlFloat[] xgetR4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.R4$18, (List)list);
            final XmlFloat[] array = new XmlFloat[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlFloat xgetR4Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlFloat xmlFloat = (XmlFloat)this.get_store().find_element_user(CTVectorImpl.R4$18, n);
            if (xmlFloat == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlFloat;
        }
    }
    
    public int sizeOfR4Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.R4$18);
        }
    }
    
    public void setR4Array(final float[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.R4$18);
        }
    }
    
    public void setR4Array(final int n, final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.R4$18, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetR4Array(final XmlFloat[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.R4$18);
        }
    }
    
    public void xsetR4Array(final int n, final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_element_user(CTVectorImpl.R4$18, n);
            if (xmlFloat2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void insertR4(final int n, final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.R4$18, n)).setFloatValue(floatValue);
        }
    }
    
    public void addR4(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.R4$18)).setFloatValue(floatValue);
        }
    }
    
    public XmlFloat insertNewR4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().insert_element_user(CTVectorImpl.R4$18, n);
        }
    }
    
    public XmlFloat addNewR4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().add_element_user(CTVectorImpl.R4$18);
        }
    }
    
    public void removeR4(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.R4$18, n);
        }
    }
    
    public List<Double> getR8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class R8List extends AbstractList<Double>
            {
                @Override
                public Double get(final int n) {
                    return CTVectorImpl.this.getR8Array(n);
                }
                
                @Override
                public Double set(final int n, final Double n2) {
                    final Double value = CTVectorImpl.this.getR8Array(n);
                    CTVectorImpl.this.setR8Array(n, n2);
                    return value;
                }
                
                @Override
                public void add(final int n, final Double n2) {
                    CTVectorImpl.this.insertR8(n, n2);
                }
                
                @Override
                public Double remove(final int n) {
                    final Double value = CTVectorImpl.this.getR8Array(n);
                    CTVectorImpl.this.removeR8(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfR8Array();
                }
            }
            return new R8List();
        }
    }
    
    @Deprecated
    public double[] getR8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.R8$20, (List)list);
            final double[] array = new double[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getDoubleValue();
            }
            return array;
        }
    }
    
    public double getR8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.R8$20, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public List<XmlDouble> xgetR8List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class R8List extends AbstractList<XmlDouble>
            {
                @Override
                public XmlDouble get(final int n) {
                    return CTVectorImpl.this.xgetR8Array(n);
                }
                
                @Override
                public XmlDouble set(final int n, final XmlDouble xmlDouble) {
                    final XmlDouble xgetR8Array = CTVectorImpl.this.xgetR8Array(n);
                    CTVectorImpl.this.xsetR8Array(n, xmlDouble);
                    return xgetR8Array;
                }
                
                @Override
                public void add(final int n, final XmlDouble xmlDouble) {
                    CTVectorImpl.this.insertNewR8(n).set((XmlObject)xmlDouble);
                }
                
                @Override
                public XmlDouble remove(final int n) {
                    final XmlDouble xgetR8Array = CTVectorImpl.this.xgetR8Array(n);
                    CTVectorImpl.this.removeR8(n);
                    return xgetR8Array;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfR8Array();
                }
            }
            return new R8List();
        }
    }
    
    @Deprecated
    public XmlDouble[] xgetR8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.R8$20, (List)list);
            final XmlDouble[] array = new XmlDouble[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlDouble xgetR8Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDouble xmlDouble = (XmlDouble)this.get_store().find_element_user(CTVectorImpl.R8$20, n);
            if (xmlDouble == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlDouble;
        }
    }
    
    public int sizeOfR8Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.R8$20);
        }
    }
    
    public void setR8Array(final double[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.R8$20);
        }
    }
    
    public void setR8Array(final int n, final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.R8$20, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetR8Array(final XmlDouble[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.R8$20);
        }
    }
    
    public void xsetR8Array(final int n, final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_element_user(CTVectorImpl.R8$20, n);
            if (xmlDouble2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void insertR8(final int n, final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.R8$20, n)).setDoubleValue(doubleValue);
        }
    }
    
    public void addR8(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.R8$20)).setDoubleValue(doubleValue);
        }
    }
    
    public XmlDouble insertNewR8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().insert_element_user(CTVectorImpl.R8$20, n);
        }
    }
    
    public XmlDouble addNewR8() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().add_element_user(CTVectorImpl.R8$20);
        }
    }
    
    public void removeR8(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.R8$20, n);
        }
    }
    
    public List<String> getLpstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LpstrList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getLpstrArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String lpstrArray = CTVectorImpl.this.getLpstrArray(n);
                    CTVectorImpl.this.setLpstrArray(n, s);
                    return lpstrArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertLpstr(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String lpstrArray = CTVectorImpl.this.getLpstrArray(n);
                    CTVectorImpl.this.removeLpstr(n);
                    return lpstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfLpstrArray();
                }
            }
            return new LpstrList();
        }
    }
    
    @Deprecated
    public String[] getLpstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.LPSTR$22, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getLpstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.LPSTR$22, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetLpstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LpstrList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTVectorImpl.this.xgetLpstrArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetLpstrArray = CTVectorImpl.this.xgetLpstrArray(n);
                    CTVectorImpl.this.xsetLpstrArray(n, xmlString);
                    return xgetLpstrArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTVectorImpl.this.insertNewLpstr(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetLpstrArray = CTVectorImpl.this.xgetLpstrArray(n);
                    CTVectorImpl.this.removeLpstr(n);
                    return xgetLpstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfLpstrArray();
                }
            }
            return new LpstrList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetLpstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.LPSTR$22, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetLpstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTVectorImpl.LPSTR$22, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfLpstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.LPSTR$22);
        }
    }
    
    public void setLpstrArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.LPSTR$22);
        }
    }
    
    public void setLpstrArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.LPSTR$22, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLpstrArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.LPSTR$22);
        }
    }
    
    public void xsetLpstrArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVectorImpl.LPSTR$22, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertLpstr(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.LPSTR$22, n)).setStringValue(stringValue);
        }
    }
    
    public void addLpstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.LPSTR$22)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewLpstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTVectorImpl.LPSTR$22, n);
        }
    }
    
    public XmlString addNewLpstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTVectorImpl.LPSTR$22);
        }
    }
    
    public void removeLpstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.LPSTR$22, n);
        }
    }
    
    public List<String> getLpwstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LpwstrList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getLpwstrArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String lpwstrArray = CTVectorImpl.this.getLpwstrArray(n);
                    CTVectorImpl.this.setLpwstrArray(n, s);
                    return lpwstrArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertLpwstr(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String lpwstrArray = CTVectorImpl.this.getLpwstrArray(n);
                    CTVectorImpl.this.removeLpwstr(n);
                    return lpwstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfLpwstrArray();
                }
            }
            return new LpwstrList();
        }
    }
    
    @Deprecated
    public String[] getLpwstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.LPWSTR$24, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getLpwstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.LPWSTR$24, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetLpwstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LpwstrList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTVectorImpl.this.xgetLpwstrArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetLpwstrArray = CTVectorImpl.this.xgetLpwstrArray(n);
                    CTVectorImpl.this.xsetLpwstrArray(n, xmlString);
                    return xgetLpwstrArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTVectorImpl.this.insertNewLpwstr(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetLpwstrArray = CTVectorImpl.this.xgetLpwstrArray(n);
                    CTVectorImpl.this.removeLpwstr(n);
                    return xgetLpwstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfLpwstrArray();
                }
            }
            return new LpwstrList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetLpwstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.LPWSTR$24, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetLpwstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTVectorImpl.LPWSTR$24, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfLpwstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.LPWSTR$24);
        }
    }
    
    public void setLpwstrArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.LPWSTR$24);
        }
    }
    
    public void setLpwstrArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.LPWSTR$24, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLpwstrArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.LPWSTR$24);
        }
    }
    
    public void xsetLpwstrArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVectorImpl.LPWSTR$24, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertLpwstr(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.LPWSTR$24, n)).setStringValue(stringValue);
        }
    }
    
    public void addLpwstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.LPWSTR$24)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewLpwstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTVectorImpl.LPWSTR$24, n);
        }
    }
    
    public XmlString addNewLpwstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTVectorImpl.LPWSTR$24);
        }
    }
    
    public void removeLpwstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.LPWSTR$24, n);
        }
    }
    
    public List<String> getBstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BstrList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getBstrArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String bstrArray = CTVectorImpl.this.getBstrArray(n);
                    CTVectorImpl.this.setBstrArray(n, s);
                    return bstrArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertBstr(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String bstrArray = CTVectorImpl.this.getBstrArray(n);
                    CTVectorImpl.this.removeBstr(n);
                    return bstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfBstrArray();
                }
            }
            return new BstrList();
        }
    }
    
    @Deprecated
    public String[] getBstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.BSTR$26, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getBstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.BSTR$26, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetBstrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BstrList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return CTVectorImpl.this.xgetBstrArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetBstrArray = CTVectorImpl.this.xgetBstrArray(n);
                    CTVectorImpl.this.xsetBstrArray(n, xmlString);
                    return xgetBstrArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    CTVectorImpl.this.insertNewBstr(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetBstrArray = CTVectorImpl.this.xgetBstrArray(n);
                    CTVectorImpl.this.removeBstr(n);
                    return xgetBstrArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfBstrArray();
                }
            }
            return new BstrList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetBstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.BSTR$26, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetBstrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(CTVectorImpl.BSTR$26, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfBstrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.BSTR$26);
        }
    }
    
    public void setBstrArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.BSTR$26);
        }
    }
    
    public void setBstrArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.BSTR$26, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBstrArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.BSTR$26);
        }
    }
    
    public void xsetBstrArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTVectorImpl.BSTR$26, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertBstr(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.BSTR$26, n)).setStringValue(stringValue);
        }
    }
    
    public void addBstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.BSTR$26)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewBstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(CTVectorImpl.BSTR$26, n);
        }
    }
    
    public XmlString addNewBstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(CTVectorImpl.BSTR$26);
        }
    }
    
    public void removeBstr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.BSTR$26, n);
        }
    }
    
    public List<Calendar> getDateList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DateList extends AbstractList<Calendar>
            {
                @Override
                public Calendar get(final int n) {
                    return CTVectorImpl.this.getDateArray(n);
                }
                
                @Override
                public Calendar set(final int n, final Calendar calendar) {
                    final Calendar dateArray = CTVectorImpl.this.getDateArray(n);
                    CTVectorImpl.this.setDateArray(n, calendar);
                    return dateArray;
                }
                
                @Override
                public void add(final int n, final Calendar calendar) {
                    CTVectorImpl.this.insertDate(n, calendar);
                }
                
                @Override
                public Calendar remove(final int n) {
                    final Calendar dateArray = CTVectorImpl.this.getDateArray(n);
                    CTVectorImpl.this.removeDate(n);
                    return dateArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfDateArray();
                }
            }
            return new DateList();
        }
    }
    
    @Deprecated
    public Calendar[] getDateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.DATE$28, (List)list);
            final Calendar[] array = new Calendar[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getCalendarValue();
            }
            return array;
        }
    }
    
    public Calendar getDateArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.DATE$28, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public List<XmlDateTime> xgetDateList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DateList extends AbstractList<XmlDateTime>
            {
                @Override
                public XmlDateTime get(final int n) {
                    return CTVectorImpl.this.xgetDateArray(n);
                }
                
                @Override
                public XmlDateTime set(final int n, final XmlDateTime xmlDateTime) {
                    final XmlDateTime xgetDateArray = CTVectorImpl.this.xgetDateArray(n);
                    CTVectorImpl.this.xsetDateArray(n, xmlDateTime);
                    return xgetDateArray;
                }
                
                @Override
                public void add(final int n, final XmlDateTime xmlDateTime) {
                    CTVectorImpl.this.insertNewDate(n).set((XmlObject)xmlDateTime);
                }
                
                @Override
                public XmlDateTime remove(final int n) {
                    final XmlDateTime xgetDateArray = CTVectorImpl.this.xgetDateArray(n);
                    CTVectorImpl.this.removeDate(n);
                    return xgetDateArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfDateArray();
                }
            }
            return new DateList();
        }
    }
    
    @Deprecated
    public XmlDateTime[] xgetDateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.DATE$28, (List)list);
            final XmlDateTime[] array = new XmlDateTime[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlDateTime xgetDateArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDateTime xmlDateTime = (XmlDateTime)this.get_store().find_element_user(CTVectorImpl.DATE$28, n);
            if (xmlDateTime == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlDateTime;
        }
    }
    
    public int sizeOfDateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.DATE$28);
        }
    }
    
    public void setDateArray(final Calendar[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.DATE$28);
        }
    }
    
    public void setDateArray(final int n, final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.DATE$28, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetDateArray(final XmlDateTime[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.DATE$28);
        }
    }
    
    public void xsetDateArray(final int n, final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(CTVectorImpl.DATE$28, n);
            if (xmlDateTime2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void insertDate(final int n, final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.DATE$28, n)).setCalendarValue(calendarValue);
        }
    }
    
    public void addDate(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.DATE$28)).setCalendarValue(calendarValue);
        }
    }
    
    public XmlDateTime insertNewDate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().insert_element_user(CTVectorImpl.DATE$28, n);
        }
    }
    
    public XmlDateTime addNewDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().add_element_user(CTVectorImpl.DATE$28);
        }
    }
    
    public void removeDate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.DATE$28, n);
        }
    }
    
    public List<Calendar> getFiletimeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FiletimeList extends AbstractList<Calendar>
            {
                @Override
                public Calendar get(final int n) {
                    return CTVectorImpl.this.getFiletimeArray(n);
                }
                
                @Override
                public Calendar set(final int n, final Calendar calendar) {
                    final Calendar filetimeArray = CTVectorImpl.this.getFiletimeArray(n);
                    CTVectorImpl.this.setFiletimeArray(n, calendar);
                    return filetimeArray;
                }
                
                @Override
                public void add(final int n, final Calendar calendar) {
                    CTVectorImpl.this.insertFiletime(n, calendar);
                }
                
                @Override
                public Calendar remove(final int n) {
                    final Calendar filetimeArray = CTVectorImpl.this.getFiletimeArray(n);
                    CTVectorImpl.this.removeFiletime(n);
                    return filetimeArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfFiletimeArray();
                }
            }
            return new FiletimeList();
        }
    }
    
    @Deprecated
    public Calendar[] getFiletimeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.FILETIME$30, (List)list);
            final Calendar[] array = new Calendar[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getCalendarValue();
            }
            return array;
        }
    }
    
    public Calendar getFiletimeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.FILETIME$30, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public List<XmlDateTime> xgetFiletimeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FiletimeList extends AbstractList<XmlDateTime>
            {
                @Override
                public XmlDateTime get(final int n) {
                    return CTVectorImpl.this.xgetFiletimeArray(n);
                }
                
                @Override
                public XmlDateTime set(final int n, final XmlDateTime xmlDateTime) {
                    final XmlDateTime xgetFiletimeArray = CTVectorImpl.this.xgetFiletimeArray(n);
                    CTVectorImpl.this.xsetFiletimeArray(n, xmlDateTime);
                    return xgetFiletimeArray;
                }
                
                @Override
                public void add(final int n, final XmlDateTime xmlDateTime) {
                    CTVectorImpl.this.insertNewFiletime(n).set((XmlObject)xmlDateTime);
                }
                
                @Override
                public XmlDateTime remove(final int n) {
                    final XmlDateTime xgetFiletimeArray = CTVectorImpl.this.xgetFiletimeArray(n);
                    CTVectorImpl.this.removeFiletime(n);
                    return xgetFiletimeArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfFiletimeArray();
                }
            }
            return new FiletimeList();
        }
    }
    
    @Deprecated
    public XmlDateTime[] xgetFiletimeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.FILETIME$30, (List)list);
            final XmlDateTime[] array = new XmlDateTime[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlDateTime xgetFiletimeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDateTime xmlDateTime = (XmlDateTime)this.get_store().find_element_user(CTVectorImpl.FILETIME$30, n);
            if (xmlDateTime == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlDateTime;
        }
    }
    
    public int sizeOfFiletimeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.FILETIME$30);
        }
    }
    
    public void setFiletimeArray(final Calendar[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.FILETIME$30);
        }
    }
    
    public void setFiletimeArray(final int n, final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.FILETIME$30, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetFiletimeArray(final XmlDateTime[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.FILETIME$30);
        }
    }
    
    public void xsetFiletimeArray(final int n, final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(CTVectorImpl.FILETIME$30, n);
            if (xmlDateTime2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void insertFiletime(final int n, final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.FILETIME$30, n)).setCalendarValue(calendarValue);
        }
    }
    
    public void addFiletime(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.FILETIME$30)).setCalendarValue(calendarValue);
        }
    }
    
    public XmlDateTime insertNewFiletime(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().insert_element_user(CTVectorImpl.FILETIME$30, n);
        }
    }
    
    public XmlDateTime addNewFiletime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().add_element_user(CTVectorImpl.FILETIME$30);
        }
    }
    
    public void removeFiletime(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.FILETIME$30, n);
        }
    }
    
    public List<Boolean> getBoolList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BoolList extends AbstractList<Boolean>
            {
                @Override
                public Boolean get(final int n) {
                    return CTVectorImpl.this.getBoolArray(n);
                }
                
                @Override
                public Boolean set(final int n, final Boolean b) {
                    final Boolean value = CTVectorImpl.this.getBoolArray(n);
                    CTVectorImpl.this.setBoolArray(n, b);
                    return value;
                }
                
                @Override
                public void add(final int n, final Boolean b) {
                    CTVectorImpl.this.insertBool(n, b);
                }
                
                @Override
                public Boolean remove(final int n) {
                    final Boolean value = CTVectorImpl.this.getBoolArray(n);
                    CTVectorImpl.this.removeBool(n);
                    return value;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfBoolArray();
                }
            }
            return new BoolList();
        }
    }
    
    @Deprecated
    public boolean[] getBoolArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.BOOL$32, (List)list);
            final boolean[] array = new boolean[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getBooleanValue();
            }
            return array;
        }
    }
    
    public boolean getBoolArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.BOOL$32, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getBooleanValue();
        }
    }
    
    public List<XmlBoolean> xgetBoolList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BoolList extends AbstractList<XmlBoolean>
            {
                @Override
                public XmlBoolean get(final int n) {
                    return CTVectorImpl.this.xgetBoolArray(n);
                }
                
                @Override
                public XmlBoolean set(final int n, final XmlBoolean xmlBoolean) {
                    final XmlBoolean xgetBoolArray = CTVectorImpl.this.xgetBoolArray(n);
                    CTVectorImpl.this.xsetBoolArray(n, xmlBoolean);
                    return xgetBoolArray;
                }
                
                @Override
                public void add(final int n, final XmlBoolean xmlBoolean) {
                    CTVectorImpl.this.insertNewBool(n).set((XmlObject)xmlBoolean);
                }
                
                @Override
                public XmlBoolean remove(final int n) {
                    final XmlBoolean xgetBoolArray = CTVectorImpl.this.xgetBoolArray(n);
                    CTVectorImpl.this.removeBool(n);
                    return xgetBoolArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfBoolArray();
                }
            }
            return new BoolList();
        }
    }
    
    @Deprecated
    public XmlBoolean[] xgetBoolArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.BOOL$32, (List)list);
            final XmlBoolean[] array = new XmlBoolean[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlBoolean xgetBoolArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_element_user(CTVectorImpl.BOOL$32, n);
            if (xmlBoolean == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlBoolean;
        }
    }
    
    public int sizeOfBoolArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.BOOL$32);
        }
    }
    
    public void setBoolArray(final boolean[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.BOOL$32);
        }
    }
    
    public void setBoolArray(final int n, final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.BOOL$32, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBoolArray(final XmlBoolean[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.BOOL$32);
        }
    }
    
    public void xsetBoolArray(final int n, final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTVectorImpl.BOOL$32, n);
            if (xmlBoolean2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void insertBool(final int n, final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.BOOL$32, n)).setBooleanValue(booleanValue);
        }
    }
    
    public void addBool(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.BOOL$32)).setBooleanValue(booleanValue);
        }
    }
    
    public XmlBoolean insertNewBool(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().insert_element_user(CTVectorImpl.BOOL$32, n);
        }
    }
    
    public XmlBoolean addNewBool() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().add_element_user(CTVectorImpl.BOOL$32);
        }
    }
    
    public void removeBool(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.BOOL$32, n);
        }
    }
    
    public List<String> getCyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CyList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getCyArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String cyArray = CTVectorImpl.this.getCyArray(n);
                    CTVectorImpl.this.setCyArray(n, s);
                    return cyArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertCy(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String cyArray = CTVectorImpl.this.getCyArray(n);
                    CTVectorImpl.this.removeCy(n);
                    return cyArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfCyArray();
                }
            }
            return new CyList();
        }
    }
    
    @Deprecated
    public String[] getCyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.CY$34, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getCyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.CY$34, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<STCy> xgetCyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CyList extends AbstractList<STCy>
            {
                @Override
                public STCy get(final int n) {
                    return CTVectorImpl.this.xgetCyArray(n);
                }
                
                @Override
                public STCy set(final int n, final STCy stCy) {
                    final STCy xgetCyArray = CTVectorImpl.this.xgetCyArray(n);
                    CTVectorImpl.this.xsetCyArray(n, stCy);
                    return xgetCyArray;
                }
                
                @Override
                public void add(final int n, final STCy stCy) {
                    CTVectorImpl.this.insertNewCy(n).set((XmlObject)stCy);
                }
                
                @Override
                public STCy remove(final int n) {
                    final STCy xgetCyArray = CTVectorImpl.this.xgetCyArray(n);
                    CTVectorImpl.this.removeCy(n);
                    return xgetCyArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfCyArray();
                }
            }
            return new CyList();
        }
    }
    
    @Deprecated
    public STCy[] xgetCyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.CY$34, (List)list);
            final STCy[] array = new STCy[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STCy xgetCyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STCy stCy = (STCy)this.get_store().find_element_user(CTVectorImpl.CY$34, n);
            if (stCy == null) {
                throw new IndexOutOfBoundsException();
            }
            return stCy;
        }
    }
    
    public int sizeOfCyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.CY$34);
        }
    }
    
    public void setCyArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.CY$34);
        }
    }
    
    public void setCyArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.CY$34, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCyArray(final STCy[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.CY$34);
        }
    }
    
    public void xsetCyArray(final int n, final STCy stCy) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STCy stCy2 = (STCy)this.get_store().find_element_user(CTVectorImpl.CY$34, n);
            if (stCy2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stCy2.set((XmlObject)stCy);
        }
    }
    
    public void insertCy(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.CY$34, n)).setStringValue(stringValue);
        }
    }
    
    public void addCy(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.CY$34)).setStringValue(stringValue);
        }
    }
    
    public STCy insertNewCy(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCy)this.get_store().insert_element_user(CTVectorImpl.CY$34, n);
        }
    }
    
    public STCy addNewCy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCy)this.get_store().add_element_user(CTVectorImpl.CY$34);
        }
    }
    
    public void removeCy(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.CY$34, n);
        }
    }
    
    public List<String> getErrorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ErrorList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getErrorArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String errorArray = CTVectorImpl.this.getErrorArray(n);
                    CTVectorImpl.this.setErrorArray(n, s);
                    return errorArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertError(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String errorArray = CTVectorImpl.this.getErrorArray(n);
                    CTVectorImpl.this.removeError(n);
                    return errorArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfErrorArray();
                }
            }
            return new ErrorList();
        }
    }
    
    @Deprecated
    public String[] getErrorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.ERROR$36, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getErrorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.ERROR$36, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<STError> xgetErrorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ErrorList extends AbstractList<STError>
            {
                @Override
                public STError get(final int n) {
                    return CTVectorImpl.this.xgetErrorArray(n);
                }
                
                @Override
                public STError set(final int n, final STError stError) {
                    final STError xgetErrorArray = CTVectorImpl.this.xgetErrorArray(n);
                    CTVectorImpl.this.xsetErrorArray(n, stError);
                    return xgetErrorArray;
                }
                
                @Override
                public void add(final int n, final STError stError) {
                    CTVectorImpl.this.insertNewError(n).set((XmlObject)stError);
                }
                
                @Override
                public STError remove(final int n) {
                    final STError xgetErrorArray = CTVectorImpl.this.xgetErrorArray(n);
                    CTVectorImpl.this.removeError(n);
                    return xgetErrorArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfErrorArray();
                }
            }
            return new ErrorList();
        }
    }
    
    @Deprecated
    public STError[] xgetErrorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.ERROR$36, (List)list);
            final STError[] array = new STError[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STError xgetErrorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STError stError = (STError)this.get_store().find_element_user(CTVectorImpl.ERROR$36, n);
            if (stError == null) {
                throw new IndexOutOfBoundsException();
            }
            return stError;
        }
    }
    
    public int sizeOfErrorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.ERROR$36);
        }
    }
    
    public void setErrorArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.ERROR$36);
        }
    }
    
    public void setErrorArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.ERROR$36, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetErrorArray(final STError[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.ERROR$36);
        }
    }
    
    public void xsetErrorArray(final int n, final STError stError) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STError stError2 = (STError)this.get_store().find_element_user(CTVectorImpl.ERROR$36, n);
            if (stError2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stError2.set((XmlObject)stError);
        }
    }
    
    public void insertError(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.ERROR$36, n)).setStringValue(stringValue);
        }
    }
    
    public void addError(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.ERROR$36)).setStringValue(stringValue);
        }
    }
    
    public STError insertNewError(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STError)this.get_store().insert_element_user(CTVectorImpl.ERROR$36, n);
        }
    }
    
    public STError addNewError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STError)this.get_store().add_element_user(CTVectorImpl.ERROR$36);
        }
    }
    
    public void removeError(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.ERROR$36, n);
        }
    }
    
    public List<String> getClsidList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClsidList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTVectorImpl.this.getClsidArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String clsidArray = CTVectorImpl.this.getClsidArray(n);
                    CTVectorImpl.this.setClsidArray(n, s);
                    return clsidArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTVectorImpl.this.insertClsid(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String clsidArray = CTVectorImpl.this.getClsidArray(n);
                    CTVectorImpl.this.removeClsid(n);
                    return clsidArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfClsidArray();
                }
            }
            return new ClsidList();
        }
    }
    
    @Deprecated
    public String[] getClsidArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.CLSID$38, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getClsidArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.CLSID$38, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<STClsid> xgetClsidList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClsidList extends AbstractList<STClsid>
            {
                @Override
                public STClsid get(final int n) {
                    return CTVectorImpl.this.xgetClsidArray(n);
                }
                
                @Override
                public STClsid set(final int n, final STClsid stClsid) {
                    final STClsid xgetClsidArray = CTVectorImpl.this.xgetClsidArray(n);
                    CTVectorImpl.this.xsetClsidArray(n, stClsid);
                    return xgetClsidArray;
                }
                
                @Override
                public void add(final int n, final STClsid stClsid) {
                    CTVectorImpl.this.insertNewClsid(n).set((XmlObject)stClsid);
                }
                
                @Override
                public STClsid remove(final int n) {
                    final STClsid xgetClsidArray = CTVectorImpl.this.xgetClsidArray(n);
                    CTVectorImpl.this.removeClsid(n);
                    return xgetClsidArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfClsidArray();
                }
            }
            return new ClsidList();
        }
    }
    
    @Deprecated
    public STClsid[] xgetClsidArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.CLSID$38, (List)list);
            final STClsid[] array = new STClsid[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STClsid xgetClsidArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STClsid stClsid = (STClsid)this.get_store().find_element_user(CTVectorImpl.CLSID$38, n);
            if (stClsid == null) {
                throw new IndexOutOfBoundsException();
            }
            return stClsid;
        }
    }
    
    public int sizeOfClsidArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.CLSID$38);
        }
    }
    
    public void setClsidArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTVectorImpl.CLSID$38);
        }
    }
    
    public void setClsidArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTVectorImpl.CLSID$38, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetClsidArray(final STClsid[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTVectorImpl.CLSID$38);
        }
    }
    
    public void xsetClsidArray(final int n, final STClsid stClsid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STClsid stClsid2 = (STClsid)this.get_store().find_element_user(CTVectorImpl.CLSID$38, n);
            if (stClsid2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stClsid2.set((XmlObject)stClsid);
        }
    }
    
    public void insertClsid(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTVectorImpl.CLSID$38, n)).setStringValue(stringValue);
        }
    }
    
    public void addClsid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTVectorImpl.CLSID$38)).setStringValue(stringValue);
        }
    }
    
    public STClsid insertNewClsid(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STClsid)this.get_store().insert_element_user(CTVectorImpl.CLSID$38, n);
        }
    }
    
    public STClsid addNewClsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STClsid)this.get_store().add_element_user(CTVectorImpl.CLSID$38);
        }
    }
    
    public void removeClsid(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.CLSID$38, n);
        }
    }
    
    public List<CTCf> getCfList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CfList extends AbstractList<CTCf>
            {
                @Override
                public CTCf get(final int n) {
                    return CTVectorImpl.this.getCfArray(n);
                }
                
                @Override
                public CTCf set(final int n, final CTCf ctCf) {
                    final CTCf cfArray = CTVectorImpl.this.getCfArray(n);
                    CTVectorImpl.this.setCfArray(n, ctCf);
                    return cfArray;
                }
                
                @Override
                public void add(final int n, final CTCf ctCf) {
                    CTVectorImpl.this.insertNewCf(n).set((XmlObject)ctCf);
                }
                
                @Override
                public CTCf remove(final int n) {
                    final CTCf cfArray = CTVectorImpl.this.getCfArray(n);
                    CTVectorImpl.this.removeCf(n);
                    return cfArray;
                }
                
                @Override
                public int size() {
                    return CTVectorImpl.this.sizeOfCfArray();
                }
            }
            return new CfList();
        }
    }
    
    @Deprecated
    public CTCf[] getCfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTVectorImpl.CF$40, (List)list);
            final CTCf[] array = new CTCf[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCf getCfArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCf ctCf = (CTCf)this.get_store().find_element_user(CTVectorImpl.CF$40, n);
            if (ctCf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCf;
        }
    }
    
    public int sizeOfCfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTVectorImpl.CF$40);
        }
    }
    
    public void setCfArray(final CTCf[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTVectorImpl.CF$40);
    }
    
    public void setCfArray(final int n, final CTCf ctCf) {
        this.generatedSetterHelperImpl((XmlObject)ctCf, CTVectorImpl.CF$40, n, (short)2);
    }
    
    public CTCf insertNewCf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCf)this.get_store().insert_element_user(CTVectorImpl.CF$40, n);
        }
    }
    
    public CTCf addNewCf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCf)this.get_store().add_element_user(CTVectorImpl.CF$40);
        }
    }
    
    public void removeCf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTVectorImpl.CF$40, n);
        }
    }
    
    public STVectorBaseType.Enum getBaseType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVectorImpl.BASETYPE$42);
            if (simpleValue == null) {
                return null;
            }
            return (STVectorBaseType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STVectorBaseType xgetBaseType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVectorBaseType)this.get_store().find_attribute_user(CTVectorImpl.BASETYPE$42);
        }
    }
    
    public void setBaseType(final STVectorBaseType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVectorImpl.BASETYPE$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTVectorImpl.BASETYPE$42);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBaseType(final STVectorBaseType stVectorBaseType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVectorBaseType stVectorBaseType2 = (STVectorBaseType)this.get_store().find_attribute_user(CTVectorImpl.BASETYPE$42);
            if (stVectorBaseType2 == null) {
                stVectorBaseType2 = (STVectorBaseType)this.get_store().add_attribute_user(CTVectorImpl.BASETYPE$42);
            }
            stVectorBaseType2.set((XmlObject)stVectorBaseType);
        }
    }
    
    public long getSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVectorImpl.SIZE$44);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTVectorImpl.SIZE$44);
        }
    }
    
    public void setSize(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVectorImpl.SIZE$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTVectorImpl.SIZE$44);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSize(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTVectorImpl.SIZE$44);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTVectorImpl.SIZE$44);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        VARIANT$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "variant");
        I1$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i1");
        I2$4 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i2");
        I4$6 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i4");
        I8$8 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "i8");
        UI1$10 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui1");
        UI2$12 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui2");
        UI4$14 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui4");
        UI8$16 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "ui8");
        R4$18 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "r4");
        R8$20 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "r8");
        LPSTR$22 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "lpstr");
        LPWSTR$24 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "lpwstr");
        BSTR$26 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "bstr");
        DATE$28 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "date");
        FILETIME$30 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "filetime");
        BOOL$32 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "bool");
        CY$34 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "cy");
        ERROR$36 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "error");
        CLSID$38 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "clsid");
        CF$40 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "cf");
        BASETYPE$42 = new QName("", "baseType");
        SIZE$44 = new QName("", "size");
    }
}
