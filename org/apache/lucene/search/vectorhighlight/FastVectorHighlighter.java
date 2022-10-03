package org.apache.lucene.search.vectorhighlight;

import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.search.highlight.Encoder;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

public class FastVectorHighlighter
{
    public static final boolean DEFAULT_PHRASE_HIGHLIGHT = true;
    public static final boolean DEFAULT_FIELD_MATCH = true;
    private final boolean phraseHighlight;
    private final boolean fieldMatch;
    private final FragListBuilder fragListBuilder;
    private final FragmentsBuilder fragmentsBuilder;
    private int phraseLimit;
    
    public FastVectorHighlighter() {
        this(true, true);
    }
    
    public FastVectorHighlighter(final boolean phraseHighlight, final boolean fieldMatch) {
        this(phraseHighlight, fieldMatch, new SimpleFragListBuilder(), new ScoreOrderFragmentsBuilder());
    }
    
    public FastVectorHighlighter(final boolean phraseHighlight, final boolean fieldMatch, final FragListBuilder fragListBuilder, final FragmentsBuilder fragmentsBuilder) {
        this.phraseLimit = Integer.MAX_VALUE;
        this.phraseHighlight = phraseHighlight;
        this.fieldMatch = fieldMatch;
        this.fragListBuilder = fragListBuilder;
        this.fragmentsBuilder = fragmentsBuilder;
    }
    
    public FieldQuery getFieldQuery(final Query query) {
        try {
            return new FieldQuery(query, null, this.phraseHighlight, this.fieldMatch);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public FieldQuery getFieldQuery(final Query query, final IndexReader reader) throws IOException {
        return new FieldQuery(query, reader, this.phraseHighlight, this.fieldMatch);
    }
    
    public final String getBestFragment(final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String fieldName, final int fragCharSize) throws IOException {
        final FieldFragList fieldFragList = this.getFieldFragList(this.fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return this.fragmentsBuilder.createFragment(reader, docId, fieldName, fieldFragList);
    }
    
    public final String[] getBestFragments(final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String fieldName, final int fragCharSize, final int maxNumFragments) throws IOException {
        final FieldFragList fieldFragList = this.getFieldFragList(this.fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return this.fragmentsBuilder.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments);
    }
    
    public final String getBestFragment(final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String fieldName, final int fragCharSize, final FragListBuilder fragListBuilder, final FragmentsBuilder fragmentsBuilder, final String[] preTags, final String[] postTags, final Encoder encoder) throws IOException {
        final FieldFragList fieldFragList = this.getFieldFragList(fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return fragmentsBuilder.createFragment(reader, docId, fieldName, fieldFragList, preTags, postTags, encoder);
    }
    
    public final String[] getBestFragments(final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String fieldName, final int fragCharSize, final int maxNumFragments, final FragListBuilder fragListBuilder, final FragmentsBuilder fragmentsBuilder, final String[] preTags, final String[] postTags, final Encoder encoder) throws IOException {
        final FieldFragList fieldFragList = this.getFieldFragList(fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return fragmentsBuilder.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments, preTags, postTags, encoder);
    }
    
    public final String[] getBestFragments(final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String storedField, final Set<String> matchedFields, final int fragCharSize, final int maxNumFragments, final FragListBuilder fragListBuilder, final FragmentsBuilder fragmentsBuilder, final String[] preTags, final String[] postTags, final Encoder encoder) throws IOException {
        final FieldFragList fieldFragList = this.getFieldFragList(fragListBuilder, fieldQuery, reader, docId, matchedFields, fragCharSize);
        return fragmentsBuilder.createFragments(reader, docId, storedField, fieldFragList, maxNumFragments, preTags, postTags, encoder);
    }
    
    private FieldFragList getFieldFragList(final FragListBuilder fragListBuilder, final FieldQuery fieldQuery, final IndexReader reader, final int docId, final String matchedField, final int fragCharSize) throws IOException {
        final FieldTermStack fieldTermStack = new FieldTermStack(reader, docId, matchedField, fieldQuery);
        final FieldPhraseList fieldPhraseList = new FieldPhraseList(fieldTermStack, fieldQuery, this.phraseLimit);
        return fragListBuilder.createFieldFragList(fieldPhraseList, fragCharSize);
    }
    
    private FieldFragList getFieldFragList(final FragListBuilder fragListBuilder, final FieldQuery fieldQuery, final IndexReader reader, final int docId, final Set<String> matchedFields, final int fragCharSize) throws IOException {
        final Iterator<String> matchedFieldsItr = matchedFields.iterator();
        if (!matchedFieldsItr.hasNext()) {
            throw new IllegalArgumentException("matchedFields must contain at least on field name.");
        }
        final FieldPhraseList[] toMerge = new FieldPhraseList[matchedFields.size()];
        int i = 0;
        while (matchedFieldsItr.hasNext()) {
            final FieldTermStack stack = new FieldTermStack(reader, docId, matchedFieldsItr.next(), fieldQuery);
            toMerge[i++] = new FieldPhraseList(stack, fieldQuery, this.phraseLimit);
        }
        return fragListBuilder.createFieldFragList(new FieldPhraseList(toMerge), fragCharSize);
    }
    
    public boolean isPhraseHighlight() {
        return this.phraseHighlight;
    }
    
    public boolean isFieldMatch() {
        return this.fieldMatch;
    }
    
    public int getPhraseLimit() {
        return this.phraseLimit;
    }
    
    public void setPhraseLimit(final int phraseLimit) {
        this.phraseLimit = phraseLimit;
    }
}
