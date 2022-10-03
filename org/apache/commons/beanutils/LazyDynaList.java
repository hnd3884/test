package org.apache.commons.beanutils;

import java.util.Map;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class LazyDynaList extends ArrayList<Object>
{
    private DynaClass elementDynaClass;
    private transient WrapDynaClass wrapDynaClass;
    private Class<?> elementType;
    private Class<?> elementDynaBeanType;
    
    public LazyDynaList() {
    }
    
    public LazyDynaList(final int capacity) {
        super(capacity);
    }
    
    public LazyDynaList(final DynaClass elementDynaClass) {
        this.setElementDynaClass(elementDynaClass);
    }
    
    public LazyDynaList(final Class<?> elementType) {
        this.setElementType(elementType);
    }
    
    public LazyDynaList(final Collection<?> collection) {
        super(collection.size());
        this.addAll(collection);
    }
    
    public LazyDynaList(final Object[] array) {
        super(array.length);
        for (final Object element : array) {
            this.add(element);
        }
    }
    
    @Override
    public void add(final int index, final Object element) {
        final DynaBean dynaBean = this.transform(element);
        this.growList(index);
        super.add(index, dynaBean);
    }
    
    @Override
    public boolean add(final Object element) {
        final DynaBean dynaBean = this.transform(element);
        return super.add(dynaBean);
    }
    
    @Override
    public boolean addAll(final Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(this.size() + collection.size());
        for (final Object e : collection) {
            this.add(e);
        }
        return true;
    }
    
    @Override
    public boolean addAll(final int index, final Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(((index > this.size()) ? index : this.size()) + collection.size());
        if (this.size() == 0) {
            this.transform(collection.iterator().next());
        }
        this.growList(index);
        int currentIndex = index;
        for (final Object e : collection) {
            this.add(currentIndex++, e);
        }
        return true;
    }
    
    @Override
    public Object get(final int index) {
        this.growList(index + 1);
        return super.get(index);
    }
    
    @Override
    public Object set(final int index, final Object element) {
        final DynaBean dynaBean = this.transform(element);
        this.growList(index + 1);
        return super.set(index, dynaBean);
    }
    
    @Override
    public Object[] toArray() {
        if (this.size() == 0 && this.elementType == null) {
            return new LazyDynaBean[0];
        }
        final Object[] array = (Object[])Array.newInstance(this.elementType, this.size());
        for (int i = 0; i < this.size(); ++i) {
            if (Map.class.isAssignableFrom(this.elementType)) {
                array[i] = ((LazyDynaMap)this.get(i)).getMap();
            }
            else if (DynaBean.class.isAssignableFrom(this.elementType)) {
                array[i] = this.get(i);
            }
            else {
                array[i] = ((WrapDynaBean)this.get(i)).getInstance();
            }
        }
        return array;
    }
    
    @Override
    public <T> T[] toArray(final T[] model) {
        final Class<?> arrayType = model.getClass().getComponentType();
        if (DynaBean.class.isAssignableFrom(arrayType) || (this.size() == 0 && this.elementType == null)) {
            return super.toArray(model);
        }
        if (arrayType.isAssignableFrom(this.elementType)) {
            T[] array;
            if (model.length >= this.size()) {
                array = model;
            }
            else {
                final T[] tempArray = array = (T[])Array.newInstance(arrayType, this.size());
            }
            for (int i = 0; i < this.size(); ++i) {
                Object elem;
                if (Map.class.isAssignableFrom(this.elementType)) {
                    elem = ((LazyDynaMap)this.get(i)).getMap();
                }
                else if (DynaBean.class.isAssignableFrom(this.elementType)) {
                    elem = this.get(i);
                }
                else {
                    elem = ((WrapDynaBean)this.get(i)).getInstance();
                }
                Array.set(array, i, elem);
            }
            return array;
        }
        throw new IllegalArgumentException("Invalid array type: " + arrayType.getName() + " - not compatible with '" + this.elementType.getName());
    }
    
    public DynaBean[] toDynaBeanArray() {
        if (this.size() == 0 && this.elementDynaBeanType == null) {
            return new LazyDynaBean[0];
        }
        final DynaBean[] array = (DynaBean[])Array.newInstance(this.elementDynaBeanType, this.size());
        for (int i = 0; i < this.size(); ++i) {
            array[i] = (DynaBean)this.get(i);
        }
        return array;
    }
    
    public void setElementType(final Class<?> elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("Element Type is missing");
        }
        final boolean changeType = this.elementType != null && !this.elementType.equals(elementType);
        if (changeType && this.size() > 0) {
            throw new IllegalStateException("Element Type cannot be reset");
        }
        this.elementType = elementType;
        Object object = null;
        try {
            object = elementType.newInstance();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Error creating type: " + elementType.getName() + " - " + e);
        }
        DynaBean dynaBean = null;
        if (Map.class.isAssignableFrom(elementType)) {
            dynaBean = this.createDynaBeanForMapProperty(object);
            this.elementDynaClass = dynaBean.getDynaClass();
        }
        else if (DynaBean.class.isAssignableFrom(elementType)) {
            dynaBean = (DynaBean)object;
            this.elementDynaClass = dynaBean.getDynaClass();
        }
        else {
            dynaBean = new WrapDynaBean(object);
            this.wrapDynaClass = (WrapDynaClass)dynaBean.getDynaClass();
        }
        this.elementDynaBeanType = dynaBean.getClass();
        if (WrapDynaBean.class.isAssignableFrom(this.elementDynaBeanType)) {
            this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        }
        else if (LazyDynaMap.class.isAssignableFrom(this.elementDynaBeanType)) {
            this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }
    }
    
    public void setElementDynaClass(final DynaClass elementDynaClass) {
        if (elementDynaClass == null) {
            throw new IllegalArgumentException("Element DynaClass is missing");
        }
        if (this.size() > 0) {
            throw new IllegalStateException("Element DynaClass cannot be reset");
        }
        try {
            final DynaBean dynaBean = elementDynaClass.newInstance();
            this.elementDynaBeanType = dynaBean.getClass();
            if (WrapDynaBean.class.isAssignableFrom(this.elementDynaBeanType)) {
                this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
                this.wrapDynaClass = (WrapDynaClass)elementDynaClass;
            }
            else if (LazyDynaMap.class.isAssignableFrom(this.elementDynaBeanType)) {
                this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
                this.elementDynaClass = elementDynaClass;
            }
            else {
                this.elementType = dynaBean.getClass();
                this.elementDynaClass = elementDynaClass;
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Error creating DynaBean from " + elementDynaClass.getClass().getName() + " - " + e);
        }
    }
    
    private void growList(final int requiredSize) {
        if (requiredSize < this.size()) {
            return;
        }
        this.ensureCapacity(requiredSize + 1);
        for (int i = this.size(); i < requiredSize; ++i) {
            final DynaBean dynaBean = this.transform(null);
            super.add(dynaBean);
        }
    }
    
    private DynaBean transform(final Object element) {
        DynaBean dynaBean = null;
        Class<?> newDynaBeanType = null;
        Class<?> newElementType = null;
        Label_0171: {
            if (element == null) {
                if (this.elementType == null) {
                    this.setElementDynaClass(new LazyDynaClass());
                }
                if (this.getDynaClass() == null) {
                    this.setElementType(this.elementType);
                }
                try {
                    dynaBean = this.getDynaClass().newInstance();
                    newDynaBeanType = dynaBean.getClass();
                    break Label_0171;
                }
                catch (final Exception e) {
                    throw new IllegalArgumentException("Error creating DynaBean: " + this.getDynaClass().getClass().getName() + " - " + e);
                }
            }
            newElementType = element.getClass();
            if (Map.class.isAssignableFrom(element.getClass())) {
                dynaBean = this.createDynaBeanForMapProperty(element);
            }
            else if (DynaBean.class.isAssignableFrom(element.getClass())) {
                dynaBean = (DynaBean)element;
            }
            else {
                dynaBean = new WrapDynaBean(element);
            }
            newDynaBeanType = dynaBean.getClass();
        }
        newElementType = dynaBean.getClass();
        if (WrapDynaBean.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        }
        else if (LazyDynaMap.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }
        if (this.elementType != null && !newElementType.equals(this.elementType)) {
            throw new IllegalArgumentException("Element Type " + newElementType + " doesn't match other elements " + this.elementType);
        }
        return dynaBean;
    }
    
    private LazyDynaMap createDynaBeanForMapProperty(final Object value) {
        final Map<String, Object> valueMap = (Map<String, Object>)value;
        return new LazyDynaMap(valueMap);
    }
    
    private DynaClass getDynaClass() {
        return (this.elementDynaClass == null) ? this.wrapDynaClass : this.elementDynaClass;
    }
}
