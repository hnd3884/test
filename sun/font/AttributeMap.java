package sun.font;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.awt.font.TextAttribute;
import java.util.AbstractMap;

public final class AttributeMap extends AbstractMap<TextAttribute, Object>
{
    private AttributeValues values;
    private Map<TextAttribute, Object> delegateMap;
    private static boolean first;
    
    public AttributeMap(final AttributeValues values) {
        this.values = values;
    }
    
    @Override
    public Set<Map.Entry<TextAttribute, Object>> entrySet() {
        return this.delegate().entrySet();
    }
    
    @Override
    public Object put(final TextAttribute textAttribute, final Object o) {
        return this.delegate().put(textAttribute, o);
    }
    
    public AttributeValues getValues() {
        return this.values;
    }
    
    private Map<TextAttribute, Object> delegate() {
        if (this.delegateMap == null) {
            if (AttributeMap.first) {
                AttributeMap.first = false;
                Thread.dumpStack();
            }
            this.delegateMap = this.values.toMap(new HashMap<TextAttribute, Object>(27));
            this.values = null;
        }
        return this.delegateMap;
    }
    
    @Override
    public String toString() {
        if (this.values != null) {
            return "map of " + this.values.toString();
        }
        return super.toString();
    }
    
    static {
        AttributeMap.first = false;
    }
}
