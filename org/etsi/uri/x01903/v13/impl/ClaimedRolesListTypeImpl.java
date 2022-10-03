package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.AnyType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ClaimedRolesListTypeImpl extends XmlComplexContentImpl implements ClaimedRolesListType
{
    private static final long serialVersionUID = 1L;
    private static final QName CLAIMEDROLE$0;
    
    public ClaimedRolesListTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<AnyType> getClaimedRoleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClaimedRoleList extends AbstractList<AnyType>
            {
                @Override
                public AnyType get(final int n) {
                    return ClaimedRolesListTypeImpl.this.getClaimedRoleArray(n);
                }
                
                @Override
                public AnyType set(final int n, final AnyType anyType) {
                    final AnyType claimedRoleArray = ClaimedRolesListTypeImpl.this.getClaimedRoleArray(n);
                    ClaimedRolesListTypeImpl.this.setClaimedRoleArray(n, anyType);
                    return claimedRoleArray;
                }
                
                @Override
                public void add(final int n, final AnyType anyType) {
                    ClaimedRolesListTypeImpl.this.insertNewClaimedRole(n).set((XmlObject)anyType);
                }
                
                @Override
                public AnyType remove(final int n) {
                    final AnyType claimedRoleArray = ClaimedRolesListTypeImpl.this.getClaimedRoleArray(n);
                    ClaimedRolesListTypeImpl.this.removeClaimedRole(n);
                    return claimedRoleArray;
                }
                
                @Override
                public int size() {
                    return ClaimedRolesListTypeImpl.this.sizeOfClaimedRoleArray();
                }
            }
            return new ClaimedRoleList();
        }
    }
    
    @Deprecated
    public AnyType[] getClaimedRoleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(ClaimedRolesListTypeImpl.CLAIMEDROLE$0, (List)list);
            final AnyType[] array = new AnyType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public AnyType getClaimedRoleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final AnyType anyType = (AnyType)this.get_store().find_element_user(ClaimedRolesListTypeImpl.CLAIMEDROLE$0, n);
            if (anyType == null) {
                throw new IndexOutOfBoundsException();
            }
            return anyType;
        }
    }
    
    public int sizeOfClaimedRoleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ClaimedRolesListTypeImpl.CLAIMEDROLE$0);
        }
    }
    
    public void setClaimedRoleArray(final AnyType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, ClaimedRolesListTypeImpl.CLAIMEDROLE$0);
    }
    
    public void setClaimedRoleArray(final int n, final AnyType anyType) {
        this.generatedSetterHelperImpl((XmlObject)anyType, ClaimedRolesListTypeImpl.CLAIMEDROLE$0, n, (short)2);
    }
    
    public AnyType insertNewClaimedRole(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().insert_element_user(ClaimedRolesListTypeImpl.CLAIMEDROLE$0, n);
        }
    }
    
    public AnyType addNewClaimedRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().add_element_user(ClaimedRolesListTypeImpl.CLAIMEDROLE$0);
        }
    }
    
    public void removeClaimedRole(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ClaimedRolesListTypeImpl.CLAIMEDROLE$0, n);
        }
    }
    
    static {
        CLAIMEDROLE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ClaimedRole");
    }
}
