package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.util.Locale;
import java.text.CharacterIterator;
import com.sun.org.apache.xerces.internal.util.IntStack;
import java.util.Stack;
import java.io.Serializable;

public class RegularExpression implements Serializable
{
    private static final long serialVersionUID = 6242499334195006401L;
    static final boolean DEBUG = false;
    String regex;
    int options;
    int nofparen;
    Token tokentree;
    boolean hasBackReferences;
    transient int minlength;
    transient Op operations;
    transient int numberOfClosures;
    transient Context context;
    transient RangeToken firstChar;
    transient String fixedString;
    transient int fixedStringOptions;
    transient BMPattern fixedStringTable;
    transient boolean fixedStringOnly;
    static final int IGNORE_CASE = 2;
    static final int SINGLE_LINE = 4;
    static final int MULTIPLE_LINES = 8;
    static final int EXTENDED_COMMENT = 16;
    static final int USE_UNICODE_CATEGORY = 32;
    static final int UNICODE_WORD_BOUNDARY = 64;
    static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 128;
    static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 256;
    static final int XMLSCHEMA_MODE = 512;
    static final int SPECIAL_COMMA = 1024;
    private static final int WT_IGNORE = 0;
    private static final int WT_LETTER = 1;
    private static final int WT_OTHER = 2;
    static final int LINE_FEED = 10;
    static final int CARRIAGE_RETURN = 13;
    static final int LINE_SEPARATOR = 8232;
    static final int PARAGRAPH_SEPARATOR = 8233;
    
    private synchronized void compile(final Token tok) {
        if (this.operations != null) {
            return;
        }
        this.numberOfClosures = 0;
        this.operations = this.compile(tok, null, false);
    }
    
    private Op compile(final Token tok, Op next, final boolean reverse) {
        Op ret = null;
        switch (tok.type) {
            case 11: {
                ret = Op.createDot();
                ret.next = next;
                break;
            }
            case 0: {
                ret = Op.createChar(tok.getChar());
                ret.next = next;
                break;
            }
            case 8: {
                ret = Op.createAnchor(tok.getChar());
                ret.next = next;
                break;
            }
            case 4:
            case 5: {
                ret = Op.createRange(tok);
                ret.next = next;
                break;
            }
            case 1: {
                ret = next;
                if (!reverse) {
                    for (int i = tok.size() - 1; i >= 0; --i) {
                        ret = this.compile(tok.getChild(i), ret, false);
                    }
                    break;
                }
                for (int i = 0; i < tok.size(); ++i) {
                    ret = this.compile(tok.getChild(i), ret, true);
                }
                break;
            }
            case 2: {
                final Op.UnionOp uni = Op.createUnion(tok.size());
                for (int j = 0; j < tok.size(); ++j) {
                    uni.addElement(this.compile(tok.getChild(j), next, reverse));
                }
                ret = uni;
                break;
            }
            case 3:
            case 9: {
                final Token child = tok.getChild(0);
                final int min = tok.getMin();
                int max = tok.getMax();
                if (min >= 0 && min == max) {
                    ret = next;
                    for (int k = 0; k < min; ++k) {
                        ret = this.compile(child, ret, reverse);
                    }
                    break;
                }
                if (min > 0 && max > 0) {
                    max -= min;
                }
                if (max > 0) {
                    ret = next;
                    for (int k = 0; k < max; ++k) {
                        final Op.ChildOp q = Op.createQuestion(tok.type == 9);
                        q.next = next;
                        q.setChild(this.compile(child, ret, reverse));
                        ret = q;
                    }
                }
                else {
                    Op.ChildOp op;
                    if (tok.type == 9) {
                        op = Op.createNonGreedyClosure();
                    }
                    else {
                        op = Op.createClosure(this.numberOfClosures++);
                    }
                    op.next = next;
                    op.setChild(this.compile(child, op, reverse));
                    ret = op;
                }
                if (min > 0) {
                    for (int k = 0; k < min; ++k) {
                        ret = this.compile(child, ret, reverse);
                    }
                    break;
                }
                break;
            }
            case 7: {
                ret = next;
                break;
            }
            case 10: {
                ret = Op.createString(tok.getString());
                ret.next = next;
                break;
            }
            case 12: {
                ret = Op.createBackReference(tok.getReferenceNumber());
                ret.next = next;
                break;
            }
            case 6: {
                if (tok.getParenNumber() == 0) {
                    ret = this.compile(tok.getChild(0), next, reverse);
                    break;
                }
                if (reverse) {
                    next = Op.createCapture(tok.getParenNumber(), next);
                    next = this.compile(tok.getChild(0), next, reverse);
                    ret = Op.createCapture(-tok.getParenNumber(), next);
                    break;
                }
                next = Op.createCapture(-tok.getParenNumber(), next);
                next = this.compile(tok.getChild(0), next, reverse);
                ret = Op.createCapture(tok.getParenNumber(), next);
                break;
            }
            case 20: {
                ret = Op.createLook(20, next, this.compile(tok.getChild(0), null, false));
                break;
            }
            case 21: {
                ret = Op.createLook(21, next, this.compile(tok.getChild(0), null, false));
                break;
            }
            case 22: {
                ret = Op.createLook(22, next, this.compile(tok.getChild(0), null, true));
                break;
            }
            case 23: {
                ret = Op.createLook(23, next, this.compile(tok.getChild(0), null, true));
                break;
            }
            case 24: {
                ret = Op.createIndependent(next, this.compile(tok.getChild(0), null, reverse));
                break;
            }
            case 25: {
                ret = Op.createModifier(next, this.compile(tok.getChild(0), null, reverse), ((Token.ModifierToken)tok).getOptions(), ((Token.ModifierToken)tok).getOptionsMask());
                break;
            }
            case 26: {
                final Token.ConditionToken ctok = (Token.ConditionToken)tok;
                final int ref = ctok.refNumber;
                final Op condition = (ctok.condition == null) ? null : this.compile(ctok.condition, null, reverse);
                final Op yes = this.compile(ctok.yes, next, reverse);
                final Op no = (ctok.no == null) ? null : this.compile(ctok.no, next, reverse);
                ret = Op.createCondition(next, ref, condition, yes, no);
                break;
            }
            default: {
                throw new RuntimeException("Unknown token type: " + tok.type);
            }
        }
        return ret;
    }
    
    public boolean matches(final char[] target) {
        return this.matches(target, 0, target.length, null);
    }
    
    public boolean matches(final char[] target, final int start, final int end) {
        return this.matches(target, start, end, null);
    }
    
    public boolean matches(final char[] target, final Match match) {
        return this.matches(target, 0, target.length, match);
    }
    
    public boolean matches(final char[] target, final int start, final int end, Match match) {
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        synchronized (this.context) {
            con = (this.context.inuse ? new Context() : this.context);
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            final int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    con.setInUse(false);
                    return false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.match(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target[matchStart];
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken range = this.firstChar;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    int ch = target[matchStart];
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
                    }
                    if (range.match(ch)) {
                        if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
    }
    
    public boolean matches(final String target) {
        return this.matches(target, 0, target.length(), null);
    }
    
    public boolean matches(final String target, final int start, final int end) {
        return this.matches(target, start, end, null);
    }
    
    public boolean matches(final String target, final Match match) {
        return this.matches(target, 0, target.length(), match);
    }
    
    public boolean matches(final String target, final int start, final int end, Match match) {
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        synchronized (this.context) {
            con = (this.context.inuse ? new Context() : this.context);
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            final int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    con.setInUse(false);
                    return false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.match(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target.charAt(matchStart);
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken range = this.firstChar;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    int ch = target.charAt(matchStart);
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1));
                    }
                    if (range.match(ch)) {
                        if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
    }
    
    private int match(final Context con, Op op, int offset, int dx, int opts) {
        final ExpressionTarget target = con.target;
        final Stack opStack = new Stack();
        final IntStack dataStack = new IntStack();
        final boolean isSetIgnoreCase = isSet(opts, 2);
        int retValue = -1;
        boolean returned = false;
        while (true) {
            if (op == null || offset > con.limit || offset < con.start) {
                if (op == null) {
                    retValue = ((isSet(opts, 512) && offset != con.limit) ? -1 : offset);
                }
                else {
                    retValue = -1;
                }
                returned = true;
            }
            else {
                retValue = -1;
                switch (op.type) {
                    case 1: {
                        final int o1 = (dx > 0) ? offset : (offset - 1);
                        if (o1 >= con.limit || o1 < 0 || !this.matchChar(op.getData(), target.charAt(o1), isSetIgnoreCase)) {
                            returned = true;
                            break;
                        }
                        offset += dx;
                        op = op.next;
                        break;
                    }
                    case 0: {
                        int o1 = (dx > 0) ? offset : (offset - 1);
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(target.charAt(o1)) && o1 + dx >= 0 && o1 + dx < con.limit) {
                                o1 += dx;
                            }
                        }
                        else {
                            int ch = target.charAt(o1);
                            if (REUtil.isHighSurrogate(ch) && o1 + dx >= 0 && o1 + dx < con.limit) {
                                o1 += dx;
                                ch = REUtil.composeFromSurrogates(ch, target.charAt(o1));
                            }
                            if (isEOLChar(ch)) {
                                returned = true;
                                break;
                            }
                        }
                        offset = ((dx > 0) ? (o1 + 1) : o1);
                        op = op.next;
                        break;
                    }
                    case 3:
                    case 4: {
                        int o1 = (dx > 0) ? offset : (offset - 1);
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        int ch = target.charAt(offset);
                        if (REUtil.isHighSurrogate(ch) && o1 + dx < con.limit && o1 + dx >= 0) {
                            o1 += dx;
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(o1));
                        }
                        final RangeToken tok = op.getToken();
                        if (!tok.match(ch)) {
                            returned = true;
                            break;
                        }
                        offset = ((dx > 0) ? (o1 + 1) : o1);
                        op = op.next;
                        break;
                    }
                    case 5: {
                        if (!this.matchAnchor(target, op, con, offset, opts)) {
                            returned = true;
                            break;
                        }
                        op = op.next;
                        break;
                    }
                    case 16: {
                        final int refno = op.getData();
                        if (refno <= 0 || refno >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
                        }
                        if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                            returned = true;
                            break;
                        }
                        final int o2 = con.match.getBeginning(refno);
                        final int literallen = con.match.getEnd(refno) - o2;
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!target.regionMatches(isSetIgnoreCase, offset - literallen, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen;
                        }
                        op = op.next;
                        break;
                    }
                    case 6: {
                        final String literal = op.getString();
                        final int literallen2 = literal.length();
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, literal, literallen2)) {
                                returned = true;
                                break;
                            }
                            offset += literallen2;
                        }
                        else {
                            if (!target.regionMatches(isSetIgnoreCase, offset - literallen2, con.limit, literal, literallen2)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen2;
                        }
                        op = op.next;
                        break;
                    }
                    case 7: {
                        final int id = op.getData();
                        if (con.closureContexts[id].contains(offset)) {
                            returned = true;
                            break;
                        }
                        con.closureContexts[id].addOffset(offset);
                    }
                    case 9: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                        break;
                    }
                    case 8:
                    case 10: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.next;
                        break;
                    }
                    case 11: {
                        if (op.size() == 0) {
                            returned = true;
                            break;
                        }
                        opStack.push(op);
                        dataStack.push(0);
                        dataStack.push(offset);
                        op = op.elementAt(0);
                        break;
                    }
                    case 15: {
                        final int refno = op.getData();
                        if (con.match != null) {
                            if (refno > 0) {
                                dataStack.push(con.match.getBeginning(refno));
                                con.match.setBeginning(refno, offset);
                            }
                            else {
                                final int index = -refno;
                                dataStack.push(con.match.getEnd(index));
                                con.match.setEnd(index, offset);
                            }
                            opStack.push(op);
                            dataStack.push(offset);
                        }
                        op = op.next;
                        break;
                    }
                    case 20:
                    case 21:
                    case 22:
                    case 23: {
                        opStack.push(op);
                        dataStack.push(dx);
                        dataStack.push(offset);
                        dx = ((op.type == 20 || op.type == 21) ? 1 : -1);
                        op = op.getChild();
                        break;
                    }
                    case 24: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                        break;
                    }
                    case 25: {
                        int localopts = opts;
                        localopts |= op.getData();
                        localopts &= ~op.getData2();
                        opStack.push(op);
                        dataStack.push(opts);
                        dataStack.push(offset);
                        opts = localopts;
                        op = op.getChild();
                        break;
                    }
                    case 26: {
                        final Op.ConditionOp cop = (Op.ConditionOp)op;
                        if (cop.refNumber > 0) {
                            if (cop.refNumber >= this.nofparen) {
                                throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                            }
                            if (con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0) {
                                op = cop.yes;
                            }
                            else if (cop.no != null) {
                                op = cop.no;
                            }
                            else {
                                op = cop.next;
                            }
                        }
                        else {
                            opStack.push(op);
                            dataStack.push(offset);
                            op = cop.condition;
                        }
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown operation type: " + op.type);
                    }
                }
            }
            while (returned) {
                if (opStack.isEmpty()) {
                    return retValue;
                }
                op = opStack.pop();
                offset = dataStack.pop();
                switch (op.type) {
                    case 7:
                    case 9: {
                        if (retValue < 0) {
                            op = op.next;
                            returned = false;
                            continue;
                        }
                        continue;
                    }
                    case 8:
                    case 10: {
                        if (retValue < 0) {
                            op = op.getChild();
                            returned = false;
                            continue;
                        }
                        continue;
                    }
                    case 11: {
                        int unionIndex = dataStack.pop();
                        if (retValue >= 0) {
                            continue;
                        }
                        if (++unionIndex < op.size()) {
                            opStack.push(op);
                            dataStack.push(unionIndex);
                            dataStack.push(offset);
                            op = op.elementAt(unionIndex);
                            returned = false;
                        }
                        else {
                            retValue = -1;
                        }
                        continue;
                    }
                    case 15: {
                        final int refno = op.getData();
                        final int saved = dataStack.pop();
                        if (retValue >= 0) {
                            continue;
                        }
                        if (refno > 0) {
                            con.match.setBeginning(refno, saved);
                            continue;
                        }
                        con.match.setEnd(-refno, saved);
                        continue;
                    }
                    case 20:
                    case 22: {
                        dx = dataStack.pop();
                        if (0 <= retValue) {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                        continue;
                    }
                    case 21:
                    case 23: {
                        dx = dataStack.pop();
                        if (0 > retValue) {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                        continue;
                    }
                    case 25: {
                        opts = dataStack.pop();
                    }
                    case 24: {
                        if (retValue >= 0) {
                            offset = retValue;
                            op = op.next;
                            returned = false;
                            continue;
                        }
                        continue;
                    }
                    case 26: {
                        final Op.ConditionOp cop2 = (Op.ConditionOp)op;
                        if (0 <= retValue) {
                            op = cop2.yes;
                        }
                        else if (cop2.no != null) {
                            op = cop2.no;
                        }
                        else {
                            op = cop2.next;
                        }
                        returned = false;
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
    
    private boolean matchChar(final int ch, final int other, final boolean ignoreCase) {
        return ignoreCase ? matchIgnoreCase(ch, other) : (ch == other);
    }
    
    boolean matchAnchor(final ExpressionTarget target, final Op op, final Context con, final int offset, final int opts) {
        boolean go = false;
        switch (op.getData()) {
            case 94: {
                if (isSet(opts, 8)) {
                    if (offset != con.start && (offset <= con.start || offset >= con.limit || !isEOLChar(target.charAt(offset - 1)))) {
                        return false;
                    }
                    break;
                }
                else {
                    if (offset != con.start) {
                        return false;
                    }
                    break;
                }
                break;
            }
            case 64: {
                if (offset != con.start && (offset <= con.start || !isEOLChar(target.charAt(offset - 1)))) {
                    return false;
                }
                break;
            }
            case 36: {
                if (isSet(opts, 8)) {
                    if (offset != con.limit && (offset >= con.limit || !isEOLChar(target.charAt(offset)))) {
                        return false;
                    }
                    break;
                }
                else {
                    if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n')) {
                        return false;
                    }
                    break;
                }
                break;
            }
            case 65: {
                if (offset != con.start) {
                    return false;
                }
                break;
            }
            case 90: {
                if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n')) {
                    return false;
                }
                break;
            }
            case 122: {
                if (offset != con.limit) {
                    return false;
                }
                break;
            }
            case 98: {
                if (con.length == 0) {
                    return false;
                }
                final int after = getWordType(target, con.start, con.limit, offset, opts);
                if (after == 0) {
                    return false;
                }
                final int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                if (after == before) {
                    return false;
                }
                break;
            }
            case 66: {
                if (con.length == 0) {
                    go = true;
                }
                else {
                    final int after = getWordType(target, con.start, con.limit, offset, opts);
                    go = (after == 0 || after == getPreviousWordType(target, con.start, con.limit, offset, opts));
                }
                if (!go) {
                    return false;
                }
                break;
            }
            case 60: {
                if (con.length == 0 || offset == con.limit) {
                    return false;
                }
                if (getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                    return false;
                }
                break;
            }
            case 62: {
                if (con.length == 0 || offset == con.start) {
                    return false;
                }
                if (getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    private static final int getPreviousWordType(final ExpressionTarget target, final int begin, final int end, int offset, final int opts) {
        int ret;
        for (ret = getWordType(target, begin, end, --offset, opts); ret == 0; ret = getWordType(target, begin, end, --offset, opts)) {}
        return ret;
    }
    
    private static final int getWordType(final ExpressionTarget target, final int begin, final int end, final int offset, final int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target.charAt(offset), opts);
    }
    
    public boolean matches(final CharacterIterator target) {
        return this.matches(target, null);
    }
    
    public boolean matches(final CharacterIterator target, Match match) {
        final int start = target.getBeginIndex();
        final int end = target.getEndIndex();
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        synchronized (this.context) {
            con = (this.context.inuse ? new Context() : this.context);
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            final int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    con.setInUse(false);
                    return false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.match(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target.setIndex(matchStart);
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken range = this.firstChar;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    int ch = target.setIndex(matchStart);
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1));
                    }
                    if (range.match(ch)) {
                        if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
    }
    
    void prepare() {
        this.compile(this.tokentree);
        this.minlength = this.tokentree.getMinLength();
        this.firstChar = null;
        if (!isSet(this.options, 128) && !isSet(this.options, 512)) {
            final RangeToken firstChar = Token.createRange();
            final int fresult = this.tokentree.analyzeFirstCharacter(firstChar, this.options);
            if (fresult == 1) {
                firstChar.compactRanges();
                this.firstChar = firstChar;
            }
        }
        if (this.operations != null && (this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null) {
            this.fixedStringOnly = true;
            if (this.operations.type == 6) {
                this.fixedString = this.operations.getString();
            }
            else if (this.operations.getData() >= 65536) {
                this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
            }
            else {
                final char[] ac = { (char)this.operations.getData() };
                this.fixedString = new String(ac);
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
        }
        else if (!isSet(this.options, 256) && !isSet(this.options, 512)) {
            final Token.FixedStringContainer container = new Token.FixedStringContainer();
            this.tokentree.findFixedString(container, this.options);
            this.fixedString = ((container.token == null) ? null : container.token.getString());
            this.fixedStringOptions = container.options;
            if (this.fixedString != null && this.fixedString.length() < 2) {
                this.fixedString = null;
            }
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
            }
        }
    }
    
    private static final boolean isSet(final int options, final int flag) {
        return (options & flag) == flag;
    }
    
    public RegularExpression(final String regex) throws ParseException {
        this(regex, null);
    }
    
    public RegularExpression(final String regex, final String options) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(regex, options);
    }
    
    public RegularExpression(final String regex, final String options, final Locale locale) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(regex, options, locale);
    }
    
    RegularExpression(final String regex, final Token tok, final int parens, final boolean hasBackReferences, final int options) {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.regex = regex;
        this.tokentree = tok;
        this.nofparen = parens;
        this.options = options;
        this.hasBackReferences = hasBackReferences;
    }
    
    public void setPattern(final String newPattern) throws ParseException {
        this.setPattern(newPattern, Locale.getDefault());
    }
    
    public void setPattern(final String newPattern, final Locale locale) throws ParseException {
        this.setPattern(newPattern, this.options, locale);
    }
    
    private void setPattern(final String newPattern, final int options, final Locale locale) throws ParseException {
        this.regex = newPattern;
        this.options = options;
        final RegexParser rp = isSet(this.options, 512) ? new ParserForXMLSchema(locale) : new RegexParser(locale);
        this.tokentree = rp.parse(this.regex, this.options);
        this.nofparen = rp.parennumber;
        this.hasBackReferences = rp.hasBackReferences;
        this.operations = null;
        this.context = null;
    }
    
    public void setPattern(final String newPattern, final String options) throws ParseException {
        this.setPattern(newPattern, options, Locale.getDefault());
    }
    
    public void setPattern(final String newPattern, final String options, final Locale locale) throws ParseException {
        this.setPattern(newPattern, REUtil.parseOptions(options), locale);
    }
    
    public String getPattern() {
        return this.regex;
    }
    
    @Override
    public String toString() {
        return this.tokentree.toString(this.options);
    }
    
    public String getOptions() {
        return REUtil.createOptionString(this.options);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RegularExpression)) {
            return false;
        }
        final RegularExpression r = (RegularExpression)obj;
        return this.regex.equals(r.regex) && this.options == r.options;
    }
    
    boolean equals(final String pattern, final int options) {
        return this.regex.equals(pattern) && this.options == options;
    }
    
    @Override
    public int hashCode() {
        return (this.regex + "/" + this.getOptions()).hashCode();
    }
    
    public int getNumberOfGroups() {
        return this.nofparen;
    }
    
    private static final int getWordType0(final char ch, final int opts) {
        if (!isSet(opts, 64)) {
            if (isSet(opts, 32)) {
                return Token.getRange("IsWord", true).match(ch) ? 1 : 2;
            }
            return isWordChar(ch) ? 1 : 2;
        }
        else {
            switch (Character.getType(ch)) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 8:
                case 9:
                case 10:
                case 11: {
                    return 1;
                }
                case 6:
                case 7:
                case 16: {
                    return 0;
                }
                case 15: {
                    switch (ch) {
                        case '\t':
                        case '\n':
                        case '\u000b':
                        case '\f':
                        case '\r': {
                            return 2;
                        }
                        default: {
                            return 0;
                        }
                    }
                    break;
                }
                default: {
                    return 2;
                }
            }
        }
    }
    
    private static final boolean isEOLChar(final int ch) {
        return ch == 10 || ch == 13 || ch == 8232 || ch == 8233;
    }
    
    private static final boolean isWordChar(final int ch) {
        return ch == 95 || (ch >= 48 && ch <= 122 && (ch <= 57 || (ch >= 65 && (ch <= 90 || ch >= 97))));
    }
    
    private static final boolean matchIgnoreCase(final int chardata, final int ch) {
        if (chardata == ch) {
            return true;
        }
        if (chardata > 65535 || ch > 65535) {
            return false;
        }
        final char uch1 = Character.toUpperCase((char)chardata);
        final char uch2 = Character.toUpperCase((char)ch);
        return uch1 == uch2 || Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
    }
    
    abstract static class ExpressionTarget
    {
        abstract char charAt(final int p0);
        
        abstract boolean regionMatches(final boolean p0, final int p1, final int p2, final String p3, final int p4);
        
        abstract boolean regionMatches(final boolean p0, final int p1, final int p2, final int p3, final int p4);
    }
    
    static final class StringTarget extends ExpressionTarget
    {
        private String target;
        
        StringTarget(final String target) {
            this.target = target;
        }
        
        final void resetTarget(final String target) {
            this.target = target;
        }
        
        @Override
        final char charAt(final int index) {
            return this.target.charAt(index);
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final String part, final int partlen) {
            return limit - offset >= partlen && (ignoreCase ? this.target.regionMatches(true, offset, part, 0, partlen) : this.target.regionMatches(offset, part, 0, partlen));
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final int offset2, final int partlen) {
            return limit - offset >= partlen && (ignoreCase ? this.target.regionMatches(true, offset, this.target, offset2, partlen) : this.target.regionMatches(offset, this.target, offset2, partlen));
        }
    }
    
    static final class CharArrayTarget extends ExpressionTarget
    {
        char[] target;
        
        CharArrayTarget(final char[] target) {
            this.target = target;
        }
        
        final void resetTarget(final char[] target) {
            this.target = target;
        }
        
        @Override
        char charAt(final int index) {
            return this.target[index];
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final String part, final int partlen) {
            return offset >= 0 && limit - offset >= partlen && (ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen));
        }
        
        private final boolean regionMatches(int offset, final int limit, final String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (this.target[offset++] != part.charAt(i++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int offset, final int limit, final String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                final char ch1 = this.target[offset++];
                final char ch2 = part.charAt(i++);
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final int offset2, final int partlen) {
            return offset >= 0 && limit - offset >= partlen && (ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen));
        }
        
        private final boolean regionMatches(int offset, final int limit, final int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if (this.target[offset++] != this.target[i++]) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int offset, final int limit, final int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                final char ch1 = this.target[offset++];
                final char ch2 = this.target[i++];
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static final class CharacterIteratorTarget extends ExpressionTarget
    {
        CharacterIterator target;
        
        CharacterIteratorTarget(final CharacterIterator target) {
            this.target = target;
        }
        
        final void resetTarget(final CharacterIterator target) {
            this.target = target;
        }
        
        @Override
        final char charAt(final int index) {
            return this.target.setIndex(index);
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final String part, final int partlen) {
            return offset >= 0 && limit - offset >= partlen && (ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen));
        }
        
        private final boolean regionMatches(int offset, final int limit, final String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (this.target.setIndex(offset++) != part.charAt(i++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int offset, final int limit, final String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                final char ch1 = this.target.setIndex(offset++);
                final char ch2 = part.charAt(i++);
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        final boolean regionMatches(final boolean ignoreCase, final int offset, final int limit, final int offset2, final int partlen) {
            return offset >= 0 && limit - offset >= partlen && (ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen));
        }
        
        private final boolean regionMatches(int offset, final int limit, final int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if (this.target.setIndex(offset++) != this.target.setIndex(i++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int offset, final int limit, final int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                final char ch1 = this.target.setIndex(offset++);
                final char ch2 = this.target.setIndex(i++);
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static final class ClosureContext
    {
        int[] offsets;
        int currentIndex;
        
        ClosureContext() {
            this.offsets = new int[4];
            this.currentIndex = 0;
        }
        
        boolean contains(final int offset) {
            for (int i = 0; i < this.currentIndex; ++i) {
                if (this.offsets[i] == offset) {
                    return true;
                }
            }
            return false;
        }
        
        void reset() {
            this.currentIndex = 0;
        }
        
        void addOffset(final int offset) {
            if (this.currentIndex == this.offsets.length) {
                this.offsets = this.expandOffsets();
            }
            this.offsets[this.currentIndex++] = offset;
        }
        
        private int[] expandOffsets() {
            final int len = this.offsets.length;
            final int newLen = len << 1;
            final int[] newOffsets = new int[newLen];
            System.arraycopy(this.offsets, 0, newOffsets, 0, this.currentIndex);
            return newOffsets;
        }
    }
    
    static final class Context
    {
        int start;
        int limit;
        int length;
        Match match;
        boolean inuse;
        ClosureContext[] closureContexts;
        private StringTarget stringTarget;
        private CharArrayTarget charArrayTarget;
        private CharacterIteratorTarget characterIteratorTarget;
        ExpressionTarget target;
        
        Context() {
            this.inuse = false;
        }
        
        private void resetCommon(final int nofclosures) {
            this.length = this.limit - this.start;
            this.setInUse(true);
            this.match = null;
            if (this.closureContexts == null || this.closureContexts.length != nofclosures) {
                this.closureContexts = new ClosureContext[nofclosures];
            }
            for (int i = 0; i < nofclosures; ++i) {
                if (this.closureContexts[i] == null) {
                    this.closureContexts[i] = new ClosureContext();
                }
                else {
                    this.closureContexts[i].reset();
                }
            }
        }
        
        void reset(final CharacterIterator target, final int start, final int limit, final int nofclosures) {
            if (this.characterIteratorTarget == null) {
                this.characterIteratorTarget = new CharacterIteratorTarget(target);
            }
            else {
                this.characterIteratorTarget.resetTarget(target);
            }
            this.target = this.characterIteratorTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        
        void reset(final String target, final int start, final int limit, final int nofclosures) {
            if (this.stringTarget == null) {
                this.stringTarget = new StringTarget(target);
            }
            else {
                this.stringTarget.resetTarget(target);
            }
            this.target = this.stringTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        
        void reset(final char[] target, final int start, final int limit, final int nofclosures) {
            if (this.charArrayTarget == null) {
                this.charArrayTarget = new CharArrayTarget(target);
            }
            else {
                this.charArrayTarget.resetTarget(target);
            }
            this.target = this.charArrayTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        
        synchronized void setInUse(final boolean inUse) {
            this.inuse = inUse;
        }
    }
}
