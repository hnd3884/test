package org.apache.xmlbeans.impl.regex;

import java.text.CharacterIterator;
import java.io.Serializable;

public class RegularExpression implements Serializable
{
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
                    else if (child.getMinLength() == 0) {
                        op = Op.createClosure(this.numberOfClosures++);
                    }
                    else {
                        op = Op.createClosure(-1);
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
            final int matchEnd = this.matchCharArray(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
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
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    return con.inuse = false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.matchCharArray(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target[matchStart];
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.matchCharArray(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                RangeToken range = this.firstChar;
                if (isSet(this.options, 2)) {
                    range = this.firstChar.getCaseInsensitiveToken();
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target[matchStart];
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
                            if (!range.match(ch)) {
                                continue;
                            }
                        }
                        else if (!range.match(ch)) {
                            final char ch2 = Character.toUpperCase((char)ch);
                            if (!range.match(ch2) && !range.match(Character.toLowerCase(ch2))) {
                                continue;
                            }
                        }
                        if (0 <= (matchEnd2 = this.matchCharArray(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
                else {
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target[matchStart];
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
                        }
                        if (range.match(ch)) {
                            if (0 <= (matchEnd2 = this.matchCharArray(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.matchCharArray(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
    }
    
    private int matchCharArray(final Context con, Op op, int offset, final int dx, final int opts) {
        final char[] target = con.charTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 1: {
                    if (isSet(opts, 2)) {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch, target[offset])) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch, target[o1])) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    else {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch != target[offset]) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || ch != target[o1]) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 0: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target[offset];
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ++offset;
                            }
                        }
                        else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ch = REUtil.composeFromSurrogates(ch, target[++offset]);
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target[o2];
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                --o2;
                            }
                        }
                        else {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                ch2 = REUtil.composeFromSurrogates(target[--o2], ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 3:
                case 4: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target[offset];
                        if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target[++offset]);
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            tok = tok.getCaseInsensitiveToken();
                            if (!tok.match(ch)) {
                                if (ch >= 65536) {
                                    return -1;
                                }
                                final char uch;
                                if (!tok.match(uch = Character.toUpperCase((char)ch)) && !tok.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok.match(ch)) {
                            return -1;
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target[o2];
                        if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                            ch2 = REUtil.composeFromSurrogates(target[--o2], ch2);
                        }
                        RangeToken tok2 = op.getToken();
                        if (isSet(opts, 2)) {
                            tok2 = tok2.getCaseInsensitiveToken();
                            if (!tok2.match(ch2)) {
                                if (ch2 >= 65536) {
                                    return -1;
                                }
                                final char uch2;
                                if (!tok2.match(uch2 = Character.toUpperCase((char)ch2)) && !tok2.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok2.match(ch2)) {
                            return -1;
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 5: {
                    boolean go = false;
                    switch (op.getData()) {
                        case 94: {
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target[offset - 1]))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.start) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 64: {
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target[offset - 1]))) {
                                return -1;
                            }
                            break;
                        }
                        case 36: {
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target[offset]))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target[offset])) && (offset + 2 != con.limit || target[offset] != '\r' || target[offset + 1] != '\n')) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 65: {
                            if (offset != con.start) {
                                return -1;
                            }
                            break;
                        }
                        case 90: {
                            if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target[offset])) && (offset + 2 != con.limit || target[offset] != '\r' || target[offset + 1] != '\n')) {
                                return -1;
                            }
                            break;
                        }
                        case 122: {
                            if (offset != con.limit) {
                                return -1;
                            }
                            break;
                        }
                        case 98: {
                            if (con.length == 0) {
                                return -1;
                            }
                            final int after = getWordType(target, con.start, con.limit, offset, opts);
                            if (after == 0) {
                                return -1;
                            }
                            final int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
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
                                return -1;
                            }
                            break;
                        }
                        case 60: {
                            if (con.length == 0 || offset == con.limit) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            break;
                        }
                        case 62: {
                            if (con.length == 0 || offset == con.start) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            break;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 16: {
                    final int refno = op.getData();
                    if (refno <= 0 || refno >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
                    }
                    if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                        return -1;
                    }
                    final int o3 = con.match.getBeginning(refno);
                    final int literallen = con.match.getEnd(refno) - o3;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset -= literallen;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset += literallen;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset -= literallen;
                    }
                    op = op.next;
                    continue;
                }
                case 6: {
                    final String literal = op.getString();
                    final int literallen2 = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset += literallen2;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen2, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset -= literallen2;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset += literallen2;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset -= literallen2;
                    }
                    op = op.next;
                    continue;
                }
                case 7: {
                    final int id = op.getData();
                    if (id >= 0) {
                        final int previousOffset = con.offsets[id];
                        if (previousOffset >= 0 && previousOffset == offset) {
                            con.offsets[id] = -1;
                            op = op.next;
                            continue;
                        }
                        con.offsets[id] = offset;
                    }
                    final int ret = this.matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    continue;
                }
                case 9: {
                    final int ret2 = this.matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.next;
                    continue;
                }
                case 8:
                case 10: {
                    final int ret2 = this.matchCharArray(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    continue;
                }
                case 11: {
                    for (int i = 0; i < op.size(); ++i) {
                        final int ret = this.matchCharArray(con, op.elementAt(i), offset, dx, opts);
                        if (ret >= 0) {
                            return ret;
                        }
                    }
                    return -1;
                }
                case 15: {
                    final int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        final int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        final int ret3 = this.matchCharArray(con, op.next, offset, dx, opts);
                        if (ret3 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret3;
                    }
                    if (con.match != null && refno < 0) {
                        final int index = -refno;
                        final int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        final int ret4 = this.matchCharArray(con, op.next, offset, dx, opts);
                        if (ret4 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret4;
                    }
                    op = op.next;
                    continue;
                }
                case 20: {
                    if (0 > this.matchCharArray(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 21: {
                    if (0 <= this.matchCharArray(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 22: {
                    if (0 > this.matchCharArray(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 23: {
                    if (0 <= this.matchCharArray(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 24: {
                    final int ret = this.matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (ret < 0) {
                        return ret;
                    }
                    offset = ret;
                    op = op.next;
                    continue;
                }
                case 25: {
                    int localopts = opts;
                    localopts |= op.getData();
                    localopts &= ~op.getData2();
                    final int ret3 = this.matchCharArray(con, op.getChild(), offset, dx, localopts);
                    if (ret3 < 0) {
                        return ret3;
                    }
                    offset = ret3;
                    op = op.next;
                    continue;
                }
                case 26: {
                    final Op.ConditionOp cop = (Op.ConditionOp)op;
                    boolean matchp = false;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = (con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0);
                    }
                    else {
                        matchp = (0 <= this.matchCharArray(con, cop.condition, offset, dx, opts));
                    }
                    if (matchp) {
                        op = cop.yes;
                    }
                    else if (cop.no != null) {
                        op = cop.no;
                    }
                    else {
                        op = cop.next;
                    }
                    continue;
                }
                default: {
                    throw new RuntimeException("Unknown operation type: " + op.type);
                }
            }
        }
        return (isSet(opts, 512) && offset != con.limit) ? -1 : offset;
    }
    
    private static final int getPreviousWordType(final char[] target, final int begin, final int end, int offset, final int opts) {
        int ret;
        for (ret = getWordType(target, begin, end, --offset, opts); ret == 0; ret = getWordType(target, begin, end, --offset, opts)) {}
        return ret;
    }
    
    private static final int getWordType(final char[] target, final int begin, final int end, final int offset, final int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target[offset], opts);
    }
    
    private static final boolean regionMatches(final char[] target, int offset, final int limit, final String part, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (partlen-- > 0) {
            if (target[offset++] != part.charAt(i++)) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean regionMatches(final char[] target, int offset, final int limit, final int offset2, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (partlen-- > 0) {
            if (target[offset++] != target[i++]) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean regionMatchesIgnoreCase(final char[] target, int offset, final int limit, final String part, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (partlen-- > 0) {
            final char ch1 = target[offset++];
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
    
    private static final boolean regionMatchesIgnoreCase(final char[] target, int offset, final int limit, final int offset2, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (partlen-- > 0) {
            final char ch1 = target[offset++];
            final char ch2 = target[i++];
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
            final int matchEnd = this.matchString(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
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
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    return con.inuse = false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.matchString(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target.charAt(matchStart);
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.matchString(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                RangeToken range = this.firstChar;
                if (isSet(this.options, 2)) {
                    range = this.firstChar.getCaseInsensitiveToken();
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target.charAt(matchStart);
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1));
                            if (!range.match(ch)) {
                                continue;
                            }
                        }
                        else if (!range.match(ch)) {
                            final char ch2 = Character.toUpperCase((char)ch);
                            if (!range.match(ch2) && !range.match(Character.toLowerCase(ch2))) {
                                continue;
                            }
                        }
                        if (0 <= (matchEnd2 = this.matchString(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
                else {
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target.charAt(matchStart);
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1));
                        }
                        if (range.match(ch)) {
                            if (0 <= (matchEnd2 = this.matchString(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.matchString(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
    }
    
    private int matchString(final Context con, Op op, int offset, final int dx, final int opts) {
        final String target = con.strTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 1: {
                    if (isSet(opts, 2)) {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch, target.charAt(offset))) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch, target.charAt(o1))) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    else {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch != target.charAt(offset)) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || ch != target.charAt(o1)) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 0: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.charAt(offset);
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ++offset;
                            }
                        }
                        else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ch = REUtil.composeFromSurrogates(ch, target.charAt(++offset));
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target.charAt(o2);
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                --o2;
                            }
                        }
                        else {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                ch2 = REUtil.composeFromSurrogates(target.charAt(--o2), ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 3:
                case 4: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.charAt(offset);
                        if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(++offset));
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            tok = tok.getCaseInsensitiveToken();
                            if (!tok.match(ch)) {
                                if (ch >= 65536) {
                                    return -1;
                                }
                                final char uch;
                                if (!tok.match(uch = Character.toUpperCase((char)ch)) && !tok.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok.match(ch)) {
                            return -1;
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target.charAt(o2);
                        if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                            ch2 = REUtil.composeFromSurrogates(target.charAt(--o2), ch2);
                        }
                        RangeToken tok2 = op.getToken();
                        if (isSet(opts, 2)) {
                            tok2 = tok2.getCaseInsensitiveToken();
                            if (!tok2.match(ch2)) {
                                if (ch2 >= 65536) {
                                    return -1;
                                }
                                final char uch2;
                                if (!tok2.match(uch2 = Character.toUpperCase((char)ch2)) && !tok2.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok2.match(ch2)) {
                            return -1;
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 5: {
                    boolean go = false;
                    switch (op.getData()) {
                        case 94: {
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target.charAt(offset - 1)))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.start) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 64: {
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target.charAt(offset - 1)))) {
                                return -1;
                            }
                            break;
                        }
                        case 36: {
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target.charAt(offset)))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n')) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 65: {
                            if (offset != con.start) {
                                return -1;
                            }
                            break;
                        }
                        case 90: {
                            if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n')) {
                                return -1;
                            }
                            break;
                        }
                        case 122: {
                            if (offset != con.limit) {
                                return -1;
                            }
                            break;
                        }
                        case 98: {
                            if (con.length == 0) {
                                return -1;
                            }
                            final int after = getWordType(target, con.start, con.limit, offset, opts);
                            if (after == 0) {
                                return -1;
                            }
                            final int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
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
                                return -1;
                            }
                            break;
                        }
                        case 60: {
                            if (con.length == 0 || offset == con.limit) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            break;
                        }
                        case 62: {
                            if (con.length == 0 || offset == con.start) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            break;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 16: {
                    final int refno = op.getData();
                    if (refno <= 0 || refno >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
                    }
                    if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                        return -1;
                    }
                    final int o3 = con.match.getBeginning(refno);
                    final int literallen = con.match.getEnd(refno) - o3;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset -= literallen;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset += literallen;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset -= literallen;
                    }
                    op = op.next;
                    continue;
                }
                case 6: {
                    final String literal = op.getString();
                    final int literallen2 = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset += literallen2;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen2, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset -= literallen2;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset += literallen2;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset -= literallen2;
                    }
                    op = op.next;
                    continue;
                }
                case 7: {
                    final int id = op.getData();
                    if (id >= 0) {
                        final int previousOffset = con.offsets[id];
                        if (previousOffset >= 0 && previousOffset == offset) {
                            con.offsets[id] = -1;
                            op = op.next;
                            continue;
                        }
                        con.offsets[id] = offset;
                    }
                    final int ret = this.matchString(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    continue;
                }
                case 9: {
                    final int ret2 = this.matchString(con, op.getChild(), offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.next;
                    continue;
                }
                case 8:
                case 10: {
                    final int ret2 = this.matchString(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    continue;
                }
                case 11: {
                    for (int i = 0; i < op.size(); ++i) {
                        final int ret = this.matchString(con, op.elementAt(i), offset, dx, opts);
                        if (ret >= 0) {
                            return ret;
                        }
                    }
                    return -1;
                }
                case 15: {
                    final int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        final int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        final int ret3 = this.matchString(con, op.next, offset, dx, opts);
                        if (ret3 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret3;
                    }
                    if (con.match != null && refno < 0) {
                        final int index = -refno;
                        final int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        final int ret4 = this.matchString(con, op.next, offset, dx, opts);
                        if (ret4 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret4;
                    }
                    op = op.next;
                    continue;
                }
                case 20: {
                    if (0 > this.matchString(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 21: {
                    if (0 <= this.matchString(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 22: {
                    if (0 > this.matchString(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 23: {
                    if (0 <= this.matchString(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 24: {
                    final int ret = this.matchString(con, op.getChild(), offset, dx, opts);
                    if (ret < 0) {
                        return ret;
                    }
                    offset = ret;
                    op = op.next;
                    continue;
                }
                case 25: {
                    int localopts = opts;
                    localopts |= op.getData();
                    localopts &= ~op.getData2();
                    final int ret3 = this.matchString(con, op.getChild(), offset, dx, localopts);
                    if (ret3 < 0) {
                        return ret3;
                    }
                    offset = ret3;
                    op = op.next;
                    continue;
                }
                case 26: {
                    final Op.ConditionOp cop = (Op.ConditionOp)op;
                    boolean matchp = false;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = (con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0);
                    }
                    else {
                        matchp = (0 <= this.matchString(con, cop.condition, offset, dx, opts));
                    }
                    if (matchp) {
                        op = cop.yes;
                    }
                    else if (cop.no != null) {
                        op = cop.no;
                    }
                    else {
                        op = cop.next;
                    }
                    continue;
                }
                default: {
                    throw new RuntimeException("Unknown operation type: " + op.type);
                }
            }
        }
        return (isSet(opts, 512) && offset != con.limit) ? -1 : offset;
    }
    
    private static final int getPreviousWordType(final String target, final int begin, final int end, int offset, final int opts) {
        int ret;
        for (ret = getWordType(target, begin, end, --offset, opts); ret == 0; ret = getWordType(target, begin, end, --offset, opts)) {}
        return ret;
    }
    
    private static final int getWordType(final String target, final int begin, final int end, final int offset, final int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target.charAt(offset), opts);
    }
    
    private static final boolean regionMatches(final String text, final int offset, final int limit, final String part, final int partlen) {
        return limit - offset >= partlen && text.regionMatches(offset, part, 0, partlen);
    }
    
    private static final boolean regionMatches(final String text, final int offset, final int limit, final int offset2, final int partlen) {
        return limit - offset >= partlen && text.regionMatches(offset, text, offset2, partlen);
    }
    
    private static final boolean regionMatchesIgnoreCase(final String text, final int offset, final int limit, final String part, final int partlen) {
        return text.regionMatches(true, offset, part, 0, partlen);
    }
    
    private static final boolean regionMatchesIgnoreCase(final String text, final int offset, final int limit, final int offset2, final int partlen) {
        return limit - offset >= partlen && text.regionMatches(true, offset, text, offset2, partlen);
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
            final int matchEnd = this.matchCharacterIterator(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
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
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
        else {
            if (this.fixedString != null) {
                final int o = this.fixedStringTable.matches(target, con.start, con.limit);
                if (o < 0) {
                    return con.inuse = false;
                }
            }
            final int limit = con.limit - this.minlength;
            int matchEnd2 = -1;
            int matchStart;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    matchStart = con.start;
                    matchEnd2 = this.matchCharacterIterator(con, this.operations, con.start, 1, this.options);
                }
                else {
                    boolean previousIsEOL = true;
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        final int ch = target.setIndex(matchStart);
                        if (isEOLChar(ch)) {
                            previousIsEOL = true;
                        }
                        else {
                            if (previousIsEOL && 0 <= (matchEnd2 = this.matchCharacterIterator(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                            previousIsEOL = false;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                RangeToken range = this.firstChar;
                if (isSet(this.options, 2)) {
                    range = this.firstChar.getCaseInsensitiveToken();
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target.setIndex(matchStart);
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1));
                            if (!range.match(ch)) {
                                continue;
                            }
                        }
                        else if (!range.match(ch)) {
                            final char ch2 = Character.toUpperCase((char)ch);
                            if (!range.match(ch2) && !range.match(Character.toLowerCase(ch2))) {
                                continue;
                            }
                        }
                        if (0 <= (matchEnd2 = this.matchCharacterIterator(con, this.operations, matchStart, 1, this.options))) {
                            break;
                        }
                    }
                }
                else {
                    for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                        int ch = target.setIndex(matchStart);
                        if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1));
                        }
                        if (range.match(ch)) {
                            if (0 <= (matchEnd2 = this.matchCharacterIterator(con, this.operations, matchStart, 1, this.options))) {
                                break;
                            }
                        }
                    }
                }
            }
            else {
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    if (0 <= (matchEnd2 = this.matchCharacterIterator(con, this.operations, matchStart, 1, this.options))) {
                        break;
                    }
                }
            }
            if (matchEnd2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, matchStart);
                    con.match.setEnd(0, matchEnd2);
                }
                con.inuse = false;
                return true;
            }
            return con.inuse = false;
        }
    }
    
    private int matchCharacterIterator(final Context con, Op op, int offset, final int dx, final int opts) {
        final CharacterIterator target = con.ciTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 1: {
                    if (isSet(opts, 2)) {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch, target.setIndex(offset))) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch, target.setIndex(o1))) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    else {
                        final int ch = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch != target.setIndex(offset)) {
                                return -1;
                            }
                            ++offset;
                        }
                        else {
                            final int o1 = offset - 1;
                            if (o1 >= con.limit || o1 < 0 || ch != target.setIndex(o1)) {
                                return -1;
                            }
                            offset = o1;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 0: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.setIndex(offset);
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ++offset;
                            }
                        }
                        else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                ch = REUtil.composeFromSurrogates(ch, target.setIndex(++offset));
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target.setIndex(o2);
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                --o2;
                            }
                        }
                        else {
                            if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                                ch2 = REUtil.composeFromSurrogates(target.setIndex(--o2), ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 3:
                case 4: {
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.setIndex(offset);
                        if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                            ch = REUtil.composeFromSurrogates(ch, target.setIndex(++offset));
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            tok = tok.getCaseInsensitiveToken();
                            if (!tok.match(ch)) {
                                if (ch >= 65536) {
                                    return -1;
                                }
                                final char uch;
                                if (!tok.match(uch = Character.toUpperCase((char)ch)) && !tok.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok.match(ch)) {
                            return -1;
                        }
                        ++offset;
                    }
                    else {
                        int o2 = offset - 1;
                        if (o2 >= con.limit || o2 < 0) {
                            return -1;
                        }
                        int ch2 = target.setIndex(o2);
                        if (REUtil.isLowSurrogate(ch2) && o2 - 1 >= 0) {
                            ch2 = REUtil.composeFromSurrogates(target.setIndex(--o2), ch2);
                        }
                        RangeToken tok2 = op.getToken();
                        if (isSet(opts, 2)) {
                            tok2 = tok2.getCaseInsensitiveToken();
                            if (!tok2.match(ch2)) {
                                if (ch2 >= 65536) {
                                    return -1;
                                }
                                final char uch2;
                                if (!tok2.match(uch2 = Character.toUpperCase((char)ch2)) && !tok2.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        }
                        else if (!tok2.match(ch2)) {
                            return -1;
                        }
                        offset = o2;
                    }
                    op = op.next;
                    continue;
                }
                case 5: {
                    boolean go = false;
                    switch (op.getData()) {
                        case 94: {
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target.setIndex(offset - 1)))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.start) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 64: {
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target.setIndex(offset - 1)))) {
                                return -1;
                            }
                            break;
                        }
                        case 36: {
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target.setIndex(offset)))) {
                                    return -1;
                                }
                                break;
                            }
                            else {
                                if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.setIndex(offset))) && (offset + 2 != con.limit || target.setIndex(offset) != '\r' || target.setIndex(offset + 1) != '\n')) {
                                    return -1;
                                }
                                break;
                            }
                            break;
                        }
                        case 65: {
                            if (offset != con.start) {
                                return -1;
                            }
                            break;
                        }
                        case 90: {
                            if (offset != con.limit && (offset + 1 != con.limit || !isEOLChar(target.setIndex(offset))) && (offset + 2 != con.limit || target.setIndex(offset) != '\r' || target.setIndex(offset + 1) != '\n')) {
                                return -1;
                            }
                            break;
                        }
                        case 122: {
                            if (offset != con.limit) {
                                return -1;
                            }
                            break;
                        }
                        case 98: {
                            if (con.length == 0) {
                                return -1;
                            }
                            final int after = getWordType(target, con.start, con.limit, offset, opts);
                            if (after == 0) {
                                return -1;
                            }
                            final int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
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
                                return -1;
                            }
                            break;
                        }
                        case 60: {
                            if (con.length == 0 || offset == con.limit) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            break;
                        }
                        case 62: {
                            if (con.length == 0 || offset == con.start) {
                                return -1;
                            }
                            if (getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            break;
                        }
                    }
                    op = op.next;
                    continue;
                }
                case 16: {
                    final int refno = op.getData();
                    if (refno <= 0 || refno >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
                    }
                    if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                        return -1;
                    }
                    final int o3 = con.match.getBeginning(refno);
                    final int literallen = con.match.getEnd(refno) - o3;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen, con.limit, o3, literallen)) {
                                return -1;
                            }
                            offset -= literallen;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset += literallen;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, o3, literallen)) {
                            return -1;
                        }
                        offset -= literallen;
                    }
                    op = op.next;
                    continue;
                }
                case 6: {
                    final String literal = op.getString();
                    final int literallen2 = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset += literallen2;
                        }
                        else {
                            if (!regionMatches(target, offset - literallen2, con.limit, literal, literallen2)) {
                                return -1;
                            }
                            offset -= literallen2;
                        }
                    }
                    else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset += literallen2;
                    }
                    else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, literal, literallen2)) {
                            return -1;
                        }
                        offset -= literallen2;
                    }
                    op = op.next;
                    continue;
                }
                case 7: {
                    final int id = op.getData();
                    if (id >= 0) {
                        final int previousOffset = con.offsets[id];
                        if (previousOffset >= 0 && previousOffset == offset) {
                            con.offsets[id] = -1;
                            op = op.next;
                            continue;
                        }
                        con.offsets[id] = offset;
                    }
                    final int ret = this.matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    continue;
                }
                case 9: {
                    final int ret2 = this.matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.next;
                    continue;
                }
                case 8:
                case 10: {
                    final int ret2 = this.matchCharacterIterator(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    continue;
                }
                case 11: {
                    for (int i = 0; i < op.size(); ++i) {
                        final int ret = this.matchCharacterIterator(con, op.elementAt(i), offset, dx, opts);
                        if (ret >= 0) {
                            return ret;
                        }
                    }
                    return -1;
                }
                case 15: {
                    final int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        final int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        final int ret3 = this.matchCharacterIterator(con, op.next, offset, dx, opts);
                        if (ret3 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret3;
                    }
                    if (con.match != null && refno < 0) {
                        final int index = -refno;
                        final int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        final int ret4 = this.matchCharacterIterator(con, op.next, offset, dx, opts);
                        if (ret4 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret4;
                    }
                    op = op.next;
                    continue;
                }
                case 20: {
                    if (0 > this.matchCharacterIterator(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 21: {
                    if (0 <= this.matchCharacterIterator(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 22: {
                    if (0 > this.matchCharacterIterator(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 23: {
                    if (0 <= this.matchCharacterIterator(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    continue;
                }
                case 24: {
                    final int ret = this.matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (ret < 0) {
                        return ret;
                    }
                    offset = ret;
                    op = op.next;
                    continue;
                }
                case 25: {
                    int localopts = opts;
                    localopts |= op.getData();
                    localopts &= ~op.getData2();
                    final int ret3 = this.matchCharacterIterator(con, op.getChild(), offset, dx, localopts);
                    if (ret3 < 0) {
                        return ret3;
                    }
                    offset = ret3;
                    op = op.next;
                    continue;
                }
                case 26: {
                    final Op.ConditionOp cop = (Op.ConditionOp)op;
                    boolean matchp = false;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = (con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0);
                    }
                    else {
                        matchp = (0 <= this.matchCharacterIterator(con, cop.condition, offset, dx, opts));
                    }
                    if (matchp) {
                        op = cop.yes;
                    }
                    else if (cop.no != null) {
                        op = cop.no;
                    }
                    else {
                        op = cop.next;
                    }
                    continue;
                }
                default: {
                    throw new RuntimeException("Unknown operation type: " + op.type);
                }
            }
        }
        return (isSet(opts, 512) && offset != con.limit) ? -1 : offset;
    }
    
    private static final int getPreviousWordType(final CharacterIterator target, final int begin, final int end, int offset, final int opts) {
        int ret;
        for (ret = getWordType(target, begin, end, --offset, opts); ret == 0; ret = getWordType(target, begin, end, --offset, opts)) {}
        return ret;
    }
    
    private static final int getWordType(final CharacterIterator target, final int begin, final int end, final int offset, final int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target.setIndex(offset), opts);
    }
    
    private static final boolean regionMatches(final CharacterIterator target, int offset, final int limit, final String part, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (partlen-- > 0) {
            if (target.setIndex(offset++) != part.charAt(i++)) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean regionMatches(final CharacterIterator target, int offset, final int limit, final int offset2, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (partlen-- > 0) {
            if (target.setIndex(offset++) != target.setIndex(i++)) {
                return false;
            }
        }
        return true;
    }
    
    private static final boolean regionMatchesIgnoreCase(final CharacterIterator target, int offset, final int limit, final String part, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (partlen-- > 0) {
            final char ch1 = target.setIndex(offset++);
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
    
    private static final boolean regionMatchesIgnoreCase(final CharacterIterator target, int offset, final int limit, final int offset2, int partlen) {
        if (offset < 0) {
            return false;
        }
        if (limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (partlen-- > 0) {
            final char ch1 = target.setIndex(offset++);
            final char ch2 = target.setIndex(i++);
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
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(regex, null);
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
        this.setPattern(newPattern, this.options);
    }
    
    private void setPattern(final String newPattern, final int options) throws ParseException {
        this.regex = newPattern;
        this.options = options;
        final RegexParser rp = isSet(this.options, 512) ? new ParserForXMLSchema() : new RegexParser();
        this.tokentree = rp.parse(this.regex, this.options);
        this.nofparen = rp.parennumber;
        this.hasBackReferences = rp.hasBackReferences;
        this.operations = null;
        this.context = null;
    }
    
    public void setPattern(final String newPattern, final String options) throws ParseException {
        this.setPattern(newPattern, REUtil.parseOptions(options));
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
    
    static final class Context
    {
        CharacterIterator ciTarget;
        String strTarget;
        char[] charTarget;
        int start;
        int limit;
        int length;
        Match match;
        boolean inuse;
        int[] offsets;
        
        Context() {
            this.inuse = false;
        }
        
        private void resetCommon(final int nofclosures) {
            this.length = this.limit - this.start;
            this.inuse = true;
            this.match = null;
            if (this.offsets == null || this.offsets.length != nofclosures) {
                this.offsets = new int[nofclosures];
            }
            for (int i = 0; i < nofclosures; ++i) {
                this.offsets[i] = -1;
            }
        }
        
        void reset(final CharacterIterator target, final int start, final int limit, final int nofclosures) {
            this.ciTarget = target;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        
        void reset(final String target, final int start, final int limit, final int nofclosures) {
            this.strTarget = target;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        
        void reset(final char[] target, final int start, final int limit, final int nofclosures) {
            this.charTarget = target;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
    }
}
