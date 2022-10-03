package com.steadystate.css.parser;

import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.ConditionFactory;
import java.io.IOException;
import org.w3c.css.sac.InputSource;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import com.steadystate.css.util.LangUtils;
import java.util.LinkedList;
import org.w3c.css.sac.LexicalUnit;
import com.steadystate.css.dom.CSSValueImpl;
import org.w3c.dom.css.CSSValue;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.media.MediaQuery;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.CSSParseException;
import java.util.List;

public class SACParserCSS3 extends AbstractSACParser implements SACParserCSS3Constants
{
    public SACParserCSS3TokenManager token_source;
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
    private static int[] jj_la1_3;
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
    
    public SACParserCSS3() {
        this((CharStream)null);
    }
    
    @Override
    public String getParserVersion() {
        return "http://www.w3.org/Style/CSS/";
    }
    
    @Override
    protected String getGrammarUri() {
        return "http://www.w3.org/TR/WD-css3-syntax-20030813";
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
                case 48:
                case 49: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        case 48: {
                            this.jj_consume_token(48);
                            continue;
                        }
                        case 49: {
                            this.jj_consume_token(49);
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
                            case 77: {
                                this.charsetRule();
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1:
                                        case 48:
                                        case 49: {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 1: {
                                                    this.jj_consume_token(1);
                                                    continue Label_0403_Outer;
                                                }
                                                case 48: {
                                                    this.jj_consume_token(48);
                                                    continue Label_0403_Outer;
                                                }
                                                case 49: {
                                                    this.jj_consume_token(49);
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
                            Label_1056: {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 22:
                                    case 59:
                                    case 61:
                                    case 62:
                                    case 66:
                                    case 72:
                                    case 73:
                                    case 74:
                                    case 75:
                                    case 76:
                                    case 104: {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 73: {
                                                this.importRule(ruleFound);
                                                break Label_1056;
                                            }
                                            case 22:
                                            case 59:
                                            case 61:
                                            case 62:
                                            case 66:
                                            case 72:
                                            case 74:
                                            case 75:
                                            case 76:
                                            case 104: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 22:
                                                    case 59:
                                                    case 61:
                                                    case 62:
                                                    case 66:
                                                    case 72: {
                                                        this.styleRule();
                                                        break;
                                                    }
                                                    case 75: {
                                                        this.mediaRule();
                                                        break;
                                                    }
                                                    case 74: {
                                                        this.pageRule();
                                                        break;
                                                    }
                                                    case 76: {
                                                        this.fontFaceRule();
                                                        break;
                                                    }
                                                    case 104: {
                                                        this.unknownAtRule();
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[5] = this.jj_gen;
                                                        this.jj_consume_token(-1);
                                                        throw new ParseException();
                                                    }
                                                }
                                                ruleFound = true;
                                                break Label_1056;
                                            }
                                            default: {
                                                this.jj_la1[6] = this.jj_gen;
                                                this.jj_consume_token(-1);
                                                throw new ParseException();
                                            }
                                        }
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[7] = this.jj_gen;
                                        final ParseException e = this.generateParseException();
                                        this.invalidRule();
                                        Token t = this.getNextToken();
                                        boolean charsetProcessed = false;
                                        if (t.kind == 77) {
                                            t = this.getNextToken();
                                            if (t.kind == 1) {
                                                t = this.getNextToken();
                                                if (t.kind == 25) {
                                                    t = this.getNextToken();
                                                    if (t.kind == 60) {
                                                        this.getNextToken();
                                                        charsetProcessed = true;
                                                    }
                                                }
                                            }
                                            final CSSParseException cpe = this.toCSSParseException("misplacedCharsetRule", e);
                                            this.getErrorHandler().error(cpe);
                                            this.getErrorHandler().warning(this.createSkipWarning("ignoringRule", cpe));
                                        }
                                        if (charsetProcessed) {
                                            break;
                                        }
                                        if (t.kind == 0 && "" != null) {
                                            return;
                                        }
                                        final CSSParseException cpe = this.toCSSParseException("invalidRule", e);
                                        this.getErrorHandler().error(cpe);
                                        this.getErrorHandler().warning(this.createSkipWarning("ignoringRule", cpe));
                                        while (t.kind != 56 && t.kind != 0) {
                                            t = this.getNextToken();
                                        }
                                        if (t.kind == 0 && "" != null) {
                                            return;
                                        }
                                        break;
                                    }
                                }
                            }
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1:
                                    case 48:
                                    case 49: {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue Label_0403_Outer;
                                            }
                                            case 48: {
                                                this.jj_consume_token(48);
                                                continue Label_0403_Outer;
                                            }
                                            case 49: {
                                                this.jj_consume_token(49);
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
                        }
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void invalidRule() throws ParseException {
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
                        case 77: {
                            this.charsetRule();
                            break;
                        }
                        case 73: {
                            this.importRule(false);
                            break;
                        }
                        case 22:
                        case 59:
                        case 61:
                        case 62:
                        case 66:
                        case 72: {
                            this.styleRule();
                            break;
                        }
                        case 75: {
                            this.mediaRule();
                            break;
                        }
                        case 74: {
                            this.pageRule();
                            break;
                        }
                        case 76: {
                            this.fontFaceRule();
                            break;
                        }
                        case 104: {
                            this.unknownAtRule();
                            break;
                        }
                        default: {
                            this.jj_la1[11] = this.jj_gen;
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
                                this.jj_la1[12] = this.jj_gen;
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public final void charsetRule() throws ParseException {
        try {
            this.jj_consume_token(77);
            final Locator locator = this.createLocator(this.token);
            this.jj_consume_token(1);
            final Token t = this.jj_consume_token(25);
            this.jj_consume_token(60);
            this.handleCharset(t.toString(), locator);
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidCharsetRule", e));
        }
    }
    
    public final void unknownAtRule() throws ParseException {
        try {
            this.jj_consume_token(104);
            final Locator locator = this.createLocator(this.token);
            final String s = this.skip();
            this.handleIgnorableAtRule(s, locator);
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidUnknownRule", this.generateParseException()));
        }
    }
    
    public final void importRule(final boolean nonImportRuleFoundBefore) throws ParseException {
        final SACMediaListImpl ml = new SACMediaListImpl();
        Label_0433: {
            try {
                ParseException e = null;
                if (nonImportRuleFoundBefore) {
                    e = this.generateParseException();
                }
                this.jj_consume_token(73);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[13] = this.jj_gen;
                            Token t = null;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 25: {
                                    t = this.jj_consume_token(25);
                                    break;
                                }
                                case 100: {
                                    t = this.jj_consume_token(100);
                                    break;
                                }
                                default: {
                                    this.jj_la1[14] = this.jj_gen;
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
                                        this.jj_la1[15] = this.jj_gen;
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 18:
                                            case 19:
                                            case 22:
                                            case 57: {
                                                this.mediaList(ml);
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[16] = this.jj_gen;
                                                break;
                                            }
                                        }
                                        this.jj_consume_token(60);
                                        if (nonImportRuleFoundBefore) {
                                            this.getErrorHandler().error(this.toCSSParseException("invalidImportRuleIgnored2", e));
                                        }
                                        else {
                                            this.handleImportStyle(this.unescape(t.image, false), (SACMediaList)ml, null, locator);
                                        }
                                        break Label_0433;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            catch (final CSSParseException e2) {
                this.getErrorHandler().error(e2);
                this.error_skipAtRule();
            }
            catch (final ParseException e) {
                this.getErrorHandler().error(this.toCSSParseException("invalidImportRule", e));
                this.error_skipAtRule();
            }
        }
    }
    
    public final void mediaRule() throws ParseException {
        boolean start = false;
        final SACMediaListImpl ml = new SACMediaListImpl();
        try {
            this.jj_consume_token(75);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                        this.mediaList(ml);
                        start = true;
                        this.handleStartMedia((SACMediaList)ml, locator);
                        this.jj_consume_token(55);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[18] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 22:
                                        case 59:
                                        case 61:
                                        case 62:
                                        case 66:
                                        case 72:
                                        case 73:
                                        case 74:
                                        case 75:
                                        case 104: {
                                            this.mediaRuleList();
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[19] = this.jj_gen;
                                            break;
                                        }
                                    }
                                    this.jj_consume_token(56);
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
            this.error_skipblock("ignoringRule", e);
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
                MediaQuery mq = this.mediaQuery();
                ml.setLocator(this.createLocator(this.token));
            Label_0017:
                while (true) {
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 71: {
                                this.jj_consume_token(71);
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1: {
                                            this.jj_consume_token(1);
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[21] = this.jj_gen;
                                            ml.add(mq);
                                            mq = this.mediaQuery();
                                            continue Label_0017;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[20] = this.jj_gen;
                                ml.add(mq);
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
    
    public final MediaQuery mediaQuery() throws ParseException {
        boolean only = false;
        boolean not = false;
        MediaQuery mq = null;
        Label_0652: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 18:
                case 19:
                case 22: {
                    Label_0281: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 18:
                            case 19: {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 19: {
                                        this.jj_consume_token(19);
                                        only = true;
                                        break;
                                    }
                                    case 18: {
                                        this.jj_consume_token(18);
                                        not = true;
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[22] = this.jj_gen;
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
                                            this.jj_la1[23] = this.jj_gen;
                                            break Label_0281;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[24] = this.jj_gen;
                                break;
                            }
                        }
                    }
                    final String s = this.medium();
                    mq = new MediaQuery(s, only, not);
                    mq.setLocator(this.createLocator(this.token));
                Label_0311:
                    while (true) {
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 17: {
                                    this.jj_consume_token(17);
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[26] = this.jj_gen;
                                                final Property p = this.mediaExpression();
                                                mq.addMediaProperty(p);
                                                continue Label_0311;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[25] = this.jj_gen;
                                    break Label_0652;
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                case 57: {
                    Property p = this.mediaExpression();
                    final String s = "all";
                    this.handleMedium(s, null);
                    mq = new MediaQuery(s, only, not);
                    mq.setLocator(this.createLocator(this.token));
                    mq.addMediaProperty(p);
                Label_0491:
                    while (true) {
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 17: {
                                    this.jj_consume_token(17);
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[28] = this.jj_gen;
                                                p = this.mediaExpression();
                                                mq.addMediaProperty(p);
                                                continue Label_0491;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[27] = this.jj_gen;
                                    break Label_0652;
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                default: {
                    this.jj_la1[29] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return mq;
    }
    
    public final Property mediaExpression() throws ParseException {
        LexicalUnit e = null;
        this.jj_consume_token(57);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                    final String p = this.property();
                    Label_0205: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 61: {
                                this.jj_consume_token(61);
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1: {
                                            this.jj_consume_token(1);
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[31] = this.jj_gen;
                                            e = this.expr();
                                            break Label_0205;
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
                    this.jj_consume_token(58);
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[33] = this.jj_gen;
                                Property prop;
                                if (e == null) {
                                    prop = new Property(p, null, false);
                                }
                                else {
                                    prop = new Property(p, new CSSValueImpl(e), false);
                                }
                                return prop;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public final void mediaRuleList() throws ParseException {
    Label_0000:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 22:
                    case 59:
                    case 61:
                    case 62:
                    case 66:
                    case 72: {
                        this.styleRule();
                        break;
                    }
                    case 75: {
                        this.mediaRule();
                        break;
                    }
                    case 74: {
                        this.pageRule();
                        break;
                    }
                    case 73: {
                        this.importRule(true);
                        break;
                    }
                    case 104: {
                        this.unknownAtRule();
                        break;
                    }
                    default: {
                        this.jj_la1[34] = this.jj_gen;
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
                            this.jj_la1[35] = this.jj_gen;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 22:
                                case 59:
                                case 61:
                                case 62:
                                case 66:
                                case 72:
                                case 73:
                                case 74:
                                case 75:
                                case 104: {
                                    continue Label_0000;
                                }
                                default: {
                                    this.jj_la1[36] = this.jj_gen;
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
        final Token t = this.jj_consume_token(22);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[37] = this.jj_gen;
                    final String medium = this.unescape(t.image, false);
                    this.handleMedium(medium, this.createLocator(t));
                    return medium;
                }
            }
        }
    }
    
    public final void pageRule() throws ParseException {
        String sel = null;
        boolean start = false;
        try {
            this.jj_consume_token(74);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[38] = this.jj_gen;
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 22:
                            case 61: {
                                sel = this.pageSelectorList();
                                break;
                            }
                            default: {
                                this.jj_la1[39] = this.jj_gen;
                                break;
                            }
                        }
                        this.jj_consume_token(55);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[40] = this.jj_gen;
                                    start = true;
                                    this.handleStartPage(null, sel, locator);
                                    this.styleDeclaration();
                                    this.jj_consume_token(56);
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
            this.error_skipblock("ignoringRule", e);
        }
        catch (final ParseException e2) {
            final CSSParseException cpe = this.toCSSParseException("invalidPageRule", e2);
            this.getErrorHandler().error(cpe);
            this.error_skipblock("ignoringRule", cpe);
        }
        finally {
            if (start) {
                this.handleEndPage(null, sel);
            }
        }
    }
    
    public final String pageSelectorList() throws ParseException {
        final LinkedList selectors = new LinkedList();
        String sel = this.pageSelector();
        selectors.add(sel);
    Label_0019:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 71: {
                        this.jj_consume_token(71);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[42] = this.jj_gen;
                                    sel = this.pageSelector();
                                    selectors.add(sel);
                                    continue Label_0019;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[41] = this.jj_gen;
                        return LangUtils.join(selectors, ", ");
                    }
                }
            }
            break;
        }
    }
    
    public final String pageSelector() throws ParseException {
        final StringBuilder pseudos = new StringBuilder();
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 61: {
                final String pseudo = this.pseudoPage();
                pseudos.append(pseudo);
                break;
            }
            case 22: {
                final Token ident = this.jj_consume_token(22);
                pseudos.append(this.unescape(ident.image, false));
                break;
            }
            default: {
                this.jj_la1[43] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 61: {
                    final String pseudo = this.pseudoPage();
                    pseudos.append(pseudo);
                    continue;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[45] = this.jj_gen;
                                return pseudos.toString();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public final String pseudoPage() throws ParseException {
        this.jj_consume_token(61);
        final Token t = this.jj_consume_token(22);
        return ":" + this.unescape(t.image, false);
    }
    
    public final void fontFaceRule() throws ParseException {
        boolean start = false;
        Label_0205: {
            try {
                this.jj_consume_token(76);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[46] = this.jj_gen;
                            this.jj_consume_token(55);
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 1: {
                                        this.jj_consume_token(1);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[47] = this.jj_gen;
                                        start = true;
                                        this.handleStartFontFace(locator);
                                        this.styleDeclaration();
                                        this.jj_consume_token(56);
                                        break Label_0205;
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
            case 63: {
                this.jj_consume_token(63);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[48] = this.jj_gen;
                            return (LexicalUnit)new LexicalUnitImpl(prev, (short)4);
                        }
                    }
                }
                break;
            }
            case 71: {
                this.jj_consume_token(71);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[49] = this.jj_gen;
                            return LexicalUnitImpl.createComma(prev);
                        }
                    }
                }
                break;
            }
            default: {
                this.jj_la1[50] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final char combinator() throws ParseException {
        char c = ' ';
        Label_0541: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 68: {
                    this.jj_consume_token(68);
                    c = '+';
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[51] = this.jj_gen;
                                break Label_0541;
                            }
                        }
                    }
                    break;
                }
                case 69: {
                    this.jj_consume_token(69);
                    c = '>';
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[52] = this.jj_gen;
                                break Label_0541;
                            }
                        }
                    }
                    break;
                }
                case 70: {
                    this.jj_consume_token(70);
                    c = '~';
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 1: {
                                this.jj_consume_token(1);
                                continue;
                            }
                            default: {
                                this.jj_la1[53] = this.jj_gen;
                                break Label_0541;
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    this.jj_consume_token(1);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 68:
                        case 69:
                        case 70: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 68: {
                                    this.jj_consume_token(68);
                                    c = '+';
                                    break;
                                }
                                case 69: {
                                    this.jj_consume_token(69);
                                    c = '>';
                                    break;
                                }
                                case 70: {
                                    this.jj_consume_token(70);
                                    c = '~';
                                    break;
                                }
                                default: {
                                    this.jj_la1[54] = this.jj_gen;
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
                                        this.jj_la1[55] = this.jj_gen;
                                        break Label_0541;
                                    }
                                }
                            }
                            break;
                        }
                        default: {
                            this.jj_la1[56] = this.jj_gen;
                            break Label_0541;
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[57] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return c;
    }
    
    public final char unaryOperator() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 64: {
                this.jj_consume_token(64);
                return '-';
            }
            case 68: {
                this.jj_consume_token(68);
                return '+';
            }
            default: {
                this.jj_la1[58] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final String property() throws ParseException {
        final Token t = this.jj_consume_token(22);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[59] = this.jj_gen;
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
            this.jj_consume_token(55);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[60] = this.jj_gen;
                        start = true;
                        this.handleStartSelector(selList, this.createLocator(t.next));
                        this.styleDeclaration();
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 56: {
                                this.jj_consume_token(56);
                                break;
                            }
                            case 0: {
                                this.jj_consume_token(0);
                                break;
                            }
                            default: {
                                this.jj_la1[61] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
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
                    this.jj_la1[62] = this.jj_gen;
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
                    case 71: {
                        this.jj_consume_token(71);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[64] = this.jj_gen;
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
                        this.jj_la1[63] = this.jj_gen;
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
            while (this.jj_2_1(2)) {
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
                        this.jj_la1[65] = this.jj_gen;
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
            Label_0686: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 22:
                    case 62: {
                        simpleSel = this.elementName();
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 59:
                                case 61:
                                case 66:
                                case 72: {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 72: {
                                            c = this.hash(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 59: {
                                            c = this._class(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 66: {
                                            c = this.attrib(c, null != pseudoElementSel);
                                            continue;
                                        }
                                        case 61: {
                                            o = this.pseudo(c, null != pseudoElementSel);
                                            if (o instanceof Condition) {
                                                c = (Condition)o;
                                                continue;
                                            }
                                            pseudoElementSel = (SimpleSelector)o;
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[67] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ParseException();
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[66] = this.jj_gen;
                                    break Label_0686;
                                }
                            }
                        }
                        break;
                    }
                    case 59:
                    case 61:
                    case 66:
                    case 72: {
                        simpleSel = (SimpleSelector)this.getSelectorFactory().createSyntheticElementSelector();
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 72: {
                                    c = this.hash(c, null != pseudoElementSel);
                                    break;
                                }
                                case 59: {
                                    c = this._class(c, null != pseudoElementSel);
                                    break;
                                }
                                case 66: {
                                    c = this.attrib(c, null != pseudoElementSel);
                                    break;
                                }
                                case 61: {
                                    o = this.pseudo(c, null != pseudoElementSel);
                                    if (o instanceof Condition) {
                                        c = (Condition)o;
                                        break;
                                    }
                                    pseudoElementSel = (SimpleSelector)o;
                                    break;
                                }
                                default: {
                                    this.jj_la1[68] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 59:
                                case 61:
                                case 66:
                                case 72: {
                                    continue;
                                }
                                default: {
                                    this.jj_la1[69] = this.jj_gen;
                                    break Label_0686;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[70] = this.jj_gen;
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
                    case '~': {
                        sel = (Selector)this.getSelectorFactory().createGeneralAdjacentSelector(sel.getSelectorType(), sel, simpleSel);
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
            this.jj_consume_token(59);
            final Locator locator = this.createLocator(this.token);
            final Token t = this.jj_consume_token(22);
            if (pseudoElementFound) {
                throw pe;
            }
            final Condition c = (Condition)this.getConditionFactory().createClassCondition(null, this.unescape(t.image, false), locator);
            return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidClassSelector", e);
        }
    }
    
    public final SimpleSelector elementName() throws ParseException {
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 22: {
                    final Token t = this.jj_consume_token(22);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, this.unescape(t.image, false), this.createLocator(this.token));
                }
                case 62: {
                    this.jj_consume_token(62);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, null, this.createLocator(this.token));
                }
                default: {
                    this.jj_la1[71] = this.jj_gen;
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
            this.jj_consume_token(66);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[72] = this.jj_gen;
                        if (pseudoElementFound) {
                            throw this.generateParseException();
                        }
                        Token t = this.jj_consume_token(22);
                        name = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[73] = this.jj_gen;
                                    Label_0729: {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 50:
                                            case 51:
                                            case 52:
                                            case 53:
                                            case 54:
                                            case 65: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 52: {
                                                        this.jj_consume_token(52);
                                                        type = 4;
                                                        break;
                                                    }
                                                    case 53: {
                                                        this.jj_consume_token(53);
                                                        type = 5;
                                                        break;
                                                    }
                                                    case 54: {
                                                        this.jj_consume_token(54);
                                                        type = 6;
                                                        break;
                                                    }
                                                    case 65: {
                                                        this.jj_consume_token(65);
                                                        type = 1;
                                                        break;
                                                    }
                                                    case 50: {
                                                        this.jj_consume_token(50);
                                                        type = 2;
                                                        break;
                                                    }
                                                    case 51: {
                                                        this.jj_consume_token(51);
                                                        type = 3;
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[74] = this.jj_gen;
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
                                                            this.jj_la1[75] = this.jj_gen;
                                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                                case 22: {
                                                                    t = this.jj_consume_token(22);
                                                                    value = this.unescape(t.image, false);
                                                                    break;
                                                                }
                                                                case 25: {
                                                                    t = this.jj_consume_token(25);
                                                                    value = this.unescape(t.image, false);
                                                                    break;
                                                                }
                                                                default: {
                                                                    this.jj_la1[76] = this.jj_gen;
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
                                                                        this.jj_la1[77] = this.jj_gen;
                                                                        break Label_0729;
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
                                                this.jj_la1[78] = this.jj_gen;
                                                break;
                                            }
                                        }
                                    }
                                    this.jj_consume_token(67);
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
                                        case 4: {
                                            c = (Condition)this.getConditionFactory().createPrefixAttributeCondition(name, null, null != value, value);
                                            break;
                                        }
                                        case 5: {
                                            c = (Condition)this.getConditionFactory().createSuffixAttributeCondition(name, null, null != value, value);
                                            break;
                                        }
                                        case 6: {
                                            c = (Condition)this.getConditionFactory().createSubstringAttributeCondition(name, null, null != value, value);
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
        boolean doubleColon = false;
        try {
            this.jj_consume_token(61);
            final Locator locator = this.createLocator(this.token);
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 61: {
                    this.jj_consume_token(61);
                    doubleColon = true;
                    break;
                }
                default: {
                    this.jj_la1[79] = this.jj_gen;
                    break;
                }
            }
            Label_1447: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 22: {
                        final Token t = this.jj_consume_token(22);
                        final String s = this.unescape(t.image, false);
                        if (pseudoElementFound) {
                            throw this.toCSSParseException("duplicatePseudo", new String[] { s }, locator);
                        }
                        if ("first-line".equals(s) || "first-letter".equals(s) || "before".equals(s) || "after".equals(s)) {
                            return this.getSelectorFactory().createPseudoElementSelector(null, s, locator, doubleColon);
                        }
                        c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, s, locator, doubleColon);
                        if ("" != null) {
                            return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                        }
                        break;
                    }
                    case 101: {
                        final Token t = this.jj_consume_token(101);
                        final String function = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[80] = this.jj_gen;
                                    final Selector sel = this.negation_arg();
                                    String arg = ((CSSFormatable)sel).getCssText(null);
                                    if ("".equals(arg)) {
                                        arg = "*";
                                    }
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[81] = this.jj_gen;
                                                this.jj_consume_token(58);
                                                if (pseudoElementFound) {
                                                    throw this.toCSSParseException("duplicatePseudo", new String[] { function + arg + ")" }, locator);
                                                }
                                                c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, function + arg + ")", locator, doubleColon);
                                                if ("" != null) {
                                                    return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                                                }
                                                break Label_1447;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 102: {
                        Token t = this.jj_consume_token(102);
                        final String function = this.unescape(t.image, false);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[82] = this.jj_gen;
                                    t = this.jj_consume_token(22);
                                    final String lang = this.unescape(t.image, false);
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1: {
                                                this.jj_consume_token(1);
                                                continue;
                                            }
                                            default: {
                                                this.jj_la1[83] = this.jj_gen;
                                                this.jj_consume_token(58);
                                                if (pseudoElementFound) {
                                                    throw this.toCSSParseException("duplicatePseudo", new String[] { "lang(" + lang + ")" }, locator);
                                                }
                                                c = (Condition)this.getConditionFactory().createLangCondition(lang, locator);
                                                if ("" != null) {
                                                    return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                                                }
                                                break Label_1447;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 103: {
                        Token t = this.jj_consume_token(103);
                        final String function = this.unescape(t.image, false);
                        final StringBuilder args = new StringBuilder();
                    Label_0934_Outer:
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[84] = this.jj_gen;
                                Label_0934:
                                    while (true) {
                                        while (true) {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 68: {
                                                    t = this.jj_consume_token(68);
                                                    break;
                                                }
                                                case 64: {
                                                    t = this.jj_consume_token(64);
                                                    break;
                                                }
                                                case 97: {
                                                    t = this.jj_consume_token(97);
                                                    break;
                                                }
                                                case 20: {
                                                    t = this.jj_consume_token(20);
                                                    break;
                                                }
                                                case 25: {
                                                    t = this.jj_consume_token(25);
                                                    break;
                                                }
                                                case 22: {
                                                    t = this.jj_consume_token(22);
                                                    break;
                                                }
                                                default: {
                                                    this.jj_la1[85] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                            }
                                            args.append(this.unescape(t.image, false));
                                            while (true) {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 1: {
                                                        t = this.jj_consume_token(1);
                                                        args.append(this.unescape(t.image, false));
                                                        continue Label_0934_Outer;
                                                    }
                                                    default: {
                                                        this.jj_la1[86] = this.jj_gen;
                                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                            case 20:
                                                            case 22:
                                                            case 25:
                                                            case 64:
                                                            case 68:
                                                            case 97: {
                                                                continue Label_0934;
                                                            }
                                                            default: {
                                                                this.jj_la1[87] = this.jj_gen;
                                                                this.jj_consume_token(58);
                                                                if (pseudoElementFound) {
                                                                    throw this.toCSSParseException("duplicatePseudo", new String[] { function + args.toString().trim() + ")" }, locator);
                                                                }
                                                                c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, function + args.toString().trim() + ")", locator, doubleColon);
                                                                if ("" != null) {
                                                                    return (pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c);
                                                                }
                                                                break Label_1447;
                                                            }
                                                        }
                                                        break;
                                                    }
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
                    default: {
                        this.jj_la1[88] = this.jj_gen;
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
            final Token t = this.jj_consume_token(72);
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
            case 22:
            case 62: {
                this.declaration();
                break;
            }
            default: {
                this.jj_la1[89] = this.jj_gen;
                break;
            }
        }
    Label_0062:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 60: {
                        this.jj_consume_token(60);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[91] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 22:
                                        case 62: {
                                            this.declaration();
                                            continue Label_0062;
                                        }
                                        case 63: {
                                            this.error_skipdecl();
                                            continue Label_0062;
                                        }
                                        default: {
                                            this.jj_la1[92] = this.jj_gen;
                                            continue Label_0062;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[90] = this.jj_gen;
                    }
                }
            }
            break;
        }
    }
    
    public final void declaration() throws ParseException {
        boolean priority = false;
        Locator starHack = null;
        Locator locator = null;
        Label_0486: {
            try {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 62: {
                        this.jj_consume_token(62);
                        starHack = this.createLocator(this.token);
                        break;
                    }
                    default: {
                        this.jj_la1[93] = this.jj_gen;
                        break;
                    }
                }
                final String p = this.property();
                locator = this.createLocator(this.token);
                this.jj_consume_token(61);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[94] = this.jj_gen;
                            final LexicalUnit e = this.expr();
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 78: {
                                    priority = this.prio();
                                    break;
                                }
                                default: {
                                    this.jj_la1[95] = this.jj_gen;
                                    break;
                                }
                            }
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 57:
                                case 61:
                                case 105: {
                                    final Token t = this.jj_consume_token(this.jj_ntk);
                                    locator = this.createLocator(this.token);
                                    final CSSParseException cpe = this.toCSSParseException("invalidDeclarationInvalidChar", new String[] { t.image }, locator);
                                    this.getErrorHandler().error(cpe);
                                    this.error_skipdecl();
                                    break;
                                }
                                default: {
                                    this.jj_la1[96] = this.jj_gen;
                                    break;
                                }
                            }
                            if (starHack != null) {
                                if (this.isIeStarHackAccepted()) {
                                    this.handleProperty("*" + p, e, priority, locator);
                                    if ("" != null) {
                                        return;
                                    }
                                }
                                final CSSParseException cpe = this.toCSSParseException("invalidDeclarationStarHack", new Object[0], starHack);
                                this.getErrorHandler().error(cpe);
                                if ("" != null) {
                                    return;
                                }
                            }
                            this.handleProperty(p, e, priority, locator);
                            break Label_0486;
                        }
                    }
                }
            }
            catch (final CSSParseException ex) {
                this.getErrorHandler().error(ex);
                this.error_skipdecl();
            }
            catch (final ParseException ex2) {
                final CSSParseException cpe2 = this.toCSSParseException("invalidDeclaration", ex2);
                this.getErrorHandler().error(cpe2);
                this.error_skipdecl();
            }
        }
    }
    
    public final boolean prio() throws ParseException {
        this.jj_consume_token(78);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[97] = this.jj_gen;
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
                    case 20:
                    case 21:
                    case 22:
                    case 25:
                    case 63:
                    case 64:
                    case 68:
                    case 71:
                    case 72:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    case 99:
                    case 100:
                    case 103:
                    case 106: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 63:
                            case 71: {
                                body = this.operator(body);
                                break;
                            }
                            default: {
                                this.jj_la1[99] = this.jj_gen;
                                break;
                            }
                        }
                        body = this.term(body);
                        continue;
                    }
                    default: {
                        this.jj_la1[98] = this.jj_gen;
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
            case 64:
            case 68: {
                op = this.unaryOperator();
                break;
            }
            default: {
                this.jj_la1[100] = this.jj_gen;
                break;
            }
        }
        if (op != ' ') {
            locator = this.createLocator(this.token);
        }
        Label_1689: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 20:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 103: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 20: {
                            final Token t = this.jj_consume_token(20);
                            try {
                                value = LexicalUnitImpl.createNumber(prev, this.intValue(op, t.image));
                            }
                            catch (final NumberFormatException e) {
                                value = LexicalUnitImpl.createNumber(prev, this.floatValue(op, t.image));
                            }
                            break Label_1689;
                        }
                        case 96: {
                            final Token t = this.jj_consume_token(96);
                            value = LexicalUnitImpl.createPercentage(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 81: {
                            final Token t = this.jj_consume_token(81);
                            value = LexicalUnitImpl.createPixel(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 82: {
                            final Token t = this.jj_consume_token(82);
                            value = LexicalUnitImpl.createCentimeter(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 83: {
                            final Token t = this.jj_consume_token(83);
                            value = LexicalUnitImpl.createMillimeter(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 84: {
                            final Token t = this.jj_consume_token(84);
                            value = LexicalUnitImpl.createInch(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 85: {
                            final Token t = this.jj_consume_token(85);
                            value = LexicalUnitImpl.createPoint(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 86: {
                            final Token t = this.jj_consume_token(86);
                            value = LexicalUnitImpl.createPica(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 79: {
                            final Token t = this.jj_consume_token(79);
                            value = LexicalUnitImpl.createEm(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 80: {
                            final Token t = this.jj_consume_token(80);
                            value = LexicalUnitImpl.createEx(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 87: {
                            final Token t = this.jj_consume_token(87);
                            value = LexicalUnitImpl.createDegree(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 88: {
                            final Token t = this.jj_consume_token(88);
                            value = LexicalUnitImpl.createRadian(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 89: {
                            final Token t = this.jj_consume_token(89);
                            value = LexicalUnitImpl.createGradian(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 90: {
                            final Token t = this.jj_consume_token(90);
                            value = LexicalUnitImpl.createMillisecond(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 91: {
                            final Token t = this.jj_consume_token(91);
                            value = LexicalUnitImpl.createSecond(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 92: {
                            final Token t = this.jj_consume_token(92);
                            value = LexicalUnitImpl.createHertz(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 93: {
                            final Token t = this.jj_consume_token(93);
                            value = LexicalUnitImpl.createKiloHertz(prev, this.floatValue(op, t.image));
                            break Label_1689;
                        }
                        case 94: {
                            final Token t = this.jj_consume_token(94);
                            value = LexicalUnitImpl.createDimension(prev, this.floatValue(op, t.image), "dpi");
                            break Label_1689;
                        }
                        case 95: {
                            final Token t = this.jj_consume_token(95);
                            value = LexicalUnitImpl.createDimension(prev, this.floatValue(op, t.image), "dpcm");
                            break Label_1689;
                        }
                        case 103: {
                            value = this.function(prev);
                            break Label_1689;
                        }
                        default: {
                            this.jj_la1[101] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                case 25: {
                    final Token t = this.jj_consume_token(25);
                    value = LexicalUnitImpl.createString(prev, this.unescape(t.image, false), t.image);
                    break;
                }
                case 106: {
                    final Token t = this.jj_consume_token(106);
                    value = LexicalUnitImpl.createIdent(prev, this.skipUnit().trim());
                    break;
                }
                case 22: {
                    final Token t = this.jj_consume_token(22);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 61: {
                            this.jj_consume_token(61);
                            throw this.toCSSParseException("invalidExprColon", new String[] { this.unescape(t.image, false) }, this.createLocator(this.token));
                        }
                        default: {
                            this.jj_la1[102] = this.jj_gen;
                            value = LexicalUnitImpl.createIdent(prev, this.unescape(t.image, false));
                            break Label_1689;
                        }
                    }
                    break;
                }
                case 100: {
                    final Token t = this.jj_consume_token(100);
                    value = LexicalUnitImpl.createURI(prev, this.unescape(t.image, true));
                    break;
                }
                case 99: {
                    value = this.unicodeRange(prev);
                    break;
                }
                case 72: {
                    value = this.hexcolor(prev);
                    break;
                }
                case 97: {
                    final Token t = this.jj_consume_token(97);
                    final int n = this.getLastNumPos(t.image);
                    value = LexicalUnitImpl.createDimension(prev, this.floatValue(op, t.image.substring(0, n + 1)), t.image.substring(n + 1));
                    break;
                }
                case 21: {
                    final Token t = this.jj_consume_token(21);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)12, t.image);
                    break;
                }
                default: {
                    this.jj_la1[103] = this.jj_gen;
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
                    this.jj_la1[104] = this.jj_gen;
                    if (value instanceof Locatable) {
                        ((Locatable)value).setLocator(locator);
                    }
                    return value;
                }
            }
        }
    }
    
    public final LexicalUnit function(final LexicalUnit prev) throws ParseException {
        LexicalUnit param = null;
        LexicalUnit body = null;
        String funct = "";
        Token t = this.jj_consume_token(103);
        funct += this.unescape(t.image, false);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[105] = this.jj_gen;
                    Label_1151: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 20:
                            case 21:
                            case 22:
                            case 25:
                            case 64:
                            case 68:
                            case 72:
                            case 79:
                            case 80:
                            case 81:
                            case 82:
                            case 83:
                            case 84:
                            case 85:
                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                            case 91:
                            case 92:
                            case 93:
                            case 94:
                            case 95:
                            case 96:
                            case 97:
                            case 99:
                            case 100:
                            case 103:
                            case 106: {
                                param = (body = this.term(null));
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 20:
                                        case 21:
                                        case 22:
                                        case 25:
                                        case 64:
                                        case 65:
                                        case 68:
                                        case 71:
                                        case 72:
                                        case 79:
                                        case 80:
                                        case 81:
                                        case 82:
                                        case 83:
                                        case 84:
                                        case 85:
                                        case 86:
                                        case 87:
                                        case 88:
                                        case 89:
                                        case 90:
                                        case 91:
                                        case 92:
                                        case 93:
                                        case 94:
                                        case 95:
                                        case 96:
                                        case 97:
                                        case 99:
                                        case 100:
                                        case 103:
                                        case 106: {
                                            Label_1129: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 65:
                                                    case 71: {
                                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                            case 71: {
                                                                t = this.jj_consume_token(71);
                                                                body = LexicalUnitImpl.createComma(body);
                                                                break;
                                                            }
                                                            case 65: {
                                                                t = this.jj_consume_token(65);
                                                                body = LexicalUnitImpl.createIdent(body, t.image);
                                                                break;
                                                            }
                                                            default: {
                                                                this.jj_la1[107] = this.jj_gen;
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
                                                                    this.jj_la1[108] = this.jj_gen;
                                                                    break Label_1129;
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        this.jj_la1[109] = this.jj_gen;
                                                        break;
                                                    }
                                                }
                                            }
                                            body = this.term(body);
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[106] = this.jj_gen;
                                            break Label_1151;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[110] = this.jj_gen;
                                break;
                            }
                        }
                    }
                    this.jj_consume_token(58);
                    return this.functionInternal(prev, funct, param);
                }
            }
        }
    }
    
    public final Selector negation_arg() throws ParseException {
        Condition c = null;
        SimpleSelector simpleSel = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 22:
            case 62: {
                simpleSel = this.elementName();
                return (Selector)simpleSel;
            }
            case 72: {
                c = this.hash(null, false);
                break;
            }
            case 59: {
                c = this._class(null, false);
                break;
            }
            case 66: {
                c = this.attrib(null, false);
                break;
            }
            case 61: {
                final Object o = this.pseudo(null, false);
                if (o instanceof Condition) {
                    c = (Condition)o;
                    break;
                }
                return (Selector)this.getSelectorFactory().createDescendantSelector((Selector)null, (SimpleSelector)o);
            }
            default: {
                this.jj_la1[111] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return (Selector)this.getSelectorFactory().createConditionalSelector(simpleSel, c);
    }
    
    public final LexicalUnit unicodeRange(final LexicalUnit prev) throws ParseException {
        final StringBuilder range = new StringBuilder();
        final Token t = this.jj_consume_token(99);
        range.append(this.unescape(t.image, false));
        return LexicalUnitImpl.createIdent(prev, range.toString().toUpperCase(Locale.ROOT));
    }
    
    public final LexicalUnit hexcolor(final LexicalUnit prev) throws ParseException {
        final Token t = this.jj_consume_token(72);
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
            if (t.kind == 55) {
                ++nesting;
            }
            else {
                if (t.kind != 56) {
                    continue;
                }
                --nesting;
            }
        } while ((t.kind != 56 && t.kind != 60) || nesting > 0);
        return sb.toString();
    }
    
    String skipUnit() throws ParseException {
        final StringBuilder sb = new StringBuilder();
        Token t = this.token;
        Token oldToken = null;
        while (t.kind != 60 && t.kind != 56 && t.kind != 0) {
            oldToken = t;
            sb.append(oldToken.image);
            this.appendUnit(t, sb);
            t = this.getNextToken();
        }
        if (t.kind != 0) {
            this.token = oldToken;
        }
        return sb.toString();
    }
    
    void appendUnit(final Token t, final StringBuilder sb) throws ParseException {
        if (t.kind == 79) {
            sb.append("ems");
            return;
        }
        if (t.kind == 80) {
            sb.append("ex");
            return;
        }
        if (t.kind == 81) {
            sb.append("px");
            return;
        }
        if (t.kind == 82) {
            sb.append("cm");
            return;
        }
        if (t.kind == 83) {
            sb.append("mm");
            return;
        }
        if (t.kind == 84) {
            sb.append("in");
            return;
        }
        if (t.kind == 85) {
            sb.append("pt");
            return;
        }
        if (t.kind == 86) {
            sb.append("pc");
            return;
        }
        if (t.kind == 87) {
            sb.append("deg");
            return;
        }
        if (t.kind == 88) {
            sb.append("rad");
            return;
        }
        if (t.kind == 89) {
            sb.append("grad");
            return;
        }
        if (t.kind == 90) {
            sb.append("ms");
            return;
        }
        if (t.kind == 91) {
            sb.append('s');
            return;
        }
        if (t.kind == 92) {
            sb.append("hz");
            return;
        }
        if (t.kind == 93) {
            sb.append("khz");
            return;
        }
        if (t.kind == 94) {
            sb.append("dpi");
            return;
        }
        if (t.kind == 95) {
            sb.append("dpcm");
            return;
        }
        if (t.kind == 96) {
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
            if (t.kind == 55) {
                ++nesting;
            }
            else {
                if (t.kind != 56) {
                    continue;
                }
                --nesting;
            }
        } while (t.kind != 0 && (t.kind != 56 || nesting > 0));
    }
    
    void error_skipdecl() throws ParseException {
        Token t = this.getToken(1);
        if (t.kind == 55) {
            this.error_skipblock(null, null);
            return;
        }
        if (t.kind == 56) {
            return;
        }
        Token oldToken = this.token;
        while (t.kind != 60 && t.kind != 56 && t.kind != 0) {
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
        } while (t.kind != 60 && t.kind != 0);
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
    
    private boolean jj_3R_79() {
        return this.jj_scan_token(69);
    }
    
    private boolean jj_3_1() {
        return this.jj_3R_67() || this.jj_3R_68();
    }
    
    private boolean jj_3R_81() {
        return this.jj_scan_token(22);
    }
    
    private boolean jj_3R_74() {
        if (this.jj_3R_77()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_77());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_76() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_81()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_82()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_73() {
        return this.jj_3R_76();
    }
    
    private boolean jj_3R_78() {
        return this.jj_scan_token(68);
    }
    
    private boolean jj_3R_75() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_78()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_79()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_80()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_89() {
        return this.jj_scan_token(66);
    }
    
    private boolean jj_3R_80() {
        return this.jj_scan_token(70);
    }
    
    private boolean jj_3R_90() {
        return this.jj_scan_token(61);
    }
    
    private boolean jj_3R_72() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_75()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }
    
    private boolean jj_3R_71() {
        if (this.jj_scan_token(70)) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(1));
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_70() {
        if (this.jj_scan_token(69)) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(1));
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_68() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_73()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_74()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_87() {
        return this.jj_scan_token(72);
    }
    
    private boolean jj_3R_69() {
        if (this.jj_scan_token(68)) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(1));
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_67() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_69()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_70()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_71()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_72()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_88() {
        return this.jj_scan_token(59);
    }
    
    private boolean jj_3R_86() {
        return this.jj_3R_90();
    }
    
    private boolean jj_3R_85() {
        return this.jj_3R_89();
    }
    
    private boolean jj_3R_84() {
        return this.jj_3R_88();
    }
    
    private boolean jj_3R_83() {
        return this.jj_3R_87();
    }
    
    private boolean jj_3R_77() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_83()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_84()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_85()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_86()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_82() {
        return this.jj_scan_token(62);
    }
    
    private static void jj_la1_init_0() {
        SACParserCSS3.jj_la1_0 = new int[] { 2, 2, 2, 2, 0, 4194304, 4194304, 4194304, 2, 2, 2, 4194304, 2, 2, 33554432, 2, 4980736, 2, 2, 4194304, 0, 2, 786432, 2, 786432, 131072, 2, 131072, 2, 4980736, 2, 2, 0, 2, 4194304, 2, 4194304, 2, 2, 4194304, 2, 0, 2, 4194304, 0, 2, 2, 2, 2, 2, 0, 2, 2, 2, 0, 2, 0, 2, 0, 2, 2, 1, 2, 0, 2, 2, 0, 0, 0, 0, 4194304, 4194304, 2, 2, 0, 2, 37748736, 2, 0, 0, 2, 2, 2, 2, 2, 38797312, 2, 38797312, 4194304, 4194304, 0, 2, 4194304, 0, 2, 0, 0, 2, 40894464, 0, 0, 1048576, 0, 40894464, 2, 2, 40894464, 0, 2, 0, 40894464, 4194304 };
    }
    
    private static void jj_la1_init_1() {
        SACParserCSS3.jj_la1_1 = new int[] { 196608, 196608, 196608, 196608, 0, 1744830464, 1744830464, 1744830464, 196608, 196608, 0, 1744830464, 0, 0, 0, 0, 33554432, 0, 0, 1744830464, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33554432, 0, 0, 536870912, 0, 1744830464, 0, 1744830464, 0, 0, 536870912, 0, 0, 0, 536870912, 536870912, 0, 0, 0, 0, 0, Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16777216, 0, 0, 0, 0, 671088640, 671088640, 671088640, 671088640, 1744830464, 1073741824, 0, 0, 8126464, 0, 0, 0, 8126464, 536870912, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1073741824, 268435456, 0, 1073741824, 1073741824, 0, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 536870912, 0, 0, 0, 0, 0, 0, 0, 0, 1744830464 };
    }
    
    private static void jj_la1_init_2() {
        SACParserCSS3.jj_la1_2 = new int[] { 0, 0, 0, 0, 8192, 7428, 7940, 7940, 0, 0, 0, 16132, 0, 0, 0, 0, 0, 0, 0, 3844, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3844, 0, 3844, 0, 0, 0, 0, 128, 0, 0, 0, 0, 0, 0, 0, 0, 128, 0, 0, 0, 112, 0, 112, 112, 17, 0, 0, 0, 0, 128, 0, 0, 260, 260, 260, 260, 260, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 17, 0, 17, 0, 0, 0, 0, 0, 0, 0, 16384, 0, 0, -32367, 128, 17, -32768, 0, -32512, 0, 0, -32365, 130, 0, 130, -32495, 260 };
    }
    
    private static void jj_la1_init_3() {
        SACParserCSS3.jj_la1_3 = new int[] { 0, 0, 0, 0, 0, 256, 256, 256, 0, 0, 0, 256, 0, 0, 16, 0, 0, 0, 0, 256, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 256, 0, 256, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 224, 0, 0, 0, 0, 0, 0, 0, 512, 0, 1179, 0, 0, 129, 0, 1179, 0, 0, 1179, 0, 0, 0, 1179, 0 };
    }
    
    public SACParserCSS3(final CharStream stream) {
        this.jj_la1 = new int[112];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.trace_indent = 0;
        this.token_source = new SACParserCSS3TokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 112; ++i) {
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
        for (int i = 0; i < 112; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public SACParserCSS3(final SACParserCSS3TokenManager tm) {
        this.jj_la1 = new int[112];
        this.jj_2_rtns = new JJCalls[1];
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
        for (int i = 0; i < 112; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final SACParserCSS3TokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 112; ++i) {
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
        final boolean[] la1tokens = new boolean[107];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 112; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((SACParserCSS3.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((SACParserCSS3.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((SACParserCSS3.jj_la1_2[i] & 1 << j) != 0x0) {
                        la1tokens[64 + j] = true;
                    }
                    if ((SACParserCSS3.jj_la1_3[i] & 1 << j) != 0x0) {
                        la1tokens[96 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 107; ++i) {
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
        return new ParseException(this.token, exptokseq, SACParserCSS3.tokenImage);
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
        jj_la1_init_2();
        jj_la1_init_3();
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
