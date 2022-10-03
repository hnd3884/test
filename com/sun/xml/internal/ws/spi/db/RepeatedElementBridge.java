package com.sun.xml.internal.ws.spi.db;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamWriter;

public class RepeatedElementBridge<T> implements XMLBridge<T>
{
    XMLBridge<T> delegate;
    CollectionHandler collectionHandler;
    static final CollectionHandler ListHandler;
    static final CollectionHandler HashSetHandler;
    
    public RepeatedElementBridge(final TypeInfo typeInfo, final XMLBridge xb) {
        this.delegate = xb;
        this.collectionHandler = create(typeInfo);
    }
    
    public CollectionHandler collectionHandler() {
        return this.collectionHandler;
    }
    
    @Override
    public BindingContext context() {
        return this.delegate.context();
    }
    
    @Override
    public void marshal(final T object, final XMLStreamWriter output, final AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, output, am);
    }
    
    @Override
    public void marshal(final T object, final OutputStream output, final NamespaceContext nsContext, final AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, output, nsContext, am);
    }
    
    @Override
    public void marshal(final T object, final Node output) throws JAXBException {
        this.delegate.marshal(object, output);
    }
    
    @Override
    public void marshal(final T object, final ContentHandler contentHandler, final AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, contentHandler, am);
    }
    
    @Override
    public void marshal(final T object, final Result result) throws JAXBException {
        this.delegate.marshal(object, result);
    }
    
    @Override
    public T unmarshal(final XMLStreamReader in, final AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(in, au);
    }
    
    @Override
    public T unmarshal(final Source in, final AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(in, au);
    }
    
    @Override
    public T unmarshal(final InputStream in) throws JAXBException {
        return this.delegate.unmarshal(in);
    }
    
    @Override
    public T unmarshal(final Node n, final AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(n, au);
    }
    
    @Override
    public TypeInfo getTypeInfo() {
        return this.delegate.getTypeInfo();
    }
    
    @Override
    public boolean supportOutputStream() {
        return this.delegate.supportOutputStream();
    }
    
    public static CollectionHandler create(final TypeInfo ti) {
        final Class javaClass = (Class)ti.type;
        if (javaClass.isArray()) {
            return new ArrayHandler((Class)ti.getItemType().type);
        }
        if (List.class.equals(javaClass) || Collection.class.equals(javaClass)) {
            return RepeatedElementBridge.ListHandler;
        }
        if (Set.class.equals(javaClass) || HashSet.class.equals(javaClass)) {
            return RepeatedElementBridge.HashSetHandler;
        }
        return new BaseCollectionHandler(javaClass);
    }
    
    static {
        ListHandler = new BaseCollectionHandler(List.class) {
            @Override
            public Object convert(final List list) {
                return list;
            }
        };
        HashSetHandler = new BaseCollectionHandler(HashSet.class) {
            @Override
            public Object convert(final List list) {
                return new HashSet(list);
            }
        };
    }
    
    static class BaseCollectionHandler implements CollectionHandler
    {
        Class type;
        
        BaseCollectionHandler(final Class c) {
            this.type = c;
        }
        
        @Override
        public int getSize(final Object c) {
            return ((Collection)c).size();
        }
        
        @Override
        public Object convert(final List list) {
            try {
                final Object o = this.type.newInstance();
                ((Collection)o).addAll(list);
                return o;
            }
            catch (final Exception e) {
                e.printStackTrace();
                return list;
            }
        }
        
        @Override
        public Iterator iterator(final Object c) {
            return ((Collection)c).iterator();
        }
    }
    
    static class ArrayHandler implements CollectionHandler
    {
        Class componentClass;
        
        public ArrayHandler(final Class component) {
            this.componentClass = component;
        }
        
        @Override
        public int getSize(final Object c) {
            return Array.getLength(c);
        }
        
        @Override
        public Object convert(final List list) {
            final Object array = Array.newInstance(this.componentClass, list.size());
            for (int i = 0; i < list.size(); ++i) {
                Array.set(array, i, list.get(i));
            }
            return array;
        }
        
        @Override
        public Iterator iterator(final Object c) {
            return new Iterator() {
                int index = 0;
                
                @Override
                public boolean hasNext() {
                    return c != null && Array.getLength(c) != 0 && this.index != Array.getLength(c);
                }
                
                @Override
                public Object next() throws NoSuchElementException {
                    Object retVal = null;
                    try {
                        retVal = Array.get(c, this.index++);
                    }
                    catch (final ArrayIndexOutOfBoundsException ex) {
                        throw new NoSuchElementException();
                    }
                    return retVal;
                }
                
                @Override
                public void remove() {
                }
            };
        }
    }
    
    public interface CollectionHandler
    {
        int getSize(final Object p0);
        
        Iterator iterator(final Object p0);
        
        Object convert(final List p0);
    }
}
