package com.microsoft.schemas.office.x2006.encryption.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptors;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTKeyEncryptorsImpl extends XmlComplexContentImpl implements CTKeyEncryptors
{
    private static final long serialVersionUID = 1L;
    private static final QName KEYENCRYPTOR$0;
    
    public CTKeyEncryptorsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTKeyEncryptor> getKeyEncryptorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class KeyEncryptorList extends AbstractList<CTKeyEncryptor>
            {
                @Override
                public CTKeyEncryptor get(final int n) {
                    return CTKeyEncryptorsImpl.this.getKeyEncryptorArray(n);
                }
                
                @Override
                public CTKeyEncryptor set(final int n, final CTKeyEncryptor ctKeyEncryptor) {
                    final CTKeyEncryptor keyEncryptorArray = CTKeyEncryptorsImpl.this.getKeyEncryptorArray(n);
                    CTKeyEncryptorsImpl.this.setKeyEncryptorArray(n, ctKeyEncryptor);
                    return keyEncryptorArray;
                }
                
                @Override
                public void add(final int n, final CTKeyEncryptor ctKeyEncryptor) {
                    CTKeyEncryptorsImpl.this.insertNewKeyEncryptor(n).set((XmlObject)ctKeyEncryptor);
                }
                
                @Override
                public CTKeyEncryptor remove(final int n) {
                    final CTKeyEncryptor keyEncryptorArray = CTKeyEncryptorsImpl.this.getKeyEncryptorArray(n);
                    CTKeyEncryptorsImpl.this.removeKeyEncryptor(n);
                    return keyEncryptorArray;
                }
                
                @Override
                public int size() {
                    return CTKeyEncryptorsImpl.this.sizeOfKeyEncryptorArray();
                }
            }
            return new KeyEncryptorList();
        }
    }
    
    @Deprecated
    public CTKeyEncryptor[] getKeyEncryptorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTKeyEncryptorsImpl.KEYENCRYPTOR$0, (List)list);
            final CTKeyEncryptor[] array = new CTKeyEncryptor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTKeyEncryptor getKeyEncryptorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKeyEncryptor ctKeyEncryptor = (CTKeyEncryptor)this.get_store().find_element_user(CTKeyEncryptorsImpl.KEYENCRYPTOR$0, n);
            if (ctKeyEncryptor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctKeyEncryptor;
        }
    }
    
    public int sizeOfKeyEncryptorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTKeyEncryptorsImpl.KEYENCRYPTOR$0);
        }
    }
    
    public void setKeyEncryptorArray(final CTKeyEncryptor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTKeyEncryptorsImpl.KEYENCRYPTOR$0);
    }
    
    public void setKeyEncryptorArray(final int n, final CTKeyEncryptor ctKeyEncryptor) {
        this.generatedSetterHelperImpl((XmlObject)ctKeyEncryptor, CTKeyEncryptorsImpl.KEYENCRYPTOR$0, n, (short)2);
    }
    
    public CTKeyEncryptor insertNewKeyEncryptor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKeyEncryptor)this.get_store().insert_element_user(CTKeyEncryptorsImpl.KEYENCRYPTOR$0, n);
        }
    }
    
    public CTKeyEncryptor addNewKeyEncryptor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKeyEncryptor)this.get_store().add_element_user(CTKeyEncryptorsImpl.KEYENCRYPTOR$0);
        }
    }
    
    public void removeKeyEncryptor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTKeyEncryptorsImpl.KEYENCRYPTOR$0, n);
        }
    }
    
    static {
        KEYENCRYPTOR$0 = new QName("http://schemas.microsoft.com/office/2006/encryption", "keyEncryptor");
    }
}
