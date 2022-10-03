package sun.security.x509;

import java.util.StringJoiner;
import java.util.Comparator;
import sun.security.util.DerEncoder;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RDN
{
    final AVA[] assertion;
    private volatile List<AVA> avaList;
    private volatile String canonicalString;
    
    public RDN(final String s) throws IOException {
        this(s, Collections.emptyMap());
    }
    
    public RDN(final String s, final Map<String, String> map) throws IOException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        final ArrayList list = new ArrayList(3);
        for (int i = s.indexOf(43); i >= 0; i = s.indexOf(43, n2)) {
            n += X500Name.countQuotes(s, n2, i);
            if (i > 0 && s.charAt(i - 1) != '\\' && n != 1) {
                final String substring = s.substring(n3, i);
                if (substring.length() == 0) {
                    throw new IOException("empty AVA in RDN \"" + s + "\"");
                }
                list.add(new AVA(new StringReader(substring), map));
                n3 = i + 1;
                n = 0;
            }
            n2 = i + 1;
        }
        final String substring2 = s.substring(n3);
        if (substring2.length() == 0) {
            throw new IOException("empty AVA in RDN \"" + s + "\"");
        }
        list.add(new AVA(new StringReader(substring2), map));
        this.assertion = (AVA[])list.toArray(new AVA[list.size()]);
    }
    
    RDN(final String s, final String s2) throws IOException {
        this(s, s2, Collections.emptyMap());
    }
    
    RDN(final String s, final String s2, final Map<String, String> map) throws IOException {
        if (!s2.equalsIgnoreCase("RFC2253")) {
            throw new IOException("Unsupported format " + s2);
        }
        int n = 0;
        final ArrayList list = new ArrayList(3);
        for (int i = s.indexOf(43); i >= 0; i = s.indexOf(43, i + 1)) {
            if (i > 0 && s.charAt(i - 1) != '\\') {
                final String substring = s.substring(n, i);
                if (substring.length() == 0) {
                    throw new IOException("empty AVA in RDN \"" + s + "\"");
                }
                list.add(new AVA(new StringReader(substring), 3, map));
                n = i + 1;
            }
        }
        final String substring2 = s.substring(n);
        if (substring2.length() == 0) {
            throw new IOException("empty AVA in RDN \"" + s + "\"");
        }
        list.add(new AVA(new StringReader(substring2), 3, map));
        this.assertion = (AVA[])list.toArray(new AVA[list.size()]);
    }
    
    RDN(final DerValue derValue) throws IOException {
        if (derValue.tag != 49) {
            throw new IOException("X500 RDN");
        }
        final DerValue[] set = new DerInputStream(derValue.toByteArray()).getSet(5);
        this.assertion = new AVA[set.length];
        for (int i = 0; i < set.length; ++i) {
            this.assertion[i] = new AVA(set[i]);
        }
    }
    
    RDN(final int n) {
        this.assertion = new AVA[n];
    }
    
    public RDN(final AVA ava) {
        if (ava == null) {
            throw new NullPointerException();
        }
        this.assertion = new AVA[] { ava };
    }
    
    public RDN(final AVA[] array) {
        this.assertion = array.clone();
        for (int i = 0; i < this.assertion.length; ++i) {
            if (this.assertion[i] == null) {
                throw new NullPointerException();
            }
        }
    }
    
    public List<AVA> avas() {
        Object avaList = this.avaList;
        if (avaList == null) {
            avaList = Collections.unmodifiableList((List<? extends AVA>)Arrays.asList((T[])this.assertion));
            this.avaList = (List<AVA>)avaList;
        }
        return (List<AVA>)avaList;
    }
    
    public int size() {
        return this.assertion.length;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RDN)) {
            return false;
        }
        final RDN rdn = (RDN)o;
        return this.assertion.length == rdn.assertion.length && this.toRFC2253String(true).equals(rdn.toRFC2253String(true));
    }
    
    @Override
    public int hashCode() {
        return this.toRFC2253String(true).hashCode();
    }
    
    DerValue findAttribute(final ObjectIdentifier objectIdentifier) {
        for (int i = 0; i < this.assertion.length; ++i) {
            if (this.assertion[i].oid.equals((Object)objectIdentifier)) {
                return this.assertion[i].value;
            }
        }
        return null;
    }
    
    void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putOrderedSetOf((byte)49, this.assertion);
    }
    
    @Override
    public String toString() {
        if (this.assertion.length == 1) {
            return this.assertion[0].toString();
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.assertion.length; ++i) {
            if (i != 0) {
                sb.append(" + ");
            }
            sb.append(this.assertion[i].toString());
        }
        return sb.toString();
    }
    
    public String toRFC1779String() {
        return this.toRFC1779String(Collections.emptyMap());
    }
    
    public String toRFC1779String(final Map<String, String> map) {
        if (this.assertion.length == 1) {
            return this.assertion[0].toRFC1779String(map);
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.assertion.length; ++i) {
            if (i != 0) {
                sb.append(" + ");
            }
            sb.append(this.assertion[i].toRFC1779String(map));
        }
        return sb.toString();
    }
    
    public String toRFC2253String() {
        return this.toRFC2253StringInternal(false, Collections.emptyMap());
    }
    
    public String toRFC2253String(final Map<String, String> map) {
        return this.toRFC2253StringInternal(false, map);
    }
    
    public String toRFC2253String(final boolean b) {
        if (!b) {
            return this.toRFC2253StringInternal(false, Collections.emptyMap());
        }
        String canonicalString = this.canonicalString;
        if (canonicalString == null) {
            canonicalString = this.toRFC2253StringInternal(true, Collections.emptyMap());
            this.canonicalString = canonicalString;
        }
        return canonicalString;
    }
    
    private String toRFC2253StringInternal(final boolean b, final Map<String, String> map) {
        if (this.assertion.length == 1) {
            return b ? this.assertion[0].toRFC2253CanonicalString() : this.assertion[0].toRFC2253String(map);
        }
        AVA[] assertion = this.assertion;
        if (b) {
            assertion = this.assertion.clone();
            Arrays.sort(assertion, AVAComparator.getInstance());
        }
        final StringJoiner stringJoiner = new StringJoiner("+");
        for (final AVA ava : assertion) {
            stringJoiner.add(b ? ava.toRFC2253CanonicalString() : ava.toRFC2253String(map));
        }
        return stringJoiner.toString();
    }
}
