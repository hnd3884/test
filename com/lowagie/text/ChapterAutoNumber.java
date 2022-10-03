package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;

public class ChapterAutoNumber extends Chapter
{
    private static final long serialVersionUID = -9217457637987854167L;
    protected boolean numberSet;
    
    public ChapterAutoNumber(final Paragraph para) {
        super(para, 0);
        this.numberSet = false;
    }
    
    public ChapterAutoNumber(final String title) {
        super(title, 0);
        this.numberSet = false;
    }
    
    @Override
    public Section addSection(final String title) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        return this.addSection(title, 2);
    }
    
    @Override
    public Section addSection(final Paragraph title) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        return this.addSection(title, 2);
    }
    
    public int setAutomaticNumber(int number) {
        if (!this.numberSet) {
            ++number;
            super.setChapterNumber(number);
            this.numberSet = true;
        }
        return number;
    }
}
