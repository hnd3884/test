package org.htmlparser.visitors;

import org.htmlparser.Tag;
import org.htmlparser.util.Translate;
import org.htmlparser.Text;

public class TextExtractingVisitor extends NodeVisitor
{
    private StringBuffer textAccumulator;
    private boolean preTagBeingProcessed;
    
    public TextExtractingVisitor() {
        this.textAccumulator = new StringBuffer();
        this.preTagBeingProcessed = false;
    }
    
    public String getExtractedText() {
        return this.textAccumulator.toString();
    }
    
    public void visitStringNode(final Text stringNode) {
        String text = stringNode.getText();
        if (!this.preTagBeingProcessed) {
            text = Translate.decode(text);
            text = this.replaceNonBreakingSpaceWithOrdinarySpace(text);
        }
        this.textAccumulator.append(text);
    }
    
    private String replaceNonBreakingSpaceWithOrdinarySpace(final String text) {
        return text.replace(' ', ' ');
    }
    
    public void visitTag(final Tag tag) {
        if (this.isPreTag(tag)) {
            this.preTagBeingProcessed = true;
        }
    }
    
    public void visitEndTag(final Tag tag) {
        if (this.isPreTag(tag)) {
            this.preTagBeingProcessed = false;
        }
    }
    
    private boolean isPreTag(final Tag tag) {
        return tag.getTagName().equals("PRE");
    }
}
