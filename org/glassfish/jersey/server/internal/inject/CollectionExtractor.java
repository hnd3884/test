package org.glassfish.jersey.server.internal.inject;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.SortedSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ParamConverter;
import java.util.Collection;

abstract class CollectionExtractor<T> extends AbstractParamValueExtractor<T> implements MultivaluedParameterExtractor<Collection<T>>
{
    protected CollectionExtractor(final ParamConverter<T> converter, final String parameterName, final String defaultStringValue) {
        super(converter, parameterName, defaultStringValue);
    }
    
    @Override
    public Collection<T> extract(final MultivaluedMap<String, String> parameters) {
        final List<String> stringList = (List<String>)parameters.get((Object)this.getName());
        final Collection<T> valueList = this.newCollection();
        if (stringList != null) {
            for (final String v : stringList) {
                valueList.add(this.fromString(v));
            }
        }
        else if (this.isDefaultValueRegistered()) {
            valueList.add(this.defaultValue());
        }
        return valueList;
    }
    
    protected abstract Collection<T> newCollection();
    
    public static <T> CollectionExtractor getInstance(final Class<?> collectionType, final ParamConverter<T> converter, final String parameterName, final String defaultValueString) {
        if (List.class == collectionType) {
            return new ListValueOf(converter, parameterName, defaultValueString);
        }
        if (Set.class == collectionType) {
            return new SetValueOf(converter, parameterName, defaultValueString);
        }
        if (SortedSet.class == collectionType) {
            return new SortedSetValueOf(converter, parameterName, defaultValueString);
        }
        throw new ProcessingException(LocalizationMessages.COLLECTION_EXTRACTOR_TYPE_UNSUPPORTED());
    }
    
    private static final class ListValueOf<T> extends CollectionExtractor<T>
    {
        ListValueOf(final ParamConverter<T> converter, final String parameter, final String defaultValueString) {
            super(converter, parameter, defaultValueString);
        }
        
        @Override
        protected List<T> newCollection() {
            return new ArrayList<T>();
        }
    }
    
    private static final class SetValueOf<T> extends CollectionExtractor<T>
    {
        SetValueOf(final ParamConverter<T> converter, final String parameter, final String defaultValueString) {
            super(converter, parameter, defaultValueString);
        }
        
        @Override
        protected Set<T> newCollection() {
            return new HashSet<T>();
        }
    }
    
    private static final class SortedSetValueOf<T> extends CollectionExtractor<T>
    {
        SortedSetValueOf(final ParamConverter<T> converter, final String parameter, final String defaultValueString) {
            super(converter, parameter, defaultValueString);
        }
        
        @Override
        protected SortedSet<T> newCollection() {
            return new TreeSet<T>();
        }
    }
}
