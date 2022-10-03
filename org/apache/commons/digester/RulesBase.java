package org.apache.commons.digester;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

public class RulesBase implements Rules
{
    protected HashMap cache;
    protected Digester digester;
    protected String namespaceURI;
    protected ArrayList rules;
    
    public RulesBase() {
        this.cache = new HashMap();
        this.digester = null;
        this.namespaceURI = null;
        this.rules = new ArrayList();
    }
    
    public Digester getDigester() {
        return this.digester;
    }
    
    public void setDigester(final Digester digester) {
        this.digester = digester;
        final Iterator items = this.rules.iterator();
        while (items.hasNext()) {
            final Rule item = items.next();
            item.setDigester(digester);
        }
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
    
    public void add(String pattern, final Rule rule) {
        final int patternLength = pattern.length();
        if (patternLength > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, patternLength - 1);
        }
        List list = this.cache.get(pattern);
        if (list == null) {
            list = new ArrayList();
            this.cache.put(pattern, list);
        }
        list.add(rule);
        this.rules.add(rule);
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
        if (this.namespaceURI != null) {
            rule.setNamespaceURI(this.namespaceURI);
        }
    }
    
    public void clear() {
        this.cache.clear();
        this.rules.clear();
    }
    
    public List match(final String pattern) {
        return this.match(null, pattern);
    }
    
    public List match(final String namespaceURI, final String pattern) {
        List rulesList = this.lookup(namespaceURI, pattern);
        if (rulesList == null || rulesList.size() < 1) {
            String longKey = "";
            final Iterator keys = this.cache.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                if (key.startsWith("*/") && (pattern.equals(key.substring(2)) || pattern.endsWith(key.substring(1))) && key.length() > longKey.length()) {
                    rulesList = this.lookup(namespaceURI, key);
                    longKey = key;
                }
            }
        }
        if (rulesList == null) {
            rulesList = new ArrayList();
        }
        return rulesList;
    }
    
    public List rules() {
        return this.rules;
    }
    
    protected List lookup(final String namespaceURI, final String pattern) {
        final List list = this.cache.get(pattern);
        if (list == null) {
            return null;
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return list;
        }
        final ArrayList results = new ArrayList();
        final Iterator items = list.iterator();
        while (items.hasNext()) {
            final Rule item = items.next();
            if (namespaceURI.equals(item.getNamespaceURI()) || item.getNamespaceURI() == null) {
                results.add(item);
            }
        }
        return results;
    }
}
