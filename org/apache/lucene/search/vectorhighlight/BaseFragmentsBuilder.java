package org.apache.lucene.search.vectorhighlight;

import org.apache.lucene.search.highlight.DefaultEncoder;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.document.Field;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.List;
import org.apache.lucene.search.highlight.Encoder;

public abstract class BaseFragmentsBuilder implements FragmentsBuilder
{
    protected String[] preTags;
    protected String[] postTags;
    public static final String[] COLORED_PRE_TAGS;
    public static final String[] COLORED_POST_TAGS;
    private char multiValuedSeparator;
    private final BoundaryScanner boundaryScanner;
    private boolean discreteMultiValueHighlighting;
    private static final Encoder NULL_ENCODER;
    
    protected BaseFragmentsBuilder() {
        this(new String[] { "<b>" }, new String[] { "</b>" });
    }
    
    protected BaseFragmentsBuilder(final String[] preTags, final String[] postTags) {
        this(preTags, postTags, new SimpleBoundaryScanner());
    }
    
    protected BaseFragmentsBuilder(final BoundaryScanner boundaryScanner) {
        this(new String[] { "<b>" }, new String[] { "</b>" }, boundaryScanner);
    }
    
    protected BaseFragmentsBuilder(final String[] preTags, final String[] postTags, final BoundaryScanner boundaryScanner) {
        this.multiValuedSeparator = ' ';
        this.discreteMultiValueHighlighting = false;
        this.preTags = preTags;
        this.postTags = postTags;
        this.boundaryScanner = boundaryScanner;
    }
    
    static Object checkTagsArgument(final Object tags) {
        if (tags instanceof String) {
            return tags;
        }
        if (tags instanceof String[]) {
            return tags;
        }
        throw new IllegalArgumentException("type of preTags/postTags must be a String or String[]");
    }
    
    public abstract List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(final List<FieldFragList.WeightedFragInfo> p0);
    
    @Override
    public String createFragment(final IndexReader reader, final int docId, final String fieldName, final FieldFragList fieldFragList) throws IOException {
        return this.createFragment(reader, docId, fieldName, fieldFragList, this.preTags, this.postTags, BaseFragmentsBuilder.NULL_ENCODER);
    }
    
    @Override
    public String[] createFragments(final IndexReader reader, final int docId, final String fieldName, final FieldFragList fieldFragList, final int maxNumFragments) throws IOException {
        return this.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments, this.preTags, this.postTags, BaseFragmentsBuilder.NULL_ENCODER);
    }
    
    @Override
    public String createFragment(final IndexReader reader, final int docId, final String fieldName, final FieldFragList fieldFragList, final String[] preTags, final String[] postTags, final Encoder encoder) throws IOException {
        final String[] fragments = this.createFragments(reader, docId, fieldName, fieldFragList, 1, preTags, postTags, encoder);
        if (fragments == null || fragments.length == 0) {
            return null;
        }
        return fragments[0];
    }
    
    @Override
    public String[] createFragments(final IndexReader reader, final int docId, final String fieldName, final FieldFragList fieldFragList, final int maxNumFragments, final String[] preTags, final String[] postTags, final Encoder encoder) throws IOException {
        if (maxNumFragments < 0) {
            throw new IllegalArgumentException("maxNumFragments(" + maxNumFragments + ") must be positive number.");
        }
        List<FieldFragList.WeightedFragInfo> fragInfos = fieldFragList.getFragInfos();
        final Field[] values = this.getFields(reader, docId, fieldName);
        if (values.length == 0) {
            return null;
        }
        if (this.discreteMultiValueHighlighting && values.length > 1) {
            fragInfos = this.discreteMultiValueHighlighting(fragInfos, values);
        }
        fragInfos = this.getWeightedFragInfoList(fragInfos);
        final int limitFragments = (maxNumFragments < fragInfos.size()) ? maxNumFragments : fragInfos.size();
        final List<String> fragments = new ArrayList<String>(limitFragments);
        final StringBuilder buffer = new StringBuilder();
        final int[] nextValueIndex = { 0 };
        for (int n = 0; n < limitFragments; ++n) {
            final FieldFragList.WeightedFragInfo fragInfo = fragInfos.get(n);
            fragments.add(this.makeFragment(buffer, nextValueIndex, values, fragInfo, preTags, postTags, encoder));
        }
        return fragments.toArray(new String[fragments.size()]);
    }
    
    protected Field[] getFields(final IndexReader reader, final int docId, final String fieldName) throws IOException {
        final List<Field> fields = new ArrayList<Field>();
        reader.document(docId, (StoredFieldVisitor)new StoredFieldVisitor() {
            public void stringField(final FieldInfo fieldInfo, final byte[] bytes) {
                final String value = new String(bytes, StandardCharsets.UTF_8);
                final FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setStoreTermVectors(fieldInfo.hasVectors());
                fields.add(new Field(fieldInfo.name, value, ft));
            }
            
            public StoredFieldVisitor.Status needsField(final FieldInfo fieldInfo) {
                return fieldInfo.name.equals(fieldName) ? StoredFieldVisitor.Status.YES : StoredFieldVisitor.Status.NO;
            }
        });
        return fields.toArray(new Field[fields.size()]);
    }
    
    protected String makeFragment(final StringBuilder buffer, final int[] index, final Field[] values, final FieldFragList.WeightedFragInfo fragInfo, final String[] preTags, final String[] postTags, final Encoder encoder) {
        final StringBuilder fragment = new StringBuilder();
        final int s = fragInfo.getStartOffset();
        final int[] modifiedStartOffset = { s };
        final String src = this.getFragmentSourceMSO(buffer, index, values, s, fragInfo.getEndOffset(), modifiedStartOffset);
        int srcIndex = 0;
        for (final FieldFragList.WeightedFragInfo.SubInfo subInfo : fragInfo.getSubInfos()) {
            for (final FieldPhraseList.WeightedPhraseInfo.Toffs to : subInfo.getTermsOffsets()) {
                fragment.append(encoder.encodeText(src.substring(srcIndex, to.getStartOffset() - modifiedStartOffset[0]))).append(this.getPreTag(preTags, subInfo.getSeqnum())).append(encoder.encodeText(src.substring(to.getStartOffset() - modifiedStartOffset[0], to.getEndOffset() - modifiedStartOffset[0]))).append(this.getPostTag(postTags, subInfo.getSeqnum()));
                srcIndex = to.getEndOffset() - modifiedStartOffset[0];
            }
        }
        fragment.append(encoder.encodeText(src.substring(srcIndex)));
        return fragment.toString();
    }
    
    protected String getFragmentSourceMSO(final StringBuilder buffer, final int[] index, final Field[] values, final int startOffset, final int endOffset, final int[] modifiedStartOffset) {
        while (buffer.length() < endOffset && index[0] < values.length) {
            buffer.append(values[index[0]++].stringValue());
            buffer.append(this.getMultiValuedSeparator());
        }
        int bufferLength = buffer.length();
        if (values[index[0] - 1].fieldType().tokenized()) {
            --bufferLength;
        }
        final int eo = (bufferLength < endOffset) ? bufferLength : this.boundaryScanner.findEndOffset(buffer, endOffset);
        modifiedStartOffset[0] = this.boundaryScanner.findStartOffset(buffer, startOffset);
        return buffer.substring(modifiedStartOffset[0], eo);
    }
    
    protected String getFragmentSource(final StringBuilder buffer, final int[] index, final Field[] values, final int startOffset, final int endOffset) {
        while (buffer.length() < endOffset && index[0] < values.length) {
            buffer.append(values[index[0]].stringValue());
            buffer.append(this.multiValuedSeparator);
            final int n = 0;
            ++index[n];
        }
        final int eo = (buffer.length() < endOffset) ? buffer.length() : endOffset;
        return buffer.substring(startOffset, eo);
    }
    
    protected List<FieldFragList.WeightedFragInfo> discreteMultiValueHighlighting(final List<FieldFragList.WeightedFragInfo> fragInfos, final Field[] fields) {
        final Map<String, List<FieldFragList.WeightedFragInfo>> fieldNameToFragInfos = new HashMap<String, List<FieldFragList.WeightedFragInfo>>();
        for (final Field field : fields) {
            fieldNameToFragInfos.put(field.name(), new ArrayList<FieldFragList.WeightedFragInfo>());
        }
        for (final FieldFragList.WeightedFragInfo fragInfo : fragInfos) {
            int fieldEnd = 0;
            for (final Field field2 : fields) {
                if (field2.stringValue().isEmpty()) {
                    ++fieldEnd;
                }
                else {
                    final int fieldStart = fieldEnd;
                    fieldEnd += field2.stringValue().length() + 1;
                    if (fragInfo.getStartOffset() >= fieldStart && fragInfo.getEndOffset() >= fieldStart && fragInfo.getStartOffset() <= fieldEnd && fragInfo.getEndOffset() <= fieldEnd) {
                        fieldNameToFragInfos.get(field2.name()).add(fragInfo);
                        break;
                    }
                    if (fragInfo.getSubInfos().isEmpty()) {
                        break;
                    }
                    final FieldPhraseList.WeightedPhraseInfo.Toffs firstToffs = fragInfo.getSubInfos().get(0).getTermsOffsets().get(0);
                    if (fragInfo.getStartOffset() < fieldEnd) {
                        if (firstToffs.getStartOffset() < fieldEnd) {
                            int fragStart = fieldStart;
                            if (fragInfo.getStartOffset() > fieldStart && fragInfo.getStartOffset() < fieldEnd) {
                                fragStart = fragInfo.getStartOffset();
                            }
                            int fragEnd = fieldEnd;
                            if (fragInfo.getEndOffset() > fieldStart && fragInfo.getEndOffset() < fieldEnd) {
                                fragEnd = fragInfo.getEndOffset();
                            }
                            final List<FieldFragList.WeightedFragInfo.SubInfo> subInfos = new ArrayList<FieldFragList.WeightedFragInfo.SubInfo>();
                            final Iterator<FieldFragList.WeightedFragInfo.SubInfo> subInfoIterator = fragInfo.getSubInfos().iterator();
                            float boost = 0.0f;
                            while (subInfoIterator.hasNext()) {
                                final FieldFragList.WeightedFragInfo.SubInfo subInfo = subInfoIterator.next();
                                final List<FieldPhraseList.WeightedPhraseInfo.Toffs> toffsList = new ArrayList<FieldPhraseList.WeightedPhraseInfo.Toffs>();
                                final Iterator<FieldPhraseList.WeightedPhraseInfo.Toffs> toffsIterator = subInfo.getTermsOffsets().iterator();
                                while (toffsIterator.hasNext()) {
                                    final FieldPhraseList.WeightedPhraseInfo.Toffs toffs = toffsIterator.next();
                                    if (toffs.getStartOffset() >= fieldEnd) {
                                        break;
                                    }
                                    final boolean startsAfterField = toffs.getStartOffset() >= fieldStart;
                                    final boolean endsBeforeField = toffs.getEndOffset() < fieldEnd;
                                    if (startsAfterField && endsBeforeField) {
                                        toffsList.add(toffs);
                                        toffsIterator.remove();
                                    }
                                    else if (startsAfterField) {
                                        toffsList.add(new FieldPhraseList.WeightedPhraseInfo.Toffs(toffs.getStartOffset(), fieldEnd - 1));
                                    }
                                    else if (endsBeforeField) {
                                        toffsList.add(new FieldPhraseList.WeightedPhraseInfo.Toffs(fieldStart, toffs.getEndOffset()));
                                        toffsIterator.remove();
                                    }
                                    else {
                                        toffsList.add(new FieldPhraseList.WeightedPhraseInfo.Toffs(fieldStart, fieldEnd - 1));
                                    }
                                }
                                if (!toffsList.isEmpty()) {
                                    subInfos.add(new FieldFragList.WeightedFragInfo.SubInfo(subInfo.getText(), toffsList, subInfo.getSeqnum(), subInfo.getBoost()));
                                    boost += subInfo.getBoost();
                                }
                                if (subInfo.getTermsOffsets().isEmpty()) {
                                    subInfoIterator.remove();
                                }
                            }
                            final FieldFragList.WeightedFragInfo weightedFragInfo = new FieldFragList.WeightedFragInfo(fragStart, fragEnd, subInfos, boost);
                            fieldNameToFragInfos.get(field2.name()).add(weightedFragInfo);
                        }
                    }
                }
            }
        }
        final List<FieldFragList.WeightedFragInfo> result = new ArrayList<FieldFragList.WeightedFragInfo>();
        for (final List<FieldFragList.WeightedFragInfo> weightedFragInfos : fieldNameToFragInfos.values()) {
            result.addAll(weightedFragInfos);
        }
        Collections.sort(result, new Comparator<FieldFragList.WeightedFragInfo>() {
            @Override
            public int compare(final FieldFragList.WeightedFragInfo info1, final FieldFragList.WeightedFragInfo info2) {
                return info1.getStartOffset() - info2.getStartOffset();
            }
        });
        return result;
    }
    
    public void setMultiValuedSeparator(final char separator) {
        this.multiValuedSeparator = separator;
    }
    
    public char getMultiValuedSeparator() {
        return this.multiValuedSeparator;
    }
    
    public boolean isDiscreteMultiValueHighlighting() {
        return this.discreteMultiValueHighlighting;
    }
    
    public void setDiscreteMultiValueHighlighting(final boolean discreteMultiValueHighlighting) {
        this.discreteMultiValueHighlighting = discreteMultiValueHighlighting;
    }
    
    protected String getPreTag(final int num) {
        return this.getPreTag(this.preTags, num);
    }
    
    protected String getPostTag(final int num) {
        return this.getPostTag(this.postTags, num);
    }
    
    protected String getPreTag(final String[] preTags, final int num) {
        final int n = num % preTags.length;
        return preTags[n];
    }
    
    protected String getPostTag(final String[] postTags, final int num) {
        final int n = num % postTags.length;
        return postTags[n];
    }
    
    static {
        COLORED_PRE_TAGS = new String[] { "<b style=\"background:yellow\">", "<b style=\"background:lawngreen\">", "<b style=\"background:aquamarine\">", "<b style=\"background:magenta\">", "<b style=\"background:palegreen\">", "<b style=\"background:coral\">", "<b style=\"background:wheat\">", "<b style=\"background:khaki\">", "<b style=\"background:lime\">", "<b style=\"background:deepskyblue\">", "<b style=\"background:deeppink\">", "<b style=\"background:salmon\">", "<b style=\"background:peachpuff\">", "<b style=\"background:violet\">", "<b style=\"background:mediumpurple\">", "<b style=\"background:palegoldenrod\">", "<b style=\"background:darkkhaki\">", "<b style=\"background:springgreen\">", "<b style=\"background:turquoise\">", "<b style=\"background:powderblue\">" };
        COLORED_POST_TAGS = new String[] { "</b>" };
        NULL_ENCODER = new DefaultEncoder();
    }
}
