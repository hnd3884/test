package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class MatchAllDocsQueryNode extends QueryNodeImpl
{
    @Override
    public String toString() {
        return "<matchAllDocs field='*' term='*'/>";
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        return "*:*";
    }
    
    @Override
    public MatchAllDocsQueryNode cloneTree() throws CloneNotSupportedException {
        final MatchAllDocsQueryNode clone = (MatchAllDocsQueryNode)super.cloneTree();
        return clone;
    }
}
