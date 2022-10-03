package javax.swing.text;

import java.util.Enumeration;

public interface AttributeSet
{
    public static final Object NameAttribute = StyleConstants.NameAttribute;
    public static final Object ResolveAttribute = StyleConstants.ResolveAttribute;
    
    int getAttributeCount();
    
    boolean isDefined(final Object p0);
    
    boolean isEqual(final AttributeSet p0);
    
    AttributeSet copyAttributes();
    
    Object getAttribute(final Object p0);
    
    Enumeration<?> getAttributeNames();
    
    boolean containsAttribute(final Object p0, final Object p1);
    
    boolean containsAttributes(final AttributeSet p0);
    
    AttributeSet getResolveParent();
    
    public interface ParagraphAttribute
    {
    }
    
    public interface CharacterAttribute
    {
    }
    
    public interface ColorAttribute
    {
    }
    
    public interface FontAttribute
    {
    }
}
