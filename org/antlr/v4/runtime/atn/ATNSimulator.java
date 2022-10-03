package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;
import java.util.List;
import java.util.IdentityHashMap;
import org.antlr.v4.runtime.dfa.DFAState;
import java.util.UUID;

public abstract class ATNSimulator
{
    @Deprecated
    public static final int SERIALIZED_VERSION;
    @Deprecated
    public static final UUID SERIALIZED_UUID;
    public static final DFAState ERROR;
    public final ATN atn;
    protected final PredictionContextCache sharedContextCache;
    
    public ATNSimulator(final ATN atn, final PredictionContextCache sharedContextCache) {
        this.atn = atn;
        this.sharedContextCache = sharedContextCache;
    }
    
    public abstract void reset();
    
    public void clearDFA() {
        throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
    }
    
    public PredictionContextCache getSharedContextCache() {
        return this.sharedContextCache;
    }
    
    public PredictionContext getCachedContext(final PredictionContext context) {
        if (this.sharedContextCache == null) {
            return context;
        }
        synchronized (this.sharedContextCache) {
            final IdentityHashMap<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
            return PredictionContext.getCachedContext(context, this.sharedContextCache, visited);
        }
    }
    
    @Deprecated
    public static ATN deserialize(final char[] data) {
        return new ATNDeserializer().deserialize(data);
    }
    
    @Deprecated
    public static void checkCondition(final boolean condition) {
        new ATNDeserializer().checkCondition(condition);
    }
    
    @Deprecated
    public static void checkCondition(final boolean condition, final String message) {
        new ATNDeserializer().checkCondition(condition, message);
    }
    
    @Deprecated
    public static int toInt(final char c) {
        return ATNDeserializer.toInt(c);
    }
    
    @Deprecated
    public static int toInt32(final char[] data, final int offset) {
        return ATNDeserializer.toInt32(data, offset);
    }
    
    @Deprecated
    public static long toLong(final char[] data, final int offset) {
        return ATNDeserializer.toLong(data, offset);
    }
    
    @Deprecated
    public static UUID toUUID(final char[] data, final int offset) {
        return ATNDeserializer.toUUID(data, offset);
    }
    
    @Deprecated
    public static Transition edgeFactory(final ATN atn, final int type, final int src, final int trg, final int arg1, final int arg2, final int arg3, final List<IntervalSet> sets) {
        return new ATNDeserializer().edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
    }
    
    @Deprecated
    public static ATNState stateFactory(final int type, final int ruleIndex) {
        return new ATNDeserializer().stateFactory(type, ruleIndex);
    }
    
    static {
        SERIALIZED_VERSION = ATNDeserializer.SERIALIZED_VERSION;
        SERIALIZED_UUID = ATNDeserializer.SERIALIZED_UUID;
        ERROR = new DFAState(new ATNConfigSet());
        ATNSimulator.ERROR.stateNumber = Integer.MAX_VALUE;
    }
}
