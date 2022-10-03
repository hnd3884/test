package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CRLValuesTypeImpl extends XmlComplexContentImpl implements CRLValuesType
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCAPSULATEDCRLVALUE$0;
    
    public CRLValuesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<EncapsulatedPKIDataType> getEncapsulatedCRLValueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EncapsulatedCRLValueList extends AbstractList<EncapsulatedPKIDataType>
            {
                @Override
                public EncapsulatedPKIDataType get(final int n) {
                    return CRLValuesTypeImpl.this.getEncapsulatedCRLValueArray(n);
                }
                
                @Override
                public EncapsulatedPKIDataType set(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    final EncapsulatedPKIDataType encapsulatedCRLValueArray = CRLValuesTypeImpl.this.getEncapsulatedCRLValueArray(n);
                    CRLValuesTypeImpl.this.setEncapsulatedCRLValueArray(n, encapsulatedPKIDataType);
                    return encapsulatedCRLValueArray;
                }
                
                @Override
                public void add(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    CRLValuesTypeImpl.this.insertNewEncapsulatedCRLValue(n).set((XmlObject)encapsulatedPKIDataType);
                }
                
                @Override
                public EncapsulatedPKIDataType remove(final int n) {
                    final EncapsulatedPKIDataType encapsulatedCRLValueArray = CRLValuesTypeImpl.this.getEncapsulatedCRLValueArray(n);
                    CRLValuesTypeImpl.this.removeEncapsulatedCRLValue(n);
                    return encapsulatedCRLValueArray;
                }
                
                @Override
                public int size() {
                    return CRLValuesTypeImpl.this.sizeOfEncapsulatedCRLValueArray();
                }
            }
            return new EncapsulatedCRLValueList();
        }
    }
    
    @Deprecated
    public EncapsulatedPKIDataType[] getEncapsulatedCRLValueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0, (List)list);
            final EncapsulatedPKIDataType[] array = new EncapsulatedPKIDataType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public EncapsulatedPKIDataType getEncapsulatedCRLValueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final EncapsulatedPKIDataType encapsulatedPKIDataType = (EncapsulatedPKIDataType)this.get_store().find_element_user(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0, n);
            if (encapsulatedPKIDataType == null) {
                throw new IndexOutOfBoundsException();
            }
            return encapsulatedPKIDataType;
        }
    }
    
    public int sizeOfEncapsulatedCRLValueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0);
        }
    }
    
    public void setEncapsulatedCRLValueArray(final EncapsulatedPKIDataType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0);
    }
    
    public void setEncapsulatedCRLValueArray(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
        this.generatedSetterHelperImpl((XmlObject)encapsulatedPKIDataType, CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0, n, (short)2);
    }
    
    public EncapsulatedPKIDataType insertNewEncapsulatedCRLValue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().insert_element_user(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0, n);
        }
    }
    
    public EncapsulatedPKIDataType addNewEncapsulatedCRLValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().add_element_user(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0);
        }
    }
    
    public void removeEncapsulatedCRLValue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CRLValuesTypeImpl.ENCAPSULATEDCRLVALUE$0, n);
        }
    }
    
    static {
        ENCAPSULATEDCRLVALUE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedCRLValue");
    }
}
