package javax.naming;

import java.util.Enumeration;
import java.util.Vector;
import java.io.Serializable;

public class Reference implements Cloneable, Serializable
{
    protected String className;
    protected Vector<RefAddr> addrs;
    protected String classFactory;
    protected String classFactoryLocation;
    private static final long serialVersionUID = -1673475790065791735L;
    
    public Reference(final String className) {
        this.addrs = null;
        this.classFactory = null;
        this.classFactoryLocation = null;
        this.className = className;
        this.addrs = new Vector<RefAddr>();
    }
    
    public Reference(final String className, final RefAddr refAddr) {
        this.addrs = null;
        this.classFactory = null;
        this.classFactoryLocation = null;
        this.className = className;
        (this.addrs = new Vector<RefAddr>()).addElement(refAddr);
    }
    
    public Reference(final String s, final String classFactory, final String classFactoryLocation) {
        this(s);
        this.classFactory = classFactory;
        this.classFactoryLocation = classFactoryLocation;
    }
    
    public Reference(final String s, final RefAddr refAddr, final String classFactory, final String classFactoryLocation) {
        this(s, refAddr);
        this.classFactory = classFactory;
        this.classFactoryLocation = classFactoryLocation;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getFactoryClassName() {
        return this.classFactory;
    }
    
    public String getFactoryClassLocation() {
        return this.classFactoryLocation;
    }
    
    public RefAddr get(final String s) {
        for (int size = this.addrs.size(), i = 0; i < size; ++i) {
            final RefAddr refAddr = this.addrs.elementAt(i);
            if (refAddr.getType().compareTo(s) == 0) {
                return refAddr;
            }
        }
        return null;
    }
    
    public RefAddr get(final int n) {
        return this.addrs.elementAt(n);
    }
    
    public Enumeration<RefAddr> getAll() {
        return this.addrs.elements();
    }
    
    public int size() {
        return this.addrs.size();
    }
    
    public void add(final RefAddr refAddr) {
        this.addrs.addElement(refAddr);
    }
    
    public void add(final int n, final RefAddr refAddr) {
        this.addrs.insertElementAt(refAddr, n);
    }
    
    public Object remove(final int n) {
        final RefAddr element = this.addrs.elementAt(n);
        this.addrs.removeElementAt(n);
        return element;
    }
    
    public void clear() {
        this.addrs.setSize(0);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof Reference) {
            final Reference reference = (Reference)o;
            if (reference.className.equals(this.className) && reference.size() == this.size()) {
                final Enumeration<RefAddr> all = this.getAll();
                final Enumeration<RefAddr> all2 = reference.getAll();
                while (all.hasMoreElements()) {
                    if (!all.nextElement().equals(all2.nextElement())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.className.hashCode();
        final Enumeration<RefAddr> all = this.getAll();
        while (all.hasMoreElements()) {
            hashCode += all.nextElement().hashCode();
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Reference Class Name: " + this.className + "\n");
        for (int size = this.addrs.size(), i = 0; i < size; ++i) {
            sb.append(this.get(i).toString());
        }
        return sb.toString();
    }
    
    public Object clone() {
        final Reference reference = new Reference(this.className, this.classFactory, this.classFactoryLocation);
        final Enumeration<RefAddr> all = this.getAll();
        reference.addrs = new Vector<RefAddr>();
        while (all.hasMoreElements()) {
            reference.addrs.addElement(all.nextElement());
        }
        return reference;
    }
}
