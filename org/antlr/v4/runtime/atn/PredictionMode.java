package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import org.antlr.v4.runtime.misc.FlexibleHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

public enum PredictionMode
{
    SLL, 
    LL, 
    LL_EXACT_AMBIG_DETECTION;
    
    public static boolean hasSLLConflictTerminatingPrediction(final PredictionMode mode, ATNConfigSet configs) {
        if (allConfigsInRuleStopStates(configs)) {
            return true;
        }
        if (mode == PredictionMode.SLL && configs.hasSemanticContext) {
            final ATNConfigSet dup = new ATNConfigSet();
            for (ATNConfig c : configs) {
                c = new ATNConfig(c, SemanticContext.NONE);
                dup.add(c);
            }
            configs = dup;
        }
        final Collection<BitSet> altsets = getConflictingAltSubsets(configs);
        final boolean heuristic = hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
        return heuristic;
    }
    
    public static boolean hasConfigInRuleStopState(final ATNConfigSet configs) {
        for (final ATNConfig c : configs) {
            if (c.state instanceof RuleStopState) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean allConfigsInRuleStopStates(final ATNConfigSet configs) {
        for (final ATNConfig config : configs) {
            if (!(config.state instanceof RuleStopState)) {
                return false;
            }
        }
        return true;
    }
    
    public static int resolvesToJustOneViableAlt(final Collection<BitSet> altsets) {
        return getSingleViableAlt(altsets);
    }
    
    public static boolean allSubsetsConflict(final Collection<BitSet> altsets) {
        return !hasNonConflictingAltSet(altsets);
    }
    
    public static boolean hasNonConflictingAltSet(final Collection<BitSet> altsets) {
        for (final BitSet alts : altsets) {
            if (alts.cardinality() == 1) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasConflictingAltSet(final Collection<BitSet> altsets) {
        for (final BitSet alts : altsets) {
            if (alts.cardinality() > 1) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean allSubsetsEqual(final Collection<BitSet> altsets) {
        final Iterator<BitSet> it = altsets.iterator();
        final BitSet first = it.next();
        while (it.hasNext()) {
            final BitSet next = it.next();
            if (!next.equals(first)) {
                return false;
            }
        }
        return true;
    }
    
    public static int getUniqueAlt(final Collection<BitSet> altsets) {
        final BitSet all = getAlts(altsets);
        if (all.cardinality() == 1) {
            return all.nextSetBit(0);
        }
        return 0;
    }
    
    public static BitSet getAlts(final Collection<BitSet> altsets) {
        final BitSet all = new BitSet();
        for (final BitSet alts : altsets) {
            all.or(alts);
        }
        return all;
    }
    
    public static BitSet getAlts(final ATNConfigSet configs) {
        final BitSet alts = new BitSet();
        for (final ATNConfig config : configs) {
            alts.set(config.alt);
        }
        return alts;
    }
    
    public static Collection<BitSet> getConflictingAltSubsets(final ATNConfigSet configs) {
        final AltAndContextMap configToAlts = new AltAndContextMap();
        for (final ATNConfig c : configs) {
            BitSet alts = ((FlexibleHashMap<K, BitSet>)configToAlts).get(c);
            if (alts == null) {
                alts = new BitSet();
                configToAlts.put(c, alts);
            }
            alts.set(c.alt);
        }
        return ((FlexibleHashMap<K, BitSet>)configToAlts).values();
    }
    
    public static Map<ATNState, BitSet> getStateToAltMap(final ATNConfigSet configs) {
        final Map<ATNState, BitSet> m = new HashMap<ATNState, BitSet>();
        for (final ATNConfig c : configs) {
            BitSet alts = m.get(c.state);
            if (alts == null) {
                alts = new BitSet();
                m.put(c.state, alts);
            }
            alts.set(c.alt);
        }
        return m;
    }
    
    public static boolean hasStateAssociatedWithOneAlt(final ATNConfigSet configs) {
        final Map<ATNState, BitSet> x = getStateToAltMap(configs);
        for (final BitSet alts : x.values()) {
            if (alts.cardinality() == 1) {
                return true;
            }
        }
        return false;
    }
    
    public static int getSingleViableAlt(final Collection<BitSet> altsets) {
        final BitSet viableAlts = new BitSet();
        for (final BitSet alts : altsets) {
            final int minAlt = alts.nextSetBit(0);
            viableAlts.set(minAlt);
            if (viableAlts.cardinality() > 1) {
                return 0;
            }
        }
        return viableAlts.nextSetBit(0);
    }
    
    static class AltAndContextMap extends FlexibleHashMap<ATNConfig, BitSet>
    {
        public AltAndContextMap() {
            super(AltAndContextConfigEqualityComparator.INSTANCE);
        }
    }
    
    private static final class AltAndContextConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig>
    {
        public static final AltAndContextConfigEqualityComparator INSTANCE;
        
        @Override
        public int hashCode(final ATNConfig o) {
            int hashCode = MurmurHash.initialize(7);
            hashCode = MurmurHash.update(hashCode, o.state.stateNumber);
            hashCode = MurmurHash.update(hashCode, o.context);
            hashCode = MurmurHash.finish(hashCode, 2);
            return hashCode;
        }
        
        @Override
        public boolean equals(final ATNConfig a, final ATNConfig b) {
            return a == b || (a != null && b != null && a.state.stateNumber == b.state.stateNumber && a.context.equals(b.context));
        }
        
        static {
            INSTANCE = new AltAndContextConfigEqualityComparator();
        }
    }
}
