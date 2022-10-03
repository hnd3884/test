package com.theorem.radius3;

import java.util.Hashtable;
import java.util.Collections;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Iterator;
import com.theorem.radius3.dictionary.RADIUSDictionary;
import java.util.List;
import java.io.Serializable;

public final class AttributeName implements Serializable
{
    public static final int UNKNOWN_VENDOR = -1;
    private static List a;
    private static List b;
    
    public static void main(final String[] array) {
        System.out.println("Lookup returns: " + lookup(23));
        System.out.println("Lookup returns: " + lookup(9, 30));
        System.out.println("Lookup returns: " + lookup(9, 107));
        System.out.println("Lookup returns: " + lookup(311, 7));
        System.out.println("Lookup returns: " + lookup(311, 23));
        System.out.println("Lookup returns: " + lookup(0));
    }
    
    public static void add(final String s, final int n) {
        synchronized (AttributeName.b) {
            AttributeName.a.add(new com/theorem/radius3/b(s, n));
        }
    }
    
    public static void addDictionary(final RADIUSDictionary radiusDictionary) {
        if (radiusDictionary == null) {
            return;
        }
        Dict.addDictionary(radiusDictionary);
    }
    
    public static String getVendor(final int n) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final String vendorName = dictionaries[i].getVendorName(n);
            if (vendorName != null) {
                return vendorName;
            }
        }
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                if (com/theorem/radius3/b.b == n) {
                    final int lastIndex = com/theorem/radius3/b.a.lastIndexOf(46);
                    return (lastIndex > -1) ? com/theorem/radius3/b.a.substring(lastIndex + 1) : com/theorem/radius3/b.a;
                }
            }
        }
        return null;
    }
    
    public static int getVendor(final String s) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final int vendorId = dictionaries[i].getVendorId(s);
            if (vendorId != -1) {
                return vendorId;
            }
        }
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                if (com/theorem/radius3/b.a.indexOf(s) >= 0) {
                    return com/theorem/radius3/b.b;
                }
            }
        }
        return -1;
    }
    
    public static String lookup(final int n) {
        return lookup(0, n);
    }
    
    public static String lookup(final int n, final int n2) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final String name = dictionaries[i].getName(n, n2);
            if (name != null) {
                return name;
            }
        }
        final String a;
        if ((a = a(n, n2)) != null) {
            return a;
        }
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                if (com/theorem/radius3/b.b != n) {
                    continue;
                }
                final String a2 = com/theorem/radius3/b.a;
                try {
                    final Field[] fields = Class.forName(a2).getFields();
                    for (int j = 0; j < fields.length; ++j) {
                        final Field field = fields[j];
                        final String name2 = field.getName();
                        if (!name2.equalsIgnoreCase("vendorId")) {
                            if (field.getInt(null) == n2) {
                                final String replace = name2.replace('_', '-');
                                com/theorem/radius3/b.a(replace, n2);
                                return replace;
                            }
                        }
                    }
                }
                catch (final Exception ex) {}
            }
        }
        return "Unknown-" + n2;
    }
    
    public static int lookup(final int n, final String s) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final int tag = dictionaries[i].getTag(n, s);
            if (tag != 0) {
                return tag;
            }
        }
        final int a;
        if ((a = a(n, s)) > 0) {
            return a;
        }
        final String replace = s.replace('-', '_');
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                if (com/theorem/radius3/b.b != n) {
                    continue;
                }
                final String a2 = com/theorem/radius3/b.a;
                try {
                    final Field[] fields = Class.forName(a2).getFields();
                    for (int j = 0; j < fields.length; ++j) {
                        final Field field = fields[j];
                        final String name = field.getName();
                        if (!name.equalsIgnoreCase("VENDORID")) {
                            if (name.equals(replace)) {
                                final int int1 = field.getInt(null);
                                com/theorem/radius3/b.a(replace.replace('_', '-'), int1);
                                return int1;
                            }
                        }
                    }
                }
                catch (final Exception ex) {}
            }
        }
        return 0;
    }
    
    public static int lookup(final String s) {
        return lookup(0, s);
    }
    
    private static String a(final int n, final int n2) {
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                final String a;
                if (com/theorem/radius3/b.b == n && (a = com/theorem/radius3/b.a(n2)) != null) {
                    return a;
                }
            }
        }
        return null;
    }
    
    private static int a(final int n, final String s) {
        synchronized (AttributeName.b) {
            final Iterator iterator = AttributeName.a.iterator();
            while (iterator.hasNext()) {
                final com/theorem/radius3/b com/theorem/radius3/b = (com/theorem/radius3/b)iterator.next();
                if (com/theorem/radius3/b.b == n) {
                    return com/theorem/radius3/b.a(s);
                }
            }
        }
        return 0;
    }
    
    static {
        AttributeName.a = new ArrayList();
        AttributeName.b = Collections.synchronizedList((List<Object>)AttributeName.a);
    }
    
    private static class com/theorem/radius3/b
    {
        String a;
        int b;
        Hashtable c;
        Hashtable d;
        
        com/theorem/radius3/b(final String a, final int b) {
            this.a = a;
            this.b = b;
            this.c = new Hashtable();
            this.d = new Hashtable();
        }
        
        final void a(final String s, final int n) {
            final Integer n2 = new Integer(n);
            this.c.put(n2, s);
            this.d.put(s, n2);
        }
        
        final String a(final int n) {
            return this.c.get(new Integer(n));
        }
        
        final int a(final String s) {
            final Integer n = this.d.get(s);
            return (n == null) ? 0 : n;
        }
    }
}
