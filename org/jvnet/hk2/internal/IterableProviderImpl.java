package org.jvnet.hk2.internal;

import java.util.NoSuchElementException;
import java.util.LinkedList;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.util.Collection;
import java.util.HashSet;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.util.List;
import java.util.Iterator;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.InjecteeImpl;
import java.util.Collections;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Unqualified;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.IterableProvider;

public class IterableProviderImpl<T> implements IterableProvider<T>
{
    private final ServiceLocatorImpl locator;
    private final Type requiredType;
    private final Set<Annotation> requiredQualifiers;
    private final Unqualified unqualified;
    private final Injectee originalInjectee;
    private final boolean isIterable;
    
    IterableProviderImpl(final ServiceLocatorImpl locator, final Type requiredType, final Set<Annotation> requiredQualifiers, final Unqualified unqualified, final Injectee originalInjectee, final boolean isIterable) {
        this.locator = locator;
        this.requiredType = requiredType;
        this.requiredQualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)requiredQualifiers);
        this.unqualified = unqualified;
        this.originalInjectee = originalInjectee;
        this.isIterable = isIterable;
    }
    
    private void justInTime() {
        final InjecteeImpl injectee = new InjecteeImpl(this.originalInjectee);
        injectee.setRequiredType(this.requiredType);
        injectee.setRequiredQualifiers((Set)this.requiredQualifiers);
        if (this.unqualified != null) {
            injectee.setUnqualified(this.unqualified);
        }
        this.locator.getInjecteeDescriptor((Injectee)injectee);
    }
    
    public T get() {
        this.justInTime();
        return this.locator.getUnqualifiedService(this.requiredType, this.unqualified, this.isIterable, (Annotation[])this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()]));
    }
    
    public ServiceHandle<T> getHandle() {
        this.justInTime();
        return this.locator.getUnqualifiedServiceHandle(this.requiredType, this.unqualified, this.isIterable, (Annotation[])this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()]));
    }
    
    public Iterator<T> iterator() {
        this.justInTime();
        final List<ServiceHandle<T>> handles = (List<ServiceHandle<T>>)ReflectionHelper.cast((Object)this.locator.getAllUnqualifiedServiceHandles(this.requiredType, this.unqualified, this.isIterable, (Annotation[])this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])));
        return new MyIterator<T>((List)handles);
    }
    
    public int getSize() {
        this.justInTime();
        return this.locator.getAllUnqualifiedServiceHandles(this.requiredType, this.unqualified, this.isIterable, (Annotation[])this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])).size();
    }
    
    public IterableProvider<T> named(final String name) {
        return this.qualifiedWith((Annotation)new NamedImpl(name));
    }
    
    public <U> IterableProvider<U> ofType(final Type type) {
        return (IterableProvider<U>)new IterableProviderImpl(this.locator, type, this.requiredQualifiers, this.unqualified, this.originalInjectee, this.isIterable);
    }
    
    public IterableProvider<T> qualifiedWith(final Annotation... qualifiers) {
        final HashSet<Annotation> moreAnnotations = new HashSet<Annotation>(this.requiredQualifiers);
        for (final Annotation qualifier : qualifiers) {
            moreAnnotations.add(qualifier);
        }
        return (IterableProvider<T>)new IterableProviderImpl(this.locator, this.requiredType, moreAnnotations, this.unqualified, this.originalInjectee, this.isIterable);
    }
    
    public Iterable<ServiceHandle<T>> handleIterator() {
        this.justInTime();
        final List<ServiceHandle<T>> handles = (List<ServiceHandle<T>>)ReflectionHelper.cast((Object)this.locator.getAllServiceHandles(this.requiredType, (Annotation[])this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])));
        return new HandleIterable<T>((List)handles);
    }
    
    @Override
    public String toString() {
        return "IterableProviderImpl(" + Pretty.type(this.requiredType) + "," + Pretty.collection((Collection)this.requiredQualifiers) + "," + System.identityHashCode(this) + ")";
    }
    
    private static class MyIterator<U> implements Iterator<U>
    {
        private final LinkedList<ServiceHandle<U>> handles;
        
        private MyIterator(final List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }
        
        @Override
        public boolean hasNext() {
            return !this.handles.isEmpty();
        }
        
        @Override
        public U next() {
            if (this.handles.isEmpty()) {
                throw new NoSuchElementException();
            }
            final ServiceHandle<U> nextHandle = this.handles.removeFirst();
            return (U)nextHandle.getService();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class HandleIterable<U> implements Iterable<ServiceHandle<U>>
    {
        private final List<ServiceHandle<U>> handles;
        
        private HandleIterable(final List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }
        
        @Override
        public Iterator<ServiceHandle<U>> iterator() {
            return new MyHandleIterator<U>((List)this.handles);
        }
    }
    
    private static class MyHandleIterator<U> implements Iterator<ServiceHandle<U>>
    {
        private final LinkedList<ServiceHandle<U>> handles;
        
        private MyHandleIterator(final List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }
        
        @Override
        public boolean hasNext() {
            return !this.handles.isEmpty();
        }
        
        @Override
        public ServiceHandle<U> next() {
            return this.handles.removeFirst();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
