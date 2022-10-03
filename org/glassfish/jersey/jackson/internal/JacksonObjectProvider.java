package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import java.util.Stack;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.glassfish.jersey.message.filtering.spi.AbstractObjectProvider;

final class JacksonObjectProvider extends AbstractObjectProvider<FilterProvider>
{
    public FilterProvider transform(final ObjectGraph graph) {
        final FilteringPropertyFilter root = new FilteringPropertyFilter(graph.getEntityClass(), graph.getFields(), (Map)this.createSubfilters(graph.getEntityClass(), graph.getSubgraphs()));
        return new FilteringFilterProvider(root);
    }
    
    private Map<String, FilteringPropertyFilter> createSubfilters(final Class<?> entityClass, final Map<String, ObjectGraph> entitySubgraphs) {
        final Map<String, FilteringPropertyFilter> subfilters = new HashMap<String, FilteringPropertyFilter>();
        for (final Map.Entry<String, ObjectGraph> entry : entitySubgraphs.entrySet()) {
            final String fieldName = entry.getKey();
            final ObjectGraph graph = entry.getValue();
            final Map<String, ObjectGraph> subgraphs = graph.getSubgraphs(fieldName);
            Map<String, FilteringPropertyFilter> subSubfilters = new HashMap<String, FilteringPropertyFilter>();
            if (!subgraphs.isEmpty()) {
                final Class<?> subEntityClass = graph.getEntityClass();
                final Set<String> processed = Collections.singleton(this.subgraphIdentifier((Class)entityClass, fieldName, (Class)subEntityClass));
                subSubfilters = this.createSubfilters(fieldName, subEntityClass, subgraphs, processed);
            }
            final FilteringPropertyFilter filter = new FilteringPropertyFilter(graph.getEntityClass(), graph.getFields(fieldName), (Map)subSubfilters);
            subfilters.put(fieldName, filter);
        }
        return subfilters;
    }
    
    private Map<String, FilteringPropertyFilter> createSubfilters(final String parent, final Class<?> entityClass, final Map<String, ObjectGraph> entitySubgraphs, final Set<String> processed) {
        final Map<String, FilteringPropertyFilter> subfilters = new HashMap<String, FilteringPropertyFilter>();
        for (final Map.Entry<String, ObjectGraph> entry : entitySubgraphs.entrySet()) {
            final String fieldName = entry.getKey();
            final ObjectGraph graph = entry.getValue();
            final String path = parent + "." + fieldName;
            final Map<String, ObjectGraph> subgraphs = graph.getSubgraphs(path);
            final Class<?> subEntityClass = graph.getEntityClass();
            final String processedSubgraph = this.subgraphIdentifier((Class)entityClass, fieldName, (Class)subEntityClass);
            Map<String, FilteringPropertyFilter> subSubfilters = new HashMap<String, FilteringPropertyFilter>();
            if (!subgraphs.isEmpty() && !processed.contains(processedSubgraph)) {
                final Set<String> subProcessed = this.immutableSetOf((Set)processed, processedSubgraph);
                subSubfilters = this.createSubfilters(path, subEntityClass, subgraphs, subProcessed);
            }
            subfilters.put(fieldName, new FilteringPropertyFilter(graph.getEntityClass(), graph.getFields(path), (Map)subSubfilters));
        }
        return subfilters;
    }
    
    private static class FilteringFilterProvider extends FilterProvider
    {
        private final FilteringPropertyFilter root;
        private final Stack<FilteringPropertyFilter> stack;
        
        public FilteringFilterProvider(final FilteringPropertyFilter root) {
            this.stack = new Stack<FilteringPropertyFilter>();
            this.root = root;
        }
        
        public BeanPropertyFilter findFilter(final Object filterId) {
            throw new UnsupportedOperationException("Access to deprecated filters not supported");
        }
        
        public PropertyFilter findPropertyFilter(final Object filterId, final Object valueToFilter) {
            if (filterId instanceof String) {
                final String id = (String)filterId;
                if (id.equals(this.root.getEntityClass().getName())) {
                    this.stack.clear();
                    return (PropertyFilter)this.stack.push(this.root);
                }
                while (!this.stack.isEmpty()) {
                    final FilteringPropertyFilter peek = this.stack.peek();
                    final FilteringPropertyFilter subfilter = peek.findSubfilter(id);
                    if (subfilter != null) {
                        this.stack.push(subfilter);
                        if (valueToFilter instanceof Map) {
                            final Map<String, ?> map = (Map<String, ?>)valueToFilter;
                            return (PropertyFilter)new FilteringPropertyFilter((Class)Map.class, (Set)map.keySet(), (Map)Collections.emptyMap());
                        }
                        return (PropertyFilter)subfilter;
                    }
                    else {
                        this.stack.pop();
                    }
                }
            }
            return (PropertyFilter)SimpleBeanPropertyFilter.filterOutAllExcept(new String[0]);
        }
    }
    
    private static final class FilteringPropertyFilter implements PropertyFilter
    {
        private final Class<?> entityClass;
        private final Set<String> fields;
        private final Map<String, FilteringPropertyFilter> subfilters;
        
        private FilteringPropertyFilter(final Class<?> entityClass, final Set<String> fields, final Map<String, FilteringPropertyFilter> subfilters) {
            this.entityClass = entityClass;
            this.fields = fields;
            this.subfilters = subfilters;
        }
        
        private boolean include(final String fieldName) {
            return this.fields.contains(fieldName) || this.subfilters.containsKey(fieldName);
        }
        
        public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider prov, final PropertyWriter writer) throws Exception {
            if (this.include(writer.getName())) {
                writer.serializeAsField(pojo, jgen, prov);
            }
        }
        
        public void serializeAsElement(final Object elementValue, final JsonGenerator jgen, final SerializerProvider prov, final PropertyWriter writer) throws Exception {
            if (this.include(writer.getName())) {
                writer.serializeAsElement(elementValue, jgen, prov);
            }
        }
        
        public void depositSchemaProperty(final PropertyWriter writer, final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
            if (this.include(writer.getName())) {
                writer.depositSchemaProperty(propertiesNode, provider);
            }
        }
        
        public void depositSchemaProperty(final PropertyWriter writer, final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
            if (this.include(writer.getName())) {
                writer.depositSchemaProperty(objectVisitor, provider);
            }
        }
        
        public FilteringPropertyFilter findSubfilter(final String fieldName) {
            return this.subfilters.get(fieldName);
        }
        
        public Class<?> getEntityClass() {
            return this.entityClass;
        }
    }
}
