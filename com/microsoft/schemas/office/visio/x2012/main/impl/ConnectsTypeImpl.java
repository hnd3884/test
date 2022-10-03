package com.microsoft.schemas.office.visio.x2012.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.ConnectType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.ConnectsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ConnectsTypeImpl extends XmlComplexContentImpl implements ConnectsType
{
    private static final long serialVersionUID = 1L;
    private static final QName CONNECT$0;
    
    public ConnectsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<ConnectType> getConnectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ConnectList extends AbstractList<ConnectType>
            {
                @Override
                public ConnectType get(final int n) {
                    return ConnectsTypeImpl.this.getConnectArray(n);
                }
                
                @Override
                public ConnectType set(final int n, final ConnectType connectType) {
                    final ConnectType connectArray = ConnectsTypeImpl.this.getConnectArray(n);
                    ConnectsTypeImpl.this.setConnectArray(n, connectType);
                    return connectArray;
                }
                
                @Override
                public void add(final int n, final ConnectType connectType) {
                    ConnectsTypeImpl.this.insertNewConnect(n).set((XmlObject)connectType);
                }
                
                @Override
                public ConnectType remove(final int n) {
                    final ConnectType connectArray = ConnectsTypeImpl.this.getConnectArray(n);
                    ConnectsTypeImpl.this.removeConnect(n);
                    return connectArray;
                }
                
                @Override
                public int size() {
                    return ConnectsTypeImpl.this.sizeOfConnectArray();
                }
            }
            return new ConnectList();
        }
    }
    
    @Deprecated
    public ConnectType[] getConnectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(ConnectsTypeImpl.CONNECT$0, (List)list);
            final ConnectType[] array = new ConnectType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public ConnectType getConnectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ConnectType connectType = (ConnectType)this.get_store().find_element_user(ConnectsTypeImpl.CONNECT$0, n);
            if (connectType == null) {
                throw new IndexOutOfBoundsException();
            }
            return connectType;
        }
    }
    
    public int sizeOfConnectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ConnectsTypeImpl.CONNECT$0);
        }
    }
    
    public void setConnectArray(final ConnectType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, ConnectsTypeImpl.CONNECT$0);
    }
    
    public void setConnectArray(final int n, final ConnectType connectType) {
        this.generatedSetterHelperImpl((XmlObject)connectType, ConnectsTypeImpl.CONNECT$0, n, (short)2);
    }
    
    public ConnectType insertNewConnect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ConnectType)this.get_store().insert_element_user(ConnectsTypeImpl.CONNECT$0, n);
        }
    }
    
    public ConnectType addNewConnect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ConnectType)this.get_store().add_element_user(ConnectsTypeImpl.CONNECT$0);
        }
    }
    
    public void removeConnect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ConnectsTypeImpl.CONNECT$0, n);
        }
    }
    
    static {
        CONNECT$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Connect");
    }
}
