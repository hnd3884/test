package org.apache.lucene.queryparser.flexible.core.config;

import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import java.util.LinkedList;

public abstract class QueryConfigHandler extends AbstractQueryConfig
{
    private final LinkedList<FieldConfigListener> listeners;
    
    public QueryConfigHandler() {
        this.listeners = new LinkedList<FieldConfigListener>();
    }
    
    public FieldConfig getFieldConfig(final String fieldName) {
        final FieldConfig fieldConfig = new FieldConfig(StringUtils.toString(fieldName));
        for (final FieldConfigListener listener : this.listeners) {
            listener.buildFieldConfig(fieldConfig);
        }
        return fieldConfig;
    }
    
    public void addFieldConfigListener(final FieldConfigListener listener) {
        this.listeners.add(listener);
    }
}
