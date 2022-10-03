package javax.naming;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Enumeration;

public class CompositeName implements Name
{
    private transient NameImpl impl;
    private static final long serialVersionUID = 1667768148915813118L;
    
    protected CompositeName(final Enumeration<String> enumeration) {
        this.impl = new NameImpl(null, enumeration);
    }
    
    public CompositeName(final String s) throws InvalidNameException {
        this.impl = new NameImpl(null, s);
    }
    
    public CompositeName() {
        this.impl = new NameImpl(null);
    }
    
    @Override
    public String toString() {
        return this.impl.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof CompositeName && this.impl.equals(((CompositeName)o).impl);
    }
    
    @Override
    public int hashCode() {
        return this.impl.hashCode();
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof CompositeName)) {
            throw new ClassCastException("Not a CompositeName");
        }
        return this.impl.compareTo(((CompositeName)o).impl);
    }
    
    @Override
    public Object clone() {
        return new CompositeName(this.getAll());
    }
    
    @Override
    public int size() {
        return this.impl.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.impl.isEmpty();
    }
    
    @Override
    public Enumeration<String> getAll() {
        return this.impl.getAll();
    }
    
    @Override
    public String get(final int n) {
        return this.impl.get(n);
    }
    
    @Override
    public Name getPrefix(final int n) {
        return new CompositeName(this.impl.getPrefix(n));
    }
    
    @Override
    public Name getSuffix(final int n) {
        return new CompositeName(this.impl.getSuffix(n));
    }
    
    @Override
    public boolean startsWith(final Name name) {
        return name instanceof CompositeName && this.impl.startsWith(name.size(), name.getAll());
    }
    
    @Override
    public boolean endsWith(final Name name) {
        return name instanceof CompositeName && this.impl.endsWith(name.size(), name.getAll());
    }
    
    @Override
    public Name addAll(final Name name) throws InvalidNameException {
        if (name instanceof CompositeName) {
            this.impl.addAll(name.getAll());
            return this;
        }
        throw new InvalidNameException("Not a composite name: " + name.toString());
    }
    
    @Override
    public Name addAll(final int n, final Name name) throws InvalidNameException {
        if (name instanceof CompositeName) {
            this.impl.addAll(n, name.getAll());
            return this;
        }
        throw new InvalidNameException("Not a composite name: " + name.toString());
    }
    
    @Override
    public Name add(final String s) throws InvalidNameException {
        this.impl.add(s);
        return this;
    }
    
    @Override
    public Name add(final int n, final String s) throws InvalidNameException {
        this.impl.add(n, s);
        return this;
    }
    
    @Override
    public Object remove(final int n) throws InvalidNameException {
        return this.impl.remove(n);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.size());
        final Enumeration<String> all = this.getAll();
        while (all.hasMoreElements()) {
            objectOutputStream.writeObject(all.nextElement());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.impl = new NameImpl(null);
        int int1 = objectInputStream.readInt();
        try {
            while (--int1 >= 0) {
                this.add((String)objectInputStream.readObject());
            }
        }
        catch (final InvalidNameException ex) {
            throw new StreamCorruptedException("Invalid name");
        }
    }
}
