package com.azul.crs.shared.models;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;

public class Schema extends Payload
{
    private final String entity;
    private final List<Field> fields;
    
    public Schema() {
        this("", Collections.emptyList());
    }
    
    public Schema(final String entity, final List<Field> fields) {
        this.entity = entity;
        this.fields = fields;
    }
    
    public List<Field> getFields() {
        return Collections.unmodifiableList((List<? extends Field>)this.fields);
    }
    
    public Map<String, List<ComparisonOperation>> getTypes() {
        return Type.getTypes();
    }
    
    public String getEntity() {
        return this.entity;
    }
    
    public enum Type
    {
        NUMERIC(ComparisonOperation.allExcept(ComparisonOperation.LIKE, ComparisonOperation.CONTAINS)), 
        STRING(ComparisonOperation.allExcept(new ComparisonOperation[0])), 
        BOOLEAN(Arrays.asList(ComparisonOperation.EQUAL, ComparisonOperation.NOT_EQUAL)), 
        DATETIME(ComparisonOperation.allExcept(ComparisonOperation.LIKE, ComparisonOperation.CONTAINS)), 
        OBJECT(Collections.emptyList());
        
        private final List<ComparisonOperation> operations;
        
        private Type(final List<ComparisonOperation> operations) {
            this.operations = Collections.unmodifiableList((List<? extends ComparisonOperation>)operations);
        }
        
        public List<ComparisonOperation> getOperations() {
            return this.operations;
        }
        
        public static Map<String, List<ComparisonOperation>> getTypes() {
            final Map<String, List<ComparisonOperation>> result = new HashMap<String, List<ComparisonOperation>>();
            for (final Type type : values()) {
                result.put(type.name(), type.getOperations());
            }
            return result;
        }
    }
    
    public static class Field
    {
        private final String name;
        private final String qualifiedName;
        private final String displayName;
        private final Type type;
        private final Integer decimalPlaces;
        
        public Field(final Field other) {
            this.name = other.getName();
            this.qualifiedName = other.getQualifiedName();
            this.type = other.getType();
            this.decimalPlaces = other.decimalPlaces;
            this.displayName = other.getDisplayName();
        }
        
        public Field() {
            this("", "", Type.OBJECT);
        }
        
        public Field(final String prefix, final String name, final Type type) {
            this(prefix, name, name, type);
        }
        
        public Field(final String prefix, final String name, final String displayName, final Type type) {
            this(prefix, name, displayName, type, null);
        }
        
        public Field(final String prefix, final String name, final String displayName, final Type type, final Integer decimalPlaces) {
            this.name = name;
            this.qualifiedName = ((prefix == null) ? name : (prefix + '.' + name));
            this.type = type;
            this.decimalPlaces = decimalPlaces;
            this.displayName = ((displayName == null) ? name : displayName);
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getQualifiedName() {
            return this.qualifiedName;
        }
        
        public String getDisplayName() {
            return this.displayName;
        }
        
        public Integer getDecimalPlaces() {
            return this.decimalPlaces;
        }
        
        public Type getType() {
            return this.type;
        }
        
        @Override
        public String toString() {
            return "Field{name='" + this.name + '\'' + ", qualifiedName='" + this.qualifiedName + '\'' + ", displayName='" + this.displayName + '\'' + ", type=" + this.type + '}';
        }
    }
    
    public enum LogicalOperation
    {
        AND("AND"), 
        OR("OR");
        
        private final String displayName;
        
        private LogicalOperation(final String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return this.displayName;
        }
    }
    
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum ComparisonOperation
    {
        EQUAL("=", "Equals to"), 
        NOT_EQUAL("!=", "Not equal"), 
        LESS("<", "Less"), 
        LESS_OR_EQUAL("<=", "Less or equal"), 
        GREATER(">", "Greater"), 
        GREATER_OR_EQUAL(">=", "Greater or equal"), 
        IN("IN", "One of"), 
        RANGE("RANGE", "In range"), 
        CONTAINS("CONTAINS", "Contains substring"), 
        LIKE("LIKE", "Like (regexp)");
        
        private final String name;
        private final String displayName;
        
        private ComparisonOperation(final String name, final String displayName) {
            this.name = name;
            this.displayName = displayName;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getDisplayName() {
            return this.displayName;
        }
        
        public static List<ComparisonOperation> allExcept(final ComparisonOperation... except) {
            final List<ComparisonOperation> exceptList = Arrays.asList(except);
            return Arrays.stream(values()).filter(e -> !exceptList.contains(e)).collect((Collector<? super ComparisonOperation, ?, List<ComparisonOperation>>)Collectors.toList());
        }
    }
}
