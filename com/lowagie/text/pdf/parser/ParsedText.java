package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.CMapAwareDocumentFont;
import java.util.ArrayList;
import java.util.List;
import com.lowagie.text.pdf.DocumentFont;
import java.nio.charset.StandardCharsets;
import com.lowagie.text.pdf.PdfString;

public class ParsedText extends ParsedTextImpl
{
    protected final Matrix textToUserSpaceTransformMatrix;
    protected final GraphicsState gs;
    protected PdfString pdfText;
    
    protected String decode(final String in) {
        if ("Identity-H".equals(this.gs.font.getEncoding())) {
            in.getBytes(StandardCharsets.UTF_16);
        }
        final byte[] bytes = in.getBytes();
        return this.gs.font.decode(bytes, 0, bytes.length);
    }
    
    protected String decode(final PdfString in) {
        final byte[] bytes = in.getOriginalBytes();
        return this.gs.font.decode(bytes, 0, bytes.length);
    }
    
    @Deprecated
    ParsedText(final String text, final GraphicsState gs, final Matrix textMatrix) {
        this(text, new GraphicsState(gs), textMatrix.multiply(gs.ctm), getUnscaledFontSpaceWidth(gs));
    }
    
    ParsedText(final PdfString text, final GraphicsState gs, final Matrix textMatrix) {
        this(text, new GraphicsState(gs), textMatrix.multiply(gs.ctm), getUnscaledFontSpaceWidth(gs));
    }
    
    private ParsedText(final PdfString text, final GraphicsState gs, final Matrix textMatrix, final float unscaledWidth) {
        super(null, pointToUserSpace(0.0f, 0.0f, textMatrix), pointToUserSpace(getStringWidth(text.toString(), gs), 0.0f, textMatrix), pointToUserSpace(1.0f, 0.0f, textMatrix), convertHeightToUser(gs.font.getFontDescriptor(1, gs.fontSize), textMatrix), convertHeightToUser(gs.font.getFontDescriptor(3, gs.fontSize), textMatrix), convertWidthToUser(unscaledWidth, textMatrix));
        this.pdfText = null;
        if ("Identity-H".equals(gs.font.getEncoding())) {
            this.pdfText = new PdfString(new String(text.getBytes(), StandardCharsets.UTF_16));
        }
        else {
            this.pdfText = text;
        }
        this.textToUserSpaceTransformMatrix = textMatrix;
        this.gs = gs;
    }
    
    @Deprecated
    private ParsedText(final String text, final GraphicsState gs, final Matrix textMatrix, final float unscaledWidth) {
        super(text, pointToUserSpace(0.0f, 0.0f, textMatrix), pointToUserSpace(getStringWidth(text, gs), 0.0f, textMatrix), pointToUserSpace(1.0f, 0.0f, textMatrix), convertHeightToUser(gs.font.getFontDescriptor(1, gs.fontSize), textMatrix), convertHeightToUser(gs.font.getFontDescriptor(3, gs.fontSize), textMatrix), convertWidthToUser(unscaledWidth, textMatrix));
        this.pdfText = null;
        this.textToUserSpaceTransformMatrix = textMatrix;
        this.gs = gs;
    }
    
    private static Vector pointToUserSpace(final float xoffset, final float yoffset, final Matrix textToUserSpaceTransformMatrix) {
        final Vector result = new Vector(xoffset, yoffset, 1.0f).cross(textToUserSpaceTransformMatrix);
        return result;
    }
    
    private static float getUnscaledFontSpaceWidth(final GraphicsState gs) {
        char charToUse = ' ';
        if (gs.font.getWidth(charToUse) == 0) {
            charToUse = ' ';
        }
        return getStringWidth(String.valueOf(charToUse), gs);
    }
    
    private static float getStringWidth(final String string, final GraphicsState gs) {
        final DocumentFont font = gs.font;
        final char[] chars = string.toCharArray();
        float totalWidth = 0.0f;
        for (final char c : chars) {
            final float w = font.getWidth(c) / 1000.0f;
            final float wordSpacing = Character.isSpaceChar(c) ? gs.wordSpacing : 0.0f;
            totalWidth += (w * gs.fontSize + gs.characterSpacing + wordSpacing) * gs.horizontalScaling;
        }
        return totalWidth;
    }
    
    public List<Word> getAsPartialWords() {
        final ArrayList<Word> result = new ArrayList<Word>();
        final CMapAwareDocumentFont font = this.gs.font;
        final char[] chars = this.pdfText.getOriginalChars();
        final boolean[] hasSpace = new boolean[chars.length];
        float totalWidth = 0.0f;
        StringBuffer wordAccum = new StringBuffer(3);
        float wordStartOffset = 0.0f;
        final boolean wordsAreComplete = this.preprocessString(chars, hasSpace);
        boolean currentBreakBefore = false;
        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            final float w = font.getWidth(c) / 1000.0f;
            if (hasSpace[i]) {
                if (wordAccum.length() > 0) {
                    result.add(this.createWord(wordAccum, wordStartOffset, totalWidth, this.getBaseline(), wordsAreComplete, currentBreakBefore));
                    wordAccum = new StringBuffer();
                }
                if (!Character.isWhitespace(c)) {
                    wordStartOffset = totalWidth;
                }
                totalWidth += (w * this.gs.fontSize + this.gs.characterSpacing + this.gs.wordSpacing) * this.gs.horizontalScaling;
                if (Character.isWhitespace(c)) {
                    wordStartOffset = totalWidth;
                }
                wordAccum.append(c);
                currentBreakBefore = true;
            }
            else {
                wordAccum.append(c);
                totalWidth += (w * this.gs.fontSize + this.gs.characterSpacing) * this.gs.horizontalScaling;
            }
        }
        if (wordAccum.length() > 0) {
            result.add(this.createWord(wordAccum, wordStartOffset, totalWidth, this.getBaseline(), wordsAreComplete, currentBreakBefore));
        }
        return result;
    }
    
    private boolean preprocessString(final char[] chars, final boolean[] hasSpace) {
        boolean wordsAreComplete = false;
        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            hasSpace[i] = false;
            final String charValue = this.gs.font.decode(c);
            if (charValue != null) {
                for (final char cFinal : charValue.toCharArray()) {
                    if (Character.isSpaceChar(cFinal)) {
                        wordsAreComplete = true;
                        hasSpace[i] = true;
                    }
                }
            }
        }
        return wordsAreComplete;
    }
    
    private Word createWord(final StringBuffer wordAccum, final float wordStartOffset, final float wordEndOffset, final Vector baseline, final boolean wordsAreComplete, final boolean currentBreakBefore) {
        final Word newWord = new Word(this.gs.font.decode(wordAccum.toString()), this.getAscent(), this.getDescent(), pointToUserSpace(wordStartOffset, 0.0f, this.textToUserSpaceTransformMatrix), pointToUserSpace(wordEndOffset, 0.0f, this.textToUserSpaceTransformMatrix), baseline, this.getSingleSpaceWidth(), wordsAreComplete, currentBreakBefore);
        return newWord;
    }
    
    public float getUnscaledTextWidth(final GraphicsState gs) {
        return getStringWidth(this.getFontCodes(), gs);
    }
    
    private static float convertWidthToUser(final float width, final Matrix textToUserSpaceTransformMatrix) {
        final Vector startPos = pointToUserSpace(0.0f, 0.0f, textToUserSpaceTransformMatrix);
        final Vector endPos = pointToUserSpace(width, 0.0f, textToUserSpaceTransformMatrix);
        return distance(startPos, endPos);
    }
    
    private static float distance(final Vector startPos, final Vector endPos) {
        return endPos.subtract(startPos).length();
    }
    
    private static float convertHeightToUser(final float height, final Matrix textToUserSpaceTransformMatrix) {
        final Vector startPos = pointToUserSpace(0.0f, 0.0f, textToUserSpaceTransformMatrix);
        final Vector endPos = pointToUserSpace(0.0f, height, textToUserSpaceTransformMatrix);
        return distance(endPos, startPos);
    }
    
    @Override
    public void accumulate(final TextAssembler p, final String contextName) {
        p.process(this, contextName);
    }
    
    @Override
    public void assemble(final TextAssembler p) {
        p.renderText(this);
    }
    
    @Override
    public String getText() {
        final String text = super.getText();
        if (text == null && this.pdfText != null) {
            return this.decode(this.pdfText);
        }
        return text;
    }
    
    public String getFontCodes() {
        if (this.pdfText != null) {
            return this.pdfText.toString();
        }
        return null;
    }
    
    @Override
    public FinalText getFinalText(final PdfReader reader, final int page, final TextAssembler assembler, final boolean useMarkup) {
        throw new RuntimeException("Final text should never be called on unprocessed word fragment.");
    }
    
    @Override
    public String toString() {
        return "[ParsedText: [" + this.getText() + "] " + this.getStartPoint() + ", " + this.getEndPoint() + "] lead]";
    }
    
    @Override
    public boolean shouldNotSplit() {
        return false;
    }
    
    @Override
    public boolean breakBefore() {
        return false;
    }
}
