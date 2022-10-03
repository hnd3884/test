package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTTagsData;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerData;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCustomerDataListImpl extends XmlComplexContentImpl implements CTCustomerDataList
{
    private static final long serialVersionUID = 1L;
    private static final QName CUSTDATA$0;
    private static final QName TAGS$2;
    
    public CTCustomerDataListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCustomerData> getCustDataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustDataList extends AbstractList<CTCustomerData>
            {
                @Override
                public CTCustomerData get(final int n) {
                    return CTCustomerDataListImpl.this.getCustDataArray(n);
                }
                
                @Override
                public CTCustomerData set(final int n, final CTCustomerData ctCustomerData) {
                    final CTCustomerData custDataArray = CTCustomerDataListImpl.this.getCustDataArray(n);
                    CTCustomerDataListImpl.this.setCustDataArray(n, ctCustomerData);
                    return custDataArray;
                }
                
                @Override
                public void add(final int n, final CTCustomerData ctCustomerData) {
                    CTCustomerDataListImpl.this.insertNewCustData(n).set((XmlObject)ctCustomerData);
                }
                
                @Override
                public CTCustomerData remove(final int n) {
                    final CTCustomerData custDataArray = CTCustomerDataListImpl.this.getCustDataArray(n);
                    CTCustomerDataListImpl.this.removeCustData(n);
                    return custDataArray;
                }
                
                @Override
                public int size() {
                    return CTCustomerDataListImpl.this.sizeOfCustDataArray();
                }
            }
            return new CustDataList();
        }
    }
    
    @Deprecated
    public CTCustomerData[] getCustDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCustomerDataListImpl.CUSTDATA$0, (List)list);
            final CTCustomerData[] array = new CTCustomerData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomerData getCustDataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomerData ctCustomerData = (CTCustomerData)this.get_store().find_element_user(CTCustomerDataListImpl.CUSTDATA$0, n);
            if (ctCustomerData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomerData;
        }
    }
    
    public int sizeOfCustDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomerDataListImpl.CUSTDATA$0);
        }
    }
    
    public void setCustDataArray(final CTCustomerData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCustomerDataListImpl.CUSTDATA$0);
    }
    
    public void setCustDataArray(final int n, final CTCustomerData ctCustomerData) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomerData, CTCustomerDataListImpl.CUSTDATA$0, n, (short)2);
    }
    
    public CTCustomerData insertNewCustData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomerData)this.get_store().insert_element_user(CTCustomerDataListImpl.CUSTDATA$0, n);
        }
    }
    
    public CTCustomerData addNewCustData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomerData)this.get_store().add_element_user(CTCustomerDataListImpl.CUSTDATA$0);
        }
    }
    
    public void removeCustData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomerDataListImpl.CUSTDATA$0, n);
        }
    }
    
    public CTTagsData getTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTagsData ctTagsData = (CTTagsData)this.get_store().find_element_user(CTCustomerDataListImpl.TAGS$2, 0);
            if (ctTagsData == null) {
                return null;
            }
            return ctTagsData;
        }
    }
    
    public boolean isSetTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomerDataListImpl.TAGS$2) != 0;
        }
    }
    
    public void setTags(final CTTagsData ctTagsData) {
        this.generatedSetterHelperImpl((XmlObject)ctTagsData, CTCustomerDataListImpl.TAGS$2, 0, (short)1);
    }
    
    public CTTagsData addNewTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTagsData)this.get_store().add_element_user(CTCustomerDataListImpl.TAGS$2);
        }
    }
    
    public void unsetTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomerDataListImpl.TAGS$2, 0);
        }
    }
    
    static {
        CUSTDATA$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custData");
        TAGS$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "tags");
    }
}
