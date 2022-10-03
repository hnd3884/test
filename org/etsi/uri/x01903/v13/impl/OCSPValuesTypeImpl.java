package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.OCSPValuesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class OCSPValuesTypeImpl extends XmlComplexContentImpl implements OCSPValuesType
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCAPSULATEDOCSPVALUE$0;
    
    public OCSPValuesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<EncapsulatedPKIDataType> getEncapsulatedOCSPValueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EncapsulatedOCSPValueList extends AbstractList<EncapsulatedPKIDataType>
            {
                @Override
                public EncapsulatedPKIDataType get(final int n) {
                    return OCSPValuesTypeImpl.this.getEncapsulatedOCSPValueArray(n);
                }
                
                @Override
                public EncapsulatedPKIDataType set(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    final EncapsulatedPKIDataType encapsulatedOCSPValueArray = OCSPValuesTypeImpl.this.getEncapsulatedOCSPValueArray(n);
                    OCSPValuesTypeImpl.this.setEncapsulatedOCSPValueArray(n, encapsulatedPKIDataType);
                    return encapsulatedOCSPValueArray;
                }
                
                @Override
                public void add(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    OCSPValuesTypeImpl.this.insertNewEncapsulatedOCSPValue(n).set((XmlObject)encapsulatedPKIDataType);
                }
                
                @Override
                public EncapsulatedPKIDataType remove(final int n) {
                    final EncapsulatedPKIDataType encapsulatedOCSPValueArray = OCSPValuesTypeImpl.this.getEncapsulatedOCSPValueArray(n);
                    OCSPValuesTypeImpl.this.removeEncapsulatedOCSPValue(n);
                    return encapsulatedOCSPValueArray;
                }
                
                @Override
                public int size() {
                    return OCSPValuesTypeImpl.this.sizeOfEncapsulatedOCSPValueArray();
                }
            }
            return new EncapsulatedOCSPValueList();
        }
    }
    
    @Deprecated
    public EncapsulatedPKIDataType[] getEncapsulatedOCSPValueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0, (List)list);
            final EncapsulatedPKIDataType[] array = new EncapsulatedPKIDataType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public EncapsulatedPKIDataType getEncapsulatedOCSPValueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final EncapsulatedPKIDataType encapsulatedPKIDataType = (EncapsulatedPKIDataType)this.get_store().find_element_user(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0, n);
            if (encapsulatedPKIDataType == null) {
                throw new IndexOutOfBoundsException();
            }
            return encapsulatedPKIDataType;
        }
    }
    
    public int sizeOfEncapsulatedOCSPValueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0);
        }
    }
    
    public void setEncapsulatedOCSPValueArray(final EncapsulatedPKIDataType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0);
    }
    
    public void setEncapsulatedOCSPValueArray(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
        this.generatedSetterHelperImpl((XmlObject)encapsulatedPKIDataType, OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0, n, (short)2);
    }
    
    public EncapsulatedPKIDataType insertNewEncapsulatedOCSPValue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().insert_element_user(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0, n);
        }
    }
    
    public EncapsulatedPKIDataType addNewEncapsulatedOCSPValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().add_element_user(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0);
        }
    }
    
    public void removeEncapsulatedOCSPValue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(OCSPValuesTypeImpl.ENCAPSULATEDOCSPVALUE$0, n);
        }
    }
    
    static {
        ENCAPSULATEDOCSPVALUE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedOCSPValue");
    }
}
