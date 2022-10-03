package javax.swing.text;

import java.util.Enumeration;

public interface MutableAttributeSet extends AttributeSet
{
    void addAttribute(final Object p0, final Object p1);
    
    void addAttributes(final AttributeSet p0);
    
    void removeAttribute(final Object p0);
    
    void removeAttributes(final Enumeration<?> p0);
    
    void removeAttributes(final AttributeSet p0);
    
    void setResolveParent(final AttributeSet p0);
}
