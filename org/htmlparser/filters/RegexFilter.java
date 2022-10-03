package org.htmlparser.filters;

import java.util.regex.Matcher;
import org.htmlparser.Text;
import org.htmlparser.Node;
import java.util.regex.Pattern;
import org.htmlparser.NodeFilter;

public class RegexFilter implements NodeFilter
{
    public static final int MATCH = 1;
    public static final int LOOKINGAT = 2;
    public static final int FIND = 3;
    protected String mPatternString;
    protected Pattern mPattern;
    protected int mStrategy;
    
    public RegexFilter() {
        this(".*", 3);
    }
    
    public RegexFilter(final String pattern) {
        this(pattern, 3);
    }
    
    public RegexFilter(final String pattern, final int strategy) {
        this.setPattern(pattern);
        this.setStrategy(strategy);
    }
    
    public String getPattern() {
        return this.mPatternString;
    }
    
    public void setPattern(final String pattern) {
        this.mPatternString = pattern;
        this.mPattern = Pattern.compile(pattern);
    }
    
    public int getStrategy() {
        return this.mStrategy;
    }
    
    public void setStrategy(final int strategy) {
        if (strategy != 1 && strategy != 2 && strategy != 3) {
            throw new IllegalArgumentException("illegal strategy (" + strategy + ")");
        }
        this.mStrategy = strategy;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (node instanceof Text) {
            final String string = ((Text)node).getText();
            final Matcher matcher = this.mPattern.matcher(string);
            switch (this.mStrategy) {
                case 1: {
                    ret = matcher.matches();
                    break;
                }
                case 2: {
                    ret = matcher.lookingAt();
                    break;
                }
                default: {
                    ret = matcher.find();
                    break;
                }
            }
        }
        return ret;
    }
}
