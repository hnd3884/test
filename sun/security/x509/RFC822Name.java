package sun.security.x509;

import java.util.Locale;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class RFC822Name implements GeneralNameInterface
{
    private String name;
    
    public RFC822Name(final DerValue derValue) throws IOException {
        this.parseName(this.name = derValue.getIA5String());
    }
    
    public RFC822Name(final String name) throws IOException {
        this.parseName(name);
        this.name = name;
    }
    
    public void parseName(final String s) throws IOException {
        if (s == null || s.length() == 0) {
            throw new IOException("RFC822Name may not be null or empty");
        }
        final String substring = s.substring(s.indexOf(64) + 1);
        if (substring.length() == 0) {
            throw new IOException("RFC822Name may not end with @");
        }
        if (substring.startsWith(".") && substring.length() == 1) {
            throw new IOException("RFC822Name domain may not be just .");
        }
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putIA5String(this.name);
    }
    
    @Override
    public String toString() {
        return "RFC822Name: " + this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof RFC822Name && this.name.equalsIgnoreCase(((RFC822Name)o).name));
    }
    
    @Override
    public int hashCode() {
        return this.name.toUpperCase(Locale.ENGLISH).hashCode();
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else if (generalNameInterface.getType() != 1) {
            n = -1;
        }
        else {
            final String lowerCase = ((RFC822Name)generalNameInterface).getName().toLowerCase(Locale.ENGLISH);
            final String lowerCase2 = this.name.toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals(lowerCase2)) {
                n = 0;
            }
            else if (lowerCase2.endsWith(lowerCase)) {
                if (lowerCase.indexOf(64) != -1) {
                    n = 3;
                }
                else if (lowerCase.startsWith(".")) {
                    n = 2;
                }
                else if (lowerCase2.charAt(lowerCase2.lastIndexOf(lowerCase) - 1) == '@') {
                    n = 2;
                }
                else {
                    n = 3;
                }
            }
            else if (lowerCase.endsWith(lowerCase2)) {
                if (lowerCase2.indexOf(64) != -1) {
                    n = 3;
                }
                else if (lowerCase2.startsWith(".")) {
                    n = 1;
                }
                else if (lowerCase.charAt(lowerCase.lastIndexOf(lowerCase2) - 1) == '@') {
                    n = 1;
                }
                else {
                    n = 3;
                }
            }
            else {
                n = 3;
            }
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        String s = this.name;
        int n = 1;
        final int lastIndex = s.lastIndexOf(64);
        if (lastIndex >= 0) {
            ++n;
            s = s.substring(lastIndex + 1);
        }
        while (s.lastIndexOf(46) >= 0) {
            s = s.substring(0, s.lastIndexOf(46));
            ++n;
        }
        return n;
    }
}
