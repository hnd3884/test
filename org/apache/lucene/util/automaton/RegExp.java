package org.apache.lucene.util.automaton;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

public class RegExp
{
    public static final int INTERSECTION = 1;
    public static final int COMPLEMENT = 2;
    public static final int EMPTY = 4;
    public static final int ANYSTRING = 8;
    public static final int AUTOMATON = 16;
    public static final int INTERVAL = 32;
    public static final int ALL = 65535;
    public static final int NONE = 0;
    private final String originalString;
    Kind kind;
    RegExp exp1;
    RegExp exp2;
    String s;
    int c;
    int min;
    int max;
    int digits;
    int from;
    int to;
    int flags;
    int pos;
    
    RegExp() {
        this.originalString = null;
    }
    
    public RegExp(final String s) throws IllegalArgumentException {
        this(s, 65535);
    }
    
    public RegExp(final String s, final int syntax_flags) throws IllegalArgumentException {
        this.originalString = s;
        this.flags = syntax_flags;
        RegExp e;
        if (s.length() == 0) {
            e = makeString("");
        }
        else {
            e = this.parseUnionExp();
            if (this.pos < this.originalString.length()) {
                throw new IllegalArgumentException("end-of-string expected at position " + this.pos);
            }
        }
        this.kind = e.kind;
        this.exp1 = e.exp1;
        this.exp2 = e.exp2;
        this.s = e.s;
        this.c = e.c;
        this.min = e.min;
        this.max = e.max;
        this.digits = e.digits;
        this.from = e.from;
        this.to = e.to;
    }
    
    public Automaton toAutomaton() {
        return this.toAutomaton(null, null, 10000);
    }
    
    public Automaton toAutomaton(final int maxDeterminizedStates) throws IllegalArgumentException, TooComplexToDeterminizeException {
        return this.toAutomaton(null, null, maxDeterminizedStates);
    }
    
    public Automaton toAutomaton(final AutomatonProvider automaton_provider, final int maxDeterminizedStates) throws IllegalArgumentException, TooComplexToDeterminizeException {
        return this.toAutomaton(null, automaton_provider, maxDeterminizedStates);
    }
    
    public Automaton toAutomaton(final Map<String, Automaton> automata, final int maxDeterminizedStates) throws IllegalArgumentException, TooComplexToDeterminizeException {
        return this.toAutomaton(automata, null, maxDeterminizedStates);
    }
    
    private Automaton toAutomaton(final Map<String, Automaton> automata, final AutomatonProvider automaton_provider, final int maxDeterminizedStates) throws IllegalArgumentException, TooComplexToDeterminizeException {
        try {
            return this.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates);
        }
        catch (final TooComplexToDeterminizeException e) {
            throw new TooComplexToDeterminizeException(this, e);
        }
    }
    
    private Automaton toAutomatonInternal(final Map<String, Automaton> automata, final AutomatonProvider automaton_provider, final int maxDeterminizedStates) throws IllegalArgumentException {
        Automaton a = null;
        switch (this.kind) {
            case REGEXP_UNION: {
                final List<Automaton> list = new ArrayList<Automaton>();
                this.findLeaves(this.exp1, Kind.REGEXP_UNION, list, automata, automaton_provider, maxDeterminizedStates);
                this.findLeaves(this.exp2, Kind.REGEXP_UNION, list, automata, automaton_provider, maxDeterminizedStates);
                a = Operations.union(list);
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_CONCATENATION: {
                final List<Automaton> list = new ArrayList<Automaton>();
                this.findLeaves(this.exp1, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider, maxDeterminizedStates);
                this.findLeaves(this.exp2, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider, maxDeterminizedStates);
                a = Operations.concatenate(list);
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_INTERSECTION: {
                a = Operations.intersection(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates), this.exp2.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates));
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_OPTIONAL: {
                a = Operations.optional(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates));
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_REPEAT: {
                a = Operations.repeat(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates));
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_REPEAT_MIN: {
                a = Operations.repeat(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates), this.min);
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_REPEAT_MINMAX: {
                a = Operations.repeat(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates), this.min, this.max);
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_COMPLEMENT: {
                a = Operations.complement(this.exp1.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates), maxDeterminizedStates);
                a = MinimizationOperations.minimize(a, maxDeterminizedStates);
                break;
            }
            case REGEXP_CHAR: {
                a = Automata.makeChar(this.c);
                break;
            }
            case REGEXP_CHAR_RANGE: {
                a = Automata.makeCharRange(this.from, this.to);
                break;
            }
            case REGEXP_ANYCHAR: {
                a = Automata.makeAnyChar();
                break;
            }
            case REGEXP_EMPTY: {
                a = Automata.makeEmpty();
                break;
            }
            case REGEXP_STRING: {
                a = Automata.makeString(this.s);
                break;
            }
            case REGEXP_ANYSTRING: {
                a = Automata.makeAnyString();
                break;
            }
            case REGEXP_AUTOMATON: {
                Automaton aa = null;
                if (automata != null) {
                    aa = automata.get(this.s);
                }
                if (aa == null && automaton_provider != null) {
                    try {
                        aa = automaton_provider.getAutomaton(this.s);
                    }
                    catch (final IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
                if (aa == null) {
                    throw new IllegalArgumentException("'" + this.s + "' not found");
                }
                a = aa;
                break;
            }
            case REGEXP_INTERVAL: {
                a = Automata.makeDecimalInterval(this.min, this.max, this.digits);
                break;
            }
        }
        return a;
    }
    
    private void findLeaves(final RegExp exp, final Kind kind, final List<Automaton> list, final Map<String, Automaton> automata, final AutomatonProvider automaton_provider, final int maxDeterminizedStates) {
        if (exp.kind == kind) {
            this.findLeaves(exp.exp1, kind, list, automata, automaton_provider, maxDeterminizedStates);
            this.findLeaves(exp.exp2, kind, list, automata, automaton_provider, maxDeterminizedStates);
        }
        else {
            list.add(exp.toAutomatonInternal(automata, automaton_provider, maxDeterminizedStates));
        }
    }
    
    public String getOriginalString() {
        return this.originalString;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        this.toStringBuilder(b);
        return b.toString();
    }
    
    void toStringBuilder(final StringBuilder b) {
        switch (this.kind) {
            case REGEXP_UNION: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("|");
                this.exp2.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_CONCATENATION: {
                this.exp1.toStringBuilder(b);
                this.exp2.toStringBuilder(b);
                break;
            }
            case REGEXP_INTERSECTION: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("&");
                this.exp2.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_OPTIONAL: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append(")?");
                break;
            }
            case REGEXP_REPEAT: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append(")*");
                break;
            }
            case REGEXP_REPEAT_MIN: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("){").append(this.min).append(",}");
                break;
            }
            case REGEXP_REPEAT_MINMAX: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("){").append(this.min).append(",").append(this.max).append("}");
                break;
            }
            case REGEXP_COMPLEMENT: {
                b.append("~(");
                this.exp1.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_CHAR: {
                b.append("\\").appendCodePoint(this.c);
                break;
            }
            case REGEXP_CHAR_RANGE: {
                b.append("[\\").appendCodePoint(this.from).append("-\\").appendCodePoint(this.to).append("]");
                break;
            }
            case REGEXP_ANYCHAR: {
                b.append(".");
                break;
            }
            case REGEXP_EMPTY: {
                b.append("#");
                break;
            }
            case REGEXP_STRING: {
                b.append("\"").append(this.s).append("\"");
                break;
            }
            case REGEXP_ANYSTRING: {
                b.append("@");
                break;
            }
            case REGEXP_AUTOMATON: {
                b.append("<").append(this.s).append(">");
                break;
            }
            case REGEXP_INTERVAL: {
                final String s1 = Integer.toString(this.min);
                final String s2 = Integer.toString(this.max);
                b.append("<");
                if (this.digits > 0) {
                    for (int i = s1.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s1).append("-");
                if (this.digits > 0) {
                    for (int i = s2.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s2).append(">");
                break;
            }
        }
    }
    
    public String toStringTree() {
        final StringBuilder b = new StringBuilder();
        this.toStringTree(b, "");
        return b.toString();
    }
    
    void toStringTree(final StringBuilder b, final String indent) {
        switch (this.kind) {
            case REGEXP_UNION:
            case REGEXP_CONCATENATION:
            case REGEXP_INTERSECTION: {
                b.append(indent);
                b.append(this.kind);
                b.append('\n');
                this.exp1.toStringTree(b, indent + "  ");
                this.exp2.toStringTree(b, indent + "  ");
                break;
            }
            case REGEXP_OPTIONAL:
            case REGEXP_REPEAT:
            case REGEXP_COMPLEMENT: {
                b.append(indent);
                b.append(this.kind);
                b.append('\n');
                this.exp1.toStringTree(b, indent + "  ");
                break;
            }
            case REGEXP_REPEAT_MIN: {
                b.append(indent);
                b.append(this.kind);
                b.append(" min=");
                b.append(this.min);
                b.append('\n');
                this.exp1.toStringTree(b, indent + "  ");
                break;
            }
            case REGEXP_REPEAT_MINMAX: {
                b.append(indent);
                b.append(this.kind);
                b.append(" min=");
                b.append(this.min);
                b.append(" max=");
                b.append(this.max);
                b.append('\n');
                this.exp1.toStringTree(b, indent + "  ");
                break;
            }
            case REGEXP_CHAR: {
                b.append(indent);
                b.append(this.kind);
                b.append(" char=");
                b.appendCodePoint(this.c);
                b.append('\n');
                break;
            }
            case REGEXP_CHAR_RANGE: {
                b.append(indent);
                b.append(this.kind);
                b.append(" from=");
                b.appendCodePoint(this.from);
                b.append(" to=");
                b.appendCodePoint(this.to);
                b.append('\n');
                break;
            }
            case REGEXP_ANYCHAR:
            case REGEXP_EMPTY: {
                b.append(indent);
                b.append(this.kind);
                b.append('\n');
                break;
            }
            case REGEXP_STRING: {
                b.append(indent);
                b.append(this.kind);
                b.append(" string=");
                b.append(this.s);
                b.append('\n');
                break;
            }
            case REGEXP_ANYSTRING: {
                b.append(indent);
                b.append(this.kind);
                b.append('\n');
                break;
            }
            case REGEXP_AUTOMATON: {
                b.append(indent);
                b.append(this.kind);
                b.append('\n');
                break;
            }
            case REGEXP_INTERVAL: {
                b.append(indent);
                b.append(this.kind);
                final String s1 = Integer.toString(this.min);
                final String s2 = Integer.toString(this.max);
                b.append("<");
                if (this.digits > 0) {
                    for (int i = s1.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s1).append("-");
                if (this.digits > 0) {
                    for (int i = s2.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s2).append(">");
                b.append('\n');
                break;
            }
        }
    }
    
    public Set<String> getIdentifiers() {
        final HashSet<String> set = new HashSet<String>();
        this.getIdentifiers(set);
        return set;
    }
    
    void getIdentifiers(final Set<String> set) {
        switch (this.kind) {
            case REGEXP_UNION:
            case REGEXP_CONCATENATION:
            case REGEXP_INTERSECTION: {
                this.exp1.getIdentifiers(set);
                this.exp2.getIdentifiers(set);
                break;
            }
            case REGEXP_OPTIONAL:
            case REGEXP_REPEAT:
            case REGEXP_REPEAT_MIN:
            case REGEXP_REPEAT_MINMAX:
            case REGEXP_COMPLEMENT: {
                this.exp1.getIdentifiers(set);
                break;
            }
            case REGEXP_AUTOMATON: {
                set.add(this.s);
                break;
            }
        }
    }
    
    static RegExp makeUnion(final RegExp exp1, final RegExp exp2) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_UNION;
        r.exp1 = exp1;
        r.exp2 = exp2;
        return r;
    }
    
    static RegExp makeConcatenation(final RegExp exp1, final RegExp exp2) {
        if ((exp1.kind == Kind.REGEXP_CHAR || exp1.kind == Kind.REGEXP_STRING) && (exp2.kind == Kind.REGEXP_CHAR || exp2.kind == Kind.REGEXP_STRING)) {
            return makeString(exp1, exp2);
        }
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CONCATENATION;
        if (exp1.kind == Kind.REGEXP_CONCATENATION && (exp1.exp2.kind == Kind.REGEXP_CHAR || exp1.exp2.kind == Kind.REGEXP_STRING) && (exp2.kind == Kind.REGEXP_CHAR || exp2.kind == Kind.REGEXP_STRING)) {
            r.exp1 = exp1.exp1;
            r.exp2 = makeString(exp1.exp2, exp2);
        }
        else if ((exp1.kind == Kind.REGEXP_CHAR || exp1.kind == Kind.REGEXP_STRING) && exp2.kind == Kind.REGEXP_CONCATENATION && (exp2.exp1.kind == Kind.REGEXP_CHAR || exp2.exp1.kind == Kind.REGEXP_STRING)) {
            r.exp1 = makeString(exp1, exp2.exp1);
            r.exp2 = exp2.exp2;
        }
        else {
            r.exp1 = exp1;
            r.exp2 = exp2;
        }
        return r;
    }
    
    private static RegExp makeString(final RegExp exp1, final RegExp exp2) {
        final StringBuilder b = new StringBuilder();
        if (exp1.kind == Kind.REGEXP_STRING) {
            b.append(exp1.s);
        }
        else {
            b.appendCodePoint(exp1.c);
        }
        if (exp2.kind == Kind.REGEXP_STRING) {
            b.append(exp2.s);
        }
        else {
            b.appendCodePoint(exp2.c);
        }
        return makeString(b.toString());
    }
    
    static RegExp makeIntersection(final RegExp exp1, final RegExp exp2) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_INTERSECTION;
        r.exp1 = exp1;
        r.exp2 = exp2;
        return r;
    }
    
    static RegExp makeOptional(final RegExp exp) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_OPTIONAL;
        r.exp1 = exp;
        return r;
    }
    
    static RegExp makeRepeat(final RegExp exp) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT;
        r.exp1 = exp;
        return r;
    }
    
    static RegExp makeRepeat(final RegExp exp, final int min) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT_MIN;
        r.exp1 = exp;
        r.min = min;
        return r;
    }
    
    static RegExp makeRepeat(final RegExp exp, final int min, final int max) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT_MINMAX;
        r.exp1 = exp;
        r.min = min;
        r.max = max;
        return r;
    }
    
    static RegExp makeComplement(final RegExp exp) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_COMPLEMENT;
        r.exp1 = exp;
        return r;
    }
    
    static RegExp makeChar(final int c) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CHAR;
        r.c = c;
        return r;
    }
    
    static RegExp makeCharRange(final int from, final int to) {
        if (from > to) {
            throw new IllegalArgumentException("invalid range: from (" + from + ") cannot be > to (" + to + ")");
        }
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CHAR_RANGE;
        r.from = from;
        r.to = to;
        return r;
    }
    
    static RegExp makeAnyChar() {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_ANYCHAR;
        return r;
    }
    
    static RegExp makeEmpty() {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_EMPTY;
        return r;
    }
    
    static RegExp makeString(final String s) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_STRING;
        r.s = s;
        return r;
    }
    
    static RegExp makeAnyString() {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_ANYSTRING;
        return r;
    }
    
    static RegExp makeAutomaton(final String s) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_AUTOMATON;
        r.s = s;
        return r;
    }
    
    static RegExp makeInterval(final int min, final int max, final int digits) {
        final RegExp r = new RegExp();
        r.kind = Kind.REGEXP_INTERVAL;
        r.min = min;
        r.max = max;
        r.digits = digits;
        return r;
    }
    
    private boolean peek(final String s) {
        return this.more() && s.indexOf(this.originalString.codePointAt(this.pos)) != -1;
    }
    
    private boolean match(final int c) {
        if (this.pos >= this.originalString.length()) {
            return false;
        }
        if (this.originalString.codePointAt(this.pos) == c) {
            this.pos += Character.charCount(c);
            return true;
        }
        return false;
    }
    
    private boolean more() {
        return this.pos < this.originalString.length();
    }
    
    private int next() throws IllegalArgumentException {
        if (!this.more()) {
            throw new IllegalArgumentException("unexpected end-of-string");
        }
        final int ch = this.originalString.codePointAt(this.pos);
        this.pos += Character.charCount(ch);
        return ch;
    }
    
    private boolean check(final int flag) {
        return (this.flags & flag) != 0x0;
    }
    
    final RegExp parseUnionExp() throws IllegalArgumentException {
        RegExp e = this.parseInterExp();
        if (this.match(124)) {
            e = makeUnion(e, this.parseUnionExp());
        }
        return e;
    }
    
    final RegExp parseInterExp() throws IllegalArgumentException {
        RegExp e = this.parseConcatExp();
        if (this.check(1) && this.match(38)) {
            e = makeIntersection(e, this.parseInterExp());
        }
        return e;
    }
    
    final RegExp parseConcatExp() throws IllegalArgumentException {
        RegExp e = this.parseRepeatExp();
        if (this.more() && !this.peek(")|") && (!this.check(1) || !this.peek("&"))) {
            e = makeConcatenation(e, this.parseConcatExp());
        }
        return e;
    }
    
    final RegExp parseRepeatExp() throws IllegalArgumentException {
        RegExp e = this.parseComplExp();
        while (this.peek("?*+{")) {
            if (this.match(63)) {
                e = makeOptional(e);
            }
            else if (this.match(42)) {
                e = makeRepeat(e);
            }
            else if (this.match(43)) {
                e = makeRepeat(e, 1);
            }
            else {
                if (!this.match(123)) {
                    continue;
                }
                int start = this.pos;
                while (this.peek("0123456789")) {
                    this.next();
                }
                if (start == this.pos) {
                    throw new IllegalArgumentException("integer expected at position " + this.pos);
                }
                final int n = Integer.parseInt(this.originalString.substring(start, this.pos));
                int m = -1;
                if (this.match(44)) {
                    start = this.pos;
                    while (this.peek("0123456789")) {
                        this.next();
                    }
                    if (start != this.pos) {
                        m = Integer.parseInt(this.originalString.substring(start, this.pos));
                    }
                }
                else {
                    m = n;
                }
                if (!this.match(125)) {
                    throw new IllegalArgumentException("expected '}' at position " + this.pos);
                }
                if (m == -1) {
                    e = makeRepeat(e, n);
                }
                else {
                    e = makeRepeat(e, n, m);
                }
            }
        }
        return e;
    }
    
    final RegExp parseComplExp() throws IllegalArgumentException {
        if (this.check(2) && this.match(126)) {
            return makeComplement(this.parseComplExp());
        }
        return this.parseCharClassExp();
    }
    
    final RegExp parseCharClassExp() throws IllegalArgumentException {
        if (!this.match(91)) {
            return this.parseSimpleExp();
        }
        boolean negate = false;
        if (this.match(94)) {
            negate = true;
        }
        RegExp e = this.parseCharClasses();
        if (negate) {
            e = makeIntersection(makeAnyChar(), makeComplement(e));
        }
        if (!this.match(93)) {
            throw new IllegalArgumentException("expected ']' at position " + this.pos);
        }
        return e;
    }
    
    final RegExp parseCharClasses() throws IllegalArgumentException {
        RegExp e = this.parseCharClass();
        while (this.more() && !this.peek("]")) {
            e = makeUnion(e, this.parseCharClass());
        }
        return e;
    }
    
    final RegExp parseCharClass() throws IllegalArgumentException {
        final int c = this.parseCharExp();
        if (this.match(45)) {
            return makeCharRange(c, this.parseCharExp());
        }
        return makeChar(c);
    }
    
    final RegExp parseSimpleExp() throws IllegalArgumentException {
        if (this.match(46)) {
            return makeAnyChar();
        }
        if (this.check(4) && this.match(35)) {
            return makeEmpty();
        }
        if (this.check(8) && this.match(64)) {
            return makeAnyString();
        }
        if (this.match(34)) {
            final int start = this.pos;
            while (this.more() && !this.peek("\"")) {
                this.next();
            }
            if (!this.match(34)) {
                throw new IllegalArgumentException("expected '\"' at position " + this.pos);
            }
            return makeString(this.originalString.substring(start, this.pos - 1));
        }
        else {
            if (!this.match(40)) {
                if ((this.check(16) || this.check(32)) && this.match(60)) {
                    final int start = this.pos;
                    while (this.more() && !this.peek(">")) {
                        this.next();
                    }
                    if (!this.match(62)) {
                        throw new IllegalArgumentException("expected '>' at position " + this.pos);
                    }
                    final String s = this.originalString.substring(start, this.pos - 1);
                    final int i = s.indexOf(45);
                    if (i == -1) {
                        if (!this.check(16)) {
                            throw new IllegalArgumentException("interval syntax error at position " + (this.pos - 1));
                        }
                        return makeAutomaton(s);
                    }
                    else {
                        if (!this.check(32)) {
                            throw new IllegalArgumentException("illegal identifier at position " + (this.pos - 1));
                        }
                        try {
                            if (i == 0 || i == s.length() - 1 || i != s.lastIndexOf(45)) {
                                throw new NumberFormatException();
                            }
                            final String smin = s.substring(0, i);
                            final String smax = s.substring(i + 1, s.length());
                            int imin = Integer.parseInt(smin);
                            int imax = Integer.parseInt(smax);
                            int digits;
                            if (smin.length() == smax.length()) {
                                digits = smin.length();
                            }
                            else {
                                digits = 0;
                            }
                            if (imin > imax) {
                                final int t = imin;
                                imin = imax;
                                imax = t;
                            }
                            return makeInterval(imin, imax, digits);
                        }
                        catch (final NumberFormatException e) {
                            throw new IllegalArgumentException("interval syntax error at position " + (this.pos - 1));
                        }
                    }
                }
                return makeChar(this.parseCharExp());
            }
            if (this.match(41)) {
                return makeString("");
            }
            final RegExp e2 = this.parseUnionExp();
            if (!this.match(41)) {
                throw new IllegalArgumentException("expected ')' at position " + this.pos);
            }
            return e2;
        }
    }
    
    final int parseCharExp() throws IllegalArgumentException {
        this.match(92);
        return this.next();
    }
    
    enum Kind
    {
        REGEXP_UNION, 
        REGEXP_CONCATENATION, 
        REGEXP_INTERSECTION, 
        REGEXP_OPTIONAL, 
        REGEXP_REPEAT, 
        REGEXP_REPEAT_MIN, 
        REGEXP_REPEAT_MINMAX, 
        REGEXP_COMPLEMENT, 
        REGEXP_CHAR, 
        REGEXP_CHAR_RANGE, 
        REGEXP_ANYCHAR, 
        REGEXP_EMPTY, 
        REGEXP_STRING, 
        REGEXP_ANYSTRING, 
        REGEXP_AUTOMATON, 
        REGEXP_INTERVAL;
    }
}
