package org.apache.lucene.search.vectorhighlight;

public class SimpleFragListBuilder extends BaseFragListBuilder
{
    public SimpleFragListBuilder() {
    }
    
    public SimpleFragListBuilder(final int margin) {
        super(margin);
    }
    
    @Override
    public FieldFragList createFieldFragList(final FieldPhraseList fieldPhraseList, final int fragCharSize) {
        return this.createFieldFragList(fieldPhraseList, new SimpleFieldFragList(fragCharSize), fragCharSize);
    }
}
