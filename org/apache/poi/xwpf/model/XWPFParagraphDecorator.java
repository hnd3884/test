package org.apache.poi.xwpf.model;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public abstract class XWPFParagraphDecorator
{
    protected XWPFParagraph paragraph;
    protected XWPFParagraphDecorator nextDecorator;
    
    public XWPFParagraphDecorator(final XWPFParagraph paragraph) {
        this(paragraph, null);
    }
    
    public XWPFParagraphDecorator(final XWPFParagraph paragraph, final XWPFParagraphDecorator nextDecorator) {
        this.paragraph = paragraph;
        this.nextDecorator = nextDecorator;
    }
    
    public String getText() {
        if (this.nextDecorator != null) {
            return this.nextDecorator.getText();
        }
        return this.paragraph.getText();
    }
}
