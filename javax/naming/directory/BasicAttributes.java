package javax.naming.directory;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutputStream;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import java.util.Locale;
import java.util.Hashtable;

public class BasicAttributes implements Attributes
{
    private boolean ignoreCase;
    transient Hashtable<String, Attribute> attrs;
    private static final long serialVersionUID = 4980164073184639448L;
    
    public BasicAttributes() {
        this.ignoreCase = false;
        this.attrs = new Hashtable<String, Attribute>(11);
    }
    
    public BasicAttributes(final boolean ignoreCase) {
        this.ignoreCase = false;
        this.attrs = new Hashtable<String, Attribute>(11);
        this.ignoreCase = ignoreCase;
    }
    
    public BasicAttributes(final String s, final Object o) {
        this();
        this.put(new BasicAttribute(s, o));
    }
    
    public BasicAttributes(final String s, final Object o, final boolean b) {
        this(b);
        this.put(new BasicAttribute(s, o));
    }
    
    @Override
    public Object clone() {
        BasicAttributes basicAttributes;
        try {
            basicAttributes = (BasicAttributes)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            basicAttributes = new BasicAttributes(this.ignoreCase);
        }
        basicAttributes.attrs = (Hashtable)this.attrs.clone();
        return basicAttributes;
    }
    
    @Override
    public boolean isCaseIgnored() {
        return this.ignoreCase;
    }
    
    @Override
    public int size() {
        return this.attrs.size();
    }
    
    @Override
    public Attribute get(final String s) {
        return this.attrs.get(this.ignoreCase ? s.toLowerCase(Locale.ENGLISH) : s);
    }
    
    @Override
    public NamingEnumeration<Attribute> getAll() {
        return new AttrEnumImpl();
    }
    
    @Override
    public NamingEnumeration<String> getIDs() {
        return new IDEnumImpl();
    }
    
    @Override
    public Attribute put(final String s, final Object o) {
        return this.put(new BasicAttribute(s, o));
    }
    
    @Override
    public Attribute put(final Attribute attribute) {
        String s = attribute.getID();
        if (this.ignoreCase) {
            s = s.toLowerCase(Locale.ENGLISH);
        }
        return this.attrs.put(s, attribute);
    }
    
    @Override
    public Attribute remove(final String s) {
        return this.attrs.remove(this.ignoreCase ? s.toLowerCase(Locale.ENGLISH) : s);
    }
    
    @Override
    public String toString() {
        if (this.attrs.size() == 0) {
            return "No attributes";
        }
        return this.attrs.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof Attributes) {
            final Attributes attributes = (Attributes)o;
            if (this.ignoreCase != attributes.isCaseIgnored()) {
                return false;
            }
            if (this.size() == attributes.size()) {
                try {
                    final NamingEnumeration<? extends Attribute> all = attributes.getAll();
                    while (all.hasMore()) {
                        final Attribute attribute = (Attribute)all.next();
                        if (!attribute.equals(this.get(attribute.getID()))) {
                            return false;
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
        int ignoreCase = this.ignoreCase ? 1 : 0;
        try {
            final NamingEnumeration<Attribute> all = this.getAll();
            while (all.hasMore()) {
                ignoreCase += all.next().hashCode();
            }
        }
        catch (final NamingException ex) {}
        return ignoreCase;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.attrs.size());
        final Enumeration<Attribute> elements = this.attrs.elements();
        while (elements.hasMoreElements()) {
            objectOutputStream.writeObject(elements.nextElement());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int int1 = objectInputStream.readInt();
        this.attrs = ((int1 >= 1) ? new Hashtable<String, Attribute>(1 + (int)(Math.min(768, int1) / 0.75f)) : new Hashtable<String, Attribute>(2));
        while (--int1 >= 0) {
            this.put((Attribute)objectInputStream.readObject());
        }
    }
    
    class AttrEnumImpl implements NamingEnumeration<Attribute>
    {
        Enumeration<Attribute> elements;
        
        public AttrEnumImpl() {
            this.elements = BasicAttributes.this.attrs.elements();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.elements.hasMoreElements();
        }
        
        @Override
        public Attribute nextElement() {
            return this.elements.nextElement();
        }
        
        @Override
        public boolean hasMore() throws NamingException {
            return this.hasMoreElements();
        }
        
        @Override
        public Attribute next() throws NamingException {
            return this.nextElement();
        }
        
        @Override
        public void close() throws NamingException {
            this.elements = null;
        }
    }
    
    class IDEnumImpl implements NamingEnumeration<String>
    {
        Enumeration<Attribute> elements;
        
        public IDEnumImpl() {
            this.elements = BasicAttributes.this.attrs.elements();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.elements.hasMoreElements();
        }
        
        @Override
        public String nextElement() {
            return this.elements.nextElement().getID();
        }
        
        @Override
        public boolean hasMore() throws NamingException {
            return this.hasMoreElements();
        }
        
        @Override
        public String next() throws NamingException {
            return this.nextElement();
        }
        
        @Override
        public void close() throws NamingException {
            this.elements = null;
        }
    }
}
