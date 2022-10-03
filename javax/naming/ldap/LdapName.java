package javax.naming.ldap;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collection;
import java.util.ArrayList;
import javax.naming.InvalidNameException;
import java.util.List;
import javax.naming.Name;

public class LdapName implements Name
{
    private transient List<Rdn> rdns;
    private transient String unparsed;
    private static final long serialVersionUID = -1595520034788997356L;
    
    public LdapName(final String unparsed) throws InvalidNameException {
        this.unparsed = unparsed;
        this.parse();
    }
    
    public LdapName(final List<Rdn> list) {
        this.rdns = new ArrayList<Rdn>(list.size());
        for (int i = 0; i < list.size(); ++i) {
            final Rdn value = list.get(i);
            if (!(value instanceof Rdn)) {
                throw new IllegalArgumentException("Entry:" + value + "  not a valid type;list entries must be of type Rdn");
            }
            this.rdns.add(value);
        }
    }
    
    private LdapName(final String unparsed, final List<Rdn> list, final int n, final int n2) {
        this.unparsed = unparsed;
        this.rdns = new ArrayList<Rdn>(list.subList(n, n2));
    }
    
    @Override
    public int size() {
        return this.rdns.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.rdns.isEmpty();
    }
    
    @Override
    public Enumeration<String> getAll() {
        return new Enumeration<String>() {
            final /* synthetic */ Iterator val$iter = LdapName.this.rdns.iterator();
            
            @Override
            public boolean hasMoreElements() {
                return this.val$iter.hasNext();
            }
            
            @Override
            public String nextElement() {
                return this.val$iter.next().toString();
            }
        };
    }
    
    @Override
    public String get(final int n) {
        return this.rdns.get(n).toString();
    }
    
    public Rdn getRdn(final int n) {
        return this.rdns.get(n);
    }
    
    @Override
    public Name getPrefix(final int n) {
        try {
            return new LdapName(null, this.rdns, 0, n);
        }
        catch (final IllegalArgumentException ex) {
            throw new IndexOutOfBoundsException("Posn: " + n + ", Size: " + this.rdns.size());
        }
    }
    
    @Override
    public Name getSuffix(final int n) {
        try {
            return new LdapName(null, this.rdns, n, this.rdns.size());
        }
        catch (final IllegalArgumentException ex) {
            throw new IndexOutOfBoundsException("Posn: " + n + ", Size: " + this.rdns.size());
        }
    }
    
    @Override
    public boolean startsWith(final Name name) {
        if (name == null) {
            return false;
        }
        final int size = this.rdns.size();
        final int size2 = name.size();
        return size >= size2 && this.matches(0, size2, name);
    }
    
    public boolean startsWith(final List<Rdn> list) {
        if (list == null) {
            return false;
        }
        final int size = this.rdns.size();
        final int size2 = list.size();
        return size >= size2 && this.doesListMatch(0, size2, list);
    }
    
    @Override
    public boolean endsWith(final Name name) {
        if (name == null) {
            return false;
        }
        final int size = this.rdns.size();
        final int size2 = name.size();
        return size >= size2 && this.matches(size - size2, size, name);
    }
    
    public boolean endsWith(final List<Rdn> list) {
        if (list == null) {
            return false;
        }
        final int size = this.rdns.size();
        final int size2 = list.size();
        return size >= size2 && this.doesListMatch(size - size2, size, list);
    }
    
    private boolean doesListMatch(final int n, final int n2, final List<Rdn> list) {
        for (int i = n; i < n2; ++i) {
            if (!this.rdns.get(i).equals(list.get(i - n))) {
                return false;
            }
        }
        return true;
    }
    
    private boolean matches(final int n, final int n2, final Name name) {
        if (name instanceof LdapName) {
            return this.doesListMatch(n, n2, ((LdapName)name).rdns);
        }
        for (int i = n; i < n2; ++i) {
            final String value = name.get(i - n);
            Rdn rdn;
            try {
                rdn = new Rfc2253Parser(value).parseRdn();
            }
            catch (final InvalidNameException ex) {
                return false;
            }
            if (!rdn.equals(this.rdns.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Name addAll(final Name name) throws InvalidNameException {
        return this.addAll(this.size(), name);
    }
    
    public Name addAll(final List<Rdn> list) {
        return this.addAll(this.size(), list);
    }
    
    @Override
    public Name addAll(int n, final Name name) throws InvalidNameException {
        this.unparsed = null;
        if (name instanceof LdapName) {
            this.rdns.addAll(n, ((LdapName)name).rdns);
        }
        else {
            final Enumeration<String> all = name.getAll();
            while (all.hasMoreElements()) {
                this.rdns.add(n++, new Rfc2253Parser(all.nextElement()).parseRdn());
            }
        }
        return this;
    }
    
    public Name addAll(final int n, final List<Rdn> list) {
        this.unparsed = null;
        for (int i = 0; i < list.size(); ++i) {
            final Rdn value = list.get(i);
            if (!(value instanceof Rdn)) {
                throw new IllegalArgumentException("Entry:" + value + "  not a valid type;suffix list entries must be of type Rdn");
            }
            this.rdns.add(i + n, value);
        }
        return this;
    }
    
    @Override
    public Name add(final String s) throws InvalidNameException {
        return this.add(this.size(), s);
    }
    
    public Name add(final Rdn rdn) {
        return this.add(this.size(), rdn);
    }
    
    @Override
    public Name add(final int n, final String s) throws InvalidNameException {
        this.rdns.add(n, new Rfc2253Parser(s).parseRdn());
        this.unparsed = null;
        return this;
    }
    
    public Name add(final int n, final Rdn rdn) {
        if (rdn == null) {
            throw new NullPointerException("Cannot set comp to null");
        }
        this.rdns.add(n, rdn);
        this.unparsed = null;
        return this;
    }
    
    @Override
    public Object remove(final int n) throws InvalidNameException {
        this.unparsed = null;
        return this.rdns.remove(n).toString();
    }
    
    public List<Rdn> getRdns() {
        return Collections.unmodifiableList((List<? extends Rdn>)this.rdns);
    }
    
    @Override
    public Object clone() {
        return new LdapName(this.unparsed, this.rdns, 0, this.rdns.size());
    }
    
    @Override
    public String toString() {
        if (this.unparsed != null) {
            return this.unparsed;
        }
        final StringBuilder sb = new StringBuilder();
        final int size = this.rdns.size();
        if (size - 1 >= 0) {
            sb.append(this.rdns.get(size - 1));
        }
        for (int i = size - 2; i >= 0; --i) {
            sb.append(',');
            sb.append(this.rdns.get(i));
        }
        return this.unparsed = sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LdapName)) {
            return false;
        }
        final LdapName ldapName = (LdapName)o;
        if (this.rdns.size() != ldapName.rdns.size()) {
            return false;
        }
        if (this.unparsed != null && this.unparsed.equalsIgnoreCase(ldapName.unparsed)) {
            return true;
        }
        for (int i = 0; i < this.rdns.size(); ++i) {
            if (!this.rdns.get(i).equals(ldapName.rdns.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof LdapName)) {
            throw new ClassCastException("The obj is not a LdapName");
        }
        if (o == this) {
            return 0;
        }
        final LdapName ldapName = (LdapName)o;
        if (this.unparsed != null && this.unparsed.equalsIgnoreCase(ldapName.unparsed)) {
            return 0;
        }
        for (int min = Math.min(this.rdns.size(), ldapName.rdns.size()), i = 0; i < min; ++i) {
            final int compareTo = this.rdns.get(i).compareTo(ldapName.rdns.get(i));
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return this.rdns.size() - ldapName.rdns.size();
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.rdns.size(); ++i) {
            n += this.rdns.get(i).hashCode();
        }
        return n;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.toString());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.unparsed = (String)objectInputStream.readObject();
        try {
            this.parse();
        }
        catch (final InvalidNameException ex) {
            throw new StreamCorruptedException("Invalid name: " + this.unparsed);
        }
    }
    
    private void parse() throws InvalidNameException {
        this.rdns = new Rfc2253Parser(this.unparsed).parseDn();
    }
}
