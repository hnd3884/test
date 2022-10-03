package javax.swing.text.html.parser;

import java.util.BitSet;

final class TagStack implements DTDConstants
{
    TagElement tag;
    Element elem;
    ContentModelState state;
    TagStack next;
    BitSet inclusions;
    BitSet exclusions;
    boolean net;
    boolean pre;
    
    TagStack(final TagElement tag, final TagStack next) {
        this.tag = tag;
        this.elem = tag.getElement();
        this.next = next;
        final Element element = tag.getElement();
        if (element.getContent() != null) {
            this.state = new ContentModelState(element.getContent());
        }
        if (next != null) {
            this.inclusions = next.inclusions;
            this.exclusions = next.exclusions;
            this.pre = next.pre;
        }
        if (tag.isPreformatted()) {
            this.pre = true;
        }
        if (element.inclusions != null) {
            if (this.inclusions != null) {
                (this.inclusions = (BitSet)this.inclusions.clone()).or(element.inclusions);
            }
            else {
                this.inclusions = element.inclusions;
            }
        }
        if (element.exclusions != null) {
            if (this.exclusions != null) {
                (this.exclusions = (BitSet)this.exclusions.clone()).or(element.exclusions);
            }
            else {
                this.exclusions = element.exclusions;
            }
        }
    }
    
    public Element first() {
        return (this.state != null) ? this.state.first() : null;
    }
    
    public ContentModel contentModel() {
        if (this.state == null) {
            return null;
        }
        return this.state.getModel();
    }
    
    boolean excluded(final int n) {
        return this.exclusions != null && this.exclusions.get(this.elem.getIndex());
    }
    
    boolean advance(final Element element) {
        if (this.exclusions != null && this.exclusions.get(element.getIndex())) {
            return false;
        }
        if (this.state != null) {
            final ContentModelState advance = this.state.advance(element);
            if (advance != null) {
                this.state = advance;
                return true;
            }
        }
        else if (this.elem.getType() == 19) {
            return true;
        }
        return this.inclusions != null && this.inclusions.get(element.getIndex());
    }
    
    boolean terminate() {
        return this.state == null || this.state.terminate();
    }
    
    @Override
    public String toString() {
        return (this.next == null) ? ("<" + this.tag.getElement().getName() + ">") : (this.next + " <" + this.tag.getElement().getName() + ">");
    }
}
