package org.apache.tika.sax.xpath;

import java.util.HashMap;
import java.util.Map;

public class XPathParser
{
    private final Map<String, String> prefixes;
    
    public XPathParser() {
        this.prefixes = new HashMap<String, String>();
    }
    
    public XPathParser(final String prefix, final String namespace) {
        this.prefixes = new HashMap<String, String>();
        this.addPrefix(prefix, namespace);
    }
    
    public void addPrefix(final String prefix, final String namespace) {
        this.prefixes.put(prefix, namespace);
    }
    
    public Matcher parse(final String xpath) {
        if (xpath.equals("/text()")) {
            return TextMatcher.INSTANCE;
        }
        if (xpath.equals("/node()")) {
            return NodeMatcher.INSTANCE;
        }
        if (xpath.equals("/descendant::node()") || xpath.equals("/descendant:node()")) {
            return new CompositeMatcher(TextMatcher.INSTANCE, new ChildMatcher(new SubtreeMatcher(NodeMatcher.INSTANCE)));
        }
        if (xpath.equals("/@*")) {
            return AttributeMatcher.INSTANCE;
        }
        if (xpath.length() == 0) {
            return ElementMatcher.INSTANCE;
        }
        if (xpath.startsWith("/@")) {
            String name = xpath.substring(2);
            String prefix = null;
            final int colon = name.indexOf(58);
            if (colon != -1) {
                prefix = name.substring(0, colon);
                name = name.substring(colon + 1);
            }
            if (this.prefixes.containsKey(prefix)) {
                return new NamedAttributeMatcher(this.prefixes.get(prefix), name);
            }
            return Matcher.FAIL;
        }
        else {
            if (xpath.startsWith("/*")) {
                return new ChildMatcher(this.parse(xpath.substring(2)));
            }
            if (xpath.startsWith("///")) {
                return Matcher.FAIL;
            }
            if (xpath.startsWith("//")) {
                return new SubtreeMatcher(this.parse(xpath.substring(1)));
            }
            if (!xpath.startsWith("/")) {
                return Matcher.FAIL;
            }
            int slash = xpath.indexOf(47, 1);
            if (slash == -1) {
                slash = xpath.length();
            }
            String name2 = xpath.substring(1, slash);
            String prefix2 = null;
            final int colon2 = name2.indexOf(58);
            if (colon2 != -1) {
                prefix2 = name2.substring(0, colon2);
                name2 = name2.substring(colon2 + 1);
            }
            if (this.prefixes.containsKey(prefix2)) {
                return new NamedElementMatcher(this.prefixes.get(prefix2), name2, this.parse(xpath.substring(slash)));
            }
            return Matcher.FAIL;
        }
    }
}
