package org.apache.catalina.valves.rewrite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

public class RewriteCond
{
    protected String testString;
    protected String condPattern;
    protected String flagsString;
    protected boolean positive;
    protected Substitution test;
    protected Condition condition;
    public boolean nocase;
    public boolean ornext;
    
    public RewriteCond() {
        this.testString = null;
        this.condPattern = null;
        this.flagsString = null;
        this.positive = true;
        this.test = null;
        this.condition = null;
        this.nocase = false;
        this.ornext = false;
    }
    
    public String getCondPattern() {
        return this.condPattern;
    }
    
    public void setCondPattern(final String condPattern) {
        this.condPattern = condPattern;
    }
    
    public String getTestString() {
        return this.testString;
    }
    
    public void setTestString(final String testString) {
        this.testString = testString;
    }
    
    public final String getFlagsString() {
        return this.flagsString;
    }
    
    public final void setFlagsString(final String flagsString) {
        this.flagsString = flagsString;
    }
    
    public void parse(final Map<String, RewriteMap> maps) {
        (this.test = new Substitution()).setSub(this.testString);
        this.test.parse(maps);
        if (this.condPattern.startsWith("!")) {
            this.positive = false;
            this.condPattern = this.condPattern.substring(1);
        }
        if (this.condPattern.startsWith("<")) {
            final LexicalCondition ncondition = new LexicalCondition();
            ncondition.type = -1;
            ncondition.condition = this.condPattern.substring(1);
            this.condition = ncondition;
        }
        else if (this.condPattern.startsWith(">")) {
            final LexicalCondition ncondition = new LexicalCondition();
            ncondition.type = 1;
            ncondition.condition = this.condPattern.substring(1);
            this.condition = ncondition;
        }
        else if (this.condPattern.startsWith("=")) {
            final LexicalCondition ncondition = new LexicalCondition();
            ncondition.type = 0;
            ncondition.condition = this.condPattern.substring(1);
            this.condition = ncondition;
        }
        else if (this.condPattern.equals("-d")) {
            final ResourceCondition ncondition2 = new ResourceCondition();
            ncondition2.type = 0;
            this.condition = ncondition2;
        }
        else if (this.condPattern.equals("-f")) {
            final ResourceCondition ncondition2 = new ResourceCondition();
            ncondition2.type = 1;
            this.condition = ncondition2;
        }
        else if (this.condPattern.equals("-s")) {
            final ResourceCondition ncondition2 = new ResourceCondition();
            ncondition2.type = 2;
            this.condition = ncondition2;
        }
        else {
            final PatternCondition ncondition3 = new PatternCondition();
            int flags = 0;
            if (this.isNocase()) {
                flags |= 0x2;
            }
            ncondition3.pattern = Pattern.compile(this.condPattern, flags);
            this.condition = ncondition3;
        }
    }
    
    public Matcher getMatcher() {
        if (this.condition instanceof PatternCondition) {
            return ((PatternCondition)this.condition).getMatcher();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "RewriteCond " + this.testString + " " + this.condPattern + ((this.flagsString != null) ? (" " + this.flagsString) : "");
    }
    
    public boolean evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
        final String value = this.test.evaluate(rule, cond, resolver);
        if (this.positive) {
            return this.condition.evaluate(value, resolver);
        }
        return !this.condition.evaluate(value, resolver);
    }
    
    public boolean isNocase() {
        return this.nocase;
    }
    
    public void setNocase(final boolean nocase) {
        this.nocase = nocase;
    }
    
    public boolean isOrnext() {
        return this.ornext;
    }
    
    public void setOrnext(final boolean ornext) {
        this.ornext = ornext;
    }
    
    public boolean isPositive() {
        return this.positive;
    }
    
    public void setPositive(final boolean positive) {
        this.positive = positive;
    }
    
    public abstract static class Condition
    {
        public abstract boolean evaluate(final String p0, final Resolver p1);
    }
    
    public static class PatternCondition extends Condition
    {
        public Pattern pattern;
        private ThreadLocal<Matcher> matcher;
        
        public PatternCondition() {
            this.matcher = new ThreadLocal<Matcher>();
        }
        
        @Override
        public boolean evaluate(final String value, final Resolver resolver) {
            final Matcher m = this.pattern.matcher(value);
            if (m.matches()) {
                this.matcher.set(m);
                return true;
            }
            return false;
        }
        
        public Matcher getMatcher() {
            return this.matcher.get();
        }
    }
    
    public static class LexicalCondition extends Condition
    {
        public int type;
        public String condition;
        
        public LexicalCondition() {
            this.type = 0;
        }
        
        @Override
        public boolean evaluate(final String value, final Resolver resolver) {
            final int result = value.compareTo(this.condition);
            switch (this.type) {
                case -1: {
                    return result < 0;
                }
                case 0: {
                    return result == 0;
                }
                case 1: {
                    return result > 0;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public static class ResourceCondition extends Condition
    {
        public int type;
        
        public ResourceCondition() {
            this.type = 0;
        }
        
        @Override
        public boolean evaluate(final String value, final Resolver resolver) {
            return resolver.resolveResource(this.type, value);
        }
    }
}
