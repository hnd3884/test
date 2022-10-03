package org.antlr.v4.runtime.tree.pattern;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.xpath.XPath;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreePattern
{
    private final int patternRuleIndex;
    private final String pattern;
    private final ParseTree patternTree;
    private final ParseTreePatternMatcher matcher;
    
    public ParseTreePattern(final ParseTreePatternMatcher matcher, final String pattern, final int patternRuleIndex, final ParseTree patternTree) {
        this.matcher = matcher;
        this.patternRuleIndex = patternRuleIndex;
        this.pattern = pattern;
        this.patternTree = patternTree;
    }
    
    public ParseTreeMatch match(final ParseTree tree) {
        return this.matcher.match(tree, this);
    }
    
    public boolean matches(final ParseTree tree) {
        return this.matcher.match(tree, this).succeeded();
    }
    
    public List<ParseTreeMatch> findAll(final ParseTree tree, final String xpath) {
        final Collection<ParseTree> subtrees = XPath.findAll(tree, xpath, this.matcher.getParser());
        final List<ParseTreeMatch> matches = new ArrayList<ParseTreeMatch>();
        for (final ParseTree t : subtrees) {
            final ParseTreeMatch match = this.match(t);
            if (match.succeeded()) {
                matches.add(match);
            }
        }
        return matches;
    }
    
    public ParseTreePatternMatcher getMatcher() {
        return this.matcher;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public int getPatternRuleIndex() {
        return this.patternRuleIndex;
    }
    
    public ParseTree getPatternTree() {
        return this.patternTree;
    }
}
