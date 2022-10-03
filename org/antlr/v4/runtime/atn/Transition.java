package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.IntervalSet;
import java.util.Map;
import java.util.List;

public abstract class Transition
{
    public static final int EPSILON = 1;
    public static final int RANGE = 2;
    public static final int RULE = 3;
    public static final int PREDICATE = 4;
    public static final int ATOM = 5;
    public static final int ACTION = 6;
    public static final int SET = 7;
    public static final int NOT_SET = 8;
    public static final int WILDCARD = 9;
    public static final int PRECEDENCE = 10;
    public static final List<String> serializationNames;
    public static final Map<Class<? extends Transition>, Integer> serializationTypes;
    public ATNState target;
    
    protected Transition(final ATNState target) {
        if (target == null) {
            throw new NullPointerException("target cannot be null.");
        }
        this.target = target;
    }
    
    public abstract int getSerializationType();
    
    public boolean isEpsilon() {
        return false;
    }
    
    public IntervalSet label() {
        return null;
    }
    
    public abstract boolean matches(final int p0, final int p1, final int p2);
    
    static {
        serializationNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList("INVALID", "EPSILON", "RANGE", "RULE", "PREDICATE", "ATOM", "ACTION", "SET", "NOT_SET", "WILDCARD", "PRECEDENCE"));
        serializationTypes = Collections.unmodifiableMap((Map<? extends Class<? extends Transition>, ? extends Integer>)new HashMap<Class<? extends Transition>, Integer>() {
            {
                ((HashMap<Class<EpsilonTransition>, Integer>)this).put(EpsilonTransition.class, 1);
                ((HashMap<Class<RangeTransition>, Integer>)this).put(RangeTransition.class, 2);
                ((HashMap<Class<RuleTransition>, Integer>)this).put(RuleTransition.class, 3);
                ((HashMap<Class<PredicateTransition>, Integer>)this).put(PredicateTransition.class, 4);
                ((HashMap<Class<AtomTransition>, Integer>)this).put(AtomTransition.class, 5);
                ((HashMap<Class<ActionTransition>, Integer>)this).put(ActionTransition.class, 6);
                ((HashMap<Class<SetTransition>, Integer>)this).put(SetTransition.class, 7);
                ((HashMap<Class<NotSetTransition>, Integer>)this).put(NotSetTransition.class, 8);
                ((HashMap<Class<WildcardTransition>, Integer>)this).put(WildcardTransition.class, 9);
                ((HashMap<Class<PrecedencePredicateTransition>, Integer>)this).put(PrecedencePredicateTransition.class, 10);
            }
        });
    }
}
