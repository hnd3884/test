package org.apache.poi.xwpf.usermodel;

public class TextSegment
{
    private PositionInParagraph beginPos;
    private PositionInParagraph endPos;
    
    public TextSegment() {
        this.beginPos = new PositionInParagraph();
        this.endPos = new PositionInParagraph();
    }
    
    public TextSegment(final int beginRun, final int endRun, final int beginText, final int endText, final int beginChar, final int endChar) {
        final PositionInParagraph beginPos = new PositionInParagraph(beginRun, beginText, beginChar);
        final PositionInParagraph endPos = new PositionInParagraph(endRun, endText, endChar);
        this.beginPos = beginPos;
        this.endPos = endPos;
    }
    
    public TextSegment(final PositionInParagraph beginPos, final PositionInParagraph endPos) {
        this.beginPos = beginPos;
        this.endPos = endPos;
    }
    
    public PositionInParagraph getBeginPos() {
        return this.beginPos;
    }
    
    public PositionInParagraph getEndPos() {
        return this.endPos;
    }
    
    public int getBeginRun() {
        return this.beginPos.getRun();
    }
    
    public void setBeginRun(final int beginRun) {
        this.beginPos.setRun(beginRun);
    }
    
    public int getBeginText() {
        return this.beginPos.getText();
    }
    
    public void setBeginText(final int beginText) {
        this.beginPos.setText(beginText);
    }
    
    public int getBeginChar() {
        return this.beginPos.getChar();
    }
    
    public void setBeginChar(final int beginChar) {
        this.beginPos.setChar(beginChar);
    }
    
    public int getEndRun() {
        return this.endPos.getRun();
    }
    
    public void setEndRun(final int endRun) {
        this.endPos.setRun(endRun);
    }
    
    public int getEndText() {
        return this.endPos.getText();
    }
    
    public void setEndText(final int endText) {
        this.endPos.setText(endText);
    }
    
    public int getEndChar() {
        return this.endPos.getChar();
    }
    
    public void setEndChar(final int endChar) {
        this.endPos.setChar(endChar);
    }
}
