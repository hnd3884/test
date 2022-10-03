package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;

public class FinalText implements TextAssemblyBuffer
{
    String _content;
    
    public FinalText(final String content) {
        this._content = content;
    }
    
    @Override
    public String getText() {
        return this._content;
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
    public FinalText getFinalText(final PdfReader reader, final int page, final TextAssembler assembler, final boolean useMarkup) {
        return this;
    }
    
    @Override
    public String toString() {
        return "[FinalText: [" + this.getText() + "] d]";
    }
}
