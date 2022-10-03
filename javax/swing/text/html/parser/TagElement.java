package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;

public class TagElement
{
    Element elem;
    HTML.Tag htmlTag;
    boolean insertedByErrorRecovery;
    
    public TagElement(final Element element) {
        this(element, false);
    }
    
    public TagElement(final Element elem, final boolean insertedByErrorRecovery) {
        this.elem = elem;
        this.htmlTag = HTML.getTag(elem.getName());
        if (this.htmlTag == null) {
            this.htmlTag = new HTML.UnknownTag(elem.getName());
        }
        this.insertedByErrorRecovery = insertedByErrorRecovery;
    }
    
    public boolean breaksFlow() {
        return this.htmlTag.breaksFlow();
    }
    
    public boolean isPreformatted() {
        return this.htmlTag.isPreformatted();
    }
    
    public Element getElement() {
        return this.elem;
    }
    
    public HTML.Tag getHTMLTag() {
        return this.htmlTag;
    }
    
    public boolean fictional() {
        return this.insertedByErrorRecovery;
    }
}
