package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMapInfo;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMapInfoImpl extends XmlComplexContentImpl implements CTMapInfo
{
    private static final long serialVersionUID = 1L;
    private static final QName SCHEMA$0;
    private static final QName MAP$2;
    private static final QName SELECTIONNAMESPACES$4;
    
    public CTMapInfoImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSchema> getSchemaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SchemaList extends AbstractList<CTSchema>
            {
                @Override
                public CTSchema get(final int n) {
                    return CTMapInfoImpl.this.getSchemaArray(n);
                }
                
                @Override
                public CTSchema set(final int n, final CTSchema ctSchema) {
                    final CTSchema schemaArray = CTMapInfoImpl.this.getSchemaArray(n);
                    CTMapInfoImpl.this.setSchemaArray(n, ctSchema);
                    return schemaArray;
                }
                
                @Override
                public void add(final int n, final CTSchema ctSchema) {
                    CTMapInfoImpl.this.insertNewSchema(n).set((XmlObject)ctSchema);
                }
                
                @Override
                public CTSchema remove(final int n) {
                    final CTSchema schemaArray = CTMapInfoImpl.this.getSchemaArray(n);
                    CTMapInfoImpl.this.removeSchema(n);
                    return schemaArray;
                }
                
                @Override
                public int size() {
                    return CTMapInfoImpl.this.sizeOfSchemaArray();
                }
            }
            return new SchemaList();
        }
    }
    
    @Deprecated
    public CTSchema[] getSchemaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTMapInfoImpl.SCHEMA$0, (List)list);
            final CTSchema[] array = new CTSchema[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSchema getSchemaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchema ctSchema = (CTSchema)this.get_store().find_element_user(CTMapInfoImpl.SCHEMA$0, n);
            if (ctSchema == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSchema;
        }
    }
    
    public int sizeOfSchemaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMapInfoImpl.SCHEMA$0);
        }
    }
    
    public void setSchemaArray(final CTSchema[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTMapInfoImpl.SCHEMA$0);
    }
    
    public void setSchemaArray(final int n, final CTSchema ctSchema) {
        this.generatedSetterHelperImpl((XmlObject)ctSchema, CTMapInfoImpl.SCHEMA$0, n, (short)2);
    }
    
    public CTSchema insertNewSchema(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchema)this.get_store().insert_element_user(CTMapInfoImpl.SCHEMA$0, n);
        }
    }
    
    public CTSchema addNewSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchema)this.get_store().add_element_user(CTMapInfoImpl.SCHEMA$0);
        }
    }
    
    public void removeSchema(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMapInfoImpl.SCHEMA$0, n);
        }
    }
    
    public List<CTMap> getMapList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MapList extends AbstractList<CTMap>
            {
                @Override
                public CTMap get(final int n) {
                    return CTMapInfoImpl.this.getMapArray(n);
                }
                
                @Override
                public CTMap set(final int n, final CTMap ctMap) {
                    final CTMap mapArray = CTMapInfoImpl.this.getMapArray(n);
                    CTMapInfoImpl.this.setMapArray(n, ctMap);
                    return mapArray;
                }
                
                @Override
                public void add(final int n, final CTMap ctMap) {
                    CTMapInfoImpl.this.insertNewMap(n).set((XmlObject)ctMap);
                }
                
                @Override
                public CTMap remove(final int n) {
                    final CTMap mapArray = CTMapInfoImpl.this.getMapArray(n);
                    CTMapInfoImpl.this.removeMap(n);
                    return mapArray;
                }
                
                @Override
                public int size() {
                    return CTMapInfoImpl.this.sizeOfMapArray();
                }
            }
            return new MapList();
        }
    }
    
    @Deprecated
    public CTMap[] getMapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTMapInfoImpl.MAP$2, (List)list);
            final CTMap[] array = new CTMap[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMap getMapArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMap ctMap = (CTMap)this.get_store().find_element_user(CTMapInfoImpl.MAP$2, n);
            if (ctMap == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMap;
        }
    }
    
    public int sizeOfMapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMapInfoImpl.MAP$2);
        }
    }
    
    public void setMapArray(final CTMap[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTMapInfoImpl.MAP$2);
    }
    
    public void setMapArray(final int n, final CTMap ctMap) {
        this.generatedSetterHelperImpl((XmlObject)ctMap, CTMapInfoImpl.MAP$2, n, (short)2);
    }
    
    public CTMap insertNewMap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMap)this.get_store().insert_element_user(CTMapInfoImpl.MAP$2, n);
        }
    }
    
    public CTMap addNewMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMap)this.get_store().add_element_user(CTMapInfoImpl.MAP$2);
        }
    }
    
    public void removeMap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMapInfoImpl.MAP$2, n);
        }
    }
    
    public String getSelectionNamespaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSelectionNamespaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
        }
    }
    
    public void setSelectionNamespaces(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSelectionNamespaces(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTMapInfoImpl.SELECTIONNAMESPACES$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        SCHEMA$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "Schema");
        MAP$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "Map");
        SELECTIONNAMESPACES$4 = new QName("", "SelectionNamespaces");
    }
}
