package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class SimpleFragmenter implements Fragmenter
{
    private static final int DEFAULT_FRAGMENT_SIZE = 100;
    private int currentNumFrags;
    private int fragmentSize;
    private OffsetAttribute offsetAtt;
    
    public SimpleFragmenter() {
        this(100);
    }
    
    public SimpleFragmenter(final int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
    
    @Override
    public void start(final String originalText, final TokenStream stream) {
        this.offsetAtt = (OffsetAttribute)stream.addAttribute((Class)OffsetAttribute.class);
        this.currentNumFrags = 1;
    }
    
    @Override
    public boolean isNewFragment() {
        final boolean isNewFrag = this.offsetAtt.endOffset() >= this.fragmentSize * this.currentNumFrags;
        if (isNewFrag) {
            ++this.currentNumFrags;
        }
        return isNewFrag;
    }
    
    public int getFragmentSize() {
        return this.fragmentSize;
    }
    
    public void setFragmentSize(final int size) {
        this.fragmentSize = size;
    }
}
