package org.apache.commons.digester;

import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ExtendedBaseRules extends RulesBase
{
    private int counter;
    private Map order;
    
    public ExtendedBaseRules() {
        this.counter = 0;
        this.order = new HashMap();
    }
    
    public void add(final String pattern, final Rule rule) {
        super.add(pattern, rule);
        ++this.counter;
        this.order.put(rule, new Integer(this.counter));
    }
    
    public List match(final String namespace, final String pattern) {
        String parentPattern = "";
        final int lastIndex = pattern.lastIndexOf(47);
        boolean hasParent = true;
        if (lastIndex == -1) {
            hasParent = false;
        }
        else {
            parentPattern = pattern.substring(0, lastIndex);
        }
        final List universalList = new ArrayList(this.counter);
        List tempList = this.cache.get("!*");
        if (tempList != null) {
            universalList.addAll(tempList);
        }
        tempList = this.cache.get("!" + parentPattern + "/?");
        if (tempList != null) {
            universalList.addAll(tempList);
        }
        boolean ignoreBasicMatches = false;
        List rulesList = this.cache.get(pattern);
        if (rulesList != null) {
            ignoreBasicMatches = true;
        }
        else if (hasParent) {
            rulesList = this.cache.get(parentPattern + "/?");
            if (rulesList != null) {
                ignoreBasicMatches = true;
            }
            else {
                rulesList = this.findExactAncesterMatch(pattern);
                if (rulesList != null) {
                    ignoreBasicMatches = true;
                }
            }
        }
        String longKey = "";
        int longKeyLength = 0;
        final Iterator keys = this.cache.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            final boolean isUniversal = key.startsWith("!");
            if (isUniversal) {
                key = key.substring(1, key.length());
            }
            final boolean wildcardMatchStart = key.startsWith("*/");
            final boolean wildcardMatchEnd = key.endsWith("/*");
            if (wildcardMatchStart || (isUniversal && wildcardMatchEnd)) {
                boolean parentMatched = false;
                boolean basicMatched = false;
                boolean ancesterMatched = false;
                final boolean parentMatchEnd = key.endsWith("/?");
                if (parentMatchEnd) {
                    parentMatched = this.parentMatch(key, pattern, parentPattern);
                }
                else if (wildcardMatchEnd) {
                    if (wildcardMatchStart) {
                        final String patternBody = key.substring(2, key.length() - 2);
                        ancesterMatched = (pattern.endsWith(patternBody) || pattern.indexOf(patternBody + "/") > -1);
                    }
                    else {
                        final String bodyPattern = key.substring(0, key.length() - 2);
                        ancesterMatched = (pattern.startsWith(bodyPattern) && (pattern.length() == bodyPattern.length() || pattern.charAt(bodyPattern.length()) == '/'));
                    }
                }
                else {
                    basicMatched = this.basicMatch(key, pattern);
                }
                if (!parentMatched && !basicMatched && !ancesterMatched) {
                    continue;
                }
                if (isUniversal) {
                    tempList = this.cache.get("!" + key);
                    if (tempList == null) {
                        continue;
                    }
                    universalList.addAll(tempList);
                }
                else {
                    if (ignoreBasicMatches) {
                        continue;
                    }
                    int keyLength = key.length();
                    if (wildcardMatchStart) {
                        --keyLength;
                    }
                    if (wildcardMatchEnd) {
                        --keyLength;
                    }
                    else if (parentMatchEnd) {
                        --keyLength;
                    }
                    if (keyLength <= longKeyLength) {
                        continue;
                    }
                    rulesList = this.cache.get(key);
                    longKey = key;
                    longKeyLength = keyLength;
                }
            }
        }
        if (rulesList == null) {
            rulesList = this.cache.get("*");
        }
        if (rulesList != null) {
            universalList.addAll(rulesList);
        }
        if (namespace != null) {
            final Iterator it = universalList.iterator();
            while (it.hasNext()) {
                final Rule rule = it.next();
                final String ns_uri = rule.getNamespaceURI();
                if (ns_uri != null && !ns_uri.equals(namespace)) {
                    it.remove();
                }
            }
        }
        Collections.sort((List<Object>)universalList, new Comparator() {
            public int compare(final Object o1, final Object o2) throws ClassCastException {
                final Integer i1 = ExtendedBaseRules.this.order.get(o1);
                final Integer i2 = ExtendedBaseRules.this.order.get(o2);
                if (i1 == null) {
                    if (i2 == null) {
                        return 0;
                    }
                    return -1;
                }
                else {
                    if (i2 == null) {
                        return 1;
                    }
                    return i1 - i2;
                }
            }
        });
        return universalList;
    }
    
    private boolean parentMatch(final String key, final String pattern, final String parentPattern) {
        return parentPattern.endsWith(key.substring(1, key.length() - 2));
    }
    
    private boolean basicMatch(final String key, final String pattern) {
        return pattern.equals(key.substring(2)) || pattern.endsWith(key.substring(1));
    }
    
    private List findExactAncesterMatch(final String parentPattern) {
        List matchingRules = null;
        int lastIndex = parentPattern.length();
        while (lastIndex-- > 0) {
            lastIndex = parentPattern.lastIndexOf(47, lastIndex);
            if (lastIndex > 0) {
                matchingRules = this.cache.get(parentPattern.substring(0, lastIndex) + "/*");
                if (matchingRules != null) {
                    return matchingRules;
                }
                continue;
            }
        }
        return null;
    }
}
