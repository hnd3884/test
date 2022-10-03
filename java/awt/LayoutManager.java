package java.awt;

public interface LayoutManager
{
    void addLayoutComponent(final String p0, final Component p1);
    
    void removeLayoutComponent(final Component p0);
    
    Dimension preferredLayoutSize(final Container p0);
    
    Dimension minimumLayoutSize(final Container p0);
    
    void layoutContainer(final Container p0);
}
