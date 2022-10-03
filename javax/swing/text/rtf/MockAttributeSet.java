package javax.swing.text.rtf;

import java.util.Enumeration;
import java.util.Dictionary;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AttributeSet;

class MockAttributeSet implements AttributeSet, MutableAttributeSet
{
    public Dictionary<Object, Object> backing;
    
    public boolean isEmpty() {
        return this.backing.isEmpty();
    }
    
    @Override
    public int getAttributeCount() {
        return this.backing.size();
    }
    
    @Override
    public boolean isDefined(final Object o) {
        return this.backing.get(o) != null;
    }
    
    @Override
    public boolean isEqual(final AttributeSet set) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public AttributeSet copyAttributes() {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public Object getAttribute(final Object o) {
        return this.backing.get(o);
    }
    
    @Override
    public void addAttribute(final Object o, final Object o2) {
        this.backing.put(o, o2);
    }
    
    @Override
    public void addAttributes(final AttributeSet set) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            this.backing.put(nextElement, set.getAttribute(nextElement));
        }
    }
    
    @Override
    public void removeAttribute(final Object o) {
        this.backing.remove(o);
    }
    
    @Override
    public void removeAttributes(final AttributeSet set) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public void removeAttributes(final Enumeration<?> enumeration) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public void setResolveParent(final AttributeSet set) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public Enumeration getAttributeNames() {
        return this.backing.keys();
    }
    
    @Override
    public boolean containsAttribute(final Object o, final Object o2) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public boolean containsAttributes(final AttributeSet set) {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
    
    @Override
    public AttributeSet getResolveParent() {
        throw new InternalError("MockAttributeSet: charade revealed!");
    }
}
