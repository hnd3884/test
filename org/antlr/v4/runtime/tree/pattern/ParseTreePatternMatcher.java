package org.antlr.v4.runtime.tree.pattern;

import java.util.Iterator;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.Token;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.TokenStream;
import java.util.Collection;
import org.antlr.v4.runtime.ParserInterpreter;
import java.util.Arrays;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Lexer;

public class ParseTreePatternMatcher
{
    private final Lexer lexer;
    private final Parser parser;
    protected String start;
    protected String stop;
    protected String escape;
    
    public ParseTreePatternMatcher(final Lexer lexer, final Parser parser) {
        this.start = "<";
        this.stop = ">";
        this.escape = "\\";
        this.lexer = lexer;
        this.parser = parser;
    }
    
    public void setDelimiters(final String start, final String stop, final String escapeLeft) {
        if (start == null || start.isEmpty()) {
            throw new IllegalArgumentException("start cannot be null or empty");
        }
        if (stop == null || stop.isEmpty()) {
            throw new IllegalArgumentException("stop cannot be null or empty");
        }
        this.start = start;
        this.stop = stop;
        this.escape = escapeLeft;
    }
    
    public boolean matches(final ParseTree tree, final String pattern, final int patternRuleIndex) {
        final ParseTreePattern p = this.compile(pattern, patternRuleIndex);
        return this.matches(tree, p);
    }
    
    public boolean matches(final ParseTree tree, final ParseTreePattern pattern) {
        final MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
        final ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
        return mismatchedNode == null;
    }
    
    public ParseTreeMatch match(final ParseTree tree, final String pattern, final int patternRuleIndex) {
        final ParseTreePattern p = this.compile(pattern, patternRuleIndex);
        return this.match(tree, p);
    }
    
    public ParseTreeMatch match(final ParseTree tree, final ParseTreePattern pattern) {
        final MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
        final ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
        return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
    }
    
    public ParseTreePattern compile(final String pattern, final int patternRuleIndex) {
        final List<? extends Token> tokenList = this.tokenize(pattern);
        final ListTokenSource tokenSrc = new ListTokenSource(tokenList);
        final CommonTokenStream tokens = new CommonTokenStream(tokenSrc);
        final ParserInterpreter parserInterp = new ParserInterpreter(this.parser.getGrammarFileName(), this.parser.getVocabulary(), Arrays.asList(this.parser.getRuleNames()), this.parser.getATNWithBypassAlts(), tokens);
        ParseTree tree = null;
        try {
            parserInterp.setErrorHandler(new BailErrorStrategy());
            tree = parserInterp.parse(patternRuleIndex);
        }
        catch (final ParseCancellationException e) {
            throw (RecognitionException)e.getCause();
        }
        catch (final RecognitionException re) {
            throw re;
        }
        catch (final Exception e2) {
            throw new CannotInvokeStartRule(e2);
        }
        if (tokens.LA(1) != -1) {
            throw new StartRuleDoesNotConsumeFullPattern();
        }
        return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
    }
    
    public Lexer getLexer() {
        return this.lexer;
    }
    
    public Parser getParser() {
        return this.parser;
    }
    
    protected ParseTree matchImpl(final ParseTree tree, final ParseTree patternTree, final MultiMap<String, ParseTree> labels) {
        if (tree == null) {
            throw new IllegalArgumentException("tree cannot be null");
        }
        if (patternTree == null) {
            throw new IllegalArgumentException("patternTree cannot be null");
        }
        if (tree instanceof TerminalNode && patternTree instanceof TerminalNode) {
            final TerminalNode t1 = (TerminalNode)tree;
            final TerminalNode t2 = (TerminalNode)patternTree;
            ParseTree mismatchedNode = null;
            if (t1.getSymbol().getType() == t2.getSymbol().getType()) {
                if (t2.getSymbol() instanceof TokenTagToken) {
                    final TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
                    labels.map(tokenTagToken.getTokenName(), tree);
                    if (tokenTagToken.getLabel() != null) {
                        labels.map(tokenTagToken.getLabel(), tree);
                    }
                }
                else if (!t1.getText().equals(t2.getText())) {
                    if (mismatchedNode == null) {
                        mismatchedNode = t1;
                    }
                }
            }
            else if (mismatchedNode == null) {
                mismatchedNode = t1;
            }
            return mismatchedNode;
        }
        if (!(tree instanceof ParserRuleContext) || !(patternTree instanceof ParserRuleContext)) {
            return tree;
        }
        final ParserRuleContext r1 = (ParserRuleContext)tree;
        final ParserRuleContext r2 = (ParserRuleContext)patternTree;
        ParseTree mismatchedNode = null;
        final RuleTagToken ruleTagToken = this.getRuleTagToken(r2);
        if (ruleTagToken != null) {
            final ParseTreeMatch m = null;
            if (r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex()) {
                labels.map(ruleTagToken.getRuleName(), tree);
                if (ruleTagToken.getLabel() != null) {
                    labels.map(ruleTagToken.getLabel(), tree);
                }
            }
            else if (mismatchedNode == null) {
                mismatchedNode = r1;
            }
            return mismatchedNode;
        }
        if (r1.getChildCount() != r2.getChildCount()) {
            if (mismatchedNode == null) {
                mismatchedNode = r1;
            }
            return mismatchedNode;
        }
        for (int n = r1.getChildCount(), i = 0; i < n; ++i) {
            final ParseTree childMatch = this.matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
            if (childMatch != null) {
                return childMatch;
            }
        }
        return mismatchedNode;
    }
    
    protected RuleTagToken getRuleTagToken(final ParseTree t) {
        if (t instanceof RuleNode) {
            final RuleNode r = (RuleNode)t;
            if (r.getChildCount() == 1 && r.getChild(0) instanceof TerminalNode) {
                final TerminalNode c = (TerminalNode)r.getChild(0);
                if (c.getSymbol() instanceof RuleTagToken) {
                    return (RuleTagToken)c.getSymbol();
                }
            }
        }
        return null;
    }
    
    public List<? extends Token> tokenize(final String pattern) {
        final List<Chunk> chunks = this.split(pattern);
        final List<Token> tokens = new ArrayList<Token>();
        for (final Chunk chunk : chunks) {
            if (chunk instanceof TagChunk) {
                final TagChunk tagChunk = (TagChunk)chunk;
                if (Character.isUpperCase(tagChunk.getTag().charAt(0))) {
                    final Integer ttype = this.parser.getTokenType(tagChunk.getTag());
                    if (ttype == 0) {
                        throw new IllegalArgumentException("Unknown token " + tagChunk.getTag() + " in pattern: " + pattern);
                    }
                    final TokenTagToken t = new TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel());
                    tokens.add(t);
                }
                else {
                    if (!Character.isLowerCase(tagChunk.getTag().charAt(0))) {
                        throw new IllegalArgumentException("invalid tag: " + tagChunk.getTag() + " in pattern: " + pattern);
                    }
                    final int ruleIndex = this.parser.getRuleIndex(tagChunk.getTag());
                    if (ruleIndex == -1) {
                        throw new IllegalArgumentException("Unknown rule " + tagChunk.getTag() + " in pattern: " + pattern);
                    }
                    final int ruleImaginaryTokenType = this.parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
                    tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
                }
            }
            else {
                final TextChunk textChunk = (TextChunk)chunk;
                final ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
                this.lexer.setInputStream(in);
                for (Token t2 = this.lexer.nextToken(); t2.getType() != -1; t2 = this.lexer.nextToken()) {
                    tokens.add(t2);
                }
            }
        }
        return tokens;
    }
    
    public List<Chunk> split(final String pattern) {
        int p = 0;
        final int n = pattern.length();
        final List<Chunk> chunks = new ArrayList<Chunk>();
        final StringBuilder buf = new StringBuilder();
        final List<Integer> starts = new ArrayList<Integer>();
        final List<Integer> stops = new ArrayList<Integer>();
        while (p < n) {
            if (p == pattern.indexOf(this.escape + this.start, p)) {
                p += this.escape.length() + this.start.length();
            }
            else if (p == pattern.indexOf(this.escape + this.stop, p)) {
                p += this.escape.length() + this.stop.length();
            }
            else if (p == pattern.indexOf(this.start, p)) {
                starts.add(p);
                p += this.start.length();
            }
            else if (p == pattern.indexOf(this.stop, p)) {
                stops.add(p);
                p += this.stop.length();
            }
            else {
                ++p;
            }
        }
        if (starts.size() > stops.size()) {
            throw new IllegalArgumentException("unterminated tag in pattern: " + pattern);
        }
        if (starts.size() < stops.size()) {
            throw new IllegalArgumentException("missing start tag in pattern: " + pattern);
        }
        final int ntags = starts.size();
        for (int i = 0; i < ntags; ++i) {
            if (starts.get(i) >= stops.get(i)) {
                throw new IllegalArgumentException("tag delimiters out of order in pattern: " + pattern);
            }
        }
        if (ntags == 0) {
            final String text = pattern.substring(0, n);
            chunks.add(new TextChunk(text));
        }
        if (ntags > 0 && starts.get(0) > 0) {
            final String text = pattern.substring(0, starts.get(0));
            chunks.add(new TextChunk(text));
        }
        for (int i = 0; i < ntags; ++i) {
            String ruleOrToken;
            final String tag = ruleOrToken = pattern.substring(starts.get(i) + this.start.length(), stops.get(i));
            String label = null;
            final int colon = tag.indexOf(58);
            if (colon >= 0) {
                label = tag.substring(0, colon);
                ruleOrToken = tag.substring(colon + 1, tag.length());
            }
            chunks.add(new TagChunk(label, ruleOrToken));
            if (i + 1 < ntags) {
                final String text2 = pattern.substring(stops.get(i) + this.stop.length(), starts.get(i + 1));
                chunks.add(new TextChunk(text2));
            }
        }
        if (ntags > 0) {
            final int afterLastTag = stops.get(ntags - 1) + this.stop.length();
            if (afterLastTag < n) {
                final String text3 = pattern.substring(afterLastTag, n);
                chunks.add(new TextChunk(text3));
            }
        }
        for (int i = 0; i < chunks.size(); ++i) {
            final Chunk c = chunks.get(i);
            if (c instanceof TextChunk) {
                final TextChunk tc = (TextChunk)c;
                final String unescaped = tc.getText().replace(this.escape, "");
                if (unescaped.length() < tc.getText().length()) {
                    chunks.set(i, new TextChunk(unescaped));
                }
            }
        }
        return chunks;
    }
    
    public static class CannotInvokeStartRule extends RuntimeException
    {
        public CannotInvokeStartRule(final Throwable e) {
            super(e);
        }
    }
    
    public static class StartRuleDoesNotConsumeFullPattern extends RuntimeException
    {
    }
}
