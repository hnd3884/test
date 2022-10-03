package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;

public class TermRangeQueryNode extends AbstractRangeQueryNode<FieldQueryNode>
{
    public TermRangeQueryNode(final FieldQueryNode lower, final FieldQueryNode upper, final boolean lowerInclusive, final boolean upperInclusive) {
        this.setBounds(lower, upper, lowerInclusive, upperInclusive);
    }
}
