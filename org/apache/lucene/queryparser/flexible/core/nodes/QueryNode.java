package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Map;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public interface QueryNode
{
    CharSequence toQueryString(final EscapeQuerySyntax p0);
    
    String toString();
    
    List<QueryNode> getChildren();
    
    boolean isLeaf();
    
    boolean containsTag(final String p0);
    
    Object getTag(final String p0);
    
    QueryNode getParent();
    
    QueryNode cloneTree() throws CloneNotSupportedException;
    
    void add(final QueryNode p0);
    
    void add(final List<QueryNode> p0);
    
    void set(final List<QueryNode> p0);
    
    void setTag(final String p0, final Object p1);
    
    void unsetTag(final String p0);
    
    Map<String, Object> getTagMap();
    
    void removeFromParent();
    
    void removeChildren(final QueryNode p0);
}
