package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import java.util.Collection;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Set;

public class ATNConfigSet implements Set<ATNConfig>
{
    protected boolean readonly;
    public AbstractConfigHashSet configLookup;
    public final ArrayList<ATNConfig> configs;
    public int uniqueAlt;
    protected BitSet conflictingAlts;
    public boolean hasSemanticContext;
    public boolean dipsIntoOuterContext;
    public final boolean fullCtx;
    private int cachedHashCode;
    
    public ATNConfigSet(final boolean fullCtx) {
        this.readonly = false;
        this.configs = new ArrayList<ATNConfig>(7);
        this.cachedHashCode = -1;
        this.configLookup = new ConfigHashSet();
        this.fullCtx = fullCtx;
    }
    
    public ATNConfigSet() {
        this(true);
    }
    
    public ATNConfigSet(final ATNConfigSet old) {
        this(old.fullCtx);
        this.addAll(old);
        this.uniqueAlt = old.uniqueAlt;
        this.conflictingAlts = old.conflictingAlts;
        this.hasSemanticContext = old.hasSemanticContext;
        this.dipsIntoOuterContext = old.dipsIntoOuterContext;
    }
    
    @Override
    public boolean add(final ATNConfig config) {
        return this.add(config, null);
    }
    
    public boolean add(final ATNConfig config, final DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        if (config.semanticContext != SemanticContext.NONE) {
            this.hasSemanticContext = true;
        }
        if (config.getOuterContextDepth() > 0) {
            this.dipsIntoOuterContext = true;
        }
        final ATNConfig existing = this.configLookup.getOrAdd(config);
        if (existing == config) {
            this.cachedHashCode = -1;
            this.configs.add(config);
            return true;
        }
        final boolean rootIsWildcard = !this.fullCtx;
        final PredictionContext merged = PredictionContext.merge(existing.context, config.context, rootIsWildcard, mergeCache);
        existing.reachesIntoOuterContext = Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
        if (config.isPrecedenceFilterSuppressed()) {
            existing.setPrecedenceFilterSuppressed(true);
        }
        existing.context = merged;
        return true;
    }
    
    public List<ATNConfig> elements() {
        return this.configs;
    }
    
    public Set<ATNState> getStates() {
        final Set<ATNState> states = new HashSet<ATNState>();
        for (final ATNConfig c : this.configs) {
            states.add(c.state);
        }
        return states;
    }
    
    public BitSet getAlts() {
        final BitSet alts = new BitSet();
        for (final ATNConfig config : this.configs) {
            alts.set(config.alt);
        }
        return alts;
    }
    
    public List<SemanticContext> getPredicates() {
        final List<SemanticContext> preds = new ArrayList<SemanticContext>();
        for (final ATNConfig c : this.configs) {
            if (c.semanticContext != SemanticContext.NONE) {
                preds.add(c.semanticContext);
            }
        }
        return preds;
    }
    
    public ATNConfig get(final int i) {
        return this.configs.get(i);
    }
    
    public void optimizeConfigs(final ATNSimulator interpreter) {
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        if (this.configLookup.isEmpty()) {
            return;
        }
        for (final ATNConfig config : this.configs) {
            config.context = interpreter.getCachedContext(config.context);
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends ATNConfig> coll) {
        for (final ATNConfig c : coll) {
            this.add(c);
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ATNConfigSet)) {
            return false;
        }
        final ATNConfigSet other = (ATNConfigSet)o;
        final boolean same = this.configs != null && this.configs.equals(other.configs) && this.fullCtx == other.fullCtx && this.uniqueAlt == other.uniqueAlt && this.conflictingAlts == other.conflictingAlts && this.hasSemanticContext == other.hasSemanticContext && this.dipsIntoOuterContext == other.dipsIntoOuterContext;
        return same;
    }
    
    @Override
    public int hashCode() {
        if (this.isReadonly()) {
            if (this.cachedHashCode == -1) {
                this.cachedHashCode = this.configs.hashCode();
            }
            return this.cachedHashCode;
        }
        return this.configs.hashCode();
    }
    
    @Override
    public int size() {
        return this.configs.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.configs.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        if (this.configLookup == null) {
            throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
        }
        return this.configLookup.contains(o);
    }
    
    public boolean containsFast(final ATNConfig obj) {
        if (this.configLookup == null) {
            throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
        }
        return this.configLookup.containsFast(obj);
    }
    
    @Override
    public Iterator<ATNConfig> iterator() {
        return this.configs.iterator();
    }
    
    @Override
    public void clear() {
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        this.configs.clear();
        this.cachedHashCode = -1;
        this.configLookup.clear();
    }
    
    public boolean isReadonly() {
        return this.readonly;
    }
    
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
        this.configLookup = null;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.elements().toString());
        if (this.hasSemanticContext) {
            buf.append(",hasSemanticContext=").append(this.hasSemanticContext);
        }
        if (this.uniqueAlt != 0) {
            buf.append(",uniqueAlt=").append(this.uniqueAlt);
        }
        if (this.conflictingAlts != null) {
            buf.append(",conflictingAlts=").append(this.conflictingAlts);
        }
        if (this.dipsIntoOuterContext) {
            buf.append(",dipsIntoOuterContext");
        }
        return buf.toString();
    }
    
    @Override
    public ATNConfig[] toArray() {
        return this.configLookup.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.configLookup.toArray(a);
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    public static class ConfigHashSet extends AbstractConfigHashSet
    {
        public ConfigHashSet() {
            super(ConfigEqualityComparator.INSTANCE);
        }
    }
    
    public static final class ConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig>
    {
        public static final ConfigEqualityComparator INSTANCE;
        
        private ConfigEqualityComparator() {
        }
        
        @Override
        public int hashCode(final ATNConfig o) {
            int hashCode = 7;
            hashCode = 31 * hashCode + o.state.stateNumber;
            hashCode = 31 * hashCode + o.alt;
            hashCode = 31 * hashCode + o.semanticContext.hashCode();
            return hashCode;
        }
        
        @Override
        public boolean equals(final ATNConfig a, final ATNConfig b) {
            return a == b || (a != null && b != null && a.state.stateNumber == b.state.stateNumber && a.alt == b.alt && a.semanticContext.equals(b.semanticContext));
        }
        
        static {
            INSTANCE = new ConfigEqualityComparator();
        }
    }
    
    public abstract static class AbstractConfigHashSet extends Array2DHashSet<ATNConfig>
    {
        public AbstractConfigHashSet(final AbstractEqualityComparator<? super ATNConfig> comparator) {
            this(comparator, 16, 2);
        }
        
        public AbstractConfigHashSet(final AbstractEqualityComparator<? super ATNConfig> comparator, final int initialCapacity, final int initialBucketCapacity) {
            super(comparator, initialCapacity, initialBucketCapacity);
        }
        
        @Override
        protected final ATNConfig asElementType(final Object o) {
            if (!(o instanceof ATNConfig)) {
                return null;
            }
            return (ATNConfig)o;
        }
        
        @Override
        protected final ATNConfig[][] createBuckets(final int capacity) {
            return new ATNConfig[capacity][];
        }
        
        @Override
        protected final ATNConfig[] createBucket(final int capacity) {
            return new ATNConfig[capacity];
        }
    }
}
