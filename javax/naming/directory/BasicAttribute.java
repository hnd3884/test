package javax.naming.directory;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.naming.OperationNotSupportedException;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.lang.reflect.Array;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Vector;

public class BasicAttribute implements Attribute
{
    protected String attrID;
    protected transient Vector<Object> values;
    protected boolean ordered;
    private static final long serialVersionUID = 6743528196119291326L;
    
    @Override
    public Object clone() {
        BasicAttribute basicAttribute;
        try {
            basicAttribute = (BasicAttribute)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            basicAttribute = new BasicAttribute(this.attrID, this.ordered);
        }
        basicAttribute.values = (Vector)this.values.clone();
        return basicAttribute;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof Attribute) {
            final Attribute attribute = (Attribute)o;
            if (this.isOrdered() != attribute.isOrdered()) {
                return false;
            }
            final int size;
            if (this.attrID.equals(attribute.getID()) && (size = this.size()) == attribute.size()) {
                try {
                    if (this.isOrdered()) {
                        for (int i = 0; i < size; ++i) {
                            if (!valueEquals(this.get(i), attribute.get(i))) {
                                return false;
                            }
                        }
                    }
                    else {
                        final NamingEnumeration<?> all = attribute.getAll();
                        while (all.hasMoreElements()) {
                            if (this.find(all.nextElement()) < 0) {
                                return false;
                            }
                        }
                    }
                }
                catch (final NamingException ex) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.attrID.hashCode();
        for (int size = this.values.size(), i = 0; i < size; ++i) {
            final Object element = this.values.elementAt(i);
            if (element != null) {
                if (element.getClass().isArray()) {
                    for (int length = Array.getLength(element), j = 0; j < length; ++j) {
                        final Object value = Array.get(element, j);
                        if (value != null) {
                            hashCode += value.hashCode();
                        }
                    }
                }
                else {
                    hashCode += element.hashCode();
                }
            }
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(this.attrID + ": ");
        if (this.values.size() == 0) {
            sb.append("No values");
        }
        else {
            int n = 1;
            final Enumeration<Object> elements = this.values.elements();
            while (elements.hasMoreElements()) {
                if (n == 0) {
                    sb.append(", ");
                }
                sb.append(elements.nextElement());
                n = 0;
            }
        }
        return sb.toString();
    }
    
    public BasicAttribute(final String s) {
        this(s, false);
    }
    
    public BasicAttribute(final String s, final Object o) {
        this(s, o, false);
    }
    
    public BasicAttribute(final String attrID, final boolean ordered) {
        this.ordered = false;
        this.attrID = attrID;
        this.values = new Vector<Object>();
        this.ordered = ordered;
    }
    
    public BasicAttribute(final String s, final Object o, final boolean b) {
        this(s, b);
        this.values.addElement(o);
    }
    
    @Override
    public NamingEnumeration<?> getAll() throws NamingException {
        return new ValuesEnumImpl();
    }
    
    @Override
    public Object get() throws NamingException {
        if (this.values.size() == 0) {
            throw new NoSuchElementException("Attribute " + this.getID() + " has no value");
        }
        return this.values.elementAt(0);
    }
    
    @Override
    public int size() {
        return this.values.size();
    }
    
    @Override
    public String getID() {
        return this.attrID;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.find(o) >= 0;
    }
    
    private int find(final Object o) {
        if (o == null) {
            for (int size = this.values.size(), i = 0; i < size; ++i) {
                if (this.values.elementAt(i) == null) {
                    return i;
                }
            }
        }
        else {
            final Class<?> class1;
            if (!(class1 = o.getClass()).isArray()) {
                return this.values.indexOf(o, 0);
            }
            for (int size2 = this.values.size(), j = 0; j < size2; ++j) {
                final Object element = this.values.elementAt(j);
                if (element != null && class1 == element.getClass() && arrayEquals(o, element)) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    private static boolean valueEquals(final Object o, final Object o2) {
        if (o == o2) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o, o2);
        }
        return o.equals(o2);
    }
    
    private static boolean arrayEquals(final Object o, final Object o2) {
        final int length;
        if ((length = Array.getLength(o)) != Array.getLength(o2)) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final Object value = Array.get(o, i);
            final Object value2 = Array.get(o2, i);
            if (value == null || value2 == null) {
                if (value != value2) {
                    return false;
                }
            }
            else if (!value.equals(value2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean add(final Object o) {
        if (this.isOrdered() || this.find(o) < 0) {
            this.values.addElement(o);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean remove(final Object o) {
        final int find = this.find(o);
        if (find >= 0) {
            this.values.removeElementAt(find);
            return true;
        }
        return false;
    }
    
    @Override
    public void clear() {
        this.values.setSize(0);
    }
    
    @Override
    public boolean isOrdered() {
        return this.ordered;
    }
    
    @Override
    public Object get(final int n) throws NamingException {
        return this.values.elementAt(n);
    }
    
    @Override
    public Object remove(final int n) {
        final Object element = this.values.elementAt(n);
        this.values.removeElementAt(n);
        return element;
    }
    
    @Override
    public void add(final int n, final Object o) {
        if (!this.isOrdered() && this.contains(o)) {
            throw new IllegalStateException("Cannot add duplicate to unordered attribute");
        }
        this.values.insertElementAt(o, n);
    }
    
    @Override
    public Object set(final int n, final Object o) {
        if (!this.isOrdered() && this.contains(o)) {
            throw new IllegalStateException("Cannot add duplicate to unordered attribute");
        }
        final Object element = this.values.elementAt(n);
        this.values.setElementAt(o, n);
        return element;
    }
    
    @Override
    public DirContext getAttributeSyntaxDefinition() throws NamingException {
        throw new OperationNotSupportedException("attribute syntax");
    }
    
    @Override
    public DirContext getAttributeDefinition() throws NamingException {
        throw new OperationNotSupportedException("attribute definition");
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.values.size());
        for (int i = 0; i < this.values.size(); ++i) {
            objectOutputStream.writeObject(this.values.elementAt(i));
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int int1 = objectInputStream.readInt();
        this.values = new Vector<Object>(Math.min(1024, int1));
        while (--int1 >= 0) {
            this.values.addElement(objectInputStream.readObject());
        }
    }
    
    class ValuesEnumImpl implements NamingEnumeration<Object>
    {
        Enumeration<Object> list;
        
        ValuesEnumImpl() {
            this.list = BasicAttribute.this.values.elements();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.list.hasMoreElements();
        }
        
        @Override
        public Object nextElement() {
            return this.list.nextElement();
        }
        
        @Override
        public Object next() throws NamingException {
            return this.list.nextElement();
        }
        
        @Override
        public boolean hasMore() throws NamingException {
            return this.list.hasMoreElements();
        }
        
        @Override
        public void close() throws NamingException {
            this.list = null;
        }
    }
}
