package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;

public class XWPFComment
{
    protected String id;
    protected String author;
    protected StringBuilder text;
    
    public XWPFComment(final CTComment comment, final XWPFDocument document) {
        this.text = new StringBuilder(64);
        this.id = comment.getId().toString();
        this.author = comment.getAuthor();
        for (final CTP ctp : comment.getPArray()) {
            if (this.text.length() > 0) {
                this.text.append("\n");
            }
            final XWPFParagraph p = new XWPFParagraph(ctp, document);
            this.text.append(p.getText());
        }
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getText() {
        return this.text.toString();
    }
}
