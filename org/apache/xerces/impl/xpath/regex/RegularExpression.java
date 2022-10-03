package org.apache.xerces.impl.xpath.regex;

import java.util.Locale;
import java.text.CharacterIterator;
import org.apache.xerces.util.IntStack;
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
    static final int ALLOW_UNRECOGNIZED_BLOCK_NAME = 2048;
    static final int HYPHEN_IN_SCHEMA_11 = 4096;
    private static final int WT_IGNORE = 0;
    private static final int WT_LETTER = 1;
    private static final int WT_OTHER = 2;
    static final int LINE_FEED = 10;
    static final int CARRIAGE_RETURN = 13;
    static final int LINE_SEPARATOR = 8232;
    static final int PARAGRAPH_SEPARATOR = 8233;
    
    private synchronized void compile(final Token token) {
        if (this.operations != null) {
            return;
        }
        this.numberOfClosures = 0;
        this.operations = this.compile(token, null, false);
    }
    
    private Op compile(final Token token, Op op, final boolean b) {
        Op op2 = null;
        switch (token.type) {
            case 11: {
                op2 = Op.createDot();
                op2.next = op;
                break;
            }
            case 0: {
                op2 = Op.createChar(token.getChar());
                op2.next = op;
                break;
            }
            case 8: {
                op2 = Op.createAnchor(token.getChar());
                op2.next = op;
                break;
            }
            case 4:
            case 5: {
                op2 = Op.createRange(token);
                op2.next = op;
                break;
            }
            case 1: {
                op2 = op;
                if (!b) {
                    for (int i = token.size() - 1; i >= 0; --i) {
                        op2 = this.compile(token.getChild(i), op2, false);
                    }
                    break;
                }
                for (int j = 0; j < token.size(); ++j) {
                    op2 = this.compile(token.getChild(j), op2, true);
                }
                break;
            }
            case 2: {
                final Op.UnionOp union = Op.createUnion(token.size());
                for (int k = 0; k < token.size(); ++k) {
                    union.addElement(this.compile(token.getChild(k), op, b));
                }
                op2 = union;
                break;
            }
            case 3:
            case 9: {
                final Token child = token.getChild(0);
                final int min = token.getMin();
                int max = token.getMax();
                if (min >= 0 && min == max) {
                    op2 = op;
                    for (int l = 0; l < min; ++l) {
                        op2 = this.compile(child, op2, b);
                    }
                    break;
                }
                if (min > 0 && max > 0) {
                    max -= min;
                }
                if (max > 0) {
                    op2 = op;
                    for (int n = 0; n < max; ++n) {
                        final Op.ChildOp question = Op.createQuestion(token.type == 9);
                        question.next = op;
                        question.setChild(this.compile(child, op2, b));
                        op2 = question;
                    }
                }
                else {
                    Op.ChildOp childOp;
                    if (token.type == 9) {
                        childOp = Op.createNonGreedyClosure();
                    }
                    else {
                        childOp = Op.createClosure(this.numberOfClosures++);
                    }
                    childOp.next = op;
                    childOp.setChild(this.compile(child, childOp, b));
                    op2 = childOp;
                }
                if (min > 0) {
                    for (int n2 = 0; n2 < min; ++n2) {
                        op2 = this.compile(child, op2, b);
                    }
                    break;
                }
                break;
            }
            case 7: {
                op2 = op;
                break;
            }
            case 10: {
                op2 = Op.createString(token.getString());
                op2.next = op;
                break;
            }
            case 12: {
                op2 = Op.createBackReference(token.getReferenceNumber());
                op2.next = op;
                break;
            }
            case 6: {
                if (token.getParenNumber() == 0) {
                    op2 = this.compile(token.getChild(0), op, b);
                    break;
                }
                if (b) {
                    op = this.compile(token.getChild(0), Op.createCapture(token.getParenNumber(), op), b);
                    op2 = Op.createCapture(-token.getParenNumber(), op);
                    break;
                }
                op = this.compile(token.getChild(0), Op.createCapture(-token.getParenNumber(), op), b);
                op2 = Op.createCapture(token.getParenNumber(), op);
                break;
            }
            case 20: {
                op2 = Op.createLook(20, op, this.compile(token.getChild(0), null, false));
                break;
            }
            case 21: {
                op2 = Op.createLook(21, op, this.compile(token.getChild(0), null, false));
                break;
            }
            case 22: {
                op2 = Op.createLook(22, op, this.compile(token.getChild(0), null, true));
                break;
            }
            case 23: {
                op2 = Op.createLook(23, op, this.compile(token.getChild(0), null, true));
                break;
            }
            case 24: {
                op2 = Op.createIndependent(op, this.compile(token.getChild(0), null, b));
                break;
            }
            case 25: {
                op2 = Op.createModifier(op, this.compile(token.getChild(0), null, b), ((Token.ModifierToken)token).getOptions(), ((Token.ModifierToken)token).getOptionsMask());
                break;
            }
            case 26: {
                final Token.ConditionToken conditionToken = (Token.ConditionToken)token;
                op2 = Op.createCondition(op, conditionToken.refNumber, (conditionToken.condition == null) ? null : this.compile(conditionToken.condition, null, b), this.compile(conditionToken.yes, op, b), (conditionToken.no == null) ? null : this.compile(conditionToken.no, op, b));
                break;
            }
            default: {
                throw new RuntimeException("Unknown token type: " + token.type);
            }
        }
        return op2;
    }
    
    public boolean matches(final char[] array) {
        return this.matches(array, 0, array.length, null);
    }
    
    public boolean matches(final char[] array, final int n, final int n2) {
        return this.matches(array, n, n2, null);
    }
    
    public boolean matches(final char[] array, final Match match) {
        return this.matches(array, 0, array.length, match);
    }
    
    public boolean matches(final char[] source, final int n, final int n2, Match match) {
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context context = null;
        synchronized (this.context) {
            context = (this.context.inuse ? new Context() : this.context);
            context.reset(source, n, n2, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(source);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        context.match = match;
        if (isSet(this.options, 512)) {
            final int match2 = this.match(context, this.operations, context.start, 1, this.options);
            if (match2 == context.limit) {
                if (context.match != null) {
                    context.match.setBeginning(0, context.start);
                    context.match.setEnd(0, match2);
                }
                context.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int matches = this.fixedStringTable.matches(source, context.start, context.limit);
            if (matches >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, matches);
                    context.match.setEnd(0, matches + this.fixedString.length());
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null && this.fixedStringTable.matches(source, context.start, context.limit) < 0) {
                context.setInUse(false);
                return false;
            }
            final int n3 = context.limit - this.minlength;
            int n4 = -1;
            int i;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    i = context.start;
                    n4 = this.match(context, this.operations, context.start, 1, this.options);
                }
                else {
                    int n5 = 1;
                    for (i = context.start; i <= n3; ++i) {
                        if (isEOLChar(source[i])) {
                            n5 = 1;
                        }
                        else {
                            if (n5 != 0 && 0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                                break;
                            }
                            n5 = 0;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken firstChar = this.firstChar;
                for (i = context.start; i <= n3; ++i) {
                    int composeFromSurrogates = source[i];
                    if (REUtil.isHighSurrogate(composeFromSurrogates) && i + 1 < context.limit) {
                        composeFromSurrogates = REUtil.composeFromSurrogates(composeFromSurrogates, source[i + 1]);
                    }
                    if (firstChar.match(composeFromSurrogates)) {
                        if (0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (i = context.start; i <= n3; ++i) {
                    if (0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                        break;
                    }
                }
            }
            if (n4 >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, i);
                    context.match.setEnd(0, n4);
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
    }
    
    public boolean matches(final String s) {
        return this.matches(s, 0, s.length(), null);
    }
    
    public boolean matches(final String s, final int n, final int n2) {
        return this.matches(s, n, n2, null);
    }
    
    public boolean matches(final String s, final Match match) {
        return this.matches(s, 0, s.length(), match);
    }
    
    public boolean matches(final String source, final int n, final int n2, Match match) {
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context context = null;
        synchronized (this.context) {
            context = (this.context.inuse ? new Context() : this.context);
            context.reset(source, n, n2, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(source);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        context.match = match;
        if (isSet(this.options, 512)) {
            final int match2 = this.match(context, this.operations, context.start, 1, this.options);
            if (match2 == context.limit) {
                if (context.match != null) {
                    context.match.setBeginning(0, context.start);
                    context.match.setEnd(0, match2);
                }
                context.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int matches = this.fixedStringTable.matches(source, context.start, context.limit);
            if (matches >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, matches);
                    context.match.setEnd(0, matches + this.fixedString.length());
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null && this.fixedStringTable.matches(source, context.start, context.limit) < 0) {
                context.setInUse(false);
                return false;
            }
            final int n3 = context.limit - this.minlength;
            int n4 = -1;
            int i;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    i = context.start;
                    n4 = this.match(context, this.operations, context.start, 1, this.options);
                }
                else {
                    int n5 = 1;
                    for (i = context.start; i <= n3; ++i) {
                        if (isEOLChar(source.charAt(i))) {
                            n5 = 1;
                        }
                        else {
                            if (n5 != 0 && 0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                                break;
                            }
                            n5 = 0;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken firstChar = this.firstChar;
                for (i = context.start; i <= n3; ++i) {
                    int n6 = source.charAt(i);
                    if (REUtil.isHighSurrogate(n6) && i + 1 < context.limit) {
                        n6 = REUtil.composeFromSurrogates(n6, source.charAt(i + 1));
                    }
                    if (firstChar.match(n6)) {
                        if (0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (i = context.start; i <= n3; ++i) {
                    if (0 <= (n4 = this.match(context, this.operations, i, 1, this.options))) {
                        break;
                    }
                }
            }
            if (n4 >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, i);
                    context.match.setEnd(0, n4);
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
    }
    
    private int match(final Context context, Op op, int pop, int n, int pop2) {
        final ExpressionTarget target = context.target;
        final Stack stack = new Stack();
        final IntStack intStack = new IntStack();
        final boolean set = isSet(pop2, 2);
        int i = 0;
        while (true) {
            int n2;
            if (op == null || pop > context.limit || pop < context.start) {
                if (op == null) {
                    n2 = ((isSet(pop2, 512) && pop != context.limit) ? -1 : pop);
                }
                else {
                    n2 = -1;
                }
                i = 1;
            }
            else {
                n2 = -1;
                switch (op.type) {
                    case 1: {
                        final int n3 = (n > 0) ? pop : (pop - 1);
                        if (n3 >= context.limit || n3 < 0 || !this.matchChar(op.getData(), target.charAt(n3), set)) {
                            i = 1;
                            break;
                        }
                        pop += n;
                        op = op.next;
                        break;
                    }
                    case 0: {
                        int n4 = (n > 0) ? pop : (pop - 1);
                        if (n4 >= context.limit || n4 < 0) {
                            i = 1;
                            break;
                        }
                        if (isSet(pop2, 4)) {
                            if (REUtil.isHighSurrogate(target.charAt(n4)) && n4 + n >= 0 && n4 + n < context.limit) {
                                n4 += n;
                            }
                        }
                        else {
                            int n5 = target.charAt(n4);
                            if (REUtil.isHighSurrogate(n5) && n4 + n >= 0 && n4 + n < context.limit) {
                                n4 += n;
                                n5 = REUtil.composeFromSurrogates(n5, target.charAt(n4));
                            }
                            if (isEOLChar(n5)) {
                                i = 1;
                                break;
                            }
                        }
                        pop = ((n > 0) ? (n4 + 1) : n4);
                        op = op.next;
                        break;
                    }
                    case 3:
                    case 4: {
                        int n6 = (n > 0) ? pop : (pop - 1);
                        if (n6 >= context.limit || n6 < 0) {
                            i = 1;
                            break;
                        }
                        int n7 = target.charAt(pop);
                        if (REUtil.isHighSurrogate(n7) && n6 + n < context.limit && n6 + n >= 0) {
                            n6 += n;
                            n7 = REUtil.composeFromSurrogates(n7, target.charAt(n6));
                        }
                        if (!op.getToken().match(n7)) {
                            i = 1;
                            break;
                        }
                        pop = ((n > 0) ? (n6 + 1) : n6);
                        op = op.next;
                        break;
                    }
                    case 5: {
                        if (!this.matchAnchor(target, op, context, pop, pop2)) {
                            i = 1;
                            break;
                        }
                        op = op.next;
                        break;
                    }
                    case 16: {
                        final int data = op.getData();
                        if (data <= 0 || data >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + data);
                        }
                        if (context.match.getBeginning(data) < 0 || context.match.getEnd(data) < 0) {
                            i = 1;
                            break;
                        }
                        final int beginning = context.match.getBeginning(data);
                        final int n8 = context.match.getEnd(data) - beginning;
                        if (n > 0) {
                            if (!target.regionMatches(set, pop, context.limit, beginning, n8)) {
                                i = 1;
                                break;
                            }
                            pop += n8;
                        }
                        else {
                            if (!target.regionMatches(set, pop - n8, context.limit, beginning, n8)) {
                                i = 1;
                                break;
                            }
                            pop -= n8;
                        }
                        op = op.next;
                        break;
                    }
                    case 6: {
                        final String string = op.getString();
                        final int length = string.length();
                        if (n > 0) {
                            if (!target.regionMatches(set, pop, context.limit, string, length)) {
                                i = 1;
                                break;
                            }
                            pop += length;
                        }
                        else {
                            if (!target.regionMatches(set, pop - length, context.limit, string, length)) {
                                i = 1;
                                break;
                            }
                            pop -= length;
                        }
                        op = op.next;
                        break;
                    }
                    case 7: {
                        final int data2 = op.getData();
                        if (context.closureContexts[data2].contains(pop)) {
                            i = 1;
                            break;
                        }
                        context.closureContexts[data2].addOffset(pop);
                    }
                    case 9: {
                        stack.push(op);
                        intStack.push(pop);
                        op = op.getChild();
                        break;
                    }
                    case 8:
                    case 10: {
                        stack.push(op);
                        intStack.push(pop);
                        op = op.next;
                        break;
                    }
                    case 11: {
                        if (op.size() == 0) {
                            i = 1;
                            break;
                        }
                        stack.push(op);
                        intStack.push(0);
                        intStack.push(pop);
                        op = op.elementAt(0);
                        break;
                    }
                    case 15: {
                        final int data3 = op.getData();
                        if (context.match != null) {
                            if (data3 > 0) {
                                intStack.push(context.match.getBeginning(data3));
                                context.match.setBeginning(data3, pop);
                            }
                            else {
                                final int n9 = -data3;
                                intStack.push(context.match.getEnd(n9));
                                context.match.setEnd(n9, pop);
                            }
                            stack.push(op);
                            intStack.push(pop);
                        }
                        op = op.next;
                        break;
                    }
                    case 20:
                    case 21:
                    case 22:
                    case 23: {
                        stack.push(op);
                        intStack.push(n);
                        intStack.push(pop);
                        n = ((op.type == 20 || op.type == 21) ? 1 : -1);
                        op = op.getChild();
                        break;
                    }
                    case 24: {
                        stack.push(op);
                        intStack.push(pop);
                        op = op.getChild();
                        break;
                    }
                    case 25: {
                        final int n10 = (pop2 | op.getData()) & ~op.getData2();
                        stack.push(op);
                        intStack.push(pop2);
                        intStack.push(pop);
                        pop2 = n10;
                        op = op.getChild();
                        break;
                    }
                    case 26: {
                        final Op.ConditionOp conditionOp = (Op.ConditionOp)op;
                        if (conditionOp.refNumber <= 0) {
                            stack.push(op);
                            intStack.push(pop);
                            op = conditionOp.condition;
                            break;
                        }
                        if (conditionOp.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + conditionOp.refNumber);
                        }
                        if (context.match.getBeginning(conditionOp.refNumber) >= 0 && context.match.getEnd(conditionOp.refNumber) >= 0) {
                            op = conditionOp.yes;
                            break;
                        }
                        if (conditionOp.no != null) {
                            op = conditionOp.no;
                            break;
                        }
                        op = conditionOp.next;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown operation type: " + op.type);
                    }
                }
            }
            while (i != 0) {
                if (stack.isEmpty()) {
                    return n2;
                }
                op = (Op)stack.pop();
                pop = intStack.pop();
                switch (op.type) {
                    case 7:
                    case 9: {
                        if (n2 < 0) {
                            op = op.next;
                            i = 0;
                            continue;
                        }
                        continue;
                    }
                    case 8:
                    case 10: {
                        if (n2 < 0) {
                            op = op.getChild();
                            i = 0;
                            continue;
                        }
                        continue;
                    }
                    case 11: {
                        int pop3 = intStack.pop();
                        if (n2 >= 0) {
                            continue;
                        }
                        if (++pop3 < op.size()) {
                            stack.push(op);
                            intStack.push(pop3);
                            intStack.push(pop);
                            op = op.elementAt(pop3);
                            i = 0;
                            continue;
                        }
                        n2 = -1;
                        continue;
                    }
                    case 15: {
                        final int data4 = op.getData();
                        final int pop4 = intStack.pop();
                        if (n2 >= 0) {
                            continue;
                        }
                        if (data4 > 0) {
                            context.match.setBeginning(data4, pop4);
                            continue;
                        }
                        context.match.setEnd(-data4, pop4);
                        continue;
                    }
                    case 20:
                    case 22: {
                        n = intStack.pop();
                        if (0 <= n2) {
                            op = op.next;
                            i = 0;
                        }
                        n2 = -1;
                        continue;
                    }
                    case 21:
                    case 23: {
                        n = intStack.pop();
                        if (0 > n2) {
                            op = op.next;
                            i = 0;
                        }
                        n2 = -1;
                        continue;
                    }
                    case 25: {
                        pop2 = intStack.pop();
                    }
                    case 24: {
                        if (n2 >= 0) {
                            pop = n2;
                            op = op.next;
                            i = 0;
                            continue;
                        }
                        continue;
                    }
                    case 26: {
                        final Op.ConditionOp conditionOp2 = (Op.ConditionOp)op;
                        if (0 <= n2) {
                            op = conditionOp2.yes;
                        }
                        else if (conditionOp2.no != null) {
                            op = conditionOp2.no;
                        }
                        else {
                            op = conditionOp2.next;
                        }
                        i = 0;
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
    
    private boolean matchChar(final int n, final int n2, final boolean b) {
        return b ? matchIgnoreCase(n, n2) : (n == n2);
    }
    
    boolean matchAnchor(final ExpressionTarget expressionTarget, final Op op, final Context context, final int n, final int n2) {
        switch (op.getData()) {
            case 94: {
                if (isSet(n2, 8)) {
                    if (n != context.start && (n <= context.start || n >= context.limit || !isEOLChar(expressionTarget.charAt(n - 1)))) {
                        return false;
                    }
                    break;
                }
                else {
                    if (n != context.start) {
                        return false;
                    }
                    break;
                }
                break;
            }
            case 64: {
                if (n != context.start && (n <= context.start || !isEOLChar(expressionTarget.charAt(n - 1)))) {
                    return false;
                }
                break;
            }
            case 36: {
                if (isSet(n2, 8)) {
                    if (n != context.limit && (n >= context.limit || !isEOLChar(expressionTarget.charAt(n)))) {
                        return false;
                    }
                    break;
                }
                else {
                    if (n != context.limit && (n + 1 != context.limit || !isEOLChar(expressionTarget.charAt(n))) && (n + 2 != context.limit || expressionTarget.charAt(n) != '\r' || expressionTarget.charAt(n + 1) != '\n')) {
                        return false;
                    }
                    break;
                }
                break;
            }
            case 65: {
                if (n != context.start) {
                    return false;
                }
                break;
            }
            case 90: {
                if (n != context.limit && (n + 1 != context.limit || !isEOLChar(expressionTarget.charAt(n))) && (n + 2 != context.limit || expressionTarget.charAt(n) != '\r' || expressionTarget.charAt(n + 1) != '\n')) {
                    return false;
                }
                break;
            }
            case 122: {
                if (n != context.limit) {
                    return false;
                }
                break;
            }
            case 98: {
                if (context.length == 0) {
                    return false;
                }
                final int wordType = getWordType(expressionTarget, context.start, context.limit, n, n2);
                if (wordType == 0) {
                    return false;
                }
                if (wordType == getPreviousWordType(expressionTarget, context.start, context.limit, n, n2)) {
                    return false;
                }
                break;
            }
            case 66: {
                int n3;
                if (context.length == 0) {
                    n3 = 1;
                }
                else {
                    final int wordType2 = getWordType(expressionTarget, context.start, context.limit, n, n2);
                    n3 = ((wordType2 == 0 || wordType2 == getPreviousWordType(expressionTarget, context.start, context.limit, n, n2)) ? 1 : 0);
                }
                if (n3 == 0) {
                    return false;
                }
                break;
            }
            case 60: {
                if (context.length == 0 || n == context.limit) {
                    return false;
                }
                if (getWordType(expressionTarget, context.start, context.limit, n, n2) != 1 || getPreviousWordType(expressionTarget, context.start, context.limit, n, n2) != 2) {
                    return false;
                }
                break;
            }
            case 62: {
                if (context.length == 0 || n == context.start) {
                    return false;
                }
                if (getWordType(expressionTarget, context.start, context.limit, n, n2) != 2 || getPreviousWordType(expressionTarget, context.start, context.limit, n, n2) != 1) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    private static final int getPreviousWordType(final ExpressionTarget expressionTarget, final int n, final int n2, int n3, final int n4) {
        int i;
        for (i = getWordType(expressionTarget, n, n2, --n3, n4); i == 0; i = getWordType(expressionTarget, n, n2, --n3, n4)) {}
        return i;
    }
    
    private static final int getWordType(final ExpressionTarget expressionTarget, final int n, final int n2, final int n3, final int n4) {
        if (n3 < n || n3 >= n2) {
            return 2;
        }
        return getWordType0(expressionTarget.charAt(n3), n4);
    }
    
    public boolean matches(final CharacterIterator characterIterator) {
        return this.matches(characterIterator, null);
    }
    
    public boolean matches(final CharacterIterator source, Match match) {
        final int beginIndex = source.getBeginIndex();
        final int endIndex = source.getEndIndex();
        synchronized (this) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context context = null;
        synchronized (this.context) {
            context = (this.context.inuse ? new Context() : this.context);
            context.reset(source, beginIndex, endIndex, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(source);
        }
        else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        context.match = match;
        if (isSet(this.options, 512)) {
            final int match2 = this.match(context, this.operations, context.start, 1, this.options);
            if (match2 == context.limit) {
                if (context.match != null) {
                    context.match.setBeginning(0, context.start);
                    context.match.setEnd(0, match2);
                }
                context.setInUse(false);
                return true;
            }
            return false;
        }
        else if (this.fixedStringOnly) {
            final int matches = this.fixedStringTable.matches(source, context.start, context.limit);
            if (matches >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, matches);
                    context.match.setEnd(0, matches + this.fixedString.length());
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
        else {
            if (this.fixedString != null && this.fixedStringTable.matches(source, context.start, context.limit) < 0) {
                context.setInUse(false);
                return false;
            }
            final int n = context.limit - this.minlength;
            int n2 = -1;
            int i;
            if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
                if (isSet(this.options, 4)) {
                    i = context.start;
                    n2 = this.match(context, this.operations, context.start, 1, this.options);
                }
                else {
                    int n3 = 1;
                    for (i = context.start; i <= n; ++i) {
                        if (isEOLChar(source.setIndex(i))) {
                            n3 = 1;
                        }
                        else {
                            if (n3 != 0 && 0 <= (n2 = this.match(context, this.operations, i, 1, this.options))) {
                                break;
                            }
                            n3 = 0;
                        }
                    }
                }
            }
            else if (this.firstChar != null) {
                final RangeToken firstChar = this.firstChar;
                for (i = context.start; i <= n; ++i) {
                    int n4 = source.setIndex(i);
                    if (REUtil.isHighSurrogate(n4) && i + 1 < context.limit) {
                        n4 = REUtil.composeFromSurrogates(n4, source.setIndex(i + 1));
                    }
                    if (firstChar.match(n4)) {
                        if (0 <= (n2 = this.match(context, this.operations, i, 1, this.options))) {
                            break;
                        }
                    }
                }
            }
            else {
                for (i = context.start; i <= n; ++i) {
                    if (0 <= (n2 = this.match(context, this.operations, i, 1, this.options))) {
                        break;
                    }
                }
            }
            if (n2 >= 0) {
                if (context.match != null) {
                    context.match.setBeginning(0, i);
                    context.match.setEnd(0, n2);
                }
                context.setInUse(false);
                return true;
            }
            context.setInUse(false);
            return false;
        }
    }
    
    void prepare() {
        this.compile(this.tokentree);
        this.minlength = this.tokentree.getMinLength();
        this.firstChar = null;
        if (!isSet(this.options, 128) && !isSet(this.options, 512)) {
            final RangeToken range = Token.createRange();
            if (this.tokentree.analyzeFirstCharacter(range, this.options) == 1) {
                range.compactRanges();
                this.firstChar = range;
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
                this.fixedString = new String(new char[] { (char)this.operations.getData() });
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
        }
        else if (!isSet(this.options, 256) && !isSet(this.options, 512)) {
            final Token.FixedStringContainer fixedStringContainer = new Token.FixedStringContainer();
            this.tokentree.findFixedString(fixedStringContainer, this.options);
            this.fixedString = ((fixedStringContainer.token == null) ? null : fixedStringContainer.token.getString());
            this.fixedStringOptions = fixedStringContainer.options;
            if (this.fixedString != null && this.fixedString.length() < 2) {
                this.fixedString = null;
            }
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
            }
        }
    }
    
    private static final boolean isSet(final int n, final int n2) {
        return (n & n2) == n2;
    }
    
    public RegularExpression(final String s) throws ParseException {
        this(s, null);
    }
    
    public RegularExpression(final String s, final String s2) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(s, s2);
    }
    
    public RegularExpression(final String s, final String s2, final Locale locale) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(s, s2, locale);
    }
    
    public RegularExpression(final String s, final String s2, final Locale locale, final short n) {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.setPattern(s, s2, locale, n);
    }
    
    RegularExpression(final String regex, final Token tokentree, final int nofparen, final boolean hasBackReferences, final int options) {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.regex = regex;
        this.tokentree = tokentree;
        this.nofparen = nofparen;
        this.options = options;
        this.hasBackReferences = hasBackReferences;
    }
    
    public void setPattern(final String s) throws ParseException {
        this.setPattern(s, Locale.getDefault());
    }
    
    public void setPattern(final String s, final Locale locale) throws ParseException {
        this.setPattern(s, this.options, locale, (short)1);
    }
    
    private void setPattern(final String regex, final int options, final Locale locale, final short n) throws ParseException {
        this.regex = regex;
        this.options = options;
        final RegexParser regexParser = isSet(this.options, 512) ? new ParserForXMLSchema(locale, n) : new RegexParser(locale);
        this.tokentree = regexParser.parse(this.regex, this.options);
        this.nofparen = regexParser.parennumber;
        this.hasBackReferences = regexParser.hasBackReferences;
        this.operations = null;
        this.context = null;
    }
    
    public void setPattern(final String s, final String s2) throws ParseException {
        this.setPattern(s, s2, Locale.getDefault());
    }
    
    public void setPattern(final String s, final String s2, final Locale locale) throws ParseException {
        this.setPattern(s, REUtil.parseOptions(s2), locale, (short)1);
    }
    
    public void setPattern(final String s, final String s2, final Locale locale, final short n) throws ParseException {
        this.setPattern(s, REUtil.parseOptions(s2), locale, n);
    }
    
    public String getPattern() {
        return this.regex;
    }
    
    public String toString() {
        return this.tokentree.toString(this.options);
    }
    
    public String getOptions() {
        return REUtil.createOptionString(this.options);
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof RegularExpression)) {
            return false;
        }
        final RegularExpression regularExpression = (RegularExpression)o;
        return this.regex.equals(regularExpression.regex) && this.options == regularExpression.options;
    }
    
    boolean equals(final String s, final int n) {
        return this.regex.equals(s) && this.options == n;
    }
    
    public int hashCode() {
        return (this.regex + "/" + this.getOptions()).hashCode();
    }
    
    public int getNumberOfGroups() {
        return this.nofparen;
    }
    
    private static final int getWordType0(final char c, final int n) {
        if (!isSet(n, 64)) {
            if (isSet(n, 32)) {
                return Token.getRange("IsWord", true).match(c) ? 1 : 2;
            }
            return isWordChar(c) ? 1 : 2;
        }
        else {
            switch (Character.getType(c)) {
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
                    switch (c) {
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
    
    private static final boolean isEOLChar(final int n) {
        return n == 10 || n == 13 || n == 8232 || n == 8233;
    }
    
    private static final boolean isWordChar(final int n) {
        return n == 95 || (n >= 48 && n <= 122 && (n <= 57 || (n >= 65 && (n <= 90 || n >= 97))));
    }
    
    private static final boolean matchIgnoreCase(final int n, final int n2) {
        if (n == n2) {
            return true;
        }
        if (n > 65535 || n2 > 65535) {
            return false;
        }
        final char upperCase = Character.toUpperCase((char)n);
        final char upperCase2 = Character.toUpperCase((char)n2);
        return upperCase == upperCase2 || Character.toLowerCase(upperCase) == Character.toLowerCase(upperCase2);
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
        
        char charAt(final int n) {
            return this.target[n];
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final String s, final int n3) {
            return n >= 0 && n2 - n >= n3 && (b ? this.regionMatchesIgnoreCase(n, n2, s, n3) : this.regionMatches(n, n2, s, n3));
        }
        
        private final boolean regionMatches(int n, final int n2, final String s, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                if (this.target[n++] != s.charAt(n4++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int n, final int n2, final String s, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                final char c = this.target[n++];
                final char char1 = s.charAt(n4++);
                if (c == char1) {
                    continue;
                }
                final char upperCase = Character.toUpperCase(c);
                final char upperCase2 = Character.toUpperCase(char1);
                if (upperCase == upperCase2) {
                    continue;
                }
                if (Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                    return false;
                }
            }
            return true;
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final int n3, final int n4) {
            return n >= 0 && n2 - n >= n4 && (b ? this.regionMatchesIgnoreCase(n, n2, n3, n4) : this.regionMatches(n, n2, n3, n4));
        }
        
        private final boolean regionMatches(int n, final int n2, final int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                if (this.target[n++] != this.target[n5++]) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int n, final int n2, final int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                final char c = this.target[n++];
                final char c2 = this.target[n5++];
                if (c == c2) {
                    continue;
                }
                final char upperCase = Character.toUpperCase(c);
                final char upperCase2 = Character.toUpperCase(c2);
                if (upperCase == upperCase2) {
                    continue;
                }
                if (Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    abstract static class ExpressionTarget
    {
        abstract char charAt(final int p0);
        
        abstract boolean regionMatches(final boolean p0, final int p1, final int p2, final String p3, final int p4);
        
        abstract boolean regionMatches(final boolean p0, final int p1, final int p2, final int p3, final int p4);
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
        
        final char charAt(final int index) {
            return this.target.setIndex(index);
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final String s, final int n3) {
            return n >= 0 && n2 - n >= n3 && (b ? this.regionMatchesIgnoreCase(n, n2, s, n3) : this.regionMatches(n, n2, s, n3));
        }
        
        private final boolean regionMatches(int n, final int n2, final String s, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                if (this.target.setIndex(n++) != s.charAt(n4++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int n, final int n2, final String s, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                final char setIndex = this.target.setIndex(n++);
                final char char1 = s.charAt(n4++);
                if (setIndex == char1) {
                    continue;
                }
                final char upperCase = Character.toUpperCase(setIndex);
                final char upperCase2 = Character.toUpperCase(char1);
                if (upperCase == upperCase2) {
                    continue;
                }
                if (Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                    return false;
                }
            }
            return true;
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final int n3, final int n4) {
            return n >= 0 && n2 - n >= n4 && (b ? this.regionMatchesIgnoreCase(n, n2, n3, n4) : this.regionMatches(n, n2, n3, n4));
        }
        
        private final boolean regionMatches(int n, final int n2, final int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                if (this.target.setIndex(n++) != this.target.setIndex(n5++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int n, final int n2, final int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                final char setIndex = this.target.setIndex(n++);
                final char setIndex2 = this.target.setIndex(n5++);
                if (setIndex == setIndex2) {
                    continue;
                }
                final char upperCase = Character.toUpperCase(setIndex);
                final char upperCase2 = Character.toUpperCase(setIndex2);
                if (upperCase == upperCase2) {
                    continue;
                }
                if (Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
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
        
        boolean contains(final int n) {
            for (int i = 0; i < this.currentIndex; ++i) {
                if (this.offsets[i] == n) {
                    return true;
                }
            }
            return false;
        }
        
        void reset() {
            this.currentIndex = 0;
        }
        
        void addOffset(final int n) {
            if (this.currentIndex == this.offsets.length) {
                this.offsets = this.expandOffsets();
            }
            this.offsets[this.currentIndex++] = n;
        }
        
        private int[] expandOffsets() {
            final int[] array = new int[this.offsets.length << 1];
            System.arraycopy(this.offsets, 0, array, 0, this.currentIndex);
            return array;
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
        
        private void resetCommon(final int n) {
            this.length = this.limit - this.start;
            this.setInUse(true);
            this.match = null;
            if (this.closureContexts == null || this.closureContexts.length != n) {
                this.closureContexts = new ClosureContext[n];
            }
            for (int i = 0; i < n; ++i) {
                if (this.closureContexts[i] == null) {
                    this.closureContexts[i] = new ClosureContext();
                }
                else {
                    this.closureContexts[i].reset();
                }
            }
        }
        
        void reset(final CharacterIterator characterIterator, final int start, final int limit, final int n) {
            if (this.characterIteratorTarget == null) {
                this.characterIteratorTarget = new CharacterIteratorTarget(characterIterator);
            }
            else {
                this.characterIteratorTarget.resetTarget(characterIterator);
            }
            this.target = this.characterIteratorTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(n);
        }
        
        void reset(final String s, final int start, final int limit, final int n) {
            if (this.stringTarget == null) {
                this.stringTarget = new StringTarget(s);
            }
            else {
                this.stringTarget.resetTarget(s);
            }
            this.target = this.stringTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(n);
        }
        
        void reset(final char[] array, final int start, final int limit, final int n) {
            if (this.charArrayTarget == null) {
                this.charArrayTarget = new CharArrayTarget(array);
            }
            else {
                this.charArrayTarget.resetTarget(array);
            }
            this.target = this.charArrayTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(n);
        }
        
        synchronized void setInUse(final boolean inuse) {
            this.inuse = inuse;
        }
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
        
        final char charAt(final int n) {
            return this.target.charAt(n);
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final String s, final int n3) {
            return n2 - n >= n3 && (b ? this.target.regionMatches(true, n, s, 0, n3) : this.target.regionMatches(n, s, 0, n3));
        }
        
        final boolean regionMatches(final boolean b, final int n, final int n2, final int n3, final int n4) {
            return n2 - n >= n4 && (b ? this.target.regionMatches(true, n, this.target, n3, n4) : this.target.regionMatches(n, this.target, n3, n4));
        }
    }
}
