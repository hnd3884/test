package javax.accessibility;

public interface AccessibleHypertext extends AccessibleText
{
    int getLinkCount();
    
    AccessibleHyperlink getLink(final int p0);
    
    int getLinkIndex(final int p0);
}
