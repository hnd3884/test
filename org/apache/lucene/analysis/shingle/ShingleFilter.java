package org.apache.lucene.analysis.shingle;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;
import java.util.LinkedList;
import org.apache.lucene.analysis.TokenFilter;

public final class ShingleFilter extends TokenFilter
{
    public static final String DEFAULT_FILLER_TOKEN = "_";
    public static final int DEFAULT_MAX_SHINGLE_SIZE = 2;
    public static final int DEFAULT_MIN_SHINGLE_SIZE = 2;
    public static final String DEFAULT_TOKEN_TYPE = "shingle";
    public static final String DEFAULT_TOKEN_SEPARATOR = " ";
    private LinkedList<InputWindowToken> inputWindow;
    private CircularSequence gramSize;
    private StringBuilder gramBuilder;
    private String tokenType;
    private String tokenSeparator;
    private char[] fillerToken;
    private boolean outputUnigrams;
    private boolean outputUnigramsIfNoShingles;
    private int maxShingleSize;
    private int minShingleSize;
    private int numFillerTokensToInsert;
    private AttributeSource nextInputStreamToken;
    private boolean isNextInputStreamToken;
    private boolean isOutputHere;
    boolean noShingleOutput;
    private AttributeSource.State endState;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final PositionLengthAttribute posLenAtt;
    private final TypeAttribute typeAtt;
    private boolean exhausted;
    
    public ShingleFilter(final TokenStream input, final int minShingleSize, final int maxShingleSize) {
        super(input);
        this.inputWindow = new LinkedList<InputWindowToken>();
        this.gramBuilder = new StringBuilder();
        this.tokenType = "shingle";
        this.tokenSeparator = " ";
        this.fillerToken = "_".toCharArray();
        this.outputUnigrams = true;
        this.outputUnigramsIfNoShingles = false;
        this.isNextInputStreamToken = false;
        this.isOutputHere = false;
        this.noShingleOutput = true;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.setMaxShingleSize(maxShingleSize);
        this.setMinShingleSize(minShingleSize);
    }
    
    public ShingleFilter(final TokenStream input, final int maxShingleSize) {
        this(input, 2, maxShingleSize);
    }
    
    public ShingleFilter(final TokenStream input) {
        this(input, 2, 2);
    }
    
    public ShingleFilter(final TokenStream input, final String tokenType) {
        this(input, 2, 2);
        this.setTokenType(tokenType);
    }
    
    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }
    
    public void setOutputUnigrams(final boolean outputUnigrams) {
        this.outputUnigrams = outputUnigrams;
        this.gramSize = new CircularSequence();
    }
    
    public void setOutputUnigramsIfNoShingles(final boolean outputUnigramsIfNoShingles) {
        this.outputUnigramsIfNoShingles = outputUnigramsIfNoShingles;
    }
    
    public void setMaxShingleSize(final int maxShingleSize) {
        if (maxShingleSize < 2) {
            throw new IllegalArgumentException("Max shingle size must be >= 2");
        }
        this.maxShingleSize = maxShingleSize;
    }
    
    public void setMinShingleSize(final int minShingleSize) {
        if (minShingleSize < 2) {
            throw new IllegalArgumentException("Min shingle size must be >= 2");
        }
        if (minShingleSize > this.maxShingleSize) {
            throw new IllegalArgumentException("Min shingle size must be <= max shingle size");
        }
        this.minShingleSize = minShingleSize;
        this.gramSize = new CircularSequence();
    }
    
    public void setTokenSeparator(final String tokenSeparator) {
        this.tokenSeparator = ((null == tokenSeparator) ? "" : tokenSeparator);
    }
    
    public void setFillerToken(final String fillerToken) {
        this.fillerToken = ((null == fillerToken) ? new char[0] : fillerToken.toCharArray());
    }
    
    public boolean incrementToken() throws IOException {
        boolean tokenAvailable = false;
        int builtGramSize = 0;
        if (this.gramSize.atMinValue() || this.inputWindow.size() < this.gramSize.getValue()) {
            this.shiftInputWindow();
            this.gramBuilder.setLength(0);
        }
        else {
            builtGramSize = this.gramSize.getPreviousValue();
        }
        if (this.inputWindow.size() >= this.gramSize.getValue()) {
            boolean isAllFiller = true;
            InputWindowToken nextToken = null;
            final Iterator<InputWindowToken> iter = this.inputWindow.iterator();
            int gramNum = 1;
            while (iter.hasNext() && builtGramSize < this.gramSize.getValue()) {
                nextToken = iter.next();
                if (builtGramSize < gramNum) {
                    if (builtGramSize > 0) {
                        this.gramBuilder.append(this.tokenSeparator);
                    }
                    this.gramBuilder.append(nextToken.termAtt.buffer(), 0, nextToken.termAtt.length());
                    ++builtGramSize;
                }
                if (isAllFiller && nextToken.isFiller) {
                    if (gramNum == this.gramSize.getValue()) {
                        this.gramSize.advance();
                    }
                }
                else {
                    isAllFiller = false;
                }
                ++gramNum;
            }
            if (!isAllFiller && builtGramSize == this.gramSize.getValue()) {
                this.inputWindow.getFirst().attSource.copyTo((AttributeSource)this);
                this.posIncrAtt.setPositionIncrement((int)(this.isOutputHere ? 0 : 1));
                this.termAtt.setEmpty().append(this.gramBuilder);
                if (this.gramSize.getValue() > 1) {
                    this.typeAtt.setType(this.tokenType);
                    this.noShingleOutput = false;
                }
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), nextToken.offsetAtt.endOffset());
                this.posLenAtt.setPositionLength(builtGramSize);
                this.isOutputHere = true;
                this.gramSize.advance();
                tokenAvailable = true;
            }
        }
        return tokenAvailable;
    }
    
    private InputWindowToken getNextToken(final InputWindowToken target) throws IOException {
        InputWindowToken newTarget = target;
        if (this.numFillerTokensToInsert > 0) {
            if (null == target) {
                newTarget = new InputWindowToken(this.nextInputStreamToken.cloneAttributes());
            }
            else {
                this.nextInputStreamToken.copyTo(target.attSource);
            }
            newTarget.offsetAtt.setOffset(newTarget.offsetAtt.startOffset(), newTarget.offsetAtt.startOffset());
            newTarget.termAtt.copyBuffer(this.fillerToken, 0, this.fillerToken.length);
            newTarget.isFiller = true;
            --this.numFillerTokensToInsert;
        }
        else if (this.isNextInputStreamToken) {
            if (null == target) {
                newTarget = new InputWindowToken(this.nextInputStreamToken.cloneAttributes());
            }
            else {
                this.nextInputStreamToken.copyTo(target.attSource);
            }
            this.isNextInputStreamToken = false;
            newTarget.isFiller = false;
        }
        else if (!this.exhausted) {
            if (this.input.incrementToken()) {
                if (null == target) {
                    newTarget = new InputWindowToken(this.cloneAttributes());
                }
                else {
                    this.copyTo(target.attSource);
                }
                if (this.posIncrAtt.getPositionIncrement() > 1) {
                    this.numFillerTokensToInsert = Math.min(this.posIncrAtt.getPositionIncrement() - 1, this.maxShingleSize - 1);
                    if (null == this.nextInputStreamToken) {
                        this.nextInputStreamToken = this.cloneAttributes();
                    }
                    else {
                        this.copyTo(this.nextInputStreamToken);
                    }
                    this.isNextInputStreamToken = true;
                    newTarget.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset());
                    newTarget.termAtt.copyBuffer(this.fillerToken, 0, this.fillerToken.length);
                    newTarget.isFiller = true;
                    --this.numFillerTokensToInsert;
                }
                else {
                    newTarget.isFiller = false;
                }
            }
            else {
                this.exhausted = true;
                this.input.end();
                this.endState = this.captureState();
                this.numFillerTokensToInsert = Math.min(this.posIncrAtt.getPositionIncrement(), this.maxShingleSize - 1);
                if (this.numFillerTokensToInsert > 0) {
                    (this.nextInputStreamToken = new AttributeSource(this.getAttributeFactory())).addAttribute((Class)CharTermAttribute.class);
                    final OffsetAttribute newOffsetAtt = (OffsetAttribute)this.nextInputStreamToken.addAttribute((Class)OffsetAttribute.class);
                    newOffsetAtt.setOffset(this.offsetAtt.endOffset(), this.offsetAtt.endOffset());
                    return this.getNextToken(target);
                }
                newTarget = null;
            }
        }
        else {
            newTarget = null;
        }
        return newTarget;
    }
    
    public void end() throws IOException {
        if (!this.exhausted) {
            super.end();
        }
        else {
            this.restoreState(this.endState);
        }
    }
    
    private void shiftInputWindow() throws IOException {
        InputWindowToken firstToken = null;
        if (this.inputWindow.size() > 0) {
            firstToken = this.inputWindow.removeFirst();
        }
        while (this.inputWindow.size() < this.maxShingleSize) {
            if (null != firstToken) {
                if (null == this.getNextToken(firstToken)) {
                    break;
                }
                this.inputWindow.add(firstToken);
                firstToken = null;
            }
            else {
                final InputWindowToken nextToken = this.getNextToken(null);
                if (null == nextToken) {
                    break;
                }
                this.inputWindow.add(nextToken);
            }
        }
        if (this.outputUnigramsIfNoShingles && this.noShingleOutput && this.gramSize.minValue > 1 && this.inputWindow.size() < this.minShingleSize) {
            this.gramSize.minValue = 1;
        }
        this.gramSize.reset();
        this.isOutputHere = false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.gramSize.reset();
        this.inputWindow.clear();
        this.nextInputStreamToken = null;
        this.isNextInputStreamToken = false;
        this.numFillerTokensToInsert = 0;
        this.isOutputHere = false;
        this.noShingleOutput = true;
        this.exhausted = false;
        this.endState = null;
        if (this.outputUnigramsIfNoShingles && !this.outputUnigrams) {
            this.gramSize.minValue = this.minShingleSize;
        }
    }
    
    private class CircularSequence
    {
        private int value;
        private int previousValue;
        private int minValue;
        
        public CircularSequence() {
            this.minValue = (ShingleFilter.this.outputUnigrams ? 1 : ShingleFilter.this.minShingleSize);
            this.reset();
        }
        
        public int getValue() {
            return this.value;
        }
        
        public void advance() {
            this.previousValue = this.value;
            if (this.value == 1) {
                this.value = ShingleFilter.this.minShingleSize;
            }
            else if (this.value == ShingleFilter.this.maxShingleSize) {
                this.reset();
            }
            else {
                ++this.value;
            }
        }
        
        public void reset() {
            final int minValue = this.minValue;
            this.value = minValue;
            this.previousValue = minValue;
        }
        
        public boolean atMinValue() {
            return this.value == this.minValue;
        }
        
        public int getPreviousValue() {
            return this.previousValue;
        }
    }
    
    private class InputWindowToken
    {
        final AttributeSource attSource;
        final CharTermAttribute termAtt;
        final OffsetAttribute offsetAtt;
        boolean isFiller;
        
        public InputWindowToken(final AttributeSource attSource) {
            this.isFiller = false;
            this.attSource = attSource;
            this.termAtt = (CharTermAttribute)attSource.getAttribute((Class)CharTermAttribute.class);
            this.offsetAtt = (OffsetAttribute)attSource.getAttribute((Class)OffsetAttribute.class);
        }
    }
}
