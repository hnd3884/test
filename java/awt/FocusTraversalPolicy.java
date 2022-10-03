package java.awt;

public abstract class FocusTraversalPolicy
{
    public abstract Component getComponentAfter(final Container p0, final Component p1);
    
    public abstract Component getComponentBefore(final Container p0, final Component p1);
    
    public abstract Component getFirstComponent(final Container p0);
    
    public abstract Component getLastComponent(final Container p0);
    
    public abstract Component getDefaultComponent(final Container p0);
    
    public Component getInitialComponent(final Window window) {
        if (window == null) {
            throw new IllegalArgumentException("window cannot be equal to null.");
        }
        Component defaultComponent = this.getDefaultComponent(window);
        if (defaultComponent == null && window.isFocusableWindow()) {
            defaultComponent = window;
        }
        return defaultComponent;
    }
}
