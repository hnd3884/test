package com.octo.captcha.component.image.textpaster.textvisitor;

import com.octo.captcha.component.image.textpaster.MutableAttributedString;

public class OverlapGlyphsTextVisitor implements TextVisitor
{
    private int overlapPixs;
    
    public OverlapGlyphsTextVisitor(final int overlapPixs) {
        this.overlapPixs = 0;
        this.overlapPixs = overlapPixs;
    }
    
    public void visit(final MutableAttributedString mutableAttributedString) {
        mutableAttributedString.overlap(this.overlapPixs);
    }
}
