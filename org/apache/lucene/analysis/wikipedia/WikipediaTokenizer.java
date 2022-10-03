package org.apache.lucene.analysis.wikipedia;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import java.util.Collections;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.analysis.Tokenizer;

public final class WikipediaTokenizer extends Tokenizer
{
    public static final String INTERNAL_LINK = "il";
    public static final String EXTERNAL_LINK = "el";
    public static final String EXTERNAL_LINK_URL = "elu";
    public static final String CITATION = "ci";
    public static final String CATEGORY = "c";
    public static final String BOLD = "b";
    public static final String ITALICS = "i";
    public static final String BOLD_ITALICS = "bi";
    public static final String HEADING = "h";
    public static final String SUB_HEADING = "sh";
    public static final int ALPHANUM_ID = 0;
    public static final int APOSTROPHE_ID = 1;
    public static final int ACRONYM_ID = 2;
    public static final int COMPANY_ID = 3;
    public static final int EMAIL_ID = 4;
    public static final int HOST_ID = 5;
    public static final int NUM_ID = 6;
    public static final int CJ_ID = 7;
    public static final int INTERNAL_LINK_ID = 8;
    public static final int EXTERNAL_LINK_ID = 9;
    public static final int CITATION_ID = 10;
    public static final int CATEGORY_ID = 11;
    public static final int BOLD_ID = 12;
    public static final int ITALICS_ID = 13;
    public static final int BOLD_ITALICS_ID = 14;
    public static final int HEADING_ID = 15;
    public static final int SUB_HEADING_ID = 16;
    public static final int EXTERNAL_LINK_URL_ID = 17;
    public static final String[] TOKEN_TYPES;
    public static final int TOKENS_ONLY = 0;
    public static final int UNTOKENIZED_ONLY = 1;
    public static final int BOTH = 2;
    public static final int UNTOKENIZED_TOKEN_FLAG = 1;
    private final WikipediaTokenizerImpl scanner;
    private int tokenOutput;
    private Set<String> untokenizedTypes;
    private Iterator<AttributeSource.State> tokens;
    private final OffsetAttribute offsetAtt;
    private final TypeAttribute typeAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final CharTermAttribute termAtt;
    private final FlagsAttribute flagsAtt;
    private boolean first;
    
    public WikipediaTokenizer() {
        this(0, Collections.emptySet());
    }
    
    public WikipediaTokenizer(final int tokenOutput, final Set<String> untokenizedTypes) {
        this.tokenOutput = 0;
        this.untokenizedTypes = Collections.emptySet();
        this.tokens = null;
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.flagsAtt = (FlagsAttribute)this.addAttribute((Class)FlagsAttribute.class);
        this.scanner = new WikipediaTokenizerImpl(this.input);
        this.init(tokenOutput, untokenizedTypes);
    }
    
    public WikipediaTokenizer(final AttributeFactory factory, final int tokenOutput, final Set<String> untokenizedTypes) {
        super(factory);
        this.tokenOutput = 0;
        this.untokenizedTypes = Collections.emptySet();
        this.tokens = null;
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.flagsAtt = (FlagsAttribute)this.addAttribute((Class)FlagsAttribute.class);
        this.scanner = new WikipediaTokenizerImpl(this.input);
        this.init(tokenOutput, untokenizedTypes);
    }
    
    private void init(final int tokenOutput, final Set<String> untokenizedTypes) {
        if (tokenOutput != 0 && tokenOutput != 1 && tokenOutput != 2) {
            throw new IllegalArgumentException("tokenOutput must be TOKENS_ONLY, UNTOKENIZED_ONLY or BOTH");
        }
        this.tokenOutput = tokenOutput;
        this.untokenizedTypes = untokenizedTypes;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.tokens != null && this.tokens.hasNext()) {
            final AttributeSource.State state = this.tokens.next();
            this.restoreState(state);
            return true;
        }
        this.clearAttributes();
        final int tokenType = this.scanner.getNextToken();
        if (tokenType == -1) {
            return false;
        }
        final String type = WikipediaTokenizerImpl.TOKEN_TYPES[tokenType];
        if (this.tokenOutput == 0 || !this.untokenizedTypes.contains(type)) {
            this.setupToken();
        }
        else if (this.tokenOutput == 1 && this.untokenizedTypes.contains(type)) {
            this.collapseTokens(tokenType);
        }
        else if (this.tokenOutput == 2) {
            this.collapseAndSaveTokens(tokenType, type);
        }
        int posinc = this.scanner.getPositionIncrement();
        if (this.first && posinc == 0) {
            posinc = 1;
        }
        this.posIncrAtt.setPositionIncrement(posinc);
        this.typeAtt.setType(type);
        this.first = false;
        return true;
    }
    
    private void collapseAndSaveTokens(final int tokenType, final String type) throws IOException {
        final StringBuilder buffer = new StringBuilder(32);
        int numAdded = this.scanner.setText(buffer);
        final int theStart = this.scanner.yychar();
        int lastPos = theStart + numAdded;
        int numSeen = 0;
        final List<AttributeSource.State> tmp = new ArrayList<AttributeSource.State>();
        this.setupSavedToken(0, type);
        tmp.add(this.captureState());
        int tmpTokType;
        while ((tmpTokType = this.scanner.getNextToken()) != -1 && tmpTokType == tokenType && this.scanner.getNumWikiTokensSeen() > numSeen) {
            final int currPos = this.scanner.yychar();
            for (int i = 0; i < currPos - lastPos; ++i) {
                buffer.append(' ');
            }
            numAdded = this.scanner.setText(buffer);
            this.setupSavedToken(this.scanner.getPositionIncrement(), type);
            tmp.add(this.captureState());
            ++numSeen;
            lastPos = currPos + numAdded;
        }
        final String s = buffer.toString().trim();
        this.termAtt.setEmpty().append(s);
        this.offsetAtt.setOffset(this.correctOffset(theStart), this.correctOffset(theStart + s.length()));
        this.flagsAtt.setFlags(1);
        if (tmpTokType != -1) {
            this.scanner.yypushback(this.scanner.yylength());
        }
        this.tokens = tmp.iterator();
    }
    
    private void setupSavedToken(final int positionInc, final String type) {
        this.setupToken();
        this.posIncrAtt.setPositionIncrement(positionInc);
        this.typeAtt.setType(type);
    }
    
    private void collapseTokens(final int tokenType) throws IOException {
        final StringBuilder buffer = new StringBuilder(32);
        int numAdded = this.scanner.setText(buffer);
        final int theStart = this.scanner.yychar();
        int lastPos = theStart + numAdded;
        int tmpTokType;
        int currPos;
        for (int numSeen = 0; (tmpTokType = this.scanner.getNextToken()) != -1 && tmpTokType == tokenType && this.scanner.getNumWikiTokensSeen() > numSeen; ++numSeen, lastPos = currPos + numAdded) {
            currPos = this.scanner.yychar();
            for (int i = 0; i < currPos - lastPos; ++i) {
                buffer.append(' ');
            }
            numAdded = this.scanner.setText(buffer);
        }
        final String s = buffer.toString().trim();
        this.termAtt.setEmpty().append(s);
        this.offsetAtt.setOffset(this.correctOffset(theStart), this.correctOffset(theStart + s.length()));
        this.flagsAtt.setFlags(1);
        if (tmpTokType != -1) {
            this.scanner.yypushback(this.scanner.yylength());
        }
        else {
            this.tokens = null;
        }
    }
    
    private void setupToken() {
        this.scanner.getText(this.termAtt);
        final int start = this.scanner.yychar();
        this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + this.termAtt.length()));
    }
    
    public void close() throws IOException {
        super.close();
        this.scanner.yyreset(this.input);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.scanner.yyreset(this.input);
        this.tokens = null;
        this.scanner.reset();
        this.first = true;
    }
    
    public void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset(this.scanner.yychar() + this.scanner.yylength());
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
    
    static {
        TOKEN_TYPES = new String[] { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "il", "el", "ci", "c", "b", "i", "bi", "h", "sh", "elu" };
    }
}
