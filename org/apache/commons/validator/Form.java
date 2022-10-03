package org.apache.commons.validator;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.commons.collections.FastHashMap;
import java.util.List;
import java.io.Serializable;

public class Form implements Serializable
{
    protected String name;
    protected List lFields;
    protected FastHashMap hFields;
    protected String inherit;
    private boolean processed;
    
    public Form() {
        this.name = null;
        this.lFields = new ArrayList();
        this.hFields = new FastHashMap();
        this.inherit = null;
        this.processed = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void addField(final Field f) {
        this.lFields.add(f);
        this.hFields.put((Object)f.getKey(), (Object)f);
    }
    
    public List getFields() {
        return Collections.unmodifiableList((List<?>)this.lFields);
    }
    
    public Field getField(final String fieldName) {
        return (Field)this.hFields.get((Object)fieldName);
    }
    
    public boolean containsField(final String fieldName) {
        return this.hFields.containsKey((Object)fieldName);
    }
    
    protected void merge(final Form depends) {
        final List templFields = new ArrayList();
        final Map temphFields = (Map)new FastHashMap();
        final Iterator dependsIt = depends.getFields().iterator();
        while (dependsIt.hasNext()) {
            final Field defaultField = dependsIt.next();
            if (defaultField != null) {
                final String fieldKey = defaultField.getKey();
                if (!this.containsField(fieldKey)) {
                    templFields.add(defaultField);
                    temphFields.put(fieldKey, defaultField);
                }
                else {
                    final Field old = this.getField(fieldKey);
                    this.hFields.remove((Object)fieldKey);
                    this.lFields.remove(old);
                    templFields.add(old);
                    temphFields.put(fieldKey, old);
                }
            }
        }
        this.lFields.addAll(0, templFields);
        this.hFields.putAll(temphFields);
    }
    
    protected void process(final Map globalConstants, final Map constants, final Map forms) {
        if (this.isProcessed()) {
            return;
        }
        int n = 0;
        if (this.isExtending()) {
            final Form parent = forms.get(this.inherit);
            if (parent != null) {
                if (!parent.isProcessed()) {
                    parent.process(constants, globalConstants, forms);
                }
                final Iterator i = parent.getFields().iterator();
                while (i.hasNext()) {
                    final Field f = i.next();
                    if (this.hFields.get((Object)f.getKey()) == null) {
                        this.lFields.add(n, f);
                        this.hFields.put((Object)f.getKey(), (Object)f);
                        ++n;
                    }
                }
            }
        }
        this.hFields.setFast(true);
        final Iterator j = this.lFields.listIterator(n);
        while (j.hasNext()) {
            final Field f2 = j.next();
            f2.process(globalConstants, constants);
        }
        this.processed = true;
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer();
        results.append("Form: ");
        results.append(this.name);
        results.append("\n");
        final Iterator i = this.lFields.iterator();
        while (i.hasNext()) {
            results.append("\tField: \n");
            results.append(i.next());
            results.append("\n");
        }
        return results.toString();
    }
    
    ValidatorResults validate(final Map params, final Map actions, final int page) throws ValidatorException {
        return this.validate(params, actions, page, null);
    }
    
    ValidatorResults validate(final Map params, final Map actions, final int page, final String fieldName) throws ValidatorException {
        final ValidatorResults results = new ValidatorResults();
        params.put("org.apache.commons.validator.ValidatorResults", results);
        if (fieldName != null) {
            final Field field = (Field)this.hFields.get((Object)fieldName);
            if (field == null) {
                throw new ValidatorException("Unknown field " + fieldName + " in form " + this.getName());
            }
            params.put("org.apache.commons.validator.Field", field);
            if (field.getPage() <= page) {
                results.merge(field.validate(params, actions));
            }
        }
        else {
            final Iterator fields = this.lFields.iterator();
            while (fields.hasNext()) {
                final Field field2 = fields.next();
                params.put("org.apache.commons.validator.Field", field2);
                if (field2.getPage() <= page) {
                    results.merge(field2.validate(params, actions));
                }
            }
        }
        return results;
    }
    
    public boolean isProcessed() {
        return this.processed;
    }
    
    public String getExtends() {
        return this.inherit;
    }
    
    public void setExtends(final String inherit) {
        this.inherit = inherit;
    }
    
    public boolean isExtending() {
        return this.inherit != null;
    }
    
    protected Map getFieldMap() {
        return (Map)this.hFields;
    }
}
