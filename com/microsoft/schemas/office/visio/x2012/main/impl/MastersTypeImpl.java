package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.MasterShortcutType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MastersTypeImpl extends XmlComplexContentImpl implements MastersType
{
    private static final long serialVersionUID = 1L;
    private static final QName MASTER$0;
    private static final QName MASTERSHORTCUT$2;
    
    public MastersTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<MasterType> getMasterList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MasterList extends AbstractList<MasterType>
            {
                @Override
                public MasterType get(final int n) {
                    return MastersTypeImpl.this.getMasterArray(n);
                }
                
                @Override
                public MasterType set(final int n, final MasterType masterType) {
                    final MasterType masterArray = MastersTypeImpl.this.getMasterArray(n);
                    MastersTypeImpl.this.setMasterArray(n, masterType);
                    return masterArray;
                }
                
                @Override
                public void add(final int n, final MasterType masterType) {
                    MastersTypeImpl.this.insertNewMaster(n).set((XmlObject)masterType);
                }
                
                @Override
                public MasterType remove(final int n) {
                    final MasterType masterArray = MastersTypeImpl.this.getMasterArray(n);
                    MastersTypeImpl.this.removeMaster(n);
                    return masterArray;
                }
                
                @Override
                public int size() {
                    return MastersTypeImpl.this.sizeOfMasterArray();
                }
            }
            return new MasterList();
        }
    }
    
    @Deprecated
    public MasterType[] getMasterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(MastersTypeImpl.MASTER$0, (List)list);
            final MasterType[] array = new MasterType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public MasterType getMasterArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final MasterType masterType = (MasterType)this.get_store().find_element_user(MastersTypeImpl.MASTER$0, n);
            if (masterType == null) {
                throw new IndexOutOfBoundsException();
            }
            return masterType;
        }
    }
    
    public int sizeOfMasterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(MastersTypeImpl.MASTER$0);
        }
    }
    
    public void setMasterArray(final MasterType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, MastersTypeImpl.MASTER$0);
    }
    
    public void setMasterArray(final int n, final MasterType masterType) {
        this.generatedSetterHelperImpl((XmlObject)masterType, MastersTypeImpl.MASTER$0, n, (short)2);
    }
    
    public MasterType insertNewMaster(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (MasterType)this.get_store().insert_element_user(MastersTypeImpl.MASTER$0, n);
        }
    }
    
    public MasterType addNewMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (MasterType)this.get_store().add_element_user(MastersTypeImpl.MASTER$0);
        }
    }
    
    public void removeMaster(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(MastersTypeImpl.MASTER$0, n);
        }
    }
    
    public List<MasterShortcutType> getMasterShortcutList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MasterShortcutList extends AbstractList<MasterShortcutType>
            {
                @Override
                public MasterShortcutType get(final int n) {
                    return MastersTypeImpl.this.getMasterShortcutArray(n);
                }
                
                @Override
                public MasterShortcutType set(final int n, final MasterShortcutType masterShortcutType) {
                    final MasterShortcutType masterShortcutArray = MastersTypeImpl.this.getMasterShortcutArray(n);
                    MastersTypeImpl.this.setMasterShortcutArray(n, masterShortcutType);
                    return masterShortcutArray;
                }
                
                @Override
                public void add(final int n, final MasterShortcutType masterShortcutType) {
                    MastersTypeImpl.this.insertNewMasterShortcut(n).set((XmlObject)masterShortcutType);
                }
                
                @Override
                public MasterShortcutType remove(final int n) {
                    final MasterShortcutType masterShortcutArray = MastersTypeImpl.this.getMasterShortcutArray(n);
                    MastersTypeImpl.this.removeMasterShortcut(n);
                    return masterShortcutArray;
                }
                
                @Override
                public int size() {
                    return MastersTypeImpl.this.sizeOfMasterShortcutArray();
                }
            }
            return new MasterShortcutList();
        }
    }
    
    @Deprecated
    public MasterShortcutType[] getMasterShortcutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(MastersTypeImpl.MASTERSHORTCUT$2, (List)list);
            final MasterShortcutType[] array = new MasterShortcutType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public MasterShortcutType getMasterShortcutArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final MasterShortcutType masterShortcutType = (MasterShortcutType)this.get_store().find_element_user(MastersTypeImpl.MASTERSHORTCUT$2, n);
            if (masterShortcutType == null) {
                throw new IndexOutOfBoundsException();
            }
            return masterShortcutType;
        }
    }
    
    public int sizeOfMasterShortcutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(MastersTypeImpl.MASTERSHORTCUT$2);
        }
    }
    
    public void setMasterShortcutArray(final MasterShortcutType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, MastersTypeImpl.MASTERSHORTCUT$2);
    }
    
    public void setMasterShortcutArray(final int n, final MasterShortcutType masterShortcutType) {
        this.generatedSetterHelperImpl((XmlObject)masterShortcutType, MastersTypeImpl.MASTERSHORTCUT$2, n, (short)2);
    }
    
    public MasterShortcutType insertNewMasterShortcut(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (MasterShortcutType)this.get_store().insert_element_user(MastersTypeImpl.MASTERSHORTCUT$2, n);
        }
    }
    
    public MasterShortcutType addNewMasterShortcut() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (MasterShortcutType)this.get_store().add_element_user(MastersTypeImpl.MASTERSHORTCUT$2);
        }
    }
    
    public void removeMasterShortcut(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(MastersTypeImpl.MASTERSHORTCUT$2, n);
        }
    }
    
    static {
        MASTER$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Master");
        MASTERSHORTCUT$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "MasterShortcut");
    }
}
