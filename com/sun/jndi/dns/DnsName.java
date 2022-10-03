package com.sun.jndi.dns;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import javax.naming.CompositeName;
import java.util.Iterator;
import java.util.Collection;
import javax.naming.InvalidNameException;
import java.util.ArrayList;
import javax.naming.Name;

public final class DnsName implements Name
{
    private String domain;
    private ArrayList<String> labels;
    private short octets;
    private static final long serialVersionUID = 7040187611324710271L;
    
    public DnsName() {
        this.domain = "";
        this.labels = new ArrayList<String>();
        this.octets = 1;
    }
    
    public DnsName(final String s) throws InvalidNameException {
        this.domain = "";
        this.labels = new ArrayList<String>();
        this.octets = 1;
        this.parse(s);
    }
    
    private DnsName(final DnsName dnsName, final int n, final int n2) {
        this.domain = "";
        this.labels = new ArrayList<String>();
        this.octets = 1;
        this.labels.addAll(dnsName.labels.subList(dnsName.size() - n2, dnsName.size() - n));
        if (this.size() == dnsName.size()) {
            this.domain = dnsName.domain;
            this.octets = dnsName.octets;
        }
        else {
            for (final String s : this.labels) {
                if (s.length() > 0) {
                    this.octets += (short)(s.length() + 1);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.domain == null) {
            final StringBuilder sb = new StringBuilder();
            for (final String s : this.labels) {
                if (sb.length() > 0 || s.length() == 0) {
                    sb.append('.');
                }
                escape(sb, s);
            }
            this.domain = sb.toString();
        }
        return this.domain;
    }
    
    public boolean isHostName() {
        final Iterator<String> iterator = this.labels.iterator();
        while (iterator.hasNext()) {
            if (!isHostNameLabel(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public short getOctets() {
        return this.octets;
    }
    
    @Override
    public int size() {
        return this.labels.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.size(); ++i) {
            n = 31 * n + this.getKey(i).hashCode();
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Name && !(o instanceof CompositeName) && this.size() == ((Name)o).size() && this.compareTo(o) == 0;
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.compareRange(0, this.size(), (Name)o);
    }
    
    @Override
    public boolean startsWith(final Name name) {
        return this.size() >= name.size() && this.compareRange(0, name.size(), name) == 0;
    }
    
    @Override
    public boolean endsWith(final Name name) {
        return this.size() >= name.size() && this.compareRange(this.size() - name.size(), this.size(), name) == 0;
    }
    
    @Override
    public String get(final int n) {
        if (n < 0 || n >= this.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.labels.get(this.size() - n - 1);
    }
    
    @Override
    public Enumeration<String> getAll() {
        return new Enumeration<String>() {
            int pos = 0;
            
            @Override
            public boolean hasMoreElements() {
                return this.pos < DnsName.this.size();
            }
            
            @Override
            public String nextElement() {
                if (this.pos < DnsName.this.size()) {
                    return DnsName.this.get(this.pos++);
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    @Override
    public Name getPrefix(final int n) {
        return new DnsName(this, 0, n);
    }
    
    @Override
    public Name getSuffix(final int n) {
        return new DnsName(this, n, this.size());
    }
    
    @Override
    public Object clone() {
        return new DnsName(this, 0, this.size());
    }
    
    @Override
    public Object remove(final int n) {
        if (n < 0 || n >= this.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final String s = this.labels.remove(this.size() - n - 1);
        final int length = s.length();
        if (length > 0) {
            this.octets -= (short)(length + 1);
        }
        this.domain = null;
        return s;
    }
    
    @Override
    public Name add(final String s) throws InvalidNameException {
        return this.add(this.size(), s);
    }
    
    @Override
    public Name add(final int n, final String s) throws InvalidNameException {
        if (n < 0 || n > this.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final int length = s.length();
        if ((n > 0 && length == 0) || (n == 0 && this.hasRootLabel())) {
            throw new InvalidNameException("Empty label must be the last label in a domain name");
        }
        if (length > 0) {
            if (this.octets + length + 1 >= 256) {
                throw new InvalidNameException("Name too long");
            }
            this.octets += (short)(length + 1);
        }
        final int n2 = this.size() - n;
        verifyLabel(s);
        this.labels.add(n2, s);
        this.domain = null;
        return this;
    }
    
    @Override
    public Name addAll(final Name name) throws InvalidNameException {
        return this.addAll(this.size(), name);
    }
    
    @Override
    public Name addAll(final int n, final Name name) throws InvalidNameException {
        if (name instanceof DnsName) {
            final DnsName dnsName = (DnsName)name;
            if (dnsName.isEmpty()) {
                return this;
            }
            if ((n > 0 && dnsName.hasRootLabel()) || (n == 0 && this.hasRootLabel())) {
                throw new InvalidNameException("Empty label must be the last label in a domain name");
            }
            final short octets = (short)(this.octets + dnsName.octets - 1);
            if (octets > 255) {
                throw new InvalidNameException("Name too long");
            }
            this.octets = octets;
            this.labels.addAll(this.size() - n, dnsName.labels);
            if (this.isEmpty()) {
                this.domain = dnsName.domain;
            }
            else if (this.domain == null || dnsName.domain == null) {
                this.domain = null;
            }
            else if (n == 0) {
                this.domain = this.domain + (dnsName.domain.equals(".") ? "" : ".") + dnsName.domain;
            }
            else if (n == this.size()) {
                this.domain = dnsName.domain + (this.domain.equals(".") ? "" : ".") + this.domain;
            }
            else {
                this.domain = null;
            }
        }
        else if (name instanceof CompositeName) {
            final DnsName dnsName2 = (DnsName)name;
        }
        else {
            for (int i = name.size() - 1; i >= 0; --i) {
                this.add(n, name.get(i));
            }
        }
        return this;
    }
    
    boolean hasRootLabel() {
        return !this.isEmpty() && this.get(0).equals("");
    }
    
    private int compareRange(final int n, final int n2, Name name) {
        if (name instanceof CompositeName) {
            name = name;
        }
        for (int min = Math.min(n2 - n, name.size()), i = 0; i < min; ++i) {
            final String value = this.get(i + n);
            final String value2 = name.get(i);
            final int n3 = this.size() - (i + n) - 1;
            final int compareLabels = compareLabels(value, value2);
            if (compareLabels != 0) {
                return compareLabels;
            }
        }
        return n2 - n - name.size();
    }
    
    String getKey(final int n) {
        return keyForLabel(this.get(n));
    }
    
    private void parse(final String domain) throws InvalidNameException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < domain.length(); ++i) {
            final char char1 = domain.charAt(i);
            if (char1 == '\\') {
                final char escapedOctet = getEscapedOctet(domain, i++);
                if (isDigit(domain.charAt(i))) {
                    i += 2;
                }
                sb.append(escapedOctet);
            }
            else if (char1 != '.') {
                sb.append(char1);
            }
            else {
                this.add(0, sb.toString());
                sb.delete(0, i);
            }
        }
        if (!domain.equals("") && !domain.equals(".")) {
            this.add(0, sb.toString());
        }
        this.domain = domain;
    }
    
    private static char getEscapedOctet(final String s, int n) throws InvalidNameException {
        try {
            final char char1 = s.charAt(++n);
            if (!isDigit(char1)) {
                return char1;
            }
            final char char2 = s.charAt(++n);
            final char char3 = s.charAt(++n);
            if (isDigit(char2) && isDigit(char3)) {
                return (char)((char1 - '0') * 100 + (char2 - '0') * 10 + (char3 - '0'));
            }
            throw new InvalidNameException("Invalid escape sequence in " + s);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new InvalidNameException("Invalid escape sequence in " + s);
        }
    }
    
    private static void verifyLabel(final String s) throws InvalidNameException {
        if (s.length() > 63) {
            throw new InvalidNameException("Label exceeds 63 octets: " + s);
        }
        for (int i = 0; i < s.length(); ++i) {
            if ((s.charAt(i) & '\uff00') != 0x0) {
                throw new InvalidNameException("Label has two-byte char: " + s);
            }
        }
    }
    
    private static boolean isHostNameLabel(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isHostNameChar(s.charAt(i))) {
                return false;
            }
        }
        return !s.startsWith("-") && !s.endsWith("-");
    }
    
    private static boolean isHostNameChar(final char c) {
        return c == '-' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }
    
    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    private static void escape(final StringBuilder sb, final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.' || char1 == '\\') {
                sb.append('\\');
            }
            sb.append(char1);
        }
    }
    
    private static int compareLabels(final String s, final String s2) {
        for (int min = Math.min(s.length(), s2.length()), i = 0; i < min; ++i) {
            char char1 = s.charAt(i);
            char char2 = s2.charAt(i);
            if (char1 >= 'A' && char1 <= 'Z') {
                char1 += ' ';
            }
            if (char2 >= 'A' && char2 <= 'Z') {
                char2 += ' ';
            }
            if (char1 != char2) {
                return char1 - char2;
            }
        }
        return s.length() - s2.length();
    }
    
    private static String keyForLabel(final String s) {
        final StringBuffer sb = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); ++i) {
            char char1 = s.charAt(i);
            if (char1 >= 'A' && char1 <= 'Z') {
                char1 += ' ';
            }
            sb.append(char1);
        }
        return sb.toString();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.toString());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        try {
            this.parse((String)objectInputStream.readObject());
        }
        catch (final InvalidNameException ex) {
            throw new StreamCorruptedException("Invalid name: " + this.domain);
        }
    }
}
