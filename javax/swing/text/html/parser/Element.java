package javax.swing.text.html.parser;

import sun.awt.AppContext;
import java.util.Hashtable;
import java.util.BitSet;
import java.io.Serializable;

public final class Element implements DTDConstants, Serializable
{
    public int index;
    public String name;
    public boolean oStart;
    public boolean oEnd;
    public BitSet inclusions;
    public BitSet exclusions;
    public int type;
    public ContentModel content;
    public AttributeList atts;
    public Object data;
    private static final Object MAX_INDEX_KEY;
    static Hashtable<String, Integer> contentTypes;
    
    Element() {
        this.type = 19;
    }
    
    Element(final String name, final int index) {
        this.type = 19;
        this.name = name;
        this.index = index;
        if (index > getMaxIndex()) {
            AppContext.getAppContext().put(Element.MAX_INDEX_KEY, index);
        }
    }
    
    static int getMaxIndex() {
        final Integer n = (Integer)AppContext.getAppContext().get(Element.MAX_INDEX_KEY);
        return (n != null) ? n : 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean omitStart() {
        return this.oStart;
    }
    
    public boolean omitEnd() {
        return this.oEnd;
    }
    
    public int getType() {
        return this.type;
    }
    
    public ContentModel getContent() {
        return this.content;
    }
    
    public AttributeList getAttributes() {
        return this.atts;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public boolean isEmpty() {
        return this.type == 17;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public AttributeList getAttribute(final String s) {
        for (AttributeList list = this.atts; list != null; list = list.next) {
            if (list.name.equals(s)) {
                return list;
            }
        }
        return null;
    }
    
    public AttributeList getAttributeByValue(final String s) {
        for (AttributeList list = this.atts; list != null; list = list.next) {
            if (list.values != null && list.values.contains(s)) {
                return list;
            }
        }
        return null;
    }
    
    public static int name2type(final String s) {
        final Integer n = Element.contentTypes.get(s);
        return (n != null) ? n : 0;
    }
    
    static {
        MAX_INDEX_KEY = new Object();
        (Element.contentTypes = new Hashtable<String, Integer>()).put("CDATA", 1);
        Element.contentTypes.put("RCDATA", 16);
        Element.contentTypes.put("EMPTY", 17);
        Element.contentTypes.put("ANY", 19);
    }
}
