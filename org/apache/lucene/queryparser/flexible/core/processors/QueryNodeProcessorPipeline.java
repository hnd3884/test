package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.ListIterator;
import java.util.Collection;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import java.util.LinkedList;
import java.util.List;

public class QueryNodeProcessorPipeline implements QueryNodeProcessor, List<QueryNodeProcessor>
{
    private LinkedList<QueryNodeProcessor> processors;
    private QueryConfigHandler queryConfig;
    
    public QueryNodeProcessorPipeline() {
        this.processors = new LinkedList<QueryNodeProcessor>();
    }
    
    public QueryNodeProcessorPipeline(final QueryConfigHandler queryConfigHandler) {
        this.processors = new LinkedList<QueryNodeProcessor>();
        this.queryConfig = queryConfigHandler;
    }
    
    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }
    
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        for (final QueryNodeProcessor processor : this.processors) {
            queryTree = processor.process(queryTree);
        }
        return queryTree;
    }
    
    @Override
    public void setQueryConfigHandler(final QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
        for (final QueryNodeProcessor processor : this.processors) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
    }
    
    @Override
    public boolean add(final QueryNodeProcessor processor) {
        final boolean added = this.processors.add(processor);
        if (added) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return added;
    }
    
    @Override
    public void add(final int index, final QueryNodeProcessor processor) {
        this.processors.add(index, processor);
        processor.setQueryConfigHandler(this.queryConfig);
    }
    
    @Override
    public boolean addAll(final Collection<? extends QueryNodeProcessor> c) {
        final boolean anyAdded = this.processors.addAll(c);
        for (final QueryNodeProcessor processor : c) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return anyAdded;
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends QueryNodeProcessor> c) {
        final boolean anyAdded = this.processors.addAll(index, c);
        for (final QueryNodeProcessor processor : c) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return anyAdded;
    }
    
    @Override
    public void clear() {
        this.processors.clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.processors.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.processors.containsAll(c);
    }
    
    @Override
    public QueryNodeProcessor get(final int index) {
        return this.processors.get(index);
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.processors.indexOf(o);
    }
    
    @Override
    public boolean isEmpty() {
        return this.processors.isEmpty();
    }
    
    @Override
    public Iterator<QueryNodeProcessor> iterator() {
        return this.processors.iterator();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.processors.lastIndexOf(o);
    }
    
    @Override
    public ListIterator<QueryNodeProcessor> listIterator() {
        return this.processors.listIterator();
    }
    
    @Override
    public ListIterator<QueryNodeProcessor> listIterator(final int index) {
        return this.processors.listIterator(index);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.processors.remove(o);
    }
    
    @Override
    public QueryNodeProcessor remove(final int index) {
        return this.processors.remove(index);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.processors.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.processors.retainAll(c);
    }
    
    @Override
    public QueryNodeProcessor set(final int index, final QueryNodeProcessor processor) {
        final QueryNodeProcessor oldProcessor = this.processors.set(index, processor);
        if (oldProcessor != processor) {
            processor.setQueryConfigHandler(this.queryConfig);
        }
        return oldProcessor;
    }
    
    @Override
    public int size() {
        return this.processors.size();
    }
    
    @Override
    public List<QueryNodeProcessor> subList(final int fromIndex, final int toIndex) {
        return this.processors.subList(fromIndex, toIndex);
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.processors.toArray(array);
    }
    
    @Override
    public Object[] toArray() {
        return this.processors.toArray();
    }
}
