package javax.management;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public class AttributeList extends ArrayList<Object>
{
    private transient volatile boolean typeSafe;
    private transient volatile boolean tainted;
    private static final long serialVersionUID = -4077085769279709076L;
    
    public AttributeList() {
    }
    
    public AttributeList(final int n) {
        super(n);
    }
    
    public AttributeList(final AttributeList list) {
        super(list);
    }
    
    public AttributeList(final List<Attribute> list) {
        if (list == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        this.adding(list);
        super.addAll(list);
    }
    
    public List<Attribute> asList() {
        this.typeSafe = true;
        if (this.tainted) {
            this.adding(this);
        }
        return (List<Attribute>)this;
    }
    
    public void add(final Attribute attribute) {
        super.add(attribute);
    }
    
    public void add(final int n, final Attribute attribute) {
        try {
            super.add(n, attribute);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new RuntimeOperationsException(ex, "The specified index is out of range");
        }
    }
    
    public void set(final int n, final Attribute attribute) {
        try {
            super.set(n, attribute);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new RuntimeOperationsException(ex, "The specified index is out of range");
        }
    }
    
    public boolean addAll(final AttributeList list) {
        return super.addAll(list);
    }
    
    public boolean addAll(final int n, final AttributeList list) {
        try {
            return super.addAll(n, list);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new RuntimeOperationsException(ex, "The specified index is out of range");
        }
    }
    
    @Override
    public boolean add(final Object o) {
        this.adding(o);
        return super.add(o);
    }
    
    @Override
    public void add(final int n, final Object o) {
        this.adding(o);
        super.add(n, o);
    }
    
    @Override
    public boolean addAll(final Collection<?> collection) {
        this.adding(collection);
        return super.addAll(collection);
    }
    
    @Override
    public boolean addAll(final int n, final Collection<?> collection) {
        this.adding(collection);
        return super.addAll(n, collection);
    }
    
    @Override
    public Object set(final int n, final Object o) {
        this.adding(o);
        return super.set(n, o);
    }
    
    private void adding(final Object o) {
        if (o == null || o instanceof Attribute) {
            return;
        }
        if (this.typeSafe) {
            throw new IllegalArgumentException("Not an Attribute: " + o);
        }
        this.tainted = true;
    }
    
    private void adding(final Collection<?> collection) {
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.adding(iterator.next());
        }
    }
}
