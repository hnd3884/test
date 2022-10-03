package java.awt;

public interface MenuContainer
{
    Font getFont();
    
    void remove(final MenuComponent p0);
    
    @Deprecated
    boolean postEvent(final Event p0);
}
