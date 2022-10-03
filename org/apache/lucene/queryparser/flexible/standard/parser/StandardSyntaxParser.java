package org.apache.lucene.queryparser.flexible.standard.parser;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QuotedFieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import java.util.Arrays;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import java.util.Vector;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;

public class StandardSyntaxParser implements SyntaxParser, StandardSyntaxParserConstants
{
    public StandardSyntaxParserTokenManager token_source;
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
    
    public StandardSyntaxParser() {
        this(new FastCharStream(new StringReader("")));
    }
    
    @Override
    public QueryNode parse(final CharSequence query, final CharSequence field) throws QueryNodeParseException {
        this.ReInit(new FastCharStream(new StringReader(query.toString())));
        try {
            final QueryNode querynode = this.TopLevelQuery(field);
            return querynode;
        }
        catch (final ParseException tme) {
            tme.setQuery(query);
            throw tme;
        }
        catch (final Error tme2) {
            final Message message = new MessageImpl(QueryParserMessages.INVALID_SYNTAX_CANNOT_PARSE, new Object[] { query, tme2.getMessage() });
            final QueryNodeParseException e = new QueryNodeParseException(tme2);
            e.setQuery(query);
            e.setNonLocalizedMessage(message);
            throw e;
        }
    }
    
    public final ModifierQueryNode.Modifier Modifiers() throws ParseException {
        ModifierQueryNode.Modifier ret = ModifierQueryNode.Modifier.MOD_NONE;
        Label_0168: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 10:
                case 11:
                case 12: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 11: {
                            this.jj_consume_token(11);
                            ret = ModifierQueryNode.Modifier.MOD_REQ;
                            break Label_0168;
                        }
                        case 12: {
                            this.jj_consume_token(12);
                            ret = ModifierQueryNode.Modifier.MOD_NOT;
                            break Label_0168;
                        }
                        case 10: {
                            this.jj_consume_token(10);
                            ret = ModifierQueryNode.Modifier.MOD_NOT;
                            break Label_0168;
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
    
    public final QueryNode TopLevelQuery(final CharSequence field) throws ParseException {
        final QueryNode q = this.Query(field);
        this.jj_consume_token(0);
        return q;
    }
    
    public final QueryNode Query(final CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        QueryNode first = null;
        first = this.DisjQuery(field);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 10:
                case 11:
                case 12:
                case 13:
                case 22:
                case 23:
                case 25:
                case 26:
                case 27:
                case 28: {
                    final QueryNode c = this.DisjQuery(field);
                    if (clauses == null) {
                        clauses = new Vector<QueryNode>();
                        clauses.addElement(first);
                    }
                    clauses.addElement(c);
                    continue;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    if (clauses != null) {
                        return new BooleanQueryNode(clauses);
                    }
                    if (first instanceof ModifierQueryNode) {
                        final ModifierQueryNode m = (ModifierQueryNode)first;
                        if (m.getModifier() == ModifierQueryNode.Modifier.MOD_NOT) {
                            return new BooleanQueryNode(Arrays.asList(m));
                        }
                    }
                    return first;
                }
            }
        }
    }
    
    public final QueryNode DisjQuery(final CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        final QueryNode first = this.ConjQuery(field);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    final QueryNode c = this.ConjQuery(field);
                    if (clauses == null) {
                        clauses = new Vector<QueryNode>();
                        clauses.addElement(first);
                    }
                    clauses.addElement(c);
                    continue;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    if (clauses != null) {
                        return new OrQueryNode(clauses);
                    }
                    return first;
                }
            }
        }
    }
    
    public final QueryNode ConjQuery(final CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        final QueryNode first = this.ModClause(field);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    this.jj_consume_token(8);
                    final QueryNode c = this.ModClause(field);
                    if (clauses == null) {
                        clauses = new Vector<QueryNode>();
                        clauses.addElement(first);
                    }
                    clauses.addElement(c);
                    continue;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    if (clauses != null) {
                        return new AndQueryNode(clauses);
                    }
                    return first;
                }
            }
        }
    }
    
    public final QueryNode ModClause(final CharSequence field) throws ParseException {
        final ModifierQueryNode.Modifier mods = this.Modifiers();
        QueryNode q = this.Clause(field);
        if (mods != ModifierQueryNode.Modifier.MOD_NONE) {
            q = new ModifierQueryNode(q, mods);
        }
        return q;
    }
    
    public final QueryNode Clause(CharSequence field) throws ParseException {
        Token fieldToken = null;
        Token boost = null;
        Token operator = null;
        Token term = null;
        boolean group = false;
        QueryNode q = null;
        Label_1277: {
            if (this.jj_2_2(3)) {
                fieldToken = this.jj_consume_token(23);
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 15:
                    case 16: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 15: {
                                this.jj_consume_token(15);
                                break;
                            }
                            case 16: {
                                this.jj_consume_token(16);
                                break;
                            }
                            default: {
                                this.jj_la1[5] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                        q = this.Term(field);
                        break;
                    }
                    case 17:
                    case 18:
                    case 19:
                    case 20: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 17: {
                                operator = this.jj_consume_token(17);
                                break;
                            }
                            case 18: {
                                operator = this.jj_consume_token(18);
                                break;
                            }
                            case 19: {
                                operator = this.jj_consume_token(19);
                                break;
                            }
                            case 20: {
                                operator = this.jj_consume_token(20);
                                break;
                            }
                            default: {
                                this.jj_la1[6] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 23: {
                                term = this.jj_consume_token(23);
                                break;
                            }
                            case 22: {
                                term = this.jj_consume_token(22);
                                break;
                            }
                            case 28: {
                                term = this.jj_consume_token(28);
                                break;
                            }
                            default: {
                                this.jj_la1[7] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        if (term.kind == 22) {
                            term.image = term.image.substring(1, term.image.length() - 1);
                        }
                        boolean lowerInclusive = false;
                        boolean upperInclusive = false;
                        FieldQueryNode qLower = null;
                        FieldQueryNode qUpper = null;
                        switch (operator.kind) {
                            case 17: {
                                lowerInclusive = true;
                                upperInclusive = false;
                                qLower = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                break;
                            }
                            case 18: {
                                lowerInclusive = true;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                break;
                            }
                            case 19: {
                                lowerInclusive = false;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                break;
                            }
                            case 20: {
                                lowerInclusive = true;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                break;
                            }
                            default: {
                                throw new Error("Unhandled case: operator=" + operator.toString());
                            }
                        }
                        q = new TermRangeQueryNode(qLower, qUpper, lowerInclusive, upperInclusive);
                        break;
                    }
                    default: {
                        this.jj_la1[8] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
            else {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 13:
                    case 22:
                    case 23:
                    case 25:
                    case 26:
                    case 27:
                    case 28: {
                        if (this.jj_2_1(2)) {
                            fieldToken = this.jj_consume_token(23);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 15: {
                                    this.jj_consume_token(15);
                                    break;
                                }
                                case 16: {
                                    this.jj_consume_token(16);
                                    break;
                                }
                                default: {
                                    this.jj_la1[9] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 22:
                            case 23:
                            case 25:
                            case 26:
                            case 27:
                            case 28: {
                                q = this.Term(field);
                                break Label_1277;
                            }
                            case 13: {
                                this.jj_consume_token(13);
                                q = this.Query(field);
                                this.jj_consume_token(14);
                                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                    case 21: {
                                        this.jj_consume_token(21);
                                        boost = this.jj_consume_token(28);
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[10] = this.jj_gen;
                                        break;
                                    }
                                }
                                group = true;
                                break Label_1277;
                            }
                            default: {
                                this.jj_la1[11] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[12] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
        }
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image);
                if (q != null) {
                    q = new BoostQueryNode(q, f);
                }
            }
            catch (final Exception ex) {}
        }
        if (group) {
            q = new GroupQueryNode(q);
        }
        return q;
    }
    
    public final QueryNode Term(final CharSequence field) throws ParseException {
        Token boost = null;
        Token fuzzySlop = null;
        boolean fuzzy = false;
        boolean regexp = false;
        boolean startInc = false;
        boolean endInc = false;
        QueryNode q = null;
        final float defaultMinSimilarity = 2.0f;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 23:
            case 25:
            case 28: {
                Token term = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 23: {
                        term = this.jj_consume_token(23);
                        q = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                        break;
                    }
                    case 25: {
                        term = this.jj_consume_token(25);
                        regexp = true;
                        break;
                    }
                    case 28: {
                        term = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 24: {
                        fuzzySlop = this.jj_consume_token(24);
                        fuzzy = true;
                        break;
                    }
                    default: {
                        this.jj_la1[14] = this.jj_gen;
                        break;
                    }
                }
                Label_0411: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 21: {
                            this.jj_consume_token(21);
                            boost = this.jj_consume_token(28);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 24: {
                                    fuzzySlop = this.jj_consume_token(24);
                                    fuzzy = true;
                                    break Label_0411;
                                }
                                default: {
                                    this.jj_la1[15] = this.jj_gen;
                                    break Label_0411;
                                }
                            }
                            break;
                        }
                        default: {
                            this.jj_la1[16] = this.jj_gen;
                            break;
                        }
                    }
                }
                if (fuzzy) {
                    float fms = defaultMinSimilarity;
                    try {
                        fms = Float.valueOf(fuzzySlop.image.substring(1));
                    }
                    catch (final Exception ex) {}
                    if (fms < 0.0f) {
                        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_FUZZY_LIMITS));
                    }
                    if (fms >= 1.0f && fms != (int)fms) {
                        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_FUZZY_EDITS));
                    }
                    q = new FuzzyQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), fms, term.beginColumn, term.endColumn);
                    break;
                }
                else {
                    if (regexp) {
                        final String re = term.image.substring(1, term.image.length() - 1);
                        q = new RegexpQueryNode(field, re, 0, re.length());
                        break;
                    }
                    break;
                }
                break;
            }
            case 26:
            case 27: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 26: {
                        this.jj_consume_token(26);
                        startInc = true;
                        break;
                    }
                    case 27: {
                        this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                Token goop1 = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 33: {
                        goop1 = this.jj_consume_token(33);
                        break;
                    }
                    case 32: {
                        goop1 = this.jj_consume_token(32);
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 29: {
                        this.jj_consume_token(29);
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                        break;
                    }
                }
                Token goop2 = null;
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 33: {
                        goop2 = this.jj_consume_token(33);
                        break;
                    }
                    case 32: {
                        goop2 = this.jj_consume_token(32);
                        break;
                    }
                    default: {
                        this.jj_la1[20] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 30: {
                        this.jj_consume_token(30);
                        endInc = true;
                        break;
                    }
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[21] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        this.jj_consume_token(21);
                        boost = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[22] = this.jj_gen;
                        break;
                    }
                }
                if (goop1.kind == 32) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                }
                if (goop2.kind == 32) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                }
                final FieldQueryNode qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(goop1.image), goop1.beginColumn, goop1.endColumn);
                final FieldQueryNode qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(goop2.image), goop2.beginColumn, goop2.endColumn);
                q = new TermRangeQueryNode(qLower, qUpper, startInc, endInc);
                break;
            }
            case 22: {
                final Token term = this.jj_consume_token(22);
                q = new QuotedFieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), term.beginColumn + 1, term.endColumn - 1);
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 24: {
                        fuzzySlop = this.jj_consume_token(24);
                        break;
                    }
                    default: {
                        this.jj_la1[23] = this.jj_gen;
                        break;
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        this.jj_consume_token(21);
                        boost = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[24] = this.jj_gen;
                        break;
                    }
                }
                int phraseSlop = 0;
                if (fuzzySlop != null) {
                    try {
                        phraseSlop = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
                        q = new SlopQueryNode(q, phraseSlop);
                    }
                    catch (final Exception ignored) {}
                    break;
                }
                break;
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image);
                if (q != null) {
                    q = new BoostQueryNode(q, f);
                }
            }
            catch (final Exception ex2) {}
        }
        return q;
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
    
    private boolean jj_2_2(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_2();
        }
        catch (final LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(1, xla);
        }
    }
    
    private boolean jj_3R_12() {
        return this.jj_scan_token(26);
    }
    
    private boolean jj_3R_11() {
        return this.jj_scan_token(25);
    }
    
    private boolean jj_3_1() {
        if (this.jj_scan_token(23)) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(15)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(16)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_8() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_12()) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(27)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_10() {
        return this.jj_scan_token(23);
    }
    
    private boolean jj_3R_7() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_10()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_11()) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(28)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_9() {
        return this.jj_scan_token(22);
    }
    
    private boolean jj_3R_5() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(17)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(18)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(19)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(20)) {
                        return true;
                    }
                }
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(23)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(22)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(28)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_4() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(15)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(16)) {
                return true;
            }
        }
        return this.jj_3R_6();
    }
    
    private boolean jj_3R_6() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_7()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_8()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_9()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3_2() {
        if (this.jj_scan_token(23)) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_5()) {
                return true;
            }
        }
        return false;
    }
    
    private static void jj_la1_init_0() {
        StandardSyntaxParser.jj_la1_0 = new int[] { 7168, 7168, 515914752, 512, 256, 98304, 1966080, 281018368, 2064384, 98304, 2097152, 515907584, 515907584, 310378496, 16777216, 16777216, 2097152, 201326592, 0, 536870912, 0, -1073741824, 2097152, 16777216, 2097152, 515899392 };
    }
    
    private static void jj_la1_init_1() {
        StandardSyntaxParser.jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0 };
    }
    
    public StandardSyntaxParser(final CharStream stream) {
        this.jj_la1 = new int[26];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = new StandardSyntaxParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 26; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final CharStream stream) {
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 26; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public StandardSyntaxParser(final StandardSyntaxParserTokenManager tm) {
        this.jj_la1 = new int[26];
        this.jj_2_rtns = new JJCalls[2];
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
        for (int i = 0; i < 26; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final StandardSyntaxParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 26; ++i) {
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
        final boolean[] la1tokens = new boolean[34];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 26; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((StandardSyntaxParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((StandardSyntaxParser.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 34; ++i) {
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
        return new ParseException(this.token, exptokseq, StandardSyntaxParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 2; ++i) {
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
                            case 1: {
                                this.jj_3_2();
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
