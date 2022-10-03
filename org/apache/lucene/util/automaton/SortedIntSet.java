package org.apache.lucene.util.automaton;

import java.util.Iterator;
import org.apache.lucene.util.ArrayUtil;
import java.util.TreeMap;
import java.util.Map;

final class SortedIntSet
{
    int[] values;
    int[] counts;
    int upto;
    private int hashCode;
    private static final int TREE_MAP_CUTOVER = 30;
    private final Map<Integer, Integer> map;
    private boolean useTreeMap;
    int state;
    
    public SortedIntSet(final int capacity) {
        this.map = new TreeMap<Integer, Integer>();
        this.values = new int[capacity];
        this.counts = new int[capacity];
    }
    
    public void incr(final int num) {
        if (this.useTreeMap) {
            final Integer key = num;
            final Integer val = this.map.get(key);
            if (val == null) {
                this.map.put(key, 1);
            }
            else {
                this.map.put(key, 1 + val);
            }
            return;
        }
        if (this.upto == this.values.length) {
            this.values = ArrayUtil.grow(this.values, 1 + this.upto);
            this.counts = ArrayUtil.grow(this.counts, 1 + this.upto);
        }
        for (int i = 0; i < this.upto; ++i) {
            if (this.values[i] == num) {
                final int[] counts = this.counts;
                final int n = i;
                ++counts[n];
                return;
            }
            if (num < this.values[i]) {
                for (int j = this.upto - 1; j >= i; --j) {
                    this.values[1 + j] = this.values[j];
                    this.counts[1 + j] = this.counts[j];
                }
                this.values[i] = num;
                this.counts[i] = 1;
                ++this.upto;
                return;
            }
        }
        this.values[this.upto] = num;
        this.counts[this.upto] = 1;
        ++this.upto;
        if (this.upto == 30) {
            this.useTreeMap = true;
            for (int i = 0; i < this.upto; ++i) {
                this.map.put(this.values[i], this.counts[i]);
            }
        }
    }
    
    public void decr(final int num) {
        if (this.useTreeMap) {
            final int count = this.map.get(num);
            if (count == 1) {
                this.map.remove(num);
            }
            else {
                this.map.put(num, count - 1);
            }
            if (this.map.size() == 0) {
                this.useTreeMap = false;
                this.upto = 0;
            }
            return;
        }
        for (int i = 0; i < this.upto; ++i) {
            if (this.values[i] == num) {
                final int[] counts = this.counts;
                final int n = i;
                --counts[n];
                if (this.counts[i] == 0) {
                    int limit;
                    for (limit = this.upto - 1; i < limit; ++i) {
                        this.values[i] = this.values[i + 1];
                        this.counts[i] = this.counts[i + 1];
                    }
                    this.upto = limit;
                }
                return;
            }
        }
        assert false;
    }
    
    public void computeHash() {
        if (this.useTreeMap) {
            if (this.map.size() > this.values.length) {
                final int size = ArrayUtil.oversize(this.map.size(), 4);
                this.values = new int[size];
                this.counts = new int[size];
            }
            this.hashCode = this.map.size();
            this.upto = 0;
            for (final int state : this.map.keySet()) {
                this.hashCode = 683 * this.hashCode + state;
                this.values[this.upto++] = state;
            }
        }
        else {
            this.hashCode = this.upto;
            for (int i = 0; i < this.upto; ++i) {
                this.hashCode = 683 * this.hashCode + this.values[i];
            }
        }
    }
    
    public FrozenIntSet freeze(final int state) {
        final int[] c = new int[this.upto];
        System.arraycopy(this.values, 0, c, 0, this.upto);
        return new FrozenIntSet(c, this.hashCode, state);
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object _other) {
        if (_other == null) {
            return false;
        }
        if (!(_other instanceof FrozenIntSet)) {
            return false;
        }
        final FrozenIntSet other = (FrozenIntSet)_other;
        if (this.hashCode != other.hashCode) {
            return false;
        }
        if (other.values.length != this.upto) {
            return false;
        }
        for (int i = 0; i < this.upto; ++i) {
            if (other.values[i] != this.values[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder().append('[');
        for (int i = 0; i < this.upto; ++i) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(this.values[i]).append(':').append(this.counts[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static final class FrozenIntSet
    {
        final int[] values;
        final int hashCode;
        final int state;
        
        public FrozenIntSet(final int[] values, final int hashCode, final int state) {
            this.values = values;
            this.hashCode = hashCode;
            this.state = state;
        }
        
        public FrozenIntSet(final int num, final int state) {
            this.values = new int[] { num };
            this.state = state;
            this.hashCode = 683 + num;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public boolean equals(final Object _other) {
            if (_other == null) {
                return false;
            }
            if (_other instanceof FrozenIntSet) {
                final FrozenIntSet other = (FrozenIntSet)_other;
                if (this.hashCode != other.hashCode) {
                    return false;
                }
                if (other.values.length != this.values.length) {
                    return false;
                }
                for (int i = 0; i < this.values.length; ++i) {
                    if (other.values[i] != this.values[i]) {
                        return false;
                    }
                }
                return true;
            }
            else {
                if (!(_other instanceof SortedIntSet)) {
                    return false;
                }
                final SortedIntSet other2 = (SortedIntSet)_other;
                if (this.hashCode != other2.hashCode) {
                    return false;
                }
                if (other2.values.length != this.values.length) {
                    return false;
                }
                for (int i = 0; i < this.values.length; ++i) {
                    if (other2.values[i] != this.values[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder().append('[');
            for (int i = 0; i < this.values.length; ++i) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(this.values[i]);
            }
            sb.append(']');
            return sb.toString();
        }
    }
}
