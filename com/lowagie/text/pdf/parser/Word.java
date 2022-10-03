package com.lowagie.text.pdf.parser;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

public class Word extends ParsedTextImpl
{
    boolean shouldNotSplit;
    boolean breakBefore;
    
    Word(final String text, final float ascent, final float descent, final Vector startPoint, final Vector endPoint, final Vector baseline, final float spaceWidth, final boolean isCompleteWord, final boolean breakBefore) {
        super(text, startPoint, endPoint, baseline, ascent, descent, spaceWidth);
        this.shouldNotSplit = isCompleteWord;
        this.breakBefore = breakBefore;
    }
    
    @Override
    public void accumulate(final TextAssembler p, final String contextName) {
        p.process(this, contextName);
    }
    
    @Override
    public void assemble(final TextAssembler p) {
        p.renderText(this);
    }
    
    private static String formatPercent(final float f) {
        return String.format("%.2f%%", f);
    }
    
    private String wordMarkup(String text, final PdfReader reader, final int page, final TextAssembler assembler) {
        final Rectangle mediaBox = reader.getPageSize(page);
        Rectangle cropBox = reader.getBoxSize(page, "crop");
        text = text.replaceAll("[ \u202f]", " ").trim();
        if (text.length() == 0) {
            return text;
        }
        mediaBox.normalize();
        if (cropBox != null) {
            cropBox.normalize();
        }
        else {
            cropBox = reader.getBoxSize(page, "trim");
            if (cropBox != null) {
                cropBox.normalize();
            }
            else {
                cropBox = mediaBox;
            }
        }
        final float xOffset = cropBox.getLeft() - mediaBox.getLeft();
        final float yOffset = cropBox.getTop() - mediaBox.getTop();
        final Vector startPoint = this.getStartPoint();
        final Vector endPoint = this.getEndPoint();
        final float pageWidth = cropBox.getWidth();
        final float pageHeight = cropBox.getHeight();
        final float leftPercent = (float)((startPoint.get(0) - xOffset - mediaBox.getLeft()) / pageWidth * 100.0);
        final float bottom = endPoint.get(1) + yOffset - this.getDescent() - mediaBox.getBottom();
        final float bottomPercent = bottom / pageHeight * 100.0f;
        final StringBuilder result = new StringBuilder();
        final float width = this.getWidth();
        final float widthPercent = width / pageWidth * 100.0f;
        final float height = this.getAscent();
        final float heightPercent = height / pageHeight * 100.0f;
        final String myId = assembler.getWordId();
        final Rectangle resultRect = new Rectangle(leftPercent, bottomPercent, leftPercent + widthPercent, bottomPercent + heightPercent);
        result.append("<span class=\"t-word\" style=\"bottom: ").append(formatPercent(resultRect.getBottom())).append("; left: ").append(formatPercent(resultRect.getLeft())).append("; width: ").append(formatPercent(resultRect.getWidth())).append("; height: ").append(formatPercent(resultRect.getHeight())).append(";\"").append(" id=\"").append(myId).append("\">").append(escapeHTML(text)).append(" ");
        result.append("</span> ");
        return result.toString();
    }
    
    private static String escapeHTML(final String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    
    @Override
    public FinalText getFinalText(final PdfReader reader, final int page, final TextAssembler assembler, final boolean useMarkup) {
        if (useMarkup) {
            return new FinalText(this.wordMarkup(this.getText(), reader, page, assembler));
        }
        return new FinalText(this.getText() + " ");
    }
    
    @Override
    public String toString() {
        return "[Word: [" + this.getText() + "] " + this.getStartPoint() + ", " + this.getEndPoint() + "] lead" + this.getAscent() + "]";
    }
    
    @Override
    public boolean shouldNotSplit() {
        return this.shouldNotSplit;
    }
    
    @Override
    public boolean breakBefore() {
        return this.breakBefore;
    }
}
