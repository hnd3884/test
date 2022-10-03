package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.RuleContext;

public abstract class PredictionContext
{
    public static final EmptyPredictionContext EMPTY;
    public static final int EMPTY_RETURN_STATE = Integer.MAX_VALUE;
    private static final int INITIAL_HASH = 1;
    public static int globalNodeCount;
    public final int id;
    public final int cachedHashCode;
    
    protected PredictionContext(final int cachedHashCode) {
        this.id = PredictionContext.globalNodeCount++;
        this.cachedHashCode = cachedHashCode;
    }
    
    public static PredictionContext fromRuleContext(final ATN atn, RuleContext outerContext) {
        if (outerContext == null) {
            outerContext = RuleContext.EMPTY;
        }
        if (outerContext.parent == null || outerContext == RuleContext.EMPTY) {
            return PredictionContext.EMPTY;
        }
        PredictionContext parent = PredictionContext.EMPTY;
        parent = fromRuleContext(atn, outerContext.parent);
        final ATNState state = atn.states.get(outerContext.invokingState);
        final RuleTransition transition = (RuleTransition)state.transition(0);
        return SingletonPredictionContext.create(parent, transition.followState.stateNumber);
    }
    
    public abstract int size();
    
    public abstract PredictionContext getParent(final int p0);
    
    public abstract int getReturnState(final int p0);
    
    public boolean isEmpty() {
        return this == PredictionContext.EMPTY;
    }
    
    public boolean hasEmptyPath() {
        return this.getReturnState(this.size() - 1) == Integer.MAX_VALUE;
    }
    
    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }
    
    @Override
    public abstract boolean equals(final Object p0);
    
    protected static int calculateEmptyHashCode() {
        int hash = MurmurHash.initialize(1);
        hash = MurmurHash.finish(hash, 0);
        return hash;
    }
    
    protected static int calculateHashCode(final PredictionContext parent, final int returnState) {
        int hash = MurmurHash.initialize(1);
        hash = MurmurHash.update(hash, parent);
        hash = MurmurHash.update(hash, returnState);
        hash = MurmurHash.finish(hash, 2);
        return hash;
    }
    
    protected static int calculateHashCode(final PredictionContext[] parents, final int[] returnStates) {
        int hash = MurmurHash.initialize(1);
        for (final PredictionContext parent : parents) {
            hash = MurmurHash.update(hash, parent);
        }
        for (final int returnState : returnStates) {
            hash = MurmurHash.update(hash, returnState);
        }
        hash = MurmurHash.finish(hash, 2 * parents.length);
        return hash;
    }
    
    public static PredictionContext merge(PredictionContext a, PredictionContext b, final boolean rootIsWildcard, final DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
        assert a != null && b != null;
        if (a == b || a.equals(b)) {
            return a;
        }
        if (a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
            return mergeSingletons((SingletonPredictionContext)a, (SingletonPredictionContext)b, rootIsWildcard, mergeCache);
        }
        if (rootIsWildcard) {
            if (a instanceof EmptyPredictionContext) {
                return a;
            }
            if (b instanceof EmptyPredictionContext) {
                return b;
            }
        }
        if (a instanceof SingletonPredictionContext) {
            a = new ArrayPredictionContext((SingletonPredictionContext)a);
        }
        if (b instanceof SingletonPredictionContext) {
            b = new ArrayPredictionContext((SingletonPredictionContext)b);
        }
        return mergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b, rootIsWildcard, mergeCache);
    }
    
    public static PredictionContext mergeSingletons(final SingletonPredictionContext a, final SingletonPredictionContext b, final boolean rootIsWildcard, final DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
        if (mergeCache != null) {
            PredictionContext previous = mergeCache.get(a, b);
            if (previous != null) {
                return previous;
            }
            previous = mergeCache.get(b, a);
            if (previous != null) {
                return previous;
            }
        }
        final PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
        if (rootMerge != null) {
            if (mergeCache != null) {
                mergeCache.put(a, b, rootMerge);
            }
            return rootMerge;
        }
        if (a.returnState == b.returnState) {
            final PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard, mergeCache);
            if (parent == a.parent) {
                return a;
            }
            if (parent == b.parent) {
                return b;
            }
            final PredictionContext a_ = SingletonPredictionContext.create(parent, a.returnState);
            if (mergeCache != null) {
                mergeCache.put(a, b, a_);
            }
            return a_;
        }
        else {
            PredictionContext singleParent = null;
            if (a == b || (a.parent != null && a.parent.equals(b.parent))) {
                singleParent = a.parent;
            }
            if (singleParent != null) {
                final int[] payloads = { a.returnState, b.returnState };
                if (a.returnState > b.returnState) {
                    payloads[0] = b.returnState;
                    payloads[1] = a.returnState;
                }
                final PredictionContext[] parents = { singleParent, singleParent };
                final PredictionContext a_2 = new ArrayPredictionContext(parents, payloads);
                if (mergeCache != null) {
                    mergeCache.put(a, b, a_2);
                }
                return a_2;
            }
            final int[] payloads = { a.returnState, b.returnState };
            PredictionContext[] parents = { a.parent, b.parent };
            if (a.returnState > b.returnState) {
                payloads[0] = b.returnState;
                payloads[1] = a.returnState;
                parents = new PredictionContext[] { b.parent, a.parent };
            }
            final PredictionContext a_2 = new ArrayPredictionContext(parents, payloads);
            if (mergeCache != null) {
                mergeCache.put(a, b, a_2);
            }
            return a_2;
        }
    }
    
    public static PredictionContext mergeRoot(final SingletonPredictionContext a, final SingletonPredictionContext b, final boolean rootIsWildcard) {
        if (rootIsWildcard) {
            if (a == PredictionContext.EMPTY) {
                return PredictionContext.EMPTY;
            }
            if (b == PredictionContext.EMPTY) {
                return PredictionContext.EMPTY;
            }
        }
        else {
            if (a == PredictionContext.EMPTY && b == PredictionContext.EMPTY) {
                return PredictionContext.EMPTY;
            }
            if (a == PredictionContext.EMPTY) {
                final int[] payloads = { b.returnState, Integer.MAX_VALUE };
                final PredictionContext[] parents = { b.parent, null };
                final PredictionContext joined = new ArrayPredictionContext(parents, payloads);
                return joined;
            }
            if (b == PredictionContext.EMPTY) {
                final int[] payloads = { a.returnState, Integer.MAX_VALUE };
                final PredictionContext[] parents = { a.parent, null };
                final PredictionContext joined = new ArrayPredictionContext(parents, payloads);
                return joined;
            }
        }
        return null;
    }
    
    public static PredictionContext mergeArrays(final ArrayPredictionContext a, final ArrayPredictionContext b, final boolean rootIsWildcard, final DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
        if (mergeCache != null) {
            PredictionContext previous = mergeCache.get(a, b);
            if (previous != null) {
                return previous;
            }
            previous = mergeCache.get(b, a);
            if (previous != null) {
                return previous;
            }
        }
        int i = 0;
        int j = 0;
        int k = 0;
        int[] mergedReturnStates = new int[a.returnStates.length + b.returnStates.length];
        PredictionContext[] mergedParents = new PredictionContext[a.returnStates.length + b.returnStates.length];
        while (i < a.returnStates.length && j < b.returnStates.length) {
            final PredictionContext a_parent = a.parents[i];
            final PredictionContext b_parent = b.parents[j];
            if (a.returnStates[i] == b.returnStates[j]) {
                final int payload = a.returnStates[i];
                final boolean both$ = payload == Integer.MAX_VALUE && a_parent == null && b_parent == null;
                final boolean ax_ax = a_parent != null && b_parent != null && a_parent.equals(b_parent);
                if (both$ || ax_ax) {
                    mergedParents[k] = a_parent;
                    mergedReturnStates[k] = payload;
                }
                else {
                    final PredictionContext mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache);
                    mergedParents[k] = mergedParent;
                    mergedReturnStates[k] = payload;
                }
                ++i;
                ++j;
            }
            else if (a.returnStates[i] < b.returnStates[j]) {
                mergedParents[k] = a_parent;
                mergedReturnStates[k] = a.returnStates[i];
                ++i;
            }
            else {
                mergedParents[k] = b_parent;
                mergedReturnStates[k] = b.returnStates[j];
                ++j;
            }
            ++k;
        }
        if (i < a.returnStates.length) {
            for (int p = i; p < a.returnStates.length; ++p) {
                mergedParents[k] = a.parents[p];
                mergedReturnStates[k] = a.returnStates[p];
                ++k;
            }
        }
        else {
            for (int p = j; p < b.returnStates.length; ++p) {
                mergedParents[k] = b.parents[p];
                mergedReturnStates[k] = b.returnStates[p];
                ++k;
            }
        }
        if (k < mergedParents.length) {
            if (k == 1) {
                final PredictionContext a_ = SingletonPredictionContext.create(mergedParents[0], mergedReturnStates[0]);
                if (mergeCache != null) {
                    mergeCache.put(a, b, a_);
                }
                return a_;
            }
            mergedParents = Arrays.copyOf(mergedParents, k);
            mergedReturnStates = Arrays.copyOf(mergedReturnStates, k);
        }
        final PredictionContext M = new ArrayPredictionContext(mergedParents, mergedReturnStates);
        if (M.equals(a)) {
            if (mergeCache != null) {
                mergeCache.put(a, b, a);
            }
            return a;
        }
        if (M.equals(b)) {
            if (mergeCache != null) {
                mergeCache.put(a, b, b);
            }
            return b;
        }
        combineCommonParents(mergedParents);
        if (mergeCache != null) {
            mergeCache.put(a, b, M);
        }
        return M;
    }
    
    protected static void combineCommonParents(final PredictionContext[] parents) {
        final Map<PredictionContext, PredictionContext> uniqueParents = new HashMap<PredictionContext, PredictionContext>();
        for (int p = 0; p < parents.length; ++p) {
            final PredictionContext parent = parents[p];
            if (!uniqueParents.containsKey(parent)) {
                uniqueParents.put(parent, parent);
            }
        }
        for (int p = 0; p < parents.length; ++p) {
            parents[p] = uniqueParents.get(parents[p]);
        }
    }
    
    public static String toDOTString(final PredictionContext context) {
        if (context == null) {
            return "";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("digraph G {\n");
        buf.append("rankdir=LR;\n");
        final List<PredictionContext> nodes = getAllContextNodes(context);
        Collections.sort(nodes, new Comparator<PredictionContext>() {
            @Override
            public int compare(final PredictionContext o1, final PredictionContext o2) {
                return o1.id - o2.id;
            }
        });
        for (final PredictionContext current : nodes) {
            if (current instanceof SingletonPredictionContext) {
                final String s = String.valueOf(current.id);
                buf.append("  s").append(s);
                String returnState = String.valueOf(current.getReturnState(0));
                if (current instanceof EmptyPredictionContext) {
                    returnState = "$";
                }
                buf.append(" [label=\"").append(returnState).append("\"];\n");
            }
            else {
                final ArrayPredictionContext arr = (ArrayPredictionContext)current;
                buf.append("  s").append(arr.id);
                buf.append(" [shape=box, label=\"");
                buf.append("[");
                boolean first = true;
                for (final int inv : arr.returnStates) {
                    if (!first) {
                        buf.append(", ");
                    }
                    if (inv == Integer.MAX_VALUE) {
                        buf.append("$");
                    }
                    else {
                        buf.append(inv);
                    }
                    first = false;
                }
                buf.append("]");
                buf.append("\"];\n");
            }
        }
        for (final PredictionContext current : nodes) {
            if (current == PredictionContext.EMPTY) {
                continue;
            }
            for (int i = 0; i < current.size(); ++i) {
                if (current.getParent(i) != null) {
                    final String s2 = String.valueOf(current.id);
                    buf.append("  s").append(s2);
                    buf.append("->");
                    buf.append("s");
                    buf.append(current.getParent(i).id);
                    if (current.size() > 1) {
                        buf.append(" [label=\"parent[" + i + "]\"];\n");
                    }
                    else {
                        buf.append(";\n");
                    }
                }
            }
        }
        buf.append("}\n");
        return buf.toString();
    }
    
    public static PredictionContext getCachedContext(final PredictionContext context, final PredictionContextCache contextCache, final IdentityHashMap<PredictionContext, PredictionContext> visited) {
        if (context.isEmpty()) {
            return context;
        }
        PredictionContext existing = visited.get(context);
        if (existing != null) {
            return existing;
        }
        existing = contextCache.get(context);
        if (existing != null) {
            visited.put(context, existing);
            return existing;
        }
        boolean changed = false;
        PredictionContext[] parents = new PredictionContext[context.size()];
        for (int i = 0; i < parents.length; ++i) {
            final PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
            if (changed || parent != context.getParent(i)) {
                if (!changed) {
                    parents = new PredictionContext[context.size()];
                    for (int j = 0; j < context.size(); ++j) {
                        parents[j] = context.getParent(j);
                    }
                    changed = true;
                }
                parents[i] = parent;
            }
        }
        if (!changed) {
            contextCache.add(context);
            visited.put(context, context);
            return context;
        }
        PredictionContext updated;
        if (parents.length == 0) {
            updated = PredictionContext.EMPTY;
        }
        else if (parents.length == 1) {
            updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0));
        }
        else {
            final ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
            updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
        }
        contextCache.add(updated);
        visited.put(updated, updated);
        visited.put(context, updated);
        return updated;
    }
    
    public static List<PredictionContext> getAllContextNodes(final PredictionContext context) {
        final List<PredictionContext> nodes = new ArrayList<PredictionContext>();
        final Map<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
        getAllContextNodes_(context, nodes, visited);
        return nodes;
    }
    
    public static void getAllContextNodes_(final PredictionContext context, final List<PredictionContext> nodes, final Map<PredictionContext, PredictionContext> visited) {
        if (context == null || visited.containsKey(context)) {
            return;
        }
        visited.put(context, context);
        nodes.add(context);
        for (int i = 0; i < context.size(); ++i) {
            getAllContextNodes_(context.getParent(i), nodes, visited);
        }
    }
    
    public String toString(final Recognizer<?, ?> recog) {
        return this.toString();
    }
    
    public String[] toStrings(final Recognizer<?, ?> recognizer, final int currentState) {
        return this.toStrings(recognizer, PredictionContext.EMPTY, currentState);
    }
    
    public String[] toStrings(final Recognizer<?, ?> recognizer, final PredictionContext stop, final int currentState) {
        final List<String> result = new ArrayList<String>();
        int perm = 0;
    Label_0012:
        while (true) {
            int offset = 0;
            boolean last = true;
            PredictionContext p = this;
            int stateNumber = currentState;
            final StringBuilder localBuffer = new StringBuilder();
            localBuffer.append("[");
            while (true) {
                while (!p.isEmpty() && p != stop) {
                    int index = 0;
                    if (p.size() > 0) {
                        int bits;
                        for (bits = 1; 1 << bits < p.size(); ++bits) {}
                        final int mask = (1 << bits) - 1;
                        index = (perm >> offset & mask);
                        last &= (index >= p.size() - 1);
                        if (index >= p.size()) {
                            ++perm;
                            continue Label_0012;
                        }
                        offset += bits;
                    }
                    if (recognizer != null) {
                        if (localBuffer.length() > 1) {
                            localBuffer.append(' ');
                        }
                        final ATN atn = recognizer.getATN();
                        final ATNState s = atn.states.get(stateNumber);
                        final String ruleName = recognizer.getRuleNames()[s.ruleIndex];
                        localBuffer.append(ruleName);
                    }
                    else if (p.getReturnState(index) != Integer.MAX_VALUE && !p.isEmpty()) {
                        if (localBuffer.length() > 1) {
                            localBuffer.append(' ');
                        }
                        localBuffer.append(p.getReturnState(index));
                    }
                    stateNumber = p.getReturnState(index);
                    p = p.getParent(index);
                }
                localBuffer.append("]");
                result.add(localBuffer.toString());
                if (last) {
                    break;
                }
                continue;
            }
        }
        return result.toArray(new String[result.size()]);
    }
    
    static {
        EMPTY = new EmptyPredictionContext();
        PredictionContext.globalNodeCount = 0;
    }
}
