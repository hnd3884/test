package javax.naming;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class CompoundName implements Name
{
    protected transient NameImpl impl;
    protected transient Properties mySyntax;
    private static final long serialVersionUID = 3513100557083972036L;
    
    protected CompoundName(final Enumeration<String> enumeration, final Properties mySyntax) {
        if (mySyntax == null) {
            throw new NullPointerException();
        }
        this.mySyntax = mySyntax;
        this.impl = new NameImpl(mySyntax, enumeration);
    }
    
    public CompoundName(final String s, final Properties mySyntax) throws InvalidNameException {
        if (mySyntax == null) {
            throw new NullPointerException();
        }
        this.mySyntax = mySyntax;
        this.impl = new NameImpl(mySyntax, s);
    }
    
    @Override
    public String toString() {
        return this.impl.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof CompoundName && this.impl.equals(((CompoundName)o).impl);
    }
    
    @Override
    public int hashCode() {
        return this.impl.hashCode();
    }
    
    @Override
    public Object clone() {
        return new CompoundName(this.getAll(), this.mySyntax);
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof CompoundName)) {
            throw new ClassCastException("Not a CompoundName");
        }
        return this.impl.compareTo(((CompoundName)o).impl);
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
        return new CompoundName(this.impl.getPrefix(n), this.mySyntax);
    }
    
    @Override
    public Name getSuffix(final int n) {
        return new CompoundName(this.impl.getSuffix(n), this.mySyntax);
    }
    
    @Override
    public boolean startsWith(final Name name) {
        return name instanceof CompoundName && this.impl.startsWith(name.size(), name.getAll());
    }
    
    @Override
    public boolean endsWith(final Name name) {
        return name instanceof CompoundName && this.impl.endsWith(name.size(), name.getAll());
    }
    
    @Override
    public Name addAll(final Name name) throws InvalidNameException {
        if (name instanceof CompoundName) {
            this.impl.addAll(name.getAll());
            return this;
        }
        throw new InvalidNameException("Not a compound name: " + name.toString());
    }
    
    @Override
    public Name addAll(final int n, final Name name) throws InvalidNameException {
        if (name instanceof CompoundName) {
            this.impl.addAll(n, name.getAll());
            return this;
        }
        throw new InvalidNameException("Not a compound name: " + name.toString());
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
        objectOutputStream.writeObject(this.mySyntax);
        objectOutputStream.writeInt(this.size());
        final Enumeration<String> all = this.getAll();
        while (all.hasMoreElements()) {
            objectOutputStream.writeObject(all.nextElement());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.mySyntax = (Properties)objectInputStream.readObject();
        this.impl = new NameImpl(this.mySyntax);
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
