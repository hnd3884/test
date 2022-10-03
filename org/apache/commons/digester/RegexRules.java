package org.apache.commons.digester;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class RegexRules extends AbstractRulesImpl
{
    private ArrayList registeredRules;
    private RegexMatcher matcher;
    
    public RegexRules(final RegexMatcher matcher) {
        this.registeredRules = new ArrayList();
        this.setRegexMatcher(matcher);
    }
    
    public RegexMatcher getRegexMatcher() {
        return this.matcher;
    }
    
    public void setRegexMatcher(final RegexMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("RegexMatcher must not be null.");
        }
        this.matcher = matcher;
    }
    
    protected void registerRule(final String pattern, final Rule rule) {
        this.registeredRules.add(new RegisteredRule(pattern, rule));
    }
    
    public void clear() {
        this.registeredRules.clear();
    }
    
    public List match(final String namespaceURI, final String pattern) {
        final ArrayList rules = new ArrayList(this.registeredRules.size());
        final Iterator it = this.registeredRules.iterator();
        while (it.hasNext()) {
            final RegisteredRule next = it.next();
            if (this.matcher.match(pattern, next.pattern)) {
                rules.add(next.rule);
            }
        }
        return rules;
    }
    
    public List rules() {
        final ArrayList rules = new ArrayList(this.registeredRules.size());
        final Iterator it = this.registeredRules.iterator();
        while (it.hasNext()) {
            rules.add(it.next().rule);
        }
        return rules;
    }
    
    private class RegisteredRule
    {
        String pattern;
        Rule rule;
        
        RegisteredRule(final String pattern, final Rule rule) {
            this.pattern = pattern;
            this.rule = rule;
        }
    }
}
