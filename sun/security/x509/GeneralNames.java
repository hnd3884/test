package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import sun.security.util.DerValue;
import java.util.List;

public class GeneralNames
{
    private final List<GeneralName> names;
    
    public GeneralNames(final DerValue derValue) throws IOException {
        this();
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for GeneralNames.");
        }
        if (derValue.data.available() == 0) {
            throw new IOException("No data available in passed DER encoded value.");
        }
        while (derValue.data.available() != 0) {
            this.add(new GeneralName(derValue.data.getDerValue()));
        }
    }
    
    public GeneralNames() {
        this.names = new ArrayList<GeneralName>();
    }
    
    public GeneralNames add(final GeneralName generalName) {
        if (generalName == null) {
            throw new NullPointerException();
        }
        this.names.add(generalName);
        return this;
    }
    
    public GeneralName get(final int n) {
        return this.names.get(n);
    }
    
    public boolean isEmpty() {
        return this.names.isEmpty();
    }
    
    public int size() {
        return this.names.size();
    }
    
    public Iterator<GeneralName> iterator() {
        return this.names.iterator();
    }
    
    public List<GeneralName> names() {
        return this.names;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        if (this.isEmpty()) {
            return;
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final Iterator<GeneralName> iterator = this.names.iterator();
        while (iterator.hasNext()) {
            iterator.next().encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof GeneralNames && this.names.equals(((GeneralNames)o).names));
    }
    
    @Override
    public int hashCode() {
        return this.names.hashCode();
    }
    
    @Override
    public String toString() {
        return this.names.toString();
    }
}
