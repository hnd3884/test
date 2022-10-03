package com.lowagie.text.pdf.parser;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import com.lowagie.text.pdf.PdfReader;

public class MarkedUpTextAssembler implements TextAssembler
{
    private PdfReader _reader;
    private ParsedTextImpl _inProgress;
    int _page;
    private int word_id_counter;
    private boolean _usePdfMarkupElements;
    Collection<FinalText> result;
    Collection<TextAssemblyBuffer> partialWords;
    
    MarkedUpTextAssembler(final PdfReader reader) {
        this._inProgress = null;
        this.word_id_counter = 1;
        this._usePdfMarkupElements = false;
        this.result = new ArrayList<FinalText>();
        this.partialWords = new ArrayList<TextAssemblyBuffer>();
        this._reader = reader;
    }
    
    MarkedUpTextAssembler(final PdfReader reader, final boolean usePdfMarkupElements) {
        this._inProgress = null;
        this.word_id_counter = 1;
        this._usePdfMarkupElements = false;
        this.result = new ArrayList<FinalText>();
        this.partialWords = new ArrayList<TextAssemblyBuffer>();
        this._reader = reader;
        this._usePdfMarkupElements = usePdfMarkupElements;
    }
    
    @Override
    public void process(final ParsedText unassembled, final String contextName) {
        this.partialWords.addAll(unassembled.getAsPartialWords());
    }
    
    @Override
    public void process(final FinalText completed, final String contextName) {
        this.clearAccumulator();
        this.result.add(completed);
    }
    
    @Override
    public void process(final Word completed, final String contextName) {
        this.partialWords.add(completed);
    }
    
    private void clearAccumulator() {
        for (final TextAssemblyBuffer partialWord : this.partialWords) {
            partialWord.assemble(this);
        }
        this.partialWords.clear();
        if (this._inProgress != null) {
            this.result.add(this._inProgress.getFinalText(this._reader, this._page, this, this._usePdfMarkupElements));
            this._inProgress = null;
        }
    }
    
    private FinalText concatenateResult(String containingElementName) {
        if (containingElementName == null) {
            return null;
        }
        final StringBuffer res = new StringBuffer();
        if (this._usePdfMarkupElements && !containingElementName.isEmpty()) {
            res.append('<').append(containingElementName).append('>');
        }
        for (final FinalText item : this.result) {
            res.append(item.getText());
        }
        this.result.clear();
        if (this._usePdfMarkupElements && !containingElementName.isEmpty()) {
            res.append("</");
            final int spacePos = containingElementName.indexOf(32);
            if (spacePos >= 0) {
                containingElementName = containingElementName.substring(0, spacePos);
            }
            res.append(containingElementName).append('>');
        }
        return new FinalText(res.toString());
    }
    
    private FinalText accumulate(final Collection<TextAssemblyBuffer> textInfo) {
        final StringBuffer res = new StringBuffer();
        for (final TextAssemblyBuffer info : textInfo) {
            res.append(info.getText());
        }
        return new FinalText(res.toString());
    }
    
    @Override
    public FinalText endParsingContext(final String containingElementName) {
        this.clearAccumulator();
        return this.concatenateResult(containingElementName);
    }
    
    @Override
    public void reset() {
        this.result.clear();
        this.partialWords.clear();
        this._inProgress = null;
    }
    
    @Override
    public void renderText(final FinalText finalText) {
        this.result.add(finalText);
    }
    
    @Override
    public void renderText(final ParsedTextImpl partialWord) {
        final boolean firstRender = this._inProgress == null;
        boolean hardReturn = false;
        if (firstRender) {
            this._inProgress = partialWord;
            return;
        }
        final Vector start = partialWord.getStartPoint();
        final Vector lastStart = this._inProgress.getStartPoint();
        final Vector lastEnd = this._inProgress.getEndPoint();
        final float dist = this._inProgress.getBaseline().subtract(lastStart).cross(lastStart.subtract(start)).lengthSquared() / this._inProgress.getBaseline().subtract(lastStart).lengthSquared();
        final float sameLineThreshold = partialWord.getAscent() * 0.5f;
        if (dist > sameLineThreshold || Float.isNaN(dist)) {
            hardReturn = true;
        }
        final float spacing = lastEnd.subtract(start).length();
        if (hardReturn || partialWord.breakBefore()) {
            this.result.add(this._inProgress.getFinalText(this._reader, this._page, this, this._usePdfMarkupElements));
            if (hardReturn) {
                this.result.add(new FinalText("\n"));
                if (this._usePdfMarkupElements) {
                    this.result.add(new FinalText("<br class='t-pdf' />"));
                }
            }
            this._inProgress = partialWord;
        }
        else if (spacing < partialWord.getSingleSpaceWidth() / 2.3 || this._inProgress.shouldNotSplit()) {
            this._inProgress = new Word(this._inProgress.getText() + partialWord.getText().trim(), partialWord.getAscent(), partialWord.getDescent(), lastStart, partialWord.getEndPoint(), this._inProgress.getBaseline(), partialWord.getSingleSpaceWidth(), this._inProgress.shouldNotSplit(), this._inProgress.breakBefore());
        }
        else {
            this.result.add(this._inProgress.getFinalText(this._reader, this._page, this, this._usePdfMarkupElements));
            this._inProgress = partialWord;
        }
    }
    
    protected PdfReader getReader() {
        return this._reader;
    }
    
    @Override
    public void setPage(final int page) {
        this._page = page;
    }
    
    @Override
    public String getWordId() {
        return "word" + this.word_id_counter++;
    }
}
