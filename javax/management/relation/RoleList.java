package javax.management.relation;

import java.util.Iterator;
import com.sun.jmx.mbeanserver.Util;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class RoleList extends ArrayList<Object>
{
    private transient boolean typeSafe;
    private transient boolean tainted;
    private static final long serialVersionUID = 5568344346499649313L;
    
    public RoleList() {
    }
    
    public RoleList(final int n) {
        super(n);
    }
    
    public RoleList(final List<Role> list) throws IllegalArgumentException {
        if (list == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        checkTypeSafe(list);
        super.addAll(list);
    }
    
    public List<Role> asList() {
        if (!this.typeSafe) {
            if (this.tainted) {
                checkTypeSafe(this);
            }
            this.typeSafe = true;
        }
        return Util.cast(this);
    }
    
    public void add(final Role role) throws IllegalArgumentException {
        if (role == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        super.add(role);
    }
    
    public void add(final int n, final Role role) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (role == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        super.add(n, role);
    }
    
    public void set(final int n, final Role role) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (role == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        super.set(n, role);
    }
    
    public boolean addAll(final RoleList list) throws IndexOutOfBoundsException {
        return list == null || super.addAll(list);
    }
    
    public boolean addAll(final int n, final RoleList list) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        return super.addAll(n, list);
    }
    
    @Override
    public boolean add(final Object o) {
        if (!this.tainted) {
            this.tainted = isTainted(o);
        }
        if (this.typeSafe) {
            checkTypeSafe(o);
        }
        return super.add(o);
    }
    
    @Override
    public void add(final int n, final Object o) {
        if (!this.tainted) {
            this.tainted = isTainted(o);
        }
        if (this.typeSafe) {
            checkTypeSafe(o);
        }
        super.add(n, o);
    }
    
    @Override
    public boolean addAll(final Collection<?> collection) {
        if (!this.tainted) {
            this.tainted = isTainted(collection);
        }
        if (this.typeSafe) {
            checkTypeSafe(collection);
        }
        return super.addAll(collection);
    }
    
    @Override
    public boolean addAll(final int n, final Collection<?> collection) {
        if (!this.tainted) {
            this.tainted = isTainted(collection);
        }
        if (this.typeSafe) {
            checkTypeSafe(collection);
        }
        return super.addAll(n, collection);
    }
    
    @Override
    public Object set(final int n, final Object o) {
        if (!this.tainted) {
            this.tainted = isTainted(o);
        }
        if (this.typeSafe) {
            checkTypeSafe(o);
        }
        return super.set(n, o);
    }
    
    private static void checkTypeSafe(final Object o) {
        try {
            final Role role = (Role)o;
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static void checkTypeSafe(final Collection<?> collection) {
        try {
            for (Role role : collection) {}
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static boolean isTainted(final Object o) {
        try {
            checkTypeSafe(o);
        }
        catch (final IllegalArgumentException ex) {
            return true;
        }
        return false;
    }
    
    private static boolean isTainted(final Collection<?> collection) {
        try {
            checkTypeSafe(collection);
        }
        catch (final IllegalArgumentException ex) {
            return true;
        }
        return false;
    }
}
