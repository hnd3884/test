package com.zoho.conf.tree;

import java.util.List;
import java.util.Locale;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Queue;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.Set;

public class ConfTree
{
    ConfNode root;
    Set<String> keys;
    private static final Logger LOGGER;
    boolean isSubTree;
    boolean ignoreSubTreePrefix;
    
    public ConfTree() {
        this.keys = new TreeSet<String>();
        this.isSubTree = false;
        this.ignoreSubTreePrefix = false;
    }
    
    public int size() {
        return this.keys.size();
    }
    
    public boolean isEmpty() {
        return this.root == null;
    }
    
    public boolean containsKey(final String key) {
        return this.keys.contains(key);
    }
    
    public String get(final String key) {
        return this.get(key, this.ignoreSubTreePrefix);
    }
    
    private String get(String key, final boolean ignoreRootNode) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key should be a instance of String");
        }
        if (this.root == null) {
            return null;
        }
        if (this.isSubTree && ignoreRootNode) {
            key = "*." + key;
        }
        String value = this.root.getValue(this.splitKeyString(key));
        if (value == null && key.lastIndexOf(".") > 1 && !ignoreRootNode) {
            value = this.get(key.substring(key.lastIndexOf("."), key.length()), false);
            if (value != null) {
                ConfTree.LOGGER.fine("Returning .root value for key : " + key);
            }
        }
        return value;
    }
    
    public String get(final String key, final String defaultValue) {
        final String val = this.get(key);
        return (val != null) ? val : defaultValue;
    }
    
    public String put(String key, final String value) {
        if (key == null && value == null) {
            throw new IllegalArgumentException("Key/value should not be null.K-" + key + ", V-" + value);
        }
        if (this.root == null) {
            this.root = new ConfNode(true);
        }
        if (key.startsWith(".")) {
            key = "*" + key;
        }
        final Queue<String> splitKeyString = this.splitKeyString(key);
        this.root.put(splitKeyString, value);
        this.keys.add(key);
        return null;
    }
    
    private Queue<String> splitKeyString(final String key) {
        final StringTokenizer tokenizer = new StringTokenizer(key, ".", false);
        final Queue<String> splittedKey = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            splittedKey.add(tokenizer.nextToken());
        }
        return splittedKey;
    }
    
    public String remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key value should not be null.");
        }
        String value = null;
        if (this.root != null) {
            value = this.root.remove(this.splitKeyString(key));
            this.keys.remove(key);
            if (this.root.getChildren().size() == 0) {
                this.root = null;
            }
        }
        return value;
    }
    
    public void putAll(final Map<? extends String, ? extends String> m) {
        for (final String key : m.keySet()) {
            this.put(key, (String)m.get(key));
        }
    }
    
    public void clear() {
        this.root = null;
        this.keys.clear();
    }
    
    public Set<String> keySet() {
        return this.keys;
    }
    
    public ConfTree getSubTree(final String commonPrefix) {
        return this.getSubTree(commonPrefix, false);
    }
    
    public ConfTree getSubTree(final String commonPrefix, final boolean ignorePrefixValidation) {
        final Set<String> newKeys = new TreeSet<String>();
        final Queue<String> splitKeyString = this.splitKeyString(commonPrefix);
        final Queue<String> keyPathCopy = new LinkedList<String>();
        if (this.root.getDefaultNode() != null) {
            keyPathCopy.addAll((Collection<?>)splitKeyString);
        }
        final ConfTree subTree = new ConfTree();
        subTree.isSubTree = Boolean.TRUE;
        subTree.ignoreSubTreePrefix = ignorePrefixValidation;
        subTree.root = this.root.getSubConfTreeNode(splitKeyString);
        if (this.root.getDefaultNode() != null && subTree.root != null) {
            subTree.root.setDefaultNode(this.root.getDefaultNode().getSubConfTreeNode(keyPathCopy));
        }
        else if (subTree.root == null) {
            if (this.root.getDefaultNode() == null) {
                return null;
            }
            subTree.root = this.root.getDefaultNode().getSubConfTreeNode(keyPathCopy);
        }
        for (final String key : this.keys) {
            if (key.toLowerCase(Locale.ENGLISH).startsWith(commonPrefix.toLowerCase(Locale.ENGLISH)) && !key.equalsIgnoreCase(commonPrefix)) {
                if (ignorePrefixValidation) {
                    newKeys.add(key.substring(commonPrefix.length() + 1));
                }
                else {
                    newKeys.add(subTree.root.getPrefix() + key.substring(commonPrefix.length()));
                }
            }
        }
        subTree.keys.addAll(newKeys);
        return subTree;
    }
    
    @Override
    public String toString() {
        if (this.root != null) {
            this.root.prettySpace = "";
            return this.root.toString();
        }
        return "EMPTY TREE";
    }
    
    public List<String> getImmediateChildren() {
        if (this.root != null) {
            return this.root.getImmediateChildren();
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ConfTree.class.getName());
    }
}
