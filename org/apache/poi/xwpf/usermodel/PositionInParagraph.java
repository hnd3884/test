package org.apache.poi.xwpf.usermodel;

public class PositionInParagraph
{
    private int posRun;
    private int posText;
    private int posChar;
    
    public PositionInParagraph() {
    }
    
    public PositionInParagraph(final int posRun, final int posText, final int posChar) {
        this.posRun = posRun;
        this.posChar = posChar;
        this.posText = posText;
    }
    
    public int getRun() {
        return this.posRun;
    }
    
    public void setRun(final int beginRun) {
        this.posRun = beginRun;
    }
    
    public int getText() {
        return this.posText;
    }
    
    public void setText(final int beginText) {
        this.posText = beginText;
    }
    
    public int getChar() {
        return this.posChar;
    }
    
    public void setChar(final int beginChar) {
        this.posChar = beginChar;
    }
}
