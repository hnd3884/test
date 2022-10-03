package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class SimpleSpanFragmenter implements Fragmenter
{
    private static final int DEFAULT_FRAGMENT_SIZE = 100;
    private int fragmentSize;
    private int currentNumFrags;
    private int position;
    private QueryScorer queryScorer;
    private int waitForPos;
    private int textSize;
    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncAtt;
    private OffsetAttribute offsetAtt;
    
    public SimpleSpanFragmenter(final QueryScorer queryScorer) {
        this(queryScorer, 100);
    }
    
    public SimpleSpanFragmenter(final QueryScorer queryScorer, final int fragmentSize) {
        this.position = -1;
        this.waitForPos = -1;
        this.fragmentSize = fragmentSize;
        this.queryScorer = queryScorer;
    }
    
    @Override
    public boolean isNewFragment() {
        this.position += this.posIncAtt.getPositionIncrement();
        if (this.waitForPos <= this.position) {
            this.waitForPos = -1;
        }
        else if (this.waitForPos != -1) {
            return false;
        }
        final WeightedSpanTerm wSpanTerm = this.queryScorer.getWeightedSpanTerm(this.termAtt.toString());
        if (wSpanTerm != null) {
            final List<PositionSpan> positionSpans = wSpanTerm.getPositionSpans();
            for (final PositionSpan positionSpan : positionSpans) {
                if (positionSpan.start == this.position) {
                    this.waitForPos = positionSpan.end + 1;
                    break;
                }
            }
        }
        final boolean isNewFrag = this.offsetAtt.endOffset() >= this.fragmentSize * this.currentNumFrags && this.textSize - this.offsetAtt.endOffset() >= this.fragmentSize >>> 1;
        if (isNewFrag) {
            ++this.currentNumFrags;
        }
        return isNewFrag;
    }
    
    @Override
    public void start(final String originalText, final TokenStream tokenStream) {
        this.position = -1;
        this.currentNumFrags = 1;
        this.textSize = originalText.length();
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)tokenStream.addAttribute((Class)PositionIncrementAttribute.class);
        this.offsetAtt = (OffsetAttribute)tokenStream.addAttribute((Class)OffsetAttribute.class);
    }
}
