package org.antlr.v4.runtime.atn;

import java.util.Iterator;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;
import java.util.ArrayList;
import java.io.InvalidClassException;
import java.util.Locale;
import java.util.List;
import java.util.UUID;

public class ATNDeserializer
{
    public static final int SERIALIZED_VERSION;
    private static final UUID BASE_SERIALIZED_UUID;
    private static final UUID ADDED_PRECEDENCE_TRANSITIONS;
    private static final UUID ADDED_LEXER_ACTIONS;
    private static final List<UUID> SUPPORTED_UUIDS;
    public static final UUID SERIALIZED_UUID;
    private final ATNDeserializationOptions deserializationOptions;
    
    public ATNDeserializer() {
        this(ATNDeserializationOptions.getDefaultOptions());
    }
    
    public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
        if (deserializationOptions == null) {
            deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
        }
        this.deserializationOptions = deserializationOptions;
    }
    
    protected boolean isFeatureSupported(final UUID feature, final UUID actualUuid) {
        final int featureIndex = ATNDeserializer.SUPPORTED_UUIDS.indexOf(feature);
        return featureIndex >= 0 && ATNDeserializer.SUPPORTED_UUIDS.indexOf(actualUuid) >= featureIndex;
    }
    
    public ATN deserialize(char[] data) {
        data = data.clone();
        for (int i = 1; i < data.length; ++i) {
            data[i] -= '\u0002';
        }
        int p = 0;
        final int version = toInt(data[p++]);
        if (version != ATNDeserializer.SERIALIZED_VERSION) {
            final String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        final UUID uuid = toUUID(data, p);
        p += 8;
        if (!ATNDeserializer.SUPPORTED_UUIDS.contains(uuid)) {
            final String reason2 = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", uuid, ATNDeserializer.SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason2));
        }
        final boolean supportsPrecedencePredicates = this.isFeatureSupported(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS, uuid);
        final boolean supportsLexerActions = this.isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid);
        final ATNType grammarType = ATNType.values()[toInt(data[p++])];
        final int maxTokenType = toInt(data[p++]);
        final ATN atn = new ATN(grammarType, maxTokenType);
        final List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
        final List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
        for (int nstates = toInt(data[p++]), j = 0; j < nstates; ++j) {
            final int stype = toInt(data[p++]);
            if (stype == 0) {
                atn.addState(null);
            }
            else {
                int ruleIndex = toInt(data[p++]);
                if (ruleIndex == 65535) {
                    ruleIndex = -1;
                }
                final ATNState s = this.stateFactory(stype, ruleIndex);
                if (stype == 12) {
                    final int loopBackStateNumber = toInt(data[p++]);
                    loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
                }
                else if (s instanceof BlockStartState) {
                    final int endStateNumber = toInt(data[p++]);
                    endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, endStateNumber));
                }
                atn.addState(s);
            }
        }
        for (final Pair<LoopEndState, Integer> pair : loopBackStateNumbers) {
            pair.a.loopBackState = atn.states.get(pair.b);
        }
        for (final Pair<BlockStartState, Integer> pair2 : endStateNumbers) {
            pair2.a.endState = atn.states.get(pair2.b);
        }
        for (int numNonGreedyStates = toInt(data[p++]), k = 0; k < numNonGreedyStates; ++k) {
            final int stateNumber = toInt(data[p++]);
            atn.states.get(stateNumber).nonGreedy = true;
        }
        if (supportsPrecedencePredicates) {
            for (int numPrecedenceStates = toInt(data[p++]), l = 0; l < numPrecedenceStates; ++l) {
                final int stateNumber2 = toInt(data[p++]);
                atn.states.get(stateNumber2).isLeftRecursiveRule = true;
            }
        }
        final int nrules = toInt(data[p++]);
        if (atn.grammarType == ATNType.LEXER) {
            atn.ruleToTokenType = new int[nrules];
        }
        atn.ruleToStartState = new RuleStartState[nrules];
        for (int l = 0; l < nrules; ++l) {
            final int s2 = toInt(data[p++]);
            final RuleStartState startState = atn.states.get(s2);
            atn.ruleToStartState[l] = startState;
            if (atn.grammarType == ATNType.LEXER) {
                int tokenType = toInt(data[p++]);
                if (tokenType == 65535) {
                    tokenType = -1;
                }
                atn.ruleToTokenType[l] = tokenType;
                if (!this.isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid)) {
                    final int actionIndexIgnored = toInt(data[p++]);
                }
            }
        }
        atn.ruleToStopState = new RuleStopState[nrules];
        for (final ATNState state : atn.states) {
            if (!(state instanceof RuleStopState)) {
                continue;
            }
            final RuleStopState stopState = (RuleStopState)state;
            atn.ruleToStopState[state.ruleIndex] = stopState;
            atn.ruleToStartState[state.ruleIndex].stopState = stopState;
        }
        for (int nmodes = toInt(data[p++]), m = 0; m < nmodes; ++m) {
            final int s3 = toInt(data[p++]);
            atn.modeToStartState.add(atn.states.get(s3));
        }
        final List<IntervalSet> sets = new ArrayList<IntervalSet>();
        for (int nsets = toInt(data[p++]), i2 = 0; i2 < nsets; ++i2) {
            final int nintervals = toInt(data[p]);
            ++p;
            final IntervalSet set = new IntervalSet(new int[0]);
            sets.add(set);
            final boolean containsEof = toInt(data[p++]) != 0;
            if (containsEof) {
                set.add(-1);
            }
            for (int j2 = 0; j2 < nintervals; ++j2) {
                set.add(toInt(data[p]), toInt(data[p + 1]));
                p += 2;
            }
        }
        for (int nedges = toInt(data[p++]), i3 = 0; i3 < nedges; ++i3) {
            final int src = toInt(data[p]);
            final int trg = toInt(data[p + 1]);
            final int ttype = toInt(data[p + 2]);
            final int arg1 = toInt(data[p + 3]);
            final int arg2 = toInt(data[p + 4]);
            final int arg3 = toInt(data[p + 5]);
            final Transition trans = this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
            final ATNState srcState = atn.states.get(src);
            srcState.addTransition(trans);
            p += 6;
        }
        for (final ATNState state2 : atn.states) {
            for (int i4 = 0; i4 < state2.getNumberOfTransitions(); ++i4) {
                final Transition t = state2.transition(i4);
                if (t instanceof RuleTransition) {
                    final RuleTransition ruleTransition = (RuleTransition)t;
                    int outermostPrecedenceReturn = -1;
                    if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule && ruleTransition.precedence == 0) {
                        outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
                    }
                    final EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
                    atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
                }
            }
        }
        for (final ATNState state2 : atn.states) {
            if (state2 instanceof BlockStartState) {
                if (((BlockStartState)state2).endState == null) {
                    throw new IllegalStateException();
                }
                if (((BlockStartState)state2).endState.startState != null) {
                    throw new IllegalStateException();
                }
                ((BlockStartState)state2).endState.startState = (BlockStartState)state2;
            }
            if (state2 instanceof PlusLoopbackState) {
                final PlusLoopbackState loopbackState = (PlusLoopbackState)state2;
                for (int i5 = 0; i5 < loopbackState.getNumberOfTransitions(); ++i5) {
                    final ATNState target = loopbackState.transition(i5).target;
                    if (target instanceof PlusBlockStartState) {
                        ((PlusBlockStartState)target).loopBackState = loopbackState;
                    }
                }
            }
            else {
                if (!(state2 instanceof StarLoopbackState)) {
                    continue;
                }
                final StarLoopbackState loopbackState2 = (StarLoopbackState)state2;
                for (int i5 = 0; i5 < loopbackState2.getNumberOfTransitions(); ++i5) {
                    final ATNState target = loopbackState2.transition(i5).target;
                    if (target instanceof StarLoopEntryState) {
                        ((StarLoopEntryState)target).loopBackState = loopbackState2;
                    }
                }
            }
        }
        for (int ndecisions = toInt(data[p++]), i6 = 1; i6 <= ndecisions; ++i6) {
            final int s4 = toInt(data[p++]);
            final DecisionState decState = atn.states.get(s4);
            atn.decisionToState.add(decState);
            decState.decision = i6 - 1;
        }
        if (atn.grammarType == ATNType.LEXER) {
            if (supportsLexerActions) {
                atn.lexerActions = new LexerAction[toInt(data[p++])];
                for (int i6 = 0; i6 < atn.lexerActions.length; ++i6) {
                    final LexerActionType actionType = LexerActionType.values()[toInt(data[p++])];
                    int data2 = toInt(data[p++]);
                    if (data2 == 65535) {
                        data2 = -1;
                    }
                    int data3 = toInt(data[p++]);
                    if (data3 == 65535) {
                        data3 = -1;
                    }
                    final LexerAction lexerAction = this.lexerActionFactory(actionType, data2, data3);
                    atn.lexerActions[i6] = lexerAction;
                }
            }
            else {
                final List<LexerAction> legacyLexerActions = new ArrayList<LexerAction>();
                for (final ATNState state3 : atn.states) {
                    for (int i7 = 0; i7 < state3.getNumberOfTransitions(); ++i7) {
                        final Transition transition = state3.transition(i7);
                        if (transition instanceof ActionTransition) {
                            final int ruleIndex2 = ((ActionTransition)transition).ruleIndex;
                            final int actionIndex = ((ActionTransition)transition).actionIndex;
                            final LexerCustomAction lexerAction2 = new LexerCustomAction(ruleIndex2, actionIndex);
                            state3.setTransition(i7, new ActionTransition(transition.target, ruleIndex2, legacyLexerActions.size(), false));
                            legacyLexerActions.add(lexerAction2);
                        }
                    }
                }
                atn.lexerActions = legacyLexerActions.toArray(new LexerAction[legacyLexerActions.size()]);
            }
        }
        this.markPrecedenceDecisions(atn);
        if (this.deserializationOptions.isVerifyATN()) {
            this.verifyATN(atn);
        }
        if (this.deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
            atn.ruleToTokenType = new int[atn.ruleToStartState.length];
            for (int i6 = 0; i6 < atn.ruleToStartState.length; ++i6) {
                atn.ruleToTokenType[i6] = atn.maxTokenType + i6 + 1;
            }
            for (int i6 = 0; i6 < atn.ruleToStartState.length; ++i6) {
                final BasicBlockStartState bypassStart = new BasicBlockStartState();
                bypassStart.ruleIndex = i6;
                atn.addState(bypassStart);
                final BlockEndState bypassStop = new BlockEndState();
                bypassStop.ruleIndex = i6;
                atn.addState(bypassStop);
                bypassStart.endState = bypassStop;
                atn.defineDecisionState(bypassStart);
                bypassStop.startState = bypassStart;
                Transition excludeTransition = null;
                ATNState endState;
                if (atn.ruleToStartState[i6].isLeftRecursiveRule) {
                    endState = null;
                    for (final ATNState state4 : atn.states) {
                        if (state4.ruleIndex != i6) {
                            continue;
                        }
                        if (!(state4 instanceof StarLoopEntryState)) {
                            continue;
                        }
                        final ATNState maybeLoopEndState = state4.transition(state4.getNumberOfTransitions() - 1).target;
                        if (!(maybeLoopEndState instanceof LoopEndState)) {
                            continue;
                        }
                        if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
                            endState = state4;
                            break;
                        }
                    }
                    if (endState == null) {
                        throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
                    }
                    excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
                }
                else {
                    endState = atn.ruleToStopState[i6];
                }
                for (final ATNState state4 : atn.states) {
                    for (final Transition transition2 : state4.transitions) {
                        if (transition2 == excludeTransition) {
                            continue;
                        }
                        if (transition2.target != endState) {
                            continue;
                        }
                        transition2.target = bypassStop;
                    }
                }
                while (atn.ruleToStartState[i6].getNumberOfTransitions() > 0) {
                    final Transition transition3 = atn.ruleToStartState[i6].removeTransition(atn.ruleToStartState[i6].getNumberOfTransitions() - 1);
                    bypassStart.addTransition(transition3);
                }
                atn.ruleToStartState[i6].addTransition(new EpsilonTransition(bypassStart));
                bypassStop.addTransition(new EpsilonTransition(endState));
                final ATNState matchState = new BasicState();
                atn.addState(matchState);
                matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i6]));
                bypassStart.addTransition(new EpsilonTransition(matchState));
            }
            if (this.deserializationOptions.isVerifyATN()) {
                this.verifyATN(atn);
            }
        }
        return atn;
    }
    
    protected void markPrecedenceDecisions(final ATN atn) {
        for (final ATNState state : atn.states) {
            if (!(state instanceof StarLoopEntryState)) {
                continue;
            }
            if (!atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule) {
                continue;
            }
            final ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
            if (!(maybeLoopEndState instanceof LoopEndState) || !maybeLoopEndState.epsilonOnlyTransitions || !(maybeLoopEndState.transition(0).target instanceof RuleStopState)) {
                continue;
            }
            ((StarLoopEntryState)state).isPrecedenceDecision = true;
        }
    }
    
    protected void verifyATN(final ATN atn) {
        for (final ATNState state : atn.states) {
            if (state == null) {
                continue;
            }
            this.checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1);
            if (state instanceof PlusBlockStartState) {
                this.checkCondition(((PlusBlockStartState)state).loopBackState != null);
            }
            if (state instanceof StarLoopEntryState) {
                final StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
                this.checkCondition(starLoopEntryState.loopBackState != null);
                this.checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);
                if (starLoopEntryState.transition(0).target instanceof StarBlockStartState) {
                    this.checkCondition(starLoopEntryState.transition(1).target instanceof LoopEndState);
                    this.checkCondition(!starLoopEntryState.nonGreedy);
                }
                else {
                    if (!(starLoopEntryState.transition(0).target instanceof LoopEndState)) {
                        throw new IllegalStateException();
                    }
                    this.checkCondition(starLoopEntryState.transition(1).target instanceof StarBlockStartState);
                    this.checkCondition(starLoopEntryState.nonGreedy);
                }
            }
            if (state instanceof StarLoopbackState) {
                this.checkCondition(state.getNumberOfTransitions() == 1);
                this.checkCondition(state.transition(0).target instanceof StarLoopEntryState);
            }
            if (state instanceof LoopEndState) {
                this.checkCondition(((LoopEndState)state).loopBackState != null);
            }
            if (state instanceof RuleStartState) {
                this.checkCondition(((RuleStartState)state).stopState != null);
            }
            if (state instanceof BlockStartState) {
                this.checkCondition(((BlockStartState)state).endState != null);
            }
            if (state instanceof BlockEndState) {
                this.checkCondition(((BlockEndState)state).startState != null);
            }
            if (state instanceof DecisionState) {
                final DecisionState decisionState = (DecisionState)state;
                this.checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
            }
            else {
                this.checkCondition(state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState);
            }
        }
    }
    
    protected void checkCondition(final boolean condition) {
        this.checkCondition(condition, null);
    }
    
    protected void checkCondition(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
    
    protected static int toInt(final char c) {
        return c;
    }
    
    protected static int toInt32(final char[] data, final int offset) {
        return data[offset] | data[offset + 1] << 16;
    }
    
    protected static long toLong(final char[] data, final int offset) {
        final long lowOrder = (long)toInt32(data, offset) & 0xFFFFFFFFL;
        return lowOrder | (long)toInt32(data, offset + 2) << 32;
    }
    
    protected static UUID toUUID(final char[] data, final int offset) {
        final long leastSigBits = toLong(data, offset);
        final long mostSigBits = toLong(data, offset + 4);
        return new UUID(mostSigBits, leastSigBits);
    }
    
    protected Transition edgeFactory(final ATN atn, final int type, final int src, final int trg, final int arg1, final int arg2, final int arg3, final List<IntervalSet> sets) {
        final ATNState target = atn.states.get(trg);
        switch (type) {
            case 1: {
                return new EpsilonTransition(target);
            }
            case 2: {
                if (arg3 != 0) {
                    return new RangeTransition(target, -1, arg2);
                }
                return new RangeTransition(target, arg1, arg2);
            }
            case 3: {
                final RuleTransition rt = new RuleTransition(atn.states.get(arg1), arg2, arg3, target);
                return rt;
            }
            case 4: {
                final PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                return pt;
            }
            case 10: {
                return new PrecedencePredicateTransition(target, arg1);
            }
            case 5: {
                if (arg3 != 0) {
                    return new AtomTransition(target, -1);
                }
                return new AtomTransition(target, arg1);
            }
            case 6: {
                final ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                return a;
            }
            case 7: {
                return new SetTransition(target, sets.get(arg1));
            }
            case 8: {
                return new NotSetTransition(target, sets.get(arg1));
            }
            case 9: {
                return new WildcardTransition(target);
            }
            default: {
                throw new IllegalArgumentException("The specified transition type is not valid.");
            }
        }
    }
    
    protected ATNState stateFactory(final int type, final int ruleIndex) {
        ATNState s = null;
        switch (type) {
            case 0: {
                return null;
            }
            case 1: {
                s = new BasicState();
                break;
            }
            case 2: {
                s = new RuleStartState();
                break;
            }
            case 3: {
                s = new BasicBlockStartState();
                break;
            }
            case 4: {
                s = new PlusBlockStartState();
                break;
            }
            case 5: {
                s = new StarBlockStartState();
                break;
            }
            case 6: {
                s = new TokensStartState();
                break;
            }
            case 7: {
                s = new RuleStopState();
                break;
            }
            case 8: {
                s = new BlockEndState();
                break;
            }
            case 9: {
                s = new StarLoopbackState();
                break;
            }
            case 10: {
                s = new StarLoopEntryState();
                break;
            }
            case 11: {
                s = new PlusLoopbackState();
                break;
            }
            case 12: {
                s = new LoopEndState();
                break;
            }
            default: {
                final String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", type);
                throw new IllegalArgumentException(message);
            }
        }
        s.ruleIndex = ruleIndex;
        return s;
    }
    
    protected LexerAction lexerActionFactory(final LexerActionType type, final int data1, final int data2) {
        switch (type) {
            case CHANNEL: {
                return new LexerChannelAction(data1);
            }
            case CUSTOM: {
                return new LexerCustomAction(data1, data2);
            }
            case MODE: {
                return new LexerModeAction(data1);
            }
            case MORE: {
                return LexerMoreAction.INSTANCE;
            }
            case POP_MODE: {
                return LexerPopModeAction.INSTANCE;
            }
            case PUSH_MODE: {
                return new LexerPushModeAction(data1);
            }
            case SKIP: {
                return LexerSkipAction.INSTANCE;
            }
            case TYPE: {
                return new LexerTypeAction(data1);
            }
            default: {
                final String message = String.format(Locale.getDefault(), "The specified lexer action type %d is not valid.", type);
                throw new IllegalArgumentException(message);
            }
        }
    }
    
    static {
        SERIALIZED_VERSION = 3;
        BASE_SERIALIZED_UUID = UUID.fromString("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
        ADDED_PRECEDENCE_TRANSITIONS = UUID.fromString("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
        ADDED_LEXER_ACTIONS = UUID.fromString("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
        (SUPPORTED_UUIDS = new ArrayList<UUID>()).add(ATNDeserializer.BASE_SERIALIZED_UUID);
        ATNDeserializer.SUPPORTED_UUIDS.add(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS);
        ATNDeserializer.SUPPORTED_UUIDS.add(ATNDeserializer.ADDED_LEXER_ACTIONS);
        SERIALIZED_UUID = ATNDeserializer.ADDED_LEXER_ACTIONS;
    }
}
