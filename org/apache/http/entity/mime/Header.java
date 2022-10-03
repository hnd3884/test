package org.apache.http.entity.mime;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class Header implements Iterable<MinimalField>
{
    private final List<MinimalField> fields;
    private final Map<String, List<MinimalField>> fieldMap;
    
    public Header() {
        this.fields = new LinkedList<MinimalField>();
        this.fieldMap = new HashMap<String, List<MinimalField>>();
    }
    
    public void addField(final MinimalField field) {
        if (field == null) {
            return;
        }
        final String key = field.getName().toLowerCase(Locale.US);
        List<MinimalField> values = this.fieldMap.get(key);
        if (values == null) {
            values = new LinkedList<MinimalField>();
            this.fieldMap.put(key, values);
        }
        values.add(field);
        this.fields.add(field);
    }
    
    public List<MinimalField> getFields() {
        return new ArrayList<MinimalField>(this.fields);
    }
    
    public MinimalField getField(final String name) {
        if (name == null) {
            return null;
        }
        final String key = name.toLowerCase(Locale.US);
        final List<MinimalField> list = this.fieldMap.get(key);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    
    public List<MinimalField> getFields(final String name) {
        if (name == null) {
            return null;
        }
        final String key = name.toLowerCase(Locale.US);
        final List<MinimalField> list = this.fieldMap.get(key);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<MinimalField>(list);
    }
    
    public int removeFields(final String name) {
        if (name == null) {
            return 0;
        }
        final String key = name.toLowerCase(Locale.US);
        final List<MinimalField> removed = this.fieldMap.remove(key);
        if (removed == null || removed.isEmpty()) {
            return 0;
        }
        this.fields.removeAll(removed);
        return removed.size();
    }
    
    public void setField(final MinimalField field) {
        if (field == null) {
            return;
        }
        final String key = field.getName().toLowerCase(Locale.US);
        final List<MinimalField> list = this.fieldMap.get(key);
        if (list == null || list.isEmpty()) {
            this.addField(field);
            return;
        }
        list.clear();
        list.add(field);
        int firstOccurrence = -1;
        int index = 0;
        final Iterator<MinimalField> it = this.fields.iterator();
        while (it.hasNext()) {
            final MinimalField f = it.next();
            if (f.getName().equalsIgnoreCase(field.getName())) {
                it.remove();
                if (firstOccurrence == -1) {
                    firstOccurrence = index;
                }
            }
            ++index;
        }
        this.fields.add(firstOccurrence, field);
    }
    
    public Iterator<MinimalField> iterator() {
        return Collections.unmodifiableList((List<? extends MinimalField>)this.fields).iterator();
    }
    
    @Override
    public String toString() {
        return this.fields.toString();
    }
}
