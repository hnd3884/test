package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAuthors;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAuthorsImpl extends XmlComplexContentImpl implements CTAuthors
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTHOR$0;
    
    public CTAuthorsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<String> getAuthorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AuthorList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTAuthorsImpl.this.getAuthorArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String authorArray = CTAuthorsImpl.this.getAuthorArray(n);
                    CTAuthorsImpl.this.setAuthorArray(n, s);
                    return authorArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTAuthorsImpl.this.insertAuthor(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String authorArray = CTAuthorsImpl.this.getAuthorArray(n);
                    CTAuthorsImpl.this.removeAuthor(n);
                    return authorArray;
                }
                
                @Override
                public int size() {
                    return CTAuthorsImpl.this.sizeOfAuthorArray();
                }
            }
            return new AuthorList();
        }
    }
    
    @Deprecated
    public String[] getAuthorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAuthorsImpl.AUTHOR$0, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getAuthorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTAuthorsImpl.AUTHOR$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<STXstring> xgetAuthorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AuthorList extends AbstractList<STXstring>
            {
                @Override
                public STXstring get(final int n) {
                    return CTAuthorsImpl.this.xgetAuthorArray(n);
                }
                
                @Override
                public STXstring set(final int n, final STXstring stXstring) {
                    final STXstring xgetAuthorArray = CTAuthorsImpl.this.xgetAuthorArray(n);
                    CTAuthorsImpl.this.xsetAuthorArray(n, stXstring);
                    return xgetAuthorArray;
                }
                
                @Override
                public void add(final int n, final STXstring stXstring) {
                    CTAuthorsImpl.this.insertNewAuthor(n).set((XmlObject)stXstring);
                }
                
                @Override
                public STXstring remove(final int n) {
                    final STXstring xgetAuthorArray = CTAuthorsImpl.this.xgetAuthorArray(n);
                    CTAuthorsImpl.this.removeAuthor(n);
                    return xgetAuthorArray;
                }
                
                @Override
                public int size() {
                    return CTAuthorsImpl.this.sizeOfAuthorArray();
                }
            }
            return new AuthorList();
        }
    }
    
    @Deprecated
    public STXstring[] xgetAuthorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAuthorsImpl.AUTHOR$0, (List)list);
            final STXstring[] array = new STXstring[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STXstring xgetAuthorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STXstring stXstring = (STXstring)this.get_store().find_element_user(CTAuthorsImpl.AUTHOR$0, n);
            if (stXstring == null) {
                throw new IndexOutOfBoundsException();
            }
            return stXstring;
        }
    }
    
    public int sizeOfAuthorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAuthorsImpl.AUTHOR$0);
        }
    }
    
    public void setAuthorArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTAuthorsImpl.AUTHOR$0);
        }
    }
    
    public void setAuthorArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTAuthorsImpl.AUTHOR$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAuthorArray(final STXstring[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTAuthorsImpl.AUTHOR$0);
        }
    }
    
    public void xsetAuthorArray(final int n, final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTAuthorsImpl.AUTHOR$0, n);
            if (stXstring2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void insertAuthor(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTAuthorsImpl.AUTHOR$0, n)).setStringValue(stringValue);
        }
    }
    
    public void addAuthor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTAuthorsImpl.AUTHOR$0)).setStringValue(stringValue);
        }
    }
    
    public STXstring insertNewAuthor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().insert_element_user(CTAuthorsImpl.AUTHOR$0, n);
        }
    }
    
    public STXstring addNewAuthor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().add_element_user(CTAuthorsImpl.AUTHOR$0);
        }
    }
    
    public void removeAuthor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAuthorsImpl.AUTHOR$0, n);
        }
    }
    
    static {
        AUTHOR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "author");
    }
}
