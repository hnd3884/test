package javax.swing.text;

public interface Element
{
    Document getDocument();
    
    Element getParentElement();
    
    String getName();
    
    AttributeSet getAttributes();
    
    int getStartOffset();
    
    int getEndOffset();
    
    int getElementIndex(final int p0);
    
    int getElementCount();
    
    Element getElement(final int p0);
    
    boolean isLeaf();
}
