package java.awt;

public interface LayoutManager2 extends LayoutManager
{
    void addLayoutComponent(final Component p0, final Object p1);
    
    Dimension maximumLayoutSize(final Container p0);
    
    float getLayoutAlignmentX(final Container p0);
    
    float getLayoutAlignmentY(final Container p0);
    
    void invalidateLayout(final Container p0);
}
