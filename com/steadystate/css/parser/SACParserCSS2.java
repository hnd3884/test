package com.steadystate.css.parser;

import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import java.util.Locale;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.ConditionFactory;
import java.io.IOException;
import org.w3c.css.sac.InputSource;
import java.util.Iterator;
import java.util.ArrayList;
import com.steadystate.css.parser.selectors.SelectorFactoryImpl;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import java.util.List;

public class SACParserCSS2 extends AbstractSACParser implements SACParserCSS2Constants
{
    public SACParserCSS2TokenManager token_source;
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
    private static int[] jj_la1_2;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    private int trace_indent;
    private boolean trace_enabled;
    
    public SACParserCSS2() {
        this((CharStream)null);
    }
    
    @Override
    public String getParserVersion() {
        return "http://www.w3.org/TR/REC-CSS2/";
    }
    
    @Override
    protected String getGrammarUri() {
        return "http://www.w3.org/TR/REC-CSS2/grammar.html";
    }
    
    public final void styleSheet() throws ParseException {
        try {
            this.handleStartDocument();
            this.styleSheetRuleList();
            this.jj_consume_token(0);
        }
        finally {
            this.handleEndDocument();
        }
    }
    
    public final void styleSheetRuleList() throws ParseException {
        boolean ruleFound = false;
    Label_0403_Outer:
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1:
                case 25:
                case 26: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        case 25: {
                            this.jj_consume_token(25);
                            continue;
                        }
                        case 26: {
                            this.jj_consume_token(26);
                            continue;
                        }
                        default: {
                            this.jj_la1[1] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                Label_0403:
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 33: {
                                this.charsetRule();
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1:
                                        case 25:
                                        case 26: {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 1: {
                                                    this.jj_consume_token(1);
                                                    continue Label_0403_Outer;
                                                }
                                                case 25: {
                                                    this.jj_consume_token(25);
                                                    continue Label_0403_Outer;
                                                }
                                                case 26: {
                                                    this.jj_consume_token(26);
                                                    continue Label_0403_Outer;
                                                }
                                                default: {
                                                    this.jj_la1[3] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                            }
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[2] = this.jj_gen;
                                            break Label_0403;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[4] = this.jj_gen;
                                break;
                            }
                        }
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 9:
                                case 11:
                                case 12:
                                case 18:
                                case 20:
                                case 29:
                                case 30:
                                case 31:
                                case 32:
                                case 34:
                                case 58: {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 29: {
                                            this.importRule(ruleFound);
                                            break;
                                        }
                                        case 9:
                                        case 11:
                                        case 12:
                                        case 18:
                                        case 20:
                                        case 30:
                                        case 31:
                                        case 32:
                                        case 34:
                                        case 58: {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 9:
                                                case 11:
                                                case 12:
                                                case 18:
                                                case 20:
                                                case 58: {
                                                    this.styleRule();
                                                    break;
                                                }
                                                case 31: {
                                                    this.mediaRule();
                                                    break;
                                                }
                                                case 30: {
                                                    this.pageRule();
                                                    break;
                                                }
                                                case 32: {
                                                    this.fontFaceRule();
                                                    break;
                                                }
                                                case 34: {
                                                    this.unknownAtRule();
                                                    break;
                                                }
                                                default: {
                                                    this.jj_la1[6] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                            }
                                            ruleFound = true;
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[7] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ParseException();
                                        }
                                    }
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1:
                                            case 25:
                                            case 26: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 1: {
                                                        this.jj_consume_token(1);
                                                        continue Label_0403_Outer;
                                                    }
                                                    case 25: {
                                                        this.jj_consume_token(25);
                                                        continue Label_0403_Outer;
                                                    }
                                                    case 26: {
                                                        this.jj_consume_token(26);
                                                        continue Label_0403_Outer;
                                                    }
                                                    default: {
                                                        this.jj_la1[9] = this.jj_gen;
                                                        this.jj_consume_token(-1);
                                                        throw new ParseException();
                                                    }
                                                }
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[8] = this.jj_gen;
                                                continue Label_0403;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[5] = this.jj_gen;
                                    return;
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public final void styleSheetRuleSingle() throws ParseException {
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 33: {
                            this.charsetRule();
                            break;
                        }
                        case 29: {
                            this.importRule(false);
                            break;
                        }
                        case 9:
                        case 11:
                        case 12:
                        case 18:
                        case 20:
                        case 58: {
                            this.styleRule();
                            break;
                        }
                        case 31: {
                            this.mediaRule();
                            break;
                        }
                        case 30: {
                            this.pageRule();
                            break;
                        }
                        case 32: {
                            this.fontFaceRule();
                            break;
                        }
                        case 34: {
                            this.unknownAtRule();
                            break;
                        }
                        default: {
                            this.jj_la1[11] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
            }
        }
    }
    
    public final void charsetRule() throws ParseException {
        Label_0186: {
            try {
                this.jj_consume_token(33);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                            final Token t = this.jj_consume_token(21);
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[13] = this.jj_gen;
                                        this.jj_consume_token(10);
                                        this.handleCharset(t.toString(), locator);
                                        break Label_0186;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            catch (final ParseException e) {
                this.getErrorHandler().error(this.toCSSParseException("invalidCharsetRule", e));
            }
        }
    }
    
    public final void unknownAtRule() throws ParseException {
        try {
            this.jj_consume_token(34);
            final Locator locator = this.createLocator(this.token);
            final String s = this.skip();
            this.handleIgnorableAtRule(s, locator);
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidUnknownRule", e));
        }
    }
    
    public final void importRule(final boolean nonImportRuleFoundBefore) throws ParseException {
        final SACMediaListImpl ml = new SACMediaListImpl();
        Label_0397: {
            try {
                this.jj_consume_token(29);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[14] = this.jj_gen;
                            Token t = null;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 21: {
                                    t = this.jj_consume_token(21);
                                    break;
                                }
                                case 24: {
                                    t = this.jj_consume_token(24);
                                    break;
                                }
                                default: {
                                    this.jj_la1[15] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[16] = this.jj_gen;
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 58: {
                                                this.mediaList(ml);
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[17] = this.jj_gen;
                                                break;
                                            }
                                        }
                                        this.jj_consume_token(10);
                                        if (nonImportRuleFoundBefore) {
                                            this.handleImportStyle(this.unescape(t.image, false), (SACMediaList)ml, null, locator);
                                        }
                                        else {
                                            this.handleImportStyle(this.unescape(t.image, false), (SACMediaList)ml, null, locator);
                                        }
                                        break Label_0397;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            catch (final CSSParseException e) {
                this.getErrorHandler().error(e);
                this.error_skipAtRule();
            }
            catch (final ParseException e2) {
                this.getErrorHandler().error(this.toCSSParseException("invalidImportRule", e2));
                this.error_skipAtRule();
            }
        }
    }
    
    public final void mediaRule() throws ParseException {
        boolean start = false;
        final SACMediaListImpl ml = new SACMediaListImpl();
        try {
            this.jj_consume_token(31);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        this.mediaList(ml);
                        start = true;
                        this.handleStartMedia((SACMediaList)ml, locator);
                        this.jj_consume_token(6);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[19] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 9:
                                        case 11:
                                        case 12:
                                        case 18:
                                        case 20:
                                        case 30:
                                        case 34:
                                        case 58: {
                                            this.mediaRuleList();
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[20] = this.jj_gen;
                                            break;
                                        }
                                    }
                                    this.jj_consume_token(7);
                                    return;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        catch (final CSSParseException e) {
            this.getErrorHandler().error(e);
            this.error_skipblock(null, null);
        }
        catch (final ParseException e2) {
            final CSSParseException cpe = this.toCSSParseException("invalidMediaRule", e2);
            this.getErrorHandler().error(cpe);
            this.error_skipblock("ignoringRule", cpe);
        }
        finally {
            if (start) {
                this.handleEndMedia((SACMediaList)ml);
            }
        }
    }
    
    public final void mediaList(final SACMediaListImpl ml) throws ParseException {
        Label_0172: {
            try {
                String s = this.medium();
                ml.setLocator(this.createLocator(this.token));
            Label_0017:
                while (true) {
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 8: {
                                this.jj_consume_token(8);
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1: {
                                            this.jj_consume_token(1);
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[22] = this.jj_gen;
                                            ml.add(s);
                                            s = this.medium();
                                            continue Label_0017;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[21] = this.jj_gen;
                                ml.add(s);
                                break Label_0172;
                            }
                        }
                    }
                    break;
                }
            }
            catch (final ParseException e) {
                throw this.toCSSParseException("invalidMediaList", e);
            }
        }
    }
    
    public final void mediaRuleList() throws ParseException {
    Label_0000:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 9:
                    case 11:
                    case 12:
                    case 18:
                    case 20:
                    case 58: {
                        this.styleRule();
                        break;
                    }
                    case 30: {
                        this.pageRule();
                        break;
                    }
                    case 34: {
                        this.unknownAtRule();
                        break;
                    }
                    default: {
                        this.jj_la1[23] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[24] = this.jj_gen;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 9:
                                case 11:
                                case 12:
                                case 18:
                                case 20:
                                case 30:
                                case 34:
                                case 58: {
                                    continue Label_0000;
                                }
                                default: {
                                    this.jj_la1[25] = this.jj_gen;
                                    return;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            break;
        }
    }
    
    public final String medium() throws ParseException {
        final Token t = this.jj_consume_token(58);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
                    this.handleMedium(t.image, this.createLocator(t));
                    return t.image;
                }
            }
        }
    }
    
    public final void pageRule() throws ParseException {
        Token t = null;
        String s = null;
        boolean start = false;
        Label_0847: {
            try {
                this.jj_consume_token(30);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[27] = this.jj_gen;
                            Label_0434: {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 11:
                                    case 58: {
                                        if (this.jj_2_1(2)) {
                                            t = this.jj_consume_token(58);
                                            s = this.pseudoPage();
                                            while (true) {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 1: {
                                                        this.jj_consume_token(1);
                                                        continue;
                                                    }
                                                    default: {
                                                        this.jj_la1[28] = this.jj_gen;
                                                        break Label_0434;
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 58: {
                                                    t = this.jj_consume_token(58);
                                                    while (true) {
                                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                            case 1: {
                                                                this.jj_consume_token(1);
                                                                continue;
                                                            }
                                                            default: {
                                                                this.jj_la1[29] = this.jj_gen;
                                                                break Label_0434;
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                                case 11: {
                                                    s = this.pseudoPage();
                                                    while (true) {
                                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                            case 1: {
                                                                this.jj_consume_token(1);
                                                                continue;
                                                            }
                                                            default: {
                                                                this.jj_la1[30] = this.jj_gen;
                                                                break Label_0434;
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                                default: {
                                                    this.jj_la1[31] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[32] = this.jj_gen;
                                        break;
                                    }
                                }
                            }
                            this.jj_consume_token(6);
                        Label_0586_Outer:
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[33] = this.jj_gen;
                                        start = true;
                                        this.handleStartPage((t != null) ? this.unescape(t.image, false) : null, s, locator);
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 58: {
                                                this.declaration();
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[34] = this.jj_gen;
                                                break;
                                            }
                                        }
                                    Label_0586:
                                        while (true) {
                                            while (true) {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 10: {
                                                        this.jj_consume_token(10);
                                                        while (true) {
                                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                case 1: {
                                                                    this.jj_consume_token(1);
                                                                    continue Label_0586_Outer;
                                                                }
                                                                default: {
                                                                    this.jj_la1[36] = this.jj_gen;
                                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                        case 58: {
                                                                            this.declaration();
                                                                            continue Label_0586;
                                                                        }
                                                                        default: {
                                                                            this.jj_la1[37] = this.jj_gen;
                                                                            continue Label_0586;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[35] = this.jj_gen;
                                                        this.jj_consume_token(7);
                                                        break Label_0847;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            catch (final ParseException e) {
                throw this.toCSSParseException("invalidPageRule", e);
            }
            finally {
                if (start) {
                    this.handleEndPage((t != null) ? this.unescape(t.image, false) : null, s);
                }
            }
        }
    }
    
    public final String pseudoPage() throws ParseException {
        this.jj_consume_token(11);
        final Token t = this.jj_consume_token(58);
        return ":" + this.unescape(t.image, false);
    }
    
    public final void fontFaceRule() throws ParseException {
        boolean start = false;
        Label_0441: {
            try {
                this.jj_consume_token(32);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[38] = this.jj_gen;
                            this.jj_consume_token(6);
                        Label_0218_Outer:
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[39] = this.jj_gen;
                                        start = true;
                                        this.handleStartFontFace(locator);
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 58: {
                                                this.declaration();
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[40] = this.jj_gen;
                                                break;
                                            }
                                        }
                                    Label_0218:
                                        while (true) {
                                            while (true) {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 10: {
                                                        this.jj_consume_token(10);
                                                        while (true) {
                                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                case 1: {
                                                                    this.jj_consume_token(1);
                                                                    continue Label_0218_Outer;
                                                                }
                                                                default: {
                                                                    this.jj_la1[42] = this.jj_gen;
                                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                        case 58: {
                                                                            this.declaration();
                                                                            continue Label_0218;
                                                                        }
                                                                        default: {
                                                                            this.jj_la1[43] = this.jj_gen;
                                                                            continue Label_0218;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[41] = this.jj_gen;
                                                        this.jj_consume_token(7);
                                                        break Label_0441;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            catch (final ParseException e) {
                throw this.toCSSParseException("invalidFontFaceRule", e);
            }
            finally {
                if (start) {
                    this.handleEndFontFace();
                }
            }
        }
    }
    
    public final LexicalUnit operator(final LexicalUnit prev) throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 13: {
                this.jj_consume_token(13);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[44] = this.jj_gen;
                            return (LexicalUnit)new LexicalUnitImpl(prev, (short)4);
                        }
                    }
                }
                break;
            }
            case 8: {
                this.jj_consume_token(8);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[45] = this.jj_gen;
                            return LexicalUnitImpl.createComma(prev);
                        }
                    }
                }
                break;
            }
            default: {
                this.jj_la1[46] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final char combinator() throws ParseException {
        char c = ' ';
        Label_0449: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 14: {
                    this.jj_consume_token(14);
                    c = '+';
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[47] = this.jj_gen;
                                break Label_0449;
                            }
                        }
                    }
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    c = '>';
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[48] = this.jj_gen;
                                break Label_0449;
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    this.jj_consume_token(1);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 14:
                        case 17: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 14: {
                                    this.jj_consume_token(14);
                                    c = '+';
                                    break;
                                }
                                case 17: {
                                    this.jj_consume_token(17);
                                    c = '>';
                                    break;
                                }
                                default: {
                                    this.jj_la1[49] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[50] = this.jj_gen;
                                        break Label_0449;
                                    }
                                }
                            }
                            break;
                        }
                        default: {
                            this.jj_la1[51] = this.jj_gen;
                            break Label_0449;
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[52] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return c;
    }
    
    public final char unaryOperator() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 15: {
                this.jj_consume_token(15);
                return '-';
            }
            case 14: {
                this.jj_consume_token(14);
                return '+';
            }
            default: {
                this.jj_la1[53] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final String property() throws ParseException {
        final Token t = this.jj_consume_token(58);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[54] = this.jj_gen;
                    return this.unescape(t.image, false);
                }
            }
        }
    }
    
    public final void styleRule() throws ParseException {
        SelectorList selList = null;
        boolean start = false;
        try {
            final Token t = this.token;
            selList = this.selectorList();
            this.jj_consume_token(6);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[55] = this.jj_gen;
                        start = true;
                        this.handleStartSelector(selList, this.createLocator(t.next));
                        this.styleDeclaration();
                        this.jj_consume_token(7);
                    }
                }
            }
        }
        catch (final CSSParseException e) {
            this.getErrorHandler().error(e);
            this.error_skipblock("ignoringRule", e);
        }
        catch (final ParseException e2) {
            final CSSParseException cpe = this.toCSSParseException("invalidStyleRule", e2);
            this.getErrorHandler().error(cpe);
            this.error_skipblock("ignoringFollowingDeclarations", cpe);
        }
        finally {
            if (start) {
                this.handleEndSelector(selList);
            }
        }
    }
    
    public final SelectorList parseSelectorsInternal() throws ParseException {
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[56] = this.jj_gen;
                    final SelectorList selectors = this.selectorList();
                    this.jj_consume_token(0);
                    return selectors;
                }
            }
        }
    }
    
    public final SelectorList selectorList() throws ParseException {
        final SelectorListImpl selList = new SelectorListImpl();
        Selector sel = this.selector();
        if (sel instanceof Locatable) {
            selList.setLocator(((Locatable)sel).getLocator());
        }
    Label_0033:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 8: {
                        this.jj_consume_token(8);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[58] = this.jj_gen;
                                    selList.add(sel);
                                    sel = this.selector();
                                    if (sel instanceof Locatable) {
                                        selList.setLocator(((Locatable)sel).getLocator());
                                        continue Label_0033;
                                    }
                                    continue Label_0033;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[57] = this.jj_gen;
                        selList.add(sel);
                        return (SelectorList)selList;
                    }
                }
            }
            break;
        }
    }
    
    public final Selector selector() throws ParseException {
        try {
            Selector sel = this.simpleSelector(null, ' ');
            while (this.jj_2_2(2)) {
                final char comb = this.combinator();
                sel = this.simpleSelector(sel, comb);
            }
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[59] = this.jj_gen;
                        this.handleSelector(sel);
                        return sel;
                    }
                }
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidSelector", e);
        }
    }
    
    public final Selector simpleSelector(Selector sel, final char comb) throws ParseException {
        SimpleSelector simpleSel = null;
        Condition c = null;
        SimpleSelector pseudoElementSel = null;
        Object o = null;
        try {
            Label_0690: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 12:
                    case 58: {
                        simpleSel = this.elementName();
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 9:
                                case 11:
                                case 18:
                                case 20: {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 20: {
                                            c = this.hash(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 9: {
                                            c = this._class(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 18: {
                                            c = this.attrib(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 11: {
                                            o = this.pseudo(c, null != pseudoElementSel);
                                            if (o instanceof Condition) {
                                                c = (Condition)o;
                                                continue;
                                            }
                                            pseudoElementSel = (SimpleSelector)o;
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[61] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ParseException();
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[60] = this.jj_gen;
                                    break Label_0690;
                                }
                            }
                        }
                        break;
                    }
                    case 9:
                    case 11:
                    case 18:
                    case 20: {
                        simpleSel = (SimpleSelector)((SelectorFactoryImpl)this.getSelectorFactory()).createSyntheticElementSelector();
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 20: {
                                    c = this.hash(c, null != pseudoElementSel);
                                    break;
                                }
                                case 9: {
                                    c = this._class(c, null != pseudoElementSel);
                                    break;
                                }
                                case 18: {
                                    c = this.attrib(c, null != pseudoElementSel);
                                    break;
                                }
                                case 11: {
                                    o = this.pseudo(c, null != pseudoElementSel);
                                    if (o instanceof Condition) {
                                        c = (Condition)o;
                                        break;
                                    }
                                    pseudoElementSel = (SimpleSelector)o;
                                    break;
                                }
                                default: {
                                    this.jj_la1[62] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 9:
                                case 11:
                                case 18:
                                case 20: {
                                    continue;
                                }
                                default: {
                                    this.jj_la1[63] = this.jj_gen;
                                    break Label_0690;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[64] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
            if (c != null) {
                simpleSel = (SimpleSelector)this.getSelectorFactory().createConditionalSelector(simpleSel, c);
            }
            if (sel == null) {
                sel = (Selector)simpleSel;
            }
            else {
                switch (comb) {
                    case ' ': {
                        sel = (Selector)this.getSelectorFactory().createDescendantSelector(sel, simpleSel);
                        break;
                    }
                    case '+': {
                        sel = (Selector)this.getSelectorFactory().createDirectAdjacentSelector(sel.getSelectorType(), sel, simpleSel);
                        break;
                    }
                    case '>': {
                        sel = (Selector)this.getSelectorFactory().createChildSelector(sel, simpleSel);
                        break;
                    }
                }
            }
            if (pseudoElementSel != null) {
                sel = (Selector)this.getSelectorFactory().createDescendantSelector(sel, pseudoElementSel);
            }
            return sel;
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidSimpleSelector", e);
        }
    }
    
    public final Condition _class(final Condition pred, final boolean pseudoElementFound) throws ParseException {
        ParseException pe = null;
        try {
            if (pseudoElementFound) {
                pe = this.generateParseException();
            }
            this.jj_consume_token(9);
            final Locator locator = this.createLocator(this.token);
            final Token t = this.jj_consume_token(58);
            if (pseudoElementFound) {
                throw pe;
            }
            final Condition c = (Condition)this.getConditionFactory().createClassCondition(null, t.image, locator);
            return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidClassSelector", e);
        }
    }
    
    public final SimpleSelector elementName() throws ParseException {
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 58: {
                    final Token t = this.jj_consume_token(58);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, this.unescape(t.image, false), this.createLocator(this.token));
                }
                case 12: {
                    this.jj_consume_token(12);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, null, this.createLocator(this.token));
                }
                default: {
                    this.jj_la1[65] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidElementName", e);
        }
    }
    
    public final Condition attrib(final Condition pred, final boolean pseudoElementFound) throws ParseException {
        String name = null;
        String value = null;
        int type = 0;
        try {
            this.jj_consume_token(18);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[66] = this.jj_gen;
                        if (pseudoElementFound) {
                            throw this.generateParseException();
                        }
                        Token t = this.jj_consume_token(58);
                        name = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[67] = this.jj_gen;
                                    Label_0597: {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 16:
                                            case 27:
                                            case 28: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 16: {
                                                        this.jj_consume_token(16);
                                                        type = 1;
                                                        break;
                                                    }
                                                    case 27: {
                                                        this.jj_consume_token(27);
                                                        type = 2;
                                                        break;
                                                    }
                                                    case 28: {
                                                        this.jj_consume_token(28);
                                                        type = 3;
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[68] = this.jj_gen;
                                                        this.jj_consume_token(-1);
                                                        throw new ParseException();
                                                    }
                                                }
                                                while (true) {
                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                        case 1: {
                                                            this.jj_consume_token(1);
                                                            continue;
                                                        }
                                                        default: {
                                                            this.jj_la1[69] = this.jj_gen;
                                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                case 58: {
                                                                    t = this.jj_consume_token(58);
                                                                    value = t.image;
                                                                    break;
                                                                }
                                                                case 21: {
                                                                    t = this.jj_consume_token(21);
                                                                    value = this.unescape(t.image, false);
                                                                    break;
                                                                }
                                                                default: {
                                                                    this.jj_la1[70] = this.jj_gen;
                                                                    this.jj_consume_token(-1);
                                                                    throw new ParseException();
                                                                }
                                                            }
                                                            while (true) {
                                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                    case 1: {
                                                                        this.jj_consume_token(1);
                                                                        continue;
                                                                    }
                                                                    default: {
                                                                        this.jj_la1[71] = this.jj_gen;
                                                                        break Label_0597;
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[72] = this.jj_gen;
                                                break;
                                            }
                                        }
                                    }
                                    this.jj_consume_token(19);
                                    Condition c = null;
                                    switch (type) {
                                        case 0: {
                                            c = (Condition)this.getConditionFactory().createAttributeCondition(name, (String)null, false, (String)null);
                                            break;
                                        }
                                        case 1: {
                                            c = (Condition)this.getConditionFactory().createAttributeCondition(name, (String)null, null != value, value);
                                            break;
                                        }
                                        case 2: {
                                            c = (Condition)this.getConditionFactory().createOneOfAttributeCondition(name, (String)null, null != value, value);
                                            break;
                                        }
                                        case 3: {
                                            c = (Condition)this.getConditionFactory().createBeginHyphenAttributeCondition(name, (String)null, null != value, value);
                                            break;
                                        }
                                    }
                                    if (c instanceof Locatable) {
                                        ((Locatable)c).setLocator(locator);
                                    }
                                    return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidAttrib", e);
        }
    }
    
    public final Object pseudo(final Condition pred, final boolean pseudoElementFound) throws ParseException {
        Condition c = null;
        String arg = "";
        try {
            this.jj_consume_token(11);
            final Locator locator = this.createLocator(this.token);
            Label_0775: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 58: {
                        final Token t = this.jj_consume_token(58);
                        final String s = this.unescape(t.image, false);
                        if (pseudoElementFound) {
                            throw this.toCSSParseException("duplicatePseudo", new String[] { s }, locator);
                        }
                        if ("first-line".equals(s) || "first-letter".equals(s) || "before".equals(s) || "after".equals(s)) {
                            return this.getSelectorFactory().createPseudoElementSelector(null, s, locator, false);
                        }
                        c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, s, locator, false);
                        if ("" != null) {
                            return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                        }
                        break;
                    }
                    case 56: {
                        Token t = this.jj_consume_token(56);
                        final String function = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[73] = this.jj_gen;
                                    t = this.jj_consume_token(58);
                                    final String lang = this.unescape(t.image, false);
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[74] = this.jj_gen;
                                                this.jj_consume_token(22);
                                                if (pseudoElementFound) {
                                                    throw this.toCSSParseException("duplicatePseudo", new String[] { "lang(" + lang + ")" }, locator);
                                                }
                                                c = (Condition)this.getConditionFactory().createLangCondition(lang, locator);
                                                if ("" != null) {
                                                    return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                                                }
                                                break Label_0775;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 57: {
                        Token t = this.jj_consume_token(57);
                        final String function = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[75] = this.jj_gen;
                                    t = this.jj_consume_token(58);
                                    arg = this.unescape(t.image, false);
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[76] = this.jj_gen;
                                                this.jj_consume_token(22);
                                                if (pseudoElementFound) {
                                                    throw this.toCSSParseException("duplicatePseudo", new String[] { function + arg + ")" }, locator);
                                                }
                                                c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, function + arg + ")", locator, false);
                                                if ("" != null) {
                                                    return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                                                }
                                                break Label_0775;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[77] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidPseudo", e);
        }
        return null;
    }
    
    public final Condition hash(final Condition pred, final boolean pseudoElementFound) throws ParseException {
        ParseException pe = null;
        try {
            if (pseudoElementFound) {
                pe = this.generateParseException();
            }
            final Token t = this.jj_consume_token(20);
            if (pseudoElementFound) {
                throw pe;
            }
            final Condition c = (Condition)this.getConditionFactory().createIdCondition(this.unescape(t.image.substring(1), false), this.createLocator(this.token));
            return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidHash", e);
        }
    }
    
    public final void styleDeclaration() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 58: {
                this.declaration();
                break;
            }
            default: {
                this.jj_la1[78] = this.jj_gen;
                break;
            }
        }
    Label_0054:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 10: {
                        this.jj_consume_token(10);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[80] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 58: {
                                            this.declaration();
                                            continue Label_0054;
                                        }
                                        default: {
                                            this.jj_la1[81] = this.jj_gen;
                                            continue Label_0054;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[79] = this.jj_gen;
                    }
                }
            }
            break;
        }
    }
    
    public final void declaration() throws ParseException {
        boolean priority = false;
        Locator locator = null;
        Label_0244: {
            try {
                final String p = this.property();
                locator = this.createLocator(this.token);
                this.jj_consume_token(11);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[82] = this.jj_gen;
                            final LexicalUnit e = this.expr();
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 35: {
                                    priority = this.prio();
                                    break;
                                }
                                default: {
                                    this.jj_la1[83] = this.jj_gen;
                                    break;
                                }
                            }
                            this.handleProperty(p, e, priority, locator);
                            break Label_0244;
                        }
                    }
                }
            }
            catch (final CSSParseException ex) {
                this.getErrorHandler().error(ex);
                this.getErrorHandler().warning(this.createSkipWarning("ignoringFollowingDeclarations", ex));
                this.error_skipdecl();
            }
            catch (final ParseException ex2) {
                final CSSParseException cpe = this.toCSSParseException("invalidDeclaration", ex2);
                this.getErrorHandler().error(cpe);
                this.getErrorHandler().warning(this.createSkipWarning("ignoringFollowingDeclarations", cpe));
                this.error_skipdecl();
            }
        }
    }
    
    public final boolean prio() throws ParseException {
        this.jj_consume_token(35);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[84] = this.jj_gen;
                    return true;
                }
            }
        }
    }
    
    public final LexicalUnit expr() throws ParseException {
        try {
            LexicalUnit body;
            final LexicalUnit head = body = this.term(null);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 8:
                    case 13:
                    case 14:
                    case 15:
                    case 20:
                    case 21:
                    case 24:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 57:
                    case 58:
                    case 61: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 8:
                            case 13: {
                                body = this.operator(body);
                                break;
                            }
                            default: {
                                this.jj_la1[86] = this.jj_gen;
                                break;
                            }
                        }
                        body = this.term(body);
                        continue;
                    }
                    default: {
                        this.jj_la1[85] = this.jj_gen;
                        return head;
                    }
                }
            }
        }
        catch (final ParseException ex) {
            throw this.toCSSParseException("invalidExpr", ex);
        }
    }
    
    public final LexicalUnit term(final LexicalUnit prev) throws ParseException {
        char op = ' ';
        LexicalUnit value = null;
        Locator locator = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 14:
            case 15: {
                op = this.unaryOperator();
                break;
            }
            default: {
                this.jj_la1[87] = this.jj_gen;
                break;
            }
        }
        if (op != ' ') {
            locator = this.createLocator(this.token);
        }
        Label_1113: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 54:
                case 57: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 54: {
                            final Token t = this.jj_consume_token(54);
                            try {
                                value = LexicalUnitImpl.createNumber(prev, this.intValue(op, t.image));
                            }
                            catch (final NumberFormatException e) {
                                value = LexicalUnitImpl.createNumber(prev, this.floatValue(op, t.image));
                            }
                            break Label_1113;
                        }
                        case 52: {
                            final Token t = this.jj_consume_token(52);
                            value = LexicalUnitImpl.createPercentage(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 39: {
                            final Token t = this.jj_consume_token(39);
                            value = LexicalUnitImpl.createPixel(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 40: {
                            final Token t = this.jj_consume_token(40);
                            value = LexicalUnitImpl.createCentimeter(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 41: {
                            final Token t = this.jj_consume_token(41);
                            value = LexicalUnitImpl.createMillimeter(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 42: {
                            final Token t = this.jj_consume_token(42);
                            value = LexicalUnitImpl.createInch(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 43: {
                            final Token t = this.jj_consume_token(43);
                            value = LexicalUnitImpl.createPoint(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 44: {
                            final Token t = this.jj_consume_token(44);
                            value = LexicalUnitImpl.createPica(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 37: {
                            final Token t = this.jj_consume_token(37);
                            value = LexicalUnitImpl.createEm(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 38: {
                            final Token t = this.jj_consume_token(38);
                            value = LexicalUnitImpl.createEx(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 45: {
                            final Token t = this.jj_consume_token(45);
                            value = LexicalUnitImpl.createDegree(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 46: {
                            final Token t = this.jj_consume_token(46);
                            value = LexicalUnitImpl.createRadian(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 47: {
                            final Token t = this.jj_consume_token(47);
                            value = LexicalUnitImpl.createGradian(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 48: {
                            final Token t = this.jj_consume_token(48);
                            value = LexicalUnitImpl.createMillisecond(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 49: {
                            final Token t = this.jj_consume_token(49);
                            value = LexicalUnitImpl.createSecond(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 50: {
                            final Token t = this.jj_consume_token(50);
                            value = LexicalUnitImpl.createHertz(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 51: {
                            final Token t = this.jj_consume_token(51);
                            value = LexicalUnitImpl.createKiloHertz(prev, this.floatValue(op, t.image));
                            break Label_1113;
                        }
                        case 57: {
                            value = this.function(prev);
                            break Label_1113;
                        }
                        default: {
                            this.jj_la1[88] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                case 21: {
                    final Token t = this.jj_consume_token(21);
                    value = LexicalUnitImpl.createString(prev, this.unescape(t.image, false), t.image);
                    break;
                }
                case 58: {
                    final Token t = this.jj_consume_token(58);
                    value = LexicalUnitImpl.createIdent(prev, this.unescape(t.image, false));
                    break;
                }
                case 24: {
                    final Token t = this.jj_consume_token(24);
                    value = LexicalUnitImpl.createURI(prev, t.image);
                    break;
                }
                case 61: {
                    final Token t = this.jj_consume_token(61);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)39, t.image);
                    break;
                }
                case 55: {
                    value = this.rgb(prev);
                    break;
                }
                case 20: {
                    value = this.hexcolor(prev);
                    break;
                }
                case 53: {
                    final Token t = this.jj_consume_token(53);
                    final int n = this.getLastNumPos(t.image);
                    value = LexicalUnitImpl.createDimension(prev, this.floatValue(op, t.image.substring(0, n + 1)), t.image.substring(n + 1));
                    break;
                }
                case 36: {
                    final Token t = this.jj_consume_token(36);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)12, t.image);
                    break;
                }
                default: {
                    this.jj_la1[89] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        if (locator == null) {
            locator = this.createLocator(this.token);
        }
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[90] = this.jj_gen;
                    if (value instanceof Locatable) {
                        ((Locatable)value).setLocator(locator);
                    }
                    return value;
                }
            }
        }
    }
    
    public final LexicalUnit function(final LexicalUnit prev) throws ParseException {
        final Token t = this.jj_consume_token(57);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[91] = this.jj_gen;
                    final LexicalUnit params = this.expr();
                    this.jj_consume_token(22);
                    return this.functionInternal(prev, t.image, params);
                }
            }
        }
    }
    
    public final LexicalUnit rgb(final LexicalUnit prev) throws ParseException {
        this.jj_consume_token(55);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[92] = this.jj_gen;
                    final LexicalUnit params = this.expr();
                    this.jj_consume_token(22);
                    return LexicalUnitImpl.createRgbColor(prev, params);
                }
            }
        }
    }
    
    public final LexicalUnit hexcolor(final LexicalUnit prev) throws ParseException {
        final Token t = this.jj_consume_token(20);
        return this.hexcolorInternal(prev, t);
    }
    
    String skip() throws ParseException {
        final StringBuilder sb = new StringBuilder();
        int nesting = 0;
        Token t = this.getToken(0);
        if (t.image != null) {
            sb.append(t.image);
        }
        do {
            t = this.getNextToken();
            if (t.kind == 0) {
                break;
            }
            sb.append(t.image);
            this.appendUnit(t, sb);
            if (t.kind == 6) {
                ++nesting;
            }
            else {
                if (t.kind != 7) {
                    continue;
                }
                --nesting;
            }
        } while ((t.kind != 7 && t.kind != 10) || nesting > 0);
        return sb.toString();
    }
    
    void appendUnit(final Token t, final StringBuilder sb) throws ParseException {
        if (t.kind == 37) {
            sb.append("ems");
            return;
        }
        if (t.kind == 38) {
            sb.append("ex");
            return;
        }
        if (t.kind == 39) {
            sb.append("px");
            return;
        }
        if (t.kind == 40) {
            sb.append("cm");
            return;
        }
        if (t.kind == 41) {
            sb.append("mm");
            return;
        }
        if (t.kind == 42) {
            sb.append("in");
            return;
        }
        if (t.kind == 43) {
            sb.append("pt");
            return;
        }
        if (t.kind == 44) {
            sb.append("pc");
            return;
        }
        if (t.kind == 45) {
            sb.append("deg");
            return;
        }
        if (t.kind == 46) {
            sb.append("rad");
            return;
        }
        if (t.kind == 47) {
            sb.append("grad");
            return;
        }
        if (t.kind == 48) {
            sb.append("ms");
            return;
        }
        if (t.kind == 49) {
            sb.append('s');
            return;
        }
        if (t.kind == 50) {
            sb.append("hz");
            return;
        }
        if (t.kind == 51) {
            sb.append("khz");
            return;
        }
        if (t.kind == 52) {
            sb.append('%');
        }
    }
    
    void error_skipblock(final String msgKey, final CSSParseException e) throws ParseException {
        if (msgKey != null) {
            this.getErrorHandler().warning(this.createSkipWarning(msgKey, e));
        }
        int nesting = 0;
        Token t;
        do {
            t = this.getNextToken();
            if (t.kind == 6) {
                ++nesting;
            }
            else {
                if (t.kind != 7) {
                    continue;
                }
                --nesting;
            }
        } while (t.kind != 0 && (t.kind != 7 || nesting > 0));
    }
    
    void error_skipdecl() throws ParseException {
        Token t = this.getToken(1);
        if (t.kind == 6) {
            this.error_skipblock(null, null);
            return;
        }
        if (t.kind == 7) {
            return;
        }
        Token oldToken = this.token;
        while (t.kind != 10 && t.kind != 7 && t.kind != 0) {
            oldToken = t;
            t = this.getNextToken();
        }
        if (t.kind != 0) {
            this.token = oldToken;
        }
    }
    
    void error_skipAtRule() throws ParseException {
        Token t = null;
        do {
            t = this.getNextToken();
        } while (t.kind != 10 && t.kind != 0);
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
    
    private boolean jj_3R_71() {
        return this.jj_scan_token(58);
    }
    
    private boolean jj_3R_67() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_71()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_72()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_64() {
        return this.jj_3R_67();
    }
    
    private boolean jj_3R_69() {
        return this.jj_scan_token(14);
    }
    
    private boolean jj_3R_66() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_69()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_70()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_79() {
        return this.jj_scan_token(18);
    }
    
    private boolean jj_3R_63() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_66()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }
    
    private boolean jj_3R_62() {
        if (this.jj_scan_token(17)) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(1));
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_60() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_64()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_65()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_77() {
        return this.jj_scan_token(20);
    }
    
    private boolean jj_3_1() {
        return this.jj_scan_token(58) || this.jj_3R_58();
    }
    
    private boolean jj_3R_61() {
        if (this.jj_scan_token(14)) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(1));
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_59() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_61()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_62()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_63()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_76() {
        return this.jj_3R_80();
    }
    
    private boolean jj_3R_75() {
        return this.jj_3R_79();
    }
    
    private boolean jj_3R_78() {
        return this.jj_scan_token(9);
    }
    
    private boolean jj_3R_74() {
        return this.jj_3R_78();
    }
    
    private boolean jj_3R_73() {
        return this.jj_3R_77();
    }
    
    private boolean jj_3R_68() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_73()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_74()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_75()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_76()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_72() {
        return this.jj_scan_token(12);
    }
    
    private boolean jj_3R_70() {
        return this.jj_scan_token(17);
    }
    
    private boolean jj_3_2() {
        return this.jj_3R_59() || this.jj_3R_60();
    }
    
    private boolean jj_3R_58() {
        return this.jj_scan_token(11);
    }
    
    private boolean jj_3R_80() {
        return this.jj_scan_token(11);
    }
    
    private boolean jj_3R_65() {
        if (this.jj_3R_68()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_68());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private static void jj_la1_init_0() {
        SACParserCSS2.jj_la1_0 = new int[] { 100663298, 100663298, 100663298, 100663298, 0, -535553536, -1072424448, -535553536, 100663298, 100663298, 2, -535553536, 2, 2, 2, 18874368, 2, 0, 2, 2, 1075059200, 256, 2, 1075059200, 2, 1075059200, 2, 2, 2, 2, 2, 2048, 2048, 2, 0, 1024, 2, 0, 2, 2, 0, 1024, 2, 0, 2, 2, 8448, 2, 2, 147456, 2, 147456, 147458, 49152, 2, 2, 2, 256, 2, 2, 1313280, 1313280, 1313280, 1313280, 1317376, 4096, 2, 2, 402718720, 2, 2097152, 2, 402718720, 2, 2, 2, 2, 0, 0, 1024, 2, 0, 2, 0, 2, 19980544, 8448, 49152, 0, 19922944, 2, 2, 2 };
    }
    
    private static void jj_la1_init_1() {
        SACParserCSS2.jj_la1_1 = new int[] { 0, 0, 0, 0, 2, 67108869, 67108869, 67108869, 0, 0, 0, 67108871, 0, 0, 0, 0, 0, 67108864, 0, 0, 67108868, 0, 0, 67108868, 0, 67108868, 0, 0, 0, 0, 0, 67108864, 67108864, 0, 67108864, 0, 0, 67108864, 0, 0, 67108864, 0, 0, 67108864, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67108864, 67108864, 0, 0, 0, 0, 67108864, 0, 0, 0, 0, 0, 0, 117440512, 67108864, 0, 0, 67108864, 0, 8, 0, 654311408, 0, 0, 39845856, 654311408, 0, 0, 0 };
    }
    
    private static void jj_la1_init_2() {
        SACParserCSS2.jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
    
    public SACParserCSS2(final CharStream stream) {
        this.jj_la1 = new int[93];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.trace_indent = 0;
        this.token_source = new SACParserCSS2TokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 93; ++i) {
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
        for (int i = 0; i < 93; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public SACParserCSS2(final SACParserCSS2TokenManager tm) {
        this.jj_la1 = new int[93];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.trace_indent = 0;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 93; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final SACParserCSS2TokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 93; ++i) {
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
    
    private int jj_ntk_f() {
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
            for (final int[] oldentry : this.jj_expentries) {
                if (oldentry.length == this.jj_expentry.length) {
                    boolean isMatched = true;
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            isMatched = false;
                            break;
                        }
                    }
                    if (isMatched) {
                        this.jj_expentries.add(this.jj_expentry);
                        break;
                    }
                    continue;
                }
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] la1tokens = new boolean[80];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 93; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((SACParserCSS2.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((SACParserCSS2.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((SACParserCSS2.jj_la1_2[i] & 1 << j) != 0x0) {
                        la1tokens[64 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 80; ++i) {
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
        return new ParseException(this.token, exptokseq, SACParserCSS2.tokenImage);
    }
    
    public final boolean trace_enabled() {
        return this.trace_enabled;
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
        jj_la1_init_2();
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
