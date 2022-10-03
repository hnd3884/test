package com.theorem.radius3.dictionary;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import com.theorem.radius3.radutil.ByteIterator;
import com.theorem.radius3.Attribute;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class RADIUSDictionary
{
    protected String a;
    private Stack b;
    private Stack c;
    private HashMap d;
    private Map e;
    private HashMap f;
    private Map g;
    private HashMap h;
    private Map i;
    private AttributeData j;
    private HashMap k;
    private Map l;
    private HashMap m;
    private Map n;
    private int o;
    public static final int UNKNOWN_VENDOR = -1;
    public static final int UNKNOWN_DATA_TYPE = -1;
    public static final int UNKNOWN = 0;
    public static final int INTEGER = 1;
    public static final int OCTETS = 2;
    public static final int STRING = 4;
    public static final int DATE = 8;
    public static final int IPADDRESS = 16;
    public static final int TUNNEL = 32;
    public static final int ENCRYPT1 = 64;
    public static final int ENCRYPT2 = 128;
    public static final int ENCRYPT3 = 256;
    public static final boolean SHORT_TAGS = false;
    public static final boolean LONG_TAGS = true;
    
    public RADIUSDictionary(final String a) {
        this.b = new Stack();
        this.c = new Stack();
        this.d = new HashMap();
        this.e = Collections.synchronizedMap((Map<Object, Object>)this.d);
        this.f = new HashMap();
        this.g = Collections.synchronizedMap((Map<Object, Object>)this.f);
        this.h = new HashMap();
        this.i = Collections.synchronizedMap((Map<Object, Object>)this.h);
        this.k = new HashMap();
        this.l = Collections.synchronizedMap((Map<Object, Object>)this.k);
        this.m = new HashMap();
        this.n = Collections.synchronizedMap((Map<Object, Object>)this.m);
        this.o = 0;
        this.a = a;
        this.j = new AttributeData();
        final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = new com/theorem/radius3/dictionary/c(0, "0");
        this.f.put("", com/theorem/radius3/dictionary/c);
        this.d.put(new Integer(0), com/theorem/radius3/dictionary/c);
    }
    
    public RADIUSDictionary() {
        this.b = new Stack();
        this.c = new Stack();
        this.d = new HashMap();
        this.e = Collections.synchronizedMap((Map<Object, Object>)this.d);
        this.f = new HashMap();
        this.g = Collections.synchronizedMap((Map<Object, Object>)this.f);
        this.h = new HashMap();
        this.i = Collections.synchronizedMap((Map<Object, Object>)this.h);
        this.k = new HashMap();
        this.l = Collections.synchronizedMap((Map<Object, Object>)this.k);
        this.m = new HashMap();
        this.n = Collections.synchronizedMap((Map<Object, Object>)this.m);
        this.o = 0;
        this.a = "No souce file";
        this.j = new AttributeData();
        final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = new com/theorem/radius3/dictionary/c(0, "0");
        this.f.put("", com/theorem/radius3/dictionary/c);
        this.d.put(new Integer(0), com/theorem/radius3/dictionary/c);
    }
    
    public void read() throws IOException {
        throw new IOException("This method must be overridden by a parser for a particular style of dictionary.");
    }
    
    public void addAttribute(final String s, final int n, final int n2) {
        this.addAttribute(s, n, n2, this.o, false);
    }
    
    public final void addAttribute(final String s, final int n, final int n2, final int n3) {
        this.addAttribute(s, n, n2, n3, false);
    }
    
    public final void addAttribute(final String s, final int n, final int n2, final int n3, final boolean b) {
        synchronized (this.l) {
            final AttributeData attributeData = new AttributeData(s, n, n2, n3);
            if (b) {
                attributeData.d();
            }
            this.k.put(attributeData.a(), attributeData);
            this.m.put(attributeData.b(), attributeData);
        }
    }
    
    public final void addAttribute(final String s, final String s2, final String s3, final String s4) throws IOException {
        this.addAttribute(s, s2, s3, s4, false);
    }
    
    public final void addAttribute(final String s, final String s2, final String s3, final String s4, final boolean b) throws IOException {
        synchronized (this.l) {
            final AttributeData attributeData = new AttributeData(s, s2, s3, s4);
            if (b) {
                attributeData.d();
            }
            this.k.put(attributeData.a(), attributeData);
            this.m.put(attributeData.b(), attributeData);
        }
    }
    
    public final Attribute createAttribute(final String s, final String s2) {
        return this.createAttribute(this.o, s, s2);
    }
    
    public final Attribute createAttribute(final int n, final String s, final String s2) {
        final int tag = this.getTag(n, s);
        if (tag == 0) {
            return null;
        }
        final int a = this.a(n, s, s2);
        if (a == -1) {
            return null;
        }
        return new Attribute(tag, ByteIterator.toBytes(a));
    }
    
    public final Attribute createAttribute(final int n, final String s, final byte[] array) {
        final int tag = this.getTag(n, s);
        if (tag == 0) {
            return null;
        }
        return new Attribute(tag, array);
    }
    
    public final Attribute createAttribute(final String s, final byte[] array) {
        return this.createAttribute(this.o, s, array);
    }
    
    public final void setVendorId(final int o) {
        this.o = o;
    }
    
    public final int getVendorId() {
        return this.o;
    }
    
    public final int getDataType(final int n) {
        return this.getDataType(this.o, n);
    }
    
    public final int getDataType(final int n, final int n2) {
        final AttributeData attributeData;
        synchronized (this.l) {
            attributeData = this.k.get(this.j.a(n, n2));
        }
        if (attributeData == null) {
            return 0;
        }
        return attributeData.g();
    }
    
    public final String getDataTypeName(int n) {
        String s = "";
        if ((n & 0x20) == 0x20) {
            s = "TUNNEL / ";
            n &= 0xFFFFFFDF;
        }
        if ((n & 0x40) == 0x40) {
            s = "ENCRYPT1 / ";
            n &= 0xFFFFFFBF;
        }
        if ((n & 0x80) == 0x80) {
            s = "ENCRYPT2 / ";
            n &= 0xFFFFFF7F;
        }
        if ((n & 0x100) == 0x100) {
            s = "ENCRYPT3 / ";
            n &= 0xFFFFFEFF;
        }
        switch (n) {
            default: {
                s = s + "UNKNOWN (" + n + ")";
                break;
            }
            case 2: {
                s += "OCTETS";
                break;
            }
            case 1: {
                s += "INTEGER";
                break;
            }
            case 16: {
                s += "IPADDRESS";
                break;
            }
            case 8: {
                s += "DATE";
                break;
            }
            case 0: {
                break;
            }
            case 4: {
                s += "STRING";
                break;
            }
        }
        return s;
    }
    
    public final int getTag(final int n, final String s) {
        final AttributeData attributeData;
        synchronized (this.n) {
            attributeData = this.m.get(this.j.a(n, s));
        }
        if (attributeData == null) {
            return 0;
        }
        return attributeData.b;
    }
    
    public final int getTag(final String s) {
        return this.getTag(this.o, s);
    }
    
    public final int getVendorId(final String s) {
        com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = null;
        synchronized (this.g) {
            com/theorem/radius3/dictionary/c = this.f.get(s);
        }
        return (com/theorem/radius3/dictionary/c == null) ? -1 : com/theorem/radius3/dictionary/c.a;
    }
    
    public final String getName(final int n, final int n2) {
        final AttributeData attributeData;
        synchronized (this.l) {
            attributeData = this.k.get(this.j.a(n, n2));
        }
        if (attributeData == null) {
            return null;
        }
        return attributeData.a;
    }
    
    public final String getName(final int n) {
        return this.getName(this.o, n);
    }
    
    public final boolean hasValue(final int n) {
        return this.hasValue(this.o, n);
    }
    
    public final boolean hasValue(final String s) {
        return this.h.get(s) != null;
    }
    
    public final int getEncryptionType(final int n) {
        return this.getEncryptionType(this.o, n);
    }
    
    public final int getEncryptionType(final int n, final int n2) {
        final AttributeData attributeData;
        synchronized (this.l) {
            attributeData = this.k.get(this.j.a(n, n2));
        }
        if (attributeData == null) {
            return 0;
        }
        return attributeData.e();
    }
    
    public final void setEncryptionType(final int n, final int n2) {
        this.setEncryptionType(this.o, n, n2);
    }
    
    public final void setEncryptionType(final int n, final int n2, final int n3) {
        synchronized (this.l) {
            final AttributeData attributeData = this.k.get(this.j.a(n, n2));
            if (attributeData != null) {
                attributeData.a(n3);
            }
        }
    }
    
    public final int getDataTypeFlags(final int n) {
        return this.getDataTypeFlags(this.o, n);
    }
    
    public final int getDataTypeFlags(final int n, final int n2) {
        synchronized (this.l) {
            final AttributeData attributeData = this.k.get(this.j.a(n, n2));
            if (attributeData != null) {
                return attributeData.c();
            }
        }
        return 0;
    }
    
    public final boolean hasValue(final int n, final int n2) {
        final String name = this.getName(n, n2);
        synchronized (this.i) {
            return this.h.get(name) != null;
        }
    }
    
    public final Iterator attributeIterator() {
        return this.attributeIterator(this.o);
    }
    
    public final Iterator attributeIterator(final int n) {
        return new Iterator() {
            Iterator a = null;
            
            public final boolean hasNext() {
                if (this.a == null) {
                    synchronized (RADIUSDictionary.this.l) {
                        final ArrayList list = new ArrayList();
                        final Iterator iterator = RADIUSDictionary.this.k.entrySet().iterator();
                        while (iterator.hasNext()) {
                            final AttributeData attributeData = ((Map.Entry<K, AttributeData>)iterator.next()).getValue();
                            if (attributeData.h() == n) {
                                list.add(attributeData.a);
                            }
                        }
                        this.a = list.iterator();
                    }
                }
                return this.a.hasNext();
            }
            
            public final Object next() {
                return this.a.next();
            }
            
            public final void remove() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("remove() not implemented");
            }
        };
    }
    
    public final boolean isVSA(final int n) {
        return n == 26;
    }
    
    public final boolean isVSA(final String s) {
        return s.equals("Vendor-Specific");
    }
    
    public final void setTunnel(final int n) {
        this.setTunnel(this.o, n);
    }
    
    public final void setTunnel(final int n, final int n2) {
        synchronized (this.l) {
            this.k.get(this.j.a(n, n2)).d();
        }
    }
    
    public final boolean isTunnel(final int n) {
        return this.isTunnel(this.o, n);
    }
    
    public final boolean isTunnel(final int n, final int n2) {
        final AttributeData attributeData;
        synchronized (this.l) {
            attributeData = this.k.get(this.j.a(n, n2));
        }
        return (attributeData.c() & 0x20) == 0x20;
    }
    
    public final int getIntValue(final int n, final String s) {
        final String name = this.getName(n);
        if (name == null) {
            return -1;
        }
        return this.a(name, s);
    }
    
    public final int getIntValue(final int n, final int n2, final String s) {
        final String name = this.getName(n, n2);
        if (name == null) {
            return -1;
        }
        return this.a(n, name, s);
    }
    
    public final String getValueName(final int n, final int n2) {
        return this.a(this.getName(n), n2);
    }
    
    public final String[] getAllValueNames(final int n, final String s) {
        final HashMap h = this.h;
        final String a = this.j.a(n, s);
        final HashMap hashMap;
        synchronized (this.i) {
            hashMap = this.h.get(a);
        }
        String[] array;
        if (hashMap != null) {
            array = new String[hashMap.size()];
            int n2 = 0;
            final Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                array[n2++] = ((Map.Entry<String, V>)iterator.next()).getKey();
            }
        }
        else {
            array = new String[0];
        }
        return array;
    }
    
    public final String[] getAllValueNames(final String s) {
        return this.getAllValueNames(this.o, s);
    }
    
    public final String getValueName(final int n, final int n2, final int n3) {
        return this.a(this.getName(n, n2), n3);
    }
    
    public final void addVendor(final String s, final int n) {
        final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = new com/theorem/radius3/dictionary/c(n, s);
        synchronized (this.e) {
            this.d.put(new Integer(n), com/theorem/radius3/dictionary/c);
        }
        synchronized (this.g) {
            this.f.put(s, com/theorem/radius3/dictionary/c);
        }
    }
    
    public final String[] getVendorNames() {
        final String[] array = new String[this.f.size() - 1];
        final Iterator iterator;
        synchronized (this.g) {
            iterator = this.f.keySet().iterator();
        }
        int n = 0;
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            if (s.length() == 0) {
                continue;
            }
            array[n++] = s;
        }
        return array;
    }
    
    public final String getVendorName(final int n) {
        synchronized (this.e) {
            final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = this.d.get(new Integer(n));
            if (com/theorem/radius3/dictionary/c != null) {
                return com/theorem/radius3/dictionary/c.b;
            }
            return null;
        }
    }
    
    public final void setVendorTagLength(final boolean b) {
        this.setVendorTagLength(this.o, b);
    }
    
    public final void setVendorTagLength(final int n, final boolean b) {
        synchronized (this.e) {
            final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = this.d.get(new Integer(n));
            if (com/theorem/radius3/dictionary/c != null) {
                com/theorem/radius3/dictionary/c.a(b);
            }
        }
    }
    
    public final boolean getVendorTagLength() {
        return this.getVendorTagLength(this.o);
    }
    
    public final boolean getVendorTagLength(final int n) {
        synchronized (this.e) {
            final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = this.d.get(new Integer(n));
            return com/theorem/radius3/dictionary/c != null && com/theorem/radius3/dictionary/c.a();
        }
    }
    
    public void merge(final RADIUSDictionary radiusDictionary) {
        final String[] vendorNames = radiusDictionary.getVendorNames();
        final String[] array = new String[vendorNames.length + 1];
        System.arraycopy(vendorNames, 0, array, 0, vendorNames.length);
        array[vendorNames.length] = "";
        final int[] array2 = new int[array.length + 1];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = radiusDictionary.getVendorId(array[i]);
        }
        for (int j = 0; j < array2.length; ++j) {
            final int n = array2[j];
            if (j != array2.length - 1) {
                this.addVendor(array[j], n);
            }
            final Iterator attributeIterator = radiusDictionary.attributeIterator(n);
            while (attributeIterator.hasNext()) {
                final String s = attributeIterator.next();
                final int tag = radiusDictionary.getTag(n, s);
                final int dataType = radiusDictionary.getDataType(n, tag);
                final int encryptionType = radiusDictionary.getEncryptionType(n, tag);
                this.addAttribute(s, tag, dataType, n, radiusDictionary.isTunnel(n, tag));
                this.setEncryptionType(n, tag, encryptionType);
                final String[] allValueNames = radiusDictionary.getAllValueNames(s);
                for (int k = 0; k < allValueNames.length; ++k) {
                    this.setSymbolicIntValue(n, s, allValueNames[k], radiusDictionary.getIntValue(n, tag, allValueNames[k]));
                }
            }
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\nVendors:\n");
        synchronized (this.g) {
            final Iterator iterator = this.f.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                if (entry.getKey().equals("")) {
                    continue;
                }
                sb.append("   ").append(entry.getKey()).append('\n');
            }
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public final String toVerboseString() {
        final StringBuffer sb = new StringBuffer(this.getClass().getName());
        sb.append(this.toString()).append("\n");
        sb.append("Attributes:\n");
        final Iterator iterator;
        synchronized (this.l) {
            iterator = this.k.entrySet().iterator();
        }
        while (iterator.hasNext()) {
            final AttributeData attributeData = ((Map.Entry<K, AttributeData>)iterator.next()).getValue();
            sb.append("     ").append(attributeData).append('\n');
            final HashMap hashMap;
            synchronized (this.i) {
                hashMap = this.h.get(attributeData.f());
            }
            if (hashMap != null) {
                final Iterator iterator2 = hashMap.entrySet().iterator();
                while (iterator2.hasNext()) {
                    final Map.Entry entry = (Map.Entry)iterator2.next();
                    sb.append("          ").append(entry.getKey()).append("->").append(entry.getValue());
                    sb.append('\n');
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }
    
    protected final void a(final String s, final LineNumberReader lineNumberReader) {
        this.b.push(s);
        this.c.push(lineNumberReader);
    }
    
    protected final void a() {
        this.b.pop();
        this.c.pop();
    }
    
    protected final String b() {
        return "'" + this.b.peek() + "':" + this.c.peek().getLineNumber();
    }
    
    public final void setSymbolicIntValue(final String s, final String s2, final int n) {
        this.setSymbolicIntValue(this.o, s, s2, n);
    }
    
    public final void setSymbolicIntValue(final int n, final String s, final String s2, final int n2) {
        synchronized (this.i) {
            final String a = this.j.a(n, s);
            HashMap hashMap = this.h.get(a);
            if (hashMap == null) {
                hashMap = new HashMap();
                this.h.put(a, hashMap);
            }
            hashMap.put(s2, new Integer(n2));
        }
    }
    
    private final int a(final String s, final String s2) {
        return this.a(this.o, s, s2);
    }
    
    private final int a(final int n, final String s, final String s2) {
        final String a = this.j.a(n, s);
        synchronized (this.i) {
            final HashMap hashMap = this.h.get(a);
            if (hashMap == null) {
                return -1;
            }
            final Integer n2 = (Integer)hashMap.get(s2);
            return (n2 == null) ? -1 : n2;
        }
    }
    
    private final String a(final String s, final int n) {
        return this.a(this.o, s, n);
    }
    
    private final String a(final int n, final String s, final int n2) {
        final String a = this.j.a(n, s);
        synchronized (this.i) {
            final HashMap hashMap = this.h.get(a);
            if (hashMap == null) {
                return null;
            }
            final Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                if ((int)entry.getValue() == n2) {
                    return (String)entry.getKey();
                }
            }
            return null;
        }
    }
    
    protected class AttributeData
    {
        String a;
        int b;
        int c;
        int d;
        
        AttributeData() {
        }
        
        AttributeData(final String a, final String s, final String s2, final String s3) throws IOException {
            this.a = a;
            try {
                this.b = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                try {
                    this.b = Integer.parseInt(s.substring(2), 16);
                }
                catch (final NumberFormatException ex2) {
                    throw new IOException(RADIUSDictionary.this.b() + " Expecting a number, found '" + s + "'");
                }
            }
            if (s2.equalsIgnoreCase("string")) {
                this.c = 4;
            }
            else if (s2.equalsIgnoreCase("integer")) {
                this.c = 1;
            }
            else if (s2.equalsIgnoreCase("ipaddr")) {
                this.c = 16;
            }
            else if (s2.equalsIgnoreCase("date")) {
                this.c = 8;
            }
            else if (s2.equalsIgnoreCase("octets")) {
                this.c = 2;
            }
            if (s3 != null && !s3.equals("")) {
                final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = RADIUSDictionary.this.f.get(s3);
                if (com/theorem/radius3/dictionary/c == null) {
                    throw new IOException(RADIUSDictionary.this.b() + " No such vendor as '" + s3 + "'");
                }
                this.d = com/theorem/radius3/dictionary/c.a;
            }
            else {
                this.d = 0;
            }
        }
        
        AttributeData(final String a, final int b, final int c, final int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
        
        final String a() {
            return this.d + ":" + this.b;
        }
        
        final String b() {
            return this.d + ":" + this.a;
        }
        
        final String a(final int n, final int n2) {
            return n + ":" + n2;
        }
        
        final String a(final int n, final String s) {
            return n + ":" + s;
        }
        
        final int c() {
            return this.c;
        }
        
        final void d() {
            this.c |= 0x20;
        }
        
        final void a(final int n) {
            this.c |= n;
        }
        
        final int e() {
            return this.c & 0xC0;
        }
        
        final String f() {
            return this.a;
        }
        
        final int g() {
            return this.c;
        }
        
        final int h() {
            return this.d;
        }
        
        public final String toString() {
            final StringBuffer sb = new StringBuffer("AttributeData: ");
            if (this.d > 0) {
                final com/theorem/radius3/dictionary/c com/theorem/radius3/dictionary/c = RADIUSDictionary.this.d.get(new Integer(this.d));
                sb.append("Vendor =").append(com/theorem/radius3/dictionary/c.b).append(" (").append(com/theorem/radius3/dictionary/c.a).append(") - ");
                if (com/theorem/radius3/dictionary/c.a()) {
                    sb.append("Long Tags ,");
                }
            }
            sb.append("Name=").append(this.a).append("(").append(this.b).append("), ");
            sb.append("Type=");
            switch (this.c) {
                default: {
                    sb.append("unknown");
                    break;
                }
                case 2: {
                    sb.append("octets");
                    break;
                }
                case 16: {
                    sb.append("ipaddr");
                    break;
                }
                case 4: {
                    sb.append("string");
                    break;
                }
                case 8: {
                    sb.append("date");
                    break;
                }
                case 1: {
                    sb.append("integer");
                    break;
                }
            }
            return sb.toString();
        }
    }
    
    protected class com/theorem/radius3/dictionary/c
    {
        int a;
        String b;
        boolean c;
        
        com/theorem/radius3/dictionary/c(final int a, final String b) {
            this.c = false;
            this.a = a;
            this.b = b;
        }
        
        final void a(final boolean c) {
            this.c = c;
        }
        
        final boolean a() {
            return this.c;
        }
    }
}
