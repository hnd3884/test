package java.awt;

interface EventFilter
{
    FilterAction acceptEvent(final AWTEvent p0);
    
    public enum FilterAction
    {
        ACCEPT, 
        REJECT, 
        ACCEPT_IMMEDIATELY;
    }
}
