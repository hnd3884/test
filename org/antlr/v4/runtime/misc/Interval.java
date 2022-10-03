package org.antlr.v4.runtime.misc;

public class Interval
{
    public static final int INTERVAL_POOL_MAX_VALUE = 1000;
    public static final Interval INVALID;
    static Interval[] cache;
    public int a;
    public int b;
    public static int creates;
    public static int misses;
    public static int hits;
    public static int outOfRange;
    
    public Interval(final int a, final int b) {
        this.a = a;
        this.b = b;
    }
    
    public static Interval of(final int a, final int b) {
        if (a != b || a < 0 || a > 1000) {
            return new Interval(a, b);
        }
        if (Interval.cache[a] == null) {
            Interval.cache[a] = new Interval(a, a);
        }
        return Interval.cache[a];
    }
    
    public int length() {
        if (this.b < this.a) {
            return 0;
        }
        return this.b - this.a + 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Interval)) {
            return false;
        }
        final Interval other = (Interval)o;
        return this.a == other.a && this.b == other.b;
    }
    
    @Override
    public int hashCode() {
        int hash = 23;
        hash = hash * 31 + this.a;
        hash = hash * 31 + this.b;
        return hash;
    }
    
    public boolean startsBeforeDisjoint(final Interval other) {
        return this.a < other.a && this.b < other.a;
    }
    
    public boolean startsBeforeNonDisjoint(final Interval other) {
        return this.a <= other.a && this.b >= other.a;
    }
    
    public boolean startsAfter(final Interval other) {
        return this.a > other.a;
    }
    
    public boolean startsAfterDisjoint(final Interval other) {
        return this.a > other.b;
    }
    
    public boolean startsAfterNonDisjoint(final Interval other) {
        return this.a > other.a && this.a <= other.b;
    }
    
    public boolean disjoint(final Interval other) {
        return this.startsBeforeDisjoint(other) || this.startsAfterDisjoint(other);
    }
    
    public boolean adjacent(final Interval other) {
        return this.a == other.b + 1 || this.b == other.a - 1;
    }
    
    public boolean properlyContains(final Interval other) {
        return other.a >= this.a && other.b <= this.b;
    }
    
    public Interval union(final Interval other) {
        return of(Math.min(this.a, other.a), Math.max(this.b, other.b));
    }
    
    public Interval intersection(final Interval other) {
        return of(Math.max(this.a, other.a), Math.min(this.b, other.b));
    }
    
    public Interval differenceNotProperlyContained(final Interval other) {
        Interval diff = null;
        if (other.startsBeforeNonDisjoint(this)) {
            diff = of(Math.max(this.a, other.b + 1), this.b);
        }
        else if (other.startsAfterNonDisjoint(this)) {
            diff = of(this.a, other.a - 1);
        }
        return diff;
    }
    
    @Override
    public String toString() {
        return this.a + ".." + this.b;
    }
    
    static {
        INVALID = new Interval(-1, -2);
        Interval.cache = new Interval[1001];
        Interval.creates = 0;
        Interval.misses = 0;
        Interval.hits = 0;
        Interval.outOfRange = 0;
    }
}
