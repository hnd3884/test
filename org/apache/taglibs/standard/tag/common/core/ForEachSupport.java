package org.apache.taglibs.standard.tag.common.core;

import javax.servlet.jsp.jstl.core.IteratedValueExpression;
import javax.servlet.jsp.jstl.core.IndexedValueExpression;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.taglibs.standard.resources.Resources;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Enumeration;
import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.IteratedExpression;
import javax.el.ValueExpression;
import java.util.Iterator;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

public abstract class ForEachSupport extends LoopTagSupport
{
    protected Iterator items;
    protected Object rawItems;
    
    protected void prepare() throws JspTagException {
        if (this.rawItems == null) {
            this.items = new ToEndIterator(this.end);
        }
        else if (this.rawItems instanceof ValueExpression) {
            this.deferredExpression = (ValueExpression)this.rawItems;
            final Object o = this.deferredExpression.getValue(this.pageContext.getELContext());
            final Iterator iterator = this.toIterator(o);
            if (this.isIndexed(o)) {
                this.items = new IndexedDeferredIterator(iterator, this.deferredExpression);
            }
            else {
                this.items = new IteratedDeferredIterator(iterator, new IteratedExpression(this.deferredExpression, this.getDelims()));
            }
        }
        else {
            this.items = this.toIterator(this.rawItems);
        }
    }
    
    private Iterator toIterator(final Object rawItems) throws JspTagException {
        if (rawItems instanceof Collection) {
            return ((Collection)rawItems).iterator();
        }
        if (rawItems.getClass().isArray()) {
            return new ArrayIterator(rawItems);
        }
        if (rawItems instanceof Iterator) {
            return (Iterator)rawItems;
        }
        if (rawItems instanceof Enumeration) {
            return new EnumerationIterator((Enumeration)rawItems);
        }
        if (rawItems instanceof Map) {
            return ((Map)rawItems).entrySet().iterator();
        }
        if (rawItems instanceof String) {
            return new EnumerationIterator((Enumeration)new StringTokenizer((String)rawItems, ","));
        }
        throw new JspTagException(Resources.getMessage("FOREACH_BAD_ITEMS"));
    }
    
    private boolean isIndexed(final Object o) {
        return o.getClass().isArray();
    }
    
    protected boolean hasNext() throws JspTagException {
        return this.items.hasNext();
    }
    
    protected Object next() throws JspTagException {
        return this.items.next();
    }
    
    public void release() {
        super.release();
        this.items = null;
        this.rawItems = null;
    }
    
    private static class ToEndIterator extends ReadOnlyIterator
    {
        private final int end;
        private int i;
        
        private ToEndIterator(final int end) {
            this.end = end;
        }
        
        public boolean hasNext() {
            return this.i <= this.end;
        }
        
        public Object next() {
            if (this.i <= this.end) {
                return this.i++;
            }
            throw new NoSuchElementException();
        }
    }
    
    private static class EnumerationIterator extends ReadOnlyIterator
    {
        private final Enumeration e;
        
        private EnumerationIterator(final Enumeration e) {
            this.e = e;
        }
        
        public boolean hasNext() {
            return this.e.hasMoreElements();
        }
        
        public Object next() {
            return this.e.nextElement();
        }
    }
    
    private static class ArrayIterator extends ReadOnlyIterator
    {
        private final Object array;
        private final int length;
        private int i;
        
        private ArrayIterator(final Object array) {
            this.i = 0;
            this.array = array;
            this.length = Array.getLength(array);
        }
        
        public boolean hasNext() {
            return this.i < this.length;
        }
        
        public Object next() {
            try {
                return Array.get(this.array, this.i++);
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }
    }
    
    private static class IndexedDeferredIterator extends DeferredIterator
    {
        private final ValueExpression itemsValueExpression;
        
        private IndexedDeferredIterator(final Iterator iterator, final ValueExpression itemsValueExpression) {
            super(iterator);
            this.itemsValueExpression = itemsValueExpression;
        }
        
        public Object next() {
            this.iterator.next();
            return new IndexedValueExpression(this.itemsValueExpression, this.currentIndex++);
        }
    }
    
    private static class IteratedDeferredIterator extends DeferredIterator
    {
        private final IteratedExpression itemsValueIteratedExpression;
        
        private IteratedDeferredIterator(final Iterator iterator, final IteratedExpression itemsValueIteratedExpression) {
            super(iterator);
            this.itemsValueIteratedExpression = itemsValueIteratedExpression;
        }
        
        public Object next() {
            this.iterator.next();
            return new IteratedValueExpression(this.itemsValueIteratedExpression, this.currentIndex++);
        }
    }
    
    private abstract static class DeferredIterator extends ReadOnlyIterator
    {
        protected final Iterator iterator;
        protected int currentIndex;
        
        protected DeferredIterator(final Iterator iterator) {
            this.currentIndex = 0;
            this.iterator = iterator;
        }
        
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
    }
    
    private abstract static class ReadOnlyIterator implements Iterator
    {
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
