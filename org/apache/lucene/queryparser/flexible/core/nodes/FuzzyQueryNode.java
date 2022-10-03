package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class FuzzyQueryNode extends FieldQueryNode
{
    private float similarity;
    private int prefixLength;
    
    public FuzzyQueryNode(final CharSequence field, final CharSequence term, final float minSimilarity, final int begin, final int end) {
        super(field, term, begin, end);
        this.similarity = minSimilarity;
        this.setLeaf(true);
    }
    
    public void setPrefixLength(final int prefixLength) {
        this.prefixLength = prefixLength;
    }
    
    public int getPrefixLength() {
        return this.prefixLength;
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return (Object)this.getTermEscaped(escaper) + "~" + this.similarity;
        }
        return (Object)this.field + ":" + (Object)this.getTermEscaped(escaper) + "~" + this.similarity;
    }
    
    @Override
    public String toString() {
        return "<fuzzy field='" + (Object)this.field + "' similarity='" + this.similarity + "' term='" + (Object)this.text + "'/>";
    }
    
    public void setSimilarity(final float similarity) {
        this.similarity = similarity;
    }
    
    @Override
    public FuzzyQueryNode cloneTree() throws CloneNotSupportedException {
        final FuzzyQueryNode clone = (FuzzyQueryNode)super.cloneTree();
        clone.similarity = this.similarity;
        return clone;
    }
    
    public float getSimilarity() {
        return this.similarity;
    }
}
