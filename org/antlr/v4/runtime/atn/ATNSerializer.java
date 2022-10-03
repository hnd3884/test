package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.Utils;
import java.util.UUID;
import java.io.InvalidClassException;
import java.util.Iterator;
import java.util.Map;
import java.util.Locale;
import org.antlr.v4.runtime.misc.Interval;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.IntervalSet;
import java.util.HashMap;
import org.antlr.v4.runtime.misc.IntegerList;
import java.util.List;

public class ATNSerializer
{
    public ATN atn;
    private List<String> tokenNames;
    
    public ATNSerializer(final ATN atn) {
        assert atn.grammarType != null;
        this.atn = atn;
    }
    
    public ATNSerializer(final ATN atn, final List<String> tokenNames) {
        assert atn.grammarType != null;
        this.atn = atn;
        this.tokenNames = tokenNames;
    }
    
    public IntegerList serialize() {
        final IntegerList data = new IntegerList();
        data.add(ATNDeserializer.SERIALIZED_VERSION);
        this.serializeUUID(data, ATNDeserializer.SERIALIZED_UUID);
        data.add(this.atn.grammarType.ordinal());
        data.add(this.atn.maxTokenType);
        int nedges = 0;
        final Map<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
        final List<IntervalSet> sets = new ArrayList<IntervalSet>();
        final IntegerList nonGreedyStates = new IntegerList();
        final IntegerList precedenceStates = new IntegerList();
        data.add(this.atn.states.size());
        for (final ATNState s : this.atn.states) {
            if (s == null) {
                data.add(0);
            }
            else {
                final int stateType = s.getStateType();
                if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
                    nonGreedyStates.add(s.stateNumber);
                }
                if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
                    precedenceStates.add(s.stateNumber);
                }
                data.add(stateType);
                if (s.ruleIndex == -1) {
                    data.add(65535);
                }
                else {
                    data.add(s.ruleIndex);
                }
                if (s.getStateType() == 12) {
                    data.add(((LoopEndState)s).loopBackState.stateNumber);
                }
                else if (s instanceof BlockStartState) {
                    data.add(((BlockStartState)s).endState.stateNumber);
                }
                if (s.getStateType() != 7) {
                    nedges += s.getNumberOfTransitions();
                }
                for (int i = 0; i < s.getNumberOfTransitions(); ++i) {
                    final Transition t = s.transition(i);
                    final int edgeType = Transition.serializationTypes.get(t.getClass());
                    if (edgeType == 7 || edgeType == 8) {
                        final SetTransition st = (SetTransition)t;
                        if (!setIndices.containsKey(st.set)) {
                            sets.add(st.set);
                            setIndices.put(st.set, sets.size() - 1);
                        }
                    }
                }
            }
        }
        data.add(nonGreedyStates.size());
        for (int j = 0; j < nonGreedyStates.size(); ++j) {
            data.add(nonGreedyStates.get(j));
        }
        data.add(precedenceStates.size());
        for (int j = 0; j < precedenceStates.size(); ++j) {
            data.add(precedenceStates.get(j));
        }
        final int nrules = this.atn.ruleToStartState.length;
        data.add(nrules);
        for (int r = 0; r < nrules; ++r) {
            final ATNState ruleStartState = this.atn.ruleToStartState[r];
            data.add(ruleStartState.stateNumber);
            if (this.atn.grammarType == ATNType.LEXER) {
                if (this.atn.ruleToTokenType[r] == -1) {
                    data.add(65535);
                }
                else {
                    data.add(this.atn.ruleToTokenType[r]);
                }
            }
        }
        final int nmodes = this.atn.modeToStartState.size();
        data.add(nmodes);
        if (nmodes > 0) {
            for (final ATNState modeStartState : this.atn.modeToStartState) {
                data.add(modeStartState.stateNumber);
            }
        }
        final int nsets = sets.size();
        data.add(nsets);
        for (final IntervalSet set : sets) {
            final boolean containsEof = set.contains(-1);
            if (containsEof && set.getIntervals().get(0).b == -1) {
                data.add(set.getIntervals().size() - 1);
            }
            else {
                data.add(set.getIntervals().size());
            }
            data.add(containsEof ? 1 : 0);
            for (final Interval I : set.getIntervals()) {
                if (I.a == -1) {
                    if (I.b == -1) {
                        continue;
                    }
                    data.add(0);
                }
                else {
                    data.add(I.a);
                }
                data.add(I.b);
            }
        }
        data.add(nedges);
        for (final ATNState s2 : this.atn.states) {
            if (s2 == null) {
                continue;
            }
            if (s2.getStateType() == 7) {
                continue;
            }
            for (int k = 0; k < s2.getNumberOfTransitions(); ++k) {
                final Transition t2 = s2.transition(k);
                if (this.atn.states.get(t2.target.stateNumber) == null) {
                    throw new IllegalStateException("Cannot serialize a transition to a removed state.");
                }
                final int src = s2.stateNumber;
                int trg = t2.target.stateNumber;
                final int edgeType2 = Transition.serializationTypes.get(t2.getClass());
                int arg1 = 0;
                int arg2 = 0;
                int arg3 = 0;
                switch (edgeType2) {
                    case 3: {
                        trg = ((RuleTransition)t2).followState.stateNumber;
                        arg1 = ((RuleTransition)t2).target.stateNumber;
                        arg2 = ((RuleTransition)t2).ruleIndex;
                        arg3 = ((RuleTransition)t2).precedence;
                        break;
                    }
                    case 10: {
                        final PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t2;
                        arg1 = ppt.precedence;
                        break;
                    }
                    case 4: {
                        final PredicateTransition pt = (PredicateTransition)t2;
                        arg1 = pt.ruleIndex;
                        arg2 = pt.predIndex;
                        arg3 = (pt.isCtxDependent ? 1 : 0);
                        break;
                    }
                    case 2: {
                        arg1 = ((RangeTransition)t2).from;
                        arg2 = ((RangeTransition)t2).to;
                        if (arg1 == -1) {
                            arg1 = 0;
                            arg3 = 1;
                            break;
                        }
                        break;
                    }
                    case 5: {
                        arg1 = ((AtomTransition)t2).label;
                        if (arg1 == -1) {
                            arg1 = 0;
                            arg3 = 1;
                            break;
                        }
                        break;
                    }
                    case 6: {
                        final ActionTransition at = (ActionTransition)t2;
                        arg1 = at.ruleIndex;
                        arg2 = at.actionIndex;
                        if (arg2 == -1) {
                            arg2 = 65535;
                        }
                        arg3 = (at.isCtxDependent ? 1 : 0);
                        break;
                    }
                    case 7: {
                        arg1 = setIndices.get(((SetTransition)t2).set);
                        break;
                    }
                    case 8: {
                        arg1 = setIndices.get(((SetTransition)t2).set);
                        break;
                    }
                }
                data.add(src);
                data.add(trg);
                data.add(edgeType2);
                data.add(arg1);
                data.add(arg2);
                data.add(arg3);
            }
        }
        final int ndecisions = this.atn.decisionToState.size();
        data.add(ndecisions);
        for (final DecisionState decStartState : this.atn.decisionToState) {
            data.add(decStartState.stateNumber);
        }
        if (this.atn.grammarType == ATNType.LEXER) {
            data.add(this.atn.lexerActions.length);
            for (final LexerAction action : this.atn.lexerActions) {
                data.add(action.getActionType().ordinal());
                switch (action.getActionType()) {
                    case CHANNEL: {
                        final int channel = ((LexerChannelAction)action).getChannel();
                        data.add((channel != -1) ? channel : 65535);
                        data.add(0);
                        break;
                    }
                    case CUSTOM: {
                        final int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
                        final int actionIndex = ((LexerCustomAction)action).getActionIndex();
                        data.add((ruleIndex != -1) ? ruleIndex : 65535);
                        data.add((actionIndex != -1) ? actionIndex : 65535);
                        break;
                    }
                    case MODE: {
                        final int mode = ((LexerModeAction)action).getMode();
                        data.add((mode != -1) ? mode : 65535);
                        data.add(0);
                        break;
                    }
                    case MORE: {
                        data.add(0);
                        data.add(0);
                        break;
                    }
                    case POP_MODE: {
                        data.add(0);
                        data.add(0);
                        break;
                    }
                    case PUSH_MODE: {
                        final int mode = ((LexerPushModeAction)action).getMode();
                        data.add((mode != -1) ? mode : 65535);
                        data.add(0);
                        break;
                    }
                    case SKIP: {
                        data.add(0);
                        data.add(0);
                        break;
                    }
                    case TYPE: {
                        final int type = ((LexerTypeAction)action).getType();
                        data.add((type != -1) ? type : 65535);
                        data.add(0);
                        break;
                    }
                    default: {
                        final String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }
        for (int l = 1; l < data.size(); ++l) {
            if (data.get(l) < 0 || data.get(l) > 65535) {
                throw new UnsupportedOperationException("Serialized ATN data element out of range.");
            }
            final int value = data.get(l) + 2 & 0xFFFF;
            data.set(l, value);
        }
        return data;
    }
    
    public String decode(char[] data) {
        data = data.clone();
        for (int i = 1; i < data.length; ++i) {
            data[i] -= '\u0002';
        }
        final StringBuilder buf = new StringBuilder();
        int p = 0;
        final int version = ATNDeserializer.toInt(data[p++]);
        if (version != ATNDeserializer.SERIALIZED_VERSION) {
            final String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        final UUID uuid = ATNDeserializer.toUUID(data, p);
        p += 8;
        if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
            final String reason2 = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason2));
        }
        ++p;
        final int maxType = ATNDeserializer.toInt(data[p++]);
        buf.append("max type ").append(maxType).append("\n");
        for (int nstates = ATNDeserializer.toInt(data[p++]), j = 0; j < nstates; ++j) {
            final int stype = ATNDeserializer.toInt(data[p++]);
            if (stype != 0) {
                int ruleIndex = ATNDeserializer.toInt(data[p++]);
                if (ruleIndex == 65535) {
                    ruleIndex = -1;
                }
                String arg = "";
                if (stype == 12) {
                    final int loopBackStateNumber = ATNDeserializer.toInt(data[p++]);
                    arg = " " + loopBackStateNumber;
                }
                else if (stype == 4 || stype == 5 || stype == 3) {
                    final int endStateNumber = ATNDeserializer.toInt(data[p++]);
                    arg = " " + endStateNumber;
                }
                buf.append(j).append(":").append(ATNState.serializationNames.get(stype)).append(" ").append(ruleIndex).append(arg).append("\n");
            }
        }
        for (int numNonGreedyStates = ATNDeserializer.toInt(data[p++]), k = 0; k < numNonGreedyStates; ++k) {
            final int stateNumber = ATNDeserializer.toInt(data[p++]);
        }
        for (int numPrecedenceStates = ATNDeserializer.toInt(data[p++]), l = 0; l < numPrecedenceStates; ++l) {
            final int stateNumber2 = ATNDeserializer.toInt(data[p++]);
        }
        for (int nrules = ATNDeserializer.toInt(data[p++]), m = 0; m < nrules; ++m) {
            final int s = ATNDeserializer.toInt(data[p++]);
            if (this.atn.grammarType == ATNType.LEXER) {
                final int arg2 = ATNDeserializer.toInt(data[p++]);
                buf.append("rule ").append(m).append(":").append(s).append(" ").append(arg2).append('\n');
            }
            else {
                buf.append("rule ").append(m).append(":").append(s).append('\n');
            }
        }
        for (int nmodes = ATNDeserializer.toInt(data[p++]), i2 = 0; i2 < nmodes; ++i2) {
            final int s2 = ATNDeserializer.toInt(data[p++]);
            buf.append("mode ").append(i2).append(":").append(s2).append('\n');
        }
        for (int nsets = ATNDeserializer.toInt(data[p++]), i3 = 0; i3 < nsets; ++i3) {
            final int nintervals = ATNDeserializer.toInt(data[p++]);
            buf.append(i3).append(":");
            final boolean containsEof = data[p++] != '\0';
            if (containsEof) {
                buf.append(this.getTokenName(-1));
            }
            for (int j2 = 0; j2 < nintervals; ++j2) {
                if (containsEof || j2 > 0) {
                    buf.append(", ");
                }
                buf.append(this.getTokenName(ATNDeserializer.toInt(data[p]))).append("..").append(this.getTokenName(ATNDeserializer.toInt(data[p + 1])));
                p += 2;
            }
            buf.append("\n");
        }
        for (int nedges = ATNDeserializer.toInt(data[p++]), i4 = 0; i4 < nedges; ++i4) {
            final int src = ATNDeserializer.toInt(data[p]);
            final int trg = ATNDeserializer.toInt(data[p + 1]);
            final int ttype = ATNDeserializer.toInt(data[p + 2]);
            final int arg3 = ATNDeserializer.toInt(data[p + 3]);
            final int arg4 = ATNDeserializer.toInt(data[p + 4]);
            final int arg5 = ATNDeserializer.toInt(data[p + 5]);
            buf.append(src).append("->").append(trg).append(" ").append(Transition.serializationNames.get(ttype)).append(" ").append(arg3).append(",").append(arg4).append(",").append(arg5).append("\n");
            p += 6;
        }
        for (int ndecisions = ATNDeserializer.toInt(data[p++]), i5 = 0; i5 < ndecisions; ++i5) {
            final int s3 = ATNDeserializer.toInt(data[p++]);
            buf.append(i5).append(":").append(s3).append("\n");
        }
        if (this.atn.grammarType == ATNType.LEXER) {
            for (int lexerActionCount = ATNDeserializer.toInt(data[p++]), i6 = 0; i6 < lexerActionCount; ++i6) {
                final LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
                final int data2 = ATNDeserializer.toInt(data[p++]);
                final int data3 = ATNDeserializer.toInt(data[p++]);
            }
        }
        return buf.toString();
    }
    
    public String getTokenName(final int t) {
        if (t == -1) {
            return "EOF";
        }
        if (this.atn.grammarType == ATNType.LEXER && t >= 0 && t <= 65535) {
            switch (t) {
                case 10: {
                    return "'\\n'";
                }
                case 13: {
                    return "'\\r'";
                }
                case 9: {
                    return "'\\t'";
                }
                case 8: {
                    return "'\\b'";
                }
                case 12: {
                    return "'\\f'";
                }
                case 92: {
                    return "'\\\\'";
                }
                case 39: {
                    return "'\\''";
                }
                default: {
                    if (Character.UnicodeBlock.of((char)t) == Character.UnicodeBlock.BASIC_LATIN && !Character.isISOControl((char)t)) {
                        return '\'' + Character.toString((char)t) + '\'';
                    }
                    final String hex = Integer.toHexString(t | 0x10000).toUpperCase().substring(1, 5);
                    final String unicodeStr = "'\\u" + hex + "'";
                    return unicodeStr;
                }
            }
        }
        else {
            if (this.tokenNames != null && t >= 0 && t < this.tokenNames.size()) {
                return this.tokenNames.get(t);
            }
            return String.valueOf(t);
        }
    }
    
    public static String getSerializedAsString(final ATN atn) {
        return new String(getSerializedAsChars(atn));
    }
    
    public static IntegerList getSerialized(final ATN atn) {
        return new ATNSerializer(atn).serialize();
    }
    
    public static char[] getSerializedAsChars(final ATN atn) {
        return Utils.toCharArray(getSerialized(atn));
    }
    
    public static String getDecoded(final ATN atn, final List<String> tokenNames) {
        final IntegerList serialized = getSerialized(atn);
        final char[] data = Utils.toCharArray(serialized);
        return new ATNSerializer(atn, tokenNames).decode(data);
    }
    
    private void serializeUUID(final IntegerList data, final UUID uuid) {
        this.serializeLong(data, uuid.getLeastSignificantBits());
        this.serializeLong(data, uuid.getMostSignificantBits());
    }
    
    private void serializeLong(final IntegerList data, final long value) {
        this.serializeInt(data, (int)value);
        this.serializeInt(data, (int)(value >> 32));
    }
    
    private void serializeInt(final IntegerList data, final int value) {
        data.add((char)value);
        data.add((char)(value >> 16));
    }
}
