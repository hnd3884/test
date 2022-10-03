package org.apache.lucene.search.vectorhighlight;

public class WeightedFragListBuilder extends BaseFragListBuilder
{
    public WeightedFragListBuilder() {
    }
    
    public WeightedFragListBuilder(final int margin) {
        super(margin);
    }
    
    @Override
    public FieldFragList createFieldFragList(final FieldPhraseList fieldPhraseList, final int fragCharSize) {
        return this.createFieldFragList(fieldPhraseList, new WeightedFieldFragList(fragCharSize), fragCharSize);
    }
}
