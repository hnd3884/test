package com.theorem.radius3.radutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Map;

public class IntMap implements Map
{
    private static int a;
    private static int b;
    int c;
    private int d;
    private boolean e;
    int f;
    private int[] g;
    private int h;
    private Object[] i;
    private int j;
    private int[] k;
    private Object[] l;
    private int m;
    
    public IntMap() {
        this.f = 0;
        this.c = 50;
        this.a();
    }
    
    public IntMap(final int c) {
        this.f = 0;
        this.c = c;
        this.a();
    }
    
    private final void a() {
        this.d = this.c(this.c);
        Arrays.fill(this.g = new int[this.d], -1);
        this.i = new Object[this.d];
        this.h = 0;
        this.k = null;
        this.j = 0;
        this.m = 0;
        this.f = IntMap.a;
        this.e = true;
    }
    
    public final Object put(final Object o, final Object o2) {
        return this.put((int)o, o2);
    }
    
    public final Object put(final int n, final Object o) {
        final int d = this.d(n);
        if (d < 0) {
            return this.get(n);
        }
        Object o2;
        if ((this.f & IntMap.a) == IntMap.a) {
            o2 = this.i[d];
            this.i[d] = o;
        }
        else {
            o2 = this.l[d];
            this.l[d] = o;
        }
        this.e = false;
        return o2;
    }
    
    public final Object get(final Object o) {
        return this.get((int)o);
    }
    
    public final Object get(final int n) {
        if (this.e) {
            return null;
        }
        final int a = this.a(n, this.g, this.h);
        if (a < 0 && this.k != null) {
            final int a2 = this.a(n, this.k, this.j);
            if (a2 >= 0) {
                return this.l[a2];
            }
            return null;
        }
        else {
            if (a < 0) {
                return null;
            }
            return this.i[a];
        }
    }
    
    public final void clear() {
        if (this.g.length == this.c && this.k == null && this.e) {
            return;
        }
        this.a();
    }
    
    public final void clear(final int c) {
        if (this.g.length == c && this.k == null && this.e) {
            return;
        }
        this.c = c;
        this.a();
    }
    
    public final boolean containsValue(final Object o) {
        return false;
    }
    
    public final boolean containsKey(final Object o) {
        return this.containsKey((int)o);
    }
    
    public final boolean containsKey(final int n) {
        if (this.e) {
            return false;
        }
        int n2 = this.a(n, this.g, this.h);
        if (n2 < 0 && this.k != null) {
            n2 = this.a(n, this.k, this.j);
            this.f = IntMap.b;
        }
        this.f = IntMap.a;
        return n2 >= 0;
    }
    
    public final boolean isEmpty() {
        return this.e = (this.h + this.j == 0);
    }
    
    public final Set keySet() {
        final HashSet set = new HashSet();
        if (this.e) {
            return set;
        }
        final Iterator keyIterator = this.keyIterator();
        while (keyIterator.hasNext()) {
            set.add(keyIterator.next());
        }
        return set;
    }
    
    public final Set entrySet() {
        final HashSet set = new HashSet();
        if (this.e) {
            return set;
        }
        final Iterator keyIterator = this.keyIterator();
        while (keyIterator.hasNext()) {
            set.add(this.get(keyIterator.next()));
        }
        return set;
    }
    
    public final void putAll(final Map map) {
        final Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            this.put(next, map.get(next));
        }
    }
    
    public final Iterator keyIterator() {
        return new Iterator() {
            int a = 0;
            int b = 0;
            int c = IntMap.this.size();
            
            public final boolean hasNext() {
                return this.b < this.c;
            }
            
            public final Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more items.");
                }
                if (this.a < IntMap.this.d) {
                    while (IntMap.this.g[this.a++] == -1) {}
                    return new Integer(IntMap.this.g[this.a]);
                }
                if (IntMap.this.k != null) {
                    while (IntMap.this.g[this.a - IntMap.this.d] == -1) {
                        ++this.a;
                    }
                    return new Integer(IntMap.this.k[this.a - IntMap.this.d]);
                }
                throw new NoSuchElementException("No more items.");
            }
            
            public final void remove() {
            }
        };
    }
    
    public final Collection values() {
        final ArrayList list = new ArrayList();
        if (this.e) {
            return list;
        }
        final Iterator keyIterator = this.keyIterator();
        while (keyIterator.hasNext()) {
            list.add(this.get(keyIterator.next()));
        }
        return list;
    }
    
    public final Object remove(final Object o) {
        return this.remove((int)o);
    }
    
    public final Object remove(final int n) {
        if (this.e) {
            return null;
        }
        Object o = null;
        final int a = this.a(n, this.g, this.h);
        if (a < 0 && this.k != null) {
            final int a2 = this.a(n, this.k, this.j);
            if (a2 >= 0) {
                o = this.l[a2];
                this.l[a2] = null;
                this.k[a2] = -1;
                --this.j;
            }
            return o;
        }
        if (a >= 0) {
            o = this.i[a];
            this.i[a] = null;
            this.g[a] = -1;
            --this.h;
        }
        return o;
    }
    
    public final int size() {
        return this.h + this.j;
    }
    
    private final int a(final int n) {
        int abs = n % this.d;
        if (abs < 0) {
            abs = Math.abs(abs);
        }
        return abs;
    }
    
    private final int b(final int n) {
        int abs = (n >> 3) % this.d;
        if (abs < 0) {
            abs = Math.abs(abs);
        }
        else if (abs == 0) {
            ++abs;
        }
        return abs;
    }
    
    private final int c(final int n) {
        int n2 = n + 1;
        Label_0027: {
            break Label_0027;
            int c;
            do {
                while (true) {
                    Label_0018: {
                        break Label_0018;
                        while (true) {
                            if (n2 % c == 0) {
                                ++n2;
                                break;
                            }
                            c = this.c(c);
                        }
                    }
                    c = 2;
                    continue;
                }
            } while (c * c <= n2);
        }
        return n2;
    }
    
    private final int a(final int n, final int[] array, final int n2) {
        int a = this.a(n);
        for (int b = this.b(n); a != (a + (n2 - 1) * b) % this.d && array[a] != -1 && array[a] != n; a = (a + b) % this.d) {}
        if (array[a] == -1) {
            return -1;
        }
        if (array[a] == n) {
            return a;
        }
        return -1;
    }
    
    private final int d(final int n) {
        int a = this.a(n);
        final int b = this.b(n);
        final int n2 = (a + (this.d - 1) * b) % this.d;
        int[] array = null;
        switch (this.m) {
            default: {
                array = this.g;
                System.out.println("Internal tableFul state error.");
                System.exit(0);
                break;
            }
            case 1: {
                Arrays.fill(this.k = new int[this.d], -1);
                this.l = new Object[this.d];
                this.j = 0;
                this.f = IntMap.b;
                ++this.m;
                return this.d(n);
            }
            case 0: {
                array = this.g;
                this.f = IntMap.a;
                break;
            }
            case 3: {
                this.m = 0;
                this.b();
                this.f = IntMap.a;
                return this.d(n);
            }
            case 2: {
                array = this.k;
                break;
            }
        }
        while (a != n2 && array[a] != -1) {
            a = (a + b) % this.d;
        }
        if (array[a] == -1) {
            array[a] = n;
            if (this.f == IntMap.a) {
                ++this.h;
            }
            else {
                ++this.j;
            }
            return a;
        }
        if (array[a] == n) {
            return -1;
        }
        ++this.m;
        return this.d(n);
    }
    
    private final int[] b() {
        final int[] g = this.g;
        final Object[] i = this.i;
        final int[] k = this.k;
        final Object[] l = this.l;
        final int d = this.d;
        this.d = this.c(d * 4);
        Arrays.fill(this.g = new int[this.d], -1);
        this.i = new Object[this.d];
        this.h = 0;
        this.j = 0;
        for (int j = 0; j < d; ++j) {
            this.i[this.d(g[j])] = i[j];
            this.i[this.d(k[j])] = l[j];
        }
        this.k = null;
        this.l = null;
        return this.g;
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer("IntHash: elements: " + this.size() + " Capacity: " + ((this.k == null) ? this.d : (this.d * 2)) + "\n");
        for (int i = 0; i < this.g.length; ++i) {
            if (this.g[i] == -1) {
                sb.append(i).append(": ").append("Empty");
            }
            else {
                sb.append(i).append(": ").append(this.g[i]).append(" -> ").append(this.i[i]);
            }
            sb.append('\n');
        }
        if (this.k != null) {
            sb.append("OverFlow\n");
            final int n = this.g.length + 1;
            for (int j = 0; j < this.k.length; ++j) {
                if (this.k[j] == -1) {
                    sb.append(j + n).append(": ").append("Empty");
                }
                else {
                    sb.append(j).append(": ").append(this.k[j]).append(" -> ").append(this.l[j]);
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }
    
    public static void main(final String[] array) {
        try {
            new IntMap().c();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private final void c() throws Exception {
        final byte[] array = new byte[0];
        for (int i = -200; i > -100; ++i) {
            if (this.put(i, array) != null) {
                System.out.println("Put at " + i + " is not null - something was overwritten.");
            }
        }
        System.out.println("Contains 09 returns " + this.containsKey(9));
        System.out.println("Contains 1000000 returns " + this.containsKey(1000000));
        int n = 0;
        for (int j = -200; j > -100; ++j) {
            if (!this.containsKey(j)) {
                System.out.println("Missing key " + j);
                ++n;
            }
        }
        System.out.println("Missing elements: " + n);
    }
    
    static {
        IntMap.a = 1;
        IntMap.b = 2;
    }
}
