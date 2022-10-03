package org.glassfish.jersey.server.internal.inject;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Set;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;

abstract class StringCollectionExtractor implements MultivaluedParameterExtractor<Collection<String>>
{
    private final String parameter;
    private final String defaultValue;
    
    protected StringCollectionExtractor(final String parameterName, final String defaultValue) {
        this.parameter = parameterName;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultValueString() {
        return this.defaultValue;
    }
    
    @Override
    public Collection<String> extract(final MultivaluedMap<String, String> parameters) {
        final List<String> stringList = (List<String>)parameters.get((Object)this.parameter);
        final Collection<String> collection = this.newCollection();
        if (stringList != null) {
            collection.addAll(stringList);
        }
        else if (this.defaultValue != null) {
            collection.add(this.defaultValue);
        }
        return collection;
    }
    
    protected abstract Collection<String> newCollection();
    
    public static StringCollectionExtractor getInstance(final Class<?> collectionType, final String parameterName, final String defaultValue) {
        if (List.class == collectionType) {
            return new ListString(parameterName, defaultValue);
        }
        if (Set.class == collectionType) {
            return new SetString(parameterName, defaultValue);
        }
        if (SortedSet.class == collectionType) {
            return new SortedSetString(parameterName, defaultValue);
        }
        throw new RuntimeException("Unsupported collection type: " + collectionType.getName());
    }
    
    private static final class ListString extends StringCollectionExtractor
    {
        public ListString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected List<String> newCollection() {
            return new ArrayList<String>();
        }
    }
    
    private static final class SetString extends StringCollectionExtractor
    {
        public SetString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected Set<String> newCollection() {
            return new HashSet<String>();
        }
    }
    
    private static final class SortedSetString extends StringCollectionExtractor
    {
        public SortedSetString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected SortedSet<String> newCollection() {
            return new TreeSet<String>();
        }
    }
}
