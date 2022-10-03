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
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import java.util.List;

public class SACParserCSSmobileOKBasic1 extends AbstractSACParser implements SACParserCSSmobileOKBasic1Constants
{
    public SACParserCSSmobileOKBasic1TokenManager token_source;
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
    
    public SACParserCSSmobileOKBasic1() {
        this((CharStream)null);
    }
    
    @Override
    public String getParserVersion() {
        return "http://www.w3.org/TR/mobileOK-basic10-tests/#validity";
    }
    
    @Override
    protected String getGrammarUri() {
        return "CSSgrammarMobileOKBasic1.0.txt";
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
    Label_0177_Outer:
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1:
                case 27:
                case 28: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        case 27: {
                            this.jj_consume_token(27);
                            continue;
                        }
                        case 28: {
                            this.jj_consume_token(28);
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
                Label_0177:
                    while (true) {
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 9:
                                case 13:
                                case 16:
                                case 29:
                                case 30:
                                case 32: {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 29: {
                                            this.importRule(ruleFound);
                                            break;
                                        }
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 9:
                                        case 13:
                                        case 16:
                                        case 30:
                                        case 32: {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 3:
                                                case 4:
                                                case 5:
                                                case 6:
                                                case 9:
                                                case 13:
                                                case 16: {
                                                    this.styleRule();
                                                    break;
                                                }
                                                case 30: {
                                                    this.mediaRule();
                                                    break;
                                                }
                                                case 32: {
                                                    this.unknownAtRule();
                                                    break;
                                                }
                                                default: {
                                                    this.jj_la1[3] = this.jj_gen;
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                            }
                                            ruleFound = true;
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[4] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ParseException();
                                        }
                                    }
                                    while (true) {
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 1:
                                            case 27:
                                            case 28: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 1: {
                                                        this.jj_consume_token(1);
                                                        continue Label_0177_Outer;
                                                    }
                                                    case 27: {
                                                        this.jj_consume_token(27);
                                                        continue Label_0177_Outer;
                                                    }
                                                    case 28: {
                                                        this.jj_consume_token(28);
                                                        continue Label_0177_Outer;
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
                                                this.jj_la1[5] = this.jj_gen;
                                                continue Label_0177;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    this.jj_la1[2] = this.jj_gen;
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
                    this.jj_la1[7] = this.jj_gen;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 29: {
                            this.importRule(false);
                            break;
                        }
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 9:
                        case 13:
                        case 16: {
                            this.styleRule();
                            break;
                        }
                        case 30: {
                            this.mediaRule();
                            break;
                        }
                        case 32: {
                            this.unknownAtRule();
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
            }
        }
    }
    
    public final void unknownAtRule() throws ParseException {
        try {
            this.jj_consume_token(32);
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
        Label_0409: {
            try {
                ParseException e = null;
                if (nonImportRuleFoundBefore) {
                    e = this.generateParseException();
                }
                this.jj_consume_token(29);
                final Locator locator = this.createLocator(this.token);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[9] = this.jj_gen;
                            Token t = null;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 24: {
                                    t = this.jj_consume_token(24);
                                    break;
                                }
                                case 26: {
                                    t = this.jj_consume_token(26);
                                    break;
                                }
                                default: {
                                    this.jj_la1[10] = this.jj_gen;
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
                                        this.jj_la1[11] = this.jj_gen;
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                            case 3: {
                                                this.mediaList(ml);
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[12] = this.jj_gen;
                                                break;
                                            }
                                        }
                                        this.jj_consume_token(14);
                                        if (nonImportRuleFoundBefore) {
                                            this.getErrorHandler().error(this.toCSSParseException("invalidImportRuleIgnored", e));
                                        }
                                        else {
                                            this.handleImportStyle(this.unescape(t.image, false), (SACMediaList)ml, null, locator);
                                        }
                                        break Label_0409;
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
            this.jj_consume_token(30);
            final Locator locator = this.createLocator(this.token);
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                        this.mediaList(ml);
                        start = true;
                        this.handleStartMedia((SACMediaList)ml, locator);
                        this.jj_consume_token(10);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[14] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 9:
                                        case 13:
                                        case 16:
                                        case 32: {
                                            this.mediaRuleList();
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[15] = this.jj_gen;
                                            break;
                                        }
                                    }
                                    this.jj_consume_token(11);
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
            this.error_skipblock();
        }
        catch (final ParseException e2) {
            final CSSParseException cpe = this.toCSSParseException("invalidMediaRule", e2);
            this.getErrorHandler().error(cpe);
            this.getErrorHandler().warning(this.createSkipWarning("ignoringRule", cpe));
            this.error_skipblock();
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
                            case 12: {
                                this.jj_consume_token(12);
                                while (true) {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 1: {
                                            this.jj_consume_token(1);
                                            continue;
                                        }
                                        default: {
                                            this.jj_la1[17] = this.jj_gen;
                                            ml.add(s);
                                            s = this.medium();
                                            continue Label_0017;
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                this.jj_la1[16] = this.jj_gen;
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
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 9:
                    case 13:
                    case 16: {
                        this.styleRule();
                        break;
                    }
                    case 32: {
                        this.unknownAtRule();
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
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
                            this.jj_la1[19] = this.jj_gen;
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 9:
                                case 13:
                                case 16:
                                case 32: {
                                    continue Label_0000;
                                }
                                default: {
                                    this.jj_la1[20] = this.jj_gen;
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
        final Token t = this.jj_consume_token(3);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                    this.handleMedium(t.image, this.createLocator(t));
                    return t.image;
                }
            }
        }
    }
    
    public final LexicalUnit operator(final LexicalUnit prev) throws ParseException {
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
                            this.jj_la1[22] = this.jj_gen;
                            return (LexicalUnit)new LexicalUnitImpl(prev, (short)4);
                        }
                    }
                }
                break;
            }
            case 12: {
                this.jj_consume_token(12);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[23] = this.jj_gen;
                            return LexicalUnitImpl.createComma(prev);
                        }
                    }
                }
                break;
            }
            default: {
                this.jj_la1[24] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final char unaryOperator() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 19: {
                this.jj_consume_token(19);
                return '-';
            }
            case 18: {
                this.jj_consume_token(18);
                return '+';
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final String property() throws ParseException {
        final Token t = this.jj_consume_token(3);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
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
            this.jj_consume_token(10);
        Label_0105_Outer:
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[27] = this.jj_gen;
                        start = true;
                        this.handleStartSelector(selList, this.createLocator(t.next));
                        this.declaration();
                    Label_0105:
                        while (true) {
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 14: {
                                        this.jj_consume_token(14);
                                        while (true) {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 1: {
                                                    this.jj_consume_token(1);
                                                    continue Label_0105_Outer;
                                                }
                                                default: {
                                                    this.jj_la1[29] = this.jj_gen;
                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                        case 3: {
                                                            this.declaration();
                                                            continue Label_0105;
                                                        }
                                                        default: {
                                                            this.jj_la1[30] = this.jj_gen;
                                                            continue Label_0105;
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[28] = this.jj_gen;
                                        this.jj_consume_token(11);
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
        catch (final CSSParseException e) {
            this.getErrorHandler().error(e);
            this.error_skipblock();
        }
        catch (final ParseException e2) {
            this.getErrorHandler().error(this.toCSSParseException("invalidStyleRule", e2));
            this.error_skipblock();
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
                    this.jj_la1[31] = this.jj_gen;
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
                    case 12: {
                        this.jj_consume_token(12);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[33] = this.jj_gen;
                                    selList.add(sel);
                                    sel = this.selector();
                                    continue Label_0033;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[32] = this.jj_gen;
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[34] = this.jj_gen;
                                    selList.add(sel);
                                    return (SelectorList)selList;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            break;
        }
    }
    
    public final Selector selector() throws ParseException {
        SimpleSelector pseudoElementSel = null;
        try {
            Selector sel = this.simpleSelector(null, ' ');
            while (this.jj_2_1(2)) {
                this.jj_consume_token(1);
                sel = this.simpleSelector(sel, ' ');
            }
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 7:
                case 8: {
                    pseudoElementSel = this.pseudoElement();
                    break;
                }
                default: {
                    this.jj_la1[35] = this.jj_gen;
                    break;
                }
            }
            if (pseudoElementSel != null) {
                sel = (Selector)this.getSelectorFactory().createDescendantSelector(sel, pseudoElementSel);
            }
            this.handleSelector(sel);
            return sel;
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidSelector", e);
        }
    }
    
    public final Selector simpleSelector(Selector sel, final char comb) throws ParseException {
        SimpleSelector simpleSel = null;
        Condition c = null;
        try {
            Label_0549: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 3:
                    case 16: {
                        simpleSel = this.elementName();
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 9: {
                                c = this.hash(c);
                                break;
                            }
                            default: {
                                this.jj_la1[36] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13: {
                                c = this._class(c);
                                break;
                            }
                            default: {
                                this.jj_la1[37] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0549;
                            }
                            default: {
                                this.jj_la1[38] = this.jj_gen;
                                break Label_0549;
                            }
                        }
                        break;
                    }
                    case 9: {
                        c = this.hash(c);
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13: {
                                c = this._class(c);
                                break;
                            }
                            default: {
                                this.jj_la1[39] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0549;
                            }
                            default: {
                                this.jj_la1[40] = this.jj_gen;
                                break Label_0549;
                            }
                        }
                        break;
                    }
                    case 13: {
                        c = this._class(c);
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0549;
                            }
                            default: {
                                this.jj_la1[41] = this.jj_gen;
                                break Label_0549;
                            }
                        }
                        break;
                    }
                    case 4:
                    case 5:
                    case 6: {
                        c = this.pseudoClass(c);
                        break;
                    }
                    default: {
                        this.jj_la1[42] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
            if (c != null) {
                simpleSel = (SimpleSelector)this.getSelectorFactory().createConditionalSelector(simpleSel, c);
            }
            if (sel != null) {
                switch (comb) {
                    case ' ': {
                        sel = (Selector)this.getSelectorFactory().createDescendantSelector(sel, simpleSel);
                        break;
                    }
                }
            }
            else {
                sel = (Selector)simpleSel;
            }
            return sel;
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidSimpleSelector", e);
        }
    }
    
    public final Condition _class(final Condition pred) throws ParseException {
        try {
            this.jj_consume_token(13);
            final Locator locator = this.createLocator(this.token);
            final Token t = this.jj_consume_token(3);
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
                case 3: {
                    final Token t = this.jj_consume_token(3);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, this.unescape(t.image, false), this.createLocator(this.token));
                }
                case 16: {
                    this.jj_consume_token(16);
                    return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, null, this.createLocator(this.token));
                }
                default: {
                    this.jj_la1[43] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidElementName", e);
        }
    }
    
    public final Condition pseudoClass(final Condition pred) throws ParseException {
        try {
            Token t = null;
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 4: {
                    t = this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    t = this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    t = this.jj_consume_token(6);
                    break;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            final String s = t.image;
            final Condition c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, s, this.createLocator(this.token), false);
            if ("" != null) {
                return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
            }
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidPseudoClass", e);
        }
        return null;
    }
    
    public final SimpleSelector pseudoElement() throws ParseException {
        try {
            Token t = null;
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: {
                    t = this.jj_consume_token(8);
                    break;
                }
                case 7: {
                    t = this.jj_consume_token(7);
                    break;
                }
                default: {
                    this.jj_la1[45] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            final String s = t.image;
            return (SimpleSelector)this.getSelectorFactory().createPseudoElementSelector(null, s, this.createLocator(this.token), false);
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidPseudoElement", e);
        }
    }
    
    public final Condition hash(final Condition pred) throws ParseException {
        try {
            final Token t = this.jj_consume_token(9);
            final Condition c = (Condition)this.getConditionFactory().createIdCondition(this.unescape(t.image.substring(1), false), this.createLocator(this.token));
            return (Condition)((pred == null) ? c : this.getConditionFactory().createAndCondition(pred, c));
        }
        catch (final ParseException e) {
            throw this.toCSSParseException("invalidHash", e);
        }
    }
    
    public final void styleDeclaration() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 3: {
                this.declaration();
                break;
            }
            default: {
                this.jj_la1[46] = this.jj_gen;
                break;
            }
        }
    Label_0054:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 14: {
                        this.jj_consume_token(14);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[48] = this.jj_gen;
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 3: {
                                            this.declaration();
                                            continue Label_0054;
                                        }
                                        default: {
                                            this.jj_la1[49] = this.jj_gen;
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
                        this.jj_la1[47] = this.jj_gen;
                    }
                }
            }
            break;
        }
    }
    
    public final void declaration() throws ParseException {
        boolean priority = false;
        Locator locator = null;
        Label_0210: {
            try {
                final String p = this.property();
                locator = this.createLocator(this.token);
                this.jj_consume_token(15);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[50] = this.jj_gen;
                            final LexicalUnit e = this.expr();
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 31: {
                                    priority = this.prio();
                                    break;
                                }
                                default: {
                                    this.jj_la1[51] = this.jj_gen;
                                    break;
                                }
                            }
                            this.handleProperty(p, e, priority, locator);
                            break Label_0210;
                        }
                    }
                }
            }
            catch (final CSSParseException ex) {
                this.getErrorHandler().error(ex);
                this.error_skipdecl();
            }
            catch (final ParseException ex2) {
                final CSSParseException cpe = this.toCSSParseException("invalidDeclaration", ex2);
                this.getErrorHandler().error(cpe);
                this.error_skipdecl();
            }
        }
    }
    
    public final boolean prio() throws ParseException {
        this.jj_consume_token(31);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[52] = this.jj_gen;
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
                    case 3:
                    case 9:
                    case 12:
                    case 17:
                    case 18:
                    case 19:
                    case 24:
                    case 26:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 47: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 12:
                            case 17: {
                                body = this.operator(body);
                                break;
                            }
                            default: {
                                this.jj_la1[54] = this.jj_gen;
                                break;
                            }
                        }
                        body = this.term(body);
                        continue;
                    }
                    default: {
                        this.jj_la1[53] = this.jj_gen;
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
            case 18:
            case 19: {
                op = this.unaryOperator();
                break;
            }
            default: {
                this.jj_la1[55] = this.jj_gen;
                break;
            }
        }
        if (op != ' ') {
            locator = this.createLocator(this.token);
        }
        Label_0819: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 42: {
                            final Token t = this.jj_consume_token(42);
                            try {
                                value = LexicalUnitImpl.createNumber(prev, this.intValue(op, t.image));
                            }
                            catch (final NumberFormatException e) {
                                value = LexicalUnitImpl.createNumber(prev, this.floatValue(op, t.image));
                            }
                            break Label_0819;
                        }
                        case 41: {
                            final Token t = this.jj_consume_token(41);
                            value = LexicalUnitImpl.createPercentage(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 35: {
                            final Token t = this.jj_consume_token(35);
                            value = LexicalUnitImpl.createPixel(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 36: {
                            final Token t = this.jj_consume_token(36);
                            value = LexicalUnitImpl.createCentimeter(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 37: {
                            final Token t = this.jj_consume_token(37);
                            value = LexicalUnitImpl.createMillimeter(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 38: {
                            final Token t = this.jj_consume_token(38);
                            value = LexicalUnitImpl.createInch(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 39: {
                            final Token t = this.jj_consume_token(39);
                            value = LexicalUnitImpl.createPoint(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 40: {
                            final Token t = this.jj_consume_token(40);
                            value = LexicalUnitImpl.createPica(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 33: {
                            final Token t = this.jj_consume_token(33);
                            value = LexicalUnitImpl.createEm(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        case 34: {
                            final Token t = this.jj_consume_token(34);
                            value = LexicalUnitImpl.createEx(prev, this.floatValue(op, t.image));
                            break Label_0819;
                        }
                        default: {
                            this.jj_la1[56] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                case 24: {
                    final Token t = this.jj_consume_token(24);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)36, t.image);
                    break;
                }
                case 3: {
                    final Token t = this.jj_consume_token(3);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)35, t.image);
                    break;
                }
                case 26: {
                    final Token t = this.jj_consume_token(26);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)24, t.image);
                    break;
                }
                case 47: {
                    final Token t = this.jj_consume_token(47);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)39, t.image);
                    break;
                }
                case 43: {
                    value = this.rgb(prev);
                    break;
                }
                case 9: {
                    value = this.hexcolor(prev);
                    break;
                }
                default: {
                    this.jj_la1[57] = this.jj_gen;
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
                    this.jj_la1[58] = this.jj_gen;
                    if (value instanceof Locatable) {
                        ((Locatable)value).setLocator(locator);
                    }
                    return value;
                }
            }
        }
    }
    
    public final LexicalUnit rgb(final LexicalUnit prev) throws ParseException {
        this.jj_consume_token(43);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[59] = this.jj_gen;
                    final LexicalUnit params = this.expr();
                    this.jj_consume_token(25);
                    return LexicalUnitImpl.createRgbColor(prev, params);
                }
            }
        }
    }
    
    public final LexicalUnit hexcolor(final LexicalUnit prev) throws ParseException {
        final Token t = this.jj_consume_token(9);
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
            if (t.kind == 10) {
                ++nesting;
            }
            else if (t.kind == 11) {
                --nesting;
            }
            else {
                if (t.kind == 14 && nesting <= 0) {
                    break;
                }
                continue;
            }
        } while (t.kind != 11 || nesting > 0);
        return sb.toString();
    }
    
    void error_skipblock() throws ParseException {
        int nesting = 0;
        Token t;
        do {
            t = this.getNextToken();
            if (t.kind == 10) {
                ++nesting;
            }
            else {
                if (t.kind != 11) {
                    continue;
                }
                --nesting;
            }
        } while (t.kind != 0 && (t.kind != 11 || nesting > 0));
    }
    
    void error_skipdecl() throws ParseException {
        Token t = this.getToken(1);
        if (t.kind == 10) {
            this.error_skipblock();
            return;
        }
        if (t.kind == 11) {
            return;
        }
        Token oldToken = this.token;
        while (t.kind != 14 && t.kind != 11 && t.kind != 0) {
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
        } while (t.kind != 14 && t.kind != 0);
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
    
    private boolean jj_3R_33() {
        return this.jj_3R_37();
    }
    
    private boolean jj_3R_38() {
        return this.jj_scan_token(9);
    }
    
    private boolean jj_3R_42() {
        return this.jj_scan_token(16);
    }
    
    private boolean jj_3R_36() {
        return this.jj_3R_40();
    }
    
    private boolean jj_3R_41() {
        return this.jj_scan_token(3);
    }
    
    private boolean jj_3R_40() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(5)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(6)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3_1() {
        return this.jj_scan_token(1) || this.jj_3R_32();
    }
    
    private boolean jj_3R_32() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_33()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_35()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_36()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_35() {
        return this.jj_3R_39();
    }
    
    private boolean jj_3R_37() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_41()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_42()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_34() {
        return this.jj_3R_38();
    }
    
    private boolean jj_3R_39() {
        return this.jj_scan_token(13);
    }
    
    private static void jj_la1_init_0() {
        SACParserCSSmobileOKBasic1.jj_la1_0 = new int[] { 402653186, 402653186, 1610687096, 1073816184, 1610687096, 402653186, 402653186, 2, 1610687096, 2, 83886080, 2, 8, 2, 2, 74360, 4096, 2, 74360, 2, 74360, 2, 2, 2, 135168, 786432, 2, 2, 16384, 2, 8, 2, 4096, 2, 2, 384, 512, 8192, 112, 8192, 112, 112, 74360, 65544, 112, 384, 8, 16384, 2, 8, 2, Integer.MIN_VALUE, 2, 84808200, 135168, 786432, 0, 83886600, 2, 2 };
    }
    
    private static void jj_la1_init_1() {
        SACParserCSSmobileOKBasic1.jj_la1_1 = new int[] { 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 36862, 0, 0, 2046, 36862, 0, 0 };
    }
    
    private static void jj_la1_init_2() {
        SACParserCSSmobileOKBasic1.jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
    
    public SACParserCSSmobileOKBasic1(final CharStream stream) {
        this.jj_la1 = new int[60];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.trace_indent = 0;
        this.token_source = new SACParserCSSmobileOKBasic1TokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 60; ++i) {
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
        for (int i = 0; i < 60; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public SACParserCSSmobileOKBasic1(final SACParserCSSmobileOKBasic1TokenManager tm) {
        this.jj_la1 = new int[60];
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
        for (int i = 0; i < 60; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final SACParserCSSmobileOKBasic1TokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 60; ++i) {
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
        final boolean[] la1tokens = new boolean[68];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 60; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((SACParserCSSmobileOKBasic1.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((SACParserCSSmobileOKBasic1.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((SACParserCSSmobileOKBasic1.jj_la1_2[i] & 1 << j) != 0x0) {
                        la1tokens[64 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 68; ++i) {
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
        return new ParseException(this.token, exptokseq, SACParserCSSmobileOKBasic1.tokenImage);
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
