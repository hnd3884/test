package org.apache.lucene.queryparser.classic;

import java.util.Iterator;
import org.apache.lucene.search.BooleanClause;
import java.util.ArrayList;
import org.apache.lucene.search.Query;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.analysis.Analyzer;
import java.util.List;

public class QueryParser extends QueryParserBase implements QueryParserConstants
{
    public QueryParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    public QueryParser(final String f, final Analyzer a) {
        this(new FastCharStream(new StringReader("")));
        this.init(f, a);
    }
    
    public final int Conjunction() throws ParseException {
        int ret = 0;
        Label_0150: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8:
                case 9: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 8: {
                            this.jj_consume_token(8);
                            ret = 1;
                            break Label_0150;
                        }
                        case 9: {
                            this.jj_consume_token(9);
                            ret = 2;
                            break Label_0150;
                        }
                        default: {
                            this.jj_la1[0] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    break;
                }
            }
        }
        return ret;
    }
    
    public final int Modifiers() throws ParseException {
        int ret = 0;
        Label_0165: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 10:
                case 11:
                case 12: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 11: {
                            this.jj_consume_token(11);
                            ret = 11;
                            break Label_0165;
                        }
                        case 12: {
                            this.jj_consume_token(12);
                            ret = 10;
                            break Label_0165;
                        }
                        case 10: {
                            this.jj_consume_token(10);
                            ret = 10;
                            break Label_0165;
                        }
                        default: {
                            this.jj_la1[2] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    break;
                }
            }
        }
        return ret;
    }
    
    @Override
    public final Query TopLevelQuery(final String field) throws ParseException {
        final Query q = this.Query(field);
        this.jj_consume_token(0);
        return q;
    }
    
    public final Query Query(final String field) throws ParseException {
        final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
        Query firstQuery = null;
        int mods = this.Modifiers();
        Query q = this.Clause(field);
        this.addClause(clauses, 0, mods, q);
        if (mods == 0) {
            firstQuery = q;
        }
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 17:
                case 19:
                case 20:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27: {
                    final int conj = this.Conjunction();
                    mods = this.Modifiers();
                    q = this.Clause(field);
                    this.addClause(clauses, conj, mods, q);
                    continue;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    if (clauses.size() == 1 && firstQuery != null) {
                        return firstQuery;
                    }
                    return this.getBooleanQuery(clauses);
                }
            }
        }
    }
    
    public final Query Clause(String field) throws ParseException {
        Token fieldToken = null;
        Token boost = null;
        if (this.jj_2_1(2)) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 20: {
                    fieldToken = this.jj_consume_token(20);
                    this.jj_consume_token(16);
                    field = this.discardEscapeChar(fieldToken.image);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    this.jj_consume_token(16);
                    field = "*";
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        Query q = null;
        Label_0349: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 13:
                case 17:
                case 19:
                case 20:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27: {
                    q = this.Term(field);
                    break;
                }
                case 14: {
                    this.jj_consume_token(14);
                    q = this.Query(field);
                    this.jj_consume_token(15);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 18: {
                            this.jj_consume_token(18);
                            boost = this.jj_consume_token(27);
                            break Label_0349;
                        }
                        default: {
                            this.jj_la1[6] = this.jj_gen;
                            break Label_0349;
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return this.handleBoost(q, boost);
    }
    
    public final Query Term(final String field) throws ParseException {
        Token boost = null;
        Token fuzzySlop = null;
        boolean prefix = false;
        boolean wildcard = false;
        boolean fuzzy = false;
        boolean regexp = false;
        boolean startInc = false;
        boolean endInc = false;
        Query q = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 13:
            case 17:
            case 20:
            case 22:
            case 23:
            case 24:
            case 27: {
                Token term = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 20: {
                        term = this.jj_consume_token(20);
                        break;
                    }
                    case 17: {
                        term = this.jj_consume_token(17);
                        wildcard = true;
                        break;
                    }
                    case 22: {
                        term = this.jj_consume_token(22);
                        prefix = true;
                        break;
                    }
                    case 23: {
                        term = this.jj_consume_token(23);
                        wildcard = true;
                        break;
                    }
                    case 24: {
                        term = this.jj_consume_token(24);
                        regexp = true;
                        break;
                    }
                    case 27: {
                        term = this.jj_consume_token(27);
                        break;
                    }
                    case 13: {
                        term = this.jj_consume_token(13);
                        term.image = term.image.substring(0, 1);
                        break;
                    }
                    default: {
                        this.jj_la1[8] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        fuzzySlop = this.jj_consume_token(21);
                        fuzzy = true;
                        break;
                    }
                    default: {
                        this.jj_la1[9] = this.jj_gen;
                        break;
                    }
                }
                Label_0519: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 18: {
                            this.jj_consume_token(18);
                            boost = this.jj_consume_token(27);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 21: {
                                    fuzzySlop = this.jj_consume_token(21);
                                    fuzzy = true;
                                    break Label_0519;
                                }
                                default: {
                                    this.jj_la1[10] = this.jj_gen;
                                    break Label_0519;
                                }
                            }
                            break;
                        }
                        default: {
                            this.jj_la1[11] = this.jj_gen;
                            break;
                        }
                    }
                }
                q = this.handleBareTokenQuery(field, term, fuzzySlop, prefix, wildcard, fuzzy, regexp);
                break;
            }
            case 25:
            case 26: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 25: {
                        this.jj_consume_token(25);
                        startInc = true;
                        break;
                    }
                    case 26: {
                        this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[12] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                Token goop1 = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 32: {
                        goop1 = this.jj_consume_token(32);
                        break;
                    }
                    case 31: {
                        goop1 = this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 28: {
                        this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[14] = this.jj_gen;
                        break;
                    }
                }
                Token goop2 = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 32: {
                        goop2 = this.jj_consume_token(32);
                        break;
                    }
                    case 31: {
                        goop2 = this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 29: {
                        this.jj_consume_token(29);
                        endInc = true;
                        break;
                    }
                    case 30: {
                        this.jj_consume_token(30);
                        break;
                    }
                    default: {
                        this.jj_la1[16] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                        break;
                    }
                }
                boolean startOpen = false;
                boolean endOpen = false;
                if (goop1.kind == 31) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                }
                else if ("*".equals(goop1.image)) {
                    startOpen = true;
                }
                if (goop2.kind == 31) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                }
                else if ("*".equals(goop2.image)) {
                    endOpen = true;
                }
                q = this.getRangeQuery(field, startOpen ? null : this.discardEscapeChar(goop1.image), endOpen ? null : this.discardEscapeChar(goop2.image), startInc, endInc);
                break;
            }
            case 19: {
                final Token term = this.jj_consume_token(19);
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        fuzzySlop = this.jj_consume_token(21);
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        break;
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                        break;
                    }
                }
                q = this.handleQuotedTerm(field, term, fuzzySlop);
                break;
            }
            default: {
                this.jj_la1[20] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return this.handleBoost(q, boost);
    }
    
    private boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (final LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(0, xla);
        }
    }
    
    private boolean jj_3R_2() {
        return this.jj_scan_token(20) || this.jj_scan_token(16);
    }
    
    private boolean jj_3_1() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_2()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_3()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_3() {
        return this.jj_scan_token(17) || this.jj_scan_token(16);
    }
    
    private static void jj_la1_init_0() {
        QueryParser.jj_la1_0 = new int[] { 768, 768, 7168, 7168, 265977600, 1179648, 262144, 265969664, 164765696, 2097152, 2097152, 262144, 100663296, Integer.MIN_VALUE, 268435456, Integer.MIN_VALUE, 1610612736, 262144, 2097152, 262144, 265953280 };
    }
    
    private static void jj_la1_init_1() {
        QueryParser.jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 };
    }
    
    protected QueryParser(final CharStream stream) {
        this.jj_la1 = new int[21];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = new QueryParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    @Override
    public void ReInit(final CharStream stream) {
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    protected QueryParser(final QueryParserTokenManager tm) {
        this.jj_la1 = new int[21];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final QueryParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    private Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }
    
    private boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok;
            for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int kind, final int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
        Label_0092:
            for (final int[] oldentry : this.jj_expentries) {
                if (oldentry.length == this.jj_expentry.length) {
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            continue Label_0092;
                        }
                    }
                    this.jj_expentries.add(this.jj_expentry);
                    break;
                }
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] la1tokens = new boolean[33];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 21; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((QueryParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((QueryParser.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 33; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.get(k);
        }
        return new ParseException(this.token, exptokseq, QueryParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 1; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        final Token first = p.first;
                        this.jj_scanpos = first;
                        this.jj_lastpos = first;
                        switch (i) {
                            case 0: {
                                this.jj_3_1();
                                break;
                            }
                        }
                    }
                    p = p.next;
                } while (p != null);
            }
            catch (final LookaheadSuccess lookaheadSuccess) {}
        }
        this.jj_rescan = false;
    }
    
    private void jj_save(final int index, final int xla) {
        JJCalls p;
        for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                final JJCalls jjCalls = p;
                final JJCalls next = new JJCalls();
                jjCalls.next = next;
                p = next;
                break;
            }
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }
    
    static {
        jj_la1_init_0();
        jj_la1_init_1();
    }
    
    public enum Operator
    {
        OR, 
        AND;
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
