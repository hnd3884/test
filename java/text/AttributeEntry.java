package java.text;

import java.util.Map;

class AttributeEntry implements Map.Entry<AttributedCharacterIterator.Attribute, Object>
{
    private AttributedCharacterIterator.Attribute key;
    private Object value;
    
    AttributeEntry(final AttributedCharacterIterator.Attribute key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AttributeEntry)) {
            return false;
        }
        final AttributeEntry attributeEntry = (AttributeEntry)o;
        return attributeEntry.key.equals(this.key) && ((this.value != null) ? attributeEntry.value.equals(this.value) : (attributeEntry.value == null));
    }
    
    @Override
    public AttributedCharacterIterator.Attribute getKey() {
        return this.key;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public Object setValue(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        return this.key.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    @Override
    public String toString() {
        return this.key.toString() + "=" + this.value.toString();
    }
}
