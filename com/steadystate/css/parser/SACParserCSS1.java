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
import com.steadystate.css.parser.selectors.SelectorFactoryImpl;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import java.util.List;

public class SACParserCSS1 extends AbstractSACParser implements SACParserCSS1Constants
{
    public SACParserCSS1TokenManager token_source;
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
    
    public SACParserCSS1() {
        this((CharStream)null);
    }
    
    @Override
    public String getParserVersion() {
        return "http://www.w3.org/TR/REC-CSS1";
    }
    
    @Override
    protected String getGrammarUri() {
        return "http://www.w3.org/TR/REC-CSS1#appendix-b";
    }
    
    public void mediaList(final SACMediaListImpl ml) {
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
                case 26:
                case 27: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        case 26: {
                            this.jj_consume_token(26);
                            continue;
                        }
                        case 27: {
                            this.jj_consume_token(27);
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
                                case 28:
                                case 30: {
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                        case 28: {
                                            this.importRule(ruleFound);
                                            break;
                                        }
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 9:
                                        case 13:
                                        case 30: {
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 3:
                                                case 4:
                                                case 5:
                                                case 6:
                                                case 9:
                                                case 13: {
                                                    this.styleRule();
                                                    break;
                                                }
                                                case 30: {
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
                                            case 26:
                                            case 27: {
                                                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                    case 1: {
                                                        this.jj_consume_token(1);
                                                        continue Label_0177_Outer;
                                                    }
                                                    case 26: {
                                                        this.jj_consume_token(26);
                                                        continue Label_0177_Outer;
                                                    }
                                                    case 27: {
                                                        this.jj_consume_token(27);
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
                        case 28: {
                            this.importRule(false);
                            break;
                        }
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 9:
                        case 13: {
                            this.styleRule();
                            break;
                        }
                        case 30: {
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
            this.jj_consume_token(30);
            final Locator locator = this.createLocator(this.token);
            final String s = this.skip();
            this.handleIgnorableAtRule(s, locator);
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidUnknownRule", e));
        }
    }
    
    public final void importRule(final boolean nonImportRuleFoundBefore) throws ParseException {
        Label_0349: {
            try {
                ParseException e = null;
                if (nonImportRuleFoundBefore) {
                    e = this.generateParseException();
                }
                this.jj_consume_token(28);
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
                                case 23: {
                                    t = this.jj_consume_token(23);
                                    break;
                                }
                                case 25: {
                                    t = this.jj_consume_token(25);
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
                                        this.jj_consume_token(14);
                                        if (nonImportRuleFoundBefore) {
                                            this.getErrorHandler().error(this.toCSSParseException("invalidImportRuleIgnored", e));
                                        }
                                        else {
                                            this.handleImportStyle(this.unescape(t.image, false), (SACMediaList)new SACMediaListImpl(), null, locator);
                                        }
                                        break Label_0349;
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
    
    public final String medium() throws ParseException {
        final Token t = this.jj_consume_token(3);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
                    this.handleMedium(t.image, this.createLocator(t));
                    return t.image;
                }
            }
        }
    }
    
    public final LexicalUnit operator(final LexicalUnit prev) throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 16: {
                this.jj_consume_token(16);
                while (true) {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: {
                            this.jj_consume_token(1);
                            continue;
                        }
                        default: {
                            this.jj_la1[13] = this.jj_gen;
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
                            this.jj_la1[14] = this.jj_gen;
                            return LexicalUnitImpl.createComma(prev);
                        }
                    }
                }
                break;
            }
            default: {
                this.jj_la1[15] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final char unaryOperator() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
            case 18: {
                this.jj_consume_token(18);
                return '-';
            }
            case 17: {
                this.jj_consume_token(17);
                return '+';
            }
            default: {
                this.jj_la1[16] = this.jj_gen;
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
                    this.jj_la1[17] = this.jj_gen;
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
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        continue;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        start = true;
                        this.handleStartSelector(selList, this.createLocator(t.next));
                        this.styleDeclaration();
                        this.jj_consume_token(11);
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
                    this.jj_la1[19] = this.jj_gen;
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
    Label_0035:
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
                                    this.jj_la1[21] = this.jj_gen;
                                    selList.add(sel);
                                    sel = this.selector();
                                    if (sel instanceof Locatable) {
                                        selList.setLocator(((Locatable)sel).getLocator());
                                        continue Label_0035;
                                    }
                                    continue Label_0035;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[20] = this.jj_gen;
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 1: {
                                    this.jj_consume_token(1);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[22] = this.jj_gen;
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
                    this.jj_la1[23] = this.jj_gen;
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
            Label_0564: {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 3: {
                        simpleSel = this.elementName();
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 9: {
                                c = this.hash(c);
                                break;
                            }
                            default: {
                                this.jj_la1[24] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13: {
                                c = this._class(c);
                                break;
                            }
                            default: {
                                this.jj_la1[25] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0564;
                            }
                            default: {
                                this.jj_la1[26] = this.jj_gen;
                                break Label_0564;
                            }
                        }
                        break;
                    }
                    case 9: {
                        simpleSel = (SimpleSelector)((SelectorFactoryImpl)this.getSelectorFactory()).createSyntheticElementSelector();
                        c = this.hash(c);
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 13: {
                                c = this._class(c);
                                break;
                            }
                            default: {
                                this.jj_la1[27] = this.jj_gen;
                                break;
                            }
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0564;
                            }
                            default: {
                                this.jj_la1[28] = this.jj_gen;
                                break Label_0564;
                            }
                        }
                        break;
                    }
                    case 13: {
                        simpleSel = (SimpleSelector)((SelectorFactoryImpl)this.getSelectorFactory()).createSyntheticElementSelector();
                        c = this._class(c);
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 4:
                            case 5:
                            case 6: {
                                c = this.pseudoClass(c);
                                break Label_0564;
                            }
                            default: {
                                this.jj_la1[29] = this.jj_gen;
                                break Label_0564;
                            }
                        }
                        break;
                    }
                    case 4:
                    case 5:
                    case 6: {
                        simpleSel = (SimpleSelector)((SelectorFactoryImpl)this.getSelectorFactory()).createSyntheticElementSelector();
                        c = this.pseudoClass(c);
                        break;
                    }
                    default: {
                        this.jj_la1[30] = this.jj_gen;
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
            final Token t = this.jj_consume_token(3);
            return (SimpleSelector)this.getSelectorFactory().createElementSelector(null, this.unescape(t.image, false), this.createLocator(this.token));
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
                    this.jj_la1[31] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            final Condition c = (Condition)this.getConditionFactory().createPseudoClassCondition(null, t.image, this.createLocator(this.token), false);
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
                    this.jj_la1[32] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            return (SimpleSelector)this.getSelectorFactory().createPseudoElementSelector(null, t.image, this.createLocator(this.token), false);
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
        Label_0279: {
            try {
                switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                    case 3: {
                        this.declaration();
                        break;
                    }
                    default: {
                        this.jj_la1[33] = this.jj_gen;
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
                                            this.jj_la1[35] = this.jj_gen;
                                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                                case 3: {
                                                    this.declaration();
                                                    continue Label_0054;
                                                }
                                                default: {
                                                    this.jj_la1[36] = this.jj_gen;
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
                                this.jj_la1[34] = this.jj_gen;
                                break Label_0279;
                            }
                        }
                    }
                    break;
                }
            }
            catch (final ParseException ex) {
                final CSSParseException cpe = this.toCSSParseException("invalidDeclaration", ex);
                this.getErrorHandler().error(cpe);
                this.getErrorHandler().warning(this.createSkipWarning("ignoringFollowingDeclarations", cpe));
                this.error_skipdecl();
            }
        }
    }
    
    public final void declaration() throws ParseException {
        boolean priority = false;
        Locator locator = null;
        Label_0244: {
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
                            this.jj_la1[37] = this.jj_gen;
                            final LexicalUnit e = this.expr();
                            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                                case 29: {
                                    priority = this.prio();
                                    break;
                                }
                                default: {
                                    this.jj_la1[38] = this.jj_gen;
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
        this.jj_consume_token(29);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[39] = this.jj_gen;
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
                    case 16:
                    case 17:
                    case 18:
                    case 23:
                    case 25:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 45: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                            case 12:
                            case 16: {
                                body = this.operator(body);
                                break;
                            }
                            default: {
                                this.jj_la1[41] = this.jj_gen;
                                break;
                            }
                        }
                        body = this.term(body);
                        continue;
                    }
                    default: {
                        this.jj_la1[40] = this.jj_gen;
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
            case 17:
            case 18: {
                op = this.unaryOperator();
                break;
            }
            default: {
                this.jj_la1[42] = this.jj_gen;
                break;
            }
        }
        if (op != ' ') {
            locator = this.createLocator(this.token);
        }
        Label_0794: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                        case 40: {
                            final Token t = this.jj_consume_token(40);
                            try {
                                value = LexicalUnitImpl.createNumber(prev, this.intValue(op, t.image));
                            }
                            catch (final NumberFormatException e) {
                                value = LexicalUnitImpl.createNumber(prev, this.floatValue(op, t.image));
                            }
                            break Label_0794;
                        }
                        case 39: {
                            final Token t = this.jj_consume_token(39);
                            value = LexicalUnitImpl.createPercentage(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 33: {
                            final Token t = this.jj_consume_token(33);
                            value = LexicalUnitImpl.createPixel(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 34: {
                            final Token t = this.jj_consume_token(34);
                            value = LexicalUnitImpl.createCentimeter(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 35: {
                            final Token t = this.jj_consume_token(35);
                            value = LexicalUnitImpl.createMillimeter(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 36: {
                            final Token t = this.jj_consume_token(36);
                            value = LexicalUnitImpl.createInch(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 37: {
                            final Token t = this.jj_consume_token(37);
                            value = LexicalUnitImpl.createPoint(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 38: {
                            final Token t = this.jj_consume_token(38);
                            value = LexicalUnitImpl.createPica(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 31: {
                            final Token t = this.jj_consume_token(31);
                            value = LexicalUnitImpl.createEm(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        case 32: {
                            final Token t = this.jj_consume_token(32);
                            value = LexicalUnitImpl.createEx(prev, this.floatValue(op, t.image));
                            break Label_0794;
                        }
                        default: {
                            this.jj_la1[43] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                case 23: {
                    final Token t = this.jj_consume_token(23);
                    value = LexicalUnitImpl.createString(prev, t.image, null);
                    break;
                }
                case 3: {
                    final Token t = this.jj_consume_token(3);
                    value = LexicalUnitImpl.createIdent(prev, t.image);
                    break;
                }
                case 25: {
                    final Token t = this.jj_consume_token(25);
                    value = LexicalUnitImpl.createURI(prev, t.image);
                    break;
                }
                case 45: {
                    final Token t = this.jj_consume_token(45);
                    value = (LexicalUnit)new LexicalUnitImpl(prev, (short)39, t.image);
                    break;
                }
                case 41: {
                    value = this.rgb(prev);
                    break;
                }
                case 9: {
                    value = this.hexcolor(prev);
                    break;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
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
                    this.jj_la1[45] = this.jj_gen;
                    if (value instanceof Locatable) {
                        ((Locatable)value).setLocator(locator);
                    }
                    return value;
                }
            }
        }
    }
    
    public final LexicalUnit rgb(final LexicalUnit prev) throws ParseException {
        this.jj_consume_token(41);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk_f() : this.jj_ntk) {
                case 1: {
                    this.jj_consume_token(1);
                    continue;
                }
                default: {
                    this.jj_la1[46] = this.jj_gen;
                    final LexicalUnit params = this.expr();
                    this.jj_consume_token(24);
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
            this.appendUnit(t, sb);
            if (t.kind == 10) {
                ++nesting;
            }
            else {
                if (t.kind != 11) {
                    continue;
                }
                --nesting;
            }
        } while ((t.kind != 11 && t.kind != 14) || nesting > 0);
        return sb.toString();
    }
    
    void appendUnit(final Token t, final StringBuilder sb) throws ParseException {
        if (t.kind == 31) {
            sb.append("ems");
            return;
        }
        if (t.kind == 32) {
            sb.append("ex");
            return;
        }
        if (t.kind == 33) {
            sb.append("px");
            return;
        }
        if (t.kind == 34) {
            sb.append("cm");
            return;
        }
        if (t.kind == 35) {
            sb.append("mm");
            return;
        }
        if (t.kind == 36) {
            sb.append("in");
            return;
        }
        if (t.kind == 37) {
            sb.append("pt");
            return;
        }
        if (t.kind == 38) {
            sb.append("pc");
            return;
        }
        if (t.kind == 39) {
            sb.append('%');
        }
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
    
    private boolean jj_3_1() {
        return this.jj_scan_token(1) || this.jj_3R_24();
    }
    
    private boolean jj_3R_24() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_25()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_26()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_27()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_28()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_27() {
        return this.jj_3R_31();
    }
    
    private boolean jj_3R_29() {
        return this.jj_scan_token(3);
    }
    
    private boolean jj_3R_26() {
        return this.jj_3R_30();
    }
    
    private boolean jj_3R_31() {
        return this.jj_scan_token(13);
    }
    
    private boolean jj_3R_25() {
        return this.jj_3R_29();
    }
    
    private boolean jj_3R_30() {
        return this.jj_scan_token(9);
    }
    
    private boolean jj_3R_28() {
        return this.jj_3R_32();
    }
    
    private boolean jj_3R_32() {
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
    
    private static void jj_la1_init_0() {
        SACParserCSS1.jj_la1_0 = new int[] { 201326594, 201326594, 1342186104, 1073750648, 1342186104, 201326594, 201326594, 2, 1342186104, 2, 41943040, 2, 2, 2, 2, 69632, 393216, 2, 2, 2, 4096, 2, 2, 384, 512, 8192, 112, 8192, 112, 112, 8824, 112, 384, 8, 16384, 2, 8, 2, 536870912, 2, -2105077240, 69632, 393216, Integer.MIN_VALUE, -2105540088, 2, 2 };
    }
    
    private static void jj_la1_init_1() {
        SACParserCSS1.jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9215, 0, 0, 511, 9215, 0, 0 };
    }
    
    private static void jj_la1_init_2() {
        SACParserCSS1.jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
    
    public SACParserCSS1(final CharStream stream) {
        this.jj_la1 = new int[47];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.trace_indent = 0;
        this.token_source = new SACParserCSS1TokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 47; ++i) {
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
        for (int i = 0; i < 47; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public SACParserCSS1(final SACParserCSS1TokenManager tm) {
        this.jj_la1 = new int[47];
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
        for (int i = 0; i < 47; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final SACParserCSS1TokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 47; ++i) {
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
        final boolean[] la1tokens = new boolean[67];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 47; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((SACParserCSS1.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((SACParserCSS1.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((SACParserCSS1.jj_la1_2[i] & 1 << j) != 0x0) {
                        la1tokens[64 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 67; ++i) {
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
        return new ParseException(this.token, exptokseq, SACParserCSS1.tokenImage);
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
