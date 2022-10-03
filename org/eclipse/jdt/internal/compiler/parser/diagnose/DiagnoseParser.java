package org.eclipse.jdt.internal.compiler.parser.diagnose;

import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.ConflictedParser;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.internal.compiler.parser.ParserBasicInformation;

public class DiagnoseParser implements ParserBasicInformation, TerminalTokens, ConflictedParser
{
    private static final boolean DEBUG = false;
    private boolean DEBUG_PARSECHECK;
    private static final int STACK_INCREMENT = 256;
    private static final int BEFORE_CODE = 2;
    private static final int INSERTION_CODE = 3;
    private static final int INVALID_CODE = 4;
    private static final int SUBSTITUTION_CODE = 5;
    private static final int DELETION_CODE = 6;
    private static final int MERGE_CODE = 7;
    private static final int MISPLACED_CODE = 8;
    private static final int SCOPE_CODE = 9;
    private static final int SECONDARY_CODE = 10;
    private static final int EOF_CODE = 11;
    private static final int BUFF_UBOUND = 31;
    private static final int BUFF_SIZE = 32;
    private static final int MAX_DISTANCE = 30;
    private static final int MIN_DISTANCE = 3;
    private CompilerOptions options;
    private LexStream lexStream;
    private int errorToken;
    private int errorTokenStart;
    private int currentToken;
    private int stackLength;
    private int stateStackTop;
    private int[] stack;
    private int[] locationStack;
    private int[] locationStartStack;
    private int tempStackTop;
    private int[] tempStack;
    private int prevStackTop;
    private int[] prevStack;
    private int nextStackTop;
    private int[] nextStack;
    private int scopeStackTop;
    private int[] scopeIndex;
    private int[] scopePosition;
    int[] list;
    int[] buffer;
    private static final int NIL = -1;
    int[] stateSeen;
    int statePoolTop;
    StateInfo[] statePool;
    private Parser parser;
    private RecoveryScanner recoveryScanner;
    private boolean reportProblem;
    
    public DiagnoseParser(final Parser parser, final int firstToken, final int start, final int end, final CompilerOptions options) {
        this(parser, firstToken, start, end, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, options);
    }
    
    public DiagnoseParser(final Parser parser, final int firstToken, final int start, final int end, final int[] intervalStartToSkip, final int[] intervalEndToSkip, final int[] intervalFlagsToSkip, final CompilerOptions options) {
        this.DEBUG_PARSECHECK = false;
        this.currentToken = 0;
        this.list = new int[479];
        this.buffer = new int[32];
        this.parser = parser;
        this.options = options;
        this.lexStream = new LexStream(32, parser.scanner, intervalStartToSkip, intervalEndToSkip, intervalFlagsToSkip, firstToken, start, end);
        this.recoveryScanner = parser.recoveryScanner;
    }
    
    private ProblemReporter problemReporter() {
        return this.parser.problemReporter();
    }
    
    private void reallocateStacks() {
        final int old_stack_length = this.stackLength;
        this.stackLength += 256;
        if (old_stack_length == 0) {
            this.stack = new int[this.stackLength];
            this.locationStack = new int[this.stackLength];
            this.locationStartStack = new int[this.stackLength];
            this.tempStack = new int[this.stackLength];
            this.prevStack = new int[this.stackLength];
            this.nextStack = new int[this.stackLength];
            this.scopeIndex = new int[this.stackLength];
            this.scopePosition = new int[this.stackLength];
        }
        else {
            System.arraycopy(this.stack, 0, this.stack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.locationStack, 0, this.locationStack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.locationStartStack, 0, this.locationStartStack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.tempStack, 0, this.tempStack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.prevStack, 0, this.prevStack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.nextStack, 0, this.nextStack = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.scopeIndex, 0, this.scopeIndex = new int[this.stackLength], 0, old_stack_length);
            System.arraycopy(this.scopePosition, 0, this.scopePosition = new int[this.stackLength], 0, old_stack_length);
        }
    }
    
    public void diagnoseParse(final boolean record) {
        this.reportProblem = true;
        boolean oldRecord = false;
        if (this.recoveryScanner != null) {
            oldRecord = this.recoveryScanner.record;
            this.recoveryScanner.record = record;
        }
        this.parser.scanner.setActiveParser(this);
        try {
            this.lexStream.reset();
            this.currentToken = this.lexStream.getToken();
            int act = 1580;
            this.reallocateStacks();
            this.stateStackTop = 0;
            this.stack[this.stateStackTop] = act;
            int tok = this.lexStream.kind(this.currentToken);
            this.locationStack[this.stateStackTop] = this.currentToken;
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
            boolean forceRecoveryAfterLBracketMissing = false;
            do {
                int prev_pos = -1;
                this.prevStackTop = -1;
                int next_pos = -1;
                this.nextStackTop = -1;
                int pos = this.stateStackTop;
                this.tempStackTop = this.stateStackTop - 1;
                for (int i = 0; i <= this.stateStackTop; ++i) {
                    this.tempStack[i] = this.stack[i];
                }
                for (act = Parser.tAction(act, tok); act <= 800; act = Parser.tAction(act, tok)) {
                    do {
                        this.tempStackTop -= Parser.rhs[act] - 1;
                        act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act]);
                    } while (act <= 800);
                    if (this.tempStackTop + 1 >= this.stackLength) {
                        this.reallocateStacks();
                    }
                    pos = ((pos < this.tempStackTop) ? pos : this.tempStackTop);
                    this.tempStack[this.tempStackTop + 1] = act;
                }
                while (act > 16382 || act < 16381) {
                    this.nextStackTop = this.tempStackTop + 1;
                    for (int i = next_pos + 1; i <= this.nextStackTop; ++i) {
                        this.nextStack[i] = this.tempStack[i];
                    }
                    for (int i = pos + 1; i <= this.nextStackTop; ++i) {
                        this.locationStack[i] = this.locationStack[this.stateStackTop];
                        this.locationStartStack[i] = this.locationStartStack[this.stateStackTop];
                    }
                    if (act > 16382) {
                        act -= 16382;
                        do {
                            this.nextStackTop -= Parser.rhs[act] - 1;
                            act = Parser.ntAction(this.nextStack[this.nextStackTop], Parser.lhs[act]);
                        } while (act <= 800);
                        pos = ((pos < this.nextStackTop) ? pos : this.nextStackTop);
                    }
                    if (this.nextStackTop + 1 >= this.stackLength) {
                        this.reallocateStacks();
                    }
                    this.tempStackTop = this.nextStackTop;
                    this.nextStack[++this.nextStackTop] = act;
                    next_pos = this.nextStackTop;
                    this.currentToken = this.lexStream.getToken();
                    for (tok = this.lexStream.kind(this.currentToken), act = Parser.tAction(act, tok); act <= 800; act = Parser.tAction(act, tok)) {
                        do {
                            final int lhs_symbol = Parser.lhs[act];
                            this.tempStackTop -= Parser.rhs[act] - 1;
                            act = ((this.tempStackTop > next_pos) ? this.tempStack[this.tempStackTop] : this.nextStack[this.tempStackTop]);
                            act = Parser.ntAction(act, lhs_symbol);
                        } while (act <= 800);
                        if (this.tempStackTop + 1 >= this.stackLength) {
                            this.reallocateStacks();
                        }
                        next_pos = ((next_pos < this.tempStackTop) ? next_pos : this.tempStackTop);
                        this.tempStack[this.tempStackTop + 1] = act;
                    }
                    if (act != 16382) {
                        this.prevStackTop = this.stateStackTop;
                        for (int i = prev_pos + 1; i <= this.prevStackTop; ++i) {
                            this.prevStack[i] = this.stack[i];
                        }
                        prev_pos = pos;
                        this.stateStackTop = this.nextStackTop;
                        for (int i = pos + 1; i <= this.stateStackTop; ++i) {
                            this.stack[i] = this.nextStack[i];
                        }
                        this.locationStack[this.stateStackTop] = this.currentToken;
                        this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
                        pos = next_pos;
                    }
                }
                if (act == 16382) {
                    final RepairCandidate candidate = this.errorRecovery(this.currentToken, forceRecoveryAfterLBracketMissing);
                    forceRecoveryAfterLBracketMissing = false;
                    if (this.parser.reportOnlyOneSyntaxError) {
                        return;
                    }
                    if (this.parser.problemReporter().options.maxProblemsPerUnit < this.parser.compilationUnit.compilationResult.problemCount) {
                        if (this.recoveryScanner == null || !this.recoveryScanner.record) {
                            return;
                        }
                        this.reportProblem = false;
                    }
                    act = this.stack[this.stateStackTop];
                    if (candidate.symbol == 0) {
                        break;
                    }
                    if (candidate.symbol > 118) {
                        final int lhs_symbol2 = candidate.symbol - 118;
                        for (act = Parser.ntAction(act, lhs_symbol2); act <= 800; act = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act])) {
                            this.stateStackTop -= Parser.rhs[act] - 1;
                        }
                        this.stack[++this.stateStackTop] = act;
                        this.currentToken = this.lexStream.getToken();
                        tok = this.lexStream.kind(this.currentToken);
                        this.locationStack[this.stateStackTop] = this.currentToken;
                        this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
                    }
                    else {
                        tok = candidate.symbol;
                        this.locationStack[this.stateStackTop] = candidate.location;
                        this.locationStartStack[this.stateStackTop] = this.lexStream.start(candidate.location);
                    }
                }
            } while (act != 16381);
        }
        finally {
            if (this.recoveryScanner != null) {
                this.recoveryScanner.record = oldRecord;
            }
            this.parser.scanner.setActiveParser(null);
        }
        if (this.recoveryScanner != null) {
            this.recoveryScanner.record = oldRecord;
        }
        this.parser.scanner.setActiveParser(null);
    }
    
    private static char[] displayEscapeCharacters(final char[] tokenSource, final int start, final int end) {
        final StringBuffer tokenSourceBuffer = new StringBuffer();
        for (int i = 0; i < start; ++i) {
            tokenSourceBuffer.append(tokenSource[i]);
        }
        for (int i = start; i < end; ++i) {
            final char c = tokenSource[i];
            Util.appendEscapedChar(tokenSourceBuffer, c, true);
        }
        for (int i = end; i < tokenSource.length; ++i) {
            tokenSourceBuffer.append(tokenSource[i]);
        }
        return tokenSourceBuffer.toString().toCharArray();
    }
    
    private RepairCandidate errorRecovery(final int error_token, final boolean forcedError) {
        this.errorToken = error_token;
        this.errorTokenStart = this.lexStream.start(error_token);
        final int prevtok = this.lexStream.previous(error_token);
        final int prevtokKind = this.lexStream.kind(prevtok);
        if (forcedError) {
            final int name_index = Parser.terminal_index[49];
            this.reportError(3, name_index, prevtok, prevtok);
            final RepairCandidate candidate = new RepairCandidate();
            candidate.symbol = 49;
            candidate.location = error_token;
            this.lexStream.reset(error_token);
            this.stateStackTop = this.nextStackTop;
            for (int j = 0; j <= this.stateStackTop; ++j) {
                this.stack[j] = this.nextStack[j];
            }
            this.locationStack[this.stateStackTop] = error_token;
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(error_token);
            return candidate;
        }
        RepairCandidate candidate2 = this.primaryPhase(error_token);
        if (candidate2.symbol != 0) {
            return candidate2;
        }
        candidate2 = this.secondaryPhase(error_token);
        if (candidate2.symbol != 0) {
            return candidate2;
        }
        if (this.lexStream.kind(error_token) == 60) {
            this.reportError(11, Parser.terminal_index[60], prevtok, prevtok);
            candidate2.symbol = 0;
            candidate2.location = error_token;
            return candidate2;
        }
        while (this.lexStream.kind(this.buffer[31]) != 60) {
            candidate2 = this.secondaryPhase(this.buffer[29]);
            if (candidate2.symbol != 0) {
                return candidate2;
            }
        }
        int i;
        for (i = 31; this.lexStream.kind(this.buffer[i]) == 60; --i) {}
        this.reportError(6, Parser.terminal_index[prevtokKind], error_token, this.buffer[i]);
        candidate2.symbol = 0;
        candidate2.location = this.buffer[i];
        return candidate2;
    }
    
    private RepairCandidate primaryPhase(final int error_token) {
        PrimaryRepairInfo repair = new PrimaryRepairInfo();
        final RepairCandidate candidate = new RepairCandidate();
        final int i = (this.nextStackTop >= 0) ? 3 : 2;
        this.buffer[i] = error_token;
        for (int j = i; j > 0; --j) {
            this.buffer[j - 1] = this.lexStream.previous(this.buffer[j]);
        }
        for (int k = i + 1; k < 32; ++k) {
            this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
        }
        if (this.nextStackTop >= 0) {
            repair.bufferPosition = 3;
            repair = this.checkPrimaryDistance(this.nextStack, this.nextStackTop, repair);
        }
        PrimaryRepairInfo new_repair = repair.copy();
        new_repair.bufferPosition = 2;
        new_repair = this.checkPrimaryDistance(this.stack, this.stateStackTop, new_repair);
        if (new_repair.distance > repair.distance || new_repair.misspellIndex > repair.misspellIndex) {
            repair = new_repair;
        }
        if (this.prevStackTop >= 0) {
            new_repair = repair.copy();
            new_repair.bufferPosition = 1;
            new_repair = this.checkPrimaryDistance(this.prevStack, this.prevStackTop, new_repair);
            if (new_repair.distance > repair.distance || new_repair.misspellIndex > repair.misspellIndex) {
                repair = new_repair;
            }
        }
        if (this.nextStackTop >= 0) {
            if (this.secondaryCheck(this.nextStack, this.nextStackTop, 3, repair.distance)) {
                return candidate;
            }
        }
        else if (this.secondaryCheck(this.stack, this.stateStackTop, 2, repair.distance)) {
            return candidate;
        }
        repair.distance = repair.distance - repair.bufferPosition + 1;
        if (repair.code == 4 || repair.code == 6 || repair.code == 5 || repair.code == 7) {
            final PrimaryRepairInfo primaryRepairInfo = repair;
            --primaryRepairInfo.distance;
        }
        if (repair.distance < 3) {
            return candidate;
        }
        if (repair.code == 3 && this.buffer[repair.bufferPosition - 1] == 0) {
            repair.code = 2;
        }
        if (repair.bufferPosition == 1) {
            this.stateStackTop = this.prevStackTop;
            for (int l = 0; l <= this.stateStackTop; ++l) {
                this.stack[l] = this.prevStack[l];
            }
        }
        else if (this.nextStackTop >= 0 && repair.bufferPosition >= 3) {
            this.stateStackTop = this.nextStackTop;
            for (int l = 0; l <= this.stateStackTop; ++l) {
                this.stack[l] = this.nextStack[l];
            }
            this.locationStack[this.stateStackTop] = this.buffer[3];
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.buffer[3]);
        }
        return this.primaryDiagnosis(repair);
    }
    
    private int mergeCandidate(final int state, final int buffer_position) {
        final char[] name1 = this.lexStream.name(this.buffer[buffer_position]);
        final char[] name2 = this.lexStream.name(this.buffer[buffer_position + 1]);
        final int len = name1.length + name2.length;
        final char[] str = CharOperation.concat(name1, name2);
        for (int k = Parser.asi(state); Parser.asr[k] != '\0'; ++k) {
            final int l = Parser.terminal_index[Parser.asr[k]];
            if (len == Parser.name[l].length()) {
                final char[] name3 = Parser.name[l].toCharArray();
                if (CharOperation.equals(str, name3, false)) {
                    return Parser.asr[k];
                }
            }
        }
        return 0;
    }
    
    private PrimaryRepairInfo checkPrimaryDistance(final int[] stck, final int stack_top, PrimaryRepairInfo repair) {
        final PrimaryRepairInfo scope_repair = this.scopeTrial(stck, stack_top, repair.copy());
        if (scope_repair.distance > repair.distance) {
            repair = scope_repair;
        }
        if (this.buffer[repair.bufferPosition] != 0 && this.buffer[repair.bufferPosition + 1] != 0) {
            final int symbol = this.mergeCandidate(stck[stack_top], repair.bufferPosition);
            if (symbol != 0) {
                final int j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 2);
                if (j > repair.distance || (j == repair.distance && repair.misspellIndex < 10)) {
                    repair.misspellIndex = 10;
                    repair.symbol = symbol;
                    repair.distance = j;
                    repair.code = 7;
                }
            }
        }
        int j = this.parseCheck(stck, stack_top, this.lexStream.kind(this.buffer[repair.bufferPosition + 1]), repair.bufferPosition + 2);
        int k;
        if (this.lexStream.kind(this.buffer[repair.bufferPosition]) == 60 && this.lexStream.afterEol(this.buffer[repair.bufferPosition + 1])) {
            k = 10;
        }
        else {
            k = 0;
        }
        if (j > repair.distance || (j == repair.distance && k > repair.misspellIndex)) {
            repair.misspellIndex = k;
            repair.code = 6;
            repair.distance = j;
        }
        int next_state = stck[stack_top];
        int max_pos = stack_top;
        this.tempStackTop = stack_top - 1;
        final int tok = this.lexStream.kind(this.buffer[repair.bufferPosition]);
        this.lexStream.reset(this.buffer[repair.bufferPosition + 1]);
        for (int act = Parser.tAction(next_state, tok); act <= 800; act = Parser.tAction(next_state, tok)) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                final int symbol = Parser.lhs[act];
                act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                act = Parser.ntAction(act, symbol);
            } while (act <= 800);
            max_pos = ((max_pos < this.tempStackTop) ? max_pos : this.tempStackTop);
            this.tempStack[this.tempStackTop + 1] = act;
            next_state = act;
        }
        int root = 0;
        for (int i = Parser.asi(next_state); Parser.asr[i] != '\0'; ++i) {
            final int symbol = Parser.asr[i];
            if (symbol != 60 && symbol != 118) {
                if (root == 0) {
                    this.list[symbol] = symbol;
                }
                else {
                    this.list[symbol] = this.list[root];
                    this.list[root] = symbol;
                }
                root = symbol;
            }
        }
        if (stck[stack_top] != next_state) {
            for (int i = Parser.asi(stck[stack_top]); Parser.asr[i] != '\0'; ++i) {
                final int symbol = Parser.asr[i];
                if (symbol != 60 && symbol != 118 && this.list[symbol] == 0) {
                    if (root == 0) {
                        this.list[symbol] = symbol;
                    }
                    else {
                        this.list[symbol] = this.list[root];
                        this.list[root] = symbol;
                    }
                    root = symbol;
                }
            }
        }
        int i = this.list[root];
        this.list[root] = 0;
        int symbol;
        for (root = (symbol = i); symbol != 0; symbol = this.list[symbol]) {
            if (symbol == 60 && this.lexStream.afterEol(this.buffer[repair.bufferPosition])) {
                k = 10;
            }
            else {
                k = 0;
            }
            j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition);
            if (j > repair.distance) {
                repair.misspellIndex = k;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            }
            else if (j == repair.distance && k > repair.misspellIndex) {
                repair.misspellIndex = k;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            }
        }
        symbol = root;
        if (this.buffer[repair.bufferPosition] != 0) {
            while (symbol != 0) {
                if (symbol == 60 && this.lexStream.afterEol(this.buffer[repair.bufferPosition + 1])) {
                    k = 10;
                }
                else {
                    k = this.misspell(symbol, this.buffer[repair.bufferPosition]);
                }
                j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
                if (j > repair.distance) {
                    repair.misspellIndex = k;
                    repair.distance = j;
                    repair.symbol = symbol;
                    repair.code = 5;
                }
                else if (j == repair.distance && k > repair.misspellIndex) {
                    repair.misspellIndex = k;
                    repair.symbol = symbol;
                    repair.code = 5;
                }
                i = symbol;
                symbol = this.list[symbol];
                this.list[i] = 0;
            }
        }
        for (i = Parser.nasi(stck[stack_top]); Parser.nasr[i] != '\0'; ++i) {
            symbol = Parser.nasr[i] + 'v';
            j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
            if (j > repair.distance) {
                repair.misspellIndex = 0;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 4;
            }
            j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition);
            if (j > repair.distance || (j == repair.distance && repair.code == 4)) {
                repair.misspellIndex = 0;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            }
        }
        return repair;
    }
    
    private RepairCandidate primaryDiagnosis(final PrimaryRepairInfo repair) {
        final int prevtok = this.buffer[repair.bufferPosition - 1];
        final int curtok = this.buffer[repair.bufferPosition];
        switch (repair.code) {
            case 2:
            case 3: {
                int name_index;
                if (repair.symbol > 118) {
                    name_index = this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition);
                }
                else {
                    name_index = this.getTermIndex(this.stack, this.stateStackTop, repair.symbol, repair.bufferPosition);
                }
                final int t = (repair.code == 3) ? prevtok : curtok;
                this.reportError(repair.code, name_index, t, t);
                break;
            }
            case 4: {
                final int name_index = this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition + 1);
                this.reportError(repair.code, name_index, curtok, curtok);
                break;
            }
            case 5: {
                int name_index;
                if (repair.misspellIndex >= 6) {
                    name_index = Parser.terminal_index[repair.symbol];
                }
                else {
                    name_index = this.getTermIndex(this.stack, this.stateStackTop, repair.symbol, repair.bufferPosition + 1);
                    if (name_index != Parser.terminal_index[repair.symbol]) {
                        repair.code = 4;
                    }
                }
                this.reportError(repair.code, name_index, curtok, curtok);
                break;
            }
            case 7: {
                this.reportError(repair.code, Parser.terminal_index[repair.symbol], curtok, this.lexStream.next(curtok));
                break;
            }
            case 9: {
                for (int i = 0; i < this.scopeStackTop; ++i) {
                    this.reportError(repair.code, -this.scopeIndex[i], this.locationStack[this.scopePosition[i]], prevtok, Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
                }
                repair.symbol = Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 'v';
                this.stateStackTop = this.scopePosition[this.scopeStackTop];
                this.reportError(repair.code, -this.scopeIndex[this.scopeStackTop], this.locationStack[this.scopePosition[this.scopeStackTop]], prevtok, this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition));
                break;
            }
            default: {
                this.reportError(repair.code, Parser.terminal_index[118], curtok, curtok);
                break;
            }
        }
        final RepairCandidate candidate = new RepairCandidate();
        switch (repair.code) {
            case 2:
            case 3:
            case 9: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition]);
                break;
            }
            case 4:
            case 5: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition + 1]);
                break;
            }
            case 7: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition + 2]);
                break;
            }
            default: {
                candidate.location = this.buffer[repair.bufferPosition + 1];
                candidate.symbol = this.lexStream.kind(this.buffer[repair.bufferPosition + 1]);
                this.lexStream.reset(this.buffer[repair.bufferPosition + 2]);
                break;
            }
        }
        return candidate;
    }
    
    private int getTermIndex(final int[] stck, final int stack_top, int tok, final int buffer_position) {
        int act = stck[stack_top];
        int max_pos = stack_top;
        int highest_symbol = tok;
        this.tempStackTop = stack_top - 1;
        this.lexStream.reset(this.buffer[buffer_position]);
        for (act = Parser.tAction(act, tok); act <= 800; act = Parser.tAction(act, tok)) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                final int lhs_symbol = Parser.lhs[act];
                act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                act = Parser.ntAction(act, lhs_symbol);
            } while (act <= 800);
            max_pos = ((max_pos < this.tempStackTop) ? max_pos : this.tempStackTop);
            this.tempStack[this.tempStackTop + 1] = act;
        }
        ++this.tempStackTop;
        final int threshold = this.tempStackTop;
        tok = this.lexStream.kind(this.buffer[buffer_position]);
        this.lexStream.reset(this.buffer[buffer_position + 1]);
        if (act > 16382) {
            act -= 16382;
        }
        else {
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        while (act <= 800) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.tempStackTop < threshold) {
                    return (highest_symbol > 118) ? Parser.non_terminal_index[highest_symbol - 118] : Parser.terminal_index[highest_symbol];
                }
                final int lhs_symbol2 = Parser.lhs[act];
                if (this.tempStackTop == threshold) {
                    highest_symbol = lhs_symbol2 + 118;
                }
                act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                act = Parser.ntAction(act, lhs_symbol2);
            } while (act <= 800);
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        return (highest_symbol > 118) ? Parser.non_terminal_index[highest_symbol - 118] : Parser.terminal_index[highest_symbol];
    }
    
    private int getNtermIndex(final int start, final int sym, final int buffer_position) {
        int highest_symbol = sym - 118;
        final int tok = this.lexStream.kind(this.buffer[buffer_position]);
        this.lexStream.reset(this.buffer[buffer_position + 1]);
        this.tempStackTop = 0;
        this.tempStack[this.tempStackTop] = start;
        int act = Parser.ntAction(start, highest_symbol);
        if (act > 800) {
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        while (act <= 800) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.tempStackTop < 0) {
                    return Parser.non_terminal_index[highest_symbol];
                }
                if (this.tempStackTop == 0) {
                    highest_symbol = Parser.lhs[act];
                }
                act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act]);
            } while (act <= 800);
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        return Parser.non_terminal_index[highest_symbol];
    }
    
    private int misspell(final int sym, final int tok) {
        final char[] name = Parser.name[Parser.terminal_index[sym]].toCharArray();
        final int n = name.length;
        final char[] s1 = new char[n + 1];
        for (int k = 0; k < n; ++k) {
            final char c = name[k];
            s1[k] = ScannerHelper.toLowerCase(c);
        }
        s1[n] = '\0';
        final char[] tokenName = this.lexStream.name(tok);
        final int len = tokenName.length;
        final int m = (len < 41) ? len : 41;
        final char[] s2 = new char[m + 1];
        for (int i = 0; i < m; ++i) {
            final char c2 = tokenName[i];
            s2[i] = ScannerHelper.toLowerCase(c2);
        }
        s2[m] = '\0';
        if (n == 1 && m == 1 && ((s1[0] == ';' && s2[0] == ',') || (s1[0] == ',' && s2[0] == ';') || (s1[0] == ';' && s2[0] == ':') || (s1[0] == ':' && s2[0] == ';') || (s1[0] == '.' && s2[0] == ',') || (s1[0] == ',' && s2[0] == '.') || (s1[0] == '\'' && s2[0] == '\"') || (s1[0] == '\"' && s2[0] == '\''))) {
            return 3;
        }
        int count = 0;
        int prefix_length = 0;
        int num_errors = 0;
        int j = 0;
        int l = 0;
        while (j < n && l < m) {
            if (s1[j] == s2[l]) {
                ++count;
                ++j;
                ++l;
                if (num_errors != 0) {
                    continue;
                }
                ++prefix_length;
            }
            else if (s1[j + 1] == s2[l] && s1[j] == s2[l + 1]) {
                count += 2;
                j += 2;
                l += 2;
                ++num_errors;
            }
            else if (s1[j + 1] == s2[l + 1]) {
                ++j;
                ++l;
                ++num_errors;
            }
            else {
                if (n - j > m - l) {
                    ++j;
                }
                else if (m - l > n - j) {
                    ++l;
                }
                else {
                    ++j;
                    ++l;
                }
                ++num_errors;
            }
        }
        if (j < n || l < m) {
            ++num_errors;
        }
        if (num_errors > ((n < m) ? n : m) / 6 + 1) {
            count = prefix_length;
        }
        return count * 10 / (((n < len) ? len : n) + num_errors);
    }
    
    private PrimaryRepairInfo scopeTrial(final int[] stck, final int stack_top, final PrimaryRepairInfo repair) {
        this.stateSeen = new int[this.stackLength];
        for (int i = 0; i < this.stackLength; ++i) {
            this.stateSeen[i] = -1;
        }
        this.statePoolTop = 0;
        this.statePool = new StateInfo[this.stackLength];
        this.scopeTrialCheck(stck, stack_top, repair, 0);
        this.stateSeen = null;
        this.statePoolTop = 0;
        repair.code = 9;
        repair.misspellIndex = 10;
        return repair;
    }
    
    private void scopeTrialCheck(final int[] stck, final int stack_top, final PrimaryRepairInfo repair, final int indx) {
        if (indx > 20) {
            return;
        }
        int act = stck[stack_top];
        for (int i = this.stateSeen[stack_top]; i != -1; i = this.statePool[i].next) {
            if (this.statePool[i].state == act) {
                return;
            }
        }
        final int old_state_pool_top = this.statePoolTop++;
        if (this.statePoolTop >= this.statePool.length) {
            System.arraycopy(this.statePool, 0, this.statePool = new StateInfo[this.statePoolTop * 2], 0, this.statePoolTop);
        }
        this.statePool[old_state_pool_top] = new StateInfo(act, this.stateSeen[stack_top]);
        this.stateSeen[stack_top] = old_state_pool_top;
    Label_0766:
        for (int j = 0; j < 291; ++j) {
            act = stck[stack_top];
            this.tempStackTop = stack_top - 1;
            int max_pos = stack_top;
            final int tok = Parser.scope_la[j];
            this.lexStream.reset(this.buffer[repair.bufferPosition]);
            for (act = Parser.tAction(act, tok); act <= 800; act = Parser.tAction(act, tok)) {
                do {
                    this.tempStackTop -= Parser.rhs[act] - 1;
                    final int lhs_symbol = Parser.lhs[act];
                    act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                    act = Parser.ntAction(act, lhs_symbol);
                } while (act <= 800);
                if (this.tempStackTop + 1 >= this.stackLength) {
                    return;
                }
                max_pos = ((max_pos < this.tempStackTop) ? max_pos : this.tempStackTop);
                this.tempStack[this.tempStackTop + 1] = act;
            }
            if (act != 16382) {
                int k;
                int l;
                for (k = Parser.scope_prefix[j], l = this.tempStackTop + 1; l >= max_pos + 1 && Parser.in_symbol(this.tempStack[l]) == Parser.scope_rhs[k]; ++k, --l) {}
                if (l == max_pos) {
                    for (l = max_pos; l >= 1 && Parser.in_symbol(stck[l]) == Parser.scope_rhs[k]; ++k, --l) {}
                }
                final int marked_pos = (max_pos < stack_top) ? (max_pos + 1) : stack_top;
                if (Parser.scope_rhs[k] == '\0' && l < marked_pos) {
                    int stack_position;
                    for (stack_position = l, l = Parser.scope_state_set[j]; stck[stack_position] != Parser.scope_state[l] && Parser.scope_state[l] != '\0'; ++l) {}
                    if (Parser.scope_state[l] != '\0') {
                        final int previous_distance = repair.distance;
                        final int distance = this.parseCheck(stck, stack_position, Parser.scope_lhs[j] + 'v', repair.bufferPosition);
                        if (distance - repair.bufferPosition + 1 < 3) {
                            int top;
                            for (top = stack_position, act = Parser.ntAction(stck[top], Parser.scope_lhs[j]); act <= 800; act = Parser.ntAction(stck[top], Parser.lhs[act])) {
                                if (Parser.rules_compliance[act] > this.options.sourceLevel) {
                                    continue Label_0766;
                                }
                                top -= Parser.rhs[act] - 1;
                            }
                            ++top;
                            l = act;
                            act = stck[top];
                            stck[top] = l;
                            this.scopeTrialCheck(stck, top, repair, indx + 1);
                            stck[top] = act;
                        }
                        else if (distance > repair.distance) {
                            this.scopeStackTop = indx;
                            repair.distance = distance;
                        }
                        if (this.lexStream.kind(this.buffer[repair.bufferPosition]) == 60 && repair.distance == previous_distance) {
                            this.scopeStackTop = indx;
                            repair.distance = 30;
                        }
                        if (repair.distance > previous_distance) {
                            this.scopeIndex[indx] = j;
                            this.scopePosition[indx] = stack_position;
                            return;
                        }
                    }
                }
            }
        }
    }
    
    private boolean secondaryCheck(final int[] stck, final int stack_top, final int buffer_position, final int distance) {
        for (int top = stack_top - 1; top >= 0; --top) {
            final int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[buffer_position]), buffer_position + 1);
            if (j - buffer_position + 1 > 3 && j > distance) {
                return true;
            }
        }
        PrimaryRepairInfo repair = new PrimaryRepairInfo();
        repair.bufferPosition = buffer_position + 1;
        repair.distance = distance;
        repair = this.scopeTrial(stck, stack_top, repair);
        return repair.distance - buffer_position > 3 && repair.distance > distance;
    }
    
    private RepairCandidate secondaryPhase(final int error_token) {
        SecondaryRepairInfo repair = new SecondaryRepairInfo();
        SecondaryRepairInfo misplaced = new SecondaryRepairInfo();
        final RepairCandidate candidate = new RepairCandidate();
        int next_last_index = 0;
        candidate.symbol = 0;
        repair.code = 0;
        repair.distance = 0;
        repair.recoveryOnNextStack = false;
        misplaced.distance = 0;
        misplaced.recoveryOnNextStack = false;
        if (this.nextStackTop >= 0) {
            this.buffer[2] = error_token;
            this.buffer[1] = this.lexStream.previous(this.buffer[2]);
            this.buffer[0] = this.lexStream.previous(this.buffer[1]);
            for (int k = 3; k < 31; ++k) {
                this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
            }
            this.buffer[31] = this.lexStream.badtoken();
            for (next_last_index = 29; next_last_index >= 1 && this.lexStream.kind(this.buffer[next_last_index]) == 60; --next_last_index) {}
            ++next_last_index;
            final int save_location = this.locationStack[this.nextStackTop];
            final int save_location_start = this.locationStartStack[this.nextStackTop];
            this.locationStack[this.nextStackTop] = this.buffer[2];
            this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
            misplaced.numDeletions = this.nextStackTop;
            misplaced = this.misplacementRecovery(this.nextStack, this.nextStackTop, next_last_index, misplaced, true);
            if (misplaced.recoveryOnNextStack) {
                final SecondaryRepairInfo secondaryRepairInfo = misplaced;
                ++secondaryRepairInfo.distance;
            }
            repair.numDeletions = this.nextStackTop + 31;
            repair = this.secondaryRecovery(this.nextStack, this.nextStackTop, next_last_index, repair, true);
            if (repair.recoveryOnNextStack) {
                final SecondaryRepairInfo secondaryRepairInfo2 = repair;
                ++secondaryRepairInfo2.distance;
            }
            this.locationStack[this.nextStackTop] = save_location;
            this.locationStartStack[this.nextStackTop] = save_location_start;
        }
        else {
            misplaced.numDeletions = this.stateStackTop;
            repair.numDeletions = this.stateStackTop + 31;
        }
        this.buffer[3] = error_token;
        this.buffer[2] = this.lexStream.previous(this.buffer[3]);
        this.buffer[1] = this.lexStream.previous(this.buffer[2]);
        this.buffer[0] = this.lexStream.previous(this.buffer[1]);
        for (int k = 4; k < 32; ++k) {
            this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
        }
        int last_index;
        for (last_index = 29; last_index >= 1 && this.lexStream.kind(this.buffer[last_index]) == 60; --last_index) {}
        ++last_index;
        misplaced = this.misplacementRecovery(this.stack, this.stateStackTop, last_index, misplaced, false);
        repair = this.secondaryRecovery(this.stack, this.stateStackTop, last_index, repair, false);
        if (misplaced.distance > 3 && (misplaced.numDeletions <= repair.numDeletions || misplaced.distance - misplaced.numDeletions >= repair.distance - repair.numDeletions)) {
            repair.code = 8;
            repair.stackPosition = misplaced.stackPosition;
            repair.bufferPosition = 2;
            repair.numDeletions = misplaced.numDeletions;
            repair.distance = misplaced.distance;
            repair.recoveryOnNextStack = misplaced.recoveryOnNextStack;
        }
        if (repair.recoveryOnNextStack) {
            this.stateStackTop = this.nextStackTop;
            for (int i = 0; i <= this.stateStackTop; ++i) {
                this.stack[i] = this.nextStack[i];
            }
            this.buffer[2] = error_token;
            this.buffer[1] = this.lexStream.previous(this.buffer[2]);
            this.buffer[0] = this.lexStream.previous(this.buffer[1]);
            for (int k = 3; k < 31; ++k) {
                this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
            }
            this.buffer[31] = this.lexStream.badtoken();
            this.locationStack[this.nextStackTop] = this.buffer[2];
            this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
            last_index = next_last_index;
        }
        if (repair.code == 10 || repair.code == 6) {
            PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
            scope_repair.distance = 0;
            scope_repair.bufferPosition = 2;
            while (scope_repair.bufferPosition <= repair.bufferPosition && repair.code != 9) {
                scope_repair = this.scopeTrial(this.stack, this.stateStackTop, scope_repair);
                final int j = (scope_repair.distance == 30) ? last_index : scope_repair.distance;
                final int k = scope_repair.bufferPosition - 1;
                if (j - k > 3 && j - k > repair.distance - repair.numDeletions) {
                    repair.code = 9;
                    final int i = this.scopeIndex[this.scopeStackTop];
                    repair.symbol = Parser.scope_lhs[i] + 'v';
                    repair.stackPosition = this.stateStackTop;
                    repair.bufferPosition = scope_repair.bufferPosition;
                }
                final PrimaryRepairInfo primaryRepairInfo = scope_repair;
                ++primaryRepairInfo.bufferPosition;
            }
        }
        if (repair.code == 0 && this.lexStream.kind(this.buffer[last_index]) == 60) {
            PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
            scope_repair.bufferPosition = last_index;
            scope_repair.distance = 0;
            for (int top = this.stateStackTop; top >= 0 && repair.code == 0; --top) {
                scope_repair = this.scopeTrial(this.stack, top, scope_repair);
                if (scope_repair.distance > 0) {
                    repair.code = 9;
                    final int i = this.scopeIndex[this.scopeStackTop];
                    repair.symbol = Parser.scope_lhs[i] + 'v';
                    repair.stackPosition = top;
                    repair.bufferPosition = scope_repair.bufferPosition;
                }
            }
        }
        if (repair.code == 0) {
            return candidate;
        }
        this.secondaryDiagnosis(repair);
        switch (repair.code) {
            case 8: {
                candidate.location = this.buffer[2];
                candidate.symbol = this.lexStream.kind(this.buffer[2]);
                this.lexStream.reset(this.lexStream.next(this.buffer[2]));
                break;
            }
            case 6: {
                candidate.location = this.buffer[repair.bufferPosition];
                candidate.symbol = this.lexStream.kind(this.buffer[repair.bufferPosition]);
                this.lexStream.reset(this.lexStream.next(this.buffer[repair.bufferPosition]));
                break;
            }
            default: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition]);
                break;
            }
        }
        return candidate;
    }
    
    private SecondaryRepairInfo misplacementRecovery(final int[] stck, final int stack_top, final int last_index, final SecondaryRepairInfo repair, final boolean stack_flag) {
        int previous_loc = this.buffer[2];
        int stack_deletions = 0;
        for (int top = stack_top - 1; top >= 0; --top) {
            if (this.locationStack[top] < previous_loc) {
                ++stack_deletions;
            }
            previous_loc = this.locationStack[top];
            int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[2]), 3);
            if (j == 30) {
                j = last_index;
            }
            if (j > 3 && j - stack_deletions > repair.distance - repair.numDeletions) {
                repair.stackPosition = top;
                repair.distance = j;
                repair.numDeletions = stack_deletions;
                repair.recoveryOnNextStack = stack_flag;
            }
        }
        return repair;
    }
    
    private SecondaryRepairInfo secondaryRecovery(final int[] stck, final int stack_top, final int last_index, final SecondaryRepairInfo repair, final boolean stack_flag) {
        int stack_deletions = 0;
        int previous_loc = this.buffer[2];
        for (int top = stack_top; top >= 0 && repair.numDeletions >= stack_deletions; --top) {
            if (this.locationStack[top] < previous_loc) {
                ++stack_deletions;
            }
            previous_loc = this.locationStack[top];
            for (int i = 2; i <= last_index - 3 + 1 && repair.numDeletions >= stack_deletions + i - 1; ++i) {
                int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[i]), i + 1);
                if (j == 30) {
                    j = last_index;
                }
                if (j - i + 1 > 3) {
                    final int k = stack_deletions + i - 1;
                    if (k < repair.numDeletions || j - k > repair.distance - repair.numDeletions || (repair.code == 10 && j - k == repair.distance - repair.numDeletions)) {
                        repair.code = 6;
                        repair.distance = j;
                        repair.stackPosition = top;
                        repair.bufferPosition = i;
                        repair.numDeletions = k;
                        repair.recoveryOnNextStack = stack_flag;
                    }
                }
                for (int l = Parser.nasi(stck[top]); l >= 0 && Parser.nasr[l] != '\0'; ++l) {
                    final int symbol = Parser.nasr[l] + 'v';
                    j = this.parseCheck(stck, top, symbol, i);
                    if (j == 30) {
                        j = last_index;
                    }
                    if (j - i + 1 > 3) {
                        final int m = stack_deletions + i - 1;
                        if (m < repair.numDeletions || j - m > repair.distance - repair.numDeletions) {
                            repair.code = 10;
                            repair.symbol = symbol;
                            repair.distance = j;
                            repair.stackPosition = top;
                            repair.bufferPosition = i;
                            repair.numDeletions = m;
                            repair.recoveryOnNextStack = stack_flag;
                        }
                    }
                }
            }
        }
        return repair;
    }
    
    private void secondaryDiagnosis(final SecondaryRepairInfo repair) {
        switch (repair.code) {
            case 9: {
                if (repair.stackPosition < this.stateStackTop) {
                    this.reportError(6, Parser.terminal_index[118], this.locationStack[repair.stackPosition], this.buffer[1]);
                }
                for (int i = 0; i < this.scopeStackTop; ++i) {
                    this.reportError(9, -this.scopeIndex[i], this.locationStack[this.scopePosition[i]], this.buffer[1], Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
                }
                repair.symbol = Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 'v';
                this.stateStackTop = this.scopePosition[this.scopeStackTop];
                this.reportError(9, -this.scopeIndex[this.scopeStackTop], this.locationStack[this.scopePosition[this.scopeStackTop]], this.buffer[1], this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition));
                break;
            }
            default: {
                this.reportError(repair.code, (repair.code == 10) ? this.getNtermIndex(this.stack[repair.stackPosition], repair.symbol, repair.bufferPosition) : Parser.terminal_index[118], this.locationStack[repair.stackPosition], this.buffer[repair.bufferPosition - 1]);
                this.stateStackTop = repair.stackPosition;
                break;
            }
        }
    }
    
    private int parseCheck(final int[] stck, final int stack_top, final int first_token, final int buffer_position) {
        int act = stck[stack_top];
        int max_pos;
        int indx;
        int ct;
        if (first_token > 118) {
            this.tempStackTop = stack_top;
            if (this.DEBUG_PARSECHECK) {
                System.out.println(this.tempStackTop);
            }
            max_pos = stack_top;
            indx = buffer_position;
            ct = this.lexStream.kind(this.buffer[indx]);
            this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
            int lhs_symbol = first_token - 118;
            act = Parser.ntAction(act, lhs_symbol);
            if (act <= 800) {
                do {
                    this.tempStackTop -= Parser.rhs[act] - 1;
                    if (this.DEBUG_PARSECHECK) {
                        System.out.print(this.tempStackTop);
                        System.out.print(" (");
                        System.out.print(-(Parser.rhs[act] - 1));
                        System.out.print(") [max:");
                        System.out.print(max_pos);
                        System.out.print("]\tprocess_non_terminal\t");
                        System.out.print(act);
                        System.out.print("\t");
                        System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
                        System.out.println();
                    }
                    if (Parser.rules_compliance[act] > this.options.sourceLevel) {
                        return 0;
                    }
                    lhs_symbol = Parser.lhs[act];
                    act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                    act = Parser.ntAction(act, lhs_symbol);
                } while (act <= 800);
                max_pos = ((max_pos < this.tempStackTop) ? max_pos : this.tempStackTop);
            }
        }
        else {
            this.tempStackTop = stack_top - 1;
            if (this.DEBUG_PARSECHECK) {
                System.out.println(this.tempStackTop);
            }
            max_pos = this.tempStackTop;
            indx = buffer_position - 1;
            ct = first_token;
            this.lexStream.reset(this.buffer[buffer_position]);
        }
        while (true) {
            if (this.DEBUG_PARSECHECK) {
                System.out.print(this.tempStackTop + 1);
                System.out.print(" (+1) [max:");
                System.out.print(max_pos);
                System.out.print("]\tprocess_terminal    \t");
                System.out.print(ct);
                System.out.print("\t");
                System.out.print(Parser.name[Parser.terminal_index[ct]]);
                System.out.println();
            }
            if (++this.tempStackTop >= this.stackLength) {
                return indx;
            }
            this.tempStack[this.tempStackTop] = act;
            act = Parser.tAction(act, ct);
            if (act <= 800) {
                --this.tempStackTop;
                if (this.DEBUG_PARSECHECK) {
                    System.out.print(this.tempStackTop);
                    System.out.print(" (-1) [max:");
                    System.out.print(max_pos);
                    System.out.print("]\treduce");
                    System.out.println();
                }
            }
            else if (act < 16381 || act > 16382) {
                if (indx == 30) {
                    return indx;
                }
                ++indx;
                ct = this.lexStream.kind(this.buffer[indx]);
                this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
                if (act > 16382) {
                    act -= 16382;
                    if (this.DEBUG_PARSECHECK) {
                        System.out.print(this.tempStackTop);
                        System.out.print("\tshift reduce");
                        System.out.println();
                    }
                }
                else {
                    if (this.DEBUG_PARSECHECK) {
                        System.out.println("\tshift");
                        continue;
                    }
                    continue;
                }
            }
            else {
                if (act == 16381) {
                    return 30;
                }
                return indx;
            }
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.DEBUG_PARSECHECK) {
                    System.out.print(this.tempStackTop);
                    System.out.print(" (");
                    System.out.print(-(Parser.rhs[act] - 1));
                    System.out.print(") [max:");
                    System.out.print(max_pos);
                    System.out.print("]\tprocess_non_terminal\t");
                    System.out.print(act);
                    System.out.print("\t");
                    System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
                    System.out.println();
                }
                if (act <= 800 && Parser.rules_compliance[act] > this.options.sourceLevel) {
                    return 0;
                }
                final int lhs_symbol = Parser.lhs[act];
                act = ((this.tempStackTop > max_pos) ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop]);
                act = Parser.ntAction(act, lhs_symbol);
            } while (act <= 800);
            max_pos = ((max_pos < this.tempStackTop) ? max_pos : this.tempStackTop);
        }
    }
    
    private void reportError(final int msgCode, final int nameIndex, final int leftToken, final int rightToken) {
        this.reportError(msgCode, nameIndex, leftToken, rightToken, 0);
    }
    
    private void reportError(final int msgCode, final int nameIndex, final int leftToken, final int rightToken, final int scopeNameIndex) {
        final int lToken = (leftToken > rightToken) ? rightToken : leftToken;
        if (lToken < rightToken) {
            this.reportSecondaryError(msgCode, nameIndex, lToken, rightToken, scopeNameIndex);
        }
        else {
            this.reportPrimaryError(msgCode, nameIndex, rightToken, scopeNameIndex);
        }
    }
    
    private void reportPrimaryError(final int msgCode, final int nameIndex, final int token, final int scopeNameIndex) {
        String name;
        if (nameIndex >= 0) {
            name = Parser.readableName[nameIndex];
        }
        else {
            name = Util.EMPTY_STRING;
        }
        final int errorStart = this.lexStream.start(token);
        final int errorEnd = this.lexStream.end(token);
        final int currentKind = this.lexStream.kind(token);
        final String errorTokenName = Parser.name[Parser.terminal_index[this.lexStream.kind(token)]];
        char[] errorTokenSource = this.lexStream.name(token);
        if (currentKind == 48) {
            errorTokenSource = displayEscapeCharacters(errorTokenSource, 1, errorTokenSource.length - 1);
        }
        int addedToken = -1;
        if (this.recoveryScanner != null && nameIndex >= 0) {
            addedToken = Parser.reverse_index[nameIndex];
        }
        switch (msgCode) {
            case 2: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.insertToken(addedToken, -1, errorStart);
                    }
                    else {
                        final int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.insertTokens(template, -1, errorStart);
                        }
                    }
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorInsertBeforeToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                    break;
                }
                break;
            }
            case 3: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.insertToken(addedToken, -1, errorEnd);
                    }
                    else {
                        final int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.insertTokens(template, -1, errorEnd);
                        }
                    }
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorInsertAfterToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                    break;
                }
                break;
            }
            case 6: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorDeleteToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName);
                    break;
                }
                break;
            }
            case 4: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                        break;
                    }
                    break;
                }
                else {
                    if (this.recoveryScanner != null) {
                        if (addedToken > -1) {
                            this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                        }
                        else {
                            final int[] template = this.getNTermTemplate(-addedToken);
                            if (template != null) {
                                this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                            }
                        }
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorInvalidToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                        break;
                    }
                    break;
                }
                break;
            }
            case 5: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    }
                    else {
                        final int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                    break;
                }
                break;
            }
            case 9: {
                final StringBuffer buf = new StringBuffer();
                int[] addedTokens = null;
                int addedTokenCount = 0;
                if (this.recoveryScanner != null) {
                    addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[-nameIndex]];
                }
                int insertedToken = 0;
                for (int i = Parser.scope_suffix[-nameIndex]; Parser.scope_rhs[i] != '\0'; ++i) {
                    buf.append(Parser.readableName[Parser.scope_rhs[i]]);
                    if (Parser.scope_rhs[i + 1] != '\0') {
                        buf.append(' ');
                    }
                    else {
                        insertedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                    }
                    if (addedTokens != null) {
                        final int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                        if (tmpAddedToken > -1) {
                            final int length = addedTokens.length;
                            if (addedTokenCount == length) {
                                System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
                            }
                            addedTokens[addedTokenCount++] = tmpAddedToken;
                        }
                        else {
                            final int[] template2 = this.getNTermTemplate(-tmpAddedToken);
                            if (template2 != null) {
                                for (int j = 0; j < template2.length; ++j) {
                                    final int length2 = addedTokens.length;
                                    if (addedTokenCount == length2) {
                                        System.arraycopy(addedTokens, 0, addedTokens = new int[length2 * 2], 0, length2);
                                    }
                                    addedTokens[addedTokenCount++] = template2[j];
                                }
                            }
                            else {
                                addedTokenCount = 0;
                                addedTokens = null;
                            }
                        }
                    }
                }
                if (addedTokenCount > 0) {
                    System.arraycopy(addedTokens, 0, addedTokens = new int[addedTokenCount], 0, addedTokenCount);
                    int completedToken = -1;
                    if (scopeNameIndex != 0) {
                        completedToken = -Parser.reverse_index[scopeNameIndex];
                    }
                    this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
                }
                if (scopeNameIndex != 0) {
                    if (insertedToken == 66) {
                        break;
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorInsertToComplete(errorStart, errorEnd, buf.toString(), Parser.readableName[scopeNameIndex]);
                        break;
                    }
                    break;
                }
                else {
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorInsertToCompleteScope(errorStart, errorEnd, buf.toString());
                        break;
                    }
                    break;
                }
                break;
            }
            case 11: {
                if (this.reportProblem) {
                    this.problemReporter().parseErrorUnexpectedEnd(errorStart, errorEnd);
                    break;
                }
                break;
            }
            case 7: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    }
                    else {
                        final int[] template3 = this.getNTermTemplate(-addedToken);
                        if (template3 != null) {
                            this.recoveryScanner.replaceTokens(template3, errorStart, errorEnd);
                        }
                    }
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorMergeTokens(errorStart, errorEnd, name);
                    break;
                }
                break;
            }
            case 8: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorMisplacedConstruct(errorStart, errorEnd);
                    break;
                }
                break;
            }
            default: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorNoSuggestion(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName);
                        break;
                    }
                    break;
                }
                else {
                    if (this.recoveryScanner != null) {
                        if (addedToken > -1) {
                            this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                        }
                        else {
                            final int[] template3 = this.getNTermTemplate(-addedToken);
                            if (template3 != null) {
                                this.recoveryScanner.replaceTokens(template3, errorStart, errorEnd);
                            }
                        }
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    private void reportSecondaryError(final int msgCode, final int nameIndex, final int leftToken, final int rightToken, final int scopeNameIndex) {
        String name;
        if (nameIndex >= 0) {
            name = Parser.readableName[nameIndex];
        }
        else {
            name = Util.EMPTY_STRING;
        }
        int errorStart = -1;
        if (this.lexStream.isInsideStream(leftToken)) {
            if (leftToken == 0) {
                errorStart = this.lexStream.start(leftToken + 1);
            }
            else {
                errorStart = this.lexStream.start(leftToken);
            }
        }
        else {
            if (leftToken == this.errorToken) {
                errorStart = this.errorTokenStart;
            }
            else {
                for (int i = 0; i <= this.stateStackTop; ++i) {
                    if (this.locationStack[i] == leftToken) {
                        errorStart = this.locationStartStack[i];
                    }
                }
            }
            if (errorStart == -1) {
                errorStart = this.lexStream.start(rightToken);
            }
        }
        final int errorEnd = this.lexStream.end(rightToken);
        int addedToken = -1;
        if (this.recoveryScanner != null && nameIndex >= 0) {
            addedToken = Parser.reverse_index[nameIndex];
        }
        switch (msgCode) {
            case 8: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorMisplacedConstruct(errorStart, errorEnd);
                    break;
                }
                break;
            }
            case 9: {
                errorStart = this.lexStream.start(rightToken);
                final StringBuffer buf = new StringBuffer();
                int[] addedTokens = null;
                int addedTokenCount = 0;
                if (this.recoveryScanner != null) {
                    addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[-nameIndex]];
                }
                int insertedToken = 0;
                for (int j = Parser.scope_suffix[-nameIndex]; Parser.scope_rhs[j] != '\0'; ++j) {
                    buf.append(Parser.readableName[Parser.scope_rhs[j]]);
                    if (Parser.scope_rhs[j + 1] != '\0') {
                        buf.append(' ');
                    }
                    else {
                        insertedToken = Parser.reverse_index[Parser.scope_rhs[j]];
                    }
                    if (addedTokens != null) {
                        final int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[j]];
                        if (tmpAddedToken > -1) {
                            final int length = addedTokens.length;
                            if (addedTokenCount == length) {
                                System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
                            }
                            addedTokens[addedTokenCount++] = tmpAddedToken;
                        }
                        else {
                            final int[] template = this.getNTermTemplate(-tmpAddedToken);
                            if (template != null) {
                                for (int k = 0; k < template.length; ++k) {
                                    final int length2 = addedTokens.length;
                                    if (addedTokenCount == length2) {
                                        System.arraycopy(addedTokens, 0, addedTokens = new int[length2 * 2], 0, length2);
                                    }
                                    addedTokens[addedTokenCount++] = template[k];
                                }
                            }
                            else {
                                addedTokenCount = 0;
                                addedTokens = null;
                            }
                        }
                    }
                }
                if (addedTokenCount > 0) {
                    System.arraycopy(addedTokens, 0, addedTokens = new int[addedTokenCount], 0, addedTokenCount);
                    int completedToken = -1;
                    if (scopeNameIndex != 0) {
                        completedToken = -Parser.reverse_index[scopeNameIndex];
                    }
                    this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
                }
                if (scopeNameIndex != 0) {
                    if (insertedToken == 66) {
                        break;
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorInsertToComplete(errorStart, errorEnd, buf.toString(), Parser.readableName[scopeNameIndex]);
                        break;
                    }
                    break;
                }
                else {
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorInsertToCompletePhrase(errorStart, errorEnd, buf.toString());
                        break;
                    }
                    break;
                }
                break;
            }
            case 7: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    }
                    else {
                        final int[] template2 = this.getNTermTemplate(-addedToken);
                        if (template2 != null) {
                            this.recoveryScanner.replaceTokens(template2, errorStart, errorEnd);
                        }
                    }
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorMergeTokens(errorStart, errorEnd, name);
                    break;
                }
                break;
            }
            case 6: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (this.reportProblem) {
                    this.problemReporter().parseErrorDeleteTokens(errorStart, errorEnd);
                    break;
                }
                break;
            }
            default: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorNoSuggestionForTokens(errorStart, errorEnd);
                        break;
                    }
                    break;
                }
                else {
                    if (this.recoveryScanner != null) {
                        if (addedToken > -1) {
                            this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                        }
                        else {
                            final int[] template2 = this.getNTermTemplate(-addedToken);
                            if (template2 != null) {
                                this.recoveryScanner.replaceTokens(template2, errorStart, errorEnd);
                            }
                        }
                    }
                    if (this.reportProblem) {
                        this.problemReporter().parseErrorReplaceTokens(errorStart, errorEnd, name);
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    private int[] getNTermTemplate(final int sym) {
        final int templateIndex = Parser.recovery_templates_index[sym];
        if (templateIndex > 0) {
            int[] result = new int[Parser.recovery_templates.length];
            int count = 0;
            for (int j = templateIndex; Parser.recovery_templates[j] != '\0'; ++j) {
                result[count++] = Parser.recovery_templates[j];
            }
            System.arraycopy(result, 0, result = new int[count], 0, count);
            return result;
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer res = new StringBuffer();
        res.append(this.lexStream.toString());
        return res.toString();
    }
    
    @Override
    public boolean atConflictScenario(final int token) {
        return token == 24 || token == 37 || (token == 11 && !this.lexStream.awaitingColonColon());
    }
    
    private static class RepairCandidate
    {
        public int symbol;
        public int location;
        
        public RepairCandidate() {
            this.symbol = 0;
            this.location = 0;
        }
    }
    
    private static class PrimaryRepairInfo
    {
        public int distance;
        public int misspellIndex;
        public int code;
        public int bufferPosition;
        public int symbol;
        
        public PrimaryRepairInfo() {
            this.distance = 0;
            this.misspellIndex = 0;
            this.code = 0;
            this.bufferPosition = 0;
            this.symbol = 0;
        }
        
        public PrimaryRepairInfo copy() {
            final PrimaryRepairInfo c = new PrimaryRepairInfo();
            c.distance = this.distance;
            c.misspellIndex = this.misspellIndex;
            c.code = this.code;
            c.bufferPosition = this.bufferPosition;
            c.symbol = this.symbol;
            return c;
        }
    }
    
    static class SecondaryRepairInfo
    {
        public int code;
        public int distance;
        public int bufferPosition;
        public int stackPosition;
        public int numDeletions;
        public int symbol;
        boolean recoveryOnNextStack;
    }
    
    private static class StateInfo
    {
        int state;
        int next;
        
        public StateInfo(final int state, final int next) {
            this.state = state;
            this.next = next;
        }
    }
}
