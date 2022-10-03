package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.bind.JAXBException;
import java.util.concurrent.Callable;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.istack.internal.SAXException2;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.util.List;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Stack;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.WeakHashMap;
import com.sun.xml.internal.bind.v2.ClassFactory;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.lang.ref.WeakReference;
import java.util.Map;

public abstract class Lister<BeanT, PropT, ItemT, PackT>
{
    private static final Map<Class, WeakReference<Lister>> arrayListerCache;
    static final Map<Class, Lister> primitiveArrayListers;
    public static final Lister ERROR;
    private static final ListIterator EMPTY_ITERATOR;
    private static final Class[] COLLECTION_IMPL_CLASSES;
    
    protected Lister() {
    }
    
    public abstract ListIterator<ItemT> iterator(final PropT p0, final XMLSerializer p1);
    
    public abstract PackT startPacking(final BeanT p0, final Accessor<BeanT, PropT> p1) throws AccessorException;
    
    public abstract void addToPack(final PackT p0, final ItemT p1) throws AccessorException;
    
    public abstract void endPacking(final PackT p0, final BeanT p1, final Accessor<BeanT, PropT> p2) throws AccessorException;
    
    public abstract void reset(final BeanT p0, final Accessor<BeanT, PropT> p1) throws AccessorException;
    
    public static <BeanT, PropT, ItemT, PackT> Lister<BeanT, PropT, ItemT, PackT> create(final Type fieldType, final ID idness, final Adapter<Type, Class> adapter) {
        final Class rawType = Utils.REFLECTION_NAVIGATOR.erasure(fieldType);
        Class itemType;
        Lister l;
        if (rawType.isArray()) {
            itemType = rawType.getComponentType();
            l = getArrayLister(itemType);
        }
        else {
            if (!Collection.class.isAssignableFrom(rawType)) {
                return null;
            }
            final Type bt = Utils.REFLECTION_NAVIGATOR.getBaseClass(fieldType, Collection.class);
            if (bt instanceof ParameterizedType) {
                itemType = Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)bt).getActualTypeArguments()[0]);
            }
            else {
                itemType = Object.class;
            }
            l = new CollectionLister(getImplClass(rawType));
        }
        if (idness == ID.IDREF) {
            l = new IDREFS(l, itemType);
        }
        if (adapter != null) {
            l = new AdaptedLister(l, adapter.adapterType);
        }
        return l;
    }
    
    private static Class getImplClass(final Class<?> fieldType) {
        return ClassFactory.inferImplClass(fieldType, Lister.COLLECTION_IMPL_CLASSES);
    }
    
    private static Lister getArrayLister(final Class componentType) {
        Lister l = null;
        if (componentType.isPrimitive()) {
            l = Lister.primitiveArrayListers.get(componentType);
        }
        else {
            final WeakReference<Lister> wr = Lister.arrayListerCache.get(componentType);
            if (wr != null) {
                l = wr.get();
            }
            if (l == null) {
                l = new ArrayLister(componentType);
                Lister.arrayListerCache.put(componentType, new WeakReference<Lister>(l));
            }
        }
        assert l != null;
        return l;
    }
    
    public static <A, B, C, D> Lister<A, B, C, D> getErrorInstance() {
        return Lister.ERROR;
    }
    
    static {
        arrayListerCache = Collections.synchronizedMap(new WeakHashMap<Class, WeakReference<Lister>>());
        primitiveArrayListers = new HashMap<Class, Lister>();
        PrimitiveArrayListerBoolean.register();
        PrimitiveArrayListerByte.register();
        PrimitiveArrayListerCharacter.register();
        PrimitiveArrayListerDouble.register();
        PrimitiveArrayListerFloat.register();
        PrimitiveArrayListerInteger.register();
        PrimitiveArrayListerLong.register();
        PrimitiveArrayListerShort.register();
        ERROR = new Lister() {
            @Override
            public ListIterator iterator(final Object o, final XMLSerializer context) {
                return Lister.EMPTY_ITERATOR;
            }
            
            @Override
            public Object startPacking(final Object o, final Accessor accessor) {
                return null;
            }
            
            @Override
            public void addToPack(final Object o, final Object o1) {
            }
            
            @Override
            public void endPacking(final Object o, final Object o1, final Accessor accessor) {
            }
            
            @Override
            public void reset(final Object o, final Accessor accessor) {
            }
        };
        EMPTY_ITERATOR = new ListIterator() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new IllegalStateException();
            }
        };
        COLLECTION_IMPL_CLASSES = new Class[] { ArrayList.class, LinkedList.class, HashSet.class, TreeSet.class, Stack.class };
    }
    
    private static final class ArrayLister<BeanT, ItemT> extends Lister<BeanT, ItemT[], ItemT, Pack<ItemT>>
    {
        private final Class<ItemT> itemType;
        
        public ArrayLister(final Class<ItemT> itemType) {
            this.itemType = itemType;
        }
        
        @Override
        public ListIterator<ItemT> iterator(final ItemT[] objects, final XMLSerializer context) {
            return new ListIterator<ItemT>() {
                int idx = 0;
                
                @Override
                public boolean hasNext() {
                    return this.idx < objects.length;
                }
                
                @Override
                public ItemT next() {
                    return objects[this.idx++];
                }
            };
        }
        
        @Override
        public Pack startPacking(final BeanT current, final Accessor<BeanT, ItemT[]> acc) {
            return new Pack((Class<ItemT>)this.itemType);
        }
        
        @Override
        public void addToPack(final Pack<ItemT> objects, final ItemT o) {
            objects.add(o);
        }
        
        @Override
        public void endPacking(final Pack<ItemT> pack, final BeanT bean, final Accessor<BeanT, ItemT[]> acc) throws AccessorException {
            acc.set(bean, pack.build());
        }
        
        @Override
        public void reset(final BeanT o, final Accessor<BeanT, ItemT[]> acc) throws AccessorException {
            acc.set(o, (ItemT[])Array.newInstance(this.itemType, 0));
        }
    }
    
    public static final class Pack<ItemT> extends ArrayList<ItemT>
    {
        private final Class<ItemT> itemType;
        
        public Pack(final Class<ItemT> itemType) {
            this.itemType = itemType;
        }
        
        public ItemT[] build() {
            return super.toArray((ItemT[])Array.newInstance(this.itemType, this.size()));
        }
    }
    
    public static final class CollectionLister<BeanT, T extends Collection> extends Lister<BeanT, T, Object, T>
    {
        private final Class<? extends T> implClass;
        
        public CollectionLister(final Class<? extends T> implClass) {
            this.implClass = implClass;
        }
        
        @Override
        public ListIterator iterator(final T collection, final XMLSerializer context) {
            final Iterator itr = collection.iterator();
            return new ListIterator() {
                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }
                
                @Override
                public Object next() {
                    return itr.next();
                }
            };
        }
        
        @Override
        public T startPacking(final BeanT bean, final Accessor<BeanT, T> acc) throws AccessorException {
            T collection = acc.get(bean);
            if (collection == null) {
                collection = ClassFactory.create((Class<T>)this.implClass);
                if (!acc.isAdapted()) {
                    acc.set(bean, collection);
                }
            }
            collection.clear();
            return collection;
        }
        
        @Override
        public void addToPack(final T collection, final Object o) {
            collection.add(o);
        }
        
        @Override
        public void endPacking(final T collection, final BeanT bean, final Accessor<BeanT, T> acc) throws AccessorException {
            try {
                if (acc.isAdapted()) {
                    acc.set(bean, collection);
                }
            }
            catch (final AccessorException ae) {
                if (acc.isAdapted()) {
                    throw ae;
                }
            }
        }
        
        @Override
        public void reset(final BeanT bean, final Accessor<BeanT, T> acc) throws AccessorException {
            final T collection = acc.get(bean);
            if (collection == null) {
                return;
            }
            collection.clear();
        }
    }
    
    private static final class IDREFS<BeanT, PropT> extends Lister<BeanT, PropT, String, Pack>
    {
        private final Lister<BeanT, PropT, Object, Object> core;
        private final Class itemType;
        
        public IDREFS(final Lister core, final Class itemType) {
            this.core = core;
            this.itemType = itemType;
        }
        
        @Override
        public ListIterator<String> iterator(final PropT prop, final XMLSerializer context) {
            final ListIterator i = this.core.iterator(prop, context);
            return new IDREFSIterator(i, context);
        }
        
        @Override
        public Pack startPacking(final BeanT bean, final Accessor<BeanT, PropT> acc) {
            return new Pack(bean, acc);
        }
        
        @Override
        public void addToPack(final Pack pack, final String item) {
            pack.add(item);
        }
        
        @Override
        public void endPacking(final Pack pack, final BeanT bean, final Accessor<BeanT, PropT> acc) {
        }
        
        @Override
        public void reset(final BeanT bean, final Accessor<BeanT, PropT> acc) throws AccessorException {
            this.core.reset(bean, acc);
        }
        
        private class Pack implements Patcher
        {
            private final BeanT bean;
            private final List<String> idrefs;
            private final UnmarshallingContext context;
            private final Accessor<BeanT, PropT> acc;
            private final LocatorEx location;
            
            public Pack(final BeanT bean, final Accessor<BeanT, PropT> acc) {
                this.idrefs = new ArrayList<String>();
                this.bean = bean;
                this.acc = acc;
                this.context = UnmarshallingContext.getInstance();
                this.location = new LocatorEx.Snapshot(this.context.getLocator());
                this.context.addPatcher(this);
            }
            
            public void add(final String item) {
                this.idrefs.add(item);
            }
            
            @Override
            public void run() throws SAXException {
                try {
                    final Object pack = IDREFS.this.core.startPacking(this.bean, this.acc);
                    for (final String id : this.idrefs) {
                        final Callable callable = this.context.getObjectFromId(id, IDREFS.this.itemType);
                        Object t;
                        try {
                            t = ((callable != null) ? callable.call() : null);
                        }
                        catch (final SAXException e) {
                            throw e;
                        }
                        catch (final Exception e2) {
                            throw new SAXException2(e2);
                        }
                        if (t == null) {
                            this.context.errorUnresolvedIDREF(this.bean, id, this.location);
                        }
                        else {
                            TODO.prototype();
                            IDREFS.this.core.addToPack(pack, t);
                        }
                    }
                    IDREFS.this.core.endPacking(pack, this.bean, this.acc);
                }
                catch (final AccessorException e3) {
                    this.context.handleError(e3);
                }
            }
        }
    }
    
    public static final class IDREFSIterator implements ListIterator<String>
    {
        private final ListIterator i;
        private final XMLSerializer context;
        private Object last;
        
        private IDREFSIterator(final ListIterator i, final XMLSerializer context) {
            this.i = i;
            this.context = context;
        }
        
        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
        
        public Object last() {
            return this.last;
        }
        
        @Override
        public String next() throws SAXException, JAXBException {
            this.last = this.i.next();
            final String id = this.context.grammar.getBeanInfo(this.last, true).getId(this.last, this.context);
            if (id == null) {
                this.context.errorMissingId(this.last);
            }
            return id;
        }
    }
}
