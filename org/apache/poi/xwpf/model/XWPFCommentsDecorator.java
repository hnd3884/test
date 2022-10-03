package org.apache.poi.xwpf.model;

import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class XWPFCommentsDecorator extends XWPFParagraphDecorator
{
    private StringBuilder commentText;
    
    public XWPFCommentsDecorator(final XWPFParagraphDecorator nextDecorator) {
        this(nextDecorator.paragraph, nextDecorator);
    }
    
    public XWPFCommentsDecorator(final XWPFParagraph paragraph, final XWPFParagraphDecorator nextDecorator) {
        super(paragraph, nextDecorator);
        this.commentText = new StringBuilder(64);
        for (final CTMarkupRange anchor : paragraph.getCTP().getCommentRangeStartArray()) {
            final XWPFComment comment;
            if ((comment = paragraph.getDocument().getCommentByID(anchor.getId().toString())) != null) {
                this.commentText.append("\tComment by ").append(comment.getAuthor()).append(": ").append(comment.getText());
            }
        }
    }
    
    public String getCommentText() {
        return this.commentText.toString();
    }
    
    @Override
    public String getText() {
        return super.getText() + (Object)this.commentText;
    }
}
