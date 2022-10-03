package com.maverick.ssh.components;

import com.maverick.util.Arrays;
import com.maverick.ssh.SshException;
import java.util.Vector;
import java.util.Hashtable;

public class ComponentFactory implements Cloneable
{
    protected Hashtable supported;
    protected Vector order;
    Class c;
    private boolean b;
    
    public synchronized String changePositionofAlgorithm(final String s, int size) throws SshException {
        if (size < 0) {
            throw new SshException("index out of bounds", 4);
        }
        if (size >= this.order.size()) {
            size = this.order.size();
        }
        final int index = this.order.indexOf(s);
        if (index < size) {
            this.order.insertElementAt(s, size);
            this.order.removeElementAt(index);
        }
        else {
            this.order.removeElementAt(index);
            this.order.insertElementAt(s, size);
        }
        return this.order.elementAt(0);
    }
    
    public synchronized String createNewOrdering(final int[] array) throws SshException {
        if (array.length > this.order.size()) {
            throw new SshException("too many indicies", 4);
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] < 0 || array[i] >= this.order.size()) {
                throw new SshException("index out of bounds", 4);
            }
            this.order.insertElementAt(this.order.elementAt(array[i]), this.order.size());
        }
        Arrays.sort(array);
        for (int j = array.length - 1; j >= 0; --j) {
            this.order.removeElementAt(array[j]);
        }
        for (int k = 0; k < array.length; ++k) {
            final Object element = this.order.elementAt(this.order.size() - 1);
            this.order.removeElementAt(this.order.size() - 1);
            this.order.insertElementAt(element, 0);
        }
        return this.order.elementAt(0);
    }
    
    public ComponentFactory(final Class c) {
        this.supported = new Hashtable();
        this.order = new Vector();
        this.c = c;
    }
    
    public boolean contains(final String s) {
        return this.supported.containsKey(s);
    }
    
    public synchronized String list(final String s) {
        return this.b(s);
    }
    
    public synchronized void add(final String s, final Class clazz) {
        if (this.b) {
            throw new IllegalStateException("Component factory is locked. Components cannot be added");
        }
        this.supported.put(s, clazz);
        if (!this.order.contains(s)) {
            this.order.addElement(s);
        }
    }
    
    public Object getInstance(final String s) throws SshException {
        if (this.supported.containsKey(s)) {
            try {
                return this.createInstance(s, this.supported.get(s));
            }
            catch (final Throwable t) {
                throw new SshException(t.getMessage(), 5);
            }
        }
        throw new SshException(s + " is not supported", 7);
    }
    
    protected Object createInstance(final String s, final Class clazz) throws Throwable {
        return clazz.newInstance();
    }
    
    private synchronized String b(final String s) {
        final StringBuffer sb = new StringBuffer();
        final int index = this.order.indexOf(s);
        if (index != -1) {
            sb.append(s);
        }
        for (int i = 0; i < this.order.size(); ++i) {
            if (index != i) {
                sb.append("," + (String)this.order.elementAt(i));
            }
        }
        if (index == -1 && sb.length() > 0) {
            return sb.toString().substring(1);
        }
        return sb.toString();
    }
    
    public synchronized void remove(final String s) {
        this.supported.remove(s);
        this.order.removeElement(s);
    }
    
    public synchronized void clear() {
        if (this.b) {
            throw new IllegalStateException("Component factory is locked. Removing all components renders it unusable");
        }
        this.supported.clear();
        this.order.removeAllElements();
    }
    
    public Object clone() {
        final ComponentFactory componentFactory = new ComponentFactory(this.c);
        componentFactory.order = (Vector)this.order.clone();
        componentFactory.supported = (Hashtable)this.supported.clone();
        componentFactory.b = this.b;
        return componentFactory;
    }
    
    public String[] toArray() {
        return this.order.toArray(new String[this.order.size()]);
    }
    
    public void lockComponents() {
        this.b = true;
    }
}
