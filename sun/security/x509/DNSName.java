package sun.security.x509;

import java.util.Locale;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class DNSName implements GeneralNameInterface
{
    private String name;
    private static final String alphaDigits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    public DNSName(final DerValue derValue) throws IOException {
        this.name = derValue.getIA5String();
    }
    
    public DNSName(final String name) throws IOException {
        if (name == null || name.length() == 0) {
            throw new IOException("DNSName must not be null or empty");
        }
        if (name.contains(" ")) {
            throw new IOException("DNSName with blank components is not permitted");
        }
        if (name.startsWith(".") || name.endsWith(".")) {
            throw new IOException("DNSName may not begin or end with a .");
        }
        int n;
        for (int i = 0; i < name.length(); i = n + 1) {
            n = name.indexOf(46, i);
            if (n < 0) {
                n = name.length();
            }
            if (n - i < 1) {
                throw new IOException("DNSName with empty components are not permitted");
            }
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".indexOf(name.charAt(i)) < 0) {
                throw new IOException("DNSName components must begin with a letter or digit");
            }
            for (int j = i + 1; j < n; ++j) {
                final char char1 = name.charAt(j);
                if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".indexOf(char1) < 0 && char1 != '-') {
                    throw new IOException("DNSName components must consist of letters, digits, and hyphens");
                }
            }
        }
        this.name = name;
    }
    
    @Override
    public int getType() {
        return 2;
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
        return "DNSName: " + this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof DNSName && this.name.equalsIgnoreCase(((DNSName)o).name));
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
        else if (generalNameInterface.getType() != 2) {
            n = -1;
        }
        else {
            final String lowerCase = ((DNSName)generalNameInterface).getName().toLowerCase(Locale.ENGLISH);
            final String lowerCase2 = this.name.toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals(lowerCase2)) {
                n = 0;
            }
            else if (lowerCase2.endsWith(lowerCase)) {
                if (lowerCase2.charAt(lowerCase2.lastIndexOf(lowerCase) - 1) == '.') {
                    n = 2;
                }
                else {
                    n = 3;
                }
            }
            else if (lowerCase.endsWith(lowerCase2)) {
                if (lowerCase.charAt(lowerCase.lastIndexOf(lowerCase2) - 1) == '.') {
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
        int n = 1;
        for (int i = this.name.indexOf(46); i >= 0; i = this.name.indexOf(46, i + 1)) {
            ++n;
        }
        return n;
    }
}
