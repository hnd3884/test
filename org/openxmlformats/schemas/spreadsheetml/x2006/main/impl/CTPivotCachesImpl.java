package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotCachesImpl extends XmlComplexContentImpl implements CTPivotCaches
{
    private static final long serialVersionUID = 1L;
    private static final QName PIVOTCACHE$0;
    
    public CTPivotCachesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPivotCache> getPivotCacheList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PivotCacheList extends AbstractList<CTPivotCache>
            {
                @Override
                public CTPivotCache get(final int n) {
                    return CTPivotCachesImpl.this.getPivotCacheArray(n);
                }
                
                @Override
                public CTPivotCache set(final int n, final CTPivotCache ctPivotCache) {
                    final CTPivotCache pivotCacheArray = CTPivotCachesImpl.this.getPivotCacheArray(n);
                    CTPivotCachesImpl.this.setPivotCacheArray(n, ctPivotCache);
                    return pivotCacheArray;
                }
                
                @Override
                public void add(final int n, final CTPivotCache ctPivotCache) {
                    CTPivotCachesImpl.this.insertNewPivotCache(n).set((XmlObject)ctPivotCache);
                }
                
                @Override
                public CTPivotCache remove(final int n) {
                    final CTPivotCache pivotCacheArray = CTPivotCachesImpl.this.getPivotCacheArray(n);
                    CTPivotCachesImpl.this.removePivotCache(n);
                    return pivotCacheArray;
                }
                
                @Override
                public int size() {
                    return CTPivotCachesImpl.this.sizeOfPivotCacheArray();
                }
            }
            return new PivotCacheList();
        }
    }
    
    @Deprecated
    public CTPivotCache[] getPivotCacheArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPivotCachesImpl.PIVOTCACHE$0, (List)list);
            final CTPivotCache[] array = new CTPivotCache[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPivotCache getPivotCacheArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotCache ctPivotCache = (CTPivotCache)this.get_store().find_element_user(CTPivotCachesImpl.PIVOTCACHE$0, n);
            if (ctPivotCache == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPivotCache;
        }
    }
    
    public int sizeOfPivotCacheArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCachesImpl.PIVOTCACHE$0);
        }
    }
    
    public void setPivotCacheArray(final CTPivotCache[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPivotCachesImpl.PIVOTCACHE$0);
    }
    
    public void setPivotCacheArray(final int n, final CTPivotCache ctPivotCache) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotCache, CTPivotCachesImpl.PIVOTCACHE$0, n, (short)2);
    }
    
    public CTPivotCache insertNewPivotCache(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotCache)this.get_store().insert_element_user(CTPivotCachesImpl.PIVOTCACHE$0, n);
        }
    }
    
    public CTPivotCache addNewPivotCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotCache)this.get_store().add_element_user(CTPivotCachesImpl.PIVOTCACHE$0);
        }
    }
    
    public void removePivotCache(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCachesImpl.PIVOTCACHE$0, n);
        }
    }
    
    static {
        PIVOTCACHE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotCache");
    }
}
