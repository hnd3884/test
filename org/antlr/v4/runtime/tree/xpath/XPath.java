package org.antlr.v4.runtime.tree.xpath;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collections;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.List;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.CharStream;
import java.io.IOException;
import java.io.Reader;
import org.antlr.v4.runtime.ANTLRInputStream;
import java.io.StringReader;
import org.antlr.v4.runtime.Parser;

public class XPath
{
    public static final String WILDCARD = "*";
    public static final String NOT = "!";
    protected String path;
    protected XPathElement[] elements;
    protected Parser parser;
    
    public XPath(final Parser parser, final String path) {
        this.parser = parser;
        this.path = path;
        this.elements = this.split(path);
    }
    
    public XPathElement[] split(final String path) {
        ANTLRInputStream in;
        try {
            in = new ANTLRInputStream(new StringReader(path));
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException("Could not read path: " + path, ioe);
        }
        final XPathLexer lexer = new XPathLexer(in) {
            @Override
            public void recover(final LexerNoViableAltException e) {
                throw e;
            }
        };
        lexer.removeErrorListeners();
        lexer.addErrorListener(new XPathLexerErrorListener());
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        try {
            tokenStream.fill();
        }
        catch (final LexerNoViableAltException e) {
            final int pos = lexer.getCharPositionInLine();
            final String msg = "Invalid tokens or characters at index " + pos + " in path '" + path + "'";
            throw new IllegalArgumentException(msg, e);
        }
        final List<Token> tokens = tokenStream.getTokens();
        final List<XPathElement> elements = new ArrayList<XPathElement>();
        final int n = tokens.size();
        int i = 0;
    Label_0412:
        while (i < n) {
            final Token el = tokens.get(i);
            Token next = null;
            switch (el.getType()) {
                case 3:
                case 4: {
                    final boolean anywhere = el.getType() == 3;
                    ++i;
                    next = tokens.get(i);
                    final boolean invert = next.getType() == 6;
                    if (invert) {
                        ++i;
                        next = tokens.get(i);
                    }
                    final XPathElement pathElement = this.getXPathElement(next, anywhere);
                    pathElement.invert = invert;
                    elements.add(pathElement);
                    ++i;
                    continue;
                }
                case 1:
                case 2:
                case 5: {
                    elements.add(this.getXPathElement(el, false));
                    ++i;
                    continue;
                }
                case -1: {
                    break Label_0412;
                }
                default: {
                    throw new IllegalArgumentException("Unknowth path element " + el);
                }
            }
        }
        return elements.toArray(new XPathElement[0]);
    }
    
    protected XPathElement getXPathElement(final Token wordToken, final boolean anywhere) {
        if (wordToken.getType() == -1) {
            throw new IllegalArgumentException("Missing path element at end of path");
        }
        final String word = wordToken.getText();
        final int ttype = this.parser.getTokenType(word);
        final int ruleIndex = this.parser.getRuleIndex(word);
        switch (wordToken.getType()) {
            case 5: {
                return anywhere ? new XPathWildcardAnywhereElement() : new XPathWildcardElement();
            }
            case 1:
            case 8: {
                if (ttype == 0) {
                    throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid token name");
                }
                return anywhere ? new XPathTokenAnywhereElement(word, ttype) : new XPathTokenElement(word, ttype);
            }
            default: {
                if (ruleIndex == -1) {
                    throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid rule name");
                }
                return anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : new XPathRuleElement(word, ruleIndex);
            }
        }
    }
    
    public static Collection<ParseTree> findAll(final ParseTree tree, final String xpath, final Parser parser) {
        final XPath p = new XPath(parser, xpath);
        return p.evaluate(tree);
    }
    
    public Collection<ParseTree> evaluate(final ParseTree t) {
        final ParserRuleContext dummyRoot = new ParserRuleContext();
        dummyRoot.children = Collections.singletonList(t);
        Collection<ParseTree> work = (Collection<ParseTree>)Collections.singleton(dummyRoot);
        Collection<ParseTree> next;
        for (int i = 0; i < this.elements.length; ++i, work = next) {
            next = new LinkedHashSet<ParseTree>();
            for (final ParseTree node : work) {
                if (node.getChildCount() > 0) {
                    final Collection<? extends ParseTree> matching = this.elements[i].evaluate(node);
                    next.addAll(matching);
                }
            }
        }
        return work;
    }
}
