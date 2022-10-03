package javax.swing.text.html.parser;

import java.util.Vector;
import java.io.Serializable;

public final class ContentModel implements Serializable
{
    public int type;
    public Object content;
    public ContentModel next;
    private boolean[] valSet;
    private boolean[] val;
    
    public ContentModel() {
    }
    
    public ContentModel(final Element element) {
        this(0, element, null);
    }
    
    public ContentModel(final int n, final ContentModel contentModel) {
        this(n, contentModel, null);
    }
    
    public ContentModel(final int type, final Object content, final ContentModel next) {
        this.type = type;
        this.content = content;
        this.next = next;
    }
    
    public boolean empty() {
        switch (this.type) {
            case 42:
            case 63: {
                return true;
            }
            case 43:
            case 124: {
                for (ContentModel next = (ContentModel)this.content; next != null; next = next.next) {
                    if (next.empty()) {
                        return true;
                    }
                }
                return false;
            }
            case 38:
            case 44: {
                for (ContentModel next2 = (ContentModel)this.content; next2 != null; next2 = next2.next) {
                    if (!next2.empty()) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void getElements(final Vector<Element> vector) {
        switch (this.type) {
            case 42:
            case 43:
            case 63: {
                ((ContentModel)this.content).getElements(vector);
                break;
            }
            case 38:
            case 44:
            case 124: {
                for (ContentModel next = (ContentModel)this.content; next != null; next = next.next) {
                    next.getElements(vector);
                }
                break;
            }
            default: {
                vector.addElement((Element)this.content);
                break;
            }
        }
    }
    
    public boolean first(final Object o) {
        switch (this.type) {
            case 42:
            case 43:
            case 63: {
                return ((ContentModel)this.content).first(o);
            }
            case 44: {
                for (ContentModel next = (ContentModel)this.content; next != null; next = next.next) {
                    if (next.first(o)) {
                        return true;
                    }
                    if (!next.empty()) {
                        return false;
                    }
                }
                return false;
            }
            case 38:
            case 124: {
                final Element element = (Element)o;
                if (this.valSet == null || this.valSet.length <= Element.getMaxIndex()) {
                    this.valSet = new boolean[Element.getMaxIndex() + 1];
                    this.val = new boolean[this.valSet.length];
                }
                if (this.valSet[element.index]) {
                    return this.val[element.index];
                }
                for (ContentModel next2 = (ContentModel)this.content; next2 != null; next2 = next2.next) {
                    if (next2.first(o)) {
                        this.val[element.index] = true;
                        break;
                    }
                }
                this.valSet[element.index] = true;
                return this.val[element.index];
            }
            default: {
                return this.content == o;
            }
        }
    }
    
    public Element first() {
        switch (this.type) {
            case 38:
            case 42:
            case 63:
            case 124: {
                return null;
            }
            case 43:
            case 44: {
                return ((ContentModel)this.content).first();
            }
            default: {
                return (Element)this.content;
            }
        }
    }
    
    @Override
    public String toString() {
        switch (this.type) {
            case 42: {
                return this.content + "*";
            }
            case 63: {
                return this.content + "?";
            }
            case 43: {
                return this.content + "+";
            }
            case 38:
            case 44:
            case 124: {
                final char[] array = { ' ', (char)this.type, ' ' };
                String s = "";
                for (ContentModel next = (ContentModel)this.content; next != null; next = next.next) {
                    s += next;
                    if (next.next != null) {
                        s += new String(array);
                    }
                }
                return "(" + s + ")";
            }
            default: {
                return this.content.toString();
            }
        }
    }
}
