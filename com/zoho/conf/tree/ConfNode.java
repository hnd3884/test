package com.zoho.conf.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map;

public class ConfNode implements Iterable<ConfNode>, Comparable<ConfNode>
{
    private String prefix;
    private String value;
    private boolean isRoot;
    private boolean isSubTree;
    private ConfNode defaultNode;
    private Map<String, ConfNode> children;
    String prettySpace;
    
    ConfNode() {
        this.children = new TreeMap<String, ConfNode>();
        this.prettySpace = "";
    }
    
    ConfNode(final boolean isRoot) {
        this.children = new TreeMap<String, ConfNode>();
        this.prettySpace = "";
        this.setRoot(isRoot);
    }
    
    void put(final Queue<String> keyPath, final String value) {
        if (keyPath == null || keyPath.isEmpty()) {
            throw new IllegalArgumentException("Keypath cannot be null/empty.");
        }
        if (this.isRoot()) {
            if (keyPath.size() == 1) {
                this.addChild(keyPath, value);
                return;
            }
            if (!keyPath.peek().equals("*")) {
                this.addToChildNode(keyPath, value);
            }
            else {
                if (this.defaultNode == null) {
                    this.setDefaultNode(new ConfNode());
                }
                this.defaultNode.put(keyPath, value);
            }
        }
        else if (!this.isRoot() && this.getPrefix() == null) {
            if (keyPath.size() == 1) {
                this.setPrefix(keyPath.poll());
                this.setValue(value);
                return;
            }
            if (keyPath.size() > 1) {
                this.setPrefix(keyPath.poll());
                this.addChild(keyPath, value);
            }
        }
        else {
            final String key = keyPath.peek();
            if (!this.getPrefix().equals(key)) {
                throw new IllegalArgumentException("Invalid child node with prefix " + keyPath + ". Prefix should be started with " + this.getPrefix());
            }
            keyPath.poll();
            if (keyPath.size() > 0) {
                this.addToChildNode(keyPath, value);
            }
            else {
                this.setValue(value);
            }
        }
    }
    
    void addToChildNode(final Queue<String> keyPath, final String value) {
        boolean isParentNodeFound = false;
        final ConfNode confNode = this.children.get(keyPath.peek());
        if (confNode != null && keyPath.peek().equals(confNode.getPrefix())) {
            confNode.put(keyPath, value);
            isParentNodeFound = true;
        }
        if (!isParentNodeFound) {
            this.addChild(keyPath, value);
        }
    }
    
    void addChild(final Queue<String> prefix, final String value) {
        final ConfNode child = new ConfNode();
        this.children.put(prefix.peek(), child);
        child.put(prefix, value);
    }
    
    String getValue(final Queue<String> keyPath) {
        final Queue<String> keyPathCopy = new LinkedList<String>();
        if (keyPath == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        if (this.getDefaultNode() != null) {
            keyPathCopy.addAll((Collection<?>)keyPath);
        }
        if (this.isRoot()) {
            for (final ConfNode confNode : this.getChildren()) {
                final String val = confNode.getValue(keyPath);
                if (val != null) {
                    return val;
                }
            }
        }
        else {
            if (keyPath.peek() == null) {
                return null;
            }
            if (keyPath.size() == 1 && this.getPrefix().equals(keyPath.peek())) {
                return this.value;
            }
            if (this.getPrefix().equals(keyPath.peek()) || this.getPrefix().equals("*") || keyPath.peek().equals("*")) {
                keyPath.poll();
                for (final ConfNode confNode : this.getChildren()) {
                    final String val = confNode.getValue(keyPath);
                    if (val != null) {
                        return val;
                    }
                }
            }
        }
        if (this.getDefaultNode() != null) {
            return this.getDefaultNode().getValue(keyPathCopy);
        }
        return null;
    }
    
    void setValue(final String value) {
        this.value = value;
    }
    
    String getPrefix() {
        return this.prefix;
    }
    
    void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    Collection<ConfNode> getChildren() {
        return this.children.values();
    }
    
    @Override
    public Iterator<ConfNode> iterator() {
        return this.children.values().iterator();
    }
    
    @Override
    public int compareTo(final ConfNode node) {
        return (this.prefix != null) ? this.prefix.compareTo(node.getPrefix()) : 1;
    }
    
    boolean isRoot() {
        return this.isRoot;
    }
    
    private void setRoot(final boolean isRoot) {
        this.isRoot = isRoot;
    }
    
    String remove(final Queue<String> keyPath) {
        final Queue<String> keyPathCopy = new LinkedList<String>();
        if (keyPath == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        if (this.getDefaultNode() != null) {
            keyPathCopy.addAll((Collection<?>)keyPath);
        }
        if (this.isRoot()) {
            for (final ConfNode confNode : this.getChildren()) {
                final String remove = confNode.remove(keyPath);
                if (remove != null) {
                    if (confNode.getChildren().isEmpty() && confNode.value == null) {
                        this.children.remove(confNode.getPrefix());
                    }
                    return remove;
                }
            }
        }
        else {
            if (keyPath.size() == 1 && this.getPrefix().equals(keyPath.peek())) {
                final String value2 = this.getValue(keyPath);
                this.value = null;
                return value2;
            }
            if (this.getPrefix().equals(keyPath.peek()) || this.getPrefix().equals("*")) {
                keyPath.poll();
                final ConfNode confNode2 = this.children.get(keyPath.peek());
                if (confNode2 != null) {
                    final String remove2 = confNode2.remove(keyPath);
                    if (remove2 != null) {
                        if (confNode2.getChildren().isEmpty() && confNode2.value == null) {
                            this.children.remove(confNode2.getPrefix());
                        }
                        return remove2;
                    }
                }
            }
        }
        if (this.getDefaultNode() != null) {
            return this.getDefaultNode().remove(keyPathCopy);
        }
        return null;
    }
    
    ConfNode getSubConfTreeNode(final Queue<String> keyPath) {
        if (this.isRoot()) {
            for (final ConfNode confNode : this.getChildren()) {
                final ConfNode subConfTreeNode = confNode.getSubConfTreeNode(keyPath);
                if (subConfTreeNode != null) {
                    return subConfTreeNode;
                }
            }
        }
        else {
            if (keyPath.size() == 1 && this.getPrefix().equals(keyPath.peek())) {
                return this;
            }
            if (this.getPrefix().equals(keyPath.peek()) || this.getPrefix().equals("*")) {
                keyPath.poll();
                final ConfNode confNode2 = this.children.get(keyPath.peek());
                if (confNode2 != null) {
                    return confNode2.getSubConfTreeNode(keyPath);
                }
            }
        }
        return null;
    }
    
    ConfNode getDefaultNode() {
        return this.defaultNode;
    }
    
    void setDefaultNode(final ConfNode defaultNode) {
        this.defaultNode = defaultNode;
    }
    
    public boolean isSubTreeRootNode() {
        return this.isSubTree;
    }
    
    public void setSubTreeRootNode(final boolean isSubTree) {
        this.isSubTree = isSubTree;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        if (this.isRoot()) {
            buff.append("ROOT");
        }
        else {
            buff.append("-").append(this.getPrefix().equals("*") ? "DEFAULT" : this.getPrefix());
            if (this.value != null) {
                buff.append(" ---> ").append(this.value);
            }
        }
        int processed = 1;
        if (this.getDefaultNode() != null) {
            final String space = this.prettySpace + "  |";
            buff.append("\n").append(space);
            this.defaultNode.prettySpace = space;
            buff.append(this.getDefaultNode().toString());
        }
        for (final String key : this.children.keySet()) {
            final ConfNode confNode = this.children.get(key);
            String space2 = this.prettySpace + "  |";
            buff.append("\n").append(space2);
            if (processed == this.children.size()) {
                space2 = space2.substring(0, space2.length() - 2) + " ";
            }
            confNode.prettySpace = space2;
            buff.append(confNode.toString());
            ++processed;
        }
        return buff.toString();
    }
    
    public List<String> getImmediateChildren() {
        final List<String> keys = new ArrayList<String>();
        for (final String key : this.children.keySet()) {
            keys.add(key);
        }
        return keys;
    }
}
