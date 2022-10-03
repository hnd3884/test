package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import java.util.Locale;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.messages.NLS;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

public abstract class QueryNodeImpl implements QueryNode, Cloneable
{
    public static final String PLAINTEXT_FIELD_NAME = "_plain";
    private boolean isLeaf;
    private Hashtable<String, Object> tags;
    private List<QueryNode> clauses;
    private QueryNode parent;
    protected boolean toQueryStringIgnoreFields;
    
    public QueryNodeImpl() {
        this.isLeaf = true;
        this.tags = new Hashtable<String, Object>();
        this.clauses = null;
        this.parent = null;
        this.toQueryStringIgnoreFields = false;
    }
    
    protected void allocate() {
        if (this.clauses == null) {
            this.clauses = new ArrayList<QueryNode>();
        }
        else {
            this.clauses.clear();
        }
    }
    
    @Override
    public final void add(final QueryNode child) {
        if (this.isLeaf() || this.clauses == null || child == null) {
            throw new IllegalArgumentException(NLS.getLocalizedMessage(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED));
        }
        this.clauses.add(child);
        ((QueryNodeImpl)child).setParent(this);
    }
    
    @Override
    public final void add(final List<QueryNode> children) {
        if (this.isLeaf() || this.clauses == null) {
            throw new IllegalArgumentException(NLS.getLocalizedMessage(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED));
        }
        for (final QueryNode child : children) {
            this.add(child);
        }
    }
    
    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }
    
    @Override
    public final void set(final List<QueryNode> children) {
        if (this.isLeaf() || this.clauses == null) {
            final ResourceBundle bundle = ResourceBundle.getBundle("org.apache.lucene.queryParser.messages.QueryParserMessages");
            final String message = bundle.getObject("Q0008E.NODE_ACTION_NOT_SUPPORTED").toString();
            throw new IllegalArgumentException(message);
        }
        for (final QueryNode child : children) {
            child.removeFromParent();
        }
        final ArrayList<QueryNode> existingChildren = new ArrayList<QueryNode>(this.getChildren());
        for (final QueryNode existingChild : existingChildren) {
            existingChild.removeFromParent();
        }
        this.allocate();
        this.add(children);
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final QueryNodeImpl clone = (QueryNodeImpl)super.clone();
        clone.isLeaf = this.isLeaf;
        clone.tags = new Hashtable<String, Object>();
        if (this.clauses != null) {
            final List<QueryNode> localClauses = new ArrayList<QueryNode>();
            for (final QueryNode clause : this.clauses) {
                localClauses.add(clause.cloneTree());
            }
            clone.clauses = localClauses;
        }
        return clone;
    }
    
    public QueryNode clone() throws CloneNotSupportedException {
        return this.cloneTree();
    }
    
    protected void setLeaf(final boolean isLeaf) {
        this.isLeaf = isLeaf;
    }
    
    @Override
    public final List<QueryNode> getChildren() {
        if (this.isLeaf() || this.clauses == null) {
            return null;
        }
        return new ArrayList<QueryNode>(this.clauses);
    }
    
    @Override
    public void setTag(final String tagName, final Object value) {
        this.tags.put(tagName.toLowerCase(Locale.ROOT), value);
    }
    
    @Override
    public void unsetTag(final String tagName) {
        this.tags.remove(tagName.toLowerCase(Locale.ROOT));
    }
    
    @Override
    public boolean containsTag(final String tagName) {
        return this.tags.containsKey(tagName.toLowerCase(Locale.ROOT));
    }
    
    @Override
    public Object getTag(final String tagName) {
        return this.tags.get(tagName.toLowerCase(Locale.ROOT));
    }
    
    private void setParent(final QueryNode parent) {
        if (this.parent != parent) {
            this.removeFromParent();
            this.parent = parent;
        }
    }
    
    @Override
    public QueryNode getParent() {
        return this.parent;
    }
    
    protected boolean isRoot() {
        return this.getParent() == null;
    }
    
    protected boolean isDefaultField(final CharSequence fld) {
        return this.toQueryStringIgnoreFields || fld == null || "_plain".equals(StringUtils.toString(fld));
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    @Override
    public Map<String, Object> getTagMap() {
        return (Map)this.tags.clone();
    }
    
    @Override
    public void removeChildren(final QueryNode childNode) {
        final Iterator<QueryNode> it = this.clauses.iterator();
        while (it.hasNext()) {
            if (it.next() == childNode) {
                it.remove();
            }
        }
        childNode.removeFromParent();
    }
    
    @Override
    public void removeFromParent() {
        if (this.parent != null) {
            final QueryNode parent = this.parent;
            this.parent = null;
            parent.removeChildren(this);
        }
    }
}
