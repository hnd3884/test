package org.apache.catalina.valves.rewrite;

import org.apache.catalina.util.URLEncoder;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Substitution
{
    protected SubstitutionElement[] elements;
    protected String sub;
    private boolean escapeBackReferences;
    
    public Substitution() {
        this.elements = null;
        this.sub = null;
    }
    
    public String getSub() {
        return this.sub;
    }
    
    public void setSub(final String sub) {
        this.sub = sub;
    }
    
    void setEscapeBackReferences(final boolean escapeBackReferences) {
        this.escapeBackReferences = escapeBackReferences;
    }
    
    public void parse(final Map<String, RewriteMap> maps) {
        this.elements = this.parseSubstitution(this.sub, maps);
    }
    
    private SubstitutionElement[] parseSubstitution(final String sub, final Map<String, RewriteMap> maps) {
        final List<SubstitutionElement> elements = new ArrayList<SubstitutionElement>();
        int pos = 0;
        int percentPos = 0;
        int dollarPos = 0;
        int backslashPos = 0;
        while (pos < sub.length()) {
            percentPos = sub.indexOf(37, pos);
            dollarPos = sub.indexOf(36, pos);
            backslashPos = sub.indexOf(92, pos);
            if (percentPos == -1 && dollarPos == -1 && backslashPos == -1) {
                final StaticElement newElement = new StaticElement();
                newElement.value = sub.substring(pos);
                pos = sub.length();
                elements.add(newElement);
            }
            else if (this.isFirstPos(backslashPos, dollarPos, percentPos)) {
                if (backslashPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                final StaticElement newElement = new StaticElement();
                newElement.value = sub.substring(pos, backslashPos) + sub.substring(backslashPos + 1, backslashPos + 2);
                pos = backslashPos + 2;
                elements.add(newElement);
            }
            else if (this.isFirstPos(dollarPos, percentPos)) {
                if (dollarPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < dollarPos) {
                    final StaticElement newElement = new StaticElement();
                    newElement.value = sub.substring(pos, dollarPos);
                    pos = dollarPos;
                    elements.add(newElement);
                }
                if (Character.isDigit(sub.charAt(dollarPos + 1))) {
                    final RewriteRuleBackReferenceElement newElement2 = new RewriteRuleBackReferenceElement();
                    newElement2.n = Character.digit(sub.charAt(dollarPos + 1), 10);
                    pos = dollarPos + 2;
                    elements.add(newElement2);
                }
                else {
                    if (sub.charAt(dollarPos + 1) != '{') {
                        throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                    }
                    final MapElement newElement3 = new MapElement();
                    final int open = sub.indexOf(123, dollarPos);
                    final int colon = findMatchingColonOrBar(true, sub, open);
                    final int def = findMatchingColonOrBar(false, sub, open);
                    final int close = findMatchingBrace(sub, open);
                    if (-1 >= open || open >= colon || colon >= close) {
                        throw new IllegalArgumentException(sub);
                    }
                    newElement3.map = maps.get(sub.substring(open + 1, colon));
                    if (newElement3.map == null) {
                        throw new IllegalArgumentException(sub + ": No map: " + sub.substring(open + 1, colon));
                    }
                    String key = null;
                    String defaultValue = null;
                    if (def > -1) {
                        if (colon >= def || def >= close) {
                            throw new IllegalArgumentException(sub);
                        }
                        key = sub.substring(colon + 1, def);
                        defaultValue = sub.substring(def + 1, close);
                    }
                    else {
                        key = sub.substring(colon + 1, close);
                    }
                    newElement3.key = this.parseSubstitution(key, maps);
                    if (defaultValue != null) {
                        newElement3.defaultValue = this.parseSubstitution(defaultValue, maps);
                    }
                    pos = close + 1;
                    elements.add(newElement3);
                }
            }
            else {
                if (percentPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < percentPos) {
                    final StaticElement newElement = new StaticElement();
                    newElement.value = sub.substring(pos, percentPos);
                    pos = percentPos;
                    elements.add(newElement);
                }
                if (Character.isDigit(sub.charAt(percentPos + 1))) {
                    final RewriteCondBackReferenceElement newElement4 = new RewriteCondBackReferenceElement();
                    newElement4.n = Character.digit(sub.charAt(percentPos + 1), 10);
                    pos = percentPos + 2;
                    elements.add(newElement4);
                }
                else {
                    if (sub.charAt(percentPos + 1) != '{') {
                        throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                    }
                    SubstitutionElement newElement5 = null;
                    final int open = sub.indexOf(123, percentPos);
                    final int colon = findMatchingColonOrBar(true, sub, open);
                    final int close2 = findMatchingBrace(sub, open);
                    if (-1 >= open || open >= close2) {
                        throw new IllegalArgumentException(sub);
                    }
                    if (colon > -1 && open < colon && colon < close2) {
                        final String type = sub.substring(open + 1, colon);
                        if (type.equals("ENV")) {
                            newElement5 = new ServerVariableEnvElement();
                            ((ServerVariableEnvElement)newElement5).key = sub.substring(colon + 1, close2);
                        }
                        else if (type.equals("SSL")) {
                            newElement5 = new ServerVariableSslElement();
                            ((ServerVariableSslElement)newElement5).key = sub.substring(colon + 1, close2);
                        }
                        else {
                            if (!type.equals("HTTP")) {
                                throw new IllegalArgumentException(sub + ": Bad type: " + type);
                            }
                            newElement5 = new ServerVariableHttpElement();
                            ((ServerVariableHttpElement)newElement5).key = sub.substring(colon + 1, close2);
                        }
                    }
                    else {
                        newElement5 = new ServerVariableElement();
                        ((ServerVariableElement)newElement5).key = sub.substring(open + 1, close2);
                    }
                    pos = close2 + 1;
                    elements.add(newElement5);
                }
            }
        }
        return elements.toArray(new SubstitutionElement[0]);
    }
    
    private static int findMatchingBrace(final String sub, final int start) {
        int nesting = 1;
        for (int i = start + 1; i < sub.length(); ++i) {
            final char c = sub.charAt(i);
            if (c == '{') {
                final char previousChar = sub.charAt(i - 1);
                if (previousChar == '$' || previousChar == '%') {
                    ++nesting;
                }
            }
            else if (c == '}' && --nesting == 0) {
                return i;
            }
        }
        return -1;
    }
    
    private static int findMatchingColonOrBar(final boolean colon, final String sub, final int start) {
        int nesting = 0;
        for (int i = start + 1; i < sub.length(); ++i) {
            final char c = sub.charAt(i);
            if (c == '{') {
                final char previousChar = sub.charAt(i - 1);
                if (previousChar == '$' || previousChar == '%') {
                    ++nesting;
                }
            }
            else if (c == '}') {
                --nesting;
            }
            else {
                if (colon) {
                    if (c != ':') {
                        continue;
                    }
                }
                else if (c != '|') {
                    continue;
                }
                if (nesting == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
        return this.evaluateSubstitution(this.elements, rule, cond, resolver);
    }
    
    private String evaluateSubstitution(final SubstitutionElement[] elements, final Matcher rule, final Matcher cond, final Resolver resolver) {
        final StringBuilder buf = new StringBuilder();
        for (final SubstitutionElement element : elements) {
            buf.append(element.evaluate(rule, cond, resolver));
        }
        return buf.toString();
    }
    
    private boolean isFirstPos(final int testPos, final int... others) {
        if (testPos < 0) {
            return false;
        }
        for (final int other : others) {
            if (other >= 0 && other < testPos) {
                return false;
            }
        }
        return true;
    }
    
    public abstract static class SubstitutionElement
    {
        public abstract String evaluate(final Matcher p0, final Matcher p1, final Resolver p2);
    }
    
    public static class StaticElement extends SubstitutionElement
    {
        public String value;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return this.value;
        }
    }
    
    public class RewriteRuleBackReferenceElement extends SubstitutionElement
    {
        public int n;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            String result = rule.group(this.n);
            if (result == null) {
                result = "";
            }
            if (Substitution.this.escapeBackReferences) {
                return URLEncoder.DEFAULT.encode(result, resolver.getUriCharset());
            }
            return result;
        }
    }
    
    public static class RewriteCondBackReferenceElement extends SubstitutionElement
    {
        public int n;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return (cond.group(this.n) == null) ? "" : cond.group(this.n);
        }
    }
    
    public static class ServerVariableElement extends SubstitutionElement
    {
        public String key;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return resolver.resolve(this.key);
        }
    }
    
    public static class ServerVariableEnvElement extends SubstitutionElement
    {
        public String key;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return resolver.resolveEnv(this.key);
        }
    }
    
    public static class ServerVariableSslElement extends SubstitutionElement
    {
        public String key;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return resolver.resolveSsl(this.key);
        }
    }
    
    public static class ServerVariableHttpElement extends SubstitutionElement
    {
        public String key;
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            return resolver.resolveHttp(this.key);
        }
    }
    
    public class MapElement extends SubstitutionElement
    {
        public RewriteMap map;
        public SubstitutionElement[] defaultValue;
        public SubstitutionElement[] key;
        
        public MapElement() {
            this.map = null;
            this.defaultValue = null;
            this.key = null;
        }
        
        @Override
        public String evaluate(final Matcher rule, final Matcher cond, final Resolver resolver) {
            String result = this.map.lookup(Substitution.this.evaluateSubstitution(this.key, rule, cond, resolver));
            if (result == null && this.defaultValue != null) {
                result = Substitution.this.evaluateSubstitution(this.defaultValue, rule, cond, resolver);
            }
            return result;
        }
    }
}
