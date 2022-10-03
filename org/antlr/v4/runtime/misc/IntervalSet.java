package org.antlr.v4.runtime.misc;

import java.util.HashSet;
import java.util.Set;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;

public class IntervalSet implements IntSet
{
    public static final IntervalSet COMPLETE_CHAR_SET;
    public static final IntervalSet EMPTY_SET;
    protected List<Interval> intervals;
    protected boolean readonly;
    
    public IntervalSet(final List<Interval> intervals) {
        this.intervals = intervals;
    }
    
    public IntervalSet(final IntervalSet set) {
        this(new int[0]);
        this.addAll((IntSet)set);
    }
    
    public IntervalSet(final int... els) {
        if (els == null) {
            this.intervals = new ArrayList<Interval>(2);
        }
        else {
            this.intervals = new ArrayList<Interval>(els.length);
            for (final int e : els) {
                this.add(e);
            }
        }
    }
    
    public static IntervalSet of(final int a) {
        final IntervalSet s = new IntervalSet(new int[0]);
        s.add(a);
        return s;
    }
    
    public static IntervalSet of(final int a, final int b) {
        final IntervalSet s = new IntervalSet(new int[0]);
        s.add(a, b);
        return s;
    }
    
    public void clear() {
        if (this.readonly) {
            throw new IllegalStateException("can't alter readonly IntervalSet");
        }
        this.intervals.clear();
    }
    
    @Override
    public void add(final int el) {
        if (this.readonly) {
            throw new IllegalStateException("can't alter readonly IntervalSet");
        }
        this.add(el, el);
    }
    
    public void add(final int a, final int b) {
        this.add(Interval.of(a, b));
    }
    
    protected void add(final Interval addition) {
        if (this.readonly) {
            throw new IllegalStateException("can't alter readonly IntervalSet");
        }
        if (addition.b < addition.a) {
            return;
        }
        final ListIterator<Interval> iter = this.intervals.listIterator();
        while (iter.hasNext()) {
            final Interval r = iter.next();
            if (addition.equals(r)) {
                return;
            }
            if (addition.adjacent(r) || !addition.disjoint(r)) {
                final Interval bigger = addition.union(r);
                iter.set(bigger);
                while (iter.hasNext()) {
                    final Interval next = iter.next();
                    if (!bigger.adjacent(next) && bigger.disjoint(next)) {
                        break;
                    }
                    iter.remove();
                    iter.previous();
                    iter.set(bigger.union(next));
                    iter.next();
                }
                return;
            }
            if (addition.startsBeforeDisjoint(r)) {
                iter.previous();
                iter.add(addition);
                return;
            }
        }
        this.intervals.add(addition);
    }
    
    public static IntervalSet or(final IntervalSet[] sets) {
        final IntervalSet r = new IntervalSet(new int[0]);
        for (final IntervalSet s : sets) {
            r.addAll((IntSet)s);
        }
        return r;
    }
    
    @Override
    public IntervalSet addAll(final IntSet set) {
        if (set == null) {
            return this;
        }
        if (set instanceof IntervalSet) {
            final IntervalSet other = (IntervalSet)set;
            for (int n = other.intervals.size(), i = 0; i < n; ++i) {
                final Interval I = other.intervals.get(i);
                this.add(I.a, I.b);
            }
        }
        else {
            for (final int value : set.toList()) {
                this.add(value);
            }
        }
        return this;
    }
    
    public IntervalSet complement(final int minElement, final int maxElement) {
        return this.complement((IntSet)of(minElement, maxElement));
    }
    
    @Override
    public IntervalSet complement(final IntSet vocabulary) {
        if (vocabulary == null || vocabulary.isNil()) {
            return null;
        }
        IntervalSet vocabularyIS;
        if (vocabulary instanceof IntervalSet) {
            vocabularyIS = (IntervalSet)vocabulary;
        }
        else {
            vocabularyIS = new IntervalSet(new int[0]);
            vocabularyIS.addAll(vocabulary);
        }
        return vocabularyIS.subtract((IntSet)this);
    }
    
    @Override
    public IntervalSet subtract(final IntSet a) {
        if (a == null || a.isNil()) {
            return new IntervalSet(this);
        }
        if (a instanceof IntervalSet) {
            return subtract(this, (IntervalSet)a);
        }
        final IntervalSet other = new IntervalSet(new int[0]);
        other.addAll(a);
        return subtract(this, other);
    }
    
    public static IntervalSet subtract(final IntervalSet left, final IntervalSet right) {
        if (left == null || left.isNil()) {
            return new IntervalSet(new int[0]);
        }
        final IntervalSet result = new IntervalSet(left);
        if (right == null || right.isNil()) {
            return result;
        }
        int resultI = 0;
        int rightI = 0;
        while (resultI < result.intervals.size() && rightI < right.intervals.size()) {
            final Interval resultInterval = result.intervals.get(resultI);
            final Interval rightInterval = right.intervals.get(rightI);
            if (rightInterval.b < resultInterval.a) {
                ++rightI;
            }
            else if (rightInterval.a > resultInterval.b) {
                ++resultI;
            }
            else {
                Interval beforeCurrent = null;
                Interval afterCurrent = null;
                if (rightInterval.a > resultInterval.a) {
                    beforeCurrent = new Interval(resultInterval.a, rightInterval.a - 1);
                }
                if (rightInterval.b < resultInterval.b) {
                    afterCurrent = new Interval(rightInterval.b + 1, resultInterval.b);
                }
                if (beforeCurrent != null) {
                    if (afterCurrent != null) {
                        result.intervals.set(resultI, beforeCurrent);
                        result.intervals.add(resultI + 1, afterCurrent);
                        ++resultI;
                        ++rightI;
                    }
                    else {
                        result.intervals.set(resultI, beforeCurrent);
                        ++resultI;
                    }
                }
                else if (afterCurrent != null) {
                    result.intervals.set(resultI, afterCurrent);
                    ++rightI;
                }
                else {
                    result.intervals.remove(resultI);
                }
            }
        }
        return result;
    }
    
    @Override
    public IntervalSet or(final IntSet a) {
        final IntervalSet o = new IntervalSet(new int[0]);
        o.addAll((IntSet)this);
        o.addAll(a);
        return o;
    }
    
    @Override
    public IntervalSet and(final IntSet other) {
        if (other == null) {
            return null;
        }
        final List<Interval> myIntervals = this.intervals;
        final List<Interval> theirIntervals = ((IntervalSet)other).intervals;
        IntervalSet intersection = null;
        final int mySize = myIntervals.size();
        final int theirSize = theirIntervals.size();
        int i = 0;
        int j = 0;
        while (i < mySize && j < theirSize) {
            final Interval mine = myIntervals.get(i);
            final Interval theirs = theirIntervals.get(j);
            if (mine.startsBeforeDisjoint(theirs)) {
                ++i;
            }
            else if (theirs.startsBeforeDisjoint(mine)) {
                ++j;
            }
            else if (mine.properlyContains(theirs)) {
                if (intersection == null) {
                    intersection = new IntervalSet(new int[0]);
                }
                intersection.add(mine.intersection(theirs));
                ++j;
            }
            else if (theirs.properlyContains(mine)) {
                if (intersection == null) {
                    intersection = new IntervalSet(new int[0]);
                }
                intersection.add(mine.intersection(theirs));
                ++i;
            }
            else {
                if (mine.disjoint(theirs)) {
                    continue;
                }
                if (intersection == null) {
                    intersection = new IntervalSet(new int[0]);
                }
                intersection.add(mine.intersection(theirs));
                if (mine.startsAfterNonDisjoint(theirs)) {
                    ++j;
                }
                else {
                    if (!theirs.startsAfterNonDisjoint(mine)) {
                        continue;
                    }
                    ++i;
                }
            }
        }
        if (intersection == null) {
            return new IntervalSet(new int[0]);
        }
        return intersection;
    }
    
    @Override
    public boolean contains(final int el) {
        for (int n = this.intervals.size(), i = 0; i < n; ++i) {
            final Interval I = this.intervals.get(i);
            final int a = I.a;
            final int b = I.b;
            if (el < a) {
                break;
            }
            if (el >= a && el <= b) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isNil() {
        return this.intervals == null || this.intervals.isEmpty();
    }
    
    @Override
    public int getSingleElement() {
        if (this.intervals != null && this.intervals.size() == 1) {
            final Interval I = this.intervals.get(0);
            if (I.a == I.b) {
                return I.a;
            }
        }
        return 0;
    }
    
    public int getMaxElement() {
        if (this.isNil()) {
            return 0;
        }
        final Interval last = this.intervals.get(this.intervals.size() - 1);
        return last.b;
    }
    
    public int getMinElement() {
        if (this.isNil()) {
            return 0;
        }
        return this.intervals.get(0).a;
    }
    
    public List<Interval> getIntervals() {
        return this.intervals;
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        for (final Interval I : this.intervals) {
            hash = MurmurHash.update(hash, I.a);
            hash = MurmurHash.update(hash, I.b);
        }
        hash = MurmurHash.finish(hash, this.intervals.size() * 2);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof IntervalSet)) {
            return false;
        }
        final IntervalSet other = (IntervalSet)obj;
        return this.intervals.equals(other.intervals);
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    public String toString(final boolean elemAreChar) {
        final StringBuilder buf = new StringBuilder();
        if (this.intervals == null || this.intervals.isEmpty()) {
            return "{}";
        }
        if (this.size() > 1) {
            buf.append("{");
        }
        final Iterator<Interval> iter = this.intervals.iterator();
        while (iter.hasNext()) {
            final Interval I = iter.next();
            final int a = I.a;
            final int b = I.b;
            if (a == b) {
                if (a == -1) {
                    buf.append("<EOF>");
                }
                else if (elemAreChar) {
                    buf.append("'").append((char)a).append("'");
                }
                else {
                    buf.append(a);
                }
            }
            else if (elemAreChar) {
                buf.append("'").append((char)a).append("'..'").append((char)b).append("'");
            }
            else {
                buf.append(a).append("..").append(b);
            }
            if (iter.hasNext()) {
                buf.append(", ");
            }
        }
        if (this.size() > 1) {
            buf.append("}");
        }
        return buf.toString();
    }
    
    @Deprecated
    public String toString(final String[] tokenNames) {
        return this.toString(VocabularyImpl.fromTokenNames(tokenNames));
    }
    
    public String toString(final Vocabulary vocabulary) {
        final StringBuilder buf = new StringBuilder();
        if (this.intervals == null || this.intervals.isEmpty()) {
            return "{}";
        }
        if (this.size() > 1) {
            buf.append("{");
        }
        final Iterator<Interval> iter = this.intervals.iterator();
        while (iter.hasNext()) {
            final Interval I = iter.next();
            final int a = I.a;
            final int b = I.b;
            if (a == b) {
                buf.append(this.elementName(vocabulary, a));
            }
            else {
                for (int i = a; i <= b; ++i) {
                    if (i > a) {
                        buf.append(", ");
                    }
                    buf.append(this.elementName(vocabulary, i));
                }
            }
            if (iter.hasNext()) {
                buf.append(", ");
            }
        }
        if (this.size() > 1) {
            buf.append("}");
        }
        return buf.toString();
    }
    
    @Deprecated
    protected String elementName(final String[] tokenNames, final int a) {
        return this.elementName(VocabularyImpl.fromTokenNames(tokenNames), a);
    }
    
    protected String elementName(final Vocabulary vocabulary, final int a) {
        if (a == -1) {
            return "<EOF>";
        }
        if (a == -2) {
            return "<EPSILON>";
        }
        return vocabulary.getDisplayName(a);
    }
    
    @Override
    public int size() {
        int n = 0;
        final int numIntervals = this.intervals.size();
        if (numIntervals == 1) {
            final Interval firstInterval = this.intervals.get(0);
            return firstInterval.b - firstInterval.a + 1;
        }
        for (int i = 0; i < numIntervals; ++i) {
            final Interval I = this.intervals.get(i);
            n += I.b - I.a + 1;
        }
        return n;
    }
    
    public IntegerList toIntegerList() {
        final IntegerList values = new IntegerList(this.size());
        for (int n = this.intervals.size(), i = 0; i < n; ++i) {
            final Interval I = this.intervals.get(i);
            final int a = I.a;
            for (int b = I.b, v = a; v <= b; ++v) {
                values.add(v);
            }
        }
        return values;
    }
    
    @Override
    public List<Integer> toList() {
        final List<Integer> values = new ArrayList<Integer>();
        for (int n = this.intervals.size(), i = 0; i < n; ++i) {
            final Interval I = this.intervals.get(i);
            final int a = I.a;
            for (int b = I.b, v = a; v <= b; ++v) {
                values.add(v);
            }
        }
        return values;
    }
    
    public Set<Integer> toSet() {
        final Set<Integer> s = new HashSet<Integer>();
        for (final Interval I : this.intervals) {
            final int a = I.a;
            for (int b = I.b, v = a; v <= b; ++v) {
                s.add(v);
            }
        }
        return s;
    }
    
    public int get(final int i) {
        final int n = this.intervals.size();
        int index = 0;
        for (int j = 0; j < n; ++j) {
            final Interval I = this.intervals.get(j);
            final int a = I.a;
            for (int b = I.b, v = a; v <= b; ++v) {
                if (index == i) {
                    return v;
                }
                ++index;
            }
        }
        return -1;
    }
    
    public int[] toArray() {
        return this.toIntegerList().toArray();
    }
    
    @Override
    public void remove(final int el) {
        if (this.readonly) {
            throw new IllegalStateException("can't alter readonly IntervalSet");
        }
        for (int n = this.intervals.size(), i = 0; i < n; ++i) {
            final Interval I = this.intervals.get(i);
            final int a = I.a;
            final int b = I.b;
            if (el < a) {
                break;
            }
            if (el == a && el == b) {
                this.intervals.remove(i);
                break;
            }
            if (el == a) {
                final Interval interval = I;
                ++interval.a;
                break;
            }
            if (el == b) {
                final Interval interval2 = I;
                --interval2.b;
                break;
            }
            if (el > a && el < b) {
                final int oldb = I.b;
                I.b = el - 1;
                this.add(el + 1, oldb);
            }
        }
    }
    
    public boolean isReadonly() {
        return this.readonly;
    }
    
    public void setReadonly(final boolean readonly) {
        if (this.readonly && !readonly) {
            throw new IllegalStateException("can't alter readonly IntervalSet");
        }
        this.readonly = readonly;
    }
    
    static {
        (COMPLETE_CHAR_SET = of(0, 65534)).setReadonly(true);
        (EMPTY_SET = new IntervalSet(new int[0])).setReadonly(true);
    }
}
